package com.example.yidongexperiment08;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

// 教学点 1: 必须继承自 View
public class CustomDrawingView extends View {

    // 教学点 2: Paint (画笔)
    // 准备三支不同风格的画笔
    private Paint mFillPaint;     // 填充画笔
    private Paint mStrokePaint;   // 描边画笔
    private Paint mTextPaint;     // 文字画笔

    // 教学点: Path (路径)，用于绘制复杂图形
    private Path mTrianglePath;
    private Path mPentagonPath;

    // 教学点: Bitmap (位图)，用于绘制图片
    private Bitmap mBitmap;

    // 教学点: RectF (浮点矩形)，用于绘制椭圆和圆角矩形
    private RectF mOvalRect;
    private RectF mRoundRect;

    // 构造函数 (必须)
    public CustomDrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        // 在构造函数中初始化所有绘图对象 (不在 onDraw 中)
        initPaints();
        initPaths();
        initBitmap();
        initRects();
    }

    /**
     * 教学点: 初始化所有画笔
     */
    private void initPaints() {
        // 1. 填充画笔 (实心)
        mFillPaint = new Paint();
        mFillPaint.setColor(Color.parseColor("#3F51B5")); // 蓝色
        mFillPaint.setStyle(Paint.Style.FILL);           // 风格: 填充
        mFillPaint.setAntiAlias(true);                   // 开启抗锯齿

        // 2. 描边画笔 (空心)
        mStrokePaint = new Paint();
        mStrokePaint.setColor(Color.parseColor("#FF4081")); // 粉色
        mStrokePaint.setStyle(Paint.Style.STROKE);         // 风格: 描边
        mStrokePaint.setStrokeWidth(10f);                  // 描边宽度 10 像素
        mStrokePaint.setAntiAlias(true);                   // 开启抗锯齿

        // 3. 文字画笔
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(60f);                       // 字体大小 60 像素
        mTextPaint.setTextAlign(Paint.Align.CENTER);       // 文本对齐: 居中
        mTextPaint.setAntiAlias(true);
        // 教学点 (额外): 设置阴影
        mTextPaint.setShadowLayer(5.0f, 10.0f, 10.0f, Color.GRAY);
    }

    /**
     * 教学点: 初始化 Path (用于三角形和五边形)
     */
    private void initPaths() {
        // 1. 初始化三角形路径
        mTrianglePath = new Path();
        mTrianglePath.moveTo(300, 400); // 移动到起点
        mTrianglePath.lineTo(400, 400); // 画第一条边
        mTrianglePath.lineTo(350, 300); // 画第二条边
        mTrianglePath.close();          // 闭合路径 (自动画第三条边)

        // 2. 初始化五边形路径
        mPentagonPath = new Path();
        mPentagonPath.moveTo(500, 400); // A
        mPentagonPath.lineTo(600, 400); // B
        mPentagonPath.lineTo(650, 320); // C
        mPentagonPath.lineTo(550, 260); // D
        mPentagonPath.lineTo(450, 320); // E
        mPentagonPath.close();          // Z
    }

    /**
     * 教学点: 初始化 Bitmap
     */
    private void initBitmap() {

        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background);
    }

    /**
     * 教学点: 初始化 RectF
     */
    private void initRects() {
        // 用于椭圆的矩形
        mOvalRect = new RectF(100f, 750f, 400f, 900f);
        // 用于圆角矩形的矩形
        mRoundRect = new RectF(500f, 750f, 800f, 900f);
    }

    /**
     * 教学点 3: 核心! 所有的绘图操作都在这里进行
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 0. 绘制背景色 (画布涂成白色)
        canvas.drawColor(Color.WHITE);

        // 1. 绘制圆形 (使用填充画笔)
        canvas.drawCircle(200f, 200f, 100f, mFillPaint);

        // 2. 绘制矩形 (使用描边画笔)
        canvas.drawRect(450f, 100f, 650f, 300f, mStrokePaint);

        // 3. 绘制正方形 (使用描边画笔)
        // (矩形的长宽相等就是正方形)
        canvas.drawRect(750f, 100f, 950f, 300f, mStrokePaint);

        // 4. 绘制三角形 (使用 Path 和填充画笔)
        canvas.drawPath(mTrianglePath, mFillPaint);

        // 5. 绘制五边形 (使用 Path 和描边画笔)
        canvas.drawPath(mPentagonPath, mStrokePaint);

        // 6. 绘制字符串 (使用文字画笔)
        // (x=500, y=600 是文本基线的位置)
        canvas.drawText("Android 绘图教学", 500f, 600f, mTextPaint);

        // 7. 绘制椭圆形 (使用 RectF 和填充画笔)
        canvas.drawOval(mOvalRect, mFillPaint);

        // 8. 绘制圆角矩形 (使用 RectF 和描边画笔)
        // (rx=20, ry=20 是 x/y 方向的圆角半径)
        canvas.drawRoundRect(mRoundRect, 20f, 20f, mStrokePaint);

        // 9. 绘制图像资源 (Bitmap)
        // (将 mBitmap 绘制到 (100, 1000) 的位置)
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, 100f, 1000f, null);
        }
    }
}