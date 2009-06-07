package org.overturetool.potrans.external_tools;

/**
 * @author miguel_ferreira
 *
 */
public class ConsoleProcessCommand {

	protected String name = null;
	protected String[] arguments = new String[] {};
	
	public ConsoleProcessCommand(String name, String[] arguments) {
		this.name = name;
		if(arguments != null)
			this.arguments = arguments;
	}
	
	public ConsoleProcessCommand(String commandName) {
		this(commandName, null);
	}
	
	private ConsoleProcessCommand(ConsoleProcessCommand cmdObject) {
		this(cmdObject.getName(), cmdObject.getArguments());
	}
	
	public String[] getCommandArray() {
		String[] commandArray = new String[arguments.length + 1];
		
		commandArray[0] = name;
		for(int i = 0; i < arguments.length; i++) 
			commandArray[i + 1] = new String(arguments[i]);
		
		return commandArray;
	}

	public String getName() {
		return name;
	}


	public String[] getArguments() {
		return arguments.clone();
	}
	
	
	public ConsoleProcessCommand clone() {
		return new ConsoleProcessCommand(this);
	}
	
}
