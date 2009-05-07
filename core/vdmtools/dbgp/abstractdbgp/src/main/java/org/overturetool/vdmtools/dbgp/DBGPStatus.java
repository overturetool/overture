package org.overturetool.vdmtools.dbgp;

public enum DBGPStatus
{
	STARTING, RUNNING, BREAK, STOPPING, STOPPED;

	@Override
	public String toString()
	{
		return super.toString().toLowerCase();
	}
}