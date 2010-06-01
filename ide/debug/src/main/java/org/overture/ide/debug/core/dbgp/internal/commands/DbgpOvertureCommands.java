package org.overture.ide.debug.core.dbgp.internal.commands;

import java.io.File;

import org.overture.ide.debug.core.dbgp.DbgpBaseCommands;
import org.overture.ide.debug.core.dbgp.DbgpRequest;
import org.overture.ide.debug.core.dbgp.IDbgpCommunicator;
import org.overture.ide.debug.core.dbgp.commands.IDbgpCommands;
import org.overture.ide.debug.core.dbgp.commands.IDbgpCoreCommands;
import org.overture.ide.debug.core.dbgp.commands.IDbgpExtendedCommands;
import org.overture.ide.debug.core.dbgp.commands.IDbgpOvertureCommands;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;
import org.overture.ide.debug.utils.communication.DBGPProxyException;
import org.overturetool.vdmj.util.Base64;

public class DbgpOvertureCommands extends DbgpBaseCommands implements
		IDbgpOvertureCommands {

	private final static String OVERTURE_COMMAND = "xcmd_overture_cmd";
	
	public DbgpOvertureCommands(IDbgpCommunicator communicator) {
		super(communicator);

	}
	

	public void getCoverage(File file) throws DbgpException {
		
		DbgpRequest request = createRequest(OVERTURE_COMMAND);
		request.addOption("-c", "coverage"); //$NON-NLS-1$
		request.setData(file.toURI().toString());
		
		send(request);

	}
	
	public void writeCoverage(File file) throws DbgpException {
		DbgpRequest request = createRequest(OVERTURE_COMMAND);
		request.addOption("-c", "writecoverage"); //$NON-NLS-1$
		request.setData(file.toURI().toString());
		
		send(request);
	}

	public void writeLog(String file) throws DbgpException {
		DbgpRequest request = createRequest(OVERTURE_COMMAND);
		request.addOption("-c", "log"); //$NON-NLS-1$
		request.addOption("--", Base64.encode(file.getBytes()));
		
		send(request);

	}


	public void createInstance(String var, String exp) throws DbgpException {
		DbgpRequest request = createRequest(OVERTURE_COMMAND);
		request.addOption("-c", "create"); //$NON-NLS-1$
		request.addOption("--", Base64.encode((var + " " + exp).getBytes()));
	}


	public void getClasses() throws DbgpException {
		DbgpRequest request = createRequest(OVERTURE_COMMAND);
		request.addOption("-c", "classes"); //$NON-NLS-1$
		
	}


	public void getCurrentline() throws DbgpException {
		DbgpRequest request = createRequest(OVERTURE_COMMAND);
		request.addOption("-c", "currentline"); //$NON-NLS-1$
		
	}


	public void getDefault(String name) throws DbgpException {
		DbgpRequest request = createRequest(OVERTURE_COMMAND);
		request.addOption("-c", "default"); //$NON-NLS-1$
		
	}


	public void getFiles() throws DbgpException {
		DbgpRequest request = createRequest(OVERTURE_COMMAND);
		request.addOption("-c", "files"); //$NON-NLS-1$
		
	}


	public void getList() throws DbgpException {
		DbgpRequest request = createRequest(OVERTURE_COMMAND);
		request.addOption("-c", "list"); //$NON-NLS-1$
		
	}


	public void getModules() throws DbgpException {
		DbgpRequest request = createRequest(OVERTURE_COMMAND);
		request.addOption("-c", "modules"); //$NON-NLS-1$
		
	}


	public void getPog(String name) throws DbgpException {
		DbgpRequest request = createRequest(OVERTURE_COMMAND);
		request.addOption("-c", "pog"); //$NON-NLS-1$
		request.addOption("--", Base64.encode(name.getBytes()));
		
	}


	public void getSource() throws DbgpException {
		DbgpRequest request = createRequest(OVERTURE_COMMAND);
		request.addOption("-c", "source"); //$NON-NLS-1$
		
	}


	public void getStack() throws DbgpException {
		DbgpRequest request = createRequest(OVERTURE_COMMAND);
		request.addOption("-c", "stack"); //$NON-NLS-1$
		
	}


	public void init() throws DbgpException {
		DbgpRequest request = createRequest(OVERTURE_COMMAND);
		request.addOption("-c", "init"); //$NON-NLS-1$
		
	}


	public void runTrace(String name, int testNo, boolean debug)
			throws DbgpException {
		DbgpRequest request = createRequest(OVERTURE_COMMAND);
		request.addOption("-c", "runtrace"); //$NON-NLS-1$
		request.addOption("--", Base64.encode((name + " " + testNo + " " + debug).getBytes()));
	}


	public void writeLatex(File dir, File file) throws DbgpException {
		DbgpRequest request = createRequest(OVERTURE_COMMAND);
		request.addOption("-c", "latex"); //$NON-NLS-1$
		request.addOption("--", Base64.encode((dir.toURI() + " " + file.toURI()).getBytes()));
		
	}


	public void writeLatexdoc(File dir, File file) throws DbgpException {
		DbgpRequest request = createRequest(OVERTURE_COMMAND);
		request.addOption("-c", "latexdoc"); //$NON-NLS-1$
		request.addOption("--", Base64.encode((dir.toURI() + " " + file.toURI()).getBytes()));
		
	}


	public void writeTrace(File file, int lnum, String display)
			throws DbgpException {
		DbgpRequest request = createRequest(OVERTURE_COMMAND);
		request.addOption("-c", "trace"); //$NON-NLS-1$
		request.addOption("--", Base64.encode((file.toURI() + " " + lnum + " " + display).getBytes()));
		
	}
	
	

	

}
