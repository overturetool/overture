/*
 * #%~
 * VDM Tools Code Generator library
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
package jp.co.csk.vdm.toolbox.VDM;


// Referenced classes of package jp.co.csk.vdm.toolbox.VDM:
//            VDMRunTimeException

public class INDEX_OUT_OF_RANGE extends VDMRunTimeException
{

	/**
	 * Overture Tool
	 */
	private static final long serialVersionUID = 8373799002740093350L;

	public INDEX_OUT_OF_RANGE()
    {
        super("Index out of range.");
    }

    public INDEX_OUT_OF_RANGE(String s)
    {
        super(s + ": Index out of range.");
    }
}
