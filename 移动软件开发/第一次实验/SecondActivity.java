package com.example.yidongexperiment01;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {

    // 用于日志输出的 TAG
    private static final String TAG = "SecondActivityLifecycle";

    private TextView textViewReceivedData;
    private Button buttonGoBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Log.d(TAG, "onCreate: SecondActivity 正在创建。");

        textViewReceivedData = findViewById(R.id.textViewReceivedData);
        buttonGoBack = findViewById(R.id.buttonGoBack);

        // ----------------------------------------
        // 实验2：接收数据
        // ----------------------------------------
        receiveDataFromIntent();

        // ----------------------------------------
        // 实验2：设置返回按钮的点击事件
        // ----------------------------------------
        buttonGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 结束当前的 Activity，返回到上一个 Activity (MainActivity)
                finish();
            }
        });
    }

    private void receiveDataFromIntent() {
        // 1. 获取启动此 Activity 的 Intent
        Intent intent = getIntent();

        // 2. 检查 Intent 是否存在 (健壮性)
        if (intent != null) {
            // 3. 使用在 MainActivity 中定义的同一个 Key ("KEY_DATA") 来检索数据
            // "无数据" 是一个默认值，如果 "KEY_DATA" 不存在，则返回该值
            String data = intent.getStringExtra("KEY_DATA");

            // 4. 检查数据是否有效（可能未输入）
            if (data != null && !data.isEmpty()) {
                // 5. 将数据显示在 TextView 上
                textViewReceivedData.setText("接收到的数据：\n" + data);
            } else {
                textViewReceivedData.setText("未接收到任何数据");
            }
        }
    }


    // ----------------------------------------
    // 实验1：同样可以观察 SecondActivity 的生命周期
    // ----------------------------------------

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: Activity 即将可见。");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Activity 已可见并获取焦点（可交互）。");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Activity 即将暂停（失去焦点）。");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Activity 已停止（完全不可见）。");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Activity 即将被销毁。");
    }
}
