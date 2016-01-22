package org.overture.codegen.trans.conc;

import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.expressions.AExternalExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.name.ATokenNameCG;
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
	private static final String SENTINEL_CLASS_NAME = "Sentinel";
	private static final String VDM_THREAD = "VDMThread";
	private static final String INIT_METHOD_NAME = "init";
	private static final String INSTANCE_PARAM_NAME = "instance";
	private static final String EVALUATE_PP_TYPE = "EvaluatePP";
	private static final String SENTINEL_CLASS_POSTFIX = "_sentinel";
	private static final String FUNC_SUM_CONSTANT_FIELD_NAME = "function_sum";

	private IRInfo info;

	// TODO: If this is suppose to be a general transformation then the integer type
	// can not be stored like this. Instead it could be passed as a parameter to the
	// transformation
	private String INTTYPE = "int";

	public SentinelTrans(IRInfo info)
	{
		this.info = info;
	}

	@Override
	public void caseADefaultClassDeclCG(ADefaultClassDeclCG node) throws AnalysisException
	{
		if (!info.getSettings().generateConc())
		{
			return;
		}

		if (node.getSuperNames().size() > 1)
		{
			info.addTransformationWarning(node, MainClassConcTrans.MULTIPLE_INHERITANCE_WARNING);
			return;
		}

		ADefaultClassDeclCG innerClass = new ADefaultClassDeclCG();
		innerClass.setStatic(true);

		String classname = node.getName();
		List<AMethodDeclCG> innerClassMethods;

		if (!node.getSuperNames().isEmpty())
		{
			innerClassMethods = info.getDeclAssistant().getAllMethods(node, info.getClasses());
		} else
		{
			innerClassMethods = node.getMethods();
		}

		String className = classname + SENTINEL_CLASS_POSTFIX;
		innerClass.setName(className);

		AClassTypeCG innerClassType = new AClassTypeCG();
		innerClassType.setName(classname);

		int n = 0;
		Boolean existing = false;
		for (AMethodDeclCG method : innerClassMethods)
		{

			for (AFieldDeclCG field : innerClass.getFields())
			{

				if (field.getName().equals(method.getName()))
				{
					existing = true;
				}
			}

			if (existing)
			{
				existing = false;
			} else
			{
				// Set up of the int type of the fields.
				String intTypeName = INTTYPE;
				AExternalTypeCG intBasicType = new AExternalTypeCG();
				intBasicType.setName(intTypeName);

				AFieldDeclCG field = new AFieldDeclCG();
				field.setName(method.getName());
				field.setAccess(IRConstants.PUBLIC);
				field.setFinal(true);
				field.setType(intBasicType);
				field.setStatic(true);

				// setting up initial values
				AExternalExpCG intValue = new AExternalExpCG();
				intValue.setType(new AIntNumericBasicTypeCG());
				intValue.setTargetLangExp("" + n);

				field.setInitial(intValue);
				// increase the number that initialize the variables.
				n++;
				innerClass.getFields().add(field);
			}
		}

		// setting up initial values
		String intTypeName = INTTYPE;
		AExternalTypeCG intBasicType = new AExternalTypeCG();
		// intBasicType.setName(intTypeName);
		intBasicType.setName(intTypeName);

		AExternalExpCG intValue = new AExternalExpCG();
		intValue.setType(new AIntNumericBasicTypeCG());
		intValue.setTargetLangExp("" + n);

		innerClass.getFields().add(info.getDeclAssistant().constructField(IRConstants.PUBLIC, FUNC_SUM_CONSTANT_FIELD_NAME, false, true, intBasicType, intValue));

		AMethodDeclCG method_pp = new AMethodDeclCG();
		AMethodTypeCG method_ppType = new AMethodTypeCG();
		method_ppType.setResult(innerClassType.clone());
		method_pp.setMethodType(method_ppType);

		// adding the first constructor to the innerclass
		method_pp.setIsConstructor(true);
		method_pp.setImplicit(false);
		method_pp.setAccess(IRConstants.PUBLIC);
		method_pp.setName(innerClass.getName());
		method_pp.setBody(new ABlockStmCG());
		innerClass.getMethods().add(method_pp);

		// adding the second constructor.

		AMethodDeclCG method_con = new AMethodDeclCG();

		// The parameter
		AExternalTypeCG evalPpType = new AExternalTypeCG();
		evalPpType.setName(EVALUATE_PP_TYPE);

		method_con.setName(innerClass.getName());
		method_con.setIsConstructor(true);
		method_con.setAccess(IRConstants.PUBLIC);

		AFormalParamLocalParamCG formalParam = new AFormalParamLocalParamCG();
		formalParam.setType(evalPpType);

		AIdentifierPatternCG identifier = new AIdentifierPatternCG();
		identifier.setName(INSTANCE_PARAM_NAME);

		formalParam.setPattern(identifier);
		method_con.getFormalParams().add(formalParam);

		AMethodTypeCG evalPPConType = new AMethodTypeCG();
		evalPPConType.setResult(innerClassType.clone());
		evalPPConType.getParams().add(evalPpType.clone());
		method_con.setMethodType(evalPPConType);

		// Creating the body of the constructor.

		// The parameters named ‘instance’ and function_sum passed to the init call statement:

		AIdentifierVarExpCG instanceParam = new AIdentifierVarExpCG();
		instanceParam.setIsLambda(false);
		instanceParam.setIsLocal(true);
		instanceParam.setName(INSTANCE_PARAM_NAME);
		instanceParam.setType(evalPpType.clone());

		AIdentifierVarExpCG function_sum = new AIdentifierVarExpCG();
		function_sum.setIsLambda(false);
		function_sum.setIsLocal(false);
		function_sum.setName(FUNC_SUM_CONSTANT_FIELD_NAME);
		function_sum.setType(new AIntNumericBasicTypeCG());

		// the init method
		APlainCallStmCG initCall = new APlainCallStmCG();
		initCall.setName(INIT_METHOD_NAME);
		initCall.setType(new AVoidTypeCG());

		// Adding argument #1
		initCall.getArgs().add(instanceParam);
		// Adding argument #2
		initCall.getArgs().add(function_sum);
		// Set the body
		method_con.setBody(initCall);
		innerClass.getMethods().add(method_con);
		// method_pp.setFormalParams();

		ATokenNameCG superName = new ATokenNameCG();
		if (!node.getSuperNames().isEmpty())
		{
			if (!node.getSuperNames().get(0).equals(VDM_THREAD))
			{
				superName.setName(node.getSuperNames().get(0) + SENTINEL_CLASS_POSTFIX);
			} else
			{
				superName.setName(SENTINEL_CLASS_NAME);
			}

			innerClass.getSuperNames().add(superName);
		} else
		{

			superName.setName(SENTINEL_CLASS_NAME);
			innerClass.getSuperNames().add(superName);
		}

		innerClass.setAccess(IRConstants.PUBLIC);
		node.getInnerClasses().add(innerClass);
	}
}