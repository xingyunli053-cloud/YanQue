package com.yanque.commons.config.logging;

import ch.qos.logback.core.rolling.RolloverFailure;
import ch.qos.logback.core.rolling.RollingPolicyBase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 使用"年月日-随机数.log"命名的日志滚动策略。
 */
public class RandomSizeRollingPolicy extends RollingPolicyBase {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
    private static final int MAX_NAME_ATTEMPTS = 100;

    private String logDirectory;
    private String activeFileName;

    /**
     * 设置日志保存目录。
     *
     * @param logDirectory 日志目录
     */
    public void setLogDirectory(String logDirectory) {
        this.logDirectory = logDirectory;
    }

    @Override
    public void start() {
        if (logDirectory == null || logDirectory.isBlank()) {
            addError("日志目录不能为空");
            return;
        }

        try {
            Files.createDirectories(Path.of(logDirectory));
            activeFileName = nextFileName();
            super.start();
        } catch (IOException ex) {
            addError("无法创建日志目录或日志文件名", ex);
        }
    }

    @Override
    public void rollover() throws RolloverFailure {
        try {
            activeFileName = nextFileName();
        } catch (IOException ex) {
            throw new RolloverFailure("无法创建新的日志文件名", ex);
        }
    }

    @Override
    public String getActiveFileName() {
        return activeFileName;
    }

    private String nextFileName() throws IOException {
        Path directory = Path.of(logDirectory);
        String date = LocalDate.now().format(DATE_FORMATTER);

        for (int attempt = 0; attempt < MAX_NAME_ATTEMPTS; attempt++) {
            String randomNumber = String.format("%08d", ThreadLocalRandom.current().nextInt(100_000_000));
            Path candidate = directory.resolve(date + "-" + randomNumber + ".log");
            if (Files.notExists(candidate)) {
                return candidate.toAbsolutePath().toString();
            }
        }

        throw new IOException("无法生成不重复的日志文件名");
    }
}
