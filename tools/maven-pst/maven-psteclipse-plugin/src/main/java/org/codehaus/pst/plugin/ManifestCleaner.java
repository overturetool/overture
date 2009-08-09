package org.codehaus.pst.plugin;

import java.io.File;
import java.util.ArrayList;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

public class ManifestCleaner extends AbstractMojoHelper
{
	File destinationDirectory;
	Log log;
	MavenProject project;

	public ManifestCleaner(Log log, File baseDirectory, MavenProject project,
			ArrayList buddies, File destinationDirectory)
	{
		super(log, baseDirectory);
		this.destinationDirectory = destinationDirectory;
		this.log = log;
		this.project = project;

		// TODO Auto-generated constructor stub
	}

	protected Log getLog()
	{
		return log;
	}

	protected void doExecute() throws MojoExecutionException,
			MojoFailureException
	{
		getLog().info("Manifest clean");
		if (project.getPackaging().equals(
				EclipseConstants.PACKING_BINARY_PLUGIN)) // &&project.getPackaging().equals(
		// EclipseConstants.PACKING_SOURCE_PLUGIN)

		// try
		{
			getLog().info("Cleaning project: ");
			getLog().info("		" + project.getArtifactId());
			getLog().info("		" + project.getName());
			getLog().info("0");
			File manifestDirectory = new File(destinationDirectory,
					ManifestGenerator.MANIFEST_DIRECTORY);

			getLog().info("1");
			File manifestFile = new File(manifestDirectory,
					ManifestGenerator.MANIFEST_FILE_NAME);
			if (manifestFile != null && manifestFile.exists())
			{
				getLog().debug(
						"Deleting manifest: " + manifestFile.getAbsolutePath());
				manifestFile.delete();
			}
			getLog().info("2");
			if (manifestDirectory != null && manifestDirectory.exists())
			{
				getLog().debug("The manifestDir is " + manifestDirectory);
				getLog().debug(
						"Deleting manifest folder: "
								+ manifestDirectory.getAbsolutePath());
				manifestDirectory.delete();
			}

			File libDirectory = new File(destinationDirectory,
					ManifestGenerator.LIB_DIRECTORY);
			getLog().info("3");
			getLog().info("LIB:" + libDirectory.getAbsolutePath());
			if (libDirectory != null && libDirectory.exists())
			{
				int h = 0;
				while (h < 100 && libDirectory.list() != null)
				// if(libDirectory.listFiles() != null)
				{
					for (int i = 0; i < libDirectory.listFiles().length; i++)
					{
						try
						{
							getLog().info(
									libDirectory.listFiles()[i].getAbsolutePath());
							libDirectory.listFiles()[i].delete();
						} catch (Exception e)
						{
							e.printStackTrace();
						}
					}
					h++;
				}
				libDirectory.delete();
			}

			File pluginPropetiesFile = new File(destinationDirectory,
					EclipseConstants.PLUGIN_PROPERTIES);
			if (pluginPropetiesFile != null && pluginPropetiesFile.exists())
				pluginPropetiesFile.delete();

			File buildPropetiesFile = new File(destinationDirectory,
					EclipseConstants.BUILD_PROPERTIES);

			if (buildPropetiesFile != null && buildPropetiesFile.exists())
				buildPropetiesFile.delete();
		} else
			getLog().info(
					"Clean of " + project.getPackaging() + " not supported");
		// catch(Exception e)
		// {
		// e.printStackTrace();
		// }
	}
}
