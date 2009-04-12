/**
 * 
 */
package org.overturetool.potrans.preparation;

import junit.framework.TestCase;

/**
 * @author gentux
 *
 */
public class CommandLineProcessCommandTest extends TestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link org.overturetool.potrans.preparation.CommandLineProcessCommand#CommandLineProcessCommand(java.lang.String, java.lang.String[])}.
	 */
	public void testCommandLineProcessCommandNameArguments() {
		String name = "name";
		String argument = "argument";
		String[] arguments = new String[] { argument };
		CommandLineProcessCommand command = new CommandLineProcessCommand(name, arguments);
		
		assertEquals(name, command.name);
		assertEquals(argument, command.arguments[0]);
	}
	
	public void testCommandLineProcessCommandNullNameArguments() {
		String name = null;
		String argument = "argument";
		String[] arguments = new String[] { argument };
		CommandLineProcessCommand command = new CommandLineProcessCommand(name, arguments);
		
		assertNull(command.name);
		assertEquals(argument, command.arguments[0]);
	}
	
	public void testCommandLineProcessCommandEmptyNameArguments() {
		String name = "";
		String argument = "argument";
		String[] arguments = new String[] { argument };
		CommandLineProcessCommand command = new CommandLineProcessCommand(name, arguments);
		
		assertEquals(name, command.name);
		assertEquals(argument, command.arguments[0]);
	}
	
	public void testCommandLineProcessCommandNameNullArguments() {
		String name = "name";
		String[] arguments = null;
		CommandLineProcessCommand command = new CommandLineProcessCommand(name, arguments);
		
		assertEquals(name, command.name);
		assertNotNull(command.arguments);
		assertEquals(0, command.arguments.length);
	}
	
	public void testCommandLineProcessCommandNameEmptyArguments() {
		String name = "name";
		String[] arguments = new String[] {};
		CommandLineProcessCommand command = new CommandLineProcessCommand(name, arguments);
		
		assertEquals(name, command.name);
		assertNotNull(command.arguments);
		assertEquals(0, command.arguments.length);
	}

	/**
	 * Test method for {@link org.overturetool.potrans.preparation.CommandLineProcessCommand#CommandLineProcessCommand(java.lang.String)}.
	 */
	public void testCommandLineProcessCommandName() {
		String name = "name";
		CommandLineProcessCommand command = new CommandLineProcessCommand(name);
		
		assertEquals(name, command.name);
		assertNotNull(command.arguments);
		assertEquals(0, command.arguments.length);
	}
	
	public void testCommandLineProcessCommandNullName() {
		CommandLineProcessCommand command = new CommandLineProcessCommand(null);
		
		assertNull(command.name);
		assertNotNull(command.arguments);
		assertEquals(0, command.arguments.length);
	}
	
	public void testCommandLineProcessCommandEmptyName() {
		String name = "";
		CommandLineProcessCommand command = new CommandLineProcessCommand(name);
		
		assertEquals(name, command.name);
		assertNotNull(command.arguments);
		assertEquals(0, command.arguments.length);
	}

	/**
	 * Test method for {@link org.overturetool.potrans.preparation.CommandLineProcessCommand#getCommandArray()}.
	 */
	public void testGetCommandArray() {
		String name = "name";
		String argument1 = "argument1";
		String argument2 = "argument2";
		String[] arguments = new String[] { argument1, argument2 };
		CommandLineProcessCommand command = new CommandLineProcessCommand(name, arguments);
		String[] expected = new String[] { name, argument1, argument2 };
		
		String[] actual = command.getCommandArray();
		
		assertEquals(expected.length, actual.length);
		for(int i = 0; i < expected.length; i++)
			assertEquals(expected[i], actual[i]);
	}
	
	public void testGetCommandArrayNullName() {
		String name = null;
		String argument1 = "argument1";
		String argument2 = "argument2";
		String[] arguments = new String[] { argument1, argument2 };
		CommandLineProcessCommand command = new CommandLineProcessCommand(name, arguments);
		String[] expected = new String[] { name, argument1, argument2 };
		
		String[] actual = command.getCommandArray();
		
		assertEquals(expected.length, actual.length);
		for(int i = 0; i < expected.length; i++)
			assertEquals(expected[i], actual[i]);
	}
	
	public void testGetCommandArrayEmptyName() {
		String name = "";
		String argument1 = "argument1";
		String argument2 = "argument2";
		String[] arguments = new String[] { argument1, argument2 };
		CommandLineProcessCommand command = new CommandLineProcessCommand(name, arguments);
		String[] expected = new String[] { name, argument1, argument2 };
		
		String[] actual = command.getCommandArray();
		
		assertEquals(expected.length, actual.length);
		for(int i = 0; i < expected.length; i++)
			assertEquals(expected[i], actual[i]);
	}
	
	public void testGetCommandArrayNullArguments() {
		String name = "name";
		String[] arguments = null;
		CommandLineProcessCommand command = new CommandLineProcessCommand(name, arguments);
		String[] expected = new String[] { name };
		
		String[] actual = command.getCommandArray();
		
		assertEquals(expected.length, actual.length);
		for(int i = 0; i < expected.length; i++)
			assertEquals(expected[i], actual[i]);
	}
	
	public void testGetCommandArrayEmptyArguments() {
		String name = "name";
		String[] arguments = new String[] {};
		CommandLineProcessCommand command = new CommandLineProcessCommand(name, arguments);
		String[] expected = new String[] { name };
		
		String[] actual = command.getCommandArray();
		
		assertEquals(expected.length, actual.length);
		for(int i = 0; i < expected.length; i++)
			assertEquals(expected[i], actual[i]);
	}

}
