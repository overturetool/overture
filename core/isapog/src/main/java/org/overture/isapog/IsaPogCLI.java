package org.overture.isapog;

import org.apache.commons.cli.*;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.codegen.ir.CodeGenBase;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overturetool.cgisa.IsaGen;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ldc on 09/02/16.
 */
public class IsaPogCLI {

    private static final String GENERATE_BOTH_OPT = "b";
    private static final String GENERATE_BOTH_LONG_OPT = "model-pos";
    private static final String GENERATE_BOTH_DESC = "Generate Isabelle theories for model and pos search to same country only.";

    private static final String PRINT_OPT = "p";
    private static final String PRINT_LONG_OPT = "print";
    private static final String PRINT_DESC = "Print output to console instead of generating files.";

    private static final String EXP_OPT = "e";
    private static final String EXP_DESC = "blah";

    private static final String USAGE = "java -jar isapog.jar source1 source2...";
    public static final String GEN_FOLDER_NAME = "generated";


    public static void main(final String[] args) {
        CommandLineParser clp = new DefaultParser();
        CommandLine cmd = null;
        Options opts = new Options();
        opts.addOption(GENERATE_BOTH_OPT, GENERATE_BOTH_LONG_OPT, false, GENERATE_BOTH_DESC);
        opts.addOption(PRINT_OPT, PRINT_LONG_OPT, false, PRINT_DESC);
        opts.addOption(EXP_OPT, EXP_DESC);

        cmd = parseAndCheck(args, clp, cmd, opts);

        process(cmd, opts);

    }

    private static void process(CommandLine cmd, Options opts) {
        try {
//            Settings.release= Release.DEFAULT;
            Settings.dialect = Dialect.VDM_SL;

            if (cmd.hasOption(EXP_OPT)) {
                TypeCheckerUtil.TypeCheckResult<PExp> expResult = TypeCheckerUtil.typeCheckExpression(cmd.getArgList().get(0));
                validateInput(expResult);
                System.out.println(IsaGen.vdmExp2IsaString(expResult.result));
                System.exit(0);
            }
            List<String> sources = cmd.getArgList();

            List<File> files = new ArrayList<>(sources.size());

            for (String s : sources) {
                File f = new File(s);
                if (f.exists()) {
                    files.add(new File(s));
                } else {
                    printUsage(opts);
                }
            }

            TypeCheckerUtil.TypeCheckResult<List<AModuleModules>> result = TypeCheckerUtil.typeCheckSl(files);

            validateInput(result);

            List<INode> ast = CodeGenBase.getNodes(result.result);

            IsaPog ip = new IsaPog(ast);

            File folder = new File(GEN_FOLDER_NAME);
            CodeGenBase.emitCode(folder, ip.getModelThyName(), ip.getModelThyString());

            if (cmd.hasOption(GENERATE_BOTH_OPT)) {
                CodeGenBase.emitCode(folder, ip.getPosThyName(), ip.getPosThyString());
            }

        } catch (AnalysisException e) {
            System.err.println("Error processing the model:");
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (org.overture.codegen.ir.analysis.AnalysisException e) {
            System.err.println("Error generating Isabelle sources:");
            System.err.println(e.getMessage());
            System.exit(1);
        }


    }

    private static void validateInput(TypeCheckerUtil.TypeCheckResult<?> result) {
        if (GeneralCodeGenUtils.hasErrors(result)) {
            System.err.println("Found errors in VDM model:");
            System.err.println(GeneralCodeGenUtils.errorStr(result));
            System.exit(1);
        }
    }

    private static CommandLine parseAndCheck(String[] args, CommandLineParser clp, CommandLine cmd, Options opts) {
        try {
            cmd = clp.parse(opts, args);
        } catch (ParseException e) {
            printUsage(opts);
        }

        if (cmd.getArgList().size() < 1) {
            printUsage(opts);
        }
        return cmd;
    }

    private static void printUsage(Options opts) {
        HelpFormatter help = new HelpFormatter();
        help.printHelp(USAGE, opts);
        System.exit(1);
    }


}
