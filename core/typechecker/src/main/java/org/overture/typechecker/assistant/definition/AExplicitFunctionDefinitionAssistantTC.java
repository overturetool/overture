package org.overture.typechecker.assistant.definition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.node.NodeList;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.PType;
import org.overture.typechecker.Environment;
import org.overture.typechecker.TypeChecker;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.pattern.PPatternAssistantTC;
import org.overture.typechecker.assistant.type.AFunctionTypeAssistantTC;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

public class AExplicitFunctionDefinitionAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AExplicitFunctionDefinitionAssistantTC(
			ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public static List<PType> getMeasureParams(AExplicitFunctionDefinition node)
	{
		AFunctionType functionType = (AFunctionType) node.getType();

		List<PType> params = new LinkedList<PType>();
		params.addAll(functionType.getParameters());

		if (node.getIsCurried())
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
			ListIterator<List<PPattern>> plists, AFunctionType ftype)
	{
		List<PType> ptypes = ftype.getParameters();
		List<PPattern> patterns = plists.next();

		if (patterns.size() > ptypes.size())
		{
			TypeChecker.report(3020, "Too many parameter patterns", node.getLocation());
			TypeChecker.detail2("Pattern(s)", patterns, "Type(s)", ptypes);
			return ftype.getResult();
		} else if (patterns.size() < ptypes.size())
		{
			TypeChecker.report(3021, "Too few parameter patterns", node.getLocation());
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

			return checkParams(node, plists, (AFunctionType) ftype.getResult());
		}

		if (plists.hasNext())
		{
			TypeChecker.report(3022, "Too many curried parameters", node.getLocation());
		}

		return ftype.getResult();
	}

	public static List<List<PDefinition>> getParamDefinitions(
			AExplicitFunctionDefinition node, AFunctionType type,
			List<List<PPattern>> paramPatternList, ILexLocation location)
	{
		List<List<PDefinition>> defList = new ArrayList<List<PDefinition>>(); // new Vector<DefinitionList>();
		AFunctionType ftype = type; // Start with the overall function
		Iterator<List<PPattern>> piter = paramPatternList.iterator();

		while (piter.hasNext())
		{
			List<PPattern> plist = piter.next();
			List<PDefinition> defs = new Vector<PDefinition>();
			List<PType> ptypes = ftype.getParameters();
			Iterator<PType> titer = ptypes.iterator();

			if (plist.size() != ptypes.size())
			{
				// This is a type/param mismatch, reported elsewhere. But we
				// have to create definitions to avoid a cascade of errors.

				PType unknown = AstFactory.newAUnknownType(location);

				for (PPattern p : plist)
				{
					defs.addAll(af.createPPatternAssistant().getDefinitions(p, unknown, NameScope.LOCAL));

				}
			} else
			{
				for (PPattern p : plist)
				{
					defs.addAll(af.createPPatternAssistant().getDefinitions(p, titer.next(), NameScope.LOCAL));
				}
			}

			defList.add(af.createPDefinitionAssistant().checkDuplicatePatterns(node, defs));

			if (ftype.getResult() instanceof AFunctionType) // else???
			{
				ftype = (AFunctionType) ftype.getResult();
			}
		}

		return defList;
	}

	public static List<PDefinition> getTypeParamDefinitions(
			AExplicitFunctionDefinition node)
	{
		List<PDefinition> defs = new ArrayList<PDefinition>();

		for (ILexNameToken pname : node.getTypeParams())
		{
			PDefinition p = AstFactory.newALocalDefinition(pname.getLocation(), pname.clone(), NameScope.NAMES, AstFactory.newAParameterType(pname.clone()));
			// pname.location, NameScope.NAMES,false,null, null, new
			// AParameterType(null,false,null,pname.clone()),false,pname.clone());

			af.createPDefinitionAssistant().markUsed(p);
			defs.add(p);
		}

		return defs;
	}

	public static AFunctionType getType(AExplicitFunctionDefinition efd,
			List<PType> actualTypes)
	{
		Iterator<PType> ti = actualTypes.iterator();
		AFunctionType ftype = (AFunctionType) efd.getType();

		if (efd.getTypeParams() != null)
		{
			for (ILexNameToken pname : efd.getTypeParams())
			{
				PType ptype = ti.next();
				ftype = (AFunctionType) PTypeAssistantTC.polymorph(ftype, pname, ptype);
			}
		}

		return ftype;
	}

	public static PDefinition findName(AExplicitFunctionDefinition d,
			ILexNameToken sought, NameScope scope)
	{
		if (af.createPDefinitionAssistant().findNameBaseCase(d, sought, scope) != null)
		{
			return d;
		}

		PDefinition predef = d.getPredef();
		if (predef != null
				&& af.createPDefinitionAssistant().findName(predef, sought, scope) != null)
		{
			return predef;
		}

		PDefinition postdef = d.getPostdef();
		if (postdef != null
				&& af.createPDefinitionAssistant().findName(postdef, sought, scope) != null)
		{
			return postdef;
		}

		return null;
	}

	public static void implicitDefinitions(AExplicitFunctionDefinition d,
			Environment env)
	{

		if (d.getPrecondition() != null)
		{
			d.setPredef(getPreDefinition(d));
			af.createPDefinitionAssistant().markUsed(d.getPredef());
		} else
		{
			d.setPredef(null);
		}

		if (d.getPostcondition() != null)
		{
			d.setPostdef(getPostDefinition(d));
			af.createPDefinitionAssistant().markUsed(d.getPostdef());
		} else
		{
			d.setPostdef(null);
		}

	}

	public static AExplicitFunctionDefinition getPostDefinition(
			AExplicitFunctionDefinition d)
	{

		List<PPattern> last = new Vector<PPattern>();
		int psize = d.getParamPatternList().size();

		for (PPattern p : d.getParamPatternList().get(psize - 1))
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
			// parameters.addAll(d.getParamPatternList().subList(0, psize - 1));
		}

		parameters.add(last);

		@SuppressWarnings("unchecked")
		AExplicitFunctionDefinition def = AstFactory.newAExplicitFunctionDefinition(d.getName().getPostName(d.getPostcondition().getLocation()), NameScope.GLOBAL, (List<ILexNameToken>) d.getTypeParams().clone(), AFunctionTypeAssistantTC.getCurriedPostType((AFunctionType) d.getType(), d.getIsCurried()), parameters, d.getPostcondition(), null, null, false, null);

		def.setAccess(d.getAccess().clone());
		def.setClassDefinition(d.getClassDefinition());
		return def;
	}

	public static AExplicitFunctionDefinition getPreDefinition(
			AExplicitFunctionDefinition d)
	{

		@SuppressWarnings("unchecked")
		AExplicitFunctionDefinition def = AstFactory.newAExplicitFunctionDefinition(d.getName().getPreName(d.getPrecondition().getLocation()), NameScope.GLOBAL, (List<ILexNameToken>) d.getTypeParams().clone(), AFunctionTypeAssistantTC.getCurriedPreType((AFunctionType) d.getType(), d.getIsCurried()), (LinkedList<List<PPattern>>) d.getParamPatternList().clone(), d.getPrecondition(), null, null, false, null);

		def.setAccess(d.getAccess().clone());
		def.setClassDefinition(d.getClassDefinition());

		return def;
	}

}
