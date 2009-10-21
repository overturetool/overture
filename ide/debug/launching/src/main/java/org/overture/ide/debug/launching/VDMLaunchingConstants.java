package org.overture.ide.debug.launching;

public interface VDMLaunchingConstants {
	
	/*******************
	 **  VDM++
	 *******************/
	
	final String VDMPP_DEBUGGING_ENGINE_ID_KEY = "vdmpp_debugging_engine_id";
	final String VDMPP_DEBUG_PLUGIN_ID = "org.overture.ide.debug.vdmpp";
	// TODO move VDMPPInterpreterPreferencePage to ui ???
	public final String VDMPP_DEBUG_INTERPRETER_TAB = "org.overture.ide.vdmpp.debug.ui.preferences.VDMPPInterpreterPreferencePage";
	final String VDMPP_LaunchPluginID = "org.overture.ide.debug.launching";
	
	/*******************
	 **  VDM-SL
	 *******************/
	public final String VDMSL_DEBUG_PLUGIN_ID = "org.overture.ide.debug.vdmsl";
	public final String VDMSL_DEBUGGING_ENGINE_ID_KEY = "vdmsl_debugging_engine_id";
	public final String VDMSL_DEBUG_INTERPRETER_TAB = "org.overture.ide.vdmsl.debug.ui.preferences.VdmSlInterpreterPreferencePage";

	/***************************
	 **  VDM-RT (concurrency)
	 ***************************/
	public final String VDMRT_DEBUG_INTERPRETER_TAB = "org.overture.ide.vdmrt.debug.ui.preferences.VdmRtInterpreterPreferencePage";
}
