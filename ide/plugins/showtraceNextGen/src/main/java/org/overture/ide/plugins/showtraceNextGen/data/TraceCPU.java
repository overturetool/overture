package org.overture.ide.plugins.showtraceNextGen.data;

import java.util.Vector;

public class TraceCPU extends TraceResource
{
    private Long id;
    private String name;
    private Boolean isVirtual;
    private Vector<Long> threads;
    private Vector<Long> objects;
    private Long current_thread;
    
    public TraceCPU(Long id, String name, boolean isVirtual)
    {
    	threads = new Vector<Long>();
    	objects = new Vector<Long>();
    	
    	current_thread = null;

		this.id = id;
        this.name = name;
        this.isVirtual = isVirtual;
    }

    public Long getId()
    {
        return id;
    }
 
    public String getName()
    {
        return name;
    }

    public Boolean isVirtual()
    {
        return isVirtual;
    }
  
    public void setCurrentThread(Long curthrd)
    {
    	current_thread = curthrd;
    	//this.setIdle(current_thread == null);
    }

    public Boolean hasCurrentThread()
    {
        return current_thread != null;
    }

    public Long getCurrentThread()
    {
    	return current_thread;
    }
    
    public void addThreadId(Long thrid)
    {
    	threads.add(thrid);
    }
    
    public Vector<Long> getThreadIds()
    {
    	return threads;
    }
    
    public void addObject(Long obj)
    {
    	objects.add(obj);
    }
    
    public Vector<Long> getObjectIds()
    {
    	return objects;
    }
    
    public Boolean hasObject(Long pobjid)
    {
    	return objects.contains(pobjid);
    }
}