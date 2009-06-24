package org.overturetool.eclipse.plugins.editor.core.internal.parser;

import org.eclipse.dltk.ast.parser.ISourceParser;
import org.eclipse.dltk.ast.parser.ISourceParserFactory;

public class OvertureSourceParserFactory implements ISourceParserFactory {

//	public static enum ToolType {VDMTools, VDMJ, Overture};
//	public static enum Dialect { VDM_PP, VDM_SL, VDM_RT }
	
	public ISourceParser createSourceParser() {
	
		return new OvertureSourceParser();
	}

}
