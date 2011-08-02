package org.overture.ast.definitions.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.APostOpExp;
import org.overture.ast.expressions.APreOpExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.patterns.assistants.APatternTypePairAssistant;
import org.overture.ast.statements.AErrorCase;
import org.overture.ast.statements.ASubclassResponsibilityStm;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.AOperationTypeAssistant;
import org.overture.ast.types.assistants.APatternListTypePairAssistant;
import org.overture.typecheck.Environment;
import org.overture.typecheck.TypeCheckInfo;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;

public class AImplicitOperationDefinitionAssistant {

	public static PDefinition findName(AImplicitOperationDefinition d,
			LexNameToken sought, NameScope scope) {
		if (PDefinitionAssistant.findNameBaseCase(d, sought, scope) != null)
		{
			return d;
		}

		PDefinition predef = d.getPredef();
		if (predef != null && PDefinitionAssistant.findName(predef, sought, scope) != null)
		{
			return predef;
		}

		PDefinition postdef = d.getPostdef();
		if (postdef != null && PDefinitionAssistant.findName(postdef,sought, scope) != null)
		{
			return postdef;
		}

		return null;
	}

	public static List<PDefinition> getDefinitions(
			AImplicitOperationDefinition d) {
		List<PDefinition> defs = new Vector<PDefinition>();
		defs.add(d);

		if (d.getPredef() != null)
		{
			defs.add(d.getPredef());
		}

		if (d.getPostdef() != null)
		{
			defs.add(d.getPostdef());
		}

		return defs;
	}

	public static LexNameList getVariableNames(AImplicitOperationDefinition d) {
		return new LexNameList(d.getName());
	}

	public static void typeResolve(AImplicitOperationDefinition d,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		d.setType(d.getType().apply(rootVisitor, question));

		if (d.getResult() != null)
		{
			APatternTypePairAssistant.typeResolve(d.getResult(), rootVisitor, question);
		}

		if (question.env.isVDMPP())
		{
			d.getName().setTypeQualifier(d.getType().getParameters());

			if (d.getBody() instanceof ASubclassResponsibilityStm)
			{
				d.getClassDefinition().setIsAbstract(true);
			}
		}

		if (d.getPrecondition() != null)
		{
			PDefinitionAssistant.typeResolve(d.getPredef(), rootVisitor, question);
		}

		if (d.getPostcondition() != null)
		{
			PDefinitionAssistant.typeResolve(d.getPostdef(), rootVisitor, question);
		}

		for (APatternListTypePair ptp: d.getParameterPatterns())
		{
			APatternListTypePairAssistant.typeResolve(ptp, rootVisitor, question);
		}
		
	}

	public static void implicitDefinitions(AImplicitOperationDefinition d,
			Environment base) {
		
		d.setState(base.findStateDefinition());

		if (d.getPrecondition() != null)
		{
			d.setPredef(getPreDefinition(d,base));
			PDefinitionAssistant.markUsed(d.getPredef());
		}

		if (d.getPostcondition() != null)
		{
			d.setPostdef(getPostDefinition(d,base));
			PDefinitionAssistant.markUsed(d.getPostdef());
		}
		
	}

	private static AExplicitFunctionDefinition getPostDefinition(
			AImplicitOperationDefinition d, Environment base) {
		
		List<List<PPattern>> parameters = new Vector<List<PPattern>>();
		List<PPattern> plist = new Vector<PPattern>();

		for (APatternListTypePair pl: d.getParameterPatterns())
		{
			plist.addAll(pl.getPatterns());
		}

		if (d.getResult() != null)
		{
			plist.add(d.getResult().getPattern());
		}

		AStateDefinition state = d.getState();
		
		if (state != null)
		{
			plist.add(new AIdentifierPattern(state.getLocation(), null, false, state.getName().getOldName()));
			plist.add(new AIdentifierPattern(state.getLocation(), null, false, state.getName()));
		}
		else if (base.isVDMPP() && !PAccessSpecifierTCAssistant.isStatic(d.getAccess()))
		{
			plist.add(new AIdentifierPattern(state.getLocation(), null, false, d.getName().getSelfName().getOldName()));
			plist.add(new AIdentifierPattern(state.getLocation(), null, false, d.getName().getSelfName()));
		}

		parameters.add(plist);
		PExp postop = new APostOpExp(null, d.getLocation(),d.getName().clone(), d.getPrecondition(), d.getPostcondition(), d.getErrors(), state, null);

		AExplicitFunctionDefinition def = new AExplicitFunctionDefinition(
				d.getPostcondition().getLocation(),
				d.getName().getPostName(d.getPostcondition().getLocation()), 
				NameScope.GLOBAL,
				false,
				PAccessSpecifierTCAssistant.getDefault(),
				null,
				parameters,
				AOperationTypeAssistant.getPostType(d.getType(),state, d.getClassDefinition(), PAccessSpecifierTCAssistant.isStatic(d.getAccess())),
				postop, 
				null, null, null);

		// Operation postcondition functions are effectively not static as
		// their expression can directly refer to instance variables, even
		// though at runtime these are passed via a "self" parameter.

		def.setAccess(PAccessSpecifierTCAssistant.getStatic(d, false));
		def.setClassDefinition(d.getClassDefinition());
		return def;
	}

	private static AExplicitFunctionDefinition getPreDefinition(
			AImplicitOperationDefinition d, Environment base) {
		
		List<List<PPattern>> parameters = new Vector<List<PPattern>>();
		List<PPattern> plist = new Vector<PPattern>();

		for (APatternListTypePair pl: d.getParameterPatterns())
		{
			plist.addAll(pl.getPatterns());
		}

		AStateDefinition state = d.getState();
		
		if (state != null)
		{
			plist.add(new AIdentifierPattern(state.getLocation(),null, false,state.getName()));
		}
		else if (base.isVDMPP() && !PAccessSpecifierTCAssistant.isStatic(d.getAccess()))
		{
			plist.add(new AIdentifierPattern(state.getLocation(),null, false,d.getName().getSelfName()));
		}

		parameters.add(plist);
		PExp preop = new APreOpExp(null,d.getLocation(),(LexNameToken) d.getName().clone(), d.getPrecondition(), d.getErrors(), state);

		AExplicitFunctionDefinition def = new AExplicitFunctionDefinition(
			d.getPrecondition().getLocation(),
			d.getName().getPreName(d.getPrecondition().getLocation()), 
			NameScope.GLOBAL,
			false,
			PAccessSpecifierTCAssistant.getDefault(),
			null,
			parameters,
			AOperationTypeAssistant.getPreType(d.getType(), state, d.getClassDefinition(), PAccessSpecifierTCAssistant.isStatic(d.getAccess())),
			preop, 
			null, null, null);

		// Operation precondition functions are effectively not static as
		// their expression can directly refer to instance variables, even
		// though at runtime these are passed via a "self" parameter.

		def.setAccess(PAccessSpecifierTCAssistant.getStatic(d, false));
		def.setClassDefinition(d.getClassDefinition());
		return def;
	}

}
