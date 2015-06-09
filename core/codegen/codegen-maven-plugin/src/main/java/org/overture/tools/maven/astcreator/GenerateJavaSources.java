package org.overture.tools.maven.astcreator;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.vdm2java.JavaCodeGenMain;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;
import org.overture.codegen.vdm2java.JavaSettings;
import org.overture.config.Release;
import org.overture.config.Settings;

/**
 * Generate Tree
 * 
 * @goal generate
 * @phase generate-sources
 * @requiresDependencyResolution compile
 */
public class GenerateJavaSources extends AstCreatorBaseMojo
{

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException
	{
		getLog().info("Preparing for VDM to Java generation...");
		// Let's make sure that maven knows to look in the output directory
		project.addCompileSourceRoot(outputDirectory.getPath());

		Settings.release = Release.VDM_10;
		Dialect dialect = Dialect.VDM_PP;

		IRSettings irSettings = new IRSettings();
		irSettings.setCharSeqAsString(true);
		irSettings.setGeneratePreConds(false);
		irSettings.setGeneratePreCondChecks(false);
		irSettings.setGeneratePostConds(false);
		irSettings.setGeneratePostCondChecks(false);

		JavaSettings javaSettings = new JavaSettings();
		javaSettings.setDisableCloning(false);

		if (JavaCodeGenUtil.isValidJavaPackage(packageName))
		{
			javaSettings.setJavaRootPackage(packageName);
		} else
		{
			getLog().error(String.format("The Java package: '%s' is not valid.", packageName));
			// throw new MojoFailureException
		}

		Collection<File> files = null;
		File specificationRoot = getResourcesDir();
		
		if(specificationDir!=null && !specificationDir.isEmpty())
		{
			specificationRoot = new File(specificationRoot,specificationDir);
		}
		
		if (specificationRoot != null && specificationRoot.exists())
		{
			files = FileUtils.listFiles(specificationRoot, new RegexFileFilter(".+\\.vpp|.+\\.vdmpp"), DirectoryFileFilter.DIRECTORY);
		}

		if (files == null || files.isEmpty())
		{
			getLog().info("Nothing to generate, no specification files.");
			return;
		}

		outputDirectory.mkdirs();

		getLog().info("Starting generation...");
		List<File> tmp = new Vector<File>();
		tmp.addAll(files);
		JavaCodeGenMain.handleOo(tmp, irSettings, javaSettings, dialect, false, outputDirectory);
		getLog().info("Generation completed.");
	}

	private Collection<? extends File> getSpecFiles(File root)
	{
		List<File> files = new Vector<File>();

		for (File file : root.listFiles(new VdmppNameFilter()))
		{// TODO
			files.add(file);
		}
		return files;
	}

}
