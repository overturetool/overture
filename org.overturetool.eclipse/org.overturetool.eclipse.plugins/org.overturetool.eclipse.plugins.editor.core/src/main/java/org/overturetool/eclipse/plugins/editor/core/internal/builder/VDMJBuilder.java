package org.overturetool.eclipse.plugins.editor.core.internal.builder;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.IScriptProject;
import org.overturetool.eclipse.plugins.editor.core.OverturePlugin;
import org.overturetool.vdmj.ExitStatus;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;

public class VDMJBuilder extends Builder {
	private final int adjustPosition = 1;
	
	public VDMJBuilder(IScriptProject project) {
		super(project);
	}

	public IStatus typeCheck() {
		
		//TODO check projectProperties if it is a VDM-SL and VDM++
		EclipseVDMJPP vdmpp = new EclipseVDMJPP();
		ArrayList<File> fileList = new ArrayList<File>(); 
		for (String filename : getSoruceFiles()) {
			fileList.add(new File(filename));
		}
		ExitStatus parseStatus = vdmpp.parse(fileList);
		clearMarkers();
		if (parseStatus == ExitStatus.EXIT_ERRORS){
			for (VDMError error : vdmpp.getParseErrors()) {
				this.addMarker(
						error.location.file.getAbsolutePath(),
						error.message,
						error.location.startLine,
						IMarker.SEVERITY_ERROR,
						error.location.startPos - adjustPosition,
						error.location.endPos - adjustPosition);
			}
		}
		ExitStatus typeCheckStatus = null;
		if (parseStatus == ExitStatus.EXIT_OK)
		{
			typeCheckStatus = vdmpp.typeCheck();
			if (typeCheckStatus == ExitStatus.EXIT_ERRORS)
			{
				for (VDMError error : vdmpp.getTypeErrors()) { 
					this.addMarker(
							error.location.file.getAbsolutePath(),
							error.message,
							error.location.startLine,
							IMarker.SEVERITY_ERROR,
							error.location.startPos - adjustPosition,
							error.location.endPos - adjustPosition);
				}
			}
			for (VDMWarning warning : vdmpp.getTypeWarnings()) {
				// TODO 
				this.addMarker(
						warning.location.file.getAbsolutePath(),
						warning.message,
						warning.location.startLine,
						IMarker.SEVERITY_WARNING,
						warning.location.startPos - adjustPosition,
						warning.location.endPos - adjustPosition);
			}
		}
		else
		{
			System.out.println("numbers of errors: " + vdmpp.getParseErrors().size() );
			for (VDMError error : vdmpp.getParseErrors()) {
				// TODO 
				this.addMarker(
						error.location.file.getAbsolutePath(),
						error.message,
						error.location.startLine,
						IMarker.SEVERITY_ERROR,
						error.location.startPos - adjustPosition,
						error.location.endPos - adjustPosition);
			}
			
		}
		
		
		
		if (typeCheckStatus == ExitStatus.EXIT_ERRORS || parseStatus.EXIT_ERRORS == ExitStatus.EXIT_ERRORS ){
			IStatus typeChecked = new Status(
					IStatus.ERROR,
					OverturePlugin.PLUGIN_ID,
					0,
					"not typechecked",
					null);
			return typeChecked;			
		}
		else
		{
			
			IStatus typeChecked = new Status(
					IStatus.OK,
					OverturePlugin.PLUGIN_ID,
					0,
					"Type Checked",
					null);
			return typeChecked;	
		}
		
		
	}


}
