package org.overture.codegen.vdm2jml;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.vdm2java.JavaCodeGen;
import org.overture.codegen.vdm2java.JavaRecordCreator;

class ReportInjector extends DepthFirstAnalysisAdaptor
{
	private JavaCodeGen javaGen;
	private AMethodDeclCG method;

	public ReportInjector(JavaCodeGen javaGen, AMethodDeclCG method)
	{
		this.javaGen = javaGen;
		this.method = method;
	}

	@Override
	public void caseAReturnStmCG(AReturnStmCG node)
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		if(node.getExp() != null)
		{
			javaGen.getTransformationAssistant().replaceNodeWith(node.getExp(), consReportCall(node.getExp().clone()));
		}
	}

	public AApplyExpCG consReportCall(SExpCG returnExp)
	{
		JavaRecordCreator recCreator = javaGen.getJavaFormat().getRecCreator();
		
		AApplyExpCG reportCall = recCreator.consUtilCall(method.getMethodType().clone(), JmlGenerator.REPORT_CALL);
		reportCall.getArgs().add(javaGen.getInfo().getExpAssistant().consStringLiteral(method.getName(), false));
		reportCall.getArgs().add(returnExp);
		
		for(AFormalParamLocalParamCG param : method.getFormalParams())
		{
			SPatternCG name = param.getPattern();
			
			if(name instanceof AIdentifierPatternCG)
			{
				AIdentifierPatternCG id = (AIdentifierPatternCG) name;
				
				AIdentifierVarExpCG arg = javaGen.getTransformationAssistant().
						consIdentifierVar(id.getName(), param.getType().clone());
				
				// TODO: what if the settings are set to char of sequences?
				reportCall.getArgs().add(javaGen.getInfo().getExpAssistant().consStringLiteral(id.getName(), false));
				reportCall.getArgs().add(arg);
			}
			else
			{
				Logger.getLog().printErrorln("Expected pattern to be an identifier pattern at this point. Got: "
						+ name
						+ " in '"
						+ this.getClass().getSimpleName()
						+ "'");
			}
		}
		
		return reportCall;
	}
}