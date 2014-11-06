/**
 * 
 */
package org.overture.codegen.trans.conc;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AInterfaceDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.APersyncDeclCG;
import org.overture.codegen.cgast.expressions.ABoolLiteralExpCG;
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG;
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.AIntLiteralExpCG;
import org.overture.codegen.cgast.expressions.ANewExpCG;
import org.overture.codegen.cgast.expressions.ASelfExpCG;
import org.overture.codegen.cgast.name.ATypeNameCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.AElseIfStmCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.statements.ALocalAssignmentStmCG;
import org.overture.codegen.cgast.statements.APlainCallStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.cgast.statements.ATryStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AExternalTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.cgast.types.AVoidTypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.vdm2java.JavaFormat;

/**
 * @author gkanos
 *
 */
public class MainClassConcTransformation extends DepthFirstAnalysisAdaptor
{
	private IRInfo info;
	//private List<AClassDeclCG> classes;

	public MainClassConcTransformation(IRInfo info, List<AClassDeclCG> classes)
	{
		this.info = info;
		//this.classes = classes;
	}
	
	@Override
	public void caseAClassDeclCG(AClassDeclCG node) throws AnalysisException
	{
		if(!info.getSettings().generateConc())
		{
			return;
		}
		
		for(AFieldDeclCG fieldCG : node.getFields())
		{
			fieldCG.setVolatile(true);
		}
		
		AInterfaceDeclCG interf = new AInterfaceDeclCG();
		interf.setName("EvaluatePP");
		
		node.getInterfaces().add(interf);
		
		
		AExternalTypeCG sentType = new AExternalTypeCG();
		sentType.setName("Sentinel");
		AFieldDeclCG sentinelfld = new AFieldDeclCG();
		sentinelfld.setName("sentinel");
		sentinelfld.setType(sentType);
		sentinelfld.setAccess(JavaFormat.JAVA_PUBLIC);
		sentinelfld.setVolatile(true);
		sentinelfld.setStatic(false);
		
		node.getFields().add(sentinelfld);
		
		
		for(AMethodDeclCG methodCG : node.getMethods())
		{
			
			if(methodCG.getStatic() != null && !methodCG.getName().contains("cg_init_")){
				if(!methodCG.getIsConstructor()){//(x.getName() != node.getName()){
					if (!methodCG.getName().equals("toString") && !methodCG.getStatic() ){//&& !methodCG.getName().equals("Run")){//x.getName() != "toString"){
						ABlockStmCG bodyStm = new ABlockStmCG();

						APlainCallStmCG entering = new APlainCallStmCG();
						APlainCallStmCG leaving = new APlainCallStmCG();


						entering.setName("entering");
						AClassTypeCG sentinel = new AClassTypeCG();
						sentinel.setName("sentinel");

						entering.setClassType(sentinel);
						entering.setType(new AVoidTypeCG());

						AFieldExpCG field = new AFieldExpCG();
						field.setMemberName(methodCG.getName());

						ACastUnaryExpCG cast = new ACastUnaryExpCG();
						AIdentifierVarExpCG varSentinel = new AIdentifierVarExpCG();
						varSentinel.setOriginal("sentinel");

						AExternalTypeCG etype = new AExternalTypeCG();
						etype.setName(node.getName()+"_sentinel");

						cast.setExp(varSentinel);
						cast.setType(etype);
						field.setObject(cast);


						entering.getArgs().add(field);

						leaving.setName("leaving");
						leaving.setClassType(sentinel.clone());
						leaving.setType(new AVoidTypeCG());
						leaving.getArgs().add(field.clone());

						bodyStm.getStatements().add(entering);
						ATryStmCG trystm = new ATryStmCG();
						trystm.setStm(methodCG.getBody());
						trystm.setFinally(leaving);
						bodyStm.getStatements().add(trystm);

						methodCG.setBody(bodyStm);
					}
				}
				//else
				
			}
			if(methodCG.getIsConstructor())
			{
				ABlockStmCG bodyConst = new ABlockStmCG();

				ALocalAssignmentStmCG stm = new ALocalAssignmentStmCG();

				AIdentifierVarExpCG field = new AIdentifierVarExpCG();

				field.setOriginal("sentinel");

				ANewExpCG newexp = new ANewExpCG();

				ATypeNameCG classtype = new ATypeNameCG();
				classtype.setName(node.getName()+"_sentinel");

				newexp.setName(classtype);
				newexp.getArgs().add(new ASelfExpCG());

				stm.setExp(newexp);
				stm.setTarget(field);

				bodyConst.getStatements().add(methodCG.getBody());
				bodyConst.getStatements().add(stm);

				methodCG.setBody(bodyConst);


				//}
			}
		}
		//declaration of the method.
		
		AIntNumericBasicTypeCG fnr = new AIntNumericBasicTypeCG();
		AIdentifierPatternCG identifier = new AIdentifierPatternCG();
		identifier.setName("fnr");
		AFormalParamLocalParamCG fnrloc = new AFormalParamLocalParamCG();
		fnrloc.setType(fnr);
		fnrloc.setPattern(identifier);
		AMethodTypeCG methType = new AMethodTypeCG();
		methType.setResult(new ABoolBasicTypeCG());
		
		AMethodDeclCG evaluatePPmethod = new AMethodDeclCG();
		evaluatePPmethod.setAccess(JavaFormat.JAVA_PUBLIC);
		evaluatePPmethod.setName("evaluatePP");
		evaluatePPmethod.setMethodType(methType);
		
		evaluatePPmethod.getFormalParams().add(fnrloc);
		
		//Body of the method.
		if (node.getMethods().size() != 0){
			
			//fixing the overloaded operation problem
			@SuppressWarnings("unchecked")
			LinkedList<AMethodDeclCG> classuniqueMethods = (LinkedList<AMethodDeclCG>) node.getMethods().clone();
		
			classuniqueMethods.clear();
			for(AMethodDeclCG method : node.getMethods())
			{
				
				if(!classuniqueMethods.contains(method))
				{
					classuniqueMethods.add(method);
				}

			}
			
			AIfStmCG bodyif = new AIfStmCG();
			for(int i=0; i < classuniqueMethods.size(); i++)
			{
				
				AIdentifierVarExpCG testVar = new AIdentifierVarExpCG();
				testVar.setOriginal("fnr");
				testVar.setType(new AIntNumericBasicTypeCG());
				
				if (i == 0){
				
						AEqualsBinaryExpCG firstBranch = new AEqualsBinaryExpCG();
						
						AIntLiteralExpCG methNum =  new AIntLiteralExpCG();
						methNum.setValue((long) i);
						
						firstBranch.setLeft(testVar);
						firstBranch.setRight(methNum);
						
						AReturnStmCG ret = new AReturnStmCG();
						ABoolLiteralExpCG boolret = new ABoolLiteralExpCG();
						boolret.setValue(true);
						ret.setExp(boolret);
						
						for (APersyncDeclCG per : node.getPerSyncs()){
							if(per.getOpname().equals(classuniqueMethods.get(i).getName())){
								ret.setExp(per.getPred());
							}
						}
						bodyif.setIfExp(firstBranch);
						bodyif.setThenStm(ret);
					}
				//}
				else
				{
					AReturnStmCG ret = new AReturnStmCG();
					ABoolLiteralExpCG boolret = new ABoolLiteralExpCG();
					boolret.setValue(true);
					ret.setExp(boolret);
					
					for (APersyncDeclCG per : node.getPerSyncs()){
						if(per.getOpname().equals(classuniqueMethods.get(i).getName())){						
								ret.setExp(per.getPred());
						}
					}					
					AElseIfStmCG newBranch = new AElseIfStmCG();
																				
					AEqualsBinaryExpCG Branches = new AEqualsBinaryExpCG();
					
					AIntLiteralExpCG methNum =  new AIntLiteralExpCG();
					methNum.setValue((long) i);
					
					Branches.setLeft(testVar);
					Branches.setRight(methNum);
					
					newBranch.setElseIf(Branches);
					newBranch.setThenStm(ret.clone());
					
					bodyif.getElseIf().add(newBranch);
				}
			}
			AReturnStmCG ret = new AReturnStmCG();
			
			ABoolLiteralExpCG defaultPer = new ABoolLiteralExpCG();
			defaultPer.setValue(true);
			
			ret.setExp(defaultPer);
			
			bodyif.setElseStm(ret);
			evaluatePPmethod.setBody(bodyif);
		}
		
		node.getMethods().add(evaluatePPmethod);
	}
}
