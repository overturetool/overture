package org.overture.typechecker.utilities.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.assistant.type.AUnionTypeAssistant;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.node.INode;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnresolvedType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.Environment;
import org.overture.typechecker.PrivateClassEnvironment;
import org.overture.typechecker.TypeCheckException;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.type.AFieldFieldAssistantTC;
import org.overture.typechecker.assistant.type.AFunctionTypeAssistantTC;
import org.overture.typechecker.assistant.type.ANamedInvariantTypeAssistantTC;
import org.overture.typechecker.assistant.type.AOperationTypeAssistantTC;
import org.overture.typechecker.assistant.type.AProductTypeAssistantTC;
import org.overture.typechecker.assistant.type.ASetTypeAssistantTC;
import org.overture.typechecker.assistant.type.AUnionTypeAssistantTC;
import org.overture.typechecker.assistant.type.AUnresolvedTypeAssistantTC;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;
import org.overture.typechecker.assistant.type.SMapTypeAssistantTC;
import org.overture.typechecker.assistant.type.SSeqTypeAssistantTC;

/**
 * This class implements a way to resolve types from general PType class.
 * 
 * @author kel
 */
public class PTypeResolver extends QuestionAnswerAdaptor<PTypeResolver.Newquestion, PType>
{
	public static class Newquestion
	{
		ATypeDefinition root;
		IQuestionAnswer<TypeCheckInfo, PType> rootVisitor;
		TypeCheckInfo question;
		
		public Newquestion(ATypeDefinition root, 
			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question)
		{
			this.question = question;
			this.root = root;
			this.rootVisitor = rootVisitor;
		}
		
		
	}
	private static final long serialVersionUID = 1L;

	protected ITypeCheckerAssistantFactory af;

