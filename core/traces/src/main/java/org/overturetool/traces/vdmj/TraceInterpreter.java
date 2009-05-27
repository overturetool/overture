package org.overturetool.traces.vdmj;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.overturetool.traces.utility.TraceXmlWrapper;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.definitions.NamedTraceDefinition;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.runtime.ClassInterpreter;
import org.overturetool.vdmj.syntax.ClassReader;
import org.overturetool.vdmj.traces.CallSequence;
import org.overturetool.vdmj.traces.TestSequence;
import org.overturetool.vdmj.traces.Verdict;
import org.overturetool.vdmj.typechecker.ClassTypeChecker;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.FlatEnvironment;
import org.overturetool.vdmj.typechecker.PrivateClassEnvironment;
import org.overturetool.vdmj.typechecker.PublicClassEnvironment;
import org.overturetool.vdmj.typechecker.TypeChecker;

public class TraceInterpreter {

	ClassList classes;
	ClassInterpreter ci;

	public void test(List<File> specFiles, String className,TraceXmlWrapper storage) throws Exception {

		classes = new ClassList();
		int parsErrors = 0;
		for (File file : specFiles) {
			LexTokenReader ltr;

			ltr = new LexTokenReader(file, Dialect.VDM_PP);

			ClassReader mr = new ClassReader(ltr);
			parsErrors += mr.getErrorCount();
			classes.addAll(mr.readClasses());
		}

		if (parsErrors == 0) {
			TypeChecker tc = new ClassTypeChecker(classes);
			tc.typeCheck();
			if (TypeChecker.getErrorCount() == 0) {
				ci = new ClassInterpreter(classes);

			} else {
				TypeChecker.printErrors(new PrintWriter(System.out));
			}
		}

		org.overturetool.vdmj.Settings.prechecks = true;
		org.overturetool.vdmj.Settings.postchecks = true;
		org.overturetool.vdmj.Settings.dynamictypechecks = true;
		ci.init(null);

		ClassDefinition classdef = ci.findClass(className);

		//PublicClassEnvironment globals = new PublicClassEnvironment(classes);
		//Environment env = new PrivateClassEnvironment(me, globals);

		if(storage!=null)
			storage.StartClass(className);
		
		for (Object string : classdef.definitions) {
			if (string instanceof NamedTraceDefinition) {
				NamedTraceDefinition mtd = (NamedTraceDefinition) string;

				if(storage!=null)
					storage.StartTrace(mtd.name.name, mtd.location.startLine, mtd.location.startPos);
				
				TestSequence tests = mtd.getTests(ci.initialContext);
				List<List<Object>> results = new Vector<List<Object>>();
				List<String> testSatements = new Vector<String>();
//				for (CallSequence callSequence : ts) {
//					List<Object> dd = interpreter.runtrace(me, env,
//							callSequence);
//					results.add(dd);
//					int yy = 8;
//				}
				Environment env = new FlatEnvironment(
						classdef.getSelfDefinition(),
						new PrivateClassEnvironment(classdef, ci.getGlobalEnvironment()));

					int n = 1;

					for (CallSequence test: tests)
					{
						if (test.getFilter() > 0)
						{
			    			Console.out.println("Test " + n + " = " + test);
							Console.out.println(
								"Test " + n + " FILTERED by test " + test.getFilter());
						}
						else
						{
							ci.init(null);	// Initialize completely between every run...
			    			List<Object> result = ci.runtrace(classdef, env, test);

			    			if (result.get(result.size()-1) == Verdict.FAILED)
			    			{
			    				String stem = test.toString(result.size() - 1);
			    				ListIterator<CallSequence> it = tests.listIterator(n);

			    				while (it.hasNext())
			    				{
			    					CallSequence other = it.next();

			    					if (other.toString().startsWith(stem))
			    					{
			    						other.setFilter(n);
			    					}
			    				}
			    			}

			    			// Bodge until we figure out how to not have explicit op names.
			    			String clean = test.toString().replaceAll("\\.\\w+`", ".");

			    			if(storage!=null)
			    			{
			    				storage.StartTest(new Integer(n).toString(),clean);
			    				storage.StopElement();
			    				
			    				storage.AddResults(new Integer(n).toString(),result);
			    			}
			    			
			    			//Console.out.println("Test " + n + " = " + clean);
			    			testSatements.add(clean);
			    			//Console.out.println("Result = " + result);
			    			results.add(result);
						}

						n++;
					}

				int yy = 8;
				
				if(storage!=null)
					storage.StopElement();
			}

			
		}
		if(storage!=null)
			storage.StopElement();
	}
}
