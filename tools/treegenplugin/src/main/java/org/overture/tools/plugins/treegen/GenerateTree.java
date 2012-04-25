package org.overture.tools.plugins.treegen;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.overture.tools.treegen.TreeGen;
import org.overture.tools.treegen.TreeGenOptions;

/**
 * Generate Tree
 * 
 * @goal treegen
 * @phase process-resources
 * @requiresDependencyResolution compile
 */
public class GenerateTree extends AstGenBaseMojo
{

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException
	{
		getLog().info("Preparing tree generating...");
		
		List<File> treeNames = new Vector<File>();
		
		for (String name : asts)
		{
			treeNames.add(new File(getResourcesDir(),name));
		}
		
		List<TreeGenOptions> options = new Vector<TreeGenOptions>();
		
		for (File file : treeNames)
		{
			if(file.exists())
			{
				System.out.println("Adding: "+ file.getName());
				TreeGenOptions option = new TreeGenOptions(file.getAbsolutePath());
				option.setJavaDirectory(getProjectJavaSrcDirectory().getAbsolutePath());
				option.setVppDirectory(getProjectVdmSrcDirectory().getAbsolutePath());
				option.setSplitVpp(true);
				options.add(option);
			}
		}
		
		
		getLog().info("Generating...");
		TreeGen.generate(options);
		
		getLog().info("Tree Generation done");
	}

	

	
	
	

}
