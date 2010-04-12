package org.overture.ide.debug.core.model;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;


public interface IVdmBreakpoint extends IBreakpoint {
	/**
	 * Suspend policy constant indicating a breakpoint will
	 * suspend the target VM when hit.
	 */
	public static final int SUSPEND_VM = 1;
	
	/**
	 * Default suspend policy constant indicating a breakpoint will
	 * suspend only the thread in which it occurred.
	 */
	public static final int SUSPEND_THREAD = 2;
	
	/**
	 * Returns whether this breakpoint is installed in at least
	 * one debug target.
	 * 
	 * @return whether this breakpoint is installed
	 * @exception CoreException if unable to access the property 
	 * 	on this breakpoint's underlying marker
	 */
	public boolean isInstalled() throws CoreException;
	/**
	 * Returns the fully qualified name of the type this breakpoint
	 * is located in, or <code>null</code> if this breakpoint
	 * is not located in a specific type - for example, a pattern breakpoint.
	 * 
	 * @return the fully qualified name of the type this breakpoint
	 *  is located in, or <code>null</code>
	 * @exception CoreException if unable to access the property
	 * 	from this breakpoint's underlying marker
	 */
	public String getTypeName() throws CoreException;
	/**
	 * Returns this breakpoint's hit count or, -1 if this
	 * breakpoint does not have a hit count.
	 * 
	 * @return this breakpoint's hit count, or -1
	 * @exception CoreException if unable to access the property
	 *  from this breakpoint's underlying marker
	 */
	public int getHitCount() throws CoreException;
	/**
	 * Sets the hit count attribute of this breakpoint.
	 * If this breakpoint is currently disabled and the hit count
	 * is set greater than -1, this breakpoint is automatically enabled.
	 * 
	 * @param count the new hit count
	 * @exception CoreException if unable to set the property
	 * 	on this breakpoint's underlying marker
	 */
	public void setHitCount(int count) throws CoreException;	
	
	/**
	 * Sets whether all threads in the target VM will be suspended
	 * when this breakpoint is hit. When <code>SUSPEND_VM</code> the target
	 * VM is suspended, and when <code>SUSPEND_THREAD</code> only the thread
	 * in which this breakpoint occurred is suspended.
	 * 
	 * @param suspendPolicy one of <code>SUSPEND_VM</code> or
	 *  <code>SUSPEND_THREAD</code>
	 * @exception CoreException if unable to set the property
	 * 	on this breakpoint's underlying marker
	 */
	public void setSuspendPolicy(int suspendPolicy) throws CoreException;
	
	/**
	 * Returns the suspend policy used by this breakpoint, one of
	 * <code>SUSPEND_VM</code> or <code>SUSPEND_THREAD</code>.
	 * 
	 * @return one of <code>SUSPEND_VM</code> or <code>SUSPEND_THREAD</code>
	 * @exception CoreException if unable to access the property 
	 * 	from this breakpoint's underlying marker
	 */
	public int getSuspendPolicy() throws CoreException;
	
	/**
	 * Returns a collection of identifiers of breakpoint listener extensions registered
	 * for this breakpoint, possibly empty.
	 * 
	 * @return breakpoint listener extension identifiers registered on this breakpoint
	 * @throws CoreException if unable to retrieve the collection
	 * @since 3.5
	 */
	public String[] getBreakpointListeners() throws CoreException;
	
	/**
	 * Adds the breakpoint listener extension with specified identifier to this breakpoint.
	 * Has no effect if an identical listener is already registered. 
	 *  
	 * @param identifier breakpoint listener extension identifier
	 * @throws CoreException if unable to add the listener
	 * @since 3.5
	 */
	public void addBreakpointListener(String identifier) throws CoreException;
	
	/**
	 * Removes the breakpoint listener extension with the specified identifier from this
	 * breakpoint and returns whether the listener was removed.
	 * 
	 * @param identifier breakpoint listener extension identifier 
	 * @return whether the listener was removed
	 * @throws CoreException if an error occurs removing the listener
	 * @since 3.5
	 */
	public boolean removeBreakpointListener(String identifier) throws CoreException;
}
