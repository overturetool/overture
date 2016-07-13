/*
 * #%~
 * The VDM Pretty Printer
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
package org.overture.prettyprinter;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.node.INode;
import org.overture.ast.types.AInMapMapType;
import org.overture.ast.types.AMapMapType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASeq1SeqType;
import org.overture.ast.types.ASeqSeqType;
import org.overture.ast.types.SSetType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AVoidReturnType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SBasicType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.util.Utils;

public class TypePrettyPrinterVisitor extends
		QuestionAnswerAdaptor<PrettyPrinterEnv, String>
{

	@Override
	public String defaultSBasicType(SBasicType node, PrettyPrinterEnv question)
			throws AnalysisException
	{
		return node.toString();
	}

	@Override
	public String defaultINode(INode node, PrettyPrinterEnv question)
			throws AnalysisException
	{
		return node.toString();
	}

	@Override
	public String defaultSSetType(SSetType node, PrettyPrinterEnv question)
			throws AnalysisException
	{
		return ""
				+ ""
				+ (node.getEmpty() ? "{}" : "set of ("
						+ node.getSetof().apply(this, question) + ")");
	}

	@Override
	public String caseASeqSeqType(ASeqSeqType node, PrettyPrinterEnv question)
			throws AnalysisException
	{
		return ""
				+ ""
				+ (node.getEmpty() ? "[]" : "seq of ("
						+ node.getSeqof().apply(this, question) + ")");
	}

	@Override
	public String caseASeq1SeqType(ASeq1SeqType node, PrettyPrinterEnv question)
			throws AnalysisException
	{
		return "" + "seq1 of (" + node.getSeqof().apply(this, question) + ")";
	}

	@Override
	public String caseAMapMapType(AMapMapType node, PrettyPrinterEnv question)
			throws AnalysisException
	{
		return "" + "map (" + node.getFrom().apply(this, question) + ") to ("
				+ node.getTo().apply(this, question) + ")";
	}

	@Override
	public String caseAInMapMapType(AInMapMapType node,
			PrettyPrinterEnv question) throws AnalysisException
	{
		return "" + "inmap (" + node.getFrom().apply(this, question) + ") to ("
				+ node.getTo().apply(this, question) + ")";
	}

	@Override
	public String caseAProductType(AProductType node, PrettyPrinterEnv question)
			throws AnalysisException
	{
		List<String> types = new Vector<String>();
		for (PType t : node.getTypes())
		{
			types.add(t.apply(this, question));
		}
		return "" + "" + Utils.listToString("(", types, " * ", ")");
	}

	@Override
	public String caseAOptionalType(AOptionalType node,
			PrettyPrinterEnv question) throws AnalysisException
	{
		return "" + "[" + node.getType().apply(this, question) + "]";
	}

	@Override
	public String caseAUnionType(AUnionType node, PrettyPrinterEnv question)
			throws AnalysisException
	{
		List<String> types = new Vector<String>();
		for (PType t : node.getTypes())
		{
			types.add(t.apply(this, question));
		}
		return ""
				+ ""
				+ (types.size() == 1 ? types.iterator().next().toString()
						: Utils.setToString(types, " | "));
	}

	@Override
	public String defaultSInvariantType(SInvariantType node,
			PrettyPrinterEnv question) throws AnalysisException
	{
		ILexNameToken name = null;
		if (node instanceof ANamedInvariantType)
		{
			name = ((ANamedInvariantType) node).getName();
		} else if (node instanceof ARecordInvariantType)
		{
			name = ((ARecordInvariantType) node).getName();
		}
		if (name != null)
		{
			if (name.getModule() != null
					&& !name.getModule().equals(question.getClassName()))
			{
				return name.getModule() + "`" + name.getFullName();
			}
			return name.getFullName();
		}

		return "unresolved";
	}

	@Override
	public String caseAVoidReturnType(AVoidReturnType node,
			PrettyPrinterEnv question) throws AnalysisException
	{
		return "()";
	}

	@Override
	public String caseAVoidType(AVoidType node, PrettyPrinterEnv question)
			throws AnalysisException
	{
		return "()";
	}

	@Override
	public String createNewReturnValue(INode arg0, PrettyPrinterEnv arg1)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createNewReturnValue(Object arg0, PrettyPrinterEnv arg1)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
