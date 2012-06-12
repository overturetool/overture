package org.overture.typechecker.visitor;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.ARealLiteralExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.lex.LexStringToken;
import org.overture.ast.patterns.ADefPatternBind;
import org.overture.ast.patterns.AExpressionPattern;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.PBind;
import org.overture.ast.statements.AAlwaysStm;
import org.overture.ast.statements.AAssignmentStm;
import org.overture.ast.statements.AAtomicStm;
import org.overture.ast.statements.ABlockSimpleBlockStm;
import org.overture.ast.statements.ACallObjectStm;
import org.overture.ast.statements.ACallStm;
import org.overture.ast.statements.ACaseAlternativeStm;
import org.overture.ast.statements.ACasesStm;
import org.overture.ast.statements.AClassInvariantStm;
import org.overture.ast.statements.ACyclesStm;
import org.overture.ast.statements.ADefLetDefStm;
import org.overture.ast.statements.ADurationStm;
import org.overture.ast.statements.AElseIfStm;
import org.overture.ast.statements.AErrorCase;
import org.overture.ast.statements.AErrorStm;
import org.overture.ast.statements.AExitStm;
import org.overture.ast.statements.AExternalClause;
import org.overture.ast.statements.AForAllStm;
import org.overture.ast.statements.AForIndexStm;
import org.overture.ast.statements.AForPatternBindStm;
import org.overture.ast.statements.AIfStm;
import org.overture.ast.statements.ALetBeStStm;
import org.overture.ast.statements.ANonDeterministicSimpleBlockStm;
import org.overture.ast.statements.ANotYetSpecifiedStm;
import org.overture.ast.statements.APeriodicStm;
import org.overture.ast.statements.AReturnStm;
import org.overture.ast.statements.ASkipStm;
import org.overture.ast.statements.ASpecificationStm;
import org.overture.ast.statements.AStartStm;
import org.overture.ast.statements.ASubclassResponsibilityStm;
import org.overture.ast.statements.ATixeStm;
import org.overture.ast.statements.ATixeStmtAlternative;
import org.overture.ast.statements.ATrapStm;
import org.overture.ast.statements.AWhileStm;
import org.overture.ast.statements.PStm;
import org.overture.ast.statements.SSimpleBlockStm;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PType;
import org.overture.ast.util.PTypeSet;
import org.overture.config.Settings;
import org.overture.typechecker.Environment;
import org.overture.typechecker.FlatCheckedEnvironment;
import org.overture.typechecker.FlatEnvironment;
import org.overture.typechecker.PrivateClassEnvironment;
import org.overture.typechecker.PublicClassEnvironment;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.TypeComparator;
import org.overture.typechecker.assistant.definition.PAccessSpecifierAssistantTC;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.PDefinitionListAssistantTC;
import org.overture.typechecker.assistant.definition.SClassDefinitionAssistantTC;
import org.overture.typechecker.assistant.pattern.ATypeBindAssistantTC;
import org.overture.typechecker.assistant.pattern.PBindAssistantTC;
import org.overture.typechecker.assistant.pattern.PMultipleBindAssistantTC;
import org.overture.typechecker.assistant.pattern.PPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.PPatternBindAssistantTC;
import org.overture.typechecker.assistant.statement.ABlockSimpleBlockStmAssistantTC;
import org.overture.typechecker.assistant.statement.ACallObjectStatementAssistantTC;
import org.overture.typechecker.assistant.statement.ACallStmAssistantTC;
import org.overture.typechecker.assistant.statement.ANonDeterministicSimpleBlockStmAssistantTC;
import org.overture.typechecker.assistant.statement.PStateDesignatorAssistantTC;
import org.overture.typechecker.assistant.statement.PStmAssistantTC;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;


