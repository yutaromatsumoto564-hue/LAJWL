package com.jinwanli;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class PdfUtils {

    public static void generateAndOpenPdf(String docName, String title, Map<String, String> content, String footer) {
        try {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            PDFont font = loadChineseFont(document);
            if (font == null) {
                JOptionPane.showMessageDialog(null, "错误：未找到中文字体(SimHei/MsYaHei)，PDF可能无法显示中文。");
                return;
            }

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            float y = 750;
            float margin = 50;
            float width = page.getMediaBox().getWidth() - 2 * margin;

            contentStream.beginText();
            contentStream.setFont(font, 24);
            contentStream.newLineAtOffset(margin, y);
            contentStream.showText(title);
            contentStream.endText();
            y -= 40;

            contentStream.moveTo(margin, y);
            contentStream.lineTo(margin + width, y);
            contentStream.stroke();
            y -= 30;

            contentStream.beginText();
            contentStream.setFont(font, 14);
            contentStream.setLeading(25f);
            contentStream.newLineAtOffset(margin, y);

            for (Map.Entry<String, String> entry : content.entrySet()) {
                String line = entry.getKey() + "    " + entry.getValue();
                contentStream.showText(line);
                contentStream.newLine();
                y -= 25;
            }
            contentStream.endText();

            y -= 20;
            contentStream.moveTo(margin, y);
            contentStream.lineTo(margin + width, y);
            contentStream.stroke();
            y -= 30;

            contentStream.beginText();
            contentStream.setFont(font, 12);
            contentStream.setLeading(18f);
            contentStream.newLineAtOffset(margin, y);
            
            String[] footerLines = footer.split("\n");
            for (String fLine : footerLines) {
                contentStream.showText(fLine);
                contentStream.newLine();
            }
            
            contentStream.newLine();
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            contentStream.showText("生成时间: " + time);
            
            contentStream.endText();
            contentStream.close();

            String fileName = "PrintPreview_" + System.currentTimeMillis() + ".pdf";
            document.save(fileName);
            document.close();

            if (Desktop.isDesktopSupported()) {
                File pdfFile = new File(fileName);
                Desktop.getDesktop().open(pdfFile);
            } else {
                JOptionPane.showMessageDialog(null, "PDF已生成，但系统不支持自动打开。\n文件路径: " + fileName);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "PDF生成失败: " + e.getMessage());
        }
    }

    private static PDFont loadChineseFont(PDDocument doc) {
        String[] fontPaths = {
            "C:/Windows/Fonts/simhei.ttf",
            "C:/Windows/Fonts/msyh.ttf",
            "C:/Windows/Fonts/simsun.ttc",
            "/System/Library/Fonts/PingFang.ttc",
            "/usr/share/fonts/truetype/droid/DroidSansFallbackFull.ttf"
        };

        for (String path : fontPaths) {
            File file = new File(path);
            if (file.exists()) {
                try {
                    return PDType0Font.load(doc, file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}