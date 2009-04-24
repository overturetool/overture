package org.overturetool.eclipse.plugins.editor.core.internal.parser;

import org.eclipse.dltk.compiler.SourceElementRequestVisitor;
import org.eclipse.dltk.core.AbstractSourceElementParser;
import org.overturetool.eclipse.plugins.editor.core.OvertureNature;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.visitors.OvertureSourceElementRequestor;

public class OvertureSourceElementParser extends AbstractSourceElementParser {

	@Override
	protected String getNatureId() {
		return OvertureNature.NATURE_ID;
	}
	
	protected SourceElementRequestVisitor createVisitor() {
		return new OvertureSourceElementRequestor(getRequestor());
	}
}
