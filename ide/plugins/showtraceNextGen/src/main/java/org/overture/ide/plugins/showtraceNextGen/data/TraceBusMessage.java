package org.overture.ide.plugins.showtraceNextGen.data;

public class TraceBusMessage
{
    private Long id;
    private Long busId;
    private Long fromCpuId;
    private Long toCpuId;
    private Long callerThreadId;
    
    public TraceBusMessage(Long id, Long busId, Long fromCpuId, Long toCpuId, Long callerThreadId)
    {
    	this.id = id;
    	this.busId = busId;
    	this.fromCpuId = fromCpuId;
    	this.toCpuId = toCpuId;
    	this.callerThreadId = callerThreadId;	    	
    }

    public Long getMsgId()
    {
        return id;
    }

    public Long getBusId()
    {
    	return busId;
    }

    public Long getFromCpu()
    {
        return fromCpuId;
    }

    public Long getFromThread()
    {
        return callerThreadId;
    }

    public Long getToCpu()
    {
        return toCpuId;
    }


}