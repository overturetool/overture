package org.overture.codegen.traces;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.traces.ATraceDefinitionTerm;
import org.overture.ast.definitions.traces.PTraceDefinition;
import org.overture.codegen.cgast.STermCG;
import org.overture.codegen.cgast.STraceDeclCG;
import org.overture.codegen.cgast.traces.ATraceDeclTermCG;
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
			termCg.getTraceDecls().add(traceDefCg);
		}
		
		return termCg;
	}
}
