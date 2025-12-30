package com.example.yidongexperiment07;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnTriggerAnr, btnTriggerCrash;
    private TextView tvStatus;
    private static final String TAG = "Exp1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTriggerAnr = findViewById(R.id.btn_trigger_anr);
        btnTriggerCrash = findViewById(R.id.btn_trigger_crash);
        tvStatus = findViewById(R.id.tv_status);

        btnTriggerAnr.setOnClickListener(this);
        btnTriggerCrash.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_trigger_anr:
                // 教学点 1: 在主线程执行耗时操作
                Log.d(TAG, "ANR 按钮点击... 主线程开始休眠");
                tvStatus.setText("主线程休眠中... (UI已卡住)");
                try {
                    Thread.sleep(10000); // 阻塞主线程10秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                tvStatus.setText("ANR 任务完成 (如果你能看到的话)");
                Log.d(TAG, "主线程休眠结束");
                break;

            case R.id.btn_trigger_crash:
                // 教学点 2: 启动子线程解决 ANR
                Log.d(TAG, "Crash 按钮点击... 启动子线程");
                tvStatus.setText("子线程已启动，UI流畅");

                // 教学点 3: Thread 和 Runnable 的标准用法
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "子线程 " + Thread.currentThread().getName() + " 开始运行");
                        try {
                            Thread.sleep(5000); // 模拟耗时
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // 教学点 4: 在子线程直接更新 UI
                        Log.d(TAG, "子线程: 尝试更新 UI...");
                        try {
                            // 这里将抛出 CalledFromWrongThreadException
                            tvStatus.setText("子线程任务完成!");
                        } catch (Exception e) {
                            Log.e(TAG, "果然出错了!", e);
                        }
                    }
                }).start();
                break;
        }
    }
}