package org.overture.codegen.vdm2java;

import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.AExplicitVarExpIR;
import org.overture.codegen.ir.expressions.AFieldExpIR;
import org.overture.codegen.ir.statements.APlainCallStmIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

import java.util.Arrays;
import java.util.List;

/**
 * This transformation assumes that all renaming have been resolved using the
 * {@link org.overture.codegen.trans.RenamedTrans}.
 */
public class UnsupportedLibWarningTrans extends DepthFirstAnalysisAdaptor {

    public static final List<String> IO_UNSUPPORTED = Arrays.asList(new String[]{"freadval", "ferror"});

    public static final List<String> VDMUTIL_UNSUPPORTED = Arrays.asList(new String[]{"get_file_pos", "seq_of_char2val"});
    public static final String UNSUPPORTED_MSG = "Unsupported code generation runtime library feature";

    private TransAssistantIR assist;

    public UnsupportedLibWarningTrans(TransAssistantIR assist)
    {
        this.assist = assist;
    }

    @Override
    public void caseAFieldExpIR(AFieldExpIR node) throws AnalysisException {

        // Examples new IO().ferror() or myIoObj.ferror()

        STypeIR objType = node.getObject().getType();

        if(objType instanceof AClassTypeIR)
        {
            AClassTypeIR classType = (AClassTypeIR) objType;

            String className = classType.getName();
            String member = node.getMemberName();

            if(isUnsupportedIO(className, member) || isUnsupportedVDMUtil(className, member)){
                warn(node);
            }
        }
    }

    @Override
    public void caseAExplicitVarExpIR(AExplicitVarExpIR node) throws AnalysisException {

        // Examples: VDMUtil`seq_of_char2val[N] or IO`printf
        STypeIR classType = node.getClassType();

        if(classType instanceof AClassTypeIR)
        {
            String className = ((AClassTypeIR) classType).getName();
            String fieldName = node.getName();

            if(isUnsupportedIO(className, fieldName) || isUnsupportedVDMUtil(className, fieldName))
            {
                warn(node);
            }
        }
    }

    @Override
    public void caseAPlainCallStmIR(APlainCallStmIR node) throws AnalysisException {

        // Example: IO`println(...)

        STypeIR classType = node.getClassType();

        if(classType instanceof AClassTypeIR)
        {
            String className = ((AClassTypeIR) classType).getName();
            String methodName = node.getName();

            // VDMUtil does not contain operations
            if(isUnsupportedIO(className, methodName)){

                warn(node);
            }
        }
    }

    public void warn(INode node) {
        assist.getInfo().addTransformationWarning(node, UNSUPPORTED_MSG);
    }

    public boolean isUnsupportedVDMUtil(String className, String fieldName) {
        return className.equals(IRConstants.VDMUTIL_LIB) && VDMUTIL_UNSUPPORTED.contains(fieldName);
    }

    public boolean isUnsupportedIO(String className, String fieldName) {
        return className.equals(IRConstants.IO_LIB) && IO_UNSUPPORTED.contains(fieldName);
    }
}
