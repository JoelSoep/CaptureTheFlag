package me.joelsoep.ctf.logger;

import lombok.Getter;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.file.FileManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class CTFLogger {
   @Getter
   private static final Logger logger = Logger.getLogger("CTF");
   FileHandler fileHandler;
   Handler customHandler = new ConsoleHandler();

    public CTFLogger() {
        logger.setUseParentHandlers(false);
        Thread.setDefaultUncaughtExceptionHandler(new GlobalExceptionHandler());
        customHandler.setFormatter(new SimpleFormatter() {

            @Override
            public String format(LogRecord record) {
                return "";
            }
        });
        customHandler.setLevel(Level.ALL);
        resetFileHandler(Main.game.getUuid().toString());
        logger.addHandler(customHandler);
    }

    public void resetFileHandler(String fileName) {
        try {
            this.fileHandler = new FileHandler(FileManager.getLogFolderPath() + fileName + ".log");
            this.fileHandler.setFormatter(new CTFLoggerFormatter());
            logger.setLevel(Level.ALL);
            logger.addHandler(fileHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class CTFLoggerFormatter extends Formatter {

        @Override
        public String format(LogRecord record) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            String timestamp = dateFormat.format(new Date(record.getMillis()));
            return timestamp + ": " + record.getMessage() + System.lineSeparator();
        }
    }

    private static class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            LogModule.CORE.severe("Uncaught exception thread " + t.getName() + ": " + System.lineSeparator() + e.getMessage());
        }
    }

}
