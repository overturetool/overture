package org.overture.codegen.trans.conc;

import java.util.List;

import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.expressions.AExternalExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.name.ATokenNameIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.APlainCallStmIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.ir.types.AIntNumericBasicTypeIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overture.codegen.ir.types.AVoidTypeIR;

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
	public void caseADefaultClassDeclIR(ADefaultClassDeclIR node)
			throws AnalysisException
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

		ADefaultClassDeclIR innerClass = new ADefaultClassDeclIR();
		innerClass.setStatic(true);

		String classname = node.getName();
		List<AMethodDeclIR> innerClassMethods;

		if (!node.getSuperNames().isEmpty())
		{
			innerClassMethods = info.getDeclAssistant().getAllMethods(node, info.getClasses());
		} else
		{
			innerClassMethods = node.getMethods();
		}

		String className = classname + concPrefixes.sentinelClassPostFix();
		innerClass.setName(className);

		AClassTypeIR innerClassType = new AClassTypeIR();
		innerClassType.setName(classname);

		int n = 0;
		Boolean existing = false;
		for (AMethodDeclIR method : innerClassMethods)
		{

			for (AFieldDeclIR field : innerClass.getFields())
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
				AExternalTypeIR intBasicType = new AExternalTypeIR();
				intBasicType.setName(intTypeName);

				AFieldDeclIR field = new AFieldDeclIR();
				field.setName(method.getName());
				field.setAccess(IRConstants.PUBLIC);
				field.setFinal(true);
				field.setType(intBasicType);
				field.setStatic(true);

				// setting up initial values
				AExternalExpIR intValue = new AExternalExpIR();
				intValue.setType(new AIntNumericBasicTypeIR());
				intValue.setTargetLangExp("" + n);

				field.setInitial(intValue);
				// increase the number that initialize the variables.
				n++;
				innerClass.getFields().add(field);
			}
		}

		// setting up initial values
		AExternalTypeIR intBasicType = new AExternalTypeIR();
		// intBasicType.setName(intTypeName);
		intBasicType.setName(concPrefixes.nativeIntTypeName());

		AExternalExpIR intValue = new AExternalExpIR();
		intValue.setType(new AIntNumericBasicTypeIR());
		intValue.setTargetLangExp("" + n);

		innerClass.getFields().add(info.getDeclAssistant().constructField(IRConstants.PUBLIC, concPrefixes.funcSumConstFieldName(), false, true, intBasicType, intValue));

		AMethodDeclIR method_pp = new AMethodDeclIR();
		AMethodTypeIR method_ppType = new AMethodTypeIR();
		method_ppType.setResult(innerClassType.clone());
		method_pp.setMethodType(method_ppType);

		// adding the first constructor to the innerclass
		method_pp.setIsConstructor(true);
		method_pp.setImplicit(false);
		method_pp.setAccess(IRConstants.PUBLIC);
		method_pp.setName(innerClass.getName());
		method_pp.setBody(new ABlockStmIR());
		innerClass.getMethods().add(method_pp);

		// adding the second constructor.

		AMethodDeclIR method_con = new AMethodDeclIR();

		// The parameter
		AExternalTypeIR evalPpType = new AExternalTypeIR();
		evalPpType.setName(concPrefixes.evalPpTypeName());

		method_con.setName(innerClass.getName());
		method_con.setIsConstructor(true);
		method_con.setAccess(IRConstants.PUBLIC);

		AFormalParamLocalParamIR formalParam = new AFormalParamLocalParamIR();
		formalParam.setType(evalPpType);

		AIdentifierPatternIR identifier = new AIdentifierPatternIR();
		identifier.setName(concPrefixes.instanceParamName());

		formalParam.setPattern(identifier);
		method_con.getFormalParams().add(formalParam);

		AMethodTypeIR evalPPConType = new AMethodTypeIR();
		evalPPConType.setResult(innerClassType.clone());
		evalPPConType.getParams().add(evalPpType.clone());
		method_con.setMethodType(evalPPConType);

		// Creating the body of the constructor.

		// The parameters named ‘instance’ and function_sum passed to the init call statement:

		AIdentifierVarExpIR instanceParam = new AIdentifierVarExpIR();
		instanceParam.setIsLambda(false);
		instanceParam.setIsLocal(true);
		instanceParam.setName(concPrefixes.instanceParamName());
		instanceParam.setType(evalPpType.clone());

		AIdentifierVarExpIR function_sum = new AIdentifierVarExpIR();
		function_sum.setIsLambda(false);
		function_sum.setIsLocal(false);
		function_sum.setName(concPrefixes.funcSumConstFieldName());
		function_sum.setType(new AIntNumericBasicTypeIR());

		// the init method
		APlainCallStmIR initCall = new APlainCallStmIR();
		initCall.setName(concPrefixes.initMethodName());
		initCall.setType(new AVoidTypeIR());

		// Adding argument #1
		initCall.getArgs().add(instanceParam);
		// Adding argument #2
		initCall.getArgs().add(function_sum);
		// Set the body
		method_con.setBody(initCall);
		innerClass.getMethods().add(method_con);
		// method_pp.setFormalParams();

		ATokenNameIR superName = new ATokenNameIR();
		if (!node.getSuperNames().isEmpty())
		{
			if (!node.getSuperNames().get(0).equals(concPrefixes.vdmThreadClassName()))
			{
				superName.setName(node.getSuperNames().get(0)
						+ concPrefixes.sentinelClassPostFix());
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