package org.overturetool.potrans.preparation;

/**
 * @author miguel_ferreira
 *
 */
public class CommandLineProcessCommand {

	private String commandName = null;
	private String[] arguments = new String[] {};
	
	public CommandLineProcessCommand(String commandName, String[] arguments) {
		this.commandName = commandName;
		if(arguments != null)
			this.arguments = arguments;
	}
	
	public CommandLineProcessCommand(String commandName) {
		this(commandName, null);
	}
	
	
	public String[] getCommandArray() {
		String[] commandArray = new String[arguments.length + 1];
		
		commandArray[0] = commandName;
		for(int i = 0; i < arguments.length; i++) 
			commandArray[i + 1] = new String(arguments[i]);
		
		return commandArray;
	}

}
