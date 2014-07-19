package org.overture.codegen.trans.patterns;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;
import org.overture.codegen.cgast.declarations.SLocalDeclCG;
import org.overture.codegen.cgast.expressions.ABoolLiteralExpCG;
import org.overture.codegen.cgast.expressions.ACharLiteralExpCG;
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
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
import org.overture.codegen.cgast.patterns.ARecordPatternCG;
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
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.cgast.types.ASeqSeqTypeCG;
import org.overture.codegen.cgast.types.ATupleTypeCG;
import org.overture.codegen.cgast.types.AUnknownTypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.trans.assistants.TransformationAssistantCG;

public class PatternTransformation extends DepthFirstAnalysisAdaptor
{
	
	private List<AClassDeclCG> classes;
	private IRInfo info;
	private TransformationAssistantCG transformationAssistant;

	private PatternMatchConfig config;
	
	private TempVarPrefixes varPrefixes;
	
	public PatternTransformation(List<AClassDeclCG> classes, TempVarPrefixes varPrefixes, IRInfo info, TransformationAssistantCG transformationAssistant, PatternMatchConfig config)
	{
		this.classes = classes;
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
		
		if (!patternInfo.isEmpty())
		{
			ABlockStmCG patternHandlingBlock = consPatternHandlingBlock(patternInfo);

			if (!patternHandlingBlock.getStatements().isEmpty())
			{
				node.getStatements().addFirst(patternHandlingBlock);
			}
		}
		
		for(SStmCG stm : node.getStatements())
		{
			stm.apply(this);
		}
	}

	private ABlockStmCG consPatternHandlingBlock(List<PatternInfo> patternInfo)
	{
		ABlockStmCG topBlock = new ABlockStmCG();
		
		for (PatternInfo info : patternInfo)
		{
			SPatternCG currentPattern = info.getPattern();
			
			if(currentPattern instanceof AIdentifierPatternCG)
			{
				continue;
			}
			else if (currentPattern instanceof AIgnorePatternCG)
			{
				AIdentifierPatternCG idPattern = getIdPattern(config.getIgnorePatternPrefix());
				transformationAssistant.replaceNodeWith(currentPattern, idPattern);
				
			} else 
			{
				ABlockStmCG currentDeclBlock = new ABlockStmCG();
				
				ABlockStmCG patternHandlingBlock = consPatternCheck(currentPattern, info.getType(), currentDeclBlock);
				currentDeclBlock.getStatements().addFirst(patternHandlingBlock);
				topBlock.getStatements().add(currentDeclBlock);
			}
		}
		
		return topBlock;
	}
	
	private ABlockStmCG consPatternCheck(SPatternCG pattern, STypeCG type, ABlockStmCG declBlock)
	{
		boolean declareVarPattern = false;
		PatternBlockData patternData = new PatternBlockData(pattern, declBlock);
		
		return consPatternCheck(declareVarPattern, pattern, type, patternData); 
	}
	
	private ABlockStmCG consPatternCheck(boolean declarePatternVar, SPatternCG pattern, STypeCG type, PatternBlockData patternData)
	{
		if(pattern instanceof ABoolPatternCG)
		{
			ABoolPatternCG boolPattern = (ABoolPatternCG) pattern; 
			
			Boolean value = boolPattern.getValue();
			ABoolLiteralExpCG consBoolLiteral = info.getExpAssistant().consBoolLiteral(value);
	
			return consSimplePatternCheck(declarePatternVar, boolPattern, consBoolLiteral, patternData);
		}
		else if(pattern instanceof ACharPatternCG)
		{
			ACharPatternCG charPattern = (ACharPatternCG) pattern;
			
			Character value = charPattern.getValue();
			ACharLiteralExpCG charLiteral = info.getExpAssistant().consCharLiteral(value);
			
			return consSimplePatternCheck(declarePatternVar, charPattern, charLiteral, patternData);
		}
		else if(pattern instanceof AIntPatternCG)
		{
			AIntPatternCG intPattern = (AIntPatternCG) pattern;
			
			Long value = intPattern.getValue();
			AIntLiteralExpCG intLit = info.getExpAssistant().consIntLiteral(value);
			
			return consSimplePatternCheck(declarePatternVar, intPattern, intLit, patternData);
		}
		else if(pattern instanceof ANullPatternCG)
		{	
			ANullExpCG nullExp = new ANullExpCG();
			nullExp.setType(new AUnknownTypeCG());
			
			return consSimplePatternCheck(declarePatternVar, pattern, nullExp, patternData);
		}
		else if(pattern instanceof AQuotePatternCG)
		{
			AQuotePatternCG quotePattern = (AQuotePatternCG) pattern;
			
			String value = quotePattern.getValue();
			AQuoteLiteralExpCG quoteLit = info.getExpAssistant().consQuoteLiteral(value);
			
			return consSimplePatternCheck(declarePatternVar, pattern, quoteLit, patternData);	
		}
		else if(pattern instanceof ARealPatternCG)
		{
			ARealPatternCG realPattern = (ARealPatternCG) pattern;
			
			Double value = realPattern.getValue();
			ARealLiteralExpCG realLit = info.getExpAssistant().consRealLiteral(value);
			
			return consSimplePatternCheck(declarePatternVar, realPattern, realLit, patternData);
			
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
			
			return consSimplePatternCheck(declarePatternVar, stringPattern, stringValue, patternData);
		}
		else if(pattern instanceof ATuplePatternCG)
		{
			ATuplePatternCG tuplePattern = (ATuplePatternCG) pattern;
			ATupleTypeCG tupleType = (ATupleTypeCG) type;
			
			return consTuplePatternCheck(tuplePattern, tupleType, patternData, null);
		}
		else if(pattern instanceof ARecordPatternCG)
		{
			ARecordPatternCG recordPattern = (ARecordPatternCG) pattern;
			ARecordTypeCG recordType = (ARecordTypeCG) type;
			
			return consRecordPatternCheck(recordPattern, recordType, patternData, null);
		}
		
		return null;
	}

