package com.example.yidongexperiment05.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import com.example.yidongexperiment05.util.AccountBean; // (修改) 导入新模型

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList; // (修改) 使用 List
import java.util.List; // (修改) 使用 List

public class XmlHelper {

    private static final String FILE_NAME = "account_list.xml"; // (修改) 新文件名
    private static final String TAG = "XmlHelper";

    // (修改) XML 标签常量
    private static final String TAG_ROOT = "all_accounts";
    private static final String TAG_ACCOUNT = "account";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_PASSWORD = "password";

    private static File getFile(Context context) {
        File directory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        if (directory != null && !directory.exists()) {
            directory.mkdirs();
        }
        return new File(directory, FILE_NAME);
    }

    /**
     * (重写) 读取所有账号信息
     * @param context 上下文
     * @return 账号列表
     */
    public static List<AccountBean> readAllAccounts(Context context) {
        List<AccountBean> accountList = new ArrayList<>();
        File file = getFile(context);
        if (!file.exists()) {
            return accountList; // 返回空列表
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(fis, "UTF-8");

            int eventType = parser.getEventType();
            AccountBean currentAccount = null;
            String currentTag = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        currentTag = parser.getName();
                        if (TAG_ACCOUNT.equals(currentTag)) {
                            // 遇到 <account> 标签，准备一个空 Phone 和 Password
                            currentAccount = new AccountBean("", "");
                        }
                        break;
                    case XmlPullParser.TEXT:
                        if (currentAccount != null) {
                            if (TAG_PHONE.equals(currentTag)) {
                                // (临时存储，因为 bean 是不可变的)
                                currentAccount = new AccountBean(parser.getText(), currentAccount.getPassword());
                            } else if (TAG_PASSWORD.equals(currentTag)) {
                                currentAccount = new AccountBean(currentAccount.getPhone(), parser.getText());
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (TAG_ACCOUNT.equals(parser.getName()) && currentAccount != null) {
                            // 遇到 </account> 标签，将完整对象添加到列表
                            if (!currentAccount.getPhone().isEmpty()) {
                                accountList.add(currentAccount);
                            }
                            currentAccount = null; // 重置
                        }
                        currentTag = null;
                        break;
                }
                eventType = parser.next();
            }
        } catch (IOException | XmlPullParserException e) {
            Log.e(TAG, "读取 XML 文件失败", e);
        }
        return accountList;
    }

    /**
     * (重写) 保存或更新一个账号
     * @param context 上下文
     * @param newAccount 要保存的账号
     */
    public static void saveAccount(Context context, AccountBean newAccount) {
        // 1. 先读取所有旧账号
        List<AccountBean> accountList = readAllAccounts(context);

        // 2. 检查账号是否已存在 (利用 AccountBean.equals 方法)
        // 如果存在，先移除旧的
        if (accountList.contains(newAccount)) {
            accountList.remove(newAccount);
        }

        // 3. 添加新（或更新）的账号
        accountList.add(newAccount);

        // 4. 将整个列表写回文件
        writeXml(context, accountList);
    }

    /**
     * (新增) 将账号列表完整写入 XML 文件
     */
    private static void writeXml(Context context, List<AccountBean> accountList) {
        File file = getFile(context);
        try (FileOutputStream fos = new FileOutputStream(file)) { // (会覆盖旧文件)
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, "UTF-8");
            serializer.startDocument("UTF-8", true);
            serializer.startTag(null, TAG_ROOT); // <all_accounts>

            for (AccountBean account : accountList) {
                serializer.startTag(null, TAG_ACCOUNT); // <account>

                serializer.startTag(null, TAG_PHONE);
                serializer.text(account.getPhone());
                serializer.endTag(null, TAG_PHONE);

                serializer.startTag(null, TAG_PASSWORD);
                serializer.text(account.getPassword());
                serializer.endTag(null, TAG_PASSWORD);

                serializer.endTag(null, TAG_ACCOUNT); // </account>
            }

            serializer.endTag(null, TAG_ROOT); // </all_accounts>
            serializer.endDocument();
            Log.d(TAG, "多账户 XML 已保存");
        } catch (IOException e) {
            Log.e(TAG, "写入 XML 文件失败", e);
        }
    }

    /**
     * (重写) 清除一个账号
     */
    public static void clearAccount(Context context, String phone) {
        if (phone == null || phone.isEmpty()) return;

        List<AccountBean> accountList = readAllAccounts(context);
        AccountBean toRemove = new AccountBean(phone, ""); // (利用 equals 只比较 phone)

        if (accountList.contains(toRemove)) {
            accountList.remove(toRemove);
            writeXml(context, accountList); // 写回剩余的账号
            Log.d(TAG, "账号 " + phone + " 已清除");
        }
    }
}