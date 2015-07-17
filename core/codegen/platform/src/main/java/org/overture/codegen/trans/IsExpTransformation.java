package org.overture.codegen.trans;

import java.util.List;

import org.overture.codegen.assistant.ExpAssistantCG;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.AGeneralIsExpCG;
import org.overture.codegen.cgast.expressions.AOrBoolBinaryExpCG;
import org.overture.codegen.cgast.expressions.SVarExpBase;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.AUnionTypeCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;


public class IsExpTransformation extends DepthFirstAnalysisAdaptor
{
	private TransAssistantCG transAssistant;
	private String isExpSubjectNamePrefix;
	
	public IsExpTransformation(TransAssistantCG transAssistant, String isExpSubjectNamePrefix)
	{
		this.transAssistant = transAssistant;
		this.isExpSubjectNamePrefix = isExpSubjectNamePrefix;
	}
	@Override
	public void caseAGeneralIsExpCG(AGeneralIsExpCG node)
			throws AnalysisException
	{
		STypeCG checkedType = node.getCheckedType();
		
		if(!(checkedType instanceof AUnionTypeCG))
		{
			node.getExp().apply(this);
			return;
		}
		
		AUnionTypeCG unionType = (AUnionTypeCG) checkedType;
		List<STypeCG> types = unionType.getTypes();
		types = transAssistant.getInfo().getTypeAssistant().clearObjectTypes(types);

		SExpCG exp = node.getExp();
		STypeCG expType = node.getExp().getType();
		
		ExpAssistantCG expAssistant = transAssistant.getInfo().getExpAssistant();
		
		if(types.size() == 1)
		{
			SExpCG isExp = expAssistant.consIsExp(exp, types.get(0));
			transAssistant.replaceNodeWith(node, isExp);
			
			isExp.apply(this);
			
		} else
		{

			ABlockStmCG replacementBlock = new ABlockStmCG();

			SExpCG expVar = null;

			if (!(exp instanceof SVarExpBase))
			{
				String varName = transAssistant.getInfo().getTempVarNameGen().nextVarName(isExpSubjectNamePrefix);
				AVarDeclCG expDecl = transAssistant.consDecl(varName, expType.clone(), exp.clone());
				replacementBlock.getLocalDefs().add(expDecl);
				expVar = transAssistant.consIdentifierVar(varName, expType.clone());
			} else
			{
				expVar = exp;
			}

			AOrBoolBinaryExpCG topOrExp = new AOrBoolBinaryExpCG();
			topOrExp.setType(new ABoolBasicTypeCG());

			STypeCG firstType = types.get(0);

			SExpCG nextIsExp = expAssistant.consIsExp(expVar, firstType);
			topOrExp.setLeft(nextIsExp);

			AOrBoolBinaryExpCG nextOrExp = topOrExp;

			for (int i = 1; i < types.size() - 1; i++)
			{
				STypeCG currentType = types.get(i);

				nextIsExp = expAssistant.consIsExp(expVar, currentType);

				AOrBoolBinaryExpCG tmp = new AOrBoolBinaryExpCG();
				tmp.setType(new ABoolBasicTypeCG());
				tmp.setLeft(nextIsExp);
				nextOrExp.setRight(tmp);
				nextOrExp = tmp;
			}

			STypeCG lastType = types.get(types.size()-1);

			nextIsExp = expAssistant.consIsExp(expVar, lastType);

			nextOrExp.setRight(nextIsExp);

			SStmCG enclosingStm = transAssistant.getEnclosingStm(node, "general is-expression");
			transAssistant.replaceNodeWith(enclosingStm, replacementBlock);
			transAssistant.replaceNodeWith(node, topOrExp);

			replacementBlock.getStatements().add(enclosingStm);

			topOrExp.apply(this);
		}
	}
}
