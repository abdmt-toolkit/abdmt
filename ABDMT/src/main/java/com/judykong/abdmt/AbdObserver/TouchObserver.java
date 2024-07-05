package com.judykong.abdmt.AbdObserver;

import java.util.ArrayList;

public class TouchObserver extends AbdObserver {

    // public static final String NAME = "Touch";
    public static final int observer = ObserverFlags.TOUCH;

    private static TouchObserver _touchObserver = null;
    private ArrayList<ArrayList<TimePoint>> _touches;

    private TouchObserver() {
        _touches = new ArrayList<ArrayList<TimePoint>>();
    }

    // Returns the Touch singleton
    public static TouchObserver getInstance() {
        if (_touchObserver == null) {
            _touchObserver = new TouchObserver();
        }
        return _touchObserver;
    }

    // Returns the name of the module
//    public String getName() {
//        return NAME;
//    }

    // Returns the number of paths stored
    public int getSize() {
        return _touches.size();
    }
    public boolean isEmpty() { return _touches.size() == 0; }

    // DEBUG
    // public int getLastSize() { return _touches.get(_touches.size()- 1).size(); }

    // Store a new path
    public void add(ArrayList<TimePoint> tch) {
        _touches.add(tch);
    }

    // Delete all stored paths
    public void clear() {
        _touches = new ArrayList<ArrayList<TimePoint>>();
    }

    // Load existing paths
    public void loadTouches(ArrayList<ArrayList<TimePoint>> touches) {
        _touches = touches;
    }

    // Returns all touches;
    public ArrayList<ArrayList<TimePoint>> getTouches() {
        return _touches;
    }

    // Returns the most recent lastN touches;
    public ArrayList<ArrayList<TimePoint>> getTouches(int lastN) {
        ArrayList<ArrayList<TimePoint>> touches = new ArrayList<ArrayList<TimePoint>>();
        for (int i = this.getSize() - 1; i >= this.getSize() - lastN; i--) {
            if (i >= 0) {
                touches.add(_touches.get(i));
            }
        }
        return touches;
    }

    // Returns the touches from startTime to endTime
    public ArrayList<ArrayList<TimePoint>> getTouchesMs(long startTime, long endTime) {
        ArrayList<ArrayList<TimePoint>> touches = new ArrayList<ArrayList<TimePoint>>();
        for (int i = this.getSize() - 1; i >= 0; i--) {
            if (i >= 0) {
                ArrayList<TimePoint> touch = _touches.get(i);
                if (startTime <= touch.get(0).time && touch.get(touch.size() - 1).time <= endTime) {
                    touches.add(touch);
                }
            }
        }
        return touches;
    }

    // Returns the touches since startTime
    public ArrayList<ArrayList<TimePoint>> getTouchesMs(long startTime) {
        ArrayList<ArrayList<TimePoint>> touches = new ArrayList<ArrayList<TimePoint>>();
        for (int i = this.getSize() - 1; i >= 0; i--) {
            if (i >= 0) {
                ArrayList<TimePoint> touch = _touches.get(i);
                if (startTime <= touch.get(0).time) {
                    touches.add(touch);
                }
            }
        }
        return touches;
    }

    // Get the path at the i-th index back from the most recent (the latest path at index 0)
    /*
    public ArrayList<TimePoint> get(int idxAgo) {
        return _touches.get(_touches.size() - idxAgo - 1);
    }
    */

    // Remove the path at the i-th index back from the most recent (the latest path at index 0)
    /*
    public void removeAt(int idxAgo) {
        _touches.remove(_touches.size() - idxAgo - 1);
    }
    */
    
    public double getTouchDirection() {
        if (_touches.size() == 0) { return 0; }
        double sum = 0;
        int n = this._touches.size();
        for (ArrayList<TimePoint> tch: this._touches) {
            sum += calculateTouchDirection(tch);
        }
        return sum / n;
    }

    public double getTouchDirection(int lastN) {
        if (_touches.size() < lastN) { return 0; }
        double sum = 0;
        int n = this._touches.size();
        for (int i = 0; i < lastN; i++) {
            sum += calculateTouchDirection(this._touches.get(n - i - 1));
        }
        return sum / lastN;
    }

