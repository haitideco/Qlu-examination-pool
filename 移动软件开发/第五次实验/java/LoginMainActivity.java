package com.example.yidongexperiment05;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yidongexperiment05.util.ViewUtil;

import java.util.Random;

public class LoginMainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private TextView tv_password;
    private EditText et_password;
    private Button btn_forget;
    private CheckBox ck_remember;
    private EditText et_phone;
    private RadioButton rb_password;
    private RadioButton rb_verifycode;

    private static final int REQUEST_CODE_FORGET = 1;

    private Button btn_login;
    private Button btn_clear_prefs;

    private String mPassword = "111111"; // 默认密码
    private String mVerifyCode;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);

        RadioGroup rb_login = findViewById(R.id.rg_login);
        tv_password = findViewById(R.id.tv_password);
        et_phone = findViewById(R.id.et_phone);
        et_password = findViewById(R.id.et_password);
        btn_forget = findViewById(R.id.btn_forget);
        ck_remember = findViewById(R.id.ck_remember);
        rb_password = findViewById(R.id.rb_password);
        rb_verifycode = findViewById(R.id.rb_verifycode);
        btn_login = findViewById(R.id.btn_login);
        btn_clear_prefs = findViewById(R.id.btn_clear_prefs);

        rb_login.setOnCheckedChangeListener(this);
        et_phone.addTextChangedListener(new HideTextWatcher(et_phone, 11));
        et_password.addTextChangedListener(new HideTextWatcher(et_password, 6));
        btn_forget.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        btn_clear_prefs.setOnClickListener(this);


        // --- (教学点: "查") ---
        // 1. "查" (Read)
        // 在 OnCreate 时，"查询" SharedPreferences
        // ---
        preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        reload();
    }

    /**
     * (教学点: "查" - Read)
     * 从 SharedPreferences "查询" 已保存的数据并填充到界面
     */
    private void reload() {
        boolean isRemember = preferences.getBoolean("isRemember", false);
        if (isRemember) {
            String phone = preferences.getString("phone", "");
            et_phone.setText(phone);

            String password = preferences.getString("password", "");
            et_password.setText(password);
            ck_remember.setChecked(true);

            // (重要) 同步内存中的密码，以便登录时校验
            this.mPassword = password;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_password:
                tv_password.setText(getString(R.string.login_password));
                et_password.setHint(getString(R.string.input_password));
                btn_forget.setText(getString(R.string.forget_password));
                ck_remember.setVisibility(View.VISIBLE);
                break;
            case R.id.rb_verifycode:
                tv_password.setText(getString(R.string.verifycode));
                et_password.setHint(getString(R.string.input_verifycode));
                btn_forget.setText(getString(R.string.get_verifycode));
                ck_remember.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        String phone = et_phone.getText().toString();
        // (教学点)
        // 验证码登录不需要11位手机号，只有在需要"忘记密码"或"登录"时才校验

        switch (v.getId()) {
            case R.id.btn_forget:
                if (phone.length() < 11) {
                    Toast.makeText(this, "请输入11位手机号", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (rb_password.isChecked()) {
                    Intent intent = new Intent(this, LoginForgetActivity.class);
                    intent.putExtra("phone", phone);
                    startActivityForResult(intent, REQUEST_CODE_FORGET);
                } else if (rb_verifycode.isChecked()) {
                    mVerifyCode = String.format("%06d", new Random().nextInt(999999));
                    AlertDialog.Builder buider = new AlertDialog.Builder(this);
                    buider.setTitle("请记住验证码");
                    buider.setMessage("手机号" + phone + ",本次验证码是" + mVerifyCode + ",请输入验证码");
                    buider.setPositiveButton("好的", null);
                    buider.create().show();
                }
                break;

            case R.id.btn_login:
                if (phone.length() < 11) {
                    Toast.makeText(this, "请输入11位手机号", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (rb_password.isChecked()) {
                    if (!mPassword.equals(et_password.getText().toString())) {
                        Toast.makeText(this, "请输入正确的密码", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    loginSuccess();
                } else if (rb_verifycode.isChecked()) {
                    if (!mVerifyCode.equals(et_password.getText().toString())) {
                        Toast.makeText(this, "请输入正确的验证码", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    loginSuccess();
                }
                break;

            case R.id.btn_clear_prefs:
                // --- (教学点: "删") ---
                // 3. "删" (Delete) - 显式删除
                // ---
                clearSharedPreferences();
                break;
        }
    }

    /**
     * (教学点: "改" - Update)
     * 接收 LoginForgetActivity 返回的新密码
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_FORGET) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                // 1. 从返回的 Intent 中获取新密码
                String new_password = data.getStringExtra("new_password");

                // 2. 更新内存中的密码 (用于本次登录)
                mPassword = new_password;

                // 3. 更新界面
                et_password.setText(new_password);

                // --- (教学点: "改") ---
                // 4. "改" (Update)
                // 无论是否勾选"记住密码"，都"修改" SharedPreferences 中已存的密码
                // ---
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("password", new_password);
                editor.commit();

                Toast.makeText(this, "已保存新密码", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * 校验通过，登录成功
     * (教学点: "增" - Create / "删" - Delete)
     */
    private void loginSuccess() {
        String desc = String.format("您的手机号码是%s，恭喜你通过登录验证，点击“确定”按钮返回上个页面",
                et_phone.getText().toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("登录成功");
        builder.setMessage(desc);
        builder.setPositiveButton("确定返回", (dialog, which) -> {
            finish();
        });
        builder.setNegativeButton("我再看看", null);
        AlertDialog dialog = builder.create();
        dialog.show();

        // (核心) 根据是否勾选，执行 "增" 或 "删"
        if (ck_remember.isChecked()) {
            // --- (教学点: "增") ---
            // 2. "增" (Create / Update)
            // 将数据 "增加" 到 SharedPreferences
            // ---
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("phone", et_phone.getText().toString());
            editor.putString("password", et_password.getText().toString());
            editor.putBoolean("isRemember", true);
            editor.commit(); // 提交保存
        } else {
            // --- (教学点: "删") ---
            // 3. "删" (Delete) - 隐式删除
            // 未勾选"记住密码"，"删除" SharedPreferences 中的数据
            // ---
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("phone");
            editor.remove("password");
            editor.putBoolean("isRemember", false);
            editor.commit(); // 提交删除
        }
    }

    /**
     * (教学点: "删" - Delete)
     * 显式“删除”所有 SharedPreferences 数据
     */
    private void clearSharedPreferences() {
        SharedPreferences.Editor editor = preferences.edit();
        // editor.clear() 会删除 "config" 文件中的所有键值对
        editor.clear();
        editor.commit();

        // 清空界面
        et_phone.setText("");
        et_password.setText("");
        ck_remember.setChecked(false);
        this.mPassword = "111111"; // 恢复默认密码

        Toast.makeText(this, "已清除所有保存的登录信息", Toast.LENGTH_SHORT).show();
    }

    // --- (HideTextWatcher 内部类保持不变) ---
    private class HideTextWatcher implements TextWatcher {
        private EditText mView;
        private int mMaxLength;
        public HideTextWatcher(EditText v, int maxLength) {
            this.mView = v;
            this.mMaxLength = maxLength;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }
        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().length() == mMaxLength) {
                ViewUtil.hideOneInputMethod(LoginMainActivity.this, mView);
            }
        }
    }
}