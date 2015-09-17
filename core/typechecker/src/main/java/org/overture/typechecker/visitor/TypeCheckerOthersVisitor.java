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
package org.overture.typechecker.visitor;

import java.util.LinkedList;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.AExternalDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexIdentifierToken;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.node.Node;
import org.overture.ast.patterns.ADefPatternBind;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.statements.AApplyObjectDesignator;
import org.overture.ast.statements.AFieldObjectDesignator;
import org.overture.ast.statements.AFieldStateDesignator;
import org.overture.ast.statements.AForPatternBindStm;
import org.overture.ast.statements.AIdentifierObjectDesignator;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.ast.statements.AMapSeqStateDesignator;
import org.overture.ast.statements.ANewObjectDesignator;
import org.overture.ast.statements.ASelfObjectDesignator;
import org.overture.ast.statements.ATixeStmtAlternative;
import org.overture.ast.statements.ATrapStm;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.Environment;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeCheckerErrors;

public class TypeCheckerOthersVisitor extends AbstractTypeCheckVisitor
{

	public TypeCheckerOthersVisitor(
			IQuestionAnswer<TypeCheckInfo, PType> typeCheckVisitor)
	{
		super(typeCheckVisitor);
	}

	@Override
	public PType caseADefPatternBind(ADefPatternBind node,
			TypeCheckInfo question) throws AnalysisException
	{
		node.setDefs(null);

		PType type = null;

		Node parent = node.getAncestor(AForPatternBindStm.class);
		if (parent != null)
		{
			type = ((AForPatternBindStm) parent).getSeqType().getSeqof();
		} else if ((parent = node.getAncestor(ATrapStm.class)) != null)
		{
			type = ((ATrapStm) parent).getType();
		} else if ((parent = node.getAncestor(ATixeStmtAlternative.class)) != null)
		{
			type = ((ATixeStmtAlternative) parent).getExp();
		}

		if (node.getBind() != null)
		{
			if (node.getBind() instanceof ATypeBind)
			{
				ATypeBind typebind = (ATypeBind) node.getBind();
				typebind.setType(question.assistantFactory.createPTypeAssistant().typeResolve(typebind.getType(), null, THIS, question));

				// resolve pattern such that it is resolved before it is cloned later in newAMultiBindListDefinition
				if (node.getBind().getPattern() != null)
				{
					question.assistantFactory.createPPatternAssistant().typeResolve(node.getBind().getPattern(), THIS, question);
				}

				if (!question.assistantFactory.getTypeComparator().compatible(typebind.getType(), type))
				{
					TypeCheckerErrors.report(3198, "Type bind not compatible with expression", node.getBind().getLocation(), node.getBind());
					TypeCheckerErrors.detail2("Bind", typebind.getType(), "Exp", type);
				}
			} else
			{
				ASetBind setbind = (ASetBind) node.getBind();
				ASetType settype = question.assistantFactory.createPTypeAssistant().getSet(setbind.getSet().apply(THIS, question));
				if (!question.assistantFactory.getTypeComparator().compatible(type, settype.getSetof()))
				{
					TypeCheckerErrors.report(3199, "Set bind not compatible with expression", node.getBind().getLocation(), node.getBind());
					TypeCheckerErrors.detail2("Bind", settype.getSetof(), "Exp", type);
				}
			}

			PDefinition def = AstFactory.newAMultiBindListDefinition(node.getBind().getLocation(), question.assistantFactory.createPBindAssistant().getMultipleBindList(node.getBind()));

			def.apply(THIS, question);
			LinkedList<PDefinition> defs = new LinkedList<PDefinition>();
			defs.add(def);
			node.setDefs(defs);
		} else
		{
			assert type != null : "Can't typecheck a pattern without a type";

			question.assistantFactory.createPPatternAssistant().typeResolve(node.getPattern(), THIS, question);
			node.setDefs(question.assistantFactory.createPPatternAssistant().getDefinitions(node.getPattern(), type, NameScope.LOCAL));
		}

		return null;
	}

