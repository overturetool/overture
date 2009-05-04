package org.overturetool.eclipse.plugins.editor.overturedebugger.commands;

import java.util.ArrayList;

import org.eclipse.dltk.dbgp.DbgpBaseCommands;
import org.eclipse.dltk.dbgp.DbgpRequest;
import org.eclipse.dltk.dbgp.IDbgpCommunicator;
import org.eclipse.dltk.dbgp.exceptions.DbgpException;
import org.eclipse.dltk.dbgp.internal.utils.DbgpXmlParser;

@SuppressWarnings("restriction")
public class DbgpSourceFileCommand extends DbgpBaseCommands{

	private static final String SET_SOURCE_FILES = "set_source_files"; //$NON-NLS-1$
	
	public DbgpSourceFileCommand(IDbgpCommunicator communicator) {
		super(communicator);
	}
	

	public boolean setSourceFiles(ArrayList<String> files) throws DbgpException {
		DbgpRequest request = createRequest(SET_SOURCE_FILES);
		request.addOption("-n", SET_SOURCE_FILES); //$NON-NLS-1$
		request.addOption("-v", "test"); //$NON-NLS-1$
		return DbgpXmlParser.parseSuccess(communicate(request));
	}

}
