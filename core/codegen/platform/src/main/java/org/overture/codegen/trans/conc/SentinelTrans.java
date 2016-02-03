package org.overture.codegen.trans.conc;

import java.util.List;

import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.ADefaultClassDeclCG;
import org.overture.codegen.ir.declarations.AFieldDeclCG;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.ir.declarations.AMethodDeclCG;
import org.overture.codegen.ir.expressions.AExternalExpCG;
import org.overture.codegen.ir.expressions.AIdentifierVarExpCG;
import org.overture.codegen.ir.name.ATokenNameCG;
import org.overture.codegen.ir.patterns.AIdentifierPatternCG;
import org.overture.codegen.ir.statements.ABlockStmCG;
import org.overture.codegen.ir.statements.APlainCallStmCG;
import org.overture.codegen.ir.types.AClassTypeCG;
import org.overture.codegen.ir.types.AExternalTypeCG;
import org.overture.codegen.ir.types.AIntNumericBasicTypeCG;
import org.overture.codegen.ir.types.AMethodTypeCG;
import org.overture.codegen.ir.types.AVoidTypeCG;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IRInfo;

public class SentinelTrans extends DepthFirstAnalysisAdaptor
{
	private IRInfo info;

	private ConcPrefixes concPrefixes;
	
	public SentinelTrans(IRInfo info, ConcPrefixes concPrefixes)
	{
		this.info = info;
		this.concPrefixes = concPrefixes;
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

		String className = classname + concPrefixes.sentinelClassPostFix();
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
				String intTypeName = concPrefixes.nativeIntTypeName();
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
		AExternalTypeCG intBasicType = new AExternalTypeCG();
		// intBasicType.setName(intTypeName);
		intBasicType.setName(concPrefixes.nativeIntTypeName());

		AExternalExpCG intValue = new AExternalExpCG();
		intValue.setType(new AIntNumericBasicTypeCG());
		intValue.setTargetLangExp("" + n);

		innerClass.getFields().add(info.getDeclAssistant().constructField(IRConstants.PUBLIC, concPrefixes.funcSumConstFieldName(), false, true, intBasicType, intValue));

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
		evalPpType.setName(concPrefixes.evalPpTypeName());

		method_con.setName(innerClass.getName());
		method_con.setIsConstructor(true);
		method_con.setAccess(IRConstants.PUBLIC);

		AFormalParamLocalParamCG formalParam = new AFormalParamLocalParamCG();
		formalParam.setType(evalPpType);

		AIdentifierPatternCG identifier = new AIdentifierPatternCG();
		identifier.setName(concPrefixes.instanceParamName());

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
		instanceParam.setName(concPrefixes.instanceParamName());
		instanceParam.setType(evalPpType.clone());

		AIdentifierVarExpCG function_sum = new AIdentifierVarExpCG();
		function_sum.setIsLambda(false);
		function_sum.setIsLocal(false);
		function_sum.setName(concPrefixes.funcSumConstFieldName());
		function_sum.setType(new AIntNumericBasicTypeCG());

		// the init method
		APlainCallStmCG initCall = new APlainCallStmCG();
		initCall.setName(concPrefixes.initMethodName());
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
			if (!node.getSuperNames().get(0).equals(concPrefixes.vdmThreadClassName()))
			{
				superName.setName(node.getSuperNames().get(0) + concPrefixes.sentinelClassPostFix());
			} else
			{
				superName.setName(concPrefixes.sentinelClassName());
			}

			innerClass.getSuperNames().add(superName);
		} else
		{

			superName.setName(concPrefixes.sentinelClassName());
			innerClass.getSuperNames().add(superName);
		}

		innerClass.setAccess(IRConstants.PUBLIC);
		node.getInnerClasses().add(innerClass);
	}
}