	public PTypeResolver(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public PType caseABracketType(ABracketType type, Newquestion question)
			throws AnalysisException
	{
		if (type.getResolved())
			return type;
		else
		{
			type.setResolved(true);
		}

		PType tmp = type;
		try
		{
			do
			{
				tmp = af.createPTypeAssistant().typeResolve(type.getType(), question.root, question.rootVisitor, question.question);
			} while (tmp instanceof ABracketType);

			tmp = af.createPTypeAssistant().typeResolve(tmp, question.root, question.rootVisitor, question.question);

			tmp.parent(type.parent());// re-link tree after bracket removal
			return tmp;
		} catch (TypeCheckException e)
		{
			PTypeAssistantTC.unResolve(type);
			throw e;
		}
	}
	
	@Override
	public PType caseAClassType(AClassType type, Newquestion question)
			throws AnalysisException
	{
		if (type.getResolved())
			return type;
		else
			type.setResolved(true);

		try
		{
			// We have to add a private class environment here because the
			// one passed in may be from a class that contains a reference
			// to this class. We need the private environment to see all
			// the definitions that are available to us while resolving...

			Environment self = new PrivateClassEnvironment(question.question.assistantFactory, type.getClassdef(), question.question.env);

			for (PDefinition d : type.getClassdef().getDefinitions())
			{
				// There is a problem resolving ParameterTypes via a FunctionType
				// when this is not being done via ExplicitFunctionDefinition
				// which extends the environment with the type names that
				// are in scope. So we skip these here.

				if (d instanceof AExplicitFunctionDefinition)
				{
					AExplicitFunctionDefinition fd = (AExplicitFunctionDefinition) d;

					if (fd.getTypeParams() != null)
					{
						continue; // Skip polymorphic functions
					}
				}
				question.question = new TypeCheckInfo(question.question.assistantFactory, self, question.question.scope, question.question.qualifiers);
				af.createPTypeAssistant().typeResolve(question.question.assistantFactory.createPDefinitionAssistant().getType(d), question.root, question.rootVisitor, question.question);
			}

			return type;
		} catch (TypeCheckException e)
		{
			PTypeAssistantTC.unResolve(type);
			throw e;
		}
	}
	
	@Override
	public PType caseAFunctionType(AFunctionType type, Newquestion question)
			throws AnalysisException
	{
		if (type.getResolved())
			return type;
		else
		{
			type.setResolved(true);
		}

		try
		{
			List<PType> fixed = new ArrayList<PType>();

			for (PType ft : type.getParameters())
			{
				fixed.add(af.createPTypeAssistant().typeResolve(ft, question.root, question.rootVisitor, question.question));
			}

			type.setParameters(fixed);
			type.setResult(af.createPTypeAssistant().typeResolve(type.getResult(), question.root, question.rootVisitor, question.question));
			return type;
		} catch (TypeCheckException e)
		{
			AFunctionTypeAssistantTC.unResolve(type);
			throw e;
		}
	}
	@Override
	public PType caseANamedInvariantType(ANamedInvariantType type,
			Newquestion question) throws AnalysisException
	{
		if (type.getResolved())
			return type;
		else
			type.setResolved(true);

		try
		{
			type.setType(af.createPTypeAssistant().typeResolve(type.getType(), question.root, question.rootVisitor, question.question));
			return type;
		} catch (TypeCheckException e)
		{
			ANamedInvariantTypeAssistantTC.unResolve(type);
			throw e;
		}
	}
	@Override
	public PType caseARecordInvariantType(ARecordInvariantType type,
			Newquestion question) throws AnalysisException
	{
		if (type.getResolved())
		{
			return type;
		} else
		{
			type.setResolved(true);
			type.setInfinite(false);
		}

		for (AFieldField f : type.getFields())
		{
			if (question.root != null)
				question.root.setInfinite(false);

			AFieldFieldAssistantTC.typeResolve(f, question.root, question.rootVisitor, question.question);

			if (question.root != null)
				type.setInfinite(type.getInfinite() || question.root.getInfinite());
		}

		if (question.root != null)
			question.root.setInfinite(type.getInfinite());
		return type;
	}
	@Override
	public PType defaultSInvariantType(SInvariantType type, Newquestion question)
			throws AnalysisException
	{
		type.setResolved(true);
		return type;
	}
	@Override
	public PType defaultSMapType(SMapType type, Newquestion question)
			throws AnalysisException
	{
		if (type.getResolved())
			return type;
		else
		{
			type.setResolved(true);
		}

		try
		{
			if (!type.getEmpty())
			{
				type.setFrom(af.createPTypeAssistant().typeResolve(type.getFrom(), question.root, question.rootVisitor, question.question));
				type.setTo(af.createPTypeAssistant().typeResolve(type.getTo(), question.root, question.rootVisitor, question.question));
			}

			return type;
		} catch (TypeCheckException e)
		{
			SMapTypeAssistantTC.unResolve(type);
			throw e;
		}
	}
	
	@Override
	public PType caseAOperationType(AOperationType type, Newquestion question)
			throws AnalysisException
	{
		if (type.getResolved())
			return type;
		else
		{
			type.setResolved(true);
		}

		try
		{
			List<PType> fixed = new ArrayList<PType>();

			for (PType ot : type.getParameters())
			{
				fixed.add(af.createPTypeAssistant().typeResolve(ot, question.root, question.rootVisitor, question.question));
			}

			type.setParameters(fixed);
			type.setResult(af.createPTypeAssistant().typeResolve(type.getResult(), question.root, question.rootVisitor, question.question));
			return type;
		} catch (TypeCheckException e)
		{
			AOperationTypeAssistantTC.unResolve(type);
			throw e;
		}
	}
	
	@Override
	public PType caseAOptionalType(AOptionalType type, Newquestion question)
			throws AnalysisException
	{
		if (type.getResolved())
			return type;
		else
		{
			type.setResolved(true);
		}
		type.setType(af.createPTypeAssistant().typeResolve(type.getType(), question.root, question.rootVisitor, question.question));

		if (question.root != null)
			question.root.setInfinite(false); // Could be nil
		return type;	
	}
	
	@Override
	public PType caseAParameterType(AParameterType type, Newquestion question)
			throws AnalysisException
	{
		if (type.getResolved())
			return type;
		else
			type.setResolved(true);

		PDefinition p = question.question.env.findName(type.getName(), NameScope.NAMES);

		if (p == null
				|| !(question.question.assistantFactory.createPDefinitionAssistant().getType(p) instanceof AParameterType))
		{
			TypeCheckerErrors.report(3433, "Parameter type @" + type.getName()
					+ " not defined", type.getLocation(), type);
		}

		return type;
	}
	
	@Override
	public PType caseAProductType(AProductType type, Newquestion question)
			throws AnalysisException
	{
		if (type.getResolved())
			return type;
		else
		{
			type.setResolved(true);
		}

		try
		{
			List<PType> fixed = new Vector<PType>();

			for (PType t : type.getTypes())
			{
				PType rt = af.createPTypeAssistant().typeResolve(t, question.root, question.rootVisitor, question.question);
				fixed.add(rt);
			}

			type.setTypes(fixed);
			return type;
		} catch (TypeCheckException e)
		{
			AProductTypeAssistantTC.unResolve(type);
			throw e;
		}
	}
	
	@Override
	public PType defaultSSeqType(SSeqType type, Newquestion question)
			throws AnalysisException
	{
		if (type.getResolved())
			return type;
		else
		{
			type.setResolved(true);
		}

		try
		{
			type.setSeqof(af.createPTypeAssistant().typeResolve(type.getSeqof(), question.root, question.rootVisitor, question.question));
			if (question.root != null)
				question.root.setInfinite(false); // Could be empty
			return type;
		} catch (TypeCheckException e)
		{
			SSeqTypeAssistantTC.unResolve(type);
			throw e;
		}
	}
	
	@Override
	public PType caseASetType(ASetType type, Newquestion question)
			throws AnalysisException
	{
		if (type.getResolved())
			return type;
		else
		{
			type.setResolved(true);
		}

		try
		{
			type.setSetof(af.createPTypeAssistant().typeResolve(type.getSetof(), question.root, question.rootVisitor, question.question));
			if (question.root != null)
				question.root.setInfinite(false); // Could be empty
			return type;
		} catch (TypeCheckException e)
		{
			ASetTypeAssistantTC.unResolve(type);
			throw e;
		}
	}
	
	@Override
	public PType caseAUnionType(AUnionType type, Newquestion question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		//return AUnionTypeAssistantTC.typeResolve(type, question.root, question.rootVisitor, question.question);
		if (type.getResolved())
		{
			return type;
		} else
		{
			type.setResolved(true);
			type.setInfinite(true);
		}

		try
		{
			PTypeSet fixed = new PTypeSet();
			for (PType t : type.getTypes())
			{
				if (question.root != null)
					question.root.setInfinite(false);

				fixed.add(af.createPTypeAssistant().typeResolve(t, question.root, question.rootVisitor, question.question));

				if (question.root != null)
					type.setInfinite(type.getInfinite() && question.root.getInfinite());
			}

			type.setTypes(new Vector<PType>(fixed));
			if (question.root != null)
				question.root.setInfinite(type.getInfinite());

			// Resolved types may be unions, so force a re-expand
			type.setExpanded(false);
			AUnionTypeAssistant.expand(type);

			return type;
		} catch (TypeCheckException e)
		{
			AUnionTypeAssistantTC.unResolve(type);
			throw e;
		}
	}
	
	@Override
	public PType caseAUnresolvedType(AUnresolvedType type, Newquestion question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		//return AUnresolvedTypeAssistantTC.typeResolve(type, question.root, question.rootVisitor, question.question);
		PType deref = AUnresolvedTypeAssistantTC.dereference(type, question.question.env, question.root);

		if (!(deref instanceof AClassType))
		{
			deref = af.createPTypeAssistant().typeResolve(deref, question.root, question.rootVisitor, question.question);
		}

		// TODO: return deref.clone()
		return deref;
	}
	
	@Override
	public PType defaultPType(PType type, Newquestion question)
			throws AnalysisException
	{
		type.setResolved(true);
		return type;
	}
//	PType result = null;
//	return result;
	
	@Override
	public PType createNewReturnValue(INode node, Newquestion question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PType createNewReturnValue(Object node, Newquestion question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}
}