	private ABlockStmCG consRecordPatternCheck(
			ARecordPatternCG recordPattern, ARecordTypeCG recordType, PatternBlockData patternData,
			AFieldExpCG valueToMatch)
	{
		AIdentifierPatternCG idPattern = getIdPattern(config.getName(recordPattern.getClass()));
		
		ABlockStmCG recordPatternBlock = declarePattern(recordPattern, recordType, valueToMatch, idPattern);

		ARecordDeclCG record = info.getAssistantManager().getDeclAssistant().findRecord(classes, recordType);
		
		List<STypeCG> types = new LinkedList<STypeCG>();
		
		for(AFieldDeclCG currentField : record.getFields())
		{
			types.add(currentField.getType());
		}
		
		AIdentifierVarExpCG recordPatternVar = new AIdentifierVarExpCG();
		recordPatternVar.setType(recordType.clone());
		recordPatternVar.setOriginal(idPattern.getName());
		recordPatternVar.setIsLambda(false);
		
		ABlockStmCG fieldCheckBlock = consFieldCheckBlock(patternData, recordPatternVar, recordPattern.getPatterns(), types);
		
		recordPatternBlock.getStatements().add(fieldCheckBlock);
		
		return recordPatternBlock;

	}

	private ABlockStmCG consTuplePatternCheck(
			ATuplePatternCG tuplePattern, ATupleTypeCG tupleType, PatternBlockData patternData,
			AFieldNumberExpCG valueToMatch)
	{
		AIdentifierPatternCG idPattern = getIdPattern(config.getName(tuplePattern.getClass()));
		
		ABlockStmCG tuplePatternBlock = declarePattern(tuplePattern, tupleType, valueToMatch, idPattern);
		
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

		if(patternData.getSuccessVarDecl() == null)
		{
			consSuccessVarCheck(tuplePattern, patternData);
		}
		
		initSuccessVar(patternData, fieldSizeComp, tuplePatternBlock);
		
		//First, the tuple pattern check requires the right number of fields
		//success_2 = tuplePattern_2.size().longValue() == 3L;
		//if (success_2) { ... }
		LinkedList<SPatternCG> patterns = tuplePattern.getPatterns();
		LinkedList<STypeCG> types = tupleType.getTypes();
		
		AIfStmCG fieldSizeCheck = new AIfStmCG();
		fieldSizeCheck.setIfExp(patternData.getSuccessVar().clone());
		fieldSizeCheck.setThenStm(consFieldCheckBlock(patternData, tuplePatternVar, patterns, types));
		
		tuplePatternBlock.getStatements().add(fieldSizeCheck);
		
		return tuplePatternBlock;
	}
	
