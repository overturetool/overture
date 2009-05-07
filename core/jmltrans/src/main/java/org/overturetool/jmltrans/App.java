package org.overturetool.jmltrans;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.overturetool.ast.imp.OmlSpecifications;
import org.overturetool.jml.ast.imp.JmlSpecifications;
import org.overturetool.jml.ast.imp.JmlWrappedJmlClass;
import org.overturetool.mapper.Vdm2Jml;
import org.overturetool.parser.imp.OvertureParser;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {	if (args.length > 0) {
	       	try {
	       		OmlSpecifications specs = null;
	       		File fname = new File(args[0]);
	       		FileInputStream theFile = new FileInputStream(fname);
	       		InputStreamReader theStream = new InputStreamReader(theFile);
	       		OvertureParser theParser = new OvertureParser(theStream);
	 			theParser.parseDocument();
				theParser.astDocument.setFilename(fname.getAbsolutePath());
				specs = (OmlSpecifications) theParser.astDocument.getSpecifications();
				Vdm2Jml mapper = new Vdm2Jml();
				JmlSpecifications jspecs = mapper.init(specs);
				
				Vector<JmlWrappedJmlClass> vect = new Vector<JmlWrappedJmlClass>();
				vect = jspecs.getClassList();
				int k, sizek = vect.size();
				
				String findir = new String(".");
				for(k = 0; k < sizek; k++) {
					
					String str = new String(((JmlWrappedJmlClass) vect.get(k)).toString());
					String name = ((JmlWrappedJmlClass) vect.get(k)).getClassVal().getIdentifier();
					
					String path = new String(findir + "/" + name + ".jml");
					File res = new File(path);
					FileWriter fw = new FileWriter(res);
					fw.write(str);
					fw.close();
					res.createNewFile();
				}
			} catch (Exception e) {			
				e.printStackTrace();
			}
    	} else {
    		System.out.println("Argument missing!");
    	}
    }
}
