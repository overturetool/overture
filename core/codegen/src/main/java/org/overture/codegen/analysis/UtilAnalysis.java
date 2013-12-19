package org.overture.codegen.analysis;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.constants.OoAstConstants;

public class UtilAnalysis extends AbstractAnalysis
{
	@Override
	public void defaultInINode(INode node) throws AnalysisException
	{
		if(node instanceof AClassTypeCG)
		{
			AClassTypeCG classType = (AClassTypeCG) node;
			
			String className = classType.getName();
			
			for(int i = 0; i < OoAstConstants.UTIL_NAMES.length; i++)
				if(className.equals(OoAstConstants.UTIL_NAMES[i]))
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
	}
}
