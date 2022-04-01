
package org.overture.core.uml2;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
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
import java.util.*;

public class Vdm2UmlMain
{
    public static final String FOLDER_ARG = "-folder";
	public static final String OUTPUT_ARG = "-output";

    public static void main(String[] args)
    {
        File outputDir = null;

        if (args == null || args.length <= 1)
        {
            usage("Too few arguments provided");
        }
        
        List<String> listArgs = Arrays.asList(args);
        List<File> files = new LinkedList<File>();

        for (Iterator<String> i = listArgs.iterator(); i.hasNext();)
        {
            String arg = i.next();
            
            if (arg.equals(FOLDER_ARG))
            {
                if (i.hasNext())
                {
                    File path = new File(i.next());

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

        handlePp();

        MsgPrinter.getPrinter().println("Finished UML transformation! Bye...\n");
    }   

    public static void handlePp(List<File> files, File outputDir)
    {
        try
        {
            TypeCheckResult<List<SClassDefinition>> tcResult = TypeCheckerUtil.typeCheckPp(files);
            
            if (GeneralCodeGenUtils.hasErrors(tcResult))
            {
                MsgPrinter.getPrinter().error("Found errors in VDM model:");
                MsgPrinter.getPrinter().errorln(GeneralCodeGenUtils.errorStr(tcResult));
                return;
            }

            String projectName = path.getName();
            List<SClassDefinition> classList = tcResult.getClasses();

            vdm2uml.convert(projectName, classList);
            
        } catch (AnalysisException e) {
            MsgPrinter.getPrinter().println("Could not generate UML: " + e.getMessage());
        }
        
        URI uri = URI.createFileURI(path + "/" + projectName);
        try
        {
            vdm2uml.save(uri);
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (CoreException e)
        {
            e.printStackTrace();
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
        MsgPrinter.getPrinter().errorln("VDM-to-Java Code Generator: " + msg
            + "\n");

        MsgPrinter.getPrinter().errorln(FOLDER_ARG
            + " <folder path>: a folder containing input vdm source files");

        MsgPrinter.getPrinter().errorln(OUTPUT_ARG
            + " <folder path>: the output folder of the generated code");
        
        // Terminate
        System.exit(1);
    }   
}

