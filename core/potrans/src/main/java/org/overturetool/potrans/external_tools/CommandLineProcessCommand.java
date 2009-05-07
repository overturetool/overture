package org.overturetool.potrans.external_tools;

/**
 * @author miguel_ferreira
 *
 */
public class CommandLineProcessCommand {

	protected String name = null;
	protected String[] arguments = new String[] {};
	
	public CommandLineProcessCommand(String name, String[] arguments) {
		this.name = name;
		if(arguments != null)
			this.arguments = arguments;
	}
	
	public CommandLineProcessCommand(String commandName) {
		this(commandName, null);
	}
	
	
	public String[] getCommandArray() {
		String[] commandArray = new String[arguments.length + 1];
		
		commandArray[0] = name;
		for(int i = 0; i < arguments.length; i++) 
			commandArray[i + 1] = new String(arguments[i]);
		
		return commandArray;
	}

}
