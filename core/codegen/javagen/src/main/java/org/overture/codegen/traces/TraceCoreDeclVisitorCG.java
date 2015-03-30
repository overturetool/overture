package org.overture.codegen.traces;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.traces.AApplyExpressionTraceCoreDefinition;
import org.overture.ast.definitions.traces.ABracketedExpressionTraceCoreDefinition;
import org.overture.ast.definitions.traces.AConcurrentExpressionTraceCoreDefinition;
import org.overture.ast.definitions.traces.ATraceDefinitionTerm;
import org.overture.ast.definitions.traces.PTraceDefinition;
import org.overture.ast.statements.PStm;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STermCG;
import org.overture.codegen.cgast.STraceCoreDeclCG;
import org.overture.codegen.cgast.STraceDeclCG;
import org.overture.codegen.cgast.traces.AApplyExpTraceCoreDeclCG;
import org.overture.codegen.cgast.traces.ABracketedExpTraceCoreDeclCG;
import org.overture.codegen.cgast.traces.AConcurrentExpTraceCoreDeclCG;
import org.overture.codegen.cgast.traces.ATraceDeclTermCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.visitor.AbstractVisitorCG;

public class TraceCoreDeclVisitorCG extends AbstractVisitorCG<IRInfo, STraceCoreDeclCG>
{
	@Override
	public STraceCoreDeclCG caseAApplyExpressionTraceCoreDefinition(
			AApplyExpressionTraceCoreDefinition node, IRInfo question)
			throws AnalysisException
	{
		PStm callStm = node.getCallStatement();
		SStmCG callStmCg = callStm.apply(question.getStmVisitor(), question);
		
		AApplyExpTraceCoreDeclCG applyTraceCoreDecl = new AApplyExpTraceCoreDeclCG();
		applyTraceCoreDecl.setCallStm(callStmCg);
		
		return applyTraceCoreDecl;
	}
	
	@Override
	public STraceCoreDeclCG caseABracketedExpressionTraceCoreDefinition(
			ABracketedExpressionTraceCoreDefinition node, IRInfo question)
			throws AnalysisException
	{
		ABracketedExpTraceCoreDeclCG bracketTraceCoreDecl = new ABracketedExpTraceCoreDeclCG();
		
		for(ATraceDefinitionTerm term : node.getTerms())
		{
			STermCG termCg = term.apply(question.getTermVisitor(), question);
			
			if(termCg instanceof ATraceDeclTermCG)
			{
				bracketTraceCoreDecl.getTerms().add((ATraceDeclTermCG) termCg);
			}
			else
			{
				Logger.getLog().printErrorln("Expected term to be of"
						+ " type ATraceDeclTermCG. Got: " + termCg);
			}
		}
		
		return bracketTraceCoreDecl;
	}
	
	@Override
	public STraceCoreDeclCG caseAConcurrentExpressionTraceCoreDefinition(
			AConcurrentExpressionTraceCoreDefinition node, IRInfo question)
			throws AnalysisException
	{
		AConcurrentExpTraceCoreDeclCG concTraceCoreDecl = new AConcurrentExpTraceCoreDeclCG();

		for(PTraceDefinition def : node.getDefs())
		{
			STraceDeclCG traceDefCg = def.apply(question.getTraceDeclVisitor(), question);
			concTraceCoreDecl.getDecls().add(traceDefCg);
		}
		
		return concTraceCoreDecl;
	}
}
