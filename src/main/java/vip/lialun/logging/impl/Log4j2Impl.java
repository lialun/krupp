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

package vip.lialun.logging.impl;

import vip.lialun.logging.Log;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log4j2Impl implements Log {
    private StdOutImpl stdOut;
    private Logger log;

    public Log4j2Impl(String clazz) {
        log = LogManager.getLogger(clazz);
        stdOut = new StdOutImpl(clazz);
    }

    @Override
    public void debug(String s) {
        try {
            log.debug(s);
        } catch (Exception ex) {
            stdOut.debug(s);
            ex.printStackTrace();
        }

    }

    @Override
    public void debug(String s, Throwable e) {
        try {
            log.debug(s, e);
        } catch (Exception ex) {
            stdOut.debug(s, e);
            ex.printStackTrace();
        }
    }

    @Override
    public void info(String s) {
        try {
            log.info(s);
        } catch (Exception ex) {
            stdOut.info(s);
            ex.printStackTrace();
        }
    }

    @Override
    public void info(String s, Throwable e) {
        try {
            log.info(s, e);
        } catch (Exception ex) {
            stdOut.info(s, e);
            ex.printStackTrace();
        }
    }

    @Override
    public void warn(String s) {
        try {
            log.warn(s);
        } catch (Exception ex) {
            stdOut.warn(s);
            ex.printStackTrace();
        }
    }

    @Override
    public void warn(String s, Throwable e) {
        try {
            log.warn(s, e);
        } catch (Exception ex) {
            stdOut.warn(s, e);
            ex.printStackTrace();
        }
    }

    @Override
    public void error(String s) {
        try {
            log.error(s);
        } catch (Exception ex) {
            stdOut.error(s);
            ex.printStackTrace();
        }
    }

    @Override
    public void error(String s, Throwable e) {
        try {
            log.error(s, e);
        } catch (Exception ex) {
            stdOut.error(s, e);
            ex.printStackTrace();
        }
    }
}
