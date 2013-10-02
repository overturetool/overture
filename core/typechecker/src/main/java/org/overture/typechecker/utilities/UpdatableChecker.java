package org.overture.typechecker.utilities;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.AExternalDefinition;
import org.overture.ast.definitions.AImportedDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.node.INode;
import org.overture.ast.typechecker.NameScope;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

/**
 * This class implements a way to check if a definition is updatable from a node in the AST
 * 
 * @author kel
 */

public class UpdatableChecker extends AnswerAdaptor<Boolean>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1212903520950027195L;
	
	protected ITypeCheckerAssistantFactory af;

	public UpdatableChecker(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public Boolean caseAAssignmentDefinition(AAssignmentDefinition node)
			throws AnalysisException
	{
		return true;
	}
	
	@Override
	public Boolean caseAInstanceVariableDefinition(
			AInstanceVariableDefinition node) throws AnalysisException
	{
		return true;
	}
	
	@Override
	public Boolean caseAExternalDefinition(AExternalDefinition node)
			throws AnalysisException
	{
		return true;
	}
	
	@Override
	public Boolean caseAImportedDefinition(AImportedDefinition node)
			throws AnalysisException
	{
		return node.getDef().apply(THIS);
	}
	
	@Override
	public Boolean caseAInheritedDefinition(AInheritedDefinition node)
			throws AnalysisException
	{
		return node.getSuperdef().apply(THIS);
	}
	
	@Override
	public Boolean caseALocalDefinition(ALocalDefinition node)
			throws AnalysisException
	{
		//TODO: Here is the same problem with the FunctioChecker
		
		return  node.getNameScope().matches(NameScope.STATE) 
				|| PTypeAssistantTC.isClass(af.createPDefinitionAssistant().getType(node));
	}

	@Override
	public Boolean caseARenamedDefinition(ARenamedDefinition node)
			throws AnalysisException
	{
		return node.getDef().apply(THIS);
	}

	@Override
	public Boolean defaultPDefinition(PDefinition node)
			throws AnalysisException
	{
		return false;
	}

	@Override
	public Boolean createNewReturnValue(INode node)
	{
		assert false : "should not happen";
		return null;
	}

	@Override
	public Boolean createNewReturnValue(Object node)
	{
		assert false : "should not happen";
		return null;
	}

}
