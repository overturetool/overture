package org.overture.typechecker.assistant.definition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.APostOpExp;
import org.overture.ast.expressions.APreOpExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.ASubclassResponsibilityStm;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PType;
import org.overture.typechecker.Environment;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.assistant.pattern.PPatternAssistantTC;
import org.overture.typechecker.assistant.type.AOperationTypeAssistantTC;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;


public class AExplicitOperationDefinitionAssistantTC {

	public static List<? extends PDefinition> getParamDefinitions(
			AExplicitOperationDefinition node) {
		
		Set<PDefinition> defs = new HashSet<PDefinition>();
		Iterator<PType> titer = node.getType().getParameters().iterator();

		for (PPattern p:  node.getParameterPatterns())
		{
   			defs.addAll(PPatternAssistantTC.getDefinitions(p,titer.next(), NameScope.LOCAL));
		}

		return new Vector<PDefinition>(defs);
	}

	public static PDefinition findName(AExplicitOperationDefinition d,
			ILexNameToken sought, NameScope scope) {
		if (PDefinitionAssistantTC.findNameBaseCase(d, sought, scope) != null)
		{
			return d;
		}

		PDefinition predef = d.getPredef();
		if (predef != null && PDefinitionAssistantTC.findName(predef, sought, scope) != null)
		{
			return predef;
		}

		PDefinition postdef = d.getPostdef();
		if (postdef != null && PDefinitionAssistantTC.findName(postdef,sought, scope) != null)
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
			TypeCheckInfo question) throws AnalysisException {

		d.setType(PTypeAssistantTC.typeResolve(d.getType(), null, rootVisitor, question));

		if (question.env.isVDMPP())
		{
			d.getName().setTypeQualifier( d.getType().getParameters());

			if (d.getBody() instanceof ASubclassResponsibilityStm)
			{
				d.getClassDefinition().setIsAbstract(true);
			}
		}

		if (d.getPrecondition() != null)
		{
		    PDefinitionAssistantTC.typeResolve(d.getPredef(), rootVisitor, question);
		}

		if (d.getPostcondition() != null)
		{
			PDefinitionAssistantTC.typeResolve(d.getPostdef(), rootVisitor, question);
		}

		for (PPattern p: d.getParameterPatterns())
		{
			PPatternAssistantTC.typeResolve(p, rootVisitor, question);
		}
		
	}

	public static void implicitDefinitions(AExplicitOperationDefinition d,
			Environment base) {
		
		d.setState(base.findStateDefinition());

		if (d.getPrecondition() != null)
		{
			d.setPredef(getPreDefinition(d,base));
			PDefinitionAssistantTC.markUsed(d.getPredef());
		}

		if (d.getPostcondition() != null)
		{
			d.setPostdef(getPostDefinition(d,base));
			PDefinitionAssistantTC.markUsed(d.getPostdef());
		}
		
	}

