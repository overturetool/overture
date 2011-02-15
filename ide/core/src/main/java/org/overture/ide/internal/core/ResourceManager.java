package org.overture.ide.internal.core;

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
import org.eclipse.core.runtime.content.IContentType;
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
					if (!file.isSynchronized(IResource.DEPTH_INFINITE))
					{
						file.refreshLocal(IResource.DEPTH_INFINITE, null);
					}
					//if (file.getContentDescription() != null
					//		&& project.getContentTypeIds().contains(file.getContentDescription().getContentType().getId()))
					if(project.isModelFile(file))
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
	public List<IVdmSourceUnit> getFiles(IProject project, IResource resource,
			String contentTypeId) throws CoreException
	{
		List<IVdmSourceUnit> list = new Vector<IVdmSourceUnit>();

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
			IContentType contentType = project.getContentTypeMatcher().findContentTypeFor(resource.toString());

			if (contentType != null
					&& ((contentTypeId != null && contentTypeId.equals(contentType.getId())) || contentTypeId == null))
				list.add(getVdmSourceUnit((IFile) resource));
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
				vdmSourceUnits.get(res).getProject().getModel().remove(vdmSourceUnits.get(res));
				vdmSourceUnits.remove(res);
			}

		} else if (res instanceof IFolder)
		{
			// no special action
		} else
		{
			System.err.println("Resource not handled in remove: " + res);
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
					// System.out.print("Resource ");
					// System.out.print(res.getFullPath());
					// System.out.println(" was removed.");
					remove(res);
					break;
				case IResourceDelta.CHANGED:
					// System.out.print("Resource ");
					// System.out.print(res.getFullPath());
					// System.out.println(" has changed.");
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
					String contentTypeId = null;
					try
					{
						if (file.getContentDescription() != null)
						{
							contentTypeId = file.getContentDescription().getContentType().getId();
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

		List<IFile> removedFiles = new Vector<IFile>();
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

}
