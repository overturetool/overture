package org.overture.codegen.trans.patterns;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;
import org.overture.codegen.cgast.declarations.SLocalDeclCG;
import org.overture.codegen.cgast.expressions.ABoolLiteralExpCG;
import org.overture.codegen.cgast.expressions.ACharLiteralExpCG;
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.AIntLiteralExpCG;
import org.overture.codegen.cgast.expressions.ANullExpCG;
import org.overture.codegen.cgast.expressions.APatternMatchRuntimeErrorExpCG;
import org.overture.codegen.cgast.expressions.AQuoteLiteralExpCG;
import org.overture.codegen.cgast.expressions.ARealLiteralExpCG;
import org.overture.codegen.cgast.patterns.ABoolPatternCG;
import org.overture.codegen.cgast.patterns.ACharPatternCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.patterns.AIgnorePatternCG;
import org.overture.codegen.cgast.patterns.AIntPatternCG;
import org.overture.codegen.cgast.patterns.ANullPatternCG;
import org.overture.codegen.cgast.patterns.AQuotePatternCG;
import org.overture.codegen.cgast.patterns.ARealPatternCG;
import org.overture.codegen.cgast.patterns.AStringPatternCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.statements.ARaiseErrorStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.AErrorTypeCG;
import org.overture.codegen.cgast.types.AObjectTypeCG;
import org.overture.codegen.cgast.types.ASeqSeqTypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.trans.assistants.TransformationAssistantCG;

public class PatternTransformation extends DepthFirstAnalysisAdaptor
{
	private IRInfo info;
	private TransformationAssistantCG transformationAssistant;

	private PatternMatchConfig config;
	
	private TempVarPrefixes varPrefixes;
	
	public PatternTransformation(TempVarPrefixes varPrefixes, IRInfo info, TransformationAssistantCG transformationAssistant, PatternMatchConfig config)
	{
		this.info = info;
		this.transformationAssistant = transformationAssistant;
		this.varPrefixes = varPrefixes;
		
		this.config = config;
	}
	
	@Override
	public void caseAMethodDeclCG(AMethodDeclCG node) throws AnalysisException
	{
		List<PatternInfo> patternInfo = extractFromParams(node.getFormalParams());
		
		if(!node.getAbstract() && node.getBody() != null)
		{
			ABlockStmCG patternHandlingBlock = consPatternHandlingBlock(patternInfo);
			
			ABlockStmCG newBody = new ABlockStmCG();
			newBody.getStatements().add(patternHandlingBlock);
			
			SStmCG oldBody = node.getBody();
			transformationAssistant.replaceNodeWith(oldBody, newBody);
			newBody.getStatements().add(oldBody);
			
			newBody.apply(this);
		}
		else
		{
			for(AFormalParamLocalParamCG param : node.getFormalParams())
			{
				String prefix = config.getName(param.getPattern().getClass());
				
				if(prefix != null)
				{
					AIdentifierPatternCG idPattern = getIdPattern(prefix);
					transformationAssistant.replaceNodeWith(param.getPattern(), idPattern);
				}
			}
		}
	}
	
	@Override
	public void caseABlockStmCG(ABlockStmCG node) throws AnalysisException
	{
		List<PatternInfo> patternInfo = extractFromLocalDefs(node.getLocalDefs());
		
		ABlockStmCG patternHandlingBlock = consPatternHandlingBlock(patternInfo);
		
		if(!patternHandlingBlock.getLocalDefs().isEmpty() || !patternHandlingBlock.getStatements().isEmpty())
		{
			node.getStatements().addFirst(patternHandlingBlock);
		}
		
		for(SStmCG stm : node.getStatements())
		{
			stm.apply(this);
		}
	}

	private ABlockStmCG consPatternHandlingBlock(List<PatternInfo> patternInfo)
	{
		ABlockStmCG patternHandlingBlock = new ABlockStmCG();

		for (PatternInfo info : patternInfo)
		{
			if (info.getPattern() instanceof AIgnorePatternCG)
			{
				AIdentifierPatternCG idPattern = getIdPattern(config.getIgnorePatternPrefix());
				transformationAssistant.replaceNodeWith(info.getPattern(), idPattern);
			} else
			{
				ABlockStmCG currentPatternHandlingBlock = consPatternCheck(info.getPattern(), info.getType());

				if (currentPatternHandlingBlock != null)
				{
					patternHandlingBlock.getStatements().add(currentPatternHandlingBlock);
				}
			}
		}
		
		return patternHandlingBlock;
	}
	
