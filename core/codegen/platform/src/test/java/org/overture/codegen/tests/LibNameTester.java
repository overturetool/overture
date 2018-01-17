package org.overture.codegen.tests;

import org.junit.Assert;
import org.junit.Test;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.assistant.DeclAssistantIR;
import org.overture.config.Settings;

public class LibNameTester {

    @Test
    public void slIO()
    {
        Settings.dialect = Dialect.VDM_SL;
        DeclAssistantIR assist = consDeclAssistant();

        Assert.assertTrue("IO is a VDM-SL library name", assist.isLibraryName("IO"));
    }

    @Test
    public void slVdmUnit()
    {
        Settings.dialect = Dialect.VDM_SL;
        DeclAssistantIR assist = consDeclAssistant();

        Assert.assertFalse("VDMUnit is not a VDM-SL library name", assist.isLibraryName("VDMUnit"));
    }

    @Test
    public void slVdmAssert()
    {
        Settings.dialect = Dialect.VDM_SL;
        DeclAssistantIR assist = consDeclAssistant();

        Assert.assertFalse("Assert is not a VDM-SL library name", assist.isLibraryName("Assert"));
    }

    @Test
    public void ppIO()
    {
        Settings.dialect = Dialect.VDM_PP;
        DeclAssistantIR assist = consDeclAssistant();

        Assert.assertTrue("IO is a VDMPP library", assist.isLibraryName("IO"));
    }

    @Test
    public void ppVdmUnit()
    {
        Settings.dialect = Dialect.VDM_PP;
        DeclAssistantIR assist = consDeclAssistant();

        Assert.assertTrue("VDMUnit is a VDMPP library", assist.isLibraryName("VDMUnit"));
    }

    @Test
    public void rtAssert()
    {
        Settings.dialect = Dialect.VDM_RT;
        DeclAssistantIR assist = consDeclAssistant();

        Assert.assertTrue("Assert is part of the VDMUnit framework", assist.isLibraryName("Assert"));
    }


    public DeclAssistantIR consDeclAssistant()
    {
        return new DeclAssistantIR(null);
    }
}
