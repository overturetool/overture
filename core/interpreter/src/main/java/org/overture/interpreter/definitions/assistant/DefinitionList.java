/*******************************************************************************
 *
 *	Copyright (c) 2008 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overture.interpreter.definitions.assistant;

import java.util.Vector;




import org.overture.interpreter.ast.definitions.PDefinitionInterpreter;
import org.overturetool.interpreter.vdmj.lex.LexNameList;
import org.overturetool.interpreter.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.runtime.Context;

import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.ValueList;


/**
 * A class to hold a list of Definitions.
 */

@SuppressWarnings("serial")
public class DefinitionList extends Vector<PDefinitionInterpreter>
{
	public DefinitionList()
	{
		super();
	}

	public DefinitionList(PDefinitionInterpreter definition)
	{
		add(definition);
	}

//	public void implicitDefinitions(Environment env)
//	{
//		for (Definition d: this)
//		{
//			d.implicitDefinitions(env);
//		}
//	}

	public DefinitionList singleDefinitions()
	{
		DefinitionList all = new DefinitionList();

		for (PDefinitionInterpreter d: this)
		{
			all.addAll(d.getDefinitions());
		}

		return all;
	}

	

	

//	public PDefinitionInterpreter findName(LexNameToken name, NameScope scope)
//	{
//		for (PDefinitionInterpreter d: this)
//		{
//			PDefinitionInterpreter def = d.findName(name, scope);
//
//			if (def != null)
//			{
//				return def;
//			}
//		}
//
//		return null;
//	}
//
//	public Definition findType(LexNameToken name, String fromModule)
//	{
//		for (Definition d: this)
//		{
//			Definition def = d.findType(name, fromModule);
//
//			if (def != null)
//			{
//				return def;
//			}
//		}
//
//		return null;
//	}
//
//	public StateDefinition findStateDefinition()
//	{
//   		for (Definition d: this)
//		{
//			if (d instanceof StateDefinition)
//			{
//				return (StateDefinition)d;
//			}
//		}
//
//   		return null;
//	}
//
//	public Statement findStatement(int lineno)
//	{
//   		for (Definition d: this)
//		{
//			Statement found = d.findStatement(lineno);
//
//			if (found != null)
//			{
//				return found;
//			}
//		}
//
//   		return null;
//	}
//
//	public Expression findExpression(int lineno)
//	{
//   		for (Definition d: this)
//		{
//			Expression found = d.findExpression(lineno);
//
//			if (found != null)
//			{
//				return found;
//			}
//		}
//
//   		return null;
//	}

//	public NameValuePairList getNamedValues(Context ctxt)
//	{
//		NameValuePairList nvl = new NameValuePairList();
//
//		for (PDefinitionInterpreter d: this)
//		{
//			nvl.addAll(d.getNamedValues(ctxt));
//		}
//
//		return nvl;
//	}

//	public LexNameList getVariableNames()
//	{
//		LexNameList variableNames = new LexNameList();
//
//		for (PDefinitionInterpreter d: this)
//		{
//			variableNames.addAll(d.getVariableNames());
//		}
//
//		return variableNames;
//	}
//
//	public void setAccessibility(AccessSpecifier access)
//	{
//		for (Definition d: this)
//		{
//			d.setAccessSpecifier(access);
//		}
//	}
//
//	public void setClassDefinition(ClassDefinition def)
//	{
//		for (Definition d: this)
//		{
//			d.setClassDefinition(def);
//		}
//	}
//
//	@Override
//	public String toString()
//	{
//		StringBuilder sb = new StringBuilder();
//
//		for (Definition d: this)
//		{
//			sb.append(d.accessSpecifier.toString());
//			sb.append(" ");
//
//			sb.append(d.kind() + " " + d.getVariableNames() + ":" + d.getType());
//			sb.append("\n");
//		}
//
//		return sb.toString();
//	}
//
//	public DefinitionSet findMatches(LexNameToken name)
//	{
//		DefinitionSet set = new DefinitionSet();
//
//		for (Definition d: singleDefinitions())
//		{
//			if (d.isFunctionOrOperation() && d.name.matches(name))
//			{
//				set.add(d);
//			}
//		}
//
//		return set;
//	}
//
//	public ProofObligationList getProofObligations(POContextStack ctxt)
//	{
//		ProofObligationList obligations = new ProofObligationList();
//
//		for (Definition d: this)
//		{
//			ctxt.push(new PONameContext(d.getVariableNames()));
//			obligations.addAll(d.getProofObligations(ctxt));
//			ctxt.pop();
//		}
//
//		return obligations;
//	}
//
//	public void markUsed()
//    {
//		for (Definition d: this)
//		{
//			d.markUsed();
//		}
//    }
//
//	public void initializedCheck()
//	{
//		for (Definition d: this)
//		{
//			if (d instanceof InstanceVariableDefinition)
//			{
//				InstanceVariableDefinition ivd = (InstanceVariableDefinition)d;
//				ivd.initializedCheck();
//			}
//		}
// 	}
//
//	public ValueList getValues(Context ctxt)
//	{
//		ValueList list = new ValueList();
//
//		for (Definition d: this)
//		{
//			list.addAll(d.getValues(ctxt));
//		}
//
//		return list;
//	}
}
