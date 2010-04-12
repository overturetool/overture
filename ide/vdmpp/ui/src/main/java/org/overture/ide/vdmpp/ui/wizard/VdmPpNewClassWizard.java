package org.overture.ide.vdmpp.ui.wizard;

import org.overture.ide.ui.wizard.VdmNewFileWizard;

public class VdmPpNewClassWizard extends VdmNewFileWizard {

	@Override
	protected String getPageDescription() {		
		return "Chose new VDM++ file name and location";
	}

	@Override
	protected String getPageName() {		
		return "VDM++ New Class Wizard";
	}

	@Override
	protected String getPageTitle() {		
		return "VDM++ New Class Wizard";
	}

	@Override
	protected String getFileExtension()
	{
		return "vdmpp";
	}

	@Override
	protected String getFileTemplate(String fileName)
	{
		String className = fileName;
		return "class " + className + "\n" + "\ttypes\n-- TODO Define types here\n"
				+ "\tvalues\n-- TODO Define values here\n" + "\tinstance variables\n-- TODO Define instance variables here\n"
				+ "\toperations\n-- TODO Define operations here\n" + "\tfunctions\n-- TODO Define functiones here\n" 
				+ "\ttraces\n-- TODO Define Combinatorial Test Traces here\n" + "end "
				+ className;
	}
}
