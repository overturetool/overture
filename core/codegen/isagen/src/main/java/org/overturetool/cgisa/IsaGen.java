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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.velocity.Template;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.ast.modules.AModuleModules;
import org.overture.codegen.ir.*;
import org.overture.codegen.ir.declarations.AFuncDeclIR;
import org.overture.codegen.ir.declarations.AInterfaceDeclIR;
import org.overture.codegen.ir.declarations.AModuleDeclIR;
import org.overture.codegen.ir.declarations.ATypeDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.printer.MsgPrinter;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.codegen.vdm2java.IJavaConstants;
import org.overture.codegen.vdm2java.JavaCodeGen;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overturetool.cgisa.transformations.*;

/**
 * Main facade class for VDM 2 Isabelle IR
 *
 * @author ldc
 */
public class IsaGen extends CodeGenBase {


	public static Map<String, AFuncDeclIR> funcGenHistoryMap = new HashMap<>();
	public static Map<STypeIR, String> typeGenHistoryMap = new HashMap<>();
	public static Map<String, SDeclIR> declGenHistoryMap = new HashMap<>();
	private IsaSettings isaSettings;
	
    public IsaGen()
    {
        this.addInvTrueMacro();

        this.getSettings().setAddStateInvToModule(false);
        this.getSettings().setGenerateInvariants(true);
        
        isaSettings = new IsaSettings();
    }
    //TODO: Auto load files in macro directory
    public static void addInvTrueMacro(){
        StringBuilder sb = new StringBuilder("#macro ( invTrue $node )\n" +
                "    definition\n" +
                "        inv_$node.Name :: $node.Name \\<Rightarrow> \\<bool>\n" +
                "        where\n" +
                "        \"inv_$node.Name \\<equiv> inv_True\"\n" +
                "#end");
        addMacro("invTrue",new StringReader(sb.toString()));
        Template template = new Template();
    }

    public static void addMacro(String name, StringReader reader){
        try {
            Template template = new Template();
            RuntimeServices runtimeServices = RuntimeSingleton.getRuntimeServices();

            SimpleNode simpleNode = runtimeServices.parse(reader, name);
            template.setRuntimeServices(runtimeServices);
            template.setData(simpleNode);
            template.initDocument();
        } catch (ParseException e)
        {
            System.out.println("Failed with: " + e);
        }
    }

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
        // Typecheck the VDMToolkit module and generate the IR
        TypeCheckerUtil.TypeCheckResult<List<AModuleModules>> listTypeCheckResult1 =
                TypeCheckerUtil.typeCheckSl(new File("src/test/resources/VDMToolkit.vdmsl"));
        AModuleModules isaToolkit = listTypeCheckResult1.result.
                stream().
                filter(mod -> mod.getName().getName().equals("VDMToolkit")).
                findAny().
                orElseThrow(() -> new AnalysisException("Failed to find VDMToolkit module"));
        super.genIrStatus(statuses, isaToolkit);

        // Get the VDMToolkit module IR
        IRStatus<PIR> vdmToolkitIR = statuses.stream().filter(x -> x.getIrNodeName().equals("VDMToolkit")).findAny().orElseThrow(() -> new AnalysisException("Failed to find VDMToolkit IR node"));
        AModuleDeclIR vdmToolkitModuleIR = (AModuleDeclIR) vdmToolkitIR.getIrNode();


