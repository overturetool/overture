package org.overturetool.umltrans.Main;


public class MainClass
{
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args)
	{
		try
		{
			new CmdLineProcesser().processCommand(args);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
