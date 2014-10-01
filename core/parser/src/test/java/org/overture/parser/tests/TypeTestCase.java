/*
 * #%~
 * The VDM parser
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.parser.tests;

import java.io.File;

import org.overture.ast.node.INode;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.syntax.ParserException;
import org.overture.parser.syntax.TypeReader;
import org.overture.parser.tests.framework.BaseParserTestCase;

public class TypeTestCase extends BaseParserTestCase<TypeReader, INode>
{
	static boolean hasRunBefore = false;

	public TypeTestCase(File file)
	{
		super(file);
	}

	public TypeTestCase()
	{

	}

	public TypeTestCase(File rootSource, String name, String content)
	{
		super(rootSource, name, content);
	}

	@Override
	protected TypeReader getReader(LexTokenReader ltr)
	{
		return new TypeReader(ltr);
	}

	@Override
	protected INode read(TypeReader reader) throws ParserException,
			LexException
	{
		return reader.readType();
	}

	@Override
	protected String getReaderTypeName()
	{
		return "Type";
	}

	@Override
	protected void setHasRunBefore(boolean b)
	{
		hasRunBefore = b;
	}

	@Override
	protected boolean hasRunBefore()
	{
		return hasRunBefore;
	}
}
