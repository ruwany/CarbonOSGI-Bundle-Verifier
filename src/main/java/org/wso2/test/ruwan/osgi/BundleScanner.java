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

import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.BundleException;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Logger;

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
        //System.out.println("Jar "+jarName);
        File jarFile = new File(dir, jarName);
        try {
            java.util.jar.JarFile jar = new java.util.jar.JarFile(jarFile);
            java.util.jar.Manifest manifest = jar.getManifest();

            Bundle bundle = new Bundle(jarName, jarFile, manifest);
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
        //System.out.println("Exp Str "+o);
        if(o != null) {
            String packages = String.valueOf(o);
            try {
                ManifestElement[]  manifestElements = ManifestElement.parseHeader("Export-Package", packages);
                for(ManifestElement me:manifestElements) {
                    //System.out.println(me.getValue());
                    exportedPackageHolder.addBundle(me.getValue(), bundle);
                }
            } catch (BundleException e) {
                e.printStackTrace();
            }
        }
    }

    public Map<String, List<Bundle>> getDuplicateExports() {
        Map<String, List<Bundle>> result = new HashMap<>();

        for(String key: exportedPackageHolder.getPackageToBundleMap().keySet()) {
            Set<Bundle> bundleList = exportedPackageHolder.getPackageToBundleMap().get(key);
            if(bundleList != null && bundleList.size() >1) {
                result.put(key, new ArrayList<Bundle>(bundleList));
            }
        }

        return result;
    }
}
