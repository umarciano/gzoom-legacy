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
import java.io.PrintWriter;
import java.io.Writer;

import org.slf4j.Logger;
import ch.qos.logback.classic.Level;

/**
 * Writer implementation for writing to an SLF4J logger.
 * Renamed from Log4jLoggerWriter but maintains compatibility.
 */
public class Log4jLoggerWriter extends PrintWriter {

    public Log4jLoggerWriter(Logger logger) {
        this(logger, Level.INFO);
    }

    public Log4jLoggerWriter(Logger logger, Level level) {
        super(new Slf4jPrintWriter(logger, level), true);
    }

    static class Slf4jPrintWriter extends Writer {

        private Logger logger = null;
        private Level level = null;
        private boolean closed = false;

        public Slf4jPrintWriter(Logger logger, Level level) {
            lock = logger;
            this.logger = logger;
            this.level = level;
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            if (closed) {
                throw new IOException("Writer is closed");
            }

            // Remove the eol
            while (len > 0 && (cbuf[len - 1] == '\n' || cbuf[len - 1] == '\r')) {
                len--;
            }

            // send to slf4j logger
            if (len > 0) {
                String message = String.copyValueOf(cbuf, off, len);
                if (level == Level.TRACE) {
                    logger.trace(message);
                } else if (level == Level.DEBUG) {
                    logger.debug(message);
                } else if (level == Level.INFO) {
                    logger.info(message);
                } else if (level == Level.WARN) {
                    logger.warn(message);
                } else if (level == Level.ERROR) {
                    logger.error(message);
                } else {
                    logger.info(message);
                }
            }
        }

        @Override
        public void flush() throws IOException {
            if (closed) {
                throw new IOException("Writer is closed");
            }
        }

        @Override
        public void close() {
            closed = true;
        }
    }
}

