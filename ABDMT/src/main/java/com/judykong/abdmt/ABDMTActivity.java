package com.judykong.abdmt;

import static androidx.constraintlayout.widget.Constraints.TAG;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.method.Touch;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.judykong.abdmt.AbdObserver.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public abstract class ABDMTActivity extends AppCompatActivity // {
    implements SharedPreferences.OnSharedPreferenceChangeListener {

    private ABDMT _abdmt;
    private TouchObserver _touchObserver;
    private GestureObserver _gestureObserver;
    private ActivityObserver _activityObserver;
    private AttentionObserver _attentionObserver;
    private AbilityModeler _abilityModeler;
    private UIAdapter _uiAdapter;

    private ABDMTActivity _activity;
    private Resources _resources;
    private int _layoutId;
    private ViewGroup _root;

    public static final String DETECTED_ACTIVITY = ".DETECTED_ACTIVITY";
    private ActivityRecognitionClient mActivityRecognitionClient;
    private BroadcastReceiver mBroadcastReceiver;
    private GestureDetectorCompat mDetector;

    CameraSource cameraSource;
    List<ABDMTData> abdmtDataList = new ArrayList<>();
    ABDMTDB abdmtDB;
    Gson gson = new Gson();

    private ArrayList<TimePoint> _currPath;
    private Activity _currActivity;

    // Testing
    // public TextView _textView;

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _abdmt = ABDMT.getInstance();
        _touchObserver = _abdmt.getTouchObserver();
        _gestureObserver = _abdmt.getGestureObserver();
        _activityObserver = _abdmt.getActivityObserver();
        _attentionObserver = _abdmt.getAttentionObserver();
        _abilityModeler = _abdmt.getAbilityModeler();
        _uiAdapter = _abdmt.getUiAdapter();

        // Init Database
        abdmtDB = ABDMTDB.getInstance(this);
        _resources = getResources();

        // Activity recognition
        mActivityRecognitionClient = new ActivityRecognitionClient(this);
        mBroadcastReceiver = new ActivityTransitionBroadcastReceiver();
        ((ActivityTransitionBroadcastReceiver) mBroadcastReceiver).activity = this;
        registerReceiver(mBroadcastReceiver, new IntentFilter(ActivityTransitionBroadcastReceiver.INTENT_ACTION));

        // Check permission on start of the app (in addition to resume)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            checkIfAlreadyHaveActivityRecognitionPermission();
            checkIfAlreadyHaveAttentionPermission();
        } else {
            startTransitionUpdate();
        }

        // Check system setting permissions
        checkIfAlreadyHaveSystemSettingPermission();

        // Clear program data
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putString(DETECTED_ACTIVITY, "")
                .apply();

        // Gesture detector
        mDetector = new GestureDetectorCompat(this, new CustomGestureListener());
    }

    @Override
    protected void onStart() {
        // showAdaptedLayout();
        /*
        try
        {
            Thread.sleep(100);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
        */
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        abdmtDataList.clear();
        abdmtDataList.addAll(abdmtDB.mainDao().getAll());

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenUnLock = pm.isScreenOn();

        if (_touchObserver.isActive() && _abdmt.loadDataOnResume()) {
            loadHistoricalTouchRecordsFromDB();
        }

        if (_gestureObserver.isActive() && _abdmt.loadDataOnResume()) {
            loadHistoricalGestureRecordsFromDB();
        }

        if (_activityObserver.isActive()) {
            // _root.postInvalidate();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                checkIfAlreadyHaveActivityRecognitionPermission();
            } else {
                startTransitionUpdate();
            }
            PreferenceManager.getDefaultSharedPreferences(this)
                    .registerOnSharedPreferenceChangeListener(this);

            if (_abdmt.loadDataOnResume()) {
                loadHistoricalActivityRecordsFromDB();
            }
        }

        if (_attentionObserver.isActive()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                checkIfAlreadyHaveAttentionPermission();
            }
            if (isScreenUnLock) {
                initCameraSource();
                if (cameraSource != null) {
                    try {
                        cameraSource.start();
                    } catch (Exception e) {
                        // e.printStackTrace();
                    }
                }
            } else {
                Log.i(TAG, "PLEASE TURN ON SCREEN");
            }
            if (_abdmt.loadDataOnResume()) {
                loadHistoricalAttentionRecordsFromDB();
            }
        }
    }

    @Override
    public void onPause() {

        if (_touchObserver.isActive() && _abdmt.saveDataOnPause()) {
            saveCurrentTouchRecordsToDB();
        }

        if (_gestureObserver.isActive() && _abdmt.saveDataOnPause()) {
            saveCurrentGestureRecordsToDB();
        }

        if (_activityObserver.isActive()) {
            if (_abdmt.saveDataOnPause()) {
                saveCurrentActivityRecordsToDB();
            }
            try {
                unregisterReceiver(mBroadcastReceiver);
                PreferenceManager.getDefaultSharedPreferences(this)
                        .unregisterOnSharedPreferenceChangeListener(this);
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }

        if (_attentionObserver.isActive()) {
            if (_abdmt.saveDataOnPause()) {
                saveCurrentAttentionRecordsToDB();
            }
            try {
                if (cameraSource != null) {
                    cameraSource.stop();
                }
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }

        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (_touchObserver.isActive() || _gestureObserver.isActive()) {
            // Need to handle button clicks here
            _abdmt.add(event);
            this.mDetector.onTouchEvent(event);
            if (event.getAction() == MotionEvent.ACTION_UP && !_abdmt.isTouchPathRecorded()) {
                // _textView.setText(_textView.getText() + "\n" + "RECORDING!");
                _abdmt.recordTouch(); // if no gesture is detected, then assume it's a touch
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void loadHistoricalTouchRecordsFromDB() {
        if (abdmtDataList.size() > 0) {
            Log.i(TAG, "Loading historical touches");
            ABDMTData data = abdmtDataList.get(0);
            _touchObserver.loadTouches(touchGestureRecordFromString(data.getTouches()));
            Log.i(TAG, "" + touchGestureRecordFromString(data.getTouches()));
        }
    }

    private void loadHistoricalGestureRecordsFromDB() {
        if (abdmtDataList.size() > 0) {
            Log.i(TAG, "Loading historical gestures");
            ABDMTData data = abdmtDataList.get(0);
            _gestureObserver.loadGestures(touchGestureRecordFromString(data.getGestures()));
        }
    }

    private void loadHistoricalActivityRecordsFromDB() {
        if (abdmtDataList.size() > 0) {
            Log.i(TAG, "Loading historical activities");
            ABDMTData data = abdmtDataList.get(0);
            _activityObserver.loadActivities(activityRecordFromString(data.getActivities()));
        }
    }

    private void loadHistoricalAttentionRecordsFromDB() {
        if (abdmtDataList.size() > 0) {
            Log.i(TAG, "Loading historical attention");
            ABDMTData data = abdmtDataList.get(0);
            _attentionObserver.loadAttention(attentionRecordFromString(data.getAttention()));
        }
    }

    private void loadHistoricalTouchRecordsFromFile() {
        try {
            String str = loadFromFile("touch_records.txt");
            _touchObserver.loadTouches(touchGestureRecordFromString(str));
            Log.i(TAG, str);
        } catch (IOException e) {
            Log.i(TAG, "Cannot load touch records");
        }
    }

    private void loadHistoricalGestureRecordsFromFile() {
        try {
            String str = loadFromFile("gesture_records.txt");
            _gestureObserver.loadGestures(touchGestureRecordFromString(str));
            Log.i(TAG, str);
        } catch (IOException e) {
            Log.i(TAG, "Cannot load gesture records");
        }
    }

    private void saveCurrentTouchRecordsToDB() {
        if (abdmtDataList.size() == 0) {
            ABDMTData data = new ABDMTData();
            data.setTouches(touchGestureRecordToString(_touchObserver.getTouches()));
            abdmtDB.mainDao().insert(data);
        } else {
            abdmtDB.mainDao().updateTouch(touchGestureRecordToString(_touchObserver.getTouches()));
        }
        abdmtDataList.clear();
        abdmtDataList.addAll(abdmtDB.mainDao().getAll());
        Log.i(TAG, "saveCurrentTouchRecordsToDB: " + abdmtDataList.size());
    }

    private void saveCurrentGestureRecordsToDB() {
        if (abdmtDataList.size() == 0) {
            ABDMTData data = new ABDMTData();
            data.setTouches(touchGestureRecordToString(_gestureObserver.getGestures()));
            abdmtDB.mainDao().insert(data);
        } else {
            abdmtDB.mainDao().updateGesture(touchGestureRecordToString(_gestureObserver.getGestures()));
        }
        abdmtDataList.clear();
        abdmtDataList.addAll(abdmtDB.mainDao().getAll());
        Log.i(TAG, "saveCurrentGestureRecordsToDB: " + abdmtDataList.size());
    }

    private void saveCurrentActivityRecordsToDB() {
        if (abdmtDataList.size() == 0) {
            ABDMTData data = new ABDMTData();
            data.setActivities(activityRecordToString(_activityObserver.getActivities()));
            abdmtDB.mainDao().insert(data);
        } else {
            abdmtDB.mainDao().updateActivity(activityRecordToString(_activityObserver.getActivities()));
        }
        abdmtDataList.clear();
        abdmtDataList.addAll(abdmtDB.mainDao().getAll());
        Log.i(TAG, "saveCurrentActivityRecordsToDB: " + abdmtDataList.size());
    }

    private void saveCurrentAttentionRecordsToDB() {
        if (abdmtDataList.size() == 0) {
            ABDMTData data = new ABDMTData();
            data.setAttention(attentionRecordToString(_attentionObserver.getAttentionRecords()));
            abdmtDB.mainDao().insert(data);
        } else {
            abdmtDB.mainDao().updateAttention(attentionRecordToString(_attentionObserver.getAttentionRecords()));
        }
        abdmtDataList.clear();
        abdmtDataList.addAll(abdmtDB.mainDao().getAll());
        Log.i(TAG, "saveCurrentAttentionRecordsToDB: " + abdmtDataList.size());
    }

    private void saveCurrentTouchRecordsToFile() {
        try {
            String str = touchGestureRecordToString(_touchObserver.getTouches());
            writeToFile("touch_records.txt", str);
            Log.i(TAG, str);
        } catch (IOException e) {
            Log.i(TAG, "Cannot write touch records");
        }
    }

    private void saveCurrentGestureRecordsToFile() {
        try {
            String str = touchGestureRecordToString(_gestureObserver.getGestures());
            writeToFile("gesture_records.txt", str);
            Log.i(TAG, str);
        } catch (IOException e) {
            Log.i(TAG, "Cannot write gesture records");
        }
    }

    private String touchGestureRecordToString(ArrayList<ArrayList<TimePoint>> record) {
        String str = gson.toJson(record);
        return str;
    }

    private ArrayList<ArrayList<TimePoint>> touchGestureRecordFromString(String str) {
        Type type = new TypeToken<ArrayList<ArrayList<TimePoint>>>(){}.getType();
        ArrayList<ArrayList<TimePoint>> record = gson.fromJson(str, type);
        if (record == null) {
            return new ArrayList<ArrayList<TimePoint>>();
        }
        return record;
    }

    private String activityRecordToString(ArrayList<Activity> record) {
        String str = gson.toJson(record);
        return str;
    }

    private ArrayList<Activity> activityRecordFromString(String str) {
        Type type = new TypeToken<ArrayList<Activity>>(){}.getType();
        ArrayList<Activity> record = gson.fromJson(str, type);
        if (record == null) {
            return new ArrayList<Activity>();
        }
        return record;
    }

    private String attentionRecordToString(ArrayList<Attention> record) {
        String str = gson.toJson(record);
        return str;
    }

    private ArrayList<Attention> attentionRecordFromString(String str) {
        Type type = new TypeToken<ArrayList<Attention>>(){}.getType();
        ArrayList<Attention> record = gson.fromJson(str, type);
        if (record == null) {
            return new ArrayList<Attention>();
        }
        return record;
    }

    public String loadFromFile(String fileName) throws IOException {
        File path = getExternalFilesDir(null);
        File file = new File(path, fileName);
        int length = (int) file.length();
        byte[] bytes = new byte[length];
        FileInputStream in = new FileInputStream(file);
        try {
            in.read(bytes);
        } finally {
            in.close();
        }
        String str = new String(bytes);
        return str;
    }

    private void writeToFile(String fileName, String str) throws IOException {
        File path = getExternalFilesDir(null);
        File file = new File(path, fileName);
        FileOutputStream stream = new FileOutputStream(file);
        try {
            stream.write(str.getBytes());
        } finally {
            stream.close();
        }
    }

    // Sets the context for easy reference to the activity object
    public void setContext(ABDMTActivity activity) {
        _activity = activity;
    }

    // Sets the root view
    public void setRoot(ViewGroup root) {
        _root = root;
    }

    // Gets the root view
    public ViewGroup getRoot() {
        return _root;
    }

    // Assign the listener implementing events interface that will receive the events
    public void setActivityChangeListener(ActivityObserver.ActivityChangeListener listener) {
        // _abdmt.activityListener = listener;
        _activityObserver.setListener(listener);
    }

    public void setAttentionChangeListener(AttentionObserver.AttentionChangeListener listener) {
        // _abdmt.attentionListener = listener;
        _attentionObserver.setListener(listener);
    }

    public void onActivityChange() {
        /*
        _textView.setText("Activity Detected!");
        for (ActivityTransitionEvent activity : detectedActivities) {
            int activityType = activity.getActivityType();
            int transitionType = activity.getTransitionType();
            _textView.setText(_textView.getText() + "\n" + _activityObserver.toStringTransitionType(transitionType) + "ing: " + _activityObserver.toStringActivityType(activityType));
        }
        */
    }

    class CustomGestureListener extends GestureDetector.SimpleOnGestureListener {

        // private static final String DEBUG_TAG = "Gestures";

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            _abdmt.recordGesture();
            // _textView.setText(_textView.getText() + "\n" + "Fling!");
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            _abdmt.recordTouch();
            // _textView.setText(_textView.getText() + "\n" + "Single Tap!");
            return true;
        }
    }

    /******** Helper Functions ********/

    public synchronized void showAdaptedLayout() {

        // Example 1: Update UI according to updated user ability profile
        // The issue with EnlargeTargets is that we don't know if it'll mess up the interface
        /*
        if (_abdmt.getAbilityProfile().hasTremor()) {
            _abdmt.uiAdapt().changeTargetSize(_abdmt.touch().getTouchExtent() * 1.2);
        }
        */

        // Example 2: Load UI resource that matches user ability
        // This method has its issue when it comes to multi-page apps:
        // Developers have to pre-write the "tremor / etc." version of every single page
        /*
        if (_abdmt.getAbilityProfile().hasTremor()) {
            if (_abdmt.getLayout() != R.layout.tremor_example) {
                _abdmt.setLayout(R.layout.tremor_example);
                _abdmt.uiAdapt().loadUISource(this, R.layout.tremor_example);
                registerButtons();
            }
        }
        */

        // Example 3: Load UI transformation that matches user ability
        /*
        if (_abdmt.getAbilityProfile().hasTremor()) {
            if (_abdmt.getLayout() != R.layout.tremor_example) {
                _abdmt.setLayout(R.layout.tremor_example);
                _abdmt.uiAdapt().loadUITransformation(this, R.raw.tremor);
            }
        }
        */

        // Example 4: Filter UI components and leave only the essential ones
        /*
        if (_abdmt.getAbilityProfile().hasTremor()) {
            if (_abdmt.getLayout() != R.layout.tremor_example) {
                _abdmt.setLayout(R.layout.tremor_example);
                _abdmt.uiAdapt().filterByTag("high");
            }
        }
        */
    }

    /*******************************************/
    /********** Activity Recognition ***********/
    /*******************************************/

    private void checkIfAlreadyHaveActivityRecognitionPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            startTransitionUpdate();
        } else {
            requestActivityRecognitionPermissions();
        }
    }

    private void checkIfAlreadyHaveAttentionPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (result != PackageManager.PERMISSION_GRANTED) {
            requestAttentionPermissions();
        }
    }

    private void checkIfAlreadyHaveSystemSettingPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(this))
                return;
            else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                this.startActivity(intent);
            }
        }
    }

    private void requestActivityRecognitionPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                1);
    }

    private void requestAttentionPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                2);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) { // Activity Recognition Permission
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startTransitionUpdate();
            } else {
                Toast.makeText(this, "Recognition permission denied", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 2) { // Camera Permission
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initCameraSource();
                if (cameraSource != null) {
                    try {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        } // This is not needed, but only to avoid warnings
                        cameraSource.start();
                    } catch (Exception e) {
                        // e.printStackTrace();
                        Log.i(TAG, "Camera could not start");
                    }
                }
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(DETECTED_ACTIVITY)) {
            updateDetectedActivitiesList();
        }
    }

    public void startTransitionUpdate() {
        ActivityTransitionRequest request = buildTransitionRequest();
        PendingIntent pendingIntent = getActivityDetectionPendingIntent();
        Task task = mActivityRecognitionClient // ActivityRecognition.getClient(this)
                .requestActivityTransitionUpdates(request, pendingIntent);
        task.addOnSuccessListener(
                result -> {
                      updateDetectedActivitiesList();
                });
    }

    public void updateDetectedActivitiesList() {
        ArrayList<ActivityTransitionEvent> detectedActivities = ActivityTransitionBroadcastReceiver.detectedActivitiesFromJson(
                PreferenceManager.getDefaultSharedPreferences(this)
                        .getString(DETECTED_ACTIVITY, ""));
        updateActivities(detectedActivities);
    }

    public void updateActivities(ArrayList<ActivityTransitionEvent> detectedActivities) {
        Activity previousActivity = _activityObserver.getCurrentActivity();
        ActivityEnum previousActivityEnum = previousActivity == null ? null : previousActivity.name;
        _abdmt.add(detectedActivities);
        ActivityObserver.ActivityChangeListener listener = _activityObserver.getListener();
        if (listener != null && detectedActivities.size() > 0) {
            switch (detectedActivities.get(detectedActivities.size() - 1).getActivityType()) {
                case DetectedActivity.STILL:
                    listener.onActivityChange(previousActivityEnum, ActivityEnum.STILL);
                    listener.onStill();
                    break;
                case DetectedActivity.WALKING:
                    listener.onActivityChange(previousActivityEnum, ActivityEnum.WALKING);
                    listener.onWalk();
                    break;
                case DetectedActivity.RUNNING:
                    listener.onActivityChange(previousActivityEnum, ActivityEnum.RUNNING);
                    listener.onRun();
                case DetectedActivity.ON_BICYCLE:
                    listener.onActivityChange(previousActivityEnum, ActivityEnum.CYCLING);
                    listener.onCycle();
                case DetectedActivity.IN_VEHICLE:
                    listener.onActivityChange(previousActivityEnum, ActivityEnum.IN_VEHICLE);
                    listener.onDrive();
                    break;
                default:
                    break;
            }
        }

        // TESTING
        /*
        _textView.setText("Activity Detected!");
        for (ActivityTransitionEvent activity : detectedActivities) {
            int activityType = activity.getActivityType();
            int transitionType = activity.getTransitionType();
            _textView.setText(_textView.getText() + "\n" + _activityObserver.toStringTransitionType(transitionType) + "ing: " + _activityObserver.toStringActivityType(activityType));
        }
        */

        //        Task task = _abdmt.add(detectedActivities);
//        task.addOnCompleteListener(
//            completedTask -> {
//                _textView.setText("Activity Detected!");
//                for (ActivityTransitionEvent activity : detectedActivities) {
//                    int activityType = activity.getActivityType();
//                    int transitionType = activity.getTransitionType();
//                    _textView.setText(_textView.getText() + "\n" + _activityObserver.toStringTransitionType(transitionType) + "ing: " + _activityObserver.toStringActivityType(activityType));
//                }
//                if (_activityObserver.getCurrentActivity().name == _activityObserver.WALKING) {
//                    _listener.onWalk();
//                } else if (_activityObserver.getCurrentActivity().name == _activityObserver.IN_VEHICLE) {
//                    _listener.onDrive();
//                }
//            }
//        );
//
//        try {
//            Tasks.await(task);
//            if (_activityObserver.getCurrentActivity().name == _activityObserver.WALKING) {
//                _listener.onWalk();
//            } else if (_activityObserver.getCurrentActivity().name == _activityObserver.IN_VEHICLE) {
//                _listener.onDrive();
//            }
//        } catch (Exception e) { }

    }

    ActivityTransitionRequest buildTransitionRequest() {
        List transitions = new ArrayList<>();
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());
        return new ActivityTransitionRequest(transitions);
    }

    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(this, ActivityTransitionBroadcastReceiver.class);
        intent.setAction(ActivityTransitionBroadcastReceiver.INTENT_ACTION);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
    }

    /*******************************************/
    /************** Eye Gaze Tracker ***********/
    /*******************************************/

    private void initCameraSource() {
        try {
            FaceDetector detector = new FaceDetector.Builder(this)
                    .setTrackingEnabled(true)
                    .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                    .setMode(FaceDetector.FAST_MODE)
                    .build();
            detector.setProcessor(new MultiProcessor.Builder(new EyeGazeTrackerDaemon(this)).build());
            cameraSource = new CameraSource.Builder(this, detector)
                    .setRequestedPreviewSize(1024, 768)
                    .setFacing(CameraSource.CAMERA_FACING_FRONT)
                    .setRequestedFps(30.0f)
                    .build();

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            cameraSource.start();
        } catch (Exception e) {
            // Toast.makeText(_activity, e.getMessage(), Toast.LENGTH_SHORT).show();
            // e.printStackTrace();
        }
    }

    /******** Helper Functions ********/

    // public abstract void registerLayout();

    /*
    public void setValue(AppCompatActivity activity) {
        _activity = activity;
    }

    public void setValue(int layoutId) {
        _layoutId = layoutId;
    }

    public void setValue(ConstraintLayout root) {
        _root = root;
    }

    public void setUserThreshold(int threshold) {
        _userThreshold = threshold;
    }
    */
}
