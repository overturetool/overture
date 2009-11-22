package org.overture.ide.plugins.idedebughelper.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil {
	public static void copyFiles(File sourceFolder, File destinationFolder) {
		for (File file : sourceFolder.listFiles()) {
			if (file.getName().equals(".svn")
					|| file.getName().equals(".project")
					|| file.getName().equals(".classpath"))
				continue;
			if (file.isFile()) {
				String fileName = new File(destinationFolder, file.getName())
						.getAbsolutePath();

				
				copyfile(file.getAbsolutePath(), fileName);
			} else {
				File newFolder = new File(destinationFolder, file.getName());
				newFolder.mkdirs();
				copyFiles(file, newFolder);
			}
		}

	}
	
	
	public static void copyfile(String srFile, String dtFile) {
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
			in.close();
			out.close();
			//System.out.println("File copied: "+ f1.getName());
		} catch (FileNotFoundException ex) {
			System.out
					.println(ex.getMessage() + " in the specified directory.");
			System.exit(0);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void renameExtensionFile(File file, String newExtension)
	{
		if (!file.isFile() || file.getName().startsWith("."))
			return;
		
		
		File dest = new File(file.getAbsolutePath().substring(
				0, file.getAbsolutePath().lastIndexOf('.'))
				+ "." + newExtension);
		file.renameTo(dest);
		file.delete();
	}
	
	public static boolean hasExtension(File file, String extension)
	{
	if(file.isFile()){
		String path = file.getName();
		if(path!=null)
		{
			String ext = path.substring(path.lastIndexOf('.')+1);
			return ext.equals(extension);
		}
	}
	return false;
	}
}
