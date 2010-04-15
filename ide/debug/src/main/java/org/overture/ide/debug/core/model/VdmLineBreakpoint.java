package org.overture.ide.debug.core.model;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.overture.ide.debug.core.IDebugConstants;

public class VdmLineBreakpoint extends VdmBreakpoint implements IVdmLineBreakpoint {

	private File file;
	private int id;
	/**
	 * The map of cached compiled expressions (ICompiledExpression) for this breakpoint, keyed by thread.
	 * This value must be cleared every time the breakpoint is added to a target.
	 */
	private Map<Object, Object> fCompiledExpressions= new HashMap<Object, Object>();
	/**
	 * Breakpoint attribute storing a breakpoint's conditional expression
	 * (value <code>"org.eclipse.jdt.debug.core.condition"</code>). This attribute is stored as a
	 * <code>String</code>.
	 */
	protected static final String CONDITION= "org.overture.ide.debug.core.condition";
	/**
	 * Breakpoint attribute storing a breakpoint's condition enabled state
	 * (value <code>"org.eclipse.jdt.debug.core.conditionEnabled"</code>). This attribute is stored as an
	 * <code>boolean</code>.
	 */
	protected static final String CONDITION_ENABLED= "org.overture.ide.debug.core.conditionEnabled"; 
	/**
	 * Breakpoint attribute storing a breakpoint's condition suspend policy
	 * (value <code>" org.eclipse.jdt.debug.core.conditionSuspendOnTrue"
	 * </code>). This attribute is stored as an <code>boolean</code>.
	 */
	protected static final String CONDITION_SUSPEND_ON_TRUE= "org.overture.ide.debug.core.conditionSuspendOnTrue"; //$NON-NLS-1$
	/**
	 * The map of the result value of the condition (IValue) for this
	 * breakpoint, keyed by debug target.
	 */
	private Map<Object, Object> fConditionValues= new HashMap<Object, Object>();
	/**
	 * Maps suspended threads to the suspend event that suspended them
	 */
	private Map<Object, Object> fSuspendEvents= new HashMap<Object, Object>();
	
	public VdmLineBreakpoint() {
		super();
	}

	
	public VdmLineBreakpoint(final IResource resource, final int lineNumber) throws CoreException {
		
		initPath(resource);
		
		
		
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				IMarker marker = resource
						.createMarker("vdm.lineBreakpoint.marker");
				setMarker(marker);
				marker.setAttribute(IBreakpoint.ENABLED, Boolean.TRUE);
				marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
				marker.setAttribute(IBreakpoint.ID, getModelIdentifier());
				marker.setAttribute(IMarker.MESSAGE, "Line Breakpoint: "
						+ resource.getName() + " [line: " + lineNumber + "]");
				marker.setAttribute(IBreakpoint.PERSISTED, Boolean.TRUE);
			}
		};
		run(getMarkerRule(resource), runnable);
	     
	  }
	
	private void initPath(IResource resource){
		IPath path = resource.getRawLocation();
		path = path.makeAbsolute();
		
		setFile(path.toFile());
	}
	
	public String getModelIdentifier() {
		return IDebugConstants.ID_VDM_DEBUG_MODEL;
	}

	private void setFile(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	
	@Override
	public void setMarker(IMarker marker) throws CoreException {
		super.setMarker(marker);
		initPath(marker.getResource());
	}


	public String getCondition() throws CoreException {
		return ensureMarker().getAttribute(CONDITION, null);
	}


	public boolean isConditionEnabled() throws CoreException {
		return ensureMarker().getAttribute(CONDITION_ENABLED, false);
	}

	public boolean isConditionSuspendOnTrue() throws DebugException {
		return ensureMarker().getAttribute(CONDITION_SUSPEND_ON_TRUE, true);
	}


	public void setCondition(String condition) throws CoreException {
		// Clear the cached compiled expressions
		fCompiledExpressions.clear();
		fConditionValues.clear();
		fSuspendEvents.clear();
		if (condition != null && condition.trim().length() == 0) {
			condition = null;
		}
		setAttributes(new String []{CONDITION}, new Object[]{condition});
		recreate();
	}


	public void setConditionEnabled(boolean conditionEnabled) throws CoreException {	
		setAttributes(new String[]{CONDITION_ENABLED}, new Object[]{Boolean.valueOf(conditionEnabled)});
		recreate();
	}


	public void setConditionSuspendOnTrue(boolean suspendOnTrue) throws CoreException {
		if (isConditionSuspendOnTrue() != suspendOnTrue) {
			setAttributes(new String[]{CONDITION_SUSPEND_ON_TRUE}, new Object[]{Boolean.valueOf(suspendOnTrue)});
			fConditionValues.clear();
			recreate();
		}
	}


	public boolean supportsCondition() {		
		return true;
	}

	public int getCharStart() throws CoreException {
		return ensureMarker().getAttribute(IMarker.CHAR_START, -1);
	}
	public int getCharEnd() throws CoreException {
		return ensureMarker().getAttribute(IMarker.CHAR_END, -1);
	}	


	public int getLineNumber() throws CoreException {
		return ensureMarker().getAttribute(IMarker.LINE_NUMBER, -1);
	}
}
