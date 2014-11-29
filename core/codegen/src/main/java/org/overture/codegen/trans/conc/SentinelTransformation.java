package org.overture.codegen.trans.conc;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.expressions.AExternalExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.APlainCallStmCG;
import org.overture.codegen.cgast.types.AExternalTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.AVoidTypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.vdm2java.JavaFormat;


public class SentinelTransformation extends DepthFirstAnalysisAdaptor
{
	private IRInfo info;
	private List<AClassDeclCG> classes;

	public SentinelTransformation(IRInfo info, List<AClassDeclCG> classes)
	{
		this.info = info;
		this.classes = classes;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void caseAClassDeclCG(AClassDeclCG node) throws AnalysisException
	{
		if(!info.getSettings().generateConc())
		{
			return;
		}

		if (node.getThread() != null)
		{
			makeThread(node);
		}

		AClassDeclCG innerClass = new AClassDeclCG();

		String classname = node.getName();
		LinkedList<AMethodDeclCG> allMethods;
		if (node.getSuperName() != "VDMThread"){
			allMethods = (LinkedList<AMethodDeclCG>) info.getDeclAssistant().getAllMethods(node, classes);
		}
		else
		{

			allMethods = (LinkedList<AMethodDeclCG>) node.getMethods().clone();
		}

		LinkedList<AMethodDeclCG> innerClassMethods = allMethods;//(LinkedList<AMethodDeclCG>) node.getMethods().clone();

		innerClass.setName(classname+"_sentinel");

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
			//
			if(existing)
			{
				existing = false;
			}
			else{
				//Set up of the int type of the fields.
				String intTypeName = JavaFormat.JAVA_INT;
				AExternalTypeCG intBasicType = new AExternalTypeCG();
				//intBasicType.setName(intTypeName);
				intBasicType.setName(intTypeName);
				//
				AFieldDeclCG field = new AFieldDeclCG();

				field.setName(method.getName());
				field.setAccess(JavaFormat.JAVA_PUBLIC);
				field.setFinal(true);
				field.setType(intBasicType);
				field.setStatic(true);
				//field.setType();
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
		String intTypeName = JavaFormat.JAVA_INT;
		AExternalTypeCG intBasicType = new AExternalTypeCG();
		//intBasicType.setName(intTypeName);
		intBasicType.setName(intTypeName);

		AExternalExpCG intValue = new AExternalExpCG();
		intValue.setType(new AIntNumericBasicTypeCG());
		intValue.setTargetLangExp("" + n);//innerClassMethods.size());


		innerClass.getFields().add(info.getDeclAssistant().constructField("public", "function_sum", false, true, intBasicType, intValue));


		AMethodDeclCG method_pp = new AMethodDeclCG();
		//adding the first constructor to the innerclass
		method_pp.setIsConstructor(true);
		method_pp.setAccess("public");
		method_pp.setName(innerClass.getName());
		//Set up body for first constructor.
		method_pp.setBody(new ABlockStmCG());
		innerClass.getMethods().add(method_pp);

		//adding the second constructor.

		AMethodDeclCG method_con = new AMethodDeclCG();

		//The parameter
		AExternalTypeCG evalPpType = new AExternalTypeCG();
		evalPpType.setName("EvaluatePP");

		method_con.setName(innerClass.getName());
		method_con.setIsConstructor(true);
		method_con.setAccess(JavaFormat.JAVA_PUBLIC);

		AFormalParamLocalParamCG formalParam = new AFormalParamLocalParamCG();
		formalParam.setType(evalPpType);

		AIdentifierPatternCG identifier = new AIdentifierPatternCG();
		identifier.setName("instance");

		formalParam.setPattern(identifier);
		method_con.getFormalParams().add(formalParam);

		//Creating the body of the constructor.

		//The parameters named ‘instance’ and function_sum passed to the init call statement:

		AIdentifierVarExpCG instanceParam = new AIdentifierVarExpCG();
		instanceParam.setIsLambda(false);
		instanceParam.setOriginal("instance");
		instanceParam.setType(evalPpType.clone());

		AIdentifierVarExpCG function_sum = new AIdentifierVarExpCG();
		function_sum.setIsLambda(false);
		function_sum.setOriginal("function_sum");
		function_sum.setType(new AIntNumericBasicTypeCG());

		//the init method
		APlainCallStmCG initCall = new APlainCallStmCG();
		initCall.setName("init");
		initCall.setType(new AVoidTypeCG());

		//Adding argumet #1
		initCall.getArgs().add(instanceParam);
		//Adding arg #2
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
		innerClass.setAccess("public");

		node.getInnerClasses().add(innerClass);

	}

	private void makeThread(AClassDeclCG node)
	{
		AClassDeclCG threadClass = getThreadClass(node.getSuperName(), node);
		threadClass.setSuperName("VDMThread");
	}

	private AClassDeclCG getThreadClass(String superName, AClassDeclCG classCg)
	{
//		if(classCg.getSuperName() == "VDMThread")
//		{
//		    return classCg;
//		}
		if(superName == null)
		{
			return classCg;
		}
		else
		{
			AClassDeclCG superClass = null;

			for(AClassDeclCG c : classes)
			{
				if(c.getName().equals(superName))
				{
					superClass = c;
					break;
				}
			}

			return getThreadClass(superClass.getName(), superClass);
		}
	}
}