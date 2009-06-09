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

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.plugin.logging.Log;

/**
 * <ul>
 * <li>Title: AbstractEclipseMojoHelper</li>
 * <li>Description: The class <code>AbstractEclipseMojoHelper</code> is an
 * abstract Mojo helper base class for dealing with Eclipse.</li>
 * <li>Created: Sep 1, 2006 by: prippete01</li>
 * </ul>
 * @author $Author: prippete01 $
 * @version $Revision: 1.2 $
 */
public abstract class AbstractEclipseMojoHelper extends AbstractMojoHelper implements EclipseConstants {
    /**
     * Legal copyright notice.
     */
    public static final String COPYRIGHT = "Copyright (c) 2006, Princeton Softech Inc. All rights reserved.";

    /**
     * SCCS header.
     */
    public static final String HEADER = "$Header: /users1/cvsroot/maven-pst/maven-psteclipse-plugin/src/main/java/com/princetonsoftech/maven/psteclipse/AbstractEclipseMojoHelper.java,v 1.2 2007/02/08 22:02:30 prippete01 Exp $";

    /**
     * The Eclipse directory.
     */
    private File eclipseDirectory;

    /**
     * The prefixes.
     */
    private List prefixes;

    /**
     * The Eclipse plugins directory.
     */
    private File pluginsDirectory;

    /**
     * The Eclipse workspace directory.
     */
    private File workspaceDirectory;

    /**
     * The Eclipse startup jar file.
     */
    /**
	 * My File.
	 * 
	 * 
	 */
    private File startupJarFile;

    /**
     * Constructs a new <code>AbstractEclipseMojoHelper</code> instance.
     * @param log the Mojo's log.
     * @param baseDirectory the base directory.
     * @param eclipseDirectory the Eclipse home directory.
     * @param prefixes the plugin artifact id prefixes.
     */
    public AbstractEclipseMojoHelper(Log log, File baseDirectory, File eclipseDirectory, List prefixes) {
        super(log, baseDirectory);
        this.eclipseDirectory = eclipseDirectory;
        this.prefixes = prefixes;
        pluginsDirectory = new File(eclipseDirectory, PLUGINS_DIRECTORY);
        workspaceDirectory = new File(eclipseDirectory, WORKSPACE_DIRECTORY);
     //   startupJarFile = new File(eclipseDirectory.getAbsolutePath()+ File.separatorChar + PLUGINS_DIRECTORY + File.separatorChar + STARTUP_JAR);
        getLog().debug("Eclipse startup...:"+PluginPackagingMojo.startupJarFileLocation);
        startupJarFile = new File(eclipseDirectory.getAbsolutePath()+ File.separatorChar + PluginPackagingMojo.startupJarFileLocation);
    }

    /**
     * Returns the Eclipse directory.
     * @return the Eclipse directory.
     */
    protected File getEclipseDirectory() {
        return eclipseDirectory;
    }

    /**
     * Returns the prefixes.
     * @return the prefixes.
     */
    protected List getPrefixes() {
        return prefixes;
    }

    /**
     * Returns the Eclipse plugins directory.
     * @return the Eclipse plugins directory.
     */
    protected File getPluginsDirectory() {
        return pluginsDirectory;
    }

    /**
     * Returns the Eclipse workspace directory.
     * @return the Eclipse workspace directory.
     */
    protected File getWorkspaceDirectory() {
        return workspaceDirectory;
    }

    /**
     * Returns the startup jar file.
     * @return the startup jar file.
     */
    protected File getStartupJarFile() {
        return startupJarFile;
    }

    /**
     * Returns a boolean flag indicating whether or not the specified artifact
     * id is a match for any of the prefixes.
     * @param artifactId the artifact id to check.
     * @return <code>true</code> if a match is found; <code>false</code>
     * otherwise.
     */
    protected boolean isMatchForPrefix(String artifactId) {
        Iterator iterator = prefixes.iterator();
        while (iterator.hasNext()) {
            String prefix = (String) iterator.next();
            if (artifactId.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
