package org.overture.ide.plugins.showtraceNextGen.data;

public abstract class TraceResource
{
    protected Long xpos;
    protected Long ypos;

    public TraceResource()
    {
        xpos = new Long(0L);
        ypos = new Long(0L);
    }

    public void setX(Long px)
    {
        xpos = px;
    }

    public void setY(Long py)
    {
        ypos = py;
    }
    
    public Long getX()
    {
        return xpos;
    }

    public Long getY()
    {
        return ypos;
    }

}