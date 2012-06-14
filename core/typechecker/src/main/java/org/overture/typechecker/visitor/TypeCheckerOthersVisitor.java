package org.overture.typechecker.visitor;

import java.util.LinkedList;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AExternalDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.LexIdentifierToken;
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
import org.overture.typechecker.TypeComparator;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.SClassDefinitionAssistantTC;
import org.overture.typechecker.assistant.pattern.PBindAssistantTC;
import org.overture.typechecker.assistant.pattern.PPatternAssistantTC;
import org.overture.typechecker.assistant.type.AApplyObjectDesignatorAssistantTC;
import org.overture.typechecker.assistant.type.ARecordInvariantTypeAssistantTC;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;


public class TypeCheckerOthersVisitor extends
		QuestionAnswerAdaptor<TypeCheckInfo, PType> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1883409865766439618L;
	final private QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor;
	
	public TypeCheckerOthersVisitor(TypeCheckVisitor typeCheckVisitor) {
		this.rootVisitor = typeCheckVisitor;
	}
	
	
	@Override
	public PType caseADefPatternBind(ADefPatternBind node,
			TypeCheckInfo question) throws Throwable {
		node.setDefs(null);
		
		PType type = null;
		
		Node parent = node.getAncestor(AForPatternBindStm.class);
		if(parent != null)
		{
			type = ((AForPatternBindStm)parent).getSeqType().getSeqof();
		}else if((parent = node.getAncestor(ATrapStm.class))!=null)
		{
				type = ((ATrapStm)parent).getType();
				//assert false:"not implemented";
		}else if((parent = node.getAncestor(ATixeStmtAlternative.class))!=null)
		{
			type = ((ATixeStmtAlternative)parent).getExp();
//			assert false:"not implemented";
		}
	
		

		if (node.getBind() != null)
		{
			if (node.getBind() instanceof ATypeBind)
			{
				ATypeBind typebind = (ATypeBind)node.getBind();
				typebind.apply(rootVisitor, question);

				if (!TypeComparator.compatible(typebind.getType(), type))
				{
					TypeCheckerErrors.report(3198, "Type bind not compatible with expression", node.getBind().getLocation(), node.getBind());
					TypeCheckerErrors.detail2("Bind", typebind.getType(), "Exp", type);
				}
			}
			else
			{
				ASetBind setbind = (ASetBind)node.getBind();
				ASetType settype = PTypeAssistantTC.getSet(setbind.getSet().apply(rootVisitor, question));
				if (!TypeComparator.compatible(type, settype.getSetof()))
				{
					TypeCheckerErrors.report(3199, "Set bind not compatible with expression", node.getBind().getLocation(), node.getBind());
					TypeCheckerErrors.detail2("Bind", settype.getSetof(), "Exp", type);
				}
			}

			PDefinition def =
					AstFactory.newAMultiBindListDefinition(node.getBind().getLocation(), PBindAssistantTC.getMultipleBindList(node.getBind()));

			def.apply(rootVisitor, question);
			LinkedList<PDefinition> defs = new LinkedList<PDefinition>();
			defs.add(def);
			node.setDefs(defs);
		}
		else
		{
			assert (type != null) :
					"Can't typecheck a pattern without a type";

			PPatternAssistantTC.typeResolve(node.getPattern(), rootVisitor, question);
			node.setDefs(PPatternAssistantTC.getDefinitions(node.getPattern(), type, NameScope.LOCAL));
		}
		
		return null;
	}
	
	@Override
	public PType caseAFieldStateDesignator(AFieldStateDesignator node,
			TypeCheckInfo question) throws Throwable {
		
		PType type = node.getObject().apply(rootVisitor,question);
		PTypeSet result = new PTypeSet();
		boolean unique = !PTypeAssistantTC.isUnion(type);
		LexIdentifierToken field = node.getField();
		
		if (PTypeAssistantTC.isRecord(type))
		{
			
    		ARecordInvariantType rec = PTypeAssistantTC.getRecord(type);
    		AFieldField rf = ARecordInvariantTypeAssistantTC.findField(rec, field.name);

    		if (rf == null)
    		{
    			TypeCheckerErrors.concern(unique, 3246, "Unknown field name, '" + field + "'",node.getLocation(),field);
    			result.add(AstFactory.newAUnknownType(field.location));
    		}
    		else
    		{
    			result.add(rf.getType());
    		}
		}

		if (PTypeAssistantTC.isClass(type))
		{
			AClassType ctype = PTypeAssistantTC.getClassType(type);
			String cname = ctype.getName().name;

			node.setObjectfield( new LexNameToken(cname, field.name, node.getObject().getLocation()));
			PDefinition fdef = SClassDefinitionAssistantTC.findName(ctype.getClassdef(),node.getObjectfield(), NameScope.STATE);

			if (fdef == null)
			{
				TypeCheckerErrors.concern(unique, 3260, "Unknown class field name, '" + field + "'",node.getLocation(),node);
				result.add(AstFactory.newAUnknownType(node.getLocation()));
			}
			else
			{
				result.add(fdef.getType());
			}
		}

		if (result.isEmpty())
		{
			TypeCheckerErrors.report(3245, "Field assignment is not of a record or object type",node.getLocation(),node);
			TypeCheckerErrors.detail2("Expression", node.getObject(), "Type", type);
			node.setType(AstFactory.newAUnknownType(field.location));
			return node.getType();
		}

		node.setType(result.getType(node.getLocation()));
		return node.getType();
	}
	
	@Override
	public PType caseAIdentifierStateDesignator(
			AIdentifierStateDesignator node, TypeCheckInfo question) {
		Environment env = question.env;
		
		if (env.isVDMPP())
		{
			// We generate an explicit name because accessing a variable
			// by name in VDM++ does not "inherit" values from a superclass.

			LexNameToken name = node.getName();
			LexNameToken exname = name.getExplicit(true);
			PDefinition def = env.findName(exname, NameScope.STATE);

			if (def == null)
			{
				TypeCheckerErrors.report(3247, "Unknown variable '" + name + "' in assignment",name.getLocation(),name);
				node.setType(AstFactory.newAUnknownType(name.getLocation()));
				return node.getType();
			}
			else if (!PDefinitionAssistantTC.isUpdatable(def))
			{
				TypeCheckerErrors.report(3301, "Variable '" + name + "' in scope is not updatable",name.getLocation(),name);
				node.setType(AstFactory.newAUnknownType(name.getLocation()));
				return node.getType();
			}
			else if (def.getClassDefinition() != null)
			{
    			if (!SClassDefinitionAssistantTC.isAccessible(env, def, true))
    			{
    				TypeCheckerErrors.report(3180, "Inaccessible member '" + name + "' of class " +
    					def.getClassDefinition().getName().name,name.getLocation(),name);
    				node.setType(AstFactory.newAUnknownType(name.getLocation()));
    				return node.getType();
    			}
    			else if (!PDefinitionAssistantTC.isStatic(def) && env.isStatic())
    			{
    				TypeCheckerErrors.report(3181, "Cannot access " + name + " from a static context",name.getLocation(),name);
    				node.setType(AstFactory.newAUnknownType(name.getLocation()));
    				return node.getType();
    			}
			}

			node.setType(PDefinitionAssistantTC.getType(def));
			return node.getType(); 
		}
		else
		{
			LexNameToken name = node.getName();
			PDefinition def = env.findName(name, NameScope.STATE);

			if (def == null)
			{
				TypeCheckerErrors.report(3247, "Unknown state variable '" + name + "' in assignment",name.getLocation(),name);
				node.setType(AstFactory.newAUnknownType(name.getLocation()));
				return node.getType();
			}
			else if (!PDefinitionAssistantTC.isUpdatable(def))
			{
				TypeCheckerErrors.report(3301, "Variable '" + name + "' in scope is not updatable",name.getLocation(),name);
				node.setType(AstFactory.newAUnknownType(name.getLocation()));
				return node.getType();
			}
			else if (def instanceof AExternalDefinition)
			{
				AExternalDefinition d = (AExternalDefinition)def;

				if (d.getReadOnly())
				{
					TypeCheckerErrors.report(3248, "Cannot assign to 'ext rd' state " + name,name.getLocation(),name);
				}
			}
			// else just state access in (say) an explicit operation

			node.setType(PDefinitionAssistantTC.getType(def));
			return node.getType();
		}
	}

	@Override
	public PType caseAMapSeqStateDesignator(AMapSeqStateDesignator node,
			TypeCheckInfo question) throws Throwable {
		PType etype = node.getExp().apply(rootVisitor, new TypeCheckInfo(question.env,NameScope.NAMESANDSTATE));
		PType rtype = node.getMapseq().apply(rootVisitor, new TypeCheckInfo(question.env));
		PTypeSet result = new PTypeSet();

		if (PTypeAssistantTC.isMap(rtype))
		{
			node.setMapType(PTypeAssistantTC.getMap(rtype));

			if (!TypeComparator.compatible(node.getMapType().getFrom(), etype))
			{
				TypeCheckerErrors.report(3242, "Map element assignment of wrong type",node.getLocation(),node);
				TypeCheckerErrors.detail2("Expect", node.getMapType().getFrom(), "Actual", etype);
			}
			else
			{
				result.add(node.getMapType().getTo());
			}
		}

		if (PTypeAssistantTC.isSeq(rtype))
		{
			node.setSeqType(PTypeAssistantTC.getSeq(rtype));

			if (!PTypeAssistantTC.isNumeric(etype))
			{
				TypeCheckerErrors.report(3243, "Seq element assignment is not numeric",node.getLocation(),node);
				TypeCheckerErrors.detail("Actual", etype);
			}
			else
			{
				result.add(node.getSeqType().getSeqof());
			}
		}

		if (result.isEmpty())
		{
			TypeCheckerErrors.report(3244, "Expecting a map or a sequence",node.getLocation(),node);
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}

		node.setType(result.getType(node.getLocation()));
		return node.getType();
	}
	
	@Override
	public PType caseASelfObjectDesignator(ASelfObjectDesignator node,
			TypeCheckInfo question) {
		PDefinition def = question.env.findName(node.getSelf(), NameScope.NAMES);

		if (def == null)
		{
			TypeCheckerErrors.report(3263, "Cannot reference 'self' from here",node.getSelf().location,node.getSelf());
			return AstFactory.newAUnknownType(node.getSelf().location);
		}

		return PDefinitionAssistantTC.getType(def);
	}
	
	@Override
	public PType caseAApplyObjectDesignator(AApplyObjectDesignator node,
			TypeCheckInfo question) throws Throwable {
		LinkedList<PType> argtypes = new LinkedList<PType>();

		for (PExp a: node.getArgs())
		{
			argtypes.add(a.apply(rootVisitor, new TypeCheckInfo(question.env, NameScope.NAMESANDSTATE)));
		}

		PType type = node.getObject().apply(rootVisitor, new TypeCheckInfo(question.env, null, argtypes));
		boolean unique = !PTypeAssistantTC.isUnion(type);
		PTypeSet result = new PTypeSet();

		if (PTypeAssistantTC.isMap(type))
		{
			SMapType map = PTypeAssistantTC.getMap(type);
			result.add(AApplyObjectDesignatorAssistantTC.mapApply(node,map, question.env, NameScope.NAMESANDSTATE, unique,rootVisitor));
		}

		if (PTypeAssistantTC.isSeq(type))
		{
			SSeqType seq = PTypeAssistantTC.getSeq(type);
			result.add(AApplyObjectDesignatorAssistantTC.seqApply(node,seq, question.env, NameScope.NAMESANDSTATE, unique,rootVisitor));
		}

		if (PTypeAssistantTC.isFunction(type))
		{
			AFunctionType ft = PTypeAssistantTC.getFunction(type);
			PTypeAssistantTC.typeResolve(ft, null, rootVisitor, new TypeCheckInfo(question.env));
			result.add(AApplyObjectDesignatorAssistantTC.functionApply(node,ft, question.env, NameScope.NAMESANDSTATE, unique,rootVisitor));
		}

		if (PTypeAssistantTC.isOperation(type))
		{
			AOperationType ot = PTypeAssistantTC.getOperation(type);
			PTypeAssistantTC.typeResolve(ot, null, rootVisitor, new TypeCheckInfo(question.env));
			result.add(AApplyObjectDesignatorAssistantTC.operationApply(node,ot, question.env, NameScope.NAMESANDSTATE, unique,rootVisitor));
		}

		if (result.isEmpty())
		{
			TypeCheckerErrors.report(3249, "Object designator is not a map, sequence, function or operation",node.getLocation(),node);
			TypeCheckerErrors.detail2("Designator", node.getObject(), "Type", type);
			return AstFactory.newAUnknownType(node.getLocation());
		}

		return result.getType(node.getLocation());
	}
	
	@Override
	public PType caseANewObjectDesignator(ANewObjectDesignator node,
			TypeCheckInfo question) throws Throwable {
		return node.getExpression().apply(rootVisitor, new TypeCheckInfo(question.env,  NameScope.NAMESANDSTATE, question.qualifiers));
	}
	
	@Override
	public PType caseAIdentifierObjectDesignator(
			AIdentifierObjectDesignator node, TypeCheckInfo question) throws Throwable {
		return node.getExpression().apply(rootVisitor, new TypeCheckInfo(question.env,  NameScope.NAMESANDSTATE, question.qualifiers));
	}
	
	@Override
	public PType caseAFieldObjectDesignator(AFieldObjectDesignator node,
			TypeCheckInfo question) throws Throwable {
		
		PType type = node.getObject().apply(rootVisitor, new TypeCheckInfo(question.env, null, question.qualifiers));
		PTypeSet result = new PTypeSet();
		boolean unique = !PTypeAssistantTC.isUnion(type);

		if (PTypeAssistantTC.isClass(type))
		{
			AClassType ctype = PTypeAssistantTC.getClassType(type);
			
			if (node.getClassName() == null)
			{
				node.setField(new LexNameToken(
					ctype.getName().name, node.getFieldName().name, node.getFieldName().location));
			}
			else
			{
				node.setField(node.getClassName());
			}

			LexNameToken field = node.getField();
			field.setTypeQualifier(question.qualifiers);
			PDefinition fdef = PDefinitionAssistantTC.findName(ctype.getClassdef(), field, NameScope.NAMESANDSTATE);

			if (fdef == null)
			{
				TypeCheckerErrors.concern(unique, 3260, "Unknown class member name, '" + field + "'",node.getLocation(),node);
				result.add(AstFactory.newAUnknownType(node.getLocation()));
			}
			else
			{
				result.add(PDefinitionAssistantTC.getType(fdef));
			}
		}

		if (PTypeAssistantTC.isRecord(type))
		{
			String sname = (node.getFieldName() != null) ? node.getFieldName().name : node.getClassName().toString();
			ARecordInvariantType rec = PTypeAssistantTC.getRecord(type);
			AFieldField rf = ARecordInvariantTypeAssistantTC.findField(rec, sname);

			if (rf == null)
			{
				TypeCheckerErrors.concern(unique, 3261, "Unknown field name, '" + sname + "'",node.getLocation(),node);
				result.add(AstFactory.newAUnknownType(node.getLocation()));
			}
			else
			{
				result.add(rf.getType());
			}
		}

		if (result.isEmpty())
		{
			TypeCheckerErrors.report(3262, "Field assignment is not of a class or record type",node.getLocation(),node);
			TypeCheckerErrors.detail2("Expression", node.getObject(), "Type", type);
			return AstFactory.newAUnknownType(node.getLocation());
		}

		return result.getType(node.getLocation());
		
	}
			
}
