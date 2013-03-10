package org.overture.ide.plugins.rttraceviewer.data;

public class TraceCPU extends TraceResource
{
    private Long id;
    private String name;
    private Boolean isVirtual;
    private Long current_thread;
    protected Boolean idle;
    
    public TraceCPU(Long id, String name, boolean isVirtual)
    {
    	current_thread = null;

		this.id = id;
        this.name = name;
        this.isVirtual = isVirtual;
        idle = new Boolean(true);
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

    public void setIdle(Boolean pidle)
    {
        idle = pidle;
    }
    
    public Boolean isIdle()
    {
        return idle;
    }
    
    public Boolean hasCurrentThread()
    {
        return current_thread != null;
    }

    public Long getCurrentThread()
    {
    	return current_thread;
    }
    
}
