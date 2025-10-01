package game.input;

import java.io.*;
import java.nio.file.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.opencv_java;

public class GestureManager {

    static { Loader.load(opencv_java.class); }

    // Classpath resources (e.g., "/cascade/left.xml")
    private final String leftRes, rightRes, upRes, downRes, shootRes; // <— added shootRes
    private final boolean showDebug;

    // Camera + conversion
    private OpenCVFrameGrabber grabber;
    private final OpenCVFrameConverter.ToMat toMat = new OpenCVFrameConverter.ToMat();
    private CanvasFrame debugCanvas;

    // Shared latest frame for workers
    private final AtomicReference<Mat> latestFrame = new AtomicReference<>();

    // Public flags (debounced)
    private final AtomicBoolean left  = new AtomicBoolean(false);
    private final AtomicBoolean right = new AtomicBoolean(false);
    private final AtomicBoolean up    = new AtomicBoolean(false);
    private final AtomicBoolean down  = new AtomicBoolean(false);
    private final AtomicBoolean shoot = new AtomicBoolean(false);           // <— added
    private final AtomicBoolean shootPulse = new AtomicBoolean(false);      // <— rising-edge pulse

    // Threads
    private ExecutorService pool;
    private Future<?> captureTask;
    private Future<?> leftTask, rightTask, upTask, downTask, shootTask;     // <— added

    // Tunables
    private final long targetCaptureIntervalMs = 30;  // ~33 FPS capture cap
    private final long targetDetectIntervalMs  = 50;  // ~20 FPS per worker
    private final boolean useEqualizeHist = false;    // set true if lighting varies a lot

    // Debounce helper: rises fast on detections, decays slow on misses
    private static final class Debouncer {
        private int score = 0;                  // 0..MAX
        private final int max;
        private final int onThreshold;
        private final AtomicBoolean out;
        private final AtomicBoolean pulse;      // optional: set true on rising edge
        private boolean lastOut = false;

        Debouncer(AtomicBoolean out, int max, int onThreshold) {
            this(out, max, onThreshold, null);
        }
        Debouncer(AtomicBoolean out, int max, int onThreshold, AtomicBoolean pulse) {
            this.out = out;
            this.max = max;
            this.onThreshold = onThreshold;
            this.pulse = pulse;
        }
        void update(boolean detected) {
            score += detected ? 2 : -1;        // rise fast, decay slow
            if (score < 0) score = 0;
            if (score > max) score = max;
            boolean now = (score >= onThreshold);
            out.set(now);
            if (pulse != null && now && !lastOut) {
                // Rising edge
                pulse.set(true);
            }
            lastOut = now;
        }
    }

    public GestureManager(String leftRes, String rightRes, String upRes, String downRes, String shootRes, boolean showDebug) {
        this.leftRes = leftRes;
        this.rightRes = rightRes;
        this.upRes = upRes;
        this.downRes = downRes;
        this.shootRes = shootRes;          // <— added
        this.showDebug = showDebug;
    }

    // Backwards-compat convenience ctor (if you don’t want shoot yet)
    public GestureManager(String leftRes, String rightRes, String upRes, String downRes, boolean showDebug) {
        this(leftRes, rightRes, upRes, downRes, null, showDebug);
    }

