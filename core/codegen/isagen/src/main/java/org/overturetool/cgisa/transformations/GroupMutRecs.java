/*
 * #%~
 * The VDM to Isabelle Translator
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
package org.overturetool.cgisa.transformations;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.overture.cgisa.extast.analysis.DepthFirstAnalysisIsaAdaptor;
import org.overture.cgisa.extast.declarations.AMrFuncGroupDeclIR;
import org.overture.codegen.ir.SDeclIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AFuncDeclIR;
import org.overture.codegen.ir.declarations.AModuleDeclIR;
import org.overture.codegen.trans.ITotalTransformation;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class GroupMutRecs extends DepthFirstAnalysisIsaAdaptor implements
        ITotalTransformation {

    private AModuleDeclIR result = null;
    Dependencies depUtils;
    DirectedGraph<AFuncDeclIR, DefaultEdge> deps;
    List<AFuncDeclIR> funcs;

    public GroupMutRecs() {
        super();
        deps = new DefaultDirectedGraph<>(DefaultEdge.class);
        depUtils = new Dependencies();
        funcs = new LinkedList<AFuncDeclIR>();
    }

    @Override
    public void caseAModuleDeclIR(AModuleDeclIR node) throws AnalysisException {
        result = new AModuleDeclIR();
        result.setExports(node.getExports());
        result.setImport(node.getImport());
        result.setIsDLModule(node.getIsDLModule());
        result.setIsFlat(node.getIsFlat());
        result.setMetaData(node.getMetaData());
        result.setName(node.getName());
        result.setSourceNode(node.getSourceNode());
        result.setTag(node.getTag());
        result.setDecls(node.getDecls());
        filterFunctions(node.getDecls());
        calcDependencies();

    }

    private void filterFunctions(LinkedList<SDeclIR> decls) {
        for (SDeclIR d : decls) {
            if (d instanceof AFuncDeclIR) {
                funcs.add((AFuncDeclIR) d);
            }
        }
    }

    private void calcDependencies() {
        try {
            this.deps = depUtils.calDepsAsGraph(funcs);
        } catch (AnalysisException e) {
            e.printStackTrace();
        }
        groupDeps();

    }

    private void groupDeps() {
        StrongConnectivityInspector<AFuncDeclIR, DefaultEdge> visitor = new StrongConnectivityInspector<>(deps);
        for (Set<AFuncDeclIR> scs : visitor.stronglyConnectedSets()) {
            if (scs.size() > 1) {
                AMrFuncGroupDeclIR aux = new AMrFuncGroupDeclIR();
                aux.setFuncs(new LinkedList<>(scs));
                // this line also removes the function from the functions block
                result.getDecls().add(aux);
            }
        }
    }

    @Override
    public AModuleDeclIR getResult() {
        return result;
    }

}
