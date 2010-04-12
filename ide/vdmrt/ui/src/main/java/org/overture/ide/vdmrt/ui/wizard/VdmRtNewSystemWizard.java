package org.overture.ide.vdmrt.ui.wizard;

import org.overture.ide.ui.wizard.VdmNewFileWizard;

public class VdmRtNewSystemWizard extends VdmNewFileWizard {

	@Override
	protected String getPageDescription() {		
		return "Chose new file name and location";
	}

	@Override
	protected String getPageName() {		
		return "VDM-RT New File Wizard";
	}

	@Override
	protected String getPageTitle() {		
		return "New System Wizard";
	}

	@Override
	protected String getFileExtension()
	{
		return "vdmrt";
	}
	
	@Override
	protected String getFileTemplate(String fileName)
	{
		String className = fileName;
		return "system " + className + "\n" + "\tinstance variables\n\n"+
		"-- Architecture definition\n\n"+
		"/*\nComputing units:\n\tPriority: <FP> - Fixed priority\n\t          <PP> - Priority?\n\n"+
		"Speed is giving in MIPS - Millioner Instruktioner Per Sekund\n*/\n\n"+
		"  cpu1 : CPU := new CPU(<FP>, 22E6);\n"+
		"  cpu2 : CPU := new CPU(<FP>, 22E6);\n\n"+
		"/*\nCommunication bus:\n \tModes: <CSMACD> - ?\n*/\n"+
		"  bus : BUS := new BUS(<CSMACD>, 72E3,{ /* Deployable objects */});\n\n\n"+
		"-- TODO Define deployable objects as static instance variables\n\n"+
		"\toperations\n\n"+
		"public "+className+" : () ==> "+className+"\n"+
		className+" () == \n("+
		"-- TODO Deploy deployable object to cpu's\n\n/*\n"+
		"cpu1.deploy(deployableObject1,\"Object 1\");\n"+
		"cpu1.setPriority( TODO: Static operation , 100);\n\n"+
		"cpu2.deploy(deployableObject2,\"Object 2\");\n"+
		"cpu2.setPriority( TODO: Static operation , 100);\n\n*/\n"+
		"-- CPU's are started implicit\n);\n\n"+
		"end "+ className;
	}

}
