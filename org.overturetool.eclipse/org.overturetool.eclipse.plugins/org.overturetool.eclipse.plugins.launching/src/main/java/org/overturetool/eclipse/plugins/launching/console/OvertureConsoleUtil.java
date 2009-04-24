package org.overturetool.eclipse.plugins.launching.console;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.console.ScriptConsoleServer;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.IExecutionEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.launching.ScriptLaunchUtil;
import org.overturetool.eclipse.plugins.editor.core.OvertureNature;
import org.overturetool.eclipse.plugins.launching.LaunchingPlugin;

public class OvertureConsoleUtil {

	public static void runDefaultTclInterpreter(
			OvertureInterpreter interpreter) throws CoreException,
			IOException {
		ScriptConsoleServer server = ScriptConsoleServer.getInstance();

		String id = server.register(interpreter);
		String port = Integer.toString(server.getPort());

		String[] args = new String[] { "127.0.0.1", port, id };

		// TODO: Add environments support
		IExecutionEnvironment exeEnv = (IExecutionEnvironment) EnvironmentManager
				.getLocalEnvironment().getAdapter(IExecutionEnvironment.class);
		IFileHandle scriptFile = LaunchingPlugin.getDefault()
				.getConsoleProxy(exeEnv);
		ScriptLaunchUtil.runScript(OvertureNature.NATURE_ID, scriptFile,
				null, null, args, null);
	}
}