    public void start() throws FrameGrabber.Exception {
        grabber = new OpenCVFrameGrabber(0);
        grabber.start();

        if (showDebug) {
            debugCanvas = new CanvasFrame("Webcam Preview");
            debugCanvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
            debugCanvas.setCanvasSize(grabber.getImageWidth(), grabber.getImageHeight());
        }

        // Cascade paths: extract from classpath to temp files
        final String leftPath  = extractResourceToTemp(leftRes);
        final String rightPath = extractResourceToTemp(rightRes);
        final String upPath    = extractResourceToTemp(upRes);
        final String downPath  = extractResourceToTemp(downRes);
        final String shootPath = (shootRes != null ? extractResourceToTemp(shootRes) : null); // <— added

        pool = Executors.newFixedThreadPool(6);

        // 1) Producer: keep latest frame fresh
        captureTask = pool.submit(() -> {
            long next = System.currentTimeMillis();
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Frame f = grabber.grab();
                    if (f != null) {
                        Mat m = toMat.convert(f);
                        if (m != null && !m.empty()) {
                            latestFrame.set(m.clone());
                            if (showDebug && debugCanvas != null && debugCanvas.isVisible()) {
                                debugCanvas.showImage(f);
                            }
                        }
                    }
                    next += targetCaptureIntervalMs;
                    long sleep = next - System.currentTimeMillis();
                    if (sleep > 0) Thread.sleep(sleep);
                } catch (Throwable t) {
                    break;
                }
            }
        });

        // 2) Consumers: one classifier per thread, with debounced outputs
        leftTask  = pool.submit(worker(new Debouncer(left,  4, 3), new CascadeClassifier(leftPath)));
        rightTask = pool.submit(worker(new Debouncer(right, 4, 3), new CascadeClassifier(rightPath)));
        upTask    = pool.submit(worker(new Debouncer(up,    4, 3), new CascadeClassifier(upPath)));
        downTask  = pool.submit(worker(new Debouncer(down,  4, 3), new CascadeClassifier(downPath)));

        // Shoot worker (uses pulse on rising edge)
        if (shootPath != null) {
            shootTask = pool.submit(worker(new Debouncer(shoot, 4, 3, shootPulse), new CascadeClassifier(shootPath)));
        }
    }

    private Runnable worker(Debouncer deb, CascadeClassifier classifier) {
        return () -> {
            long next = System.currentTimeMillis();
            Mat gray = new Mat();
            RectVector detections = new RectVector();

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Mat src = latestFrame.get();
                    if (src != null && !src.empty()) {
                        opencv_imgproc.cvtColor(src, gray, opencv_imgproc.COLOR_BGR2GRAY);
                        if (useEqualizeHist) opencv_imgproc.equalizeHist(gray, gray);
                        detections.resize(0);
                        classifier.detectMultiScale(gray, detections);
                        deb.update(detections.size() > 0);
                    } else {
                        deb.update(false);
                    }
                    next += targetDetectIntervalMs;
                    long sleep = next - System.currentTimeMillis();
                    if (sleep > 0) Thread.sleep(sleep);
                } catch (Throwable t) {
                    deb.update(false);
                }
            }
            // cleanup mats
            gray.close();
            detections.close();
        };
    }

    public boolean isLeft()  { return left.get();  }
    public boolean isRight() { return right.get(); }
    public boolean isUp()    { return up.get();    }
    public boolean isDown()  { return down.get();  }
    public boolean isShoot() { return shoot.get(); }                  // <— added

    /** Returns true exactly once per detected shoot rising edge. */
    public boolean consumeShootPulse() {                               // <— added
        return shootPulse.getAndSet(false);
    }

    public void stop() {
        try {
            if (captureTask != null) captureTask.cancel(true);
            if (leftTask != null)    leftTask.cancel(true);
            if (rightTask != null)   rightTask.cancel(true);
            if (upTask != null)      upTask.cancel(true);
            if (downTask != null)    downTask.cancel(true);
            if (shootTask != null)   shootTask.cancel(true);           // <— added
            if (pool != null)        pool.shutdownNow();
            if (grabber != null)     grabber.stop();
        } catch (Exception ignored) {}
        if (debugCanvas != null) debugCanvas.dispose();
        Mat m = latestFrame.getAndSet(null);
        if (m != null) m.close();
    }

    /** Extract a classpath resource (e.g., "/cascade/left.xml") to a temp file and return its absolute path. */
    private static String extractResourceToTemp(String resourcePath) {
        if (resourcePath == null || !resourcePath.startsWith("/")) {
            throw new IllegalArgumentException("Resource path must start with '/': " + resourcePath);
        }
        try (InputStream in = GestureManager.class.getResourceAsStream(resourcePath)) {
            if (in == null) throw new FileNotFoundException("Resource not found on classpath: " + resourcePath);
            String filename = Paths.get(resourcePath).getFileName().toString();
            Path tmp = Files.createTempFile("cascade_", "_" + filename);
            tmp.toFile().deleteOnExit();
            Files.copy(in, tmp, StandardCopyOption.REPLACE_EXISTING);
            return tmp.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to extract resource: " + resourcePath, e);
        }
    }
}
