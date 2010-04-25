package org.overture.ide.internal.core;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
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
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.content.IContentType;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.VdmCore;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.core.resources.VdmProject;
import org.overture.ide.core.resources.VdmSourceUnit;
import org.overture.ide.internal.core.ast.VdmModelManager;

public class ResourceManager implements IResourceChangeListener
{

	Map<String, IVdmProject> projects = new Hashtable<String, IVdmProject>();
	Map<IFile, IVdmSourceUnit> vdmSourceUnits = new Hashtable<IFile, IVdmSourceUnit>();

	/**
	 * A handle to the unique Singleton instance.
	 */
	static private ResourceManager _instance = null;

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
					if (file.getContentDescription()!=null && project.getContentTypeIds().contains(file.getContentDescription().getContentType().getId()))
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
			if (resource instanceof IFolder
					&& resource.getLocation().lastSegment().startsWith("."))// skip
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

	public void resourceChanged(IResourceChangeEvent event)
	{
		try
		{
			IResource res = event.getResource();
			switch (event.getType())
			{
				case IResourceChangeEvent.PRE_DELETE:
				case IResourceChangeEvent.PRE_CLOSE:
					if (event.getResource() instanceof IProject)
					{
						// VDM Project is closing
						if (hasProject((IProject) res))
						{
							remove(res);
						}
					}
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
		if (res instanceof IProject)
		{
			if (projects.containsKey(res.getName()))
			{
				projects.remove(res.getName());
			}
		} else if (res instanceof IFile)
		{
			if (vdmSourceUnits.containsKey(res))
			{
				vdmSourceUnits.remove(res);
			}

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

		private void add(IResource res)
		{
			if (res instanceof IProject
					&& VdmProject.isVdmProject((IProject) res))
			{
				Assert.isNotNull(VdmProject.createProject((IProject) res), "VDM Project creation faild for project: "
						+ res);
			} else if (res instanceof IFile)
			{
				IFile file = (IFile) res;
				if (VdmProject.isVdmProject(file.getProject()))
				{
					IVdmProject project = VdmProject.createProject(file.getProject());
					Assert.isNotNull(project, "Project creation faild for file: "
							+ res);

					try
					{
						project.getSpecFiles();// sync with content type files
					} catch (CoreException e)
					{
						if (VdmCore.DEBUG)
						{
							e.printStackTrace();
						}
					}
					// IVdmSourceUnit unit = createSourceUnit(file, project);
					// Assert.isNotNull(unit, "Source unit creation faild for: "
					// + res);
				}
			}

		}
	}
}
