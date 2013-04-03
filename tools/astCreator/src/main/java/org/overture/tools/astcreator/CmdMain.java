package org.overture.tools.astcreator;

import java.io.File;
import java.io.FileInputStream;

public class CmdMain {
	public final static boolean GENERATE_VDM = false;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			help();
		}

		if (args.length == 2) {
			String grammarFilePath = args[0];
			grammarFilePath = grammarFilePath.replace('/', File.separatorChar)
					.replace('\\', File.separatorChar);
			String outputPath = args[1];
			outputPath = outputPath.replace('/', File.separatorChar).replace(
					'\\', File.separatorChar);
			FileInputStream toStringFile = new FileInputStream(grammarFilePath
					+ Main.TO_STRING_FILE_NAME_EXT);
			Main.create(toStringFile, new FileInputStream(grammarFilePath),
					new File(outputPath), true, GENERATE_VDM);
		}

		if (args.length == 4) {
			System.out.println("Creating extension AST.");
			String ast1FileName = args[0];
			String ast2FileName = args[1];
			File ast1File = new File(ast1FileName);
			File ast2File = new File(ast2FileName);
			String extName = args[2];
			File output = new File(args[3]);
			String ast1ToStringFileName = ast1FileName
					+ Main.TO_STRING_FILE_NAME_EXT;
			String ast2ToStringFileName = ast2FileName
					+ Main.TO_STRING_FILE_NAME_EXT;
			File ast1ToStringFile = new File(ast1ToStringFileName);
			File ast2ToStringFile = new File(ast2ToStringFileName);
			FileInputStream ast1ToStringFileStream = null;
			FileInputStream ast2ToStringFileStream = null;

			// Check ast file 1
			if (!(ast1File.exists() && ast1File.canRead())) {
				System.out.println(ast1File
						+ " does not exists or is not reable.");
				return;
			}

			// Check ast file 2
			if (!(ast2File.exists() && ast2File.canRead())) {
				System.out.println(ast2File
						+ " does not exists or is not reable.");
				return;
			}

			// Check ast 1 toString file
			if ((ast1ToStringFile.canRead()))
				ast1ToStringFileStream = new FileInputStream(ast1ToStringFile);

			// Check ast 2 toString file
			if (ast2ToStringFile.canRead())
				ast2ToStringFileStream = new FileInputStream(ast2ToStringFile);

			// Fire in the hall
			Main.create(ast1ToStringFileStream, ast2ToStringFileStream,
					new FileInputStream(ast1File),
					new FileInputStream(ast2File), output, extName,
					GENERATE_VDM, false);

		}

	}

	private static void help() {
		StringBuffer buf = new StringBuffer();
		buf.append("Generates a AST from a grammar file.\n");
		buf.append("generator [grammar file] [output path]\n\n");
		buf.append("grammar file: The file path to the grammar file to be generated.\n");
		buf.append("output path: The output path to the folder where the ");
		buf.append("              generated sources should be placed\n");
		// buf.append("tostring grammar file: Optional argument for a toString grammar file.\n");

		System.out.println(buf.toString());

	}

}
