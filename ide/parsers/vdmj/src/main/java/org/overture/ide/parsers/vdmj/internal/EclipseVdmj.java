package org.overture.ide.parsers.vdmj.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import org.overturetool.vdmj.ExitStatus;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.VDMJ;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.messages.InternalException;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;
import org.overturetool.vdmj.modules.ModuleList;
import org.overturetool.vdmj.runtime.Interpreter;
import org.overturetool.vdmj.syntax.ClassReader;
import org.overturetool.vdmj.syntax.ModuleReader;
import org.overturetool.vdmj.syntax.SyntaxReader;
import org.overturetool.vdmj.typechecker.TypeChecker;

public class EclipseVdmj extends VDMJ // implements IEclipseVdmj
{

	@SuppressWarnings("unchecked")
	public List modules = new Vector();
	private List<VDMError> parseErrors = new ArrayList<VDMError>();
	private List<VDMWarning> parseWarnings = new ArrayList<VDMWarning>();
	private Dialect dialect;

	public EclipseVdmj() {
	}

	public EclipseVdmj(Dialect dialect) {
		this.dialect = dialect;
	}

	@Override
	public ExitStatus parse(List<File> files) {
		parseErrors.clear();
		parseWarnings.clear();

		modules.clear();
		LexLocation.resetLocations();
		int perrs = 0;
		// int pwarn = 0;
		// // long duration = 0;
		//
		// for (File file : files)
		// {
		//
		// }

		// int n = classes.notLoaded();
		//
		// if (n > 0)
		// {
		// info("Parsed " + plural(n, "class", "es") + " in " +
		// (double)(duration)/1000 + " secs. ");
		// info(perrs == 0 ? "No syntax errors" :
		// "Found " + plural(perrs, "syntax error", "s"));
		// infoln(pwarn == 0 ? "" : " and " +
		// (warnings ? "" : "suppressed ") + plural(pwarn, "warning", "s"));
		// }

		return perrs == 0 ? ExitStatus.EXIT_OK : ExitStatus.EXIT_ERRORS;

	}

	/**
	 * @see org.overturetool.vdmj.VDMJ#parse(java.util.List)
	 */
	@SuppressWarnings("unchecked")
	public ExitStatus parse(String content, File file) {
		parseErrors.clear();
		parseWarnings.clear();
		modules.clear();
		LexLocation.resetLocations();
		int perrs = 0;
		SyntaxReader reader = null;

		try {
			LexTokenReader ltr = new LexTokenReader(content, dialect, file);

			switch (dialect) {
			case VDM_PP:
			case VDM_RT:
				reader = new ClassReader(ltr);
				modules.addAll(((ClassReader) reader).readClasses());
				break;
			case VDM_SL:
				reader = new ModuleReader(ltr);
				modules.addAll(((ModuleReader) reader).readModules());
				break;

			default:
				break;
			}

		} catch (InternalException e) {
			println(e.toString());
		} catch (Throwable e) {
			println(e.toString());

			if (e instanceof StackOverflowError) {
				e.printStackTrace();
			}

			perrs++;
		}

		if (reader != null && reader.getErrorCount() > 0) {
			perrs += reader.getErrorCount();
			// reader.printErrors(Console.out);
			for (VDMError error : reader.getErrors()) {
				parseErrors.add(error);
			}
		}

		if (reader != null && reader.getWarningCount() > 0) {
			// reader.printWarnings(Console.out);
			for (VDMWarning warning : reader.getWarnings()) {
				parseWarnings.add(warning);
			}
		}

		return perrs == 0 ? ExitStatus.EXIT_OK : ExitStatus.EXIT_ERRORS;
	}

	public List<VDMWarning> getParseWarnings() {
		return parseWarnings;
	}

	public List<VDMError> getParseErrors() {
		return parseErrors;
	}

	public List<VDMWarning> getTypeWarnings() {
		return TypeChecker.getWarnings();
	}

	public List<VDMError> getTypeErrors() {
		return TypeChecker.getErrors();
	}

	@SuppressWarnings("unchecked")
	public List getModules() {
		return modules;
	}

	@SuppressWarnings("unchecked")
	public ExitStatus parse(File file) {
		parseErrors.clear();
		parseWarnings.clear();

		modules.clear();
		LexLocation.resetLocations();

		int perrs = 0;
		ModuleReader reader = null;

		try {
			if (file.getName().endsWith(".lib")) {
				FileInputStream fis = new FileInputStream(file);
				GZIPInputStream gis = new GZIPInputStream(fis);
				ObjectInputStream ois = new ObjectInputStream(gis);

				ModuleList loaded = null;
				// long begin = System.currentTimeMillis();

				try {
					loaded = (ModuleList) ois.readObject();
				} catch (Exception e) {
					// println(file + " is not a valid VDM++ library");
					perrs++;

				} finally {
					ois.close();
				}

				// long end = System.currentTimeMillis();
				loaded.setLoaded();
				modules.addAll(loaded);
				// classes.remap();

				// infoln("Loaded " + plural(loaded.size(), "class", "es") +
				// " from " + file + " in " + (double)(end-begin)/1000 +
				// " secs");
			} else {
				LexTokenReader ltr = new LexTokenReader(file, Settings.dialect, filecharset);
				reader = new ModuleReader(ltr);
				modules.addAll(reader.readModules());
			}
		} catch (InternalException e) {
			println(e.toString());
		} catch (Throwable e) {
			println(e.toString());

			if (e instanceof StackOverflowError) {
				e.printStackTrace();
			}

			perrs++;
		}

		if (reader != null && reader.getErrorCount() > 0) {
			perrs += reader.getErrorCount();
			// reader.printErrors(Console.out);
			for (VDMError error : reader.getErrors()) {
				parseErrors.add(error);
			}
		}

		if (reader != null && reader.getWarningCount() > 0) {
			// reader.printWarnings(Console.out);
			for (VDMWarning warning : reader.getWarnings()) {
				parseWarnings.add(warning);
			}
		}

		return perrs == 0 ? ExitStatus.EXIT_OK : ExitStatus.EXIT_ERRORS;
	}

	@Override
	public Interpreter getInterpreter() throws Exception {
		return null;
	}

	@Override
	protected ExitStatus interpret(List<File> filenames) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExitStatus typeCheck() {
		// TODO Auto-generated method stub
		return null;
	}

}
