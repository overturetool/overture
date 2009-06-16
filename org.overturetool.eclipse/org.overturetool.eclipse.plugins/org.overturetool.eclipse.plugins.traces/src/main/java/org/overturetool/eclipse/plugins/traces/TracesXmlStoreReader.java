package org.overturetool.eclipse.plugins.traces;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.overturetool.traces.utility.TraceTestResult;
import org.overturetool.traces.utility.TraceTestStatus;
import org.overturetool.traces.utility.TraceXmlStorageManager;
import org.overturetool.traces.utility.TraceXmlWrapper;
import org.overturetool.traces.utility.XmlFileWriter;
import org.overturetool.traces.utility.ITracesHelper.TestResultType;
import org.overturetool.vdmj.definitions.NamedTraceDefinition;
import org.overturetool.vdmj.traces.Verdict;
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
		private TestResultType verdict;
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

		public void setVerdict(TestResultType verdict)
		{
			this.verdict = verdict;
		}

		public TestResultType getVerdict()
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

	StringBuilder data= new StringBuilder();
	File file;
	String className;
	String traceName;
	HashMap<String, Integer> traceCount = new HashMap<String, Integer>();

	HashMap<String, TraceStatusXml> traceStatus = new HashMap<String, TraceStatusXml>();

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

	public TracesXmlStoreReader(File file, String className)
			throws SAXException, IOException
	{
		super();
		this.file = file;
		this.className = className;
		Parse();
		initialParse = false;
	}

	private void Parse() throws SAXException, IOException
	{
		XMLReader xr = XMLReaderFactory.createXMLReader();
		TracesXmlStoreReader handler = this;
		xr.setContentHandler(handler);
		xr.setErrorHandler(handler);

		FileReader r = new FileReader(this.file);
		xr.parse(new InputSource(r));

	}

	public List<TraceTestResult> GetTraceTestResults(String traceName,
			Integer startNumber, Integer stopNumber) throws SAXException,
			IOException
	{
		traceTestResults = new Vector<TraceTestResult>();

		this.traceTaceTestStartNumber = startNumber;
		this.traceTaceTestStopNumber = stopNumber;
		this.traceName = traceName;
		this.traceTestParse = true;

		Parse();
		List<TraceTestResult> traceTestResults1 = traceTestResults;
		traceTestResults = null;
		return traceTestResults1;
	}

	// //////////////////////////////////////////////////////////////////
	// Event handlers.
	// //////////////////////////////////////////////////////////////////

	public void startDocument()
	{
		// System.out.println("Start document");
	}

	public void endDocument()
	{
		// System.out.println("End document");
	}

	public void setDocumentLocator(Locator locator)
	{
		this.locator = locator;
	}

	public void startElement(String uri, String name, String qName,
			Attributes atts)
	{
		// if ("".equals(uri))
//		 String kkk=("Start element: " + qName);
//		for (int i = 0; i < atts.getLength(); i++)
//		{
//			kkk+=(" "+ atts.getLocalName(i)+ "=" + atts.getValue(i));
//		}
//		System.out.println(kkk);
		// else
		// System.out.println("Start element: {" + uri + "}" + name);

		if (name.equals(TraceXmlWrapper.CLASS_TAG))
		{
			String cName = atts.getValue(TraceXmlWrapper.NAME_TAG);
			if (cName != null && cName.equals(className))
				inClass = true;
		} else if (initialParse && inClass
				&& name.equals(TraceXmlWrapper.TRACE_TAG))
		{
			String tName = atts.getValue(TraceXmlWrapper.NAME_TAG);

			inTrace = true;
			currentTraceName = tName;

			traceCount.put(
					atts.getValue(TraceXmlWrapper.NAME_TAG),
					Integer.parseInt(atts.getValue(TraceXmlWrapper.NUMBER_OF_TESTS_TAG)));

		} else if (inClass && name.equals(TraceXmlWrapper.TRACE_TAG))
		{
			String tName = atts.getValue(TraceXmlWrapper.NAME_TAG);
			if (tName != null && tName.equals(traceName))
				inTrace = true;
		} else if (inClass
				&& this.traceTestParse
				&& (name.equals(TraceXmlWrapper.TEST_CASE_TAG) || name.equals(TraceXmlWrapper.RESULT_TAG)))
		{
			String numberValue = atts.getValue(TraceXmlWrapper.NUMBER_TAG);
			if (numberValue != null)
			{
				Integer number = Integer.parseInt(numberValue);
				if (number != null && traceTaceTestStartNumber <= number
						&& traceTaceTestStopNumber >= number)
				{

//					boolean found = false;
//					for (int i = 0; i < traceTestResults.size(); i++)
//					{
//						TraceTestResult res = traceTestResults.get(i);
//						if (res.getNumber().equals(number))
//						{
//							if (name.equals(TraceXmlWrapper.TEST_CASE_TAG))
//								this.insertArgument = true;
//							else if (name.equals(TraceXmlWrapper.RESULT_TAG))
//							{
//								String verdict = atts.getValue(TraceXmlWrapper.VERDICT_TAG);
//
//								res.setStatus(GetVerdict(verdict));
//
//								this.insertResult = true;
//							}
//
//							this.currentResult = res;
//							currentResultIndex = i;
//							found = true;
//							break;
//						}
//					}
//					if (!found)
//					{
						TraceTestResult res = GetResult(number); // new TraceTestResult();
//						res.setNumber(number);
						String verdict = atts.getValue(TraceXmlWrapper.VERDICT_TAG);
						if (name.equals(TraceXmlWrapper.TEST_CASE_TAG))
							this.insertArgument = true;
						else if (name.equals(TraceXmlWrapper.RESULT_TAG))
						{
							res.setStatus(GetVerdict(verdict));
							this.insertResult = true;
						}
//						traceTestResults.add(res);
//						currentResultIndex = traceTestResults.size() - 1;

//					}
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
			tmp.setVerdict(GetVerdict(atts.getValue(TraceXmlWrapper.VERDICT_TAG)));
			traceStatus.put(currentTraceName, tmp);
		}

		// if(locator!=null)
		// {
		// int col = locator.getColumnNumber();
		// int line = locator.getLineNumber();
		// String publicId = locator.getPublicId();
		// String systemId = locator.getSystemId();
		// }
	}
	
	private TraceTestResult GetResult(Integer number)
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

	private static TestResultType GetVerdict(String verdict)
	{
		if (verdict != null)
		{
			if (verdict.equals(Verdict.FAILED.toString()))
				return (TestResultType.Fail);
			else if (verdict.equals(Verdict.INCONCLUSIVE.toString()))
				return (TestResultType.Inconclusive);
			else if (verdict.equals(Verdict.PASSED.toString()))
				return (TestResultType.Ok);
			else if (verdict.equals("SKIPPED"))
				return (TestResultType.Skipped);
		}
		return TestResultType.Unknown;
	}

	public void endElement(String uri, String name, String qName)
	{
		
		
		if (inClass && inTrace && traceTestResults!=null 
				&& traceTestResults.size() > 0 && data.toString().length()>0) //&& currentResult != null
		{
			if (insertArgument)
			{
				List<String> arguments = new Vector<String>();
				for (String string : XmlFileWriter.DeNormalizeValue(data.toString()).trim().split(";"))
				{
					arguments.add(string.trim());
				}
				traceTestResults.get(currentResultIndex).setArguments(arguments);

			} else if (insertResult)
			{
				List<String> results = new Vector<String>();
				for (String string : XmlFileWriter.DeNormalizeValue(data.toString()).trim().split(";"))
				{
					results.add(string.trim());
				}
				traceTestResults.get(currentResultIndex).setResults(results);

			}

		}
		
		
		data = new StringBuilder();
		
		
		
		
		
		// if ("".equals(uri))
		// System.out.println("End element: " + qName);
		// else
		// System.out.println("End element:   {" + uri + "}" + name);
		if (name.equals(TraceXmlWrapper.CLASS_TAG))
			inClass = false;
		else if (inClass && name.equals(TraceXmlWrapper.TRACE_TAG))
			inTrace = false;
		else if (inClass && inTrace
				&& name.equals(TraceXmlWrapper.TEST_CASE_TAG))
			insertArgument = false;
		else if (inClass && inTrace && name.equals(TraceXmlWrapper.RESULT_TAG))
			insertResult = false;
	}

	public void characters(char ch[], int start, int length)
	{
		// System.out.print("Characters:    \"");

		StringBuilder sb = new StringBuilder();

		for (int i = start; i < start + length; i++)
		{
			switch (ch[i])
			{
			// case '\\':
			// System.out.print("\\\\");
			// break;
			// case '"':
			// System.out.print("\\\"");
			// break;
			case '\n':
				// System.out.print("\\n");
				break;
			case '\r':
				// System.out.print("\\r");
				break;
			case '\t':
				// System.out.print("\\t");
				break;
			default:
				sb.append(ch[i]);
				break;
			}
		}
		
//		if(sb.toString().trim().length()>0)
//			System.out.println(sb.toString().trim());
		
		
		data.append(sb.toString());
		// System.out.print("\"\n");
	}

	public void error(SAXParseException e)
	{

	}

	public void warning(SAXParseException e)
	{

	}

	public void skippedEntity(String name)
	{

	}

	public List<String> GetTraces()
	{
		return new ArrayList<String>(traceCount.keySet());
	}

	public Integer GetTraceTestCount(String traceName)
	{
		if (traceCount.containsKey(traceName))
			return traceCount.get(traceName);
		else
			return 0; // TODO
	}

	public HashMap<String, TraceStatusXml> GetTraceStatus()
	{
		return traceStatus;
	}
}
