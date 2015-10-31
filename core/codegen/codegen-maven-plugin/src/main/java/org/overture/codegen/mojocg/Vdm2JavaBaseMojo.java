package org.overture.codegen.mojocg;

import java.io.File;
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
	 * @parameter expression="${project.build.directory}/generated-sources/vdmCodeGen"
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
	
	/**
	 * 
	 * 
	 * @parameter
	 */
	protected Properties delegates;

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

	public abstract void execute() throws MojoExecutionException,
			MojoFailureException;

}
