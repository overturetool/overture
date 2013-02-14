package org.overturetool.proofsupport;

import java.util.ArrayList;
import java.util.List;

import org.overturetool.proofsupport.external_tools.Utilities;

public class Proof {

    private static final String VDM_ADDITIONAL_TAC = "VDM_ADDITIONAL_TAC";
    private static final String VDM_GENERIC_TAC = "VDM_GENERIC_TAC";

    private static final String proofCommand = "can TAC_PROOF(([]:(term list), ``%1$s``), %2$s);";
    protected String theory = null;
    protected ArrayList<String> holProofExpressions = null;
    protected ArrayList<String> vdmProofExpressions = null;

    public Proof(String theory, List<String> holProofExpressions, List<String> vdmProofExpressions) {
        this.theory = theory.trim() + Utilities.LINE_SEPARATOR;
        this.holProofExpressions = new ArrayList<String>(holProofExpressions);
        this.vdmProofExpressions = new ArrayList<String>(vdmProofExpressions);
    }

    public String getProofCommand(int index) {
        String predicate = holProofExpressions.get(index);
        return buildProofCommand(predicate);
    }

    public String getProofExpression(int index) {
        return vdmProofExpressions.get(index);
    }

    private String buildProofCommand(String predicate) {
        // TODO: replace VDM_GEENRIC_TAC by VDM_ADDITIONAL_TAC
        //       when some kind of timer is in place to make the proofs timeout
        //       if needed.
        return String.format(proofCommand, predicate, VDM_GENERIC_TAC);
    }

    public int proofCommandsSize() {
        return holProofExpressions.size();
    }

    public String getTheory() {
        return theory;
    }

    public String getProofScript() {
        StringBuffer sb = new StringBuffer(theory);
        for (String predicate : holProofExpressions)
            sb.append(buildProofCommand(predicate)).append(Utilities.LINE_SEPARATOR);
        return sb.toString();
    }

    @Override
    public String toString() {
        return getTheory();
    }

}
