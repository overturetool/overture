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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.PType;
import org.overture.typechecker.Environment;
import org.overture.typechecker.TypeCheckException;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class PDefinitionListAssistantTC implements IAstAssistant
{
	protected ITypeCheckerAssistantFactory af;

	public PDefinitionListAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public void implicitDefinitions(List<PDefinition> paramDefinitions,
			Environment env)
	{
		for (PDefinition d : paramDefinitions)
		{
			af.createPDefinitionAssistant().implicitDefinitions(d, env);
			// System.out.println();
		}

	}

	public PDefinition findName(List<PDefinition> definitions,
			ILexNameToken name, NameScope scope)
	{
		for (PDefinition d : definitions)
		{
			PDefinition def = af.createPDefinitionAssistant().findName(d, name, scope);

			if (def != null)
			{
				return def;
			}
		}

		return null;
	}

	public AStateDefinition findStateDefinition(List<PDefinition> definitions)
	{
		for (PDefinition d : definitions)
		{
			if (d instanceof AStateDefinition)
			{
				return (AStateDefinition) d;
			}
		}

		return null;
	}

	public void unusedCheck(List<PDefinition> definitions)
	{
		for (PDefinition d : definitions)
		{
			af.createPDefinitionAssistant().unusedCheck(d);
		}

	}

	public Set<PDefinition> findMatches(List<PDefinition> definitions,
			ILexNameToken name)
	{

		Set<PDefinition> set = new HashSet<PDefinition>();

		for (PDefinition d : singleDefinitions(definitions))
		{
			if (af.createPDefinitionAssistant().isFunctionOrOperation(d)
					&& d.getName().matches(name))
			{
				set.add(d);
			}
		}

		return set;
	}

	public List<PDefinition> singleDefinitions(List<PDefinition> definitions)
	{
		List<PDefinition> all = new ArrayList<PDefinition>();

		for (PDefinition d : definitions)
		{
			all.addAll(af.createPDefinitionAssistant().getDefinitions(d));
		}

		return all;
	}

	public void markUsed(List<PDefinition> definitions)
	{
		for (PDefinition d : definitions)
		{
			af.createPDefinitionAssistant().markUsed(d);
		}

	}

	public void typeCheck(List<PDefinition> defs,
			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws AnalysisException
	{
		for (PDefinition d : defs)
		{
			if (d.getName() != null && d.getName().getName().equals("RESULT"))
			{
				TypeCheckerErrors.report(3336, "Illegal use of RESULT reserved identifier", d.getLocation(), d);
			}
			
			d.apply(rootVisitor, question);
		}
	}

	public LexNameList getVariableNames(List<PDefinition> list)
	{

		LexNameList variableNames = new LexNameList();

		for (PDefinition d : list)
		{
			variableNames.addAll(af.createPDefinitionAssistant().getVariableNames(d));
		}

		return variableNames;
	}

	public void setAccessibility(List<PDefinition> defs,
			AAccessSpecifierAccessSpecifier access)
	{
		for (PDefinition d : defs)
		{
			d.setAccess(access.clone());
		}

	}

	public void typeResolve(List<PDefinition> definitions,
			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws AnalysisException
	{
		TypeCheckException problem = null;
		
		for (PDefinition definition : definitions)
		{
			try
			{
				af.createPDefinitionAssistant().typeResolve(definition, rootVisitor, question);
			}
			catch (TypeCheckException te)
			{
				if (problem == null)
				{
					problem = te;
				}
				else
				{
					problem.addExtra(te);
				}
			}
		}
		
		if (problem != null)
		{
			throw problem;
		}
	}

	public PDefinition findType(LinkedList<PDefinition> actualDefs,
			ILexNameToken name, String fromModule)
	{
		for (PDefinition d : actualDefs)
		{
			PDefinition def = af.createPDefinitionAssistant().findType(d, name, fromModule);

			if (def != null)
			{
				return def;
			}
		}

		return null;
	}

	public void initializedCheck(LinkedList<PDefinition> definitions)
	{
		for (PDefinition d : definitions)
		{
			if (d instanceof AInstanceVariableDefinition)
			{
				AInstanceVariableDefinition ivd = (AInstanceVariableDefinition) d;
				initializedCheck(ivd);
			}
		}
	}

	public void setClassDefinition(List<PDefinition> defs,
			SClassDefinition classDefinition)
	{
		af.createPDefinitionAssistant().setClassDefinition(defs, classDefinition);

	}

	public boolean hasSubclassResponsibilities(List<PDefinition> definitions)
	{
		PDefinitionAssistantTC assistant = af.createPDefinitionAssistant();

		for (PDefinition d : definitions)
		{
			if (assistant.isSubclassResponsibility(d))
			{
				return true;
			}
		}

		return false;
	}

	public void removeDuplicates(List<PDefinition> definitions)
	{
		LinkedList<PDefinition> fixed = new LinkedList<PDefinition>();

		for (PDefinition d : definitions)
		{
			boolean found = false;

			if (d instanceof AInheritedDefinition)
			{
				AInheritedDefinition indef = (AInheritedDefinition)d;
				List<PType> q = indef.getSuperdef().getName().getTypeQualifier();
				indef.getName().setTypeQualifier(q);
			}

			for (PDefinition e : fixed)
			{
				if (e.getName() != null && af.getLexNameTokenAssistant().isEqual(e.getName(), d.getName()))
				{
					found = true;
					break;
				}
			}

			if (!found)
			{
				fixed.add(d);
			}
		}

		if (fixed.size() < definitions.size())
		{
			definitions.clear();
			definitions.addAll(fixed);
		}
	}
	
	
	public List<PDefinition> removeAbstracts(List<PDefinition> list)
	{
		List<PDefinition> keep = new Vector<PDefinition>();
		PDefinitionAssistantTC assistant = af.createPDefinitionAssistant();
		
		for (PDefinition def: list)
		{
			if (assistant.isSubclassResponsibility(def))
			{
				boolean found = false;
				
				for (PDefinition def2: list)
				{
					if (def2 instanceof AInheritedDefinition)
					{
						AInheritedDefinition indef2 = (AInheritedDefinition)def2;
						List<PType> q = indef2.getSuperdef().getName().getTypeQualifier();
						indef2.getName().setTypeQualifier(q);
					}
					
					if (!assistant.isSubclassResponsibility(def2) &&
						af.createPDefinitionAssistant().findName(def, def2.getName(), NameScope.NAMESANDSTATE) != null)
					{
						found = true;
						break;
					}
				}
				
				if (!found)
				{
					keep.add(def);
				}
			}
			else
			{
				keep.add(def);
			}
		}
		
		return keep;
	}
	
	public void initializedCheck(AInstanceVariableDefinition ivd)
	{
		if (!ivd.getInitialized()
				&& !af.createPAccessSpecifierAssistant().isStatic(ivd.getAccess()))
		{
			TypeCheckerErrors.warning(5001, "Instance variable '"
					+ ivd.getName() + "' is not initialized", ivd.getLocation(), ivd);
		}

	}
}
