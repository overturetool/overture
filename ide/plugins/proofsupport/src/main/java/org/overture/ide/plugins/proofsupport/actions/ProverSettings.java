package org.overture.ide.plugins.proofsupport.actions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.eclipse.core.resources.IProject;

public class ProverSettings {

	public static final String FileName="proverSettings.txt";
	
	public static void createDefaultFile(IProject selectedProject) {
		try {
			
			File projectFile = selectedProject.getLocation().toFile();
			
			File settings = new File(projectFile,FileName);
			
			//settings.createNewFile();
			
			BufferedWriter outputStream = new BufferedWriter(new FileWriter(settings));
			
			
			

			outputStream.append("/Users/UserName/root/opt/vdmpp/bin/vppde");
			outputStream.append("\n" +"/Users/UserName/root/opt/mosml");
			outputStream.append("\n" +"/Users/UserName/root/opt/hol");


			outputStream.close();
			selectedProject.refreshLocal(IProject.DEPTH_ONE, null);
			
		} catch (Exception e) {
			// We don't care
		}
		
	}

}
