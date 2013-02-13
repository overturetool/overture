package org.overturetool.tools.astg;

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
import java.util.List;
import java.util.Vector;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Says "Hi" to the user.
 * 
 * 
 * @phase process-resources
 * @requiresDependencyResolution compile
 */
public abstract class AstGenBaseMojo extends AbstractMojo
{
	/**
	 * The prefix of the generated classes.
	 * 
	 * @parameter
	 * @required
	 */
	protected String ast;

	// /**
	// * The package of the generated classes.
	// *
	// * @parameter
	// */
	// protected String packageName;

	/**
	 * My top level of tree.
	 * 
	 * @parameter
	 */
	protected List<String> names;

	/**
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	protected org.apache.maven.project.MavenProject project;

	/**
	 * default-value="${project.reporting.outputDirectory}"
	 * 
	 * @parameter
	 */
	private File projectOutputDirectory;

	protected File getProjectOutputDirectory()
	{
		if (projectOutputDirectory == null
				|| projectOutputDirectory.length() == 0)
		{
			File output = new File(project.getFile().getParentFile(), "target");
			if (!output.exists())
				output.mkdirs();

			return output;

		} else
			return projectOutputDirectory;
	}
	
	protected File getProjectJavaSrcDirectory()
	{
		File output = new File(project.getFile().getParentFile(), "src/main/java".replace('/', File.separatorChar));
		return output;
	}
	protected File getProjectVdmSrcDirectory()
	{
		File output = new File(project.getFile().getParentFile(), "src/main/vpp".replace('/', File.separatorChar));
		return output;
	}

	protected File getResourcesDir()
	{
		File resources = new File(project.getFile().getParentFile(),
				"src/main/resources".replace('/', File.separatorChar));
		return resources;
	}

	protected List<File> getGrammas()
	{
		List<File> grammas = new Vector<File>();
		grammas.add(new File(getResourcesDir(), ast));
		System.out.println("AST file: " + grammas.get(0).getAbsolutePath());
		return grammas;
	}

	public abstract void execute() throws MojoExecutionException,
			MojoFailureException;

}
