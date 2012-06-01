package org.overture.pog.tests.framework;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import org.overture.pog.obligations.ProofObligation;
import org.overture.pog.obligations.ProofObligationList;
import org.overture.pog.tests.OvertureTestHelperPOG;
import org.overture.typecheck.TypeChecker;
import org.overturetool.test.framework.ResultTestCase;
import org.overturetool.test.framework.results.Result;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;
import org.overturetool.vdmj.syntax.ParserException;
import org.overturetool.vdmj.util.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ModuleTestCase extends ResultTestCase<ProofObligationList>
{

	public static final Boolean printOks = false;

	File file;
	String name;
	String content;
	String expectedType;
	List<VDMError> errors = new Vector<VDMError>();
	List<VDMWarning> warnings = new Vector<VDMWarning>();

	public ModuleTestCase()
	{
		super();

	}

	public ModuleTestCase(File file)
	{
		super(file);
		this.file = file;
		this.content = file.getName();
	}

	@Override
	public String getName()
	{
		return this.content;
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Settings.dialect = Dialect.VDM_SL;
		Settings.release = Release.VDM_10;
		TypeChecker.clearErrors();
	}

	public void test() throws ParserException, LexException, IOException
	{
		if (content != null)
		{
			moduleTc(content);
		}
	}

	private void moduleTc(String module) throws ParserException, LexException,
			IOException
	{

	
		Result<ProofObligationList> result;
		try {
			result = new OvertureTestHelperPOG().getProofObligations(file);
			compareResults(result, file.getAbsolutePath());
		} catch (Exception e) {
			assert false : "Test failed due to parse or type check error";
		}

	}


	

	@Override
	protected File createResultFile(String filename) {
		return new File(filename + ".result");
	}

	@Override
	protected File getResultFile(String filename) {
		return new File(filename + ".result");
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
			try {
				message.setAttribute("object",toString( po));
			}  catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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

    /** Write the object to a Base64 string. */
    private static String toString( Serializable o ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( o );
        oos.close();
        
        return   Base64.encode( baos.toByteArray() ).toString();
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
	protected boolean assertEqualResults(ProofObligationList expected,
			ProofObligationList actual) {
		//FIXME: check is not sufficient
		return expected.size()==actual.size();
	}
}
