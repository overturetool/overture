package org.overture.prettyprinter;

import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.PExp;
import org.overture.ast.node.INode;
import org.overture.codegen.ir.IRGenerator;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.IRSettings;
import org.overture.core.npp.IPrettyPrinter;
import org.overture.core.npp.ISymbolTable;
import org.overture.core.npp.IndentTracker;
import org.overture.core.npp.VdmSymbolTable;

public class RefactoringPrettyPrinter extends
QuestionAnswerAdaptor < IndentTracker, String > implements IPrettyPrinter {

    private static final String NODE_NOT_FOUND_ERROR = "ERROR: Node Not Found.";
    protected IRGenerator generator;

    ISymbolTable mytable;
    static ASTPrettyPrinter expPrinter;

    public static RefactoringPrettyPrinter newInstance() {
        return new RefactoringPrettyPrinter(VdmSymbolTable.getInstance());
    }

    public static String prettyPrint(INode nodes) throws AnalysisException {
        String s = nodes.apply(newInstance(), new IndentTracker());
        return s.replace("\t", "  ");
    }

    public RefactoringPrettyPrinter(ISymbolTable nsTable) {
        mytable = nsTable;
        this.generator = new IRGenerator();
        IRGenerator irGenerator = new IRGenerator();
		IRSettings irSettings = new IRSettings();
		irSettings.setCharSeqAsString(true);
		irSettings.setGeneratePreConds(false);
		irSettings.setGeneratePreCondChecks(false);
		irSettings.setGeneratePostConds(false);
		irSettings.setGeneratePostCondChecks(false);
		generator.getIRInfo().setSettings(irSettings);
		
        expPrinter = new ASTPrettyPrinter(this, nsTable,irGenerator.getIRInfo().getTcFactory(),getInfo().getIdStateDesignatorDefs());
    }

    @Override
    public void setInsTable(ISymbolTable it) {
        mytable = it;
    }


    @Override
    public String defaultPExp(PExp node, IndentTracker question) throws AnalysisException {
        return node.apply(expPrinter, question);
    }
    
    @Override
    public String defaultINode(INode node, IndentTracker question) throws AnalysisException {
    	return node.apply(expPrinter, question);
    }
    
    @Override
    public String createNewReturnValue(INode node, IndentTracker question)
    throws AnalysisException {
        return NODE_NOT_FOUND_ERROR;
    }

    @Override
    public String createNewReturnValue(Object node, IndentTracker question)
    throws AnalysisException {
        return NODE_NOT_FOUND_ERROR;
    }

	public static String prettyPrint(List<INode> userModules) {

        for (INode modules : userModules)
		{
			try {
				modules.apply(newInstance(), new IndentTracker());
			} catch (AnalysisException e) {
				e.printStackTrace();
			}
        	return expPrinter.getVDMText();
		}
		return null;
	}
	
	public IRInfo getInfo()
	{
		return generator.getIRInfo();
	}
}