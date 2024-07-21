package me.joelsoep.ctf.util.deprecated;

import lombok.Getter;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.file.FileManager;
import me.joelsoep.ctf.logger.LogModule;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.*;

@Deprecated
public class CTFLogger {
   @Getter
   private static final Logger logger = Logger.getLogger("CTF");
   FileHandler fileHandler;
   Handler customHandler = new ConsoleHandler();

    public CTFLogger() {
        logger.setUseParentHandlers(true);
        //Thread.setDefaultUncaughtExceptionHandler(new GlobalExceptionHandler());
        customHandler.setFormatter(new SimpleFormatter() {

            @Override
            public String format(LogRecord record) {
                return "";
            }
        });
        customHandler.setLevel(Level.ALL);
        resetFileHandler(Main.getGame().getUuid().toString());
        logger.addHandler(customHandler);
    }

    public void resetFileHandler(String fileName) {
        try {
            this.closeFileHandler();
            this.fileHandler = new FileHandler(FileManager.getLogFolderPath() + fileName + ".log");
            this.fileHandler.setFormatter(new CTFLoggerFormatter());
            logger.setLevel(Level.ALL);
            logger.addHandler(fileHandler);
            LogModule.CORE.log("--- START OF NEW LOG FILE ---");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeFileHandler() {
        if (this.fileHandler == null) {
            return;
        }
        this.fileHandler.close();;
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
            Arrays.stream(e.getStackTrace()).toList().forEach(element -> LogModule.CORE.severe(element.toString()));
            System.err.println(e.getMessage());
            Arrays.stream(e.getStackTrace()).toList().forEach(element -> System.err.println(element.toString()));
        }
    }

}
