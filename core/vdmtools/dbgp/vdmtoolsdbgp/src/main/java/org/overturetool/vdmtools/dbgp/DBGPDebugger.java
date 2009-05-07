package org.overturetool.vdmtools.dbgp;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.overturetool.vdmtools.VDMToolsProject;

public class DBGPDebugger extends AbstractDBGPDebugger{

	
	public DBGPDebugger(
			String host,
			int port,
			String ideKey,
			String dialect,
			String expression,
			ArrayList<String> files
			) throws IOException {
			super(host, port, ideKey, dialect, expression, files);
			try {
				ArrayList<String> osFileList = new ArrayList<String>();
				for (String fileUri : files) {
					try {
						osFileList.add((new File(new URI(fileUri)).getAbsolutePath()));
						
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
				}
				VDMToolsProject.getInstance().addFilesToProject(osFileList);
				VDMToolsProject.getInstance().parseProject();
				VDMToolsProject.getInstance().typeCheckProject();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void setupCommands() {
		addStrategy(DBGPCommandType.STDOUT, new StdOutCommand());
		addStrategy(DBGPCommandType.STDIN, new StdInCommand());
		addStrategy(DBGPCommandType.STDERR, new StdErrCommand());
		addStrategy(DBGPCommandType.STDERR, new StdErrCommand());
		addStrategy(DBGPCommandType.FEATURE_SET, new FeatureSetCommand());
		addStrategy(DBGPCommandType.FEATURE_GET, new FeatureGetCommand());
		addStrategy(DBGPCommandType.RUN , new RunCommand(expression));
		addStrategy(DBGPCommandType.BREAKPOINT_SET, new SetBreakPointCommand());
		addStrategy(DBGPCommandType.STACK_GET, new StackGetCommand());
		addStrategy(DBGPCommandType.BREAKPOINT_GET, new GetBreakPointCommand());
		addStrategy(DBGPCommandType.CONTEXT_NAMES, new ContextNamesCommand());
		addStrategy(DBGPCommandType.CONTEXT_GET, new ContextGetCommand());
		addStrategy(DBGPCommandType.SOURCE, new SourceCommand());
		addStrategy(DBGPCommandType.STEP_OVER, new StepOverCommand());
		addStrategy(DBGPCommandType.EVAL, new EvalCommand());
		addStrategy(DBGPCommandType.BREAKPOINT_REMOVE, new RemoveBreakPointCommand());
		addStrategy(DBGPCommandType.STOP, new StopCommand());
		addStrategy(DBGPCommandType.STEP_INTO, new StepIntoCommand());
		addStrategy(DBGPCommandType.BREAKPOINT_UPDATE, new UpdateBreakPointCommand());
		addStrategy(DBGPCommandType.STACK_DEPTH, new StackDepthCommand());
		addStrategy(DBGPCommandType.PROPERTY_GET, new PropertyGetCommand());
		addStrategy(DBGPCommandType.PROPERTY_SET, new PropertySetCommand());


		// ************************************
		// ** not available in the Corba interface
		// *************************************
//		strategies.put("step_out", new StepOutCommand(this));
//		strategies.put("break", new BreakCommand(this));
		
	}

	@Override
	protected String init() {
		
		String fileName = this.getFiles().get(0).getAbsolutePath();
		String response = 
			"<init appid=\""+ dialect.name() + "\"\r\n" +
			"      idekey=\""+ ideKey + "\"\r\n" +
			"      session=\"" + sessionId + "\"\r\n" +
			"      thread=\"" + Thread.currentThread().getId() + "\"\r\n" + 
			"      parent=\"" + dialect.name() + "\"\r\n" + 
			"      language=\"" + dialect.name() + "\"\r\n" + 
			"      protocol_version=\"1.0\"\r\n" + 
			"      fileuri=\"file://" + fileName + "\"\r\n" + 
			"/>";
		return response;
	}
}
