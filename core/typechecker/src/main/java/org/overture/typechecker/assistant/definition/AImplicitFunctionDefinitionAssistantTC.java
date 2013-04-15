package org.overture.typechecker.assistant.definition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.ANotYetSpecifiedExp;
import org.overture.ast.expressions.ASubclassResponsibilityExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.PType;
import org.overture.typechecker.Environment;
import org.overture.typechecker.FlatCheckedEnvironment;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.assistant.pattern.APatternTypePairAssistant;
import org.overture.typechecker.assistant.type.AFunctionTypeAssistantTC;
import org.overture.typechecker.assistant.type.APatternListTypePairAssistantTC;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

public class AImplicitFunctionDefinitionAssistantTC {

	public static AFunctionType getType(AImplicitFunctionDefinition impdef, List<PType> actualTypes)
	{		
		Iterator<PType> ti = actualTypes.iterator();
		AFunctionType ftype = (AFunctionType)impdef.getType();

		for (ILexNameToken pname: impdef.getTypeParams())
		{
			PType ptype = ti.next();
			//AFunctionTypeAssistent.
			ftype = (AFunctionType)PTypeAssistantTC.polymorph(ftype,pname, ptype);
		}

		return ftype;
	}

	public static List<PDefinition> getTypeParamDefinitions(
			AImplicitFunctionDefinition node) {
		
		List<PDefinition> defs = new ArrayList<PDefinition>();

		for (ILexNameToken pname: node.getTypeParams())
		{
			PDefinition p = 
					AstFactory.newALocalDefinition(pname.getLocation(), pname.clone(), NameScope.NAMES, AstFactory.newAParameterType(pname.clone()));
//					new ALocalDefinition(
//				pname.location, NameScope.NAMES,false,null, null, new AParameterType(null,false,null,pname.clone()),false,pname.clone());

			PDefinitionAssistantTC.markUsed(p);
			defs.add(p);
		}

		return defs;
	}

