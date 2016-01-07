package org.overture.codegen.traces;

public class TraceNames
{
	// Related to TraceNode
	
	public String traceNodeNodeClassName()
	{
		return "TraceNode";
	}
	
	public String executeTestsMethodName()
	{
		return "executeTests";
	}
	
	// Related to the AlternativeTraceNode
	
	public String altTraceNodeNodeClassName()
	{
		return "AlternativeTraceNode";
	}

	public String altTraceNodeNamePrefix()
	{
		return "alternatives_";
	}
	
	// Related to the StatementTraceNode

	public String stmTraceNodeClassName()
	{
		return "StatementTraceNode";
	}

	public String stmTraceNodeNamePrefix()
	{
		return "apply_";
	}
	
	// Related to the CallStatement
	
	public String callStmClassTypeName()
	{
		return "CallStatementPp";
	}
	
	public String callStmBaseClassTypeName()
	{
		return "CallStatement";
	}
	
	public String callStmNamePrefix()
	{
		return "callStm_";
	}
	
	public String callStmIsTypeCorrectNamePrefix()
	{
		return "isTypeCorrect";
	}
	
	public String callStmMeetsPreCondNamePrefix()
	{
		return "meetsPreCond";
	}
	
	public String callStmExecMethodNamePrefix()
	{
		return "execute";
	}
	
	public String callStmMethodParamName()
	{
		return "instance";
	}
	
	public String callStmArgNamePrefix()
	{
		return "arg_";
	}
	
	public String callStmResultNamePrefix()
	{
		return "result_";
	}

	// Related to the SequenceTraceNode 

	public String seqTraceNodeNamePrefix()
	{
		return "sequence_";
	}

	public String seqClassTypeName()
	{
		return "SequenceTraceNode";
	}
	
	// Related to the ConcurrentTraceNode

	public String concTraceNodeNamePrefix()
	{
		return "concurrent_";
	}

	public String concTraceNodeNodeClassName()
	{
		return "ConcurrentTraceNode";
	}

	// Related to the RepeatTraceNode	
	
	public String repeatTraceNodeNamePrefix()
	{
		return "repeat_";
	}

	public String repeatTraceNodeNodeClassName()
	{
		return "RepeatTraceNode";
	}
	
	public String runtimePackage()
	{
		return "org.overture.codegen.runtime";
	}

	//Name of the method that when invoked executes the trace
	
	public String runTraceMethodName()
	{
		return "Run";
	}
	
	// Name of the method that is used to add child trace nodes to
	// a parent trace node
	
	public String addMethodName()
	{
		return "add";
	}
	
	// Storage related
	
	public String storeClassName()
	{
		return "Store";
	}
	
	public String storeVarName()
	{
		return "store";
	}
	
	public String storeStaticRegistrationMethodName()
	{
		return "staticReg";
	}
	
	public String storeRegistrationMethodName()
	{
		return "register";
	}
	
	public String storeGetValueMethodName()
	{
		return "getValue";
	}
	
	// ID Generator related
	
	public String idGeneratorClassName()
	{
		return "IdGenerator";
	}
	
	public String idGeneratorVarName()
	{
		return "gen";
	}
	
	public String idConstNamePrefix()
	{
		return "ID_";
	}
	
	public String idGeneratorIncrementMethodName()
	{
		return "inc";
	}
	
	// Utility stuff
	
	public String voidValueEnclosingClassName()
	{
		return "Utils";
	}
	
	public String voidValueFieldName()
	{
		return "VOID_VALUE";
	}
	
	public String testAccumulatorClassName()
	{
		return "TestAccumulator";
	}
	
	public String traceMethodParamName()
	{
		return "testAccumulator";
	}
	
	public String traceUtilClassName()
	{
		return "TraceUtil";
	}
	
	public String readStateMethodName()
	{
		return "readState";
	}
	
	public String traceVarClassName()
	{
		return "TraceVariable";
	}
	
	public String addVarFirstMethodName()
	{
		return "addVarFirst";
	}
}
