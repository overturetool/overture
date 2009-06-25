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

package org.overturetool.vdmj.syntax;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.overturetool.parser.imp.OvertureParser;
import org.overturetool.vdmj.ast.ASTConverter;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.messages.InternalException;
import org.overturetool.vdmj.messages.VDMError;

/**
 * A class to wrap an OvertureParser in a VDMJ syntax reader.
 */

public class OvertureReader extends SyntaxReader
{
	public final File filename;
	public final OvertureParser theParser;

	public OvertureReader(File filename) throws Exception
	{
		this(filename, Charset.defaultCharset().name());
	}

	public OvertureReader(File filename, String charset) throws Exception
	{
		super();
		this.filename = filename;
		FileInputStream stream = new FileInputStream(filename);
		InputStreamReader theStream = new InputStreamReader(stream, charset);

		try
		{
    		theParser = new OvertureParser(theStream);
    		theParser.parseDocument();
    		theParser.astDocument.setFilename(filename.getPath());
		}
		catch (CGException e)
		{
			throw new InternalException(0, e.getMessage());
		}

		theStream.close();
	}

	@Override
	public void close() throws IOException
	{
		return;
	}

	public ClassList readClasses()
	{
		ASTConverter converter = new ASTConverter(filename, theParser.astDocument);
		return converter.convertDocument();
	}

	@Override
	public void setCurrentModule(String module)
	{
		return;
	}

	@Override
	public String getCurrentModule()
	{
		return "";
	}

	@Override
	public int getErrorCount()
	{
		return theParser.errors;
	}

	@Override
	public List<VDMError> getErrors()
	{
		List<VDMError> list = new Vector<VDMError>();
		// TODO supply parser errors!
		return list;
	}
}
