package org.overture.codegen.vdm2cpp;

import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptorQuestion;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.name.ATypeNameCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AMapMapTypeCG;
import org.overture.codegen.cgast.types.ASeqSeqTypeCG;
import org.overture.codegen.cgast.types.ASetSetTypeCG;

public class DependencyAnalyser extends DepthFirstAnalysisAdaptorQuestion<DependencyManager> 
{
	
	@Override
	public void inAClassTypeCG(AClassTypeCG node, DependencyManager question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		super.inAClassTypeCG(node, question);
		question.addTargetLanguageType("shared_ptr", "std", "memory");
	}
	@Override
	public void caseATypeNameCG(ATypeNameCG node, DependencyManager question)
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
			question.addTargetLanguageType("shared_ptr","std", "memory");
		}
	}
	
	
	@Override
	public void inARecordDeclCG(ARecordDeclCG node, DependencyManager question)
			throws AnalysisException {
		// if a record is being declared it will declare a Ptr type;
		question.addTargetLanguageType("shared_ptr", "std", "memory");
	}
	
	@Override
	public void caseAClassDeclCG(AClassDeclCG node, DependencyManager question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		question.addTargetLanguageType("shared_ptr", "std", "memory");
		super.caseAClassDeclCG(node, question);
	}
	
	@Override
	public void inASeqSeqTypeCG(ASeqSeqTypeCG node, DependencyManager question)
			throws AnalysisException {
		question.addTargetLanguageType("sequence", "vdm_collections", "vdm_collections/sequence.hpp");
	}
	
	@Override
	public void inASetSetTypeCG(ASetSetTypeCG node, DependencyManager question)
			throws AnalysisException {
		question.addTargetLanguageType("set", "vdm_collections", "vdm_collections/set.hpp");
	}
	
	@Override
	public void inAMapMapTypeCG(AMapMapTypeCG node, DependencyManager question)
			throws AnalysisException {
		question.addTargetLanguageType("map", "vdm_collections", "vdm_collections/map.hpp");
	}
	
}
