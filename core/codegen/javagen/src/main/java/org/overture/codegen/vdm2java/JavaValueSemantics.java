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
package org.overture.codegen.vdm2java;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.assistant.AssistantManager;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.SClassDeclCG;
import org.overture.codegen.cgast.expressions.AAddrEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AAddrNotEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.ACardUnaryExpCG;
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG;
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AExplicitVarExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AFieldNumberExpCG;
import org.overture.codegen.cgast.expressions.AHeadUnaryExpCG;
import org.overture.codegen.cgast.expressions.AInSetBinaryExpCG;
import org.overture.codegen.cgast.expressions.AIndicesUnaryExpCG;
import org.overture.codegen.cgast.expressions.AInstanceofExpCG;
import org.overture.codegen.cgast.expressions.ALenUnaryExpCG;
import org.overture.codegen.cgast.expressions.AMapSeqGetExpCG;
import org.overture.codegen.cgast.expressions.ANewExpCG;
import org.overture.codegen.cgast.expressions.ANotEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.ASetProperSubsetBinaryExpCG;
import org.overture.codegen.cgast.expressions.ASetSubsetBinaryExpCG;
import org.overture.codegen.cgast.expressions.ATupleCompatibilityExpCG;
import org.overture.codegen.cgast.expressions.ATupleSizeExpCG;
import org.overture.codegen.cgast.statements.AAssignToExpStmCG;
import org.overture.codegen.cgast.statements.ACallObjectExpStmCG;
import org.overture.codegen.cgast.statements.AForAllStmCG;
import org.overture.codegen.cgast.statements.AMapSeqUpdateStmCG;
import org.overture.codegen.cgast.types.AExternalTypeCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.cgast.types.ATupleTypeCG;
import org.overture.codegen.cgast.types.SMapTypeCG;
import org.overture.codegen.cgast.types.SSeqTypeCG;
import org.overture.codegen.cgast.types.SSetTypeCG;

public class JavaValueSemantics
{
	private JavaFormat javaFormat;
	private JavaSettings javaSettings;
	private List<INode> cloneFreeNodes;
	
	public JavaValueSemantics(JavaFormat javaFormat)
	{
		this.javaFormat = javaFormat;
		this.javaSettings = new JavaSettings();
		this.cloneFreeNodes = new LinkedList<>();
	}
	
	public void clear()
	{
		cloneFreeNodes.clear();
	}
	
	public void addCloneFreeNode(INode node)
	{
		cloneFreeNodes.add(node);
	}
	
	public List<INode> getCloneFreeNodes()
	{
		return cloneFreeNodes;
	}

	public void setJavaSettings(JavaSettings javaSettings)
	{
		this.javaSettings = javaSettings;
	}
	
	public JavaSettings getJavaSettings()
	{
		return javaSettings;
	}

	public boolean cloneMember(AFieldNumberExpCG exp)
	{
		if (javaSettings.getDisableCloning())
		{
			return false;
		}
		
		if(isCloneFree(exp))
		{
			return false;
		}

		// Generally tuples need to be cloned, for example, if they
		// contain a record field (that must be cloned)

		if (exp.parent() instanceof AFieldNumberExpCG)
		{
			return false;
		}
		
		if(cloneNotNeededMapPutGet(exp))
		{
			return false;
		}
		
		if(cloneNotNeededAssign(exp))
		{
			return false;
		}
		
		STypeCG type = exp.getTuple().getType();

		if (type instanceof ATupleTypeCG)
		{
			ATupleTypeCG tupleType = (ATupleTypeCG) type;

			long field = exp.getField();
			STypeCG fieldType = tupleType.getTypes().get((int) (field - 1));

			if (usesStructuralEquivalence(fieldType))
			{
				return true;
			}
		}

		return false;
	}
	
