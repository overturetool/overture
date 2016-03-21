package org.overture.codegen.vdm2jml;

public class JmlSettings
{
	private boolean genInvariantFor;
	
	public JmlSettings()
	{
		this.genInvariantFor = false;
	}

	public boolean genInvariantFor()
	{
		return genInvariantFor;
	}
	
	public void setGenInvariantFor(boolean genInvariantFor)
	{
		this.genInvariantFor = genInvariantFor;
	}
}
