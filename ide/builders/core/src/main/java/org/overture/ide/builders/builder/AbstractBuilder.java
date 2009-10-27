package org.overture.ide.builders.builder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.ast.parser.SourceParserManager;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.overture.ide.ast.AstManager;
import org.overture.ide.ast.RootNode;
import org.overture.ide.ast.dltk.DltkConverter;
import org.overture.ide.utility.ProjectUtility;

public abstract class AbstractBuilder {
	@SuppressWarnings("unchecked")
	public abstract IStatus buileModelElements(IProject project,
			List modelElements);

	public abstract String getNatureId();

	public static IFile findIFile(IProject project, File file) {
		return ProjectUtility.findIFile(project, file);
	}

	protected static void addMarker(IFile file, String message, int lineNumber,
			int severity, int charStart, int charEnd) throws CoreException {
		if (file != null) {
			IMarker marker = file.createMarker(IMarker.PROBLEM);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
			StringBuilder content = inputStreamToString(file.getContents());
			if (content != null) {
				DltkConverter converter = new DltkConverter(content.toString().toCharArray());
				marker.setAttribute(IMarker.CHAR_START, converter.convert(
						lineNumber, charStart));
				marker.setAttribute(IMarker.CHAR_END, converter.convert(
						lineNumber, charEnd));
			}
		} else
			System.out.println("Cannot set marker in missing file: " + file);
	}

	private static StringBuilder inputStreamToString(InputStream is) {
		StringBuilder out = new StringBuilder();
		Reader in = null;
		try {
			final char[] buffer = new char[0x10000];

			in = new InputStreamReader(is, "UTF-8");
			int read;
			do {
				read = in.read(buffer, 0, buffer.length);
				if (read > 0) {
					out.append(buffer, 0, read);
				}
			} while (read >= 0);
			return out;
		} catch (Exception e) {
			return null;
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
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
			IProgressMonitor monitor) throws CoreException, IOException {
		if (monitor != null)
			monitor.subTask("Parsing files");
		AbstractBuilder.syncProjectResources(project);
		for (IResource res : project.members(IContainer.INCLUDE_PHANTOMS
				| IContainer.INCLUDE_TEAM_PRIVATE_MEMBERS)) {
			parseMissingFiles(project, natureId, res);
		}

	}

	/***
	 * Try to parse a resource, by testing if it is a file with a known content
	 * type related to the project. Search sub folders.
	 * 
	 * @param project the project
	 * @param natureId the nature used by the builder
	 * @param resource the resource to parse
	 * @throws CoreException
	 * @throws IOException
	 */
	private static void parseMissingFiles(IProject project, String natureId,
			IResource resource) throws CoreException, IOException {
		if (resource instanceof IFolder)
		{
			if (resource instanceof IFolder
					&& resource.getLocation().lastSegment().startsWith("."))// skip
				return;
				// .
				// folders
				// like
				// .svn
			
			for (IResource res : ((IFolder) resource).members(IContainer.INCLUDE_PHANTOMS
					| IContainer.INCLUDE_TEAM_PRIVATE_MEMBERS)) {
				// System.out.println("Looking at file for parse: "
				// + res.getName());
				// System.out.println(project.getContentTypeMatcher().findContentTypeFor(
				// res.getFullPath().toString()));

				
					parseMissingFiles(project, natureId, res);
			}
		}
		// check if it is a IFile and that there exists a known content type for
		// this file and the project
		else if (resource instanceof IFile
				&& project.getContentTypeMatcher().findContentTypeFor(
						resource.toString()) != null)
		{
			//System.out.println("Parsing file: "+resource);
			parseMissingFiles(project, natureId, (IFile) resource);
		}

	}

	private static void parseMissingFiles(IProject project, String natureId,
			final IFile f) throws CoreException, IOException {

		Path path = new Path(f.getProject().getFullPath().addTrailingSeparator().toString()
				+ f.getProjectRelativePath().toString());
		File fileSystemFile = project.getFile(path.removeFirstSegments(1)).getLocation().toFile();
		RootNode rootNode = AstManager.instance().getRootNode(project, natureId);
		if (rootNode !=null && rootNode.hasFile(
						fileSystemFile))
			return;

		System.out.println("Trying to parse missing file: " + f.getName());
		InputStream inStream = f.getContents();
		List<Character> content = new Vector<Character>();
		int c = -1;
		while ((c = inStream.read()) != -1)
			content.add((char) c);

		IProblemReporter reporter = new IProblemReporter() {

			@SuppressWarnings("unchecked")
			public Object getAdapter(Class adapter) {

				return null;
			}

			public void reportProblem(IProblem problem) {
				int severity = IMarker.SEVERITY_ERROR;
				if (problem.isWarning())
					severity = IMarker.SEVERITY_WARNING;

				try {
					addMarker(f, problem.getMessage(),
							problem.getSourceLineNumber(), severity,
							problem.getSourceStart(), problem.getSourceEnd());
				} catch (CoreException e) {

					e.printStackTrace();
				}
				
//				System.out.println("refresh parser problem: " + problem.getID()
//						+ " " + problem.getMessage());
			}
		};
		char[] source = new char[content.size()];
		for (int i = 0; i < content.size(); i++) {
			source[i] = content.get(i);
		}
		try {
			SourceParserManager.getInstance().getSourceParser(project, natureId).parse(
					path.toString().toCharArray(), source, reporter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
