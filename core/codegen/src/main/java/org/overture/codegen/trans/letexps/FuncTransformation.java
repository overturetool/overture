package org.overture.codegen.trans.letexps;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AFuncDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.expressions.ANotImplementedExpCG;
import org.overture.codegen.cgast.statements.ANotImplementedStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.cgast.types.ATemplateTypeCG;
import org.overture.codegen.ir.SourceNode;

public class FuncTransformation extends DepthFirstAnalysisAdaptor
{
	public FuncTransformation()
	{
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void caseAFuncDeclCG(AFuncDeclCG node) throws AnalysisException
	{
		String access = node.getAccess();
		Boolean isAbstract = node.getAbstract();
		LinkedList<ATemplateTypeCG> templateTypes = node.getTemplateTypes();
		AMethodTypeCG methodType = node.getMethodType();
		LinkedList<AFormalParamLocalParamCG> formalParams = node.getFormalParams();
		String name = node.getName();
		SExpCG body = node.getBody();
		SourceNode sourceNode = node.getSourceNode();
		
		AMethodDeclCG method = new AMethodDeclCG();
		method.setSourceNode(sourceNode);
		method.setAccess(access);
		method.setAbstract(isAbstract);
		method.setTemplateTypes((List<? extends ATemplateTypeCG>) templateTypes.clone());
		method.setMethodType(methodType.clone());
		method.setFormalParams((List<? extends AFormalParamLocalParamCG>) formalParams.clone());
		method.setName(name);
		method.setStatic(true);
		method.setIsConstructor(false);
		
		if(!(body instanceof ANotImplementedExpCG))
		{
			AReturnStmCG returnStm = new AReturnStmCG();
			returnStm.setExp(body.clone());
			method.setBody(returnStm);
		}
		else
		{
			method.setBody(new ANotImplementedStmCG());
		}
		
		AClassDeclCG enclosingClas = getEnclosingClass(node, "function declarations");
		enclosingClas.getFunctions().remove(node);
		enclosingClas.getMethods().add(method);
	}
	
	private AClassDeclCG getEnclosingClass(AFuncDeclCG node, String nodeStr) throws AnalysisException
	{
		AClassDeclCG enclosingClass = node.getAncestor(AClassDeclCG.class);

		//A Function must always have an enclosing class in a PP model
		if (enclosingClass == null)
		{
			throw new AnalysisException(String.format("Could not find enclosing class for function %s", node));
		}
		
		return enclosingClass;
	}
}