	private void consSuccessVarCheck(SPatternCG pattern, PatternBlockData patternData)
	{
		String successVarName = this.info.getTempVarNameGen().nextVarName(varPrefixes.getSuccessVarNamePrefix());
		SExpCG init = null;
		
		if (!patternData.IsRootPattern(pattern))
		{
			init = info.getExpAssistant().consBoolLiteral(false);
			init.setType(new ABoolBasicTypeCG());
		}
		else
		{
			init = new AUndefinedExpCG();
			init.setType(new AUnknownTypeCG());
		}
		
		AVarLocalDeclCG successVarDecl = transformationAssistant.consDecl(successVarName, new ABoolBasicTypeCG(), init);
		patternData.setSuccessVarDecl(successVarDecl);
		
		AIdentifierVarExpCG successVar = new AIdentifierVarExpCG();
		successVar.setIsLambda(false);
		successVar.setOriginal(successVarName);
		successVar.setType(new ABoolBasicTypeCG());
		patternData.setSuccessVar(successVar);

		patternData.getDeclBlock().getLocalDefs().add(successVarDecl);

		APatternMatchRuntimeErrorExpCG matchFail = new APatternMatchRuntimeErrorExpCG();
		matchFail.setType(new AErrorTypeCG());
		matchFail.setMessage(config.getMatchFailedMessage(pattern));
		ARaiseErrorStmCG noMatchStm = new ARaiseErrorStmCG();
		noMatchStm.setError(matchFail);

		AIfStmCG ifCheck = new AIfStmCG();
		ifCheck.setIfExp(transformationAssistant.consBoolCheck(successVar.getOriginal(), true));
		ifCheck.setThenStm(noMatchStm);

		patternData.getDeclBlock().getStatements().add(ifCheck);
	}
	
	private void initSuccessVar(PatternBlockData patternData, SExpCG initExp, ABlockStmCG patternBlock)
	{
		if(patternData.getSuccessVarDecl().getExp() instanceof AUndefinedExpCG)
		{
			patternData.getSuccessVarDecl().setExp(initExp);
		}
		else
		{
			ALocalAssignmentStmCG successVarAssignment = new ALocalAssignmentStmCG();
			successVarAssignment.setTarget(patternData.getSuccessVar().clone());
			successVarAssignment.setExp(initExp);
			
			patternBlock.getStatements().add(successVarAssignment);
		}
	}
	
	private ABlockStmCG declarePattern(SPatternCG pattern,
			STypeCG type, SExpCG valueToMatch,
			AIdentifierPatternCG idPattern)
	{
		ABlockStmCG patternBlock = new ABlockStmCG();
		
		if(valueToMatch != null)
		{
			AVarLocalDeclCG patternDecl = consVarDecl(type.clone(), valueToMatch, idPattern.clone());
			patternBlock.getLocalDefs().add(patternDecl);
		}
		else
		{
			transformationAssistant.replaceNodeWith(pattern, idPattern);
		}
		
		return patternBlock;
	}

	private AVarLocalDeclCG consVarDecl(STypeCG type, SExpCG valueToMatch,
			AIdentifierPatternCG idPattern)
	{
		AVarLocalDeclCG patternDecl = new AVarLocalDeclCG();
		patternDecl.setType(type);
		patternDecl.setExp(valueToMatch);
		patternDecl.setPattern(idPattern);
		return patternDecl;
	}

	private ABlockStmCG consFieldCheckBlock(PatternBlockData patternData,
			AIdentifierVarExpCG patternVar,
			List<SPatternCG> patterns, List<STypeCG> types)
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
				SExpCG valueToMatch = consFieldValueToMatch(patternVar, i, currentType);

