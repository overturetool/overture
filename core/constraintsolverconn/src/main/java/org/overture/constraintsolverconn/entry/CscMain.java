package org.overture.constraintsolverconn.entry;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;

<<<<<<< 398f8f2ca1757f9b3a3eb608fdd2b594d22e6d4b
public class CscMain
{

	public static void main(String[] args) throws AnalysisException
	{
=======
// added by his
import java.io.Reader;
import java.io.StringWriter;

public class CscMain {
>>>>>>> a1082541b986672b3363953499d7b4b6b5ec508a

		Settings.dialect = Dialect.VDM_RT;
		Settings.release = Release.VDM_10; // clearing to display "VDM classic"

		Csc csc = new Csc();
		String result;
<<<<<<< 398f8f2ca1757f9b3a3eb608fdd2b594d22e6d4b

		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("src/main/java/org/overture/constraintsolverconn/visitor/Sample.vpp".replace('/', '\\')), "UTF-8"));
			String line;

			while ((line = br.readLine()) != null)
			{
				if (!line.substring(0, 2).equals("--"))
				{
=======
		
		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(
							      new FileInputStream("c:/users/ishihiro/csccode/core/constraintsolverconn/src/main/java/org/overture/constraintsolverconn/visitor/Sample.vpp"), "UTF-8"));
			
			String line;
			ProcessBuilder pb;
			while((line=br.readLine()) != null) {
				if(!line.substring(0,2).equals("--")) {
>>>>>>> a1082541b986672b3363953499d7b4b6b5ec508a
					result = csc.visitExp(line);
					// System.out.println(result);

					// the next line is not required if the output is evaluated 
					// by using ProB interpreter on Web
					result=result.replaceAll("\"", "\\\\\"");
					//System.out.println(result);
					pb = new ProcessBuilder("C:/Users/ishihiro/Desktop/ProB/probcli", "-eval", result);
					Process p = pb.start();
					BufferedReader brProB = new BufferedReader(new InputStreamReader(p.getInputStream()));
					Catcher c = new Catcher(brProB);
					c.start();
					p.waitFor();
					p.destroy();
					//Exp val/Sol and Eval val

					c.showResultFromProB(c.out.toString());
					//Input and Output
					System.out.printf("\tInput: %s\tOutput: %s\n", line, result);
				}
			}
			br.close();
		} catch (IOException e)
		{
			System.out.println("IOException: " + e);
		} catch(InterruptedException e) {
			System.out.println("IOException: " + e);
		}
<<<<<<< 398f8f2ca1757f9b3a3eb608fdd2b594d22e6d4b
		// Csc csc = new Csc();
		// String result;
		// String result = csc.visitExp("( 1 + ( 2  - 3 ) * 4 ) / 5");
		// String result = csc.visitExp("- (1 ** (5 mod 3) + true)");

		// System.out.println("Result is: " + result);

		// result = csc.visitExp("1+2+3");
		// System.out.println("Result is: " + result);

		// result = csc.visitExp("{1} \\ {2,3}");
		// System.out.println("Result is: " + result);

		// result = csc.visitExp("dom {1|->2, 3|->3}");
		// System.out.println("Result is: " + result);

		// result = csc.visitExp("rng {1|->2, 3|->3}");
		// System.out.println("Result is: " + result);

		// result = csc.visitExp("{1|->2, 3|->3} = {1|->2, 3|->3}");
		// System.out.println("Result is: " + result);

		// result = csc.visitExp("{1|->2, 3|->3} <> {1|->2, 3|->3}");
		// System.out.println("Result is: " + result);

		// result = csc.visitExp("reverse [1,2,3]");
		// unable to parse expression
		// reverse not available in VDM classic
		// System.out.println("Result is: " + result);

		// result = csc.visitExp("len [1,2,3] ^ [2,4]");
		// unable to type check expression
		// System.out.println("Result is: " + result);

		/*
		 * result = csc.visitExp("{}"); System.out.println("Result is: " + result); result = csc.visitExp("{1, 2, 3}");
		 * System.out.println("Result is: " + result); result = csc.visitExp("{x | x in set nat1 & x <= 3}");
		 * System.out.println("Result is: " + result);
		 */

		System.out.println("... Done.");
	}

}
=======

		System.out.println("... Done.");
	}
	
}

class Catcher extends Thread {
    Reader in;
    StringWriter out = new StringWriter();
    public Catcher(Reader in) {
	this.in = in;
    }

    public void run() {
	int c;
	try {
	    while((c=in.read())!=-1) {
		out.write((char)c);
	    }
	} catch(IOException e) {
	    e.printStackTrace();
	}
    }

    public void showResultFromProB(String output) {
	String ans="Exp val/Sol: ";
	//Exp val/Sol
	if(output.indexOf("Expression Value ")>0) {
	    int tpos1 = output.indexOf("Expression Value ");
	    int tpos2 = output.lastIndexOf("Evaluation results:");
	    
	    ans+=output.substring(tpos1+"Expression Value =".length()+1 ,tpos2).replace(" ","").replace("\t","").replace("\n","").replace("\r","");

	} else if(output.indexOf("Solution:")>0) {
	    int tpos1 = output.indexOf("Solution:");
	    int tpos2 = output.lastIndexOf("Evaluation results:");
	    ans+=output.substring(tpos1+"Solution:".length()+1 ,tpos2).replace(" ","").replace("\t","").replace("\n","").replace("\r","");
	}
	ans+="\t";
	//Eval val
	int pos1=output.lastIndexOf("Evaluation results:");
	int pos2=output.lastIndexOf("/");
	ans+=("\tEval res: " + output.substring(pos1+"Evaluation results:".length()+2 ,pos2)).replace(" ","").replace("\t","").replace("\n","").replace("\r","");

	System.out.print(ans);
    }
}
>>>>>>> a1082541b986672b3363953499d7b4b6b5ec508a
