package org.overture.typecheck;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.node.NodeList;
import org.overture.ast.patterns.APatternInnerListPatternList;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.runtime.HelperDefinition;
import org.overture.runtime.HelperPattern;
import org.overture.runtime.TypeChecker;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;

public class HelperAExplicitFunctionDefinition {

	public static PType checkParams(AExplicitFunctionDefinition node,
			ListIterator<APatternInnerListPatternList> plists,
			AFunctionType ftype) {
		NodeList<PType> ptypes = ftype.getParameters();
		APatternInnerListPatternList patterns = plists.next();

		if (patterns.getList().size() > ptypes.size())
		{
			TypeChecker.report(3020, "Too many parameter patterns",node.getLocation());
			TypeChecker.detail2("Pattern(s)", patterns, "Type(s)", ptypes);
			return ftype.getResult();
		}
		else if (patterns.getList().size() < ptypes.size())
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

	public static List<List<PDefinition>> getParamDefinitions(AExplicitFunctionDefinition node,AFunctionType type, NodeList<APatternInnerListPatternList> paramPatternList, LexLocation location)
	{
		List<List<PDefinition>> defList = new ArrayList<List<PDefinition>>(); //new Vector<DefinitionList>();
		AFunctionType ftype = type;	// Start with the overall function
		Iterator<APatternInnerListPatternList> piter = paramPatternList.iterator();

		while (piter.hasNext())
		{
			APatternInnerListPatternList plist = piter.next();
			Set<PDefinition> defs = new HashSet<PDefinition>(); 
			NodeList<PType> ptypes = ftype.getParameters();
			Iterator<PType> titer = ptypes.iterator();

			if (plist.getList().size() != ptypes.size())
			{
				// This is a type/param mismatch, reported elsewhere. But we
				// have to create definitions to avoid a cascade of errors.

				PType unknown = new AUnknownType(location);

				for (PPattern p: plist.getList())
				{
					//TODO: getDefinitions is different
					HelperPattern.getDefinitions(p,unknown,NameScope.LOCAL);
					//defs.addAll(p.getDefinitions(unknown, NameScope.LOCAL));
				}
			}
			else
			{
    			for (PPattern p: plist.getList())
    			{
    				//TODO: getDefinitions is different
    				HelperPattern.getDefinitions(p,titer.next(),NameScope.LOCAL);					
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
				pname.location, pname, NameScope.NAMES,false,null, new AParameterType(null,pname));

			HelperDefinition.markUsed(p);
			defs.add(p);
		}

		return defs;
	}
}
