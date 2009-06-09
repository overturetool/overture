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
 * <li>Title: PDETestEclipseCleaner</li>
 * <li>Description: The class <code>PDETestEclipseCleaner</code> is a Mojo
 * helper that cleans out a target Eclipse environment, removing all
 * non-Eclipse, non-3rd party plugins in preparation for a test run.</li>
 * <li>Created: Aug 29, 2006 by: prippete01</li>
 * </ul>
 * @author $Author: prippete01 $
 * @version $Revision: 1.4 $
 */
public class PDETestEclipseCleaner extends AbstractEclipseMojoHelper {
    /**
     * Legal copyright notice.
     */
    public static final String COPYRIGHT = "Copyright (c) 2006, Princeton Softech Inc. All rights reserved.";

    /**
     * SCCS header.
     */
    public static final String HEADER = "$Header: /users1/cvsroot/maven-pst/maven-psteclipse-plugin/src/main/java/com/princetonsoftech/maven/psteclipse/PDETestEclipseCleaner.java,v 1.4 2007/02/08 22:02:30 prippete01 Exp $";

    /**
     * Constructs a new <code>PDETestEclipseCleaner</code> instance.
     * @param log the Mojo's log.
     * @param baseDirectory the base directory.
     * @param eclipseDirectory the Eclipse directory.
     * @param prefixes the plugin artifact id prefixes.
     */
    public PDETestEclipseCleaner(Log log, File baseDirectory, File eclipseDirectory, List prefixes) {
        super(log, baseDirectory, eclipseDirectory, prefixes);
    }

    /* 
     * (non-Javadoc)
     * @see org.codehaus.pst.plugin.AbstractMojoHelper#doExecute()
     */
    protected void doExecute() throws MojoExecutionException, MojoFailureException {
        File eclipseDirectory = getEclipseDirectory();
        getLog().info("Cleaning target Eclipse environment in '" + eclipseDirectory + "'...");
        File pluginsDirectory = getPluginsDirectory();
        getLog().debug("Looking for plugins in '" + pluginsDirectory + "'...");
        File[] files = pluginsDirectory.listFiles();
        for (int i = 0; i < files.length; i++) {
            String name = files[i].getName();
            if (!isMatchForPrefix(name)) {
                getLog().debug("Skipping plugin '" + name + "'.");
                continue;
            }
            getLog().info("Cleaning plug-in '" + name + "'...");
            if (files[i].isDirectory()) {
                deleteDirectory(files[i]);
            } else {
                deleteFile(files[i]);
            }
        }
    }

    /**
     * Deletes the specified directory after deleting its contents.
     * @param directory the directory to delete.
     * @throws MojoExecutionException
     */
    private void deleteDirectory(File directory) throws MojoExecutionException {
        getLog().debug("Deleting directory '" + directory + "'...");
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                deleteDirectory(files[i]);
            } else {
                deleteFile(files[i]);
            }
        }
        if (!directory.delete()) {
            throw new MojoExecutionException("Unable to delete directory '" + directory + "'");
        }
    }

    /**
     * Deletes the specified file.
     * @param file the file.
     * @throws MojoExecutionException
     */
    private void deleteFile(File file) throws MojoExecutionException {
        getLog().debug("Deleting file '" + file + "'...");
        if (!file.delete()) {
            throw new MojoExecutionException("Unable to delete file '" + file + "'");
        }
    }
}
