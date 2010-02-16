package org.overture.ide.core.parser;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import org.overture.ide.core.ICoreConstants;

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

	public ISourceParser getSourceParser(IProject project, String natureId)
			throws CoreException {
		IConfigurationElement[] config = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(
						ICoreConstants.EXTENSION_PARSER_ID);

		IConfigurationElement selectedParser = getParserWithHeighestPriority(
				natureId, config);
		
		if (selectedParser != null) {
			final Object o = selectedParser.createExecutableExtension("class");

			if (o instanceof AbstractParserParticipant) {
				AbstractParserParticipant parser = (AbstractParserParticipant) o;

				parser.setProject(project);
				parser.setNatureId(natureId);
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
}
