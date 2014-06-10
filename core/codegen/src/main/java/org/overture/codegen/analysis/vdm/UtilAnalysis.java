package org.overture.codegen.analysis.vdm;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.expressions.ATupleExpCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.ATupleTypeCG;
import org.overture.codegen.constants.IRConstants;

public class UtilAnalysis extends AbstractAnalysis
{
	@Override
	public void defaultInINode(INode node) throws AnalysisException
	{
		if(node instanceof AClassTypeCG)
		{
			AClassTypeCG classType = (AClassTypeCG) node;
			
			String className = classType.getName();
			
			for(int i = 0; i < IRConstants.UTIL_NAMES.length; i++)
				if(className.equals(IRConstants.UTIL_NAMES[i]))
				{
					setFound();
					throw new AnalysisException();
				}
		}
		else if(node instanceof ARecordDeclCG)
		{
			setFound();
			throw new AnalysisException();
		}
		else if(node instanceof ATupleTypeCG || node instanceof ATupleExpCG)
		{
			setFound();
			throw new AnalysisException();
		}
	}
}
