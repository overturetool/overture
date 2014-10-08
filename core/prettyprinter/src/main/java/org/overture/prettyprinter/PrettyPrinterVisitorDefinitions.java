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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AUndefinedExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.PStm;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.PType;
import org.overture.ast.util.Utils;

public class PrettyPrinterVisitorDefinitions extends
		QuestionAnswerAdaptor<PrettyPrinterEnv, String>
{
	final static TypePrettyPrinterVisitor typePrinter = new TypePrettyPrinterVisitor();

	@Override
	public String caseAClassClassDefinition(AClassClassDefinition node,
			PrettyPrinterEnv question) throws AnalysisException
	{
		StringBuffer sb = new StringBuffer();
		question.setClassName(node.getName().getName());

		sb.append("class " + node.getName());
		sb.append("\n");

		// print types
		printDefsToStringBuffer(sb, node, question, ATypeDefinition.class);

		printDefsToStringBuffer(sb, node, question, AValueDefinition.class);

		printDefsToStringBuffer(sb, node, question, AInstanceVariableDefinition.class);

		printDefsToStringBuffer(sb, node, question, AExplicitOperationDefinition.class);

		printDefsToStringBuffer(sb, node, question, AExplicitFunctionDefinition.class);

		sb.append("end " + node.getName());
		return sb.toString();
	}

	private void printDefsToStringBuffer(StringBuffer sb,
			AClassClassDefinition node, PrettyPrinterEnv question,
			Class<? extends PDefinition> pDefClass) throws AnalysisException
	{
		List<PDefinition> defs = getDefinitions(node.getDefinitions(), pDefClass);

		if (defs.isEmpty())
		{
			return;
		}

		if (ATypeDefinition.class.equals(pDefClass))
		{
			sb.append("types\n");
			question.increaseIdent();
			for (PDefinition def : defs)
			{
				sb.append(def.apply(this, question));
				sb.append("\n");
			}
			question.decreaseIdent();
		} else if (AValueDefinition.class.equals(pDefClass))
		{
			sb.append("values\n");
			question.increaseIdent();
			for (PDefinition def : defs)
			{
				sb.append(def.apply(this, question));
				sb.append("\n");
			}
			question.decreaseIdent();
		} else if (AInstanceVariableDefinition.class.equals(pDefClass))
		{
			sb.append("instance variables\n");
			question.increaseIdent();
			for (PDefinition def : defs)
			{
				sb.append(def.apply(this, question));
				sb.append("\n");
			}
			question.decreaseIdent();
		} else if (AExplicitOperationDefinition.class.equals(pDefClass))
		{
			sb.append("operations\n");
			question.increaseIdent();
			for (PDefinition def : defs)
			{
				sb.append(def.apply(this, question));
				sb.append("\n");
			}
			question.decreaseIdent();
		} else if (AExplicitFunctionDefinition.class.equals(pDefClass))
		{
			sb.append("functions\n");
			question.increaseIdent();
			for (PDefinition def : defs)
			{
				sb.append(def.apply(this, question));
				sb.append("\n");
			}
			question.decreaseIdent();
		} else
		{
		}
		sb.append("\n");
	}

	private List<PDefinition> getDefinitions(
			LinkedList<PDefinition> definitions,
			Class<? extends PDefinition> pDefClass)
	{
		List<PDefinition> result = new Vector<PDefinition>();

		for (PDefinition pDefinition : definitions)
		{
			if (pDefClass.isInstance(pDefinition))
			{
				result.add(pDefinition);
			}
		}

		return result;
	}

	@Override
	public String caseAInstanceVariableDefinition(
			AInstanceVariableDefinition node, PrettyPrinterEnv question)
			throws AnalysisException
	{
		StringBuilder sb = new StringBuilder(question.getIdent());
		sb.append(node.getName()
				+ ":"
				+ node.getType().apply(typePrinter, question)
				+ (node.getExpression() != null ? " := " + node.getExpression()
						: ""));
		return sb.toString() + ";";
	}

	@Override
	public String caseAValueDefinition(AValueDefinition node,
			PrettyPrinterEnv question) throws AnalysisException
	{
		StringBuilder sb = new StringBuilder(question.getIdent());
		sb.append(node.getPattern()
				+ (node.getType() == null ? "" : ":"
						+ node.getType().apply(typePrinter, question))
				+ (node.getExpression() != null ? " = " + node.getExpression()
						: ""));// node.toString());
		return sb.toString() + ";";
	}

	@Override
	public String caseATypeDefinition(ATypeDefinition node,
			PrettyPrinterEnv question) throws AnalysisException
	{
		StringBuilder sb = new StringBuilder(question.getIdent());

		sb.append(node.getAccess().getAccess() + " ");
		sb.append(node.getName());
		if (node.getType() instanceof ARecordInvariantType)
		{
			ARecordInvariantType record = (ARecordInvariantType) node.getType();
			sb.append(" :: ");
			for (Iterator<AFieldField> itr = record.getFields().iterator(); itr.hasNext();)
			{
				if (itr.hasNext())
				{
					sb.append("\n" + question.getIdent());
				}
				sb.append(itr.next().apply(this, question));

			}
		} else
		{
			sb.append(" = " + node.getType().apply(typePrinter, question));
		}
		// + (node.getType() instanceof ARecordInvariantType ? " :: "
		// : " = ") + node.getType().apply(typePrinter, question));
		return sb.toString() + ";";
	}

	@Override
	public String caseAFieldField(AFieldField node, PrettyPrinterEnv question)
			throws AnalysisException
	{
		question.increaseIdent();
		StringBuilder sb = new StringBuilder(question.getIdent());
		sb.append(node.getTag() + " : "
				+ node.getType().apply(typePrinter, question));
		question.decreaseIdent();
		return sb.toString();
	}

	@Override
	public String defaultPType(PType node, PrettyPrinterEnv question)
			throws AnalysisException
	{
		return node.toString();
	}

	@Override
	public String caseARecordInvariantType(ARecordInvariantType node,
			PrettyPrinterEnv question) throws AnalysisException
	{
		StringBuilder sb = new StringBuilder();
		question.increaseIdent();
		sb.append("\n");
		for (AFieldField f : node.getFields())
		{
			sb.append(question.getIdent() + f.getTag() + " : "
					+ f.getType().apply(typePrinter, question) + "\n");
		}
		question.decreaseIdent();

		if (node.getFields().size() > 0)
		{
			sb.delete(sb.length() - 1, sb.length());
		}
		return sb.toString();
	}

	@Override
	public String caseAExplicitOperationDefinition(
			AExplicitOperationDefinition d, PrettyPrinterEnv question)
			throws AnalysisException
	{
		StringBuilder sb = new StringBuilder(question.getIdent());
		String type = ": ";

		AOperationType optype = (AOperationType) d.getType();

		if (optype.getParameters().isEmpty())
		{
			type += "() ";
		} else
		{
			for (Iterator<PType> iterator = optype.getParameters().iterator(); iterator.hasNext();)
			{
				type += iterator.next().apply(typePrinter, question);
				if (iterator.hasNext())
				{
					type += " * ";
				}

			}
		}

		type += " ==> " + optype.getResult().apply(typePrinter, question);

		String tmp = d.getAccess()
				+ " "
				+ d.getName()
				+ " "
				+ type
				+ "\n"
				+ question.getIdent()
				+ d.getName()
				+ "("
				+ Utils.listToString(d.getParameterPatterns())
				+ ")"
				+ (d.getBody() == null ? "" : " ==\n"
						+ question.increaseIdent() + d.getBody()
						+ question.decreaseIdent().trim())
				+ (d.getPrecondition() == null ? "" : "\n"
						+ question.getIdent() + "pre " + d.getPrecondition())
				+ (d.getPostcondition() == null ? "" : "\n"
						+ question.getIdent() + "post " + d.getPostcondition());
		sb.append(tmp + ";\n");
		return sb.toString();
	}

	@Override
	public String caseAExplicitFunctionDefinition(
			AExplicitFunctionDefinition d, PrettyPrinterEnv question)
			throws AnalysisException
	{
		AFunctionType functype = (AFunctionType) d.getType();

		StringBuilder params = new StringBuilder();

		for (List<PPattern> plist : d.getParamPatternList())
		{
			params.append("(" + Utils.listToString(plist) + ")");
		}

		String accessStr = d.getAccess().toString();
		if (d.getNameScope() == NameScope.LOCAL)
		{
			accessStr = "";
		}

		String type = ": ";
		if (functype.getParameters().isEmpty())
		{
			type += "() ";
		} else
		{
			for (Iterator<PType> iterator = functype.getParameters().iterator(); iterator.hasNext();)
			{
				type += iterator.next().apply(typePrinter, question);
				if (iterator.hasNext())
				{
					type += " * ";
				}

			}
		}

		type += " " + (functype.getPartial() ? "-" : "+") + "> "
				+ functype.getResult().apply(typePrinter, question);

		String tmp = question.getIdent()
				+ accessStr
				+ d.getName().getName()
				+ type
				+ "\n"
				+ question.getIdent()
				+ d.getName().getName()
				+ params
				+ " ==\n"
				+ question.increaseIdent()
				+ d.getBody().apply(this, question)
				+ question.decreaseIdent().trim()
				+ (d.getPrecondition() == null ? "" : "\n"
						+ question.getIdent() + "pre " + d.getPrecondition())
				+ (d.getPostcondition() == null ? "" : "\n"
						+ question.getIdent() + "post " + d.getPostcondition());

		return tmp + ";\n";
	}

	@Override
	public String defaultPStm(PStm node, PrettyPrinterEnv question)
			throws AnalysisException
	{
		return node.toString();
	}

	@Override
	public String defaultPExp(PExp node, PrettyPrinterEnv question)
			throws AnalysisException
	{
		return node.toString();
	}

	@Override
	public String caseAUndefinedExp(AUndefinedExp node,
			PrettyPrinterEnv question) throws AnalysisException
	{
		return "undefined";
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
