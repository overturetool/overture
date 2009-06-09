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
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

/**
 * <ul>
 * <li>Title: PDETestEclipseValidator</li>
 * <li>Description: The class <code>PDETestEclipseValidator</code> is a Mojo
 * helper that validates a target Eclipse installation.</li>
 * <li>Created: Aug 29, 2006 by: prippete01</li>
 * </ul>
 * @author $Author: prippete01 $
 * @version $Revision: 1.6 $
 */
public class PDETestEclipseValidator extends AbstractEclipseMojoHelper {
    /**
     * Legal copyright notice.
     */
    public static final String COPYRIGHT = "Copyright (c) 2006, Princeton Softech Inc. All rights reserved.";

    /**
     * SCCS header.
     */
    public static final String HEADER = "$Header: /users1/cvsroot/maven-pst/maven-psteclipse-plugin/src/main/java/com/princetonsoftech/maven/psteclipse/PDETestEclipseValidator.java,v 1.6 2007/02/08 22:02:30 prippete01 Exp $";

    /**
     * The Eclipse PDE test framework version.
     */
    private String testFrameworkVersion;
    
    /**
     * Constructs a new <code>PDETestEclipseValidator</code> instance.
     * @param log the Mojo's log.
     * @param baseDirectory the base directory.
     * @param eclipseDirectory the Eclipse directory.
     * @param prefixes the plugin prefixes.
     * @param testFrameworkVersion the test framework version.
     */
    public PDETestEclipseValidator(Log log, File baseDirectory, File eclipseDirectory, List prefixes, String testFrameworkVersion) {
        super(log, baseDirectory, eclipseDirectory, prefixes);
        this.testFrameworkVersion = testFrameworkVersion;
    }

    /* 
     * (non-Javadoc)
     * @see org.codehaus.pst.plugin.AbstractMojoHelper#doExecute()
     */
    protected void doExecute() throws MojoExecutionException, MojoFailureException {
        File eclipseDirectory = getEclipseDirectory();
        getLog().info("Validating target Eclipse environment in '" + eclipseDirectory + "'...");
        if (!eclipseDirectory.isDirectory()) {
            throw new MojoExecutionException("The Eclipse directory location '" + eclipseDirectory + "' is not a valid directory");
        }
        File startupJarFile = getStartupJarFile();
        getLog().debug("Eclipse startup jar must be in '" + startupJarFile + ".");
        if (!startupJarFile.exists()) {
            getLog().debug("The file '" + startupJarFile + "' does not exist.");
            throw new MojoExecutionException("The required startup jar file was not found in '" + eclipseDirectory + "'");
        }
        File pluginsDirectory = getPluginsDirectory();
        getLog().debug("Eclipse plugins directory must be in '" + pluginsDirectory + ".");
        if (!pluginsDirectory.isDirectory()) {
            getLog().debug("The location '" + pluginsDirectory + "' does not exist or is not a directory.");
            throw new MojoExecutionException("The required plugins directory was not found in '" + eclipseDirectory + "'");
        }
        getLog().debug("The Eclipse PDE test framework version is '" + testFrameworkVersion + "'.");
        File testPluginDirectory = (pluginsDirectory);//, "org.eclipse.tptp.test_4.2.200.v200901090100_" + testFrameworkVersion);
        getLog().debug("The Eclipse PDE test framework plugin must be in '" + testPluginDirectory + "'.");
        if (!testPluginDirectory.isDirectory()) {
            getLog().debug("The Eclipse PDE test framework plugin does not exist.");
            throw new MojoExecutionException("The required Eclipse test framework plugin 'org.eclipse.test_" + testFrameworkVersion + "' was not found in '" + pluginsDirectory + "'");
        }
    }
}









