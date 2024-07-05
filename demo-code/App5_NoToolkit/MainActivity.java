package com.example.adaptivereading;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.widget.Toast;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.face.FaceDetector;
public class MainActivity extends AppCompatActivity {
    CameraSource cameraSource;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
    }
    @Override
    public void onResume() {
        super.onResume();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenUnLock = pm.isScreenOn();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            checkIfAlreadyHaveAttentionPermission();
        }
        if (isScreenUnLock) {
            initCameraSource();
            if (cameraSource != null) {
                try {
                    cameraSource.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        checkIfAlreadyHaveSystemSettingPermission();
    }
    @Override
    public void onPause() {
        try {
            if (cameraSource != null) {
                cameraSource.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPause();
    }
    @Override
    public void onDestroy() {
        if (cameraSource != null) {
            cameraSource.release();
        }
        super.onDestroy();
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
    private void requestAttentionPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 2);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initCameraSource();
            if (cameraSource != null) {
                try {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    cameraSource.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
        }
    }
    private void initCameraSource() {
        try {
            FaceDetector detector = new FaceDetector.Builder(this)
                    .setTrackingEnabled(true)
                    .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                    .setMode(FaceDetector.FAST_MODE)
                    .build();
            detector.setProcessor(new MultiProcessor.Builder(new AttentionTrackerDaemon(this, MainActivity.this)).build());
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
            e.printStackTrace();
        }
    }
}