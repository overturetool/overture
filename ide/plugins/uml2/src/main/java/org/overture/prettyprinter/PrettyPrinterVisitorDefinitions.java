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
import org.overture.ast.definitions.EDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.PType;
import org.overture.ast.util.Utils;

public class PrettyPrinterVisitorDefinitions extends
		QuestionAnswerAdaptor<PrettyPrinterEnv, String>
{
	// private PrettyPrinterVisitor main;

	public PrettyPrinterVisitorDefinitions(
			PrettyPrinterVisitor prettyPrinterVisitor)
	{
		// main = prettyPrinterVisitor;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5018749137104836194L;

	@Override
	public String caseAClassClassDefinition(AClassClassDefinition node,
			PrettyPrinterEnv question) throws AnalysisException
	{
		StringBuffer sb = new StringBuffer();

		sb.append("class " + node.getName());
		sb.append("\n");

		// print types
		printDefsToStringBuffer(sb, node, question, EDefinition.TYPE);

		printDefsToStringBuffer(sb, node, question, EDefinition.VALUE);

		printDefsToStringBuffer(sb, node, question, EDefinition.INSTANCEVARIABLE);

		printDefsToStringBuffer(sb, node, question, EDefinition.EXPLICITOPERATION);

		printDefsToStringBuffer(sb, node, question, EDefinition.EXPLICITFUNCTION);

		sb.append("end " + node.getName());
		return sb.toString();
	}

	private void printDefsToStringBuffer(StringBuffer sb,
			AClassClassDefinition node, PrettyPrinterEnv question,
			EDefinition kind) throws AnalysisException
	{
		List<PDefinition> defs = getDefinitions(node.getDefinitions(), kind);

		if (defs.isEmpty())
		{
			return;
		}

		switch (kind)
		{
			case TYPE:
			{
				sb.append("types\n");
				question.increaseIdent();
				for (PDefinition def : defs)
				{
					sb.append(def.apply(this, question));
					sb.append("\n");
				}
				question.decreaseIdent();
			}
				break;
			case VALUE:
			{
				sb.append("values\n");
				question.increaseIdent();
				for (PDefinition def : defs)
				{
					sb.append(def.apply(this, question));
					sb.append("\n");
				}
				question.decreaseIdent();
			}
				break;
			case INSTANCEVARIABLE:
			{
				sb.append("instance variables\n");
				question.increaseIdent();
				for (PDefinition def : defs)
				{
					sb.append(def.apply(this, question));
					sb.append("\n");
				}
				question.decreaseIdent();
			}
				break;
			case EXPLICITOPERATION:
			{
				sb.append("operations\n");
				question.increaseIdent();
				for (PDefinition def : defs)
				{
					sb.append(def.apply(this, question));
					sb.append("\n");
				}
				question.decreaseIdent();
			}
				break;
			case EXPLICITFUNCTION:
			{
				sb.append("functions\n");
				question.increaseIdent();
				for (PDefinition def : defs)
				{
					sb.append(def.apply(this, question));
					sb.append("\n");
				}
				question.decreaseIdent();
			}
				break;
			default:
				break;
		}

	}

	private List<PDefinition> getDefinitions(
			LinkedList<PDefinition> definitions, EDefinition kind)
	{
		List<PDefinition> result = new Vector<PDefinition>();

		for (PDefinition pDefinition : definitions)
		{
			if (pDefinition.kindPDefinition() == kind)
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
		sb.append(node.toString());
		return sb.toString() + ";";
	}

	@Override
	public String caseAValueDefinition(AValueDefinition node,
			PrettyPrinterEnv question) throws AnalysisException
	{
		StringBuilder sb = new StringBuilder(question.getIdent());
		sb.append(node.toString());
		return sb.toString() + ";";
	}

	@Override
	public String caseATypeDefinition(ATypeDefinition node,
			PrettyPrinterEnv question) throws AnalysisException
	{
		StringBuilder sb = new StringBuilder(question.getIdent());
		sb.append(node.toString());
		return sb.toString() + ";";
	}

	@Override
	public String caseAExplicitOperationDefinition(
			AExplicitOperationDefinition d, PrettyPrinterEnv question)
			throws AnalysisException
	{
		StringBuilder sb = new StringBuilder(question.getIdent());
		String type = ": ";

		if (d.getType().getParameters().isEmpty())
		{
			type += "() ";
		} else
		{
			for (Iterator<PType> iterator = d.getType().getParameters().iterator(); iterator.hasNext();)
			{
				type += iterator.next();
				if (iterator.hasNext())
				{
					type += " * ";
				}

			}
		}

		type += " ==> " + d.getType().getResult();

		String tmp = d.getName()
				+ " "
				+ type
				+ "\n\t"
				+ d.getName()
				+ "("
				+ Utils.listToString(d.getParameterPatterns())
				+ ")"
				+ (d.getBody() == null ? "" : " ==\n" + d.getBody())
				+ (d.getPrecondition() == null ? "" : "\n\tpre "
						+ d.getPrecondition())
				+ (d.getPostcondition() == null ? "" : "\n\tpost "
						+ d.getPostcondition());
		sb.append(tmp + ";");
		return sb.toString();
	}

	@Override
	public String caseAExplicitFunctionDefinition(
			AExplicitFunctionDefinition d, PrettyPrinterEnv question)
			throws AnalysisException
	{

		StringBuilder params = new StringBuilder();

		for (List<PPattern> plist : d.getParamPatternList())
		{
			params.append("(" + Utils.listToString(plist) + ")");
		}

		String accessStr = d.getAccess().toString();
		if (d.getNameScope() == NameScope.LOCAL)
			accessStr = "";

		String type = ": ";
		if (d.getType().getParameters().isEmpty())
		{
			type += "() ";
		} else
		{
			for (Iterator<PType> iterator = d.getType().getParameters().iterator(); iterator.hasNext();)
			{
				type += iterator.next();
				if (iterator.hasNext())
				{
					type += " * ";
				}

			}
		}

		type += " " + (d.getType().getPartial() ? "-" : "+") + "> "
				+ d.getType().getResult();

		String tmp = accessStr
				+ d.getName().name
				+ type
				+ "\n\t"
				+ d.getName().name
				+ params
				+ " ==\n"
				+ d.getBody()
				+ (d.getPrecondition() == null ? "" : "\n\tpre "
						+ d.getPrecondition())
				+ (d.getPostcondition() == null ? "" : "\n\tpost "
						+ d.getPostcondition());

		return tmp;
	}

}
