package org.overture.ide.utility;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.overturetool.vdmj.Release;

public class VdmProject
{
	private final String BUILDER_ID="org.eclipse.dltk.core.scriptbuilder";
	private final String LANGUAGE_VERSION_ARGUMENT_KEY = "VDM_LANGUAGE_VERSION";
	private IProject project;

	public VdmProject(IProject project) {
		this.project = project;
	}

	public IProject getProject()
	{
		return project;
	}

	public static void addBuilder(IProject project, String name,String argumentKey, String argumentValue) throws CoreException
	{
		Vector<ICommand> buildCommands = new Vector<ICommand>();
		boolean found = false;
		IProjectDescription description = project.getDescription();
		for (ICommand command : description.getBuildSpec())
		{
			buildCommands.add(command);
			if (command.getBuilderName().equals(name))
			{
				found = true;
				if(argumentKey!=null && argumentValue!=null)
				{
					Map arguments = new HashMap<String, String>();
					arguments.put(argumentKey, argumentValue);
					command.setArguments(arguments);
				}
				
			}
		}

		if (!found)
		{
			ICommand newCommand = description.newCommand();
			newCommand.setBuilderName(name);
			if(argumentKey!=null && argumentValue!=null)
			{
				Map arguments = new HashMap<String, String>();
				arguments.put(argumentKey, argumentValue);
				newCommand.setArguments(arguments);
			}
			
			buildCommands.add(newCommand);

			

		}
		ICommand[] commands = new ICommand[buildCommands.size()];
		commands = buildCommands.toArray(commands);
		description.setBuildSpec(commands);

		project.setDescription(description, null);

	}
	
	public void setBuilder(Release languageVersion) throws CoreException
	{
		addBuilder(getProject(), BUILDER_ID, LANGUAGE_VERSION_ARGUMENT_KEY, languageVersion.toString());
	}
	
	public boolean hasBuilder() throws CoreException
	{
		Vector<ICommand> buildCommands = new Vector<ICommand>();
		IProjectDescription description = project.getDescription();
		for (ICommand command : description.getBuildSpec())
		{
			buildCommands.add(command);
			if (command.getBuilderName().equals(BUILDER_ID))
				return true;
		}
		return false;
	}
	
	public Release getLanguageVersion() throws CoreException
	{
		if(!hasBuilder())
			return Release.DEFAULT;
		else
		{
			Vector<ICommand> buildCommands = new Vector<ICommand>();
			IProjectDescription description = project.getDescription();
			for (ICommand command : description.getBuildSpec())
			{
				buildCommands.add(command);
				if (command.getBuilderName().equals(BUILDER_ID))
				{
				if(command.getArguments().containsKey(LANGUAGE_VERSION_ARGUMENT_KEY))
				{
					Object languageVersion = command.getArguments().get(LANGUAGE_VERSION_ARGUMENT_KEY);
					if(languageVersion!=null)
					return Release.valueOf(languageVersion.toString());
				}
				}
					
			}
		}
		return Release.DEFAULT;
	}

}
