package org.overture.ide.utility;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.overturetool.vdmj.Release;

public class VdmProject implements IVdmProject
{
	private final String BUILDER_ID = "org.eclipse.dltk.core.scriptbuilder";
	private final String LANGUAGE_VERSION_ARGUMENT_KEY = "VDM_LANGUAGE_VERSION";
	private final String DYNAMIC_TYPE_CHECKS_ARGUMENT_KEY = "VDM_DYNAMIC_TYPE_CHECKS";
	private final String INV_CHECKS_ARGUMENT_KEY = "INV_CHECKS";
	private final String POST_CHECKS_ARGUMENT_KEY = "POST_CHECKS";
	private final String PRE_CHECKS_ARGUMENT_KEY = "PRE_CHECKS";
	private final String SUPRESS_WARNINGS_ARGUMENT_KEY = "SUPPRESS_WARNINGS";

	private IProject project;

	public VdmProject(IProject project) {
		this.project = project;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.overture.ide.utility.IVdmProject#getProject()
	 */
	/* (non-Javadoc)
	 * @see org.overture.ide.utility.IVdmProject1#getProject()
	 */
	public IProject getProject()
	{
		return project;
	}

	@SuppressWarnings("unchecked")
	public static void addBuilder(IProject project, String name,
			String argumentKey, String argumentValue) throws CoreException
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
				if (argumentKey != null && argumentValue != null)
				{

					Map arguments = command.getArguments();
					if (arguments == null)
						arguments = new HashMap<String, String>();

					if (arguments.containsKey(argumentKey))
						arguments.remove(argumentKey);

					arguments.put(argumentKey, argumentValue);

					command.setArguments(arguments);
				}

			}
		}

		if (!found)
		{
			ICommand newCommand = description.newCommand();
			newCommand.setBuilderName(name);
			if (argumentKey != null && argumentValue != null)
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.overture.ide.utility.IVdmProject#setBuilder(org.overturetool.vdmj
	 * .Release)
	 */
	/* (non-Javadoc)
	 * @see org.overture.ide.utility.IVdmProject1#setBuilder(org.overturetool.vdmj.Release)
	 */
	public void setBuilder(Release languageVersion) throws CoreException
	{
		addBuilder(getProject(),
				BUILDER_ID,
				LANGUAGE_VERSION_ARGUMENT_KEY,
				languageVersion.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.overture.ide.utility.IVdmProject#hasBuilder()
	 */
	/* (non-Javadoc)
	 * @see org.overture.ide.utility.IVdmProject1#hasBuilder()
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.overture.ide.utility.IVdmProject#getLanguageVersion()
	 */
	/* (non-Javadoc)
	 * @see org.overture.ide.utility.IVdmProject1#getLanguageVersion()
	 */
	public Release getLanguageVersion() throws CoreException
	{
		if (!hasBuilder())
			return Release.DEFAULT;
		else
		{
			Object languageVersion = getBuilderArguemnt(LANGUAGE_VERSION_ARGUMENT_KEY);
			if (languageVersion != null)
				return Release.valueOf(languageVersion.toString());

		}
		return Release.DEFAULT;
	}

	/* (non-Javadoc)
	 * @see org.overture.ide.utility.IVdmProject1#hasDynamictypechecks()
	 */
	public boolean hasDynamictypechecks()
	{
		return hasArgument(DYNAMIC_TYPE_CHECKS_ARGUMENT_KEY, true);
	}

	/* (non-Javadoc)
	 * @see org.overture.ide.utility.IVdmProject1#hasInvchecks()
	 */
	public boolean hasInvchecks()
	{
		return hasArgument(INV_CHECKS_ARGUMENT_KEY, true);
	}

	/* (non-Javadoc)
	 * @see org.overture.ide.utility.IVdmProject1#hasPostchecks()
	 */
	public boolean hasPostchecks()
	{
		return hasArgument(POST_CHECKS_ARGUMENT_KEY, true);
	}

	/* (non-Javadoc)
	 * @see org.overture.ide.utility.IVdmProject1#hasPrechecks()
	 */
	public boolean hasPrechecks()
	{
		return hasArgument(PRE_CHECKS_ARGUMENT_KEY, true);
	}
	/* (non-Javadoc)
	 * @see org.overture.ide.utility.IVdmProject1#hasSuppressWarnings()
	 */
	public boolean hasSuppressWarnings()
	{
		return hasArgument(SUPRESS_WARNINGS_ARGUMENT_KEY, false);
	}
	
	/* (non-Javadoc)
	 * @see org.overture.ide.utility.IVdmProject1#setDynamictypechecks(java.lang.Boolean)
	 */
	public void setDynamictypechecks(Boolean value) throws CoreException
	{
		addBuilder(getProject(),
				BUILDER_ID,
				DYNAMIC_TYPE_CHECKS_ARGUMENT_KEY,
				value.toString());
	}

	/* (non-Javadoc)
	 * @see org.overture.ide.utility.IVdmProject1#setInvchecks(java.lang.Boolean)
	 */
	public void setInvchecks(Boolean value) throws CoreException
	{
		addBuilder(getProject(),
				BUILDER_ID,
				INV_CHECKS_ARGUMENT_KEY,
				value.toString());
	}

	/* (non-Javadoc)
	 * @see org.overture.ide.utility.IVdmProject1#setPostchecks(java.lang.Boolean)
	 */
	public void setPostchecks(Boolean value) throws CoreException
	{
		addBuilder(getProject(),
				BUILDER_ID,
				POST_CHECKS_ARGUMENT_KEY,
				value.toString());
	}

	/* (non-Javadoc)
	 * @see org.overture.ide.utility.IVdmProject1#setPrechecks(java.lang.Boolean)
	 */
	public void setPrechecks(Boolean value) throws CoreException
	{
		addBuilder(getProject(),
				BUILDER_ID,
				PRE_CHECKS_ARGUMENT_KEY,
				value.toString());
	}
	/* (non-Javadoc)
	 * @see org.overture.ide.utility.IVdmProject1#setSuppressWarnings(java.lang.Boolean)
	 */
	public void setSuppressWarnings(Boolean value) throws CoreException
	{
		addBuilder(getProject(),
				BUILDER_ID,
				SUPRESS_WARNINGS_ARGUMENT_KEY,
				value.toString());
	}

	private boolean hasArgument(String argumentKey,boolean defaultValue)
	{
		Object argument;
		try
		{
			argument = getBuilderArguemnt(argumentKey);
			if (argument != null)
			{
				return Boolean.parseBoolean(argument.toString());
			}
		} catch (CoreException e)
		{
			// Could not retrieve value so return default
		}

		return defaultValue;
	}
	private Object getBuilderArguemnt(String argumentKey) throws CoreException
	{
		if (hasBuilder())
		{
			Vector<ICommand> buildCommands = new Vector<ICommand>();
			IProjectDescription description = project.getDescription();
			for (ICommand command : description.getBuildSpec())
			{
				buildCommands.add(command);
				if (command.getBuilderName().equals(BUILDER_ID))
				{
					if (command.getArguments().containsKey(argumentKey))
					{
						Object argumentValue = command.getArguments()
								.get(argumentKey);
						if (argumentValue != null)
							return argumentValue;
					}
				}

			}
		}
		return null;
	}

	/**
	 * For this marvelous project we need to: - create the default Eclipse
	 * project - add the custom project nature - create the folder structure
	 * 
	 * @param projectName
	 * @param location
	 * @param natureId
	 * @return
	 */
	public static IVdmProject createProject(String projectName, URI location,
			String nature)
	{
		Assert.isNotNull(projectName);
		Assert.isTrue(projectName.trim().length() > 0);

		IProject project = createBaseProject(projectName, location);
		try
		{
			addNature(project, nature);

			//String[] paths = { };//"parent/child1-1/child2", "parent/child1-2/child2/child3" }; //$NON-NLS-1$ //$NON-NLS-2$
			// addToProjectStructure(project, paths);
		} catch (CoreException e)
		{
			e.printStackTrace();
			project = null;
		}

		return new VdmProject(project);
	}

	/**
	 * Just do the basics: create a basic project.
	 * 
	 * @param location
	 * @param projectName
	 */
	private static IProject createBaseProject(String projectName, URI location)
	{
		// it is acceptable to use the ResourcesPlugin class
		IProject newProject = ResourcesPlugin.getWorkspace()
				.getRoot()
				.getProject(projectName);

		if (!newProject.exists())
		{
			URI projectLocation = location;
			IProjectDescription desc = newProject.getWorkspace()
					.newProjectDescription(newProject.getName());

			if (location != null
					|| ResourcesPlugin.getWorkspace()
							.getRoot()
							.getLocationURI()
							.equals(location))
			{
				projectLocation = null;
			}

			desc.setLocationURI(projectLocation);
			try
			{
				newProject.create(desc, null);
				if (!newProject.isOpen())
				{
					newProject.open(null);
				}
			} catch (CoreException e)
			{
				e.printStackTrace();
			}
		}

		return newProject;
	}

	private static void addNature(IProject project, String nature)
			throws CoreException
	{
		if (!project.hasNature(nature))
		{
			IProjectDescription description = project.getDescription();
			String[] prevNatures = description.getNatureIds();
			String[] newNatures = new String[prevNatures.length + 1];
			System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
			newNatures[prevNatures.length] = nature;
			description.setNatureIds(newNatures);

			IProgressMonitor monitor = null;
			project.setDescription(description, monitor);
		}
	}
	
	
	public void typeCheck() throws CoreException
	{
		getProject().build(IncrementalProjectBuilder.FULL_BUILD , null);
	}

}
