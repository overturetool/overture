package org.overture.ast.definitions.assistants;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.APostOpExp;
import org.overture.ast.expressions.APreOpExp;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.patterns.assistants.PPatternTCAssistant;
import org.overture.ast.statements.ASubclassResponsibilityStm;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.AOperationTypeAssistant;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.typecheck.Environment;
import org.overture.typecheck.TypeCheckInfo;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;


public class AExplicitOperationDefinitionAssistant {

	public static List<? extends PDefinition> getParamDefinitions(
			AExplicitOperationDefinition node) {
		
		Set<PDefinition> defs = new HashSet<PDefinition>();
		Iterator<PType> titer = node.getType().getParameters().iterator();

		for (PPattern p:  node.getParameterPatterns())
		{
   			defs.addAll(PPatternTCAssistant.getDefinitions(p,titer.next(), NameScope.LOCAL));
		}

		return new Vector<PDefinition>(defs);
	}

	public static PDefinition findName(AExplicitOperationDefinition d,
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
			AExplicitOperationDefinition d) {
		
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

	public static LexNameList getVariableNames(AExplicitOperationDefinition d) {
		
		return new LexNameList(d.getName());
	}

	public static void typeResolve(AExplicitOperationDefinition d,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {

		d.setType(PTypeAssistant.typeResolve(d.getType(), null, rootVisitor, question));

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

		for (PPattern p: d.getParameterPatterns())
		{
			PPatternTCAssistant.typeResolve(p, rootVisitor, question);
		}
		
	}

	public static void implicitDefinitions(AExplicitOperationDefinition d,
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
			AExplicitOperationDefinition d, Environment base) {
		
		List<List<PPattern>> parameters = new Vector<List<PPattern>>();
		List<PPattern> plist = new Vector<PPattern>();
		plist.addAll((List<PPattern>)d.getParameterPatterns().clone());

		if (!(d.getType().getResult() instanceof AVoidType))
		{
    		LexNameToken result =
    			new LexNameToken(d.getName().module, "RESULT", d.getLocation());
    		plist.add(new AIdentifierPattern(d.getLocation(),null, false,result));
		}
		
		AStateDefinition state = d.getState();

		if (state != null)	// Two args, called Sigma~ and Sigma
		{
			plist.add(new AIdentifierPattern(state.getLocation(),null, false,state.getName().getOldName()));
			plist.add(new AIdentifierPattern(state.getLocation(),null, false,state.getName()));
		}
		else if (base.isVDMPP() && !PAccessSpecifierTCAssistant.isStatic(d.getAccess()))
		{
			// Two arguments called "self~" and "self"
			plist.add(new AIdentifierPattern(d.getLocation(),null,false, d.getName().getSelfName().getOldName()));
			plist.add(new AIdentifierPattern(d.getLocation(),null,false, d.getName().getSelfName()));
		}

		parameters.add(plist);
		APostOpExp postop = new APostOpExp(null,d.getLocation(), d.getName(), d.getPrecondition(), d.getPostcondition(), null, state, null);

		AExplicitFunctionDefinition def = new AExplicitFunctionDefinition(
				d.getLocation(),
				d.getName().getPostName(d.getPostcondition().getLocation()), 
				NameScope.GLOBAL,
				false,
				PAccessSpecifierAssistant.getDefault(),
				null,
				parameters,
				AOperationTypeAssistant.getPostType(d.getType(),state, d.getClassDefinition(), PAccessSpecifierTCAssistant.isStatic(d.getAccess())),
				postop, null, null, null);

		// Operation postcondition functions are effectively not static as
		// their expression can directly refer to instance variables, even
		// though at runtime these are passed via a "self" parameter.

		def.setAccess(PAccessSpecifierTCAssistant.getStatic(d,false));
		def.setClassDefinition(d.getClassDefinition());
		return def;
		
	}

	private static AExplicitFunctionDefinition getPreDefinition(
			AExplicitOperationDefinition d, Environment base) {
		
		List<List<PPattern>> parameters = new Vector<List<PPattern>>();
		List<PPattern> plist = new Vector<PPattern>();
		plist.addAll((List<PPattern>)d.getParameterPatterns().clone());

		if (d.getState() != null)
		{
			plist.add(new AIdentifierPattern(d.getLocation(),null, false, d.getState().getName()));
		}
		else if (base.isVDMPP() && !PAccessSpecifierTCAssistant.isStatic(d.getAccess()))
		{
			plist.add(new AIdentifierPattern(d.getLocation(),null,false, d.getName().getSelfName()));
		}

		parameters.add(plist);
		APreOpExp preop = new APreOpExp(null,d.getLocation(),d.getName().clone(), d.getPrecondition(), null, d.getState());

		AExplicitFunctionDefinition def = new AExplicitFunctionDefinition(
			d.getPrecondition().getLocation(),
			d.getName().getPreName(d.getPrecondition().getLocation()), 
			NameScope.GLOBAL,
			false,
			PAccessSpecifierAssistant.getDefault(),
			null,
			parameters,
			AOperationTypeAssistant.getPreType(d.getType(),d.getState(), d.getClassDefinition(), PAccessSpecifierTCAssistant.isStatic(d.getAccess())),
			preop, 
			null, null, null);

		// Operation precondition functions are effectively not static as
		// their expression can directly refer to instance variables, even
		// though at runtime these are passed via a "self" parameter.

		def.setAccess(PAccessSpecifierTCAssistant.getStatic(def, false));
		def.setClassDefinition(def.getClassDefinition());
		return def;
	}

}
