package com.example.yidongexperiment07;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ImageLunBoActivity extends AppCompatActivity {
    private static final String TAG = "CarouselProject";

    // --- 教学点 1: 核心常量 ---

    // 轮播的心跳 (3 秒切换一次)
    private static final int CYCLE_DELAY_MILLIS = 3000;
    // 模拟下载的耗时 (1 秒)
    private static final int DOWNLOAD_SIM_DELAY_MILLIS = 1000;

    // 消息：图片 "下载" 完成
    private static final int MSG_IMAGE_READY = 1001;
    // 消息：轮播心跳 (触发下一次下载)
    private static final int MSG_START_NEXT_CYCLE = 1002;

    // --- 教学点 2: 数据与 UI ---

    // 模拟的图片数据源 (使用本地 drawable 资源)
    private final int[] mImageResources = {
            R.drawable.xiaomi,
            R.drawable.rongyao,
            R.drawable.oppo
    };

    private ImageSwitcher mImageSwitcher;
    private LinearLayout mIndicatorLayout;
    private List<ImageView> mIndicatorDots = new ArrayList<>();

    // 当前轮播到的索引
    private int mCurrentIndex = 0;

    // --- 教学点 3: Handler ---

    // 核心：使用静态内部类 + 弱引用，防止内存泄漏 (API 28 最佳实践)
    private final UiHandler mUiHandler = new UiHandler(this);

    // --- 教学点 4: Activity 生命周期 ---

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_lun_bo);

        initViews();
        initIndicators();

        // (为了方便教学演示，我们用按钮手动控制)
        Button btnStart = findViewById(R.id.btn_start);
        Button btnStop = findViewById(R.id.btn_stop);
        btnStart.setOnClickListener(v -> startCarousel());
        btnStop.setOnClickListener(v -> stopCarousel());

        // 自动设置第一张图片
        mImageSwitcher.setImageResource(mImageResources[mCurrentIndex]);
        updateIndicators(mCurrentIndex);
    }

    /**
     * 教学点 5: 在 onStart 或 onResume 中启动轮播
     */
    @Override
    protected void onStart() {
        super.onStart();
        // startCarousel(); // 可以让它自动开始
    }

    /**
     * 教学点 6: 在 onStop 或 onPause 中停止轮播 (非常重要!)
     * 否则 Handler 会在 Activity 不可见时继续运行，导致内存泄漏或崩溃
     */
    @Override
    protected void onStop() {
        super.onStop();
        stopCarousel();
    }

    // --- 教学点 7: UI 初始化 (ImageSwitcher) ---
    private void initViews() {
        mImageSwitcher = findViewById(R.id.image_switcher);
        mIndicatorLayout = findViewById(R.id.ll_indicator);

        // 1. 设置动画 (淡入/淡出)
        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        mImageSwitcher.setInAnimation(in);
        mImageSwitcher.setOutAnimation(out);

        // 2. 设置 ViewFactory (必须)
        // 它告诉 ImageSwitcher 如何创建新的 ImageView 来显示图片
        mImageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
                        ImageSwitcher.LayoutParams.MATCH_PARENT,
                        ImageSwitcher.LayoutParams.MATCH_PARENT));
                return imageView;
            }
        });
    }

    // --- 教学点 8: 指示器 (选讲) ---
    private void initIndicators() {
        mIndicatorLayout.removeAllViews();
        mIndicatorDots.clear();

        for (int i = 0; i < mImageResources.length; i++) {
            ImageView dot = new ImageView(this);
            // 使用我们之前创建的 drawable (如果没创建，可以用默认的)
            dot.setImageResource(R.drawable.dot_unselected);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);

            mIndicatorLayout.addView(dot, params);
            mIndicatorDots.add(dot);
        }
    }

    private void updateIndicators(int position) {
        for (int i = 0; i < mIndicatorDots.size(); i++) {
            mIndicatorDots.get(i).setImageResource(
                    i == position ? R.drawable.dot_selected : R.drawable.dot_unselected
            );
        }
    }

    // --- 教学点 9: 轮播控制 (核心) ---

    /**
     * 开始轮播
     */
    private void startCarousel() {
        Log.d(TAG, "轮播器启动...");
        // 立即触发第一次心跳
        // (使用 post 而不是 sendEmptyMessage 可以直接发送 Runnable)
        mUiHandler.sendEmptyMessage(MSG_START_NEXT_CYCLE);
    }

    /**
     * 停止轮播
     */
    private void stopCarousel() {
        Log.d(TAG, "轮播器停止...");
        // 教学点: 移除 Handler 队列中所有未处理的消息
        // (包括 MSG_IMAGE_READY 和 MSG_START_NEXT_CYCLE)
        mUiHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 模拟下载 (在子线程中执行)
     */
    private void simulateDownload(int index) {
        Log.d(TAG, "开始模拟 '下载' 第 " + index + " 张图片 (在子线程)...");

        new Thread(() -> {
            try {
                // 教学点: 模拟耗时操作 (例如 1 秒)
                Thread.sleep(DOWNLOAD_SIM_DELAY_MILLIS);

                // 模拟 "下载" 成功，获取到图片资源
                int imageRes = mImageResources[index];

                // 教学点: 下载完成，打包 Message (使用 obtain 优化)
                Message msg = mUiHandler.obtainMessage();
                msg.what = MSG_IMAGE_READY;
                msg.arg1 = imageRes; // 传递图片资源 ID
                msg.arg2 = index;    // 传递索引

                // 教学点: 发送消息回主线程
                mUiHandler.sendMessage(msg);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 教学点 10: 静态内部类 Handler (处理所有 UI 任务)
     */
    private static class UiHandler extends Handler {
        // 持有 Activity 的弱引用
        private final WeakReference<ImageLunBoActivity> mActivityRef;

        UiHandler(ImageLunBoActivity activity) {
            this.mActivityRef = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ImageLunBoActivity activity = mActivityRef.get();
            if (activity == null) {
                // Activity 已被销毁，停止所有操作
                Log.w(TAG, "Handler: Activity 已被销S毁，丢弃消息");
                return;
            }

            switch (msg.what) {
                case MSG_START_NEXT_CYCLE:
                    // 1. 收到 "心跳" 消息，开始模拟下载下一张图片
                    Log.d(TAG, "Handler: 收到心跳，准备下载 index: " + activity.mCurrentIndex);
                    activity.simulateDownload(activity.mCurrentIndex);
                    break;

                case MSG_IMAGE_READY:
                    // 2. 收到 "图片就绪" 消息 (来自子线程)
                    int imageResource = msg.arg1;
                    int downloadedIndex = msg.arg2;
                    Log.d(TAG, "Handler: 收到图片就绪 index: " + downloadedIndex);

                    // 3. 更新 UI (ImageSwitcher 和指示器)
                    activity.mImageSwitcher.setImageResource(imageResource);
                    activity.updateIndicators(downloadedIndex);

                    // 4. 计算下一个索引
                    activity.mCurrentIndex = (downloadedIndex + 1) % activity.mImageResources.length;

                    // 5. 教学点: 延迟操作 (关键)
                    // 发送一个 "心跳" 消息，在 N 秒后执行
                    // 这就创建了 "循环"
                    sendEmptyMessageDelayed(MSG_START_NEXT_CYCLE, CYCLE_DELAY_MILLIS);
                    break;
            }
        }
    }
}