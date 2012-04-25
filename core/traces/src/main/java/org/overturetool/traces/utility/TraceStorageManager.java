package org.overturetool.traces.utility;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.overturetool.traces.Filtering;
@SuppressWarnings("unchecked")
public class TraceStorageManager {
	private String rootPath;

	public TraceStorageManager(String path) {
		rootPath = path;
	}

	public void SaveResults(HashMap resSet) {
		String extention = "res";
		String extentionStatus = "verdict";
		
		Iterator re = resSet.entrySet().iterator();
		while (re.hasNext()) {
			Map.Entry res = (Map.Entry) re.next();

			String clName = res.getKey().toString();
			File classFolder = new File(rootPath + File.separatorChar + clName);

//			if (!classFolder.exists())
//				classFolder.mkdir();

			Iterator trI = ((HashMap) res.getValue()).entrySet().iterator();
			while (trI.hasNext()) {
				Map.Entry tr = (Map.Entry) trI.next();

				String trace = tr.getKey().toString();
				String traceFolderName=trace;
				if(traceFolderName.contains("/"))
				{
				String []tmp = traceFolderName.split("/");
				trace =tmp[1];
				traceFolderName = tmp[0] + File.separatorChar + tmp[1];
				}

				File traceFolder = new File(classFolder.getAbsolutePath()
						+ File.separatorChar + traceFolderName);

				if (!traceFolder.exists())
					traceFolder.mkdirs();

				Iterator numI = ((HashMap) tr.getValue()).entrySet().iterator();
				while (numI.hasNext()) {
					Map.Entry num = (Map.Entry) numI.next();
					String traceNum = num.getKey().toString();

					FileWriter outputFileReader;
					FileWriter outputFileReaderStatus;
					try {

						outputFileReader = new FileWriter(traceFolder.getAbsolutePath()
								+ File.separatorChar
								+ trace
								+ "-"
								+ traceNum
								+ "." + extention);
						
						outputFileReaderStatus = new FileWriter(traceFolder.getAbsolutePath()
								+ File.separatorChar
								+ trace
								+ "-"
								+ traceNum
								+ "." + extentionStatus);

						PrintWriter outputStream = new PrintWriter(outputFileReader);
						PrintWriter outputStreamStatus = new PrintWriter(outputFileReaderStatus);

						Vector results = (Vector) num.getValue();
						for (int i1 = 0; i1 < results.size(); i1++) {
							Filtering.TraceResult tRes = ((Filtering.TraceResult) results.get(i1));
							outputStream.println( tRes.output);
							
							outputStreamStatus.println(tRes.status.toString());
						}

						outputStream.close();
						outputStreamStatus.close();

					} catch (IOException e) {
						// TODO Auto-generated catch block
						 e.printStackTrace();
					}
				}
			}
		}
	}
		public void SaveStatements(HashMap resSet) {
			String extention = "arg";
			
			Iterator re = resSet.entrySet().iterator();
			while (re.hasNext()) {
				Map.Entry res = (Map.Entry) re.next();

				String clName = res.getKey().toString();
				File classFolder = new File(rootPath + File.separatorChar + clName);

//				if (!classFolder.exists())
//					classFolder.mkdir();

				Iterator trI = ((HashMap) res.getValue()).entrySet().iterator();
				while (trI.hasNext()) {
					Map.Entry tr = (Map.Entry) trI.next();

					String trace = tr.getKey().toString();
					String traceFolderName=trace;
					if(traceFolderName.contains("/"))
					{
					String []tmp = traceFolderName.split("/");
					trace =tmp[1];
					traceFolderName = tmp[0] + File.separatorChar + tmp[1];
					}

					File traceFolder = new File(classFolder.getAbsolutePath()
							+ File.separatorChar + traceFolderName);
					

					if (!traceFolder.exists())
						traceFolder.mkdirs();

					Iterator numI = ((HashMap) tr.getValue()).entrySet().iterator();
					while (numI.hasNext()) {
						Map.Entry num = (Map.Entry) numI.next();
						String traceNum = num.getKey().toString();

						FileWriter outputFileReader;
						try {

							outputFileReader = new FileWriter(traceFolder.getAbsolutePath()
									+ File.separatorChar
									+ trace
									+ "-"
									+ traceNum
									+ "." + extention);

							PrintWriter outputStream = new PrintWriter(outputFileReader);

							Vector results = (Vector) num.getValue();
							for (int i1 = 0; i1 < results.size(); i1++) {
								//Filtering.TraceResult tRes = ((Filtering.TraceResult) results.get(i1));
								outputStream.println(results.get(i1).toString());
							}

							outputStream.close();

						} catch (IOException e) {
							// TODO Auto-generated catch block
						//	 e.printStackTrace();
						}
					}
				}
			}
	}
}
