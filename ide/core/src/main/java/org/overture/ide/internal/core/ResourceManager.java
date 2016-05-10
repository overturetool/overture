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
package org.overture.ide.internal.core;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Vector;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.jobs.Job;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.VdmCore;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.core.resources.VdmSourceUnit;
import org.overture.ide.internal.core.ast.VdmModelManager;
import org.overture.ide.internal.core.resources.VdmProject;

public class ResourceManager implements IResourceChangeListener
{

	Map<String, IVdmProject> projects = new Hashtable<String, IVdmProject>();
	Map<IFile, IVdmSourceUnit> vdmSourceUnits = new Hashtable<IFile, IVdmSourceUnit>();
	Queue<IVdmProject> lastAccessed = new LinkedList<IVdmProject>();
	ArrayList<IProject> projectsToRebuild = new ArrayList<IProject>();

	/**
	 * A handle to the unique Singleton instance.
	 */
	static private volatile ResourceManager _instance = null;

	/**
	 * @return The unique instance of this class.
	 */
	static public ResourceManager getInstance()
	{
		if (null == _instance)
		{
			_instance = new ResourceManager();
		}
		return _instance;
	}

	public IVdmSourceUnit getVdmSourceUnit(IFile file)
	{
		if (file == null)
		{
			return null;
		}

		if (vdmSourceUnits.containsKey(file))
		{
			return vdmSourceUnits.get(file);
		} else
		{
			if (VdmProject.isVdmProject(file.getProject()))
			{
				IVdmProject project = VdmProject.createProject(file.getProject());

				try
				{
					if (!file.isSynchronized(IResource.DEPTH_ONE))
					{
						file.refreshLocal(IResource.DEPTH_ONE, null);
					}
					// if (file.getContentDescription() != null
					// &&
					// project.getContentTypeIds().contains(file.getContentDescription().getContentType().getId()))
					if (project.isModelFile(file))
					{
						IVdmSourceUnit unit = createSourceUnit(file, project);
						return unit;
					}
				} catch (CoreException e)
				{
					if (VdmCore.DEBUG)
					{
						e.printStackTrace();
					}
				}

			} else
				System.err.println("project is not vdm complient");
		}
		return null;
	}

	private IVdmSourceUnit createSourceUnit(IFile file, IVdmProject project)
	{
		IVdmModel model = project.getModel();
		model.addVdmSourceUnit(new VdmSourceUnit(project, file));
		IVdmSourceUnit unit = model.getVdmSourceUnit(file);
		vdmSourceUnits.put(file, unit);
		return unit;
	}

	/***
	 * Recursive search of a project for files based on the content type
	 * 
	 * @param project
	 *            the project to search
	 * @param resource
	 *            the resource currently selected to be searched
	 * @param contentTypeId
	 *            a possibly null content type id, if null it is just checked that a content type exist for the file
	 * @return a list of IFiles
	 * @throws CoreException
	 */
	public List<IVdmSourceUnit> getFiles(IVdmProject project,
			IResource resource, IContentType contentTypeId)
			throws CoreException
	{
		List<IVdmSourceUnit> list = new ArrayList<IVdmSourceUnit>();

		if (resource instanceof IFolder)
		{
			if (resource.getLocation().lastSegment().startsWith("."))// skip
				return list;
			// . folders like.svn
			for (IResource res : ((IFolder) resource).members(IContainer.INCLUDE_PHANTOMS
					| IContainer.INCLUDE_TEAM_PRIVATE_MEMBERS))
			{

				list.addAll(getFiles(project, res, contentTypeId));
			}
		}
		// check if it is a IFile and that there exists a known content type for
		// this file and the project
		else if (resource instanceof IFile)
		{
			// if (contentTypeId != null
			// && contentTypeId.isAssociatedWith(resource.toString()) &&
			// !resource.getName().startsWith("~"))
			if (project.isModelFile((IFile) resource))
			{
				list.add(getVdmSourceUnit((IFile) resource));
			}
		}
		return list;
	}

	public boolean hasProject(IProject project)
	{
		return projects.containsKey(project.getName());
	}

	public IVdmProject getProject(IProject project)
	{
		return projects.get(project.getName());
	}

