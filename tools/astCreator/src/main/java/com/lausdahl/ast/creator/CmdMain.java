package com.lausdahl.ast.creator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class CmdMain
{
	public final static boolean GENERATE_VDM = false;

	/**
	 * @param args
	 * @throws AstCreatorException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, AstCreatorException
	{
		if (args.length == 0)
		{
			help();
		}
		
		if(args.length==2)
		{
			String grammarFilePath = args[0];
			grammarFilePath= grammarFilePath.replace('/', File.separatorChar).replace('\\', File.separatorChar);
			String outputPath = args[1];
			outputPath=outputPath.replace('/', File.separatorChar).replace('\\', File.separatorChar);
			Main.create(new FileInputStream(grammarFilePath), new File(outputPath), true,GENERATE_VDM);
		}
	
		
	}

	private static void help()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("Generates a AST from a grammar file.\n");
		buf.append("generator [grammer file] [output path]\n\n");
		buf.append("grammer file: The file path to the grammar file to be generated.\n");
		buf.append("output path: The output path to the folder where the ");
		buf.append("              generated sources should be placed\n");
		//buf.append("tostring grammar file: Optional argument for a toString grammar file.\n");

		System.out.println(buf.toString());

	}

}
