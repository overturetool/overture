/*
 * #%~
 * VDM to Isabelle Code Generation
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

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.merging.TemplateCallable;
import org.overture.codegen.merging.TemplateStructure;
import org.overturetool.cgisa.helpers.IsMethodTypeVisitor;
import org.overturetool.cgisa.helpers.IsSeqOfCharTypeVisitor;

public class IsaTranslationUtils
{

	private static final String TEMPLATE_CALLABLE_NAME = "Isa";
	private static final Object PARAM_SEP = " and ";
	private MergeVisitor mergeVisitor;

	public IsaTranslationUtils(TemplateStructure templateStructure)
	{
		TemplateCallable[] templateCallables = new TemplateCallable[] { new TemplateCallable(TEMPLATE_CALLABLE_NAME, this) };
		this.mergeVisitor = new MergeVisitor(new IsaTemplateManager(templateStructure), templateCallables);
	}

	public MergeVisitor getMergeVisitor()
	{
		return mergeVisitor;
	}

	// Translations (call merge visitor)

	public String trans(INode node) throws AnalysisException
	{
		StringWriter writer = new StringWriter();
		node.apply(mergeVisitor, writer);

		return writer.toString();
	}

	public String transParams(List<AFormalParamLocalParamCG> params)
			throws AnalysisException
	{
		StringBuilder sb = new StringBuilder();

		Iterator<AFormalParamLocalParamCG> it = params.iterator();

		while (it.hasNext())
		{
			StringWriter writer = new StringWriter();
			it.next().apply(mergeVisitor, writer);
			sb.append(writer.toString());
			if (it.hasNext())
			{
				sb.append(PARAM_SEP);
			}

		}

		return sb.toString();
	}

	public String transArgs(List<INode> args) throws AnalysisException
	{
		if (args.isEmpty())
		{
			return "";
		}
		StringBuilder sb = new StringBuilder();

		Iterator<INode> it = args.iterator();

		while (it.hasNext())
		{
			sb.append(trans(it.next()));
			if (it.hasNext()){
				sb.append(PARAM_SEP);
			}
		}

		return sb.toString();
	}

	// Auxiliary Constructions

	public String norm(String name)
	{
		return name;
	}

	public String makeString(List<SExpCG> args) throws AnalysisException
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

	// Controlflow

	public String filter(AFieldDeclCG field) throws AnalysisException
	{
		if (field.getFinal() && field.getStatic())
		{
			return trans(field);
		}

		return "";
	}

	// Checks

	public boolean isString(STypeCG node) throws AnalysisException
	{
		return node.apply(new IsSeqOfCharTypeVisitor());
	}

	public boolean isFunc(STypeCG node) throws AnalysisException
	{
		return node.apply(new IsMethodTypeVisitor());
	}
}
