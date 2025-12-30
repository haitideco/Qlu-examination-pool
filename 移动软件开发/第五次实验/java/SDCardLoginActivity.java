package com.example.yidongexperiment05;

import androidx.annotation.Nullable; // (新增) 导入
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity; // (新增) 导入
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.yidongexperiment05.util.AccountBean; // (新增) 导入
import com.example.yidongexperiment05.util.XmlHelper;

public class SDCardLoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etPhone;
    private EditText etPassword;
    private CheckBox ckRemember;
    private Button btnLogin;
    private Button btnLoadInfo;
    private Button btnResetPassword;

    // 定义一个请求码
    private static final int REQUEST_CODE_SELECT_ACCOUNT = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s_d_card_login);

        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        ckRemember = findViewById(R.id.ck_remember);
        btnLogin = findViewById(R.id.btn_login);
        btnLoadInfo = findViewById(R.id.btn_load_info);
        btnResetPassword = findViewById(R.id.btn_reset_password);

        btnLogin.setOnClickListener(this);
        btnLoadInfo.setOnClickListener(this);
        btnResetPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_login) {
            handleLogin();
        } else if (id == R.id.btn_load_info) {
            // 跳转到另外的 activity
            Intent intent = new Intent(this, AccountListActivity.class);
            startActivityForResult(intent, REQUEST_CODE_SELECT_ACCOUNT);
        } else if (id == R.id.btn_reset_password) {
            handleReset();
        }
    }

    /**
     * (新增) 接收 AccountListActivity 返回的数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_ACCOUNT && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                // 自动填写登录信息
                String phone = data.getStringExtra("selected_phone");
                String password = data.getStringExtra("selected_password");
                etPhone.setText(phone);
                etPassword.setText(password);
                ckRemember.setChecked(true); // 默认为选中
            }
        }
    }

    /**
     * 处理登录按钮点击
     */
    private void handleLogin() {
        String phone = etPhone.getText().toString();
        String password = etPassword.getText().toString();

        if (phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "手机号和密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // (模拟登录验证)

        AccountBean account = new AccountBean(phone, password);

        if (ckRemember.isChecked()) {
            // 1. 保存或更新账号到 XML
            XmlHelper.saveAccount(this, account);
            Toast.makeText(this, "登录成功！信息已保存到SD卡(XML)", Toast.LENGTH_SHORT).show();
        } else {
            // 2. 未勾选，从 XML 中清除此账号
            XmlHelper.clearAccount(this, phone);
            Toast.makeText(this, "登录成功！(未保存)", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 处理重置密码按钮点击
     */
    private void handleReset() {
        String phone = etPhone.getText().toString();
        if (phone.isEmpty()) {
            Toast.makeText(this, "请输入您要重置密码的手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, ResetPasswordActivity.class);
        intent.putExtra("phone_to_reset", phone);
        startActivity(intent);
    }
}