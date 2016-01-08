package org.overture.codegen.cgen;

import static org.overture.codegen.ir.CodeGenBase.EVAL_METHOD_PREFIX;
import static org.overture.codegen.ir.CodeGenBase.INTERFACE_NAME_PREFIX;
import static org.overture.codegen.ir.CodeGenBase.PARAM_NAME_PREFIX;
import static org.overture.codegen.ir.CodeGenBase.TEMPLATE_TYPE_PREFIX;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgen.transformations.MethodParamTransformation;
import org.overture.codegen.cgen.transformations.VdmBasicTypesCppTrans;
import org.overture.codegen.cgen.transformations.VdmSeqCppTrans;
import org.overture.codegen.cgen.transformations.VdmSetCppTrans;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.traces.TraceNames;
import org.overture.codegen.trans.AssignStmTransformation;
import org.overture.codegen.trans.CallObjStmTransformation;
import org.overture.codegen.trans.SeqConversionTransformation;
import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.trans.funcvalues.FunctionValueAssistant;
import org.overture.codegen.trans.funcvalues.FunctionValueTransformation;
import org.overture.codegen.trans.letexps.FuncTransformation;

public class XTransSeries {

	private XCodeGen codeGen;

	public XTransSeries(XCodeGen codeGen)
	{
		this.codeGen = codeGen;
	}
	
	
	public List<DepthFirstAnalysisAdaptor> consAnalyses(
			List<AClassDeclCG> classes,
			FunctionValueAssistant functionValueAssistant)
	{
		// Data and functionality to support the transformations
		IRInfo irInfo = codeGen.getIRGenerator().getIRInfo();
		TempVarPrefixes varPrefixes = codeGen.getTempVarPrefixes();
		ITempVarGen nameGen = irInfo.getTempVarNameGen();
		TraceNames traceNamePrefixes = codeGen.getTracePrefixes();
		TransAssistantCG transAssistant = codeGen.getTransAssistant();
//		IPostCheckCreator postCheckCreator = new JavaPostCheckCreator(POST_CHECK_METHOD_NAME);
		
		VdmBasicTypesCppTrans typeTrans = new VdmBasicTypesCppTrans(transAssistant);
		VdmSetCppTrans setTrans = new VdmSetCppTrans(transAssistant); 
		VdmSeqCppTrans seqTrans = new VdmSeqCppTrans(transAssistant); 
		
		
		// Construct the transformations
		FuncTransformation funcTransformation = new FuncTransformation(transAssistant);
//		DivideTransformation divideTrans = new DivideTransformation(irInfo);
		CallObjStmTransformation callObjTransformation = new CallObjStmTransformation(irInfo, classes);
		AssignStmTransformation assignTransformation = new AssignStmTransformation(irInfo, classes, transAssistant);
//		PrePostTransformation prePostTransformation = new PrePostTransformation(irInfo);
//		IfExpTransformation ifExpTransformation = new IfExpTransformation(transAssistant);
		FunctionValueTransformation funcValueTransformation = new FunctionValueTransformation(irInfo, transAssistant, functionValueAssistant, INTERFACE_NAME_PREFIX, TEMPLATE_TYPE_PREFIX, EVAL_METHOD_PREFIX, PARAM_NAME_PREFIX);
//		ILanguageIterator langIterator = new JavaLanguageIterator(transAssistant, nameGen, varPrefixes);
//		TransformationVisitor transVisitor = new TransformationVisitor(irInfo, classes, varPrefixes, transAssistant, consExists1CounterData(), langIterator, TERNARY_IF_EXP_NAME_PREFIX, CASES_EXP_RESULT_NAME_PREFIX, AND_EXP_NAME_PREFIX, OR_EXP_NAME_PREFIX, WHILE_COND_NAME_PREFIX, REC_MODIFIER_NAME_PREFIX);
//		PatternTransformation patternTransformation = new PatternTransformation(classes, varPrefixes, irInfo, transAssistant, new PatternMatchConfig(), CASES_EXP_NAME_PREFIX);
//		PreCheckTransformation preCheckTransformation = new PreCheckTransformation(irInfo, transAssistant, new JavaValueSemanticsTag(false));
//		PostCheckTransformation postCheckTransformation = new PostCheckTransformation(postCheckCreator, irInfo, transAssistant, FUNC_RESULT_NAME_PREFIX, new JavaValueSemanticsTag(false));
//		IsExpTransformation isExpTransformation = new IsExpTransformation(irInfo, transAssistant, IS_EXP_SUBJECT_NAME_PREFIX);
		SeqConversionTransformation seqConversionTransformation = new SeqConversionTransformation(transAssistant);
//		TracesTransformation tracesTransformation = new TracesTransformation(irInfo, classes, transAssistant, varPrefixes, traceNamePrefixes, langIterator, new JavaCallStmToStringBuilder());
//		UnionTypeTransformation unionTypeTransformation = new UnionTypeTransformation(transAssistant, irInfo, classes, APPLY_EXP_NAME_PREFIX, OBJ_EXP_NAME_PREFIX, CALL_STM_OBJ_NAME_PREFIX, MISSING_OP_MEMBER, MISSING_MEMBER);
//		JavaClassToStringTrans javaToStringTransformation = new JavaClassToStringTrans(irInfo);
//		RecordMetodsTransformation recTransformation = new RecordMetodsTransformation(codeGen.getJavaFormat().getRecCreator());

		// Start concurrency transformations
//		SentinelTransformation concurrencytransform = new SentinelTransformation(irInfo, classes);
//		MainClassConcTransformation mainclassTransform = new MainClassConcTransformation(irInfo, classes);
//		MutexDeclTransformation mutexTransform = new MutexDeclTransformation(irInfo, classes);
//		InstanceVarPPEvalTransformation instanceVarPPEval = new InstanceVarPPEvalTransformation(irInfo, transAssistant, classes);
		// End concurrency transformations

		
		/* C transformations */
		
		MethodParamTransformation methodTransformation = new MethodParamTransformation(transAssistant);
		
		
		// Set up order of transformations
		List<DepthFirstAnalysisAdaptor> transformations = new LinkedList<DepthFirstAnalysisAdaptor>();

//		transformations.add(typeTrans);
		transformations.add(setTrans);
//		transformations.add(seqTrans);
		//transformations.add(assignTransformation);
		
//		transformations.add(divideTrans);

//		transformations.add(callObjTransformation);
		transformations.add(funcTransformation);
//		transformations.add(prePostTransformation);
//		transformations.add(ifExpTransformation);
		transformations.add(funcValueTransformation);
//		transformations.add(transVisitor);
//		transformations.add(tracesTransformation);
//		transformations.add(patternTransformation);
//		transformations.add(preCheckTransformation);
//		transformations.add(postCheckTransformation);
//		transformations.add(isExpTransformation);
//		transformations.add(unionTypeTransformation);
//		transformations.add(javaToStringTransformation);
//		transformations.add(concurrencytransform);
//		transformations.add(mutexTransform);
//		transformations.add(mainclassTransform);
		transformations.add(seqConversionTransformation);
//		transformations.add(instanceVarPPEval);
//		transformations.add(recTransformation);

		
		/* C transformations */
		transformations.add(methodTransformation);
		
		return transformations;
	}
	
}