	public static PDefinition findName(AImplicitFunctionDefinition d,
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

	public static List<PDefinition> getDefinitions(AImplicitFunctionDefinition d) {
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

	public static LexNameList getVariableNames(AImplicitFunctionDefinition d) {
		return new LexNameList(d.getName());
	}

	public static void typeResolve(AImplicitFunctionDefinition d,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws AnalysisException {
		
		if (d.getTypeParams().size() > 0)
		{
			FlatCheckedEnvironment params =	new FlatCheckedEnvironment(
				AImplicitFunctionDefinitionAssistantTC.getTypeParamDefinitions(d), question.env, NameScope.NAMES);			
			d.setType(PTypeAssistantTC.typeResolve(PDefinitionAssistantTC.getType(d), null, rootVisitor, new TypeCheckInfo(params, question.scope,question.qualifiers)));
		}
		else
		{
			question.qualifiers = null;
			d.setType(PTypeAssistantTC.typeResolve( PDefinitionAssistantTC.getType(d), null, rootVisitor, question));
		}

		if (d.getResult() != null)
		{
			APatternTypePairAssistant.typeResolve(d.getResult(),rootVisitor,question);
		}

		if (question.env.isVDMPP())
		{
			AFunctionType fType = (AFunctionType) PDefinitionAssistantTC.getType(d);
			d.getName().setTypeQualifier(fType.getParameters());

			if (d.getBody() instanceof ASubclassResponsibilityExp)
			{
				d.getClassDefinition().setIsAbstract(true);
			}
		}

		if (d.getBody() instanceof ASubclassResponsibilityExp ||
				d.getBody() instanceof ANotYetSpecifiedExp)
		{
			d.setIsUndefined(true);
		}

		if (d.getPrecondition() != null)
		{
			PDefinitionAssistantTC.typeResolve(d.getPredef(), rootVisitor, question);
		}

		if (d.getPostcondition() != null)
		{
			PDefinitionAssistantTC.typeResolve(d.getPostdef(), rootVisitor, question);
		}

		for (APatternListTypePair pltp: d.getParamPatterns())
		{
			APatternListTypePairAssistantTC.typeResolve(pltp,rootVisitor,question);
		}
		
	}

	public static void implicitDefinitions(AImplicitFunctionDefinition d,
			Environment env) {
		
		if (d.getPrecondition() != null)
		{
			d.setPredef(getPreDefinition(d));
			PDefinitionAssistantTC.markUsed(d.getPredef());
		}
		else
		{
			d.setPredef(null);
		}

		if (d.getPostcondition() != null)
		{
			d.setPostdef(getPostDefinition(d));
			PDefinitionAssistantTC.markUsed(d.getPostdef());
		}
		else
		{
			d.setPostdef(null);
		}
		
	}

	private static AExplicitFunctionDefinition getPostDefinition(
			AImplicitFunctionDefinition d) {
		
		List<List<PPattern>> parameters = getParamPatternList(d);
		parameters.get(0).add(d.getResult().getPattern().clone());

		
		@SuppressWarnings("unchecked")
		AExplicitFunctionDefinition def =
				AstFactory.newAExplicitFunctionDefinition(
						d.getName().getPostName(d.getPostcondition().getLocation()), 
						NameScope.GLOBAL, 
						(List<ILexNameToken>)d.getTypeParams().clone(), 
						AFunctionTypeAssistantTC.getPostType((AFunctionType)d.getType()), 
						parameters, d.getPostcondition(), null, null, false, null);
				 
//				new AExplicitFunctionDefinition(d.getPostcondition().getLocation(), 
//				d.getName().getPostName(d.getPostcondition().getLocation()), NameScope.GLOBAL, false, 
//				null, PAccessSpecifierAssistant.getDefault(), (List<LexNameToken>)d.getTypeParams().clone(), 
//				parameters, AFunctionTypeAssistantTC.getPostType(d.getType()), 
//				d.getPostcondition(), null, null, null, null, null, null, 
//				null, false, false, null, null, null, null, parameters.size() > 1, null);
		
		def.setAccess(d.getAccess().clone());
		def.setClassDefinition(d.getClassDefinition());
		return def;
	}

	private static AExplicitFunctionDefinition getPreDefinition(
			AImplicitFunctionDefinition d) {
		
		@SuppressWarnings("unchecked")
		AExplicitFunctionDefinition def = 
				AstFactory.newAExplicitFunctionDefinition(
						d.getName().getPreName(d.getPrecondition().getLocation()), 
						NameScope.GLOBAL,
						(List<ILexNameToken>) d.getTypeParams().clone(),
						AFunctionTypeAssistantTC.getPreType((AFunctionType)d.getType()),
						getParamPatternList(d),
						d.getPrecondition(), null, null, false,null);
				
				
//				new AExplicitFunctionDefinition(d.getPrecondition().getLocation(), 
//				d.getName().getPreName(d.getPrecondition().getLocation()), NameScope.GLOBAL, false, 
//				null, PAccessSpecifierAssistant.getDefault(), (List<LexNameToken>) d.getTypeParams().clone(), 
//				parameters, AFunctionTypeAssistantTC.getPreType(d.getType()), d.getPrecondition(), 
//				null, null, null, null, null, null, null, false, false, null, null, null, null, parameters.size() > 1, null);
		
			def.setAccess(d.getAccess().clone());
			def.setClassDefinition(d.getClassDefinition());
			return def;
	}

	@SuppressWarnings("unchecked")
	public static List<List<PPattern>> getParamPatternList(
			AImplicitFunctionDefinition d) {
		
		List<List<PPattern>> parameters = new ArrayList<List<PPattern>>();
		List<PPattern> plist = new ArrayList<PPattern>();

		for (APatternListTypePair pl: d.getParamPatterns())
		{
			plist.addAll((Collection<PPattern>) pl.getPatterns().clone());
		}

		parameters.add(plist);
		return parameters;
	}
}
