package org.overture.codegen.vdm2cpp.visitors;

import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptorQuestion;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.declarations.ATypeDeclCG;
import org.overture.codegen.cgast.expressions.AExplicitVarExpCG;
import org.overture.codegen.cgast.name.ATypeNameCG;
import org.overture.codegen.cgast.statements.APlainCallStmCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AMapMapTypeCG;
import org.overture.codegen.cgast.types.ASeqSeqTypeCG;
import org.overture.codegen.cgast.types.ASetSetTypeCG;
import org.overture.codegen.vdm2cpp.DependencyManager;

public class DependencyAnalyser extends DepthFirstAnalysisAdaptorQuestion<DependencyManager> 
{

	@Override
	public void inAClassTypeCG(AClassTypeCG node, DependencyManager question)
			throws AnalysisException {

		question.addTargetLanguageType("metaiv", "", "metaiv.h");
		question.addTargetLanguageType("cg", "", "cg.h");
		question.addTargetLanguageType("cg_aux", "", "cg_aux.h");
		question.addTargetLanguageType("CGBase", "", "CGBase.hpp");
		question.addTargetLanguageType("VDMExtraUtils","vdm", "VDMExtraUtils.h");
		question.addClassType(node.getName(), node.getName());
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
			//question.addTargetLanguageType("shared_ptr","std", "memory");
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
	public void inARecordDeclCG(ARecordDeclCG node, DependencyManager question)
			throws AnalysisException {
		// if a record is being declared it will declare a Ptr type;
		//question.addTargetLanguageType("shared_ptr", "std", "memory");
	}
	
	@Override
	public void inAClassDeclCG(AClassDeclCG node, DependencyManager question)
			throws AnalysisException {
		//question.addTargetLanguageType("shared_ptr", "std", "memory");
	}
	
	@Override
	public void inASeqSeqTypeCG(ASeqSeqTypeCG node, DependencyManager question)
			throws AnalysisException {
		//question.addTargetLanguageType("sequence", "vdm_collections", "vdm_collections/sequence.hpp");
	}
	
	@Override
	public void inASetSetTypeCG(ASetSetTypeCG node, DependencyManager question)
			throws AnalysisException {
		//question.addTargetLanguageType("set", "vdm_collections", "vdm_collections/set.hpp");
	}
	
	@Override
	public void inAMapMapTypeCG(AMapMapTypeCG node, DependencyManager question)
			throws AnalysisException {
		//question.addTargetLanguageType("map", "vdm_collections", "vdm_collections/map.hpp");
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