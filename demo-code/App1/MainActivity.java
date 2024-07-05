package com.judykong.walktap;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import com.judykong.abdmt.*;
import com.judykong.abdmt.AbdObserver.*;
public class MainActivity extends ABDMTActivity
        implements View.OnClickListener {
    private static final String _password = "1680";
    private ABDMT _abdmt;
    private TouchObserver _touchObserver;
    private AbilityModeler _abilityModeler;
    private UIAdapter _uiAdapter;
    private TextView _textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        setContext(this);
        setRoot(findViewById(R.id.root));
        _abdmt = ABDMT.getInstance();
        _touchObserver = _abdmt.getTouchObserver();
        _abilityModeler = _abdmt.getAbilityModeler();
        _uiAdapter = _abdmt.getUiAdapter();
        _abdmt.startObserver(ObserverFlags.TOUCH);
        _abdmt.startObserver(ObserverFlags.GESTURE);
        _uiAdapter.registerWidgets(getRoot());
        registerButtons();
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
         if (_abilityModeler.hasTremor()) {
            _uiAdapter.enforceMinSize(_touchObserver.getTouchExtent() * 4);
         }
        return super.dispatchTouchEvent(event);
    }
    @Override
    public void onClick(View view)
    {
        String text = (String) _textView.getText();
        if (text.equals("Enter Password") || text.equals("Wrong Password!")) {
            text = "";
        }
        switch (view.getId()) {
            case R.id.button1:
                _textView.setText(text + "1");
                break;
            case R.id.button2:
                _textView.setText(text + "2");
                break;
            case R.id.button3:
                _textView.setText(text + "3");
                break;
            case R.id.button4:
                _textView.setText(text + "4");
                break;
            case R.id.button5:
                _textView.setText(text + "5");
                break;
            case R.id.button6:
                _textView.setText(text + "6");
                break;
            case R.id.button7:
                _textView.setText(text + "7");
                break;
            case R.id.button8:
                _textView.setText(text + "8");
                break;
            case R.id.button9:
                _textView.setText(text + "9");
                break;
            case R.id.button10:
                if (text.length() > 0) {
                    _textView.setText(text.substring(0, text.length() - 1));
                }
                break;
            case R.id.button11:
                _textView.setText(text + "0");
                break;
            case R.id.button12:
                if (text.equals(_password)) {
                    startActivity(new Intent(MainActivity.this, Success.class));
                } else {
                    _textView.setText("Wrong Password!");
                }
                break;
            default:
                break;
        }
    }
    public void registerButtons() {
        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(this);
        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(this);
        Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(this);
        Button button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(this);
        Button button5 = (Button) findViewById(R.id.button5);
        button5.setOnClickListener(this);
        Button button6 = (Button) findViewById(R.id.button6);
        button6.setOnClickListener(this);
        Button button7 = (Button) findViewById(R.id.button7);
        button7.setOnClickListener(this);
        Button button8 = (Button) findViewById(R.id.button8);
        button8.setOnClickListener(this);
        Button button9 = (Button) findViewById(R.id.button9);
        button9.setOnClickListener(this);
        Button button10 = (Button) findViewById(R.id.button10);
        button10.setOnClickListener(this);
        Button button11 = (Button) findViewById(R.id.button11);
        button11.setOnClickListener(this);
        Button button12 = (Button) findViewById(R.id.button12);
        button12.setOnClickListener(this);
        _textView = findViewById(R.id.textView);
    }
}