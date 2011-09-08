package org.overture.ast.definitions.assistants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.ANotYetSpecifiedExp;
import org.overture.ast.expressions.ASubclassResponsibilityExp;
import org.overture.ast.node.NodeList;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.patterns.assistants.PPatternAssistantTC;
import org.overture.ast.patterns.assistants.PPatternListAssistant;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.AFunctionTypeAssistant;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.typecheck.Environment;
import org.overture.typecheck.FlatCheckedEnvironment;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.TypeChecker;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;

public class AExplicitFunctionDefinitionAssistant {

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

				PType unknown = new AUnknownType(location,false,null);

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

		for (LexNameToken pname: node.getTypeParams())
		{
			PDefinition p = new ALocalDefinition(
				pname.location, NameScope.NAMES,false,null, null, new AParameterType(null,false,null,pname.clone()),false,pname.clone());

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
			for (LexNameToken pname: efd.getTypeParams())
			{
				PType ptype = ti.next();
				ftype = (AFunctionType) PTypeAssistant.polymorph(ftype,pname, ptype);
			}
		}

		return ftype;
	}

	public static PDefinition findName(AExplicitFunctionDefinition d,
			LexNameToken sought, NameScope scope) {
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
			TypeCheckInfo question) {
		
		
		
		if (d.getTypeParams().size() != 0)
		{
			FlatCheckedEnvironment params =	new FlatCheckedEnvironment(
				AExplicitFunctionDefinitionAssistant.getTypeParamDefinitions(d), question.env, NameScope.NAMES);
			
			TypeCheckInfo newQuestion = new TypeCheckInfo(params,question.scope);			
			
			d.setType(PTypeAssistant.typeResolve(PDefinitionAssistantTC.getType(d), null, rootVisitor, newQuestion));
		}
		else
		{
			d.setType(PTypeAssistant.typeResolve(PDefinitionAssistantTC.getType(d), null, rootVisitor, question));
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
			PPatternListAssistant.typeResolve(pp, rootVisitor, question);
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

		LexNameToken result = new LexNameToken(d.getName().module, "RESULT", d.getLocation());
		last.add(new AIdentifierPattern(d.getLocation(),null,false,result));

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

		AExplicitFunctionDefinition def = new AExplicitFunctionDefinition(
			d.getPostcondition().getLocation(),
			d.getName().getPostName(d.getPostcondition().getLocation()), 
			NameScope.GLOBAL,
			false,
			PAccessSpecifierAssistant.getDefault(),
			(List<LexNameToken>)d.getTypeParams().clone(), 
			parameters,
			AFunctionTypeAssistant.getCurriedPostType(d.getType(),d.getIsCurried()),
			d.getPostcondition(), 
			null, null, null);

		def.setAccess(d.getAccess());
		def.setClassDefinition(d.getClassDefinition());
		return def;
	}

	private static AExplicitFunctionDefinition getPreDefinition(
			AExplicitFunctionDefinition d) {
		LinkedList<List<PPattern>> paramPatterns = (LinkedList<List<PPattern>>) d.getParamPatternList().clone();
		
		AExplicitFunctionDefinition def = new AExplicitFunctionDefinition(
				d.getPrecondition().getLocation(),
				d.getName().getPreName(d.getPrecondition().getLocation()), //name
				NameScope.GLOBAL, //namescope 
				false, 
				d.getAccess(), 
				(List<LexNameToken>) d.getTypeParams().clone(), 
				paramPatterns, 
				AFunctionTypeAssistant.getCurriedPreType(d.getType(),d.getIsCurried()), //type 
				d.getPrecondition().clone(), 
				null, null, null);
		
		def.setClassDefinition(d.getClassDefinition());
		
		return def;
	}


}
