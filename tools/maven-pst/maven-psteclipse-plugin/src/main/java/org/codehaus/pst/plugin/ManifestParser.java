/*
 * Copyright (C) 2006 Princeton Softech, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.pst.plugin;

import java.util.LinkedList;
import java.util.List;
import java.util.jar.Manifest;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;

/**
 * <ul>
 * <li>Title: ManifestParser</li>
 * <li>Description: The class <code>ManifestParser</code> is a helper class
 * for parsing values in an Eclipse plug-in's manifest.</li>
 * <li>Created: Aug 2, 2006 by: prippete01</li>
 * </ul>
 * @author $Author: prippete01 $
 * @version $Revision: 1.7 $
 */
public class ManifestParser implements ManifestConstants {
    /**
     * Legal copyright notice.
     */
    public static final String COPYRIGHT = "Copyright (c) 2006, Princeton Softech Inc. All rights reserved.";

    /**
     * SCCS header.
     */
    public static final String HEADER = "$Header: /users1/cvsroot/maven-pst/maven-psteclipse-plugin/src/main/java/com/princetonsoftech/maven/psteclipse/ManifestParser.java,v 1.7 2007/02/08 22:02:30 prippete01 Exp $";

    /**
     * The group id.
     */
    private String groupId = GROUP_ID_PST_ECLIPSE;
    
    /**
     * The artifact id.
     */
    private String artifactId;

    /**
     * The version.
     */
    private String version;

    /**
     * The dependency management.
     */
    private DependencyManagement management = new DependencyManagement();

    /**
     * The manifest.
     */
    private Manifest manifest;

    /**
     * Constructs a new <code>ManifestParser</code> instance.
     * @param manifest
     */
    public ManifestParser(Manifest manifest) {
        super();
        this.manifest = manifest;
    }

    /**
     * Returns the group id.
     * @return the group id.
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * Sets the group id.
     * @param groupId the group id.
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * Returns the artifact id.
     * @return the artifact id if found; <code>null</code> otherwise.
     */
    public String getArtifactId() {
        return artifactId;
    }

    /**
     * Returns a boolean flag indicating whether or not the parser has an
     * artifact id.
     * @return <code>true</code> if the parser has an artifact id;
     * <code>false</code> otherwise.
     */
    public boolean hasArtifactId() {
        return (artifactId != null);
    }

    /**
     * Returns the version.
     * @return the version if found; <code>null</code> otherwise.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Returns a boolean flag indicating whether or not the parser has a
     * version.
     * @return <code>true</code> if the parser has a version;
     * <code>false</code> otherwise.
     */
    public boolean hasVersion() {
        return (version != null);
    }

    /**
     * Returns the dependency management.
     * @return the dependency management.
     */
    public DependencyManagement getDependencyManagement() {
        return management;
    }

    /**
     * Parses the manifest.
     */
    public void parse() {
        parseArtifactId();
        parseVersion();
        parseRequiredBundle();
        parseBundleClasspath();
    }

    /**
     * Parses the bundle symbolic name ('Bundle-SymbolicName').
     */
    private void parseArtifactId() {
        artifactId = manifest.getMainAttributes().getValue(BUNDLE_SYMBOLIC_NAME);
        if (artifactId == null) {
            return;
        }
        int index = artifactId.indexOf(';');
        if (index > 0) {
            artifactId = artifactId.substring(0, index);
        }
    }