	private ABlockStmCG consPatternCheck(SPatternCG pattern, STypeCG type)
	{
		if(pattern instanceof ABoolPatternCG)
		{
			ABoolPatternCG boolPattern = (ABoolPatternCG) pattern; 
			
			Boolean value = boolPattern.getValue();
			ABoolLiteralExpCG consBoolLiteral = info.getExpAssistant().consBoolLiteral(value);
	
			return consPatternCheck(boolPattern, type,  consBoolLiteral);
		}
		else if(pattern instanceof ACharPatternCG)
		{
			ACharPatternCG charPattern = (ACharPatternCG) pattern;
			
			Character value = charPattern.getValue();
			ACharLiteralExpCG charLiteral = info.getExpAssistant().consCharLiteral(value);
			
			return consPatternCheck(charPattern, type, charLiteral);
		}
		else if(pattern instanceof AIntPatternCG)
		{
			AIntPatternCG intPattern = (AIntPatternCG) pattern;
			
			Long value = intPattern.getValue();
			AIntLiteralExpCG intLit = info.getExpAssistant().consIntLiteral(value);
			
			return consPatternCheck(intPattern, type, intLit);
		}
		else if(pattern instanceof ANullPatternCG)
		{	
			ANullExpCG nullExp = new ANullExpCG();
			nullExp.setType(new AObjectTypeCG());
			
			return consPatternCheck(pattern, type, new ANullExpCG());
		}
		else if(pattern instanceof AQuotePatternCG)
		{
			AQuotePatternCG quotePattern = (AQuotePatternCG) pattern;
			
			String value = quotePattern.getValue();
			AQuoteLiteralExpCG quoteLit = info.getExpAssistant().consQuoteLiteral(value);
			
			return consPatternCheck(pattern, type, quoteLit);	
		}
		else if(pattern instanceof ARealPatternCG)
		{
			ARealPatternCG realPattern = (ARealPatternCG) pattern;
			
			Double value = realPattern.getValue();
			ARealLiteralExpCG realLit = info.getExpAssistant().consRealLiteral(value);
			
			return consPatternCheck(realPattern, type, realLit);
			
		}
		else if(pattern instanceof AStringPatternCG)
		{
			AStringPatternCG stringPattern = (AStringPatternCG) pattern;
			String value = stringPattern.getValue();
			
			SExpCG stringValue = null;
			
			if(info.getSettings().getCharSeqAsString())
			{
				stringValue = info.getExpAssistant().consStringLiteral(value, false);
			}
			else
			{
				ASeqSeqTypeCG seqType = new ASeqSeqTypeCG();
				seqType.setEmpty(false);
				seqType.setSeqOf(new ACharBasicTypeCG());
				
				stringValue = info.getExpAssistant().consCharSequence(seqType, value);
			}
			
			return consPatternCheck(stringPattern, type, stringValue);
		}
		
		return null;
	}

	private <T> ABlockStmCG consPatternCheck(SPatternCG pattern, STypeCG type, SExpCG valueToMatch)
	{
		AIdentifierPatternCG idPattern = getIdPattern(config.getName(pattern.getClass()));
		transformationAssistant.replaceNodeWith(pattern, idPattern);

		AIdentifierVarExpCG var = new AIdentifierVarExpCG();
		var.setType(type.clone());
		var.setOriginal(idPattern.getName());
		var.setIsLambda(false);
		
		AEqualsBinaryExpCG check = new AEqualsBinaryExpCG();
		check.setType(new ABoolBasicTypeCG());
		check.setLeft(var);
		check.setRight(valueToMatch);
		
		String successVarName = info.getTempVarNameGen().nextVarName(varPrefixes.getSuccessVarNamePrefix());
		AVarLocalDeclCG successVarDecl = transformationAssistant.consDecl(successVarName, check);
		
		APatternMatchRuntimeErrorExpCG matchFail = new APatternMatchRuntimeErrorExpCG();
		matchFail.setType(new AErrorTypeCG());
		matchFail.setMessage(config.getMatchFailedMessage(pattern));
		ARaiseErrorStmCG noMatchStm = new ARaiseErrorStmCG();
		noMatchStm.setError(matchFail);
		
		AIfStmCG ifCheck = new AIfStmCG();
		ifCheck.setIfExp(transformationAssistant.consBoolCheck(successVarName, true));
		ifCheck.setThenStm(noMatchStm);
		
		ABlockStmCG block = new ABlockStmCG();
		block.getLocalDefs().add(successVarDecl);
		block.getStatements().add(ifCheck);
		
		return block;
	}
	
	public List<PatternInfo> extractFromLocalDefs(List<SLocalDeclCG> localDefs)
	{
		List<PatternInfo> patternInfo = new LinkedList<PatternInfo>();
		
		for(SLocalDeclCG decl : localDefs)
		{
			if(decl instanceof AVarLocalDeclCG)
			{
				AVarLocalDeclCG varDecl = (AVarLocalDeclCG) decl;
				
				STypeCG type = varDecl.getType();
				SPatternCG pattern = varDecl.getPattern();
				
				patternInfo.add(new PatternInfo(type, pattern));
			}
		}
		
		return patternInfo;
	}
	
	public List<PatternInfo> extractFromParams(List<AFormalParamLocalParamCG> params)
	{
		List<PatternInfo> patternInfo = new LinkedList<PatternInfo>();
		
		for(AFormalParamLocalParamCG param : params)
		{
			STypeCG type = param.getType();
			SPatternCG pattern = param.getPattern();
			
			patternInfo.add(new PatternInfo(type, pattern));
		}
		
		return patternInfo;
	}
	
	private AIdentifierPatternCG getIdPattern(String namePrefix)
	{
		String name = info.getTempVarNameGen().nextVarName(namePrefix);
		
		AIdentifierPatternCG idPattern = new AIdentifierPatternCG();
		idPattern.setName(name);
		return idPattern;
	}
}
