package com.judykong.abdmt.AbdObserver;

import android.graphics.PointF;

import java.util.ArrayList;

public class GestureObserver extends AbdObserver {

    // public static final String NAME = "Gesture";
    public static final int observer = ObserverFlags.GESTURE;

    private static GestureObserver _gestureObserver = null;
    private ArrayList<ArrayList<TimePoint>> _gestures;

    private GestureObserver() {
        _gestures = new ArrayList<ArrayList<TimePoint>>();
        Kernel = gaussianKernel(GaussianStdDev);
    }

    // Returns the Gesture singleton
    public static GestureObserver getInstance() {
        if (_gestureObserver == null) {
            _gestureObserver = new GestureObserver();
        }
        return _gestureObserver;
    }

    // Returns the name of the module
//    public String getName() {
//        return NAME;
//    }

    // Returns the number of paths stored
    public int getSize() {
        return _gestures.size();
    }
    public boolean isEmpty() { return _gestures.size() == 0; }

    // Store a new path
    public void add(ArrayList<TimePoint> path) {
        _gestures.add(path);
    }

    // Delete all stored paths
    public void clear() {
        _gestures = new ArrayList<ArrayList<TimePoint>>();
    }

    // Load existing paths
    public void loadGestures(ArrayList<ArrayList<TimePoint>> gestures) {
        _gestures = gestures;
    }

    // Returns all gestures;
    public ArrayList<ArrayList<TimePoint>> getGestures() {
        return _gestures;
    }

    // Returns the most recent lastN gestures;
    public ArrayList<ArrayList<TimePoint>> getGestures(int lastN) {
        ArrayList<ArrayList<TimePoint>> gestures = new ArrayList<ArrayList<TimePoint>>();
        for (int i = this.getSize() - 1; i >= this.getSize() - lastN; i--) {
            if (i >= 0) {
                gestures.add(_gestures.get(i));
            }
        }
        return gestures;
    }

    // Returns the gestures from startTime to endTime
    public ArrayList<ArrayList<TimePoint>> getGesturesMs(long startTime, long endTime) {
        ArrayList<ArrayList<TimePoint>> gestures = new ArrayList<ArrayList<TimePoint>>();
        for (int i = this.getSize() - 1; i >= 0; i--) {
            if (i >= 0) {
                ArrayList<TimePoint> gesture = _gestures.get(i);
                if (startTime <= gesture.get(0).time && gesture.get(gesture.size() - 1).time <= endTime) {
                    gestures.add(gesture);
                }
            }
        }
        return gestures;
    }

    // Returns the gestures since startTime
    public ArrayList<ArrayList<TimePoint>> getGesturesMs(long startTime) {
        ArrayList<ArrayList<TimePoint>> gestures = new ArrayList<ArrayList<TimePoint>>();
        for (int i = this.getSize() - 1; i >= 0; i--) {
            if (i >= 0) {
                ArrayList<TimePoint> gesture = _gestures.get(i);
                if (startTime <= gesture.get(0).time) {
                    gestures.add(gesture);
                }
            }
        }
        return gestures;
    }

    /*
    // Get the path at the i-th index back from the most recent (the latest path at index 0)
    public ArrayList<TimePoint> get(int idxAgo) {
        return _gestures.get(_gestures.size() - idxAgo - 1);
    }

    // Remove the path at the i-th index back from the most recent (the latest path at index 0)
    public void removeAt(int idxAgo) {
        _gestures.remove(_gestures.size() - idxAgo - 1);
    }
    */

    /**** Movement Variability ****/

    // Parameters: None
    // Return: Average movement variability across all stored paths
    public double getMovementVariability() {
        if (_gestures.size() == 0) { return 0; }
        double sum = 0;
        int n = this._gestures.size();
        for (ArrayList<TimePoint> path: this._gestures) {
            sum += calculateMovementVariability(path);
        }
        return sum / n;
    }

    // Parameters: The number of (last) paths to calculate the average movement variability for
    // Return: Average movement variability across the last N paths
    public double getMovementVariability(int lastN) {
        if (_gestures.size() < lastN) { return 0; }
        double sum = 0;
        int n = this._gestures.size();
        for (int i = 0; i < lastN; i++) {
            sum += calculateMovementVariability(this._gestures.get(n - i - 1));
        }
        return sum / lastN;
    }

    // Parameters: The index range of paths to calculate the average movement variability for
    // Return: Average movement variability of paths from startTime to endTime
    public double getMovementVariabilityMs(long startTime, long endTime) {
        if (_gestures.size() == 0) { return 0; }
        double sum = 0;
        int n = this._gestures.size();
        int count = 0;
        for (ArrayList<TimePoint> path: this._gestures) {
            TimePoint tch0 = path.get(0);
            TimePoint tchN = path.get(path.size() - 1);
            if (startTime <= tch0.time && tchN.time <= endTime) {
                sum += calculateMovementVariability(path);
                count ++;
            }
        }
        return count == 0 ? 0 : sum / count;
    }

