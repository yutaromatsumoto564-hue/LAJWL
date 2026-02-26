package com.jinwanli;

import com.jinwanli.util.DataCleaner;

public class MainApp {
    public static void main(String[] args) {
        // 数据清洗
        DataCleaner.cleanExpenseData();
        
        // 直接启动主窗口，跳过登录
        new MainFrame().setVisible(true);
    }
} 