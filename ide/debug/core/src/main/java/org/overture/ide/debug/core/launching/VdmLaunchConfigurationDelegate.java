package org.overture.ide.debug.core.launching;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.overture.ide.debug.core.model.VdmDebugTarget;

public class VdmLaunchConfigurationDelegate implements
		ILaunchConfigurationDelegate {

	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		
		
		List commandList = new ArrayList();
		
		
		ILaunchConfigurationWorkingCopy workConfiguration = configuration.getWorkingCopy();		
		workConfiguration.setAttribute(IVdmConstants.ATTR_VDM_PROGRAM, "file:/C:/OvertureDevelopment/runtime-EclipseApplication1/alarm/");
		configuration = workConfiguration.doSave();
	
//		
//		String path = "C:\\Program Files\\Java\\jdk1.6.0_18\\bin\\";		
//		commandList.add("\"C:\\Program Files\\Java\\jdk1.6.0_18\\bin\\java.exe\" -cp C:\\vdmj-2.0.0.jar org.overturetool.vdmj.debug.DBGPReader");
//		commandList.add("-h 172.20.185.243 -p 10000 -k dbgp_1265361483486 -w -q -vdmpp -r classic -c Cp1252" );
//		commandList.add("-e64 bmV3IFRlc3QxKCkuUnVuKCk= -default64");
//		commandList.add("VGVzdDE= file:/C:/Documents%20and%20Settings/ari/Desktop/OvertureIde/workspace/alarm/alarm.vdmpp");
//		commandList.add("file:/C:/Documents%20and%20Settings/ari/Desktop/OvertureIde/workspace/alarm/expert.vdmpp"); 
//		commandList.add("file:/C:/Documents%20and%20Settings/ari/Desktop/OvertureIde/workspace/alarm/plant.vdmpp"); 
//		commandList.add("file:/C:/Documents%20and%20Settings/ari/Desktop/OvertureIde/workspace/alarm/test1.vdmpp");
//	
//		String[] commandLine = (String[]) commandList.toArray(new String[commandList.size()]);
		
		
//		ConsoleWriter cw = new ConsoleWriter("Launch Console");
//		
//		VdmDebugTarget target = null;
//		if (mode.equals(ILaunchManager.DEBUG_MODE)) {
//			
//			SocketAcceptor socketAcceptor = new SocketAcceptor();
//			Thread acceptorThread = new Thread(socketAcceptor);
//			acceptorThread.setName("Socket Acceptor");
//			acceptorThread.start();
//			
//			try {
//				Thread.sleep(200);
//			} catch (InterruptedException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//						
//			Process process = null;
//			System.out.println("java.exe -cp C:\\vdmj-2.0.0.jar org.overturetool.vdmj.debug.DBGPReader " + 
//					"-h 172.20.185.243 -p 10000 -k dbgp_1265361483486 -w -q -vdmpp -r classic -c Cp1252 " + 
//					"-e64 bmV3IFRlc3QxKCkuUnVuKCk= -default64 " + 
//					"VGVzdDE= file:/C:/OvertureDevelopment/runtime-EclipseApplication1/alarm/alarm.vdmpp " + 
//					"file:/C:/OvertureDevelopment/runtime-EclipseApplication1/alarm/expert.vdmpp " + 
//					"file:/C:/OvertureDevelopment/runtime-EclipseApplication1/alarm/plant.vdmpp " +  
////			"file:/C:/OvertureDevelopment/runtime-EclipseApplication1/alarm/test1.vdmpp ");
//			try {
//				process = Runtime.getRuntime().exec("java.exe -cp C:\\vdmj-2.0.0.jar org.overturetool.vdmj.debug.DBGPReader " + 
//						"-h 172.20.185.243 -p 10000 -k dbgp_1265361483486 -w -q -vdmpp -r classic -c Cp1252 " + 
//						"-e64 bmV3IFRlc3QxKCkuUnVuKCk= -default64 " + 
//						"VGVzdDE= file:/C:/OvertureDevelopment/runtime-EclipseApplication1/alarm/alarm.vdmpp " + 
//						"file:/C:/OvertureDevelopment/runtime-EclipseApplication1/alarm/expert.vdmpp " + 
//						"file:/C:/OvertureDevelopment/runtime-EclipseApplication1/alarm/plant.vdmpp " +
//				"file:/C:/OvertureDevelopment/runtime-EclipseApplication1/alarm/test1.vdmpp ");
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//			new ProcessConsolePrinter("interpreter" ,cw,process.getInputStream()).start();
//			new ProcessConsolePrinter("interpreter error" ,cw,process.getErrorStream()).start();
//
//			
//
//			Socket s = socketAcceptor.getSocket();
//			
//			if(s == null)
//				abort("Failed to connect to debugger",null);
//			
//			
//			
//			IProcess p = DebugPlugin.newProcess(launch, process, path);
//			target = new VdmDebugTarget(launch,p,s);
//			launch.addDebugTarget(target);

		//Process process = DebugPlugin.exec(commandLine, null); 
//		}
//	}


	}

}
