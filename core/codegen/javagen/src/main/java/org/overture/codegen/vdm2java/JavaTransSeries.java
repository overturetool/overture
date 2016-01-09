package org.overture.codegen.vdm2java;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.expressions.AIntLiteralExpCG;
import org.overture.codegen.cgast.types.AExternalTypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.traces.TraceNames;
import org.overture.codegen.traces.TracesTrans;
import org.overture.codegen.trans.AssignStmTrans;
import org.overture.codegen.trans.AtomicStmTrans;
import org.overture.codegen.trans.CallObjStmTrans;
import org.overture.codegen.trans.ConstructorTrans;
import org.overture.codegen.trans.DivideTrans;
import org.overture.codegen.trans.Exp2StmTrans;
import org.overture.codegen.trans.Exp2StmVarPrefixes;
import org.overture.codegen.trans.IPostCheckCreator;
import org.overture.codegen.trans.IsExpTransformation;
import org.overture.codegen.trans.PostCheckTransformation;
import org.overture.codegen.trans.PreCheckTransformation;
import org.overture.codegen.trans.PrePostTransformation;
import org.overture.codegen.trans.SeqConversionTransformation;
import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.trans.TransformationVisitor;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.trans.conc.InstanceVarPPEvalTransformation;
import org.overture.codegen.trans.conc.MainClassConcTransformation;
import org.overture.codegen.trans.conc.MutexDeclTransformation;
import org.overture.codegen.trans.conc.SentinelTransformation;
import org.overture.codegen.trans.funcvalues.FunctionValueAssistant;
import org.overture.codegen.trans.funcvalues.FunctionValueTransformation;
import org.overture.codegen.trans.iterator.ILanguageIterator;
import org.overture.codegen.trans.iterator.JavaLanguageIterator;
import org.overture.codegen.trans.letexps.FuncTransformation;
import org.overture.codegen.trans.letexps.IfExpTransformation;
import org.overture.codegen.trans.patterns.PatternMatchConfig;
import org.overture.codegen.trans.patterns.PatternTransformation;
import org.overture.codegen.trans.quantifier.Exists1CounterData;
import org.overture.codegen.trans.uniontypes.UnionTypeTransformation;

import static org.overture.codegen.ir.CodeGenBase.*;

public class JavaTransSeries
{
	private static final String OBJ_INIT_CALL_NAME_PREFIX = "cg_init_";
	
	private JavaCodeGen codeGen;

	public JavaTransSeries(JavaCodeGen codeGen)
	{
		this.codeGen = codeGen;
	}

