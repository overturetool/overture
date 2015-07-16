package org.overture.tools.maven.astcreator;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Says "Hi" to the user.
 * 
 * @phase generate-sources
 * @requiresDependencyResolution compile
 */
public abstract class AstCreatorBaseMojo extends AbstractMojo
{

//	protected final String PLUGIN_GROUPID = "org.overture.maven.tools";
//	protected final String PLUGIN_ARTIFACTID = "ast-creator-plugin";
//
//	/**
//	 * The prefix of the generated classes.
//	 * 
//	 * @parameter
//	 * @optional
//	 */
//	protected boolean extendedTreeOnly = false;
//
//	/**
//	 * The prefix of the generated classes.
//	 * 
//	 * @parameter
//	 * @required
//	 */
//	protected String vdmResourceFolderName;
//
//	/**
//	 * The prefix of the generated classes.
//	 * 
//	 * @parameter
//	 */
//	protected String extendedAst;
//
//	/**
//	 * The extended tree dependency groupid.
//	 * 
//	 * @parameter
//	 */
//	protected String extendedAstGroupId;
//
//	/**
//	 * The extended tree dependency artifactid.
//	 * 
//	 * @parameter
//	 */
//	protected String extendedAstArtifactId;
//
//	/**
//	 * The prefix of the extended generated classes.
//	 * 
//	 * @parameter
//	 */
//	protected String extendedName;

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
	 */
	protected String specificationDir;

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

//	/**
//	 * Enables generation of vDM source code corresponding to the Java generated tree.
//	 * 
//	 * @parameter
//	 */
//	protected Boolean generateVdm = false;
//
//	/**
//	 * The package of the generated classes.
//	 * 
//	 * @parameter
//	 */
//	protected List<String> deletePackageOnGenerate;

	// /**
	// * My top level of tree.
	// *
	// * @parameter
	// */
	// protected List<String> names;

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

//	protected File getProjectVdmSrcDirectory()
//	{
//		File output = new File(project.getFile().getParentFile(), "src/main/vdm".replace('/', File.separatorChar));
//		return output;
//	}

	protected File getResourcesDir()
	{
		File resources = new File(project.getFile().getParentFile(), "src/main/resources".replace('/', File.separatorChar));
		return resources;
	}

	// protected List<File> getGrammas()
	// {
	// List<File> grammas = new Vector<File>();
	// grammas.add(new File(getResourcesDir(), ast));
	// System.out.println("AST file: " + grammas.get(0).getAbsolutePath());
	// return grammas;
	// }

	public abstract void execute() throws MojoExecutionException,
			MojoFailureException;

}
