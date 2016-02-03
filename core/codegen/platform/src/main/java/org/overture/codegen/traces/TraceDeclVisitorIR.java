package org.overture.codegen.traces;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.traces.AInstanceTraceDefinition;
import org.overture.ast.definitions.traces.ALetBeStBindingTraceDefinition;
import org.overture.ast.definitions.traces.ALetDefBindingTraceDefinition;
import org.overture.ast.definitions.traces.ARepeatTraceDefinition;
import org.overture.ast.definitions.traces.PTraceCoreDefinition;
import org.overture.ast.definitions.traces.PTraceDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.codegen.ir.SExpCG;
import org.overture.codegen.ir.SMultipleBindCG;
import org.overture.codegen.ir.STraceCoreDeclCG;
import org.overture.codegen.ir.STraceDeclCG;
import org.overture.codegen.ir.patterns.ASetMultipleBindCG;
import org.overture.codegen.ir.traces.ALetBeStBindingTraceDeclCG;
import org.overture.codegen.ir.traces.ALetDefBindingTraceDeclCG;
import org.overture.codegen.ir.traces.ARepeatTraceDeclCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.visitor.AbstractVisitorCG;

public class TraceDeclVisitorCG extends AbstractVisitorCG<IRInfo, STraceDeclCG>
{
	@Override
	public STraceDeclCG caseAInstanceTraceDefinition(
			AInstanceTraceDefinition node, IRInfo question)
			throws AnalysisException
	{
		assert false : "This node does not exist in the VDM AST";
		return null;
	}
	
	@Override
	public STraceDeclCG caseALetBeStBindingTraceDefinition(
			ALetBeStBindingTraceDefinition node, IRInfo question)
			throws AnalysisException
	{
		PMultipleBind multipleBind = node.getBind();
		
		if (!(multipleBind instanceof ASetMultipleBind))
		{
			question.addUnsupportedNode(node, "Generation of the let be st trace definition"
					+ " is only supported for a multiple set bind. Got: " + multipleBind);
			return null;
		}
		
		
		PTraceDefinition body = node.getBody();
		PExp stExp = node.getStexp();
		
		SMultipleBindCG multipleBindCg = multipleBind.apply(question.getMultipleBindVisitor(), question);
		
		if (!(multipleBindCg instanceof ASetMultipleBindCG))
		{
			return null;
		}
		
		STraceDeclCG bodyCg = body.apply(question.getTraceDeclVisitor(), question);
		SExpCG stExpCg = stExp != null ? stExp.apply(question.getExpVisitor(), question) : null;
		
		ALetBeStBindingTraceDeclCG letBeSt = new ALetBeStBindingTraceDeclCG();
		letBeSt.setBind((ASetMultipleBindCG) multipleBindCg);
		letBeSt.setBody(bodyCg);
		letBeSt.setStExp(stExpCg);;
		
		return letBeSt;
	}
	
	@Override
	public STraceDeclCG caseALetDefBindingTraceDefinition(
			ALetDefBindingTraceDefinition node, IRInfo question)
			throws AnalysisException
	{
		PTraceDefinition body = node.getBody();
		
		ALetDefBindingTraceDeclCG letDef = new ALetDefBindingTraceDeclCG();
		
		question.getDeclAssistant().setFinalLocalDefs(node.getLocalDefs(), letDef.getLocalDefs(), question);
		
		STraceDeclCG bodyCg = body.apply(question.getTraceDeclVisitor(), question);
		letDef.setBody(bodyCg);
		
		return letDef;
	}
	
	@Override
	public STraceDeclCG caseARepeatTraceDefinition(ARepeatTraceDefinition node,
			IRInfo question) throws AnalysisException
	{
		PTraceCoreDefinition core = node.getCore();
		Long from = node.getFrom();
		Long to = node.getTo();

		STraceCoreDeclCG coreCg = core.apply(question.getTraceCoreDeclVisitor(), question);
		
		ARepeatTraceDeclCG repeatTraceDecl = new ARepeatTraceDeclCG();
		repeatTraceDecl.setCore(coreCg);
		repeatTraceDecl.setFrom(from);
		repeatTraceDecl.setTo(to);
		
		return repeatTraceDecl;
	}
	
}
