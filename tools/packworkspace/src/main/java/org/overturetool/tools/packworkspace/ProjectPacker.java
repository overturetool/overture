package org.overturetool.tools.packworkspace;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Vector;

import org.overturetool.vdmj.lex.Dialect;

public class ProjectPacker implements Comparable<ProjectPacker>
{
	static final String VDM_README_FILENAME="README.txt";
	File root;
	VdmReadme settings=null;
	Dialect dialect= Dialect.VDM_PP;
	File newLocation;
	public ProjectPacker(File root,Dialect dialect) {
		this.root = root;
		this.dialect = dialect;
		File readme = new File(root,VDM_README_FILENAME);
		
			settings = new VdmReadme(readme, root.getName()+getName(dialect).toUpperCase(),dialect,false);
			if(!readme.exists())
			{
				System.out.println("Creating initial README file for: "+ root.getAbsolutePath());
				settings.createReadme();
			}
//			File readme1 = new File(root,"README.txt.txt");
//			if(readme1.exists())
//				readme1.delete();
			//copyfile(readme.getAbsolutePath(), new File(readme.getAbsolutePath()+".txt").getAbsolutePath());
			
			settings.initialize();
	}
	
	
	


	public File packTo(File location)
	{
		if(settings==null)
		{
			System.out.println("Skipping project: "+ root.getAbsolutePath());
			return null;
		}
		
		System.out.println("Writing project: "+settings.getName());
		
		File outputLocation = new File(location,settings.getName());
		outputLocation.mkdirs();
		
		copyFiles(root,outputLocation,dialect);
		settings.writeProjectFile(outputLocation);
		settings.writeReadmeContentFile(outputLocation, VDM_README_FILENAME);
		newLocation= outputLocation;
		return outputLocation;
	}
	
	private static String createNewFileName(File newExample, File file,
			Dialect  dialect) {

		String name = file.getName();
		String extension = ".vdm" +getName(dialect).toLowerCase();

		name = name.substring(0, name.indexOf('.')) + extension;

		return new File(newExample, name).getAbsolutePath();
	}
	
	
	
	public File getNewLocation()
	{
		return newLocation;
	}
	public VdmReadme getSettings()
	{
		return settings;
	}
	public Dialect getDialect()
	{
		return dialect;
	}
	public List<File> getSpecFiles()
	{
		List<File> specFiles = new Vector<File>();
		for(File f : getNewLocation().listFiles())
		{
			if(f.getName().toLowerCase().endsWith(".vdmpp")||f.getName().toLowerCase().endsWith(".vdmrt")||f.getName().toLowerCase().endsWith(".vdmsl"))
				specFiles.add(f);
		}
		return specFiles;
	}
	
	
	public static String getName(Dialect dialect)
	{
		switch(dialect)
		{
		case VDM_PP:
			return "PP";
		case VDM_RT:
			return "RT";
		case VDM_SL:
		return "SL";
		default:
			return "PP";
		}
	}
	
	
	private static void copyFiles(File source, File destination,
			Dialect dialect) {
		
		
		for (File file : source.listFiles()) {
			if (file.getName().equals(".svn")
					|| file.getName().equals(".project")
					|| file.getName().equals(".classpath"))
				continue;
			if (file.isFile()) {
				String fileName = new File(destination, file.getName())
						.getAbsolutePath();
				
				if (file.getName().endsWith("vpp")||file.getName().endsWith("vdm"))
					fileName = createNewFileName(destination, file, dialect);
				copyfile(file.getAbsolutePath(), fileName);
			} else {
				File newFolder = new File(destination, file.getName());
				newFolder.mkdirs();
				copyFiles(file, newFolder, dialect);
			}
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





	public int compareTo(ProjectPacker o)
	{
		return this.getSettings().getName().toLowerCase().compareTo(o.getSettings().getName().toLowerCase());
	}
}
