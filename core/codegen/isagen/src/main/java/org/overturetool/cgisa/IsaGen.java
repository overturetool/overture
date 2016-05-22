/*
 * #%~
 * VDM to Isabelle Translation
 * %%
 * Copyright (C) 2008 - 2015 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */

package org.overturetool.cgisa;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.codegen.ir.CodeGenBase;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.IRStatus;
import org.overture.codegen.ir.PIR;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.VdmNodeInfo;
import org.overture.codegen.ir.declarations.AModuleDeclIR;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overturetool.cgisa.transformations.GroupMutRecs;
import org.overturetool.cgisa.transformations.SortDependencies;
import org.overturetool.cgisa.transformations.StateInit;

/**
 * Main facade class for VDM 2 Isabelle IR
 *
 * @author ldc
 */
public class IsaGen extends CodeGenBase {

    public static String vdmExp2IsaString(PExp exp) throws AnalysisException,
            org.overture.codegen.ir.analysis.AnalysisException {
        IsaGen ig = new IsaGen();
        GeneratedModule r = ig.generateIsabelleSyntax(exp);
        if (r.hasMergeErrors()) {
            throw new org.overture.codegen.ir.analysis.AnalysisException(exp.toString()
                    + " cannot be generated. Merge errors:"
                    + r.getMergeErrors().toString());
        }
        if (r.hasUnsupportedIrNodes()) {
            throw new org.overture.codegen.ir.analysis.AnalysisException(exp.toString()
                    + " cannot be generated. Unsupported in IR:"
                    + r.getUnsupportedInIr().toString());
        }
        if (r.hasUnsupportedTargLangNodes()) {
            throw new org.overture.codegen.ir.analysis.AnalysisException(exp.toString()
                    + " cannot be generated. Unsupported in TargLang:"
                    + r.getUnsupportedInTargLang().toString());
        }

        return r.getContent();
    }


    /**
     * Main entry point into the Isabelle Translator component. Takes an AST and returns corresponding Isabelle Syntax.
     *
     * @param statuses The IR statuses holding the nodes to be code generated.
     * @return The generated Isabelle syntax
     * @throws AnalysisException
     *
     */
    @Override
    protected GeneratedData genVdmToTargetLang(List<IRStatus<PIR>> statuses) throws AnalysisException {
        GeneratedData r = new GeneratedData();
        try {
            // Apply transformations
            for (IRStatus<PIR> status : statuses) {
                // make init expression an op
                StateInit stateInit = new StateInit(getInfo());
                generator.applyPartialTransformation(status, stateInit);

                // transform away any recursion cycles
                GroupMutRecs groupMR = new GroupMutRecs();
                generator.applyTotalTransformation(status, groupMR);

                if (status.getIrNode() instanceof AModuleDeclIR) {
                    AModuleDeclIR cClass = (AModuleDeclIR) status.getIrNode();
                    // then sort remaining dependencies
                    SortDependencies sortTrans = new SortDependencies(cClass.getDecls());
                    generator.applyPartialTransformation(status, sortTrans);
                }
            }

            r.setClasses(prettyPrint(statuses));
        } catch (org.overture.codegen.ir.analysis.AnalysisException e) {
            throw new AnalysisException(e);
        }
        return r;

    }

    public GeneratedModule generateIsabelleSyntax(PExp exp)
            throws AnalysisException,
            org.overture.codegen.ir.analysis.AnalysisException {
        IRStatus<SExpIR> status = this.generator.generateFrom(exp);

        if (status.canBeGenerated()) {
            return prettyPrint(status);
        }

        throw new org.overture.codegen.ir.analysis.AnalysisException(exp.toString()
                + " cannot be code-generated");
    }


    private List<GeneratedModule> prettyPrint(List<IRStatus<PIR>> statuses)
            throws org.overture.codegen.ir.analysis.AnalysisException {
        // Apply merge visitor to pretty print Isabelle syntax
        IsaTranslations isa = new IsaTranslations();
        MergeVisitor pp = isa.getMergeVisitor();

        List<GeneratedModule> generated = new ArrayList<GeneratedModule>();

        for (IRStatus<PIR> status : statuses) {
            generated.add(prettyPrintNode(pp, status));

        }

        // Return syntax
        return generated;
    }

    private GeneratedModule prettyPrint(IRStatus<? extends INode> status)
            throws org.overture.codegen.ir.analysis.AnalysisException {
        // Apply merge visitor to pretty print Isabelle syntax
        IsaTranslations isa = new IsaTranslations();
        MergeVisitor pp = isa.getMergeVisitor();
        return prettyPrintNode(pp, status);
    }

    private GeneratedModule prettyPrintNode(MergeVisitor pp,
                                            IRStatus<? extends INode> status)
            throws org.overture.codegen.ir.analysis.AnalysisException {
        INode irClass = status.getIrNode();

        StringWriter sw = new StringWriter();

        irClass.apply(pp, sw);

        if (pp.hasMergeErrors()) {
            return new GeneratedModule(status.getIrNodeName(), irClass, pp.getMergeErrors(), false);
        } else if (pp.hasUnsupportedTargLangNodes()) {
            return new GeneratedModule(status.getIrNodeName(), new HashSet<VdmNodeInfo>(), pp.getUnsupportedInTargLang(), false);
        } else {
            // Code can be generated. Ideally, should format it
            GeneratedModule generatedModule = new GeneratedModule(status.getIrNodeName(), irClass, sw.toString(), false);
            generatedModule.setTransformationWarnings(status.getTransformationWarnings());
            return generatedModule;
        }
    }
}