    public double getTouchDirectionMs(long startTime, long endTime) {
        if (_touches.size() == 0) { return 0; }
        double sum = 0;
        int count = 0;
        for (ArrayList<TimePoint> tch: this._touches) {
            TimePoint tch0 = tch.get(0);
            TimePoint tchN = tch.get(tch.size() - 1);
            if (startTime <= tch0.time && tchN.time <= endTime) {
                sum += calculateTouchDirection(tch);
                count++;
            }
        }
        return count == 0 ? 0 : sum / count;
    }

    public double getTouchDirectionMs(long startTime) {
        if (_touches.size() == 0) { return 0; }
        double sum = 0;
        int count = 0;
        for (ArrayList<TimePoint> tch: this._touches) {
            TimePoint tch0 = tch.get(0);
            TimePoint tchN = tch.get(tch.size() - 1);
            if (startTime <= tch0.time) {
                sum += calculateTouchDirection(tch);
                count++;
            }
        }
        return count == 0 ? 0 : sum / count;
    }

    public double getTouchVariability() {
        if (_touches.size() == 0) { return 0; }
        double sum = 0;
        int n = this._touches.size();
        for (ArrayList<TimePoint> tch: this._touches) {
            sum += calculateTouchVariability(tch);
        }
        return sum / n;
    }

    public double getTouchVariability(int lastN) {
        if (_touches.size() < lastN) { return 0; }
        double sum = 0;
        int n = this._touches.size();
        for (int i = 0; i < lastN; i++) {
            sum += calculateTouchVariability(this._touches.get(n - i - 1));
        }
        return sum / lastN;
    }

    public double getTouchVariabilityMs(long startTime, long endTime) {
        if (_touches.size() == 0) { return 0; }
        double sum = 0;
        int count = 0;
        for (ArrayList<TimePoint> tch: this._touches) {
            TimePoint tch0 = tch.get(0);
            TimePoint tchN = tch.get(tch.size() - 1);
            if (startTime <= tch0.time && tchN.time <= endTime) {
                sum += calculateTouchVariability(tch);
                count++;
            }
        }
        return count == 0 ? 0 : sum / count;
    }

    public double getTouchVariabilityMs(long startTime) {
        if (_touches.size() == 0) { return 0; }
        double sum = 0;
        int count = 0;
        for (ArrayList<TimePoint> tch: this._touches) {
            TimePoint tch0 = tch.get(0);
            TimePoint tchN = tch.get(tch.size() - 1);
            if (startTime <= tch0.time) {
                sum += calculateTouchVariability(tch);
                count++;
            }
        }
        return count == 0 ? 0 : sum / count;
    }

    public double getTouchDrift() {
        if (_touches.size() == 0) { return 0; }
        double sum = 0;
        int n = this._touches.size();
        for (ArrayList<TimePoint> tch: this._touches) {
            sum += calculateTouchDrift(tch);
        }
        return sum / n;
    }

    public double getTouchDrift(int lastN) {
        if (_touches.size() < lastN) { return 0; }
        double sum = 0;
        int n = this._touches.size();
        for (int i = 0; i < lastN; i++) {
            sum += calculateTouchDrift(this._touches.get(n - i - 1));
        }
        return sum / lastN;
    }

    public double getTouchDriftMs(long startTime, long endTime) {
        if (_touches.size() == 0) { return 0; }
        double sum = 0;
        int count = 0;
        for (ArrayList<TimePoint> tch: this._touches) {
            TimePoint tch0 = tch.get(0);
            TimePoint tchN = tch.get(tch.size() - 1);
            if (startTime <= tch0.time && tchN.time <= endTime) {
                sum += calculateTouchDrift(tch);
                count++;
            }
        }
        return count == 0 ? 0 : sum / count;
    }

    public double getTouchDriftMs(long startTime) {
        if (_touches.size() == 0) { return 0; }
        double sum = 0;
        int count = 0;
        for (ArrayList<TimePoint> tch: this._touches) {
            TimePoint tch0 = tch.get(0);
            TimePoint tchN = tch.get(tch.size() - 1);
            if (startTime <= tch0.time) {
                sum += calculateTouchDrift(tch);
                count++;
            }
        }
        return count == 0 ? 0 : sum / count;
    }

