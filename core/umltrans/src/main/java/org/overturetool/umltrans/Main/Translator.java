package org.overturetool.umltrans.Main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.List;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.overturetool.api.xml.XmlDocument;
import org.overturetool.parser.imp.OvertureParser;
import org.overturetool.tex.ClassExstractorFromTexFiles;
import org.overturetool.umltrans.StatusLog;
import org.overturetool.umltrans.uml2vdm.Oml2Vpp;
import org.overturetool.umltrans.uml2vdm.Uml2Vdm;
import org.overturetool.umltrans.uml2vdm.Xml2UmlModel;
import org.overturetool.umltrans.vdm2uml.Uml2XmiEAxml;
import org.overturetool.umltrans.vdm2uml.Vdm2Uml;
import org.overturetool.umltrans.xml.XmlParser;

public class Translator
{
	public static void setOutput(PrintWriter outputWriter)
	{
		StatusLog.out = outputWriter;
	}
	public static String TranslateVdmToUml(String specData, String outputFile)
			throws CGException, ParseException, IOException
	{
		String xmiDocumentFileName = outputFile;// files[0].substring(0,
		
		OvertureParser op = new OvertureParser(specData);
		op.parseDocument();
		if(op.errors>0)
		{
			
			FileWriter outputFileReader = new FileWriter(outputFile);

			BufferedWriter outputStream = new BufferedWriter(outputFileReader);
			
			outputStream.write(specData);
			outputStream.close();
			throw new ParseException("Parse errors encountered during parse of vdm file: "+ outputFile, 0);//\n"+ specData
		}

			Vdm2Uml w = new Vdm2Uml();
		Uml2XmiEAxml xmi = new Uml2XmiEAxml();
		xmi.Save(
				xmiDocumentFileName,
				w.Init(op.astDocument.getSpecifications()),
				w.GetLog());
		
		return xmiDocumentFileName;
	}

	public static String TransLateTexVdmToUml(List<File> files,
			File outputFile) throws FileNotFoundException, CGException,
			IOException, ParseException
	{
			StringBuilder sb = new StringBuilder();
		for (File file : files)
		{
			sb.append("\n" + ClassExstractorFromTexFiles.exstractAsString(file.getAbsolutePath()));
		}

		return Translator.TranslateVdmToUml(sb.toString(), outputFile.getAbsolutePath());

	}

	public static String Test(String par)
	{
		return par;
	}

	public static List<File> TexConvert(List<File> files)
			throws IOException
	{
		File output = new File(files.get(0), "tmp");
		output.createNewFile();
		new ClassExstractorFromTexFiles();
		return ClassExstractorFromTexFiles.exstract(
				files,
				output);
	}

	public static void TransLateUmlToVdm(File file, File outputFile)
			throws Exception
	{

		XmlDocument doc = XmlParser.Parse(file.getAbsolutePath(), false);
		
		StatusLog log = new StatusLog();
		
		Xml2UmlModel xmlUmlModel = new Xml2UmlModel(log);
		xmlUmlModel.VisitXmlDocument(doc);

		Uml2Vdm u = new Uml2Vdm();
		Oml2Vpp vpp = new Oml2Vpp();


		if(!outputFile.isDirectory())
			throw new Exception("Output directory not valid: "+ outputFile.getAbsolutePath());
		
		String outputPath = outputFile.getAbsolutePath();
		if (!outputPath.endsWith(new Character(File.separatorChar).toString()))
			outputPath += File.separatorChar;
		
		vpp.Save(outputPath, u.Init(xmlUmlModel.result),log);
	}
}