public class TypeCheckerStmVisitor extends QuestionAnswerAdaptor<TypeCheckInfo, PType> 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6418355785507933395L;
	final private QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor;
	
	
	public TypeCheckerStmVisitor(TypeCheckVisitor typeCheckVisitor) 
	{
		this.rootVisitor = typeCheckVisitor;
		
	}
	
	@Override
	public PType caseAAlwaysStm(AAlwaysStm node, TypeCheckInfo question) 
	{
		node.getAlways().apply(rootVisitor, question);
		node.setType(node.getBody().apply(rootVisitor, question));
		return node.getType();
	}
	
	@Override
	public PType caseAAssignmentStm(AAssignmentStm node, TypeCheckInfo question) 
	{
		
		node.setTargetType(node.getTarget().apply(rootVisitor, new TypeCheckInfo(question.env)));
		node.setExpType(node.getExp().apply(rootVisitor, new TypeCheckInfo(question.env,question.scope)));
		
		if (!TypeComparator.compatible(node.getTargetType(), node.getExpType()))
		{
			TypeCheckerErrors.report(3239, "Incompatible types in assignment", node.getLocation(), node);
			TypeCheckerErrors.detail2("Target", node.getTarget(), "Expression", node.getExp());
		}

		node.setClassDefinition(question.env.findClassDefinition());
		node.setStateDefinition(question.env.findStateDefinition());

		PDefinition encl = question.env.getEnclosingDefinition();

		if (encl != null)
		{
			if (encl instanceof AExplicitOperationDefinition)
			{
				AExplicitOperationDefinition op = (AExplicitOperationDefinition)encl;
				node.setInConstructor(op.getIsConstructor()); 
			}
			else if (encl instanceof AImplicitOperationDefinition)
			{
				AImplicitOperationDefinition op = (AImplicitOperationDefinition)encl;
				node.setInConstructor(op.getIsConstructor()); 
			}
		}

		if (node.getInConstructor())
		{			
			// Mark assignment target as initialized (so no warnings)
			PDefinition state;
			state = PStateDesignatorAssistantTC.targetDefinition(node.getTarget(), question);
			
			if (state instanceof AInstanceVariableDefinition)
			{
				AInstanceVariableDefinition iv = (AInstanceVariableDefinition)state;
				iv.setInitialized(true);
			}
		}

		node.setType(AstFactory.newAVoidType(node.getLocation()));
		return node.getType() ;
	}
	
	@Override
	public PType caseAAtomicStm(AAtomicStm node, TypeCheckInfo question) 
	{
		
		node.setStatedef(question.env.findStateDefinition());
		
		for (AAssignmentStm stmt : node.getAssignments()) {
			stmt.apply(rootVisitor, question);
		}
		
		node.setType(AstFactory.newAVoidType(node.getLocation()));
		return node.getType();
	}
	
	@Override
	public PType caseAForPatternBindStm(AForPatternBindStm node,
			TypeCheckInfo question) {
		
		PType stype = node.getExp().apply(rootVisitor, new TypeCheckInfo(question.env,question.scope));
		Environment local = question.env;

		if (PTypeAssistantTC.isSeq(stype))
		{
			node.setSeqType(PTypeAssistantTC.getSeq(stype));
			node.getPatternBind().apply(rootVisitor,new TypeCheckInfo(question.env,question.scope));
			List<PDefinition> defs = PPatternBindAssistantTC.getDefinitions(node.getPatternBind());
			PDefinitionListAssistantTC.typeCheck(defs, rootVisitor, new TypeCheckInfo(question.env, question.scope));
			local = new FlatCheckedEnvironment(defs, question.env, question.scope);
		}
		else
		{
			TypeCheckerErrors.report(3223, "Expecting sequence type after 'in'",node.getLocation(),node);
		}

		PType rt = node.getStatement().apply(rootVisitor, new TypeCheckInfo(local,question.scope));
		local.unusedCheck();
		node.setType(rt);
		return rt;
	}
	
	@Override
	public PType defaultSSimpleBlockStm(SSimpleBlockStm node,
			TypeCheckInfo question) {
		boolean notreached = false;
		PTypeSet rtypes = new PTypeSet();
		PType last = null;

		for (PStm stmt: node.getStatements())
		{
			PType stype = stmt.apply(rootVisitor,question);

			if (notreached)
			{
				TypeCheckerErrors.warning(5006, "Statement will not be reached", stmt.getLocation(), stmt);
			}
			else
			{
				last = stype;
				notreached = true;

    			if (stype instanceof AUnionType)
    			{
    				AUnionType ust = (AUnionType)stype;

    				for (PType t: ust.getTypes())
    				{
    					ABlockSimpleBlockStmAssistantTC.addOne(rtypes, t);

    					if (t instanceof AVoidType ||
    						t instanceof AUnknownType)
    					{
    						notreached = false;
    					}
    				}
    			}
    			else
    			{
    				ABlockSimpleBlockStmAssistantTC.addOne(rtypes, stype);

					if (stype instanceof AVoidType ||
						stype instanceof AUnknownType)
					{
						notreached = false;
					}
    			}
			}
		}

		// If the last statement reached has a void component, add this to the overall
		// return type, as the block may return nothing.

		
		if (last != null &&
			(PTypeAssistantTC.isType(last, AVoidType.class) || PTypeAssistantTC.isUnknown(last)))
		{
			rtypes.add(AstFactory.newAVoidType(node.getLocation()));
		}

		node.setType(rtypes.isEmpty() ?
				AstFactory.newAVoidType(node.getLocation()) : rtypes.getType(node.getLocation()));
		return node.getType();
	}
	
	
	
	@Override
	public PType caseABlockSimpleBlockStm(ABlockSimpleBlockStm node, TypeCheckInfo question) 
	{
		// Each dcl definition is in scope for later definitions...

		Environment local = question.env;

		for (PDefinition d: node.getAssignmentDefs())
		{
			local = new FlatCheckedEnvironment(d, local, question.scope);	// cumulative
			PDefinitionAssistantTC.implicitDefinitions(d, local);
			d.apply(rootVisitor,new TypeCheckInfo(local, question.scope));
		}

		// For type checking purposes, the definitions are treated as
		// local variables. At runtime (below) they have to be treated
		// more like (updatable) state.

		PType r = defaultSSimpleBlockStm(node,new TypeCheckInfo(local,question.scope));
		local.unusedCheck(question.env);
		node.setType(r);
		return r;
	}
	
	@Override
	public PType caseACallObjectStm(ACallObjectStm node, TypeCheckInfo question) 
	{
		PType dtype = node.getDesignator().apply(rootVisitor,question);
		
		if (!PTypeAssistantTC.isClass(dtype))
		{
			TypeCheckerErrors.report(3207, "Object designator is not an object type", node.getLocation(), node);
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}

		AClassType ctype = PTypeAssistantTC.getClassType(dtype);
		
		SClassDefinition classdef = ctype.getClassdef();
		SClassDefinition self = question.env.findClassDefinition();
		Environment classenv = null;
		

		if (self == classdef || PDefinitionAssistantTC.hasSupertype(self, PDefinitionAssistantTC.getType(classdef)))
		{
			// All fields visible. Note that protected fields are inherited
			// into "locals" so they are effectively private
			classenv = new PrivateClassEnvironment(self);
		}
		else
		{
			// Only public fields externally visible
			classenv = new PublicClassEnvironment(classdef);
		}

		if (node.getClassname() == null)
		{
			node.setField(new LexNameToken(
				ctype.getName().name, node.getFieldname().getName(), node.getFieldname().getLocation()));
		}
		else
		{
			node.setField(node.getClassname());
		}

		node.getField().location.executable(true);
		List<PType> atypes = ACallObjectStatementAssistantTC.getArgTypes(node.getArgs(), rootVisitor, question);
		node.getField().setTypeQualifier(atypes);
		PDefinition fdef = classenv.findName(node.getField(), question.scope);

		// Special code for the deploy method of CPU

		if (Settings.dialect == Dialect.VDM_RT &&
			node.getField().module.equals("CPU") && node.getField().name.equals("deploy"))
		{ 
			
			if (!PTypeAssistantTC.isType(atypes.get(0), AClassType.class))
			{
				TypeCheckerErrors.report(3280, "Argument to deploy must be an object", 
						node.getArgs().get(0).getLocation(), node.getArgs().get(0));
			}

			node.setType(AstFactory.newAVoidType(node.getLocation()));
			return node.getType();
		}
		else if (Settings.dialect == Dialect.VDM_RT &&
				node.getField().module.equals("CPU") && node.getField().name.equals("setPriority"))
		{
			if (!(atypes.get(0) instanceof AOperationType))
			{
				TypeCheckerErrors.report(3290, "Argument to setPriority must be an operation", 
						node.getArgs().get(0).getLocation(), node.getArgs().get(0));
			}
			else
 {
				// Convert the variable expression to a string...
				AVariableExp a1 = (AVariableExp) node.getArgs().get(0);
				node.getArgs().remove(0);
				node.getArgs().add(
						0,
						AstFactory.newAStringLiteralExp(new LexStringToken(a1
								.getName().getExplicit(true).getName(), a1
								.getLocation())));

				if (a1.getName().module.equals(a1.getName().name)) // it's a
																	// constructor
				{
					TypeCheckerErrors.report(3291,
							"Argument to setPriority cannot be a constructor",
							node.getArgs().get(0).getLocation(), node.getArgs()
									.get(0));

    			}
			}

			node.setType(AstFactory.newAVoidType(node.getLocation()));
			return node.getType();
		}
		else if (fdef == null)
		{
			TypeCheckerErrors.report(3209, "Member " + node.getField() + " is not in scope", node.getLocation(), node);
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}
		else if (PDefinitionAssistantTC.isStatic(fdef) && !question.env.isStatic())
		{
			// warning(5005, "Should invoke member " + field + " from a static context");
		}

		PType type = PDefinitionAssistantTC.getType(fdef);

		if (PTypeAssistantTC.isOperation(type))
		{
			AOperationType optype = PTypeAssistantTC.getOperation(type);
			optype.apply(rootVisitor, question);
			node.getField().setTypeQualifier(optype.getParameters());
			ACallObjectStatementAssistantTC.checkArgTypes(type, optype.getParameters(), atypes);	// Not necessary?
			node.setType(optype.getResult());
			return node.getType();
		}
		else if (PTypeAssistantTC.isFunction(type))
		{
			// This is the case where a function is called as an operation without
			// a "return" statement.

			AFunctionType ftype = PTypeAssistantTC.getFunction(type);
			ftype.apply(rootVisitor, question);
			node.getField().setTypeQualifier(ftype.getParameters());
			ACallObjectStatementAssistantTC.checkArgTypes(type, ftype.getParameters(), atypes);	// Not necessary?
			node.setType(ftype.getResult());
			return node.getType();
		}
		else
		{
			TypeCheckerErrors.report(3210, "Object member is neither a function nor an operation", node.getLocation(), node);
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}
	}
	
	@Override
	public PType caseACallStm(ACallStm node, TypeCheckInfo question) 
	{
		List<PType> atypes = ACallObjectStatementAssistantTC.getArgTypes(node.getArgs(), rootVisitor, question);

		if (question.env.isVDMPP())
		{
			node.getName().setTypeQualifier(atypes);
		}

		PDefinition opdef = question.env.findName(node.getName(), question.scope);

		if (opdef == null)
		{
			TypeCheckerErrors.report(3213, "Operation " + node.getName() + " is not in scope", node.getLocation(), node);
			question.env.listAlternatives(node.getName());
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}

		if (!PDefinitionAssistantTC.isStatic(opdef) && question.env.isStatic())
		{
			TypeCheckerErrors.report(3214, "Cannot call " + node.getName() + " from static context", node.getLocation(), node);
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}

		PType type = PDefinitionAssistantTC.getType(opdef);

		if (PTypeAssistantTC.isOperation(type))
		{
    		AOperationType optype = PTypeAssistantTC.getOperation(type);

    		
    		PTypeAssistantTC.typeResolve(optype, null, rootVisitor, question);
    		// Reset the name's qualifier with the actual operation type so
    		// that runtime search has a simple TypeComparator call.

    		if (question.env.isVDMPP())
    		{
    			node.getName().setTypeQualifier(optype.getParameters());
    		}

    		ACallStmAssistantTC.checkArgTypes(node,optype, optype.getParameters(), atypes);
    		node.setType(optype.getResult());
    		return optype.getResult();
		}
		else if (PTypeAssistantTC.isFunction(type))
		{
			// This is the case where a function is called as an operation without
			// a "return" statement.

    		AFunctionType ftype = PTypeAssistantTC.getFunction(type);
    		PTypeAssistantTC.typeResolve(ftype, null, rootVisitor, question);

    		// Reset the name's qualifier with the actual function type so
    		// that runtime search has a simple TypeComparator call.

    		if (question.env.isVDMPP())
    		{
    			node.getName().setTypeQualifier(ftype.getParameters());
    		}

    		ACallStmAssistantTC.checkArgTypes(node,ftype, ftype.getParameters(), atypes);
    		node.setType(ftype.getResult());
    		return ftype.getResult();
		}
		else
		{
			TypeCheckerErrors.report(3210, "Name is neither a function nor an operation", node.getLocation(), node);
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}
	}
	
	//TODO correct, possibly wrong typecheck implementation
	@Override
	public PType caseACaseAlternativeStm(ACaseAlternativeStm node,TypeCheckInfo question) 
	{

		if (node.getDefs().size() == 0)
		{
			node.setDefs(new LinkedList<PDefinition>());
			PPatternAssistantTC.typeResolve(node.getPattern(), rootVisitor, question);

			if (node.getPattern() instanceof AExpressionPattern)
			{
				// Only expression patterns need type checking...
				AExpressionPattern ep = (AExpressionPattern)node.getPattern();
				ep.getExp().apply(rootVisitor, question);
			}
			
			PPatternAssistantTC.typeResolve(node.getPattern(), rootVisitor, question);
			
			ACasesStm stm = (ACasesStm) node.parent();		
			node.getDefs().addAll(PPatternAssistantTC.getDefinitions(node.getPattern(), stm.getExp().getType(), NameScope.LOCAL));
		}
		
		PDefinitionListAssistantTC.typeCheck(node.getDefs(), rootVisitor, question);
		
		if (!PPatternAssistantTC.matches(node.getPattern(), node.getCtype()))
		{
			TypeCheckerErrors.report(3311, "Pattern cannot match",node.getPattern().getLocation(),node.getPattern());
		}
		
		Environment local = new FlatCheckedEnvironment(node.getDefs(), question.env, question.scope);
		PType r = node.getResult().apply(rootVisitor, new TypeCheckInfo(local,question.scope));
		local.unusedCheck();
		
		return r;
	}
	
	
	
	@Override
	public PType caseACasesStm(ACasesStm node, TypeCheckInfo question) 
	{
		
		PType expType = node.getExp().apply(rootVisitor, question);

		PTypeSet rtypes = new PTypeSet();

		for (ACaseAlternativeStm c: node.getCases())
		{
			c.setCtype(expType);
			rtypes.add(c.apply(rootVisitor, question));
		}

		if (node.getOthers()!= null)
		{
			rtypes.add(node.getOthers().apply(rootVisitor, question));
		}
		else
		{
			rtypes.add(AstFactory.newAVoidType(node.getLocation()));
		}
		
		node.setType(rtypes.getType(node.getLocation()));
		return node.getType();
	}
	
	@Override
	public PType caseAClassInvariantStm(AClassInvariantStm node,TypeCheckInfo question) 
	{
		// Definitions already checked.
		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return node.getType();
	}
	
	@Override
	public PType caseACyclesStm(ACyclesStm node, TypeCheckInfo question) 
	{
		
		if (node.getCycles() instanceof AIntLiteralExp)
		{
			AIntLiteralExp i = (AIntLiteralExp)node.getCycles();

			if (i.getValue().value < 0)
			{
				TypeCheckerErrors.report(3282, "Argument to cycles must be integer >= 0", node.getCycles().getLocation(), node.getCycles());
			}

			node.setValue(i.getValue().value);
		}
		else if (node.getCycles() instanceof ARealLiteralExp)
		{
			ARealLiteralExp i = (ARealLiteralExp)node.getCycles();

			if (i.getValue().value < 0 ||
				Math.floor(i.getValue().value) != i.getValue().value)
			{
				TypeCheckerErrors.report(3282, "Argument to cycles must be integer >= 0", node.getCycles().getLocation(), node.getCycles());
			}

			node.setValue((long) i.getValue().value);
		}
		else
		{
			TypeCheckerErrors.report(3282, "Argument to cycles must be integer >= 0", node.getCycles().getLocation(), node.getCycles());
		}

		node.setType(node.getStatement().apply(rootVisitor, question));
		return node.getType();
	}
	
	
	//TODO: Missing the other DefStatement
	
	@Override 
	public PType caseADefLetDefStm(ADefLetDefStm node, TypeCheckInfo question) 
	{
		
		// Each local definition is in scope for later local definitions...

		Environment local = question.env;

		for (PDefinition d: node.getLocalDefs())
		{
			if (d instanceof AExplicitFunctionDefinition)
			{
				// Functions' names are in scope in their bodies, whereas
				// simple variable declarations aren't

				local = new FlatCheckedEnvironment(d, local, question.scope);	// cumulative
				PDefinitionAssistantTC.implicitDefinitions(d, local);
				PDefinitionAssistantTC.typeResolve(d, rootVisitor, new TypeCheckInfo(local));
				
				if (question.env.isVDMPP())
				{
					SClassDefinition cdef = question.env.findClassDefinition();
					d.setClassDefinition(cdef);
					d.setAccess(PAccessSpecifierAssistantTC.getStatic(d, true));
				}
				
				d.apply(rootVisitor, new TypeCheckInfo(local,question.scope));
			}
			else
			{
				PDefinitionAssistantTC.implicitDefinitions(d, local);
				PDefinitionAssistantTC.typeResolve(d, rootVisitor, question);
				d.apply(rootVisitor, new TypeCheckInfo(local,question.scope));
				local = new FlatCheckedEnvironment(d, local, question.scope);	// cumulative
			}
		}

		PType r = node.getStatement().apply(rootVisitor, new TypeCheckInfo(local,question.scope));
		local.unusedCheck(question.env);
		node.setType(r);
		return r;
	}
	
	@Override
	public PType caseADurationStm(ADurationStm node, TypeCheckInfo question) 
	{
		long durationValue = 0;
		
		if (node.getDuration() instanceof AIntLiteralExp)
		{
			AIntLiteralExp i = (AIntLiteralExp)node.getDuration();

			if (i.getValue().value < 0)
			{
				TypeCheckerErrors.report(3281, "Arguments to duration must be integer >= 0", node.getDuration().getLocation(), node.getDuration());
			}

			durationValue = i.getValue().value;
		}
		else if (node.getDuration() instanceof ARealLiteralExp)
		{
			ARealLiteralExp i = (ARealLiteralExp)node.getDuration();

			if (i.getValue().value < 0 ||
				Math.floor(i.getValue().value) != i.getValue().value)
			{
				TypeCheckerErrors.report(3282, "Argument to duration must be integer >= 0", node.getDuration().getLocation(), node.getDuration());
			}

			durationValue = (long)i.getValue().value;
		}
		else
		{
			TypeCheckerErrors.report(3281, "Arguments to duration must be integer >= 0", node.getDuration().getLocation(), node.getDuration());
		}

		node.setStep(durationValue);//sets the input value [ns] to internal
		node.setType(node.getStatement().apply(rootVisitor, question));
		return node.getType();
	}
	
	@Override
	public PType caseAElseIfStm(AElseIfStm node, TypeCheckInfo question) 
	{
		if (!PTypeAssistantTC.isType(node.getElseIf().apply(rootVisitor, question), ABooleanBasicType.class))
		{
			TypeCheckerErrors.report(3218, "Expression is not boolean", node.getLocation(), node);
		}

		node.setType(node.getThenStm().apply(rootVisitor, question));
		return node.getType();
	}
	
	@Override
	public PType caseAErrorStm(AErrorStm node, TypeCheckInfo question) 
	{
		node.setType(AstFactory.newAUnknownType(node.getLocation()));
		return node.getType();	// Because we terminate anyway
	}
	
	@Override
	public PType caseAExitStm(AExitStm node, TypeCheckInfo question) 
	{
		if (node.getExpression() != null)
		{
			node.setExpType(node.getExpression().apply(rootVisitor, question));
		}

		// This is unknown because the statement doesn't actually return a
		// value - so if this is the only statement in a body, it is not a
		// type error (should return the same type as the definition return
		// type).

		node.setType(AstFactory.newAUnknownType(node.getLocation()));
		return node.getType();
	}
	
	@Override
	public PType caseAForAllStm(AForAllStm node, TypeCheckInfo question) 
	{
		node.setType(node.getSet().apply(rootVisitor, question));
		PPatternAssistantTC.typeResolve(node.getPattern(), rootVisitor, question);

		if (PTypeAssistantTC.isSet(node.getType()))
		{
			ASetType st = PTypeAssistantTC.getSet(node.getType());
			List<PDefinition> defs = PPatternAssistantTC.getDefinitions(node.getPattern(), st.getSetof(), NameScope.LOCAL);

			Environment local = new FlatCheckedEnvironment(defs, question.env, question.scope);
			PType rt = node.getStatement().apply(rootVisitor, new TypeCheckInfo(local, question.scope));
			local.unusedCheck();
			node.setType(rt);
			return rt;
		}
		else
		{
			TypeCheckerErrors.report(3219, "For all statement does not contain a set type", node.getLocation(), node);
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}
	}
	
	@Override
	public PType caseAForIndexStm(AForIndexStm node, TypeCheckInfo question) 
	{
		PType ft = node.getFrom().apply(rootVisitor,question);
		PType tt = node.getTo().apply(rootVisitor,question);

		if (!PTypeAssistantTC.isNumeric(ft))
		{
			TypeCheckerErrors.report(3220, "From type is not numeric", node.getLocation(), node);
		}

		if (!PTypeAssistantTC.isNumeric(tt))
		{
			TypeCheckerErrors.report(3221, "To type is not numeric", node.getLocation(), node);
		}

		if (node.getBy() != null)
		{
			PType bt = node.getBy().apply(rootVisitor,question);

			if (!PTypeAssistantTC.isNumeric(bt))
			{
				TypeCheckerErrors.report(3222, "By type is not numeric", node.getLocation(), node);
			}
		}
		
		PDefinition vardef = 
				AstFactory.newALocalDefinition(node.getVar().getLocation(), node.getVar(), NameScope.LOCAL, ft);
		Environment local = new FlatCheckedEnvironment(vardef, question.env, question.scope);
		PType rt = node.getStatement().apply(rootVisitor, new TypeCheckInfo(local,question.scope));
		local.unusedCheck();
		node.setType(rt);
		return rt;
	}
	
	@Override
	public PType caseAIfStm(AIfStm node, TypeCheckInfo question) 
	{
		
		PType test = node.getIfExp().apply(rootVisitor, question);

		if (!PTypeAssistantTC.isType(test, ABooleanBasicType.class))
		{
			TypeCheckerErrors.report(3224, "If expression is not boolean", node.getIfExp().getLocation(), node.getIfExp());
		}

		PTypeSet rtypes = new PTypeSet();
		rtypes.add(node.getThenStm().apply(rootVisitor, question));

		if (node.getElseIf()!= null)
		{
			for (AElseIfStm stmt: node.getElseIf())
			{
				rtypes.add(stmt.apply(rootVisitor, question));
			}
		}

		if (node.getElseStm() != null)
		{
			rtypes.add(node.getElseStm().apply(rootVisitor, question));
		}
		else
		{
			rtypes.add(AstFactory.newAVoidType(node.getLocation()));
		}
		
		node.setType(rtypes.getType(node.getLocation()));
		return node.getType();
	}
	
	@Override
	public PType caseALetBeStStm(ALetBeStStm node, TypeCheckInfo question) 
	{
		 
		node.setDef(
				AstFactory.newAMultiBindListDefinition(node.getLocation(), PMultipleBindAssistantTC.getMultipleBindList(node.getBind())));				
		node.getDef().apply(rootVisitor, question);
		Environment local = new FlatCheckedEnvironment(node.getDef(), question.env, question.scope);

		if (node.getSuchThat()!= null && !PTypeAssistantTC.isType(node.getSuchThat().apply(rootVisitor, new TypeCheckInfo(local,question.scope)), ABooleanBasicType.class))
		{
			TypeCheckerErrors.report(3225, "Such that clause is not boolean", node.getLocation(), node);
		}

		PType r = node.getStatement().apply(rootVisitor,  new TypeCheckInfo(local,question.scope));
		local.unusedCheck();
		node.setType(r);
		return r;
	}
	
	@Override
	public PType caseANonDeterministicSimpleBlockStm(
			ANonDeterministicSimpleBlockStm node, TypeCheckInfo question) 
	{
		//PType r = defaultSSimpleBlockStm(node,question);
		
		PTypeSet rtypes = new PTypeSet();
		int rcount = 0;

		for (PStm stmt: node.getStatements())
		{
			PType stype = stmt.apply(rootVisitor,question);

			if (PTypeAssistantTC.isType(stype, AUnionType.class))
			{
				AUnionType ust = (AUnionType)stype;
				for (PType t: ust.getTypes())
				{
					if (ANonDeterministicSimpleBlockStmAssistantTC.addOne(rtypes, t)) rcount++;
				}
			}
			else
			{
				if (ANonDeterministicSimpleBlockStmAssistantTC.addOne(rtypes, stype)) rcount++;
			}
		}
		
		if (rcount > 1)
		{
			TypeCheckerErrors.warning(5016, "Some statements will not be reached",node.getLocation(),node);
		}

		node.setType(rtypes.isEmpty() ?
			 AstFactory.newAVoidType(node.getLocation()) : rtypes.getType(node.getLocation()));
		return node.getType();
	}
	
	@Override
	public PType caseANotYetSpecifiedStm(ANotYetSpecifiedStm node,
			TypeCheckInfo question) 
	{
		node.setType(AstFactory.newAUnknownType(node.getLocation()));	// Because we terminate anyway
		return node.getType();
	}
	
	@Override
	public PType caseAReturnStm(AReturnStm node, TypeCheckInfo question) 
	{

		if (node.getExpression()== null)
		{
			node.setType(AstFactory.newAVoidReturnType(node.getLocation()));
			return node.getType();
		}
		else
		{
			node.setType(node.getExpression().apply(rootVisitor, question));
			return node.getType();
		}
	}
	
	
	@Override
	public PType caseASkipStm(ASkipStm node, TypeCheckInfo question) 
	{
		node.setType(AstFactory.newAVoidType(node.getLocation()));
		return node.getType();
	}
	
	@Override
	public PType caseASpecificationStm(ASpecificationStm node, TypeCheckInfo question) 
	{

		List<PDefinition> defs = new LinkedList<PDefinition>();

		// Now we build local definitions for each of the externals, so
		// that they can be added to the local environment, while the
		// global state is made inaccessible.

		if (node.getExternals()!= null)
		{
    		for (AExternalClause clause: node.getExternals())
    		{
    			for (LexNameToken name: clause.getIdentifiers())
    			{
    				if (question.env.findName(name, NameScope.STATE) == null)
    				{
    					TypeCheckerErrors.report(3274, "External variable is not in scope: " + name, name.getLocation(), name);
    				}
    				else
    				{
    					defs.add(AstFactory.newALocalDefinition(name.location, name, NameScope.STATE, clause.getType()));
    				}
    			}
    		}
		}

		if (node.getErrors()!= null)
		{
			for (AErrorCase err: node.getErrors())
			{
				PType lt = err.getLeft().apply(rootVisitor, question);
				PType rt = err.getRight().apply(rootVisitor, question);

				if (!PTypeAssistantTC.isType(lt, ABooleanBasicType.class))
				{
					TypeCheckerErrors.report(3275, "Error clause must be a boolean", err.getLeft().getLocation(), err.getLeft());
				}

				if (!PTypeAssistantTC.isType(rt, ABooleanBasicType.class))
				{
					TypeCheckerErrors.report(3275, "Error clause must be a boolean", err.getRight().getLocation(), err.getRight());
				}
			}
		}

		PDefinitionListAssistantTC.typeCheck(defs, rootVisitor, question);
		Environment local = new FlatEnvironment(defs, question.env);	// NB. No check //Unused
		
		
		if (node.getPrecondition() != null &&
			!PTypeAssistantTC.isType(node.getPrecondition().apply(rootVisitor, new TypeCheckInfo(local,NameScope.NAMESANDSTATE)), ABooleanBasicType.class))
		{
			TypeCheckerErrors.report(3233, "Precondition is not a boolean expression", node.getPrecondition().getLocation(), 
					node.getPrecondition());
		}

		if (node.getPostcondition() != null &&
			!PTypeAssistantTC.isType(node.getPostcondition().apply(rootVisitor, new TypeCheckInfo(local,NameScope.NAMESANDANYSTATE)), ABooleanBasicType.class))
		{
			TypeCheckerErrors.report(3234, "Postcondition is not a boolean expression", node.getPostcondition().getLocation(), 
					node.getPostcondition());
		}

		node.setType(AstFactory.newAVoidType(node.getLocation()));
		return node.getType();
	}
	
	@Override
	public PType caseATrapStm(ATrapStm node, TypeCheckInfo question) {
		PTypeSet rtypes = new PTypeSet();

		PStm body = node.getBody();
		
		PType bt = body.apply(rootVisitor, question);
		rtypes.add(bt);

		PTypeSet extype = PStmAssistantTC.exitCheck(body);
		PType ptype = null;

		if (extype.isEmpty())
		{
			TypeCheckerErrors.report(3241, "Body of trap statement does not throw exceptions",node.getLocation(),node);
			ptype = AstFactory.newAUnknownType(body.getLocation());
		}
		else
		{
			ptype = extype.getType(body.getLocation());
		}
		node.setType(ptype);
		node.getPatternBind().apply(rootVisitor, question);
		//TODO: PatternBind stuff
		List<PDefinition> defs = PPatternBindAssistantTC.getDefinitions(node.getPatternBind());
		PDefinitionListAssistantTC.typeCheck(defs, rootVisitor, question);
		Environment local = new FlatCheckedEnvironment(defs, question.env, question.scope);
		rtypes.add(node.getWith().apply(rootVisitor, new TypeCheckInfo(local,question.scope,question.qualifiers)));
		
		node.setType(rtypes.getType(node.getLocation()));
		return node.getType();
	}
	
	@Override
	public PType caseAWhileStm(AWhileStm node, TypeCheckInfo question) {
		question.qualifiers = null;
		node.getExp().apply(rootVisitor, question);
		node.setType(node.getStatement().apply(rootVisitor, question));		
		return node.getType();
	}
	
	@Override
	public PType caseAPeriodicStm(APeriodicStm node, TypeCheckInfo question) {
		int nargs = (Settings.dialect == Dialect.VDM_RT) ? 4 : 1;
		Long[] values = new Long[4]; 
		List<PExp> args = node.getArgs();
		
		if (args.size() != nargs)
		{
			TypeCheckerErrors.report(3287, "Periodic thread must have " + nargs + " argument(s)",node.getLocation(),node);
		}
		else
		{
			int i = 0;

			for (PExp arg: args)
			{
				arg.getLocation().hit();
				values[i] = -1L;

				if (arg instanceof AIntLiteralExp)
				{
					AIntLiteralExp e = (AIntLiteralExp)arg;
					values[i] = e.getValue().value;
				}
				else if (arg instanceof ARealLiteralExp)
				{
					ARealLiteralExp r = (ARealLiteralExp)arg;
					values[i] = Math.round(r.getValue().value);
				}

				if (values[i] < 0)
				{
					TypeCheckerErrors.report(2027, "Expecting +ive literal number in periodic statement",arg.getLocation(),arg);
				}

				i++;
			}
			
			node.setPeriod(values[0]);
			node.setJitter(values[1]);
			node.setDelay(values[2]);
			node.setOffset(values[3]);
			
			if (values[0] == 0)
			{
				TypeCheckerErrors.report(3288, "Period argument must be non-zero",args.get(0).getLocation(),args.get(0));
			}

			if (args.size() == 4)
			{
				if (values[2] >= values[0])
				{
					TypeCheckerErrors.report(
						3289, "Delay argument must be less than the period",args.get(2).getLocation(),args.get(2));
				}
			}
		}

		LexNameToken opname = node.getOpname();
		
		opname.setTypeQualifier(new LinkedList<PType>());
		opname.getLocation().hit();
		PDefinition opdef = question.env.findName(opname, NameScope.NAMES);

		if (opdef == null)
		{
			TypeCheckerErrors.report(3228, opname + " is not in scope",node.getLocation(),node);
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}

		// Operation must be "() ==> ()"

		AOperationType expected =
				AstFactory.newAOperationType(node.getLocation(), new Vector<PType>(), AstFactory.newAVoidType(node.getLocation()));			
		
		opdef = PDefinitionAssistantTC.deref(opdef);

		if (opdef instanceof AExplicitOperationDefinition)
		{
			AExplicitOperationDefinition def = (AExplicitOperationDefinition)opdef;

			if (!PTypeAssistantTC.equals(def.getType(), expected))
			{
				TypeCheckerErrors.report(3229, opname + " should have no parameters or return type",node.getLocation(),node);
				TypeCheckerErrors.detail("Actual", def.getType());
			}
		}
		else if (opdef instanceof AImplicitOperationDefinition)
		{
			AImplicitOperationDefinition def = (AImplicitOperationDefinition)opdef;

			if (def.getBody() == null)
			{
				TypeCheckerErrors.report(3230, opname + " is implicit",node.getLocation(),node);
			}

			if (!PTypeAssistantTC.equals(def.getType(), expected))
			{
				TypeCheckerErrors.report(3231, opname + " should have no parameters or return type",node.getLocation(),node);
				TypeCheckerErrors.detail("Actual", def.getType());
			}
		}
		else
		{
			TypeCheckerErrors.report(3232, opname + " is not an operation name",node.getLocation(),node);
		}

		node.setType(AstFactory.newAVoidType(node.getLocation()));
		return node.getType();
	}

	
	@Override
	public PType caseAStartStm(AStartStm node, TypeCheckInfo question) 
	{
		
		PType type = node.getObj().apply(rootVisitor, question);

		if (PTypeAssistantTC.isSet(type))
		{
			ASetType set = PTypeAssistantTC.getSet(type);

			if (!PTypeAssistantTC.isClass(set.getSetof()))
			{
				TypeCheckerErrors.report(3235, "Expression is not a set of object references", node.getObj().getLocation(), node.getObj());
			}
			else
			{
				AClassType ctype = PTypeAssistantTC.getClassType(set.getSetof());
				
				if (SClassDefinitionAssistantTC.findThread(ctype.getClassdef()) == null)
				{
					TypeCheckerErrors.report(3236, "Class does not define a thread", node.getObj().getLocation(), node.getObj());
				}
			}
		}
		else if (PTypeAssistantTC.isClass(type))
		{
			AClassType ctype = PTypeAssistantTC.getClassType(type);

			if (SClassDefinitionAssistantTC.findThread(ctype.getClassdef()) == null)
			{
				TypeCheckerErrors.report(3237, "Class does not define a thread", node.getObj().getLocation(), node.getObj());
			}
		}
		else
		{
			TypeCheckerErrors.report(3238, "Expression is not an object reference or set of object references", 
					node.getObj().getLocation(), node.getObj());
		}

		node.setType(AstFactory.newAVoidType(node.getLocation()));
		return node.getType();
	}
	
	
	@Override
	public PType caseASubclassResponsibilityStm(ASubclassResponsibilityStm node, TypeCheckInfo question) 
	{
		node.setType(AstFactory.newAUnknownType(node.getLocation()));	// Because we terminate anyway
		return node.getType(); 
	}
	
	@Override
	public PType caseATixeStm(ATixeStm node, TypeCheckInfo question) {
		
		PType rt = node.getBody().apply(rootVisitor, question);
		PTypeSet extypes = PStmAssistantTC.exitCheck(node.getBody());

		if (!extypes.isEmpty())
		{
			PType union = extypes.getType(node.getLocation());

    		for (ATixeStmtAlternative tsa: node.getTraps())
    		{    			
    			tsa.setExp(union);
    			tsa.apply(rootVisitor, question);
    		}
		}
		node.setType(rt);
		return rt;
	}
	
	@Override
	public PType caseATixeStmtAlternative(ATixeStmtAlternative node,TypeCheckInfo question) {
		
		//TODO fix 
		//patternBind.typeCheck(base, scope, ext)
		//PPatternBindAssistant.typeCheck(node.getPatternBind(), null, rootVisitor, question);
		//DefinitionList defs = patternBind.getDefinitions();
		node.getPatternBind().apply(rootVisitor, new TypeCheckInfo(question.env, question.scope));
		List<PDefinition> defs = PPatternBindAssistantTC.getDefinitions(node.getPatternBind());
		PDefinitionListAssistantTC.typeCheck(defs, rootVisitor, question);
		Environment local = new FlatCheckedEnvironment(defs, question.env, question.scope);
		node.getStatement().apply(rootVisitor, new TypeCheckInfo(local, question.scope,question.qualifiers));
		local.unusedCheck();
		
		return null;
	}
	
	@Override
	public PType caseADefPatternBind(ADefPatternBind node,
			TypeCheckInfo question) {
		
		node.setDefs(null);

		PBind bind = node.getBind();
		PType type = node.getType();
		
		if (bind != null)
		{
			if (bind instanceof ATypeBind)
			{
				ATypeBind typebind = (ATypeBind)bind;
				ATypeBindAssistantTC.typeResolve(typebind, rootVisitor, question);

				if (!TypeComparator.compatible(typebind.getType(), type))
				{
					TypeCheckerErrors.report(3198, "Type bind not compatible with expression",bind.getLocation(),bind);
					TypeCheckerErrors.detail2("Bind", typebind.getType(), "Exp", type);
				}
			}
			else
			{
				ASetBind setbind = (ASetBind)bind;
				ASetType settype = PTypeAssistantTC.getSet(setbind.getSet().apply(rootVisitor, question));

				if (!TypeComparator.compatible(type, settype.getSetof()))
				{
					TypeCheckerErrors.report(3199, "Set bind not compatible with expression",bind.getLocation(),bind);
					TypeCheckerErrors.detail2("Bind", settype.getSetof(), "Exp", type);
				}
			}

			PDefinition def =
					AstFactory.newAMultiBindListDefinition(bind.getLocation(), PBindAssistantTC.getMultipleBindList(bind));				

			def.apply(rootVisitor, question);
			List<PDefinition> defs = new LinkedList<PDefinition>();
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
	
	
	
	
	
	
}
