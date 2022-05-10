package org.overture.core.uml2;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;
import org.overture.core.uml2.vdm2uml.Vdm2Uml;
import org.eclipse.emf.common.util.URI;
import org.overture.codegen.printer.MsgPrinter;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.codegen.utils.GeneralUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Vdm2UmlMain
{
    public static final String FOLDER_ARG = "-folder";
	public static final String OUTPUT_ARG = "-output";
	public static final String OO_ARG = "-pp";
	public static final String RT_ARG = "-rt";
    public static final String ASOC = "-preferasoc";
    public static final String DEPLOY = "-deployoutside";

    public static void main(String[] args)
    {
        File outputDir = null;
        String projectName = "";
        boolean preferAssociations = false;
        boolean deployArtifactsOutsideNodes = false;        

        if (args == null || args.length <= 1)
        {
            usage("Too few arguments provided");
        }
        
        List<String> listArgs = Arrays.asList(args);
        List<File> files = new LinkedList<File>();

        Settings.release = Release.VDM_10;

        for (Iterator<String> i = listArgs.iterator(); i.hasNext();)
        {
            String arg = i.next();    

            if (arg.equals(FOLDER_ARG))
            {
                if (i.hasNext())
                {
                    File path = new File(i.next());
                    projectName = path.getName();

                    if (path.isDirectory())
                    {
                        files.addAll(filterFiles(GeneralUtils.getFiles(path)));
                    } else
                    {
                        usage("Could not find path: " + path);
                    }
                } else
                {
                    usage(FOLDER_ARG + " requires a directory");
                }
            } else if (arg.equals(OUTPUT_ARG))
            {
                if (i.hasNext())
                {
                    outputDir = new File(i.next());
                    outputDir.mkdirs(); 

                    if (!outputDir.isDirectory())
                    {
                        usage(outputDir + " is not a directory");
                    }
                } else
                {
                    usage(OUTPUT_ARG + " requires a directory");
                }
            } else if (arg.equals(OO_ARG)) 
            {
                Settings.dialect = Dialect.VDM_PP;
            } else if (arg.equals(RT_ARG)) 
            {
                Settings.dialect = Dialect.VDM_RT;
            } else if (arg.equals(ASOC)) 
            {
                preferAssociations = true;
            } else if (arg.equals(DEPLOY)) 
            {
                deployArtifactsOutsideNodes = true;
            }
        }

        MsgPrinter.getPrinter().println("Starting UML transformation...\n");
        
        if (files.isEmpty())
        {
            usage("Input files are missing");
        }

        if (outputDir == null)
        {
            usage("No output directory specified");
        }

        handleOo(files,outputDir,preferAssociations,deployArtifactsOutsideNodes,Settings.dialect,projectName);

        MsgPrinter.getPrinter().println("Finished UML transformation! Bye...\n");
    }   

    public static void handleOo(List<File> files, File outputDir, 
        boolean preferAssociations, boolean deployArtifactsOutsideNodes, 
        Dialect dialect, String projectName)
    {
        try 
        {
            Vdm2Uml vdm2uml = new Vdm2Uml(preferAssociations, deployArtifactsOutsideNodes);

            TypeCheckResult<List<SClassDefinition>> tcResult = null; // TypeCheckerUtil.typeCheckPp(files);

            if (dialect == Dialect.VDM_PP) 
            {
                tcResult = TypeCheckerUtil.typeCheckPp(files);
            } else 
            {
                tcResult = TypeCheckerUtil.typeCheckRt(files);
            }
            
            if (GeneralCodeGenUtils.hasErrors(tcResult))
            {
                MsgPrinter.getPrinter().error("Found errors in VDM model:");
                MsgPrinter.getPrinter().errorln(GeneralCodeGenUtils.errorStr(tcResult));
                return;
            }

            List<SClassDefinition> classList = tcResult.result;

            vdm2uml.convert(projectName, classList);
            
            URI uri = URI.createFileURI(outputDir.getPath() + "/" + projectName);

            try 
            {
                vdm2uml.save(uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (AnalysisException e)
        {
            MsgPrinter.getPrinter().println("Could not code generate model: "
					+ e.getMessage());
        }
        
    }

    public static List<File> filterFiles(List<File> files)
    {
        List<File> filtered = new LinkedList<File>();

        for (File f : files)
        {
            if (GeneralCodeGenUtils.isVdmSourceFile(f))
            {
                filtered.add(f);
            }
        }

        return filtered;
    }


    public static void usage(String msg)
    {
        MsgPrinter.getPrinter().errorln("VDM-to-UML Transformation: " + msg
            + "\n");

        MsgPrinter.getPrinter().errorln(FOLDER_ARG
            + " <folder path>: a folder containing input vdm source files");

        MsgPrinter.getPrinter().errorln(OUTPUT_ARG
            + " <folder path>: the output folder of the generated code");
        
        // Terminate
        System.exit(1);
    }   
}

