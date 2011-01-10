/*******************************************************************************
 *
 *	Copyright (C) 2008 Fujitsu Services Ltd.
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

package org.overturetool.vdmj.lex;

import java.io.IOException;
import java.io.InputStream;

/**
 * A class to read an ODF encoded VDM file.
 */

public class ODFStreamReader extends XMLStreamReader
{
	public ODFStreamReader(InputStream in) throws IOException
	{
		super(in, "content.xml");
	}

	@Override
	protected String despace(String in)
	{
		int n = 1;
		String replace = "";

		while (in.indexOf("<text:s text:c") > 0)
		{
			String pattern = "<text:s text:c=\"" + (n++) + "\"/>";
			replace = replace + " ";

			if (in.indexOf(pattern) > 0)
			{
				in = in.replaceAll(pattern, replace);
			}
		}

		return in
    		.replaceAll("<text:tab/>", "\t")
			.replaceAll("<text:s/>", " ")
    		.replaceAll("</text:p>", "\n")
			.replaceAll("<text:p [^/>]+/>", "\n");
	}
}
