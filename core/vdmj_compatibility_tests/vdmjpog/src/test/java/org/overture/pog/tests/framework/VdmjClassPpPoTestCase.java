package org.overture.pog.tests.framework;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.overture.test.framework.results.IMessage;
import org.overture.test.framework.results.Result;
import org.overture.test.util.XmlResultReaderWritter;
import org.overture.test.util.XmlResultReaderWritter.IResultStore;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.pog.ProofObligation;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.syntax.ParserException;
import org.overturetool.vdmj.typechecker.ClassTypeChecker;
import org.overturetool.vdmj.typechecker.TypeChecker;
import org.overturetool.vdmj.util.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class VdmjClassPpPoTestCase extends BasicPogTestCase implements IResultStore<ProofObligationList> {

	public static final String tcHeader = "-- TCErrors:";

	File file;
	String name;
	String content;
	String expectedType;
	ParserType parserType;

	public VdmjClassPpPoTestCase() {
		super("test");
	}

	public VdmjClassPpPoTestCase(File file) {
		super("test");
		this.parserType = ParserType.Module;
		this.file = file;
		this.content = file.getName();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Settings.dialect = Dialect.VDM_PP;
		Settings.release = Release.VDM_10;
		TypeChecker.clearErrors();
	}

	public void test() throws ParserException, LexException, IOException {
		if (content != null) {
			moduleTc(content);
		}
	}

	private void moduleTc(String expressionString) throws ParserException,
			LexException, IOException {
	
		ClassList modules = parse(ParserType.Class, file);
		
		prepareClassesForTc(modules);
		ClassTypeChecker moduleTC = new ClassTypeChecker(modules);
		moduleTC.typeCheck();

//		ProofObligationList pos = new ProofObligationList();
//		for (ClassDefinition module : modules) {
//			pos.addAll(module.getProofObligations(new POContextStack()));
//		}
		
//		System.out.println(pos);
		File resultFile = new File(file.getAbsolutePath() + ".result");
		XmlResultReaderWritter<ProofObligationList> xmlResult = new XmlResultReaderWritter<ProofObligationList>(resultFile,this);
		
		xmlResult.setResult("proof_obligation", new Result<ProofObligationList>(modules.getProofObligations(),new Vector<IMessage>(),new Vector<IMessage>()));
		try {
			xmlResult.saveInXml();			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
	
	protected void prepareClassesForTc(ClassList classes)
	{
		
	}

	public void encondeResult(ProofObligationList result,Document doc, Element resultElement) {
		for (ProofObligation po : result) {
			Element message = doc.createElement("po");
			message.setAttribute("resource", file.getName());
			message.setAttribute("number",
					new Integer(po.number).toString());
			message.setAttribute("message", po.toString());
			message.setAttribute("column",po.location.startPos+"");
			message.setAttribute("line", po.location.startLine+"");
			message.setAttribute("toString", po.toString());
			message.setAttribute("object","");
//			try {
//				//message.setAttribute("object",toString( po));
//			}  catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			resultElement.appendChild(message);
		}
		
	}
	
	 /** Read the object from Base64 string. 
	 * @throws Exception */
    private static Object fromString( String s ) throws Exception {
       
        ObjectInputStream ois = new ObjectInputStream( 
                                        new ByteArrayInputStream(Base64.decode(s) ) );
        Object o  = ois.readObject();
        ois.close();
        return o;
    }

    

	public ProofObligationList decodeResult(Node node) {
		ProofObligationList list = new ProofObligationList();
		
		for (int i = 0; i < node.getChildNodes().getLength(); i++)
		{
			Node cn = node.getChildNodes().item(i);
			if(cn.getNodeType()==Node.ELEMENT_NODE && cn.getNodeName().equals("po"))
			{
				String nodeType = cn.getAttributes().getNamedItem("object").getNodeValue();
				try
				{
					list.add((ProofObligation) fromString(nodeType));
				} catch (Exception e)
				{
					fail("Not able to decode stored result");
				}
			}
		}
		return list;
	}
	
	
	@Override
	public String getName() {
		
		return file==null?"no name":file.getName();
	}

}
