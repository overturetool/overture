package org.overturetool.eclipse.plugins.launching.console;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.eclipse.dltk.console.ConsoleRequest;
import org.eclipse.dltk.console.IScriptExecResult;
import org.eclipse.dltk.console.IScriptConsoleIO;
import org.eclipse.dltk.console.IScriptInterpreter;
import org.eclipse.dltk.console.InterpreterResponse;
import org.eclipse.dltk.console.ScriptExecResult;
import org.eclipse.dltk.console.ShellResponse;

public class OvertureInterpreter implements IScriptInterpreter,
		ConsoleRequest {

	private static final String COMPLETE_COMMAND = "complete";

	private static final String DESCRIBE_COMMAND = "describe";

	private static final String CLOSE_COMMAND = "close";

	private IScriptConsoleIO protocol;

	private int state;

	// IScriptInterpreter
	public IScriptExecResult exec(String command) throws IOException {
		InterpreterResponse response = protocol.execInterpreter(command);

		state = response.getState();
		return new ScriptExecResult(response.getContent());
	}

	public int getState() {
		return state;
	}

	// IScriptInterpreterShell
	public List getCompletions(String commandLine, int position)
			throws IOException {

		String[] args = new String[] { commandLine, Integer.toString(position) };

		ShellResponse response = protocol.execShell(COMPLETE_COMMAND, args);

		return response.getCompletions();
	}

	public String getDescription(String commandLine, int position)
			throws IOException {
		String[] args = new String[] { commandLine, Integer.toString(position) };

		ShellResponse response = protocol.execShell(DESCRIBE_COMMAND, args);

		return response.getDescription();
	}

	public String[] getNames(String type) throws IOException {
		return null;
	}

	public void close() throws IOException {
		protocol.execShell(CLOSE_COMMAND, new String[] {});
		protocol.close();
	}

	// IScriptConsoleProtocol
	public void consoleConnected(IScriptConsoleIO protocol) {
		this.protocol = protocol;
	}

	public String getInitialOuput() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addInitialListenerOperation(Runnable runnable) {
		// TODO Auto-generated method stub

	}

	public InputStream getInitialOutputStream() {
		return null;
	}

	public boolean isValid() {
		return protocol != null;
	}
}
