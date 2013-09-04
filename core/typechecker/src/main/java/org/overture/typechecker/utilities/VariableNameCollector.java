package org.overture.typechecker.utilities;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AExternalDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AImportedDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.AMultiBindListDefinition;
import org.overture.ast.definitions.AMutexSyncDefinition;
import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ast.definitions.APerSyncDefinition;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.AThreadDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AUntypedDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.node.INode;
import org.overture.ast.node.IToken;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.definition.AClassInvariantDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AEqualsDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AExplicitFunctionDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AExplicitOperationDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AExternalDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AImplicitFunctionDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AImplicitOperationDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AImportedDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AInheritedDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AInstanceVariableDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.ALocalDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AMultiBindListDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AMutexSyncDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.ANamedTraceDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.APerSyncDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.ARenamedDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AStateDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AThreadDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.ATypeDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AUntypedDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AValueDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.SClassDefinitionAssistantTC;

/**
 * This class implements a way to collect variable names from a node in the AST
 * 
 * @author kel
 */
public class VariableNameCollector extends AnswerAdaptor<LexNameList>
{
	/**
	 * Generated serial version
	 */
	private static final long serialVersionUID = 1L;

	protected ITypeCheckerAssistantFactory af;

	public VariableNameCollector(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public LexNameList caseAAssignmentDefinition(AAssignmentDefinition node)
			throws AnalysisException
	{
		return new LexNameList(node.getName());
	}

	@Override
	public LexNameList defaultSClassDefinition(SClassDefinition node)
			throws AnalysisException
	{
		return SClassDefinitionAssistantTC.getVariableNames((SClassDefinition) node);
	}

	@Override
	public LexNameList caseAClassInvariantDefinition(
			AClassInvariantDefinition node) throws AnalysisException
	{
		return AClassInvariantDefinitionAssistantTC.getVariableNames((AClassInvariantDefinition) node);
	}

	@Override
	public LexNameList caseAEqualsDefinition(AEqualsDefinition node)
			throws AnalysisException
	{
		return AEqualsDefinitionAssistantTC.getVariableNames((AEqualsDefinition) node);
	}

	@Override
	public LexNameList caseAExplicitFunctionDefinition(
			AExplicitFunctionDefinition node) throws AnalysisException
	{
		return AExplicitFunctionDefinitionAssistantTC.getVariableNames((AExplicitFunctionDefinition) node);
	}

	@Override
	public LexNameList caseAExplicitOperationDefinition(
			AExplicitOperationDefinition node) throws AnalysisException
	{
		return AExplicitOperationDefinitionAssistantTC.getVariableNames((AExplicitOperationDefinition) node);
	}

	@Override
	public LexNameList caseAExternalDefinition(AExternalDefinition node)
			throws AnalysisException
	{
		return AExternalDefinitionAssistantTC.getVariableNames((AExternalDefinition) node);
	}

	@Override
	public LexNameList caseAImplicitFunctionDefinition(
			AImplicitFunctionDefinition node) throws AnalysisException
	{
		return AImplicitFunctionDefinitionAssistantTC.getVariableNames((AImplicitFunctionDefinition) node);
	}

	@Override
	public LexNameList caseAImplicitOperationDefinition(
			AImplicitOperationDefinition node) throws AnalysisException
	{
		return AImplicitOperationDefinitionAssistantTC.getVariableNames((AImplicitOperationDefinition) node);
	}

	@Override
	public LexNameList caseAImportedDefinition(AImportedDefinition node)
			throws AnalysisException
	{
		return AImportedDefinitionAssistantTC.getVariableNames((AImportedDefinition) node);
	}

	@Override
	public LexNameList caseAInheritedDefinition(AInheritedDefinition node)
			throws AnalysisException
	{
		return AInheritedDefinitionAssistantTC.getVariableNames((AInheritedDefinition) node);
	}

	@Override
	public LexNameList caseAInstanceVariableDefinition(
			AInstanceVariableDefinition node) throws AnalysisException
	{
		return AInstanceVariableDefinitionAssistantTC.getVariableNames((AInstanceVariableDefinition) node);
	}

	@Override
	public LexNameList caseALocalDefinition(ALocalDefinition node)
			throws AnalysisException
	{
		return ALocalDefinitionAssistantTC.getVariableNames((ALocalDefinition) node);
	}

	@Override
	public LexNameList caseAMultiBindListDefinition(
			AMultiBindListDefinition node) throws AnalysisException
	{
		return AMultiBindListDefinitionAssistantTC.getVariableNames((AMultiBindListDefinition) node);
	}

	@Override
	public LexNameList caseAMutexSyncDefinition(AMutexSyncDefinition node)
			throws AnalysisException
	{
		return AMutexSyncDefinitionAssistantTC.getVariableNames((AMutexSyncDefinition) node);
	}

	@Override
	public LexNameList caseANamedTraceDefinition(ANamedTraceDefinition node)
			throws AnalysisException
	{
		return ANamedTraceDefinitionAssistantTC.getVariableNames((ANamedTraceDefinition) node);
	}

	@Override
	public LexNameList caseAPerSyncDefinition(APerSyncDefinition node)
			throws AnalysisException
	{
		return APerSyncDefinitionAssistantTC.getVariableNames((APerSyncDefinition) node);
	}

	@Override
	public LexNameList caseARenamedDefinition(ARenamedDefinition node)
			throws AnalysisException
	{
		return ARenamedDefinitionAssistantTC.getVariableNames((ARenamedDefinition) node);
	}

	@Override
	public LexNameList caseAStateDefinition(AStateDefinition node)
			throws AnalysisException
	{
		return AStateDefinitionAssistantTC.getVariableNames((AStateDefinition) node);
	}

	@Override
	public LexNameList caseAThreadDefinition(AThreadDefinition node)
			throws AnalysisException
	{
		return AThreadDefinitionAssistantTC.getVariableNames((AThreadDefinition) node);
	}

	@Override
	public LexNameList caseATypeDefinition(ATypeDefinition node)
			throws AnalysisException
	{
		return ATypeDefinitionAssistantTC.getVariableNames((ATypeDefinition) node);
	}

	@Override
	public LexNameList caseAUntypedDefinition(AUntypedDefinition node)
			throws AnalysisException
	{
		return AUntypedDefinitionAssistantTC.getVariableNames((AUntypedDefinition) node);
	}

	@Override
	public LexNameList caseAValueDefinition(AValueDefinition node)
			throws AnalysisException
	{
		return AValueDefinitionAssistantTC.getVariableNames((AValueDefinition) node);
	}

	@Override
	public LexNameList defaultINode(INode node) throws AnalysisException
	{
		assert false : "default case should never happen in getVariableNames";
		return null;
	}

	@Override
	public LexNameList defaultIToken(IToken node) throws AnalysisException
	{
		assert false : "default case should never happen in getVariableNames";
		return null;
	}
}
