package org.overture.codegen.mojocg;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * The VDM-to-Java code generator base mojo.
 * 
 * @phase generate-sources
 * @requiresDependencyResolution compile
 */
public abstract class Vdm2JavaBaseMojo extends AbstractMojo
{
	/**
	 * The package of the generated classes.
	 *
	 * @parameter
	 * @required
	 */
	protected String packageName;

	/**
	 * The name of the folder under resources where the specification is stored.
	 * 
	 * @parameter
	 * @required
	 */
	protected File specificationDir;

	/**
	 * Name of the directory into which the astCreatorPlugin should dump the ast files.
	 * 
	 * @parameter property="outputDirectory" default-value="${project.build.directory}/generated-sources/javacode"
	 */
	protected File outputDirectory;

	/**
	 * Dialect to use. Can be either 'sl' or 'pp'
	 * 
	 * @parameter
	 * @required
	 */
	protected String dialect;

	/**
	 * VDM version to use to use. Can be either 'vdm10' or 'classic'
	 * 
	 * @parameter
	 * @required
	 */
	protected String release;

	/**
	 * A flag to configure formatting of the generated code
	 * 
	 * @parameter
	 */
	protected boolean formatCode = true;

	/**
	 * Generate VDM location information for code generated constructs
	 * 
	 * @parameter
	 */
	protected boolean printVdmLocations = false;

	/**
	 * Print detailed information about the code generation process
	 * 
	 * @parameter
	 */
	protected boolean verbose = true;

	/**
	 * @parameter property="project" default-value="${project}"
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

	/**
	 * @parameter
	 */
	protected Properties delegates;
	
	/**
	 * Modules (or classes) that should not be code generated
	 * 
	 * @parameter
	 */
	protected List<String> modulesToSkip;

	/**
	 * @parameter
	 */
	protected boolean genJUnit4Tests = false;

	/**
	 * @parameter
	 */
	protected boolean separateTestCode = false;
	
	/**
	 * VDMPP and VDMRT exclusive feature: Code generate the concurrency constructs
	 * 
	 * @parameter
	 */
	protected boolean genConcurrency = false;
	
	/**
	 * Code generate the VDM-RT system class
	 * 
	 * @parameter
	 */
	protected boolean genSystemClass = false;

	protected File getProjectOutputDirectory()
	{
		if (projectOutputDirectory == null
				|| projectOutputDirectory.length() == 0)
		{
			File output = new File(project.getFile().getParentFile(), "target");
			if (!output.exists())
			{
				output.mkdirs();
			}

			return output;

		} else
		{
			return projectOutputDirectory;
		}
	}

	protected File getProjectJavaSrcDirectory()
	{
		File output = new File(project.getFile().getParentFile(), "src/main/java".replace('/', File.separatorChar));
		return output;
	}

	public abstract void execute()
			throws MojoExecutionException, MojoFailureException;

}
