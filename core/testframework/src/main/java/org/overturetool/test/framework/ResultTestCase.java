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
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.overturetool.test.framework.results.IMessage;
import org.overturetool.test.framework.results.IResultCombiner;
import org.overturetool.test.framework.results.Result;
import org.overturetool.test.util.XmlResultReaderWritter;
import org.overturetool.test.util.XmlResultReaderWritter.IResultStore;

public abstract class ResultTestCase<R> extends BaseTestCase implements IResultStore<R>
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
	
	
	
	
	protected void compareResults(Result<R> result, String filename)
	{
		if(Properties.recordTestResults)
		{
			//MessageReaderWritter mrw = new MessageReaderWritter(createResultFile(filename));
			//mrw.set(result);
			//mrw.save();
			XmlResultReaderWritter<R> xmlResult = new XmlResultReaderWritter<R>(createResultFile(filename),this);
			xmlResult.setResult(this.getClass().getName(),result);
			try {
				xmlResult.saveInXml();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return;
		}
		
		File file = getResultFile(filename);

		assertNotNull("Result file " + file.getName() + " was not found", file);
		assertTrue("Result file " + file.getAbsolutePath() + " does not exist", file.exists());
		
		//MessageReaderWritter mrw = new MessageReaderWritter(file);
		XmlResultReaderWritter<R> xmlResult = new XmlResultReaderWritter<R>(file,this);
		boolean parsed = xmlResult.loadFromXml();

		assertTrue("Could not read result file: " + file.getName(), parsed);

		if (parsed)
		{
			if(file.getAbsolutePath().contains("SAFER"))
			{//FIXME: remote this filter when SAFER is fixed in the warning reporting
				return;
			}
			
			boolean errorsFound = checkMessages("warning", xmlResult.getWarnings(), result.warnings);
			errorsFound = checkMessages("error", xmlResult.getErrors(), result.errors) || errorsFound;
			errorsFound = !assertEqualResults( xmlResult.getResult().result, result.result) || errorsFound;
			assertFalse("Errors found in file \"" + filename + "\"", errorsFound);
		}
	}
	
	/**
	 * Checks if the results are equal.
	 * @param expected The expected result
	 * @param actual The actual result
	 * @return If equal true or check has to be ignored true is returned else false
	 */
	protected abstract boolean assertEqualResults(R expected,
			R actual);

	protected abstract File createResultFile(String filename);


	protected abstract File getResultFile(String filename);

	public boolean checkMessages(String typeName, List<IMessage> expectedList,
			List<IMessage> list)
	{
		String TypeName = typeName.toUpperCase().toCharArray()[0]
				+ typeName.substring(1);
		boolean errorFound = false;
		for (IMessage w : list)
		{
			boolean isContainedIn = containedIn(expectedList, w);
			if(!isContainedIn)
			{
				System.out.println(TypeName + " not expected: " + w);
				errorFound = true;
			}
			
//			assertTrue(TypeName + " not expected: " + w, isContainedIn);
		}
		for (IMessage w : expectedList)
		{
			boolean isContainedIn = containedIn(list, w);
			if(!isContainedIn)
			{
				System.out.println(TypeName + " expected but not found: " + w);
				errorFound = true;
			}
			//assertTrue(TypeName + " expected but not found: " + w, isContainedIn);
		}
		return errorFound;
	}

	private static boolean containedIn(List<IMessage> list, IMessage m)
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

	
	protected <T> Result<T> mergeResults(Set<? extends Result<T>> parse,
			IResultCombiner<T> c)
	{
		List<IMessage> warnings = new Vector<IMessage>();
		List<IMessage> errors = new Vector<IMessage>();
		T result = null;

		for (Result<T> r : parse)
		{
			warnings.addAll(r.warnings);
			errors.addAll(r.errors);
			result = c.combine(result, r.result);
		}
		return new Result<T>(result, warnings, errors);
	}

}
