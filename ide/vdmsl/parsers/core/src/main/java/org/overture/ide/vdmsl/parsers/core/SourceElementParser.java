package org.overture.ide.vdmsl.parsers.core;

import org.eclipse.dltk.compiler.SourceElementRequestVisitor;
import org.eclipse.dltk.core.AbstractSourceElementParser;
import org.overture.ide.vdmsl.core.VdmSlProjectNature;

public class SourceElementParser extends AbstractSourceElementParser
{
	

	@Override
	protected String getNatureId()
	{
		return VdmSlProjectNature.VDM_SL_NATURE;
	}

	@Override
	protected SourceElementRequestVisitor createVisitor()
	{
		return new OvertureSourceElementRequestor(getRequestor());
	}
	
	

}
