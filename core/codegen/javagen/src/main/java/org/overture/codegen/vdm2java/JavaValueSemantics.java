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
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.expressions.*;
import org.overture.codegen.ir.expressions.AIsOfClassExpIR;
import org.overture.codegen.ir.statements.AAssignToExpStmIR;
import org.overture.codegen.ir.statements.ACallObjectExpStmIR;
import org.overture.codegen.ir.statements.AForAllStmIR;
import org.overture.codegen.ir.statements.AMapCompAddStmIR;
import org.overture.codegen.ir.statements.AMapSeqUpdateStmIR;
import org.overture.codegen.ir.statements.ASeqCompAddStmIR;
import org.overture.codegen.ir.statements.ASetCompAddStmIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overture.codegen.ir.types.ARecordTypeIR;
import org.overture.codegen.ir.types.ATupleTypeIR;
import org.overture.codegen.ir.types.AUnionTypeIR;
import org.overture.codegen.ir.types.SMapTypeIR;
import org.overture.codegen.ir.types.SSeqTypeIR;
import org.overture.codegen.ir.types.SSetTypeIR;

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

	public boolean cloneMember(AFieldNumberExpIR exp)
	{
		if (javaSettings.getDisableCloning())
		{
			return false;
		}

		if (isCloneFree(exp))
		{
			return false;
		}

		// Generally tuples need to be cloned, for example, if they
		// contain a record field (that must be cloned)

		if (exp.parent() instanceof AFieldNumberExpIR)
		{
			return false;
		}

		if (cloneNotNeededMapPutGet(exp))
		{
			return false;
		}

		if (cloneNotNeededAssign(exp))
		{
			return false;
		}

		List<ATupleTypeIR> tupleTypes = getTypes(exp.getTuple().getType(), ATupleTypeIR.class);
		final int idx = (int) (exp.getField() - 1);

		for (ATupleTypeIR tupleType : tupleTypes)
		{
			STypeIR fieldType = tupleType.getTypes().get(idx);

			if (mayBeValueType(fieldType))
			{
				return true;
			}
		}

		return false;
	}

	public boolean cloneMember(AFieldExpIR exp)
	{
		if (javaSettings.getDisableCloning())
		{
			return false;
		}

		if (isCloneFree(exp))
		{
			return false;
		}

		INode parent = exp.parent();
		if (cloneNotNeeded(parent))
		{
			return false;
		}

		if (cloneNotNeededMapPutGet(exp))
		{
			return false;
		}

		if (cloneNotNeededAssign(exp))
		{
			return false;
		}

		List<ARecordTypeIR> recTypes = getTypes(exp.getObject().getType(), ARecordTypeIR.class);
		String memberName = exp.getMemberName();
		List<SClassDeclIR> classes = javaFormat.getIrInfo().getClasses();
		AssistantManager man = javaFormat.getIrInfo().getAssistantManager();

		for (ARecordTypeIR r : recTypes)
		{
			AFieldDeclIR field = man.getDeclAssistant().getFieldDecl(classes, r, memberName);

			if (field != null && mayBeValueType(field.getType()))
			{
				return true;
			}
		}

		return false;
	}

	private <T extends STypeIR> List<T> getTypes(STypeIR type, Class<T> filter)
	{
		List<T> filteredTypes = new LinkedList<>();

		if (filter.isInstance(type))
		{
			filteredTypes.add(filter.cast(type));
		} else if (type instanceof AUnionTypeIR)
		{
			List<STypeIR> types = ((AUnionTypeIR) type).getTypes();

			for (STypeIR t : types)
			{
				filteredTypes.addAll(getTypes(t, filter));
			}
		}

		return filteredTypes;
	}

	public boolean shouldClone(SExpIR exp)
	{
		if (javaSettings.getDisableCloning())
		{
			return false;
		}

		if (isCloneFree(exp))
		{
			return false;
		}

		if (inRecClassNonConstructor(exp))
		{
			return false;
		}

		if (compAdd(exp))
		{
			return false;
		}

		INode parent = exp.parent();

		if (cloneNotNeeded(parent))
		{
			return false;
		}

		if (parent instanceof AAssignToExpStmIR)
		{
			AAssignToExpStmIR assignment = (AAssignToExpStmIR) parent;
			if (assignment.getTarget() == exp)
			{
				return false;
			}
		}

		if (parent instanceof ACallObjectExpStmIR)
		{
			ACallObjectExpStmIR callObjStm = (ACallObjectExpStmIR) parent;

			if (callObjStm.getObj() == exp)
			{
				return false;
			}
		}

		if (cloneNotNeededMapPutGet(exp))
		{
			return false;
		}

		if (isPrePostArgument(exp))
		{
			return false;
		}

		STypeIR type = exp.getType();

		if (mayBeValueType(type))
		{
			if (parent instanceof ANewExpIR)
			{
				ANewExpIR newExp = (ANewExpIR) parent;
				STypeIR newExpType = newExp.getType();

				if (mayBeValueType(newExpType))
				{
					return false;
				}
			}

			return true;
		}

		return false;
	}

	private boolean compAdd(SExpIR exp)
	{
		INode parent = exp.parent();

		if (parent instanceof ASeqCompAddStmIR)
		{
			ASeqCompAddStmIR add = (ASeqCompAddStmIR) parent;
			return add.getSeq() == exp;
		}

		if (parent instanceof ASetCompAddStmIR)
		{
			ASetCompAddStmIR add = (ASetCompAddStmIR) parent;
			return add.getSet() == exp;
		}

		if (parent instanceof AMapCompAddStmIR)
		{
			AMapCompAddStmIR add = (AMapCompAddStmIR) parent;
			return add.getMap() == exp;
		}

		return false;
	}

	private boolean inRecClassNonConstructor(SExpIR exp)
	{
		ADefaultClassDeclIR encClass = exp.getAncestor(ADefaultClassDeclIR.class);

		if (encClass != null)
		{
			LinkedList<AMethodDeclIR> methods = encClass.getMethods();

			boolean isRec = false;
			for (AMethodDeclIR m : methods)
			{
				if (m.getIsConstructor()
						&& m.getMethodType().getResult() instanceof ARecordTypeIR)
				{
					isRec = true;
					break;
				}
			}

			if (!isRec)
			{
				return false;
			} else
			{
				AMethodDeclIR encMethod = exp.getAncestor(AMethodDeclIR.class);

				if (encMethod != null)
				{
					return !encMethod.getIsConstructor();
				} else
				{
					return false;
				}
			}
		} else
		{
			return false;
		}
	}

	private boolean cloneNotNeededMapPutGet(SExpIR exp)
	{
		INode parent = exp.parent();

		if (parent instanceof AMapSeqUpdateStmIR)
		{
			AMapSeqUpdateStmIR mapSeqUpd = (AMapSeqUpdateStmIR) parent;

			if (mapSeqUpd.getCol() == exp)
			{
				return true;
			}
		}

		if (parent instanceof AMapSeqGetExpIR)
		{
			AMapSeqGetExpIR mapSeqGet = (AMapSeqGetExpIR) parent;

			if (mapSeqGet.getCol() == exp)
			{
				return true;
			}
		}

		return false;
	}

	private boolean cloneNotNeeded(INode parent)
	{
		while (parent instanceof ACastUnaryExpIR)
		{
			parent = parent.parent();
		}

		if (parent instanceof AApplyExpIR)
		{
			// Cloning is not needed if the expression is
			// used to look up a value in a sequence or a map
			SExpIR root = ((AApplyExpIR) parent).getRoot();

			if (!(root.getType() instanceof AMethodTypeIR))
			{
				return true;
			}
		}

		return parent instanceof AFieldExpIR
				|| parent instanceof AFieldNumberExpIR
				|| parent instanceof ATupleSizeExpIR
				|| parent instanceof ATupleCompatibilityExpIR
				|| parent instanceof AEqualsBinaryExpIR
				|| parent instanceof ANotEqualsBinaryExpIR
				|| parent instanceof AAddrEqualsBinaryExpIR
				|| parent instanceof AAddrNotEqualsBinaryExpIR
				|| parent instanceof AForAllStmIR
				|| parent instanceof AIsOfClassExpIR
				|| parent instanceof SIsExpIR
				|| cloneNotNeededCollectionOperator(parent)
				|| cloneNotNeededUtilCall(parent);
	}

	private boolean isPrePostArgument(SExpIR exp)
	{
		INode parent = exp.parent();

		if (!(parent instanceof AApplyExpIR))
		{
			return false;
		}

		AApplyExpIR applyExp = (AApplyExpIR) parent;

		Object tag = applyExp.getTag();

		if (!(tag instanceof JavaValueSemanticsTag))
		{
			return false;
		}

		JavaValueSemanticsTag javaTag = (JavaValueSemanticsTag) tag;

		if (javaTag.mustClone())
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
		return parent instanceof ALenUnaryExpIR
				|| parent instanceof AIndicesUnaryExpIR
				|| parent instanceof AHeadUnaryExpIR;
	}

	private boolean cloneNotNeededSetOperators(INode parent)
	{
		return parent instanceof ACardUnaryExpIR
				|| parent instanceof AInSetBinaryExpIR
				|| parent instanceof ASetSubsetBinaryExpIR
				|| parent instanceof ASetProperSubsetBinaryExpIR;
	}

	private boolean cloneNotNeededUtilCall(INode node)
	{
		if (!(node instanceof AApplyExpIR))
		{
			return false;
		}

		AApplyExpIR applyExp = (AApplyExpIR) node;
		SExpIR root = applyExp.getRoot();

		if (!(root instanceof AExplicitVarExpIR))
		{
			return false;
		}

		AExplicitVarExpIR explicitVar = (AExplicitVarExpIR) root;

		STypeIR classType = explicitVar.getClassType();

		return classType instanceof AExternalTypeIR
				&& ((AExternalTypeIR) classType).getName().equals(JavaFormat.UTILS_FILE);
	}

	public boolean mayBeValueType(STypeIR type)
	{
		if (type instanceof AUnionTypeIR)
		{
			LinkedList<STypeIR> types = ((AUnionTypeIR) type).getTypes();

			for (STypeIR t : types)
			{
				if (mayBeValueType(t))
				{
					return true;
				}
			}

			return false;
		} else
		{
			return type instanceof ARecordTypeIR || type instanceof ATupleTypeIR
					|| type instanceof SSeqTypeIR || type instanceof SSetTypeIR
					|| type instanceof SMapTypeIR;
		}
	}

	private boolean cloneNotNeededAssign(SExpIR exp)
	{
		INode parent = exp.parent();

		if (parent instanceof AAssignToExpStmIR)
		{
			AAssignToExpStmIR assignment = (AAssignToExpStmIR) parent;
			if (assignment.getTarget() == exp)
			{
				return true;
			}
		}

		return false;
	}

	public boolean isCloneFree(SExpIR exp)
	{
		if (exp == null)
		{
			return false;
		}

		INode next = exp;

		while (next != null)
		{
			if (contains(next))
			{
				return true;
			} else
			{
				next = next.parent();
			}
		}

		return false;
	}

	private boolean contains(INode node)
	{
		for (INode n : cloneFreeNodes)
		{
			if (n == node)
			{
				return true;
			}
		}

		return false;
	}
}
