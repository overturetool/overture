/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
//package org.overture.ide.core.utility;
//
//import java.io.File;
//import java.net.URI;
//import java.util.List;
//import java.util.Vector;
//
//import org.eclipse.core.resources.IContainer;
//import org.eclipse.core.resources.IFile;
//import org.eclipse.core.resources.IFolder;
//import org.eclipse.core.resources.IProject;
//import org.eclipse.core.resources.IProjectDescription;
//import org.eclipse.core.resources.IResource;
//import org.eclipse.core.resources.IWorkspace;
//import org.eclipse.core.resources.IWorkspaceRoot;
//import org.eclipse.core.resources.ResourcesPlugin;
//import org.eclipse.core.runtime.Assert;
//import org.eclipse.core.runtime.CoreException;
//import org.eclipse.core.runtime.IPath;
//import org.eclipse.core.runtime.IProgressMonitor;
//import org.eclipse.core.runtime.Path;
//import org.eclipse.core.runtime.content.IContentType;
//import org.overture.ide.core.ast.NotAllowedException;
//
//public class ProjectUtility
//{
//
////	/***
////	 * Get files from a eclipse project
////	 * 
////	 * @param project
////	 *            the project to scan
////	 * @param contentTypeId
////	 *            of the type of files that should be returned
////	 * @return a list of IFile
////	 * @throws CoreException
////	 */
////	public static List<IFile> getFiles(IProject project, String contentTypeId)
////			throws CoreException
////	{
////		List<IFile> list = new Vector<IFile>();
////		for (IResource res : project.members(IContainer.INCLUDE_PHANTOMS
////				| IContainer.INCLUDE_TEAM_PRIVATE_MEMBERS))
////		{
////			list.addAll(getFiles(project, res, contentTypeId));
////		}
////		return list;
////	}
////
////	/***
////	 * Get files from a eclipse project that has a defined content type
////	 * 
////	 * @param project
////	 *            the project to scan
////	 * @return a list of IFile
////	 * @throws CoreException
////	 */
////	public static List<IFile> getFiles(IProject project) throws CoreException
////	{
////		List<IFile> list = new Vector<IFile>();
////		for (IResource res : project.members(IContainer.INCLUDE_PHANTOMS
////				| IContainer.INCLUDE_TEAM_PRIVATE_MEMBERS))
////		{
////			list.addAll(getFiles(project, res, null));
////		}
////		return list;
////	}
//
//	
//	/***
//	 * Gets the IFile from the Eclipse filesystem from a normal file placed in a
//	 * project
//	 * 
//	 * @param project
//	 *            the project which holds the file
//	 * @param file
//	 *            the File to look up
//	 * @return a new IFile representing the file in the eclipse filesystem
//	 */
//	public static IFile findIFile(IProject project, File file)
//	{
//		IWorkspace workspace = ResourcesPlugin.getWorkspace();
//		IPath location = Path.fromOSString(file.getAbsolutePath());
//		IFile ifile = workspace.getRoot().getFileForLocation(location);
//
//		if (ifile == null)
//		{
//
//			IPath absolutePath = new Path(file.getAbsolutePath());
//			// check if the project contains a IFile which maps to the same
//			// filesystem location
//			try
//			{
//				for (IFile f : getFiles(project))
//				{
//					if (f.getLocation().equals(absolutePath))
//						return f;
//				}
//			} catch (CoreException e1)
//			{
//			}
//
//			// project does not contain this file, this means that the file has
//			// been include elsewhere and a link will be created to the file
//			// instead.
//			try
//			{
//				linkFileToProject(project, file);
//			} catch (CoreException e)
//			{
//			}
//
//		}
//		return ifile;
//	}
//
//	public static void linkFileToProject(IProject project, File file)
//			throws CoreException
//	{
//		IPath absolutePath = new Path(file.getAbsolutePath());
//		IFile ifile = project.getFile(absolutePath.lastSegment());
//		ifile.createLink(absolutePath, IResource.NONE, null);
//	}
//
//	public static File getFile(IProject project, IPath path)
//	{
//		return project.getFile(path.removeFirstSegments(1))
//				.getLocation()
//				.toFile();
//	}
//
//	public static File getFile(IWorkspaceRoot wroot, IPath path)
//	{
//		return wroot.getFile(path.removeFirstSegments(1))
//				.getLocation()
//				.toFile();
//	}
//
//	public static File getFile(IProject project, IFile file)
//	{
//		Path path = new Path(project.getFullPath()
//				.addTrailingSeparator()
//				.toString()
//				+ file.getProjectRelativePath().toString());
//		return getFile(project, path);
//	}
//
//	
//
//
//	
//
//	
//}
