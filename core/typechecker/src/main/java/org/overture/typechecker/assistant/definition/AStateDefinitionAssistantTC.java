package org.overture.typechecker.assistant.definition;

import java.util.List;
import java.util.Vector;

import org.overture.ast.assistant.pattern.PTypeList;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AFunctionType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AStateDefinitionAssistantTC
{
	protected ITypeCheckerAssistantFactory af;

	public AStateDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public AExplicitFunctionDefinition getInitDefinition(AStateDefinition d)
	{
		ILexLocation loc = d.getInitPattern().getLocation();
		List<PPattern> params = new Vector<PPattern>();
		params.add(d.getInitPattern().clone());

		List<List<PPattern>> parameters = new Vector<List<PPattern>>();
		parameters.add(params);

		PTypeList ptypes = new PTypeList();
		ptypes.add(AstFactory.newAUnresolvedType(d.getName()));
		AFunctionType ftype = AstFactory.newAFunctionType(loc, false, ptypes, AstFactory.newABooleanBasicType(loc));

		PExp body = AstFactory.newAStateInitExp(d);

		AExplicitFunctionDefinition def = AstFactory.newAExplicitFunctionDefinition(d.getName().getInitName(loc), NameScope.GLOBAL, null, ftype, parameters, body, null, null, false, null);

		return def;
	}

	public AExplicitFunctionDefinition getInvDefinition(AStateDefinition d)
	{

		ILexLocation loc = d.getInvPattern().getLocation();
		List<PPattern> params = new Vector<PPattern>();
		params.add(d.getInvPattern().clone());

		List<List<PPattern>> parameters = new Vector<List<PPattern>>();
		parameters.add(params);

		PTypeList ptypes = new PTypeList();
		ptypes.add(AstFactory.newAUnresolvedType(d.getName()));
		AFunctionType ftype = AstFactory.newAFunctionType(loc, false, ptypes, AstFactory.newABooleanBasicType(loc));

		return AstFactory.newAExplicitFunctionDefinition(d.getName().getInvName(loc), NameScope.GLOBAL, null, ftype, parameters, d.getInvExpression(), null, null, true, null);
	}

}