	public boolean cloneMember(AFieldExpCG exp)
	{
		if (javaSettings.getDisableCloning())
		{
			return false;
		}
		
		if(isCloneFree(exp))
		{
			return false;
		}

		INode parent = exp.parent();
		if (cloneNotNeeded(parent))
		{
			return false;
		}
		
		if(cloneNotNeededMapPutGet(exp))
		{
			return false;
		}
		
		if(cloneNotNeededAssign(exp))
		{
			return false;
		}

		STypeCG type = exp.getObject().getType();

		if (type instanceof ARecordTypeCG)
		{
			ARecordTypeCG recordType = (ARecordTypeCG) type;

			String memberName = exp.getMemberName();

			List<SClassDeclCG> classes = javaFormat.getIrInfo().getClasses();
			AssistantManager assistantManager = javaFormat.getIrInfo().getAssistantManager();

			AFieldDeclCG memberField = assistantManager.getDeclAssistant().getFieldDecl(classes, recordType, memberName);

			if (memberField != null
					&& usesStructuralEquivalence(memberField.getType()))
			{
				return true;
			}
		}

		return false;
	}

	public boolean shouldClone(SExpCG exp)
	{
		if (javaSettings.getDisableCloning())
		{
			return false;
		}
		
		if(isCloneFree(exp))
		{
			return false;
		}

		if(inRecClassNonConstructor(exp))
		{
			return false;
		}
		
		INode parent = exp.parent();

		if (cloneNotNeeded(parent))
		{
			return false;
		}

		if (parent instanceof AAssignToExpStmCG)
		{
			AAssignToExpStmCG assignment = (AAssignToExpStmCG) parent;
			if (assignment.getTarget() == exp)
			{
				return false;
			}
		}
		
		if(parent instanceof ACallObjectExpStmCG)
		{
			ACallObjectExpStmCG callObjStm = (ACallObjectExpStmCG) parent;
			
			if(callObjStm.getObj() == exp)
			{
				return false;
			}
		}
		
		if(cloneNotNeededMapPutGet(exp))
		{
			return false;
		}
		
		if(isPrePostArgument(exp))
		{
			return false;
		}
		
		STypeCG type = exp.getType();

		if (usesStructuralEquivalence(type))
		{
			if (parent instanceof ANewExpCG)
			{
				ANewExpCG newExp = (ANewExpCG) parent;
				STypeCG newExpType = newExp.getType();

				if (usesStructuralEquivalence(newExpType))
				{
					return false;
				}
			}

			return true;
		}

		return false;
	}

	private boolean inRecClassNonConstructor(SExpCG exp)
	{
		ADefaultClassDeclCG encClass = exp.getAncestor(ADefaultClassDeclCG.class);
		
		if(encClass != null)
		{
			LinkedList<AMethodDeclCG> methods = encClass.getMethods();
			
			boolean isRec = false;
			for(AMethodDeclCG m : methods)
			{
				if(m.getIsConstructor() && m.getMethodType().getResult() instanceof ARecordTypeCG)
				{
					isRec = true;
					break;
				}
			}
			
			if(!isRec)
			{
				return false;
			}
			else
			{
				AMethodDeclCG encMethod = exp.getAncestor(AMethodDeclCG.class);
				
				if(encMethod != null)
				{
					return !encMethod.getIsConstructor();
				}
				else
				{
					return false;
				}
			}
		}
		else
		{
			return false;
		}
	}

	private boolean cloneNotNeededMapPutGet(SExpCG exp)
	{
		INode parent = exp.parent();
		
		if(parent instanceof AMapSeqUpdateStmCG)
		{
			AMapSeqUpdateStmCG mapSeqUpd = (AMapSeqUpdateStmCG) parent;
			
			if(mapSeqUpd.getCol() == exp)
			{
				return true;
			}
		}
		
		if(parent instanceof AMapSeqGetExpCG)
		{
			AMapSeqGetExpCG mapSeqGet = (AMapSeqGetExpCG) parent;
			
			if(mapSeqGet.getCol() == exp)
			{
				return true;
			}
		}

		return false;
	}

