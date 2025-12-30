package com.example.yidongexperiment07; // (您的包名)

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class HandleDemoActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvLog;
    private Button btnCancel;

    // 教学点 A: 定义消息类型
    private static final int MSG_TYPE_STRING = 1;
    private static final int MSG_TYPE_COMPLEX = 2;

    // 教学点 B: (最佳实践) 使用静态内部类 + 弱引用 解决 Handler 内存泄漏
    private final MyHandler mHandler = new MyHandler(this); // 'this' 是 HandleDemoActivity

    // 教学点 C: 用于 postDelayed 的 Runnable 实例，以便取消
    private Runnable delayedTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 确保您的布局文件名是 activity_handle_demo.xml
        setContentView(R.layout.activity_handle_demo);

        tvLog = findViewById(R.id.tv_log);
        btnCancel = findViewById(R.id.btn_cancel);
        findViewById(R.id.btn_send_message).setOnClickListener(this);
        findViewById(R.id.btn_post_runnable).setOnClickListener(this);
        findViewById(R.id.btn_post_delayed).setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_message:
                startSendMessageTask();
                break;
            case R.id.btn_post_runnable:
                startPostTask();
                break;
            case R.id.btn_post_delayed:
                startPostDelayedTask();
                break;
            case R.id.btn_cancel:
                cancelTasks();
                break;
        }
    }

    private void appendLog(String message) {
        tvLog.append("\n[" + getCurrentTime() + "] " + message);
    }

    // --- 教学点 1 & 4: sendMessage & Message.obj ---
    private void startSendMessageTask() {
        appendLog("主线程: 'Send' 任务已启动...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000); // 模拟耗时

                    Message msg = Message.obtain();
                    msg.what = MSG_TYPE_COMPLEX;
                    msg.arg1 = 200;
                    msg.arg2 = 101;
                    UserData user = new UserData("张三", 25);
                    msg.obj = user;

                    // 3. 子线程只负责发送消息
                    mHandler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // --- 教学点 2: post(Runnable) ---
    private void startPostTask() {
        appendLog("主线程: 'Post' 任务已启动...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);

                    // 3. 子线程只负责“投递”一个 Runnable
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            // 4. 这个 Runnable 在主线程执行，可以安全调用 appendLog
                            appendLog("主线程: post(Runnable) 任务被执行了。");
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // --- 教学点 5: postDelayed ---
    private void startPostDelayedTask() {
        appendLog("主线程: 任务已提交，3秒后执行...");

        delayedTask = new Runnable() {
            @Override
            public void run() {
                // 这个 Runnable 在主线程执行
                appendLog("主线程(Delayed): " + getCurrentTime());
            }
        };

        mHandler.postDelayed(delayedTask, 3000);
    }

    // --- 教学点 6: removeCallbacks / removeMessages ---
    private void cancelTasks() {
        if (delayedTask != null) {
            mHandler.removeCallbacks(delayedTask);
            appendLog("主线程: 已取消 postDelayed 任务。");
        }
        mHandler.removeMessages(MSG_TYPE_COMPLEX);
        appendLog("主线程: 已尝试取消 MSG_TYPE_COMPLEX 任务。");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 教学点 M: 必须在 Activity 销毁时清理，防止泄漏
        mHandler.removeCallbacksAndMessages(null);
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    // --- 静态内部类 Handler (完全正确) ---
    private static class MyHandler extends Handler {
        private final WeakReference<HandleDemoActivity> mActivityRef;

        public MyHandler(HandleDemoActivity activity) {
            super(Looper.getMainLooper());
            this.mActivityRef = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            HandleDemoActivity activity = mActivityRef.get();
            if (activity == null || activity.isFinishing()) {
                return; // Activity 已经销毁，不再处理
            }

            // 教学点 N: 在主线程处理消息
            switch (msg.what) {
                case MSG_TYPE_STRING:
                    break;
                case MSG_TYPE_COMPLEX:
                    // (从 sendMessage 任务接收)
                    int code = msg.arg1;
                    int id = msg.arg2;
                    UserData user = (UserData) msg.obj;

                    String log = String.format(Locale.CHINA,
                            "主线程: 收到 Message!\n what=%d, arg1=%d, arg2=%d\n UserData: %s",
                            msg.what, code, id, user.toString());

                    // 在主线程安全地调用 appendLog
                    activity.appendLog(log);
                    break;
            }
        }
    }

    // --- 模拟的数据类 (完全正确) ---
    private static class UserData {
        String name;
        int age;
        public UserData(String name, int age) { this.name = name; this.age = age; }
        @Override
        public String toString() { return "姓名: " + name + ", 年龄: " + age; }
    }
}