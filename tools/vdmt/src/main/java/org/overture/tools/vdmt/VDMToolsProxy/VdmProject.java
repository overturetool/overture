package org.overture.tools.vdmt.VDMToolsProxy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.overture.tools.vdmt.Util;

public class VdmProject {
	private static final String SRC_MAIN_JAVA = "/src/main/java";
	final String extension = ".vpp";
	protected File baseDir;
	protected Log log;
	protected File vppde;
	CodeGenCheckSum checkSum;

	protected List<File> dependedArtifactsSourceLocation = new ArrayList<File>();

	protected List<File> files = new ArrayList<File>();
	private final String LOG_SPLIT_LINE = "------------------------------------------------------------------------";

	public VdmProject(Log log, File vppde, File baseDir,
			List<File> dependedArtifactsSourceLocation) {
		this.baseDir = baseDir;
		this.dependedArtifactsSourceLocation = dependedArtifactsSourceLocation;
		this.log = log;
		this.vppde = vppde;
		checkSum = new CodeGenCheckSum(baseDir);
		exstractFiles();

	}

	/*
	 * Extract vpp files from project and depended projects and add to addFile
	 */
	private void exstractFiles() {
		// Get files in base dir
		for (File file : Util.GetFiles(getVppLocation(baseDir), extension)) {

			addFile(file);
		}

		// get files from dependencies
		for (File dFile : dependedArtifactsSourceLocation) {
			for (File file : Util.GetFiles(getVppLocation(dFile), extension)) {
				addFile(file);
			}
		}

	}

	/*
	 * Add vpp file to files for the current project
	 */
	private void addFile(File file) {
		if (file.exists()) {
			files.add(file);
			log.info("File added: " + file.getAbsolutePath());
		}

	}

	/*
	 * Get location of vpp files for the a project from the main folder
	 */
	private File getVppLocation(File mainSource) {
		return new File(mainSource.getAbsolutePath()
				+ "/src/main/vpp".replace('/', File.separatorChar));
	}

	/*
	 * Get location of java files from a projects main folder
	 */
	private File getJavaLocation(File mainSource) {
		return new File(mainSource.getAbsolutePath()
				+ SRC_MAIN_JAVA.replace('/', File.separatorChar));
	}

	/*
	 * Type check a project
	 */
	public void typeCheck() throws MojoFailureException, MojoExecutionException {
		// String out = new String();

		/* out = */executeCmdVdmTools(" -t" + getSpecFiles(),
				getJavaLocation(baseDir));

		printSuccess("VDM Type check");

	}

