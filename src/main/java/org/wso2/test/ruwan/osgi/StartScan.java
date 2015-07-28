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
import java.util.List;
import java.util.Map;

public class StartScan {

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

    private static void printMap(Map<String, List<Bundle>> duplicates) {
        for (String key : duplicates.keySet()) {
            System.out.println("Package " + key);
            for (Bundle b : duplicates.get(key)) {
                System.out.println("    " + b.getBundleName());
            }
        }
    }

}