        GeneratedData r = new GeneratedData();
        try {


            // Apply transformations
            for (IRStatus<PIR> status : statuses) {
                if(status.getIrNodeName().equals("VDMToolkit")){
                    System.out.println("Skipping VDMToolkit transformations");
                } else {


                    // transform away any recursion cycles
                    GroupMutRecs groupMR = new GroupMutRecs();
                    generator.applyTotalTransformation(status, groupMR);
                    if (status.getIrNode() instanceof AModuleDeclIR) {
                        AModuleDeclIR cClass = (AModuleDeclIR) status.getIrNode();
                        
                        // then sort remaining dependencies
                        SortDependencies sortTrans = new SortDependencies(cClass.getDecls());
                        generator.applyPartialTransformation(status, sortTrans);
                    }
                    
                    // Transform all token types to isa_VDMToken
                    // Transform all nat types to isa_VDMNat
                    // Transform all nat1 types to isa_VDMNat
                    // Transform all int types to isa_VDMInt
                    IsaBasicTypesConv invConv = new IsaBasicTypesConv(getInfo(), this.transAssistant, vdmToolkitModuleIR);
                    generator.applyPartialTransformation(status, invConv);
                    
                    
                    // Transform Seq and Set types into isa_VDMSeq and isa_VDMSet
                    IsaTypeTypesConv invSSConv = new IsaTypeTypesConv(getInfo(), this.transAssistant, vdmToolkitModuleIR);
                    generator.applyPartialTransformation(status, invSSConv);
                    
                    
                    IsaInvGenTrans invTrans = new IsaInvGenTrans(getInfo(), vdmToolkitModuleIR);
                    generator.applyPartialTransformation(status, invTrans);
                    
                    IsaFuncDeclConv funcConv = new IsaFuncDeclConv(getInfo(), this.transAssistant, vdmToolkitModuleIR);
                    generator.applyPartialTransformation(status, funcConv);
                    
                    
                    
                }
            }
            printIR(statuses);
            r.setClasses(prettyPrint(statuses));
        } catch (org.overture.codegen.ir.analysis.AnalysisException e) {
            throw new AnalysisException(e);
        }
        return r;

    }

    private void printIR(List<IRStatus<PIR>> statuses) {
    	
    	new File("../isagen/target/generatedIRtext/").mkdirs();
		AModuleDeclIR decls = (AModuleDeclIR) statuses.get(0).getIrNode();
		
		for (int i = 0; i < decls.getDecls().size(); i++)
		{
			SDeclIR n = decls.getDecls().get(i).clone();
			
			PrintWriter writer = null;
			try {
				writer = new PrintWriter("../isagen/target/generatedIRtext/" + decls.getDecls().get(i).getClass().toString().substring(43)
						+ i + "_IR.txt", "UTF-8");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
	    	

			writer.println("Source Node : " + n.getSourceNode());
			writer.println("Parent Node : " + n.parent());
			
			//print children neatly
			List<String> keys = n.getChildren(true).keySet().stream().filter(k -> k != null && k != "_sourceNode").collect(Collectors.toList());
			writer.println("Children w/ inherited fields : ");
			for (int x = 0; x < keys.size(); x++)
			{
				if (keys.get(x) != null && n.getChildren(true).get(keys.get(x)) != null)				
					writer.println("---- " + keys.get(x) + " = " + n.getChildren(true).get(keys.get(x)).toString());
			}
			
			writer.println("Class : " + n.getClass());
	    	writer.close();
		}
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
            if(status.getIrNodeName().equals("VDMToolkit")){
                System.out.println("Skipping VDMToolkit transformations");
            } else {
                generated.add(prettyPrintNode(pp, status));
            }

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
    
    protected void setIsaSettings(IsaSettings s)
    {
    	isaSettings = s;
    }
    
	public void genIsaSourceFiles(File root,
			List<GeneratedModule> generatedClasses)
	{
		for (GeneratedModule classCg : generatedClasses)
		{
			if (classCg.canBeGenerated())
			{
				genIsaSourceFile(root, classCg);
			}
		}
	}

	public void genIsaSourceFile(File root, GeneratedModule generatedModule)
	{
		if (root == null)
		{
			MsgPrinter.getPrinter().error("Invalid file directory = null");
			return;
		}

		if (generatedModule != null && generatedModule.canBeGenerated()
				&& !generatedModule.hasMergeErrors())
		{
			String isaFileName = generatedModule.getName() + IIsaConstants.THY_FILE_EXTENSION;
			emitCode(root, isaFileName, generatedModule.getContent());
		}
	}
}
