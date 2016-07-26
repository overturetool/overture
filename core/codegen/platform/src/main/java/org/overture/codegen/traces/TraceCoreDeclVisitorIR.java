package org.overture.codegen.traces;

import org.apache.log4j.Logger;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.traces.AApplyExpressionTraceCoreDefinition;
import org.overture.ast.definitions.traces.ABracketedExpressionTraceCoreDefinition;
import org.overture.ast.definitions.traces.AConcurrentExpressionTraceCoreDefinition;
import org.overture.ast.definitions.traces.ATraceDefinitionTerm;
import org.overture.ast.definitions.traces.PTraceDefinition;
import org.overture.ast.statements.PStm;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STermIR;
import org.overture.codegen.ir.STraceCoreDeclIR;
import org.overture.codegen.ir.STraceDeclIR;
import org.overture.codegen.ir.traces.AApplyExpTraceCoreDeclIR;
import org.overture.codegen.ir.traces.ABracketedExpTraceCoreDeclIR;
import org.overture.codegen.ir.traces.AConcurrentExpTraceCoreDeclIR;
import org.overture.codegen.ir.traces.ATraceDeclTermIR;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.visitor.AbstractVisitorIR;

public class TraceCoreDeclVisitorIR extends AbstractVisitorIR<IRInfo, STraceCoreDeclIR>
{
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	@Override
	public STraceCoreDeclIR caseAApplyExpressionTraceCoreDefinition(
			AApplyExpressionTraceCoreDefinition node, IRInfo question)
			throws AnalysisException
	{
		PStm callStm = node.getCallStatement();
		SStmIR callStmCg = callStm.apply(question.getStmVisitor(), question);
		
		AApplyExpTraceCoreDeclIR applyTraceCoreDecl = new AApplyExpTraceCoreDeclIR();
		applyTraceCoreDecl.setCallStm(callStmCg);
		
		return applyTraceCoreDecl;
	}
	
	@Override
	public STraceCoreDeclIR caseABracketedExpressionTraceCoreDefinition(
			ABracketedExpressionTraceCoreDefinition node, IRInfo question)
			throws AnalysisException
	{
		ABracketedExpTraceCoreDeclIR bracketTraceCoreDecl = new ABracketedExpTraceCoreDeclIR();
		
		for(ATraceDefinitionTerm term : node.getTerms())
		{
			STermIR termCg = term.apply(question.getTermVisitor(), question);
			
			if(termCg instanceof ATraceDeclTermIR)
			{
				bracketTraceCoreDecl.getTerms().add((ATraceDeclTermIR) termCg);
			}
			else
			{
				log.error("Expected term to be of type ATraceDeclTermIR. Got: "
						+ termCg);
			}
		}
		
		return bracketTraceCoreDecl;
	}
	
	@Override
	public STraceCoreDeclIR caseAConcurrentExpressionTraceCoreDefinition(
			AConcurrentExpressionTraceCoreDefinition node, IRInfo question)
			throws AnalysisException
	{
		AConcurrentExpTraceCoreDeclIR concTraceCoreDecl = new AConcurrentExpTraceCoreDeclIR();

		for(PTraceDefinition def : node.getDefs())
		{
			STraceDeclIR traceDefCg = def.apply(question.getTraceDeclVisitor(), question);
			concTraceCoreDecl.getDecls().add(traceDefCg);
		}
		
		return concTraceCoreDecl;
	}
}
