/*******************************************************************************
 *
 *	Copyright (c) 2008 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overturetool.vdmj;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.overturetool.vdmj.definitions.BUSClassDefinition;
import org.overturetool.vdmj.definitions.CPUClassDefinition;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.messages.MessageException;
import org.overturetool.vdmj.syntax.OvertureReader;

/**
 * The main class of the VICE Overture parser/checker/interpreter.
 */

public class VDMOV extends VDMPP
{
	public VDMOV()
	{
		Settings.dialect = Dialect.VDM_RT;		// Overture is VICE
	}

	/**
	 * @see org.overturetool.vdmj.VDMJ#parse(java.util.List)
	 */

	@Override
	public ExitStatus parse(List<File> files)
	{
		classes.clear();
		LexLocation.resetLocations();
		long convert = 0;
   		int perrs = 0;
   		long duration = 0;

   		for (File file: files)
   		{
   			try
   			{
   				if (file.getName().endsWith(".lib"))
   				{
   					FileInputStream fis = new FileInputStream(file);
   	    	        GZIPInputStream gis = new GZIPInputStream(fis);
   	    	        ObjectInputStream ois = new ObjectInputStream(gis);

   	    	        ClassList loaded = null;
   	    	        long begin = System.currentTimeMillis();

   	    	        try
   	    	        {
   	    	        	loaded = (ClassList)ois.readObject();
   	    	        }
       	 			catch (Exception e)
       				{
       	   				println(file + " is not a valid VDM++ library");
       	   				perrs++;
       	   				continue;
       				}
       	 			finally
       	 			{
       	 				ois.close();
       	 			}


   	    	        long end = System.currentTimeMillis();
       	 			loaded.setLoaded();
   	    	        classes.addAll(loaded);
   	    	        classes.remap();

   	    	   		infoln("Loaded " + plural(loaded.size(), "class", "es") +
   	    	   			" from " + file + " in " + (double)(end-begin)/1000 + " secs");
   				}
   				else
   				{
   					long before = System.currentTimeMillis();
       				OvertureReader reader = new OvertureReader(file, filecharset);
    				long beforeConvert = System.currentTimeMillis();
        			classes.addAll(reader.readClasses());
        			convert += System.currentTimeMillis() - beforeConvert;
        			perrs += reader.getErrorCount();
        	  		long after = System.currentTimeMillis();
        	  		duration += (after - before);
   				}
    		}
			catch (MessageException e)
			{
   				println(e.toString());
			}
			catch (Throwable e)
			{
   				println(e.toString());
   				perrs++;
			}
   		}

   		int n = classes.notLoaded();

   		if (n > 0)
   		{
       		info("Overture parsed " + plural(n, "class", "es") + " in " +
       			(double)(duration)/1000 + " secs (" +
       			(double)convert/1000 + " secs AST convert). ");
       		infoln(perrs == 0 ? "No syntax errors" :
       			"Found " + plural(perrs, "syntax error", "s"));
   		}

   		return perrs == 0 ? ExitStatus.EXIT_OK : ExitStatus.EXIT_ERRORS;
	}

	@Override
	public ExitStatus typeCheck()
	{
		try
		{
			classes.add(new CPUClassDefinition());
  			classes.add(new BUSClassDefinition());
		}
		catch (Exception e)
		{
			throw new MessageException("Internal 0011: CPU or BUS creation failure");
		}

		return super.typeCheck();
	}
}
