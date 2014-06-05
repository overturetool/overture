package org.overture.codegen.vdm2java;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.PType;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AFormalParamLocalDeclCG;
import org.overture.codegen.cgast.declarations.AInterfaceDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.expressions.AAnonymousClassExpCG;
import org.overture.codegen.cgast.expressions.ALambdaExpCG;
import org.overture.codegen.cgast.expressions.SVarExpCG;
import org.overture.codegen.cgast.pattern.AIdentifierPatternCG;
import org.overture.codegen.cgast.pattern.PPatternCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.cgast.types.AInterfaceTypeCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.cgast.types.ATemplateTypeCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.constants.IRConstants;
import org.overture.codegen.transform.TransformationAssistantCG;

public class FunctionValueVisitor extends DepthFirstAnalysisAdaptor
{
	private int counter = 0;
	
	private TransformationAssistantCG transformationAssistant;
	
	private FunctionValueAssistant functionValueAssistant;
	
	private String interfaceNamePrefix;
	private String templateTypePrefix;
	private String evalMethodName;
	private String paramNamePrefix;
	
	public FunctionValueVisitor(TransformationAssistantCG transformationAssistant, FunctionValueAssistant functionValueAssistant, String interfaceNamePrefix, String templateTypePrefix, String evalMethodName, String paramNamePrefix)
	{
		this.transformationAssistant = transformationAssistant;
		this.functionValueAssistant = functionValueAssistant;
		this.interfaceNamePrefix = interfaceNamePrefix;
		this.templateTypePrefix = templateTypePrefix;
		this.evalMethodName = evalMethodName;
		this.paramNamePrefix = paramNamePrefix;
		
		this.functionValueAssistant = new FunctionValueAssistant();
	}

	public FunctionValueAssistant getFunctionValueAssistant()
	{
		return functionValueAssistant;
	}

	@Override
	public void inAMethodTypeCG(AMethodTypeCG node) throws AnalysisException
	{
		if(node.parent() instanceof AMethodDeclCG)
			return;
		
		if(node.parent() instanceof SVarExpCG)
			return;
		
		PType vdmType = node.getEquivalent();
		
		if(!(vdmType instanceof AFunctionType))
		{
			//vdmType == null:
			//Can be the case if the default constructor in the IR AST has been manually
			//constructed. In this case it is not needed to construct the
			//interface
			
			//vdmType can also be an operation type, but operations cannot be used as values
			return;
		}
		
		AInterfaceDeclCG info = functionValueAssistant.findInterface(node);
		
		if(info == null)
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
		
		if(lambdaInterface == null)
		{
			@SuppressWarnings("unchecked")
			List<? extends AFormalParamLocalDeclCG> formalParams = (List<? extends AFormalParamLocalDeclCG>) node.getParams().clone();
			lambdaInterface = consInterface(methodType, formalParams);
			
			functionValueAssistant.registerInterface(lambdaInterface);
		}
		
		LinkedList<AFormalParamLocalDeclCG> params = node.getParams();
		
		AInterfaceTypeCG classType = new AInterfaceTypeCG();
		classType.setName(lambdaInterface.getName());
		
		AMethodDeclCG lambdaDecl = lambdaInterface.getMethodSignatures().get(0).clone();
		
		for(int i = 0; i < params.size(); i++)
		{
			AFormalParamLocalDeclCG paramLocalDeclCG = params.get(i);
			PTypeCG paramType = paramLocalDeclCG.getType();
			PPatternCG pattern = paramLocalDeclCG.getPattern();
			
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
		classExp.setType(classType);;
		classExp.getMethods().add(lambdaDecl);
		
		transformationAssistant.replaceNodeWith(node, classExp);
		
		classExp.apply(this);
	}
	
	private AInterfaceDeclCG consInterface(AMethodTypeCG methodType)
	{
		List<AFormalParamLocalDeclCG> params = new LinkedList<AFormalParamLocalDeclCG>();
		
		List<PTypeCG> paramTypes = methodType.getParams();
		
		for(int i = 0; i < paramTypes.size(); i++)
		{
			PTypeCG paramType = paramTypes.get(i);
			
			AFormalParamLocalDeclCG param = new AFormalParamLocalDeclCG();
			
			String nextParamName = paramNamePrefix + (i + 1);
			AIdentifierPatternCG idPattern = new AIdentifierPatternCG();
			idPattern.setName(nextParamName);
			
			param.setType(paramType.clone());
			param.setPattern(idPattern);
			
			params.add(param);
		}
		
		return consInterface(methodType, params);
	}
	
	private AInterfaceDeclCG consInterface(AMethodTypeCG methodType, List<? extends AFormalParamLocalDeclCG> params)
	{
		AInterfaceDeclCG methodTypeInterface = new AInterfaceDeclCG();
		
		methodTypeInterface.setPackage(null);
		methodTypeInterface.setName(getTypeName());
		
		AMethodDeclCG evalMethod = new AMethodDeclCG();
		evalMethod.setAbstract(true);
		evalMethod.setAccess(IRConstants.PUBLIC);
		evalMethod.setBody(null);
		evalMethod.setIsConstructor(false);
		evalMethod.setMethodType(methodType.clone());
		evalMethod.setName(evalMethodName);
		evalMethod.setStatic(false);
		
		AMethodTypeCG evalMethodType = new AMethodTypeCG();
		
		for(int i = 0; i < params.size(); i++)
		{
			ATemplateTypeCG templateType = new ATemplateTypeCG();
			templateType.setName(templateTypePrefix + (i + 1));

			AFormalParamLocalDeclCG formalParam = new AFormalParamLocalDeclCG();
			formalParam.setType(templateType);
			formalParam.setPattern(params.get(i).getPattern().clone());
			
			evalMethod.getFormalParams().add(formalParam);
			methodTypeInterface.getTemplateTypes().add(templateType.clone());
			evalMethodType.getParams().add(templateType.clone());
		}
		
		methodTypeInterface.getMethodSignatures().add(evalMethod);
		
		ATemplateTypeCG templateTypeResult = new ATemplateTypeCG();
		templateTypeResult.setName(templateTypePrefix + (methodType.getParams().size() + 1));
		methodTypeInterface.getTemplateTypes().add(templateTypeResult);
		evalMethodType.setResult(templateTypeResult.clone());
		
		evalMethod.setMethodType(evalMethodType);
		
		return methodTypeInterface;
	}
	
	private String getTypeName()
	{
		String typeName = interfaceNamePrefix + (++counter);
		return typeName;
	}
}
