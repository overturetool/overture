/*
 * #%~
 * VDM Code Generator
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.codegen.trans.funcvalues;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.PType;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AInterfaceDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.expressions.AAnonymousClassExpIR;
import org.overture.codegen.ir.expressions.ALambdaExpIR;
import org.overture.codegen.ir.expressions.AMethodInstantiationExpIR;
import org.overture.codegen.ir.expressions.SVarExpIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.AReturnStmIR;
import org.overture.codegen.ir.types.AInterfaceTypeIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overture.codegen.ir.types.ATemplateTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class FuncValTrans extends DepthFirstAnalysisAdaptor
{
	private TransAssistantIR transformationAssistant;
	private FuncValAssistant functionValueAssistant;
	private FuncValPrefixes funcValPrefixes;

	public FuncValTrans(TransAssistantIR transformationAssistant,
			FuncValAssistant functionValueAssistant,
			FuncValPrefixes funcValPrefixes)
	{
		this.transformationAssistant = transformationAssistant;
		this.functionValueAssistant = functionValueAssistant;
		this.funcValPrefixes = funcValPrefixes;
	}

	public FuncValAssistant getFunctionValueAssistant()
	{
		return functionValueAssistant;
	}

	@Override
	public void inAMethodTypeIR(AMethodTypeIR node) throws AnalysisException
	{
		if (node.parent() instanceof AMethodDeclIR)
		{
			return;
		}

		if (node.parent() instanceof SVarExpIR)
		{
			return;
		}

		if (node.parent() instanceof AMethodInstantiationExpIR)
		{
			return;
		}

		PType vdmType = node.getEquivalent();

		if (!(vdmType instanceof AFunctionType))
		{
			// vdmType == null:
			// Can be the case if the default constructor in the IR AST has been manually
			// constructed. In this case it is not needed to construct the
			// interface

			// vdmType can also be an operation type, but operations cannot be used as values
			return;
		}

		AInterfaceDeclIR info = functionValueAssistant.findInterface(node);

		if (info == null)
		{
			info = consInterface(node);
			functionValueAssistant.registerInterface(info);
		}
	}

	@Override
	public void inALambdaExpIR(ALambdaExpIR node) throws AnalysisException
	{
		AMethodTypeIR methodType = (AMethodTypeIR) node.getType().clone();
		AInterfaceDeclIR lambdaInterface = functionValueAssistant.findInterface(methodType);

		if (lambdaInterface == null)
		{
			@SuppressWarnings("unchecked")
			List<? extends AFormalParamLocalParamIR> formalParams = (List<? extends AFormalParamLocalParamIR>) node.getParams().clone();
			lambdaInterface = consInterface(methodType, formalParams);

			functionValueAssistant.registerInterface(lambdaInterface);
		}

		LinkedList<AFormalParamLocalParamIR> params = node.getParams();

		AInterfaceTypeIR classType = new AInterfaceTypeIR();
		classType.setName(lambdaInterface.getName());

		AMethodDeclIR lambdaDecl = lambdaInterface.getMethodSignatures().get(0).clone();

		for (int i = 0; i < params.size(); i++)
		{
			AFormalParamLocalParamIR paramLocalDeclIR = params.get(i);
			STypeIR paramType = paramLocalDeclIR.getType();
			SPatternIR pattern = paramLocalDeclIR.getPattern();

			classType.getTypes().add(paramType.clone());
			lambdaDecl.getFormalParams().get(i).setType(paramType.clone());
			lambdaDecl.getFormalParams().get(i).setPattern(pattern.clone());
		}

		classType.getTypes().add(methodType.getResult().clone());
		lambdaDecl.getMethodType().setResult(methodType.getResult().clone());

		AReturnStmIR lambdaReturn = new AReturnStmIR();
		lambdaReturn.setExp(node.getExp().clone());

		ABlockStmIR lambdaBody = new ABlockStmIR();
		lambdaBody.getStatements().add(lambdaReturn);

		lambdaDecl.setAbstract(false);
		lambdaDecl.setBody(lambdaBody);

		AAnonymousClassExpIR classExp = new AAnonymousClassExpIR();
		classExp.setType(classType);
		classExp.getMethods().add(lambdaDecl);

		transformationAssistant.replaceNodeWithRecursively(node, classExp, this);
	}

	private AInterfaceDeclIR consInterface(AMethodTypeIR methodType)
	{
		List<AFormalParamLocalParamIR> params = new LinkedList<AFormalParamLocalParamIR>();

		List<STypeIR> paramTypes = methodType.getParams();

		for (int i = 0; i < paramTypes.size(); i++)
		{
			STypeIR paramType = paramTypes.get(i);

			AFormalParamLocalParamIR param = new AFormalParamLocalParamIR();

			String nextParamName = funcValPrefixes.param() + (i + 1);
			AIdentifierPatternIR idPattern = new AIdentifierPatternIR();
			idPattern.setName(nextParamName);

			param.setType(paramType.clone());
			param.setPattern(idPattern);

			params.add(param);
		}

		return consInterface(methodType, params);
	}

	private AInterfaceDeclIR consInterface(AMethodTypeIR methodType,
			List<? extends AFormalParamLocalParamIR> params)
	{
		AInterfaceDeclIR methodTypeInterface = new AInterfaceDeclIR();

		methodTypeInterface.setPackage(null);
		methodTypeInterface.setName(transformationAssistant.getInfo().getTempVarNameGen().nextVarName(funcValPrefixes.funcInterface()));

		AMethodDeclIR evalMethod = new AMethodDeclIR();
		evalMethod.setImplicit(false);
		evalMethod.setAbstract(true);
		evalMethod.setAccess(IRConstants.PUBLIC);
		evalMethod.setBody(null);
		evalMethod.setIsConstructor(false);
		evalMethod.setMethodType(methodType.clone());
		evalMethod.setName(funcValPrefixes.evalMethod());
		evalMethod.setStatic(false);

		AMethodTypeIR evalMethodType = new AMethodTypeIR();

		for (int i = 0; i < params.size(); i++)
		{
			ATemplateTypeIR templateType = new ATemplateTypeIR();
			templateType.setName(funcValPrefixes.templateType() + (i + 1));

			AFormalParamLocalParamIR formalParam = new AFormalParamLocalParamIR();
			formalParam.setType(templateType);
			formalParam.setPattern(params.get(i).getPattern().clone());

			evalMethod.getFormalParams().add(formalParam);
			methodTypeInterface.getTemplateTypes().add(templateType.clone());
			evalMethodType.getParams().add(templateType.clone());
		}

		methodTypeInterface.getMethodSignatures().add(evalMethod);

		ATemplateTypeIR templateTypeResult = new ATemplateTypeIR();
		templateTypeResult.setName(funcValPrefixes.templateType()
				+ (methodType.getParams().size() + 1));
		methodTypeInterface.getTemplateTypes().add(templateTypeResult);
		evalMethodType.setResult(templateTypeResult.clone());

		evalMethod.setMethodType(evalMethodType);

		return methodTypeInterface;
	}
}
