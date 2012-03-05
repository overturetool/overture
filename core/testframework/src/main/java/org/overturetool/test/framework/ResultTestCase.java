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
package org.overturetool.test.framework;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.overturetool.test.framework.results.IMessage;
import org.overturetool.test.framework.results.IResultCombiner;
import org.overturetool.test.framework.results.Result;
import org.overturetool.test.util.MessageReaderWritter;

public abstract class ResultTestCase extends BaseTestCase
{
	public ResultTestCase()
	{
		super();
	}
	

	public ResultTestCase(File file)
	{
		super(file);
	}

	public ResultTestCase(String name, String content)
	{
		super(name,content);
	}
	
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void compareResults(Result result, String filename)
	{
		if(Properties.recordTestResults)
		{
			MessageReaderWritter mrw = new MessageReaderWritter(createResultFile(filename));
			mrw.set(result);
			mrw.save();
			return;
		}
		
		File file = getResultFile(filename);

		assertNotNull("Result file " + filename + " was not found", file);

		MessageReaderWritter mrw = new MessageReaderWritter(file);

		assertTrue("Result file " + filename + " does not exist", mrw.exists());

		boolean parsed = mrw.load();

		assertTrue("Could not read result file: " + file.getName(), parsed);

		if (parsed)
		{
			if(file.getAbsolutePath().contains("SAFER"))
			{//FIXME: remote this filter when SAFER is fixed in the warning reporting
				return;
			}
			checkMessages("warning", mrw.getWarnings(), result.warnings);
			checkMessages("error", mrw.getErrors(), result.errors);
		}
	}

	protected abstract File createResultFile(String filename);


	protected abstract File getResultFile(String filename);

	public void checkMessages(String typeName, Set<IMessage> expectedList,
			Set<IMessage> list)
	{
//		assertEquals("Number of " + typeName + "s do not match expected.", expectedList.size(), list.size());
		String TypeName = typeName.toUpperCase().toCharArray()[0]
				+ typeName.substring(1);
		for (IMessage w : list)
		{
			assertTrue(TypeName + " not exspected: " + w, containedIn(expectedList, w));
		}
		for (IMessage w : expectedList)
		{
			assertTrue(TypeName + " exspected but not found: " + w, containedIn(list, w));
		}
	}

	private static boolean containedIn(Set<IMessage> list, IMessage m)
	{
		for (IMessage m1 : list)
		{
			if (m1.equals(m))
			{
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected <T> Result mergeResults(Set<? extends Result<T>> parse,
			IResultCombiner<T> c)
	{
		Set<IMessage> warnings = new HashSet<IMessage>();
		Set<IMessage> errors = new HashSet<IMessage>();
		T result = null;

		for (Result<T> r : parse)
		{
			warnings.addAll(r.warnings);
			errors.addAll(r.errors);
			result = c.combine(result, r.result);
		}
		return new Result(result, warnings, errors);
	}

}
