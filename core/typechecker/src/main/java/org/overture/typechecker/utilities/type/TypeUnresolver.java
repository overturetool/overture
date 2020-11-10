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

import org.overture.ast.analysis.AnalysisAdaptor;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.SSetType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * This class set a type to unresolved.
 * 
 * @author kel
 */
public class TypeUnresolver extends AnalysisAdaptor
{
	protected ITypeCheckerAssistantFactory af;

	public TypeUnresolver(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public void caseABracketType(ABracketType type) throws AnalysisException
	{
		if (!type.getResolved())
		{
			return;
		} else
		{
			type.setResolved(false);
		}
		type.apply(THIS);
	}

	@Override
	public void caseAClassType(AClassType type) throws AnalysisException
	{
		if (type.getResolved())
		{
			type.setResolved(false);

			for (PDefinition d : type.getClassdef().getDefinitions())
			{
				// PTypeAssistantTC.unResolve(af.createPDefinitionAssistant().getType(d));
				af.createPTypeAssistant().unResolve(af.createPDefinitionAssistant().getType(d));
			}
		}
	}

	@Override
	public void caseAFunctionType(AFunctionType type) throws AnalysisException
	{
		if (!type.getResolved())
		{
			return;
		} else
		{
			type.setResolved(false);
		}

		for (PType ft : type.getParameters())
		{
			ft.apply(THIS);
		}

		type.getResult().apply(THIS);
	}

	@Override
	public void caseANamedInvariantType(ANamedInvariantType type)
			throws AnalysisException
	{
		if (!type.getResolved())
		{
			return;
		} else
		{
			type.setResolved(false);
		}
		// PTypeAssistantTC.unResolve(type.getType());
		type.getType().apply(THIS);
	}

	@Override
	public void caseARecordInvariantType(ARecordInvariantType type)
			throws AnalysisException
	{
		if (!type.getResolved())
		{
			return;
		} else
		{
			type.setResolved(false);
		}

		for (AFieldField f : type.getFields())
		{
			af.createPTypeAssistant().unResolve(f.getType());
		}
	}

	@Override
	public void defaultSInvariantType(SInvariantType type)
			throws AnalysisException
	{
		type.setResolved(false);
	}

	@Override
	public void defaultSMapType(SMapType type) throws AnalysisException
	{
		if (!type.getResolved())
		{
			return;
		} else
		{
			type.setResolved(false);
		}

		if (!type.getEmpty())
		{
			type.getFrom().apply(THIS);
			type.getTo().apply(THIS);
		}
	}

	@Override
	public void caseAOperationType(AOperationType type)
			throws AnalysisException
	{
		if (!type.getResolved())
		{
			return;
		} else
		{
			type.setResolved(false);
		}

		for (PType ot : type.getParameters())
		{
			ot.apply(THIS);
		}

		type.getResult().apply(THIS);
	}

	@Override
	public void caseAOptionalType(AOptionalType type) throws AnalysisException
	{
		if (!type.getResolved())
		{
			return;
		} else
		{
			type.setResolved(false);
		}
		// PTypeAssistantTC.unResolve(type.getType());
		type.getType().apply(THIS);
	}

	@Override
	public void caseAProductType(AProductType type) throws AnalysisException
	{
		if (!type.getResolved())
		{
			return;
		} else
		{
			type.setResolved(false);
		}

		for (PType t : type.getTypes())
		{
			t.apply(THIS);
		}
	}

	@Override
	public void defaultSSeqType(SSeqType type) throws AnalysisException
	{
		if (!type.getResolved())
		{
			return;
		} else
		{
			type.setResolved(false);
		}
		type.getSeqof().apply(THIS);
	}

	@Override
	public void defaultSSetType(SSetType type) throws AnalysisException
	{
		if (!type.getResolved())
		{
			return;
		} else
		{
			type.setResolved(false);
		}
		type.getSetof().apply(THIS);
	}

	@Override
	public void caseAUnionType(AUnionType type) throws AnalysisException
	{
		if (!type.getResolved())
		{
			return;
		} else
		{
			type.setResolved(false);
		}

		for (PType t : type.getTypes())
		{
			t.apply(THIS);
		}
	}

	@Override
	public void defaultPType(PType type) throws AnalysisException
	{
		type.setResolved(false);
	}
}
