package org.overture.codegen.runtime;

/**
 * This class was defined as intermediate layer class in order to extend through it the Thread class and activate the
 * daemon property for the threads that are started. The daemon property needs to be active in order for the Java
 * threads to have similar behavior as the VDM threads in respect to the JVM and the VDM debugger.
 * 
 * @author gkanos
 */
public class VDMThread extends Thread
{
	public VDMThread()
	{
		setDaemon(true);
	}
}
