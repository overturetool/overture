package org.overture.codegen.trans;

import java.util.List;

import org.overture.codegen.assistant.ExpAssistantIR;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.AGeneralIsExpIR;
import org.overture.codegen.ir.expressions.AOrBoolBinaryExpIR;
import org.overture.codegen.ir.expressions.SVarExpBase;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.AUnionTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;


public class IsExpTrans extends DepthFirstAnalysisAdaptor
{
	private TransAssistantIR transAssistant;
	private String isExpSubjectNamePrefix;
	
	public IsExpTrans(TransAssistantIR transAssistant, String isExpSubjectNamePrefix)
	{
		this.transAssistant = transAssistant;
		this.isExpSubjectNamePrefix = isExpSubjectNamePrefix;
	}
	@Override
	public void caseAGeneralIsExpIR(AGeneralIsExpIR node)
			throws AnalysisException
	{
		STypeIR checkedType = node.getCheckedType();
		
		if(!(checkedType instanceof AUnionTypeIR))
		{
			node.getExp().apply(this);
			return;
		}
		
		AUnionTypeIR unionType = (AUnionTypeIR) checkedType;
		List<STypeIR> types = unionType.getTypes();
		types = transAssistant.getInfo().getTypeAssistant().clearObjectTypes(types);

		SExpIR exp = node.getExp();
		STypeIR expType = node.getExp().getType();
		
		ExpAssistantIR expAssistant = transAssistant.getInfo().getExpAssistant();
		
		if(types.size() == 1)
		{
			SExpIR isExp = expAssistant.consIsExp(exp, types.get(0));
			transAssistant.replaceNodeWith(node, isExp);
			
			isExp.apply(this);
			
		} else
		{

			ABlockStmIR replacementBlock = new ABlockStmIR();

			SExpIR expVar = null;

			if (!(exp instanceof SVarExpBase))
			{
				String varName = transAssistant.getInfo().getTempVarNameGen().nextVarName(isExpSubjectNamePrefix);
				AVarDeclIR expDecl = transAssistant.consDecl(varName, expType.clone(), exp.clone());
				replacementBlock.getLocalDefs().add(expDecl);
				expVar = transAssistant.getInfo().getExpAssistant().consIdVar(varName, expType.clone());
			} else
			{
				expVar = exp;
			}

			AOrBoolBinaryExpIR topOrExp = new AOrBoolBinaryExpIR();
			topOrExp.setType(new ABoolBasicTypeIR());

			STypeIR firstType = types.get(0);

			SExpIR nextIsExp = expAssistant.consIsExp(expVar, firstType);
			topOrExp.setLeft(nextIsExp);

			AOrBoolBinaryExpIR nextOrExp = topOrExp;

			for (int i = 1; i < types.size() - 1; i++)
			{
				STypeIR currentType = types.get(i);

				nextIsExp = expAssistant.consIsExp(expVar, currentType);

				AOrBoolBinaryExpIR tmp = new AOrBoolBinaryExpIR();
				tmp.setType(new ABoolBasicTypeIR());
				tmp.setLeft(nextIsExp);
				nextOrExp.setRight(tmp);
				nextOrExp = tmp;
			}

			STypeIR lastType = types.get(types.size()-1);

			nextIsExp = expAssistant.consIsExp(expVar, lastType);

			nextOrExp.setRight(nextIsExp);

			SStmIR enclosingStm = transAssistant.getEnclosingStm(node, "general is-expression");
			transAssistant.replaceNodeWith(enclosingStm, replacementBlock);
			transAssistant.replaceNodeWith(node, topOrExp);

			replacementBlock.getStatements().add(enclosingStm);

			topOrExp.apply(this);
		}
	}
}
