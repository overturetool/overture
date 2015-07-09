package org.overture.pog.tests.newtests;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.expressions.PExp;
import org.overture.ast.types.AFunctionType;

public class TypeIntegrityVisitor extends DepthFirstAnalysisAdaptor
{

	List<PExp> untypeExps = new Vector<PExp>();

	@Override
	public void defaultInPExp(PExp node) throws AnalysisException
	{
		if (node.getType() == null)
		{
			untypeExps.add(node);
		}
	}

	public List<PExp> getUntypeExps()
	{
		return untypeExps;
	}

	public void reset()
	{
		untypeExps.clear();
	}
	
	@Override
	public void caseAFunctionType(AFunctionType node) throws AnalysisException
	{
		// do nothing
	}

}
