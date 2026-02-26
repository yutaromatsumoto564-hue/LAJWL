package com.jinwanli;

import com.jinwanli.util.DataCleaner;

public class MainApp {
    public static void main(String[] args) {
        // 数据清洗
        DataCleaner.cleanExpenseData();
        
        // 启动登录窗口
        new LoginFrame();
    }
} 