/*
 * #%~
 * The Overture Abstract Syntax Tree
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
package org.overture.ast.util;

import java.util.List;
import java.util.TreeSet;

import org.overture.ast.assistant.IAstAssistantFactory;
import org.overture.ast.assistant.pattern.PTypeList;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.ASeq1SeqType;
import org.overture.ast.types.ASeqSeqType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SNumericBasicType;

@SuppressWarnings("serial")
public class PTypeSet extends TreeSet<PType>
{
	public IAstAssistantFactory assistantFactory;

	public PTypeSet(IAstAssistantFactory af)
	{
		super(new PTypeComparator());
		assistantFactory = af;
	}

	public PTypeSet(PType t, IAstAssistantFactory af)
	{
		super(new PTypeComparator());
		assistantFactory = af;
		add(t);
	}

	public PTypeSet(PType t1, PType t2, IAstAssistantFactory af)
	{
		super(new PTypeComparator());
		assistantFactory = af;
		add(t1);
		add(t2);
	}

	public PTypeSet(List<PType> types, IAstAssistantFactory af)
	{
		super(new PTypeComparator());
		assistantFactory = af;
		addAll(types);
	}

	@Override
	public boolean add(PType t) // TODO: Create visitor over this method???? Need a assistantFactory but the call is
								// from 1770 places. gkanos
	{
		if (t instanceof ASeq1SeqType)
		{
			// If we add a Seq1Type, and there is already a SeqType in the set
			// we ignore the Seq1Type.

			ASeq1SeqType s1t = (ASeq1SeqType) t;
			ASeqSeqType st = AstFactory.newASeqSeqType(s1t.getLocation(), s1t.getSeqof());
			if (contains(st))
			{
				return false; // Was already there
			}
		} else if (t instanceof ASeqSeqType)
		{
			// If we add a SeqType, and there is already a Seq1Type in the set
			// we replace the Seq1Type.

			ASeqSeqType st = (ASeqSeqType) t;
			ASeq1SeqType s1t = AstFactory.newASeq1SeqType(st.getLocation(), st.getSeqof());

			if (contains(s1t))
			{
				remove(s1t); // Replace seq with seq1
			}
		} else if (t instanceof SNumericBasicType)
		{
			for (PType x : this)// what the this keyword refer to.. gkanos
			{
				if (x instanceof SNumericBasicType)
				{
					// this is the only call that causes problem. gkanos
					if (assistantFactory.createSNumericBasicTypeAssistant().getWeight(assistantFactory.createPTypeAssistant().getNumeric(x)) < assistantFactory.createSNumericBasicTypeAssistant().getWeight(assistantFactory.createPTypeAssistant().getNumeric(t)))
					{
						remove(x);
						break;
					} else
					{
						return false; // Was already there
					}
				}
			}
		}
		else if (t instanceof AOptionalType)
		{
			AOptionalType opt = (AOptionalType)t;
			
			if (!(opt.getType() instanceof AUnknownType) && contains(opt.getType()))
			{
				remove(opt.getType());	// Because T | [T] = [T]
			}
		}


		return super.add(t);
	}

	public PType getType(ILexLocation location)
	{
		// If there are any Optional(Unknowns) these are the result of
		// nil values, which set the overall type as optional. Other
		// optional types stay.

		boolean optional = false;

		// You get less confusing results without this, it seems...

		// Iterator<PType> tit = this.iterator();
		//
		// while (tit.hasNext())
		// {
		// PType t = tit.next();
		//
		// if (t instanceof AOptionalType)
		// {
		// AOptionalType ot = (AOptionalType) t;
		//
		// if (ot.getType() instanceof AUnknownType)
		// {
		// if (this.size() > 1)
		// {
		// tit.remove();
		// optional = true;
		// } else
		// {
		// optional = false;
		// }
		// }
		// }
		// }

		assert this.size() > 0 : "Getting type of empty TypeSet";
		PType result;

		if (this.size() == 1)
		{
			result = this.iterator().next();
		} else
		{

			PTypeList types = new PTypeList();

			for (PType pType : this)
			{
				types.add(pType);// .clone()
			}

			result = AstFactory.newAUnionType(location, types);
		}

		return optional ? AstFactory.newAOptionalType(location, result)
				: result;
	}

	@Override
	public String toString()
	{
		return Utils.setToString(this, ", ");
	}
}
