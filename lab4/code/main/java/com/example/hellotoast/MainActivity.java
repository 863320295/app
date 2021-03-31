package com.example.hellotoast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private int mCount = 0;
    private TextView mShowCount;
    public static final String EXTRA_MESSAGE = "com.example.hellotoast.extra.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mShowCount = (TextView) findViewById(R.id.show_count);
    }

    public void showToast(View view) {
        Toast toast = Toast.makeText(this, R.string.toast_message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void toZero(View view) {
        mCount = 0;
        view.setBackgroundColor(Color.parseColor("#FFAAAAAA"));
        findViewById(R.id.button_count).setBackgroundColor(Color.parseColor("#FF33B5E5"));
        if (mShowCount != null)
            mShowCount.setText(Integer.toString(mCount));
    }

    public void countUp(View view) {
        mCount++;
//        if (mCount == 1)
//            findViewById(R.id.button_zero).setBackgroundColor(Color.parseColor("#FFF000"));
//        if (mCount % 2 == 0)
//            view.setBackgroundColor(Color.parseColor("#FF33B5E5"));
//        if (mCount % 2 == 1)
//            view.setBackgroundColor(Color.parseColor("#FFAA66CC"));
        if (mShowCount != null)
            mShowCount.setText(Integer.toString(mCount));
    }

    public void launchSecondActivity(View view) {
        Intent intent = new Intent(this, SecondActivity.class);
        String message = mShowCount.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}
