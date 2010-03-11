package org.overture.ide.core.parser;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.overture.ide.core.Activator;
import org.overture.ide.core.ICoreConstants;
import org.overture.ide.core.ast.AstManager;
import org.overture.ide.core.ast.RootNode;
import org.overture.ide.core.utility.IVdmProject;

public class SourceParserManager {
	/**
	 * A handle to the unique Singleton instance.
	 */
	static private SourceParserManager _instance = null;

	/**
	 * @return The unique instance of this class.
	 */
	static public SourceParserManager getInstance() {
		if (null == _instance) {
			_instance = new SourceParserManager();
		}
		return _instance;
	}

	public ISourceParser getSourceParser(IVdmProject project)
			throws CoreException {
		IConfigurationElement[] config = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(
						ICoreConstants.EXTENSION_PARSER_ID);

		IConfigurationElement selectedParser = getParserWithHeighestPriority(
				project.getVdmNature(), config);
		
		if (selectedParser != null) {
			final Object o = selectedParser.createExecutableExtension("class");

			if (o instanceof AbstractParserParticipant) {
				AbstractParserParticipant parser = (AbstractParserParticipant) o;

				parser.setProject(project);
				
				return parser;
			}

		}
		return null;

	}

	private IConfigurationElement getParserWithHeighestPriority(
			String natureId, IConfigurationElement[] config) {
		IConfigurationElement selectedParser = null;
		int selectedParserPriority = 0;
		for (IConfigurationElement e : config) {
			if (e.getAttribute("nature").equals(natureId)) {
				if (selectedParser == null) {
					selectedParser = e;
					String selectedParserPriorityString = selectedParser
							.getAttribute("priority");
					if (selectedParserPriorityString != null)
						selectedParserPriority = Integer
								.parseInt(selectedParserPriorityString);
				} else {
					String parserPriorityString = selectedParser
							.getAttribute("priority");
					if (parserPriorityString != null) {
						int parserPriority = Integer
								.parseInt(parserPriorityString);
						if (parserPriority > selectedParserPriority) {
							selectedParser = e;
							selectedParserPriority = parserPriority;
						}
					}
				}
			}
		}
		return selectedParser;
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
	public static void parseMissingFiles(IVdmProject project, IProgressMonitor monitor)
			throws CoreException, IOException
	{
		if (monitor != null)
		{
			monitor.subTask("Parsing files");
		}
		if (!project.isSynchronized(IResource.DEPTH_INFINITE))
		{
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		}
		List<IFile> files = project.getSpecFiles();
		for (IFile iFile : files)
		{
			parseFile(project, iFile);
		}

	}

	@SuppressWarnings("unchecked")
	public static void parseFile(IVdmProject project,
			final IFile file) throws CoreException, IOException
	{
		

		RootNode rootNode = AstManager.instance()
				.getRootNode(project);
		if (rootNode != null && rootNode.hasFile(project.getFile(file)))
			return;

		try
		{
			SourceParserManager.getInstance()
					.getSourceParser(project)
					.parse(file);
		} catch (Exception e)
		{
			if (Activator.DEBUG)
			{
				System.err.println("Error in parseFile");
				e.printStackTrace();
			}
		}
	}
}
