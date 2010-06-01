//package org.overture.ide.debug.core.model.internal;
//
//import java.util.Map;
//
//import org.eclipse.core.resources.IResource;
//import org.eclipse.core.resources.IWorkspaceRunnable;
//import org.eclipse.core.runtime.CoreException;
//import org.eclipse.core.runtime.IProgressMonitor;
//import org.eclipse.debug.core.DebugException;
//import org.eclipse.debug.core.model.IBreakpoint;
//import org.overture.ide.debug.core.VdmDebugPlugin;
//
//public class VdmExceptionBreakpoint extends AbstractVdmBreakpoint
//		implements IVdmExceptionBreakpoint {
//
//	private static final String Vdm_EXCEPTION_BREAKPOINT = "org.eclipse.dltk.debug.VdmExceptionBreakpointMarker"; //$NON-NLS-1$
//
//	/**
//	 * Breakpoint attribute storing the fully qualified name of the type
//	 * this breakpoint is located in.
//	 * (value <code>"org.eclipse.jdt.debug.core.typeName"</code>). This attribute is a <code>String</code>.
//	 */
//	protected static final String TYPE_NAME = VdmDebugPlugin.PLUGIN_ID + ".typeName"; //$NON-NLS-1$	
//	/**
//	 * Exception breakpoint attribute storing the suspend on caught value
//	 * (value <code>"org.eclipse.dltk.debug.core.caught"</code>). This attribute is stored as a <code>boolean</code>.
//	 * When this attribute is <code>true</code>, a caught exception of the associated
//	 * type will cause excecution to suspend .
//	 */
//	protected static final String CAUGHT = DLTKDebugPlugin.PLUGIN_ID + ".caught"; //$NON-NLS-1$
//	/**
//	 * Exception breakpoint attribute storing the suspend on uncaught value
//	 * (value <code>"org.eclipse.dltk.debug.core.uncaught"</code>). This attribute is stored as a
//	 * <code>boolean</code>. When this attribute is <code>true</code>, an uncaught
//	 * exception of the associated type will cause excecution to suspend.
//	 */
//	protected static final String UNCAUGHT = DLTKDebugPlugin.PLUGIN_ID + ".uncaught"; //$NON-NLS-1$	
//	/**
//	 * Allows the user to specify whether we should suspend if subclasses of the specified exception are thrown/caught
//	 */
//	protected static final String SUSPEND_ON_SUBCLASSES = DLTKDebugPlugin.PLUGIN_ID + ".suspend_on_subclasses"; //$NON-NLS-1$
//
//	/**
//	 * Name of the exception that was actually hit (could be a
//	 * subtype of the type that is being caught).
//	 */
//	protected String fExceptionName = null;
//
//	
//	
//	public VdmExceptionBreakpoint() {
//	}
//	
//	/**
//	 * Creates and returns an exception breakpoint for the
//	 * given type. Caught and uncaught specify where the exception
//	 * should cause thread suspensions - that is, in caught and/or uncaught locations.
//	 * Checked indicates if the given exception is a checked exception.
//	 * @param resource the resource on which to create the associated
//	 *  breakpoint marker 
//	 * @param exceptionName the fully qualified name of the exception for
//	 *  which to create the breakpoint
//	 * @param caught whether to suspend in caught locations
//	 * @param uncaught whether to suspend in uncaught locations
//	 * @param checked whether the exception is a checked exception
//	 * @param add whether to add this breakpoint to the breakpoint manager
//	 * @return a Java exception breakpoint
//	 * @exception DebugException if unable to create the associated marker due
//	 *  to a lower level exception.
//	 */
//	public VdmExceptionBreakpoint(final String debugModelId,
//			final IResource resource, final String exceptionName,
//			final boolean caught, final boolean uncaught, final boolean add,
//			final Map attributes) throws DebugException {
//		IWorkspaceRunnable wr = new IWorkspaceRunnable() {
//			public void run(IProgressMonitor monitor) throws CoreException {
//				// create the marker
//				setMarker(resource.createMarker(Vdm_EXCEPTION_BREAKPOINT));
//
//				// add general breakpoint attributes
//				addVdmBreakpointAttributes(attributes, debugModelId, true);
//
//				// add exception breakpoint attributes
//				attributes.put(TYPE_NAME, exceptionName);
//				attributes.put(CAUGHT, Boolean.valueOf(caught));
//				attributes.put(UNCAUGHT, Boolean.valueOf(uncaught));
//
//				ensureMarker().setAttributes(attributes);
//
//				register(add);
//			}
//
//		};
//		run(getMarkerRule(resource), wr);
//	}
//
//	/**
//	 * Enable this exception breakpoint.
//	 * 
//	 * If the exception breakpoint is not catching caught or uncaught,
//	 * turn both modes on. If this isn't done, the resulting
//	 * state (enabled with caught and uncaught both disabled)
//	 * is ambiguous.
//	 */
//	public void setEnabled(boolean enabled) throws CoreException {
//		if (enabled) {
//			if (!(isCaught() || isUncaught())) {
//				setAttributes(new String[] { CAUGHT, UNCAUGHT }, new Object[] {
//						Boolean.TRUE, Boolean.TRUE });
//			}
//		}
//		super.setEnabled(enabled);
//	}
//
//	/**
//	 * @see IVdmExceptionBreakpoint#isCaught()
//	 */
//	public boolean isCaught() throws CoreException {
//		return ensureMarker().getAttribute(CAUGHT, false);
//	}
//
//	/**
//	 * @see IVdmExceptionBreakpoint#setCaught(boolean)
//	 */
//	public void setCaught(boolean caught) throws CoreException {
//		if (caught == isCaught()) {
//			return;
//		}
//		setAttribute(CAUGHT, caught);
//		if (caught && !isEnabled()) {
//			setEnabled(true);
//		} else if (!(caught || isUncaught())) {
//			setEnabled(false);
//		}
//	}
//
//	/* (non-Javadoc)
//	 * @see org.eclipse.dltk.debug.core.IJavaExceptionBreakpoint#setSuspendOnSubclasses(boolean)
//	 */
//	public void setSuspendOnSubclasses(boolean suspend) throws CoreException {
//		if (suspend != isSuspendOnSubclasses()) {
//			setAttribute(SUSPEND_ON_SUBCLASSES, suspend);
//		}
//	}
//
//	/* (non-Javadoc)
//	 * @see org.eclipse.dltk.debug.core.IJavaExceptionBreakpoint#isSuspendOnSubclasses()
//	 */
//	public boolean isSuspendOnSubclasses() throws CoreException {
//		return ensureMarker().getAttribute(SUSPEND_ON_SUBCLASSES, false);
//	}
//
//	/**
//	 * @see IJavaExceptionBreakpoint#isUncaught()
//	 */
//	public boolean isUncaught() throws CoreException {
//		return ensureMarker().getAttribute(UNCAUGHT, false);
//	}
//
//	/**
//	 * @see IJavaExceptionBreakpoint#setUncaught(boolean)
//	 */
//	public void setUncaught(boolean uncaught) throws CoreException {
//		if (uncaught == isUncaught()) {
//			return;
//		}
//		setAttribute(UNCAUGHT, uncaught);
//		if (uncaught && !isEnabled()) {
//			setEnabled(true);
//		} else if (!(uncaught || isCaught())) {
//			setEnabled(false);
//		}
//	}
//
//	/**
//	 * Sets the name of the exception that was last hit
//	 * 
//	 * @param name fully qualified exception name
//	 */
//	protected void setExceptionTypeName(String name) {
//		fExceptionName = name;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.eclipse.dltk.debug.core.IJavaExceptionBreakpoint#getExceptionTypeName()
//	 */
//	public String getExceptionTypeName() {
//		return fExceptionName;
//	}
//
//	private static final String[] UPDATABLE_ATTRS = new String[] {
//			IBreakpoint.ENABLED, AbstractVdmBreakpoint.HIT_CONDITION,
//			AbstractVdmBreakpoint.HIT_VALUE, CAUGHT, UNCAUGHT,
//			SUSPEND_ON_SUBCLASSES };
//
//	public String[] getUpdatableAttributes() {
//		return UPDATABLE_ATTRS;
//	}
//
//	public String getTypeName() throws CoreException {
//		return (String) ensureMarker().getAttribute(TYPE_NAME);
//	}
//}
