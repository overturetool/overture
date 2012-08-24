package org.overture.ide.plugins.showtraceNextGen.data;

public class TraceObject extends TraceResource
{
    private Long id;
    private String name;
    
    public TraceObject(Long id, String name)
    {
    	this.id = id;
    	this.name = name;
    }
        
    public Long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

}