	@Override
	public PType caseAFieldStateDesignator(AFieldStateDesignator node,
			TypeCheckInfo question) throws AnalysisException
	{

		PType type = node.getObject().apply(THIS, question);
		PTypeSet result = new PTypeSet(question.assistantFactory);
		boolean unique = !question.assistantFactory.createPTypeAssistant().isUnion(type);
		ILexIdentifierToken field = node.getField();

		if (question.assistantFactory.createPTypeAssistant().isRecord(type))
		{

			ARecordInvariantType rec = question.assistantFactory.createPTypeAssistant().getRecord(type);
			AFieldField rf = question.assistantFactory.createARecordInvariantTypeAssistant().findField(rec, field.getName());

			if (rf == null)
			{
				TypeCheckerErrors.concern(unique, 3246, "Unknown field name, '"
						+ field + "'", node.getLocation(), field);
				result.add(AstFactory.newAUnknownType(field.getLocation()));
			} else
			{
				result.add(rf.getType());
			}
		}

		if (question.assistantFactory.createPTypeAssistant().isClass(type, question.env))
		{
			AClassType ctype = question.assistantFactory.createPTypeAssistant().getClassType(type, question.env);
			String cname = ctype.getName().getName();

			node.setObjectfield(new LexNameToken(cname, field.getName(), node.getObject().getLocation()));
			PDefinition fdef = question.assistantFactory.createPDefinitionAssistant().findName(ctype.getClassdef(), node.getObjectfield(), NameScope.STATE);
			// SClassDefinitionAssistantTC.findName(ctype.getClassdef(), node.getObjectfield(), NameScope.STATE);

			if (fdef == null)
			{
				TypeCheckerErrors.concern(unique, 3260, "Unknown class field name, '"
						+ field + "'", field.getLocation(), field);
				result.add(AstFactory.newAUnknownType(node.getLocation()));

			} else if (question.assistantFactory.createSClassDefinitionAssistant().isAccessible(question.env, fdef, false))
			{

				result.add(fdef.getType());

			} else
			{

				TypeCheckerErrors.concern(unique, 3092, "Inaccessible member "
						+ field.getName() + " of class " + cname, field.getLocation(), field);
				result.add(AstFactory.newAUnknownType(node.getLocation()));
			}
		}

		if (result.isEmpty())
		{
			TypeCheckerErrors.report(3245, "Field assignment is not of a record or object type", node.getLocation(), node);
			TypeCheckerErrors.detail2("Expression", node.getObject(), "Type", type);
			node.setType(AstFactory.newAUnknownType(field.getLocation()));
			return node.getType();
		}

		node.setType(result.getType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseAIdentifierStateDesignator(
			AIdentifierStateDesignator node, TypeCheckInfo question)
	{
		Environment env = question.env;
		PDefinition encl = env.getEnclosingDefinition();

		if (env.isVDMPP())
		{
			// We generate an explicit name because accessing a variable
			// by name in VDM++ does not "inherit" values from a superclass.

			ILexNameToken name = node.getName();
			ILexNameToken exname = name.getExplicit(true);
			PDefinition def = env.findName(exname, NameScope.STATE);

			if (def == null)
			{

				Set<PDefinition> matches = env.findMatches(exname);

				if (!matches.isEmpty())
				{
					PDefinition match = matches.iterator().next(); // Just take first

					if (question.assistantFactory.createPDefinitionAssistant().isFunction(match))
					{
						TypeCheckerErrors.report(3247, "Function apply not allowed in state designator", name.getLocation(), name);
					} else
					{
						TypeCheckerErrors.report(3247, "Operation call not allowed in state designator", name.getLocation(), name);
					}

					node.setType(match.getType());
					return node.getType();
				} else
				{
					TypeCheckerErrors.report(3247, "Symbol '" + name
							+ "' is not an updatable variable", name.getLocation(), name);
				}

				node.setType(AstFactory.newAUnknownType(name.getLocation()));
				return node.getType();
			} else if (!question.assistantFactory.createPDefinitionAssistant().isUpdatable(def))
			{
				TypeCheckerErrors.report(3301, "Variable '" + name
						+ "' in scope is not updatable", name.getLocation(), name);
				node.setType(AstFactory.newAUnknownType(name.getLocation()));
				return node.getType();
			} else if (encl != null &&
				encl.getAccess().getPure() &&
				question.assistantFactory.createPDefinitionAssistant().isInstanceVariable(def))
			{
					TypeCheckerErrors.report(3338, "Cannot update state in a pure operation", name.getLocation(), name);
			}
			else if (def.getClassDefinition() != null)
			{
				if (!question.assistantFactory.createSClassDefinitionAssistant().isAccessible(env, def, true))
				{
					TypeCheckerErrors.report(3180, "Inaccessible member '"
							+ name + "' of class "
							+ def.getClassDefinition().getName().getName(), name.getLocation(), name);
					node.setType(AstFactory.newAUnknownType(name.getLocation()));
					return node.getType();
				} else if (!question.assistantFactory.createPDefinitionAssistant().isStatic(def)
						&& env.isStatic())
				{
					TypeCheckerErrors.report(3181, "Cannot access " + name
							+ " from a static context", name.getLocation(), name);
					node.setType(AstFactory.newAUnknownType(name.getLocation()));
					return node.getType();
				}
			} else if (def instanceof AExternalDefinition)
			{
				AExternalDefinition d = (AExternalDefinition) def;

				if (d.getReadOnly())
				{
					TypeCheckerErrors.report(3248, "Cannot assign to 'ext rd' state "
							+ name, name.getLocation(), name);
				}
			}
			// else just state access in (say) an explicit operation

			node.setType(question.assistantFactory.createPDefinitionAssistant().getType(def));
			return node.getType();
		} else
		{
			ILexNameToken name = node.getName();
			PDefinition def = env.findName(name, NameScope.STATE);

			if (def == null)
			{
				TypeCheckerErrors.report(3247, "Unknown state variable '"
						+ name + "' in assignment", name.getLocation(), name);
				node.setType(AstFactory.newAUnknownType(name.getLocation()));
				return node.getType();
			} else if (question.assistantFactory.createPDefinitionAssistant().isFunction(def))
			{
				TypeCheckerErrors.report(3247, "Function apply not allowed in state designator", name.getLocation(), name);
				node.setType(AstFactory.newAUnknownType(name.getLocation()));
				return node.getType();
			} else if (question.assistantFactory.createPDefinitionAssistant().isOperation(def))
			{
				TypeCheckerErrors.report(3247, "Operation call not allowed in state designator", name.getLocation(), name);
				node.setType(AstFactory.newAUnknownType(name.getLocation()));
				return node.getType();
			} else if (!question.assistantFactory.createPDefinitionAssistant().isUpdatable(def))
			{
				TypeCheckerErrors.report(3301, "Variable '" + name
						+ "' in scope is not updatable", name.getLocation(), name);
				node.setType(AstFactory.newAUnknownType(name.getLocation()));
				return node.getType();
			}
			else if (encl != null && encl.getAccess().getPure() && !(def instanceof AAssignmentDefinition))
			{
				TypeCheckerErrors.report(3338, "Cannot update state in a pure operation", name.getLocation(), name);
			}
			else if (def instanceof AExternalDefinition)
			{
				AExternalDefinition d = (AExternalDefinition) def;

				if (d.getReadOnly())
				{
					TypeCheckerErrors.report(3248, "Cannot assign to 'ext rd' state "
							+ name, name.getLocation(), name);
				}
			}
			// else just state access in (say) an explicit operation

			node.setType(question.assistantFactory.createPDefinitionAssistant().getType(def));
			return node.getType();
		}
	}

	@Override
	public PType caseAMapSeqStateDesignator(AMapSeqStateDesignator node,
			TypeCheckInfo question) throws AnalysisException
	{
		PType etype = node.getExp().apply(THIS, new TypeCheckInfo(question.assistantFactory, question.env, NameScope.NAMESANDSTATE));
		PType rtype = node.getMapseq().apply(THIS, new TypeCheckInfo(question.assistantFactory, question.env));
		PTypeSet result = new PTypeSet(question.assistantFactory);

		if (question.assistantFactory.createPTypeAssistant().isMap(rtype))
		{
			node.setMapType(question.assistantFactory.createPTypeAssistant().getMap(rtype));

			if (!question.assistantFactory.getTypeComparator().compatible(node.getMapType().getFrom(), etype))
			{
				TypeCheckerErrors.report(3242, "Map element assignment of wrong type", node.getLocation(), node);
				TypeCheckerErrors.detail2("Expect", node.getMapType().getFrom(), "Actual", etype);
			} else
			{
				result.add(node.getMapType().getTo());
			}
		}

		if (question.assistantFactory.createPTypeAssistant().isSeq(rtype))
		{
			node.setSeqType(question.assistantFactory.createPTypeAssistant().getSeq(rtype));

			if (!question.assistantFactory.createPTypeAssistant().isNumeric(etype))
			{
				TypeCheckerErrors.report(3243, "Seq index is not numeric", node.getLocation(), node);
				TypeCheckerErrors.detail("Actual", etype);
			} else
			{
				result.add(node.getSeqType().getSeqof());
			}
		}

		if (question.assistantFactory.createPTypeAssistant().isFunction(rtype))
		{
			// Error case, but improves errors if we work out the return type
			AFunctionType ftype = question.assistantFactory.createPTypeAssistant().getFunction(rtype);
			result.add(ftype.getResult());
		}

		if (question.assistantFactory.createPTypeAssistant().isOperation(rtype))
		{
			// Error case, but improves errors if we work out the return type
			AOperationType otype = question.assistantFactory.createPTypeAssistant().getOperation(rtype);
			result.add(otype.getResult());
		}

		if (result.isEmpty())
		{
			TypeCheckerErrors.report(3244, "Expecting a map or a sequence", node.getLocation(), node);
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}

		node.setType(result.getType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseASelfObjectDesignator(ASelfObjectDesignator node,
			TypeCheckInfo question)
	{
		PDefinition def = question.env.findName(node.getSelf(), NameScope.NAMES);

		if (def == null)
		{
			TypeCheckerErrors.report(3263, "Cannot reference 'self' from here", node.getSelf().getLocation(), node.getSelf());
			return AstFactory.newAUnknownType(node.getSelf().getLocation());
		}

		return question.assistantFactory.createPDefinitionAssistant().getType(def);
	}

	@Override
	public PType caseAApplyObjectDesignator(AApplyObjectDesignator node,
			TypeCheckInfo question) throws AnalysisException
	{
		LinkedList<PType> argtypes = new LinkedList<PType>();

		for (PExp a : node.getArgs())
		{
			argtypes.add(a.apply(THIS, new TypeCheckInfo(question.assistantFactory, question.env, NameScope.NAMESANDSTATE)));
		}

		PType type = node.getObject().apply(THIS, new TypeCheckInfo(question.assistantFactory, question.env, null, argtypes));
		boolean unique = !question.assistantFactory.createPTypeAssistant().isUnion(type);
		PTypeSet result = new PTypeSet(question.assistantFactory);

		if (question.assistantFactory.createPTypeAssistant().isMap(type))
		{
			SMapType map = question.assistantFactory.createPTypeAssistant().getMap(type);
			result.add(mapApply(node, map, question.env, NameScope.NAMESANDSTATE, unique, THIS));
		}

		if (question.assistantFactory.createPTypeAssistant().isSeq(type))
		{
			SSeqType seq = question.assistantFactory.createPTypeAssistant().getSeq(type);
			result.add(seqApply(node, seq, question.env, NameScope.NAMESANDSTATE, unique, THIS, question));
		}

		if (question.assistantFactory.createPTypeAssistant().isFunction(type))
		{
			AFunctionType ft = question.assistantFactory.createPTypeAssistant().getFunction(type);
			question.assistantFactory.createPTypeAssistant().typeResolve(ft, null, THIS, new TypeCheckInfo(question.assistantFactory, question.env));
			result.add(functionApply(node, ft, question.env, NameScope.NAMESANDSTATE, unique, THIS));
		}

		if (question.assistantFactory.createPTypeAssistant().isOperation(type))
		{
			AOperationType ot = question.assistantFactory.createPTypeAssistant().getOperation(type);
			question.assistantFactory.createPTypeAssistant().typeResolve(ot, null, THIS, new TypeCheckInfo(question.assistantFactory, question.env));
			result.add(operationApply(node, ot, question.env, NameScope.NAMESANDSTATE, unique, THIS));
		}

		if (result.isEmpty())
		{
			TypeCheckerErrors.report(3249, "Object designator is not a map, sequence, function or operation", node.getLocation(), node);
			TypeCheckerErrors.detail2("Designator", node.getObject(), "Type", type);
			return AstFactory.newAUnknownType(node.getLocation());
		}

		return result.getType(node.getLocation());
	}

	@Override
	public PType caseANewObjectDesignator(ANewObjectDesignator node,
			TypeCheckInfo question) throws AnalysisException
	{
		return node.getExpression().apply(THIS, new TypeCheckInfo(question.assistantFactory, question.env, NameScope.NAMESANDSTATE, question.qualifiers));
	}

	@Override
	public PType caseAIdentifierObjectDesignator(
			AIdentifierObjectDesignator node, TypeCheckInfo question)
			throws AnalysisException
	{
		return node.getExpression().apply(THIS, new TypeCheckInfo(question.assistantFactory, question.env, NameScope.NAMESANDSTATE, question.qualifiers));
	}

	@Override
	public PType caseAFieldObjectDesignator(AFieldObjectDesignator node,
			TypeCheckInfo question) throws AnalysisException
	{

		PType type = node.getObject().apply(THIS, new TypeCheckInfo(question.assistantFactory, question.env, null, question.qualifiers));
		PTypeSet result = new PTypeSet(question.assistantFactory);
		boolean unique = !question.assistantFactory.createPTypeAssistant().isUnion(type);

		if (question.assistantFactory.createPTypeAssistant().isClass(type, question.env))
		{
			AClassType ctype = question.assistantFactory.createPTypeAssistant().getClassType(type, question.env);

			if (node.getClassName() == null)
			{
				node.setField(new LexNameToken(ctype.getName().getName(), node.getFieldName().getName(), node.getFieldName().getLocation()));
			} else
			{
				node.setField(node.getClassName());
			}

			ILexNameToken field = node.getField();
			field.setTypeQualifier(question.qualifiers);
			PDefinition fdef = question.assistantFactory.createPDefinitionAssistant().findName(ctype.getClassdef(), field, NameScope.NAMESANDSTATE);

			if (fdef == null)
			{
				TypeCheckerErrors.concern(unique, 3260, "Unknown class member name, '"
						+ field + "'", node.getLocation(), node);
				result.add(AstFactory.newAUnknownType(node.getLocation()));
			}
			else if (!question.assistantFactory.createSClassDefinitionAssistant().isAccessible(question.env, fdef, false))
			{
				TypeCheckerErrors.concern(unique, 3260, "Inaccessible class member name, '"
					+ field + "'", node.getLocation(), node);
				result.add(AstFactory.newAUnknownType(node.getLocation()));
			}
			else
			{
				result.add(question.assistantFactory.createPDefinitionAssistant().getType(fdef));
			}
		}

		if (question.assistantFactory.createPTypeAssistant().isRecord(type))
		{
			String sname = node.getFieldName() != null ? node.getFieldName().getName()
					: node.getClassName().toString();
			ARecordInvariantType rec = question.assistantFactory.createPTypeAssistant().getRecord(type);
			AFieldField rf = question.assistantFactory.createARecordInvariantTypeAssistant().findField(rec, sname);

			if (rf == null)
			{
				TypeCheckerErrors.concern(unique, 3261, "Unknown field name, '"
						+ sname + "'", node.getLocation(), node);
				result.add(AstFactory.newAUnknownType(node.getLocation()));
			} else
			{
				result.add(rf.getType());
			}
		}

		if (result.isEmpty())
		{
			TypeCheckerErrors.report(3262, "Field assignment is not of a class or record type", node.getLocation(), node);
			TypeCheckerErrors.detail2("Expression", node.getObject(), "Type", type);
			return AstFactory.newAUnknownType(node.getLocation());
		}

		return result.getType(node.getLocation());

	}
	
	public PType mapApply(AApplyObjectDesignator node, SMapType map,
			Environment env, NameScope scope, boolean unique,
			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor)
			throws AnalysisException
	{

		if (node.getArgs().size() != 1)
		{
			TypeCheckerErrors.concern(unique, 3250, "Map application must have one argument", node.getLocation(), node);
			return AstFactory.newAUnknownType(node.getLocation());
		}

		PType argtype = node.getArgs().get(0).apply(rootVisitor, new TypeCheckInfo(env.af, env, scope));

		if (!env.af.getTypeComparator().compatible(map.getFrom(), argtype))
		{
			TypeCheckerErrors.concern(unique, 3251, "Map application argument is incompatible type", node.getLocation(), node);
			TypeCheckerErrors.detail2(unique, "Map domain", map.getFrom(), "Argument", argtype);
		}

		return map.getTo();
	}

	public PType seqApply(AApplyObjectDesignator node, SSeqType seq,
			Environment env, NameScope scope, boolean unique,
			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor, TypeCheckInfo question)
			throws AnalysisException
	{

		if (node.getArgs().size() != 1)
		{
			TypeCheckerErrors.concern(unique, 3252, "Sequence application must have one argument", node.getLocation(), node);
			return AstFactory.newAUnknownType(node.getLocation());
		}

		PType argtype = node.getArgs().get(0).apply(rootVisitor, new TypeCheckInfo(question.assistantFactory, env, scope));

		if (!env.af.createPTypeAssistant().isNumeric(argtype))
		{
			TypeCheckerErrors.concern(unique, 3253, "Sequence argument is not numeric", node.getLocation(), node);
			TypeCheckerErrors.detail(unique, "Type", argtype);
		}

		return seq.getSeqof();
	}

	public PType functionApply(AApplyObjectDesignator node,
			AFunctionType ftype, Environment env, NameScope scope,
			boolean unique, IQuestionAnswer<TypeCheckInfo, PType> rootVisitor)
			throws AnalysisException
	{

		LinkedList<PType> ptypes = ftype.getParameters();

		if (node.getArgs().size() > ptypes.size())
		{
			TypeCheckerErrors.concern(unique, 3254, "Too many arguments", node.getLocation(), node);
			TypeCheckerErrors.detail2(unique, "Args", node.getArgs(), "Params", ptypes);
			return ftype.getResult();
		} else if (node.getArgs().size() < ptypes.size())
		{
			TypeCheckerErrors.concern(unique, 3255, "Too few arguments", node.getLocation(), node);
			TypeCheckerErrors.detail2(unique, "Args", node.getArgs(), "Params", ptypes);
			return ftype.getResult();
		}

		int i = 0;

		for (PExp a : node.getArgs())
		{
			PType at = a.apply(rootVisitor, new TypeCheckInfo(env.af, env, scope));
			PType pt = ptypes.get(i++);

			if (!env.af.getTypeComparator().compatible(pt, at))
			{

				// TypeCheckerErrors.concern(unique, 3256, "Inappropriate type for argument " + i
				// +". (Expected: "+pt+" Actual: "+at+")" ,node.getLocation(),node);
				TypeCheckerErrors.concern(unique, 3256, "Inappropriate type for argument "
						+ i, node.getLocation(), node);
				TypeCheckerErrors.detail2(unique, "Expect", pt, "Actual", at);
			}
		}

		return ftype.getResult();
	}

	public PType operationApply(AApplyObjectDesignator node,
			AOperationType optype, Environment env, NameScope scope,
			boolean unique, IQuestionAnswer<TypeCheckInfo, PType> rootVisitor)
			throws AnalysisException
	{
		LinkedList<PType> ptypes = optype.getParameters();

		if (node.getArgs().size() > ptypes.size())
		{
			TypeCheckerErrors.concern(unique, 3257, "Too many arguments", node.getLocation(), node);
			TypeCheckerErrors.detail2(unique, "Args", node.getArgs(), "Params", ptypes);
			return optype.getResult();
		} else if (node.getArgs().size() < ptypes.size())
		{
			TypeCheckerErrors.concern(unique, 3258, "Too few arguments", node.getLocation(), node);
			TypeCheckerErrors.detail2(unique, "Args", node.getArgs(), "Params", ptypes);
			return optype.getResult();
		}

		int i = 0;

		for (PExp a : node.getArgs())
		{
			PType at = a.apply(rootVisitor, new TypeCheckInfo(env.af, env, scope));
			PType pt = ptypes.get(i++);

			if (!env.af.getTypeComparator().compatible(pt, at))
			{ // + ". (Expected: "+pt+" Actual: "+at+")"
				TypeCheckerErrors.concern(unique, 3259, "Inappropriate type for argument "
						+ i, node.getLocation(), node);
				TypeCheckerErrors.detail2(unique, "Expect", pt, "Actual", at);
			}
		}

		return optype.getResult();
	}

}