	private boolean cloneNotNeeded(INode parent)
	{
		while(parent instanceof ACastUnaryExpCG)
		{
			parent = parent.parent();
		}
		
		if (parent instanceof AApplyExpCG)
		{
			// Cloning is not needed if the expression is
			// used to look up a value in a sequence or a map
			SExpCG root = ((AApplyExpCG) parent).getRoot();

			if (!(root.getType() instanceof AMethodTypeCG))
			{
				return true;
			}
		}

		return parent instanceof AFieldExpCG
				|| parent instanceof AFieldNumberExpCG
				|| parent instanceof ATupleSizeExpCG
				|| parent instanceof ATupleCompatibilityExpCG
				|| parent instanceof AEqualsBinaryExpCG
				|| parent instanceof ANotEqualsBinaryExpCG
				|| parent instanceof AAddrEqualsBinaryExpCG
				|| parent instanceof AAddrNotEqualsBinaryExpCG
				|| parent instanceof AForAllStmCG
				|| parent instanceof AInstanceofExpCG
				|| cloneNotNeededCollectionOperator(parent)
				|| cloneNotNeededUtilCall(parent);
	}

	private boolean isPrePostArgument(SExpCG exp)
	{
		INode parent = exp.parent();
		
		if(!(parent instanceof AApplyExpCG))
		{
			return false;
		}
		
		AApplyExpCG applyExp = (AApplyExpCG) parent;
		
		Object tag = applyExp.getTag();
		
		if(!(tag instanceof JavaValueSemanticsTag))
		{
			return false;
		}
		
		JavaValueSemanticsTag javaTag = (JavaValueSemanticsTag) tag;
		
		if(javaTag.mustClone())
		{
			return false;
		}
		
		return applyExp.getArgs().contains(exp);
	}

	private boolean cloneNotNeededCollectionOperator(INode parent)
	{
		return cloneNotNeededSeqOperators(parent)
				|| cloneNotNeededSetOperators(parent);
	}

	private boolean cloneNotNeededSeqOperators(INode parent)
	{
		return parent instanceof ALenUnaryExpCG
				|| parent instanceof AIndicesUnaryExpCG
				|| parent instanceof AHeadUnaryExpCG;
	}

	private boolean cloneNotNeededSetOperators(INode parent)
	{
		return 	parent instanceof ACardUnaryExpCG
				|| parent instanceof AInSetBinaryExpCG
				|| parent instanceof ASetSubsetBinaryExpCG
				|| parent instanceof ASetProperSubsetBinaryExpCG;
	}

	private boolean cloneNotNeededUtilCall(INode node)
	{
		if (!(node instanceof AApplyExpCG))
		{
			return false;
		}

		AApplyExpCG applyExp = (AApplyExpCG) node;
		SExpCG root = applyExp.getRoot();

		if (!(root instanceof AExplicitVarExpCG))
		{
			return false;
		}

		AExplicitVarExpCG explicitVar = (AExplicitVarExpCG) root;

		STypeCG classType = explicitVar.getClassType();

		return classType instanceof AExternalTypeCG
				&& ((AExternalTypeCG) classType).getName().equals(JavaFormat.UTILS_FILE);
	}

	public boolean usesStructuralEquivalence(STypeCG type)
	{
		return type instanceof ARecordTypeCG || type instanceof ATupleTypeCG
				|| type instanceof SSeqTypeCG || type instanceof SSetTypeCG
				|| type instanceof SMapTypeCG;
	}
	
	private boolean cloneNotNeededAssign(SExpCG exp)
	{
		INode parent = exp.parent();
		
		if (parent instanceof AAssignToExpStmCG)
		{
			AAssignToExpStmCG assignment = (AAssignToExpStmCG) parent;
			if (assignment.getTarget() == exp)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isCloneFree(SExpCG exp)
	{
		if(exp == null)
		{
			return false;
		}
		
		INode next = exp;
		
		while(next != null)
		{
			if(contains(next))
			{
				return true;
			}
			else
			{
				next = next.parent();
			}
		}
		
		return false;
	}
	
	private boolean contains(INode node)
	{
		for(INode n : cloneFreeNodes)
		{
			if(n == node)
			{
				return true;
			}
		}
		
		return false;
	}
}
