package org.overturetool.umltrans.Main;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public abstract class CmdLineHelper
{
	public void processCommand(String[] args) throws Exception
	{
		Object[] tmp = exstractParameters(args);
		Hashtable<String, String> par = (Hashtable<String, String>) tmp[0];
		List<String> files = (List<String>) tmp[1];
		
		if (args.length == 0 || args[0].replace("--", "-").startsWith("-help")
				|| args[0].startsWith("?") || args[0].startsWith("-?")
				|| args[0].startsWith("/?"))
		{
			printHelp();
			return;
			// } else if (args[0].equals("-GUI")) {
			// RunGUI();
			// return;
		} else
		{
			handleCommand(par,files);
		}
	}

	protected abstract void handleCommand(Hashtable<String, String> parameters, List<String> files) throws Exception;

	protected  String[] paramterTypes;
	
	protected final static String HelpParameter="-help";
	
	protected static boolean containsKeys(Hashtable<String, String> parameters, String[] keys)
	{
		boolean containsAll=true;
		for (String string : keys)
		{
			if(!parameters.containsKey(string))
				containsAll=false;
		}
		return containsAll;
	}
	
//	private static String[] splitInputFiles(String files, String splitter)
//	{
//		if (files.contains(splitter))
//			return files.split(splitter);
//		else
//			return new String[] { files };
//
//	}

	public abstract  void printHelp();


	private  Object[] exstractParameters(String[] parameters)
	{

		Hashtable<String, String> pars = new Hashtable<String, String>();
		List<String> specFiles = null;
		try
		{

			int lastFoundParameter = 0;
			for (int i = 0; i < parameters.length; i++)
			{
				for (String parType : paramterTypes)
				{
					if (parameters[i].equals(parType)
							&& parameters.length > i + 1)
					{
						pars.put(parType, parameters[i + 1]);
						lastFoundParameter = i;
					}
				}
			}

			ArrayList<String> specF = new ArrayList<String>();
			if (lastFoundParameter + 2 < parameters.length)
				for (int i = lastFoundParameter + 2; i < parameters.length; i++)
				{
					specF.add(parameters[i]);
				}

	//		specFiles = new String[specF.size()];
		//	specF.toArray(specFiles);
specFiles = specF;
			
		} catch (Exception e)
		{
			// TODO: handle exception
		}
		return new Object[] { pars, specFiles };
	}
}
