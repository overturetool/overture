package org.overture.typechecker.assistant.definition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.ANotYetSpecifiedExp;
import org.overture.ast.expressions.ASubclassResponsibilityExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.node.NodeList;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.PType;
import org.overture.typechecker.Environment;
import org.overture.typechecker.FlatCheckedEnvironment;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeChecker;
import org.overture.typechecker.assistant.pattern.PPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.PPatternListAssistantTC;
import org.overture.typechecker.assistant.type.AFunctionTypeAssistantTC;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

public class AExplicitFunctionDefinitionAssistantTC {

	public static List<PType> getMeasureParams(AExplicitFunctionDefinition node)
	{		
		AFunctionType functionType = (AFunctionType)node.getType();
		
		List<PType> params = new LinkedList<PType>();
		params.addAll(functionType.getParameters());
		
		if(node.getIsCurried())
		{
			PType rtype = functionType.getResult();
		
			while (rtype instanceof AFunctionType)
			{
				AFunctionType ftype = (AFunctionType) rtype;
				params.addAll(ftype.getParameters());
				rtype = ftype.getResult();
			}
		}
		
		return params;
	}
	
	public static PType checkParams(AExplicitFunctionDefinition node,
			ListIterator<List<PPattern>> plists,
			AFunctionType ftype) {
		List<PType> ptypes = ftype.getParameters();
		List<PPattern> patterns = plists.next();

		if (patterns.size() > ptypes.size())
		{
			TypeChecker.report(3020, "Too many parameter patterns",node.getLocation());
			TypeChecker.detail2("Pattern(s)", patterns, "Type(s)", ptypes);
			return ftype.getResult();
		}
		else if (patterns.size() < ptypes.size())
		{
			TypeChecker.report(3021, "Too few parameter patterns",node.getLocation());
			TypeChecker.detail2("Pattern(s)", patterns, "Type(s)", ptypes);
			return ftype.getResult();
		}

		if (ftype.getResult() instanceof AFunctionType)
		{
			if (!plists.hasNext())
			{
				// We're returning the function itself
				return ftype.getResult();
			}

			// We're returning what the function returns, assuming we
			// pass the right parameters. Note that this recursion
			// means that we finally return the result of calling the
			// function with *all* of the curried argument sets applied.
			// This is because the type check of the body determines
			// the return type when all of the curried parameters are
			// provided.

			return checkParams(node,plists, (AFunctionType)ftype.getResult());
		}

		if (plists.hasNext())
		{
			TypeChecker.report(3022, "Too many curried parameters",node.getLocation());
		}

		return ftype.getResult();
	}

	public static List<List<PDefinition>> getParamDefinitions(AExplicitFunctionDefinition node,AFunctionType type, List<List<PPattern>> paramPatternList, LexLocation location)
	{
		List<List<PDefinition>> defList = new ArrayList<List<PDefinition>>(); //new Vector<DefinitionList>();
		AFunctionType ftype = type;	// Start with the overall function
		Iterator<List<PPattern>> piter = paramPatternList.iterator();

		while (piter.hasNext())
		{
			List<PPattern> plist = piter.next();
			Set<PDefinition> defs = new HashSet<PDefinition>(); 
			List<PType> ptypes = ftype.getParameters();
			Iterator<PType> titer = ptypes.iterator();

			if (plist.size() != ptypes.size())
			{
				// This is a type/param mismatch, reported elsewhere. But we
				// have to create definitions to avoid a cascade of errors.

				PType unknown = AstFactory.newAUnknownType(location);

				for (PPattern p: plist)
				{
					defs.addAll(PPatternAssistantTC.getDefinitions(p,unknown,NameScope.LOCAL));

				}
			}
			else
			{
    			for (PPattern p: plist)
    			{
    				defs.addAll(PPatternAssistantTC.getDefinitions(p,titer.next(),NameScope.LOCAL));					
    			}
			}
			
			defList.add(new ArrayList<PDefinition>(defs));

			if (ftype.getResult() instanceof AFunctionType)	// else???
			{
				ftype = (AFunctionType)ftype.getResult();
			}
		}

		return defList;
	}
	
	
	public static List<PDefinition> getTypeParamDefinitions(AExplicitFunctionDefinition node)
	{
		List<PDefinition> defs = new ArrayList<PDefinition>();

		for (ILexNameToken pname: node.getTypeParams())
		{
			PDefinition p = AstFactory.newALocalDefinition(
					pname.getLocation(), pname.clone(),NameScope.NAMES, AstFactory.newAParameterType(pname.clone()));
				//pname.location, NameScope.NAMES,false,null, null, new AParameterType(null,false,null,pname.clone()),false,pname.clone());

			PDefinitionAssistantTC.markUsed(p);
			defs.add(p);
		}

		return defs;
	}

	public static LexNameList getVariableNames(
			AExplicitFunctionDefinition efd) {
		
		return new LexNameList(efd.getName());
	}
	
	public static AFunctionType getType(AExplicitFunctionDefinition efd, List<PType> actualTypes)
	{
		Iterator<PType> ti = actualTypes.iterator();
		AFunctionType ftype = (AFunctionType)efd.getType();
				
		if (efd.getTypeParams() != null)
		{
			for (ILexNameToken pname: efd.getTypeParams())
			{
				PType ptype = ti.next();
				ftype = (AFunctionType) PTypeAssistantTC.polymorph(ftype,pname, ptype);
			}
		}

		return ftype;
	}

