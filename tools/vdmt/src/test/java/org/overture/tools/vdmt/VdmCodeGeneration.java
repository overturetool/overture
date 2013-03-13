package org.overture.tools.vdmt;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import junit.framework.TestCase;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.overture.tools.vdmt.VDMToolsProxy.VdmProject;
import org.overture.maven.MavenSettings;

public abstract class VdmCodeGeneration extends TestCase
{
protected	MavenSettings settings = null;
protected	File vppde = null;
protected	File targetFolder = null;
protected	File resourcesFolder = null;
	

	@Override
	protected void setUp() throws Exception
	{
		settings = new MavenSettings();
		vppde = new File(settings.getDefaultProfile().getProperty(
				org.overture.maven.Profile.USER_VDMTOOLS_CMD_PATH));
		targetFolder = getLocation();
		resourcesFolder = new File(targetFolder.getParentFile(),
				"src/test/resources".replace('/', File.separatorChar));
		super.setUp();
	}

	

	private File getStdLibJarFile()
	{
		
		
		for (File f : settings.getMavenRepo().listFiles())
		{
			if(f.isFile()&&f.getName().toLowerCase().startsWith("stdlib"))
				return f;
			else if (f.isDirectory())
			{
			File stdlib = getStdLibJarFile(f);
			if(stdlib!=null)
				return stdlib;
				
			}
		}
		assertFalse("StdLib not found",true);
		return null;
	}
	
	private File getStdLibJarFile(File dir)
	{
		
		
		for (File f : dir.listFiles())
		{
			if(f.isFile()&&f.getName().toLowerCase().startsWith("stdlib"))
				return f;
			else if (f.isDirectory())
			{
			File stdlib = getStdLibJarFile(f);
			if(stdlib!=null)
				return stdlib;
				
			}
		}
		
		return null;
	}

	protected File createTestProject(String projectName) throws IOException
	{
		File file = new File(targetFolder, projectName);
		if(file.exists())
			deleteTmpData(file);
	
			file.mkdirs();
		File testProject = new File(resourcesFolder, projectName);

		CopyDirectory cd = new CopyDirectory();
		cd.copyDirectory(testProject, file);

		return file;
	}
	
	private void deleteTmpData(File outputFolder)
	{
		for (File f : outputFolder.listFiles())
		{
			if (f.isFile())
				f.delete();
		}

		while (outputFolder.listFiles().length > 0)
			for (File f : outputFolder.listFiles())
			{
				deleteTmpData(f);
			}

		outputFolder.delete();

	}

	private File getLocation()
	{
		URL location = null;
		final String classLocation = VdmCodeGeneration.class.getName().replace(
				'.',
				'/')
				+ ".class";
		final ClassLoader loader = VdmCodeGeneration.class.getClassLoader();

		assertNotNull("Cannot load the class", loader);

		location = loader.getResource(classLocation);

		return new File(location.getFile()).getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().getParentFile();
	}
	
	protected void runGeneration(Log log, File vppde, File baseDir,
			List<File> dependedArtifactsSourceLocation,List<String> excludePackages,
			List<String> excludeClasses, List<String> importPackages)
	{
		try
		{
			VdmProject p = new VdmProject(log, vppde, baseDir,
					dependedArtifactsSourceLocation);
			p.codeGen(excludePackages, excludeClasses, importPackages);
		} catch (MojoFailureException e)
		{
			assertFalse("Code generation faild: "+e.getMessage(), true);
		} catch (MojoExecutionException e)
		{
			assertFalse("Code generation error: "+e.getMessage(), true);
		}

		assertFalse("Compile error", JavaCommandLineCompiler.compile(
				new File(settings.getDefaultProfile().getProperty(
						org.overture.maven.Profile.USER_JAVAC)),
				baseDir,
				getStdLibJarFile()));
	}
}
