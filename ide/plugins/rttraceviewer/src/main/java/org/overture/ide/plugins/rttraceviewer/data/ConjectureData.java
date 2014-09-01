/*
 * #%~
 * RT Trace Viewer Plugin
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
package org.overture.ide.plugins.rttraceviewer.data;

import java.util.Vector;

public class ConjectureData extends Vector<Conjecture>
{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

//    Vector<Conjecture> conjectures;
    
    public ConjectureData()
    {
//    	conjectures = new Vector<Conjecture>();
    }
    
    public void addConjecture(Conjecture c)
    {
    	//TODO: Check if conjecture already exists in the list
    	add(c);
    }
    
    public Vector<Conjecture> getConjecture(long time)
    {
    	Vector<Conjecture> result = new Vector<Conjecture>();
    	
    	for(Conjecture c : this)
    	{
    		if(c.getTime() == time)
    			result.add(c);
    	}

    	return result;
    }
    
}

