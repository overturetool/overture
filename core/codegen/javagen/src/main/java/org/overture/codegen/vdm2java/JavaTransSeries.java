package org.overture.codegen.vdm2java;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.expressions.AIntLiteralExpCG;
import org.overture.codegen.cgast.types.AExternalTypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.traces.TraceNames;
import org.overture.codegen.traces.TracesTrans;
import org.overture.codegen.trans.AssignStmTrans;
import org.overture.codegen.trans.CallObjStmTrans;
import org.overture.codegen.trans.DivideTrans;
import org.overture.codegen.trans.Exp2StmVarPrefixes;
import org.overture.codegen.trans.IPostCheckCreator;
import org.overture.codegen.trans.IsExpTrans;
import org.overture.codegen.trans.LetBeStTrans;
import org.overture.codegen.trans.PostCheckTrans;
import org.overture.codegen.trans.PreCheckTrans;
import org.overture.codegen.trans.PrePostTrans;
import org.overture.codegen.trans.SeqConvTrans;
import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.trans.Exp2StmTrans;
import org.overture.codegen.trans.WhileStmTrans;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.trans.conc.EvalPermPredTrans;
import org.overture.codegen.trans.conc.MainClassConcTrans;
import org.overture.codegen.trans.conc.MutexDeclTrans;
import org.overture.codegen.trans.conc.SentinelTrans;
import org.overture.codegen.trans.funcvalues.FuncValTrans;
import org.overture.codegen.trans.funcvalues.FunctionValueAssistant;
import org.overture.codegen.trans.iterator.ILanguageIterator;
import org.overture.codegen.trans.iterator.JavaLanguageIterator;
import org.overture.codegen.trans.letexps.FuncTrans;
import org.overture.codegen.trans.letexps.IfExpTrans;
import org.overture.codegen.trans.patterns.PatternMatchConfig;
import org.overture.codegen.trans.patterns.PatternTrans;
import org.overture.codegen.trans.quantifier.Exists1CounterData;
import org.overture.codegen.trans.uniontypes.UnionTypeTrans;

public class JavaTransSeries
{
	public static final String EVAL_METHOD_PREFIX = "eval";
	public static final String POST_CHECK_METHOD_NAME = "postCheck";

	// Name Prefixes (NP) of temporary variables created by the transformations
	public static final String INTERFACE_NP = "Func_";
	public static final String TEMPLATE_TYPE_NP = "T_";
	public static final String PARAM_NP = "param_";
	public static final String APPLY_EXP_NP = "apply_";
	public static final String OBJ_EXP_NP = "obj_";
	public static final String CALL_STM_OBJ_NP = "callStmObj_";
	public static final String CASES_EXP_NP = "casesExp_";
	public static final String WHILE_COND_NP = "whileCond_";
	public static final String IS_EXP_SUBJECT_NP = "isExpSubject_";
	public static final String FUNC_RES_NP = "funcResult_";

	private JavaCodeGen codeGen;

	public JavaTransSeries(JavaCodeGen codeGen)
	{
		this.codeGen = codeGen;
	}

	public List<DepthFirstAnalysisAdaptor> consAnalyses(
			FunctionValueAssistant funcValAssist)
	{
		// Data and functionality to support the transformations
		IRInfo info = codeGen.getIRGenerator().getIRInfo();
		TempVarPrefixes prefixes = codeGen.getTempVarPrefixes();
		TraceNames tracePrefixes = codeGen.getTracePrefixes();
		TransAssistantCG transAssist = codeGen.getTransAssistant();
		IPostCheckCreator postCheckCreator = new JavaPostCheckCreator(POST_CHECK_METHOD_NAME);

		Exp2StmVarPrefixes exp2stmPrefixes = new Exp2StmVarPrefixes();

		// Construct the transformations
		FuncTrans funcTr = new FuncTrans(transAssist);
		DivideTrans divideTr = new DivideTrans(info);
		CallObjStmTrans callObjTr = new CallObjStmTrans(info);
		AssignStmTrans assignTr = new AssignStmTrans(transAssist);
		PrePostTrans prePostTr = new PrePostTrans(info);
		IfExpTrans ifExpTr = new IfExpTrans(transAssist);
		FuncValTrans funcValTr = new FuncValTrans(transAssist, funcValAssist, INTERFACE_NP, TEMPLATE_TYPE_NP, EVAL_METHOD_PREFIX, PARAM_NP);
		ILanguageIterator langIte = new JavaLanguageIterator(transAssist, prefixes);
		LetBeStTrans letBeStTr = new LetBeStTrans(transAssist, langIte);
		WhileStmTrans whileTr = new WhileStmTrans(transAssist, WHILE_COND_NP);
		Exp2StmTrans exp2stmTr = new Exp2StmTrans(prefixes, transAssist, consExists1CounterData(), langIte, exp2stmPrefixes);
		PatternTrans patternTr = new PatternTrans(prefixes, transAssist, new PatternMatchConfig(), CASES_EXP_NP);
		PreCheckTrans preCheckTr = new PreCheckTrans(transAssist, new JavaValueSemanticsTag(false));
		PostCheckTrans postCheckTr = new PostCheckTrans(postCheckCreator, transAssist, FUNC_RES_NP, new JavaValueSemanticsTag(false));
		IsExpTrans isExpTr = new IsExpTrans(transAssist, IS_EXP_SUBJECT_NP);
		SeqConvTrans seqConvTr = new SeqConvTrans(transAssist);
		TracesTrans tracesTr = new TracesTrans(transAssist, prefixes, tracePrefixes, langIte, new JavaCallStmToStringBuilder());
		UnionTypeTrans unionTypeTr = new UnionTypeTrans(transAssist, APPLY_EXP_NP, OBJ_EXP_NP, CALL_STM_OBJ_NP);
		JavaToStringTrans javaToStringTr = new JavaToStringTrans(info);
		RecMethodsTrans recTr = new RecMethodsTrans(codeGen.getJavaFormat().getRecCreator());

		// Start concurrency transformations
		SentinelTrans sentinelTr = new SentinelTrans(info);
		MainClassConcTrans mainClassTr = new MainClassConcTrans(info);
		MutexDeclTrans mutexTr = new MutexDeclTrans(info);
		EvalPermPredTrans evalPermPredTr = new EvalPermPredTrans(transAssist);
		// End concurrency transformations

		// Set up order of transformations
		List<DepthFirstAnalysisAdaptor> series = new LinkedList<DepthFirstAnalysisAdaptor>();

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

		return series;
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
