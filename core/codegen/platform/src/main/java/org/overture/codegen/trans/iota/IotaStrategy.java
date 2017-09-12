package org.overture.codegen.trans.iota;

import org.apache.log4j.Logger;
import org.overture.ast.expressions.AIotaExp;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.codegen.ir.*;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.*;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.*;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.AErrorTypeIR;
import org.overture.codegen.ir.types.AIntNumericBasicTypeIR;
import org.overture.codegen.ir.types.ANatNumericBasicTypeIR;
import org.overture.codegen.trans.AbstractIterationStrategy;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.trans.iterator.ILanguageIterator;
import org.overture.codegen.trans.quantifier.CounterData;

import java.util.LinkedList;
import java.util.List;

public class IotaStrategy  extends AbstractIterationStrategy {

    protected Logger log = Logger.getLogger(this.getClass().getName());

    protected String resultVarName;
    protected String counterName;
    protected SExpIR predicate;

    protected static final String MULTIPLE_RESULTS_ERROR_MSG = "Iota selects more than one result";
    protected static final String NO_RESULT_ERROR_MSG = "Iota does not select a result";

    protected CounterData counterData;

    public IotaStrategy(TransAssistantIR transformationAssistant, SExpIR predicate, String resultVarName, String counterName, ILanguageIterator langIterator, ITempVarGen tempGen, IterationVarPrefixes iteVarPrefixes, CounterData counterData) {
        super(transformationAssistant, langIterator, tempGen, iteVarPrefixes);
        this.resultVarName = resultVarName;
        this.counterName = counterName;
        this.predicate = predicate;
        this.counterData = counterData;
    }

    @Override
    public List<AVarDeclIR> getOuterBlockDecls(AIdentifierVarExpIR setVar,
                                               List<SPatternIR> patterns) throws AnalysisException
    {
        if (firstBind)
        {
            IRInfo info = transAssist.getInfo();

            AIdentifierPatternIR resId = new AIdentifierPatternIR();
            resId.setName(resultVarName);

            List<AVarDeclIR> decls = new LinkedList<>();

            STypeIR elemType = transAssist.getElementType(setVar.getType());

            AUndefinedExpIR initExp = info.getExpAssistant().consUndefinedExp();
            decls.add(info.getDeclAssistant().consLocalVarDecl(elemType, resId, initExp));

            AIdentifierPatternIR countId = new AIdentifierPatternIR();
            countId.setName(counterName);

            decls.add(consCounterDecl(info, countId));

            return decls;
        } else
        {
            return null;
        }
    }

    public AVarDeclIR consCounterDecl(IRInfo info, AIdentifierPatternIR countId) {

        return transAssist.getInfo().getDeclAssistant().consLocalVarDecl(counterData.getType().clone(), transAssist.getInfo().getPatternAssistant().consIdPattern(counterName), counterData.getExp().clone());
    }

    @Override
    public List<SStmIR> getForLoopStms(AIdentifierVarExpIR setVar,
                                       List<SPatternIR> patterns, SPatternIR pattern)
    {
        if(lastBind)
        {
            AIdentifierVarExpIR col = new AIdentifierVarExpIR();
            col.setType(new AIntNumericBasicTypeIR());
            col.setIsLambda(false);
            col.setIsLocal(true);
            col.setName(counterName);

            AIncrementStmIR inc = new AIncrementStmIR();
            inc.setVar(col);

            AGreaterNumericBinaryExpIR tooManyMatches = new AGreaterNumericBinaryExpIR();
            tooManyMatches.setType(new ABoolBasicTypeIR());
            tooManyMatches.setLeft(transAssist.getInfo().getExpAssistant().consIdVar(counterName, new ANatNumericBasicTypeIR()));
            tooManyMatches.setRight(transAssist.getInfo().getExpAssistant().consIntLiteral(1));

            String name = null;
            if(pattern instanceof AIdentifierPatternIR)
            {
                name = ((AIdentifierPatternIR) pattern).getName();
            }
            else
            {
                log.error("Expected pattern to be an identifier at this point. Got: " + pattern);
            }

            AAssignToExpStmIR resultAssign = new AAssignToExpStmIR();
            STypeIR elementType = transAssist.getElementType(setVar.getType());
            resultAssign.setTarget(transAssist.getInfo().getExpAssistant().consIdVar(resultVarName, elementType));
            resultAssign.setExp(transAssist.getInfo().getExpAssistant().consIdVar(name, elementType.clone()));

            AIfStmIR matchesCheck = new AIfStmIR();
            matchesCheck.setIfExp(tooManyMatches);
            matchesCheck.setThenStm(consIotaMultipleResultsError());
            matchesCheck.setElseStm(resultAssign);

            ABlockStmIR outerThen = new ABlockStmIR();
            outerThen.getStatements().add(inc);
            outerThen.getStatements().add(matchesCheck);

            AIfStmIR outerIf = new AIfStmIR();
            outerIf.setIfExp(predicate);
            outerIf.setThenStm(outerThen);

            return packStm(outerIf);
        }
        else
        {
            return null;
        }
    }

    @Override
    public List<SStmIR> getPostOuterBlockStms(AIdentifierVarExpIR setVar,
                                              List<SPatternIR> patterns)
    {
        AEqualsBinaryExpIR noMatches = new AEqualsBinaryExpIR();
        noMatches.setType(new ABoolBasicTypeIR());
        noMatches.setLeft(transAssist.getInfo().getExpAssistant().consIdVar(counterName, new AIntNumericBasicTypeIR()));
        noMatches.setRight(transAssist.getInfo().getExpAssistant().consIntLiteral(0));

        AIfStmIR ifStm = new AIfStmIR();
        ifStm.setIfExp(noMatches);
        ifStm.setThenStm(consIotaMultipleResultsError());

        return packStm(ifStm);
    }

    public SStmIR consIotaNoMatchError()
    {
        return consIotaError(NO_RESULT_ERROR_MSG);
    }

    public SStmIR consIotaMultipleResultsError()
    {
        return consIotaError(MULTIPLE_RESULTS_ERROR_MSG);
    }

    public SStmIR consIotaError(String msg)
    {
        AIotaRuntimeErrorExpIR error = new AIotaRuntimeErrorExpIR();
        error.setType(new AErrorTypeIR());
        error.setMessage(msg);

        ARaiseErrorStmIR raise = new ARaiseErrorStmIR();
        raise.setError(error);

        return raise;
    }
}