    public double getTouchDuration() {
        if (_touches.size() == 0) { return 0; }
        double sum = 0;
        int n = this._touches.size();
        for (ArrayList<TimePoint> tch: this._touches) {
            sum += calculateTouchDuration(tch);
        }
        return sum / n;
    }

    public double getTouchDuration(int lastN) {
        if (_touches.size() < lastN) { return 0; }
        double sum = 0;
        int n = this._touches.size();
        for (int i = 0; i < lastN; i++) {
            sum += calculateTouchDuration(this._touches.get(n - i - 1));
        }
        return sum / lastN;
    }

    public double getTouchDurationMs(long startTime, long endTime) {
        if (_touches.size() == 0) { return 0; }
        double sum = 0;
        int count = 0;
        for (ArrayList<TimePoint> tch: this._touches) {
            TimePoint tch0 = tch.get(0);
            TimePoint tchN = tch.get(tch.size() - 1);
            if (startTime <= tch0.time && tchN.time <= endTime) {
                sum += calculateTouchDuration(tch);
                count++;
            }
        }
        return count == 0 ? 0 : sum / count;
    }

    public double getTouchDurationMs(long startTime) {
        if (_touches.size() == 0) { return 0; }
        double sum = 0;
        int count = 0;
        for (ArrayList<TimePoint> tch: this._touches) {
            TimePoint tch0 = tch.get(0);
            TimePoint tchN = tch.get(tch.size() - 1);
            if (startTime <= tch0.time) {
                sum += calculateTouchDuration(tch);
                count++;
            }
        }
        return count == 0 ? 0 : sum / count;
    }

    public double getTouchExtent() {
        if (_touches.size() == 0) { return 0; }
        double sum = 0;
        int n = this._touches.size();
        for (ArrayList<TimePoint> tch: this._touches) {
            sum += calculateTouchExtent(tch);
        }
        return sum / n;
    }

    public double getTouchExtent(int lastN) {
        if (_touches.size() < lastN) { return 0; }
        double sum = 0;
        int n = this._touches.size();
        for (int i = 0; i < lastN; i++) {
            sum += calculateTouchExtent(this._touches.get(n - i - 1));
        }
        return sum / lastN;
    }

    public double getTouchExtentMs(long startTime, long endTime) {
        if (_touches.size() == 0) { return 0; }
        double sum = 0;
        int count = 0;
        for (ArrayList<TimePoint> tch: this._touches) {
            TimePoint tch0 = tch.get(0);
            TimePoint tchN = tch.get(tch.size() - 1);
            if (startTime <= tch0.time && tchN.time <= endTime) {
                sum += calculateTouchExtent(tch);
                count++;
            }
        }
        return count == 0 ? 0 : sum / count;
    }

    public double getTouchExtentMs(long startTime) {
        if (_touches.size() == 0) { return 0; }
        double sum = 0;
        int count = 0;
        for (ArrayList<TimePoint> tch: this._touches) {
            TimePoint tch0 = tch.get(0);
            TimePoint tchN = tch.get(tch.size() - 1);
            if (startTime <= tch0.time) {
                sum += calculateTouchExtent(tch);
                count++;
            }
        }
        return count == 0 ? 0 : sum / count;
    }

    public double getTouchAreaChange(boolean signed) {
        if (_touches.size() == 0) { return 0; }
        double sum = 0;
        int n = this._touches.size();
        for (ArrayList<TimePoint> tch: this._touches) {
            sum += calculateTouchAreaChange(tch, signed);
        }
        return sum / n;
    }

    public double getTouchAreaChange(int lastN, boolean signed) {
        if (_touches.size() < lastN) { return 0; }
        double sum = 0;
        int n = this._touches.size();
        for (int i = 0; i < lastN; i++) {
            sum += calculateTouchAreaChange(this._touches.get(n - i - 1), signed);
        }
        return sum / lastN;
    }

    public double getTouchAreaChangeMs(long startTime, long endTime, boolean signed) {
        if (_touches.size() == 0) { return 0; }
        double sum = 0;
        int count = 0;
        for (ArrayList<TimePoint> tch: this._touches) {
            TimePoint tch0 = tch.get(0);
            TimePoint tchN = tch.get(tch.size() - 1);
            if (startTime <= tch0.time && tchN.time <= endTime) {
                sum += calculateTouchAreaChange(tch, signed);
                count++;
            }
        }
        return count == 0 ? 0 : sum / count;
    }

