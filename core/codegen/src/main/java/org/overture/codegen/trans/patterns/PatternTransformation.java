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
import org.overture.codegen.cgast.expressions.AFieldNumberExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.AIntLiteralExpCG;
import org.overture.codegen.cgast.expressions.ANullExpCG;
import org.overture.codegen.cgast.expressions.APatternMatchRuntimeErrorExpCG;
import org.overture.codegen.cgast.expressions.AQuoteLiteralExpCG;
import org.overture.codegen.cgast.expressions.ARealLiteralExpCG;
import org.overture.codegen.cgast.expressions.ATupleSizeExpCG;
import org.overture.codegen.cgast.expressions.AUndefinedExpCG;
import org.overture.codegen.cgast.patterns.ABoolPatternCG;
import org.overture.codegen.cgast.patterns.ACharPatternCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.patterns.AIgnorePatternCG;
import org.overture.codegen.cgast.patterns.AIntPatternCG;
import org.overture.codegen.cgast.patterns.ANullPatternCG;
import org.overture.codegen.cgast.patterns.AQuotePatternCG;
import org.overture.codegen.cgast.patterns.ARealPatternCG;
import org.overture.codegen.cgast.patterns.AStringPatternCG;
import org.overture.codegen.cgast.patterns.ATuplePatternCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.statements.ALocalAssignmentStmCG;
import org.overture.codegen.cgast.statements.ARaiseErrorStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.AErrorTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.AObjectTypeCG;
import org.overture.codegen.cgast.types.ASeqSeqTypeCG;
import org.overture.codegen.cgast.types.ATupleTypeCG;
import org.overture.codegen.cgast.types.AUnknownTypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.logging.Logger;
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
				SPatternCG paramPattern = param.getPattern();
				
				if(!(paramPattern instanceof AIdentifierPatternCG))
				{
					String prefix = config.getName(param.getPattern().getClass());
					
					if(prefix != null)
					{
						AIdentifierPatternCG idPattern = getIdPattern(prefix);
						transformationAssistant.replaceNodeWith(param.getPattern(), idPattern);
					}
					else
					{
						Logger.getLog().printError("Could not find prefix for pattern: " + paramPattern);
					}
				}
			}
		}
	}
	
	@Override
	public void caseABlockStmCG(ABlockStmCG node) throws AnalysisException
	{
		List<PatternInfo> patternInfo = extractFromLocalDefs(node.getLocalDefs());
		
		ABlockStmCG patternHandlingBlock = consPatternHandlingBlock(patternInfo);
		
		if(!patternHandlingBlock.getStatements().isEmpty())
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
			SPatternCG currentPattern = info.getPattern();
			
			if (currentPattern instanceof AIgnorePatternCG)
			{
				AIdentifierPatternCG idPattern = getIdPattern(config.getIgnorePatternPrefix());
				transformationAssistant.replaceNodeWith(currentPattern, idPattern);
				
			} else if(!(currentPattern instanceof AIdentifierPatternCG))
			{
				String successVarName = this.info.getTempVarNameGen().nextVarName(varPrefixes.getSuccessVarNamePrefix());
				AUndefinedExpCG init = new AUndefinedExpCG();
				init.setType(new AUnknownTypeCG());
				AVarLocalDeclCG successVarDecl = transformationAssistant.consDecl(successVarName, new ABoolBasicTypeCG(), init);

				AIdentifierVarExpCG successVar = new AIdentifierVarExpCG();
				successVar.setIsLambda(false);
				successVar.setOriginal(successVarName);
				successVar.setType(new ABoolBasicTypeCG());

				patternHandlingBlock.getLocalDefs().add(successVarDecl);

				ABlockStmCG currentPatternHandlingBlock = consPatternCheck(false, currentPattern, info.getType(), successVar, successVarDecl, patternHandlingBlock);

				if (currentPatternHandlingBlock != null)
				{
					patternHandlingBlock.getStatements().add(currentPatternHandlingBlock);
				} else
				{
					Logger.getLog().printErrorln("Could not construct pattern handling block for pattern: "
							+ currentPattern);

				}

				APatternMatchRuntimeErrorExpCG matchFail = new APatternMatchRuntimeErrorExpCG();
				matchFail.setType(new AErrorTypeCG());
				matchFail.setMessage(config.getMatchFailedMessage(currentPattern));
				ARaiseErrorStmCG noMatchStm = new ARaiseErrorStmCG();
				noMatchStm.setError(matchFail);

				AIfStmCG ifCheck = new AIfStmCG();
				ifCheck.setIfExp(transformationAssistant.consBoolCheck(successVar.getOriginal(), true));
				ifCheck.setThenStm(noMatchStm);

				currentPatternHandlingBlock.getStatements().add(ifCheck);
			}
		}
		
		return patternHandlingBlock;
	}
	
	private ABlockStmCG consPatternCheck(boolean declarePatternVar, SPatternCG pattern, STypeCG type, AIdentifierVarExpCG successVar, AVarLocalDeclCG successVarDecl, ABlockStmCG declBlock)
	{
		if(pattern instanceof ABoolPatternCG)
		{
			ABoolPatternCG boolPattern = (ABoolPatternCG) pattern; 
			
			Boolean value = boolPattern.getValue();
			ABoolLiteralExpCG consBoolLiteral = info.getExpAssistant().consBoolLiteral(value);
	
			return consPatternCheck(declarePatternVar, boolPattern, type,  consBoolLiteral, successVar, successVarDecl);
		}
		else if(pattern instanceof ACharPatternCG)
		{
			ACharPatternCG charPattern = (ACharPatternCG) pattern;
			
			Character value = charPattern.getValue();
			ACharLiteralExpCG charLiteral = info.getExpAssistant().consCharLiteral(value);
			
			return consPatternCheck(declarePatternVar, charPattern, type, charLiteral, successVar, successVarDecl);
		}
		else if(pattern instanceof AIntPatternCG)
		{
			AIntPatternCG intPattern = (AIntPatternCG) pattern;
			
			Long value = intPattern.getValue();
			AIntLiteralExpCG intLit = info.getExpAssistant().consIntLiteral(value);
			
			return consPatternCheck(declarePatternVar, intPattern, type, intLit, successVar, successVarDecl);
		}
		else if(pattern instanceof ANullPatternCG)
		{	
			ANullExpCG nullExp = new ANullExpCG();
			nullExp.setType(new AObjectTypeCG());
			
			return consPatternCheck(declarePatternVar, pattern, type, new ANullExpCG(), successVar, successVarDecl);
		}
		else if(pattern instanceof AQuotePatternCG)
		{
			AQuotePatternCG quotePattern = (AQuotePatternCG) pattern;
			
			String value = quotePattern.getValue();
			AQuoteLiteralExpCG quoteLit = info.getExpAssistant().consQuoteLiteral(value);
			
			return consPatternCheck(declarePatternVar, pattern, type, quoteLit, successVar, successVarDecl);	
		}
		else if(pattern instanceof ARealPatternCG)
		{
			ARealPatternCG realPattern = (ARealPatternCG) pattern;
			
			Double value = realPattern.getValue();
			ARealLiteralExpCG realLit = info.getExpAssistant().consRealLiteral(value);
			
			return consPatternCheck(declarePatternVar, realPattern, type, realLit, successVar, successVarDecl);
			
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
			
			return consPatternCheck(declarePatternVar, stringPattern, type, stringValue, successVar, successVarDecl);
		}
		else if(pattern instanceof ATuplePatternCG)
		{
			ATuplePatternCG tuplePattern = (ATuplePatternCG) pattern;
			ATupleTypeCG tupleType = (ATupleTypeCG) type;
			
			return consTuplePatternCheck(tuplePattern, tupleType, successVar, successVarDecl, declBlock, null);
		}
		
		return null;
	}

	private ABlockStmCG consTuplePatternCheck(
			ATuplePatternCG tuplePattern, ATupleTypeCG tupleType, AIdentifierVarExpCG successVar,
			AVarLocalDeclCG successVarDecl, ABlockStmCG declBlock,
			AFieldNumberExpCG valueToMatch)
	{
		AIdentifierPatternCG idPattern = getIdPattern(config.getName(tuplePattern.getClass()));
		
		ABlockStmCG tuplePatternBlock = new ABlockStmCG();
		
		if(valueToMatch != null)
		{
			AVarLocalDeclCG tuplePatternDecl = new AVarLocalDeclCG();
			tuplePatternDecl.setType(tupleType);
			tuplePatternDecl.setExp(valueToMatch);
			tuplePatternDecl.setPattern(idPattern);
			
			tuplePatternBlock.getLocalDefs().add(tuplePatternDecl);
		}
		else
		{
			transformationAssistant.replaceNodeWith(tuplePattern, idPattern);
		}
		
		AIdentifierVarExpCG tuplePatternVar = new AIdentifierVarExpCG();
		tuplePatternVar.setType(tupleType.clone());
		tuplePatternVar.setOriginal(idPattern.getName());
		tuplePatternVar.setIsLambda(false);
		
		ATupleSizeExpCG tupleSize = new ATupleSizeExpCG();
		tupleSize.setType(new AIntNumericBasicTypeCG());
		tupleSize.setTuple(tuplePatternVar);
		
		AEqualsBinaryExpCG fieldSizeComp = new AEqualsBinaryExpCG();
		fieldSizeComp.setType(new ABoolBasicTypeCG());
		fieldSizeComp.setLeft(tupleSize);
		fieldSizeComp.setRight(info.getExpAssistant().consIntLiteral(tupleType.getTypes().size()));

		if(successVarDecl.getExp() instanceof AUndefinedExpCG)
		{
			successVarDecl.setExp(fieldSizeComp);
		}
		else
		{
			ALocalAssignmentStmCG successVarAssignment = new ALocalAssignmentStmCG();
			successVarAssignment = new ALocalAssignmentStmCG();
			successVarAssignment.setTarget(successVar.clone());
			successVarAssignment.setExp(fieldSizeComp);
			
			tuplePatternBlock.getStatements().add(successVarAssignment);
		}
		
		//First, the tuple pattern check requires the right number of fields
		//success_2 = tuplePattern_2.size().longValue() == 3L;
		//if (success_2) { ... }
		LinkedList<SPatternCG> patterns = tuplePattern.getPatterns();
		LinkedList<STypeCG> types = tupleType.getTypes();
		
		AIfStmCG fieldSizeCheck = new AIfStmCG();
		fieldSizeCheck.setIfExp(successVar.clone());
		fieldSizeCheck.setThenStm(consFieldCheckBlock(successVar, successVarDecl, declBlock, tuplePatternVar, patterns, types));
		
		tuplePatternBlock.getStatements().add(fieldSizeCheck);
		
		return tuplePatternBlock;
	}

	private ABlockStmCG consFieldCheckBlock(AIdentifierVarExpCG successVar,
			AVarLocalDeclCG successVarDecl, ABlockStmCG declBlock,
			AIdentifierVarExpCG tuplePatternVar,
			LinkedList<SPatternCG> patterns, LinkedList<STypeCG> types)
	{
		ABlockStmCG thenPart = new ABlockStmCG();
		ABlockStmCG topBlock = thenPart;
		
		for(int i = 0; i < patterns.size(); i++)
		{
			SPatternCG currentPattern = patterns.get(i);
			STypeCG currentType = types.get(i);
			
			if(currentPattern instanceof AIgnorePatternCG)
			{
				continue;
			}
			else if(currentPattern instanceof AIdentifierPatternCG)
			{
				//Now, read the field
				//Example:  a = ((Number) tuplePattern_1.get(0));
				
				AIdentifierPatternCG currentId = (AIdentifierPatternCG) currentPattern;
				
				AVarLocalDeclCG varDecl = new AVarLocalDeclCG();
				varDecl.setType(currentType.clone());
				varDecl.setPattern(currentPattern.clone());
				varDecl.setExp(new AUndefinedExpCG());
				
				declBlock.getLocalDefs().add(varDecl);
				
				AIdentifierVarExpCG var = new AIdentifierVarExpCG();
				var.setType(currentType.clone());
				var.setOriginal(currentId.getName());
				var.setIsLambda(false);

				AFieldNumberExpCG fieldNumberExp = consTupleFieldExp(tuplePatternVar, i, currentType);
				
				ALocalAssignmentStmCG localAssignment = new ALocalAssignmentStmCG();
				localAssignment.setTarget(var);
				localAssignment.setExp(fieldNumberExp);
			
				thenPart.getStatements().add(localAssignment);
			}
			else
			{
				ABlockStmCG patternBlock = null;
				
				if (currentPattern instanceof ATuplePatternCG)
				{
					ATuplePatternCG nextTuplePattern = (ATuplePatternCG) currentPattern;
					ATupleTypeCG nextTupleType = (ATupleTypeCG) currentType;
					
					//The next tuple pattern is a field of the previous tuple pattern
					//Example:  Tuple tuplePattern_2 = ((Tuple) tuplePattern_1.get(2))
					AFieldNumberExpCG valueToMath = consTupleFieldExp(tuplePatternVar, i, currentType);
					
					patternBlock = consTuplePatternCheck(nextTuplePattern, nextTupleType, successVar, successVarDecl, declBlock, valueToMath);
					
				} else
				{
					patternBlock = consPatternCheck(true, currentPattern, currentType, successVar, successVarDecl, declBlock);
				}
				
				if (patternBlock != null)
				{
					thenPart.getStatements().add(patternBlock);

					//The tuple pattern have more field patterns to be generated.
					//Check the success variable and add a new nesting level
					if (i < patterns.size() - 1)
					{
						AIfStmCG successVarCheck = new AIfStmCG();
						successVarCheck.setIfExp(successVar.clone());

						thenPart.getStatements().add(successVarCheck);

						ABlockStmCG newThenPart = new ABlockStmCG();
						successVarCheck.setThenStm(newThenPart);

						thenPart = newThenPart;
					}
				}
			}
		}
		
		return topBlock;
	}

	private <T> ABlockStmCG consPatternCheck(boolean declarePatternVar, SPatternCG pattern, STypeCG type, SExpCG valueToMatch, AIdentifierVarExpCG successVar, AVarLocalDeclCG successVarDecl)
	{
		// Example:
		// Number intPattern_2 = 1L;
        // Boolean success_2 = intPattern_2.longValue() == 1L;

		AIdentifierPatternCG idPattern = getIdPattern(config.getName(pattern.getClass()));
		transformationAssistant.replaceNodeWith(pattern, idPattern);
		
		ABlockStmCG block = new ABlockStmCG();
		
		if (declarePatternVar)
		{
			AVarLocalDeclCG patternDecl = new AVarLocalDeclCG();
			patternDecl.setPattern(idPattern.clone());
			patternDecl.setType(type.clone());
			patternDecl.setExp(valueToMatch.clone());
			
			block.getLocalDefs().add(patternDecl);
		}

		AIdentifierVarExpCG var = new AIdentifierVarExpCG();
		var.setType(type.clone());
		var.setOriginal(idPattern.getName());
		var.setIsLambda(false);
		
		AEqualsBinaryExpCG check = new AEqualsBinaryExpCG();
		check.setType(new ABoolBasicTypeCG());
		check.setLeft(var);
		check.setRight(valueToMatch);
		
		if(successVarDecl.getExp() instanceof AUndefinedExpCG)
		{
			successVarDecl.setExp(check);
		}
		else
		{
			ALocalAssignmentStmCG successAssignment = new ALocalAssignmentStmCG();
			successAssignment.setTarget(successVar.clone());
			successAssignment.setExp(check);
			
			block.getStatements().add(successAssignment);
		}
		
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
	
	private AFieldNumberExpCG consTupleFieldExp(AIdentifierVarExpCG tuplePatternVar,
			int i, STypeCG currentType)
	{
		AFieldNumberExpCG fieldNumberExp = new AFieldNumberExpCG();
		fieldNumberExp.setType(currentType.clone());
		fieldNumberExp.setTuple(tuplePatternVar.clone());
		fieldNumberExp.setField(new Long(1+i));
		
		return fieldNumberExp;
	}
}
