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

import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.PIR;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.ARecordDeclIR;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.ABoolLiteralExpIR;
import org.overture.codegen.ir.expressions.ACastUnaryExpIR;
import org.overture.codegen.ir.expressions.ACharLiteralExpIR;
import org.overture.codegen.ir.expressions.AEqualsBinaryExpIR;
import org.overture.codegen.ir.expressions.AFieldExpIR;
import org.overture.codegen.ir.expressions.AFieldNumberExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.AInstanceofExpIR;
import org.overture.codegen.ir.expressions.AIntLiteralExpIR;
import org.overture.codegen.ir.expressions.ANotUnaryExpIR;
import org.overture.codegen.ir.expressions.APatternMatchRuntimeErrorExpIR;
import org.overture.codegen.ir.expressions.AQuoteLiteralExpIR;
import org.overture.codegen.ir.expressions.ARealLiteralExpIR;
import org.overture.codegen.ir.expressions.ATupleCompatibilityExpIR;
import org.overture.codegen.ir.expressions.AUndefinedExpIR;
import org.overture.codegen.ir.expressions.SVarExpIR;
import org.overture.codegen.ir.patterns.ABoolPatternIR;
import org.overture.codegen.ir.patterns.ACharPatternIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.patterns.AIgnorePatternIR;
import org.overture.codegen.ir.patterns.AIntPatternIR;
import org.overture.codegen.ir.patterns.ANullPatternIR;
import org.overture.codegen.ir.patterns.AQuotePatternIR;
import org.overture.codegen.ir.patterns.ARealPatternIR;
import org.overture.codegen.ir.patterns.ARecordPatternIR;
import org.overture.codegen.ir.patterns.AStringPatternIR;
import org.overture.codegen.ir.patterns.ATuplePatternIR;
import org.overture.codegen.ir.statements.AAssignToExpStmIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.ACaseAltStmStmIR;
import org.overture.codegen.ir.statements.ACasesStmIR;
import org.overture.codegen.ir.statements.AContinueStmIR;
import org.overture.codegen.ir.statements.AForAllStmIR;
import org.overture.codegen.ir.statements.AIfStmIR;
import org.overture.codegen.ir.statements.ALocalPatternAssignmentStmIR;
import org.overture.codegen.ir.statements.ARaiseErrorStmIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.ACharBasicTypeIR;
import org.overture.codegen.ir.types.AErrorTypeIR;
import org.overture.codegen.ir.types.ARecordTypeIR;
import org.overture.codegen.ir.types.ASeqSeqTypeIR;
import org.overture.codegen.ir.types.ATupleTypeIR;
import org.overture.codegen.ir.types.AUnionTypeIR;
import org.overture.codegen.ir.types.AUnknownTypeIR;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.DeclarationTag;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class PatternTrans extends DepthFirstAnalysisAdaptor
{
	private TransAssistantIR transAssistant;

	private PatternVarPrefixes config;

	private IterationVarPrefixes iteVarPrefixes;

	private String casesExpNamePrefix;
	
	public PatternTrans(IterationVarPrefixes iteVarPrefixes,
			TransAssistantIR transAssistant, PatternVarPrefixes config,
			String casesExpNamePrefix)
	{
		this.transAssistant = transAssistant;
		this.iteVarPrefixes = iteVarPrefixes;

		this.config = config;

		this.casesExpNamePrefix = casesExpNamePrefix;
	}

	@Override
	public void caseALocalPatternAssignmentStmIR(
			ALocalPatternAssignmentStmIR node) throws AnalysisException
	{
		AVarDeclIR nextElementDecl = node.getNextElementDecl();
		SPatternIR pattern = nextElementDecl.getPattern();

		if (pattern instanceof AIdentifierPatternIR)
		{
			return;
		}
		
		if (pattern  instanceof AIgnorePatternIR)
		{
			AIdentifierPatternIR idPattern = getIdPattern(config.getIgnorePatternPrefix());
			transAssistant.replaceNodeWith(node.getTarget(), idPattern);
			transAssistant.replaceNodeWith(pattern, idPattern.clone());
			return;
		}

		DeclarationTag tag = fetchTag(node);

		ABlockStmIR replacementBlock = consPatternHandlingInIterationBlock(nextElementDecl, tag, node.getExp());
		transAssistant.replaceNodeWith(node, replacementBlock);
	}

	@Override
	public void caseACasesStmIR(ACasesStmIR node) throws AnalysisException
	{
		List<ACaseAltStmStmIR> nodeCases = node.getCases();
		SPatternIR firstOriginal = nodeCases.get(0).getPattern().clone();
		
		ABlockStmIR replacementBlock = new ABlockStmIR();
		String expName = transAssistant.getInfo().getTempVarNameGen().nextVarName(casesExpNamePrefix);

		SExpIR exp = node.getExp();
		
		if (!(node.getExp() instanceof SVarExpIR))
		{
			AVarDeclIR expVarDecl = transAssistant.getInfo().getDeclAssistant().consLocalVarDecl(node.getExp().getType().clone(),
					transAssistant.getInfo().getPatternAssistant().consIdPattern(expName), node.getExp().clone());
			replacementBlock.getLocalDefs().add(expVarDecl);
			exp = transAssistant.getInfo().getExpAssistant().consIdVar(expName,
					node.getExp().getType().clone());
		}
		
		List<PatternInfo> patternInfo = extractFromCases(nodeCases, exp);
		PatternBlockData patternData = new PatternBlockData(MismatchHandling.NONE);

		List<ABlockStmIR> blocks = consPatternHandlingBlockCases(patternInfo, patternData);

		replacementBlock.getStatements().add(blocks.get(0));

		ANotUnaryExpIR notSuccess = transAssistant.getInfo().getExpAssistant().negate(patternData.getSuccessVar());

		AIfStmIR ifStm = new AIfStmIR();
		ABlockStmIR enclosingIf = new ABlockStmIR();
		enclosingIf.getStatements().add(ifStm);
		replacementBlock.getStatements().add(enclosingIf);

		ifStm.setIfExp(notSuccess);

		AIfStmIR nextCase = ifStm;

		if (nodeCases.size() > 1)
		{
			ifStm.setElseStm(nodeCases.get(0).getResult().clone());
			
			nextCase = new AIfStmIR();

			enclosingIf = new ABlockStmIR();
			ifStm.setThenStm(enclosingIf);
			enclosingIf.getStatements().add(nextCase);

			// All cases except for the first and the last
			for (int i = 1; i < nodeCases.size() - 1; i++)
			{
				enclosingIf.getStatements().addFirst(blocks.get(i));
				enclosingIf = new ABlockStmIR();

				ACaseAltStmStmIR currentCase = nodeCases.get(i);
				nextCase.setIfExp(notSuccess.clone());
				nextCase.setElseStm(currentCase.getResult().clone());

				AIfStmIR tmp = new AIfStmIR();
				enclosingIf.getStatements().add(tmp);

				nextCase.setThenStm(enclosingIf);
				nextCase = tmp;
			}
		}
		else
		{
			APatternMatchRuntimeErrorExpIR matchFail = new APatternMatchRuntimeErrorExpIR();
			matchFail.setType(new AErrorTypeIR());
			matchFail.setMessage(config.getMatchFailedMessage(firstOriginal));
			ARaiseErrorStmIR noMatchStm = new ARaiseErrorStmIR();
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
	public void caseAMethodDeclIR(AMethodDeclIR node) throws AnalysisException
	{
		List<PatternInfo> patternInfo = extractFromParams(node.getFormalParams());

		if (!node.getAbstract() && node.getBody() != null)
		{
			ABlockStmIR patternHandlingBlock = consPatternHandlingBlock(patternInfo);

			ABlockStmIR newBody = new ABlockStmIR();
			newBody.getStatements().add(patternHandlingBlock);

			SStmIR oldBody = node.getBody();
			transAssistant.replaceNodeWith(oldBody, newBody);
			newBody.getStatements().add(oldBody);

			newBody.apply(this);
		} else
		{
			for (AFormalParamLocalParamIR param : node.getFormalParams())
			{
				SPatternIR paramPattern = param.getPattern();

				if (!(paramPattern instanceof AIdentifierPatternIR))
				{
					String prefix = config.getName(param.getPattern().getClass());

					if (prefix != null)
					{
						AIdentifierPatternIR idPattern = getIdPattern(prefix);
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
	public void caseABlockStmIR(ABlockStmIR node) throws AnalysisException
	{
		boolean taggedBlock = false;
		for (int i = 0; i < node.getLocalDefs().size(); i++)
		{
			AVarDeclIR dec = node.getLocalDefs().get(i);

			if (dec.getTag() != null)
			{
				taggedBlock = true;

				DeclarationTag tag = fetchTag(dec);

				if (tag.isDeclared() || !(dec instanceof AVarDeclIR))
				{
					continue;
				}

				AVarDeclIR nextElementDecl = (AVarDeclIR) dec;

				SPatternIR pattern = nextElementDecl.getPattern();

				if (pattern instanceof AIdentifierPatternIR)
				{
					continue;
				}

				// TODO: Make it such that the successer var is passed on (multiple binds)
				ABlockStmIR patternHandlingBlock = consPatternHandlingInIterationBlock(nextElementDecl, tag, nextElementDecl.getExp());

				List<SStmIR> stms = new LinkedList<SStmIR>();
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
						SStmIR stm = transAssistant.getEnclosingStm(currentDeclBlockPair.getNextDecl(), "block statement pattern handling");
						ABlockStmIR block = new ABlockStmIR();
						
						transAssistant.replaceNodeWith(stm, block);
						block.getStatements().add(currentDeclBlockPair.getBlock());
						block.getStatements().add(stm);
					}
					else
					{
						// If there is no next declaration the current declaration
						// must be the last declaration of the block statement
						INode parent = currentDeclBlockPair.getDecl().parent();
						
						if(parent instanceof ABlockStmIR)
						{
							ABlockStmIR enc = (ABlockStmIR) parent;
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

		for (SStmIR stm : node.getStatements())
		{
			stm.apply(this);
		}
	}
	
	@Override
	public void caseAForAllStmIR(AForAllStmIR node) throws AnalysisException
	{
		SPatternIR pattern = node.getPattern();
		
		if(pattern instanceof AIdentifierPatternIR)
		{
			node.getExp().apply(this);
			node.getBody().apply(this);
			return;
		}

		if (pattern  instanceof AIgnorePatternIR)
		{
			AIdentifierPatternIR idPattern = getIdPattern(config.getIgnorePatternPrefix());
			transAssistant.replaceNodeWith(pattern, idPattern);
		}
		
		PatternBlockData patternData = new PatternBlockData(MismatchHandling.LOOP_CONTINUE);
		patternData.setPattern(pattern);
		ABlockStmIR declBlock = new ABlockStmIR();
		patternData.setDeclBlock(declBlock);
		
		ABlockStmIR patternHandlingBlock = consPatternCheck(false, pattern, transAssistant.getInfo().getTypeAssistant().findElementType(node.getExp().getType().clone()), patternData, null);

		if (patternHandlingBlock != null)
		{
			declBlock.getStatements().addFirst(patternHandlingBlock);
		}
		
		declBlock.getStatements().add(node.getBody().clone());

		transAssistant.replaceNodeWith(node.getBody(), declBlock);
		
		node.getExp().apply(this);
		node.getBody().apply(this);
	}

	private ABlockStmIR consPatternHandlingInIterationBlock(
			AVarDeclIR nextElementDecl, DeclarationTag tag,
			SExpIR assignedExp)
	{
		PatternInfo declInfo = extractPatternInfo(nextElementDecl);
		ABlockStmIR declBlockTmp = new ABlockStmIR();
		PatternBlockData data = new PatternBlockData(declInfo.getPattern(), declBlockTmp, MismatchHandling.LOOP_CONTINUE);

		AVarDeclIR successVarDecl = tag.getSuccessVarDecl();
		if (successVarDecl != null)
		{
			SPatternIR successVarDeclPattern = successVarDecl.getPattern();
			if (successVarDeclPattern instanceof AIdentifierPatternIR)
			{
				AIdentifierPatternIR idPattern = (AIdentifierPatternIR) successVarDeclPattern;
				data.setSuccessVarDecl(successVarDecl.clone());
				AIdentifierVarExpIR successVar = transAssistant.consSuccessVar(idPattern.getName());
				data.setSuccessVar(successVar);
			} else
			{
				Logger.getLog().printErrorln("Expected success variable declaration to use an identifier pattern. Got: "
						+ successVarDeclPattern);
			}
		}

		List<PatternInfo> patternInfo = new LinkedList<>();
		patternInfo.add(declInfo);

		ABlockStmIR replacementBlock = new ABlockStmIR();
		replacementBlock.getStatements().add(consPatternCheck(false, declInfo.getPattern(), declInfo.getType(), data, declInfo.getActualValue()));
		replacementBlock.getStatements().addAll(declBlockTmp.getStatements());
		ABlockStmIR enclosingBlock = nextElementDecl.getAncestor(ABlockStmIR.class);
		enclosingBlock.getLocalDefs().addAll(declBlockTmp.getLocalDefs());

		AVarDeclIR nextDeclCopy = nextElementDecl.clone();

		if (tag == null || !tag.isDeclared())
		{
			replacementBlock.getLocalDefs().addFirst(nextDeclCopy);
		} else
		{
			SPatternIR nextDeclPattern = nextDeclCopy.getPattern();
			if (nextDeclPattern instanceof AIdentifierPatternIR)
			{
				AIdentifierVarExpIR varExp = new AIdentifierVarExpIR();
				varExp.setType(nextDeclCopy.getType());
				varExp.setIsLocal(true);
				varExp.setIsLambda(false);
				varExp.setName(((AIdentifierPatternIR) nextDeclPattern).getName());

				AAssignToExpStmIR assignment = new AAssignToExpStmIR();
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
	
	private List<ABlockStmIR> consPatternHandlingBlockCases(
			List<PatternInfo> patternInfo, PatternBlockData patternData)
	{
		List<ABlockStmIR> patternHandlingBlocks = new LinkedList<ABlockStmIR>();

		for (PatternInfo currentInfo : patternInfo)
		{
			SPatternIR currentPattern = currentInfo.getPattern();

			ABlockStmIR nextPatternBlock = new ABlockStmIR();
			patternData.setDeclBlock(nextPatternBlock);

			// Use same success variable
			patternData.setPattern(currentPattern);

			if (currentPattern instanceof AIdentifierPatternIR)
			{
				nextPatternBlock.getStatements().add(consIdVarDeclaration(currentInfo, currentPattern));
				initSuccessVar(patternData, this.transAssistant.getInfo().getExpAssistant().consBoolLiteral(true), nextPatternBlock);
			} else if (currentPattern instanceof AIgnorePatternIR)
			{
				initSuccessVar(patternData, this.transAssistant.getInfo().getExpAssistant().consBoolLiteral(true), nextPatternBlock);

			} else
			{
				STypeIR currentType = currentInfo.getType();
				SExpIR currentActualValue = currentInfo.getActualValue();

				boolean declareVarPattern = true;
				ABlockStmIR patternCheck = consPatternCheck(declareVarPattern, currentPattern, currentType, patternData, currentActualValue);

				patternCheck.getLocalDefs().addAll(nextPatternBlock.getLocalDefs());
				nextPatternBlock = patternCheck;
			}

			patternHandlingBlocks.add(nextPatternBlock);
		}

		return patternHandlingBlocks;
	}

	private ABlockStmIR consIdVarDeclaration(PatternInfo currentInfo,
			SPatternIR currentPattern)
	{
		AIdentifierPatternIR idPattern = (AIdentifierPatternIR) currentPattern;
		AVarDeclIR idPatternDecl = transAssistant.getInfo().getDeclAssistant().
				consLocalVarDecl(currentInfo.getType().clone(),
						idPattern.clone(), currentInfo.getActualValue().clone());

		ABlockStmIR wrappingStatement = new ABlockStmIR();
		wrappingStatement.getLocalDefs().add(idPatternDecl);

		return wrappingStatement;
	}

	private ABlockStmIR consPatternHandlingBlock(List<PatternInfo> patternInfo)
	{
		ABlockStmIR topBlock = new ABlockStmIR();

		for (PatternInfo info : patternInfo)
		{
			SPatternIR currentPattern = info.getPattern();
			
			if(!basicCaseHandled(currentPattern))
			{
				ABlockStmIR currentDeclBlock = new ABlockStmIR();

				ABlockStmIR patternHandlingBlock = consPatternCheck(currentPattern, info.getType(), info.getActualValue(), currentDeclBlock);
				currentDeclBlock.getStatements().addFirst(patternHandlingBlock);
				topBlock.getStatements().add(currentDeclBlock);
			}
		}

		return topBlock;
	}
	
	private List<DeclBlockPair> consPatternHandlingBlocksSeparate(List<AVarDeclIR> decls, List<PatternInfo> patternInfo)
	{
		List<DeclBlockPair> blocks = new LinkedList<>();
		
		for (int i = 0; i < patternInfo.size(); i++)
		{
			PatternInfo info = patternInfo.get(i);

			if(!basicCaseHandled(info.getPattern()))
			{
				ABlockStmIR currentDeclBlock = new ABlockStmIR();

				ABlockStmIR patternHandlingBlock = consPatternCheck(info.getPattern(), info.getType(), info.getActualValue(), currentDeclBlock);
				currentDeclBlock.getStatements().addFirst(patternHandlingBlock);
				
				AVarDeclIR nextDecl = i < decls.size() - 1 ? decls.get(1 + i) : null;
				DeclBlockPair declBlockPair = new DeclBlockPair(decls.get(i), nextDecl, currentDeclBlock);
				
				blocks.add(declBlockPair);
			}
		}

		return blocks;
	}
	
	private boolean basicCaseHandled(SPatternIR currentPattern)
	{
		if (currentPattern instanceof AIdentifierPatternIR)
		{
			return true;
		} else if (currentPattern instanceof AIgnorePatternIR)
		{
			AIdentifierPatternIR idPattern = getIdPattern(config.getIgnorePatternPrefix());
			transAssistant.replaceNodeWith(currentPattern, idPattern);

			return true;
		}
		
		return false;
	}
	
	private ABlockStmIR consPatternCheck(SPatternIR pattern, STypeIR type,
			SExpIR actualValue, ABlockStmIR declBlock)
	{
		boolean declareVarPattern = false;
		PatternBlockData patternData = new PatternBlockData(pattern, declBlock, MismatchHandling.RAISE_ERROR);

		return consPatternCheck(declareVarPattern, pattern, type, patternData, actualValue);
	}

	private ABlockStmIR consPatternCheck(boolean declarePatternVar,
			SPatternIR pattern, STypeIR type, PatternBlockData patternData,
			SExpIR actualValue)
	{
		if (pattern instanceof ABoolPatternIR)
		{
			ABoolPatternIR boolPattern = (ABoolPatternIR) pattern;

			Boolean value = boolPattern.getValue();
			ABoolLiteralExpIR consBoolLiteral = transAssistant.getInfo().getExpAssistant().consBoolLiteral(value);

			return consSimplePatternCheck(declarePatternVar, boolPattern, consBoolLiteral, patternData, actualValue);
		} else if (pattern instanceof ACharPatternIR)
		{
			ACharPatternIR charPattern = (ACharPatternIR) pattern;

			Character value = charPattern.getValue();
			ACharLiteralExpIR charLiteral = transAssistant.getInfo().getExpAssistant().consCharLiteral(value);

			return consSimplePatternCheck(declarePatternVar, charPattern, charLiteral, patternData, actualValue);
		} else if (pattern instanceof AIntPatternIR)
		{
			AIntPatternIR intPattern = (AIntPatternIR) pattern;

			Long value = intPattern.getValue();
			AIntLiteralExpIR intLit = transAssistant.getInfo().getExpAssistant().consIntLiteral(value);

			return consSimplePatternCheck(declarePatternVar, intPattern, intLit, patternData, actualValue);
		} else if (pattern instanceof ANullPatternIR)
		{
			return consSimplePatternCheck(declarePatternVar, pattern, transAssistant.getInfo().getExpAssistant().consNullExp(), patternData, actualValue);
		} else if (pattern instanceof AQuotePatternIR)
		{
			AQuotePatternIR quotePattern = (AQuotePatternIR) pattern;

			String value = quotePattern.getValue();
			AQuoteLiteralExpIR quoteLit = transAssistant.getInfo().getExpAssistant().consQuoteLiteral(value);

			return consSimplePatternCheck(declarePatternVar, pattern, quoteLit, patternData, actualValue);
		} else if (pattern instanceof ARealPatternIR)
		{
			ARealPatternIR realPattern = (ARealPatternIR) pattern;

			Double value = realPattern.getValue();
			ARealLiteralExpIR realLit = transAssistant.getInfo().getExpAssistant().consRealLiteral(value);

			return consSimplePatternCheck(declarePatternVar, realPattern, realLit, patternData, actualValue);

		} else if (pattern instanceof AStringPatternIR)
		{
			AStringPatternIR stringPattern = (AStringPatternIR) pattern;
			String value = stringPattern.getValue();

			SExpIR stringValue = null;

			if (transAssistant.getInfo().getSettings().getCharSeqAsString())
			{
				stringValue = transAssistant.getInfo().getExpAssistant().consStringLiteral(value, false);
			} else
			{
				ASeqSeqTypeIR seqType = new ASeqSeqTypeIR();
				seqType.setEmpty(false);
				seqType.setSeqOf(new ACharBasicTypeIR());

				stringValue = transAssistant.getInfo().getExpAssistant().consCharSequence(seqType, value);
			}

			return consSimplePatternCheck(declarePatternVar, stringPattern, stringValue, patternData, actualValue);
		} else if (pattern instanceof ATuplePatternIR)
		{
			ATuplePatternIR tuplePattern = (ATuplePatternIR) pattern;
			
			if (type instanceof ATupleTypeIR)
			{
				ATupleTypeIR tupleType = (ATupleTypeIR) type;

				return consTuplePatternCheck(declarePatternVar, tuplePattern, 
						tupleType, patternData, actualValue, false);
			}
			else if(type instanceof AUnionTypeIR)
			{
					return consUnionTypedTuplePatternCheck(declarePatternVar,
							(AUnionTypeIR) type, patternData, actualValue, tuplePattern);
			}
			else
			{
				Logger.getLog().printErrorln("Expected tuple type or union type "
						+ "in 'PatternTransformation'. Got: " + type);
			}
		} else if (pattern instanceof ARecordPatternIR)
		{
			ARecordPatternIR recordPattern = (ARecordPatternIR) pattern;
			ARecordTypeIR recordType = (ARecordTypeIR) recordPattern.getType();
			
			if(type instanceof ARecordTypeIR)
			{
				
				return consRecordPatternCheck(declarePatternVar, recordPattern,
						recordType, patternData, actualValue, checkRecordPattern(actualValue));
				
			}
			else if(type instanceof AUnionTypeIR)
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

	private ABlockStmIR consUnionTypedTuplePatternCheck(
			boolean declarePatternVar, AUnionTypeIR unionType,
			PatternBlockData patternData, SExpIR actualValue,
			ATuplePatternIR tuplePattern)
	{
		ATupleTypeIR resTupleType = transAssistant.getInfo().getPatternAssistant().getTupleType(unionType, tuplePattern);

		ABlockStmIR tuplePatternCheck = consTuplePatternCheck(declarePatternVar, tuplePattern, resTupleType, patternData, actualValue, true);

		AInstanceofExpIR instanceCheck = new AInstanceofExpIR();
		instanceCheck.setType(new ABoolBasicTypeIR());
		instanceCheck.setCheckedType(patternData.getRootPatternVar().getType().clone());
		instanceCheck.setExp(patternData.getRootPatternVar().clone());

		AIfStmIR typeCheck = new AIfStmIR();
		typeCheck.setIfExp(instanceCheck);
		typeCheck.setThenStm(tuplePatternCheck);

		ABlockStmIR block = new ABlockStmIR();
		block.getStatements().add(typeCheck);

		return block;
	}

	private ABlockStmIR consRecordPatternCheck(boolean declarePattern,
			ARecordPatternIR recordPattern, ARecordTypeIR recordType,
			PatternBlockData patternData, SExpIR actualValue, boolean checkRecordType)
	{
		AIdentifierPatternIR idPattern = getIdPattern(config.getName(recordPattern.getClass()));

		AIdentifierVarExpIR recordPatternVar = new AIdentifierVarExpIR();
		recordPatternVar.setType(recordType.clone());
		recordPatternVar.setName(idPattern.getName());
		recordPatternVar.setIsLambda(false);
		recordPatternVar.setIsLocal(true);
		patternData.setRootPatternVar(recordPatternVar);
		
		if(!declarePattern)
		{
			actualValue = recordPatternVar;
		}
		
		ABlockStmIR recordPatternBlock = initPattern(declarePattern, recordPattern, recordType, actualValue, idPattern);

		ARecordDeclIR record = transAssistant.getInfo().getAssistantManager().getDeclAssistant().findRecord(transAssistant.getInfo().getClasses(), recordType);

		if (patternData.getSuccessVarDecl() == null)
		{
			consSuccessVarCheck(recordPattern, patternData);
		}

		mismatchHandling(recordPattern, patternData);
		initSuccessVar(patternData, transAssistant.getInfo().getExpAssistant().consBoolLiteral(true), recordPatternBlock);

		List<STypeIR> types = new LinkedList<STypeIR>();

		for (AFieldDeclIR currentField : record.getFields())
		{
			types.add(currentField.getType());
		}

		ABlockStmIR fieldCheckBlock = consFieldCheckBlock(patternData, recordPatternVar, 
				recordPattern.getPatterns(), types, checkRecordType && !declarePattern);

		recordPatternBlock.getStatements().add(fieldCheckBlock);

		if (checkRecordType)
		{
			AInstanceofExpIR instanceOfExp = new AInstanceofExpIR();
			instanceOfExp.setType(new ABoolBasicTypeIR());
			instanceOfExp.setExp(actualValue.clone());
			instanceOfExp.setCheckedType(recordType.clone());

			AIfStmIR ifStm = new AIfStmIR();
			ifStm.setIfExp(instanceOfExp);
			ifStm.setThenStm(recordPatternBlock);

			AAssignToExpStmIR setFalse = new AAssignToExpStmIR();
			setFalse.setTarget(patternData.getSuccessVar().clone());
			setFalse.setExp(transAssistant.getInfo().getExpAssistant().consBoolLiteral(false));
			ifStm.setElseStm(setFalse);

			ABlockStmIR wrappingBlock = new ABlockStmIR();
			wrappingBlock.getStatements().add(ifStm);

			return wrappingBlock;
		}
		
		return recordPatternBlock;
	}

	@SuppressWarnings("unchecked")
	private ABlockStmIR consTuplePatternCheck(boolean declarePatternVar,
			ATuplePatternIR tuplePattern, ATupleTypeIR tupleType,
			PatternBlockData patternData, SExpIR actualValue, boolean cast)
	{
		AIdentifierPatternIR idPattern = getIdPattern(config.getName(tuplePattern.getClass()));

		ABlockStmIR tuplePatternBlock = initPattern(declarePatternVar, tuplePattern, tupleType, actualValue, idPattern);

		AIdentifierVarExpIR tuplePatternVar = new AIdentifierVarExpIR();
		tuplePatternVar.setType(tupleType.clone());
		tuplePatternVar.setName(idPattern.getName());
		tuplePatternVar.setIsLambda(false);
		tuplePatternVar.setIsLocal(true);
		patternData.setRootPatternVar(tuplePatternVar);

		ATupleCompatibilityExpIR tupleCheck = new ATupleCompatibilityExpIR();
		tupleCheck.setType(new ABoolBasicTypeIR());
		
		if (!cast)
		{
			tupleCheck.setTuple(tuplePatternVar.clone());
		} else
		{
			ACastUnaryExpIR castTuple = new ACastUnaryExpIR();
			castTuple.setType(tupleType.clone());
			castTuple.setExp(tuplePatternVar.clone());
			tupleCheck.setTuple(castTuple);
		}
		
		tupleCheck.setTypes((List<? extends STypeIR>) tupleType.getTypes().clone());

		if (patternData.getSuccessVarDecl() == null)
		{
			consSuccessVarCheck(tuplePattern, patternData);
		}

		mismatchHandling(tuplePattern, patternData);
		initSuccessVar(patternData, tupleCheck, tuplePatternBlock);

		LinkedList<SPatternIR> patterns = tuplePattern.getPatterns();
		LinkedList<STypeIR> types = tupleType.getTypes();

		AIfStmIR fieldSizeCheck = new AIfStmIR();
		fieldSizeCheck.setIfExp(patternData.getSuccessVar().clone());
		fieldSizeCheck.setThenStm(consFieldCheckBlock(patternData, tuplePatternVar, patterns, types, cast));

		tuplePatternBlock.getStatements().add(fieldSizeCheck);

		return tuplePatternBlock;
	}

	private void consSuccessVarCheck(SPatternIR pattern,
			PatternBlockData patternData)
	{
		String successVarName = this.transAssistant.getInfo().getTempVarNameGen().nextVarName(iteVarPrefixes.success());
		SExpIR init = null;

		if (!patternData.IsRootPattern(pattern))
		{
			init = transAssistant.getInfo().getExpAssistant().consBoolLiteral(pattern instanceof ATuplePatternIR ? false
					: true);
			init.setType(new ABoolBasicTypeIR());
		} else
		{
			init = new AUndefinedExpIR();
			init.setType(new AUnknownTypeIR());
		}

		AVarDeclIR successVarDecl = transAssistant.consDecl(successVarName, new ABoolBasicTypeIR(), init);
		patternData.setSuccessVarDecl(successVarDecl);

		AIdentifierVarExpIR successVar = transAssistant.consSuccessVar(successVarName);
		patternData.setSuccessVar(successVar);

		patternData.getDeclBlock().getLocalDefs().add(successVarDecl);
	}

	private void mismatchHandling(SPatternIR pattern,
			PatternBlockData patternData)
	{
		if (!patternData.IsRootPattern(pattern))
		{
			return;
		}

		if (patternData.getMismatchHandling() == MismatchHandling.RAISE_ERROR)
		{
			APatternMatchRuntimeErrorExpIR matchFail = new APatternMatchRuntimeErrorExpIR();
			matchFail.setType(new AErrorTypeIR());
			matchFail.setMessage(config.getMatchFailedMessage(pattern));
			ARaiseErrorStmIR noMatchStm = new ARaiseErrorStmIR();
			noMatchStm.setError(matchFail);

			AIfStmIR consMismatchCheck = consMismatchCheck(patternData.getSuccessVar(), noMatchStm);
			patternData.getDeclBlock().getStatements().add(consMismatchCheck);
		} else if (patternData.getMismatchHandling() == MismatchHandling.LOOP_CONTINUE)
		{
			AIfStmIR consMismatchCheck = consMismatchCheck(patternData.getSuccessVar(), new AContinueStmIR());
			patternData.getDeclBlock().getStatements().add(consMismatchCheck);
		}
	}

	private AIfStmIR consMismatchCheck(AIdentifierVarExpIR successVar,
			SStmIR noMatchStm)
	{
		AIfStmIR ifCheck = new AIfStmIR();
		ifCheck.setIfExp(transAssistant.consBoolCheck(successVar.getName(), true));
		ifCheck.setThenStm(noMatchStm);

		return ifCheck;
	}

	private void initSuccessVar(PatternBlockData patternData, SExpIR initExp,
			ABlockStmIR patternBlock)
	{
		if (patternData.getSuccessVarDecl().getExp() instanceof AUndefinedExpIR)
		{
			patternData.getSuccessVarDecl().setExp(initExp);
		} else
		{
			AAssignToExpStmIR successVarAssignment = new AAssignToExpStmIR();
			successVarAssignment.setTarget(patternData.getSuccessVar().clone());
			successVarAssignment.setExp(initExp);

			patternBlock.getStatements().add(successVarAssignment);
		}
	}

	private ABlockStmIR initPattern(boolean declare, SPatternIR pattern,
			STypeIR type, SExpIR actualValue, AIdentifierPatternIR idPattern)
	{
		ABlockStmIR patternBlock = new ABlockStmIR();

		if (declare)
		{
			AVarDeclIR patternDecl = transAssistant.getInfo().getDeclAssistant().
					consLocalVarDecl(type.clone(), idPattern.clone(),
							actualValue.clone());
			patternBlock.getLocalDefs().add(patternDecl);
		} else
		{
			transAssistant.replaceNodeWith(pattern, idPattern);
		}

		return patternBlock;
	}

	private ABlockStmIR consFieldCheckBlock(PatternBlockData patternData,
			AIdentifierVarExpIR patternVar, List<SPatternIR> patterns,
			List<STypeIR> types, boolean cast)
	{
		ABlockStmIR thenPart = new ABlockStmIR();
		ABlockStmIR topBlock = thenPart;

		for (int i = 0; i < patterns.size(); i++)
		{
			SPatternIR currentPattern = patterns.get(i);
			STypeIR currentType = types.get(i);

			if (skipPattern(currentPattern))
			{
				continue;
			} else
			{
				SExpIR actualValue = consFieldValueToMatch(patternVar, i, currentType, cast);

				if (currentPattern instanceof AIdentifierPatternIR)
				{
					AAssignToExpStmIR localAssignment = declareAndAssignIdVarAssignment(patternData.getDeclBlock(), currentPattern, currentType, actualValue);
					thenPart.getStatements().add(localAssignment);
				} else
				{
					ABlockStmIR patternBlock = consPatternBlock(patternData, currentPattern, currentType, actualValue, cast);

					if (patternBlock != null)
					{
						thenPart.getStatements().add(patternBlock);

						// The tuple/record pattern have more field patterns to be generated.
						// Check the success variable and add a new nesting level
						if (morePatternsToGenerate(patterns,i))
						{
							AIfStmIR successVarCheck = new AIfStmIR();
							successVarCheck.setIfExp(patternData.getSuccessVar().clone());

							thenPart.getStatements().add(successVarCheck);

							ABlockStmIR newThenPart = new ABlockStmIR();
							successVarCheck.setThenStm(newThenPart);

							thenPart = newThenPart;
						}
					}
				}
			}
		}

		return topBlock;
	}

	private boolean skipPattern(SPatternIR pattern)
	{
		return pattern instanceof AIgnorePatternIR;
	}
	
	private boolean morePatternsToGenerate(List<SPatternIR> patterns, int currentPatternIndex)
	{
		int nextPatternIndex = currentPatternIndex + 1;

		for (int i = nextPatternIndex; i < patterns.size(); i++)
		{
			SPatternIR nextPattern = patterns.get(i);
			
			if (!skipPattern(nextPattern))
			{
				return true;
			}
		}

		return false;
	}

	private ABlockStmIR consPatternBlock(PatternBlockData patternData,
			SPatternIR currentPattern, STypeIR currentType, SExpIR actualValue, boolean cast)
	{
		ABlockStmIR patternBlock = null;

		if (currentPattern instanceof ATuplePatternIR)
		{
			ATuplePatternIR nextTuplePattern = (ATuplePatternIR) currentPattern;
			ATupleTypeIR nextTupleType = (ATupleTypeIR) currentType;

			patternBlock = consTuplePatternCheck(true, nextTuplePattern, nextTupleType, patternData, actualValue, cast);

		} else if (currentPattern instanceof ARecordPatternIR)
		{
			ARecordPatternIR nextRecordPattern = (ARecordPatternIR) currentPattern;
			ARecordTypeIR nextRecordType = (ARecordTypeIR) nextRecordPattern.getType();
			boolean checkRecordPattern = checkRecordPattern(actualValue);
			
			patternBlock = consRecordPatternCheck(true, nextRecordPattern, nextRecordType, patternData, actualValue, checkRecordPattern);
		} else
		{
			patternBlock = consPatternCheck(true, currentPattern, currentType, patternData, actualValue);
		}

		return patternBlock;
	}

	private SExpIR consFieldValueToMatch(AIdentifierVarExpIR patternVar,
			int fieldNumber, STypeIR currentType, boolean cast)
	{
		if (patternVar.getType() instanceof ATupleTypeIR)
		{
			return consTupleFieldExp(patternVar, fieldNumber, currentType, cast);
		} else if (patternVar.getType() instanceof ARecordTypeIR)
		{
			return consRecFieldExp(patternVar, fieldNumber, currentType, cast);
		}

		return null;
	}

	private AAssignToExpStmIR declareAndAssignIdVarAssignment(
			ABlockStmIR declBlock, SPatternIR currentPattern,
			STypeIR currentType, SExpIR valueToMatch)
	{
		AIdentifierPatternIR currentId = (AIdentifierPatternIR) currentPattern;

		AVarDeclIR idVarDecl = transAssistant.getInfo().getDeclAssistant().consLocalVarDecl(currentType.clone(), 
				currentPattern.clone(), new AUndefinedExpIR());

		declBlock.getLocalDefs().add(idVarDecl);

		AIdentifierVarExpIR var = new AIdentifierVarExpIR();
		var.setType(currentType.clone());
		var.setName(currentId.getName());
		var.setIsLocal(true);
		var.setIsLambda(false);

		AAssignToExpStmIR localAssignment = new AAssignToExpStmIR();
		localAssignment.setTarget(var);
		localAssignment.setExp(valueToMatch);

		return localAssignment;
	}

	private <T> ABlockStmIR consSimplePatternCheck(boolean declarePatternVar,
			SPatternIR pattern, SExpIR valueToMatch,
			PatternBlockData patternData, SExpIR actualValue)
	{
		// Example:
		// Number intPattern_2 = 1L;
		// Boolean success_2 = intPattern_2.longValue() == 1L;

		AIdentifierPatternIR idPattern = getIdPattern(config.getName(pattern.getClass()));
		transAssistant.replaceNodeWith(pattern, idPattern);

		ABlockStmIR block = new ABlockStmIR();

		if (declarePatternVar)
		{
			AVarDeclIR patternDecl = transAssistant.getInfo().getDeclAssistant().
					consLocalVarDecl(actualValue.getType().clone(), 
					idPattern.clone(), actualValue.clone());
			block.getLocalDefs().add(patternDecl);
		}

		AIdentifierVarExpIR var = new AIdentifierVarExpIR();
		var.setType(valueToMatch.getType().clone());
		var.setName(idPattern.getName());
		var.setIsLambda(false);
		var.setIsLocal(true);
		patternData.setRootPatternVar(var);

		AEqualsBinaryExpIR check = new AEqualsBinaryExpIR();
		check.setType(new ABoolBasicTypeIR());
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

	public List<PatternInfo> extractFromLocalDefs(List<AVarDeclIR> localDefs)
	{
		List<PatternInfo> patternInfo = new LinkedList<>();

		for (AVarDeclIR decl : localDefs)
		{
			PatternInfo currentInfo = extractPatternInfo(decl);
			patternInfo.add(currentInfo);
		}

		return patternInfo;
	}

	private PatternInfo extractPatternInfo(AVarDeclIR decl)
	{
		STypeIR type = decl.getType();
		SPatternIR pattern = decl.getPattern();
		SExpIR actualValue = decl.getExp();

		return new PatternInfo(type, pattern, actualValue);
	}

	public List<PatternInfo> extractFromParams(
			List<AFormalParamLocalParamIR> params)
	{
		List<PatternInfo> patternInfo = new LinkedList<>();

		for (AFormalParamLocalParamIR param : params)
		{
			STypeIR type = param.getType();
			SPatternIR pattern = param.getPattern();

			patternInfo.add(new PatternInfo(type, pattern, null));
		}

		return patternInfo;
	}

	public List<PatternInfo> extractFromCases(List<ACaseAltStmStmIR> cases,
			SExpIR exp)
	{
		List<PatternInfo> patternInfo = new LinkedList<>();

		for (ACaseAltStmStmIR alt : cases)
		{
			patternInfo.add(new PatternInfo(alt.getPatternType(), alt.getPattern(), exp));
		}

		return patternInfo;
	}

	private AIdentifierPatternIR getIdPattern(String namePrefix)
	{
		String name = transAssistant.getInfo().getTempVarNameGen().nextVarName(namePrefix);

		AIdentifierPatternIR idPattern = new AIdentifierPatternIR();
		idPattern.setName(name);

		return idPattern;
	}

	private AFieldNumberExpIR consTupleFieldExp(
			AIdentifierVarExpIR tuplePatternVar, int i, STypeIR currentType, boolean cast)
	{
		AFieldNumberExpIR fieldNumberExp = new AFieldNumberExpIR();
		fieldNumberExp.setType(currentType.clone());
		
		if (!cast)
		{
			fieldNumberExp.setTuple(tuplePatternVar.clone());
		}
		else
		{
			ACastUnaryExpIR castedExp = new ACastUnaryExpIR();
			castedExp.setType(tuplePatternVar.getType().clone());
			castedExp.setExp(tuplePatternVar.clone());
			fieldNumberExp.setTuple(castedExp);
		}
		
		fieldNumberExp.setField(new Long(1 + i));

		return fieldNumberExp;
	}

	private SExpIR consRecFieldExp(AIdentifierVarExpIR patternVar, int i,
			STypeIR currentType, boolean cast)
	{
		ARecordTypeIR recordType = (ARecordTypeIR) patternVar.getType();

		AFieldDeclIR recordField = transAssistant.getInfo().getAssistantManager().getDeclAssistant().getFieldDecl(transAssistant.getInfo().getClasses(), recordType, i);
		String fieldName = recordField.getName();

		AFieldExpIR fieldExp = consRecFieldExp(patternVar, currentType, fieldName);
		
		if(cast)
		{
			ACastUnaryExpIR casted = new ACastUnaryExpIR();
			casted.setType(recordType.clone());
			casted.setExp(fieldExp.getObject());
			
			fieldExp.setObject(casted);
		}
		
		return fieldExp;
	}

	private AFieldExpIR consRecFieldExp(AIdentifierVarExpIR patternVar,
			STypeIR currentType, String fieldName)
	{
		AFieldExpIR fieldExp = new AFieldExpIR();
		fieldExp.setType(currentType.clone());
		fieldExp.setObject(patternVar.clone());
		fieldExp.setMemberName(fieldName);

		return fieldExp;
	}

	private DeclarationTag fetchTag(PIR node)
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

	private boolean checkRecordPattern(SExpIR actualValue)
	{
		return actualValue != null && actualValue.getType() instanceof AUnionTypeIR;
	}
}
