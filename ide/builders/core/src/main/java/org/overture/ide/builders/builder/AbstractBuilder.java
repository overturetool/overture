package org.overture.ide.builders.builder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.ast.parser.SourceParserManager;
import org.overture.ide.core.ast.AstManager;
import org.overture.ide.core.ast.RootNode;
import org.overture.ide.utility.FileUtility;
import org.overture.ide.utility.IVdmProject;
import org.overture.ide.utility.ProjectUtility;

public abstract class AbstractBuilder {
	public abstract IStatus buileModelElements(IVdmProject project,
			RootNode rooList);

	public abstract String getNatureId();

	public abstract String getContentTypeId();

	public static IFile findIFile(IProject project, File file) {
		return ProjectUtility.findIFile(project, file);
	}

	/***
	 * This method syncs the project resources. It is called before an instance
	 * of the AbstractBuilder is created
	 * 
	 * @param project
	 *            The project which should be synced
	 */
	public static void syncProjectResources(IProject project) {
		if (!project.isSynchronized(IResource.DEPTH_INFINITE))
			try {
				project.refreshLocal(IResource.DEPTH_INFINITE, null);

			} catch (CoreException e1) {

				e1.printStackTrace();
			}
	}

	/***
	 * This method removed all problem markers and its sub-types from the
	 * project. It is called before an instance of the AbstractBuilder is
	 * created
	 * 
	 * @param project
	 *            The project which should be build.
	 */
	public static void clearProblemMarkers(IProject project) {
		try {
			project.deleteMarkers(IMarker.PROBLEM, true,
					IResource.DEPTH_INFINITE);

		} catch (CoreException e) {

			e.printStackTrace();
		}

	}

	/***
	 * Parses files in a project which has a content type and sould be parsed
	 * before a build could be performed
	 * 
	 * @param project
	 *            the project
	 * @param natureId
	 *            the nature if which is used by the parser, it is used to store
	 *            the ast and look up if a file needs parsing
	 * @param monitor
	 *            an optional monitor, can be null
	 * @throws CoreException
	 * @throws IOException
	 */
	public static void parseMissingFiles(IProject project, String natureId,
			String contentTypeId, IProgressMonitor monitor)
			throws CoreException, IOException {
		if (monitor != null)
			monitor.subTask("Parsing files");
		AbstractBuilder.syncProjectResources(project);
	
		List<IFile> files = ProjectUtility.getFiles(project, contentTypeId);
		for (IFile iFile : files) {
			parseFile(project, natureId, iFile);
		}

	}


	public static void parseFile(IProject project, String natureId,
			final IFile file) throws CoreException, IOException {
		Path path = new Path(file.getProject().getFullPath().addTrailingSeparator().toString()
				+ file.getProjectRelativePath().toString());
		File fileSystemFile = project.getFile(path.removeFirstSegments(1)).getLocation().toFile();

		RootNode rootNode = AstManager.instance().getRootNode(project, natureId);
		if (rootNode != null && rootNode.hasFile(fileSystemFile))
			return;

		List<Character> content = FileUtility.getContent(file);

		
		char[] source =FileUtility.getCharContent(content);
		
		try {
			SourceParserManager.getInstance().getSourceParser(project, natureId).parse(
					path.toString().toCharArray(), source, null);//We can parse null as reporter since the VDMJ parser does not use it.
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
