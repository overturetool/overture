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
package org.overture.ast.intf.lex;

import java.util.List;

import org.overture.ast.types.PType;

public interface ILexNameToken extends ILexIdentifierToken
{

	String getFullName();

	int compareTo(ILexNameToken o);

	ILexNameToken copy();

	ILexNameToken getClassName();

	boolean getExplicit();

	ILexNameToken clone();

	ILexNameToken getExplicit(boolean ex);

	ILexIdentifierToken getIdentifier();

	ILexNameToken getInitName(ILexLocation l);

	ILexNameToken getInvName(ILexLocation l);

	boolean isReserved();

	ILexLocation getLocation();

	ILexNameToken getModifiedName(String classname);

	String getModule();

	String getName();

	ILexNameToken getNewName();

	boolean getOld();

	ILexNameToken getOldName();

	ILexNameToken getPerName(ILexLocation loc);

	ILexNameToken getPostName(ILexLocation l);

	ILexNameToken getPreName(ILexLocation l);

	ILexNameToken getSelfName();

	String getSimpleName();

	ILexNameToken getThreadName();

	ILexNameToken getThreadName(ILexLocation loc);

	List<PType> getTypeQualifier();

	List<PType> typeQualifier();

	boolean isOld();

	boolean matches(ILexNameToken other);

	void setTypeQualifier(List<PType> types);

	// int hashCode(IAstAssistantFactory assistantFactory);
	// Try to declare it here as requested by eclipse.
	// Didn't seem to work.

}
