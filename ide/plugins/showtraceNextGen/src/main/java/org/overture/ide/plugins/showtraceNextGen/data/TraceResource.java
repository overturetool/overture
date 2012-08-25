package org.overture.ide.plugins.showtraceNextGen.data;

public abstract class TraceResource
{
    protected Long xpos;
    protected Long ypos;
    protected Boolean idle;
    protected Boolean visible;
    
    public TraceResource()
    {
        xpos = new Long(0L);
        ypos = new Long(0L);
        idle = new Boolean(true);
        visible = new Boolean(false);
    }

    public void setX(Long px)
    {
        xpos = px;
    }

    public void setY(Long py)
    {
        ypos = py;
    }
    
    public void setIdle(Boolean pidle)
    {
        idle = pidle;
    }

    public Long getX()
    {
        return xpos;
    }

    public Long getY()
    {
        return ypos;
    }
    
    public Boolean isIdle()
    {
        return idle;
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