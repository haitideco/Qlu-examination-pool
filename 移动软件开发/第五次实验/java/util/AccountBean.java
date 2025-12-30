package com.example.yidongexperiment05.util;

public class AccountBean {
    private String phone;
    private String password;

    public AccountBean(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    // Getters
    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    // (教学点) 重写 equals 和 hashCode 是 List 操作 (如 contains, remove) 的关键
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AccountBean that = (AccountBean) obj;
        return phone.equals(that.phone); // 我们定义：只要 phone 相同，就是同一个账号
    }

    @Override
    public int hashCode() {
        return phone.hashCode();
    }
}