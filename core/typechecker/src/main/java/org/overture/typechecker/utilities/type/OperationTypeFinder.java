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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.assistant.pattern.PTypeList;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.TypeChecker;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Used to get a operation type from a type
 * 
 * @author kel
 */
public class OperationTypeFinder extends TypeUnwrapper<String, AOperationType>
{
	protected ITypeCheckerAssistantFactory af;

	public OperationTypeFinder(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public AOperationType defaultSInvariantType(SInvariantType type, String fromModule)
			throws AnalysisException
	{
		if (TypeChecker.isOpaque(type, fromModule)) return null;
		
		if (type instanceof ANamedInvariantType)
		{
			return ((ANamedInvariantType) type).getType().apply(THIS, fromModule);
		}
		else
		{
			return null;
		}
	}

	@Override
	public AOperationType caseAOperationType(AOperationType type, String fromModule)
			throws AnalysisException
	{
		return type;
	}

	@Override
	public AOperationType caseAUnionType(AUnionType type, String fromModule)
			throws AnalysisException
	{
		if (!type.getOpDone())
		{
			type.setOpDone(true);
			type.setOpType(af.createPTypeAssistant().getOperation(AstFactory.newAUnknownType(type.getLocation()), fromModule));
			PTypeSet result = new PTypeSet(af);
			Map<Integer, PTypeSet> params = new HashMap<Integer, PTypeSet>();
			List<PDefinition> defs = new Vector<PDefinition>();

			for (PType t : type.getTypes())
			{
				if (af.createPTypeAssistant().isOperation(t, fromModule))
				{
					if (t.getDefinitions() != null)
					{
						defs.addAll(t.getDefinitions());
					}
					AOperationType op = t.apply(THIS, fromModule);
					result.add(op.getResult());

					for (int p = 0; p < op.getParameters().size(); p++)
					{
						PType pt = op.getParameters().get(p);
						PTypeSet pset = params.get(p);

						if (pset == null)
						{
							pset = new PTypeSet(pt, af);
							params.put(p, pset);
						} else
						{
							pset.add(pt);
						}
					}
				}
			}

			if (!result.isEmpty())
			{
				PType rtype = result.getType(type.getLocation());
				PTypeList plist = new PTypeList();

				for (int i = 0; i < params.size(); i++)
				{
					PType pt = params.get(i).getType(type.getLocation());
					plist.add(pt);
				}

				type.setOpType(AstFactory.newAOperationType(type.getLocation(), plist, rtype));
				type.getOpType().setDefinitions(defs);
			} else
			{
				type.setOpType(null);
			}
		}

		return (AOperationType) type.getOpType();
	}

	@Override
	public AOperationType caseAUnknownType(AUnknownType type, String fromModule)
			throws AnalysisException
	{
		return AstFactory.newAOperationType(type.getLocation(), new PTypeList(), AstFactory.newAUnknownType(type.getLocation()));
	}

	@Override
	public AOperationType defaultPType(PType type, String fromModule) throws AnalysisException
	{
		assert false : "Can't getOperation of a non-operation";
		return null;
	}
}
