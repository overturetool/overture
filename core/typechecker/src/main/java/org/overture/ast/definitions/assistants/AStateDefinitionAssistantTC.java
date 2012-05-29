package org.overture.ast.definitions.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.patterns.assistants.PTypeList;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.AFieldFieldAssistantTC;
import org.overture.ast.types.assistants.PTypeAssistantTC;
import org.overture.typecheck.Environment;
import org.overture.typecheck.TypeCheckException;
import org.overture.typecheck.TypeCheckInfo;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;

public class AStateDefinitionAssistantTC {

	public static PDefinition findType(AStateDefinition d, LexNameToken sought,
			String fromModule) {
		
		if (PDefinitionAssistantTC.findName(d,sought, NameScope.STATE) != null)
		{
			return d;
		}

		return null;
	}

	public static PDefinition findName(AStateDefinition definition, LexNameToken sought,
			NameScope scope) {
		
		if (scope.matches(NameScope.NAMES))
		{
			PDefinition invdef = definition.getInvdef();
			
    		if (invdef != null && PDefinitionAssistantTC.findName(invdef, sought, scope) != null)
    		{
    			return invdef;
    		}

    		PDefinition initdef = definition.getInitdef();
    		if (initdef != null && PDefinitionAssistantTC.findName(initdef,sought, scope) != null)
    		{
    			return initdef;
    		}
		}
		
//		if ( PDefinitionAssistantTC.findName(definition.getRecordDefinition(), sought, scope) != null)
//		{
//			return definition.getRecordDefinition();
//		}

		for (PDefinition d: definition.getStateDefs())
		{
			PDefinition def = PDefinitionAssistantTC.findName(d, sought, scope);

			if (def != null)
			{
				return def;
			}
		}

		return null;
	}

	public static void unusedCheck(AStateDefinition d) {

		PDefinitionListAssistantTC.unusedCheck(d.getStateDefs());
	}

	public static List<PDefinition> getDefinitions(AStateDefinition d) {
		return d.getStateDefs();
	}

	public static LexNameList getVariableNames(AStateDefinition d) {
		return PDefinitionListAssistantTC.getVariableNames(d.getStateDefs());
	}

	public static void typeResolve(AStateDefinition d,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		for (AFieldField f: d.getFields())
		{
			try
			{
				AFieldFieldAssistantTC.typeResolve(f,null,rootVisitor,question);
			}
			catch (TypeCheckException e)
			{
				AFieldFieldAssistantTC.unResolve(f);
				throw e;
			}
		}

		d.setRecordType(PTypeAssistantTC.typeResolve(d.getRecordType(), null, rootVisitor, question));

		if (d.getInvPattern() != null)
		{
			PDefinitionAssistantTC.typeResolve(d.getInvdef(), rootVisitor, question);
		}

		if (d.getInitPattern() != null)
		{
			PDefinitionAssistantTC.typeResolve(d.getInitdef(), rootVisitor, question);
		}
		
	}

	public static void implicitDefinitions(AStateDefinition d, Environment env) {
		if (d.getInvPattern() != null)
		{
			d.setInvdef(getInvDefinition(d));
		}

		if (d.getInitPattern() != null)
		{
			d.setInitdef(getInitDefinition(d));
		}
		
	}

	private static AExplicitFunctionDefinition getInitDefinition(AStateDefinition d) {
		LexLocation loc = d.getInitPattern().getLocation();
		List<PPattern> params = new Vector<PPattern>();
		params.add(d.getInitPattern().clone());

		List<List<PPattern>> parameters = new Vector<List<PPattern>>();
		parameters.add(params);

		PTypeList ptypes = new PTypeList();
		ptypes.add(AstFactory.newAUnresolvedType(d.getName()));
		AFunctionType ftype =
				AstFactory.newAFunctionType( loc, false, ptypes, AstFactory.newABooleanBasicType(loc));

		PExp body = AstFactory.newAStateInitExp(d);

		AExplicitFunctionDefinition def = 
				AstFactory.newAExplicitFunctionDefinition(
						d.getName().getInitName(loc), 
						NameScope.GLOBAL, 
						null, ftype, parameters, body, null, null, false, null);
	
		return def;
	}

	private static AExplicitFunctionDefinition getInvDefinition(
			AStateDefinition d) {
		
		LexLocation loc = d.getInvPattern().getLocation();
		List<PPattern> params = new Vector<PPattern>();
		params.add(d.getInvPattern().clone());

		List<List<PPattern>> parameters = new Vector<List<PPattern>>();
		parameters.add(params);

		PTypeList ptypes = new PTypeList();
		ptypes.add(AstFactory.newAUnresolvedType(d.getName()));
		AFunctionType ftype =
			AstFactory.newAFunctionType( loc, false, ptypes, AstFactory.newABooleanBasicType(loc));

		return AstFactory.newAExplicitFunctionDefinition(
						d.getName().getInvName(loc), NameScope.GLOBAL, 
						null, ftype, parameters, d.getInvExpression(),  null, null, true, null);
	}

}