	@SuppressWarnings("unchecked")
	private static AExplicitFunctionDefinition getPostDefinition(
			AExplicitOperationDefinition d, Environment base) {
		
		List<List<PPattern>> parameters = new Vector<List<PPattern>>();
		List<PPattern> plist = new Vector<PPattern>();
		plist.addAll((List<PPattern>)d.getParameterPatterns().clone());

		if (!( d.getType().getResult() instanceof AVoidType))
		{
    		LexNameToken result =
    			new LexNameToken(d.getName().getModule(), "RESULT", d.getLocation());
    		plist.add(AstFactory.newAIdentifierPattern(result));
		}
		
		AStateDefinition state = d.getState();

		if (state != null)	// Two args, called Sigma~ and Sigma
		{
			plist.add(AstFactory.newAIdentifierPattern(state.getName().getOldName()));
			plist.add(AstFactory.newAIdentifierPattern(state.getName().clone()));
		}
		else if (base.isVDMPP() && !PAccessSpecifierAssistantTC.isStatic(d.getAccess()))
		{
			// Two arguments called "self~" and "self"
			plist.add(AstFactory.newAIdentifierPattern(d.getName().getSelfName().getOldName()));
			plist.add(AstFactory.newAIdentifierPattern(d.getName().getSelfName()));
		}

		parameters.add(plist);
		APostOpExp postop = 
				AstFactory.newAPostOpExp(d.getName().clone(),d.getPrecondition(),d.getPostcondition(),null,d.getState()); 

		AExplicitFunctionDefinition def = 
				AstFactory.newAExplicitFunctionDefinition(
						d.getName().getPostName(d.getPostcondition().getLocation()), 
						NameScope.GLOBAL, 
						null, 
						AOperationTypeAssistantTC.getPostType(d.getType(),state, d.getClassDefinition(), PAccessSpecifierAssistantTC.isStatic(d.getAccess())), 
						parameters, 
						postop, null, null, false, null);
				
//				new AExplicitFunctionDefinition(d.getLocation(), 
//				d.getName().getPostName(d.getPostcondition().getLocation()), 
//				NameScope.GLOBAL, false, 
//				null, PAccessSpecifierAssistant.getDefault(), 
//				null, parameters, 
//				AOperationTypeAssistantTC.getPostType(d.getType(),state, d.getClassDefinition(), PAccessSpecifierAssistantTC.isStatic(d.getAccess())),
//				postop, null, null, null, null, null, null, 
//				null, false, false, null, null, null, null, parameters.size() > 1, null);
		

		// Operation postcondition functions are effectively not static as
		// their expression can directly refer to instance variables, even
		// though at runtime these are passed via a "self" parameter.

		def.setAccess(PAccessSpecifierAssistantTC.getStatic(d,false));
		def.setClassDefinition(d.getClassDefinition());
		return def;
		
	}

	@SuppressWarnings("unchecked")
	private static AExplicitFunctionDefinition getPreDefinition(
			AExplicitOperationDefinition d, Environment base) {
		
		List<List<PPattern>> parameters = new Vector<List<PPattern>>();
		List<PPattern> plist = new Vector<PPattern>();
		plist.addAll((List<PPattern>)d.getParameterPatterns().clone());

		if (d.getState() != null)
		{
			plist.add(AstFactory.newAIdentifierPattern(d.getState().getName().clone()));
		}
		else if (base.isVDMPP() && !PAccessSpecifierAssistantTC.isStatic(d.getAccess()))
		{
			plist.add(AstFactory.newAIdentifierPattern(d.getName().getSelfName()));
		}

		parameters.add(plist);
		APreOpExp preop = 
				AstFactory.newAPreOpExp(d.getName().clone(), d.getPrecondition(),null,d.getState());

		AExplicitFunctionDefinition def = 
				AstFactory.newAExplicitFunctionDefinition(
						d.getName().getPreName(d.getPrecondition().getLocation()),
						NameScope.GLOBAL,
						null, 
						AOperationTypeAssistantTC.getPreType(d.getType(),d.getState(), d.getClassDefinition(), PAccessSpecifierAssistantTC.isStatic(d.getAccess())),
						parameters, 
						preop, null, null, false, null);
//				new AExplicitFunctionDefinition(d.getPrecondition().getLocation(), 
//				d.getName().getPreName(d.getPrecondition().getLocation()), 
//				NameScope.GLOBAL, false, null, PAccessSpecifierAssistant.getDefault(), 
//				null, parameters, AOperationTypeAssistantTC.getPreType(d.getType(),d.getState(), d.getClassDefinition(), PAccessSpecifierAssistantTC.isStatic(d.getAccess())),
//				preop, null, null, null, null, null, null, null, false, false, null, null, null, null, parameters.size() > 1, null);
		
//		;

		// Operation precondition functions are effectively not static as
		// their expression can directly refer to instance variables, even
		// though at runtime these are passed via a "self" parameter.

		def.setAccess(PAccessSpecifierAssistantTC.getStatic(def, false));
		def.setClassDefinition(def.getClassDefinition());
		return def;
	}

	public static List<List<PPattern>> getParamPatternList(AExplicitOperationDefinition func) {
		List<List<PPattern>> parameters = new ArrayList<List<PPattern>>();
		List<PPattern> plist = new ArrayList<PPattern>();

		for (PPattern p: func.getParameterPatterns())
		{
			plist.add(p);
		}

		parameters.add(plist);
		return parameters;
	}

}
