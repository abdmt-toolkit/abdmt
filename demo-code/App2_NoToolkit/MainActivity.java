package com.judykong.catchpokemon_notoolkit;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.view.Surface;
import android.view.TextureView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.Task;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class MainActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener{
    private CameraCaptureSession myCameraCaptureSession;
    private String myCameraID;
    private CameraManager myCameraManager;
    private CameraDevice myCameraDevice;
    private ImageView myPokemon;
    private TextureView myTextrureView;
    private TextView myTextView;
    private View myView;
    private ImageView myPokemonBall;
    private CaptureRequest.Builder myCaptureRequestBuilder;
    private Button btnCapture;
    private Button btnNull;
    private Button backButton;
    private ImageView newPokemonView;
    private TextView newTextView;
    private float prevX, prevY;
    private boolean isCameraOn;
    private boolean isHoldingPokemonBall;
    String currentActivity = "";
    int gestures = 0;
    public static final String DETECTED_ACTIVITY = ".DETECTED_ACTIVITY";
    private ActivityRecognitionClient mActivityRecognitionClient;
    private BroadcastReceiver mBroadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        btnCapture = findViewById(R.id.btnTakePicture);
        btnNull = findViewById(R.id.btnTakePicture2);
        myTextrureView = findViewById(R.id.textureView);
        myTextView = findViewById(R.id.textView);
        myCameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        myPokemon = findViewById(R.id.pokemonView);
        myPokemonBall = findViewById(R.id.imageView);
        myView = findViewById(R.id.view);
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraPreview();
            }
        });
        myPokemonBall.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event != null && event.getAction() == MotionEvent.ACTION_DOWN) {
                    isHoldingPokemonBall = true;
                }
                return true;
            }
        });
        backButton = findViewById(R.id.btnGoBack2);
        newPokemonView = findViewById(R.id.pokemonView3);
        newTextView = findViewById(R.id.textView2);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButton.setVisibility(View.INVISIBLE);
                newPokemonView.setVisibility(View.INVISIBLE);
                newTextView.setVisibility(View.INVISIBLE);
                myPokemon.setVisibility(View.VISIBLE);
                myPokemonBall.setVisibility(View.VISIBLE);
                btnCapture.setVisibility(View.VISIBLE);
                btnNull.setVisibility(View.VISIBLE);
                myTextView.setVisibility(View.VISIBLE);
            }
        });
        isCameraOn = false;
        isHoldingPokemonBall = false;
        openCamera();
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
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event != null) {
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    prevX = x;
                    prevY = y;
                    if (currentActivity == "WALKING") {
                        gestures += 1;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (isHoldingPokemonBall) {
                        myPokemonBall.setTranslationX(x - prevX);
                        myPokemonBall.setTranslationY(y - prevY);
                    }
                    if (currentActivity == "WALKING") {
                        gestures += 1;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (y <= myPokemon.getY() + myPokemon.getHeight()) {
                        backButton.setVisibility(View.VISIBLE);
                        newPokemonView.setVisibility(View.VISIBLE);
                        newTextView.setVisibility(View.VISIBLE);
                        myPokemon.setVisibility(View.INVISIBLE);
                        myPokemonBall.setVisibility(View.INVISIBLE);
                        btnCapture.setVisibility(View.INVISIBLE);
                        btnNull.setVisibility(View.INVISIBLE);
                        myTextView.setVisibility(View.INVISIBLE); // startActivity(new Intent(MainActivity.this, PokemonCaptured.class));
                    } else {
                        myTextView.setText("You missed! Reach a little farther!");
                        myTextView.setTextColor(Color.RED);
                    }
                    myPokemonBall.setTranslationX(0);
                    myPokemonBall.setTranslationY(0);
                    isHoldingPokemonBall = false;
                    if (currentActivity == "WALKING") {
                        gestures += 1;
                    }
                    break;
                default:
                    break;
            }
        }
        if (isWalkingWhileUsingGestures()) { // Turn on camera
            if (!isCameraOn) {
                cameraPreview();
                myTextView.setText("Turning on camera \n while you are walking!");
                myTextView.setTextColor(Color.GREEN);
                myTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                ViewGroup.LayoutParams params = myPokemon.getLayoutParams();
                params.width = (int) (myPokemon.getWidth() * 1.1);
                params.height = (int) (myPokemon.getHeight() * 1.1);
                myPokemon.setLayoutParams(params);
            }
        }
        return super.dispatchTouchEvent(event);
    }
    private boolean isWalkingWhileUsingGestures() {
        if (currentActivity != "WALKING") return false;
        return (gestures > 0);
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
                    currentActivity = "WALKING";
                    break;
                default:
                    currentActivity = "";
                    gestures = 0;
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
    private CameraDevice.StateCallback myStateCallBack = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            myCameraDevice = camera;
        }
        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            myCameraDevice.close();
        }
        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            myCameraDevice.close();
            myCameraDevice = null;
        }
    };
    private void openCamera() {
        try {
            myCameraID = myCameraManager.getCameraIdList()[0];
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            myCameraManager.openCamera(myCameraID, myStateCallBack, null);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void cameraPreview(){
        if (!isCameraOn) {
            myView.setVisibility(View.INVISIBLE);
            SurfaceTexture mySurfaceTexture = myTextrureView.getSurfaceTexture();
            Surface mySurface = new Surface(mySurfaceTexture);
            try {
                myCaptureRequestBuilder = myCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                myCaptureRequestBuilder.addTarget(mySurface);
                myCameraDevice.createCaptureSession(Arrays.asList(mySurface), new CameraCaptureSession.StateCallback() {
                            @Override
                            public void onConfigured(@NonNull CameraCaptureSession session) {
                                myCameraCaptureSession = session;
                                myCaptureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                                try {
                                    myCameraCaptureSession.setRepeatingRequest(myCaptureRequestBuilder.build(), null, null);
                                } catch (CameraAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onConfigureFailed(@NonNull CameraCaptureSession session) { }
                        }, null
                );
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else {
            myView.setVisibility(View.VISIBLE);
        }
        isCameraOn = !isCameraOn;
    }
}