    /**
     * Parses the bundle version ('Bundle-Version').
     */
    private void parseVersion() {
        version = manifest.getMainAttributes().getValue(BUNDLE_VERSION);
        if (version == null) {
            return;
        }
        String[] elements = version.split("\\.");
        if (elements.length > 3) {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < 3; i++) {
                if (i > 0) {
                    buffer.append('.');
                }
                buffer.append(elements[i]);
            }
            version = buffer.toString();
        }
    }

    /**
     * Parses the required bundles ('Require-Bundle').
     */
    private void parseRequiredBundle() {
        String requiredBundlesString = manifest.getMainAttributes().getValue(REQUIRE_BUNDLE);
        if (requiredBundlesString == null) {
            return;
        }
        String[] requiredBundles = splitString(requiredBundlesString);
        for (int i = 0; i < requiredBundles.length; i++) {
            String[] requiredBundleAttributes = requiredBundles[i].split(";");
            String symbolicName = requiredBundleAttributes[0].trim();
            String visibility = null;
            String resolution = null;
            String bundleVersion = null;
            if (requiredBundleAttributes.length > 1) {
                for (int j = 1; j < requiredBundleAttributes.length; j++) {
                    String attr = requiredBundleAttributes[j];
                    if (attr.indexOf(":=") > 0) {
                        String key = attr.substring(0, attr.indexOf(":=")).trim();
                        String value = attr.substring(attr.indexOf(":=") + 2).trim();
                        if (key.equalsIgnoreCase(KEY_VISIBILITY)) {
                            visibility = value;
                        } else if (key.equalsIgnoreCase(KEY_RESOLUTION)) {
                            resolution = value;
                        }
                    } else if (attr.indexOf("=") > 0) {
                        String key = attr.substring(0, attr.indexOf("=")).trim();
                        String value = attr.substring(attr.indexOf("=") + 1).trim();
                        if (key.equalsIgnoreCase(KEY_BUNDLE_VERSION)) {
                            if (value.startsWith("\"")) {
                                value = value.substring(1);
                            }
                            if (value.endsWith("\"")) {
                                value = value.substring(0, value.length() - 1);
                            }
                            bundleVersion = value;
                        }
                    }
                }
            }
            addDependency(symbolicName, bundleVersion, null, null);
        }
    }

    /**
     * Parses the bundled classpath ('Bundle-Classpath')
     */
    private void parseBundleClasspath() {
        String classpath = manifest.getMainAttributes().getValue(BUNDLE_CLASSPATH);
        if (classpath == null) {
            return;
        }
        String[] bundledClasspath = classpath.split(",");
        for (int i = 0; i < bundledClasspath.length; i++) {
            String classpathEntry = bundledClasspath[i];
            if (classpathEntry.equals(".")) {
                continue;
            }
            int beginIndex = classpathEntry.lastIndexOf('/') + 1;
            int endIndex = classpathEntry.lastIndexOf('.');
            if (endIndex == -1) {
                endIndex = classpathEntry.length();
            }
            String artifactId = this.artifactId + "." + classpathEntry.substring(beginIndex, endIndex);
            addDependency(artifactId, null, SCOPE_SYSTEM, classpathEntry);
        }
    }

    /**
     * Add a dependency to the DependencyManagement
     * @param artifactId the artifact id.
     * @param version the version.
     * @param scope the scope.
     * @param systemPath the system path.
     */
    private void addDependency(String artifactId, String version, String scope, String systemPath) {
        Dependency dependency = new Dependency();
        dependency.setArtifactId(artifactId);
        dependency.setGroupId(groupId);
        if (version != null && version.length() > 0) {
            if (version.startsWith("[")) {
                version = version.substring(1, version.length() - 1);
                String[] versions = version.split(",");
                version = versions[0];
            }
        }
        dependency.setVersion(version);
        dependency.setScope(scope);
        dependency.setSystemPath(systemPath);
        if (!SCOPE_SYSTEM.equals(scope)) {
            dependency.setType("pom");
        }
        management.addDependency(dependency);
    }

    /**
     * Split the given String with a ',' as a delimiter. A version range string
     * is ignored.
     * @param string the string to split.
     * @return the split string.
     */
    private String[] splitString(String string) {
        char[] chars = string.toCharArray();

        List result = new LinkedList();
        StringBuffer buffer = new StringBuffer();
        boolean isInVersionSpec = false;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];

            if ((c == '[' || c == '(') && !isInVersionSpec) {
                isInVersionSpec = true;
                buffer.append(c);
            } else if ((c == ']' || c == ')') && isInVersionSpec) {
                isInVersionSpec = false;
                buffer.append(c);
            } else if (c == ',' && !isInVersionSpec) {
                result.add(buffer.toString());
                buffer.setLength(0);
            } else {
                buffer.append(c);
            }
        }
        result.add(buffer.toString());
        return (String[]) result.toArray(new String[0]);
    }
}
