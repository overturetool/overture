package org.overture.codegen.traces;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.traces.ATraceDefinitionTerm;
import org.overture.ast.definitions.traces.PTraceDefinition;
import org.overture.codegen.ir.STermIR;
import org.overture.codegen.ir.STraceDeclIR;
import org.overture.codegen.ir.traces.ATraceDeclTermIR;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.visitor.AbstractVisitorIR;

public class TermVisitorIR extends AbstractVisitorIR<IRInfo, STermIR>
{
	@Override
	public STermIR caseATraceDefinitionTerm(ATraceDefinitionTerm node,
			IRInfo question) throws AnalysisException
	{
		ATraceDeclTermIR termCg = new ATraceDeclTermIR();

		for(PTraceDefinition traceDef : node.getList())
		{
			STraceDeclIR traceDefCg = traceDef.apply(question.getTraceDeclVisitor(), question);
			
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
