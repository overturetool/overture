package org.overture.ide.plugins.rttraceviewer.data;

public class TraceObject extends TraceResource
{
    private Long id;
    private String name;
    protected Boolean visible;
    public TraceObject(Long id, String name)
    {
    	this.id = id;
    	this.name = name;
    	visible = new Boolean(false);
    }
        
    public Long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public Boolean isVisible()
    {
    	return visible;
    }

    public void setVisible(Boolean isVisible)
    {
    	this.visible = isVisible;
    }
}
