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
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class StartScan {

    public static void main(String[] args) {
        String carbonHome = System.getenv("CARBON_HOME");
        if (carbonHome == null ) {
          if(args.length <= 0) {
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
        } else {
            System.out.println(
                    "The given location does not point to a valid directory " + carbonHome);
        }
    }


}
