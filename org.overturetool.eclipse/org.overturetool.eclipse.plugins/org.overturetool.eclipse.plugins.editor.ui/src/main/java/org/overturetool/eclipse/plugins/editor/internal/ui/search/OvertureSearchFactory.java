package org.overturetool.eclipse.plugins.editor.internal.ui.search;

import org.eclipse.dltk.core.search.AbstractSearchFactory;
import org.eclipse.dltk.core.search.IMatchLocatorParser;
import org.eclipse.dltk.core.search.matching.MatchLocator;

public class OvertureSearchFactory extends AbstractSearchFactory {

	/***
	 * Search factory class return MatchLocator base class
	 * @param locator  
	 */
	public IMatchLocatorParser createMatchParser(MatchLocator locator) {
		return new OvertureMatchLocationParser(locator);
	}

}
