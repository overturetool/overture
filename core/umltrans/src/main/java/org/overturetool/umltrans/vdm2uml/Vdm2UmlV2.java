package org.overturetool.umltrans.vdm2uml;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.overturetool.umltrans.StatusLog;
import org.overturetool.umltrans.uml.IUmlModel;

public class Vdm2UmlV2 {


	private StatusLog log = null;
	
	
	public Vdm2UmlV2() {
		
		try {
			log = new StatusLog();
		} catch (CGException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public IUmlModel init()
	{
		IUmlModel model = null;
		
		
		return model;
	}
	
	
}
