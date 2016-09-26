/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.eclipse.osgi.internal.wso2;

public class LoadingIntrospection {

    private static ThreadLocal<LoadingIntrospection> introspectionThreadLocal = new ThreadLocal<LoadingIntrospection>();

    private boolean entered = false;
    private String className;

    protected LoadingIntrospection(boolean entered, String className) {
        this.entered = entered;
        this.className = className;
    }

    public static void push(String className) {
        introspectionThreadLocal.set(new LoadingIntrospection(true, className));
    }

    public static void pop() {
        if( introspectionThreadLocal.get() != null) {
            introspectionThreadLocal.get().setEntered(false);
            introspectionThreadLocal.remove();
        }
    }

    public static String getIntroClassName() {
        return introspectionThreadLocal.get().getClassName();
    }

    public static boolean isEnabled() {
        return introspectionThreadLocal.get() != null && introspectionThreadLocal.get().isEntered();
    }

    public boolean isEntered() {
        return entered;
    }

    protected void setEntered(boolean entered) {
        this.entered = entered;
    }

    public String getClassName() {
        return className;
    }

    protected void setClassName(String className) {
        this.className = className;
    }
}
