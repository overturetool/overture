/*
 * #%~
 * VDM to Isabelle Translation
 * %%
 * Copyright (C) 2008 - 2015 Overture
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
package org.overturetool.cgisa;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SMultipleBindCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AFuncDeclCG;
import org.overture.codegen.cgast.declarations.ANamedTypeDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.declarations.ATypeDeclCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.merging.TemplateCallable;
import org.overture.codegen.merging.TemplateStructure;
import org.overturetool.cgisa.utils.IsMethodTypeVisitor;
import org.overturetool.cgisa.utils.IsSeqOfCharTypeVisitor;

public class IsaTranslations
{

	private static final String TEMPLATE_CALLABLE_NAME = "Isa";
	private static final String TYPE_PARAM_SEP = " and ";
	private static final String LIST_SEP = ", ";
	private static final Object TUPLE_TYPE_SEPARATOR = "*";
	private MergeVisitor mergeVisitor;

	protected IsaChecks isaUtils;

	public IsaTranslations(TemplateStructure templateStructure)
	{
		TemplateCallable[] templateCallables = new TemplateCallable[] { new TemplateCallable(TEMPLATE_CALLABLE_NAME, this) };
		this.mergeVisitor = new MergeVisitor(new IsaTemplateManager(templateStructure, this.getClass()), templateCallables);
		this.isaUtils = new IsaChecks();
	}

	public MergeVisitor getMergeVisitor()
	{
		return mergeVisitor;
	}

	// Translations

	public String trans(INode node) throws AnalysisException
	{
		StringWriter writer = new StringWriter();
		node.apply(mergeVisitor, writer);

		return writer.toString();
	}

	public String transApplyParams(List<SExpCG> params)
			throws AnalysisException
	{
		return transNodeList(params, LIST_SEP);
	}

	public String transTypeParams(List<AFormalParamLocalParamCG> params)
			throws AnalysisException
	{
		return transNodeList(params, TYPE_PARAM_SEP);
	}

	public String transBinds(List<? extends SMultipleBindCG> binds)
			throws AnalysisException
	{
		return transNodeList(binds, LIST_SEP);
	}

	public String transNodeList(List<? extends INode> params, String sep)
			throws AnalysisException
	{
		StringBuilder sb = new StringBuilder();

		Iterator<? extends INode> it = params.iterator();

		while (it.hasNext())
		{
			StringWriter writer = new StringWriter();
			it.next().apply(mergeVisitor, writer);
			sb.append(writer.toString());
			if (it.hasNext())
			{
				sb.append(sep);
			}
		}
		return sb.toString();
	}

	public String transString(List<SExpCG> args) throws AnalysisException
	{
		StringBuilder sb = new StringBuilder();
		sb.append("''");
		for (SExpCG arg : args)
		{
			sb.append(trans(arg));
		}
		sb.append("''");
		return sb.toString();
	}

	public String transSeq(List<SExpCG> args) throws AnalysisException
	{
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(transNodeList(args, LIST_SEP));
		sb.append("]");
		return sb.toString();
	}

	public String rec2Tuple(ARecordDeclCG record) throws AnalysisException
	{
		StringBuilder sb = new StringBuilder();

		Iterator<AFieldDeclCG> it = record.getFields().iterator();

		while (it.hasNext())
		{
			AFieldDeclCG n = it.next();

			sb.append(trans(n.getType()));
			if (it.hasNext())
			{
				sb.append(TUPLE_TYPE_SEPARATOR);
			}
		}

		return sb.toString();
	}


	// Hacks - translations that manipulate the tree in grostesque way due to
	// issues with the IR
	// FIXME Unhack result name extraction for implicit functions
	public String hackResultName(AFuncDeclCG func) throws AnalysisException
	{
		SourceNode x = func.getSourceNode();
		if (x.getVdmNode() instanceof AImplicitFunctionDefinition)
		{
			AImplicitFunctionDefinition iFunc = (AImplicitFunctionDefinition) x.getVdmNode();
			return iFunc.getResult().getPattern().toString();
		}
		throw new AnalysisException("Expected AFuncDeclCG in implicit function source. Got: "
				+ x.getVdmNode().getClass().toString());
	}

	// FIXME Unhack invariant extraction for named types
	public String hackInv(ANamedTypeDeclCG type)
	{
		ATypeDeclCG tDecl = (ATypeDeclCG) type.parent();

		if (tDecl.getInv() != null)
		{
			AFuncDeclCG invFunc = (AFuncDeclCG) tDecl.getInv();
			StringBuilder sb = new StringBuilder();
			sb.append("inv ");
			sb.append(invFunc.getFormalParams().get(0).getPattern().toString());
			sb.append(" == ");
			sb.append(invFunc.getName());
			sb.append("(");
			sb.append("&");
			sb.append(invFunc.getFormalParams().get(0).getPattern().toString());
			sb.append(")");
			return sb.toString();
		}
		return "";
	}

	// FIXME Unhack invariant extraction for namedt ypes
	public String hackInv(ARecordDeclCG type)
	{

		if (type.getInvariant() != null)
		{
			AFuncDeclCG invFunc = (AFuncDeclCG) type.getInvariant();
			StringBuilder sb = new StringBuilder();
			sb.append("inv ");
			sb.append(invFunc.getFormalParams().get(0).getPattern().toString());
			sb.append(" == ");
			sb.append(invFunc.getName());
			sb.append("(");
			sb.append("&");
			sb.append(invFunc.getFormalParams().get(0).getPattern().toString());
			sb.append(")");
			return sb.toString();
		}
		return "";
	}

	public String hackInvDecl(ARecordDeclCG type) throws AnalysisException
	{
		if (type.getInvariant() != null)
		{
			return trans(type.getInvariant());
		}
		return "";
	}

	// Renamings

	public String norm(String name)
	{
		return name.replaceAll("-", "_");
	}

	public String varWrap(String v)
	{
		StringBuilder sb = new StringBuilder();
		sb.append('<');
		sb.append(v);
		sb.append('>');
		return sb.toString();
	}

	// Control flow

	public String filter(AFieldDeclCG field) throws AnalysisException
	{
		if (field.getFinal() && field.getStatic())
		{
			return trans(field);
		}

		return "";
	}

	// Checks

	public boolean isRoot(INode node)
	{
		return isaUtils.isRoot(node);
	}
	
	public boolean isRootRec(AApplyExpCG node){
		return isaUtils.isRootRec(node);
	}

	public boolean isString(STypeCG node) throws AnalysisException
	{
		return node.apply(new IsSeqOfCharTypeVisitor());
	}

	public boolean isFunc(STypeCG node) throws AnalysisException
	{
		return node.apply(new IsMethodTypeVisitor());
	}

	public boolean isRecordDecl(ATypeDeclCG node)
	{
		return (node.getDecl() instanceof ARecordDeclCG);
	}
}
