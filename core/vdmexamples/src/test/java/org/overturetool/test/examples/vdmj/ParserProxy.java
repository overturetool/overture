/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overturetool.test.examples.vdmj;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.overturetool.test.examples.vdmj.VdmjFactories.ParserFactory;
import org.overturetool.test.framework.results.IMessage;
import org.overturetool.test.framework.results.Result;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;
import org.overturetool.vdmj.syntax.SyntaxReader;

public class ParserProxy<T extends SyntaxReader, R>
{
	final ParserFactory<T, R> factory;
	final Set<File> files = new HashSet<File>();

	public ParserProxy(ParserFactory<T, R> reader, Set<File> files)
	{
		this.factory = reader;
		this.files.clear();
		this.files.addAll(files);
	}

	public Set<Result<R>> parse() throws Exception
	{
		Set<Result<R>> completeResult = new HashSet<Result<R>>();

		for (File file : files)
		{
			T reader = null;
			R result = null;
			Set<IMessage> warnings = new HashSet<IMessage>();
			Set<IMessage> errors = new HashSet<IMessage>();
			reader = factory.createReader(factory.createTokenReader(file));
			result = factory.read(reader);

			for (VDMError m : reader.getErrors())
			{
				errors.add(factory.convertMessage(m));
			}

			for (VDMWarning m : reader.getWarnings())
			{
				errors.add(factory.convertMessage(m));
			}

			completeResult.add(new Result<R>(result, warnings, errors));
		}
		return completeResult;
	}
}