    public double getTouchAreaChangeMs(long startTime, boolean signed) {
        if (_touches.size() == 0) { return 0; }
        double sum = 0;
        int count = 0;
        for (ArrayList<TimePoint> tch: this._touches) {
            TimePoint tch0 = tch.get(0);
            TimePoint tchN = tch.get(tch.size() - 1);
            if (startTime <= tch0.time) {
                sum += calculateTouchAreaChange(tch, signed);
                count++;
            }
        }
        return count == 0 ? 0 : sum / count;
    }

    public double getTouchAreaVariability() {
        if (_touches.size() == 0) { return 0; }
        double sum = 0;
        int n = this._touches.size();
        for (ArrayList<TimePoint> tch: this._touches) {
            sum += calculateTouchAreaVariability(tch);
        }
        return sum / n;
    }

    public double getTouchAreaVariability(int lastN) {
        if (_touches.size() < lastN) { return 0; }
        double sum = 0;
        int n = this._touches.size();
        for (int i = 0; i < lastN; i++) {
            sum += calculateTouchAreaVariability(this._touches.get(n - i - 1));
        }
        return sum / lastN;
    }

    public double getTouchAreaVariabilityMs(long startTime, long endTime) {
        if (_touches.size() == 0) { return 0; }
        double sum = 0;
        int count = 0;
        for (ArrayList<TimePoint> tch: this._touches) {
            TimePoint tch0 = tch.get(0);
            TimePoint tchN = tch.get(tch.size() - 1);
            if (startTime <= tch0.time && tchN.time <= endTime) {
                sum += calculateTouchAreaVariability(tch);
                count++;
            }
        }
        return count == 0 ? 0 : sum / count;
    }

    public double getTouchAreaVariabilityMs(long startTime) {
        if (_touches.size() == 0) { return 0; }
        double sum = 0;
        int count = 0;
        for (ArrayList<TimePoint> tch: this._touches) {
            TimePoint tch0 = tch.get(0);
            TimePoint tchN = tch.get(tch.size() - 1);
            if (startTime <= tch0.time) {
                sum += calculateTouchAreaVariability(tch);
                count++;
            }
        }
        return count == 0 ? 0 : sum / count;
    }

    public double getTouchAreaDeviation() {
        if (_touches.size() == 0) { return 0; }
        double sum = 0;
        int n = this._touches.size();
        for (ArrayList<TimePoint> tch: this._touches) {
            sum += calculateTouchAreaDeviation(tch);
        }
        return sum / n;
    }

    public double getTouchAreaDeviation(int lastN) {
        if (_touches.size() < lastN) { return 0; }
        double sum = 0;
        int n = this._touches.size();
        for (int i = 0; i < lastN; i++) {
            sum += calculateTouchAreaDeviation(this._touches.get(n - i - 1));
        }
        return sum / lastN;
    }

    public double getTouchAreaDeviationMs(long startTime, long endTime) {
        if (_touches.size() == 0) { return 0; }
        double sum = 0;
        int count = 0;
        for (ArrayList<TimePoint> tch: this._touches) {
            TimePoint tch0 = tch.get(0);
            TimePoint tchN = tch.get(tch.size() - 1);
            if (startTime <= tch0.time && tchN.time <= endTime) {
                sum += calculateTouchAreaDeviation(tch);
                count++;
            }
        }
        return count == 0 ? 0 : sum / count;
    }

    public double getTouchAreaDeviationMs(long startTime) {
        if (_touches.size() == 0) { return 0; }
        double sum = 0;
        int count = 0;
        for (ArrayList<TimePoint> tch: this._touches) {
            TimePoint tch0 = tch.get(0);
            TimePoint tchN = tch.get(tch.size() - 1);
            if (startTime <= tch0.time) {
                sum += calculateTouchAreaDeviation(tch);
                count++;
            }
        }
        return count == 0 ? 0 : sum / count;
    }

    public double getTouchAngleChange(boolean signed) {
        if (_touches.size() == 0) { return 0; }
        double sum = 0;
        int n = this._touches.size();
        for (ArrayList<TimePoint> tch: this._touches) {
            sum += calculateTouchAngleChange(tch, signed);
        }
        return sum / n;
    }

