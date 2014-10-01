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
package org.overture.ide.debug.core.model.internal;

import org.overture.ide.debug.core.model.ArrayVdmType;
import org.overture.ide.debug.core.model.AtomicVdmType;
import org.overture.ide.debug.core.model.ComplexVdmType;
import org.overture.ide.debug.core.model.HashVdmType;
import org.overture.ide.debug.core.model.IVdmType;
import org.overture.ide.debug.core.model.IVdmTypeFactory;
import org.overture.ide.debug.core.model.SetVdmType;

public class VdmTypeFactory implements IVdmTypeFactory
{

	public IVdmType buildType(String type)
	{

		if (type.equals("bool"))
		{
			return new AtomicVdmType(type);
		} else if (type.equals("nat"))
		{
			return new AtomicVdmType(type);
		} else if (type.equals("real"))
		{
			return new AtomicVdmType(type);
		} else if (type.equals("char"))
		{
			return new AtomicVdmType(type);
		} else if (type.equals("nat1"))
		{
			return new AtomicVdmType(type);
		} else if (type.equals("map"))
		{
			return new HashVdmType();
		} else if (type.equals("seq"))
		{
			return new ArrayVdmType();
		} else if (type.equals("set"))
		{
			return new SetVdmType();
		}

		else
		{
			return new ComplexVdmType(type);
		}
		// System.out.println("VdmTypeFactory.buildType");
		// return null;
	}

}
