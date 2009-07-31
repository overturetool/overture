package org.overturetool.umltrans.Main;

import java.io.IOException;

public class MainClass
{
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args)
	{
		try
		{
			new CmdLineProcesser().processCommand(args);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
//		try
//		{
//			if (args.length == 0)
//			{
//				PrintHelp();
//				return;
//			}
//
//			String tmp = "";
//			for (int i = 1; i < args.length; i++)
//			{
//				tmp += args[i].trim();
//			}
//
//			if (args[0].endsWith("-r"))
//				RemoveTex(SplitInputFiles(tmp, ";"));
//			else if (args[0].endsWith("-v"))
//				ToVpp(SplitInputFiles(args[1], ";"));
//			else if (args[0].endsWith("-u"))
//				ToUml(SplitInputFiles(args[1], ";"));
//			else if (args[0].endsWith("-x"))
//				PrintXmlDoc(args[1]);
//			else
//				PrintHelp();
//		} catch (Exception ex)
//		{
//			System.out.println(ex.getMessage() + "\n\n");
//			ex.printStackTrace();
//			PrintHelp();
//		}

	}

//	private static String[] SplitInputFiles(String files, String splitter)
//	{
//		if (files.contains(splitter))
//			return files.split(splitter);
//		else
//			return new String[] { files };
//
//	}
//
//	private static void PrintXmlDoc(String string)
//	{
//		try
//		{
//			XmlParser.Parse(string, true);
//		} catch (Exception e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//
//	private static void PrintHelp()
//	{
//
//		System.out.print("UMLTRANS: You must specify direction\n");
//		System.out.print("\nUsage: org.overture.umltrans.jar (-r | -v | -u) file1 ; file2 ; ...\n\n");
//		System.out.print("-r create vpp files from VDM tex files\n");
//		System.out.print("-v create VDM vpp file from UML model\n");
//		System.out.print("-u create UML model from VDM vpp files\n\n");
//		System.out.print("-x Print XML fils as VDM XML Doc operation\n\n");
//		System.out.print("\n Example: org.overture.umltrans.jar -u file1.vpp;file2.vpp\n");
//
//	}
//
//	public static void RemoveTex(String[] args) throws IOException
//	{
//		File output = new File(new File(args[0]).getParent()
//				+ File.separatorChar + "tmp");
//		output.mkdir();
//		ClassExstractorFromTexFiles.exstract(args, output.getAbsolutePath());
//	}
//
//	public static void ToUml(String[] args)
//	{
//		// String fileName =
//		// "C:\\COMU\\Source\\Vdm2Uml\\src\\Uml\\ImplementationUml.tex";
//		// String fileName2 =
//		// "C:\\COMU\\Source\\Vdm2Uml\\src\\Uml\\InterfacesUml.tex";
//		String fileName = "C:\\COMU\\Source\\Vdm2Uml\\src\\TestClasses\\ClassWithMap2.vpp";
//		String[] inputfiles = new String[] { fileName };// , fileName2
//
//		if (args.length > 0)
//			inputfiles = args;
//
//		try
//		{
//			for (String string : inputfiles)
//			{
//				System.out.println("Input file: " + string);
//			}
//			// File output =new File(new File(inputfiles[0]).getParent()+
//			// File.separatorChar + "tmp");
//			// output.mkdir();
//			// String[] files =
//			// ClassExstractorFromTexFiles.exstract(inputfiles,output.getAbsolutePath());
//			String outputFile = inputfiles[0] + ".xml";
//			Translator.TransLateTexVdmToUml(inputfiles, outputFile);
//			System.out.println("Done: " + outputFile);
//		} catch (Exception e)
//		{
//
//			e.printStackTrace();
//		}
//	}
//
//	public static void ToVpp(String[] args)
//	{
//		String file = "";
//		if (args.length > 0)
//			file = args[0];
//
//		try
//		{
//
//			String outputFile = file + ".vpp";
//			Translator.TransLateUmlToVdm(file, outputFile);
//			System.out.println("Done: " + outputFile);
//
//		} catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//	}
//	
//	
//	private static ArrayList<File> GetFiles(File file)
//	{
//		ArrayList<File> files = new ArrayList<File>();
//		// if(file.getName().contains(".svn"))
//		// return files;
//		if (file.isDirectory())
//			for (File currentFile : file.listFiles())
//			{
//				if (currentFile.isDirectory())
//				{
//					for (File file2 : GetFiles(currentFile))
//					{
//						files.add(file2);
//					}
//
//					int ki = 0;
//				} else if (currentFile.isFile())
//					files.add(currentFile);
//			}
//		else
//			files.add(file);
//		return files;
//	}

}
