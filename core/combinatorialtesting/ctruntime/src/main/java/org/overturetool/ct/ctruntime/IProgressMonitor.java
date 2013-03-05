package org.overturetool.ct.ctruntime;

import java.io.IOException;

public interface IProgressMonitor
{

	public abstract void progress(Integer procentage) throws IOException;

	public abstract void progressStartTrace(String traceName)
			throws IOException;
	
	public abstract void progressCompleted()
	throws IOException;
	
	public abstract void progressError(String message)
	throws IOException;
	

}