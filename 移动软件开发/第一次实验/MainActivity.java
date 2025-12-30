package com.example.yidongexperiment01;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // 导入 Log 类
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

// 确保继承自 AppCompatActivity 以便使用 AppTheme
public class MainActivity extends AppCompatActivity {

    // 实验1：用于日志输出的 TAG
    private static final String TAG = "MainActivityLifecycle";

    // 实验1 和 实验2：定义UI控件
    private TextView textViewStatus;
    private Button buttonChangeText;
    private EditText editTextData;
    private Button buttonGoToSecond;

    // 实验1：用于记录点击次数的变量
    private int clickCount = 0;

    // ----------------------------------------
    // 实验1：Activity 生命周期回调
    // ----------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: Activity 正在创建。");

        // 实验1：初始化UI控件
        textViewStatus = findViewById(R.id.textViewStatus);
        buttonChangeText = findViewById(R.id.buttonChangeText);

        // 实验2：初始化UI控件
        editTextData = findViewById(R.id.editTextData);
        buttonGoToSecond = findViewById(R.id.buttonGoToSecond);

        // ----------------------------------------
        // 实验1：设置按钮点击事件监听器
        // ----------------------------------------
        buttonChangeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "buttonChangeText: 按钮被点击。");
                clickCount++;
                // 控制界面元素：更改 TextView 的文本
                textViewStatus.setText("文本已被更改 " + clickCount + " 次");
            }
        });

        // ----------------------------------------
        // 实验2：设置启动第二个Activity的按钮监听器
        // ----------------------------------------
        buttonGoToSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "buttonGoToSecond: 准备启动 SecondActivity。");
                launchSecondActivity();
            }
        });
    }

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
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: Activity 正在重新启动（从停止状态）。");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Activity 即将被销毁。");
    }

    // ----------------------------------------
    // 实验2：启动 Activity 并传递数据的方法
    // ----------------------------------------
    private void launchSecondActivity() {
        // 1. 获取 EditText 中的数据
        String dataToSend = editTextData.getText().toString();

        // 2. 创建一个 Intent
        // 参数：(上下文, 目标Activity.class)
        Intent intent = new Intent(MainActivity.this, SecondActivity.class);

        // 3. 使用 putExtra 方法将数据放入 Intent
        // "KEY_DATA" 是一个键（Key），SecondActivity 将使用它来检索数据
        intent.putExtra("KEY_DATA", dataToSend);

        // 4. 使用 Intent 启动 Activity
        startActivity(intent);
    }
}