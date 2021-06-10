package com.example.sample3;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import java.util.Locale;



public class MainActivity extends AppCompatActivity {

    private static final long START_TIME = 10000;

    private  TextView mTextViewCountDown;
    private  Button mButtonStartPause;
    private  Button getmButtonReset;

    private  CountDownTimer mCountDownTimer;
    private  boolean mTimerRunning;

    private long mTimeLeftInMillis = START_TIME;

    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("mTimerRunningの初期値は？ " + mTimerRunning);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mTextViewCountDown = findViewById(R.id.textView_CountDown);
        mButtonStartPause = findViewById(R.id.button_start_pause);
        getmButtonReset = findViewById(R.id.buttonreset);

        mButtonStartPause.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                System.out.println(mTimerRunning);
                if (mTimerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });

        getmButtonReset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                resetTimer();
            }
        });

        updateCountDownText();
    }

    private void startTimer(){
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }
            @Override
            public void onFinish() {
                mTimerRunning = false;
                mButtonStartPause.setText("スタート");
                getmButtonReset.setVisibility(View.INVISIBLE);
            }
        }.start();

        mTimerRunning = true;
        mButtonStartPause.setText("一時停止");
        getmButtonReset.setVisibility(View.INVISIBLE);
    }

    private void pauseTimer(){
        System.out.println("一時停止処理前：" + mTimerRunning);
        mCountDownTimer.cancel();
        mTimerRunning = false;
        System.out.println("一時停止処理後：" + mTimerRunning);
        mButtonStartPause.setText("スタート");
        getmButtonReset.setVisibility(View.VISIBLE);
    }

    private void resetTimer(){
        mTimeLeftInMillis = START_TIME;
        updateCountDownText();
        mButtonStartPause.setVisibility(View.VISIBLE);
        getmButtonReset.setVisibility(View.INVISIBLE);
    }

    private void updateCountDownText(){
        int minutes = (int)(mTimeLeftInMillis/1000)/60;
        int seconds = (int)(mTimeLeftInMillis/1000)%60;
        String timerLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        mTextViewCountDown.setText(timerLeftFormatted);
    }

}











