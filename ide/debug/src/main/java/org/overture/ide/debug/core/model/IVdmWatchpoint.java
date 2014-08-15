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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IWatchpoint;

/**
 * A breakpoint on a field. If a watchpoint is an access watchpoint, it will suspend execution when its field is
 * accessed. If a watchpoint is a modification watchpoint, it will suspend execution when its field is modified.
 */
public interface IVdmWatchpoint extends IVdmLineBreakpoint, IWatchpoint
{
	/**
	 * Returns the name of the field associated with this watchpoint
	 * 
	 * @return field the name of the field on which this watchpoint is installed
	 * @exception CoreException
	 *                if unable to access the property on this breakpoint's underlying marker
	 */
	String getFieldName() throws CoreException;
}
