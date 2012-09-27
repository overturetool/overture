package org.overture.tools.plugins.astcreator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.overture.tools.plugins.astcreator.util.Util;

import com.lausdahl.ast.creator.Main;
import com.lausdahl.ast.creator.env.Environment;

/**
 * Generate Tree
 * 
 * @goal generate
 * @phase generate-sources
 * @requiresDependencyResolution compile
 */
public class GenerateTree extends AstCreatorBaseMojo
{

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException
	{
		// Let's make sure that maven knows to look in the output directory
		project.addCompileSourceRoot(outputDirectory.getPath());

		File treeName = null;

		treeName = new File(getResourcesDir(), ast);

		getLog().info("Preparing tree generating...");

		File toStringAstFile = new File(treeName.getAbsolutePath()
				+ ".tostring");

		if (isCrcEqual(treeName))
		{
			if (toStringAstFile.exists() && isCrcEqual(toStringAstFile))
			{
				if (extendedast == null || extendedast.isEmpty()
						|| isCrcEqual(new File(getResourcesDir(), extendedast)))
				{
					getLog().info("Nothing to generate, source already up-to-date");
					return;
				}
			}
		}

		if (deletePackageOnGenerate != null)
		{

			for (String relatevePath : deletePackageOnGenerate)
			{
				relatevePath = relatevePath.replace('.', File.separatorChar);
				getLog().info("Deleteting folder: " + relatevePath);
				File f = new File(getGeneratedFolder(), relatevePath.replace('/', File.separatorChar));
				if (f.exists())
				{
					deleteDir(f);
				} else
				{
					getLog().warn("Folder not found and delete skipped: "
							+ relatevePath);
				}
			}

		}

		if (treeName.exists())
		{
			File generated = getGeneratedFolder();

			getLog().info("Generator starting with input: " + treeName);
			Environment env1 = null;
			if (extendedast == null)
			{
				generateSingleAst(treeName, toStringAstFile, generated, env1);
			} else
			{
				File extendedAstFile = new File(getResourcesDir(), extendedast);
				if (!extendedAstFile.exists())
				{
					getLog().equals("Extended AST file does not exist: "
							+ extendedAstFile.getAbsolutePath());
					return;
				}
				try
				{
					Main.create(treeName, extendedAstFile, generated, "Interpreter", generateVdm());
				} catch (Exception e)
				{
					getLog().error(e);
				}
			}
		} else
		{
			getLog().error("Cannot find input file: "
					+ treeName.getAbsolutePath());

		}
	}
	
	public boolean generateVdm()
	{
		return generateVdm!=null && generateVdm;
	}

	public File getGeneratedFolder()
	{
		// if (useSrcOutput)
		// {
		// 	return getProjectJavaSrcDirectory();
		// }
		//return new File(getProjectOutputDirectory(), "generated-sources/astCreator".replace('/', File.separatorChar));
		return outputDirectory;
	}

	public void generateSingleAst(File treeName, File toStringAstFile,
			File generated, Environment env1)
	{
		try
		{
			env1 = Main.create(new FileInputStream(treeName.getAbsolutePath()), generated, true, generateVdm());
			setCrc(treeName);
			setCrc(toStringAstFile);
		} catch (Exception e)
		{
			getLog().error(e);
		}
		if (env1 != null)
		{
			getLog().info("Generator completed with "
					+ env1.getAllDefinitions().size() + " generated files.\n\n");
		}
	}

	public static boolean deleteDir(File dir)
	{
		if (dir.isDirectory())
		{
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++)
			{
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success)
				{
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}

	public boolean isCrcEqual(File astFile)
	{
		String name = astFile.getName();
		long sourceCrc = Util.getCheckSum(astFile.getAbsolutePath());

		File crcFile = new File(getProjectOutputDirectory(), name + ".crc");
		if (!crcFile.exists())
		{
			return false;
		}

		String crcString;
		try
		{
			crcString = Util.readFile(crcFile);
		} catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}

		long destinationCrc = Long.valueOf(crcString);

		return destinationCrc == sourceCrc;
	}

	public void setCrc(File astFile) throws IOException
	{
		String name = astFile.getName();
		Long sourceCrc = Util.getCheckSum(astFile.getAbsolutePath());

		File crcFile = new File(getProjectOutputDirectory(), name + ".crc");
		Util.writeFile(crcFile, sourceCrc.toString());
	}

}
