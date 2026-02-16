package com.jinwanli;

import java.io.*;
import java.util.Properties;

public class ConfigManager {
    private static final String CONFIG_FILE = "config.properties";
    private static Properties properties;
    
    static {
        properties = new Properties();
        loadConfig();
    }
    
    private static void loadConfig() {
        try {
            File configFile = new File(CONFIG_FILE);
            if (configFile.exists()) {
                FileInputStream fis = new FileInputStream(configFile);
                properties.load(fis);
                fis.close();
            } else {
                // 创建默认配置文件
                createDefaultConfig();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void createDefaultConfig() {
        try {
            // 设置默认用户名和密码（实际项目中应该加密存储）
            properties.setProperty("username", "admin");
            properties.setProperty("password", "123456");
            properties.setProperty("app.name", "金万里企业管理");
            properties.setProperty("app.version", "1.0.0");
            
            saveConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void saveConfig() {
        try {
            FileOutputStream fos = new FileOutputStream(CONFIG_FILE);
            properties.store(fos, "金万里企业管理系统配置文件");
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public static void setProperty(String key, String value) {
        properties.setProperty(key, value);
        saveConfig();
    }
    
    public static boolean validateLogin(String username, String password) {
        String savedUsername = getProperty("username");
        String savedPassword = getProperty("password");
        return username.equals(savedUsername) && password.equals(savedPassword);
    }
    
    public static void changePassword(String newPassword) {
        setProperty("password", newPassword);
    }
}