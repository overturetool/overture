package org.overture.tools.vdmt.VDMToolsProxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.overture.tools.vdmt.Util;

public class VdmProject {
	final String extension = ".vpp";
	private File baseDir;
	private Log log;
	private File vppde;

	private ArrayList<File> dependedArtifactsSourceLocation = new ArrayList<File>();

	private ArrayList<File> files = new ArrayList<File>();

	public VdmProject(Log log, File vppde, File baseDir,
			ArrayList<File> dependedArtifactsSourceLocation) {
		this.baseDir = baseDir;
		this.dependedArtifactsSourceLocation = dependedArtifactsSourceLocation;
		this.log = log;
		this.vppde = vppde;

		exstractFiles();

	}

	private void exstractFiles() {
		// Get files in base dir
		for (File file : Util.GetFiles(GetVppLocation(baseDir), extension)) {

			AddFile(file);
		}

		// get files from dependencies
		for (File dFile : dependedArtifactsSourceLocation) {
			for (File file : Util.GetFiles(GetVppLocation(dFile), extension)) {
				AddFile(file);
			}
		}

	}

	private void AddFile(File file) {
		if (file.exists()) {
			files.add(file);
			log.info("File added: " + file.getAbsolutePath());
		}

	}

	private File GetVppLocation(File mainSource) {
		return new File(mainSource.getAbsolutePath()
				+ "/src/main/vpp".replace('/', File.separatorChar));
	}

	private File GetJavaLocation(File mainSource) {
		return new File(mainSource.getAbsolutePath()
				+ "/src/main/java".replace('/', File.separatorChar));
	}

	public void TypeCheck() throws MojoFailureException {
		String out = new String();

		out = ExecuteCmdVdmTools(" -t" + GetSpecFiles(),
				GetJavaLocation(baseDir));

		
		
			PrintSuccess("VDM Type check");

	}

	private String ExecuteCmdVdmTools(String arguments, File baseDirectory)
			throws MojoFailureException {
		String out = new String();
		Process p =null;
		try {
			String line;
			// ProcessBuilder pb = null;
			String arg;
			log.debug("ExecuteCmdVdmTools");
			log.debug("VdmTools: " + vppde.getAbsolutePath() + " Exists: "
					+ vppde.exists());
			log.debug("Base directory: " + baseDirectory.getAbsolutePath()
					+ " Exists:" + baseDirectory.exists());
			log.debug("Parameters: " + arguments);
			log.debug("OS = " + System.getProperty("os.name"));
			if (IsWindows()) {

				// pb = new ProcessBuilder("\"" + vppde.getAbsolutePath() + "\""
				// + arguments.trim());
				arg = "\"" + vppde.getAbsolutePath() + "\"" + arguments.trim();
			} else if (IsMac()) {
				// pb = new ProcessBuilder("open " + vppde.getAbsolutePath() +
				// " "+ arguments.trim());
				arg = "open " + vppde.getAbsolutePath() + " "
						+ arguments.trim();
			} else {
				// pb = new ProcessBuilder(vppde.getAbsolutePath() + " "+
				// arguments.trim());
				arg = vppde.getAbsolutePath() + " " + arguments.trim();
			}

			// pb.directory(baseDirectory);

			// pb.redirectErrorStream(true);
			arg.replace("\"", "");
			log.debug("Process args: " + arg);
			 p = Runtime.getRuntime().exec(arg, null, baseDirectory);

			// Process p = pb.start();

			BufferedReader input = new BufferedReader(new InputStreamReader(p
					.getInputStream()));
			while ((line = input.readLine()) != null) {
				out += "\n" + line;
				log.debug(line);
				if (!line.endsWith("done")
						&& !line
								.endsWith("with super classes are POS type correct"))
					if (line.startsWith("  Warning"))
						log.warn("\n" + line);
					// else
					// log.info("\n" + line);
					else if (line.startsWith("Couldn't open file"))
						throw new MojoFailureException(line);
					else if(line.contains("Errors detected") || line.contains("  Expected"))
					throw new MojoFailureException("VDM Type check faild: Errors detected",line,out);
			}
			input.close();
		} catch (Exception err) {
			
			if (err instanceof MojoFailureException)
				throw (MojoFailureException) err;
			else {
				out += err.getMessage();

				log.error("\n" + err.getMessage());
				log.debug(getStackTrace(err));
				throw new MojoFailureException("ExecuteCmdVdmTools", err
						.getMessage(), getStackTrace(err));
			}
		}finally
		{
			if(p!=null)
				p.destroy();
		}
		return out;
	}

