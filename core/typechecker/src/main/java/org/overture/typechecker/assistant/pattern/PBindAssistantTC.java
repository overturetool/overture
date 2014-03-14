package org.overture.typechecker.assistant.pattern;

import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.patterns.PBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class PBindAssistantTC
{
	protected ITypeCheckerAssistantFactory af;

	public PBindAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public List<PMultipleBind> getMultipleBindList(PBind bind)
	{
		try
		{
			return bind.apply(af.getMultipleBindLister());
		} catch (AnalysisException e)
		{
			return null;
		}

	}

}
