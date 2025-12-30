package com.example.yidongexperiment05;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.yidongexperiment05.util.AccountBean; // (修改) 导入
import com.example.yidongexperiment05.util.XmlHelper;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText etPhoneReset;
    private EditText etNewPassword;
    private EditText etConfirmPassword;
    private Button btnConfirmReset;
    private String mPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        etPhoneReset = findViewById(R.id.et_phone_reset);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnConfirmReset = findViewById(R.id.btn_confirm_reset);

        mPhone = getIntent().getStringExtra("phone_to_reset");
        if (mPhone != null) {
            etPhoneReset.setText(mPhone);
        } else {
            Toast.makeText(this, "未指定账号", Toast.LENGTH_SHORT).show();
        }

        btnConfirmReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleConfirmReset();
            }
        });
    }

    /**
     * (需求3) 对相关密码进行修改，并保存到sd卡对应的xml文件
     */
    private void handleConfirmReset() {
        String newPassword = etNewPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if (mPhone == null || mPhone.isEmpty()) {
            Toast.makeText(this, "错误：未指定账号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (newPassword.isEmpty()) {
            Toast.makeText(this, "新密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        // (修改)
        // 1. 创建 AccountBean 对象
        AccountBean updatedAccount = new AccountBean(mPhone, newPassword);
        // 2. 调用 XmlHelper 保存 (这会自动覆盖旧的)
        XmlHelper.saveAccount(this, updatedAccount);

        Toast.makeText(this, "账号 " + mPhone + " 的密码已重置", Toast.LENGTH_LONG).show();
        finish();
    }
}