	public static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		t.printStackTrace(pw);
		pw.flush();
		sw.flush();
		return sw.toString();
	}

	public static Boolean IsMac() {
		String osName = System.getProperty("os.name");

		return osName.toUpperCase().indexOf("MAC".toUpperCase()) > -1;
	}

	public static Boolean IsWindows() {
		String osName = System.getProperty("os.name");

		return osName.toUpperCase().indexOf("Windows".toUpperCase()) > -1;
	}

	public void CodeGen(List<String> excludePackages,
			List<String> excludeClasses, List<String> importPackages)
			throws MojoFailureException {

		if (excludePackages == null)
			excludePackages = new ArrayList<String>();

		if (excludeClasses == null)
			excludeClasses = new ArrayList<String>();

		if (importPackages == null)
			importPackages = new ArrayList<String>();

		Dictionary<String, File> classToJavaFile = new Hashtable<String, File>();
		ArrayList<String> classes = new ArrayList<String>();
		Dictionary<String, ArrayList<String>> packageToClasses = new Hashtable<String, ArrayList<String>>();

		ArrayList<File> javaFiles = new ArrayList<File>();
		List<String> packages = importPackages;
		for (File file : Util.GetFiles(GetVppLocation(baseDir), extension)) {
			if (file.exists())
				for (String className : Util.GetClasses(file.getAbsolutePath())) {
					if (excludeClasses.contains(className))
						continue;

					classes.add(className);
					// set the coresponding Java file
					String tmp = Util.GetPackageAsPathPart(baseDir
							.getAbsolutePath(), file.getAbsolutePath());
					String tmp2 = tmp.replace("/src/main/vpp".replace('/',
							File.separatorChar), "/src/main/java".replace('/',
							File.separatorChar));
					String javaFile = baseDir.getAbsolutePath()
							+ tmp2.replace(".vpp", ".java");
					classToJavaFile.put(className, new File(javaFile));
					javaFiles.add(new File(javaFile));
					String packageName = Util.GetPackage(baseDir, file);
					packageName = packageName.replace("src.main.vpp.", "")
							.trim();
					if (packageName.equals("src.main.vpp"))
						packageName = "";

					ArrayList<String> classList = packageToClasses
							.get(packageName);
					if (classList == null) {
						classList = new ArrayList<String>();
						classList.add(className);
						packageToClasses.put(packageName, classList);
						packages.add(packageName);
					} else
						classList.add(className);
					log.info("Class: " + className + " Package: " + packageName
							+ " Java: " + javaFile);
				}
		}

		Enumeration<String> itr = packageToClasses.keys();

		while (itr.hasMoreElements()) {
			String packageName = (String) itr.nextElement();
			if (!excludePackages.contains(packageName))
				CodeGen(packageToClasses.get(packageName), packageName);
		}

		log.info("Updating imports");

		for (String string : excludePackages) {
			if (packages.contains(string))
				packages.remove(string);
		}
		SetImports(packages, javaFiles);

	}

	private void SetImports(List<String> packages, ArrayList<File> files) {
		for (File file : files) {
			if (file.exists())
				new VdmJavaFile(file).AddPackages(packages);
		}
	}

	private void CodeGen(ArrayList<String> vdmClasses, String packageName)
			throws MojoFailureException {
		if (vdmClasses.isEmpty())
			log.info("Nothing to update");

		String classes = "";
		for (Object object : vdmClasses) {
			classes += object.toString().trim() + ",";
		}
		classes = classes.substring(0, classes.lastIndexOf(","));

		String out = new String();

		String line;
		String arg = " -j -L -z " + packageName + " " + "-K " + classes + " "
				+ GetSpecFiles();

		out = ExecuteCmdVdmTools(arg, GetJavaLocation(baseDir));
PrintSuccess("VDM Code generation");
	}

	private String GetSpecFiles() {
		String filePaths = new String();

		for (File file : files) {

			String f = "";
			// if (file.getAbsolutePath().startsWith(baseDir.getAbsolutePath()))
			// f = " \".."+File.separatorChar+".."+File.separatorChar +
			// "/src/main/vpp".replace('/', File.separatorChar)
			// + file.getAbsolutePath().substring(
			// GetJavaLocation(baseDir).getAbsolutePath()
			// .length()) + "\"";
			// else
			if (IsWindows())
				f = " \"" + file.getAbsolutePath() + "\"";
			else
				f = " " + file.getAbsolutePath();
			log.debug("File: " + f + " Exists: " + file.exists());
			filePaths += f;

		}

		// log.info("SpecFiles: " + filePaths);
		return filePaths;
	}

	public void PrintSuccess(String message) {
		log
				.info("------------------------------------------------------------------------");
		log.info(message + " SUCCESSFUL");
		log
				.info("------------------------------------------------------------------------");
	}
}
