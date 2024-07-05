package com.judykong.cyclingnotification_notoolkit;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.Task;
import java.util.ArrayList;
import java.util.List;
public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener{
    public static final String DETECTED_ACTIVITY = ".DETECTED_ACTIVITY";
    private ActivityRecognitionClient mActivityRecognitionClient;
    private BroadcastReceiver mBroadcastReceiver;
    View smallView, smallViewTitleText, smallViewContentText;
    View largeView, largeViewTitleText, largeViewContentText;
    Button btnCancel, btnIgnore, btnReply;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        smallView = findViewById(R.id.smallView);
        smallViewTitleText = findViewById(R.id.smallViewTitleText);
        smallViewContentText = findViewById(R.id.smallViewContentText);
        btnCancel = findViewById(R.id.btnCancel);
        btnIgnore = findViewById(R.id.btnIgnore);
        btnReply = findViewById(R.id.btnReply);
        largeView = findViewById(R.id.largeView);
        largeViewTitleText = findViewById(R.id.largeViewTitleText);
        largeViewContentText = findViewById(R.id.largeViewContentText);
        btnCancel.setOnClickListener(this);
        smallView.setVisibility(View.VISIBLE);
        smallViewTitleText.setVisibility(View.VISIBLE);
        smallViewContentText.setVisibility(View.VISIBLE);
        mActivityRecognitionClient = new ActivityRecognitionClient(this);
        mBroadcastReceiver = new ActivityTransitionBroadcastReceiver();
        ((ActivityTransitionBroadcastReceiver) mBroadcastReceiver).activity = this;
        registerReceiver(mBroadcastReceiver, new IntentFilter(ActivityTransitionBroadcastReceiver.INTENT_ACTION));
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putString(DETECTED_ACTIVITY, "")
                .apply();
    }
    @Override
    public void onResume() {
        super.onResume();
        findViewById(R.id.root).postInvalidate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION);
            if (result == PackageManager.PERMISSION_GRANTED) {
                startTransitionUpdate();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACTIVITY_RECOGNITION},1);
            }
        } else {
            startTransitionUpdate();
        }
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            ViewGroup.LayoutParams params;
            params = btnCancel.getLayoutParams();
            params.width = (int) (btnCancel.getWidth() * 1.03);
            params.height = (int) (btnCancel.getHeight() * 1.03);
            btnCancel.setLayoutParams(params);
            params = btnIgnore.getLayoutParams();
            params.width = (int) (btnIgnore.getWidth() * 1.03);
            params.height = (int) (btnIgnore.getHeight() * 1.03);
            btnIgnore.setLayoutParams(params);
            params = btnReply.getLayoutParams();
            params.width = (int) (btnReply.getWidth() * 1.03);
            params.height = (int) (btnReply.getHeight() * 1.03);
            btnReply.setLayoutParams(params);
            params = largeView.getLayoutParams();
            params.width = (int) (largeView.getWidth() * 1.03);
            params.height = (int) (largeView.getHeight() * 1.03);
            largeView.setLayoutParams(params);
            params = largeViewTitleText.getLayoutParams();
            params.width = (int) (largeViewTitleText.getWidth() * 1.03);
            params.height = (int) (largeViewTitleText.getHeight() * 1.03);
            largeViewTitleText.setLayoutParams(params);
            params = largeViewContentText.getLayoutParams();
            params.width = (int) (largeViewContentText.getWidth() * 1.03);
            params.height = (int) (largeViewContentText.getHeight() * 1.03);
            largeViewContentText.setLayoutParams(params);
            float fontSize;
            fontSize = btnCancel.getTextSize();
            btnCancel.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize * (float) 1.03);
            fontSize = btnIgnore.getTextSize();
            btnIgnore.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize * (float) 1.03);
            fontSize = btnReply.getTextSize();
            btnReply.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize * (float) 1.03);
        }
        return super.onTouchEvent(event);
    }
    @Override
    public void onClick(View view){
        btnCancel.setVisibility(View.INVISIBLE);
        btnReply.setVisibility(View.INVISIBLE);
        btnIgnore.setVisibility(View.INVISIBLE);
        largeView.setVisibility(View.INVISIBLE);
        largeViewContentText.setVisibility(View.INVISIBLE);
        largeViewTitleText.setVisibility(View.INVISIBLE);
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
        Task task = mActivityRecognitionClient.requestActivityTransitionUpdates(request, pendingIntent);
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
        if (detectedActivities.size() > 0) {
            switch (detectedActivities.get(detectedActivities.size() - 1).getActivityType()) {
                case DetectedActivity.WALKING:
                    smallView.setVisibility(View.INVISIBLE);
                    smallViewTitleText.setVisibility(View.INVISIBLE);
                    smallViewContentText.setVisibility(View.INVISIBLE);
                    btnCancel.setVisibility(View.VISIBLE);
                    btnReply.setVisibility(View.VISIBLE);
                    btnIgnore.setVisibility(View.VISIBLE);
                    largeView.setVisibility(View.VISIBLE);
                    largeViewContentText.setVisibility(View.VISIBLE);
                    largeViewTitleText.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
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
        return new ActivityTransitionRequest(transitions);
    }
    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(this, ActivityTransitionBroadcastReceiver.class);
        intent.setAction(ActivityTransitionBroadcastReceiver.INTENT_ACTION);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}