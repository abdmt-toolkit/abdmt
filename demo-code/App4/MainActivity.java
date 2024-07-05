package com.judykong.zoomingeditor;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.EditText;
import com.judykong.abdmt.*;
import com.judykong.abdmt.AbdObserver.*;
public class MainActivity extends ABDMTActivity {
    private ABDMT _abdmt;
    private TouchObserver _touchObserver;
    private GestureObserver _gestureObserver;
    private UIAdapter _uiAdapter;
    private EditText inputTextView;
    private int cursorPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        setContext(this);
        setRoot(findViewById(R.id.root));
        _abdmt = ABDMT.getInstance();
        _touchObserver = _abdmt.getTouchObserver();
        _gestureObserver = _abdmt.getGestureObserver();
        _uiAdapter = _abdmt.getUiAdapter();
        _abdmt.startObserver(ObserverFlags.TOUCH);
        _abdmt.startObserver(ObserverFlags.GESTURE);
        _uiAdapter.registerWidgets(getRoot());
        inputTextView = findViewById(R.id.inputText);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (_touchObserver.getTouchDuration(1) > 400) {
                _uiAdapter.resizeFonts(1.5);
                cursorPosition = inputTextView.getSelectionEnd();
            }
        }
        if (inputTextView.getSelectionStart() == inputTextView.getSelectionEnd()
          && inputTextView.getSelectionEnd() != cursorPosition) {
            _uiAdapter.revertUI();
        }
        return super.dispatchTouchEvent(event);
    }
}