    // Parameters: The index range of paths to calculate the average movement variability for
    // Return: Average movement variability of paths since startTime
    public double getMovementVariabilityMs(long startTime) {
        if (_gestures.size() == 0) { return 0; }
        double sum = 0;
        int n = this._gestures.size();
        int count = 0;
        for (ArrayList<TimePoint> path: this._gestures) {
            TimePoint tch0 = path.get(0);
            TimePoint tchN = path.get(path.size() - 1);
            if (startTime <= tch0.time) {
                sum += calculateMovementVariability(path);
                count ++;
            }
        }
        return count == 0 ? 0 : sum / count;
    }

    /**** Movement Error ****/

    // Parameters: None
    // Return: Average movement error across all stored paths
    public double getMovementError() {
        if (_gestures.size() == 0) { return 0; }
        double sum = 0;
        int n = this._gestures.size();
        for (ArrayList<TimePoint> path: this._gestures) {
            sum += calculateMovementError(path);
        }
        return sum / n;
    }

    // Parameters: The number of (last) paths to calculate the average movement error for
    // Return: Average movement error across the last N paths
    public double getMovementError(int lastN) {
        if (_gestures.size() < lastN) { return 0; }
        double sum = 0;
        int n = this._gestures.size();
        for (int i = 0; i < lastN; i++) {
            sum += calculateMovementError(this._gestures.get(n - i - 1));
        }
        return sum / lastN;
    }

    // Parameters: The index range of paths to calculate the average movement error for
    // Return: Average movement error of paths in index range from startTime to endTime
    public double getMovementErrorMs(long startTime, long endTime) {
        if (_gestures.size() == 0) { return 0; }
        double sum = 0;
        int n = this._gestures.size();
        int count = 0;
        for (ArrayList<TimePoint> path: this._gestures) {
            TimePoint tch0 = path.get(0);
            TimePoint tchN = path.get(path.size() - 1);
            if (startTime <= tch0.time && tchN.time <= endTime) {
                sum += calculateMovementError(path);
                count ++;
            }
        }
        return count == 0 ? 0 : sum / count;
    }

    // Parameters: The index range of paths to calculate the average movement error for
    // Return: Average movement error of paths in index range since startTime
    public double getMovementErrorMs(long startTime) {
        if (_gestures.size() == 0) { return 0; }
        double sum = 0;
        int n = this._gestures.size();
        int count = 0;
        for (ArrayList<TimePoint> path: this._gestures) {
            TimePoint tch0 = path.get(0);
            TimePoint tchN = path.get(path.size() - 1);
            if (startTime <= tch0.time) {
                sum += calculateMovementError(path);
                count ++;
            }
        }
        return count == 0 ? 0 : sum / count;
    }

    /**** Movement Offset ****/

    // Parameters: None
    // Return: Average movement offset across all stored paths
    public double getMovementOffset() {
        if (_gestures.size() == 0) { return 0; }
        double sum = 0;
        int n = this._gestures.size();
        for (ArrayList<TimePoint> path: this._gestures) {
            sum += calculateMovementOffset(path);
        }
        return sum / n;
    }

    // Parameters: The number of (last) paths to calculate the average movement offset for
    // Return: Average movement offset across the last N paths
    public double getMovementOffset(int lastN) {
        if (_gestures.size() < lastN) { return 0; }
        double sum = 0;
        int n = this._gestures.size();
        for (int i = 0; i < lastN; i++) {
            sum += calculateMovementOffset(this._gestures.get(n - i - 1));
        }
        return sum / lastN;
    }

    // Parameters: The index range of paths to calculate the average movement offset for
    // Return: Average movement offset of paths in index range from startTime to endTime
    public double getMovementOffsetMs(long startTime, long endTime) {
        if (_gestures.size() == 0) { return 0; }
        double sum = 0;
        int n = this._gestures.size();
        int count = 0;
        for (ArrayList<TimePoint> path: this._gestures) {
            TimePoint tch0 = path.get(0);
            TimePoint tchN = path.get(path.size() - 1);
            if (startTime <= tch0.time && tchN.time <= endTime) {
                sum += calculateMovementOffset(path);
                count ++;
            }
        }
        return count == 0 ? 0 : sum / count;
    }

