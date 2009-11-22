package org.overture.ide.parsers.vdmj.sourceparsers;


import org.eclipse.dltk.ast.parser.ISourceParser;
import org.eclipse.dltk.ast.parser.ISourceParserFactory;
import org.overture.ide.parsers.vdmj.internal.VdmRtParser;
import org.overture.ide.vdmrt.core.VdmRtProjectNature;

public class VdmRtSourceParserFactory implements ISourceParserFactory
{

	public ISourceParser createSourceParser()
	{
		return new VdmRtParser(VdmRtProjectNature.VDM_RT_NATURE);
	}

}
