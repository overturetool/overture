package org.overturetool.umltrans.Main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.overturetool.ast.imp.OmlSpecifications;
import org.overturetool.ast.itf.IOmlSpecifications;
import org.overturetool.parser.imp.OvertureParser;
import org.overturetool.tex.ClassExstractorFromTexFiles;
import org.overturetool.umltrans.Oml2Vpp;
import org.overturetool.umltrans.Uml2Vdm;
import org.overturetool.umltrans.Uml2XmiEAxml;
import org.overturetool.umltrans.Vdm2Uml;
import org.overturetool.umltrans.Xml2UmlModel;
import org.overturetool.umltrans.XmlDocument;
import org.overturetool.umltrans.xml.XmlParser;

public class Translator
{
	public static String TranslateVdmToUml(String specData, String outputFile) throws CGException
	{
		String xmiDocumentFileName = outputFile;// files[0].substring(0,
		// files[0].length() - 4) +
		// ".xml";
		
		
		OvertureParser op = new OvertureParser(specData);
		op.parseDocument();

//		IOmlSpecifications[] specs = new IOmlSpecifications[files.length];
//
//		for (int i = 0; i < files.length; i++)
//		{
//
//			FileInputStream fis = new FileInputStream(files[i]);
//
//			OvertureParser op = new OvertureParser(fis);
//			op.parseDocument();
//			specs[i] = op.astDocument.getSpecifications();
//			fis.close();
//		}
//
//		Vector tmp = new Vector();
//		for (int i = 0; i < specs.length; i++)
//		{
//			tmp.addAll(specs[i].getClassList());
//
//		}

		Vdm2Uml w = new Vdm2Uml();
		Uml2XmiEAxml xmi = new Uml2XmiEAxml();
		xmi.Save(xmiDocumentFileName, w.init(op.astDocument.getSpecifications()));
		// System.out.println(xmi);

		return xmiDocumentFileName;
	}

	public static String TransLateTexVdmToUml(String[] files, String outputFile) throws FileNotFoundException, CGException, IOException
	{
	//	File output =new File(files[0]+ File.separatorChar + "tmp");
	//	output.createNewFile();
		
		StringBuilder sb=new StringBuilder();
		for (String file : files) {
			sb.append("\n" + ClassExstractorFromTexFiles.exstractAsString(file));
		}
		
		
	//	String[] files1 = ClassExstractorFromTexFiles.exstract(files,output.getAbsolutePath());
		return Translator.TranslateVdmToUml(sb.toString(), outputFile);

	}

	public static String Test(String par)
	{
		return par;
	}

	public static String[] TexConvert(String[] files) throws IOException
	{
		File output =new File(files[0]+ File.separatorChar + "tmp");
		output.createNewFile();
		return new ClassExstractorFromTexFiles().exstract(files,output.getAbsolutePath());
	}

	public static void TransLateUmlToVdm(String file, String outputFile) throws Exception
	{

		XmlDocument doc = XmlParser.Parse(file,false);
		Vdm2Uml v = new Vdm2Uml();
		// cr tmp := v.init(new TestData().ClassAssociation.getSpecifications())
		// --p tmp

		// create xmi := new Uml2XmiEAxml(=
		// d xmi.Save("testRun.xml",tmp)
		// p xmi.doc
		Xml2UmlModel xmlUmlModel = new Xml2UmlModel();
		xmlUmlModel.VisitXmlDocument(doc);
		// p xmlUmlModel.result --here the umlModel is present

		Uml2Vdm u = new Uml2Vdm();
		Oml2Vpp vpp = new Oml2Vpp();
		// --cr spec := u.init(tmp).getSpecifications()

		vpp.Save(outputFile, u.init(xmlUmlModel.result));
	}
}
