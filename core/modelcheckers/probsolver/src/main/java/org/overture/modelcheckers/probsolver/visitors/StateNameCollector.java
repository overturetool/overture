/*
 * #%~
 * Integration of the ProB Solver for VDM
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
package org.overture.modelcheckers.probsolver.visitors;

import java.util.Collection;
import java.util.HashSet;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.types.AFieldField;

public class StateNameCollector extends DepthFirstAnalysisAdaptor
{
	class StateNamedObtainedException extends AnalysisException
	{
		/**
		 * serial
		 */
		private static final long serialVersionUID = 1L;
		public final Collection<ILexNameToken> names;

		public StateNamedObtainedException(Collection<ILexNameToken> names)
		{
			this.names = names;
		}
	}

	public static Collection<ILexNameToken> collectStateNamesFromOwningDefinition(
			PDefinition def)
	{
		INode container = def.getAncestor(SClassDefinition.class);
		if (container == null)
		{
			container = def.getAncestor(AModuleModules.class);
		}

		try
		{
			container.apply(new StateNameCollector());
		} catch (StateNamedObtainedException e)
		{
			return e.names;
		} catch (AnalysisException e)
		{
			e.printStackTrace();
		}
		return new Vector<ILexNameToken>();
	}
	
	public final Collection<ILexNameToken> names = new HashSet<ILexNameToken>();

//	@Override
//	public void caseAStateDefinition(AStateDefinition node)
//			throws AnalysisException
//	{
//		Collection<ILexNameToken> collection = new Vector<ILexNameToken>();
//		for (AFieldField field : node.getFields())
//		{
//			collection.add(field.getTagname());
//		}
//		throw new StateNamedObtainedException(collection);
//	}
	
	@Override
	public void outAStateDefinition(AStateDefinition node)
			throws AnalysisException
	{
		throw new StateNamedObtainedException(names);
	}
	
	@Override
	public void outAClassClassDefinition(AClassClassDefinition node)
			throws AnalysisException
	{
		outSClassDefinition(node);
	}
	
	@Override
	public void outSClassDefinition(SClassDefinition node)
			throws AnalysisException
	{
		throw new StateNamedObtainedException(names);
	}

	
	@Override
	public void caseAInstanceVariableDefinition(AInstanceVariableDefinition node)
			throws AnalysisException
	{
		names.add(node.getName());
	}
	
	@Override
	public void caseAFieldField(AFieldField node) throws AnalysisException
	{
		names.add(node.getTagname());
	}
	
	

}
