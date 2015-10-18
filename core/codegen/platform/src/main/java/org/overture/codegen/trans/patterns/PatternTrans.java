/*
 * #%~
 * VDM Code Generator
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.codegen.trans.patterns;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.PCG;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.ABoolLiteralExpCG;
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG;
import org.overture.codegen.cgast.expressions.ACharLiteralExpCG;
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AFieldNumberExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.AInstanceofExpCG;
import org.overture.codegen.cgast.expressions.AIntLiteralExpCG;
import org.overture.codegen.cgast.expressions.ANotUnaryExpCG;
import org.overture.codegen.cgast.expressions.APatternMatchRuntimeErrorExpCG;
import org.overture.codegen.cgast.expressions.AQuoteLiteralExpCG;
import org.overture.codegen.cgast.expressions.ARealLiteralExpCG;
import org.overture.codegen.cgast.expressions.ATupleCompatibilityExpCG;
import org.overture.codegen.cgast.expressions.AUndefinedExpCG;
import org.overture.codegen.cgast.expressions.SVarExpCG;
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
import org.overture.codegen.cgast.statements.AAssignToExpStmCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.ACaseAltStmStmCG;
import org.overture.codegen.cgast.statements.ACasesStmCG;
import org.overture.codegen.cgast.statements.AContinueStmCG;
import org.overture.codegen.cgast.statements.AForAllStmCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.statements.ALocalPatternAssignmentStmCG;
import org.overture.codegen.cgast.statements.ARaiseErrorStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.AErrorTypeCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.cgast.types.ASeqSeqTypeCG;
import org.overture.codegen.cgast.types.ATupleTypeCG;
import org.overture.codegen.cgast.types.AUnionTypeCG;
import org.overture.codegen.cgast.types.AUnknownTypeCG;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.DeclarationTag;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class PatternTrans extends DepthFirstAnalysisAdaptor
{
	private TransAssistantCG transAssistant;

	private PatternVarPrefixes config;

	private IterationVarPrefixes iteVarPrefixes;

	private String casesExpNamePrefix;
	
	public PatternTrans(IterationVarPrefixes iteVarPrefixes,
			TransAssistantCG transAssistant, PatternVarPrefixes config,
			String casesExpNamePrefix)
	{
		this.transAssistant = transAssistant;
		this.iteVarPrefixes = iteVarPrefixes;

		this.config = config;

		this.casesExpNamePrefix = casesExpNamePrefix;
	}

	@Override
	public void caseALocalPatternAssignmentStmCG(
			ALocalPatternAssignmentStmCG node) throws AnalysisException
	{
		AVarDeclCG nextElementDecl = node.getNextElementDecl();
		SPatternCG pattern = nextElementDecl.getPattern();

		if (pattern instanceof AIdentifierPatternCG)
		{
			return;
		}
		
		if (pattern  instanceof AIgnorePatternCG)
		{
			AIdentifierPatternCG idPattern = getIdPattern(config.getIgnorePatternPrefix());
			transAssistant.replaceNodeWith(node.getTarget(), idPattern);
			transAssistant.replaceNodeWith(pattern, idPattern.clone());
			return;
		}

		DeclarationTag tag = fetchTag(node);

		ABlockStmCG replacementBlock = consPatternHandlingInIterationBlock(nextElementDecl, tag, node.getExp());
		transAssistant.replaceNodeWith(node, replacementBlock);
	}

	@Override
	public void caseACasesStmCG(ACasesStmCG node) throws AnalysisException
	{
		List<ACaseAltStmStmCG> nodeCases = node.getCases();
		SPatternCG firstOriginal = nodeCases.get(0).getPattern().clone();
		
		ABlockStmCG replacementBlock = new ABlockStmCG();
		String expName = transAssistant.getInfo().getTempVarNameGen().nextVarName(casesExpNamePrefix);

		SExpCG exp = node.getExp();
		
		if (!(node.getExp() instanceof SVarExpCG))
		{
			AVarDeclCG expVarDecl = transAssistant.getInfo().getDeclAssistant().consLocalVarDecl(node.getExp().getType().clone(),
					transAssistant.getInfo().getPatternAssistant().consIdPattern(expName), node.getExp().clone());
			replacementBlock.getLocalDefs().add(expVarDecl);
			exp = transAssistant.getInfo().getExpAssistant().consIdVar(expName,
					node.getExp().getType().clone());
		}
		
		List<PatternInfo> patternInfo = extractFromCases(nodeCases, exp);
		PatternBlockData patternData = new PatternBlockData(MismatchHandling.NONE);

		List<ABlockStmCG> blocks = consPatternHandlingBlockCases(patternInfo, patternData);

		replacementBlock.getStatements().add(blocks.get(0));

		ANotUnaryExpCG notSuccess = transAssistant.getInfo().getExpAssistant().negate(patternData.getSuccessVar());

		AIfStmCG ifStm = new AIfStmCG();
		ABlockStmCG enclosingIf = new ABlockStmCG();
		enclosingIf.getStatements().add(ifStm);
		replacementBlock.getStatements().add(enclosingIf);

		ifStm.setIfExp(notSuccess);

		AIfStmCG nextCase = ifStm;

		if (nodeCases.size() > 1)
		{
			ifStm.setElseStm(nodeCases.get(0).getResult().clone());
			
			nextCase = new AIfStmCG();

			enclosingIf = new ABlockStmCG();
			ifStm.setThenStm(enclosingIf);
			enclosingIf.getStatements().add(nextCase);

			// All cases except for the first and the last
			for (int i = 1; i < nodeCases.size() - 1; i++)
			{
				enclosingIf.getStatements().addFirst(blocks.get(i));
				enclosingIf = new ABlockStmCG();

				ACaseAltStmStmCG currentCase = nodeCases.get(i);
				nextCase.setIfExp(notSuccess.clone());
				nextCase.setElseStm(currentCase.getResult().clone());

				AIfStmCG tmp = new AIfStmCG();
				enclosingIf.getStatements().add(tmp);

				nextCase.setThenStm(enclosingIf);
				nextCase = tmp;
			}
		}
		else
		{
			APatternMatchRuntimeErrorExpCG matchFail = new APatternMatchRuntimeErrorExpCG();
			matchFail.setType(new AErrorTypeCG());
			matchFail.setMessage(config.getMatchFailedMessage(firstOriginal));
			ARaiseErrorStmCG noMatchStm = new ARaiseErrorStmCG();
			noMatchStm.setError(matchFail);
			
			ifStm.setElseStm(noMatchStm);
		}

		enclosingIf.getStatements().addFirst(blocks.get(blocks.size() - 1));
		nextCase.setIfExp(patternData.getSuccessVar().clone());
		nextCase.setThenStm(nodeCases.get(nodeCases.size() - 1).getResult().clone());

		if (node.getOthers() != null)
		{
			nextCase.setElseStm(node.getOthers().clone());
		}

		transAssistant.replaceNodeWith(node, replacementBlock);

		ifStm.apply(this);
	}

	@Override
	public void caseAMethodDeclCG(AMethodDeclCG node) throws AnalysisException
	{
		List<PatternInfo> patternInfo = extractFromParams(node.getFormalParams());

		if (!node.getAbstract() && node.getBody() != null)
		{
			ABlockStmCG patternHandlingBlock = consPatternHandlingBlock(patternInfo);

			ABlockStmCG newBody = new ABlockStmCG();
			newBody.getStatements().add(patternHandlingBlock);

			SStmCG oldBody = node.getBody();
			transAssistant.replaceNodeWith(oldBody, newBody);
			newBody.getStatements().add(oldBody);

			newBody.apply(this);
		} else
		{
			for (AFormalParamLocalParamCG param : node.getFormalParams())
			{
				SPatternCG paramPattern = param.getPattern();

				if (!(paramPattern instanceof AIdentifierPatternCG))
				{
					String prefix = config.getName(param.getPattern().getClass());

					if (prefix != null)
					{
						AIdentifierPatternCG idPattern = getIdPattern(prefix);
						transAssistant.replaceNodeWith(param.getPattern(), idPattern);
					} else
					{
						Logger.getLog().printError("Could not find prefix for pattern: "
								+ paramPattern);
					}
				}
			}
		}
	}

	@Override
	public void caseABlockStmCG(ABlockStmCG node) throws AnalysisException
	{
		boolean taggedBlock = false;
		for (int i = 0; i < node.getLocalDefs().size(); i++)
		{
			AVarDeclCG dec = node.getLocalDefs().get(i);

			if (dec.getTag() != null)
			{
				taggedBlock = true;

				DeclarationTag tag = fetchTag(dec);

				if (tag.isDeclared() || !(dec instanceof AVarDeclCG))
				{
					continue;
				}

				AVarDeclCG nextElementDecl = (AVarDeclCG) dec;

				SPatternCG pattern = nextElementDecl.getPattern();

				if (pattern instanceof AIdentifierPatternCG)
				{
					return;
				}

				// TODO: Make it such that the successer var is passed on (multiple binds)
				ABlockStmCG patternHandlingBlock = consPatternHandlingInIterationBlock(nextElementDecl, tag, nextElementDecl.getExp());

				List<SStmCG> stms = new LinkedList<SStmCG>();
				stms.addAll(patternHandlingBlock.getStatements());
				stms.addAll(node.getStatements());

				node.setStatements(stms);

				dec.apply(this);
			}
		}

		if (!taggedBlock)
		{
			List<PatternInfo> patternInfo = extractFromLocalDefs(node.getLocalDefs());

			if (!patternInfo.isEmpty())
			{
				List<DeclBlockPair> declBlockPairs = consPatternHandlingBlocksSeparate(node.getLocalDefs(), patternInfo);
				
				for(int i = 0; i < declBlockPairs.size(); i++)
				{
					DeclBlockPair currentDeclBlockPair = declBlockPairs.get(i);
					if(currentDeclBlockPair.getNextDecl() != null)
					{
						// The pattern handling block must be put before the
						// enclosing statement of the next declaration
						SStmCG stm = transAssistant.getEnclosingStm(currentDeclBlockPair.getNextDecl(), "block statement pattern handling");
						ABlockStmCG block = new ABlockStmCG();
						
						transAssistant.replaceNodeWith(stm, block);
						block.getStatements().add(currentDeclBlockPair.getBlock());
						block.getStatements().add(stm);
					}
					else
					{
						// If there is no next declaration the current declaration
						// must be the last declaration of the block statement
						INode parent = currentDeclBlockPair.getDecl().parent();
						
						if(parent instanceof ABlockStmCG)
						{
							ABlockStmCG enc = (ABlockStmCG) parent;
							enc.getStatements().addFirst(currentDeclBlockPair.getBlock());

						}
						else
						{
							Logger.getLog().printErrorln("Expected parent of current declaration "
									+ "to be a block statement but got: "
									+ parent
									+ ". Pattern handling block could not be added.");
						}
					}
				}
			}
		}

		for (SStmCG stm : node.getStatements())
		{
			stm.apply(this);
		}
	}
	
	@Override
	public void caseAForAllStmCG(AForAllStmCG node) throws AnalysisException
	{
		SPatternCG pattern = node.getPattern();
		
		if(pattern instanceof AIdentifierPatternCG)
		{
			node.getExp().apply(this);
			node.getBody().apply(this);
			return;
		}

		if (pattern  instanceof AIgnorePatternCG)
		{
			AIdentifierPatternCG idPattern = getIdPattern(config.getIgnorePatternPrefix());
			transAssistant.replaceNodeWith(pattern, idPattern);
		}
		
		PatternBlockData patternData = new PatternBlockData(MismatchHandling.LOOP_CONTINUE);
		patternData.setPattern(pattern);
		ABlockStmCG declBlock = new ABlockStmCG();
		patternData.setDeclBlock(declBlock);
		
		ABlockStmCG patternHandlingBlock = consPatternCheck(false, pattern, transAssistant.getInfo().getTypeAssistant().findElementType(node.getExp().getType().clone()), patternData, null);

		if (patternHandlingBlock != null)
		{
			declBlock.getStatements().addFirst(patternHandlingBlock);
		}
		
		declBlock.getStatements().add(node.getBody().clone());

		transAssistant.replaceNodeWith(node.getBody(), declBlock);
		
		node.getExp().apply(this);
		node.getBody().apply(this);
	}

	private ABlockStmCG consPatternHandlingInIterationBlock(
			AVarDeclCG nextElementDecl, DeclarationTag tag,
			SExpCG assignedExp)
	{
		PatternInfo declInfo = extractPatternInfo(nextElementDecl);
		ABlockStmCG declBlockTmp = new ABlockStmCG();
		PatternBlockData data = new PatternBlockData(declInfo.getPattern(), declBlockTmp, MismatchHandling.LOOP_CONTINUE);

		AVarDeclCG successVarDecl = tag.getSuccessVarDecl();
		if (successVarDecl != null)
		{
			SPatternCG successVarDeclPattern = successVarDecl.getPattern();
			if (successVarDeclPattern instanceof AIdentifierPatternCG)
			{
				AIdentifierPatternCG idPattern = (AIdentifierPatternCG) successVarDeclPattern;
				data.setSuccessVarDecl(successVarDecl.clone());
				AIdentifierVarExpCG successVar = transAssistant.consSuccessVar(idPattern.getName());
				data.setSuccessVar(successVar);
			} else
			{
				Logger.getLog().printErrorln("Expected success variable declaration to use an identifier pattern. Got: "
						+ successVarDeclPattern);
			}
		}

		List<PatternInfo> patternInfo = new LinkedList<PatternInfo>();
		patternInfo.add(declInfo);

		ABlockStmCG replacementBlock = new ABlockStmCG();
		replacementBlock.getStatements().add(consPatternCheck(false, declInfo.getPattern(), declInfo.getType(), data, declInfo.getActualValue()));
		replacementBlock.getStatements().addAll(declBlockTmp.getStatements());
		ABlockStmCG enclosingBlock = nextElementDecl.getAncestor(ABlockStmCG.class);
		enclosingBlock.getLocalDefs().addAll(declBlockTmp.getLocalDefs());

		AVarDeclCG nextDeclCopy = nextElementDecl.clone();

		if (tag == null || !tag.isDeclared())
		{
			replacementBlock.getLocalDefs().addFirst(nextDeclCopy);
		} else
		{
			SPatternCG nextDeclPattern = nextDeclCopy.getPattern();
			if (nextDeclPattern instanceof AIdentifierPatternCG)
			{
				AIdentifierVarExpCG varExp = new AIdentifierVarExpCG();
				varExp.setType(nextDeclCopy.getType());
				varExp.setIsLocal(true);
				varExp.setIsLambda(false);
				varExp.setName(((AIdentifierPatternCG) nextDeclPattern).getName());

				AAssignToExpStmCG assignment = new AAssignToExpStmCG();
				assignment.setTarget(varExp);
				assignment.setExp(assignedExp.clone());
				replacementBlock.getStatements().addFirst(assignment);
			} else
			{
				Logger.getLog().printErrorln("Expected the declaration to have its pattern transformed into an identifier pattern. Got: "
						+ nextDeclPattern);
			}
		}

		return replacementBlock;
	}
	
	private List<ABlockStmCG> consPatternHandlingBlockCases(
			List<PatternInfo> patternInfo, PatternBlockData patternData)
	{
		List<ABlockStmCG> patternHandlingBlocks = new LinkedList<ABlockStmCG>();

		for (PatternInfo currentInfo : patternInfo)
		{
			SPatternCG currentPattern = currentInfo.getPattern();

			ABlockStmCG nextPatternBlock = new ABlockStmCG();
			patternData.setDeclBlock(nextPatternBlock);

			// Use same success variable
			patternData.setPattern(currentPattern);

			if (currentPattern instanceof AIdentifierPatternCG)
			{
				nextPatternBlock.getStatements().add(consIdVarDeclaration(currentInfo, currentPattern));
				initSuccessVar(patternData, this.transAssistant.getInfo().getExpAssistant().consBoolLiteral(true), nextPatternBlock);
			} else if (currentPattern instanceof AIgnorePatternCG)
			{
				initSuccessVar(patternData, this.transAssistant.getInfo().getExpAssistant().consBoolLiteral(true), nextPatternBlock);

			} else
			{
				STypeCG currentType = currentInfo.getType();
				SExpCG currentActualValue = currentInfo.getActualValue();

				boolean declareVarPattern = true;
				ABlockStmCG patternCheck = consPatternCheck(declareVarPattern, currentPattern, currentType, patternData, currentActualValue);

				patternCheck.getLocalDefs().addAll(nextPatternBlock.getLocalDefs());
				nextPatternBlock = patternCheck;
			}

			patternHandlingBlocks.add(nextPatternBlock);
		}

		return patternHandlingBlocks;
	}

	private ABlockStmCG consIdVarDeclaration(PatternInfo currentInfo,
			SPatternCG currentPattern)
	{
		AIdentifierPatternCG idPattern = (AIdentifierPatternCG) currentPattern;
		AVarDeclCG idPatternDecl = transAssistant.getInfo().getDeclAssistant().
				consLocalVarDecl(currentInfo.getType().clone(),
						idPattern.clone(), currentInfo.getActualValue().clone());

		ABlockStmCG wrappingStatement = new ABlockStmCG();
		wrappingStatement.getLocalDefs().add(idPatternDecl);

		return wrappingStatement;
	}

	private ABlockStmCG consPatternHandlingBlock(List<PatternInfo> patternInfo)
	{
		ABlockStmCG topBlock = new ABlockStmCG();

		for (PatternInfo info : patternInfo)
		{
			SPatternCG currentPattern = info.getPattern();
			
			if(!basicCaseHandled(currentPattern))
			{
				ABlockStmCG currentDeclBlock = new ABlockStmCG();

				ABlockStmCG patternHandlingBlock = consPatternCheck(currentPattern, info.getType(), info.getActualValue(), currentDeclBlock);
				currentDeclBlock.getStatements().addFirst(patternHandlingBlock);
				topBlock.getStatements().add(currentDeclBlock);
			}
		}

		return topBlock;
	}
	
	private List<DeclBlockPair> consPatternHandlingBlocksSeparate(List<AVarDeclCG> decls, List<PatternInfo> patternInfo)
	{
		List<DeclBlockPair> blocks = new LinkedList<DeclBlockPair>();
		
		for (int i = 0; i < patternInfo.size(); i++)
		{
			PatternInfo info = patternInfo.get(i);

			if(!basicCaseHandled(info.getPattern()))
			{
				ABlockStmCG currentDeclBlock = new ABlockStmCG();

				ABlockStmCG patternHandlingBlock = consPatternCheck(info.getPattern(), info.getType(), info.getActualValue(), currentDeclBlock);
				currentDeclBlock.getStatements().addFirst(patternHandlingBlock);
				
				AVarDeclCG nextDecl = i < decls.size() - 1 ? decls.get(1 + i) : null;
				DeclBlockPair declBlockPair = new DeclBlockPair(decls.get(i), nextDecl, currentDeclBlock);
				
				blocks.add(declBlockPair);
			}
		}

		return blocks;
	}
	
	private boolean basicCaseHandled(SPatternCG currentPattern)
	{
		if (currentPattern instanceof AIdentifierPatternCG)
		{
			return true;
		} else if (currentPattern instanceof AIgnorePatternCG)
		{
			AIdentifierPatternCG idPattern = getIdPattern(config.getIgnorePatternPrefix());
			transAssistant.replaceNodeWith(currentPattern, idPattern);

			return true;
		}
		
		return false;
	}
	
	private ABlockStmCG consPatternCheck(SPatternCG pattern, STypeCG type,
			SExpCG actualValue, ABlockStmCG declBlock)
	{
		boolean declareVarPattern = false;
		PatternBlockData patternData = new PatternBlockData(pattern, declBlock, MismatchHandling.RAISE_ERROR);

		return consPatternCheck(declareVarPattern, pattern, type, patternData, actualValue);
	}

	private ABlockStmCG consPatternCheck(boolean declarePatternVar,
			SPatternCG pattern, STypeCG type, PatternBlockData patternData,
			SExpCG actualValue)
	{
		if (pattern instanceof ABoolPatternCG)
		{
			ABoolPatternCG boolPattern = (ABoolPatternCG) pattern;

			Boolean value = boolPattern.getValue();
			ABoolLiteralExpCG consBoolLiteral = transAssistant.getInfo().getExpAssistant().consBoolLiteral(value);

			return consSimplePatternCheck(declarePatternVar, boolPattern, consBoolLiteral, patternData, actualValue);
		} else if (pattern instanceof ACharPatternCG)
		{
			ACharPatternCG charPattern = (ACharPatternCG) pattern;

			Character value = charPattern.getValue();
			ACharLiteralExpCG charLiteral = transAssistant.getInfo().getExpAssistant().consCharLiteral(value);

			return consSimplePatternCheck(declarePatternVar, charPattern, charLiteral, patternData, actualValue);
		} else if (pattern instanceof AIntPatternCG)
		{
			AIntPatternCG intPattern = (AIntPatternCG) pattern;

			Long value = intPattern.getValue();
			AIntLiteralExpCG intLit = transAssistant.getInfo().getExpAssistant().consIntLiteral(value);

			return consSimplePatternCheck(declarePatternVar, intPattern, intLit, patternData, actualValue);
		} else if (pattern instanceof ANullPatternCG)
		{
			return consSimplePatternCheck(declarePatternVar, pattern, transAssistant.getInfo().getExpAssistant().consNullExp(), patternData, actualValue);
		} else if (pattern instanceof AQuotePatternCG)
		{
			AQuotePatternCG quotePattern = (AQuotePatternCG) pattern;

			String value = quotePattern.getValue();
			AQuoteLiteralExpCG quoteLit = transAssistant.getInfo().getExpAssistant().consQuoteLiteral(value);

			return consSimplePatternCheck(declarePatternVar, pattern, quoteLit, patternData, actualValue);
		} else if (pattern instanceof ARealPatternCG)
		{
			ARealPatternCG realPattern = (ARealPatternCG) pattern;

			Double value = realPattern.getValue();
			ARealLiteralExpCG realLit = transAssistant.getInfo().getExpAssistant().consRealLiteral(value);

			return consSimplePatternCheck(declarePatternVar, realPattern, realLit, patternData, actualValue);

		} else if (pattern instanceof AStringPatternCG)
		{
			AStringPatternCG stringPattern = (AStringPatternCG) pattern;
			String value = stringPattern.getValue();

			SExpCG stringValue = null;

			if (transAssistant.getInfo().getSettings().getCharSeqAsString())
			{
				stringValue = transAssistant.getInfo().getExpAssistant().consStringLiteral(value, false);
			} else
			{
				ASeqSeqTypeCG seqType = new ASeqSeqTypeCG();
				seqType.setEmpty(false);
				seqType.setSeqOf(new ACharBasicTypeCG());

				stringValue = transAssistant.getInfo().getExpAssistant().consCharSequence(seqType, value);
			}

			return consSimplePatternCheck(declarePatternVar, stringPattern, stringValue, patternData, actualValue);
		} else if (pattern instanceof ATuplePatternCG)
		{
			ATuplePatternCG tuplePattern = (ATuplePatternCG) pattern;
			
			if (type instanceof ATupleTypeCG)
			{
				ATupleTypeCG tupleType = (ATupleTypeCG) type;

				return consTuplePatternCheck(declarePatternVar, tuplePattern, 
						tupleType, patternData, actualValue, false);
			}
			else if(type instanceof AUnionTypeCG)
			{
					return consUnionTypedTuplePatternCheck(declarePatternVar,
							(AUnionTypeCG) type, patternData, actualValue, tuplePattern);
			}
			else
			{
				Logger.getLog().printErrorln("Expected tuple type or union type "
						+ "in 'PatternTransformation'. Got: " + type);
			}
		} else if (pattern instanceof ARecordPatternCG)
		{
			ARecordPatternCG recordPattern = (ARecordPatternCG) pattern;
			ARecordTypeCG recordType = (ARecordTypeCG) recordPattern.getType();
			
			if(type instanceof ARecordTypeCG)
			{
				
				return consRecordPatternCheck(declarePatternVar, recordPattern,
						recordType, patternData, actualValue, checkRecordPattern(actualValue));
				
			}
			else if(type instanceof AUnionTypeCG)
			{
				return consRecordPatternCheck(declarePatternVar, recordPattern,
						recordType, patternData, actualValue, true);
			}
			else
			{
				Logger.getLog().printErrorln("Expected record type or union type"
						+ "in PatternTransformation. Got: " + type);
			}
		}

		return null;
	}

	private ABlockStmCG consUnionTypedTuplePatternCheck(
			boolean declarePatternVar, AUnionTypeCG unionType,
			PatternBlockData patternData, SExpCG actualValue,
			ATuplePatternCG tuplePattern)
	{
		List<ATupleTypeCG> tupleTypes = new LinkedList<ATupleTypeCG>();

		for (STypeCG nextType : unionType.getTypes())
		{
			if (nextType instanceof ATupleTypeCG)
			{
				ATupleTypeCG nextTupleType = ((ATupleTypeCG) nextType);

				if (nextTupleType.getTypes().size() == tuplePattern.getPatterns().size())
				{
					tupleTypes.add(nextTupleType);
				}
			}
		}

		ATupleTypeCG resTupleType = new ATupleTypeCG();

		if (tupleTypes.size() == 1)
		{
			resTupleType = tupleTypes.get(0);
		} else
		{
			for (int i = 0; i < tuplePattern.getPatterns().size(); i++)
			{
				AUnionTypeCG fieldType = new AUnionTypeCG();

				for (ATupleTypeCG t : tupleTypes)
				{
					fieldType.getTypes().add(t.getTypes().get(i).clone());
				}

				resTupleType.getTypes().add(fieldType);
			}
		}

		ABlockStmCG tuplePatternCheck = consTuplePatternCheck(declarePatternVar, tuplePattern, resTupleType, patternData, actualValue, true);

		AInstanceofExpCG instanceCheck = new AInstanceofExpCG();
		instanceCheck.setType(new ABoolBasicTypeCG());
		instanceCheck.setCheckedType(patternData.getRootPatternVar().getType().clone());
		instanceCheck.setExp(patternData.getRootPatternVar().clone());

		AIfStmCG typeCheck = new AIfStmCG();
		typeCheck.setIfExp(instanceCheck);
		typeCheck.setThenStm(tuplePatternCheck);

		ABlockStmCG block = new ABlockStmCG();
		block.getStatements().add(typeCheck);

		return block;
	}

	private ABlockStmCG consRecordPatternCheck(boolean declarePattern,
			ARecordPatternCG recordPattern, ARecordTypeCG recordType,
			PatternBlockData patternData, SExpCG actualValue, boolean checkRecordType)
	{
		AIdentifierPatternCG idPattern = getIdPattern(config.getName(recordPattern.getClass()));

		AIdentifierVarExpCG recordPatternVar = new AIdentifierVarExpCG();
		recordPatternVar.setType(recordType.clone());
		recordPatternVar.setName(idPattern.getName());
		recordPatternVar.setIsLambda(false);
		recordPatternVar.setIsLocal(true);
		patternData.setRootPatternVar(recordPatternVar);
		
		if(!declarePattern)
		{
			actualValue = recordPatternVar;
		}
		
		ABlockStmCG recordPatternBlock = initPattern(declarePattern, recordPattern, recordType, actualValue, idPattern);

		ARecordDeclCG record = transAssistant.getInfo().getAssistantManager().getDeclAssistant().findRecord(transAssistant.getInfo().getClasses(), recordType);

		if (patternData.getSuccessVarDecl() == null)
		{
			consSuccessVarCheck(recordPattern, patternData);
		}

		mismatchHandling(recordPattern, patternData);
		initSuccessVar(patternData, transAssistant.getInfo().getExpAssistant().consBoolLiteral(true), recordPatternBlock);

		List<STypeCG> types = new LinkedList<STypeCG>();

		for (AFieldDeclCG currentField : record.getFields())
		{
			types.add(currentField.getType());
		}

		ABlockStmCG fieldCheckBlock = consFieldCheckBlock(patternData, recordPatternVar, 
				recordPattern.getPatterns(), types, checkRecordType && !declarePattern);

		recordPatternBlock.getStatements().add(fieldCheckBlock);

		if (checkRecordType)
		{
			AInstanceofExpCG instanceOfExp = new AInstanceofExpCG();
			instanceOfExp.setType(new ABoolBasicTypeCG());
			instanceOfExp.setExp(actualValue.clone());
			instanceOfExp.setCheckedType(recordType.clone());

			AIfStmCG ifStm = new AIfStmCG();
			ifStm.setIfExp(instanceOfExp);
			ifStm.setThenStm(recordPatternBlock);

			AAssignToExpStmCG setFalse = new AAssignToExpStmCG();
			setFalse.setTarget(patternData.getSuccessVar().clone());
			setFalse.setExp(transAssistant.getInfo().getExpAssistant().consBoolLiteral(false));
			ifStm.setElseStm(setFalse);

			ABlockStmCG wrappingBlock = new ABlockStmCG();
			wrappingBlock.getStatements().add(ifStm);

			return wrappingBlock;
		}
		
		return recordPatternBlock;
	}

	@SuppressWarnings("unchecked")
	private ABlockStmCG consTuplePatternCheck(boolean declarePatternVar,
			ATuplePatternCG tuplePattern, ATupleTypeCG tupleType,
			PatternBlockData patternData, SExpCG actualValue, boolean cast)
	{
		AIdentifierPatternCG idPattern = getIdPattern(config.getName(tuplePattern.getClass()));

		ABlockStmCG tuplePatternBlock = initPattern(declarePatternVar, tuplePattern, tupleType, actualValue, idPattern);

		AIdentifierVarExpCG tuplePatternVar = new AIdentifierVarExpCG();
		tuplePatternVar.setType(tupleType.clone());
		tuplePatternVar.setName(idPattern.getName());
		tuplePatternVar.setIsLambda(false);
		tuplePatternVar.setIsLocal(true);
		patternData.setRootPatternVar(tuplePatternVar);

		ATupleCompatibilityExpCG tupleCheck = new ATupleCompatibilityExpCG();
		tupleCheck.setType(new ABoolBasicTypeCG());
		
		if (!cast)
		{
			tupleCheck.setTuple(tuplePatternVar.clone());
		} else
		{
			ACastUnaryExpCG castTuple = new ACastUnaryExpCG();
			castTuple.setType(tupleType.clone());
			castTuple.setExp(tuplePatternVar.clone());
			tupleCheck.setTuple(castTuple);
		}
		
		tupleCheck.setTypes((List<? extends STypeCG>) tupleType.getTypes().clone());

		if (patternData.getSuccessVarDecl() == null)
		{
			consSuccessVarCheck(tuplePattern, patternData);
		}

		mismatchHandling(tuplePattern, patternData);
		initSuccessVar(patternData, tupleCheck, tuplePatternBlock);

		LinkedList<SPatternCG> patterns = tuplePattern.getPatterns();
		LinkedList<STypeCG> types = tupleType.getTypes();

		AIfStmCG fieldSizeCheck = new AIfStmCG();
		fieldSizeCheck.setIfExp(patternData.getSuccessVar().clone());
		fieldSizeCheck.setThenStm(consFieldCheckBlock(patternData, tuplePatternVar, patterns, types, cast));

		tuplePatternBlock.getStatements().add(fieldSizeCheck);

		return tuplePatternBlock;
	}

	private void consSuccessVarCheck(SPatternCG pattern,
			PatternBlockData patternData)
	{
		String successVarName = this.transAssistant.getInfo().getTempVarNameGen().nextVarName(iteVarPrefixes.success());
		SExpCG init = null;

		if (!patternData.IsRootPattern(pattern))
		{
			init = transAssistant.getInfo().getExpAssistant().consBoolLiteral(pattern instanceof ATuplePatternCG ? false
					: true);
			init.setType(new ABoolBasicTypeCG());
		} else
		{
			init = new AUndefinedExpCG();
			init.setType(new AUnknownTypeCG());
		}

		AVarDeclCG successVarDecl = transAssistant.consDecl(successVarName, new ABoolBasicTypeCG(), init);
		patternData.setSuccessVarDecl(successVarDecl);

		AIdentifierVarExpCG successVar = transAssistant.consSuccessVar(successVarName);
		patternData.setSuccessVar(successVar);

		patternData.getDeclBlock().getLocalDefs().add(successVarDecl);
	}

	private void mismatchHandling(SPatternCG pattern,
			PatternBlockData patternData)
	{
		if (!patternData.IsRootPattern(pattern))
		{
			return;
		}

		if (patternData.getMismatchHandling() == MismatchHandling.RAISE_ERROR)
		{
			APatternMatchRuntimeErrorExpCG matchFail = new APatternMatchRuntimeErrorExpCG();
			matchFail.setType(new AErrorTypeCG());
			matchFail.setMessage(config.getMatchFailedMessage(pattern));
			ARaiseErrorStmCG noMatchStm = new ARaiseErrorStmCG();
			noMatchStm.setError(matchFail);

			AIfStmCG consMismatchCheck = consMismatchCheck(patternData.getSuccessVar(), noMatchStm);
			patternData.getDeclBlock().getStatements().add(consMismatchCheck);
		} else if (patternData.getMismatchHandling() == MismatchHandling.LOOP_CONTINUE)
		{
			AIfStmCG consMismatchCheck = consMismatchCheck(patternData.getSuccessVar(), new AContinueStmCG());
			patternData.getDeclBlock().getStatements().add(consMismatchCheck);
		}
	}

	private AIfStmCG consMismatchCheck(AIdentifierVarExpCG successVar,
			SStmCG noMatchStm)
	{
		AIfStmCG ifCheck = new AIfStmCG();
		ifCheck.setIfExp(transAssistant.consBoolCheck(successVar.getName(), true));
		ifCheck.setThenStm(noMatchStm);

		return ifCheck;
	}

	private void initSuccessVar(PatternBlockData patternData, SExpCG initExp,
			ABlockStmCG patternBlock)
	{
		if (patternData.getSuccessVarDecl().getExp() instanceof AUndefinedExpCG)
		{
			patternData.getSuccessVarDecl().setExp(initExp);
		} else
		{
			AAssignToExpStmCG successVarAssignment = new AAssignToExpStmCG();
			successVarAssignment.setTarget(patternData.getSuccessVar().clone());
			successVarAssignment.setExp(initExp);

			patternBlock.getStatements().add(successVarAssignment);
		}
	}

	private ABlockStmCG initPattern(boolean declare, SPatternCG pattern,
			STypeCG type, SExpCG actualValue, AIdentifierPatternCG idPattern)
	{
		ABlockStmCG patternBlock = new ABlockStmCG();

		if (declare)
		{
			AVarDeclCG patternDecl = transAssistant.getInfo().getDeclAssistant().
					consLocalVarDecl(type.clone(), idPattern.clone(),
							actualValue.clone());
			patternBlock.getLocalDefs().add(patternDecl);
		} else
		{
			transAssistant.replaceNodeWith(pattern, idPattern);
		}

		return patternBlock;
	}

	private ABlockStmCG consFieldCheckBlock(PatternBlockData patternData,
			AIdentifierVarExpCG patternVar, List<SPatternCG> patterns,
			List<STypeCG> types, boolean cast)
	{
		ABlockStmCG thenPart = new ABlockStmCG();
		ABlockStmCG topBlock = thenPart;

		for (int i = 0; i < patterns.size(); i++)
		{
			SPatternCG currentPattern = patterns.get(i);
			STypeCG currentType = types.get(i);

			if (skipPattern(currentPattern))
			{
				continue;
			} else
			{
				SExpCG actualValue = consFieldValueToMatch(patternVar, i, currentType, cast);

				if (currentPattern instanceof AIdentifierPatternCG)
				{
					AAssignToExpStmCG localAssignment = declareAndAssignIdVarAssignment(patternData.getDeclBlock(), currentPattern, currentType, actualValue);
					thenPart.getStatements().add(localAssignment);
				} else
				{
					ABlockStmCG patternBlock = consPatternBlock(patternData, currentPattern, currentType, actualValue, cast);

					if (patternBlock != null)
					{
						thenPart.getStatements().add(patternBlock);

						// The tuple/record pattern have more field patterns to be generated.
						// Check the success variable and add a new nesting level
						if (morePatternsToGenerate(patterns,i))
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
		}

		return topBlock;
	}

	private boolean skipPattern(SPatternCG pattern)
	{
		return pattern instanceof AIgnorePatternCG;
	}
	
	private boolean morePatternsToGenerate(List<SPatternCG> patterns, int currentPatternIndex)
	{
		int nextPatternIndex = currentPatternIndex + 1;

		for (int i = nextPatternIndex; i < patterns.size(); i++)
		{
			SPatternCG nextPattern = patterns.get(i);
			
			if (!skipPattern(nextPattern))
			{
				return true;
			}
		}

		return false;
	}

	private ABlockStmCG consPatternBlock(PatternBlockData patternData,
			SPatternCG currentPattern, STypeCG currentType, SExpCG actualValue, boolean cast)
	{
		ABlockStmCG patternBlock = null;

		if (currentPattern instanceof ATuplePatternCG)
		{
			ATuplePatternCG nextTuplePattern = (ATuplePatternCG) currentPattern;
			ATupleTypeCG nextTupleType = (ATupleTypeCG) currentType;

			patternBlock = consTuplePatternCheck(true, nextTuplePattern, nextTupleType, patternData, actualValue, cast);

		} else if (currentPattern instanceof ARecordPatternCG)
		{
			ARecordPatternCG nextRecordPattern = (ARecordPatternCG) currentPattern;
			ARecordTypeCG nextRecordType = (ARecordTypeCG) nextRecordPattern.getType();
			boolean checkRecordPattern = checkRecordPattern(actualValue);
			
			patternBlock = consRecordPatternCheck(true, nextRecordPattern, nextRecordType, patternData, actualValue, checkRecordPattern);
		} else
		{
			patternBlock = consPatternCheck(true, currentPattern, currentType, patternData, actualValue);
		}

		return patternBlock;
	}

	private SExpCG consFieldValueToMatch(AIdentifierVarExpCG patternVar,
			int fieldNumber, STypeCG currentType, boolean cast)
	{
		if (patternVar.getType() instanceof ATupleTypeCG)
		{
			return consTupleFieldExp(patternVar, fieldNumber, currentType, cast);
		} else if (patternVar.getType() instanceof ARecordTypeCG)
		{
			return consRecFieldExp(patternVar, fieldNumber, currentType, cast);
		}

		return null;
	}

	private AAssignToExpStmCG declareAndAssignIdVarAssignment(
			ABlockStmCG declBlock, SPatternCG currentPattern,
			STypeCG currentType, SExpCG valueToMatch)
	{
		AIdentifierPatternCG currentId = (AIdentifierPatternCG) currentPattern;

		AVarDeclCG idVarDecl = transAssistant.getInfo().getDeclAssistant().consLocalVarDecl(currentType.clone(), 
				currentPattern.clone(), new AUndefinedExpCG());

		declBlock.getLocalDefs().add(idVarDecl);

		AIdentifierVarExpCG var = new AIdentifierVarExpCG();
		var.setType(currentType.clone());
		var.setName(currentId.getName());
		var.setIsLocal(true);
		var.setIsLambda(false);

		AAssignToExpStmCG localAssignment = new AAssignToExpStmCG();
		localAssignment.setTarget(var);
		localAssignment.setExp(valueToMatch);

		return localAssignment;
	}

	private <T> ABlockStmCG consSimplePatternCheck(boolean declarePatternVar,
			SPatternCG pattern, SExpCG valueToMatch,
			PatternBlockData patternData, SExpCG actualValue)
	{
		// Example:
		// Number intPattern_2 = 1L;
		// Boolean success_2 = intPattern_2.longValue() == 1L;

		AIdentifierPatternCG idPattern = getIdPattern(config.getName(pattern.getClass()));
		transAssistant.replaceNodeWith(pattern, idPattern);

		ABlockStmCG block = new ABlockStmCG();

		if (declarePatternVar)
		{
			AVarDeclCG patternDecl = transAssistant.getInfo().getDeclAssistant().
					consLocalVarDecl(actualValue.getType().clone(), 
					idPattern.clone(), actualValue.clone());
			block.getLocalDefs().add(patternDecl);
		}

		AIdentifierVarExpCG var = new AIdentifierVarExpCG();
		var.setType(valueToMatch.getType().clone());
		var.setName(idPattern.getName());
		var.setIsLambda(false);
		var.setIsLocal(true);
		patternData.setRootPatternVar(var);

		AEqualsBinaryExpCG check = new AEqualsBinaryExpCG();
		check.setType(new ABoolBasicTypeCG());
		check.setLeft(var);
		check.setRight(valueToMatch);

		if (patternData.getSuccessVarDecl() == null)
		{
			consSuccessVarCheck(pattern, patternData);
		}

		mismatchHandling(pattern, patternData);
		initSuccessVar(patternData, check, block);

		return block;
	}

	public List<PatternInfo> extractFromLocalDefs(List<AVarDeclCG> localDefs)
	{
		List<PatternInfo> patternInfo = new LinkedList<PatternInfo>();

		for (AVarDeclCG decl : localDefs)
		{
			PatternInfo currentInfo = extractPatternInfo(decl);
			patternInfo.add(currentInfo);
		}

		return patternInfo;
	}

	private PatternInfo extractPatternInfo(AVarDeclCG decl)
	{
		STypeCG type = decl.getType();
		SPatternCG pattern = decl.getPattern();
		SExpCG actualValue = decl.getExp();

		return new PatternInfo(type, pattern, actualValue);
	}

	public List<PatternInfo> extractFromParams(
			List<AFormalParamLocalParamCG> params)
	{
		List<PatternInfo> patternInfo = new LinkedList<PatternInfo>();

		for (AFormalParamLocalParamCG param : params)
		{
			STypeCG type = param.getType();
			SPatternCG pattern = param.getPattern();

			patternInfo.add(new PatternInfo(type, pattern, null));
		}

		return patternInfo;
	}

	public List<PatternInfo> extractFromCases(List<ACaseAltStmStmCG> cases,
			SExpCG exp)
	{
		List<PatternInfo> patternInfo = new LinkedList<PatternInfo>();

		for (ACaseAltStmStmCG alt : cases)
		{
			patternInfo.add(new PatternInfo(alt.getPatternType(), alt.getPattern(), exp));
		}

		return patternInfo;
	}

	private AIdentifierPatternCG getIdPattern(String namePrefix)
	{
		String name = transAssistant.getInfo().getTempVarNameGen().nextVarName(namePrefix);

		AIdentifierPatternCG idPattern = new AIdentifierPatternCG();
		idPattern.setName(name);

		return idPattern;
	}

	private AFieldNumberExpCG consTupleFieldExp(
			AIdentifierVarExpCG tuplePatternVar, int i, STypeCG currentType, boolean cast)
	{
		AFieldNumberExpCG fieldNumberExp = new AFieldNumberExpCG();
		fieldNumberExp.setType(currentType.clone());
		
		if (!cast)
		{
			fieldNumberExp.setTuple(tuplePatternVar.clone());
		}
		else
		{
			ACastUnaryExpCG castedExp = new ACastUnaryExpCG();
			castedExp.setType(tuplePatternVar.getType().clone());
			castedExp.setExp(tuplePatternVar.clone());
			fieldNumberExp.setTuple(castedExp);
		}
		
		fieldNumberExp.setField(new Long(1 + i));

		return fieldNumberExp;
	}

	private SExpCG consRecFieldExp(AIdentifierVarExpCG patternVar, int i,
			STypeCG currentType, boolean cast)
	{
		ARecordTypeCG recordType = (ARecordTypeCG) patternVar.getType();

		AFieldDeclCG recordField = transAssistant.getInfo().getAssistantManager().getDeclAssistant().getFieldDecl(transAssistant.getInfo().getClasses(), recordType, i);
		String fieldName = recordField.getName();

		AFieldExpCG fieldExp = consRecFieldExp(patternVar, currentType, fieldName);
		
		if(cast)
		{
			ACastUnaryExpCG casted = new ACastUnaryExpCG();
			casted.setType(recordType.clone());
			casted.setExp(fieldExp.getObject());
			
			fieldExp.setObject(casted);
		}
		
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

	private DeclarationTag fetchTag(PCG node)
	{
		if (node != null)
		{
			Object tag = node.getTag();
			if (tag instanceof DeclarationTag)
			{
				return (DeclarationTag) tag;
			}
		}

		Logger.getLog().printErrorln("Could not fetch declaration tag from pattern assignment: "
				+ node);

		return null;
	}

	private boolean checkRecordPattern(SExpCG actualValue)
	{
		return actualValue != null && actualValue.getType() instanceof AUnionTypeCG;
	}
}
