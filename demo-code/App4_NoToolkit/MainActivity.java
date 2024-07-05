package com.judykong.zoomingeditor_notoolkit;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class MainActivity extends AppCompatActivity {
    public class TimePoint {
        public float x;
        public float y;
        public long time;
        public TimePoint(float x, float y, long time) {
            this.x = x;
            this.y = y;
            this.time = time;
        }
    }
    private class UIParams {
        public float textSize;
        public UIParams() {}
    }
    private class UIChange {
        public String type;
        public Map<Integer, UIParams> modifiedTargets;
        public UIChange(String type, Map<Integer, UIParams> modifiedTargets) {
            this.type = type;
            this.modifiedTargets = modifiedTargets;
        }
    }
    private TextView titleTextView;
    private EditText inputTextView;
    private int cursorPosition;
    private ArrayList<TimePoint> _currPath = new ArrayList<TimePoint>();
    private List<UIChange> _uiHistory = new ArrayList<UIChange>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        titleTextView = findViewById(R.id.textView);
        inputTextView = findViewById(R.id.inputText);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        long time = event.getEventTime();
        TimePoint pt = new TimePoint(x, y, time);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                _currPath = new ArrayList<TimePoint>();
                _currPath.add(pt);
                break;
            case MotionEvent.ACTION_MOVE:
                for (int i = 0; i < event.getHistorySize(); i++) {
                    x = event.getHistoricalX(i);
                    y = event.getHistoricalY(i);
                    time = event.getHistoricalEventTime(i);
                    pt = new TimePoint(x, y, time);
                    _currPath.add(pt);
                }
                break;
            case MotionEvent.ACTION_UP:
                _currPath.add(pt);
                break;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            TimePoint startPt = _currPath.get(0);
            TimePoint endPt = _currPath.get(_currPath.size() - 1);
            if (endPt.time - startPt.time > 400) {
                Map<Integer, UIParams> modification = new HashMap<Integer, UIParams>();
                UIParams params;
                float fontSize;
                fontSize = titleTextView.getTextSize();
                params = new UIParams();
                params.textSize = fontSize;
                modification.put(titleTextView.getId(), params);
                titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize * (float) 1.5);
                fontSize = inputTextView.getTextSize();
                params = new UIParams();
                params.textSize = fontSize;
                modification.put(inputTextView.getId(), params);
                inputTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize * (float) 1.5);
                _uiHistory.add(new UIChange("resizeFonts", modification));
                cursorPosition = inputTextView.getSelectionEnd();
            }
        }
        if (inputTextView.getSelectionStart() == inputTextView.getSelectionEnd()
          && inputTextView.getSelectionEnd() != cursorPosition) {
            revertUI();
        }
        return super.dispatchTouchEvent(event);
    }
    public void revertUI() {
        if (_uiHistory.size() > 0) {
            UIChange history = _uiHistory.get(_uiHistory.size() - 1);
            _uiHistory.remove(_uiHistory.size() - 1);
            Map<Integer, UIParams> modifiedTargets = history.modifiedTargets;
            if (modifiedTargets.containsKey(titleTextView.getId())) {
                UIParams params = modifiedTargets.get(titleTextView.getId());
                titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, params.textSize);
            }
            if (modifiedTargets.containsKey(inputTextView.getId())) {
                UIParams params = modifiedTargets.get(inputTextView.getId());
                inputTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, params.textSize);
            }
        }
    }
}