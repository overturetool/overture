package org.overture.ide.debug.core;

public interface IDebugConstants {

	final String VDMPP_DEBUGGING_ENGINE_ID_KEY = "vdmpp_debugging_engine_id";
	String PLUGIN_ID = "org.overture.ide.debug";
	
	
	
	// used in the launch configuration
	final String DEBUGGING_MODULE = "vdmDebuggingModule";
	final String DEBUGGING_OPERATION = "vdmDebuggingMethod";
	final String DEBUGGING_REMOTE_CONTROL = "vdmDebuggingRemoteControlClass";
	final String DEBUGGING_CREATE_COVERAGE = "create_coverage";
	final String DEBUGGING_REMOTE_DEBUG = "remote_debug";
	final String DEBUGGING_VM_MEMORY_OPTION = "vdmDebuggingMemoryOption";
	
	
	public static final String ID_VDM_DEBUG_MODEL = "org.overture.ide.debug.vdm";
	public static final String ATTR_VDM_PROGRAM = ID_VDM_DEBUG_MODEL + ".ATTR_VDM_PROGRAM";
	public static final String ID_VDM_EXECUTABLE = "java";
}