    // Parameters: The index range of paths to calculate the average movement offset for
    // Return: Average movement offset of paths in index range since startTime
    public double getMovementOffsetMs(long startTime) {
        if (_gestures.size() == 0) { return 0; }
        double sum = 0;
        int n = this._gestures.size();
        int count = 0;
        for (ArrayList<TimePoint> path: this._gestures) {
            TimePoint tch0 = path.get(0);
            TimePoint tchN = path.get(path.size() - 1);
            if (startTime <= tch0.time) {
                sum += calculateMovementOffset(path);
                count ++;
            }
        }
        return count == 0 ? 0 : sum / count;
    }

    /**** Movement Duration ****/

    // Parameters: None
    // Return: Average movement duration across all stored paths
    public long getMovementDuration() {
        if (_gestures.size() == 0) { return 0; }
        long sum = 0;
        int n = this._gestures.size();
        for (ArrayList<TimePoint> path: this._gestures) {
            sum += calculateMovementDuration(path);
        }
        return sum / n;
    }

    // Parameters: The number of (last) paths to calculate the average movement offset for
    // Return: Average movement duration across the last N paths
    public long getMovementDuration(int lastN) {
        if (_gestures.size() < lastN) { return 0; }
        long sum = 0;
        int n = this._gestures.size();
        for (int i = 0; i < lastN; i++) {
            sum += calculateMovementDuration(this._gestures.get(n - i - 1));
        }
        return sum / lastN;
    }

    // Parameters: The index range of paths to calculate the average movement offset for
    // Return: Average movement duration of paths in index range from startTime to endTime
    public long getMovementDurationMs(long startTime, long endTime) {
        if (_gestures.size() == 0) { return 0; }
        long sum = 0;
        int n = this._gestures.size();
        int count = 0;
        for (ArrayList<TimePoint> path: this._gestures) {
            TimePoint tch0 = path.get(0);
            TimePoint tchN = path.get(path.size() - 1);
            if (startTime <= tch0.time && tchN.time <= endTime) {
                sum += calculateMovementDuration(path);
                count ++;
            }
        }
        return count == 0 ? 0 : sum / count;
    }

    // Parameters: The index range of paths to calculate the average movement offset for
    // Return: Average movement duration of paths in index range since startTime
    public long getMovementDurationMs(long startTime) {
        if (_gestures.size() == 0) { return 0; }
        long sum = 0;
        int n = this._gestures.size();
        int count = 0;
        for (ArrayList<TimePoint> path: this._gestures) {
            TimePoint tch0 = path.get(0);
            TimePoint tchN = path.get(path.size() - 1);
            if (startTime <= tch0.time) {
                sum += calculateMovementDuration(path);
                count ++;
            }
        }
        return count == 0 ? 0 : sum / count;
    }

    /**** Helper Functions ****/

    private final int GaussianStdDev = 5;
    private final int Hertz = 100;
    private double[] Kernel;

    private double[] gaussianKernel(int stdev) {
        int size = 3 * stdev * 2 + 1;
        double[] kernel = new double[size];
        for (int i = -kernel.length / 2, j = 0; i <= kernel.length / 2; i++, j++) {
            kernel[j] = (1.0 / (Math.sqrt(2.0 * Math.PI) * stdev)) * Math.pow(
                    Math.E, -(i * i) / (2.0 * stdev * stdev));
        }
        return kernel;
    }

    private ArrayList<TimePoint> resample(ArrayList<TimePoint> points, int hertz) {
        double I = 1000.0 / hertz;
        double T = 0.0;
        ArrayList<TimePoint> srcPts = new ArrayList<TimePoint>(points);
        int n = (int) Math.ceil((points.get(points.size() - 1).time - points.get(0).time) / I);
        ArrayList<TimePoint> dstPts = new ArrayList<TimePoint>(n);
        dstPts.add(srcPts.get(0));
        for (int i = 1; i < srcPts.size(); i++) {
            TimePoint pt1 = srcPts.get(i - 1);
            TimePoint pt2 = srcPts.get(i);
            double dt = pt2.time - pt1.time;

            if ((T + dt) >= I) {
                double pct = (I - T) / dt;
                double qx = pt1.x + pct * (pt2.x - pt1.x);
                double qy = pt1.y + pct * (pt2.y - pt1.y);
                double qt = pt1.time + (I - T);
                // NOTE: set contact area to 0 because we are not using them for calculation
                TimePoint q = new TimePoint((float) qx, (float) qy, 0, 0, 0, 0, (long) qt);
                dstPts.add(q);
                srcPts.add(i, q);
                T = 0.0;
            }
            else T += dt;
        }
        if (dstPts.size() == n - 1) {
            dstPts.add(srcPts.get(srcPts.size() - 1));
        }
        return dstPts;
    }

