/*
 * Copyright  2017-2018. the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package vip.lialun.logging;


import vip.lialun.logging.impl.Log4j2Impl;
import vip.lialun.logging.impl.Slf4jImpl;
import vip.lialun.logging.impl.StdOutImpl;

import java.lang.reflect.Constructor;

public final class LogFactory {

    private static Constructor<? extends Log> logConstructor;

    static {
        tryImplementation(LogFactory::useSlf4jLogging);
        tryImplementation(LogFactory::useLog4J2Logging);
        tryImplementation(LogFactory::useStdOutLogging);
    }

    private LogFactory() {
        // disable construction
    }

    public static Log getLog(Class<?> aClass) {
        return getLog(aClass.getName());
    }

    public static Log getLog(String logger) {
        try {
            return logConstructor.newInstance(new Object[]{logger});
        } catch (Throwable t) {
            throw new LogException("Error creating logger for logger " + logger + ".  Cause: " + t, t);
        }
    }

    private static synchronized void useSlf4jLogging() {
        setImplementation(Slf4jImpl.class);
    }

    private static synchronized void useLog4J2Logging() {
        setImplementation(Log4j2Impl.class);
    }

    private static synchronized void useStdOutLogging() {
        setImplementation(StdOutImpl.class);
    }


    private static void tryImplementation(Runnable runnable) {
        if (logConstructor == null) {
            try {
                runnable.run();
            } catch (Throwable t) {
                // ignore
            }
        }
    }

    private static void setImplementation(Class<? extends Log> implClass) {
        try {
            Constructor<? extends Log> candidate = implClass.getConstructor(new Class[]{String.class});
            Log log = candidate.newInstance(new Object[]{LogFactory.class.getName()});
            log.debug("Logging initialized using '" + implClass + "' adapter.");
            logConstructor = candidate;
        } catch (Throwable t) {
            throw new LogException("Error setting Log implementation.  Cause: " + t, t);
        }
    }

}
