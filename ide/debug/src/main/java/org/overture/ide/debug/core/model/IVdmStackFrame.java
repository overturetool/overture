/*
 * #%~
 * org.overture.ide.debug
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
package org.overture.ide.debug.core.model;

import java.net.URI;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IStackFrame;

public interface IVdmStackFrame extends IStackFrame
{
	IVdmStack getStack();

	IVdmThread getVdmThread();

	int getLevel();

	String getSourceLine();

	/**
	 * Return line number of the command start or -1 if not available
	 * 
	 * @return
	 */
	int getBeginLine();

	/**
	 * Return column number of the command start or -1 if not available
	 * 
	 * @return
	 */
	int getBeginColumn();

	/**
	 * Return line number of the command end or -1 if not available
	 * 
	 * @return
	 */
	int getEndLine();

	/**
	 * Return column number of the command end or -1 if not available
	 * 
	 * @return
	 */
	int getEndColumn();

	URI getSourceURI();

	IVdmVariable findVariable(String varName) throws DebugException;

	String getWhere();
}
