/*
 * #%~
 * Combinatorial Testing
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
package org.overture.ide.plugins.combinatorialtesting;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.overture.ct.utils.TraceTestResult;
import org.overture.ct.utils.TraceXmlWrapper;
import org.overture.ct.utils.XmlFileWriter;
import org.overture.interpreter.traces.TraceReductionType;
import org.overture.interpreter.traces.Verdict;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class TracesXmlStoreReader extends DefaultHandler
{
	public class TraceStatusXml
	{
		private Integer testCount;
		private Integer skippedTestCount;
		private Verdict verdict;
		private Integer faildCount;
		private Integer inconclusiveCount;

		public void setTestCount(Integer testCount)
		{
			this.testCount = testCount;
		}

		public Integer getTestCount()
		{
			return testCount;
		}

		public void setSkippedTestCount(Integer skippedTestCount)
		{
			this.skippedTestCount = skippedTestCount;
		}

		public Integer getSkippedTestCount()
		{
			return skippedTestCount;
		}

		public void setVerdict(Verdict verdict)
		{
			this.verdict = verdict;
		}

		public Verdict getVerdict()
		{
			return verdict;
		}

		public void setFaildCount(Integer faildCount)
		{
			this.faildCount = faildCount;
		}

		public Integer getFaildCount()
		{
			return faildCount;
		}

		public void setInconclusiveCount(Integer inconclusiveCount)
		{
			this.inconclusiveCount = inconclusiveCount;
		}

		public Integer getInconclusiveCount()
		{
			return inconclusiveCount;
		}
	}

	public class TraceInfo
	{
		private String className;
		private String traceName;
		private Integer testCount;
		private Float subset;
		private TraceReductionType traceReductionType;
		private Long seed;

		public void setSeed(Long seed)
		{
			this.seed = seed;
		}

		public Long getSeed()
		{
			return seed;
		}

		public void setTraceReductionType(TraceReductionType traceReductionType)
		{
			this.traceReductionType = traceReductionType;
		}

		public TraceReductionType getTraceReductionType()
		{
			return traceReductionType;
		}

		public void setSubset(Float subset)
		{
			this.subset = subset;
		}

		public Float getSubset()
		{
			return subset;
		}

		public void setTestCount(Integer testCount)
		{
			this.testCount = testCount;
		}

		public Integer getTestCount()
		{
			return testCount;
		}

		public void setTraceName(String traceName)
		{
			this.traceName = traceName;
		}

		public String getTraceName()
		{
			return traceName;
		}

		public void setClassName(String className)
		{
			this.className = className;
		}

		public String getClassName()
		{
			return className;
		}
	}

	StringBuilder data = new StringBuilder();
	File file;
	String className;
	String traceName;
	Map<String, TraceInfo> traceCount = new HashMap<String, TraceInfo>();

	Map<String, TraceStatusXml> traceStatus = new HashMap<String, TraceStatusXml>();

	List<TraceTestResult> traceTestResults;
	TraceTestResult currentResult;
	int currentResultIndex;
	Locator locator;

	boolean inClass = false;
	boolean inTrace = false;
	boolean initialParse = true;
	boolean traceTestParse = false;

	boolean insertArgument = false;
	boolean insertResult = false;

	Integer traceTaceTestStartNumber;
	Integer traceTaceTestStopNumber;
	String currentTraceName;
	final String charset;

	public TracesXmlStoreReader(File file, String className)
			throws SAXException, IOException
	{
		this(file, className, "utf-8");
	}

	public TracesXmlStoreReader(File file, String className, String charset)
			throws SAXException, IOException
	{
		super();
		this.file = file;
		this.className = className;
		this.charset = charset;
		parse();
		initialParse = false;
	}

	private void parse() throws SAXException, IOException
	{
		XMLReader xr = XMLReaderFactory.createXMLReader();
		TracesXmlStoreReader handler = this;
		xr.setContentHandler(handler);
		xr.setErrorHandler(handler);

		// FileReader r = new FileReader(this.file); this cannot be used since it doesnt suppport char set
		InputStreamReader r = new InputStreamReader(new FileInputStream(this.file), "UTF-8");
		InputSource input = new InputSource(r);
		input.setEncoding("UTF-8");
		xr.parse(input);
		r.close();

	}

	public List<TraceTestResult> getTraceTestResults(String traceName,
			Integer startNumber, Integer stopNumber) throws SAXException,
			IOException
	{
		traceTestResults = new ArrayList<TraceTestResult>();

		this.traceTaceTestStartNumber = startNumber;
		this.traceTaceTestStopNumber = stopNumber;
		this.traceName = traceName;
		this.traceTestParse = true;

		parse();
		List<TraceTestResult> traceTestResults1 = traceTestResults;
		traceTestResults = null;
		return traceTestResults1;
	}

	// //////////////////////////////////////////////////////////////////
	// Event handlers.
	// //////////////////////////////////////////////////////////////////

	@Override
	public void startDocument()
	{
		// System.out.println("Start document");
	}

	@Override
	public void endDocument()
	{
		// System.out.println("End document");
	}

	@Override
	public void setDocumentLocator(Locator locator)
	{
		this.locator = locator;
	}

	@Override
	public void startElement(String uri, String name, String qName,
			Attributes atts)
	{
		if (name.equals(TraceXmlWrapper.CLASS_TAG))
		{
			String cName = atts.getValue(TraceXmlWrapper.NAME_TAG);
			if (cName != null && cName.equals(className))
			{
				inClass = true;
			}
		} else if (inClass && name.equals(TraceXmlWrapper.TRACE_TAG))
		{
			String tName = atts.getValue(TraceXmlWrapper.NAME_TAG);

			inTrace = true;
			currentTraceName = tName;
			if (initialParse)
			{
				TraceInfo info = new TraceInfo();
				info.setTestCount(Integer.parseInt(atts.getValue(TraceXmlWrapper.NUMBER_OF_TESTS_TAG)));
				info.setSubset(Float.parseFloat(atts.getValue(TraceXmlWrapper.SUBSET)));
				info.setTraceReductionType(TraceReductionType.valueOf(atts.getValue(TraceXmlWrapper.TRACE_REDUCTION)));
				info.setSeed(Long.parseLong(atts.getValue(TraceXmlWrapper.SEED)));
				info.setClassName(className);
				info.setTraceName(currentTraceName);
				traceCount.put(atts.getValue(TraceXmlWrapper.NAME_TAG), info);
			}
		} else if (inClass && name.equals(TraceXmlWrapper.TRACE_TAG))
		{
			String tName = atts.getValue(TraceXmlWrapper.NAME_TAG);
			if (tName != null && tName.equals(traceName))
			{
				inTrace = true;
			}
		} else if (inClass
				&& this.traceTestParse
				&& (name.equals(TraceXmlWrapper.TEST_CASE_TAG) || name.equals(TraceXmlWrapper.RESULT_TAG))
				&& traceName.equals(currentTraceName))
		{
			String numberValue = atts.getValue(TraceXmlWrapper.NUMBER_TAG);
			if (numberValue != null)
			{
				Integer number = Integer.parseInt(numberValue);
				if (number != null && traceTaceTestStartNumber <= number
						&& traceTaceTestStopNumber >= number)
				{

					TraceTestResult res = getResult(number); // new
					String verdict = atts.getValue(TraceXmlWrapper.VERDICT_TAG);
					if (name.equals(TraceXmlWrapper.TEST_CASE_TAG))
					{
						this.insertArgument = true;
					} else if (name.equals(TraceXmlWrapper.RESULT_TAG))
					{
						res.setStatus(getVerdict(verdict));
						this.insertResult = true;
					}
				}
			}
		}

		if (initialParse && inClass && inTrace
				&& name.equals(TraceXmlWrapper.STATUS_TAG))
		{
			TraceStatusXml tmp = new TraceStatusXml();
			tmp.setFaildCount(Integer.parseInt(atts.getValue(TraceXmlWrapper.NUMBER_OF_FAILD_TESTS_TAG)));
			tmp.setInconclusiveCount(Integer.parseInt(atts.getValue(TraceXmlWrapper.NUMBER_OF_INCONCLUSIVE_TESTS_TAG)));
			tmp.setSkippedTestCount(Integer.parseInt(atts.getValue(TraceXmlWrapper.NUMBER_OF_SKIPPED_TESTS_TAG)));
			tmp.setVerdict(getVerdict(atts.getValue(TraceXmlWrapper.VERDICT_TAG)));
			traceStatus.put(currentTraceName, tmp);
		}

	}

	private TraceTestResult getResult(Integer number)
	{

		for (int i = 0; i < traceTestResults.size(); i++)
		{
			TraceTestResult res = traceTestResults.get(i);
			if (res.getNumber().equals(number))
			{
				currentResultIndex = i;
				return res;
			}
		}

		TraceTestResult res = new TraceTestResult();
		res.setNumber(number);
		traceTestResults.add(res);
		currentResultIndex = traceTestResults.size() - 1;
		return res;

	}

	private static Verdict getVerdict(String verdict)
	{
		if (verdict != null)
		{
			try
			{
				return Enum.valueOf(Verdict.class, verdict);
			} catch (IllegalArgumentException ex)
			{
				return null;
			}
		}

		return null;
	}

	@Override
	public void endElement(String uri, String name, String qName)
	{

		if (inClass && inTrace && traceTestResults != null
				&& traceTestResults.size() > 0 && data.toString().length() > 0) 
		{
			if (insertArgument)
			{
				List<String> arguments = new ArrayList<String>();
				for (String string : XmlFileWriter.deNormalizeValue(data.toString()).trim().split(";"))
				{
					arguments.add(string.trim());
				}
				traceTestResults.get(currentResultIndex).setArguments(arguments);

			} else if (insertResult)
			{
				List<String> results = new ArrayList<String>();
				for (String string : XmlFileWriter.deNormalizeValue(data.toString()).trim().split(";"))
				{
					results.add(string.trim());
				}
				traceTestResults.get(currentResultIndex).setResults(results);

			}

		}

		data = new StringBuilder();

		if (name.equals(TraceXmlWrapper.CLASS_TAG))
		{
			inClass = false;
		} else if (inClass && name.equals(TraceXmlWrapper.TRACE_TAG))
		{
			inTrace = false;
		} else if (inClass && inTrace
				&& name.equals(TraceXmlWrapper.TEST_CASE_TAG))
		{
			insertArgument = false;
		} else if (inClass && inTrace
				&& name.equals(TraceXmlWrapper.RESULT_TAG))
		{
			insertResult = false;
		}
	}

	@Override
	public void characters(char ch[], int start, int length)
	{
		StringBuilder sb = new StringBuilder();

		for (int i = start; i < start + length; i++)
		{
			switch (ch[i])
			{
				case '\n':
					break;
				case '\r':
					break;
				case '\t':
					break;
				default:
					sb.append(ch[i]);
					break;
			}
		}


		data.append(sb.toString());
	}

	@Override
	public void error(SAXParseException e)
	{

	}

	@Override
	public void warning(SAXParseException e)
	{

	}

	@Override
	public void skippedEntity(String name)
	{

	}

	public List<String> getTraces()
	{
		return new ArrayList<String>(traceCount.keySet());
	}

	public Integer getTraceTestCount(String traceName)
	{
		if (traceCount.containsKey(traceName))
		{
			return traceCount.get(traceName).getTestCount();
		} else
		{
			return 0; // TODO
		}
	}

	public TraceInfo getTraceInfo(String traceName)
	{
		if (traceCount.containsKey(traceName))
		{
			return traceCount.get(traceName);
		}

		return null;
	}

	public Map<String, TraceStatusXml> getTraceStatus()
	{
		return traceStatus;
	}
}