    public double getTouchAngleChange(int lastN, boolean signed) {
        if (_touches.size() < lastN) { return 0; }
        double sum = 0;
        int n = this._touches.size();
        for (int i = 0; i < lastN; i++) {
            sum += calculateTouchAngleChange(this._touches.get(n - i - 1), signed);
        }
        return sum / lastN;
    }

    public double getTouchAngleChangeMs(long startTime, long endTime, boolean signed) {
        if (_touches.size() == 0) { return 0; }
        double sum = 0;
        int count = 0;
        for (ArrayList<TimePoint> tch: this._touches) {
            TimePoint tch0 = tch.get(0);
            TimePoint tchN = tch.get(tch.size() - 1);
            if (startTime <= tch0.time && tchN.time <= endTime) {
                sum += calculateTouchAngleChange(tch, signed);
                count++;
            }
        }
        return count == 0 ? 0 : sum / count;
    }

    public double getTouchAngleChangeMs(long startTime, boolean signed) {
        if (_touches.size() == 0) { return 0; }
        double sum = 0;
        int count = 0;
        for (ArrayList<TimePoint> tch: this._touches) {
            TimePoint tch0 = tch.get(0);
            TimePoint tchN = tch.get(tch.size() - 1);
            if (startTime <= tch0.time) {
                sum += calculateTouchAngleChange(tch, signed);
                count++;
            }
        }
        return count == 0 ? 0 : sum / count;
    }

    public double getTouchAngleVariability() {
        if (_touches.size() == 0) { return 0; }
        double sum = 0;
        int n = this._touches.size();
        for (ArrayList<TimePoint> tch: this._touches) {
            sum += calculateTouchAngleVariability(tch);
        }
        return sum / n;
    }

    public double getTouchAngleVariability(int lastN) {
        if (_touches.size() < lastN) { return 0; }
        double sum = 0;
        int n = this._touches.size();
        for (int i = 0; i < lastN; i++) {
            sum += calculateTouchAngleVariability(this._touches.get(n - i - 1));
        }
        return sum / lastN;
    }

    public double getTouchAngleVariabilityMs(long startTime, long endTime) {
        if (_touches.size() == 0) { return 0; }
        double sum = 0;
        int count = 0;
        for (ArrayList<TimePoint> tch: this._touches) {
            TimePoint tch0 = tch.get(0);
            TimePoint tchN = tch.get(tch.size() - 1);
            if (startTime <= tch0.time && tchN.time <= endTime) {
                sum += calculateTouchAngleVariability(tch);
                count++;
            }
        }
        return count == 0 ? 0 : sum / count;
    }

    public double getTouchAngleVariabilityMs(long startTime) {
        if (_touches.size() == 0) { return 0; }
        double sum = 0;
        int count = 0;
        for (ArrayList<TimePoint> tch: this._touches) {
            TimePoint tch0 = tch.get(0);
            TimePoint tchN = tch.get(tch.size() - 1);
            if (startTime <= tch0.time) {
                sum += calculateTouchAngleVariability(tch);
                count++;
            }
        }
        return count == 0 ? 0 : sum / count;
    }

    public double getTouchAngleDeviation() {
        if (_touches.size() == 0) { return 0; }
        double sum = 0;
        int n = this._touches.size();
        for (ArrayList<TimePoint> tch: this._touches) {
            sum += calculateTouchAngleDeviation(tch);
        }
        return sum / n;
    }

    public double getTouchAngleDeviation(int lastN) {
        if (_touches.size() < lastN) { return 0; }
        double sum = 0;
        int n = this._touches.size();
        for (int i = 0; i < lastN; i++) {
            sum += calculateTouchAngleDeviation(this._touches.get(n - i - 1));
        }
        return sum / lastN;
    }

    public double getTouchAngleDeviationMs(long startTime, long endTime) {
        if (_touches.size() == 0) { return 0; }
        double sum = 0;
        int count = 0;
        for (ArrayList<TimePoint> tch: this._touches) {
            TimePoint tch0 = tch.get(0);
            TimePoint tchN = tch.get(tch.size() - 1);
            if (startTime <= tch0.time && tchN.time <= endTime) {
                sum += calculateTouchAngleDeviation(tch);
                count++;
            }
        }
        return count == 0 ? 0 : sum / count;
    }

