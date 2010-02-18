package org.overture.ide.core.utility;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.content.IContentType;

public class ProjectUtility
{

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
	public static List<IFile> getFiles(IProject project, String contentTypeId)
			throws CoreException
	{
		List<IFile> list = new Vector<IFile>();
		for (IResource res : project.members(IContainer.INCLUDE_PHANTOMS
				| IContainer.INCLUDE_TEAM_PRIVATE_MEMBERS))
		{
			list.addAll(getFiles(project, res, contentTypeId));
		}
		return list;
	}

	/***
	 * Get files from a eclipse project that has a defined content type
	 * 
	 * @param project
	 *            the project to scan
	 * @return a list of IFile
	 * @throws CoreException
	 */
	public static List<IFile> getFiles(IProject project) throws CoreException
	{
		List<IFile> list = new Vector<IFile>();
		for (IResource res : project.members(IContainer.INCLUDE_PHANTOMS
				| IContainer.INCLUDE_TEAM_PRIVATE_MEMBERS))
		{
			list.addAll(getFiles(project, res, null));
		}
		return list;
	}

	/***
	 * Recursive search of a project for files based on the content type
	 * 
	 * @param project
	 *            the project to search
	 * @param resource
	 *            the resource currently selected to be searched
	 * @param contentTypeId
	 *            a possibly null content type id, if null it is just checked
	 *            that a content type exist for the file
	 * @return a list of IFiles
	 * @throws CoreException
	 */
	private static List<IFile> getFiles(IProject project, IResource resource,
			String contentTypeId) throws CoreException
	{
		List<IFile> list = new Vector<IFile>();

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
			IContentType contentType = project.getContentTypeMatcher()
					.findContentTypeFor(resource.toString());

			if (contentType != null
					&& ((contentTypeId != null && contentTypeId.equals(contentType.getId())) || contentTypeId == null))
				list.add((IFile) resource);
		}
		return list;
	}

	/***
	 * Gets the IFile from the Eclipse filesystem from a normal file placed in a
	 * project
	 * 
	 * @param project
	 *            the project which holds the file
	 * @param file
	 *            the File to look up
	 * @return a new IFile representing the file in the eclipse filesystem
	 */
	public static IFile findIFile(IProject project, File file)
	{
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IPath location = Path.fromOSString(file.getAbsolutePath());
		IFile ifile = workspace.getRoot().getFileForLocation(location);

		if (ifile == null)
		{

			IPath absolutePath = new Path(file.getAbsolutePath());
			// check if the project contains a IFile which maps to the same
			// filesystem location
			try
			{
				for (IFile f : getFiles(project))
				{
					if (f.getLocation().equals(absolutePath))
						return f;
				}
			} catch (CoreException e1)
			{
			}

			// project does not contain this file, this means that the file has
			// been include elsewhere and a link will be created to the file
			// instead.
			try
			{
				linkFileToProject(project, file);
			} catch (CoreException e)
			{
			}

		}
		return ifile;
	}

	public static void linkFileToProject(IProject project, File file)
			throws CoreException
	{
		IPath absolutePath = new Path(file.getAbsolutePath());
		IFile ifile = project.getFile(absolutePath.lastSegment());
		ifile.createLink(absolutePath, IResource.NONE, null);
	}

	public static File getFile(IProject project, IPath path)
	{
		return project.getFile(path.removeFirstSegments(1))
				.getLocation()
				.toFile();
	}

	public static File getFile(IWorkspaceRoot wroot, IPath path)
	{
		return wroot.getFile(path.removeFirstSegments(1))
				.getLocation()
				.toFile();
	}

	public static File getFile(IProject project, IFile file)
	{
		Path path = new Path(project.getFullPath()
				.addTrailingSeparator()
				.toString()
				+ file.getProjectRelativePath().toString());
		return getFile(project, path);
	}

	public static IProject createProject(String projectName, URI location, String nature) {
        Assert.isNotNull(projectName);
        Assert.isTrue(projectName.trim().length() > 0);

        IProject project = createBaseProject(projectName, location);
        try {
            addNature(project,nature);
            String[] paths = { }; //$NON-NLS-1$ //$NON-NLS-2$
            addToProjectStructure(project, paths);
        } catch (CoreException e) {
            e.printStackTrace();
            project = null;
        }
        return project;
    }

	private static IProject createBaseProject(String projectName, URI location) {
        // it is acceptable to use the ResourcesPlugin class
        IProject newProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

        if (!newProject.exists()) {
            URI projectLocation = location;
            IProjectDescription desc = newProject.getWorkspace().newProjectDescription(newProject.getName());
            if (location != null && ResourcesPlugin.getWorkspace().getRoot().getLocationURI().equals(location)) {
                projectLocation = null;
            }

            desc.setLocationURI(projectLocation);
            try {
                newProject.create(desc, null);
                if (!newProject.isOpen()) {
                    newProject.open(null);
                }
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }

        return newProject;
    }

	private static void createFolder(IFolder folder) throws CoreException {
        IContainer parent = folder.getParent();
        if (parent instanceof IFolder) {
            createFolder((IFolder) parent);
        }
        if (!folder.exists()) {
            folder.create(false, true, null);
        }
    }
	
	private static void addToProjectStructure(IProject newProject, String[] paths) throws CoreException {
        for (String path : paths) {
            IFolder etcFolders = newProject.getFolder(path);
            createFolder(etcFolders);
        }
    }

	private static void addNature(IProject project, String nature) throws CoreException {
        if (!project.hasNature(nature)) {
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

	

	
}
