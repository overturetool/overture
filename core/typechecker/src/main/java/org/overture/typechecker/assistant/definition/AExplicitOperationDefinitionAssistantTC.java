package org.overture.typechecker.assistant.definition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.APostOpExp;
import org.overture.ast.expressions.APreOpExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PType;
import org.overture.typechecker.Environment;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.pattern.PPatternAssistantTC;
import org.overture.typechecker.assistant.type.AOperationTypeAssistantTC;

public class AExplicitOperationDefinitionAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AExplicitOperationDefinitionAssistantTC(
			ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public static List<? extends PDefinition> getParamDefinitions(
			AExplicitOperationDefinition node)
	{

		List<PDefinition> defs = new Vector<PDefinition>();
		Iterator<PType> titer = ((AOperationType) node.getType()).getParameters().iterator();

		for (PPattern p : node.getParameterPatterns())
		{
			defs.addAll(af.createPPatternAssistant().getDefinitions(p, titer.next(), NameScope.LOCAL));
		}

		return af.createPDefinitionAssistant().checkDuplicatePatterns(node, defs);
	}

	@SuppressWarnings("unchecked")
	public static AExplicitFunctionDefinition getPostDefinition(
			AExplicitOperationDefinition d, Environment base)
	{

		List<List<PPattern>> parameters = new Vector<List<PPattern>>();
		List<PPattern> plist = new Vector<PPattern>();
		plist.addAll((List<PPattern>) d.getParameterPatterns().clone());

		if (!(((AOperationType) d.getType()).getResult() instanceof AVoidType))
		{
			LexNameToken result = new LexNameToken(d.getName().getModule(), "RESULT", d.getLocation());
			plist.add(AstFactory.newAIdentifierPattern(result));
		}

		AStateDefinition state = d.getState();

		if (state != null) // Two args, called Sigma~ and Sigma
		{
			plist.add(AstFactory.newAIdentifierPattern(state.getName().getOldName()));
			plist.add(AstFactory.newAIdentifierPattern(state.getName().clone()));
		}
		else if (base.isVDMPP())
		{
			// Two arguments called "self~" and "self"
			plist.add(AstFactory.newAIdentifierPattern(d.getName().getSelfName().getOldName()));
			
			if (!PAccessSpecifierAssistantTC.isStatic(d.getAccess()))
			{
				plist.add(AstFactory.newAIdentifierPattern(d.getName().getSelfName()));
			}
		}

		parameters.add(plist);
		APostOpExp postop = AstFactory.newAPostOpExp(d.getName().clone(), d.getPrecondition(), d.getPostcondition(), null, d.getState());

		AExplicitFunctionDefinition def = AstFactory.newAExplicitFunctionDefinition(d.getName().getPostName(d.getPostcondition().getLocation()), NameScope.GLOBAL, null, AOperationTypeAssistantTC.getPostType((AOperationType) d.getType(), state, d.getClassDefinition(), PAccessSpecifierAssistantTC.isStatic(d.getAccess())), parameters, postop, null, null, false, null);

		// Operation postcondition functions are effectively not static as
		// their expression can directly refer to instance variables, even
		// though at runtime these are passed via a "self" parameter.

		def.setAccess(PAccessSpecifierAssistantTC.getStatic(d, false));
		def.setClassDefinition(d.getClassDefinition());
		return def;

	}

	@SuppressWarnings("unchecked")
	public static AExplicitFunctionDefinition getPreDefinition(
			AExplicitOperationDefinition d, Environment base)
	{

		List<List<PPattern>> parameters = new Vector<List<PPattern>>();
		List<PPattern> plist = new Vector<PPattern>();
		plist.addAll((List<PPattern>) d.getParameterPatterns().clone());

		if (d.getState() != null)
		{
			plist.add(AstFactory.newAIdentifierPattern(d.getState().getName().clone()));
		} else if (base.isVDMPP()
				&& !PAccessSpecifierAssistantTC.isStatic(d.getAccess()))
		{
			plist.add(AstFactory.newAIdentifierPattern(d.getName().getSelfName()));
		}

		parameters.add(plist);
		APreOpExp preop = AstFactory.newAPreOpExp(d.getName().clone(), d.getPrecondition(), null, d.getState());

		AExplicitFunctionDefinition def = AstFactory.newAExplicitFunctionDefinition(d.getName().getPreName(d.getPrecondition().getLocation()), NameScope.GLOBAL, null, AOperationTypeAssistantTC.getPreType((AOperationType) d.getType(), d.getState(), d.getClassDefinition(), PAccessSpecifierAssistantTC.isStatic(d.getAccess())), parameters, preop, null, null, false, null);

		// Operation precondition functions are effectively not static as
		// their expression can directly refer to instance variables, even
		// though at runtime these are passed via a "self" parameter.

		def.setAccess(PAccessSpecifierAssistantTC.getStatic(def, false));
		def.setClassDefinition(def.getClassDefinition());
		return def;
	}

	public static List<List<PPattern>> getParamPatternList(
			AExplicitOperationDefinition func)
	{
		List<List<PPattern>> parameters = new ArrayList<List<PPattern>>();
		List<PPattern> plist = new ArrayList<PPattern>();

		for (PPattern p : func.getParameterPatterns())
		{
			plist.add(p);
		}

		parameters.add(plist);
		return parameters;
	}

}
