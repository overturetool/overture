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
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AInterfaceDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.expressions.AAnonymousClassExpCG;
import org.overture.codegen.cgast.expressions.ALambdaExpCG;
import org.overture.codegen.cgast.expressions.AMethodInstantiationExpCG;
import org.overture.codegen.cgast.expressions.SVarExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.cgast.types.AInterfaceTypeCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.cgast.types.ATemplateTypeCG;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class FuncValTrans extends DepthFirstAnalysisAdaptor
{
	private TransAssistantCG transformationAssistant;
	private FuncValAssistant functionValueAssistant;
	private FuncValPrefixes funcValPrefixes;

	public FuncValTrans(TransAssistantCG transformationAssistant,
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
	public void inAMethodTypeCG(AMethodTypeCG node) throws AnalysisException
	{
		if (node.parent() instanceof AMethodDeclCG)
		{
			return;
		}

		if (node.parent() instanceof SVarExpCG)
		{
			return;
		}

		if (node.parent() instanceof AMethodInstantiationExpCG)
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

		AInterfaceDeclCG info = functionValueAssistant.findInterface(node);

		if (info == null)
		{
			info = consInterface(node);
			functionValueAssistant.registerInterface(info);
		}
	}

	@Override
	public void inALambdaExpCG(ALambdaExpCG node) throws AnalysisException
	{
		AMethodTypeCG methodType = (AMethodTypeCG) node.getType().clone();
		AInterfaceDeclCG lambdaInterface = functionValueAssistant.findInterface(methodType);

		if (lambdaInterface == null)
		{
			@SuppressWarnings("unchecked")
			List<? extends AFormalParamLocalParamCG> formalParams = (List<? extends AFormalParamLocalParamCG>) node.getParams().clone();
			lambdaInterface = consInterface(methodType, formalParams);

			functionValueAssistant.registerInterface(lambdaInterface);
		}

		LinkedList<AFormalParamLocalParamCG> params = node.getParams();

		AInterfaceTypeCG classType = new AInterfaceTypeCG();
		classType.setName(lambdaInterface.getName());

		AMethodDeclCG lambdaDecl = lambdaInterface.getMethodSignatures().get(0).clone();

		for (int i = 0; i < params.size(); i++)
		{
			AFormalParamLocalParamCG paramLocalDeclCG = params.get(i);
			STypeCG paramType = paramLocalDeclCG.getType();
			SPatternCG pattern = paramLocalDeclCG.getPattern();

			classType.getTypes().add(paramType.clone());
			lambdaDecl.getFormalParams().get(i).setType(paramType.clone());
			lambdaDecl.getFormalParams().get(i).setPattern(pattern.clone());
		}

		classType.getTypes().add(methodType.getResult().clone());
		lambdaDecl.getMethodType().setResult(methodType.getResult().clone());

		AReturnStmCG lambdaReturn = new AReturnStmCG();
		lambdaReturn.setExp(node.getExp().clone());

		ABlockStmCG lambdaBody = new ABlockStmCG();
		lambdaBody.getStatements().add(lambdaReturn);

		lambdaDecl.setAbstract(false);
		lambdaDecl.setBody(lambdaBody);

		AAnonymousClassExpCG classExp = new AAnonymousClassExpCG();
		classExp.setType(classType);
		classExp.getMethods().add(lambdaDecl);

		transformationAssistant.replaceNodeWithRecursively(node, classExp, this);
	}

	private AInterfaceDeclCG consInterface(AMethodTypeCG methodType)
	{
		List<AFormalParamLocalParamCG> params = new LinkedList<AFormalParamLocalParamCG>();

		List<STypeCG> paramTypes = methodType.getParams();

		for (int i = 0; i < paramTypes.size(); i++)
		{
			STypeCG paramType = paramTypes.get(i);

			AFormalParamLocalParamCG param = new AFormalParamLocalParamCG();

			String nextParamName = funcValPrefixes.param() + (i + 1);
			AIdentifierPatternCG idPattern = new AIdentifierPatternCG();
			idPattern.setName(nextParamName);

			param.setType(paramType.clone());
			param.setPattern(idPattern);

			params.add(param);
		}

		return consInterface(methodType, params);
	}

	private AInterfaceDeclCG consInterface(AMethodTypeCG methodType,
			List<? extends AFormalParamLocalParamCG> params)
	{
		AInterfaceDeclCG methodTypeInterface = new AInterfaceDeclCG();

		methodTypeInterface.setPackage(null);
		methodTypeInterface.setName(transformationAssistant.getInfo().getTempVarNameGen().nextVarName(funcValPrefixes.funcInterface()));

		AMethodDeclCG evalMethod = new AMethodDeclCG();
		evalMethod.setImplicit(false);
		evalMethod.setAbstract(true);
		evalMethod.setAccess(IRConstants.PUBLIC);
		evalMethod.setBody(null);
		evalMethod.setIsConstructor(false);
		evalMethod.setMethodType(methodType.clone());
		evalMethod.setName(funcValPrefixes.evalMethod());
		evalMethod.setStatic(false);

		AMethodTypeCG evalMethodType = new AMethodTypeCG();

		for (int i = 0; i < params.size(); i++)
		{
			ATemplateTypeCG templateType = new ATemplateTypeCG();
			templateType.setName(funcValPrefixes.templateType() + (i + 1));

			AFormalParamLocalParamCG formalParam = new AFormalParamLocalParamCG();
			formalParam.setType(templateType);
			formalParam.setPattern(params.get(i).getPattern().clone());

			evalMethod.getFormalParams().add(formalParam);
			methodTypeInterface.getTemplateTypes().add(templateType.clone());
			evalMethodType.getParams().add(templateType.clone());
		}

		methodTypeInterface.getMethodSignatures().add(evalMethod);

		ATemplateTypeCG templateTypeResult = new ATemplateTypeCG();
		templateTypeResult.setName(funcValPrefixes.templateType()
				+ (methodType.getParams().size() + 1));
		methodTypeInterface.getTemplateTypes().add(templateTypeResult);
		evalMethodType.setResult(templateTypeResult.clone());

		evalMethod.setMethodType(evalMethodType);

		return methodTypeInterface;
	}
}
