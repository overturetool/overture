package org.overture.core.tests.demos;

import static org.junit.Assert.fail;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.apache.commons.collections4.CollectionUtils;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;

/**
 * When setting up a test in the new framework you need a specific result type for your test. You can reuse it for
 * multiple tests but it will typically be specific to a particular module. <br>
 * <br>
 * There is no interface defining this result so you can use whatever you want, including the classes that represent
 * your module's output directly. But it's a good idea to have a dedicated result class. It should be as small as
 * possible and only contain data that is actually relevant for test purposes. <br>
 * <br>
 * out {@link DemoTestResult} is extremely simple. It's simply a collection of strings, implemented by extending
 * {@link Vector}. It also has a couple of utility methods.
 *
 * @author ldc
 */
public class DemoTestResult extends Vector<String> implements Serializable,
        List<String> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * One of the key things you must do is convert the output of your analysis into a test result. You can do it either
     * in the constructor or in a static method, as we have done here.<br>
     * <br>
     *
     * @param ast This will typically be the output of your analysis. The ID analysis does nothing so this is just the
     *            AST itself.
     * @return a new instance of {@link DemoTestResult}
     */
    public static DemoTestResult convert(List<INode> ast) {
        DemoTestResult r = new DemoTestResult();
        for (INode n : ast) {
            if (n instanceof AModuleModules) // ModuleModules prints the file path so we skip it
            {
                for (PDefinition p : ((AModuleModules) n).getDefs()) {
                    r.add(p.toString());
                }

            } else {
                r.add(n.toString());
            }
        }
        return r;
    }

    /**
     * In the new test framework, comparison of results is entirely in your hands. So you will a way of comparing 2
     * instances of a test result. Again, this can be done in various different ways (including mainlining it in the
     * test class). We suggest doing it in a way that allows for easy reuse across multiple tests. <br>
     * <br>
     * With the {@link DemoTestResult} case we have implemented the comparison in a static method on the
     * {@link DemoTestResult} class that takes 2 results but it could easily have been done as an instance method that
     * takes only one result (the "other"). We also take the testname so we can report it in the failure message.<br>
     * <br>
     * The ID tests simply call this method and assume that if it completes, then the test has passed, So, if the
     * results do not match, we must call {@link org.junit.Assert#fail()}.
     *
     * @param actual   actual result
     * @param expected expected result
     * @param testName name of the test
     */
    public static boolean compare(DemoTestResult actual, DemoTestResult expected,
                                  String testName) {
        Collection<String> stored_notfound = CollectionUtils.removeAll(expected, actual);
        Collection<String> found_notstored = CollectionUtils.removeAll(actual, expected);

        return (stored_notfound.isEmpty() && found_notstored.isEmpty());

    }
}
