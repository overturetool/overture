package org.overturetool.umltrans.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
	public static String TranslateVdmToUml(String specData, String outputFile)
			throws CGException
	{
		String xmiDocumentFileName = outputFile;// files[0].substring(0,
		
		OvertureParser op = new OvertureParser(specData);
		op.parseDocument();

			Vdm2Uml w = new Vdm2Uml();
		Uml2XmiEAxml xmi = new Uml2XmiEAxml();
		xmi.Save(
				xmiDocumentFileName,
				w.init(op.astDocument.getSpecifications()),
				w.getLog());
		
		return xmiDocumentFileName;
	}

	public static String TransLateTexVdmToUml(List<String> files,
			String outputFile) throws FileNotFoundException, CGException,
			IOException
	{
		// File output =new File(files[0]+ File.separatorChar + "tmp");
		// output.createNewFile();

		StringBuilder sb = new StringBuilder();
		for (String file : files)
		{
			sb.append("\n" + ClassExstractorFromTexFiles.exstractAsString(file));
		}

		// String[] files1 =
		// ClassExstractorFromTexFiles.exstract(files,output.getAbsolutePath());
		return Translator.TranslateVdmToUml(sb.toString(), outputFile);

	}

	public static String Test(String par)
	{
		return par;
	}

	public static List<String> TexConvert(List<String> files)
			throws IOException
	{
		File output = new File(files.get(0) + File.separatorChar + "tmp");
		output.createNewFile();
		new ClassExstractorFromTexFiles();
		return ClassExstractorFromTexFiles.exstract(
				files,
				output.getAbsolutePath());
	}

	public static void TransLateUmlToVdm(String file, String outputFile)
			throws Exception
	{

		XmlDocument doc = XmlParser.Parse(file, false);
		
		StatusLog log = new StatusLog();
		
		Xml2UmlModel xmlUmlModel = new Xml2UmlModel(log);
		xmlUmlModel.VisitXmlDocument(doc);

		Uml2Vdm u = new Uml2Vdm();
		Oml2Vpp vpp = new Oml2Vpp();

		if (!outputFile.endsWith(new Character(File.separatorChar).toString()))
			outputFile += File.separatorChar;
		vpp.Save(outputFile, u.init(xmlUmlModel.result),log);
	}
}
