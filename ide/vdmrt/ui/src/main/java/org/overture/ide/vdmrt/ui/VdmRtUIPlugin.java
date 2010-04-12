package org.overture.ide.vdmrt.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;

public class VdmRtUIPlugin extends AbstractUIPlugin {

	private static final boolean DEBUG = true;
	
	
	
	
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
