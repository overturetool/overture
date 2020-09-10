/*
 * #%~
 * org.overture.ide.core
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.internal.core.resources;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.core.runtime.jobs.Job;
import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.ide.core.ICoreConstants;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.VdmCore;
import org.overture.ide.core.ast.NotAllowedException;
import org.overture.ide.core.builder.SafeBuilder;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.core.resources.ModelBuildPath;
import org.overture.ide.core.resources.Options;
import org.overture.ide.core.utility.ILanguage;
import org.overture.ide.core.utility.LanguageManager;

import org.overture.ide.internal.core.ResourceManager;
import org.overture.ide.internal.core.ast.VdmModelManager;

public class VdmProject implements IVdmProject
{
	private static final String LANGUAGE_VERSION_ARGUMENT_KEY = "LANGUAGE_VERSION";
	// private static final String DYNAMIC_TYPE_CHECKS_ARGUMENT_KEY = "DYNAMIC_TYPE_CHECKS";
	// private static final String INV_CHECKS_ARGUMENT_KEY = "INV_CHECKS";
	// private static final String POST_CHECKS_ARGUMENT_KEY = "POST_CHECKS";
	// private static final String PRE_CHECKS_ARGUMENT_KEY = "PRE_CHECKS";
	private static final String SUPRESS_WARNINGS_ARGUMENT_KEY = "SUPPRESS_WARNINGS";
	private static final String USE_STRICT_LET_DEF = "USE_STRICT_LET_DEF";

	// private static final String MEASURE_CHECKS_ARGUMENT_KEY = "MEASURE_CHECKS";
	
	

	public final IProject project;
	private ILanguage language = null;
	private final ModelBuildPath modelpath;
	private Options options = null;

	private VdmProject(IProject project) throws CoreException,
			NotAllowedException
	{
		this.project = project;

		for (ILanguage language : LanguageManager.getInstance().getLanguages())
		{
			if (project.hasNature(language.getNature()))
			{
				this.language = language;
				break;
			}
		}
		if (this.language == null)
			throw new NotAllowedException();

		this.modelpath = new ModelBuildPath(this);

		// Fix for old projects with Script Builder
		// this.setBuilder(this.getLanguageVersion());
	}

	public static boolean isVdmProject(IProject project)
	{

		try
		{

			for (ILanguage language : LanguageManager.getInstance().getLanguages())
			{

				if (project.hasNature(language.getNature()))
				{
					return true;
				}

			}
		} catch (CoreException e)
		{

		}
		return false;
	}

	public synchronized static IVdmProject createProject(IProject project)
	{
		if (ResourceManager.getInstance().hasProject(project))
			return ResourceManager.getInstance().getProject(project);
		else
		{
			try
			{
				IVdmProject vdmProject = new VdmProject(project);
				return ResourceManager.getInstance().addProject(vdmProject);
			} catch (Exception e)
			{
				if (VdmCore.DEBUG)
				{
					VdmCore.log("VdmModelManager createProject", e);
				}
				return null;
			}
		}
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

					@SuppressWarnings("rawtypes")
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
				@SuppressWarnings("rawtypes")
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
	 * @see org.overture.ide.utility.IVdmProject#setBuilder(org.overture.vdmj .Release)
	 */
	/*
	 * (non-Javadoc)
	 * @see org.overture.ide.utility.IVdmProject1#setBuilder(org.overture.vdmj .Release)
	 */
	public void setBuilder(Release languageVersion) throws CoreException
	{
		addBuilder(getProject(), ICoreConstants.BUILDER_ID, LANGUAGE_VERSION_ARGUMENT_KEY, languageVersion.toString());
	}

	private IProject getProject()
	{
		return this.project;
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.ide.utility.IVdmProject#hasBuilder()
	 */
	/*
	 * (non-Javadoc)
	 * @see org.overture.ide.utility.IVdmProject1#hasBuilder()
	 */
	public boolean hasBuilder() throws CoreException
	{
		Vector<ICommand> buildCommands = new Vector<ICommand>();
		IProjectDescription description = project.getDescription();
		for (ICommand command : description.getBuildSpec())
		{
			buildCommands.add(command);
			if (command.getBuilderName().equals(ICoreConstants.BUILDER_ID))
				return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.ide.utility.IVdmProject#getLanguageVersion()
	 */
	/*
	 * (non-Javadoc)
	 * @see org.overture.ide.utility.IVdmProject1#getLanguageVersion()
	 */
	public Release getLanguageVersion() throws CoreException
	{
		if (!hasBuilder())
			return Release.DEFAULT;
		else
		{
			Object languageVersion = getBuilderArguemnt(LANGUAGE_VERSION_ARGUMENT_KEY);
			if (languageVersion != null
					&& Release.lookup(languageVersion.toString()) != null)
				return Release.lookup(languageVersion.toString());

		}
		return Release.DEFAULT;
	}

	public String getLanguageVersionName() throws CoreException
	{
		return getLanguageVersion().toString();
	}

	// /*
	// * (non-Javadoc)
	// * @see org.overture.ide.utility.IVdmProject1#hasDynamictypechecks()
	// */
	// public boolean hasDynamictypechecks()
	// {
	// return hasArgument(DYNAMIC_TYPE_CHECKS_ARGUMENT_KEY, true);
	// }
	//
	// /*
	// * (non-Javadoc)
	// * @see org.overture.ide.utility.IVdmProject1#hasInvchecks()
	// */
	// public boolean hasInvchecks()
	// {
	// return hasArgument(INV_CHECKS_ARGUMENT_KEY, true);
	// }
	//
	// /*
	// * (non-Javadoc)
	// * @see org.overture.ide.utility.IVdmProject1#hasPostchecks()
	// */
	// public boolean hasPostchecks()
	// {
	// return hasArgument(POST_CHECKS_ARGUMENT_KEY, true);
	// }
	//
	// /*
	// * (non-Javadoc)
	// * @see org.overture.ide.utility.IVdmProject1#hasPrechecks()
	// */
	// public boolean hasPrechecks()
	// {
	// return hasArgument(PRE_CHECKS_ARGUMENT_KEY, true);
	// }
	//
	// public boolean hasMeasurechecks()
	// {
	// return hasArgument(MEASURE_CHECKS_ARGUMENT_KEY, true);
	// }

	/*
	 * (non-Javadoc)
	 * @see org.overture.ide.utility.IVdmProject1#hasSuppressWarnings()
	 */
	public boolean hasSuppressWarnings()
	{
		return hasArgument(SUPRESS_WARNINGS_ARGUMENT_KEY, false);
	}

	
	@Override
	public boolean hasUseStrictLetDef() 
	{
		return hasArgument(USE_STRICT_LET_DEF, false);
	}
	


	// /*
	// * (non-Javadoc)
	// * @see org.overture.ide.utility.IVdmProject1#setDynamictypechecks(java.lang. Boolean)
	// */
	// public void setDynamictypechecks(Boolean value) throws CoreException
	// {
	// addBuilder(getProject(), ICoreConstants.BUILDER_ID, DYNAMIC_TYPE_CHECKS_ARGUMENT_KEY, value.toString());
	// }
	//
	// /*
	// * (non-Javadoc)
	// * @see org.overture.ide.utility.IVdmProject1#setInvchecks(java.lang.Boolean)
	// */
	// public void setInvchecks(Boolean value) throws CoreException
	// {
	// addBuilder(getProject(), ICoreConstants.BUILDER_ID, INV_CHECKS_ARGUMENT_KEY, value.toString());
	// }
	//
	// /*
	// * (non-Javadoc)
	// * @see org.overture.ide.utility.IVdmProject1#setPostchecks(java.lang.Boolean)
	// */
	// public void setPostchecks(Boolean value) throws CoreException
	// {
	// addBuilder(getProject(), ICoreConstants.BUILDER_ID, POST_CHECKS_ARGUMENT_KEY, value.toString());
	// }
	//
	// /*
	// * (non-Javadoc)
	// * @see org.overture.ide.utility.IVdmProject1#setPrechecks(java.lang.Boolean)
	// */
	// public void setPrechecks(Boolean value) throws CoreException
	// {
	// addBuilder(getProject(), ICoreConstants.BUILDER_ID, PRE_CHECKS_ARGUMENT_KEY, value.toString());
	// }
	//
	// public void setMeasurechecks(Boolean value) throws CoreException
	// {
	// addBuilder(getProject(), ICoreConstants.BUILDER_ID,MEASURE_CHECKS_ARGUMENT_KEY, value.toString());
	// }

	/*
	 * (non-Javadoc)
	 * @see org.overture.ide.utility.IVdmProject1#setSuppressWarnings(java.lang.Boolean )
	 */
	public void setSuppressWarnings(Boolean value) throws CoreException
	{
		addBuilder(getProject(), ICoreConstants.BUILDER_ID, SUPRESS_WARNINGS_ARGUMENT_KEY, value.toString());
	}
	
	@Override
	public void setUseStrictLetDef(Boolean value) throws CoreException 
	{
		addBuilder(getProject(), ICoreConstants.BUILDER_ID, USE_STRICT_LET_DEF , value.toString());
	}

	private boolean hasArgument(String argumentKey, boolean defaultValue)
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
			return getBuilderArgument(project, ICoreConstants.BUILDER_ID, argumentKey);

		return null;
	}

	public static Object getBuilderArgument(IProject project, String builderId,
			String argumentKey) throws CoreException
	{
		Vector<ICommand> buildCommands = new Vector<ICommand>();
		IProjectDescription description = project.getDescription();
		for (ICommand command : description.getBuildSpec())
		{
			buildCommands.add(command);
			if (command.getBuilderName().equals(builderId))
			{
				if (command.getArguments().containsKey(argumentKey))
				{
					Object argumentValue = command.getArguments().get(argumentKey);
					if (argumentValue != null)
						return argumentValue;
				}
			}

		}
		return null;
	}

	/**
	 * For this marvelous project we need to: - create the default Eclipse project - add the custom project nature -
	 * create the folder structure
	 * 
	 * @param projectName
	 * @param location
	 * @param natureId
	 * @return
	 * @throws NotAllowedException
	 * @throws CoreException
	 */
	public static IVdmProject createProject(String projectName, URI location,
			String nature) throws CoreException, NotAllowedException
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
			VdmCore.log("VdmProject createProject", e);
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
		IProject newProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

		if (!newProject.exists())
		{
			URI projectLocation = location;
			IProjectDescription desc = newProject.getWorkspace().newProjectDescription(newProject.getName());

			if (location != null
					|| ResourcesPlugin.getWorkspace().getRoot().getLocationURI().equals(location))
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
				VdmCore.log("VdmModelManager createBaseProject", e);
			}
		}

		return newProject;
	}

	public static void addNature(IProject project, String nature)
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

	public boolean typeCheck(IProgressMonitor monitor) throws CoreException
	{

		// getProject().build(IncrementalProjectBuilder.FULL_BUILD, null);
		IVdmModel model = getModel();
		if (model.isTypeCorrect())
		{
			return true; // no need to do any work
		} else
		{
			final IVdmProject currentProject = this;

			final IProgressMonitor mon = monitor;
			ISafeRunnable runnable = new ISafeRunnable()
			{

				public void handleException(Throwable e)
				{
					VdmCore.log("VdmProject typeCheck ISafeRunnable", e);
				}

				@SuppressWarnings("deprecation")
				public void run() throws Exception
				{

					final SafeBuilder builder = new SafeBuilder(currentProject, mon);
					builder.start();
					while (builder.isAlive())
					{
						Thread.sleep(100);
						if (mon.isCanceled())
						{
							builder.stop();
						}
					}

				}

			};
			SafeRunner.run(runnable);

			return model.isTypeCorrect();
		}
	}

	public void typeCheck(boolean clean, IProgressMonitor monitor)
			throws CoreException
	{
		if (clean)
			getProject().build(IncrementalProjectBuilder.FULL_BUILD, monitor);
		else
			getProject().build(IncrementalProjectBuilder.AUTO_BUILD, monitor);
	}

	public static void waitForBuidCompletion()
	{
		while (true)
		{
			Job[] jobs = Job.getJobManager().find(null);
			boolean builderFound = false;
			for (Job job : jobs)
			{
				if (job.getName().contains("org.overture.ide.builders.vdmj"))
					builderFound = true;

			}
			if (!builderFound)
				return;
			else
			{
				try
				{
					Thread.sleep(200);
				} catch (InterruptedException e)
				{

				}
			}
		}
	}

	/***
	 * This method removed all problem markers and its sub-types from the project. It is called before an instance of
	 * the AbstractBuilder is created
	 * 
	 * @param project
	 *            The project which should be build.
	 */
	public void clearProblemMarkers()
	{
		try
		{
			this.project.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);

		} catch (CoreException e)
		{
			if (VdmCore.DEBUG)
			{
				VdmCore.log("VdmProject clearProblemMarkers", e);
			}
		}

	}

	public File getFile(IFile file)
	{
		Path path = new Path(file.getProject().getFullPath().addTrailingSeparator().toString()
				+ file.getProjectRelativePath().toString());
		return getSystemFile(path);
	}

	public File getSystemFile(IPath path)
	{
		return project.getFile(path.removeFirstSegments(1)).getLocation().toFile();
	}

	public File getFile(IWorkspaceRoot wroot, IPath path)
	{
		return wroot.getFile(path.removeFirstSegments(1)).getLocation().toFile();
	}

	/***
	 * Get files from a eclipse project
	 * 
	 * @param project
	 *            the project to scan
	 * @param contentTypeId
	 *            of the type of files that should be returned
	 * @return a list of IFile
	 * @throws CoreException
	 */
	public List<IVdmSourceUnit> getSpecFiles() throws CoreException
	{
		List<IVdmSourceUnit> list = new Vector<IVdmSourceUnit>();
		IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
		for (String contentTypeId : language.getContentTypes())
		{
			for (IVdmSourceUnit iVdmSourceUnit : getFiles(contentTypeManager.getContentType(contentTypeId)))
			{
				if(!list.contains(iVdmSourceUnit))
				{
					list.add(iVdmSourceUnit);
				}
			}
		}

		return list;
	}

	public String getVdmNature()
	{
		return language.getNature();
	}

	/***
	 * Get files from a eclipse project
	 * 
	 * @param project
	 *            the project to scan
	 * @param iContentType
	 *            of the type of files that should be returned
	 * @return a list of IFile
	 * @throws CoreException
	 */
	public List<IVdmSourceUnit> getFiles(IContentType iContentType)
			throws CoreException
	{
		List<IVdmSourceUnit> list = new Vector<IVdmSourceUnit>();

		for (IContainer container : modelpath.getModelSrcPaths())
		{
			if (!container.exists() || !container.isAccessible())
			{
				continue;
			}
			for (IResource res : container.members(IContainer.INCLUDE_PHANTOMS
					| IContainer.INCLUDE_TEAM_PRIVATE_MEMBERS))
			{
				list.addAll(ResourceManager.getInstance().getFiles(this, res, iContentType));
			}
		}

		return list;
	}

	public boolean isModelFile(IFile file) throws CoreException
	{
		for (IContainer src : modelpath.getModelSrcPaths())
		{
			// Check model path
			if (src.getFullPath().isPrefixOf(file.getFullPath())&& !modelpath.getOutput().getFullPath().isPrefixOf(file.getFullPath()))//TODO check this, does it break something? not using the actual location. This was changed to do linked files.
			{
				//Check content type
				for (IContentType contentType : getContentTypeIds())
				{
					if(contentType.isAssociatedWith(file.getName())&& !file.getName().startsWith("~$"))
					{
						return true;
					}
				}
//				if (file.getContentDescription() != null && getContentTypeIds().contains(file.getContentDescription().getContentType().getId()))
//				{
//					return true;
//				}
			}
		}

		return false;
	}
	

	/***
	 * Gets the IFile from the Eclipse filesystem from a normal file placed in a project
	 * 
	 * @param project
	 *            the project which holds the file
	 * @param file
	 *            the File to look up
	 * @return a new IFile representing the file in the eclipse filesystem
	 */
	public IFile findIFile(File file)
	{
		IPath location = Path.fromOSString(file.getAbsolutePath());
		IFile ifile = project.getFile(location);

		if (ifile == null || !ifile.exists())
		{

			IPath absolutePath = new Path(file.getAbsolutePath());
			// check if the project contains a IFile which maps to the same
			// file system location
			try
			{
				for (IVdmSourceUnit f : getSpecFiles())
				{
					if (f.getFile().getLocation().equals(absolutePath))
						return f.getFile();
				}
			} catch (CoreException e1)
			{
			}

			// project does not contain this file, this means that the file has
			// been include elsewhere and a link will be created to the file
			// instead.
			try
			{
				linkFileToProject(file);
			} catch (CoreException e)
			{
			}

		}
		return ifile;
	}

	public void linkFileToProject(File file) throws CoreException
	{
		final IPath absolutePath = new Path(file.getAbsolutePath());
		final IFile ifile = project.getFile(absolutePath.lastSegment());
		Job j = new Job("Link file")
		{

			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				try
				{
					ifile.createLink(absolutePath, IResource.REPLACE, null);
				} catch (CoreException e)
				{
					VdmCore.log("VdmProject linkFileToProject", e);
					return Status.CANCEL_STATUS;
				}

				return Status.OK_STATUS;
			}

		};
		j.setPriority(Job.BUILD);
		j.schedule();

	}

	public IVdmModel getModel()
	{
		return VdmModelManager.getInstance().getModel(this);
	}

	@Override
	public String toString()
	{
		return getName();
	}

	public Dialect getDialect()
	{
		return this.language.getDialect();
	}

	public IVdmSourceUnit findSourceUnit(IFile file) throws CoreException
	{
		for (IVdmSourceUnit source : getSpecFiles())
		{
			if(source.getFile().equals(file))
			{
				return source;
			}
		}
		return null;
	}

	public List<IContentType> getContentTypeIds()
	{
		IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
		List<IContentType> types = new Vector<IContentType>();
		for (String type : language.getContentTypes())
		{
			types.add(contentTypeManager.getContentType(type));
		}
		return types;
	}

	public String getName()
	{
		return this.project.getName();
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter)
	{
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	public ModelBuildPath getModelBuildPath()
	{
		return this.modelpath;
	}

	public Options getOptions()
	{
		if (options == null)
		{
			options = Options.load(this);
		}
		return options;
	}


		

	// @Override
	// public boolean equals(Object obj)
	// {
	// return project.equals(obj);
	// }
	// @Override
	// public int hashCode()
	// {
	// return project.hashCode();
	// }

}
