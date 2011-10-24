package org.overturetool.test.framework.examples;

public class Message implements IMessage
{
	private int number;

	private int line;

	private int col;

	private String message;
	
	public Message(int number, int line, int col, String message)
	{
		this.number = number;
		this.line = line;
		this.col= col;
		this.message = message;
	}

	public void setNumber(int number)
	{
		this.number = number;
	}

	public int getNumber()
	{
		return number;
	}

	public void setLine(int line)
	{
		this.line = line;
	}

	public int getLine()
	{
		return line;
	}

	public void setCol(int col)
	{
		this.col = col;
	}

	public int getCol()
	{
		return col;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public String getMessage()
	{
		return message;
	}

	

}
