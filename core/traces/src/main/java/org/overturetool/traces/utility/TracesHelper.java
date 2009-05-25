package org.overturetool.traces.utility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.Tuple;

import org.overturetool.ast.imp.OmlSpecifications;
import org.overturetool.ast.itf.IOmlClass;
import org.overturetool.ast.itf.IOmlNamedTrace;
import org.overturetool.ast.itf.IOmlSpecifications;
import org.overturetool.parser.imp.OvertureParser;
import org.overturetool.traces.*;
import org.overturetool.traces.API.Util;

import org.overturetool.traces.toolbox.*;
@SuppressWarnings("unchecked")

public class TracesHelper {
	Filtering filter = null;
	Expand exp = null;
	IOmlSpecifications spec = null;
	File[] specFiles;
	HashMap<String, File> classToFileMap = new HashMap<String, File>();
	RTERR errorLog = new RTERR();

	public TracesHelper(String vdmPath, File[] files, Boolean useVDMJ, int max)
			throws IOException, CGException {
		// String tmp = ClassExstractorFromTexFiles.exstractAsString(file);
		// OvertureParser parser = new OvertureParser(tmp);
		// parser.parseDocument();
		specFiles = files;
		spec = GetSpecification(files, classToFileMap);

		HashSet classes = new HashSet();
		for (int i = 0; i < spec.getClassList().size(); i++) {
			classes.add(((IOmlClass) spec.getClassList().get(i)).getIdentifier());
		}

		exp = new Expand(new Long(max), errorLog);

		HashMap expandedTraces = exp.ExpandSpecTraces(spec, classes);

		HashSet files1 = new HashSet();
		for (File f : files) {
			files1.add(f.getAbsoluteFile());
		}

		ToolBox tb = null;
		if (useVDMJ)
			tb = new VDMJToolBox(files1);
		else {
			tb = new VDMToolsToolBox(files1);
			external_VDMToolsToolBox.SetVDMToolPath(vdmPath);
		}
		filter = new Filtering(expandedTraces, tb, errorLog);
	}

	public String[] GetTraceClasNames() throws Exception {
		Object[] tmp = filter.GetTraceClassNames().toArray();
		String[] tmp2 = new String[tmp.length];
		for (int i = 0; i < tmp.length; i++) {
			tmp2[i] = tmp[i].toString();
		}
		return tmp2;
	}

	public String[] GetTraces(String className) throws Exception {
		Object[] tmp = filter.GetTraces(className).toArray();
		ArrayList<String> res = new ArrayList<String>();
		for (int i = 0; i < tmp.length; i++) {
			res.add(tmp[i].toString());
		}
		Collections.sort(res);

		return ToStringArray(res.toArray());
	}

	private String[] ToStringArray(Object[] arr) {

		String[] tmp2 = new String[arr.length];
		for (int i = 0; i < arr.length; i++) {
			tmp2[i] = arr[i].toString();
		}
		return tmp2;
	}

	public String[] GetTraceTestCases(String className, String trace) throws Exception {
		Object[] tmp = filter.GetTraceTestCases(className, trace).toArray();
		ArrayList<String> res = new ArrayList<String>();
		for (int i = 0; i < tmp.length; i++) {
			res.add(tmp[i].toString());
		}
		Collections.sort(res);

		return ToStringArray(res.toArray());
	}

	public void SetFail(String className, String trace, String num) throws CGException {
		long number = Integer.parseInt(num);

		filter.SetFail(className, trace, number, (long) 1);
	}

	public void SetOk(String className, String trace, String num) throws CGException {
		long number = Integer.parseInt(num);
		TestResult res = GetResult(className, trace, num);
		// Object[] args = (Object[]) res[1];
		for (int i = 0; i < res.args.length; i++) {
			filter.SetOk(className, trace, number, (long) i + 1);
		}

	}

	public String GetTraceDefinitionString(String className, String trace) throws Exception {
		return filter.GetTraceDefinitionString(spec, className, trace);
	}

