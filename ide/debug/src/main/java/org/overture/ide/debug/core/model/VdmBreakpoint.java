package org.overture.ide.debug.core.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.Breakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.overture.ide.debug.core.IDebugConstants;

public class VdmBreakpoint extends Breakpoint implements IVdmBreakpoint {


	public static final String BREAKPOINT_LISTENERS = IDebugConstants.EXTENSION_POINT_VDM_BREAKPOINT_LISTENERS;
	protected static final String HIT_COUNT = "org.overture.ide.debug.core.hitCount";
	
	
	/**
	 * Breakpoint attribute storing the expired value (value <code>"org.eclipse.jdt.debug.core.expired"</code>).
	 * This attribute is stored as a <code>boolean</code>. Once a hit count has
	 * been reached, a breakpoint is considered to be "expired".
	 */
	protected static final String EXPIRED = "org.overture.ide.debug.core.expired"; //$NON-NLS-1$
	
	/**
	 * Breakpoint attribute storing the number of debug targets a
	 * breakpoint is installed in (value <code>"org.eclipse.jdt.debug.core.installCount"</code>).
	 * This attribute is a <code>int</code>.
	 */
	protected static final String INSTALL_COUNT = "org.overture.ide.debug.core.installCount"; //$NON-NLS-1$	
	
	/**
	 * Breakpoint attribute storing suspend policy code for 
	 * this breakpoint.
	 * (value <code>"org.eclipse.jdt.debug.core.suspendPolicy</code>).
	 * This attribute is an <code>int</code> corresponding
	 * to <code>IJavaBreakpoint.SUSPEND_VM</code> or
	 * <code>IJavaBreakpoint.SUSPEND_THREAD</code>.
	 */
	protected static final String SUSPEND_POLICY = "org.overture.ide.debug.core.suspendPolicy"; 
	
	/**
	 * Breakpoint attribute storing the fully qualified name of the type
	 * this breakpoint is located in.
	 * (value <code>"org.eclipse.jdt.debug.core.typeName"</code>). This attribute is a <code>String</code>.
	 */
	protected static final String TYPE_NAME = "org.overture.ide.debug.core.typeName";
	
	/**
	 * List of breakpoint listener identifiers corresponding to breakpoint
	 * listener extensions. Listeners are cached with the breakpoint object
	 * such that they can be notified when a breakpoint is removed.
	 */
	private List<String> fBreakpointListenerIds = null;

	/**
	 * Stores the type name that this breakpoint was last installed
	 * in. When a breakpoint is created, the TYPE_NAME attribute assigned to it
	 * is that of its top level enclosing type. When installed, the type
	 * may actually be an inner type. We need to keep track of the type 
	 * type the breakpoint was installed in, in case we need to re-install
	 * the breakpoint for HCR (i.e. in case an inner type is HCR'd).
	 */
	protected String fInstalledTypeName = null;
	
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.debug.core.IJavaBreakpoint#addBreakpointListener(java.lang.String)
	 */
	public synchronized void addBreakpointListener(String identifier) throws CoreException {
		if (fBreakpointListenerIds == null) {
			fBreakpointListenerIds = new ArrayList<String>();
		}
		if (!fBreakpointListenerIds.contains(identifier)) {
			fBreakpointListenerIds.add(identifier);
			writeBreakpointListeners();
		}
	}

