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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AImplicitFunctionDefinitionAssistantTC implements IAstAssistant
{
	protected ITypeCheckerAssistantFactory af;

	public AImplicitFunctionDefinitionAssistantTC(
			ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public AFunctionType getType(AImplicitFunctionDefinition impdef,
			List<PType> actualTypes)
	{
		Iterator<PType> ti = actualTypes.iterator();
		AFunctionType ftype = (AFunctionType) impdef.getType();

		for (ILexNameToken pname : impdef.getTypeParams())
		{
			PType ptype = ti.next();
			// AFunctionTypeAssistent.
			ftype = (AFunctionType) af.createPTypeAssistant().polymorph(ftype, pname, ptype);
		}

		return ftype;
	}
	
	public List<List<PDefinition>> getParamDefinitions(
			AImplicitFunctionDefinition node, AFunctionType type,
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

	public List<PDefinition> getTypeParamDefinitions(
			AImplicitFunctionDefinition node)
	{

		List<PDefinition> defs = new ArrayList<PDefinition>();

		for (ILexNameToken pname : node.getTypeParams())
		{
			PDefinition p = AstFactory.newALocalDefinition(pname.getLocation(), pname.clone(), NameScope.NAMES, AstFactory.newAParameterType(pname.clone()));
			// new ALocalDefinition(
			// pname.location, NameScope.NAMES,false,null, null, new
			// AParameterType(null,false,null,pname.clone()),false,pname.clone());

			af.createPDefinitionAssistant().markUsed(p);
			defs.add(p);
		}

		return defs;
	}

	public AExplicitFunctionDefinition getPostDefinition(
			AImplicitFunctionDefinition d)
	{

		List<List<PPattern>> parameters = getParamPatternList(d);
		parameters.get(0).add(d.getResult().getPattern().clone());

		@SuppressWarnings("unchecked")
		AExplicitFunctionDefinition def = AstFactory.newAExplicitFunctionDefinition(d.getName().getPostName(d.getPostcondition().getLocation()), NameScope.GLOBAL, (List<ILexNameToken>) d.getTypeParams().clone(), af.createAFunctionTypeAssistant().getPostType((AFunctionType) d.getType()), parameters, d.getPostcondition(), null, null, false, null);

		def.setAccess(d.getAccess().clone());
		def.setClassDefinition(d.getClassDefinition());
		return def;
	}

	public AExplicitFunctionDefinition getPreDefinition(
			AImplicitFunctionDefinition d)
	{

		@SuppressWarnings("unchecked")
		AExplicitFunctionDefinition def = AstFactory.newAExplicitFunctionDefinition(d.getName().getPreName(d.getPrecondition().getLocation()), NameScope.GLOBAL, (List<ILexNameToken>) d.getTypeParams().clone(), af.createAFunctionTypeAssistant().getPreType((AFunctionType) d.getType()), getParamPatternList(d), d.getPrecondition(), null, null, false, null);

		def.setAccess(d.getAccess().clone());
		def.setClassDefinition(d.getClassDefinition());
		return def;
	}

	@SuppressWarnings("unchecked")
	public List<List<PPattern>> getParamPatternList(
			AImplicitFunctionDefinition d)
	{

		List<List<PPattern>> parameters = new ArrayList<List<PPattern>>();
		List<PPattern> plist = new ArrayList<PPattern>();

		for (APatternListTypePair pl : d.getParamPatterns())
		{
			plist.addAll((Collection<PPattern>) pl.getPatterns().clone());
		}

		parameters.add(plist);
		return parameters;
	}
}
