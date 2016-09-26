/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.test.ruwan.osgi.debug;

import com.sun.jdi.*;
import com.sun.jdi.event.*;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequestManager;

import java.io.IOException;
import java.util.List;

public class DebugMain {

    public static final String CLASS_NAME = "org.eclipse.osgi.internal.baseadaptor.DefaultClassLoader";
    public static final String FIELD_NAME = "loadClass";
    public static final String METHOD_NAME = "loadClass";
    private static final String INSPECT_CLASS_NAME = "org.wso2.carbon.dataservices.core.odata.ODataEndpoint";

    public static void main(String[] args)
            throws IOException, InterruptedException, IncompatibleThreadStateException {

        // connect
        VirtualMachine vm = new VMAcquirer().connect(5005);

        // set watch field on already loaded classes
        List<ReferenceType> referenceTypes = vm.classesByName(CLASS_NAME);
        for (ReferenceType refType : referenceTypes) {
            addMethodWatch(vm, refType);
        }
        // watch for loaded classes
        addClassWatch(vm);

        // resume the vm
        vm.resume();

        // process events
        EventQueue eventQueue = vm.eventQueue();
        while (true) {
            EventSet eventSet = eventQueue.remove();
            for (Event event : eventSet) {
                System.out.println("debug .x");
                if (event instanceof VMDeathEvent || event instanceof VMDisconnectEvent) {
                    // exit
                    return;
                } else if (event instanceof ClassPrepareEvent) {
                    // watch field on loaded class
                    ClassPrepareEvent classPrepEvent = (ClassPrepareEvent) event;
                    ReferenceType refType = classPrepEvent.referenceType();
                    addMethodWatch(vm, refType);
                } else if (event instanceof ModificationWatchpointEvent) {
                    // a Test.foo has changed
                    ModificationWatchpointEvent modEvent = (ModificationWatchpointEvent) event;
                    System.out.println("old=" + modEvent.valueCurrent());
                    System.out.println("new=" + modEvent.valueToBe());
                    System.out.println();
                } else if (event instanceof BreakpointEvent) {
                    BreakpointEvent breakpointEvent = (BreakpointEvent) event;
                    for (StackFrame sf : breakpointEvent.thread().frames()) {
                        int i = 0;
                        try {
                            for (Value lv : sf.getArgumentValues()) {
                                System.out.println("lv ...-------------- " + lv.toString());
                                if (i == 0 && lv instanceof StringReference) {
                                    StringReference stringReference = (StringReference) lv;
                                    if (stringReference.value()
                                            .equals(INSPECT_CLASS_NAME)) {
                                        System.out.println("Called ...");
                                    }
                                }
                                i++;

                            }
                        } catch (Exception e) {
                            //System.out.println("Error Occured: " + e.getMessage());
                        }
                    }
                }
            }
            eventSet.resume();
        }
    }

    /** Watch all classes of name "Test" */
    private static void addClassWatch(VirtualMachine vm) {
        EventRequestManager erm = vm.eventRequestManager();
        ClassPrepareRequest classPrepareRequest = erm.createClassPrepareRequest();
        classPrepareRequest.addClassFilter(CLASS_NAME);
        classPrepareRequest.setEnabled(true);
    }

    /** Watch field of name "foo" */
    private static void addMethodWatch(VirtualMachine vm, ReferenceType refType) {
        EventRequestManager erm = vm.eventRequestManager();
        List<Method> methods = refType.methodsByName(METHOD_NAME);
        for (Method method : methods) {
            BreakpointRequest breakpointRequest = null;
//            try {
//                Location location =
//                breakpointRequest = erm.createBreakpointRequest(.get(0));
//            } catch (AbsentInformationException e) {
//                e.printStackTrace();
//            }
            //                breakpointRequest = erm.createBreakpointRequest(method.location());
            breakpointRequest.setEnabled(true);
        }

    }

}
