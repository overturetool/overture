package org.overture.ide.vdmpp.ui.internal.outline;


import org.eclipse.dltk.compiler.SourceElementRequestVisitor;
import org.eclipse.dltk.core.AbstractSourceElementParser;
import org.overture.ide.vdmpp.core.VdmPpProjectNature;

public class SourceElementParser extends AbstractSourceElementParser
{
	

	@Override
	protected String getNatureId()
	{
		return VdmPpProjectNature.VDM_PP_NATURE;
	}

	@Override
	protected SourceElementRequestVisitor createVisitor()
	{
		return new VdmPpSourceElementRequestor(getRequestor());
	}
	
	

}
