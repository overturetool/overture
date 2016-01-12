package org.overture.ide.plugins.javagen.commands;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.overture.codegen.vdm2java.JavaSettings;
import org.overture.ide.plugins.javagen.CodeGenConsole;
import org.overture.ide.plugins.javagen.util.LaunchConfigData;
import org.overture.ide.plugins.javagen.util.PluginVdm2JavaUtil;

public class Vdm2JavaLaunchConfigCommand extends Vdm2JavaCommand
{
	@Override
	public JavaSettings getJavaSettings(IProject project,
			List<String> classesToSkip)
	{
		List<LaunchConfigData> launchConfigs = PluginVdm2JavaUtil.getProjectLaunchConfigs(project);

		if (!launchConfigs.isEmpty())
		{
			String entryExp = PluginVdm2JavaUtil.dialog(launchConfigs);

			if (entryExp != null)
			{
				JavaSettings javaSettings = super.getJavaSettings(project, classesToSkip);
				javaSettings.setVdmEntryExp(entryExp);
				return javaSettings;
			}
			else
			{
				CodeGenConsole.GetInstance().println("Process cancelled by user.");
			}
		} else
		{
			CodeGenConsole.GetInstance().println(PluginVdm2JavaUtil.WARNING
					+ " No launch configuration could be found for this project.\n");
			CodeGenConsole.GetInstance().println("Cancelling launch configuration based code generation...\n");
		}

		return null;
	}
}
