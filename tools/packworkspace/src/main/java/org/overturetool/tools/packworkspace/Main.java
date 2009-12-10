package org.overturetool.tools.packworkspace;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.overturetool.tools.packworkspace.OvertureProject.Natures;

public class Main {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		if (args.length == 0 || !new File(args[0]).exists()) {
			System.out
					.println("Please supply a vaild path to the examples root.");
			System.out
					.println("	e.g. C:\\overture\\overturesvn\\documentation\\examples");
			return;
		}
		File inputRootFolder = new File(args[0]);

		File tmpFolder = new File("examples");
		if (tmpFolder.exists()) {
			System.out
					.println("The folder \""
							+ tmpFolder.getName()
							+ "\" already exists, it is used as the temp folder for packing and added to be deleted. Delete ok. ( y / n):");
			if (System.in.read() == (int) 'y')
				delete(tmpFolder);
			else
				return;
		}
		

		for (File inputFolder : inputRootFolder.listFiles()) {
			
			packExamples(tmpFolder, inputFolder);
			
			
		}

		// new Zip().Zip(tmpFolder, new File("examples.zip"));
		if(tmpFolder.exists())
			delete(tmpFolder);
	}

	private static void packExamples(File tmpFolder, File inputFolder) {
		Natures nature = findNature(inputFolder);

		if (nature != null)
		{
			//if(tmpFolder.exists())
			while(tmpFolder.exists())
				delete(tmpFolder);
			tmpFolder.mkdir();
			String name =  "examples"+nature+".zip";
			System.out.println("PACKING: "+name);
			for (File exampleFolder : inputFolder.listFiles()) {
				if (exampleFolder.getName().equals(".svn"))
					continue;

				
				
				packExample(tmpFolder, exampleFolder, nature);
				
				
			}
			FolderZiper.zipFolder(tmpFolder.getName(),name);
			while(tmpFolder.exists())
				delete(tmpFolder);
		}
	}

	private static Natures findNature(File inputFolder) {
		String name = inputFolder.getName().toLowerCase();
		if (name.endsWith(Natures.Pp.toString().toLowerCase())
				|| name.contains("++"))
			return Natures.Pp;
		if (name.endsWith(Natures.Rt.toString().toLowerCase())
				|| name.contains("VICE".toLowerCase()))
			return Natures.Rt;
		else if (name.endsWith(Natures.Sl.toString().toLowerCase()))
			return Natures.Sl;
		else
			return null;
	}

	private static void delete(File tmpFolder) {
		if (tmpFolder != null && tmpFolder.exists() )
		{
			if (tmpFolder.isFile())
				tmpFolder.delete();
			else 
			{
				for (File file : tmpFolder.listFiles()) {
					delete(file);
				}
				tmpFolder.delete();
			}
		}
	}

	private static void packExample(File tmpFolder, File exampleFolder,
			Natures nature) {
		if (exampleFolder.exists() && exampleFolder != null
				&& exampleFolder.list() != null
				&& exampleFolder.list().length > 0) {
			String projectName = exampleFolder.getName() + nature;
			File newExample = new File(tmpFolder, projectName);
			newExample.mkdir();

			System.out.println("DIALIGHT: " + nature + " CREATING: "
					+ projectName);
			createProjectFile(newExample, nature, projectName);
			copyFiles(exampleFolder, newExample, nature);

		}

	}

	private static void copyFiles(File exampleFolder, File newExample,
			Natures nature) {
		for (File file : exampleFolder.listFiles()) {
			if (file.getName().equals(".svn")
					|| file.getName().equals(".project")
					|| file.getName().equals(".classpath"))
				continue;
			if (file.isFile()) {
				String fileName = new File(newExample, file.getName())
						.getAbsolutePath();

				if (file.getName().endsWith("vpp"))
					fileName = createNewFileName(newExample, file, nature);
				copyfile(file.getAbsolutePath(), fileName);
			} else {
				File newFolder = new File(newExample, file.getName());
				newFolder.mkdirs();
				copyFiles(file, newFolder, nature);
			}
		}

	}

	private static String createNewFileName(File newExample, File file,
			Natures nature) {

		String name = file.getName();
		String extension = ".vdm" + nature.toString().toLowerCase();

		name = name.substring(0, name.indexOf('.')) + extension;

		return new File(newExample, name).getAbsolutePath();
	}

	private static void createProjectFile(File newExample, Natures nature,
			String projectName) {
		FileWriter outputFileReader;

		try {
			outputFileReader = new FileWriter(new File(newExample, ".project"));
			BufferedWriter outputStream = new BufferedWriter(outputFileReader);

			String projectNature = "";
			switch (nature) {
			case Pp:
				projectNature = OvertureProject.VDMPP_NATURE;
				break;
			case Sl:
				projectNature = OvertureProject.VDMSL_NATURE;
				break;
			case Rt:
				projectNature = OvertureProject.VDMRT_NATURE;
				break;

			}
			outputStream.write(OvertureProject.EclipseProject.replace(
					OvertureProject.NATURE_SPACEHOLDER, projectNature).replace(
					OvertureProject.NAME_PLACEHOLDER, projectName));
			outputStream.flush();
			outputStream.close();
			outputFileReader.close();

		} catch (IOException e) {
			e.printStackTrace();

		}

	}

	private static void copyfile(String srFile, String dtFile) {
		try {
			File f1 = new File(srFile);
			File f2 = new File(dtFile);
			InputStream in = new FileInputStream(f1);

			// For Append the file.
			// OutputStream out = new FileOutputStream(f2,true);

			// For Overwrite the file.
			OutputStream out = new FileOutputStream(f2);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.flush();
			in.close();
			out.close();
			
			// System.out.println("File copied: "+ f1.getName());
		} catch (FileNotFoundException ex) {
			System.out
					.println(ex.getMessage() + " in the specified directory.");
			System.exit(0);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

}
