package org.overturetool.traces.utility;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.overturetool.ast.itf.IOmlClass;
import org.overturetool.parser.imp.OvertureParser;
import org.overturetool.traces.Expand;
import org.overturetool.traces.Filtering;
import org.overturetool.traces.RTERR;
import org.overturetool.traces.ToolBox;
import org.overturetool.traces.VDMJToolBox;
import org.overturetool.traces.VDMToolsToolBox;
import org.overturetool.traces.external_VDMToolsToolBox;

public class CmdTrace {
	public enum ToolBoxType {
		VDMTools, VDMJ
	};

	public static void RunCmd(String outputPath, ArrayList<String> c, int max, ArrayList<String> files, ToolBoxType toolBoxType, String VDMToolsPath)
			throws Exception {
		long start = System.currentTimeMillis(); // start timing
		RTERR errorLog = new RTERR();
		System.out.println("Parsing files...");
		StringBuilder sb = new StringBuilder();
		for (String file : files) {
			System.out.println(new File(file).getName());
			sb.append("\n" + ClassExstractorFromTexFiles.exstractAsString(file));
		}

		OvertureParser parser = new OvertureParser(sb.toString());

		parser.parseDocument();

		HashSet classes = new HashSet();
		if (c.size() != 0)
			classes.addAll(c);
		else
			// add all classes as default
			for (int i = 0; i < parser.astDocument.getSpecifications().getClassList().size(); i++) {
				classes.add(((IOmlClass) parser.astDocument.getSpecifications().getClassList().get(i)).getIdentifier());
			}

		Expand exp = new Expand(new Long(max), errorLog);

		System.out.println();
		System.out.println("Expanding traces...");
		HashMap expandedTraces = exp.ExpandSpecTraces(parser.astDocument.getSpecifications(), classes);

		ToolBox tb = null;

		HashSet hFiles = new HashSet();
		hFiles.addAll(files);
		if (toolBoxType == ToolBoxType.VDMTools) {
			external_VDMToolsToolBox.SetVDMToolPath(VDMToolsPath);
			tb = new VDMToolsToolBox(hFiles);
		} else if (toolBoxType == ToolBoxType.VDMJ)
			tb = new VDMJToolBox(hFiles);

		org.overturetool.traces.Filtering filter = new Filtering(expandedTraces, tb, errorLog);

		if (errorLog.GetErrors().size() > 0) {
			PrintErrors(errorLog);
			System.out.println("Trace expansion ended.");
			return;
		}

		for (Object clnm : filter.GetTraceClassNames()) {
			for (Object tr : filter.GetTraces(clnm.toString())) {
				System.out.println(tr);
			}

		}
		System.out.println();
		System.out.println("Filtering and running trace test cases...");
		filter.filterAll();

		System.out.println("--------------------Statistics-------------------");
		System.out.println("Number of tests:                       "
				+ SetDigits(filter.GetTestCount(), 10));
		System.out.println();
		System.out.println("Number of successful tests:            "
				+ SetDigits(filter.GetSuccessCount(), 10));
		System.out.println();
		System.out.println("Number of skipped tests:               "
				+ SetDigits(filter.GetSkippedCount(), 10));
		System.out.println("Number of failed tests:                "
				+ SetDigits(filter.GetFaildCount(), 10));
		System.out.println("Number of expand failed tests:         "
				+ SetDigits(filter.GetExpandFaildCount(), 10));
		System.out.println("Number of inconclusive tests:          "
				+ SetDigits(filter.GetInconclusiveCount(), 10));
		// System.out.println();
		// System.out.println("Trace test case calls:                 "
		// + SetDigits(filter.iii, 10));
		System.out.println("-------------------------------------------------");

		long stop = System.currentTimeMillis(); // stop timing
		System.out.println("Elapsed seconds: " + (stop - start) / 1000); // print
		// execution
		// time
		System.out.println();
		start = System.currentTimeMillis();
		TraceStorageManager tsm = new TraceStorageManager(outputPath);
		System.out.println("Saving results...");
		tsm.SaveResults(filter.GetResults());
		System.out.println("Saving arguments...");
		tsm.SaveStatements(filter.GetStatements());

		stop = System.currentTimeMillis(); // stop timing
		System.out.println("Saving elapsed in seconds: " + (stop - start)
				/ 1000); // print execution time

		System.out.println();
		System.out.println("Output written to: " + outputPath);

		System.out.println("Done.");
	}

	private static String SetDigits(Long val, int digits) {
		String value = val.toString();
		while (value.length() < digits)
			value = " " + value;
		return value;

	}

	public static ArrayList<String> GetErrors(RTERR errorLog)
			throws CGException {
		ArrayList<String> errStrings = new ArrayList<String>();
		HashMap errs = errorLog.GetErrors();

		Iterator re = errs.entrySet().iterator();
		while (re.hasNext()) {
			Map.Entry res = (Map.Entry) re.next();

			HashMap ers = (HashMap) res.getValue();

			Iterator tr = ers.entrySet().iterator();
			while (tr.hasNext()) {
				Map.Entry tRes = (Map.Entry) tr.next();

				HashSet es = (HashSet) tRes.getValue();

				for (Object object : es) {
					String msg = res.getKey().toString();
					msg += " " + tRes.getKey().toString();
					RTERR.ErrMsg e = (RTERR.ErrMsg) object;
					msg += " (l " + e.line + ", c " + e.col + ") " + e.mes;
					errStrings.add(msg);
				}
			}

		}
		return errStrings;
	}

	public static void PrintErrors(RTERR errorLog) {
		try {
			System.out.println("Errors detected:");
			for (String err : GetErrors(errorLog)) {
				System.out.println(err);
			}
		} catch (CGException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
