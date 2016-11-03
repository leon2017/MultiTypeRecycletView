package com.wangjun.app.multityperecycletview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private android.widget.Button button;
    private android.widget.Button button2;
    private android.widget.RelativeLayout activitymain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.activitymain = (RelativeLayout) findViewById(R.id.activity_main);
        this.button2 = (Button) findViewById(R.id.button2);
        this.button = (Button) findViewById(R.id.button);
        this.button.setOnClickListener(this);
        this.button2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.button:
                intent.setClass(MainActivity.this,NormalActivity.class);
                break;
            case R.id.button2:
                intent.setClass(MainActivity.this,MultiTypeActivity.class);
                break;
        }
        startActivity(intent);
    }
}
