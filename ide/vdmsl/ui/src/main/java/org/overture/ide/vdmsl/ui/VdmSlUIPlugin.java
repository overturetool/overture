package org.overture.ide.vdmsl.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;

public class VdmSlUIPlugin extends AbstractUIPlugin {

	private static final boolean DEBUG = true;
	
	public VdmSlUIPlugin() {
		println("VdmppUIPlugin started");
	}
	
	
	public static void println(String s)
	{
		if(DEBUG)
			System.out.println(s);
	}
	public static void printe(Exception e) {
		println(e.getStackTrace().toString());
		println(e.getMessage());
		
	}

}
