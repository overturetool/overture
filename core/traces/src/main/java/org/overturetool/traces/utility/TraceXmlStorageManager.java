package org.overturetool.traces.utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.overturetool.traces.Filtering;

public class TraceXmlStorageManager {
	XmlFileWriter xml;

	private String rootPath;
	final String CLASS_TAG = "Class";
	final String TRACE_TAG = "Trace";
	final String TEST_CASE_TAG = "TestCase";
	final String RESULT_TAG = "Result";
	final String STATEMENT_TAG = "Statement";
	final String NAME_TAG = "Name";
	final String NUMBER_TAG = "No";
	final String VERDICT_TAG = "Verdict";

	public TraceXmlStorageManager(String path) {
		rootPath = path;
	}

	@SuppressWarnings("unchecked")
	public void SaveStatements(HashMap statements, HashMap resultMap) {
		try {
			String extention = "arg";

			Iterator statementItr = statements.entrySet().iterator();
			while (statementItr.hasNext()) {
				Map.Entry res = (Map.Entry) statementItr.next();

				String clName = res.getKey().toString();
				File classFolder = new File(rootPath + File.separatorChar
						+ clName);
				
				xml = new XmlFileWriter();
				xml.StartDocument(rootPath + File.separatorChar
						+ clName
						+ ".xml", "Traces");

				xml.StartElement(CLASS_TAG, NAME_TAG, clName);
				
				
				

				Iterator trI = ((HashMap) res.getValue()).entrySet().iterator();
				while (trI.hasNext()) {
					Map.Entry tr = (Map.Entry) trI.next();

					String trace = tr.getKey().toString();
					
					
					
					
					
//					String traceFolderName=trace;
//					if(traceFolderName.contains("/"))
//					{
//					String []tmp = traceFolderName.split("/");
//					trace =tmp[1];
//					traceFolderName = tmp[0] + File.separatorChar + tmp[1];
//					}
//
//					File traceFolder = new File(classFolder.getAbsolutePath()
//							+ File.separatorChar + traceFolderName);
//					
//					
//					xml = new XmlFileWriter();
//					xml.StartDocument(classFolder.getAbsolutePath()
//							+ File.separatorChar + traceFolderName
//							+ ".xml", "Traces");
//
//					xml.StartElement(CLASS_TAG, NAME_TAG, clName);
//					
//					
					
					
					

					xml.StartElement(TRACE_TAG, NAME_TAG, trace);

					Iterator numI = ((HashMap) tr.getValue()).entrySet()
							.iterator();
					while (numI.hasNext()) {
						Map.Entry num = (Map.Entry) numI.next();
						String traceNum = num.getKey().toString();

						xml.StartElement(TEST_CASE_TAG, NUMBER_TAG, traceNum);

						Vector results = (Vector) num.getValue();
						for (Integer i1 = 0; i1 < results.size(); i1++) {
							xml.StartElement(STATEMENT_TAG, NUMBER_TAG, new Integer(i1+1)
									.toString());
							xml.WriteValue(results.get(i1).toString());
							xml.StopElement(STATEMENT_TAG);
						}

						HashMap classMap = (HashMap) resultMap.get(clName);
						if (classMap != null) {
							HashMap traceMap = (HashMap) ((HashMap) classMap)
									.get(trace);
							if (traceMap != null) {

								Iterator testCaseItr = traceMap.entrySet()
										.iterator();
								while (testCaseItr.hasNext()) {
									Map.Entry testCaseResult = (Map.Entry) testCaseItr
											.next();
									String testCaseResultNum = testCaseResult
											.getKey().toString();
									
									if(!testCaseResultNum.equals( traceNum))
continue;
									Vector testCaseResults = (Vector) testCaseResult
											.getValue();

									Boolean first = true;
									Boolean resultStarted= false;
									Integer index=0;
									for (Object object : testCaseResults) {

										Filtering.TraceResult tRes = (Filtering.TraceResult) object;
										if (tRes == null)
											continue;
										if(first)
										{
											Object arr[] = testCaseResults.toArray();
											xml.StartElement(RESULT_TAG,VERDICT_TAG,((Filtering.TraceResult) arr[arr.length-1]).status
													.toString());
											first=false;
											resultStarted=true;
										}
										index++;
										
										xml.StartElement(RESULT_TAG, NUMBER_TAG,
												index.toString());
										xml.WriteValue(tRes.output);
										xml.StopElement(RESULT_TAG);
										
									}
									if(resultStarted)
									xml.StopElement(RESULT_TAG);
									break;
								}
							}

						}

						xml.StopElement(TEST_CASE_TAG);

					}
					xml.StopElement(TRACE_TAG);
					
				}
				xml.StopElement(CLASS_TAG);
				xml.StopDocument();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
