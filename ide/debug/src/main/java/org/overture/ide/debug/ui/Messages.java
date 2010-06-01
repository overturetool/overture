package org.overture.ide.debug.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.overture.ide.debug.ui.messages"; //$NON-NLS-1$
	public static String DebugConsoleManager_debugConsole;
	/**
	 * @since 2.0
	 */
	public static String DebugConsoleManager_terminated;
	public static String DLTKDebugUIPlugin_internalError;
	public static String VdmDebuggerConsoleToFileHyperlink_error;
	public static String VdmDebugModelPresentation_breakpointText;
	public static String VdmDebugModelPresentation_breakpointNoResourceText;
	public static String VdmDebugModelPresentation_breakpointText2;
	public static String VdmDebugModelPresentation_breakpointText3;
	public static String VdmDebugModelPresentation_breakpointText4;
	public static String VdmDebugModelPresentation_breakpointText5;
	public static String VdmDebugModelPresentation_debugTargetText;
	public static String VdmDebugModelPresentation_expressionText;
	public static String VdmDebugModelPresentation_stackFrameText;
	public static String VdmDebugModelPresentation_stackFrameText2;
	public static String VdmDebugModelPresentation_stackFrameText3;
	public static String VdmDebugModelPresentation_threadText;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
