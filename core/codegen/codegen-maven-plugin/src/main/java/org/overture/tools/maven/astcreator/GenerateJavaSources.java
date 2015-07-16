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
	public static final String VDM_PP = "pp";
	public static final String VDM_SL = "sl";
	
	public static final String VDM_10 = "vdm10";
	public static final String VDM_CLASSIC = "classic";

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException
	{
		getLog().info("Preparing for VDM to Java generation...");
		// Let's make sure that maven knows to look in the output directory
		project.addCompileSourceRoot(outputDirectory.getPath());

		IRSettings irSettings = new IRSettings();
		irSettings.setCharSeqAsString(true);
		irSettings.setGeneratePreConds(false);
		irSettings.setGeneratePreCondChecks(false);
		irSettings.setGeneratePostConds(false);
		irSettings.setGeneratePostCondChecks(false);

		JavaSettings javaSettings = new JavaSettings();
		javaSettings.setDisableCloning(false);
		javaSettings.setFormatCode(formatCode);

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
			files = FileUtils.listFiles(specificationRoot, new RegexFileFilter(".+\\.vpp|.+\\.vdmpp|.+\\.vsl|.+\\.vdmsl"), DirectoryFileFilter.DIRECTORY);
		}

		if (files == null || files.isEmpty())
		{
			getLog().info("Nothing to generate, no specification files.");
			return;
		}

		outputDirectory.mkdirs();

		getLog().info("Starting Java code generation...");
		List<File> tmp = new Vector<File>();
		tmp.addAll(files);
		
		if(release.equals(VDM_10))
		{
			Settings.release = Release.VDM_10;
		}
		else if(release.equals(VDM_CLASSIC))
		{
			Settings.release = Release.CLASSIC;
		}
		else
		{
			String errorMsg = String.format("Expected VDM version to be '%s' or '%s'", VDM_10, VDM_CLASSIC);
			getLog().error(errorMsg);
			throw new MojoFailureException(errorMsg); 
		}

		if (dialect.equals(VDM_PP))
		{
			JavaCodeGenMain.handleOo(tmp, irSettings, javaSettings, Dialect.VDM_PP, false, outputDirectory);
		} else if (dialect.equals(VDM_SL))
		{
			JavaCodeGenMain.handleSl(tmp, irSettings, javaSettings, false, outputDirectory);
		} else
		{
			String errorMsg = String.format("Expected dialect to be '%s' or '%s'", VDM_SL, VDM_PP);
			getLog().error(errorMsg);
			throw new MojoExecutionException(errorMsg);
		}
		
		getLog().info("Generation completed.");
	}
}
