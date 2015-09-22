package org.overture.codegen.trans.conc;

import java.util.LinkedList;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.expressions.AExternalExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.APlainCallStmCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AExternalTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.cgast.types.AVoidTypeCG;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IRInfo;


public class SentinelTrans extends DepthFirstAnalysisAdaptor
{
	private IRInfo info;
	
	// TODO: If this is suppose to be a general transformation then the integer type
	// can not be stored like this. Instead it could be passed as a parameter to the
	// transformation
	private String INTTYPE = "int";

	public SentinelTrans(IRInfo info)
	{
		this.info = info;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void caseADefaultClassDeclCG(ADefaultClassDeclCG node) throws AnalysisException
	{
		if(!info.getSettings().generateConc())
		{
			return;
		}

		ADefaultClassDeclCG innerClass = new ADefaultClassDeclCG();
		innerClass.setStatic(true);

		String classname = node.getName();
		LinkedList<AMethodDeclCG> allMethods;

		if (node.getSuperName() != null){
				allMethods = (LinkedList<AMethodDeclCG>) info.getDeclAssistant().getAllMethods(node, info.getClasses());
		}
		else
		{
			allMethods = (LinkedList<AMethodDeclCG>) node.getMethods().clone();
		}
		

		LinkedList<AMethodDeclCG> innerClassMethods = allMethods;

		String className = classname + "_sentinel";
		innerClass.setName(className);
		
		AClassTypeCG innerClassType = new AClassTypeCG();
		innerClassType.setName(classname);

		int n = 0;
		Boolean existing = false;
		for(AMethodDeclCG method : innerClassMethods){

			for(AFieldDeclCG field : innerClass.getFields())
			{

				if(field.getName().equals(method.getName()))
				{
					existing = true;
				}
			}
			
			if(existing)
			{
				existing = false;
			}
			else{
				//Set up of the int type of the fields.
				String intTypeName = INTTYPE;
				AExternalTypeCG intBasicType = new AExternalTypeCG();
				intBasicType.setName(intTypeName);

				AFieldDeclCG field = new AFieldDeclCG();
				field.setName(method.getName());
				field.setAccess(IRConstants.PUBLIC);
				field.setFinal(true);
				field.setType(intBasicType);
				field.setStatic(true);
				
				//setting up initial values
				AExternalExpCG intValue = new AExternalExpCG();
				intValue.setType(new AIntNumericBasicTypeCG());
				intValue.setTargetLangExp("" + n);

				field.setInitial(intValue);
				//increase the number that initialize the variables.
				n++;
				innerClass.getFields().add(field);
			}
		}

		//setting up initial values
		String intTypeName = INTTYPE;
		AExternalTypeCG intBasicType = new AExternalTypeCG();
		//intBasicType.setName(intTypeName);
		intBasicType.setName(intTypeName);

		AExternalExpCG intValue = new AExternalExpCG();
		intValue.setType(new AIntNumericBasicTypeCG());
		intValue.setTargetLangExp("" + n);


		innerClass.getFields().add(info.getDeclAssistant().constructField(IRConstants.PUBLIC, "function_sum", false, true, intBasicType, intValue));


		AMethodDeclCG method_pp = new AMethodDeclCG();
		AMethodTypeCG method_ppType = new AMethodTypeCG();
		method_ppType.setResult(innerClassType.clone());
		method_pp.setMethodType(method_ppType);
		
		//adding the first constructor to the innerclass
		method_pp.setIsConstructor(true);
		method_pp.setImplicit(false);
		method_pp.setAccess(IRConstants.PUBLIC);
		method_pp.setName(innerClass.getName());
		method_pp.setBody(new ABlockStmCG());
		innerClass.getMethods().add(method_pp);

		//adding the second constructor.

		AMethodDeclCG method_con = new AMethodDeclCG();

		//The parameter
		AExternalTypeCG evalPpType = new AExternalTypeCG();
		evalPpType.setName("EvaluatePP");

		method_con.setName(innerClass.getName());
		method_con.setIsConstructor(true);
		method_con.setAccess(IRConstants.PUBLIC);

		AFormalParamLocalParamCG formalParam = new AFormalParamLocalParamCG();
		formalParam.setType(evalPpType);

		AIdentifierPatternCG identifier = new AIdentifierPatternCG();
		identifier.setName("instance");

		formalParam.setPattern(identifier);
		method_con.getFormalParams().add(formalParam);
		
		AMethodTypeCG evalPPConType = new AMethodTypeCG();
		evalPPConType.setResult(innerClassType.clone());
		evalPPConType.getParams().add(evalPpType.clone());
		method_con.setMethodType(evalPPConType);

		//Creating the body of the constructor.

		//The parameters named ‘instance’ and function_sum passed to the init call statement:

		AIdentifierVarExpCG instanceParam = new AIdentifierVarExpCG();
		instanceParam.setIsLambda(false);
		instanceParam.setIsLocal(true);
		instanceParam.setName("instance");
		instanceParam.setType(evalPpType.clone());

		AIdentifierVarExpCG function_sum = new AIdentifierVarExpCG();
		function_sum.setIsLambda(false);
		function_sum.setIsLocal(false);
		function_sum.setName("function_sum");
		function_sum.setType(new AIntNumericBasicTypeCG());

		//the init method
		APlainCallStmCG initCall = new APlainCallStmCG();
		initCall.setName("init");
		initCall.setType(new AVoidTypeCG());

		//Adding argument #1
		initCall.getArgs().add(instanceParam);
		//Adding argument #2
		initCall.getArgs().add(function_sum);
		//Set the body
		method_con.setBody(initCall);
		innerClass.getMethods().add(method_con);
		//method_pp.setFormalParams();

		if (node.getSuperName() != null){
			if(!node.getSuperName().equals("VDMThread")){
				innerClass.setSuperName(node.getSuperName()+"_sentinel");
			}
			else
			{
				innerClass.setSuperName("Sentinel");
			}
		}
		else{

			innerClass.setSuperName("Sentinel");
		}
		innerClass.setAccess(IRConstants.PUBLIC);

		node.getInnerClasses().add(innerClass);

	}
}