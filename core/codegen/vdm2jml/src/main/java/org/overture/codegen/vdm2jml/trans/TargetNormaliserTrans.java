package org.overture.codegen.vdm2jml.trans;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.overture.codegen.assistant.DeclAssistantIR;
import org.overture.codegen.assistant.ExpAssistantIR;
import org.overture.codegen.assistant.PatternAssistantIR;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.AFieldExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.AMapSeqGetExpIR;
import org.overture.codegen.ir.expressions.SVarExpIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.AAssignToExpStmIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.AMapSeqUpdateStmIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2jml.JmlGenerator;
import org.overture.codegen.vdm2jml.data.StateDesInfo;

public class TargetNormaliserTrans extends DepthFirstAnalysisAdaptor
{
	public static final String STATE_DES = "stateDes_";

	private JmlGenerator jmlGen;

	private StateDesInfo stateDesInfo;

	private Logger log = Logger.getLogger(this.getClass().getName());

	public TargetNormaliserTrans(JmlGenerator jmlGen)
	{
		this.jmlGen = jmlGen;
		this.stateDesInfo = new StateDesInfo();
	}

	@Override
	public void caseAFieldExpIR(AFieldExpIR node) throws AnalysisException
	{
		if (!(node.parent() instanceof AAssignToExpStmIR))
		{
			return;
		}

		if (!(node.getObject() instanceof SVarExpIR))
		{
			normaliseTarget((AAssignToExpStmIR) node.parent(), node.getObject());
		}
	}

	@Override
	public void caseAMapSeqUpdateStmIR(AMapSeqUpdateStmIR node)
			throws AnalysisException
	{
		if (!(node.getCol() instanceof SVarExpIR))
		{
			normaliseTarget(node, node.getCol());
		}
	}

	private void normaliseTarget(SStmIR node, SExpIR target)
	{
		List<AVarDeclIR> varDecls = new LinkedList<AVarDeclIR>();
		List<AIdentifierVarExpIR> vars = new LinkedList<AIdentifierVarExpIR>();

		SExpIR newTarget = splitTarget(target, varDecls, vars);

		markAsCloneFree(varDecls);
		markAsCloneFree(vars);

		stateDesInfo.addStateDesVars(node, vars);
		stateDesInfo.addStateDesDecl(node, varDecls);

		if (varDecls.isEmpty())
		{
			return;
		}

		ABlockStmIR replBlock = new ABlockStmIR();
		jmlGen.getJavaGen().getTransAssistant().replaceNodeWith(node, replBlock);

		for (AVarDeclIR var : varDecls)
		{
			replBlock.getLocalDefs().add(var);
		}

		replBlock.getStatements().add(node);
		jmlGen.getJavaGen().getTransAssistant().replaceNodeWith(target, newTarget);
	}

	private SExpIR splitTarget(SExpIR target, List<AVarDeclIR> varDecls,
			List<AIdentifierVarExpIR> vars)
	{
		DeclAssistantIR dAssist = jmlGen.getJavaGen().getInfo().getDeclAssistant();
		PatternAssistantIR pAssist = jmlGen.getJavaGen().getInfo().getPatternAssistant();
		ExpAssistantIR eAssist = jmlGen.getJavaGen().getInfo().getExpAssistant();
		ITempVarGen nameGen = jmlGen.getJavaGen().getInfo().getTempVarNameGen();
		TransAssistantIR tr = jmlGen.getJavaGen().getTransAssistant();

		if (target instanceof SVarExpIR)
		{
			AIdentifierVarExpIR var = ((AIdentifierVarExpIR) target).clone();
			vars.add(var);
			return var;
		} else if (target instanceof AMapSeqGetExpIR)
		{
			// Utils.mapSeqGet(a.myMap, 1).b
			AMapSeqGetExpIR get = (AMapSeqGetExpIR) target;
			SExpIR newCol = splitTarget(get.getCol().clone(), varDecls, vars);
			tr.replaceNodeWith(get.getCol(), newCol);
			// Utils.mapSeqGet(tmp_1, 1).b

			AIdentifierPatternIR id = pAssist.consIdPattern(nameGen.nextVarName(STATE_DES));
			AVarDeclIR varDecl = dAssist.consLocalVarDecl(get.getType().clone(), id, get.clone());
			varDecls.add(varDecl);
			// B tmp_2 = Utils.mapSeqGet(tmp_1, 1).b

			// tmp_2
			AIdentifierVarExpIR var = eAssist.consIdVar(id.getName(), get.getType().clone());
			vars.add(var);
			return var;

		} else if (target instanceof AFieldExpIR)
		{
			// a.b.c
			AFieldExpIR field = (AFieldExpIR) target;
			SExpIR newObj = splitTarget(field.getObject().clone(), varDecls, vars);
			tr.replaceNodeWith(field.getObject(), newObj);
			// tmp_1.c

			AIdentifierPatternIR id = pAssist.consIdPattern(nameGen.nextVarName(STATE_DES));
			AVarDeclIR varDecl = dAssist.consLocalVarDecl(field.getType().clone(), id, field.clone());
			varDecls.add(varDecl);
			// C tmp_2 = tmp1.c

			AIdentifierVarExpIR var = eAssist.consIdVar(id.getName(), field.getType().clone());
			vars.add(var);
			return var;
		} else
		{
			log.error("Got unexpected target:" + target);
			return null;
		}
	}

	private void markAsCloneFree(List<? extends INode> nodes)
	{
		for (INode v : nodes)
		{
			jmlGen.getJavaGen().getJavaFormat().getValueSemantics().addCloneFreeNode(v);
		}
	}

	public StateDesInfo getStateDesInfo()
	{
		return stateDesInfo;
	}
}
