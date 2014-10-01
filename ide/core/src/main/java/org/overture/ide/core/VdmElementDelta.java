/*
 * #%~
 * org.overture.ide.core
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
package org.overture.ide.core;

public class VdmElementDelta implements IVdmElementDelta
{
	private int kind;
	private IVdmElement element;

	public VdmElementDelta(IVdmElement element, int kind) {
		this.element = element;
		this.kind = kind;

	}

	public IVdmElement getElement()
	{
		return this.element;
	}

	public int getKind()
	{
		return this.kind;
	}

}
