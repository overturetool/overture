package org.overture.codegen.ir;

import org.overture.codegen.traces.TraceNames;
import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantCG;


public class CodeGenBase
{
	public static final String INTERFACE_NAME_PREFIX = "Func_";
	public static final String TEMPLATE_TYPE_PREFIX = "T_";
	public static final String EVAL_METHOD_PREFIX = "eval";
	public static final String PARAM_NAME_PREFIX = "param_";
	public static final String APPLY_EXP_NAME_PREFIX = "apply_";
	public static final String OBJ_EXP_NAME_PREFIX = "obj_";
	public static final String TERNARY_IF_EXP_NAME_PREFIX = "ternaryIfExp_";
	public static final String CALL_STM_OBJ_NAME_PREFIX = "callStmObj_";
	public static final String CASES_EXP_NAME_PREFIX = "casesExp_";
	public static final String CASES_EXP_RESULT_NAME_PREFIX = "casesExpResult_";
	public static final String AND_EXP_NAME_PREFIX = "andResult_";
	public static final String OR_EXP_NAME_PREFIX = "orResult_";
	public static final String WHILE_COND_NAME_PREFIX = "whileCond";
	public static final String IS_EXP_SUBJECT_NAME_PREFIX = "isExpSubject_";
	public static final String REC_MODIFIER_NAME_PREFIX = "recModifierExp_";
	public static final String MISSING_OP_MEMBER = "Missing operation member: ";
	public static final String MISSING_MEMBER = "Missing member: ";
	public static final String INVALID_NAME_PREFIX = "cg_";
	public static final String OBJ_INIT_CALL_NAME_PREFIX = "cg_init_";
	public static final String FUNC_RESULT_NAME_PREFIX = "funcResult_";
	public static final String POST_CHECK_METHOD_NAME = "postCheck";
	public static final String QUOTES = "quotes";

	protected IRGenerator generator;
	protected TransAssistantCG transAssistant;
	protected TempVarPrefixes varPrefixes;
	protected TraceNames tracePrefixes;
	
	protected CodeGenBase()
	{
		super();
		this.generator = new IRGenerator(OBJ_INIT_CALL_NAME_PREFIX);
		this.varPrefixes = new TempVarPrefixes();
		this.tracePrefixes = new TraceNames();
	}
	
	public void setIRGenerator(IRGenerator generator)
	{
		this.generator = generator;
	}

	public IRGenerator getIRGenerator()
	{
		return generator;
	}

	public void setSettings(IRSettings settings)
	{
		generator.getIRInfo().setSettings(settings);
	}
	
	public IRSettings getSettings()
	{
		return generator.getIRInfo().getSettings();
	}
	
	public IRInfo getInfo()
	{
		return generator.getIRInfo();
	}
	
	public void setTransAssistant(TransAssistantCG transAssistant)
	{
		this.transAssistant = transAssistant;
	}
	
	public TransAssistantCG getTransformationAssistant()
	{
		return transAssistant;
	}
	
	public void setTempVarPrefixes(TempVarPrefixes varPrefixes)
	{
		this.varPrefixes = varPrefixes;
	}
	
	public TempVarPrefixes getTempVarPrefixes()
	{
		return varPrefixes;
	}

	public TraceNames getTracePrefixes()
	{
		return tracePrefixes;
	}

	public void setTracePrefixes(TraceNames tracePrefixes)
	{
		this.tracePrefixes = tracePrefixes;
	}
}