	/**
	 * Writes the current breakpoint listener collection to the underlying marker.
	 * 
	 * @throws CoreException
	 */
	private void writeBreakpointListeners() throws CoreException {
		StringBuffer buf = new StringBuffer();
		Iterator<String> iterator = fBreakpointListenerIds.iterator();
		while (iterator.hasNext()) {
			buf.append((String)iterator.next());
			if (iterator.hasNext()) {
				buf.append(","); //$NON-NLS-1$
			}
		}
		setAttribute(BREAKPOINT_LISTENERS, buf.toString());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.debug.core.IJavaBreakpoint#getBreakpointListeners()
	 */
	public synchronized String[] getBreakpointListeners() throws CoreException {
		// use the cache in case the underlying marker has been deleted
		if (fBreakpointListenerIds == null) {
			return new String[0];
		}
		return (String[]) fBreakpointListenerIds.toArray(new String[fBreakpointListenerIds.size()]);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.debug.core.IJavaBreakpoint#getHitCount()
	 */
	public int getHitCount() throws CoreException {
		return ensureMarker().getAttribute(HIT_COUNT, -1);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.debug.core.IJavaBreakpoint#getSuspendPolicy()
	 */
	public int getSuspendPolicy() throws CoreException {
		return ensureMarker().getAttribute(SUSPEND_POLICY, IVdmBreakpoint.SUSPEND_THREAD);
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.debug.core.IJavaBreakpoint#getTypeName()
	 */
	public String getTypeName() throws CoreException {
		if (fInstalledTypeName == null) {
			return ensureMarker().getAttribute(TYPE_NAME, null);
		}
		return fInstalledTypeName;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.debug.core.IJavaBreakpoint#isInstalled()
	 */
	public boolean isInstalled() throws CoreException {
		return ensureMarker().getAttribute(INSTALL_COUNT, 0) > 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.debug.core.IJavaBreakpoint#removeBreakpointListener(java.lang.String)
	 */
	public synchronized boolean removeBreakpointListener(String identifier) throws CoreException {
		if (fBreakpointListenerIds != null) {
			if (fBreakpointListenerIds.remove(identifier)) {
				writeBreakpointListeners();
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.debug.core.IJavaBreakpoint#setHitCount(int)
	 */
	public void setHitCount(int count) throws CoreException {	
		if (getHitCount() != count) {
			if (!isEnabled() && count > -1) {
				setAttributes(new String []{ENABLED, HIT_COUNT, EXPIRED},
					new Object[]{Boolean.TRUE, new Integer(count), Boolean.FALSE});
			} else {
				setAttributes(new String[]{HIT_COUNT, EXPIRED},
					new Object[]{new Integer(count), Boolean.FALSE});
			}
			recreate();
		}
	}

	/**
	 * An attribute of this breakpoint has changed - recreate event requests in
	 * all targets.
	 */
	protected void recreate() throws CoreException {
		DebugPlugin plugin = DebugPlugin.getDefault();
        if (plugin != null) {
            IDebugTarget[] targets = plugin.getLaunchManager().getDebugTargets();
            for (int i = 0; i < targets.length; i++) {
                IDebugTarget target = targets[i];
                MultiStatus multiStatus = new MultiStatus(IDebugConstants.PLUGIN_ID, IDebugConstants.ERROR, "Breakpoint exception", null);
                IDebugTarget vdmTarget = (IDebugTarget) target.getAdapter(IDebugTarget.class);
                if (vdmTarget instanceof VdmDebugTarget) {
                    try {
                        recreate((VdmDebugTarget) vdmTarget);
                    } catch (CoreException e) {
                        multiStatus.add(e.getStatus());
                    }
                }
                if (!multiStatus.isOK()) {
                    throw new CoreException(multiStatus);
                }
            }
        }
    }
	
	/**
	 * Recreate this breakpoint in the given target, as long as the
	 * target already contains this breakpoint.
	 * 
	 * @param target the target in which to re-create the breakpoint 
	 */
	protected void recreate(VdmDebugTarget target) throws CoreException {
		if (!target.isTerminated() && target.getBreakpoints().contains(this)) {
//			removeRequests(target);
//			createRequests(target);
			System.out.println("TODO: breakpoint changed!! (VdmBreakpoint)");
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.debug.core.IJavaBreakpoint#setSuspendPolicy(int)
	 */
	public void setSuspendPolicy(int suspendPolicy) throws CoreException {
		if(getSuspendPolicy() != suspendPolicy) {
			setAttribute(SUSPEND_POLICY, suspendPolicy);
			recreate();
		}
	}

	public String getModelIdentifier() {
		return IDebugConstants.ID_VDM_DEBUG_MODEL;
	}

	/**
	 * Returns whether this breakpoint should be "skipped". Breakpoints
	 * are skipped if the breakpoint manager is disabled and the breakpoint
	 * is registered with the manager
	 * 
	 * @return whether this breakpoint should be skipped
	 */
	public boolean shouldSkipBreakpoint() throws CoreException {
		DebugPlugin plugin = DebugPlugin.getDefault();
        return plugin != null && isRegistered() && !plugin.getBreakpointManager().isEnabled();
	}
	
}
