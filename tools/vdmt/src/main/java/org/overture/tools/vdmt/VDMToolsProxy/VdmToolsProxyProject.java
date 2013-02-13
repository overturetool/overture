package org.overture.tools.vdmt.VDMToolsProxy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.overturetool.vdmtools.VDMToolsError;
import org.overturetool.vdmtools.VDMToolsProject;

public class VdmToolsProxyProject extends VdmProject
{

	VDMToolsProject project;

	public VdmToolsProxyProject(Log log, File vdmToolsCmd, File parentFile,
			ArrayList<File> dependedVppLocations) throws MojoExecutionException
	{
		super(log, vdmToolsCmd, parentFile, dependedVppLocations);
		project = VDMToolsProject.getInstance();
		try
		{
			project.init(vdmToolsCmd.getAbsolutePath(), "VDM_PP");

			File vppFiles[] = new File[files.size()];
			vppFiles = files.toArray(vppFiles);
			
			for (File file : vppFiles)
			{
				project.addFilesToProject(new File[]{file});
				project.parseProject();
				if(VDMToolsProject.getInstance().getParseErrors().size()>0)
					log.info("Parse errors");
			}
			//project.addFilesToProject(vppFiles);
			
			
			if(VDMToolsProject.getInstance().getParseErrors().size()>0)
				log.info("Parse errors");

		} catch (Exception e)
		{
			throw new MojoExecutionException(
					"VDM Tools project not initialized correctly", e);
		}

	}

	public void typeCheck(List<String> excludePackages, List<String> excludeClasses,
			List<String> importPackages) throws MojoExecutionException
	{
		StringBuilder sb = new StringBuilder();
		try
		{
			for (VDMToolsError error : project.typeCheckProject())
			{
				sb.append(" " + error.getMessage() + " | "
						+ error.getFilename() + " (" + error.getLineNr() + ","
						+ error.getColNr() + ")");
			}
			if (sb.length() != 0)
				throw new MojoFailureException(sb.toString());
			
		} catch (Exception e)
		{
			throw new MojoExecutionException("Error in type check",e);
		}

	}

}
