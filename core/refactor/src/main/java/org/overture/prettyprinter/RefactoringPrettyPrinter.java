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
    /**
     * The attribute table for handling non abstract syntax such as separators.
     */
    ISymbolTable mytable;
    static ASTPrettyPrinter expPrinter;

    // PatternNpp
    // BindNpp...

    /**
     * Creates a VDM-syntax pretty printer. <br>
     * <b>Warning:</b> this method pre-loads {@link VdmSymbolTable} attributes. Extensions should use
     * {@link #NewPrettyPrinter(ISymbolTable)} and configure it instead.
     * 
     * @return a new instance of {@link RefactoringPrettyPrinter}
     */
    public static RefactoringPrettyPrinter newInstance() {
        return new RefactoringPrettyPrinter(VdmSymbolTable.getInstance());
    }

    public static String prettyPrint(INode nodes) throws AnalysisException {
        String s = nodes.apply(newInstance(), new IndentTracker());
        return s.replace("\t", "  ");
    }

    /**
     * Instantiates a new pretty printer for base ASTs.
     * 
     * @param nsTable
     *            the attributes table for the printer
     */
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

    /*
     * (non-Javadoc)
     * @see org.overture.core.npp.IPrettyPrinter#setInsTable(org.overture.core.npp .InsTable)
     */
    @
    Override
    public void setInsTable(ISymbolTable it) {
        mytable = it;
    }


    @
    Override
    public String defaultPExp(PExp node, IndentTracker question) throws AnalysisException {
        return node.apply(expPrinter, question);
    }
    @Override
    public String defaultINode(INode node, IndentTracker question) throws AnalysisException {
    	return node.apply(expPrinter, question);
    }
    @
    Override
    public String createNewReturnValue(INode node, IndentTracker question)
    throws AnalysisException {
        return NODE_NOT_FOUND_ERROR;
    }

    @
    Override
    public String createNewReturnValue(Object node, IndentTracker question)
    throws AnalysisException {
        return NODE_NOT_FOUND_ERROR;
    }

	public static String prettyPrint(List<INode> userModules) {

        for (INode modules : userModules)
		{
        	String s = "";
			try {
				s = modules.apply(newInstance(), new IndentTracker());
			} catch (AnalysisException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	return expPrinter.getVDMText();
		}
        
        //
        
		return null;
	}
	
	public IRInfo getInfo()
	{
		return generator.getIRInfo();
	}
}