	public IOmlNamedTrace GetTraceDefinition(String className, String trace) throws Exception {
		return filter.GetTraceDefinition(spec, className, trace);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<TestStatus> RunAll() throws CGException {

		HashMap resSet = filter.filterAll();
		ArrayList<TestStatus> ret = new ArrayList<TestStatus>();

		Iterator re = resSet.entrySet().iterator();
		while (re.hasNext()) {
			Map.Entry res = (Map.Entry) re.next();

			String clName = res.getKey().toString();

			Iterator trI = ((HashMap) res.getValue()).entrySet().iterator();
			while (trI.hasNext()) {
				Map.Entry tr = (Map.Entry) trI.next();

				String trace = tr.getKey().toString();

				Iterator numI = ((HashMap) tr.getValue()).entrySet().iterator();
				while (numI.hasNext()) {
					Map.Entry num = (Map.Entry) numI.next();
					String traceNum = num.getKey().toString();
					Vector results = (Vector) num.getValue();
					ArrayList<String> stat = new ArrayList<String>();
					for (int i1 = 0; i1 < results.size(); i1++) {
						String tmp = ((Filtering.TraceResult) results.get(i1)).status.toString();
						stat.add(tmp);
					}
					TestResultType status = GetStatus(stat.toArray());
					ret.add(new TestStatus(clName, trace, traceNum, status));
				}
			}
		}
		return ret;
	}

	public ArrayList<TestStatus> RunSingle(String className, String traceName, String number) throws CGException {

		long n = Long.parseLong(number);
		HashMap resSet = filter.ExecuteTraceTestCase(className, traceName, n);
		ArrayList<TestStatus> ret = new ArrayList<TestStatus>();

		Iterator re = resSet.entrySet().iterator();
		while (re.hasNext()) {
			Map.Entry res = (Map.Entry) re.next();

			String clName = res.getKey().toString();

			Iterator trI = ((HashMap) res.getValue()).entrySet().iterator();
			while (trI.hasNext()) {
				Map.Entry tr = (Map.Entry) trI.next();

				String trace = tr.getKey().toString();

				Iterator numI = ((HashMap) tr.getValue()).entrySet().iterator();
				while (numI.hasNext()) {
					Map.Entry num = (Map.Entry) numI.next();
					String traceNum = num.getKey().toString();
					Vector results = (Vector) num.getValue();
					ArrayList<String> stat = new ArrayList<String>();
					for (int i1 = 0; i1 < results.size(); i1++) {
						String tmp = ((Filtering.TraceResult) results.get(i1)).status.toString();
						stat.add(tmp);
					}
					TestResultType status = GetStatus(stat.toArray());
					ret.add(new TestStatus(clName, trace, traceNum, status));
				}
			}
		}
		return ret;
	}

	private TestResultType GetStatus(Object[] values) {
		TestResultType status = TestResultType.Ok;
		for (int i1 = 0; i1 < values.length; i1++) {
			String tmp = values[i1].toString();

			if (tmp.equals("<FAIL>"))
				status = TestResultType.Fail;
			else if (tmp.equals("<SKIPPED>"))
				status = TestResultType.Skipped;
			else if (tmp.equals("<EXPAND_FAIL>"))
				status = TestResultType.ExpansionFaild;
			else if (status == TestResultType.Ok
					&& tmp.equals("<INCONCLUSIVE>"))
				status = TestResultType.Inconclusive;
			else if ((status != TestResultType.Fail && status != TestResultType.Inconclusive)
					&& tmp.equals("<OK>"))
				status = TestResultType.Ok;

		}
		return status;

	}

	public class TestStatus {
		public TestStatus(String className, String traceName, String traceTestCaseNmber, TestResultType status) {
			this.ClassName = className;
			this.TraceName = traceName;
			this.TraceTestCaseNumber = traceTestCaseNmber;
			this.Status = status;
		}

		public String ClassName;
		public String TraceName;
		public String TraceTestCaseNumber;
		public TestResultType Status;
	}

	public class TestResult extends TestStatus {
		public TestResult(String className, String traceName, String traceTestCaseNmber, TestResultType status, String[] args, String[] res) {
			super(className, traceName, traceTestCaseNmber, status);
			// TODO Auto-generated constructor stub
			this.args = args;
			this.result = res;
		}

		public String[] args;
		public String[] result;
	}

	public enum TestResultType {
		Ok, Fail, Inconclusive, ExpansionFaild, Unknown, Skipped
	}

	// public static final int OK = 0;
	// public static final int FAIL = 1;
	// public static final int INCONCLUSIVE = 2;
	// public static final int EXPAND_FAIL=3;

	public TestResult GetResult(String className, String trace, String num) throws CGException {
		Long n = Long.parseLong(num);
		Tuple res = filter.GetResult(className, trace, n);

		Vector args = (Vector) res.GetField(1);
		Vector results = new Vector();// ) ToolBox.ResultsToStrings((Vector)
		// res.GetField(2));

		ArrayList<String> stat = new ArrayList<String>();

		for (int i1 = 0; i1 < ((Vector) res.GetField(2)).size(); i1++) {
			Filtering.TraceResult tr = ((Filtering.TraceResult) ((Vector) res.GetField(2)).get(i1));
			String tmp = tr.status.toString();
			stat.add(tmp);

			results.add(tr.output);
		}
		TestResultType status = GetStatus(stat.toArray());

		String[] argsString = new String[args.size()];
		args.copyInto(argsString);
		String[] resString = new String[results.size()];
		results.copyInto(resString);

		return new TestResult(className, trace, num, status, argsString, resString);
	}

	public TestResultType GetStatus(String className, String trace, String num) throws CGException {
		Long n = Long.parseLong(num);
		TestResultType status = TestResultType.Unknown;
		try {

			Tuple res = filter.GetResult(className, trace, n);

			ArrayList<String> stat = new ArrayList<String>();

			for (int i1 = 0; i1 < ((Vector) res.GetField(2)).size(); i1++) {
				Filtering.TraceResult tr = ((Filtering.TraceResult) ((Vector) res.GetField(2)).get(i1));
				String tmp = tr.status.toString();
				stat.add(tmp);
			}
			status = GetStatus(stat.toArray());
		} catch (Exception e) {
			// TODO: handle exception
		}
		return status;
	}

	public ArrayList<String> GetErrors() throws CGException {
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

	public boolean HasError(String className, String trace) throws CGException {
		return errorLog.HasError(className, trace);
	}

	public ArrayList<TraceError> GetError(String className, String trace) throws CGException {
		ArrayList<TraceError> errors = new ArrayList<TraceError>();
		HashSet errs = errorLog.GetErrMsg(className, trace);
		Iterator re = errs.iterator();
		while (re.hasNext()) {
			RTERR.ErrMsg res = (RTERR.ErrMsg) re.next();
			errors.add(new TraceError(this.classToFileMap.get(className), className, trace, res.mes, res.line.intValue(), res.col.intValue()));
		}
		return errors;
	}

	public static class TraceError {
		public String ClassName;
		public String Trace;
		public String Message;
		public int Line;
		public int Column;
		public File File;

		public TraceError(File file, String className, String traceName, String message, int line, int col) {
			this.File = file;
			this.ClassName = className;
			this.Trace = traceName;
			this.Message = message;
			this.Line = line;
			this.Column = col;
		}

		public String toString() {
			return ClassName + " " + Trace + " (l " + Line + ", c " + Column
					+ ") " + Message;
		}
	}

	public void PrintErrors() {
		try {
			System.out.println("Errors detected:");
			for (String err : GetErrors()) {
				System.out.println(err);
			}
		} catch (CGException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static ArrayList<String> GetClassNamesWithTraces(File[] files) throws CGException, IOException {
		ArrayList<String> classes = new ArrayList<String>();
		HashMap<String, File> fileClassMap = new HashMap<String, File>();
		for (Object clnm : Util.GetTraceDefinitionClasses(GetSpecification(files, fileClassMap))) {
			classes.add(clnm.toString());
		}
		return classes;
	}

	public ArrayList<String> GetClassNamesWithTraces() throws CGException, IOException {
		return TracesHelper.GetClassNamesWithTraces(specFiles);
	}

	public static IOmlSpecifications GetSpecification(File[] files, HashMap<String, File> fileClassMap) throws IOException, CGException {
		ArrayList<IOmlSpecifications> specs = new ArrayList<IOmlSpecifications>();
		// HashMap<String,String> fileClassMap = new HashMap<String, String>();
		for (File file : files) {
			System.out.println("Removing tex from vpp file:"+file.getName());
			String data = ClassExstractorFromTexFiles.exstractAsString(file.getAbsolutePath());

			OvertureParser parser = new OvertureParser(data);
			parser.parseDocument();
			System.out.println("Parse done file:"+file.getName());
			if(parser.errors>0)
			{
				System.out.println("Parse error in trace: File="+ file.getName());
			}
			specs.add(parser.astDocument.getSpecifications());
			for (Object c : parser.astDocument.getSpecifications().getClassList()) {
				IOmlClass cl = (IOmlClass) c;
				fileClassMap.put(cl.getIdentifier(), file);
			}
		}
if(specs.size()>0)
{
		OmlSpecifications spec = (OmlSpecifications) specs.get(0);
		for (int i = 1; i < specs.size(); i++) {
			spec.getClassList().addAll(specs.get(i).getClassList());
		}

		return (IOmlSpecifications) spec;
	}else return null;
}

	public void Save(String path) throws CGException {
		TraceStorageManager tsm = new TraceStorageManager(path);

		tsm.SaveResults(filter.GetResults());

		tsm.SaveStatements(filter.GetStatements());

	}

	public File GetFile(String className) {
		return classToFileMap.get(className);
	}
	
	public int GetSkippedCount(String className, String traceName) throws CGException
	{
		return filter.GetSkippedCount(className, traceName).intValue();
	}

}
