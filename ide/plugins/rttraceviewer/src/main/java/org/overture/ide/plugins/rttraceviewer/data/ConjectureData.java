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

