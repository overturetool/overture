package org.overture.ide.plugins.codegen.commands;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.overture.codegen.vdm2java.JavaSettings;
import org.overture.ide.plugins.codegen.CodeGenConsole;
import org.overture.ide.plugins.codegen.util.LaunchConfigData;
import org.overture.ide.plugins.codegen.util.PluginVdm2JavaUtil;

public class Vdm2JavaLaunchConfigCommand extends Vdm2JavaCommand
{
	private static final String WARNING = "[WARNING]";
	@Override
	public JavaSettings getJavaSettings(IProject project,
			List<String> classesToSkip)
	{
		JavaSettings javaSettings = super.getJavaSettings(project, classesToSkip);

		List<LaunchConfigData> launchConfigs = PluginVdm2JavaUtil.getProjectLaunchConfigs(project);

		if (!launchConfigs.isEmpty())
		{
			String entryExp = PluginVdm2JavaUtil.dialog(launchConfigs);
			javaSettings.setVdmEntryExp(entryExp);
		} else
		{
			CodeGenConsole.GetInstance().println(WARNING + " No launch configuration could be found for this project.");
			CodeGenConsole.GetInstance().println(WARNING + " Continuing Java code generation without launch configuration...\n");
		}

		return javaSettings;
	}
}
