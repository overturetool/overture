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

import java.io.File;

import org.overture.ast.lex.LexLocation;
import org.overture.ast.node.ExternalNode;

public interface ILexLocation extends ExternalNode
{

	String toString();

	String toShortString();

	public LexLocation clone();

	boolean within(ILexLocation span);

	public void executable(boolean exe);

	public void hit();

	public boolean getExecutable();

	public long getHits();

	public void setHits(long hits);

	/**
	 * The filename of the token.
	 * 
	 * @return
	 */
	public File getFile();

	/**
	 * The module/class name of the token.
	 * 
	 * @return
	 */
	public String getModule();

	/**
	 * The line number of the start of the token.
	 * 
	 * @return
	 */
	public int getStartLine();

	/**
	 * The character position of the start of the token.
	 * 
	 * @return
	 */
	public int getStartPos();

	/**
	 * The last line of the token.
	 * 
	 * @return
	 */
	public int getEndLine();

	/**
	 * The character position of the end of the token.
	 * 
	 * @return
	 */
	public int getEndPos();

	public int getStartOffset();

	public int getEndOffset();

}
