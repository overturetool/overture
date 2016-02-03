package org.overture.codegen.traces;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.traces.ATraceDefinitionTerm;
import org.overture.ast.definitions.traces.PTraceDefinition;
import org.overture.codegen.ir.STermCG;
import org.overture.codegen.ir.STraceDeclCG;
import org.overture.codegen.ir.traces.ATraceDeclTermCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.visitor.AbstractVisitorCG;

public class TermVisitorCG extends AbstractVisitorCG<IRInfo, STermCG>
{
	@Override
	public STermCG caseATraceDefinitionTerm(ATraceDefinitionTerm node,
			IRInfo question) throws AnalysisException
	{
		ATraceDeclTermCG termCg = new ATraceDeclTermCG();

		for(PTraceDefinition traceDef : node.getList())
		{
			STraceDeclCG traceDefCg = traceDef.apply(question.getTraceDeclVisitor(), question);
			
			if(traceDefCg != null)
			{
				termCg.getTraceDecls().add(traceDefCg);
			}
			else
			{
				return null;
			}
		}
		
		return termCg;
	}
}
