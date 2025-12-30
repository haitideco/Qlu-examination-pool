package com.example.yidongexperiment07;

import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.ref.WeakReference;

public class AsyncTaskActivity extends AppCompatActivity {
    private Button btnStart, btnCancel;
    private ProgressBar progressBar;
    private TextView tvStatus;

    private MyDownloadTask mTask;
    private static final String TAG = "Exp3_AsyncTask";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_task);
        Log.d(TAG, "onCreate: Activity " + this.hashCode() + " 创建");

        btnStart = findViewById(R.id.btn_start_task);
        btnCancel = findViewById(R.id.btn_cancel_task);
        progressBar = findViewById(R.id.progress_bar);
        tvStatus = findViewById(R.id.tv_status);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 教学点 A: 创建任务并执行
                // 传入的 "http://www.example.com/file.zip" 将作为 doInBackground 的参数
                mTask = new MyDownloadTask(AsyncTaskActivity.this);
                mTask.execute("http://www.example.com/file.zip");
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTask != null && mTask.getStatus() == AsyncTask.Status.RUNNING) {
                    // 教学点 B: 取消任务
                    // true 表示允许中断正在执行的线程
                    mTask.cancel(true);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Activity " + this.hashCode() + " 销毁");

        // 教学点 C: 旋转屏幕时，旧的 Activity 会 onDestroy。
        // 如果不在这里取消任务，AsyncTask 会继续持有旧 Activity 的引用，
        // 导致内存泄漏，并且在 onPostExecute 中更新 UI 时导致崩溃。
        // (注：即使取消，doInBackground 仍可能运行完，
        //  但 onPostExecute 不会执行，而是执行 onCancelled)

    }

    /**
     * 教学点 D: 三个泛型
     * 1. Params:   String (doInBackground 的参数, 即 URL)
     * 2. Progress: Integer (onProgressUpdate 的参数, 即下载进度)
     * 3. Result:   Boolean (doInBackground 的返回值, onPostExecute 的参数, 即是否成功)
     */
    private static class MyDownloadTask extends AsyncTask<String, Integer, Boolean> {

        // 教学点 E: 陷阱！
        // AsyncTask 默认是匿名内部类，会隐式持有 Activity。
        // 改成静态内部类 + 弱引用 是标准修复手段。
        private WeakReference<AsyncTaskActivity> activityReference;

        MyDownloadTask(AsyncTaskActivity context) {
            activityReference = new WeakReference<>(context);
        }

        private AsyncTaskActivity getActivity() {
            AsyncTaskActivity activity = activityReference.get();
            // 教学点 F: 旋转后，旧 Activity 的 WeakReference 会返回 null
            if (activity == null || activity.isFinishing()) {
                Log.e(TAG, "Activity 丢失！(可能已旋转或销毁)");
                return null;
            }
            return activity;
        }

        // 教学点 1: onPreExecute (UI 线程)
        @Override
        protected void onPreExecute() {
            AsyncTaskActivity activity = getActivity();
            if (activity == null) return;

            activity.tvStatus.setText("准备下载...");
            activity.btnStart.setEnabled(false);
            activity.btnCancel.setEnabled(true);
        }

        // 教学点 2: doInBackground (子线程)
        @Override
        protected Boolean doInBackground(String... urls) {
            Log.d(TAG, "doInBackground: 开始下载 " + urls[0]);

            for (int i = 0; i <= 100; i++) {
                // 教学点 G: 检查是否被取消
                if (isCancelled()) {
                    Log.d(TAG, "doInBackground: 任务被取消");
                    return false;
                }

                try {
                    Thread.sleep(100); // 模拟下载耗时
                } catch (InterruptedException e) {
                    Log.d(TAG, "doInBackground: 线程被中断 (cancel(true))");
                    return false;
                }

                // 教学点 3: publishProgress (触发 onProgressUpdate)
                publishProgress(i);
            }
            return true;
        }

        // 教学点 3 (续): onProgressUpdate (UI 线程)
        @Override
        protected void onProgressUpdate(Integer... values) {
            AsyncTaskActivity activity = getActivity();
            if (activity == null) return;

            int progress = values[0];
            activity.progressBar.setProgress(progress);
            activity.tvStatus.setText("下载中... " + progress + "%");
        }

        // 教学点 4: onPostExecute (UI 线程)
        // (如果任务被 cancel，此方法*不会*被调用)
        @Override
        protected void onPostExecute(Boolean success) {
            AsyncTaskActivity activity = getActivity();
            if (activity == null) return;

            activity.btnStart.setEnabled(true);
            activity.btnCancel.setEnabled(false);
            if (success) {
                activity.tvStatus.setText("下载完成!");
                Toast.makeText(activity, "下载成功", Toast.LENGTH_SHORT).show();
            } else {
                activity.tvStatus.setText("下载失败。");
            }
        }

        // 教学点 5: onCancelled (UI 线程)
        // (在 doInBackground 返回后，如果被 cancel，则调用此方法)
        @Override
        protected void onCancelled() {
            AsyncTaskActivity activity = getActivity();
            if (activity == null) return;

            activity.tvStatus.setText("任务已取消");
            activity.progressBar.setProgress(0);
            activity.btnStart.setEnabled(true);
            activity.btnCancel.setEnabled(false);
        }
    }
}