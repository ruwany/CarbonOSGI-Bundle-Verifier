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
import java.util.HashSet;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class BundleScanner {

    private ExportedPackageHolder exportedPackageHolder = new ExportedPackageHolder();


    public void scanDirectory(File root) {
        File repo = new File(root, "repository");
        File components = new File(repo, "components");
        File plugins = new File(components, "plugins");

        String[] jarNames = plugins.list(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        });

        for (int i=0; i< jarNames.length; i++) {
            scanJar(plugins, jarNames[i]);
        }
    }

    private  void scanJar(File dir, String jarName) {
        System.out.println("Jar "+jarName);
        File jarFile = new File(dir, jarName);
        try {
            java.util.jar.JarFile jar = new java.util.jar.JarFile(jarFile);
            java.util.jar.Manifest manifest = jar.getManifest();

            Bundle bundle = new Bundle();
            for (Object key : manifest.getMainAttributes().keySet())  {
                Attributes.Name name = (Attributes.Name) key;
                if(name.toString().equals("Export-Package")) {
                    scanPackageExports(jar, bundle, manifest, manifest.getMainAttributes().get(key));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void scanPackageExports(JarFile jar, Bundle bundle, Manifest manifest, Object o) {
        System.out.println("Exp Str "+o);
        if(o != null) {
            String packages = String.valueOf(o);
            Set<String> packagesSet = new HashSet<>();
            collectPackages(packagesSet, packages, 0);
            for (String p: packagesSet) {
                System.out.println("Exported "+p);
            }
//            packages = packages.replaceAll("\"(.*)\"", "");
//            System.out.println("Exp2 Str " + packages);
//            String[] packagesArray = packages.split(",");
//            for (String pkgEntry: packagesArray) {
//                String exportedPackageName = getPackageName(pkgEntry);
//                System.out.println("Exported "+exportedPackageName);
//                exportedPackageHolder.addBundle(exportedPackageName, bundle);
//            }
        }
    }

    private String getPackageName(String pkgEntry) {
        if (pkgEntry == null) {
            return null;
        }
        int firstSemicolonIndex = pkgEntry.indexOf(";");
        if(firstSemicolonIndex > 0) {
            return pkgEntry.substring(0, firstSemicolonIndex);
        }

        return pkgEntry;
    }

    private void collectPackages(Set<String> packagesCollected, String scan, int index) {
        if(index >= scan.length()) {
            return;
        }

        char c = scan.charAt(index);
        StringBuilder sb = new StringBuilder();
        while(notDelimiter(c) ) {
            sb.append(c);
            index ++;
            if (index >= scan.length() ) {
                break;
            }
            c = scan.charAt(index);
        }
        if(sb.length() >0) {
            packagesCollected.add(sb.toString());
        }

        //Now skip until we find another comma

        int quotesFound = 0;
        do {
            index++;
            if (index < scan.length()) {
                c = scan.charAt(index);
                if(isQuote(c)) {
                    quotesFound ++;
                }
            }
        } while (notDelimiter(c) && (quotesFound % 2 == 1) && (index < scan.length()));

        if(scan.length() > index) {
            collectPackages(packagesCollected, scan, index);
        }
    }

    private boolean isQuote(char c) {
        switch (c) {
            case '"' :return true;
        }
        return false;
    }

    private boolean notDelimiter(char c) {
        switch (c) {
            case ',':
        case ';':
                return false;
        }
        return true;
    }
}
