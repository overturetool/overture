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
import java.util.ArrayList;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * <ul>
 * <li>Title: ManifestGeneratorMojo</li>
 * <li>Description: The class <code>ManifestGeneratorMojo</code> is a Mojo
 * that generates an Eclipse plugin manifest, and deploys the dependent
 * artifacts, for a binary plugin.</li>
 * <li>Created: Aug 9, 2006 by: prisgupt01</li>
 * </ul>
 * @author $Author: prippete01 $
 * @version $Revision: 1.10 $
 * @goal eclipse-plugin
 * @phase validate
 * @requiresProject true
 * @requiresDependencyResolution compile
 */
public class ManifestGeneratorMojo extends AbstractEclipseMojo implements ManifestConstants {
    /**
     * Legal copyright notice.
     */
    public static final String COPYRIGHT = "Copyright (c) 2006, Princeton Softech Inc. All rights reserved.";

    /**
     * SCCS header.
     */
    public static final String HEADER = "$Header: /users1/cvsroot/maven-pst/maven-psteclipse-plugin/src/main/java/com/princetonsoftech/maven/psteclipse/ManifestGeneratorMojo.java,v 1.10 2007/02/08 22:02:30 prippete01 Exp $";

    /**
     * The base directory.
     * @parameter expression="${basedir}"
     * @required
     */
    private File baseDirectory;

    /**
     * The Maven project.
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * The Eclipse buddies.
     * @parameter expression="${buddies}"
     * @optional
     */
    private ArrayList buddies;
    


    /**
     * Constructs a new <code>ManifestGeneratorMojo</code> instance.
     */
    public ManifestGeneratorMojo() {
        super();
    }

    /**
     * Returns the base directory.
     * @return the base directory.
     */
    public File getBaseDirectory() {
        return baseDirectory;
    }

    /**
     * Sets the base directory.
     * @param baseDirectory the base directory.
     */
    public void setBaseDirectory(File baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    /**
     * Returns the mavenProject.
     * @return the mavenProject.
     */
    public MavenProject getMavenProject() {
        return project;
    }

    /**
     * Sets the project.
     * @param project the project.
     */
    public void setMavenProject(MavenProject project) {
        this.project = project;
    }

    /**
     * Returns the buddies.
     * @return the buddies.
     */
    public ArrayList getBuddies() {
        return buddies;
    }

    /**
     * Sets the buddies.
     * @param buddies the buddies.
     */
    public void setBuddies(ArrayList buddies) {
        this.buddies = buddies;
    }

    /* 
     * (non-Javadoc)
     * @see org.codehaus.pst.plugin.AbstractEclipseMojo#doExecute()
     */
    protected void doExecute() throws MojoExecutionException, MojoFailureException {
      
//    	if(doNotExportPackagePrefixes!=null)
//    	{
//    		getLog().info("Exclude packages");
//    		java.util.Iterator itr = doNotExportPackagePrefixes.iterator();
//    		while(itr.hasNext())
//    		{
//    		Object s = itr.next();
//    		getLog().info(s.toString());
//    		}
//    	}
    	
    	ManifestGenerator generator = new ManifestGenerator(getLog(), baseDirectory, project, buddies, baseDirectory,doNotExportPackagePrefixes);
        generator.execute();
    }
}