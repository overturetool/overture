package org.overture.codegen.traces;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.ACallObjectExpStmIR;
import org.overture.codegen.ir.statements.AContinueStmIR;
import org.overture.codegen.ir.statements.AIfStmIR;
import org.overture.codegen.ir.statements.ALocalPatternAssignmentStmIR;
import org.overture.codegen.ir.types.ASetSetTypeIR;
import org.overture.codegen.ir.types.SSetTypeIR;
import org.overture.codegen.trans.DeclarationTag;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.trans.iterator.ILanguageIterator;
import org.overture.codegen.trans.let.LetBeStStrategy;
import org.overture.config.Settings;

public class TraceLetBeStStrategy extends LetBeStStrategy
{
	protected TraceNodeData nodeData;
	protected AVarDeclIR altTests;
	protected AIdentifierPatternIR id;
	protected TraceNames tracePrefixes;
	protected StoreAssistant storeAssistant;
	protected Map<String, String> idConstNameMap;
	protected TraceStmBuilder builder;

	private Logger log = Logger.getLogger(this.getClass().getSimpleName());

	public TraceLetBeStStrategy(TransAssistantIR transAssistant,
			SExpIR suchThat, SSetTypeIR setType, ILanguageIterator langIterator,
			ITempVarGen tempGen, IterationVarPrefixes iteVarPrefixes,
			StoreAssistant storeAssistant, Map<String, String> idConstNameMap,
			TraceNames tracePrefixes, AIdentifierPatternIR id,
			AVarDeclIR altTests, TraceNodeData nodeData,
			TraceStmBuilder builder)
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
	public List<AVarDeclIR> getOuterBlockDecls(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns) throws AnalysisException
	{
		STypeIR elementType = transAssist.getElementType(setSeqType);

		for (SPatternIR id : patterns)
		{
			AVarDeclIR decl = transAssist.getInfo().getDeclAssistant().consLocalVarDecl(elementType.clone(), id.clone(), transAssist.getInfo().getExpAssistant().consUndefinedExp());
			decl.setFinal(true);
			decls.add(decl);
		}

		return packDecl(altTests);
	}

	@Override
	public List<SStmIR> getPreForLoopStms(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns, SPatternIR pattern)
	{
		return null;
	}

	@Override
	public SExpIR getForLoopCond(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns, SPatternIR pattern)
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
	public AVarDeclIR getNextElementDeclared(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns, SPatternIR pattern)
			throws AnalysisException
	{
		AVarDeclIR nextElementDecl = decls.get(count++);
		tagNextElementDeclared(nextElementDecl);

		nextElementDecl.setExp(langIterator.consNextElementCall(setVar));

		return nextElementDecl;
	}

	@Override
	public ALocalPatternAssignmentStmIR getNextElementAssigned(
			AIdentifierVarExpIR setVar, List<SPatternIR> patterns,
			SPatternIR pattern) throws AnalysisException
	{
		return null;
	}

	@Override
	public List<SStmIR> getForLoopStms(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns, SPatternIR pattern)
	{
		ABlockStmIR block = new ABlockStmIR();

		if (suchThat != null)
		{
			AIfStmIR ifStm = new AIfStmIR();
			ifStm.setIfExp(transAssist.getInfo().getExpAssistant().negate(suchThat.clone()));
			ifStm.setThenStm(new AContinueStmIR());
			block.getStatements().add(ifStm);
		}

		AVarDeclIR nextElementDecl = decls.get(count - 1);

		IdentifierPatternCollector idCollector = new IdentifierPatternCollector();
		idCollector.setTopNode(nextElementDecl);

		PatternTypeFinder typeFinder = new PatternTypeFinder(transAssist.getInfo());

		if (setVar.getType() instanceof ASetSetTypeIR)
		{
			STypeIR elemType = ((ASetSetTypeIR) setVar.getType()).getSetOf();

			try
			{
				nextElementDecl.getPattern().apply(typeFinder, elemType);
			} catch (AnalysisException e)
			{
				log.error("Unexpectected problem occurred when trying to determine the type of pattern "
						+ nextElementDecl.getPattern());
				e.printStackTrace();
			}
		} else
		{
			log.error("Expected set type. Got: " + setVar.getType());
		}

		List<AIdentifierVarExpIR> traceVars = new LinkedList<>();

		for (AIdentifierPatternIR idToReg : idCollector.findOccurences())
		{
			if (Settings.dialect != Dialect.VDM_SL)
			{
				String idConstName = idConstNameMap.get(idToReg.getName());
				block.getStatements().add(transAssist.wrap(storeAssistant.consIdConstDecl(idConstName)));
				storeAssistant.appendStoreRegStms(block, idToReg.getName(), idConstName, false);
			}

			traceVars.add(this.transAssist.getInfo().getExpAssistant().consIdVar(idToReg.getName(), PatternTypeFinder.getType(typeFinder, idToReg)));
		}

		block.getStatements().add(nodeData.getStms());

		STypeIR instanceType = altTests.getType().clone();
		AIdentifierVarExpIR subject = nodeData.getNodeVar();

		for (int i = traceVars.size() - 1; i >= 0; i--)
		{
			block.getStatements().add(builder.consAddTraceVarCall(subject, traceVars.get(i)));
		}

		// E.g. alternatives_2.add(apply_1)
		ACallObjectExpStmIR addCall = transAssist.consInstanceCallStm(instanceType, id.getName(), tracePrefixes.addMethodName(), subject);
		block.getStatements().add(addCall);

		return packStm(block);
	}

	@Override
	public List<SStmIR> getPostOuterBlockStms(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns)
	{
		return null;
	}
}
