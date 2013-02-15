package org.overture.ide.plugins.showtraceNextGen.data;

import java.util.Vector;

public class TraceBus extends TraceResource
{
    private Long id;
    private String name;
    private Boolean isVirtual;
    private Vector<Long> connectedCPUs;

    public TraceBus(Long id, String name, Boolean isVirtual, Vector<Long> connectedCPUs)
    {
		this.id = id;
		this.name = name;
		this.isVirtual = isVirtual;    	
		this.connectedCPUs = connectedCPUs;
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
    
    public Vector<Long> getConnectedCpuIds()
    {
    	return connectedCPUs;
    }

}