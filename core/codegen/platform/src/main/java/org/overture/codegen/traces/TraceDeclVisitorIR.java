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
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SMultipleBindIR;
import org.overture.codegen.ir.STraceCoreDeclIR;
import org.overture.codegen.ir.STraceDeclIR;
import org.overture.codegen.ir.patterns.ASetMultipleBindIR;
import org.overture.codegen.ir.traces.ALetBeStBindingTraceDeclIR;
import org.overture.codegen.ir.traces.ALetDefBindingTraceDeclIR;
import org.overture.codegen.ir.traces.ARepeatTraceDeclIR;
import org.overture.codegen.visitor.AbstractVisitorIR;

public class TraceDeclVisitorIR extends AbstractVisitorIR<IRInfo, STraceDeclIR>
{
	@Override
	public STraceDeclIR caseAInstanceTraceDefinition(
			AInstanceTraceDefinition node, IRInfo question)
			throws AnalysisException
	{
		assert false : "This node does not exist in the VDM AST";
		return null;
	}

	@Override
	public STraceDeclIR caseALetBeStBindingTraceDefinition(
			ALetBeStBindingTraceDefinition node, IRInfo question)
			throws AnalysisException
	{
		PMultipleBind multipleBind = node.getBind();

		if (!(multipleBind instanceof ASetMultipleBind))
		{
			question.addUnsupportedNode(node, "Generation of the let be st trace definition"
					+ " is only supported for a multiple set bind. Got: "
					+ multipleBind);
			return null;
		}

		PTraceDefinition body = node.getBody();
		PExp stExp = node.getStexp();

		SMultipleBindIR multipleBindCg = multipleBind.apply(question.getMultipleBindVisitor(), question);

		if (!(multipleBindCg instanceof ASetMultipleBindIR))
		{
			return null;
		}

		STraceDeclIR bodyCg = body.apply(question.getTraceDeclVisitor(), question);
		SExpIR stExpCg = stExp != null
				? stExp.apply(question.getExpVisitor(), question) : null;

		ALetBeStBindingTraceDeclIR letBeSt = new ALetBeStBindingTraceDeclIR();
		letBeSt.setBind((ASetMultipleBindIR) multipleBindCg);
		letBeSt.setBody(bodyCg);
		letBeSt.setStExp(stExpCg);
		;

		return letBeSt;
	}

	@Override
	public STraceDeclIR caseALetDefBindingTraceDefinition(
			ALetDefBindingTraceDefinition node, IRInfo question)
			throws AnalysisException
	{
		PTraceDefinition body = node.getBody();

		ALetDefBindingTraceDeclIR letDef = new ALetDefBindingTraceDeclIR();

		question.getDeclAssistant().setFinalLocalDefs(node.getLocalDefs(), letDef.getLocalDefs(), question);

		STraceDeclIR bodyCg = body.apply(question.getTraceDeclVisitor(), question);
		letDef.setBody(bodyCg);

		return letDef;
	}

	@Override
	public STraceDeclIR caseARepeatTraceDefinition(ARepeatTraceDefinition node,
			IRInfo question) throws AnalysisException
	{
		PTraceCoreDefinition core = node.getCore();
		Long from = node.getFrom();
		Long to = node.getTo();

		STraceCoreDeclIR coreCg = core.apply(question.getTraceCoreDeclVisitor(), question);

		ARepeatTraceDeclIR repeatTraceDecl = new ARepeatTraceDeclIR();
		repeatTraceDecl.setCore(coreCg);
		repeatTraceDecl.setFrom(from);
		repeatTraceDecl.setTo(to);

		return repeatTraceDecl;
	}

}
