package org.overture.ide.core.parser;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.overture.ide.core.ICoreConstants;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.IVdmProject;
import org.overture.ide.core.IVdmSourceUnit;
import org.overture.ide.core.VdmCore;
import org.overture.ide.internal.core.ast.VdmModelManager;

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

	/**
	 * Loads a source parser for the given project
	 * @param project the project to load the source parser for
	 * @return a valid source parser for the current project based on the highest priority parser available from the nature, or null if no parser could be found
	 * @throws CoreException
	 */
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

	/**
	 * Gets the parser available for the given nature having the highest priority
	 * @param natureId the nature to lookup a parser for
	 * @param config the configuration of the extension point
	 * @return a valid source parser or null
	 */
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
		List<IVdmSourceUnit> files = project.getSpecFiles();
		for (IVdmSourceUnit file : files)
		{
			IVdmModel model = file.getProject().getModel();
			if (model != null && model.hasFile(file))
				return;
			parseFile( file);
		}

	}

	/**
	 * Parse a single file from a project
	 * @param project the project where the file originates from
	 * @param file the file to be parsed
	 * @throws CoreException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static void parseFile(
			final IVdmSourceUnit file) throws CoreException, IOException
	{
		

//		IVdmModel model = file.getProject().getModel();
//		if (model != null && model.hasFile(file))
//			return;

		try
		{
			SourceParserManager.getInstance()
					.getSourceParser(file.getProject())
					.parse(file);
		} catch (Exception e)
		{
			if (VdmCore.DEBUG)
			{
				System.err.println("Error in parseFile");
				e.printStackTrace();
			}
		}
	}
}
