package org.overturetool.eclipse.plugins.editor.core.internal.parser;

import org.eclipse.dltk.ast.parser.ISourceParser;
import org.eclipse.dltk.ast.parser.ISourceParserFactory;

public class OvertureSourceParserFactory implements ISourceParserFactory {

	public static enum ToolType {VDMTools, VDMJ, Overture};
	public static enum Dialect { VDM_PP, VDM_SL, VDM_RT }
	
	public ISourceParser createSourceParser() {
		// TODO if overtureParser / VDMJ 
		// change the boolean to get project specific properties.
		
		ToolType toolType = ToolType.VDMJ;
		Dialect dialect = Dialect.VDM_PP;
		
		switch (toolType){
			case VDMTools:
				return new VDMToolsParser(dialect);
			case VDMJ:
				return new VDMJSourceParser(dialect);
			case Overture:
				break;
			default:
		}
		return null;
	}

}
