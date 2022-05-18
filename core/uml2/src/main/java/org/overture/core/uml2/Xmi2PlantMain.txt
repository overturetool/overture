package org.overture.core.uml2;
import org.overture.core.uml2.xmi2plant.Xmi2Plant;
import org.eclipse.emf.common.util.URI;
import org.overture.codegen.printer.MsgPrinter;
import java.io.File;
//import java.io.IOException;
import java.util.*;



public class Xmi2PlantMain {
    
    public static final String FILE_ARG = "-file";

    public static void main(String[] args)
    {
        File umlPath = null;

        if (args == null)
        {
            usage("Too few arguments provided");
        }
        List<String> listArgs = Arrays.asList(args);

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

        final Xmi2Plant Xmi2Plant = new Xmi2Plant();

        MsgPrinter.getPrinter().println("Importing UML...\n");
        
        URI uri = URI.createFileURI(umlPath.getPath());

        if (!Xmi2Plant.initialize(uri, null))
        {
            usage("Failed importing .uml file. Maybe it doesnt have the EMF UML format");
        }

        MsgPrinter.getPrinter().println("Model initialized...\n");
        
        String parentDir = umlPath.getParent();
        
        File outputDir = new File(parentDir + "/diagrams"); 

        Xmi2Plant.convert(outputDir);
    }

    public static void usage(String msg)
    {
        MsgPrinter.getPrinter().errorln("XMI-to-PLANT Transformation: " + msg
            + "\n");

        MsgPrinter.getPrinter().errorln(FILE_ARG
            + " <file path>: an input UML file");

        // Terminate
        System.exit(1);
    }   
}