    public double getTouchAngleDeviationMs(long startTime) {
        if (_touches.size() == 0) { return 0; }
        double sum = 0;
        int count = 0;
        for (ArrayList<TimePoint> tch: this._touches) {
            TimePoint tch0 = tch.get(0);
            TimePoint tchN = tch.get(tch.size() - 1);
            if (startTime <= tch0.time) {
                sum += calculateTouchAngleDeviation(tch);
                count++;
            }
        }
        return count == 0 ? 0 : sum / count;
    }

    /**** Helper Functions ****/
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

    private double calculateTouchDirection(ArrayList<TimePoint> tch) {
        TimePoint startPt = tch.get(0);
        TimePoint endPt = tch.get(tch.size() - 1);
        return Math.PI * 2.0 - angle(startPt, endPt, true);
    }

    public double calculateTouchVariability(ArrayList<TimePoint> tch) {
        double sum = 0;
        for (int idx = 0; idx < tch.size() - 1; idx++) {
            sum += distance(tch.get(idx), tch.get(idx + 1));
        }
        return sum;
    }

    private double calculateTouchDrift(ArrayList<TimePoint> tch) {
        TimePoint startPt = tch.get(0);
        TimePoint endPt = tch.get(tch.size() - 1);
        return distance(startPt, endPt);
    }

    private double calculateTouchDuration(ArrayList<TimePoint> tch) {
        TimePoint startPt = tch.get(0);
        TimePoint endPt = tch.get(tch.size() - 1);
        return endPt.time - startPt.time;
    }

    private double calculateTouchExtent(ArrayList<TimePoint> tch) {
        double maxDist = 0;
        for (TimePoint pt1: tch) {
            for (TimePoint pt2: tch) {
                double dist = distance(pt1, pt2);
                maxDist = Math.max(dist, maxDist);
            }
        }
        return maxDist;
    }

    private double calculateTouchAreaChange(ArrayList<TimePoint> tch, boolean signed) {
        TimePoint startPt = tch.get(0);
        TimePoint endPt = tch.get(tch.size() - 1);
        if (signed) { return endPt.contactArea - startPt.contactArea; }
        return Math.abs(endPt.contactArea - startPt.contactArea);
    }

    private double calculateTouchAreaVariability(ArrayList<TimePoint> tch) {
        double sum = 0;
        for (int i = 0; i < tch.size() - 1; i++) {
            sum += Math.abs(tch.get(i + 1).contactArea - tch.get(i).contactArea);
        }
        return sum;
    }

    private double calculateTouchAreaDeviation(ArrayList<TimePoint> tch) {
        int n = tch.size();
        double sum = 0;
        for (TimePoint pt : tch) {
            sum += pt.contactArea;
        }
        double avg = sum / n;
        double sumSqr = 0;
        for (TimePoint pt : tch) {
            sumSqr += (pt.contactArea - avg) * (pt.contactArea - avg);
        }
        return Math.sqrt(sumSqr / n);
    }

    private double calculateTouchAngleChange(ArrayList<TimePoint> tch, boolean signed) {
        TimePoint startPt = tch.get(0);
        TimePoint endPt = tch.get(tch.size() - 1);
        if (signed) { return endPt.orientation - startPt.orientation; }
        return Math.abs(endPt.orientation - startPt.orientation);
    }

    private double calculateTouchAngleVariability(ArrayList<TimePoint> tch) {
        double sum = 0;
        for (int i = 0; i < tch.size() - 1; i++) {
            sum += Math.abs(tch.get(i + 1).orientation - tch.get(i).orientation);
        }
        return sum;
    }

    private double calculateTouchAngleDeviation(ArrayList<TimePoint> tch) {
        int n = tch.size();
        double sum = 0;
        for (TimePoint pt : tch) {
            sum += pt.orientation;
        }
        double avg = sum / n;
        double sumSqr = 0;
        for (TimePoint pt : tch) {
            sumSqr += (pt.orientation - avg) * (pt.orientation - avg);
        }
        return Math.sqrt(sumSqr / n);
    }

}
