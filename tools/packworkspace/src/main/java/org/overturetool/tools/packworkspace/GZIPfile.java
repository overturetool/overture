package org.overturetool.tools.packworkspace;

//program
//package com.yc.ycportal.ge.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class GZIPfile
{
	private boolean flag = true;

	// define a interface，used to invoke this method
	public static GZIPfile getInterface()
	{
		return new GZIPfile();
	}

	// creat a method to unzip the folder
	public boolean openFile(String InfileName, String OutfileName)
	{
		/**
		 * @InfileName name for the method and path for the folder
		 * @OutfileName the path to save the folder when unzip is finished
		 * @return boolean，check if the operation is successful or not
		 */
		try
		{
			GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(InfileName));
			FileOutputStream out = new FileOutputStream(OutfileName);
			byte[] bt = new byte[1024];
			int length = 0;
			while ((length = gzip.read(bt)) > 0)
			{
				out.write(bt, 0, length);
			}
		} catch (Exception e)
		{
			this.flag = false;
			System.out.println(e.getMessage());
		}
		return flag;
	}

	// creat a method to process the readed folder
	public boolean compFile(String InfileName, String OutfileName)
	{
		/**
		 * @InfileName name for the method and path for the folder
		 * @OutfileName the path to save the folder when unzip is finished
		 * @return boolean，check if the operation is successful or not
		 */
		try
		{
			GZIPOutputStream gzip = new GZIPOutputStream(new FileOutputStream(OutfileName));
			FileInputStream in = new FileInputStream(InfileName);
			byte[] bt = new byte[1024];
			int length = 0;
			while ((length = in.read(bt)) > 0)
			{
				gzip.write(bt, 0, length);
			}
		} catch (Exception e)
		{
			flag = false;
			System.out.println(e.getMessage());
		}
		return flag;
	}

	public static void main(String args[])
	{

	}

	public void zip(File dir2Zip,File zipFile)
	{
		try
		{
			// create a ZipOutputStream to zip the data to
			ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));//".\\curDir.zip"
			// assuming that there is a directory named inFolder (If there
			// isn't create one) in the same directory as the one the code runs
			// from,
			// call the zipDir method
			
			zip(dir2Zip.listFiles(),zos);
			//zipDir(dir2Zip,dir2Zip, zos);//".\\inFolder"
			// close the stream
			zos.close();
		} catch (Exception e)
		{
			// handle exception
		}
	}

	// here is the code for the method
	public void zipDir(File rootZipDir,File zipDir, ZipOutputStream zos)
	{
		try
		{
			// create a new File object based on the directory we have to zip
			// File
			// zipDir = new File(dir2zip);
			// get a listing of the directory content
			String[] dirList = zipDir.list();
			byte[] readBuffer = new byte[2156];
			int bytesIn = 0;
			// loop through dirList, and zip the files
			for (int i = 0; i < dirList.length; i++)
			{
				File f = new File(zipDir, dirList[i]);
				if (f.isDirectory())
				{
					// if the File object is a directory, call this
					// function again to add its content recursively
					String filePath = f.getPath();
					zipDir(rootZipDir,new File(filePath), zos);
					// loop again
					continue;
				}
				
				// if we reached here, the File object f was not a directory
				// create a FileInputStream on top of f
				FileInputStream fis = new FileInputStream(f.getAbsoluteFile());
				// create a new zip entry
				ZipEntry anEntry = new ZipEntry(f.getPath().replace(rootZipDir.getPath()+File.separatorChar, ""));
				System.out.println("Zentry: " +anEntry.getName());
				// place the zip entry in the ZipOutputStream object
				zos.putNextEntry(anEntry);
				// now write the content of the file to the ZipOutputStream
				while ((bytesIn = fis.read(readBuffer)) != -1)
				{
					zos.write(readBuffer, 0, bytesIn);
				}
				// close the Stream
				fis.close();
			}
		} catch (Exception e)
		{
			// handle exception
		}
	}
	
	public void zip(File[] files, ZipOutputStream zos ) throws IOException
	{
		byte[] readBuffer = new byte[2156];
		int bytesIn = 0;
		
		for (File file : files)
		{
			if (file.isDirectory())
			{
				 
				zip(file.listFiles(), zos);
				// loop again
				continue;
			}else
			{
				// if we reached here, the File object f was not a directory
				// create a FileInputStream on top of f
				FileInputStream fis = new FileInputStream(file.getAbsoluteFile());
				// create a new zip entry
				ZipEntry anEntry = new ZipEntry(file.getPath().substring(file.getPath().indexOf(File.separatorChar)));
				System.out.println("Zentry: " +anEntry.getName());
				// place the zip entry in the ZipOutputStream object
				zos.putNextEntry(anEntry);
				// now write the content of the file to the ZipOutputStream
				while ((bytesIn = fis.read(readBuffer)) != -1)
				{
					zos.write(readBuffer, 0, bytesIn);
				}
				// close the Stream
				fis.close();
			}
		}
		
	}
}