	public synchronized IVdmProject addProject(IVdmProject project)
	{

		if (projects.containsKey(project.getName()))
			return projects.get(project.getName());
		else
		{

			IVdmProject p = getLeastAccessed();
			if (p != null)
			{
				lastAccessed.remove(p);
				lastAccessed.add(project);
			}

			try
			{
				projects.put(project.getName(), project);
				VdmModelManager.getInstance().createModel(project);
				// System.out.println("Creating project: " + project.getName());
				project.getSpecFiles();
				return project;
			} catch (CoreException e)
			{
				if (VdmCore.DEBUG)
				{
					e.printStackTrace();
				}
				return null;
			}
		}
	}

	private IVdmProject getLeastAccessed()
	{

		return lastAccessed.poll();

	}

	public void resourceChanged(IResourceChangeEvent event)
	{

		try
		{
			IResource res = event.getResource();
			switch (event.getType())
			{
				case IResourceChangeEvent.PRE_DELETE:
				case IResourceChangeEvent.PRE_CLOSE:
					remove(res);
					break;
				case IResourceChangeEvent.PRE_BUILD:

					break;
				case IResourceChangeEvent.POST_CHANGE:

					event.getDelta().accept(new DeltaPrinter());
					break;

				default:
					break;
			}

			// rebuilds projects added to the projectsToRebuild
			// during the exploration of the delta
			rebuildProjects();
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private synchronized void remove(IResource res)
	{
		// VDM Project is closing
		if (res instanceof IProject && hasProject((IProject) res))
		{
			if (projects.containsKey(res.getName()))
			{
				projects.remove(res.getName());
			}
		} else if (res instanceof IFile)
		{
			if (vdmSourceUnits.containsKey(res))
			{

				final IVdmProject vdmProject = vdmSourceUnits.get(res).getProject();
				vdmProject.getModel().remove(vdmSourceUnits.get(res));
				vdmSourceUnits.remove(res);

				IProject p = res.getProject();
				synchronized (projectsToRebuild)
				{
					if (!projectsToRebuild.contains(p))
					{
						projectsToRebuild.add(p);
					}
				}
			}

		} else if (res instanceof IFolder)
		{
			// no special action
		} else
		{
//			System.err.println("Resource not handled in remove: " + res);
		}

	}

	class DeltaPrinter implements IResourceDeltaVisitor
	{
		public boolean visit(IResourceDelta delta)
		{
			IResource res = delta.getResource();
			switch (delta.getKind())
			{
				case IResourceDelta.ADDED:
					// System.out.print("Resource ");
					// System.out.print(res.getFullPath());
					// System.out.println(" was added.");
					add(res);
					break;
				case IResourceDelta.REMOVED:
					remove(res);
//					if (res instanceof IFile)
//					{
//						if (isProjectBuildConttent((IFile) res))
//						{
//							IProject p = res.getProject();
//							synchronized (projectsToRebuild)
//							{
//								if (!projectsToRebuild.contains(p))
//								{
//									projectsToRebuild.add(p);
//								}
//							}
//						}
//
//					}

					break;
				case IResourceDelta.CHANGED:
					try
					{
						for (IResourceDelta resourceDelta : delta.getAffectedChildren())
						{
							resourceDelta.accept(new DeltaPrinter());
						}

					} catch (CoreException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
			}

			return true; // visit the children
		}

		private void add(final IResource res)
		{
			if (res instanceof IProject
					&& VdmProject.isVdmProject((IProject) res))
			{
				addBuilderProject((IProject) res);// , false);

			} else if (res instanceof IFile)
			{
				IFile file = (IFile) res;
				
				//FIXME use new method 	isProjectBuildConttent(file)
				if (VdmProject.isVdmProject(file.getProject()))
				{
					// Add the VDM builder to the project if missing and the
					// project have the correct nature
					addBuilderProject(res.getProject());
					// Call getVdmSourceUnit to associate the the IFile to the
					// project if the project contains the content type

					IVdmProject project = (IVdmProject) res.getProject().getAdapter(IVdmProject.class);
					Assert.isNotNull(project, "Project in ResourceManager is null for file: "
							+ file);
					IContentType contentTypeId = null;
					try
					{
						if (file.getContentDescription() != null)
						{
							contentTypeId = file.getContentDescription().getContentType();
						}
					} catch (CoreException e)
					{

					}
					if (project.getContentTypeIds().contains(contentTypeId))
					{
						getVdmSourceUnit(file);
					}
				}
			}

		}
	}


	private List<IProject> addBuilders = new Vector<IProject>();
	private boolean addBuilderThreadRunning = false;

	private synchronized IProject getAddBuidlerProject()
	{
		if (addBuilders.size() > 0)
		{
			IProject p = addBuilders.get(0);
			addBuilders.remove(p);
			return p;
		}
		return null;
	}

	private synchronized void addBuilderProject(IProject project)
	{
		if (!addBuilders.contains(project))
		{
			addBuilders.add(project);
			if (!addBuilderThreadRunning)
			{

				System.out.println("starting add builder thread");
				new AddBuilderThread().start();
				addBuilderThreadRunning = true;
			}
		}
	}

	/**
	 * Sync existing IVdmSource files with the build path of the project. This is used when the build path changed and
	 * the project should be updated. This method removed old IVdmSource files which no longer is withing the build
	 * path.
	 * 
	 * @param project
	 * @throws CoreException
	 */
	public synchronized void syncBuildPath(IVdmProject project)
			throws CoreException
	{
		List<IVdmSourceUnit> syncedVdmSourceUnits = project.getSpecFiles();

		List<IFile> removedFiles = new ArrayList<IFile>();
		IProject p = (IProject) project.getAdapter(IProject.class);

		for (IFile file : vdmSourceUnits.keySet())
		{
			if (file.getProject().equals(p)
					&& !syncedVdmSourceUnits.contains(vdmSourceUnits.get(file)))
			{
				removedFiles.add(file);
				System.out.println("Found an existing file removed from build path: "
						+ file);
			}
		}

		// remove the old files
		for (IFile iFile : removedFiles)
		{
			remove(iFile);
		}
	}

	private class AddBuilderThread extends Thread
	{
		@Override
		public void run()
		{

			try
			{
				while (ResourcesPlugin.getWorkspace().isTreeLocked())
				{
					try
					{
						Thread.sleep(500);
					} catch (InterruptedException e)
					{
					}
				}
				ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable()
				{

					public void run(IProgressMonitor monitor)
							throws CoreException
					{

						IProject p = null;
						while (addBuilders.size() > 0)
						{
							p = getAddBuidlerProject();
							if (p == null)
							{
								addBuilderThreadRunning = false;
								System.out.println("ended add builder thread");
								return;
							}

							System.out.println("Adding builder for: " + p);
							IVdmProject project = VdmProject.createProject(p);
							Assert.isNotNull(project, "VDM Project creation faild for project: "
									+ p);
							try
							{
								if (!project.hasBuilder())
								{
									project.setBuilder(project.getLanguageVersion());
								}
								project.getSpecFiles();// sync with content type
								// files
							} catch (CoreException e1)
							{
								e1.printStackTrace();
							}

						}
						System.out.println("DONE adding");
					}
				}, null);
			} catch (CoreException e)
			{
				VdmCore.log("Error in ResourceManager: AddBuilderThread", e);
			}
		}
	}

	private void rebuildProjects()
	{
		synchronized (projectsToRebuild)
		{
			for (int i = 0; i < projectsToRebuild.size(); i++)
			{
				IProject p = projectsToRebuild.get(i);
				IVdmProject vdmProject = (IVdmProject) p.getAdapter(IVdmProject.class);
				if (vdmProject != null)
				{
					scheduleBuilder(vdmProject);
				}
			}
			projectsToRebuild.clear();
		}
	}

	private void scheduleBuilder(IVdmProject project)
	{
		final IVdmProject vdmProject = project;
		Job job = new Job("Refresh resources")
		{

			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				try
				{
					vdmProject.getModel().setIsTypeChecked(false);
					vdmProject.typeCheck(true, monitor);
				} catch (CoreException e)
				{
					VdmCore.log("Faild to auto build project", e);
				}
				return Status.OK_STATUS;
			}
		};

		job.setRule(ResourcesPlugin.getWorkspace().getRuleFactory().buildRule());
		job.schedule();
	}

}
