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

import java.util.List;

import org.overturetool.vdmj.definitions.BUSClassDefinition;
import org.overturetool.vdmj.definitions.CPUClassDefinition;
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
	protected ExitStatus parse(List<String> files)
	{
		classes.clear();
		LexLocation.resetLocations();
		long before = System.currentTimeMillis();
		long convert = 0;
   		int perrs = 0;

   		for (String file: files)
   		{
   			try
   			{
   				OvertureReader reader = new OvertureReader(file, filecharset);
				long beforeConvert = System.currentTimeMillis();
    			classes.addAll(reader.readClasses());
    			convert += System.currentTimeMillis() - beforeConvert;
    			perrs += reader.getErrorCount();
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

  		long after = System.currentTimeMillis();

   		info("Overture parsed " + plural(classes.size(), "class", "es") + " in " +
   			(double)(after-before)/1000 + " secs (" +
   			(double)convert/1000 + " secs AST convert). ");
   		infoln(perrs == 0 ? "No syntax errors" :
   			"Found " + plural(perrs, "syntax error", "s"));

   		return perrs == 0 ? ExitStatus.EXIT_OK : ExitStatus.EXIT_ERRORS;
	}

	@Override
	protected ExitStatus typeCheck()
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
