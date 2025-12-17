/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.base.util;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.util.exception.ExceptionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;

/**
 * Configurable Debug logging wrapper class
 *
 */
public final class Debug {

    public static final boolean useLog4J = true;
    public static final String noModuleModule = "NoModule";  // set to null for previous behavior

    static DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);

    public static final String SYS_DEBUG = System.getProperty("DEBUG");
    public static final int ALWAYS = 0;
    public static final int VERBOSE = 1;
    public static final int TIMING = 2;
    public static final int INFO = 3;
    public static final int IMPORTANT = 4;
    public static final int WARNING = 5;
    public static final int ERROR = 6;
    public static final int FATAL = 7;
    public static final int NOTIFY = 8;

    public static final String[] levels = {"Always", "Verbose", "Timing", "Info", "Important", "Warning", "Error", "Fatal", "Notify"};
    public static final String[] levelProps = {"", "print.verbose", "print.timing", "print.info", "print.important", "print.warning", "print.error", "print.fatal", "print.notify"};
    // Note: Logback doesn't have FATAL, mapping to ERROR. NOTIFY is custom level.
    public static final Level[] levelObjs = {Level.INFO, Level.DEBUG, Level.INFO, Level.INFO, Level.INFO, Level.WARN, Level.ERROR, Level.ERROR, NotifyLevel.NOTIFY};

    protected static Map<String, Integer> levelStringMap = new HashMap<String, Integer>();

    protected static PrintStream printStream = System.out;
    protected static PrintWriter printWriter = new PrintWriter(printStream);

    protected static boolean levelOnCache[] = new boolean[9];
    protected static boolean packException = true;
    protected static final boolean useLevelOnCache = true;

    protected static Logger root = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

    static {
        levelStringMap.put("verbose", Debug.VERBOSE);
        levelStringMap.put("timing", Debug.TIMING);
        levelStringMap.put("info", Debug.INFO);
        levelStringMap.put("important", Debug.IMPORTANT);
        levelStringMap.put("warning", Debug.WARNING);
        levelStringMap.put("error", Debug.ERROR);
        levelStringMap.put("fatal", Debug.FATAL);
        levelStringMap.put("always", Debug.ALWAYS);
        levelStringMap.put("notify", Debug.NOTIFY);

        // Logback auto-configures from logback.xml in classpath
        // No manual configuration needed like in Log4j
        
        // initialize levelOnCache
        for (int i = 0; i < 9; i++) {
            levelOnCache[i] = (i == Debug.ALWAYS || UtilProperties.propertyValueEqualsIgnoreCase("debug.properties", levelProps[i], "true"));
        }

        if (SYS_DEBUG != null) {
            for (int x = 0; x < 8; x++) {
                levelOnCache[x] = true;
            }
            // Set all loggers to DEBUG level using Logback API
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            for (ch.qos.logback.classic.Logger logger : loggerContext.getLoggerList()) {
                logger.setLevel(Level.DEBUG);
            }
        }

        // configure exception packing
        packException = UtilProperties.propertyValueEqualsIgnoreCase("debug.properties", "pack.exception", "true");
    }

    public static PrintStream getPrintStream() {
        return printStream;
    }

    public static void setPrintStream(PrintStream printStream) {
        Debug.printStream = printStream;
        Debug.printWriter = new PrintWriter(printStream);
    }

    public static PrintWriter getPrintWriter() {
        return printWriter;
    }

    public static Logger getLogger(String module) {
        if (UtilValidate.isNotEmpty(module)) {
            return LoggerFactory.getLogger(module);
        } else {
            return root;
        }
    }

    /** Gets an Integer representing the level number from a String representing the level name; will return null if not found */
    public static Integer getLevelFromString(String levelName) {
        if (levelName == null) return null;
        return levelStringMap.get(levelName.toLowerCase());
    }

    /** Gets an int representing the level number from a String representing the level name; if level not found defaults to Debug.INFO */
    public static int getLevelFromStringWithDefault(String levelName) {
        Integer levelInt = getLevelFromString(levelName);
        if (levelInt == null) {
            return Debug.INFO;
        } else {
            return levelInt;
        }
    }

    public static void putMDC(String key, Object obj) {
        MDC.put(key, String.valueOf(obj));
    }

    public static void removeMDC(String key) {
        MDC.remove(key);
    }

    /**
     * Helper method to log with SLF4J using appropriate level
     */
    private static void logWithLevel(Logger logger, Level level, String msg, Throwable t) {
        if (level == Level.TRACE) {
            if (t != null) logger.trace(msg, t);
            else logger.trace(msg);
        } else if (level == Level.DEBUG) {
            if (t != null) logger.debug(msg, t);
            else logger.debug(msg);
        } else if (level == Level.INFO) {
            if (t != null) logger.info(msg, t);
            else logger.info(msg);
        } else if (level == Level.WARN) {
            if (t != null) logger.warn(msg, t);
            else logger.warn(msg);
        } else if (level == Level.ERROR) {
            if (t != null) logger.error(msg, t);
            else logger.error(msg);
        } else {
            // Default to INFO for unknown levels
            if (t != null) logger.info(msg, t);
            else logger.info(msg);
        }
    }

    public static void log(int level, Throwable t, String msg, String module) {
        log(level, t, msg, module, "org.ofbiz.base.util.Debug");
    }

    public static void log(int level, Throwable t, String msg, String module, String callingClass) {
        if (isOn(level)) {
            // pack the exception
            if (packException && t != null) {
                msg = System.getProperty("line.separator") + ExceptionHelper.packException(msg, t, true);
                t = null;
            }

            // log
            if (useLog4J) {
                Logger logger = getLogger(module);
                if (SYS_DEBUG != null && logger instanceof ch.qos.logback.classic.Logger) {
                    ((ch.qos.logback.classic.Logger)logger).setLevel(Level.DEBUG);
                }
                // SLF4J doesn't have log(callingClass, level, msg, t), use level-specific methods
                logWithLevel(logger, levelObjs[level], msg, t);
            } else {
                StringBuilder prefixBuf = new StringBuilder();

                prefixBuf.append(dateFormat.format(new java.util.Date()));
                prefixBuf.append(" [OFBiz");
                if (module != null) {
                    prefixBuf.append(":");
                    prefixBuf.append(module);
                }
                prefixBuf.append(":");
                prefixBuf.append(levels[level]);
                prefixBuf.append("] ");
                if (msg != null) {
                    getPrintWriter().print(prefixBuf.toString());
                    getPrintWriter().println(msg);
                }
                if (t != null) {
                    getPrintWriter().print(prefixBuf.toString());
                    getPrintWriter().println("Received throwable:");
                    t.printStackTrace(getPrintWriter());
                }
            }
        }
    }

    public static boolean isOn(int level) {
        if (useLevelOnCache) {
            return levelOnCache[level];
        } else {
            return (level == Debug.ALWAYS || UtilProperties.propertyValueEqualsIgnoreCase("debug", levelProps[level], "true"));
        }
    }

    // leaving these here
    public static void log(String msg) {
        log(Debug.ALWAYS, null, msg, noModuleModule);
    }
    public static void log(Throwable t) {
        log(Debug.ALWAYS, t, null, noModuleModule);
    }

    public static void log(String msg, String module) {
        log(Debug.ALWAYS, null, msg, module);
    }


    public static void log(Throwable t, String module) {
        log(Debug.ALWAYS, t, null, module);
    }

    public static void log(Throwable t, String msg, String module) {
        log(Debug.ALWAYS, t, msg, module);
    }

    public static boolean verboseOn() {
        return isOn(Debug.VERBOSE);
    }

    public static void logVerbose(String msg, String module) {
        log(Debug.VERBOSE, null, msg, module);
    }

    public static void logVerbose(Throwable t, String module) {
        log(Debug.VERBOSE, t, null, module);
    }

    public static void logVerbose(Throwable t, String msg, String module) {
        log(Debug.VERBOSE, t, msg, module);
    }

    public static boolean timingOn() {
        return isOn(Debug.TIMING);
    }

    public static void logTiming(String msg, String module) {
        log(Debug.TIMING, null, msg, module);
    }

    public static void logTiming(Throwable t, String module) {
        log(Debug.TIMING, t, null, module);
    }

    public static void logTiming(Throwable t, String msg, String module) {
        log(Debug.TIMING, t, msg, module);
    }

    public static boolean infoOn() {
        return isOn(Debug.INFO);
    }

    public static void logInfo(String msg, String module) {
        log(Debug.INFO, null, msg, module);
    }

    public static void logInfo(Throwable t, String module) {
        log(Debug.INFO, t, null, module);
    }

    public static void logInfo(Throwable t, String msg, String module) {
        log(Debug.INFO, t, msg, module);
    }

    public static boolean importantOn() {
        return isOn(Debug.IMPORTANT);
    }

    public static void logImportant(String msg, String module) {
        log(Debug.IMPORTANT, null, msg, module);
    }

    public static void logImportant(Throwable t, String module) {
        log(Debug.IMPORTANT, t, null, module);
    }

    public static void logImportant(Throwable t, String msg, String module) {
        log(Debug.IMPORTANT, t, msg, module);
    }

    public static boolean warningOn() {
        return isOn(Debug.WARNING);
    }

    public static void logWarning(String msg, String module) {
        log(Debug.WARNING, null, msg, module);
    }

    public static void logWarning(Throwable t, String module) {
        log(Debug.WARNING, t, null, module);
    }

    public static void logWarning(Throwable t, String msg, String module) {
        log(Debug.WARNING, t, msg, module);
    }

    public static boolean errorOn() {
        return isOn(Debug.ERROR);
    }

    public static void logError(String msg, String module) {
        log(Debug.ERROR, null, msg, module);
    }

    public static void logError(Throwable t, String module) {
        log(Debug.ERROR, t, null, module);
    }

    public static void logError(Throwable t, String msg, String module) {
        log(Debug.ERROR, t, msg, module);
    }

    public static boolean fatalOn() {
        return isOn(Debug.FATAL);
    }

    public static void logFatal(String msg, String module) {
        log(Debug.FATAL, null, msg, module);
    }

    public static void logFatal(Throwable t, String module) {
        log(Debug.FATAL, t, null, module);
    }

    public static void logFatal(Throwable t, String msg, String module) {
        log(Debug.FATAL, t, msg, module);
    }

    public static void logNotify(String msg, String module) {
        log(Debug.NOTIFY, null, msg, module);
    }

    public static void logNotify(Throwable t, String module) {
        log(Debug.NOTIFY, t, null, module);
    }

    public static void logNotify(Throwable t, String msg, String module) {
        log(Debug.NOTIFY, t, msg, module);
    }

    public static void set(int level, boolean on) {
        if (!useLevelOnCache)
            return;
        levelOnCache[level] = on;
    }

    public static synchronized Appender<ch.qos.logback.classic.spi.ILoggingEvent> getNewFileAppender(String name, String logFile, long maxSize, int backupIdx, String pattern) {
        if (pattern == null) {
            pattern = "%-5r[%24F:%-3L:%-5p]%X %m%n";
        }

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        
        RollingFileAppender<ch.qos.logback.classic.spi.ILoggingEvent> newAppender = 
            new RollingFileAppender<ch.qos.logback.classic.spi.ILoggingEvent>();
        
        newAppender.setContext(context);
        newAppender.setName(name);
        newAppender.setFile(logFile);
        newAppender.setAppend(true);
        
        // Configure rolling policy
        ch.qos.logback.core.rolling.FixedWindowRollingPolicy rollingPolicy = 
            new ch.qos.logback.core.rolling.FixedWindowRollingPolicy();
        rollingPolicy.setContext(context);
        rollingPolicy.setParent(newAppender);
        rollingPolicy.setFileNamePattern(logFile + ".%i");
        rollingPolicy.setMinIndex(1);
        if (backupIdx > 0) {
            rollingPolicy.setMaxIndex(backupIdx);
        } else {
            rollingPolicy.setMaxIndex(10);
        }
        rollingPolicy.start();
        
        // Configure triggering policy
        ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy<ch.qos.logback.classic.spi.ILoggingEvent> triggeringPolicy = 
            new ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy<ch.qos.logback.classic.spi.ILoggingEvent>();
        triggeringPolicy.setContext(context);
        if (maxSize > 0) {
            triggeringPolicy.setMaxFileSize(ch.qos.logback.core.util.FileSize.valueOf(String.valueOf(maxSize)));
        } else {
            triggeringPolicy.setMaxFileSize(ch.qos.logback.core.util.FileSize.valueOf("10MB"));
        }
        triggeringPolicy.start();
        
        newAppender.setRollingPolicy(rollingPolicy);
        newAppender.setTriggeringPolicy(triggeringPolicy);
        
        // Configure encoder
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern(pattern);
        encoder.start();
        
        newAppender.setEncoder(encoder);
        newAppender.start();

        return newAppender;
    }

    public static boolean registerFileAppender(String module, String name, String logFile, long maxSize, int backupIdx, String pattern) {
        Logger logger = LoggerFactory.getLogger(module);
        
        // Cast to Logback logger to access appender methods
        if (!(logger instanceof ch.qos.logback.classic.Logger)) {
            return false;
        }
        
        ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) logger;
        boolean found = false;

        Appender<ch.qos.logback.classic.spi.ILoggingEvent> foundAppender = logbackLogger.getAppender(name);
        if (foundAppender == null) {
            // Search in all loggers
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            for (ch.qos.logback.classic.Logger log : context.getLoggerList()) {
                foundAppender = log.getAppender(name);
                if (foundAppender != null) {
                    break;
                }
            }
        } else {
            return true;
        }

        if (foundAppender == null) {
            if (logFile != null) {
                foundAppender = getNewFileAppender(name, logFile, maxSize, backupIdx, pattern);
                if (foundAppender != null) {
                    found = true;
                }
            }
        } else {
            found = true;
        }

        if (foundAppender != null) {
            logbackLogger.addAppender(foundAppender);
        }
        return found;
    }

    public static boolean registerFileAppender(String module, String name, String logFile) {
        return registerFileAppender(module, name, logFile, 0, 10, null);
    }

    public static boolean registerFileAppender(String module, String name) {
        return registerFileAppender(module, name, null, -1, -1, null);
    }
}
