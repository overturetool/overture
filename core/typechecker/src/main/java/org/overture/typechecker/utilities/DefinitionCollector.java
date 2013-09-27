package org.overture.typechecker.utilities;

import java.util.List;
import java.util.Vector;

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
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.node.INode;
import org.overture.ast.node.IToken;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.definition.PDefinitionListAssistantTC;

/**
 * This class implements a way to collect definitions from a node in the AST
 * 
 * @author kel
 */
public class DefinitionCollector extends AnswerAdaptor<List<PDefinition>>
{

	/**
	 * Generated serial version
	 */
	private static final long serialVersionUID = 1L;

	protected ITypeCheckerAssistantFactory af;

	public DefinitionCollector(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public List<PDefinition> caseAAssignmentDefinition(
			AAssignmentDefinition node) throws AnalysisException
	{
		List<PDefinition> res = new Vector<PDefinition>();
		res.add(node);
		return res;
	}

	@Override
	public List<PDefinition> defaultSClassDefinition(SClassDefinition node)
			throws AnalysisException
	{
		List<PDefinition> all = new Vector<PDefinition>();

		all.addAll(node.getAllInheritedDefinitions());
		all.addAll(PDefinitionListAssistantTC.singleDefinitions(node.getDefinitions()));

		return all;
	}

	@Override
	public List<PDefinition> caseAClassInvariantDefinition(
			AClassInvariantDefinition node) throws AnalysisException
	{
		return new Vector<PDefinition>();
	}

	@Override
	public List<PDefinition> caseAEqualsDefinition(AEqualsDefinition node)
			throws AnalysisException
	{
		return node.getDefs() == null ? new Vector<PDefinition>()
				: node.getDefs();
	}

	@Override
	public List<PDefinition> caseAExplicitFunctionDefinition(
			AExplicitFunctionDefinition node) throws AnalysisException
	{
		List<PDefinition> defs = new Vector<PDefinition>();
		defs.add(node);

		if (node.getPredef() != null)
		{
			defs.add(node.getPredef());
		}

		if (node.getPostdef() != null)
		{
			defs.add(node.getPostdef());
		}

		return defs;
	}

	@Override
	public List<PDefinition> caseAExplicitOperationDefinition(
			AExplicitOperationDefinition node) throws AnalysisException
	{
		List<PDefinition> defs = new Vector<PDefinition>();
		defs.add(node);

		if (node.getPredef() != null)
		{
			defs.add(node.getPredef());
		}

		if (node.getPostdef() != null)
		{
			defs.add(node.getPostdef());
		}

		return defs;
	}

	@Override
	public List<PDefinition> caseAExternalDefinition(AExternalDefinition node)
			throws AnalysisException
	{
		List<PDefinition> result = new Vector<PDefinition>();
		result.add(node.getState());

		return result;
	}

	@Override
	public List<PDefinition> caseAImplicitFunctionDefinition(
			AImplicitFunctionDefinition node) throws AnalysisException
	{
		List<PDefinition> defs = new Vector<PDefinition>();
		defs.add(node);

		if (node.getPredef() != null)
		{
			defs.add(node.getPredef());
		}

		if (node.getPostdef() != null)
		{
			defs.add(node.getPostdef());
		}

		return defs;
	}

	@Override
	public List<PDefinition> caseAImplicitOperationDefinition(
			AImplicitOperationDefinition node) throws AnalysisException
	{
		List<PDefinition> defs = new Vector<PDefinition>();
		defs.add(node);

		if (node.getPredef() != null)
		{
			defs.add(node.getPredef());
		}

		if (node.getPostdef() != null)
		{
			defs.add(node.getPostdef());
		}

		return defs;
	}

	@Override
	public List<PDefinition> caseAImportedDefinition(AImportedDefinition node)
			throws AnalysisException
	{
		List<PDefinition> result = new Vector<PDefinition>();
		result.add(node.getDef());
		return result;
	}

	@Override
	public List<PDefinition> caseAInheritedDefinition(AInheritedDefinition node)
			throws AnalysisException
	{
		return node.getSuperdef().apply(THIS);
	}

	@Override
	public List<PDefinition> caseAInstanceVariableDefinition(
			AInstanceVariableDefinition node) throws AnalysisException
	{
		List<PDefinition> res = new Vector<PDefinition>();
		res.add(node);
		return res;
	}

	@Override
	public List<PDefinition> caseALocalDefinition(ALocalDefinition node)
			throws AnalysisException
	{
		List<PDefinition> res = new Vector<PDefinition>();
		res.add(node);
		return res;
	}

	@Override
	public List<PDefinition> caseAMultiBindListDefinition(
			AMultiBindListDefinition node) throws AnalysisException
	{
		return node.getDefs() == null ? new Vector<PDefinition>()
				: node.getDefs();
	}

	@Override
	public List<PDefinition> caseAMutexSyncDefinition(AMutexSyncDefinition node)
			throws AnalysisException
	{
		return new Vector<PDefinition>();
	}

	@Override
	public List<PDefinition> caseANamedTraceDefinition(
			ANamedTraceDefinition node) throws AnalysisException
	{
		List<PDefinition> result = new Vector<PDefinition>();
		result.add(node);
		return result;
	}

	@Override
	public List<PDefinition> caseAPerSyncDefinition(APerSyncDefinition node)
			throws AnalysisException
	{
		List<PDefinition> result = new Vector<PDefinition>();
		result.add(node);
		return result;
	}

	@Override
	public List<PDefinition> caseARenamedDefinition(ARenamedDefinition node)
			throws AnalysisException
	{
		List<PDefinition> result = new Vector<PDefinition>();
		result.add(node);
		return result;
	}

	@Override
	public List<PDefinition> caseAStateDefinition(AStateDefinition node)
			throws AnalysisException
	{
		return node.getStateDefs();
	}

	@Override
	public List<PDefinition> caseAThreadDefinition(AThreadDefinition node)
			throws AnalysisException
	{
		
		List<PDefinition> result = new Vector<PDefinition>();
		result.add(node.getOperationDef());
		return result;
	}

	@Override
	public List<PDefinition> caseATypeDefinition(ATypeDefinition node)
			throws AnalysisException
	{
		
		List<PDefinition> defs = new Vector<PDefinition>();
		defs.add(node);

		if (node.getInvdef() != null)
		{
			defs.add(node.getInvdef());
		}

		return defs;
	}

	@Override
	public List<PDefinition> caseAUntypedDefinition(AUntypedDefinition node)
			throws AnalysisException
	{
	
		List<PDefinition> result = new Vector<PDefinition>();
		result.add(node);
		return result;
	}

	@Override
	public List<PDefinition> caseAValueDefinition(AValueDefinition node)
			throws AnalysisException
	{
		return node.getDefs();
	}

	@Override
	public List<PDefinition> defaultINode(INode node) throws AnalysisException
	{
		assert false : "getDefinitions should never hit the default case";
		return null;
	}

	@Override
	public List<PDefinition> defaultIToken(IToken node)
			throws AnalysisException
	{
		assert false : "getDefinitions should never hit the default case";
		return null;
	}
}
