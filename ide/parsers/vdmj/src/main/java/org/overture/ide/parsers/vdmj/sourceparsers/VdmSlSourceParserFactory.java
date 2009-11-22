package org.overture.ide.parsers.vdmj.sourceparsers;


import org.eclipse.dltk.ast.parser.ISourceParser;
import org.eclipse.dltk.ast.parser.ISourceParserFactory;
import org.overture.ide.parsers.vdmj.internal.VdmSlParser;
import org.overture.ide.vdmsl.core.VdmSlProjectNature;

public class VdmSlSourceParserFactory implements ISourceParserFactory
{

	public ISourceParser createSourceParser()
	{
		return new VdmSlParser(VdmSlProjectNature.VDM_SL_NATURE);
	}

}
