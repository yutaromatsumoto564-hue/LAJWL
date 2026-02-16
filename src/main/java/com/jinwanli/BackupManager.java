package com.jinwanli;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;

public class BackupManager {
    private static final String BACKUP_DIR = "backup";
    private static final String DATE_FORMAT = "yyyyMMdd_HHmmss";

    public static void performBackup() {
        try {
            Path backupPath = Paths.get(BACKUP_DIR);
            if (!Files.exists(backupPath)) {
                Files.createDirectories(backupPath);
            }

            String timestamp = new SimpleDateFormat(DATE_FORMAT).format(new Date());
            String fileName = "backup_" + timestamp + ".zip";
            Path targetFile = backupPath.resolve(fileName);

            // 模拟创建文件 (实际应写入 ZipOutputStream)
            Files.createFile(targetFile);
            System.out.println("备份成功: " + targetFile.toAbsolutePath());

            cleanupOldBackups(backupPath);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("备份失败: " + e.getMessage());
        }
    }

    // 使用 NIO 流进行文件清理
    private static void cleanupOldBackups(Path backupDir) {
        long sevenDaysAgo = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000);

        try (Stream<Path> files = Files.list(backupDir)) {
            files.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".zip"))
                    .forEach(path -> {
                        try {
                            long lastModified = Files.getLastModifiedTime(path).toMillis();
                            if (lastModified < sevenDaysAgo) {
                                Files.delete(path);
                                System.out.println("已清理旧备份: " + path.getFileName());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}