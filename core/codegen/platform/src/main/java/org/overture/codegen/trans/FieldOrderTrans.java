package org.overture.codegen.trans;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.node.INode;
import org.overture.codegen.assistant.AssistantBase;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.expressions.AExplicitVarExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.SVarExpIR;
import org.overture.codegen.ir.types.AClassTypeIR;

import java.util.*;

public class FieldOrderTrans extends DepthFirstAnalysisAdaptor {

    static class VarExpFinder extends DepthFirstAnalysisAdaptor
    {
        private List<SVarExpIR> vars;

        private String className;

        private VarExpFinder(String className)
        {
            this.vars = new LinkedList<>();
            this.className = className;
        }

        public static List<SVarExpIR> findOccurences(String className, SExpIR root) throws AnalysisException {

            VarExpFinder finder = new VarExpFinder(className);

            root.apply(finder);

            return finder.vars;
        }

        @Override
        public void caseAIdentifierVarExpIR(AIdentifierVarExpIR node) throws AnalysisException {

            INode var = AssistantBase.getVdmNode(node);

            if(var instanceof AVariableExp) {
                PDefinition def = ((AVariableExp) var).getVardef();

                if (def instanceof ARenamedDefinition) {

                    return;
                }
            }

            vars.add(node);
        }

        @Override
        public void caseAExplicitVarExpIR(AExplicitVarExpIR node) throws AnalysisException {

            STypeIR classType = node.getClassType();

            if(classType instanceof AClassTypeIR)
            {
                String fieldClassName = ((AClassTypeIR) classType).getName();

                if(className.equals(fieldClassName))
                {
                    vars.add(node);
                }
            }
        }
    }

    @Override
    public void caseADefaultClassDeclIR(ADefaultClassDeclIR node) throws AnalysisException {

        final String className = node.getName();

        final LinkedList<AFieldDeclIR> fields = node.getFields();

        Map<String, AFieldDeclIR> fieldMap = new HashMap<>();
        DefaultDirectedGraph<AFieldDeclIR, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);

        for(AFieldDeclIR f : fields)
        {
            fieldMap.put(f.getName(), f);
            graph.addVertex(f);
        }

        boolean hasDependencies = false;

        for(AFieldDeclIR f : fields)
        {
            List<AFieldDeclIR> dependencies = findDependencies(className, f, fieldMap);

            hasDependencies = hasDependencies || !dependencies.isEmpty();

            for(AFieldDeclIR d : dependencies)
            {
                graph.addEdge(f, d);
            }
        }

        if(!hasDependencies)
        {
            return;
        }

        List<AFieldDeclIR> ordered = new LinkedList<>();

        TopologicalOrderIterator<AFieldDeclIR, DefaultEdge> ite = new TopologicalOrderIterator(graph, new Comparator<AFieldDeclIR>() {
            @Override
            public int compare(AFieldDeclIR f1, AFieldDeclIR f2) {

                // Try to maintain the original order
                return fields.indexOf(f2) - fields.indexOf(f1);
            }
        });

        while(ite.hasNext())
        {
            AFieldDeclIR next = ite.next();
            ordered.add(next);
        }

        Collections.reverse(ordered);

        node.setFields(ordered);
    }

    private List<AFieldDeclIR> findDependencies(String className, AFieldDeclIR f, Map<String, AFieldDeclIR> fieldMap) throws AnalysisException {

        List<SVarExpIR> varExps = VarExpFinder.findOccurences(className, f.getInitial());

        List<AFieldDeclIR> dependencies = new LinkedList<>();

        for(SVarExpIR v : varExps)
        {
            AFieldDeclIR field = fieldMap.get(v.getName());
            if(field != null)
            {
                dependencies.add(field);
            }
        }

        return dependencies;
    }
}
