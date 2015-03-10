package org.overturetool.cgisa.transformations;

import org.overture.cgisa.extast.analysis.DepthFirstAnalysisIsaAdaptor;
import org.overture.cgisa.extast.declarations.AIsaClassDeclCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.trans.ITotalTransformation;

public class GroupMutRecs extends DepthFirstAnalysisIsaAdaptor implements
		ITotalTransformation
{

	private AIsaClassDeclCG result = null;

	@Override
	public void caseAClassDeclCG(AClassDeclCG node) throws AnalysisException
	{
		// clone original class into extended class
		result = new AIsaClassDeclCG();
		result.setAbstract(node.getAbstract());
		result.setAccess(node.getAccess());
		result.setFields(node.getFields());
		result.setFunctions(node.getFunctions());
		result.setInnerClasses(node.getInnerClasses());
		result.setInterfaces(node.getInterfaces());
		result.setMethods(node.getMethods());
		result.setMutexSyncs(node.getMutexSyncs());
		result.setName(node.getName());
		result.setPackage(node.getPackage());
		result.setPerSyncs(node.getPerSyncs());
		result.setSourceNode(node.getSourceNode());
		result.setStatic(node.getStatic());
		result.setSuperName(node.getSuperName());
		result.setTag(node.getTag());
		result.setThread(node.getThread());
		result.setTraces(node.getTraces());
		result.setTypeDecls(node.getTypeDecls());

		// now compute the mr func groups

	}

	@Override
	public AIsaClassDeclCG getResult()
	{
		return result;
	}

}