    private ArrayList<PointF> filter(ArrayList<PointF> series, double[] filter) {
        double[] newy = new double[series.size()];
        for (int i = 0; i < series.size(); i++) {
            for (int k = 0, j = i - filter.length / 2; j <= i + filter.length / 2; k++, j++) {
                if (0 <= j && j < series.size()) {
                    newy[i] += series.get(j).y * filter[k];
                }
            }
        }
        ArrayList<PointF> newpts = new ArrayList<PointF>();
        for (int i = 0; i < newy.length; i++) {
            newpts.add(new PointF(series.get(i).x, (float) newy[i]));
        }
        return newpts;
    }

    private ArrayList<TimePoint> smooth(ArrayList<TimePoint> points) {
        int halfLen = Kernel.length / 2;
        ArrayList<PointF> posx = new ArrayList<PointF>();
        ArrayList<PointF> posy = new ArrayList<PointF>();
        for (int i = 0; i < halfLen; i++) {
            posx.add(new PointF(0, points.get(0).x));
            posy.add(new PointF(0, points.get(0).y));
        }
        for (int i = 0; i < points.size(); i++) {
            posx.add(new PointF(points.get(i).time, points.get(i).x));
            posy.add(new PointF(points.get(i).time, points.get(i).y));
        }
        for (int i = 0; i < halfLen; i++) {
            posx.add(new PointF(0, points.get(points.size() - 1).x));
            posy.add(new PointF(0, points.get(points.size() - 1).y));
        }
        posx = filter(posx, Kernel);
        posy = filter(posy, Kernel);
        ArrayList<TimePoint> smoothed = new ArrayList<TimePoint>();
        for (int i = halfLen; i < points.size() + halfLen; i++) {
            smoothed.add(new TimePoint(posx.get(i).y, posy.get(i).y,
                    points.get(i - halfLen).major,
                    points.get(i - halfLen).minor,
                    points.get(i - halfLen).contactArea,
                    points.get(i - halfLen).orientation,
                    points.get(i - halfLen).time));
        }
        return smoothed;
    }

    private double angle(TimePoint start, TimePoint end, boolean positiveOnly) {
        double radians = 0.0;
        if (start.x != end.x) {
            radians = Math.atan2(end.y - start.y, end.x - start.x);
        } else {
            if (end.y < start.y) radians = -Math.PI / 2.0;
            else if (end.y > start.y) radians = +Math.PI / 2.0;
        }
        if (positiveOnly && radians < 0.0) { radians += Math.PI * 2.0; }
        return radians;
    }

    private double distance(TimePoint start, TimePoint end) {
        return Math.sqrt(
                (start.x - end.x) * (start.x - end.x) + (start.y - end.y) * (start.y - end.y));
    }

    private TimePoint rotatePoint(TimePoint p, TimePoint c, double radians) {
        double originalRadians = angle(c, p, true);
        double newRadians = originalRadians + radians;
        double x = distance(p, c) * Math.cos(newRadians);
        double y = distance(p, c) * Math.sin(newRadians);
        TimePoint q = new TimePoint((float) x, (float) y, p.major, p.minor, p.contactArea, p.orientation + radians, p.time);
        return q;
    }

    private ArrayList<TimePoint> rotate(ArrayList<TimePoint> points) {
        TimePoint src = points.get(0);
        TimePoint tar = points.get(points.size() - 1);
        double radians = angle(src, tar, true);
        ArrayList<TimePoint> newPoints = new ArrayList<TimePoint>();
        for (TimePoint pt : points) {
            TimePoint newPt = rotatePoint(pt, src, -radians);
            newPoints.add(newPt);
        }
        return newPoints;
    }

    private double calculateMovementVariability(ArrayList<TimePoint> path) {
        path = resample(path, Hertz);
        path = smooth(path);
        path = rotate(path);
        int n = path.size();
        double sum = 0;
        for (TimePoint pt : path) {
            sum += pt.y;
        }
        double avg = sum / n;
        double sumSqr = 0;
        for (TimePoint pt : path) {
            sumSqr += (pt.y - avg) * (pt.y - avg);
        }
        return Math.sqrt(sumSqr / (n - 1));
    }

    private double calculateMovementError(ArrayList<TimePoint> path) {
        path = resample(path, Hertz);
        path = smooth(path);
        path = rotate(path);
        int n = path.size();
        double sum = 0;
        for (TimePoint pt : path) {
            sum += Math.abs(pt.y);
        }
        return sum / n;
    }

    private double calculateMovementOffset(ArrayList<TimePoint> path) {
        path = resample(path, Hertz);
        path = smooth(path);
        path = rotate(path);
        int n = path.size();
        double sum = 0;
        for (TimePoint pt : path) {
            sum += pt.y;
        }
        return sum / n;
    }

    private long calculateMovementDuration(ArrayList<TimePoint> path) {
        path = resample(path, Hertz);
        path = smooth(path);
        path = rotate(path);
        return path.get(path.size() - 1).time - path.get(0).time;
    }
}
