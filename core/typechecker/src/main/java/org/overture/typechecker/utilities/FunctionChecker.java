package org.overture.typechecker.utilities;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImportedDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.types.AParameterType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;


/**
 * This class implements a way to check if a node is a function.
 * 
 * @author kel
 */
public class FunctionChecker extends AnswerAdaptor<Boolean>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ITypeCheckerAssistantFactory af;

	public FunctionChecker(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public Boolean caseAExplicitFunctionDefinition(
			AExplicitFunctionDefinition node) throws AnalysisException
	{
		return true;
	}
	
	@Override
	public Boolean caseAImplicitFunctionDefinition(
			AImplicitFunctionDefinition node) throws AnalysisException
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
		//TODO: HERE QUESTION ABOUY HANDLING THIS CODE!
		return (node.getValueDefinition() || PTypeAssistantTC.isType(af.createPDefinitionAssistant().getType(node), AParameterType.class)) ? false
		:PTypeAssistantTC.isFunction(af.createPDefinitionAssistant().getType(node)); 
		//af.createPTypeAssistant().isFunction(af.createPDefinitionAssistant().getType(node)); 
		//Non-static call works correctly but it give a warning for the static method.
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
}
