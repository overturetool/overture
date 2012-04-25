package org.overturetool.jml.batch;

import java.io.*;
import java.util.Iterator;
import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.overturetool.ast.imp.*;
import org.overturetool.jml.ast.imp.JmlSpecifications;
import org.overturetool.jml.ast.imp.JmlWrappedJmlClass;
import org.overturetool.mapper.BuildTypes;
import org.overturetool.mapper.Jml2Vdm;
import org.overturetool.mapper.Vdm2Jml;
import org.overturetool.parser.imp.OvertureParser;

public class Main {

	
	public static void main(String[] args) throws IOException, CGException {
		
		//args have a number of files to translate
		Vector<File> vec = new Vector<File>();
		int i, size = args.length;
		
		if(args.length == 0) {
			
			throw new IOException("Arguments missing.");
		}
		
		String mode = args[0];
		
		System.out.println(mode);
		for(i = 1; i < size; i++) {
			
			vec.add(new File(args[i]));
		}
		
		// VDM++ TO JML Mapping
		if(mode.equals(new String("vpp"))) {
			
			//invoking the parser
			Iterator it = vec.iterator();
			Vector<OmlSpecifications> specs = new Vector();
			//OmlSpecifications specs = null;
			
			while(it.hasNext()) {

				File fname = (File) it.next();
				
				//create streams
				FileInputStream theFile = new FileInputStream(fname);
				InputStreamReader theStream = new InputStreamReader(theFile);
				
				//create parser
				OvertureParser theParser = new OvertureParser(theStream);
				
				//parse the document
				theParser.parseDocument();
				theParser.astDocument.setFilename(fname.getAbsolutePath());
				
				//get the specifications
				specs.add((OmlSpecifications) theParser.astDocument.getSpecifications());
				
				/*
				//get the classes of the current oml specification
				Vector<OmlClass> classList = new Vector<OmlClass>();
				classList = specs.getClassList();
				
				//An Oml specification can have multiple classes. For each class, convert using the mapper.
				Iterator<OmlClass> clit = classList.iterator();
				
				while(clit.hasNext()) {
					
					OmlClass cl = (OmlClass) clit.next();
					
					System.out.println("Class name: " + cl.getIdentifier());
					
				}
				
				System.out.println("Success!!");
				*/
			}
			
			OmlSpecifications allspecs = mergeSpecs(specs);
			
			
			Vdm2Jml mapper = new Vdm2Jml();
			
			mapper.gatherTypeInfo(allspecs);
			
			//JmlSpecifications jspecs = mapper.init(allspecs);
			
			BuildTypes build = new BuildTypes(mapper.to_uclass,"/Users/vilhena/Desktop/");
			
			try {
				build.buildClasses();
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			/*
			Vector<JmlWrappedJmlClass> vect = new Vector<JmlWrappedJmlClass>();
			vect = jspecs.getClassList();
			int k, sizek = vect.size();
			
			for(k = 0; k < sizek; k++) {
				
				
				//System.out.println(((JmlWrappedJmlClass) jspecs.getClassList().get(k)).getClassVal().getKind().toString());
				//System.out.println(((JmlWrappedJmlClass) jspecs.getClassList().get(k)).toString() + "\n\n--------------\n\n");
			}
			
			System.out.println("FROM VDM++ TO JML:\n\n");
			
			
			Jml2Vdm rev = new Jml2Vdm();
			
			OmlSpecifications omls = rev.build_uvdm(jspecs);
			Vector omlcl = omls.getClassList();
			
			int j, sizej = omlcl.size();
			for(j = 0; j < sizej; j++) {
				
				
				System.out.println(((OmlClass) omlcl.get(j)).toString());
			}
			*/
			
			// Write type information
			
			
			
		}
		if(mode.equals(new String( "jml"))) {
			
			System.out.println("Jml to Vpp mapper");
		}

	}
	
	public static OmlSpecifications mergeSpecs(Vector<OmlSpecifications> specs) throws CGException {
		
		Vector<OmlClass> classlist = new Vector<OmlClass>();
		
		int i, size = specs.size();
		
		for(i = 0; i < size; i++) {
			
			OmlSpecifications sp = (OmlSpecifications) specs.get(i);
			Vector<OmlClass> vec = new Vector<OmlClass>();
			vec = sp.getClassList();
			int j, s = vec.size();
			
			for(j = 0; j < s; j++) {
				
				OmlClass cl = vec.get(j);
				classlist.add(cl);
			}
		}
		
		OmlSpecifications newsp = new OmlSpecifications(classlist);
		
		return newsp;
	}

}
