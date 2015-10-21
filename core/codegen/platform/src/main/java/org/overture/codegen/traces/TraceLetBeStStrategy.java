package org.overture.codegen.traces;

import java.util.List;
import java.util.Map;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.ACallObjectExpStmCG;
import org.overture.codegen.cgast.statements.AContinueStmCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.statements.ALocalPatternAssignmentStmCG;
import org.overture.codegen.cgast.types.SSetTypeCG;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.trans.DeclarationTag;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.trans.iterator.ILanguageIterator;
import org.overture.codegen.trans.let.LetBeStStrategy;

public class TraceLetBeStStrategy extends LetBeStStrategy
{
	protected TraceNodeData nodeData;
	protected AVarDeclCG altTests;
	protected AIdentifierPatternCG id;
	protected TraceNames tracePrefixes;
	protected StoreAssistant storeAssistant;
	protected Map<String, String> idConstNameMap;
	
	public TraceLetBeStStrategy(TransAssistantCG transAssistant,
			SExpCG suchThat, SSetTypeCG setType,
			ILanguageIterator langIterator, ITempVarGen tempGen,
			IterationVarPrefixes iteVarPrefixes, StoreAssistant storeAssistant,Map<String, String> idConstNameMap, TraceNames tracePrefixes,
			AIdentifierPatternCG id, AVarDeclCG altTests, TraceNodeData nodeData)
	{
		super(transAssistant, suchThat, setType, langIterator, tempGen, iteVarPrefixes);

		this.storeAssistant = storeAssistant;
		this.idConstNameMap = idConstNameMap;
		this.tracePrefixes = tracePrefixes;
		this.id = id;
		this.altTests = altTests;
		this.nodeData = nodeData;
	}

	@Override
	public List<AVarDeclCG> getOuterBlockDecls(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns) throws AnalysisException
	{
		for (SPatternCG id : patterns)
		{
			AVarDeclCG decl = transAssistant.getInfo().getDeclAssistant().
					consLocalVarDecl(setType.getSetOf().clone(), id.clone(), transAssistant.getInfo().getExpAssistant().consUndefinedExp());
			decl.setFinal(true);
			decls.add(decl);
		}

		return packDecl(altTests);
	}

	@Override
	public List<SStmCG> getPreForLoopStms(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
	{
		return null;
	}

	@Override
	public SExpCG getForLoopCond(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
			throws AnalysisException
	{
		return langIterator.getForLoopCond(setVar, patterns, pattern);
	}

	@Override
	public DeclarationTag consDeclarationTag()
	{
		return new DeclarationTag(false, successVarDecl);
	}

	@Override
	public AVarDeclCG getNextElementDeclared(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
			throws AnalysisException
	{
		AVarDeclCG nextElementDecl = decls.get(count++);
		tagNextElementDeclared(nextElementDecl);

		nextElementDecl.setExp(langIterator.consNextElementCall(setVar));

		return nextElementDecl;
	}

	@Override
	public ALocalPatternAssignmentStmCG getNextElementAssigned(
			AIdentifierVarExpCG setVar, List<SPatternCG> patterns,
			SPatternCG pattern) throws AnalysisException
	{
		return null;
	}

	@Override
	public List<SStmCG> getForLoopStms(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
	{
		ABlockStmCG block = new ABlockStmCG();

		if (suchThat != null)
		{
			AIfStmCG ifStm = new AIfStmCG();
			ifStm.setIfExp(transAssistant.getInfo().getExpAssistant().negate(suchThat.clone()));
			ifStm.setThenStm(new AContinueStmCG());
			block.getStatements().add(ifStm);
		}
		
		AVarDeclCG nextElementDecl = decls.get(count - 1);
		
		
		IdentifierPatternCollector idCollector = new IdentifierPatternCollector();
		idCollector.setTopNode(nextElementDecl);
		
		for(AIdentifierPatternCG idToReg : idCollector.findOccurences())
		{
			String idConstName = idConstNameMap.get(idToReg.getName());
			block.getStatements().add(transAssistant.wrap(storeAssistant.consIdConstDecl(idConstName)));
			storeAssistant.appendStoreRegStms(block, idToReg.getName(), idConstName);
		}
		
		block.getStatements().add(nodeData.getStms());

		STypeCG instanceType = altTests.getType().clone();
		String addName = tracePrefixes.addMethodName();
		AIdentifierVarExpCG arg = nodeData.getNodeVar();
		ACallObjectExpStmCG addCall = transAssistant.consInstanceCallStm(instanceType, id.getName(), addName, arg);
		block.getStatements().add(addCall);

		return packStm(block);
	}

	@Override
	public List<SStmCG> getPostOuterBlockStms(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns)
	{
		return null;
	}
}
