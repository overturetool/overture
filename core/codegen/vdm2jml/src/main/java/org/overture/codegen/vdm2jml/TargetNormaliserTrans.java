package org.overture.codegen.vdm2jml;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.expressions.AFieldExp;
import org.overture.codegen.assistant.DeclAssistantCG;
import org.overture.codegen.assistant.ExpAssistantCG;
import org.overture.codegen.assistant.PatternAssistantCG;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AMapSeqGetExpCG;
import org.overture.codegen.cgast.expressions.SVarExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.ACallObjectExpStmCG;
import org.overture.codegen.cgast.statements.AMapSeqUpdateStmCG;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class TargetNormaliserTrans extends DepthFirstAnalysisAdaptor
{
	public static final String STATE_DES = "stateDes_";

	// Consider atomic
	// Consider cloning
	// Consider the 'index' exp and side effects a.b('a').c (also for applies, although get calls do not really receive
	// any arguments)

	private JmlGenerator jmlGen;

	public TargetNormaliserTrans(JmlGenerator jmlGen)
	{
		this.jmlGen = jmlGen;
	}

	@Override
	public void caseACallObjectExpStmCG(ACallObjectExpStmCG node)
			throws AnalysisException
	{
		List<AVarDeclCG> temps = new LinkedList<AVarDeclCG>();
		SExpCG target = splitTarget(node.getObj(), temps);

		ABlockStmCG replBlock = new ABlockStmCG();
		jmlGen.getJavaGen().getTransAssistant().replaceNodeWith(node, replBlock);

		for (AVarDeclCG t : temps)
		{
			replBlock.getLocalDefs().add(t);
		}

		replBlock.getStatements().add(node);

		if (target != node.getObj())
		{
			jmlGen.getJavaGen().getTransAssistant().replaceNodeWith(node.getObj(), target);
		}
	}

	@Override
	public void caseAMapSeqUpdateStmCG(AMapSeqUpdateStmCG node)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		super.caseAMapSeqUpdateStmCG(node);
	}

	@Override
	public void caseAMapSeqGetExpCG(AMapSeqGetExpCG node)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		super.caseAMapSeqGetExpCG(node);
	}

	public SExpCG splitTarget(SExpCG target, List<AVarDeclCG> vars)
	{
		DeclAssistantCG dAssist = jmlGen.getJavaGen().getInfo().getDeclAssistant();
		PatternAssistantCG pAssist = jmlGen.getJavaGen().getInfo().getPatternAssistant();
		ExpAssistantCG eAssist = jmlGen.getJavaGen().getInfo().getExpAssistant();
		ITempVarGen nameGen = jmlGen.getJavaGen().getInfo().getTempVarNameGen();
		TransAssistantCG tr = jmlGen.getJavaGen().getTransAssistant();

		if (target instanceof SVarExpCG)
		{
			return target;
		} else if (target instanceof AMapSeqGetExpCG)
		{
			// Utils.mapSeqGet(a.get_m(), 1).get_b()
			
			AMapSeqGetExpCG get = (AMapSeqGetExpCG) target;
			SExpCG newCol = splitTarget(get.getCol().clone(), vars);
			tr.replaceNodeWith(get.getCol(), newCol);
			// Utils.mapSeqGet(tmp_2, 1).get_b()
			
			AIdentifierPatternCG id = pAssist.consIdPattern(nameGen.nextVarName(STATE_DES));
			AVarDeclCG varDecl = dAssist.consLocalVarDecl(get.getType().clone(), id, get.clone());
			vars.add(varDecl);
			// B tmp_1 = Utils.mapSeqGet(tmp_2, 1).get_b()

			// tmp_1
			return eAssist.consIdVar(id.getName(), get.getType().clone());

		} else if (target instanceof AApplyExpCG)
		{
			// a.get_b().get_c()

			AApplyExpCG app = (AApplyExpCG) target;
			SExpCG newRoot = splitTarget(app.getRoot().clone(), vars);
			tr.replaceNodeWith(app.getRoot(), newRoot);
			// tmp_2.get_c()

			AIdentifierPatternCG id = pAssist.consIdPattern(nameGen.nextVarName(STATE_DES));
			AVarDeclCG varDecl = dAssist.consLocalVarDecl(app.getType().clone(), id, app.clone());
			vars.add(varDecl);
			// C tmp_1 = tmp_2.get_c()

			// tmp_1
			return eAssist.consIdVar(id.getName(), app.getType().clone());

		} else if (target instanceof AFieldExpCG)
		{
			AFieldExpCG field = (AFieldExpCG) target;
			SExpCG newObj = splitTarget(field.getObject().clone(), vars);
			tr.replaceNodeWith(field.getObject(), newObj);
			
			return field;
		} else
		{
			Logger.getLog().printErrorln("Expected a variable expression at this point in '"
					+ this.getClass().getSimpleName() + "'. Got " + target);
			return null;
		}
	}
}
