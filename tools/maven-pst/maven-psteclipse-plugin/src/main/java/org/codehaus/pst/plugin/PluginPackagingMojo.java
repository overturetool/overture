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

import org.apache.maven.plugin.MojoExecutionException;

/**
 * <ul>
 * <li>Title: PluginPackagingMojo</li>
 * <li>Description: The class <code>PluginPackagingMojo</code> is a packaging
 * Mojo for packaging Eclipse source plugins.</li>
 * <li>Created: Aug 29, 2006 by: prisgupt01</li>
 * </ul>
 * @author $Author: prippete01 $
 * @version $Revision: 1.2 $
 * @goal package
 * @requiresProject true
 */
public class PluginPackagingMojo extends AbstractPluginPackagingMojo {
    /**
     * Legal copyright notice.
     */
    public static final String COPYRIGHT = "Copyright (c) 2006, Princeton Softech Inc. All rights reserved.";

    /**
     * SCCS header.
     */
    public static final String HEADER = "$Header: /users1/cvsroot/maven-pst/maven-psteclipse-plugin/src/main/java/com/princetonsoftech/maven/psteclipse/PluginPackagingMojo.java,v 1.2 2007/02/08 22:02:30 prippete01 Exp $";

    /**
     * Directory containing the classes.
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     */
    private File classesDirectory;

    /**
     * Directory containing the generated JAR.
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File outputDirectory;

    /*
     * (non-Javadoc)
     * @see org.codehaus.pst.plugin.AbstractPackagingMojo#getClassesDirectories()
     */
    protected File[] getClassesDirectories() {
        return new File[] { classesDirectory };
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.pst.plugin.AbstractPackagingMojo#getClassifier()
     */
    protected String getClassifier() {
        return "";
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.pst.plugin.AbstractPackagingMojo#getManifest()
     */
    protected File getManifest() throws MojoExecutionException {
        return getPluginManifestFile();
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.pst.plugin.AbstractPackagingMojo#getOutputDirectory()
     */
    protected File getOutputDirectory() {
        return outputDirectory;
    }
}
