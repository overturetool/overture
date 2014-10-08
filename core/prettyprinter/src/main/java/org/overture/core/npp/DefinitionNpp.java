package org.overture.core.npp;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;

public class DefinitionNpp extends QuestionAnswerAdaptor<IndentTracker, String> implements
		IPrettyPrinter
{
	ISymbolTable mytable;
	IPrettyPrinter rootNpp;

	private static String DEFINITION_NOT_FOUND = "ERROR: definition Node not found";
	private static String space = " ";
	
	@Override
	public void setInsTable(ISymbolTable it)
	{
		mytable = it;
		
	}
	
	public DefinitionNpp(NewPrettyPrinter root, ISymbolTable nst)
	{
		rootNpp = root;
		mytable = nst;
	}
	
	@Override
	public String defaultPDefinition(PDefinition node, IndentTracker question)
			throws AnalysisException
	{
		return node.toString();
	}
	
	@Override
	public String caseAExplicitOperationDefinition(
			AExplicitOperationDefinition node, IndentTracker question)
			throws AnalysisException
	{
		String name = node.getName().toString();
		System.out.print(name);
		String body = node.getBody().toString();
		String precond = rootNpp.defaultPExp(node.getPrecondition(), question);
		String postcond = rootNpp.defaultPExp(node.getPostcondition(), question);
		String args = node.getParamDefinitions().toString();
		
		String access = node.getAccess().toString();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(access);
		sb.append(space);
		sb.append(name);
		sb.append(mytable.getCOLON());
		sb.append(args);
		sb.append("\n");
		sb.append("(");
		sb.append(body);
		sb.append(")");
		sb.append(precond);
		sb.append("\n");
		sb.append(postcond);
		
		return sb.toString();
	}
	
	@Override
	public String caseAImplicitOperationDefinition(
			AImplicitOperationDefinition node, IndentTracker question)
			throws AnalysisException
	{
		String postcond = rootNpp.defaultPExp(node.getPostcondition(), question);
		String precond = rootNpp.defaultPExp(node.getPrecondition(), question);
		System.out.print("mpike kai dw");
		StringBuilder sb  = new StringBuilder();
		
		sb.append(mytable.getPREOP());
		sb.append(space);
		sb.append(precond);
		sb.append("\n");
		sb.append(mytable.getPOSTOP());
		sb.append(space);
		sb.append(postcond);
		
		return sb.toString();
	}
	
	@Override
	public String caseAModuleModules(AModuleModules node, IndentTracker question)
			throws AnalysisException
	{
		String def;
		StringBuilder sb = new StringBuilder();
		
		while (node.getDefs().size() != 0){
			def = node.getDefs().poll().apply(THIS, question);
			sb.append(def);	
		}
		return sb.toString();
	}

	@Override
	public String createNewReturnValue(INode node, IndentTracker question)
			throws AnalysisException
	{
		return DEFINITION_NOT_FOUND;
	}

	@Override
	public String createNewReturnValue(Object node, IndentTracker question)
			throws AnalysisException
	{
		return DEFINITION_NOT_FOUND;
	}

}
