/*
 * #%~
 * The VDM Type Checker
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.typechecker.assistant.definition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.node.NodeList;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeChecker;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AExplicitFunctionDefinitionAssistantTC implements IAstAssistant
{
	protected ITypeCheckerAssistantFactory af;

	public AExplicitFunctionDefinitionAssistantTC(
			ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public List<PType> getMeasureParams(AExplicitFunctionDefinition node)
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

	public PType checkParams(AExplicitFunctionDefinition node,
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

	public List<PDefinition> getTypeParamDefinitions(
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

	public AFunctionType getType(AExplicitFunctionDefinition efd,
			List<PType> actualTypes)
	{
		Iterator<PType> ti = actualTypes.iterator();
		AFunctionType ftype = (AFunctionType) efd.getType();

		if (efd.getTypeParams() != null)
		{
			for (ILexNameToken pname : efd.getTypeParams())
			{
				PType ptype = ti.next();
				ftype = (AFunctionType) af.createPTypeAssistant().polymorph(ftype, pname, ptype);
			}
		}

		return ftype;
	}

	public AExplicitFunctionDefinition getPostDefinition(
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
		AExplicitFunctionDefinition def = AstFactory.newAExplicitFunctionDefinition(d.getName().getPostName(d.getPostcondition().getLocation()), NameScope.GLOBAL, (List<ILexNameToken>) d.getTypeParams().clone(), af.createAFunctionTypeAssistant().getCurriedPostType((AFunctionType) d.getType(), d.getIsCurried()), parameters, d.getPostcondition(), null, null, false, null);

		def.setAccess(d.getAccess().clone());
		def.setClassDefinition(d.getClassDefinition());
		return def;
	}

	public AExplicitFunctionDefinition getPreDefinition(
			AExplicitFunctionDefinition d)
	{

		@SuppressWarnings("unchecked")
		AExplicitFunctionDefinition def = AstFactory.newAExplicitFunctionDefinition(d.getName().getPreName(d.getPrecondition().getLocation()), NameScope.GLOBAL, (List<ILexNameToken>) d.getTypeParams().clone(), af.createAFunctionTypeAssistant().getCurriedPreType((AFunctionType) d.getType(), d.getIsCurried()), (LinkedList<List<PPattern>>) d.getParamPatternList().clone(), d.getPrecondition(), null, null, false, null);

		def.setAccess(d.getAccess().clone());
		def.setClassDefinition(d.getClassDefinition());

		return def;
	}

}
