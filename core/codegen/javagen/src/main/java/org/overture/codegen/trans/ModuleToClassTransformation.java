package org.overture.codegen.trans;

import org.overture.ast.modules.AModuleModules;
import org.overture.ast.types.AClassType;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SDeclCG;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFuncDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.AModuleDeclCG;
import org.overture.codegen.cgast.declarations.ANamedTraceDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.declarations.AStateDeclCG;
import org.overture.codegen.cgast.declarations.ATypeDeclCG;
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AExplicitVarExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.SVarExpCG;
import org.overture.codegen.cgast.name.ATypeNameCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class ModuleToClassTransformation extends DepthFirstAnalysisAdaptor
		implements ITotalTransformation
{
	private AClassDeclCG clazz = null;
	private TransAssistantCG transAssistant;
	
	public ModuleToClassTransformation(TransAssistantCG transAssistant)
	{
		this.transAssistant = transAssistant;
	}
	
	@Override
	public void caseAModuleDeclCG(AModuleDeclCG node) throws AnalysisException
	{
		clazz = new AClassDeclCG();
		clazz.setSourceNode(node.getSourceNode());
		// clazz.setTag(node.getTag());
		clazz.setAccess(IRConstants.PUBLIC);
		clazz.setName(node.getName());

		makeStateAccessExplicit(node);
		
		/*
		 * else if (declCg instanceof AMethodDeclCG || declCg instanceof AFuncDeclCG || declCg instanceof ATypeDeclCG ||
		 * declCg instanceof AStateDeclCG || declCg instanceof ANamedTraceDeclCG || declCg instanceof AFieldDeclCG)
		 */

		// TODO: exploit isLocal for state designators (no this should be transformed out of the tree) + variable expressions
		
		for (SDeclCG decl : node.getDecls())
		{
			decl = decl.clone();
			if (decl instanceof AMethodDeclCG)
			{
				AMethodDeclCG method = (AMethodDeclCG) decl;
				method.setAccess(IRConstants.PUBLIC);
				method.setStatic(true);
				
				clazz.getMethods().add(method);
				
			} else if (decl instanceof AFuncDeclCG)
			{
				// Functions are static by definition
				AFuncDeclCG func = (AFuncDeclCG) decl;
				func.setAccess(IRConstants.PUBLIC);
				
				clazz.getFunctions().add(func);

			} else if (decl instanceof ATypeDeclCG)
			{
				
				ATypeDeclCG typeDecl = (ATypeDeclCG) decl;
				typeDecl.setAccess(IRConstants.PUBLIC);
				
				clazz.getTypeDecls().add(typeDecl);
				
			} else if (decl instanceof AStateDeclCG)
			{
				// DO clever things..
				
				AStateDeclCG stateDecl = (AStateDeclCG) decl;
				
				ARecordDeclCG record = new ARecordDeclCG();
				record.setName(stateDecl.getName());
				
				for(AFieldDeclCG field : stateDecl.getFields())
				{
					record.getFields().add(field.clone());
				}
				
				ATypeDeclCG typeDecl = new ATypeDeclCG();
				typeDecl.setAccess(IRConstants.PUBLIC);
				typeDecl.setDecl(record);
				
				clazz.getTypeDecls().add(typeDecl);
				
				
				ATypeNameCG typeName = new ATypeNameCG();
				typeName.setName(stateDecl.getName());
				typeName.setDefiningClass(clazz.getName());
				
				ARecordTypeCG stateType = new ARecordTypeCG();
				stateType.setName(typeName);
				
				AFieldDeclCG stateField = new AFieldDeclCG();
				stateField.setAccess(IRConstants.PRIVATE);
				stateField.setFinal(true);
				stateField.setInitial(getInitExp(stateDecl));
				stateField.setName(stateDecl.getName());
				stateField.setStatic(true);
				stateField.setType(stateType);
				stateField.setVolatile(false);
				
				clazz.getFields().add(stateField);
				
				
			} else if (decl instanceof ANamedTraceDeclCG)
			{
				clazz.getTraces().add((ANamedTraceDeclCG) decl);
				
			} else if (decl instanceof AFieldDeclCG)
			{
				AFieldDeclCG field = (AFieldDeclCG) decl;
				field.setStatic(true);
				
				clazz.getFields().add(field);
			} else
			{
				Logger.getLog().printErrorln("Got unexpected declaration: "
						+ decl + " in '" + this.getClass().getSimpleName()
						+ "'");
			}

		}

		// TODO Auto-generated method stub
		super.caseAModuleDeclCG(node);
	}

	private void makeStateAccessExplicit(final AModuleDeclCG module) throws AnalysisException
	{
		final AStateDeclCG stateDecl = getStateDecl(module);
		
		if(stateDecl == null)
		{
			// Nothing to do
			return;
		}
		
		module.apply(new DepthFirstAnalysisAdaptor()
		{
			@Override
			public void caseAIdentifierVarExpCG(AIdentifierVarExpCG node)
					throws AnalysisException
			{
				if(node.parent() instanceof AFieldExpCG)
				{
					return;
				}
				
				if(!node.getIsLocal())
				{
					// Then it must be made explicit
					
					
					
					AExplicitVarExpCG eVar = new AExplicitVarExpCG();
					eVar.setClassType(transAssistant.consClassType(stateDecl.getName()));
					eVar.setIsLambda(false);
					eVar.setIsLocal(node.getIsLocal());
					eVar.setName(node.getName());
					eVar.setSourceNode(node.getSourceNode());
					eVar.setTag(node.getTag());
					eVar.setType(node.getType().clone());
					
					
					transAssistant.replaceNodeWith(node, eVar);
					
				}
			}
			
			//TODO: STate designators?
		});
	}

	public AStateDeclCG getStateDecl(AModuleDeclCG module)
	{
		for(SDeclCG decl : module.getDecls())
		{
			if(decl instanceof AStateDeclCG)
			{
				return (AStateDeclCG) decl;
			}
		}
		
		return null;
	}
	
	private SExpCG getInitExp(AStateDeclCG stateDecl)
	{
		if(stateDecl.getInitExp() instanceof AEqualsBinaryExpCG)
		{
			AEqualsBinaryExpCG eqExp = (AEqualsBinaryExpCG) stateDecl.getInitExp();
			
			return eqExp.getRight().clone();
		}
		else
		{
			return null;
		}
	}

	public INode getResult()
	{
		return clazz;
	}
}
