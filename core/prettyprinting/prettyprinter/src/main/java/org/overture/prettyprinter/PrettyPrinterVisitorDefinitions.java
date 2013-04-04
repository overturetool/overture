package org.overture.prettyprinter;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.EDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.prettyprinter.PrettyPrinterEnv;

public class PrettyPrinterVisitorDefinitions extends
		QuestionAnswerAdaptor<PrettyPrinterEnv,String>
{

	
	
//	private PrettyPrinterVisitor main;

	public PrettyPrinterVisitorDefinitions(
			PrettyPrinterVisitor prettyPrinterVisitor)
	{
//		main = prettyPrinterVisitor;
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
		
		sb.append("class " + node.getName()); sb.append("\n");
		
		
		//print types
		printDefsToStringBuffer(sb,node,question,ATypeDefinition.kindPDefinition);
		
		printDefsToStringBuffer(sb,node,question,AValueDefinition.kindPDefinition);
		
		
		
		
		sb.append("end " + node.getName()); 
		return sb.toString();
	}
	
	
	
	private void printDefsToStringBuffer(StringBuffer sb,
			AClassClassDefinition node, PrettyPrinterEnv question, String kind) throws AnalysisException
	{
		List<PDefinition> defs = getDefinitions(node.getDefinitions(), kind);
		
		if(defs.isEmpty())
		{
			return;
		}
		
		switch (kind)
		{		
			case ATypeDefinition.kindPDefinition:
			{
				sb.append("types\n");
				question.increaseIdent();
				for (PDefinition def : defs)
				{
					sb.append(def.apply(this,question));
					sb.append("\n");
				}
				question.decreaseIdent();
			}
			break;
			case AValueDefinition.kindPDefinition:
			{
				sb.append("values\n");
				question.increaseIdent();
				for (PDefinition def : defs)
				{
					sb.append(def.apply(this,question));
					sb.append("\n");
				}
				question.decreaseIdent();
			}
			break;
			default:
				break;
		}
		
	}



	private List<PDefinition> getDefinitions(LinkedList<PDefinition> definitions, String kind)	
	{
		List<PDefinition> result = new Vector<PDefinition>();
		
		for (PDefinition pDefinition : definitions)
		{
			if(pDefinition.kindPDefinition() == kind)
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
		return sb.toString();
	}
	
	@Override
	public String caseAValueDefinition(AValueDefinition node,
			PrettyPrinterEnv question) throws AnalysisException
	{
		StringBuilder sb = new StringBuilder(question.getIdent());
		sb.append(node.toString());
		return sb.toString();
	}
	
	@Override
	public String caseATypeDefinition(ATypeDefinition node,
			PrettyPrinterEnv question) throws AnalysisException
	{
		StringBuilder sb = new StringBuilder(question.getIdent());
		sb.append(node.toString());
		return sb.toString();
	}
	
}
