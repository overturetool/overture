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

/**
 * <ul>
 * <li>Title: DeployConstants</li>
 * <li>Description: The interface <code>DeployConstants</code> defines a
 * number of useful deployment constants.</li>
 * <li>Created: Aug 3, 2006 by: prippete01</li>
 * </ul>
 * @author $Author: prippete01 $
 * @version $Revision: 1.4 $
 */
public interface DeployConstants {
    /**
     * The 'zip' packaging.
     */
    public static final String PACKAGING_ZIP = "zip";

    /**
     * The 'jar' packaging.
     */
    public static final String PACKAGING_JAR = "jar";

    /**
     * The 'pom' packaging.
     */
    public static final String PACKAGING_POM = "pom";

    /**
     * The 'source-plugin' packaging.
     */
    public static final String PACKAGING_SOURCE_PLUGIN = "source-plugin";

    /**
     * The 'binary-plugin' packaging.
     */
    public static final String PACKAGING_BINARY_PLUGIN = "binary-plugin";

    /**
     * The key for the "user.dir" property.
     */
    public static final String KEY_USER_DIR = "user.dir";

    /**
     * The "plugins" directory.
     */
    public static final String DIR_PLUGINS = "plugins";

    /**
     * The '.zip' extension.
     */
    public static final String EXTENSION_ZIP = ".zip";

    /**
     * The '.jar' extension.
     */
    public static final String EXTENSION_JAR = ".jar";

    /**
     * The '.pom' extension.
     */
    public static final String EXTENSION_POM = ".pom";

}
