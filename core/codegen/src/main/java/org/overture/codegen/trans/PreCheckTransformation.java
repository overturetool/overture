package org.overture.codegen.trans;

import java.util.LinkedList;

import org.overture.codegen.cgast.SDeclCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.APreCondRuntimeErrorExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.statements.ARaiseErrorStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.AErrorTypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.assistants.TransformationAssistantCG;

public class PreCheckTransformation extends DepthFirstAnalysisAdaptor {

	private IRInfo info;
	private TransformationAssistantCG transformationAssistant;
	
	public PreCheckTransformation(IRInfo info, TransformationAssistantCG transformationAssistant)
	{
		this.info = info;
		this.transformationAssistant = transformationAssistant;
	}
	
	@Override
	public void caseAMethodDeclCG(AMethodDeclCG node) throws AnalysisException {
		
		if(!info.getSettings().generatePreCondChecks())
		{
			return;
		}
		
		SDeclCG preCond = node.getPreCond();
		
		if(preCond == null)
		{
			return;
		}
		
		if(!(preCond instanceof AMethodDeclCG))
		{
			Logger.getLog().printErrorln("Expected pre condition to be a method declaration at this point. Got: " + preCond);
			return;
		}

		AMethodDeclCG preCondMethod = (AMethodDeclCG) preCond;
		
		AIdentifierVarExpCG preVar = new AIdentifierVarExpCG();
		preVar.setType(preCondMethod.getMethodType().clone());
		preVar.setOriginal(preCondMethod.getName());
		preVar.setIsLambda(false);
		
		AApplyExpCG preCondCall = new AApplyExpCG();
		preCondCall.setType(new ABoolBasicTypeCG());
		preCondCall.setRoot(preVar);
		
		LinkedList<AFormalParamLocalParamCG> params = node.getFormalParams();
		
		for(AFormalParamLocalParamCG p : params)
		{
			SPatternCG paramPattern = p.getPattern();
			
			if(!(paramPattern instanceof AIdentifierPatternCG))
			{
				Logger.getLog().printErrorln("Expected parameter pattern to be an identifier pattern at this point. Got: " + paramPattern);
				return;
			}
			
			AIdentifierPatternCG paramId = (AIdentifierPatternCG) paramPattern;
			
			AIdentifierVarExpCG paramArg = new AIdentifierVarExpCG();
			paramArg.setIsLambda(false);
			paramArg.setType(p.getType().clone());
			paramArg.setOriginal(paramId.getName());
			
			preCondCall.getArgs().add(paramArg);
		}
		
		SStmCG body = node.getBody();

		
		APreCondRuntimeErrorExpCG runtimeError = new APreCondRuntimeErrorExpCG();
		runtimeError.setType(new AErrorTypeCG());
		runtimeError.setMessage(String.format("Precondition failure: pre_%s ", node.getName()));
		
		ARaiseErrorStmCG raiseError= new ARaiseErrorStmCG();
		raiseError.setError(runtimeError);
		
		AIfStmCG ifCheck = new AIfStmCG();
		ifCheck.setIfExp(info.getExpAssistant().negate(preCondCall));
		ifCheck.setThenStm(raiseError);
		
		ABlockStmCG newBody = new ABlockStmCG();
		newBody.getStatements().add(ifCheck);
		newBody.getStatements().add(body.clone());
		
		transformationAssistant.replaceNodeWith(body, newBody);
	}
}
