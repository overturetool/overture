package org.overture.interpreter.utilities.definition;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.definitions.AImportedDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.node.INode;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

/***************************************
 * This method checks if a definition is a value.
 * 
 * @author gkanos
 ****************************************/

public class DefinitionValueChecker extends AnswerAdaptor<Boolean>
{
	protected IInterpreterAssistantFactory af;

	public DefinitionValueChecker(IInterpreterAssistantFactory af)
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
	public Boolean caseAValueDefinition(AValueDefinition def)
			throws AnalysisException
	{
		return true;
	}

	@Override
	public Boolean caseALocalDefinition(ALocalDefinition def)
			throws AnalysisException
	{
		return def.getValueDefinition() != null;
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
