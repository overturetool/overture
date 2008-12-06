package org.overturetool.umltrans.Main;

import java.io.IOException;

import org.overturetool.tex.ClassExstractorFromTexFiles;

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
			if (args.length == 0)
			{
				PrintHelp();
				return;
			}
			if (args[0].endsWith("-removeTex"))
				RemoveTex(args[1].split(";"));
			else if (args[0].endsWith("-toVpp"))
				ToVpp(args[1].split(";"));
			else if (args[0].endsWith("-toUml"))
				ToUml(args[1].split(";"));
			else
				PrintHelp();
		} catch (Exception ex)
		{
			System.out.println(ex.getMessage() + "\n" + ex.getStackTrace() + "\n\n");
			PrintHelp();
		}

	}

	private static void PrintHelp()
	{
		System.out.print("VDM <-> UML Transformation\n");
		System.out.print("-removeTex      : For generating vpp files from tex files\n");
		System.out.print("-toVpp          : For generating creating VDM vpp file from a UML model\n");
		System.out.print("-toUml          : For generating a UML model from VDM vpp files\n");
		System.out.print("\n Example: org.overture.umltrans.jar -toUml file1.vpp;file2.vpp\n");

	}

	public static void RemoveTex(String[] args) throws IOException
	{
		ClassExstractorFromTexFiles.exstract(args);
	}

	public static void ToUml(String[] args)
	{
		// String fileName =
		// "C:\\COMU\\Source\\Vdm2Uml\\src\\Uml\\ImplementationUml.tex";
		// String fileName2 =
		// "C:\\COMU\\Source\\Vdm2Uml\\src\\Uml\\InterfacesUml.tex";
		String fileName = "C:\\COMU\\Source\\Vdm2Uml\\src\\TestClasses\\ClassWithMap2.vpp";
		String[] inputfiles = new String[]
		{ fileName };// , fileName2

		if (args.length > 0)
			inputfiles = args;

		try
		{
			String[] files = ClassExstractorFromTexFiles.exstract(inputfiles);
			String outputFile = files[0] + ".xml";
			Translator.TranslateVdmToUml(files, outputFile);
			System.out.println("Done: " + outputFile);
		} catch (Exception e)
		{

			e.printStackTrace();
		}
	}

	public static void ToVpp(String[] args)
	{
		String file = "";
		if (args.length > 0)
			file = args[0];

		try
		{

			String outputFile = file + ".vpp";
			Translator.TransLateUmlToVdm(file, outputFile);
			System.out.println("Done: " + outputFile);

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
