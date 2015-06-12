package org.overture.codegen.vdm2cpp.stdlib;

import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptorQuestion;
import org.overture.codegen.cgast.expressions.AExplicitVarExpCG;
import org.overture.codegen.cgast.name.ATypeNameCG;
import org.overture.codegen.cgast.statements.APlainCallStmCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AExternalTypeCG;
import org.overture.codegen.vdm2cpp.DependencyManager;


public class DependencyAnalyser extends DepthFirstAnalysisAdaptorQuestion<DependencyManager> 
{

	@Override
	public void inAClassTypeCG(AClassTypeCG node, DependencyManager question)
			throws AnalysisException {
		question.addTargetLanguageType("shared_ptr", "std", "memory");
		question.addTargetLanguageType("vector", "std", "vector");
		question.addTargetLanguageType("set", "std", "set");
		question.addTargetLanguageType("map", "std", "map");
		question.addTargetLanguageType("vdm_types", "vdm", "vdm_types.hpp");
		question.addTargetLanguageType("vdm", "vdm", "vdm.hpp");
		
		question.addClassType(node.getName(), node.getName());
	}
	
	@Override
	public void inAExternalTypeCG(AExternalTypeCG node,
			DependencyManager question) throws AnalysisException {
		// TODO Auto-generated method stub
		question.addTargetLanguageType(node.getName(), "", node.getName()+".hpp");
		
		super.inAExternalTypeCG(node, question);
		
		
	}
	
	@Override
	public void inATypeNameCG(ATypeNameCG node, DependencyManager question)
			throws AnalysisException 
	{
		
		if(node.getDefiningClass() != null)
		{
			question.addClassType(node.getDefiningClass(), node.getName());
		}
		else
		{
			// if there is no defining class then it is the class itself
			question.addClassType(node.getName(), node.getName());
		}
	}
	

	@Override
	public void inAPlainCallStmCG(APlainCallStmCG node,
			DependencyManager question) throws AnalysisException {
		// TODO Auto-generated method stub
		if(node.getClassType() instanceof AClassTypeCG)
		{
			AClassTypeCG cls = (AClassTypeCG)node.getClassType();
			
			String name = cls.getName();
			question.addClassType(name,name);
		}
	}
		
	@Override
	public void inAExplicitVarExpCG(AExplicitVarExpCG node, DependencyManager question) throws AnalysisException {
		STypeCG class_type = node.getClassType();
		if(class_type != null)
		{
			if(class_type instanceof AClassTypeCG)
			{
				AClassTypeCG cg = (AClassTypeCG) class_type;
				question.addClassType(cg.getName(),cg.getName());
			}
		}
	}
	
}