	/*
	 * Execute VDM Tools command line
	 */
	private String executeCmdVdmTools(String arguments, File baseDirectory)
			throws MojoFailureException, MojoExecutionException {
		String out = new String();
		Process p = null;
		try {
			String line;
			ProcessBuilder pb = null;
			String arg = "";
			log.debug("ExecuteCmdVdmTools");
			log.debug("VdmTools: " + vppde.getAbsolutePath() + " Exists: "
					+ vppde.exists());
			if (!vppde.exists())
				throw new MojoFailureException("VDM Tools Path not valid: "
						+ vppde.getAbsolutePath());
			log.debug("Base directory: " + baseDirectory.getAbsolutePath()
					+ " Exists:" + baseDirectory.exists());
			log.debug("Parameters: " + arguments);
			log.debug("OS = " + System.getProperty("os.name"));
			if (isWindows()) {

				pb = new ProcessBuilder("\"" + vppde.getAbsolutePath() + "\" "
						+ arguments.trim());
				// arg = "\"" + vppde.getAbsolutePath() + "\" " +
				// arguments.trim();
			} else if (isMac()) {
				// pb = new ProcessBuilder("open " + vppde.getAbsolutePath() +
				// " "+ arguments.trim());
				arg = vppde.getAbsolutePath() + " " + arguments.trim();
			} else {
				// pb = new ProcessBuilder(vppde.getAbsolutePath() + " "+
				// arguments.trim());
				arg = vppde.getAbsolutePath() + " " + arguments.trim();
			}
			if (pb != null) {
				pb.directory(baseDirectory);
				pb.redirectErrorStream(true);
				p = pb.start();
			} else {
				// arg.replace("\"", "");
				log.debug("Process args: " + arg);
				p = Runtime.getRuntime().exec(arg, null, baseDirectory);
			}
			// Process p = pb.start();

			BufferedReader input = new BufferedReader(new InputStreamReader(p
					.getInputStream()));
			while ((line = input.readLine()) != null) {
				out += "\n" + line;
				log.debug(line);
				// if (!line.endsWith("done")&&
				// !line.endsWith("with super classes are POS type correct"))
				if (line.startsWith("  Warning"))
					log.warn("\n" + line);
				// else
				// log.info("\n" + line);
				else if (line.startsWith("Couldn't open file"))
					throw new MojoFailureException(line);
				else if (line.contains("Errors detected")
						|| line.contains("  Expected")
						|| line.startsWith("  Error["))
					throw new MojoFailureException(
							"VDM Type check faild: Errors detected", line, out);
			}
			input.close();
		} catch (Exception err) {

			if (err instanceof MojoFailureException)
				throw (MojoFailureException) err;
			else {
				out += err.getMessage();

				log.error("\n" + err.getMessage());
				log.debug(getStackTrace(err));
				throw new MojoExecutionException("ExecuteCmdVdmTools", err
						.getMessage(), getStackTrace(err));
			}
		} finally {
			if (p != null)
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

	public static Boolean isMac() {
		String osName = System.getProperty("os.name");

		return osName.toUpperCase().indexOf("MAC".toUpperCase()) > -1;
	}

	public static Boolean isWindows() {
		String osName = System.getProperty("os.name");

		return osName.toUpperCase().indexOf("Windows".toUpperCase()) > -1;
	}

	public void codeGen(List<String> excludePackages,
			List<String> excludeClasses, List<String> importPackages)
			throws MojoFailureException, MojoExecutionException {
		long before = System.currentTimeMillis();
		
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
		for (File file : Util.GetFiles(getVppLocation(baseDir), extension)) {
			if (file.exists())
				for (String className : Util.GetClasses(file.getAbsolutePath())) {
					if (excludeClasses.contains(className))
						continue;

					classes.add(className);
					
					File javaFile = getJavaFile(file);
					
					if(!javaFile.exists() ){
						javaFile=getValidJavaFileName(javaFile,className);
					}
					
//					LexTokenReader ltr = new LexTokenReader(file,Dialect.VDM_PP);
//					ClassReader reader = new ClassReader(ltr);
//				ClassList clList=	reader.readClasses();
					
					classToJavaFile.put(className, javaFile);
					javaFiles.add(javaFile);
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
			if (!excludePackages.contains(packageName)) {
				List<String> filteredClasses = checkSum.filter(packageToClasses
						.get(packageName), files);
				if (filteredClasses.size() > 0)
					codeGen(filteredClasses, packageName);
			}

		}

		updateCheckSums(files);

		updateImports(excludePackages, packages, javaFiles);

		updateErrorUndefined(javaFiles);
		
		updateSuppressWarnings(javaFiles);
		
		
		long after = System.currentTimeMillis();

	

		printSuccess("VDM Code generation finished in "+(double) (after - before) / 1000 + " secs. ");
	}

	private File getValidJavaFileName(File javaFile,String className) {
		
		
		if(javaFile.getName().substring(0,javaFile.getName().length()-4).equals(className))
			return javaFile;
		else
			return new File(javaFile.getParentFile(),className+".java");
	}

	private File getJavaFile(File vppFile) {
		// set the corresponding Java file
		String tmp = Util.GetPackageAsPathPart(baseDir
				.getAbsolutePath(), vppFile.getAbsolutePath());
		String tmp2 = tmp.replace("/src/main/vpp".replace('/',
				File.separatorChar), SRC_MAIN_JAVA.replace('/',
				File.separatorChar));
		return new File( baseDir.getAbsolutePath()
				+ tmp2.replace(".vpp", ".java"));
	}

	private void updateCheckSums(List<File> vppFiles) {

		for (File file : vppFiles) {
			if (!file.exists())
				continue;
			checkSum.addCheckSum(file.getAbsolutePath());
		}

		checkSum.saveCheckSums();

	}

	private void updateErrorUndefined(List<File> javaFiles) {
		log
				.info("Updating VDM error / undefined - Util.Runtime error replace by CGException");
		for (File file : javaFiles) {

			if (file.exists())
				new VdmJavaFile(file).replaceUtilRuntimeErrorWithCGException();
		}
	}

	private void updateImports(List<String> excludePackages,
			List<String> packages, List<File> javaFiles) {
		log.info("Updating imports");

		for (String string : excludePackages) {
			if (packages.contains(string))
				packages.remove(string);
		}
		setImports(packages, javaFiles);
	}
	
	private void updateSuppressWarnings(List<File> javaFiles) {
		List<String> warnigs = new ArrayList<String>();
		warnigs.add("all");
		warnigs.add("unchecked");
		warnigs.add("unused");
		
		log
				.info("Updating Suppress warnings");
		for (File file : javaFiles) {

			if (file.exists())
				new VdmJavaFile(file).addSuppressWarnings(warnigs);
		}
	}

	private void setImports(List<String> packages, List<File> files) {
		log.info("Packages to auto import");
		for (String string : packages) {
			log.info(string);
		}
		for (File file : files) {

			if (file.exists())
				new VdmJavaFile(file).addPackages(packages);
		}
	}

	private void codeGen(List<String> vdmClasses, String packageName)
			throws MojoFailureException, MojoExecutionException {
		if (vdmClasses.isEmpty())
			log.info("Nothing to update");

		if (vdmClasses.size() > 20)
			codeGen(vdmClasses.subList(20, vdmClasses.size()), packageName);

		String classes = "";
		for (Object object : vdmClasses) {
			classes += object.toString().trim() + ",";
		}
		classes = classes.substring(0, classes.lastIndexOf(","));

		log.info(LOG_SPLIT_LINE);
		log.info("Generating classes (max 20) for package: " + packageName
				+ " (" + Math.ceil((double) vdmClasses.size() / (double) 20)
				+ ")");
		log.info("");

		for (String className : vdmClasses) {
			log.info("Class: " + className);
		}
		log.info("");

		String arg = " -j -L -z " + packageName + " " + "-K " + classes + " "
				+ getSpecFiles();

		/* String out = */executeCmdVdmTools(arg, getJavaLocation(baseDir));
		printSuccess("VDM Code generation ( " + packageName + ")");
	}

	/*
	 * Create command line format for specification files
	 */
	private String getSpecFiles() {
		String filePaths = new String();

		for (File file : files) {

			String f = "";
			if (isWindows())
				f = " \"" + file.getAbsolutePath() + "\"";
			else
				f = " " + file.getAbsolutePath();
			log.debug("File: " + f + " Exists: " + file.exists());
			filePaths += f;

		}

		// log.info("SpecFiles: " + filePaths);
		return filePaths;
	}

	/*
	 * Print successful message
	 */
	public void printSuccess(String message) {
		log.info(LOG_SPLIT_LINE);
		log.info(message + " SUCCESSFUL");
		log.info(LOG_SPLIT_LINE);
	}

	final String VDM_TOOLS_PROJECT_FILE_INFO_FILE_PATH_LENGTH_TOKEN = "#";
	final String VDM_TOOLS_PROJECT_FILE_COUNT = "COUNT";
	final String VDM_TOOLS_PROJECT_INIT = "bCOUNT1,k13,ProjectFilePPf3,f"
			+ VDM_TOOLS_PROJECT_FILE_COUNT + ",";
	final String VDM_TOOLS_PROJECT_FILE_INFO = "e2,m4,filem"
			+ VDM_TOOLS_PROJECT_FILE_INFO_FILE_PATH_LENGTH_TOKEN + ",";
	final String VDM_TOOLS_PROJECT_OPT = "FormatVersion:2\n" + "DTC:1\n"
			+ "PRE:1\n" + "POST:1\n" + "INV:1\n" + "CONTEXT:0\n"
			+ "MAXINSTR:1000\n" + "PRIORITY:0\n"
			+ "PRIMARYALGORITHM:instruction_number_slice\n" + "TASKSWITCH:0\n"
			+ "MAXTIME:1000\n" + "TIMEFACTOR:1\n" + "STEPSIZE:100\n"
			+ "JITTERMODE:Early\n" + "DEFAULTCPUCAPACITY:1000000\n"
			+ "DEFAULTVCPUCAPACITY:INFINITE\n" + "LOGARGS:\n"
			+ "PRINT_FORMAT:1\n" + "DEF:pos\n" + "errlevel:1\n" + "SEP:1\n"
			+ "VDMSLMOD:0\n" + "INDEX:0\n" + "PrettyPrint_RTI:0\n"
			+ "CG_RTI:0\n" + "CG_CHECKPREPOST:1\n" + "C_flag:0\n"
			+ "JCG_SKEL:0\n" + "JCG_GENPREPOST:0\n" + "JCG_TYPES:0\n"
			+ "JCG_SMALLTYPES:0\n" + "JCG_LONGS:1\n" + "JCG_PACKAGE:\n"
			+ "JCG_CONCUR:0\n" + "JCG_CHECKPREPOST:0\n" + "JCG_VDMPREFIX:1\n"
			+ "JCG_INTERFACES:\n" + "Seed_nondetstmt:-1\n"
			+ "j2v_stubsOnly:0\n" + "j2v_transforms:0";

	public void createVdmToolsProject(String projectName)
			throws MojoExecutionException {
		StringBuilder sb = new StringBuilder();
		sb.append(VDM_TOOLS_PROJECT_INIT.replaceAll(
				VDM_TOOLS_PROJECT_FILE_COUNT,
				new Integer(files.size()).toString()).replaceAll("COUNT1",
				new Integer(files.size() + 3).toString()));

		for (File file : files) {
			String filePath = file.getAbsolutePath();
			if (filePath.startsWith(baseDir.getAbsolutePath()))
				filePath = "."
						+ File.separatorChar
						+ ".."
						+ File.separatorChar
						+ ".."
						+ File.separatorChar
						+ ".."
						+ filePath.substring(new Long(baseDir.getAbsolutePath()
								.length()).intValue());
			sb.append(VDM_TOOLS_PROJECT_FILE_INFO.replaceAll(
					VDM_TOOLS_PROJECT_FILE_INFO_FILE_PATH_LENGTH_TOKEN,
					new Integer(filePath.length()).toString())
					+ filePath.replace('\\', '/'));
		}

		

		createProjectFile(getJavaLocation(baseDir).getAbsolutePath(),projectName, sb,".prj");

		createProjectOptionsFile(projectName);
		printSuccess("VDM Tools project created");
		log
				.info("Note: There is a file length limitation in VDM Tools so if squares are");
		log
				.info("shown inside a file path and VDM Tools log window says cannot open file");
		log
				.info("the path to the file is to long. To resolve it make the path shorter.");
		log
				.info("Or you can place the file inside the current project since the path then");
		log.info("will be relative");
	}

	private void createProjectOptionsFile(String projectName)
			throws MojoExecutionException {
		// Write opt file
		File optFile = new File(getJavaLocation(baseDir).getAbsolutePath()
				+ File.separatorChar + projectName + ".opt");

		FileWriter outputFileReader;
		try {
			if (optFile.exists())
				optFile.delete();
			outputFileReader = new FileWriter(optFile);

			BufferedWriter outputStream = new BufferedWriter(outputFileReader);
			outputStream.write(VDM_TOOLS_PROJECT_OPT.replaceAll("JCG_PACKAGE:",
					"JCG_PACKAGE:org.overturetool." + projectName));
			outputStream.close();
			log.info("Options file: " + optFile.getAbsolutePath());

		} catch (IOException e) {
			throw new MojoExecutionException(
					"Fail to create VDM Tools options file", e);
		}
	}

	private File createProjectFile( String outputDirPath,String projectName, StringBuilder sb,String extension)
			throws MojoExecutionException {
		// Write project file
		File projectFile = new File(outputDirPath
				+ File.separatorChar + projectName + extension);

		FileWriter outputFileReader;
		try {
			if (projectFile.exists())
				projectFile.delete();
			outputFileReader = new FileWriter(projectFile);

			BufferedWriter outputStream = new BufferedWriter(outputFileReader);
			outputStream.write(sb.toString());
			outputStream.close();
			log.info("Project file: " + projectFile.getAbsolutePath());
		} catch (IOException e) {
			throw new MojoExecutionException(
					"Fail to create VDM Tools project file", e);
		}
		return projectFile;
	}

	public void createSpecfileParameter(String projectName)
			throws MojoExecutionException {
		File projectFile = new File(baseDir.getAbsolutePath()
				+ File.separatorChar + projectName + ".log");

		FileWriter outputFileReader;
		try {
			if (projectFile.exists())
				projectFile.delete();
			outputFileReader = new FileWriter(projectFile);
			StringBuilder sb = new StringBuilder();

			for (File file : files) {
				sb.append(file.getAbsolutePath() + " ");
			}

			BufferedWriter outputStream = new BufferedWriter(outputFileReader);
			outputStream.write(sb.toString());
			outputStream.close();
			log.info("Project file: " + projectFile.getAbsolutePath());
		} catch (IOException e) {
			throw new MojoExecutionException(
					"Fail to create VDM Tools project file", e);
		}
	}

	final String overtureProjectFile = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<projectDescription>\n" + "<name>PROJECT_NAME</name>\n" +
			"	<comment></comment>\n" +
			"	<projects>\n" +
			"	</projects>\n" +
			"	<buildSpec>\n" +
			"		<buildCommand>\n" +
			"			<name>org.eclipse.dltk.core.scriptbuilder</name>\n" +
			"			<arguments>\n" +
			"			</arguments>\n" +
			"		</buildCommand>\n" +
			"	</buildSpec>\n" +
			"	<natures>\n" +
			"		<nature>org.overture.ide.vdmpp.core.nature</nature>\n" +
			"	</natures>\n" + "LINKED_SOURCE" + "</projectDescription>";
	
	final String overtureLinkedSourceFolder = "	<linkedResources>\n"+
"LINK"+
"	</linkedResources>\n";
final String overtureSourceLink =		"		<link>\n"+
"		<name>NAME</name>\n"+
"		<type>2</type>\n"+
"		<location>PATH</location>\n"+
"		</link>\n";
	public void createOvertureProject(String projectName,  String outputDirPath) throws MojoExecutionException {
		StringBuilder sb = new StringBuilder();
		List<String> linkedLocations = new Vector<String>();
log.info("Creating overture project file");
		for (File file : files) {
			File f = file.getParentFile();
			while(f!=null && f.isDirectory()&&f.getParent()!=null && !f.getName().equals("vpp"))
			{
				f=f.getParentFile();
//				log.info(f.getAbsolutePath());
			}
			
			if(f!=null && !linkedLocations.contains(f.getAbsolutePath()))
			{
				linkedLocations.add(f.getAbsolutePath());
				sb.append(overtureSourceLink.replace("NAME", f.getName()+linkedLocations.size()).replace("PATH", f.getAbsolutePath().replace('\\', '/')));
			}
		}

		StringBuilder data = new StringBuilder();
		data.append(overtureProjectFile.replace("PROJECT_NAME", projectName).replace("LINKED_SOURCE",overtureLinkedSourceFolder.replace("LINK", sb.toString())));
		
		createProjectFile(outputDirPath,".project", data,"");

		
		printSuccess("Overture project created");
		

	}
}
