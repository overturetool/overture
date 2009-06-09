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
 * <li>Title: EclipseConstants</li>
 * <li>Description: The interface <code>EclipseConstants</code> is an
 * interface that provides suitable constants for the Mojos and helpers involved
 * with Eclipse.</li>
 * <li>Created: Sep 1, 2006 by: prippete01</li>
 * </ul>
 * @author $Author: prippete01 $
 * @version $Revision: 1.2 $
 */
public interface EclipseConstants {
    /**
     * The Eclipse 'eclipse' directory.
     */
    public static final String ECLIPSE_DIRECTORY = "eclipse";

    /**
     * The Eclipse 'plugins' directory.
     */
    public static final String PLUGINS_DIRECTORY = "plugins";

    /**
     * The Eclipse 'workspace' directory.
     */
    public static final String WORKSPACE_DIRECTORY = "workspace";

    /**
     * The Eclipse 'startup.jar'.
     */
    //public static final String STARTUP_JAR = "org.eclipse.equinox.common_3.4.0.v20080421-2006.jar";//"startup.jar";
    
    /**
     * 
     */
    public static final String ORG_ECLIPSE_SWT_FILE_PREFIX="org.eclipse.swt";
    
    
    public static final String PACKING_SOURCE_PLUGIN="source-plugin";
    public static final String PACKING_BINARY_PLUGIN="binary-plugin";
}
