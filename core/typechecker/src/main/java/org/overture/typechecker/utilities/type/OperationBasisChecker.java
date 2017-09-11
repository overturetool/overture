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
package org.overture.typechecker.utilities.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Used to determine if a type is a operation type
 * 
 * @author kel
 */
public class OperationBasisChecker extends TypeUnwrapper<Boolean>
{

	protected ITypeCheckerAssistantFactory af;

	public OperationBasisChecker(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public Boolean defaultSInvariantType(SInvariantType type)
			throws AnalysisException
	{
		if (type instanceof ANamedInvariantType)
		{
			if (type.getOpaque())
			{
				return false;
			}
			return ((ANamedInvariantType) type).getType().apply(THIS);// PTypeAssistantTC.isOperation(type.getType());
		} else
		{
			return false;
		}

	}

	@Override
	public Boolean caseAOperationType(AOperationType node)
			throws AnalysisException
	{
		return true;
	}
	
	@Override
	public Boolean caseALocalDefinition(ALocalDefinition node)
		throws AnalysisException
	{
		return !af.createPTypeAssistant().isUnknown(af.createPDefinitionAssistant().getType(node)) &&
			  af.createPTypeAssistant().isOperation(af.createPDefinitionAssistant().getType(node));
	}

	@Override
	public Boolean caseAUnionType(AUnionType type) throws AnalysisException
	{
		// return af.createAUnionTypeAssistant().getOperation(type) != null;

		// if (!type.getOpDone())
		// {
		// type.setOpDone(true);
		// type.setOpType(PTypeAssistantTC.getOperation(AstFactory.newAUnknownType(type.getLocation())));
		//
		// PTypeSet result = new PTypeSet();
		// Map<Integer, PTypeSet> params = new HashMap<Integer, PTypeSet>();
		// List<PDefinition> defs = new Vector<PDefinition>();
		//
		// for (PType t : type.getTypes())
		// {
		// if (PTypeAssistantTC.isOperation(t))
		// {
		// if (t.getDefinitions() != null)
		// {
		// defs.addAll(t.getDefinitions());
		// }
		// AOperationType op = PTypeAssistantTC.getOperation(t);
		// result.add(op.getResult());
		//
		// for (int p = 0; p < op.getParameters().size(); p++)
		// {
		// PType pt = op.getParameters().get(p);
		// PTypeSet pset = params.get(p);
		//
		// if (pset == null)
		// {
		// pset = new PTypeSet(pt);
		// params.put(p, pset);
		// } else
		// {
		// pset.add(pt);
		// }
		// }
		// }
		// }
		//
		// if (!result.isEmpty())
		// {
		// PType rtype = result.getType(type.getLocation());
		// PTypeList plist = new PTypeList();
		//
		// for (int i = 0; i < params.size(); i++)
		// {
		// PType pt = params.get(i).getType(type.getLocation());
		// plist.add(pt);
		// }
		//
		// type.setOpType(AstFactory.newAOperationType(type.getLocation(), plist, rtype));
		// type.getOpType().setDefinitions(defs);
		// } else
		// {
		// type.setOpType(null);
		// }
		// }
		//
		// return (AOperationType) type.getOpType() != null;
		return type.apply(af.getOperationTypeFinder()) != null;
	}

	@Override
	public Boolean caseAUnknownType(AUnknownType type) throws AnalysisException
	{
		return true;
	}

	@Override
	public Boolean defaultPType(PType type) throws AnalysisException
	{
		return false;
	}

}
