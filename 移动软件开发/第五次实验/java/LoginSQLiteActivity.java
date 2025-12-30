package com.example.yidongexperiment05;

import android.app.Activity;
import android.content.Intent;
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

import com.example.yidongexperiment05.util.LoginDBHelper;
import com.example.yidongexperiment05.util.LoginInfo;
import com.example.yidongexperiment05.util.ViewUtil;

import java.util.Random;

/**
 * 演示 SQLite 数据库的 "增" (Create), "删" (Delete), "改" (Update), "查" (Read)
 */
public class LoginSQLiteActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener, View.OnFocusChangeListener {

    private TextView tv_password;
    private EditText et_password;
    private Button btn_forget;
    private CheckBox ck_remember;
    private EditText et_phone;
    private RadioButton rb_password;
    private RadioButton rb_verifycode;
    private Button btn_login;
    private Button btn_clear_db;

    private static final int REQUEST_CODE_FORGET = 1;

    private String mPassword = "111111"; // 默认密码
    private String mVerifyCode;
    private LoginDBHelper mHelper;

    // (修复 1)
    // 新增两个成员变量，用于"暂存"
    private String mNewPasswordToSave = null;
    private String mPhoneToSave = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);

        // --- (控件初始化) ---
        RadioGroup rb_login = findViewById(R.id.rg_login);
        tv_password = findViewById(R.id.tv_password);
        et_phone = findViewById(R.id.et_phone);
        et_password = findViewById(R.id.et_password);
        btn_forget = findViewById(R.id.btn_forget);
        ck_remember = findViewById(R.id.ck_remember);
        rb_password = findViewById(R.id.rb_password);
        rb_verifycode = findViewById(R.id.rb_verifycode);
        btn_login = findViewById(R.id.btn_login);

        btn_clear_db = findViewById(R.id.btn_clear_prefs);

        // --- (监听器设置) ---
        rb_login.setOnCheckedChangeListener(this);
        et_phone.addTextChangedListener(new HideTextWatcher(et_phone, 11));
        et_password.addTextChangedListener(new HideTextWatcher(et_password, 6));
        btn_forget.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        et_password.setOnFocusChangeListener(this);

        if (btn_clear_db != null) {
            btn_clear_db.setOnClickListener(this);
        }
    }

    /**
     * (教学点: "查" - Read)
     * 从 SQLite "查询" 已保存的数据并填充到界面
     */
    private void reload() {
        LoginInfo info = mHelper.queryTop(); // "查"
        if (info != null && info.remember) {
            et_phone.setText(info.phone);
            et_password.setText(info.password);
            ck_remember.setChecked(true);

            // 同步内存中的密码
            this.mPassword = info.password;
        }
    }

    /**
     * (修复 2)
     * 在 onStart 中处理"改"和"查"
     */
    @Override
    protected void onStart() {
        super.onStart();
        mHelper = LoginDBHelper.getInstance(this);

        // 1. 打开数据库连接
        mHelper.openReadLink();
        mHelper.openWriteLink();

        // 2. (核心) 检查是否有 "暂存" 的新密码需要"改"
        if (mNewPasswordToSave != null && mPhoneToSave != null) {

            // --- (教学点: "改" - Update) ---
            // 此时数据库连接已打开，可以安全执行"改"操作
            long result = mHelper.updatePassword(mPhoneToSave, mNewPasswordToSave);

            if (result > 0) {
                Toast.makeText(this, "密码已在数据库中更新", Toast.LENGTH_SHORT).show();
                et_password.setText(mNewPasswordToSave); // 更新UI
                this.mPassword = mNewPasswordToSave; // 更新内存
            }

            // 3. (核心) 清除暂存
            mNewPasswordToSave = null;
            mPhoneToSave = null;
        }

        // 4. "查" (Read)
        reload();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHelper.closeLink(); // 保持不变
    }

    // --- (onCheckedChanged 保持不变) ---
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            // 选择了密码登录
            case R.id.rb_password:
                tv_password.setText(getString(R.string.login_password));
                et_password.setHint(getString(R.string.input_password));
                btn_forget.setText(getString(R.string.forget_password));
                ck_remember.setVisibility(View.VISIBLE);
                break;
            // S选择了验证码登录
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

        int id = v.getId();
        if (id == R.id.btn_forget) {
            if (phone.length() < 11) {
                Toast.makeText(this, "请输入11位手机号", Toast.LENGTH_SHORT).show();
                return;
            }

            if (rb_password.isChecked()) {
                Intent intent = new Intent(this, LoginForgetActivity.class);
                intent.putExtra("phone", phone);
                startActivityForResult(intent, REQUEST_CODE_FORGET);
            } else if (rb_verifycode.isChecked()) {
                // ... (验证码逻辑不变)
                mVerifyCode = String.format("%06d", new Random().nextInt(999999));
                AlertDialog.Builder buider = new AlertDialog.Builder(this);
                buider.setTitle("请记住验证码");
                buider.setMessage("手机号" + phone + ",本次验证码是" + mVerifyCode + ",请输入验证码");
                buider.setPositiveButton("好的", null);
                AlertDialog dialog = buider.create();
                dialog.show();
            }
        } else if (id == R.id.btn_login) {
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
        } else if (id == R.id.btn_clear_prefs) {
            // --- (教学点: "删") ---
            // 4. "删" (Delete) - 显式删除
            // ---
            clearLoginInfo();
        }
    }


    /**
     * (修复 3)
     * (教学点: "改")
     * 接收 LoginForgetActivity 返回的新密码
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_FORGET) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                // (核心)
                // 而是将新密码和手机号"暂存"到成员变量中
                mNewPasswordToSave = data.getStringExtra("new_password");
                mPhoneToSave = et_phone.getText().toString(); // 必须暂存当时的手机号

                // (可选) 更新内存变量，以便在 onStart/reload 之前登录
                this.mPassword = mNewPasswordToSave;
            }
        }
    }

    /**
     * (教学点: "增" - Create / Update)
     * 校验通过，登录成功
     */
    private void loginSuccess() {
        String desc = String.format("您的手机号码是%s，恭喜你通过登录验证...",
                et_phone.getText().toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("登录成功");
        builder.setMessage(desc);
        builder.setPositiveButton("确定返回", (dialog, which) -> {
            finish();
        });
        builder.setNegativeButton("我再看看", null);
        builder.create().show();

        // "增" (Create / Update)
        LoginInfo info = new LoginInfo();
        info.phone = et_phone.getText().toString();
        info.password = et_password.getText().toString();
        info.remember = ck_remember.isChecked();
        mHelper.save(info); // save 方法会先删除旧的，再插入新的
    }

    /**
     * (新增)
     * (教学点: "删" - Delete)
     * 显式“删除”指定手机号的 SQLite 数据
     */
    private void clearLoginInfo() {
        String phone = et_phone.getText().toString();
        if (phone.isEmpty()) {
            Toast.makeText(this, "请输入要删除的手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginInfo info = new LoginInfo();
        info.phone = phone;
        long result = mHelper.delete(info); // "删"

        if (result > 0) {
            Toast.makeText(this, "已从数据库删除 " + phone + " 的信息", Toast.LENGTH_SHORT).show();
            // 清空界面
            et_password.setText("");
            ck_remember.setChecked(false);
            this.mPassword = "111111"; // 恢复默认密码
        } else {
            Toast.makeText(this, "数据库中无此用户信息", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * (教学点: "查" - Read)
     * 当密码框获取焦点时，"查询" 数据库
     * 确保 onFocusChange 更新 mPassword
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.getId() == R.id.et_password && hasFocus) {
            String phone = et_phone.getText().toString();
            if (phone.isEmpty()) {
                return; // 如果手机号为空，则不查询
            }

            LoginInfo info = mHelper.queryByPhone(phone); // "查"

            if (info != null) {
                // 查到了
                et_password.setText(info.password);
                ck_remember.setChecked(info.remember);
                this.mPassword = info.password; // (重要) 同步内存密码
            } else {
                // 没查到 (新用户)
                et_password.setText("");
                ck_remember.setChecked(false);
                this.mPassword = "111111"; // 恢复默认
            }
        }
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
        public void onTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().length() == mMaxLength) {
                ViewUtil.hideOneInputMethod(LoginSQLiteActivity.this, mView);
            }
        }
    }
}