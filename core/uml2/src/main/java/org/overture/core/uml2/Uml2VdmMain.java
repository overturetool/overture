package org.overture.core.uml2;

import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.core.uml2.uml2vdm.Uml2Vdm;
import org.eclipse.emf.common.util.URI;
import org.overture.codegen.printer.MsgPrinter;
//import org.overture.codegen.utils.GeneralCodeGenUtils;
//import org.overture.codegen.utils.GeneralUtils;
import java.io.File;
//import java.io.IOException;
import java.util.*;

public class Uml2VdmMain {
    
    public static final String FILE_ARG = "-file";

    public static void main(String[] args)
    {

        File umlPath = null;

        if (args == null)
        {
            usage("Too few arguments provided");
        }
        List<String> listArgs = Arrays.asList(args);

        //Settings.release = Release.VDM_10;
        //Settings.dialect = Dialect.VDM_PP;

        for (Iterator<String> i = listArgs.iterator(); i.hasNext();)
        {
            String arg = i.next();  

            if (arg.equals(FILE_ARG))
            {
                MsgPrinter.getPrinter().println("file arg found \n");
                if (i.hasNext()) 
                {
                    umlPath = new File(i.next());

                    if (!umlPath.isFile())
                    {
                        usage("Could not find path: " + umlPath);
                    }
                } 
            } else
            {
                usage(FILE_ARG + " requires a UML file");
            }
        }

        final Uml2Vdm uml2vdm = new Uml2Vdm();

        MsgPrinter.getPrinter().println("Importing UML...\n");
        
        URI uri = URI.createFileURI(umlPath.getPath());

        if (!uml2vdm.initialize(uri, null))
        {
            usage("Failed importing .uml file. Maybe it doesnt have the EMF UML format");
        }

        MsgPrinter.getPrinter().println("Model initialized...\n");
        
        //extracting parrent directory
        String parentDir = umlPath.getParent();
        
        //name of UML file
        //String name = umlPath.getName();
        
        File outputDir = new File(parentDir + "/uml_import"); //+name

        //File outputDir = new File("UML Import");


        uml2vdm.convert(outputDir);
    }


    public static void usage(String msg)
    {
        MsgPrinter.getPrinter().errorln("UML-to-VDM Transformation: " + msg
            + "\n");

        MsgPrinter.getPrinter().errorln(FILE_ARG
            + " <file path>: an input UML file");

        // Terminate
        System.exit(1);
    }   
}


