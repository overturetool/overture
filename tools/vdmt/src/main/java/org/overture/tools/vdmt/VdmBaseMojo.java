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

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Says "Hi" to the user.
 * 
 * @aggregator
 * @requiresProject
 * @requiresDependencyResolution test scopes
 */
public abstract class VdmBaseMojo extends AbstractMojo {
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
	protected List<String> excludePackages;

	/**
	 * My excludePackages.
	 * 
	 * @parameter
	 */
	protected List<String> excludeClasses;

	/**
	 * My excludePackages.
	 * 
	 * @parameter
	 */
	protected List<String> importPackages;

	/**
	 * default-value="${project.reporting.outputDirectory}"
	 * 
	 * @parameter
	 */
	private File projectOutputDirectory;

	protected String getProjectOutputDirectory() {
		if (projectOutputDirectory == null
				|| projectOutputDirectory.length() == 0) {
			File output = new File(project.getFile().getParentFile(), "target");
			if (!output.exists())
				output.mkdirs();

			return output.getAbsolutePath();

		} else
			return projectOutputDirectory.getAbsolutePath();
	}

	protected ArrayList<File> dependedVppLocations = new ArrayList<File>();

	@SuppressWarnings("unchecked")
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			getLog().info(
					"VDM Tools cmd path located at:"
							+ vdmToolsCmd.getAbsolutePath());

		} catch (Exception e) {
			getLog()
					.error(
							"VDM Tools cmd path not located. Check your settings file \"user.vdmtoolscmdpath\" not found in active profile");
		}

		// Artifact artifact = project.getArtifact();

		// String id = artifact.getArtifactId();
		// String group = artifact.getGroupId();
		getLog().info("Depended artifacts: ");
		Iterator ite = project.getDependencyArtifacts().iterator();
		while (ite.hasNext()) {
			Object ooo = ite.next();
			getLog().info(ooo.toString());
			if (ooo instanceof Artifact) {
				Artifact a = (Artifact) ooo;

				// String did = a.getArtifactId();
				// String dgroup = a.getGroupId();
				File df = a.getFile();
				// String fileName = "";
				// if (df != null)
				// fileName = df.getAbsolutePath();

				// getLog().info("Dependency: Group:" + dgroup + " Id: "+ did +
				// " File: " + fileName);

				if (!addProjectBaseToList(df, true)
						&& a.getScope().equals("compile")) {// &&
															// dgroup.equals(group)

					// else
					// getLog().info("No vpp dependency found in: "+
					// vppLocation.getAbsolutePath());

					// we have a version of the depended artifact form the
					// repository. Try to guess the source location
					File guessLocation = new File(project.getBasedir()
							.getParentFile(), a.getArtifactId());
					if (!addProjectBaseToList(guessLocation, false)) {
						String artifact = a.getGroupId() + ":"
								+ a.getArtifactId() + " " + a.getVersion();
						getLog().warn(
								"Could not resolve source location for: "
										+ artifact);
//						throw new MojoFailureException(
//								"Unable to resolve source location for: "
//										+ artifact);
					}
				}
			}
		}

	}

	private boolean addProjectBaseToList(File df, boolean isPomFile) {
		if (df == null || !df.exists())
			return false;
		File dependedProjectRoot = null;
		if (!isPomFile)
			dependedProjectRoot = df; // df.getParentFile().getParentFile();
		else
			dependedProjectRoot = df.getParentFile();
		// File vppLocation = new File(dependedProjectRoot.getAbsolutePath());//
		// +
		// "/src/main".replace('/',
		// File.separatorChar)
		if (dependedProjectRoot.exists()) {
			// getLog().info( "Vpp dependency found: " +
			// vppLocation.getAbsolutePath());
			dependedVppLocations.add(dependedProjectRoot);
		}
		return true;
	}

}