				ALocalAssignmentStmCG localAssignment = consIdVarAssignment(patternData.getDeclBlock(), currentPattern, currentType, valueToMatch);
				thenPart.getStatements().add(localAssignment);
			}
			else
			{
				ABlockStmCG patternBlock = consPatternBlock(patternData, patternVar, i, currentPattern, currentType);
				
				if (patternBlock != null)
				{
					thenPart.getStatements().add(patternBlock);

					//The tuple pattern have more field patterns to be generated.
					//Check the success variable and add a new nesting level
					if (i < patterns.size() - 1)
					{
						AIfStmCG successVarCheck = new AIfStmCG();
						successVarCheck.setIfExp(patternData.getSuccessVar().clone());

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

	private ABlockStmCG consPatternBlock(PatternBlockData patternData,
			AIdentifierVarExpCG patternVar, int i, SPatternCG currentPattern,
			STypeCG currentType)
	{
		ABlockStmCG patternBlock = null;
		
		if (currentPattern instanceof ATuplePatternCG)
		{
			ATuplePatternCG nextTuplePattern = (ATuplePatternCG) currentPattern;
			ATupleTypeCG nextTupleType = (ATupleTypeCG) currentType;

			// The next tuple pattern is a field of the previous tuple pattern
			// Example: Tuple tuplePattern_2 = ((Tuple) tuplePattern_1.get(2))
			AFieldNumberExpCG valueToMath = consTupleFieldExp(patternVar, i, currentType);

			patternBlock = consTuplePatternCheck(nextTuplePattern, nextTupleType, patternData, valueToMath);
			
		}
		else if (currentPattern instanceof ARecordPatternCG)
		{
			ARecordPatternCG nextRecordPattern = (ARecordPatternCG) currentPattern;
			ARecordTypeCG nextRecordType = (ARecordTypeCG) currentType;
			
			AFieldExpCG valueToMatch = consRecFieldExp(patternVar, i, currentType);
			
			patternBlock = consRecordPatternCheck(nextRecordPattern, nextRecordType, patternData, valueToMatch);
		}
		else
		{
			patternBlock = consPatternCheck(true, currentPattern, currentType, patternData);
		}
		
		return patternBlock;
	}

	private SExpCG consFieldValueToMatch(AIdentifierVarExpCG patternVar, int i,
			STypeCG currentType)
	{
		SExpCG valueToMatch = null;
		
		if (patternVar.getType() instanceof ATupleTypeCG)
		{
			// Now, read the field
			// Example: a = ((Number) tuplePattern_1.get(0));
			valueToMatch = consTupleFieldExp(patternVar, i, currentType);
		}
		else if (patternVar.getType() instanceof ARecordTypeCG)
		{
			valueToMatch = consRecFieldExp(patternVar, i, currentType);
		}
		
		return valueToMatch;
	}

	private ALocalAssignmentStmCG consIdVarAssignment(ABlockStmCG declBlock,
			SPatternCG currentPattern, STypeCG currentType, SExpCG valueToMatch)
	{
		AIdentifierPatternCG currentId = (AIdentifierPatternCG) currentPattern;

		AVarLocalDeclCG idVarDecl = new AVarLocalDeclCG();
		idVarDecl.setType(currentType.clone());
		idVarDecl.setPattern(currentPattern.clone());
		idVarDecl.setExp(new AUndefinedExpCG());

		declBlock.getLocalDefs().add(idVarDecl);
		
		AIdentifierVarExpCG var = new AIdentifierVarExpCG();
		var.setType(currentType.clone());
		var.setOriginal(currentId.getName());
		var.setIsLambda(false);
		
		ALocalAssignmentStmCG localAssignment = new ALocalAssignmentStmCG();
		localAssignment.setTarget(var);
		localAssignment.setExp(valueToMatch);
		
		return localAssignment;
	}

	private <T> ABlockStmCG consSimplePatternCheck(boolean declarePatternVar, SPatternCG pattern, SExpCG valueToMatch, PatternBlockData patternData)
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
			patternDecl.setType(valueToMatch.getType().clone());
			patternDecl.setExp(valueToMatch.clone());
			
			block.getLocalDefs().add(patternDecl);
		}

		AIdentifierVarExpCG var = new AIdentifierVarExpCG();
		var.setType(valueToMatch.getType().clone());
		var.setOriginal(idPattern.getName());
		var.setIsLambda(false);
		
		AEqualsBinaryExpCG check = new AEqualsBinaryExpCG();
		check.setType(new ABoolBasicTypeCG());
		check.setLeft(var);
		check.setRight(valueToMatch);
		
		if(patternData.getSuccessVarDecl() == null)
		{
			consSuccessVarCheck(pattern, patternData);
		}
		
		initSuccessVar(patternData, check, block);
		
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

	private AFieldExpCG consRecFieldExp(AIdentifierVarExpCG patternVar, int i,
			STypeCG currentType)
	{
		ARecordTypeCG recordType = (ARecordTypeCG) patternVar.getType();
		
		AFieldDeclCG recordField = info.getAssistantManager().getDeclAssistant().getFieldDecl(classes, recordType, i);
		String fieldName = recordField.getName();
		
		AFieldExpCG fieldExp = consRecFieldExp(patternVar, currentType, fieldName);
		
		return fieldExp;
	}
	
	private AFieldExpCG consRecFieldExp(AIdentifierVarExpCG patternVar,
			STypeCG currentType, String fieldName)
	{
		AFieldExpCG fieldExp = new AFieldExpCG();
		fieldExp.setType(currentType.clone());
		fieldExp.setObject(patternVar.clone());
		fieldExp.setMemberName(fieldName);
		
		return fieldExp;
	}
}
