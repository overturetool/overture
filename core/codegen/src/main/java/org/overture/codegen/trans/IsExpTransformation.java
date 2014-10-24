package org.overture.codegen.trans;

import org.overture.codegen.assistant.ExpAssistantCG;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;
import org.overture.codegen.cgast.expressions.AGeneralIsExpCG;
import org.overture.codegen.cgast.expressions.AOrBoolBinaryExpCG;
import org.overture.codegen.cgast.expressions.SVarExpBase;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.AUnionTypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.trans.assistants.TransformationAssistantCG;


public class IsExpTransformation extends DepthFirstAnalysisAdaptor
{
	private IRInfo info;
	private TransformationAssistantCG transformationAssistant;
	private String isExpSubjectNamePrefix;
	
	public IsExpTransformation(IRInfo info, TransformationAssistantCG transformationAssistant, String isExpSubjectNamePrefix)
	{
		this.info = info;
		this.transformationAssistant = transformationAssistant;
		this.isExpSubjectNamePrefix = isExpSubjectNamePrefix;
	}
	@Override
	public void caseAGeneralIsExpCG(AGeneralIsExpCG node)
			throws AnalysisException
	{
		//TODO: Assumes deflattened structure?
		
		STypeCG checkedType = node.getCheckedType();
		
		if(!(checkedType instanceof AUnionTypeCG))
		{
			node.getExp().apply(this);
			return;
		}
		
		SExpCG exp = node.getExp();
		STypeCG expType = node.getExp().getType();
		
		ABlockStmCG replacementBlock = new ABlockStmCG();
		
		SExpCG expVar = null;
		
		if(!(exp instanceof SVarExpBase))
		{
			String varName = info.getTempVarNameGen().nextVarName(isExpSubjectNamePrefix);
			AVarLocalDeclCG expDecl = transformationAssistant.consDecl(varName, expType.clone(), exp.clone());
			replacementBlock.getLocalDefs().add(expDecl);
			expVar = transformationAssistant.consIdentifierVar(varName, expType.clone());
		}
		else
		{
			expVar = exp;
		}
		
		ExpAssistantCG expAssistant = info.getExpAssistant();
		
		AUnionTypeCG unionType = (AUnionTypeCG) checkedType;
		
		AOrBoolBinaryExpCG topOrExp = new AOrBoolBinaryExpCG();
		topOrExp.setType(new ABoolBasicTypeCG());
		
		STypeCG firstType = unionType.getTypes().getFirst();
		
		SExpCG nextIsExp = expAssistant.consIsExp(expVar, firstType);
		topOrExp.setLeft(nextIsExp);
		
		AOrBoolBinaryExpCG nextOrExp = topOrExp;
		
		for(int i = 1; i < unionType.getTypes().size() - 1; i++)
		{
			STypeCG currentType = unionType.getTypes().get(i);
			
			nextIsExp = expAssistant.consIsExp(expVar, currentType);

			AOrBoolBinaryExpCG tmp = new AOrBoolBinaryExpCG();
			tmp.setType(new ABoolBasicTypeCG());
			tmp.setLeft(nextIsExp);
			nextOrExp.setRight(tmp);
			nextOrExp = tmp;
		}

		STypeCG lastType = unionType.getTypes().getLast();
		
		nextIsExp = expAssistant.consIsExp(expVar, lastType);
		
		nextOrExp.setRight(nextIsExp);
				
		SStmCG enclosingStm = transformationAssistant.getEnclosingStm(node, "general is-expression");
		transformationAssistant.replaceNodeWith(enclosingStm, replacementBlock);
		transformationAssistant.replaceNodeWith(node, topOrExp);
		
		replacementBlock.getStatements().add(enclosingStm);
		
		topOrExp.apply(this);
	}
}
