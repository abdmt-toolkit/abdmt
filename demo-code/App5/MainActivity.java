package com.example.adaptivereading;
import android.os.Bundle;
import com.judykong.abdmt.*;
import com.judykong.abdmt.AbdObserver.*;
public class MainActivity extends ABDMTActivity {
    private ABDMT _abdmt;
    private UIAdapter _uiAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        setContext(this);
        setRoot(findViewById(R.id.root));
        _abdmt = ABDMT.getInstance();
        _uiAdapter = _abdmt.getUiAdapter();
        _abdmt.startObserver(ObserverFlags.ATTENTION);
        _uiAdapter.registerWidgets(getRoot());
        setAttentionChangeListener(new AttentionObserver.AttentionChangeListener() {
            @Override
            public void onLookingAtScreen() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        _uiAdapter.changeBrightness(MainActivity.this, 1.5, true);
                    }
                });
            }
            @Override
            public void onLookingAwayFromScreen() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        _uiAdapter.changeBrightness(MainActivity.this, 1, true);
                    }
                });
            }
        });
    }
}