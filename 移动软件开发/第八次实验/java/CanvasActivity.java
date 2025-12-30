package com.example.yidongexperiment08;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class CanvasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 加载包含我们自定义 View 的布局文件
        setContentView(R.layout.activity_canvas);
    }
}
