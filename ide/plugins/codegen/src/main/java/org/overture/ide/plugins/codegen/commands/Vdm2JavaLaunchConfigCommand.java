package org.overture.ide.plugins.codegen.commands;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.overture.codegen.vdm2java.JavaSettings;
import org.overture.ide.plugins.codegen.util.PluginVdm2JavaUtil;

public class Vdm2JavaLaunchConfigCommand extends Vdm2JavaCommand
{
	@Override
	public JavaSettings getJavaSettings(IProject project,
			List<String> classesToSkip)
	{
		JavaSettings javaSettings =  super.getJavaSettings(project, classesToSkip);
		
		String entryExp = PluginVdm2JavaUtil.dialog(project);
		
		javaSettings.setVdmLaunchConfigEntryExp(entryExp);
		
		return javaSettings;
	}
}
