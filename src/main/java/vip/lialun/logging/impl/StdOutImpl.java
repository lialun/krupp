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

public class StdOutImpl implements Log {
    private static final String SEPARATOR = ": ";
    private String clazz;

    public StdOutImpl(String clazz) {
        this.clazz = clazz;
    }

    @Override
    public void debug(String s) {
        System.out.println(clazz + SEPARATOR + s);
    }

    @Override
    public void debug(String s, Throwable e) {
        System.out.println(clazz + SEPARATOR + s);
        e.printStackTrace();
    }

    @Override
    public void info(String s) {
        System.out.println(clazz + SEPARATOR + s);
    }

    @Override
    public void info(String s, Throwable e) {
        System.out.println(clazz + SEPARATOR + s);
        e.printStackTrace();
    }

    @Override
    public void warn(String s) {
        System.out.println(clazz + SEPARATOR + s);
    }

    @Override
    public void warn(String s, Throwable e) {
        System.out.println(clazz + SEPARATOR + s);
        e.printStackTrace();
    }

    @Override
    public void error(String s) {
        System.out.println(clazz + SEPARATOR + s);
    }

    @Override
    public void error(String s, Throwable e) {
        System.out.println(clazz + SEPARATOR + s);
        e.printStackTrace(System.err);
    }
}
