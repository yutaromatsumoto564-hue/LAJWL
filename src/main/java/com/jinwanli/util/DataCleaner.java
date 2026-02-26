package com.jinwanli.util;

import com.jinwanli.DataManager;
import com.jinwanli.model.ExpenseRecord;
import java.util.ArrayList;
import java.util.List;

public class DataCleaner {
    public static void cleanExpenseData() {
        // 获取当前的支出记录
        List<ExpenseRecord> expenseRecords = DataManager.getInstance().getExpenseRecords();
        List<ExpenseRecord> cleanedRecords = new ArrayList<>();
        
        for (ExpenseRecord record : expenseRecords) {
            // 跳过房租和设备维护分类
            if ("房租".equals(record.getCategory()) || "设备维护".equals(record.getCategory())) {
                continue;
            }
            
            // 修改原材料采购为材料采购
            if ("原材料采购".equals(record.getCategory())) {
                record.setCategory("材料采购");
            }
            
            // 添加到清理后的列表
            cleanedRecords.add(record);
        }
        
        // 替换原有的记录列表
        // 先清空原列表
        expenseRecords.clear();
        // 再添加清理后的记录
        expenseRecords.addAll(cleanedRecords);
        // 保存到文件
        DataManager.getInstance().saveExpenses();
        
        System.out.println("Expense data cleaned successfully. Removed " + (DataManager.getInstance().getExpenseRecords().size() - cleanedRecords.size()) + " records.");
    }
}
