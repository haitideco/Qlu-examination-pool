package com.example.yidongexperiment08;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class SelectorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 教学点:
        // 所有的状态改变逻辑都已在 XML 中定义,
        // Java 类中不需要写任何额外的代码。
        setContentView(R.layout.activity_selector);
    }
}
