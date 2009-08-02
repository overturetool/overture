package org.overture.tools.vdmt;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.overture.tools.vdmt.VDMToolsProxy.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Says "Hi" to the user.
 * 
 * 
 * @phase process-resources
 * @requiresDependencyResolution compile
 */
public class VdmBaseMojo extends AbstractMojo {
	/**
	 * My File.
	 * 
	 * @parameter expression="${user.vppde.bin}"
	 */
	protected File vdmToolsCmd;
	/**
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	protected org.apache.maven.project.MavenProject project;
	
	 /**
     * My excludePackages.
     *
     * @parameter
     */
    protected List excludePackages;
    
    /**
     * My excludePackages.
     *
     * @parameter
     */
    protected List excludeClasses;
    
    /**
     * My excludePackages.
     *
     * @parameter
     */
    protected List importPackages;

	protected ArrayList<File> dependedVppLocations = new ArrayList<File>();

	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			getLog().info("VDM Tools cmd path located at:"
					+ vdmToolsCmd.getAbsolutePath());

		} catch (Exception e) {
			getLog().error("VDM Tools cmd path not located. Check your settings file \"user.vdmtoolscmdpath\" not found in active profile");
		}

		Artifact artifact = project.getArtifact();

		String id = artifact.getArtifactId();
		String group = artifact.getGroupId();

		Iterator ite = project.getDependencyArtifacts().iterator();
		while (ite.hasNext()) {
			Object ooo = ite.next();
			if (ooo instanceof Artifact) {
				Artifact a = (Artifact) ooo;

				String did = a.getArtifactId();
				String dgroup = a.getGroupId();
				File df = a.getFile();
				String fileName = "";
				if (df != null)
					fileName = df.getAbsolutePath();

				// getLog().info("Dependency: Group:" + dgroup + " Id: "+ did +
				// " File: " + fileName);

				if (df != null) {// && dgroup.equals(group)
					File dependedProjectRoot = df.getParentFile().getParentFile();
					File vppLocation = new File(dependedProjectRoot.getAbsolutePath());// +
																						// "/src/main".replace('/',
																						// File.separatorChar)
					if (vppLocation.exists()) {
						// getLog().info( "Vpp dependency found: " +
						// vppLocation.getAbsolutePath());
						dependedVppLocations.add(vppLocation);
					}
					// else
					// getLog().info("No vpp dependency found in: "+
					// vppLocation.getAbsolutePath());
				}
			}
		}

	}

}
