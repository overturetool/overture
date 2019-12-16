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
package org.overture.typechecker;

import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class LexNameTokenAssistant implements IAstAssistant
{
	public ITypeCheckerAssistantFactory af;

	public LexNameTokenAssistant(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public boolean isEqual(ILexNameToken token, Object other)
	{
		if (!(other instanceof ILexNameToken))
		{
			return false;
		}

		ILexNameToken lother = (ILexNameToken) other;

//		if (token.getTypeQualifier() != null
//				&& lother.getTypeQualifier() != null)
//		{
//			if (!af.getTypeComparator().compatible(token.getTypeQualifier(), lother.getTypeQualifier()))
//			{
//				return false;
//			}
//		} else if (token.getTypeQualifier() != null
//				&& lother.getTypeQualifier() == null
//				|| token.getTypeQualifier() == null
//				&& lother.getTypeQualifier() != null)
//		{
//			return false;
//		}
//
//		return token.matches(lother);

		if (!token.matches(lother))
		{
			return false;
		}
		else if (token.getTypeQualifier() != null
				&& lother.getTypeQualifier() != null)
		{
			return af.getTypeComparator().compatible(token.getTypeQualifier(), lother.getTypeQualifier());
		}
		else
		{
			return !(token.getTypeQualifier() != null
					&& lother.getTypeQualifier() == null
					|| token.getTypeQualifier() == null
					&& lother.getTypeQualifier() != null);
		}
	}
}
