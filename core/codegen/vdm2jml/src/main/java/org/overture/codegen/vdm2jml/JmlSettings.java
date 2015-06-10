package org.overture.codegen.vdm2jml;

public class JmlSettings
{
	private boolean injectReportCalls;

	public JmlSettings()
	{
		super();
		this.injectReportCalls = false;
	}

	public boolean injectReportCalls()
	{
		return injectReportCalls;
	}

	public void setInjectReportCalls(boolean injectReportCalls)
	{
		this.injectReportCalls = injectReportCalls;
	}
}
