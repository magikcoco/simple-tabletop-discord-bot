package com.magikcoco.manager;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggingManager {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static FileHandler fh;
    private final static LoggingManager INSTANCE = new LoggingManager();

    private LoggingManager(){
        try {
            File logFile = new File("log"+(System.currentTimeMillis()/1000L)+".txt");
            boolean result = logFile.createNewFile();
            fh = new FileHandler(logFile.getAbsolutePath());
            LOGGER.addHandler(fh);
            SimpleFormatter sf = new SimpleFormatter();
            fh.setFormatter(sf);
            LOGGER.setUseParentHandlers(false);
            if(result){
                LOGGER.log(Level.INFO, "New log file was made");
            } else {
                LOGGER.log(Level.INFO, "Using existing log file");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Critical error occurred creating the LoggingManager");
            System.exit(2);
        }
    }

    public static LoggingManager getInstance(){

        return INSTANCE;
    }

    /**
     * @param logMessage the message attached to this log
     */
    public void logInfo(String logMessage){
        LOGGER.log(Level.INFO, logMessage);
    }

    /**
     * @param logMessage the message attached to this log
     */
    public void logError(String logMessage){
        LOGGER.log(Level.SEVERE, logMessage);
    }
}
