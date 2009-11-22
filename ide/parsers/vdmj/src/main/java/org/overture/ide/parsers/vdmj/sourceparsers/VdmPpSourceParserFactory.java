package org.overture.ide.parsers.vdmj.sourceparsers;


import org.eclipse.dltk.ast.parser.ISourceParser;
import org.eclipse.dltk.ast.parser.ISourceParserFactory;
import org.overture.ide.parsers.vdmj.internal.VdmPpParser;
import org.overture.ide.vdmpp.core.VdmPpProjectNature;

public class VdmPpSourceParserFactory implements ISourceParserFactory
{

	public ISourceParser createSourceParser()
	{
		return new VdmPpParser(VdmPpProjectNature.VDM_PP_NATURE);
	}

}
