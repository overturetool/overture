package org.overture.codegen.vdm2java;


import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.PType;
import org.overture.codegen.assistant.AssistantBase;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.name.ATypeNameIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.config.Settings;

import java.util.LinkedList;
import java.util.List;

/**
 * This transformation will rename a module M to M_Module in case it has a state definition named M.
 * This is done to avoid naming conflicts in the generated Java code.
 *
 * This transformation assumes that all renaming have been resolved using the
 * {@link org.overture.codegen.trans.RenamedTrans}.
 */
public class ModuleRenamerTrans extends DepthFirstAnalysisAdaptor {

    private static final String CLASS_SUFFIX = "_Module";

    private List<String> modulesToRename;

    private TransAssistantIR assist;

    public ModuleRenamerTrans(TransAssistantIR assist)
    {
        this.assist = assist;
    }

    public void init() {

        // Build a list of all modules that have the same name as their state definition.
        if(modulesToRename != null)
        {
            return;
        }
        else
        {
            modulesToRename = new LinkedList<>();
        }

        for(SClassDeclIR clazz : assist.getInfo().getClasses())
        {
            INode vdmNode = AssistantBase.getVdmNode(clazz);

            if(vdmNode instanceof AModuleModules)
            {
                AModuleModules module = (AModuleModules) vdmNode;

                for(PDefinition def : module.getDefs())
                {
                    String name = module.getName().getName();
                    if(def instanceof AStateDefinition)
                    {
                        if(name.equals(def.getName().getName()))
                        {
                            modulesToRename.add(name);
                        }

                        break;
                    }
                    else if(def instanceof ATypeDefinition)
                    {
                        ATypeDefinition typeDef = (ATypeDefinition) def;

                        PType type = typeDef.getType();

                        if(type instanceof ARecordInvariantType)
                        {
                            String recName = ((ARecordInvariantType) type).getName().getName();

                            if(recName.equals(name))
                            {
                                modulesToRename.add(name);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void defaultInSClassDeclIR(SClassDeclIR node) throws AnalysisException {

        if(Settings.dialect != Dialect.VDM_SL)
        {
            return;
        }

        init();

        super.defaultInSClassDeclIR(node);

        String name = node.getName();

        if(mustRename(name))
        {
            node.setName(consNewName(name));
        }
    }

    @Override
    public void caseAMethodDeclIR(AMethodDeclIR node) throws AnalysisException {

        super.caseAMethodDeclIR(node);

        String name = node.getName();
        if(node.getIsConstructor() && mustRename(name))
        {
            node.setName(consNewName(name));
        }
    }

    @Override
    public void caseATypeNameIR(ATypeNameIR node) throws AnalysisException {

        String definingClass = node.getDefiningClass();

        if(mustRename(definingClass))
        {
            node.setDefiningClass(consNewName(definingClass));
        }
    }

    @Override
    public void caseAClassTypeIR(AClassTypeIR node) throws AnalysisException {

        String name = node.getName();

        if(mustRename(name))
        {
            node.setName(consNewName(name));
        }
    }

    private boolean mustRename(String name)
    {
        return modulesToRename != null &&  modulesToRename.contains(name);
    }

    private String consNewName(String oldName)
    {
        return oldName + CLASS_SUFFIX;
    }
}
