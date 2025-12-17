/*
 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements.  See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.
 */

package org.ofbiz.base.util;

import ch.qos.logback.classic.Level;

/**
 * NotifyLevel - Custom level constants for Logback
 * Since Logback's Level class is final, we cannot extend it.
 * Instead, we use standard ERROR level for NOTIFY messages.
 * In Logback, custom levels require complex implementation, so we map NOTIFY to ERROR.
 */
public class NotifyLevel {

    public static final int NOTIFY_INT = Level.ERROR_INT;
    public static final Level NOTIFY = Level.ERROR;
    public static final Level notify = Level.ERROR;

    private NotifyLevel() {
        // Utility class, prevent instantiation
    }
}
