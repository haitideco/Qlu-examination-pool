package com.example.yidongexperiment04;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu; // (实验1) 导入 SubMenu 类
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 实验：演示 OptionsMenu 和 ContextMenu (使用 Java 编程式创建)

 */
public class MenuLabActivity extends AppCompatActivity {

    private TextView tvResult;

    // =======================================================
    // == 步骤 1: 定义菜单项的唯一 ID
    // == 自己定义常量来识别点击了哪个菜单项
    // =======================================================

    // OptionsMenu IDs
    private static final int MENU_SETTINGS_ID = 101;
    private static final int MENU_EXIT_ID = 102;
    // SubMenu IDs (实验1)
    private static final int SUBMENU_HELP_ID = 103;
    private static final int SUBMENU_ABOUT_ID = 104;

    // ContextMenu IDs
    private static final int CONTEXT_EDIT_ID = 201;
    private static final int CONTEXT_COPY_ID = 202;
    private static final int CONTEXT_DELETE_ID = 203;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_experiment);

        tvResult = findViewById(R.id.tv_result);

        // 步骤 1: 设置 Toolbar (不变)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 步骤 2: 注册 ContextMenu (不变)
        TextView tvContextTrigger = findViewById(R.id.tv_context_trigger);
        registerForContextMenu(tvContextTrigger);
    }

    // =======================================================
    // == (实验2) OptionsMenu 的创建与响应
    // == (实验1) 学习Menu类中菜单项目、子菜单的创建方法
    // =======================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // (实验1) 使用 Menu.add() 方法编程式创建菜单项
        // public MenuItem add(int groupId, int itemId, int order, CharSequence title)
        // groupId: 组ID, Menu.NONE 表示不分组
        // itemId:  我们上面定义的常量ID，用于点击时识别
        // order:   显示顺序, Menu.NONE (或 0) 表示默认顺序
        // title:   显示的标题

        // 1. 添加 "设置"
        menu.add(Menu.NONE, MENU_SETTINGS_ID, 0, "设置");

        // 2. 添加 "退出"
        menu.add(Menu.NONE, MENU_EXIT_ID, 0, "退出");

        // 3. (实验1) 使用 Menu.addSubMenu() 创建子菜单
        SubMenu subMenu = menu.addSubMenu(Menu.NONE, Menu.NONE, 0, "更多 (SubMenu)");

        // 4. (实验1) 向 SubMenu 中添加子项
        subMenu.add(Menu.NONE, SUBMENU_HELP_ID, 0, "帮助");
        subMenu.add(Menu.NONE, SUBMENU_ABOUT_ID, 0, "关于");

        return true; // 返回 true 才会显示菜单
    }

    /**
     * OptionsMenu 菜单项被点击时的回调方法。
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String message = "";
        // if 判断，现在比较的是我们自己定义的常量 ID
        int itemId = item.getItemId();

        if (itemId == MENU_SETTINGS_ID) {
            message = "点击了 [设置]";
        } else if (itemId == MENU_EXIT_ID) {
            message = "点击了 [退出]";
            finish();
        } else if (itemId == SUBMENU_HELP_ID) { // (实验1) 响应 SubMenu
            message = "点击了 SubMenu 中的 [帮助]";
        } else if (itemId == SUBMENU_ABOUT_ID) { // (实验1) 响应 SubMenu
            message = "点击了 SubMenu 中的 [关于]";
        } else {
            return super.onOptionsItemSelected(item);
        }

        tvResult.setText(message);
        return true;
    }

    // =======================================================
    // == (实验2) ContextMenu 的创建与响应
    // =======================================================

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        // 使用 menu.add() 编程式创建
        menu.setHeaderTitle("文件操作");
        menu.add(Menu.NONE, CONTEXT_EDIT_ID, 0, "编辑");
        menu.add(Menu.NONE, CONTEXT_COPY_ID, 0, "复制");
        menu.add(Menu.NONE, CONTEXT_DELETE_ID, 0, "删除");
    }

    /**
     * ContextMenu 菜单项被点击时的回调方法。
     */
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        String message = "";
        int itemId = item.getItemId();

        if (itemId == CONTEXT_EDIT_ID) {
            message = "点击了 [编辑]";
        } else if (itemId == CONTEXT_COPY_ID) {
            message = "点击了 [复制]";
        } else if (itemId == CONTEXT_DELETE_ID) {
            message = "点击了 [删除]";
        } else {
            return super.onContextItemSelected(item);
        }

        tvResult.setText(message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        return true;
    }
}
