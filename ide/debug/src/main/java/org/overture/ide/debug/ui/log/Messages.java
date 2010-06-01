package org.overture.ide.debug.ui.log;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.overture.ide.debug.ui.log.messages"; //$NON-NLS-1$
	public static String VdmDebugLogView_clear;
	public static String VdmDebugLogView_copy;
	public static String EventKind_Change;
	public static String EventKind_Create;
	public static String EventKind_ModelSpecific;
	public static String EventKind_Resume;
	public static String EventKind_Suspend;
	public static String EventKind_Terminate;
	public static String EventKind_Unknown;
	public static String ItemType_Input;
	public static String ItemType_Output;
	public static String ItemType_Event;
	public static String Column_Date;
	public static String Column_Time;
	public static String Column_Type;
	public static String Column_Session;
	public static String Column_Message;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