	public List<DepthFirstAnalysisAdaptor> consAnalyses(
			List<AClassDeclCG> classes,
			FunctionValueAssistant functionValueAssistant)
	{
		// Data and functionality to support the transformations
		IRInfo info = codeGen.getIRGenerator().getIRInfo();
		JavaVarPrefixManager varMan = codeGen.getVarPrefixManager();
		IterationVarPrefixes iteVarPrefixes = varMan.getIteVarPrefixes();
		Exp2StmVarPrefixes exp2stmPrefixes = varMan.getExp2stmPrefixes();
		TraceNames tracePrefixes = varMan.getTracePrefixes();
		FuncValPrefixes funcValPrefixes = varMan.getFuncValPrefixes();
		PatternVarPrefixes patternPrefixes = varMan.getPatternPrefixes();
		UnionTypeVarPrefixes unionTypePrefixes = varMan.getUnionTypePrefixes();
		List<INode> cloneFreeNodes = codeGen.getJavaFormat().getValueSemantics().getCloneFreeNodes();
		
		TransAssistantCG transAssist = codeGen.getTransAssistant();
		IPostCheckCreator postCheckCreator = new JavaPostCheckCreator(varMan.postCheckMethodName());

		// Construct the transformations
		AtomicStmTrans atomicTr = new AtomicStmTrans(transAssist, varMan.atomicTmpVar());
		FuncTrans funcTr = new FuncTrans(transAssist);
		DivideTrans divideTr = new DivideTrans(info);
		CallObjStmTrans callObjTr = new CallObjStmTrans(info);
		AssignStmTrans assignTr = new AssignStmTrans(transAssist);
		PrePostTrans prePostTr = new PrePostTrans(info);
		IfExpTrans ifExpTr = new IfExpTrans(transAssist);
		FuncValTrans funcValTr = new FuncValTrans(transAssist, funcValAssist, funcValPrefixes);
		ILanguageIterator langIte = new JavaLanguageIterator(transAssist, iteVarPrefixes);
		LetBeStTrans letBeStTr = new LetBeStTrans(transAssist, langIte, iteVarPrefixes);
		WhileStmTrans whileTr = new WhileStmTrans(transAssist, varMan.whileCond());
		Exp2StmTrans exp2stmTr = new Exp2StmTrans(iteVarPrefixes, transAssist, consExists1CounterData(), langIte, exp2stmPrefixes);
		PatternTrans patternTr = new PatternTrans(iteVarPrefixes, transAssist, patternPrefixes, varMan.casesExp());
		PreCheckTrans preCheckTr = new PreCheckTrans(transAssist, new JavaValueSemanticsTag(false));
		PostCheckTrans postCheckTr = new PostCheckTrans(postCheckCreator, transAssist, varMan.funcRes(), new JavaValueSemanticsTag(false));
		IsExpTrans isExpTr = new IsExpTrans(transAssist, varMan.isExpSubject());
		SeqConvTrans seqConvTr = new SeqConvTrans(transAssist);
		TracesTrans tracesTr = new TracesTrans(transAssist, iteVarPrefixes, tracePrefixes, langIte, new JavaCallStmToStringBuilder(), cloneFreeNodes);
		UnionTypeTrans unionTypeTr = new UnionTypeTrans(transAssist, unionTypePrefixes, cloneFreeNodes);
		JavaToStringTrans javaToStringTr = new JavaToStringTrans(info);
		RecMethodsTrans recTr = new RecMethodsTrans(codeGen.getJavaFormat().getRecCreator());
		ConstructorTrans ctorTr = new ConstructorTrans(transAssist, OBJ_INIT_CALL_NAME_PREFIX);
		ImportsTrans impTr = new ImportsTrans(info);
		JUnit4Trans junitTr = new JUnit4Trans(transAssist, codeGen);

		// Start concurrency transformations
		SentinelTransformation concurrencytransform = new SentinelTransformation(irInfo, classes);
		MainClassConcTransformation mainclassTransform = new MainClassConcTransformation(irInfo, classes);
		MutexDeclTransformation mutexTransform = new MutexDeclTransformation(irInfo, classes);
		InstanceVarPPEvalTransformation instanceVarPPEval = new InstanceVarPPEvalTransformation(irInfo, transAssistant, classes);
		// End concurrency transformations

		// Set up order of transformations
		series.add(atomicTr);
		series.add(divideTr);
		series.add(assignTr);
		series.add(callObjTr);
		series.add(funcTr);
		series.add(prePostTr);
		series.add(ifExpTr);
		series.add(funcValTr);
		series.add(letBeStTr);
		series.add(whileTr);
		series.add(exp2stmTr);
		series.add(tracesTr);
		series.add(patternTr);
		series.add(preCheckTr);
		series.add(postCheckTr);
		series.add(isExpTr);
		series.add(unionTypeTr);
		series.add(javaToStringTr);
		series.add(sentinelTr);
		series.add(mutexTr);
		series.add(mainClassTr);
		series.add(seqConvTr);
		series.add(evalPermPredTr);
		series.add(recTr);
		series.add(ctorTr);
		series.add(impTr);
		series.add(junitTr);

		return transformations;
	}

	private Exists1CounterData consExists1CounterData()
	{
		AExternalTypeCG type = new AExternalTypeCG();
		type.setName("Long");

		IRInfo irInfo = codeGen.getIRGenerator().getIRInfo();
		AIntLiteralExpCG initExp = irInfo.getExpAssistant().consIntLiteral(0);

		return new Exists1CounterData(type, initExp);
	}
}
