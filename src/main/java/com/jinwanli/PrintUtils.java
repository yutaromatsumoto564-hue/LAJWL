package com.jinwanli;

import java.awt.*;
import java.awt.print.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JOptionPane;

public class PrintUtils implements Printable {
    private String title;
    private Map<String, String> content;
    private String footer;

    public PrintUtils(String title, Map<String, String> content, String footer) {
        this.title = title;
        this.content = content;
        this.footer = footer;
    }

    public static void printTicket(String docName, String title, Map<String, String> content, String footer) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName(docName);
        job.setPrintable(new PrintUtils(title, content, footer));
        
        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "打印失败: " + ex.getMessage());
            }
        }
    }

    @Override
    public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
        if (page > 0) return NO_SUCH_PAGE;

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());

        int y = 50;
        int x = 50;
        int lineHeight = 25;

        g2d.setFont(new Font("微软雅黑", Font.BOLD, 22));
        g2d.drawString(title, x, y);
        y += 40;
        
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawLine(x, y, (int)pf.getImageableWidth() - x, y);
        y += 30;

        g2d.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        for (Map.Entry<String, String> entry : content.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            
            g2d.drawString(key, x, y);
            g2d.drawString(value, x + 200, y); 
            y += lineHeight;
        }

        y += 20;
        g2d.drawLine(x, y, (int)pf.getImageableWidth() - x, y);
        y += 40;

        g2d.setFont(new Font("微软雅黑", Font.ITALIC, 12));
        String[] footers = footer.split("\n");
        for (String line : footers) {
            g2d.drawString(line, x, y);
            y += 20;
        }
        
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.drawString("打印时间: " + time, x, (int)pf.getImageableHeight() - 50);

        return PAGE_EXISTS;
    }
}