	public static PDefinition findName(AExplicitFunctionDefinition d,
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

	public static List<PDefinition> getDefinitions(AExplicitFunctionDefinition d) {

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

	public static void typeResolve(AExplicitFunctionDefinition d,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws AnalysisException {
		
		
		
		if (d.getTypeParams().size() != 0)
		{
			FlatCheckedEnvironment params =	new FlatCheckedEnvironment(
				AExplicitFunctionDefinitionAssistantTC.getTypeParamDefinitions(d), question.env, NameScope.NAMES);
			
			TypeCheckInfo newQuestion = new TypeCheckInfo(params,question.scope);			
			
			d.setType(PTypeAssistantTC.typeResolve(PDefinitionAssistantTC.getType(d), null, rootVisitor, newQuestion));
		}
		else
		{
			d.setType(PTypeAssistantTC.typeResolve(PDefinitionAssistantTC.getType(d), null, rootVisitor, question));
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
			PDefinitionAssistantTC.typeResolve(d.getPredef(),rootVisitor,question);
		}

		if (d.getPostcondition() != null)
		{
			PDefinitionAssistantTC.typeResolve(d.getPostdef(),rootVisitor,question);
		}

		for (List<PPattern> pp: d.getParamPatternList())
		{
			PPatternListAssistantTC.typeResolve(pp, rootVisitor, question);
		}
		
	}

	public static void implicitDefinitions(AExplicitFunctionDefinition d,
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
			AExplicitFunctionDefinition d) {
		
		List<PPattern> last = new Vector<PPattern>();
		int psize = d.getParamPatternList().size();

		for (PPattern p: d.getParamPatternList().get(psize - 1))
		{
			last.add(p.clone());
		}

		LexNameToken result = new LexNameToken(d.getName().getModule(), "RESULT", d.getLocation());
		last.add(AstFactory.newAIdentifierPattern(result));

		List<List<PPattern>> parameters = new Vector<List<PPattern>>();

		if (psize > 1)
		{
			
			for (List<PPattern> pPatternList : d.getParamPatternList().subList(0, psize - 1))
			{
				NodeList<PPattern> tmpList = new NodeList<PPattern>(null);
				for (PPattern pPattern2 : pPatternList)
				{
					tmpList.add(pPattern2.clone());
				}
				parameters.add(tmpList);
			}
//			parameters.addAll(d.getParamPatternList().subList(0, psize - 1));
		}

		parameters.add(last);

		@SuppressWarnings("unchecked")
		AExplicitFunctionDefinition def = 
				AstFactory.newAExplicitFunctionDefinition(
						d.getName().getPostName(d.getPostcondition().getLocation()), 
						NameScope.GLOBAL, 
						(List<ILexNameToken>)d.getTypeParams().clone(), 
						AFunctionTypeAssistantTC.getCurriedPostType((AFunctionType)d.getType(),d.getIsCurried()),
						parameters, 
						d.getPostcondition(), 
						null, null, false, null);
				
//				new AExplicitFunctionDefinition(d.getPostcondition().getLocation(), d.getName().getPostName(d.getPostcondition().getLocation()),
//				NameScope.GLOBAL, false, null, PAccessSpecifierAssistant.getDefault(), (List<LexNameToken>)d.getTypeParams().clone(), 
//				parameters, AFunctionTypeAssistantTC.getCurriedPostType(d.getType(),d.getIsCurried()), 
//				d.getPostcondition(), null, null, null, null, null, null,
//				null, false, false, null, null, null, null, parameters.size() > 1, null);
		
		def.setAccess(d.getAccess().clone());
		def.setClassDefinition(d.getClassDefinition());
		return def;
	}

	private static AExplicitFunctionDefinition getPreDefinition(
			AExplicitFunctionDefinition d) {
		
		@SuppressWarnings("unchecked")
		AExplicitFunctionDefinition def = 
				AstFactory.newAExplicitFunctionDefinition(
						d.getName().getPreName(d.getPrecondition().getLocation()),
						NameScope.GLOBAL, 
						(List<ILexNameToken>) d.getTypeParams().clone(),
						AFunctionTypeAssistantTC.getCurriedPreType((AFunctionType) d.getType(),d.getIsCurried()), 
						(LinkedList<List<PPattern>>) d.getParamPatternList().clone(), 
						d.getPrecondition(), null, null, false, null);
//				new AExplicitFunctionDefinition(d.getPrecondition().getLocation(), 
//				d.getName().getPreName(d.getPrecondition().getLocation()), NameScope.GLOBAL, false, null,
//				d.getAccess().clone(), (List<LexNameToken>) d.getTypeParams().clone(), paramPatterns, 
//				AFunctionTypeAssistantTC.getCurriedPreType(d.getType(),d.getIsCurried()), d.getPrecondition(), 
//				null, null, 
//				null, null, null, null, null, false, false, null, 
//				null, null, null, paramPatterns.size() > 1, null);
		
		
		def.setAccess(d.getAccess().clone());
		def.setClassDefinition(d.getClassDefinition());
		
		return def;
	}


}
