package org.overture.codegen.traces;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.overture.ast.lex.Dialect;
import org.overture.codegen.ir.SExpCG;
import org.overture.codegen.ir.SPatternCG;
import org.overture.codegen.ir.SStmCG;
import org.overture.codegen.ir.STypeCG;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AVarDeclCG;
import org.overture.codegen.ir.expressions.AIdentifierVarExpCG;
import org.overture.codegen.ir.patterns.AIdentifierPatternCG;
import org.overture.codegen.ir.statements.ABlockStmCG;
import org.overture.codegen.ir.statements.ACallObjectExpStmCG;
import org.overture.codegen.ir.statements.AContinueStmCG;
import org.overture.codegen.ir.statements.AIfStmCG;
import org.overture.codegen.ir.statements.ALocalPatternAssignmentStmCG;
import org.overture.codegen.ir.types.ASetSetTypeCG;
import org.overture.codegen.ir.types.SSetTypeCG;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.DeclarationTag;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.trans.iterator.ILanguageIterator;
import org.overture.codegen.trans.let.LetBeStStrategy;
import org.overture.config.Settings;

public class TraceLetBeStStrategy extends LetBeStStrategy
{
	protected TraceNodeData nodeData;
	protected AVarDeclCG altTests;
	protected AIdentifierPatternCG id;
	protected TraceNames tracePrefixes;
	protected StoreAssistant storeAssistant;
	protected Map<String, String> idConstNameMap;
	protected TraceStmBuilder builder;

	public TraceLetBeStStrategy(TransAssistantCG transAssistant, SExpCG suchThat, SSetTypeCG setType,
			ILanguageIterator langIterator, ITempVarGen tempGen, IterationVarPrefixes iteVarPrefixes,
			StoreAssistant storeAssistant, Map<String, String> idConstNameMap, TraceNames tracePrefixes,
			AIdentifierPatternCG id, AVarDeclCG altTests, TraceNodeData nodeData, TraceStmBuilder builder)
	{
		super(transAssistant, suchThat, setType, langIterator, tempGen, iteVarPrefixes);

		this.storeAssistant = storeAssistant;
		this.idConstNameMap = idConstNameMap;
		this.tracePrefixes = tracePrefixes;
		this.id = id;
		this.altTests = altTests;
		this.nodeData = nodeData;
		this.builder = builder;
	}

	@Override
	public List<AVarDeclCG> getOuterBlockDecls(AIdentifierVarExpCG setVar, List<SPatternCG> patterns)
			throws AnalysisException
	{
		for (SPatternCG id : patterns)
		{
			AVarDeclCG decl = transAssist.getInfo().getDeclAssistant().consLocalVarDecl(setType.getSetOf().clone(), id.clone(), transAssist.getInfo().getExpAssistant().consUndefinedExp());
			decl.setFinal(true);
			decls.add(decl);
		}

		return packDecl(altTests);
	}

	@Override
	public List<SStmCG> getPreForLoopStms(AIdentifierVarExpCG setVar, List<SPatternCG> patterns, SPatternCG pattern)
	{
		return null;
	}

	@Override
	public SExpCG getForLoopCond(AIdentifierVarExpCG setVar, List<SPatternCG> patterns, SPatternCG pattern)
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
	public AVarDeclCG getNextElementDeclared(AIdentifierVarExpCG setVar, List<SPatternCG> patterns, SPatternCG pattern)
			throws AnalysisException
	{
		AVarDeclCG nextElementDecl = decls.get(count++);
		tagNextElementDeclared(nextElementDecl);

		nextElementDecl.setExp(langIterator.consNextElementCall(setVar));

		return nextElementDecl;
	}

	@Override
	public ALocalPatternAssignmentStmCG getNextElementAssigned(AIdentifierVarExpCG setVar, List<SPatternCG> patterns,
			SPatternCG pattern) throws AnalysisException
	{
		return null;
	}

	@Override
	public List<SStmCG> getForLoopStms(AIdentifierVarExpCG setVar, List<SPatternCG> patterns, SPatternCG pattern)
	{
		ABlockStmCG block = new ABlockStmCG();

		if (suchThat != null)
		{
			AIfStmCG ifStm = new AIfStmCG();
			ifStm.setIfExp(transAssist.getInfo().getExpAssistant().negate(suchThat.clone()));
			ifStm.setThenStm(new AContinueStmCG());
			block.getStatements().add(ifStm);
		}

		AVarDeclCG nextElementDecl = decls.get(count - 1);

		IdentifierPatternCollector idCollector = new IdentifierPatternCollector();
		idCollector.setTopNode(nextElementDecl);

		PatternTypeFinder typeFinder = new PatternTypeFinder(transAssist.getInfo());

		if (setVar.getType() instanceof ASetSetTypeCG)
		{
			STypeCG elemType = ((ASetSetTypeCG) setVar.getType()).getSetOf();

			try
			{
				nextElementDecl.getPattern().apply(typeFinder, elemType);
			} catch (AnalysisException e)
			{
				Logger.getLog().printErrorln("Unexpectected problem occurred when trying to determine the type of pattern "
						+ nextElementDecl.getPattern());
				e.printStackTrace();
			}
		} else
		{
			Logger.getLog().printErrorln("Expected type of set to be a set type in '" + this.getClass().getSimpleName()
					+ "'. Got: " + setVar.getType());
		}

		List<AIdentifierVarExpCG> traceVars = new LinkedList<>();
		
		for (AIdentifierPatternCG idToReg : idCollector.findOccurences())
		{
			if(Settings.dialect != Dialect.VDM_SL)
			{
				String idConstName = idConstNameMap.get(idToReg.getName());
				block.getStatements().add(transAssist.wrap(storeAssistant.consIdConstDecl(idConstName)));
				storeAssistant.appendStoreRegStms(block, idToReg.getName(), idConstName, false);
			}
			
			traceVars.add(this.transAssist.getInfo().getExpAssistant().consIdVar(idToReg.getName(), PatternTypeFinder.getType(typeFinder, idToReg)));
		}

		block.getStatements().add(nodeData.getStms());

		STypeCG instanceType = altTests.getType().clone();
		AIdentifierVarExpCG subject = nodeData.getNodeVar();

		for (int i = traceVars.size() - 1; i >= 0; i--)
		{
			block.getStatements().add(builder.consAddTraceVarCall(subject, traceVars.get(i)));
		}

		// E.g. alternatives_2.add(apply_1)
		ACallObjectExpStmCG addCall = transAssist.consInstanceCallStm(instanceType, id.getName(), tracePrefixes.addMethodName(), subject);
		block.getStatements().add(addCall);

		return packStm(block);
	}

	@Override
	public List<SStmCG> getPostOuterBlockStms(AIdentifierVarExpCG setVar, List<SPatternCG> patterns)
	{
		return null;
	}
}
