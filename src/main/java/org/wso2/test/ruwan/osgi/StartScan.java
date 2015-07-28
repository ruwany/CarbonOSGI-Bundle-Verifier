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

import org.apache.commons.cli.*;

import java.io.File;
import java.util.*;

public class StartScan {

    private static final String CMD_OPTION_CARBON_HOME = "d";
    private static final String CMD_OPTION_PACKAGE_VERSIONS = "pv";

    private String carbonHome;

    public static void main(String[] args) {
        StartScan startScan = new StartScan();
        startScan.dosScan(args);
    }

    private void dosScan(String[] args) {
        carbonHome = System.getenv("CARBON_HOME");
        boolean matchPackageVersions  = false;

        CommandLineParser parser = new BasicParser();
        try {
            CommandLine cmd = parser.parse(getOptions(), args);
            if (cmd.hasOption("d")) {
                carbonHome = cmd.getOptionValue(CMD_OPTION_CARBON_HOME);
            }
            matchPackageVersions = cmd.hasOption(CMD_OPTION_PACKAGE_VERSIONS);
        } catch (ParseException e) {
            System.out.println(e.getLocalizedMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "scan", getOptions() );
            return;
        }


        File root = new File(carbonHome);
        BundleScanner bundleScanner = new BundleScanner(matchPackageVersions);
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

    private Options getOptions() {
        Options options = new Options();
        options.addOption(CMD_OPTION_PACKAGE_VERSIONS, false, "Check Package Versions");
        Option directory = OptionBuilder.withArgName("carbon_home")
                .hasArg()
                .withDescription("Carbon Home" )
                .create(CMD_OPTION_CARBON_HOME);
        options.addOption(directory);

        return options;
    }

    private class PackageBundles {

        private Set<String> packages = new HashSet<>();
        private Set<Bundle> bundles = new HashSet<>();
    }
}
