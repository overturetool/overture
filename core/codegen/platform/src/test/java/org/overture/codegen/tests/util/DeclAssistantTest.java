package org.overture.codegen.tests.util;


import org.junit.Assert;
import org.junit.Test;
import org.overture.codegen.assistant.DeclAssistantIR;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.name.ATokenNameIR;

import java.util.LinkedList;

public class DeclAssistantTest {

    @Test
    public void parentTest()
    {
        IRInfo info = new IRInfo();

        ADefaultClassDeclIR clazz = new ADefaultClassDeclIR();
        clazz.setName("A");

        Assert.assertFalse("Expected parent of " + clazz.getName() + " to not be a test", info.getDeclAssistant().parentIsTest(clazz));

        ATokenNameIR testClass = new ATokenNameIR();
        testClass.setName(IRConstants.TEST_CASE);

        clazz.getSuperNames().add(testClass);

        Assert.assertTrue("Expected parent of " + clazz.getName() + " to be a test", info.getDeclAssistant().parentIsTest(clazz));
    }

    @Test
    public void isTestSimple()
    {
        LinkedList<SClassDeclIR> classes = new LinkedList<>();

        IRInfo info = new IRInfo();

        ADefaultClassDeclIR clazz = new ADefaultClassDeclIR();
        clazz.setName("A");

        classes.add(clazz);

        Assert.assertFalse("Expected " + clazz.getName() + " to not be a test", info.getDeclAssistant().isTest(clazz, classes));

        ATokenNameIR testClass = new ATokenNameIR();
        testClass.setName(IRConstants.TEST_CASE);

        clazz.getSuperNames().add(testClass);

        Assert.assertTrue("Expected " + clazz.getName() + " to be a test", info.getDeclAssistant().isTest(clazz, classes));
    }

    @Test
    public void isTestAncestorIsTest()
    {
        LinkedList<SClassDeclIR> classes = new LinkedList<>();

        IRInfo info = new IRInfo();

        ADefaultClassDeclIR clazzA = new ADefaultClassDeclIR();
        clazzA.setName("A");

        ATokenNameIR b = new ATokenNameIR();
        b.setName("B");

        clazzA.getSuperNames().add(b);

        ADefaultClassDeclIR clazzB = new ADefaultClassDeclIR();
        clazzB.setName("B");

        ATokenNameIR testCase = new ATokenNameIR();
        testCase.setName(IRConstants.TEST_CASE);

        clazzB.getSuperNames().add(testCase);

        classes.add(clazzA);
        classes.add(clazzB);

        Assert.assertFalse("Expected parent of " + clazzA.getName() + " not to be a test", info.getDeclAssistant().parentIsTest(clazzA));
        Assert.assertTrue("Expected " + clazzA.getName() + " to be a test", info.getDeclAssistant().isTest(clazzA, classes));
        Assert.assertTrue("Expected parent of " + clazzB.getName() + " to be a test", info.getDeclAssistant().parentIsTest(clazzB));
        Assert.assertTrue("Expected " + clazzB.getName() + " to be a test", info.getDeclAssistant().isTest(clazzB, classes));
    }
}
