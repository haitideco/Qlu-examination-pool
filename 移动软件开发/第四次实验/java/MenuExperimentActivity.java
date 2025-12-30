package com.example.yidongexperiment04;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MenuExperimentActivity extends AppCompatActivity {

    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_experiment);

        // --- 初始化控件 ---
        tvResult = findViewById(R.id.tv_result);
        Toolbar toolbar = findViewById(R.id.toolbar);
        // 将 Toolbar 设置为 Activity 的 ActionBar
        setSupportActionBar(toolbar);

        // --- 为 ContextMenu 注册视图 ---
        TextView tvContextTrigger = findViewById(R.id.tv_context_trigger);
        registerForContextMenu(tvContextTrigger);
    }

    // =======================================================
    // == OptionsMenu (选项菜单) 的创建与响应
    // =======================================================

    /**
     * (实验2) 创建 OptionMenu 的回调方法
     * 系统在第一次需要显示菜单时调用此方法
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // (实验1) 使用 getMenuInflater 将 XML 文件加载到 Menu 对象中
        // 这一步完成了菜单项目和子菜单的创建
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true; // 返回 true 才会显示菜单
    }

    /**
     * (实验2) 响应 OptionMenu 菜单项点击事件的回调方法
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_search) {
            tvResult.setText("您点击了 [搜索]");
        } else if (itemId == R.id.submenu_settings) {
            tvResult.setText("您点击了子菜单中的 [设置]");
        } else if (itemId == R.id.submenu_about) {
            tvResult.setText("您点击了子菜单中的 [关于]");
        } else {
            // 如果不是我们处理的，交还给父类
            return super.onOptionsItemSelected(item);
        }
        return true; // 返回 true 表示我们已经处理了点击事件
    }

    // =======================================================
    // == ContextMenu (上下文菜单) 的创建与响应
    // =======================================================

    /**
     * (实验2) 创建 ContextMenu 的回调方法
     * 当注册过的 View (tv_context_trigger) 被长按时调用
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // 使用 getMenuInflater 加载菜单资源
        getMenuInflater().inflate(R.menu.context_menu, menu);
        // (可选) 为上下文菜单设置标题
        menu.setHeaderTitle("文件操作");
    }

    /**
     * (实验2) 响应 ContextMenu 菜单项点击事件的回调方法
     */
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.context_copy) {
            tvResult.setText("您点击了 [复制]");
        } else if (itemId == R.id.context_cut) {
            tvResult.setText("您点击了 [剪切]");
        } else if (itemId == R.id.context_paste) {
            tvResult.setText("您点击了 [粘贴]");
        } else {
            return super.onContextItemSelected(item);
        }
        return true;
    }
}