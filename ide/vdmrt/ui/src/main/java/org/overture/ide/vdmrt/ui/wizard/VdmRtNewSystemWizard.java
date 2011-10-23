/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
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
		"Speed is giving in MIPS - Millions of instructions per second\n*/\n\n"+
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
