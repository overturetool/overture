package org.overture.typechecker.utilities;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.definitions.AImportedDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.node.INode;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/***************************************
 * This method checks if a definition is an instance variable.
 * 
 * @author gkanos
 ****************************************/
public class PureDefinitionChecker extends AnswerAdaptor<Boolean>
{
	protected ITypeCheckerAssistantFactory af;

	public PureDefinitionChecker(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public Boolean caseAImportedDefinition(AImportedDefinition def)
			throws AnalysisException
	{
		return def.getDef().apply(THIS);
	}

	@Override
	public Boolean caseAInheritedDefinition(AInheritedDefinition def)
			throws AnalysisException
	{
		return def.getSuperdef().apply(THIS);
	}

	@Override
	public Boolean caseARenamedDefinition(ARenamedDefinition def)
			throws AnalysisException
	{
		return def.getDef().apply(THIS);
	}

	@Override
	public Boolean caseAInstanceVariableDefinition(
			AInstanceVariableDefinition def) throws AnalysisException
	{
		return true;
	}

	@Override
	public Boolean defaultPDefinition(PDefinition def) throws AnalysisException
	{
		return false;
	}

	@Override
	public Boolean createNewReturnValue(INode node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean createNewReturnValue(Object node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}
}
