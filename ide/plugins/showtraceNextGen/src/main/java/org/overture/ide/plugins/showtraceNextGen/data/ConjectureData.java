package org.overture.ide.plugins.showtraceNextGen.data;

import java.util.Vector;

public class ConjectureData
{
    Vector<Conjecture> conjectures;
    
    public ConjectureData()
    {
    	conjectures = new Vector<Conjecture>();
    }
    
    public void addConjecture(Conjecture c)
    {
    	//TODO: Check if conjecture already exists in the list
    	conjectures.add(c);
    }
    
    public Vector<Conjecture> getConjecture(long time)
    {
    	Vector<Conjecture> result = new Vector<Conjecture>();
    	
    	for(Conjecture c : conjectures)
    	{
    		if(c.getTime() == time)
    			result.add(c);
    	}

    	return result;
    }
    
}

