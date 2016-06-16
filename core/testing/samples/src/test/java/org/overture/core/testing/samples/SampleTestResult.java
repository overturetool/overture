package org.overture.core.testing.samples;

import static org.junit.Assert.fail;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.core.testing.AbsResultTest;

/**
 * When setting up a test you need a specific result type for your test. You can reuse it for
 * multiple testing but it will typically be specific to a particular module.
 * </p>
 * There is no interface defining this result so you can use whatever you want, including the classes that represent
 * your module's output directly. But it's a good idea to have a dedicated result class. It should be as small as
 * possible and only contain data that is actually relevant for test purposes.
 * </p>
 * {@link SampleTestResult} is extremely simple. It's simply a collection of strings, implemented by extending
 * {@link Vector}. It also has a couple of utility methods.
 *
 * @author ldc
 */
public class SampleTestResult extends Vector<String> implements Serializable,
        List<String> {

    private static final long serialVersionUID = 1L;

    /**
     * One of the things you must do is convert the output of your analysis into a test result. You can do it either
     * in the constructor or in a static method, as we have done here.
     * </p>
     *
     * @param ast This will typically be the output of your analysis. The ID analysis does nothing so this is just the
     *            AST itself.
     * @return a new instance of {@link SampleTestResult}
     */
    public static SampleTestResult convert(List<INode> ast) {
        SampleTestResult r = new SampleTestResult();
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
     * By default, result comparison is based on an equality check as part of assertEquals. This
     * behavior can be overridden to provide more sophisticated result comparison. If the comparison is
     * to be reused in multiple testing, it should be placed in a separate class as we have done here.
     * </p>
     * When overriding the behavior, it is important to do things: calling JUnit assertions or failures to ensure
     * the test integrates correctly and using {@link AbsResultTest#testInfo()} in failure messages so the test results are easy to update.
     * In this case, since the comparison is done in an external class, it receives the info message as a parameter.
     *
     * @param actual   actual result
     * @param expected expected result
     * @param infoMessage name of the test
     */
    public static void compare(SampleTestResult actual, SampleTestResult expected,
                               String infoMessage) {
            Collection<String> stored_notfound = CollectionUtils.removeAll(expected, actual);
            Collection<String> found_notstored = CollectionUtils.removeAll(actual, expected);

            if (stored_notfound.isEmpty() && found_notstored.isEmpty())
            {
                // Results match, testing pass;do nothing
            } else
            {
                StringBuilder sb = new StringBuilder();
                sb.append(infoMessage);
                sb.append(" ");
                if (!stored_notfound.isEmpty())
                {
                    sb.append("Expected (but not found) Strings: " + "\n");
                    for (String pr : stored_notfound)
                    {
                        sb.append(pr + "\n");
                    }
                }
                if (!found_notstored.isEmpty())
                {
                    sb.append("Found (but not expected) Strings: " + "\n");
                    for (String pr : found_notstored)
                    {
                        sb.append(pr + "\n");
                    }
                }
                Assert.fail(sb.toString());
            }
    }
}
