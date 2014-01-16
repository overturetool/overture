package org.overture.typechecker.utilities;

import java.util.Iterator;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.AMultiBindListDefinition;
import org.overture.ast.definitions.AMutexSyncDefinition;
import org.overture.ast.definitions.AThreadDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.node.INode;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * This class implements a way to check equality of definitions
 * 
 * @author kel
 */
public class DefinitionEqualityChecker extends
		QuestionAnswerAdaptor<Object, Boolean>
{
<<<<<<< HEAD
=======

>>>>>>> origin/pvj/main
	protected ITypeCheckerAssistantFactory af;

	public DefinitionEqualityChecker(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public Boolean caseAEqualsDefinition(AEqualsDefinition node, Object other)
			throws AnalysisException
	{
		if (other instanceof AEqualsDefinition)
		{
			return node.toString().equals(other.toString());
		}

		return false;
	}

	@Override
	public Boolean caseAMultiBindListDefinition(AMultiBindListDefinition node,
			Object other) throws AnalysisException
	{
		if (other instanceof AMultiBindListDefinition)
		{
			return node.toString().equals(other.toString());
		}

		return false;
	}

	@Override
	public Boolean caseAMutexSyncDefinition(AMutexSyncDefinition node,
			Object other) throws AnalysisException
	{
		if (other instanceof AMutexSyncDefinition)
		{
			return node.toString().equals(other.toString());
		}

		return false;
	}

	@Override
	public Boolean caseAThreadDefinition(AThreadDefinition node, Object other)
			throws AnalysisException
	{
		if (other instanceof AThreadDefinition)
		{
			AThreadDefinition tho = (AThreadDefinition) other;
			return tho.getOperationName().equals(node.getOperationName());
		}

		return false;
	}

	@Override
	public Boolean caseAValueDefinition(AValueDefinition node, Object other)
			throws AnalysisException
	{
		if (other instanceof AValueDefinition)
		{
			AValueDefinition vdo = (AValueDefinition) other;

			if (node.getDefs().size() == vdo.getDefs().size())
			{
				Iterator<PDefinition> diter = vdo.getDefs().iterator();

				for (PDefinition d : node.getDefs())
				{
					if (!af.createPDefinitionAssistant().equals(diter.next(), d))
					{
						return false;
					}
				}

				return true;
			}
		}

		return false;
	}

	@Override
	public Boolean defaultPDefinition(PDefinition node, Object other)
			throws AnalysisException
	{
		return equalsBaseCase(node, other);
	}

	private boolean equalsBaseCase(PDefinition def, Object other) // Used for sets of definitions.
	{
		if (other instanceof PDefinition)
		{
			PDefinition odef = (PDefinition) other;
			return def.getName() != null && odef.getName() != null
					&& def.getName().equals(odef.getName());
		}
		return false;
	}

	@Override
	public Boolean createNewReturnValue(INode node, Object question)
	{
		return false;
	}

	@Override
	public Boolean createNewReturnValue(Object node, Object question)
	{
		return false;
	}
}
