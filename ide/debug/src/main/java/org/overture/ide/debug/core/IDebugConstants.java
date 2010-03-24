package org.overture.ide.debug.core;

public interface IDebugConstants
{

	final String VDMPP_DEBUGGING_ENGINE_ID_KEY = "vdmpp_debugging_engine_id";
	String PLUGIN_ID = "org.overture.ide.debug";
	

	// used in the launch configuration
	final static String VDM_LAUNCH_CONFIG_PROJECT = "vdm_launch_config_project";
	final static String VDM_LAUNCH_CONFIG_MODULE = "vdm_launch_config_module";
	final static String VDM_LAUNCH_CONFIG_OPERATION = "vdm_launch_config_method";
	final static String VDM_LAUNCH_CONFIG_EXPRESSION_SEPERATOR = "vdm_launch_config_expression_seperator";
	final static String VDM_LAUNCH_CONFIG_EXPRESSION= "vdm_launch_config_expression";
	
	final static String VDM_LAUNCH_CONFIG_DEFAULT = "vdm_launch_config_default";
	
	final static String VDM_LAUNCH_CONFIG_REMOTE_CONTROL = "vdm_launch_config_remote_control_class";
	final static String VDM_LAUNCH_CONFIG_CREATE_COVERAGE = "vdm_launch_config_create_coverage";
	final static String VDM_LAUNCH_CONFIG_REMOTE_DEBUG = "vdm_launch_config_remote_debug";
	final static String VDM_LAUNCH_CONFIG_VM_MEMORY_OPTION = "vdm_launch_config_memory_option";
	final static String VDM_LAUNCH_CONFIG_ENABLE_LOGGING = "vdm_launch_config_enable_logging";
	
	final static String VDM_DEBUG_SESSION_ID = "vdm_debug_session_id";

	public static final String ID_VDM_DEBUG_MODEL = "org.overture.ide.debug.vdm";
	public static final String ATTR_VDM_PROGRAM = ID_VDM_DEBUG_MODEL
			+ ".ATTR_VDM_PROGRAM";
	public static final String ID_VDM_EXECUTABLE = "java";
	
	
	//debugger
	public static final String DEBUG_ENGINE_BUNDLE_ID = "org.overture.ide.generated.vdmj";
	public static final String DEBUG_ENGINE_CLASS = "org.overturetool.vdmj.debug.DBGPReader";
}
