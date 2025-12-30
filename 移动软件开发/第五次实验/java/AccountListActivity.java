package com.example.yidongexperiment05;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yidongexperiment05.util.AccountBean;
import com.example.yidongexperiment05.util.XmlHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 仅显示所有账号名
 */
public class AccountListActivity extends AppCompatActivity {

    private ListView lvAccounts;
    private TextView tvEmpty;
    private List<AccountBean> mAccountList; // 存储从XML读取的完整列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_list);

        lvAccounts = findViewById(R.id.lv_accounts);
        tvEmpty = findViewById(R.id.tv_empty);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAccounts();
    }

    private void loadAccounts() {
        // 1. 从 XMLHelper 读取所有账户
        mAccountList = XmlHelper.readAllAccounts(this);

        // 2. 检查是否为空
        if (mAccountList == null || mAccountList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            lvAccounts.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            lvAccounts.setVisibility(View.VISIBLE);

            // 3. 仅显示账号名 (Phone)
            List<String> phoneList = new ArrayList<>();
            for (AccountBean account : mAccountList) {
                phoneList.add(account.getPhone());
            }

            // 4. 使用 ArrayAdapter 显示账号列表
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    phoneList
            );
            lvAccounts.setAdapter(adapter);

            // 5. 设置点击事件
            lvAccounts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // 获取被点击的完整账户信息
                    AccountBean selectedAccount = mAccountList.get(position);

                    // 创建一个 Intent 用于返回数据
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("selected_phone", selectedAccount.getPhone());
                    resultIntent.putExtra("selected_password", selectedAccount.getPassword());

                    // 设置结果，并关闭当前 Activity
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
            });
        }
    }
}