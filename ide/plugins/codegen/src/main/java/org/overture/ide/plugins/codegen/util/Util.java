package org.overture.ide.plugins.codegen.util;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.overture.ide.core.resources.IVdmProject;

public class Util
{	
	public static File getOutputFolder(IVdmProject project) throws CoreException
	{
		File outputDir = project.getModelBuildPath().getOutput().getLocation().toFile();
		outputDir = new File(outputDir, "java");
		outputDir.mkdirs();
		return outputDir;
	}
}
