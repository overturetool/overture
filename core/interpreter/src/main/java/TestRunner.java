import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.interpreter.messages.Console;
import org.overture.interpreter.runtime.*;
import org.overture.interpreter.values.*;

import java.util.List;
import java.util.Vector;

public class TestRunner {
    public static Value collectTests(Value obj) {
        List<String> tests = new Vector<String>();
        ObjectValue instance = (ObjectValue) obj;

        if (ClassInterpreter.getInstance() instanceof ClassInterpreter) {
            for (SClassDefinition def : ((ClassInterpreter) ClassInterpreter.getInstance()).getClasses()) {
                if (def.getIsAbstract() || !isTestClass(def)) {
                    continue;
                }
                tests.add(def.getName().getName());
            }
        }

        Context mainContext = new StateContext(Interpreter.getInstance().getAssistantFactory(), instance.type.getLocation(), "reflection scope");

        mainContext.putAll(ClassInterpreter.getInstance().initialContext);
        mainContext.setThreadState(ClassInterpreter.getInstance().initialContext.threadState.dbgp, ClassInterpreter.getInstance().initialContext.threadState.CPU);

        ValueSet vals = new ValueSet();
        for (String value : tests) {
            try {
                vals.add(ClassInterpreter.getInstance().evaluate("new " + value
                        + "()", mainContext));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        try {
            return new SetValue(vals);
        } catch (ValueException e) {
            return null;    // Not reached
        }
    }

    private static boolean isTestClass(SClassDefinition def) {
        if (def.getIsAbstract() || def.getName().getName().equals("Test")
                || def.getName().getName().equals("TestCase")
                || def.getName().getName().equals("TestSuite")) {
            return false;
        }

        if (checkForSuper(def, "TestSuite")) {
            // the implementation must be upgrade before this work.
            // The upgrade should handle the static method for creatint the suire
            return false;
        }

        return checkForSuper(def, "Test");
    }

    private static boolean checkForSuper(SClassDefinition def, String superName) {
        for (SClassDefinition superDef : def.getSuperDefs()) {
            if (superDef.getName().getName().equals(superName)
                    || checkForSuper(superDef, superName)) {
                return true;
            }
        }
        return false;
    }


    public static Value run() {
        //lets find the modules
        List<AModuleModules> tests = new Vector<>();

        if (ModuleInterpreter.getInstance() instanceof ModuleInterpreter) {
            for (AModuleModules def : ((ModuleInterpreter) ModuleInterpreter.getInstance()).getModules()) {
                if (!def.getName().getName().endsWith("Test")) {
                    continue;
                }
                tests.add(def);
            }
        }

        int testCount = 0;
        int testFailCount = 0;
        int testErrorCount = 0;

        for (AModuleModules module : tests) {
            String moduleName = module.getName().getName();
            for (PDefinition def : module.getDefs()) {

                String testName = def.getName().getName();
                if (def instanceof AExplicitOperationDefinition && testName.startsWith("test")) {
                    try {
                        testCount++;
                        Console.out.println("Executing test: " + moduleName + "`" + testName + "()");
                        if (TestCase.reflectionRunTest(module, (AExplicitOperationDefinition) def)) {
                            Console.out.println("\tOK");
                        } else {
                            testFailCount++;
                            Console.out.println("\tFAIL");
                        }
                    } catch (Exception e) {
                        testErrorCount++;
                        Console.out.println("\tERROR");
                    }
                }
            }
        }

        String header = "----------------------------------------\n" +
                "|    TEST RESULTS                      |\n" +
                "|--------------------------------------|";
        Console.out.println(header);
        Console.out.println(String.format("| Executed: %1$-" + 27 + "s", testCount) + "|");
        Console.out.println(String.format("| Failures: %1$-" + 27 + "s", testFailCount) + "|");
        Console.out.println(String.format("| Errors  : %1$-" + 27 + "s", testErrorCount) + "|");
        Console.out.println("|______________________________________|");


        if (testErrorCount == 0 && testFailCount == 0) {
            Console.out.println("|              SUCCESS                 |");
        } else {
            Console.out.println("|              FAILURE                 |");
        }
        Console.out.println("|______________________________________|\n");


        return new VoidValue();
    }
}
