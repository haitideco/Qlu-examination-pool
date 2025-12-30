package com.example.yidongexperiment02;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 在这里更改要加载的布局文件
        // 例如，加载 LinearLayout 示例：
        setContentView(R.layout.constraintlayout);


        // 但更好的做法是为每个布局创建单独的 Activity

        // 示例：为 LinearLayout 中的按钮添加点击事件
        Button btnLinear = findViewById(R.id.btn_linear);
        if (btnLinear != null) {
            btnLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "LinearLayout 按钮被点击", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // 提示：
    // 为了满足实验要求（每个布局一个项目），最简单的方法是：
    // 1. 创建这个基础项目。
    // 2. 将其复制6次。
    // 3. 在每个项目中，修改 MainActivity.java 中的 setContentView()
    //    来加载对应的布局 XML（例如, R.layout.activity_relative_layout）。
    // 4. 并将对应的 XML 文件内容粘贴进去。
}