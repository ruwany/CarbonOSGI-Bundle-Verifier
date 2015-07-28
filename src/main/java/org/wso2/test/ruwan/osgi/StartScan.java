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

package org.wso2.test.ruwan.osgi;

import java.io.File;
import java.util.*;

public class StartScan {

    private String carbonHome;

    public static void main(String[] args) {
        String carbonHome = System.getenv("CARBON_HOME");
        if (carbonHome == null) {
            if (args.length <= 0) {
                System.out.println(
                        "Either CARBON_HOME environment needs to be set or provide the carbon home as an argument");
                return;
            } else {
                carbonHome = args[0];
            }
        }

        StartScan startScan = new StartScan();
        startScan.dosScan(carbonHome);
    }

    private void dosScan(String carbonHome) {
        this.carbonHome = carbonHome;
        File root = new File(carbonHome);
        BundleScanner bundleScanner = new BundleScanner();
        if (root.isDirectory() && root.isDirectory()) {
            bundleScanner.scanDirectory(root);
            Map<String, List<Bundle>> duplicates = bundleScanner.getDuplicateExports();
            printMap(duplicates);
        } else {
            System.out.println(
                    "The given location does not point to a valid directory " + carbonHome);
        }
    }

    private void printMap(Map<String, List<Bundle>> duplicates) {
        System.out.println(
                "*** WARNING ****\n" + "The following packages are exported by duplicate bundles.\n"
                        + "Those may cause class loader errors such as NoClassDefFound.\n"
                        + "Please remove the unwanted bundles");

        printPackagesAggregate(duplicates);
    }

    private void printOnePerPackage(Map<String, List<Bundle>> duplicates) {
        int i = 1;
        for (String key : duplicates.keySet()) {
            System.out.println("["+i+"]\nPackages " + key);
            System.out.println(" Exported by multiple bundles");
            for (Bundle b : duplicates.get(key)) {
                String location = b.getBundleFile().getPath();
                location = location.replace(carbonHome, "");
                System.out.println("    " + b.getBundleName() +" at the location "+location);
            }
            i++;
        }
    }

    private void printPackagesAggregate(Map<String, List<Bundle>> duplicates) {
        int i = 1;
        Collection<PackageBundles> bundles = coalatePackages(duplicates);
        for (PackageBundles packageBundles : bundles) {
            System.out.println("[" + i + "]\nPackages ");
            for (String p : packageBundles.packages) {
                System.out.println("    " + p);
            }
            System.out.println("  Exported By");
            for (Bundle b : packageBundles.bundles) {
                String location = b.getBundleFile().getPath();
                location = location.replace(carbonHome, "");
                System.out.println("    " + b.getBundleName() + " at the location " + location);
            }
        }
    }

    private Collection<PackageBundles> coalatePackages(Map<String, List<Bundle>> duplicates) {

        Map<String, PackageBundles> bundlesMap = new HashMap<>();
        for (String key : duplicates.keySet()) {
            String bundleCoallateKey = "";
            for (Bundle b : duplicates.get(key)) {
                bundleCoallateKey += b.getBundleName();
            }
            PackageBundles packageBundles = bundlesMap.get(bundleCoallateKey);
            if (packageBundles == null) {
                packageBundles = new PackageBundles();
                bundlesMap.put(bundleCoallateKey, packageBundles);
            }
            for (Bundle b : duplicates.get(key)) {
                packageBundles.bundles.add(b);
            }
            packageBundles.packages.add(key);
        }

        return bundlesMap.values();
    }

    private class PackageBundles {

        private Set<String> packages = new HashSet<>();
        private Set<Bundle> bundles = new HashSet<>();
        ;
    }
}
