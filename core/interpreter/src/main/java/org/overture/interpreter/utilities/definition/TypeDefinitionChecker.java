package org.overture.interpreter.utilities.definition;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.definitions.AImportedDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.node.INode;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

/***************************************
 * This method checks what is the type of Definition.
 * 
 * @author gkanos
 ****************************************/
public class TypeDefinitionChecker extends AnswerAdaptor<Boolean>
{
	protected IInterpreterAssistantFactory af;

	public TypeDefinitionChecker(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public Boolean defaultSClassDefinition(SClassDefinition def)
			throws AnalysisException
	{
		return true;
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
	public Boolean caseATypeDefinition(ATypeDefinition def)
			throws AnalysisException
	{
		return true;
	}

	@Override
	public Boolean defaultPDefinition(PDefinition node)
			throws AnalysisException
	{
		return false;
	}

	@Override
	public Boolean createNewReturnValue(INode node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Boolean createNewReturnValue(Object node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return false;
	}
}
