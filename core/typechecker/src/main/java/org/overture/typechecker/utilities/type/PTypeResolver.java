/*
 * #%~
 * The VDM Type Checker
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.typechecker.utilities.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.definitions.ABusClassDefinition;
import org.overture.ast.definitions.ACpuClassDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImportedDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.factory.AstFactory;
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

/**
 * This class implements a way to resolve types from general PType class.
 * 
 * @author kel
 */
public class PTypeResolver extends
		QuestionAnswerAdaptor<PTypeResolver.Newquestion, PType>
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
		{
			return type;
		} else
		{
			type.setResolved(true);
		}

		PType tmp = type;
		try
		{
			do
			{
				tmp = af.createPTypeAssistant().typeResolve(((ABracketType)tmp).getType(), question.root, question.rootVisitor, question.question);
			}
			while (tmp instanceof ABracketType);

			tmp = af.createPTypeAssistant().typeResolve(tmp, question.root, question.rootVisitor, question.question);

			tmp.parent(type.parent());// re-link tree after bracket removal
			return tmp;
		}
		catch (TypeCheckException e)
		{
			af.createPTypeAssistant().unResolve(type);
			throw e;
		}
	}

	@Override
	public PType caseAClassType(AClassType type, Newquestion question)
			throws AnalysisException
	{
		if (type.getResolved())
		{
			return type;
		} else
		{
			type.setResolved(true);
		}

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
			af.createPTypeAssistant().unResolve(type);
			throw e;
		}
	}

	@Override
	public PType caseAFunctionType(AFunctionType type, Newquestion question)
			throws AnalysisException
	{
		if (type.getResolved())
		{
			return type;
		} else
		{
			type.setResolved(true);
		}

		List<PType> fixed = new ArrayList<PType>();
		TypeCheckException problem = null;

		for (PType ft : type.getParameters())
		{
			try
			{
				fixed.add(af.createPTypeAssistant().typeResolve(ft, question.root, question.rootVisitor, question.question));
			}
			catch (TypeCheckException e)
			{
				if (problem == null)
				{
					problem = e;
				}
				else
				{
					problem.addExtra(e);
				}
				
				fixed.add(AstFactory.newAUnknownType(type.getLocation()));
			}
		}

		try
		{
			type.setParameters(fixed);
			type.setResult(af.createPTypeAssistant().typeResolve(type.getResult(), question.root, question.rootVisitor, question.question));
		}
		catch (TypeCheckException e)
		{
			if (problem == null)
			{
				problem = e;
			}
			else
			{
				problem.addExtra(e);
			}
			
			fixed.add(AstFactory.newAUnknownType(type.getLocation()));
		}
		
		if (problem != null)
		{
			type.apply(af.getTypeUnresolver());
			throw problem;
		}

		return type;
	}

	@Override
	public PType caseANamedInvariantType(ANamedInvariantType type,
			Newquestion question) throws AnalysisException
	{
		if (type.getResolved())
		{
			return type;
		} else
		{
			type.setResolved(true);
		}

		try
		{
			type.setType(af.createPTypeAssistant().typeResolve(type.getType(), question.root, question.rootVisitor, question.question));
			return type;
		} catch (TypeCheckException e)
		{
			af.createPTypeAssistant().unResolve(type);
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
		
		TypeCheckException problem = null;

		for (AFieldField f : type.getFields())
		{
			if (question.root != null)
			{
				question.root.setInfinite(false);
			}

			try
			{
				f.apply(THIS, question);
			}
			catch (TypeCheckException e)
			{
				if (problem == null)
				{
					problem = e;
				}
				else
				{
					problem.addExtra(e);
				}
			}

			if (question.root != null)
			{
				type.setInfinite(type.getInfinite()
						|| question.root.getInfinite());
			}
		}
		
		if (problem != null)
		{
			type.apply(af.getTypeUnresolver());
			throw problem;
		}

		if (question.root != null)
		{
			question.root.setInfinite(type.getInfinite());
		}
		
		return type;
	}

	@Override
	public PType caseAFieldField(AFieldField f, Newquestion question)
			throws AnalysisException
	{
		// Recursion defence done by the type
		f.setType(af.createPTypeAssistant().typeResolve(f.getType(), question.root, question.rootVisitor, question.question));

		if (question.question.env.isVDMPP())
		{
			if (f.getType() instanceof AFunctionType)
			{
				f.getTagname().setTypeQualifier(((AFunctionType) f.getType()).getParameters());
			} else if (f.getType() instanceof AOperationType)
			{
				f.getTagname().setTypeQualifier(((AOperationType) f.getType()).getParameters());
			}
		}
		return f.getType();
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
		{
			return type;
		} else
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
			type.apply(af.getTypeUnresolver());
			throw e;
		}
	}

	@Override
	public PType caseAOperationType(AOperationType type, Newquestion question)
			throws AnalysisException
	{
		if (type.getResolved())
		{
			return type;
		} else
		{
			type.setResolved(true);
		}

		List<PType> fixed = new ArrayList<PType>();
		TypeCheckException problem = null;

		for (PType ot : type.getParameters())
		{
			try
			{
				fixed.add(af.createPTypeAssistant().typeResolve(ot, question.root, question.rootVisitor, question.question));
			}
			catch (TypeCheckException e)
			{
				if (problem == null)
				{
					problem = e;
				}
				else
				{
					problem.addExtra(e);
				}
				
				fixed.add(AstFactory.newAUnknownType(type.getLocation()));
			}
		}

		try
		{
			type.setParameters(fixed);
			type.setResult(af.createPTypeAssistant().typeResolve(type.getResult(), question.root, question.rootVisitor, question.question));
		}
		catch (TypeCheckException e)
		{
			if (problem == null)
			{
				problem = e;
			}
			else
			{
				problem.addExtra(e);
			}
			
			fixed.add(AstFactory.newAUnknownType(type.getLocation()));
		}
		
		if (problem != null)
		{
			type.apply(af.getTypeUnresolver());
			throw problem;
		}

		return type;
	}

	@Override
	public PType caseAOptionalType(AOptionalType type, Newquestion question)
			throws AnalysisException
	{
		if (type.getResolved())
		{
			return type;
		} else
		{
			type.setResolved(true);
		}
		type.setType(af.createPTypeAssistant().typeResolve(type.getType(), question.root, question.rootVisitor, question.question));

		if (question.root != null)
		{
			question.root.setInfinite(false); // Could be nil
		}
		return type;
	}

	@Override
	public PType caseAParameterType(AParameterType type, Newquestion question)
			throws AnalysisException
	{
		if (type.getResolved())
		{
			return type;
		} else
		{
			type.setResolved(true);
		}

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
		{
			return type;
		} else
		{
			type.setResolved(true);
		}

		List<PType> fixed = new Vector<PType>();
		TypeCheckException problem = null;

		for (PType t : type.getTypes())
		{
			try
			{
				PType rt = af.createPTypeAssistant().typeResolve(t, question.root, question.rootVisitor, question.question);
				fixed.add(rt);
			}
			catch (TypeCheckException e)
			{
				if (problem == null)
				{
					problem = e;
				}
				else
				{
					problem.addExtra(e);
				}
			}
		}
		
		if (problem != null)
		{
			type.apply(af.getTypeUnresolver());
			throw problem;
		}

		type.setTypes(fixed);
		return type;
	}

	@Override
	public PType defaultSSeqType(SSeqType type, Newquestion question)
			throws AnalysisException
	{
		if (type.getResolved())
		{
			return type;
		} else
		{
			type.setResolved(true);
		}

		try
		{
			type.setSeqof(af.createPTypeAssistant().typeResolve(type.getSeqof(), question.root, question.rootVisitor, question.question));
			if (question.root != null)
			{
				question.root.setInfinite(false); // Could be empty
			}
			return type;
		} catch (TypeCheckException e)
		{
			type.apply(af.getTypeUnresolver());
			throw e;
		}
	}

	@Override
	public PType caseASetType(ASetType type, Newquestion question)
			throws AnalysisException
	{
		if (type.getResolved())
		{
			return type;
		} else
		{
			type.setResolved(true);
		}

		try
		{
			type.setSetof(af.createPTypeAssistant().typeResolve(type.getSetof(), question.root, question.rootVisitor, question.question));
			if (question.root != null)
			{
				question.root.setInfinite(false); // Could be empty
			}
			return type;
		} catch (TypeCheckException e)
		{
			type.apply(af.getTypeUnresolver());
			throw e;
		}
	}

	@Override
	public PType caseAUnionType(AUnionType type, Newquestion question)
			throws AnalysisException
	{
		if (type.getResolved())
		{
			return type;
		} else
		{
			type.setResolved(true);
			type.setInfinite(true);
		}

		PTypeSet fixed = new PTypeSet(af);
		TypeCheckException problem = null;

		for (PType t : type.getTypes())
		{
			if (question.root != null)
			{
				question.root.setInfinite(false);
			}

			try
			{
				fixed.add(af.createPTypeAssistant().typeResolve(t, question.root, question.rootVisitor, question.question));
			}
			catch (TypeCheckException e)
			{
				if (problem == null)
				{
					problem = e;
				}
				else
				{
					problem.addExtra(e);
				}
			}

			if (question.root != null)
			{
				type.setInfinite(type.getInfinite()
						&& question.root.getInfinite());
			}
		}
		
		if (problem != null)
		{
			type.apply(af.getTypeUnresolver());
			throw problem;
		}

		type.setTypes(new Vector<PType>(fixed));
		
		if (question.root != null)
		{
			question.root.setInfinite(type.getInfinite());
		}

		// Resolved types may be unions, so force a re-expand
		type.setExpanded(false);
		af.createAUnionTypeAssistant().expand(type);

		return type;
	}

	@Override
	public PType caseAUnresolvedType(AUnresolvedType type, Newquestion question)
			throws AnalysisException
	{

		PType deref = dereference(type, question.question.env, question.root, question.question.assistantFactory);

		if (!(deref instanceof AClassType))
		{
			deref = af.createPTypeAssistant().typeResolve(deref, question.root, question.rootVisitor, question.question);
		}

		// TODO: return deref.clone()
		return deref;
	}

	private static PType dereference(AUnresolvedType type, Environment env,
			ATypeDefinition root, ITypeCheckerAssistantFactory af)
	{
		PDefinition def = env.findType(type.getName(), type.getLocation().getModule());

		if (def == null)
		{
			throw new TypeCheckException("Unable to resolve type name '"
					+ type.getName() + "'", type.getLocation(), type);
		}

		if (def instanceof AImportedDefinition)
		{
			AImportedDefinition idef = (AImportedDefinition) def;
			def = idef.getDef();
		}

		if (def instanceof ARenamedDefinition)
		{
			ARenamedDefinition rdef = (ARenamedDefinition) def;
			def = rdef.getDef();
		}

		if (!(def instanceof ATypeDefinition)
				&& !(def instanceof AStateDefinition)
				&& !(def instanceof SClassDefinition)
				&& !(def instanceof AInheritedDefinition))
		{
			TypeCheckerErrors.report(3434, "'" + type.getName()
					+ "' is not the name of a type definition", type.getLocation(), type);
		}

		if (def instanceof ATypeDefinition)
		{
			if (def == root)
			{
				root.setInfinite(true);
			}
		}

		if ((def instanceof ACpuClassDefinition || def instanceof ABusClassDefinition)
				&& !env.isSystem())
		{
			TypeCheckerErrors.report(3296, "Cannot use '" + type.getName()
					+ "' outside system class", type.getLocation(), type);
		}

		PType r;
		r = af.createPDefinitionAssistant().getType(def);

		List<PDefinition> tempDefs = new Vector<PDefinition>();
		tempDefs.add(def);
		r.setDefinitions(tempDefs);
		return r;
	}

	// @Override
	// public PType caseATypeBind(ATypeBind type, Newquestion question)
	// throws AnalysisException
	// {
	// type.setType(af.createPTypeAssistant().typeResolve(type.getType(), null, question.rootVisitor,
	// question.question));
	// }

	@Override
	public PType defaultPType(PType type, Newquestion question)
			throws AnalysisException
	{
		type.setResolved(true);
		return type;
	}

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
