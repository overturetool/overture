package org.overture.codegen.ir;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.velocity.app.Velocity;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.expressions.ANotYetSpecifiedExp;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.statements.ANotYetSpecifiedStm;	
import org.overture.ast.util.modules.ModuleList;
import org.overture.codegen.analysis.vdm.UnreachableStmRemover;
import org.overture.codegen.assistant.DeclAssistantIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.trans.BlockCleanupTrans;
import org.overture.codegen.trans.OldNameRenamer;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.utils.Generated;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.config.Settings;


/**
 * This class is a base class implementation of a code generator that is developed on top of the Code Generation
 * Platform (CGP). Code generators that want to translate VDM models into some target language can inherit from this
 * class.
 * 
 * @author pvj
 */
abstract public class CodeGenBase implements IREventCoordinator
{
	/**
	 * The {@link #generator} field is used for constructing and transforming the Intermediate Representation (IR)
	 * generated from the VDM AST.
	 */
	protected IRGenerator generator;
	
	/**
	 * The {@link #transAssistant} provides functionality that is convenient when implementing transformations.
	 */
	protected TransAssistantIR transAssistant;
	
	/**
	 * The {@link #irObserver} can receive and manipulate the initial and final versions of the IR via the
	 * {@link #initialIrEvent(List)} and {@link #finalIrEvent(List)} methods.
	 */
	protected IREventObserver irObserver;
	
	/**
	 * Constructs this code generator by initializing the template engine and constructing the {@link #generator}.
	 */
	protected CodeGenBase()
	{
		super();
		initVelocity();
		this.irObserver = null;
		this.generator = new IRGenerator();
		this.transAssistant = new TransAssistantIR(generator.getIRInfo());
	}
	
	/**
	 * This method clears the state of the {@link #generator}, which forms the first step of the code generation
	 * process. That is, this method is invoked upon entering {@link #generate(List)}.
	 */
	protected void clear()
	{
		generator.clear();
	}
	
	/**
	 * The entry point of this class. This method translates a VDM AST into target language code. Use of the template
	 * method design pattern ensures that the state of this code generator is properly initialized and that the VDM AST
	 * is pre-processed in accordance with the {@link #preProcessAst(List)} method.
	 * 
	 * @param ast
	 *            The VDM AST, which can be either VDM modules or classes.
	 * @return The data generated from the VDM AST.
	 * @throws AnalysisException
	 *             If a problem occurs during the construction of the IR.
	 */
	public GeneratedData generate(List<INode> ast) throws AnalysisException
	{
		clear();

		if(Settings.dialect == Dialect.VDM_SL)
		{
			ModuleList moduleList = new ModuleList(getModules(ast));
			moduleList.combineDefaults();
			ast = getNodes(moduleList);
		}
		
		preProcessAst(ast);

		List<IRStatus<PIR>> statuses = new LinkedList<>();

		for (INode node : ast)
		{
			genIrStatus(statuses, node);
		}

		return genVdmToTargetLang(statuses);
	}
	
	/**
	 * This method translates a VDM node into an IR status.
	 * 
	 * @param statuses
	 *            A list of previously generated IR statuses. The generated IR status will be added to this list.
	 * @param node
	 *            The VDM node from which we generate an IR status
	 * @throws AnalysisException
	 *             If something goes wrong during the construction of the IR status.
	 */
	protected void genIrStatus(
			List<IRStatus<PIR>> statuses,
			INode node) throws AnalysisException
	{
		IRStatus<PIR> status = generator.generateFrom(node);
		
		if(status != null)
		{
			statuses.add(status);
		}
	}
	
	/**
	 * Translates a list of IR statuses into target language code. This method must be implemented by all code
	 * generators that inherit from this class. This method is invoked by {@link #generate(List)}.
	 * 
	 * @param statuses
	 *            The IR statuses holding the nodes to be code generated.
	 * @return The generated code as well as information about the code generation process.
	 * @throws AnalysisException
	 *             If something goes wrong during the code generation process.
	 */
	abstract protected GeneratedData genVdmToTargetLang(List<IRStatus<PIR>> statuses) throws AnalysisException;
	
	/**
	 * This method pre-processes the VDM AST by<br>
	 * (1) computing a definition table that can be used during the analysis of the IR to determine if a VDM identifier
	 * state designator is local or not.<br>
	 * (2) Removing unreachable statements from the VDM AST <br>
	 * (3) Normalizing old names by replacing tilde signs '~' with underscores '_' <br>
	 * (4) Simplifying the standard libraries by cutting nodes that are not likely to be meaningful during the analysis
	 * of the VDM AST. Library classes are processed using {@link #simplifyLibrary(INode)}, whereas non-library modules
	 * or classes are processed using {@link #preProcessVdmUserClass(INode)}.
	 * 
	 * @param ast
	 *            The VDM AST subject to pre-processing.
	 * @throws AnalysisException
	 *             In case something goes wrong during the VDM AST analysis.
	 */
	protected void preProcessAst(List<INode> ast) throws AnalysisException
	{
		generator.computeDefTable(getUserModules(ast));
		removeUnreachableStms(ast);
		handleOldNames(ast);
		
		for (INode node : ast)
		{
			if (getInfo().getAssistantManager().getDeclAssistant().isLibrary(node))
			{
				simplifyLibrary(node);
			}
			else
			{
				preProcessVdmUserClass(node);
			}
		}
	}
	
	/**
	 * Initializes the Apache Velocity template engine.
	 */
	protected void initVelocity()
	{
		Velocity.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
		Velocity.init();
	}
	
	/**
	 * Formats the generated code. By default, this method simply returns the input unmodified.
	 * 
	 * @param code
	 *            The unformatted code.
	 * @return The formatted code.
	 */
	protected String formatCode(StringWriter code)
	{
		return code.toString();
	}
	
	
	/**
	 * Convenience method for extracting the VDM module definitions from a list of VDM nodes.
	 * 
	 * @param ast
	 *            A list of VDM nodes.
	 * @return The module definitions extracted from <code>ast</code>.
	 */
	public static List<AModuleModules> getModules(List<INode> ast)
	{
		List<AModuleModules> modules = new LinkedList<>();
		
		for(INode n : ast)
		{
			if(n instanceof AModuleModules)
			{
				modules.add((AModuleModules) n);
			}
		}
		
		return modules;
	}
	
	/**
	 * Convenience method for extracting the VDM class definitions from a list of VDM nodes.
	 * 
	 * @param ast
	 *            A list of VDM nodes.
	 * @return The class definitions extracted from <code>ast</code>.
	 */
	public static List<SClassDefinition> getClasses(List<INode> ast)
	{
		List<SClassDefinition> classes = new LinkedList<>();
		
		for(INode n : ast)
		{
			if(n instanceof SClassDefinition)
			{
				classes.add((SClassDefinition) n);
			}
		}
		
		return classes;
	}
	
	/**
	 * Convenience method for converting a VDM AST into a list of nodes.
	 * 
	 * @param ast
	 *            The VDM AST.
	 * @return The VDM AST as a list of nodes.
	 */
	public static List<INode> getNodes(List<? extends INode> ast)
	{
		List<INode> nodes = new LinkedList<>();
		
		nodes.addAll(ast);
		
		return nodes;
	}
	
	/**
	 * Applies the {@link #cleanup(IRStatus)} method to all the IR statuses.
	 * 
	 * @param statuses
	 *            The IR statuses
	 */
	public void cleanup(List<IRStatus<PIR>> statuses)
	{
		for (IRStatus<PIR> s : statuses)
		{
			cleanup(s);
		}
	}

	/**
	 * Cleans up an IR status by removing redundant {@link ABlockStmIR} nodes using the {@link BlockCleanupTrans}
	 * transformation.
	 * 
	 * @param status
	 *            The IR status
	 */
	public void cleanup(IRStatus<PIR> status)
	{
		if (status != null && status.getIrNode() != null)
		{
			try
			{
				status.getIrNode().apply(new BlockCleanupTrans());
			} catch (org.overture.codegen.ir.analysis.AnalysisException e)
			{
				e.printStackTrace();
				Logger.getLog().printErrorln("Problem encountered when trying to cleanup blocks in '"
						+ this.getClass().getSimpleName() + "'");
			}
		}
	}
	
	/**
	 * Given an IR status this method determines if it represents a test case or not.
	 * 
	 * @param status The IR status.
	 * @return True if the <code>status</code> represents a test case - false otherwise.
	 */
	protected boolean isTestCase(IRStatus<? extends PIR> status)
	{
		return getInfo().getDeclAssistant().isTestCase(status.getIrNode().getSourceNode().getVdmNode());
	}
	
	/**
	 * This method extracts the user modules or classes from a VDM AST.
	 * 
	 * @param ast The VDM AST.
	 * @return A list of user modules or classes.
	 */
	protected List<INode> getUserModules(List<? extends INode> ast)
	{
		List<INode> userModules = new LinkedList<INode>();

		for (INode node : ast)
		{
			if (!getInfo().getDeclAssistant().isLibrary(node))
			{
				userModules.add(node);
			}
		}

		return userModules;
	}
	
	/**
	 * This method removes unreachable statements from the VDM AST.
	 * 
	 * @param ast
	 *            The VDM AST subject to processing.
	 * @throws AnalysisException
	 *             If something goes wrong when trying to remove unreachable statements from the <code>ast</code>.
	 */
	protected void removeUnreachableStms(List<? extends INode> ast) throws AnalysisException
	{
		UnreachableStmRemover remover = new UnreachableStmRemover();

		for (INode node : ast)
		{
			node.apply(remover);
		}
	}
	
	/**
	 * Simplifies a VDM standard library class or module by removing sub-nodes that are likely not to be of interest
	 * during the generation of the IR.
	 * 
	 * @param module
	 *            A VDM class or module
	 */
	protected void simplifyLibrary(INode module)
	{
		List<PDefinition> defs = null;
		
		if(module instanceof SClassDefinition)
		{
			defs = ((SClassDefinition) module).getDefinitions();
		}
		else if(module instanceof AModuleModules)
		{
			defs = ((AModuleModules) module).getDefs();
		}
		else
		{
			// Nothing to do
			return;
		}
		
		for (PDefinition def : defs)
		{
			if (def instanceof SOperationDefinition)
			{
				SOperationDefinition op = (SOperationDefinition) def;

				op.setBody(new ANotYetSpecifiedStm());
				op.setPrecondition(null);
				op.setPostcondition(null);
			} else if (def instanceof SFunctionDefinition)
			{
				SFunctionDefinition func = (SFunctionDefinition) def;

				func.setBody(new ANotYetSpecifiedExp());
				func.setPrecondition(null);
				func.setPostcondition(null);
			}
		}
	}
	
	/**
	 * Processes old names by replacing the tilde sign '~' with an underscore.
	 * 
	 * @param vdmAst The VDM AST subject to processing.
	 * @throws AnalysisException If something goes wrong during the renaming process.
	 */
	protected void handleOldNames(List<? extends INode> vdmAst) throws AnalysisException
	{
		OldNameRenamer oldNameRenamer = new OldNameRenamer();
		
		for(INode module : vdmAst)
		{
			module.apply(oldNameRenamer);
		}
	}
	
	/**
	 * Given a list of IR statuses this method retrieves the names of the user-defined test cases.
	 * 
	 * @param statuses
	 *            The list of IR statuses from which the test case names are retrieved
	 * @return The names of the user-defined test cases.
	 */
	protected List<String> getUserTestCases(List<IRStatus<PIR>> statuses)
	{
		List<String> userTestCases = new LinkedList<>();
		
		for (IRStatus<PIR> s : statuses)
		{
			if(getInfo().getDeclAssistant().isTestCase(s.getVdmNode()))
			{
				userTestCases.add(getInfo().getDeclAssistant().getNodeName(s.getVdmNode()));
			}
		}
		
		return userTestCases;
	}
	
	/**
	 * Pre-processing of a user class. This method is invoked by {@link #preProcessAst(List)}. This method does nothing
	 * by default.
	 * 
	 * @param vdmModule
	 *            The user module or class.
	 */
	protected void preProcessVdmUserClass(INode vdmModule)
	{
	}
	
	/**
	 * Determines whether a VDM module or class should be code generated or not.
	 * 
	 * @param vdmNode
	 *            The node to be checked.
	 * @return True if <code>node</code> should be code generated - false otherwise.
	 */
	protected boolean shouldGenerateVdmNode(INode vdmNode)
	{
		DeclAssistantIR declAssistant = getInfo().getDeclAssistant();
		
		if (declAssistant.isLibrary(vdmNode))
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	/**
	 * This method translates an IR module or class into target language code.
	 * 
	 * @param mergeVisitor
	 *            The visitor that translates the IR module or class into target language code.
	 * @param status
	 *            The IR status that holds the IR node that we want to code generate.
	 * @return The generated code and data about what has been generated.
	 * @throws org.overture.codegen.ir.analysis.AnalysisException
	 *             If something goes wrong during the code generation process.
	 */
	protected GeneratedModule genIrModule(MergeVisitor mergeVisitor,
			IRStatus<? extends PIR> status) throws org.overture.codegen.ir.analysis.AnalysisException
	{
		if (status.canBeGenerated())
		{
			mergeVisitor.init();
			StringWriter writer = new StringWriter();
			status.getIrNode().apply(mergeVisitor, writer);

			boolean isTestCase = isTestCase(status);

			GeneratedModule generatedModule;
			if (mergeVisitor.hasMergeErrors())
			{
				generatedModule = new GeneratedModule(status.getIrNodeName(), status.getIrNode(), mergeVisitor.getMergeErrors(), isTestCase);
			} else if (mergeVisitor.hasUnsupportedTargLangNodes())
			{
				generatedModule = new GeneratedModule(status.getIrNodeName(), new HashSet<>(), mergeVisitor.getUnsupportedInTargLang(), isTestCase);
			} else
			{
				generatedModule = new GeneratedModule(status.getIrNodeName(), status.getIrNode(), formatCode(writer), isTestCase);
				generatedModule.setTransformationWarnings(status.getTransformationWarnings());
			}

			return generatedModule;
		} else
		{
			return new GeneratedModule(status.getIrNodeName(), status.getUnsupportedInIr(), new HashSet<>(), isTestCase(status));
		}
	}
	
	/**
	 * Translates an IR expression into target language code.
	 * 
	 * @param expStatus
	 *            The IR status that holds the expressions that we want to code generate.
	 * @param mergeVisitor
	 *            The visitor that translates the IR expression into target language code.
	 * @return The generated code and data about what has been generated.
	 * @throws org.overture.codegen.ir.analysis.AnalysisException
	 *             If something goes wrong during the code generation process.
	 */
	protected Generated genIrExp(IRStatus<SExpIR> expStatus, MergeVisitor mergeVisitor)
			throws org.overture.codegen.ir.analysis.AnalysisException
	{
		StringWriter writer = new StringWriter();
		SExpIR expCg = expStatus.getIrNode();

		if (expStatus.canBeGenerated())
		{
			mergeVisitor.init();
			
			expCg.apply(mergeVisitor, writer);

			if (mergeVisitor.hasMergeErrors())
			{
				return new Generated(mergeVisitor.getMergeErrors());
			} else if (mergeVisitor.hasUnsupportedTargLangNodes())
			{
				return new Generated(new HashSet<>(), mergeVisitor.getUnsupportedInTargLang());
			} else
			{
				String code = writer.toString();

				return new Generated(code);
			}
		} else
		{

			return new Generated(expStatus.getUnsupportedInIr(), new HashSet<>());
		}
	}

	/**
	 * Emits generated code to a file. The file will be encoded using UTF-8.
	 * 
	 * @param outputFolder The output folder that will store the generated code.
	 * @param fileName The name of the file that will store the generated code.
	 * @param code The generated code.
	 */
	public static void emitCode(File outputFolder, String fileName, String code)
	{
		emitCode(outputFolder, fileName, code, "UTF-8");
	}

	/**
	 * Emits generated code to a file. 
	 * 
	 * @param outputFolder outputFolder The output folder that will store the generated code.
	 * @param fileName The name of the file that will store the generated code.
	 * @param code The generated code.
	 * @param encoding The encoding to use for the generated code.
	 */
	public static void emitCode(File outputFolder, String fileName, String code, String encoding)
	{
		try
		{
			File javaFile = new File(outputFolder, File.separator + fileName);
			javaFile.getParentFile().mkdirs();
			javaFile.createNewFile();
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(javaFile, false), encoding));
			BufferedWriter out = new BufferedWriter(writer);
			out.write(code);
			out.close();

		} catch (IOException e)
		{
			Logger.getLog().printErrorln("Error when saving class file: " + fileName);
			e.printStackTrace();
		}
	}
	
	/**
	 * Registration of the {@link #irObserver}.
	 */
	@Override
	public void registerIrObs(IREventObserver obs)
	{
		if(obs != null && irObserver == null)
		{
			irObserver = obs;
		}
	}

	/**
	 * Unregistration of the {@link #irObserver}.
	 */
	@Override
	public void unregisterIrObs(IREventObserver obs)
	{
		if(obs != null && irObserver == obs)
		{
			irObserver = null;
		}
	}
	
	/**
	 * Notifies the {@link #irObserver} when the initial version of the IR has been constructed. This method allows the
	 * {@link #irObserver} to modify the initial version of the IR.
	 * 
	 * @param ast
	 *            The initial version of the IR.
	 * @return A possibly modified version of the initial IR.
	 */
	public List<IRStatus<PIR>> initialIrEvent(List<IRStatus<PIR>> ast)
	{
		if(irObserver != null)
		{
			return irObserver.initialIRConstructed(ast, getInfo());
		}
		
		return ast;
	}
	
	/**
	 * Notifies the {@link #irObserver} when the final version of the IR has been constructed. This method allows the
	 * {@link #irObserver} to modify the IR.
	 * 
	 * @param ast
	 *            The final version of the IR.
	 * @return A possibly modified version of the IR
	 */
	public List<IRStatus<PIR>> finalIrEvent(List<IRStatus<PIR>> ast)
	{
		if(irObserver != null)
		{
			return irObserver.finalIRConstructed(ast, getInfo());
		}
		
		return ast;
	}
	
	public void setIRGenerator(IRGenerator generator)
	{
		this.generator = generator;
	}

	public IRGenerator getIRGenerator()
	{
		return generator;
	}

	public void setSettings(IRSettings settings)
	{
		generator.getIRInfo().setSettings(settings);
	}
	
	public IRSettings getSettings()
	{
		return generator.getIRInfo().getSettings();
	}
	
	public IRInfo getInfo()
	{
		return generator.getIRInfo();
	}
	
	public void setTransAssistant(TransAssistantIR transAssistant)
	{
		this.transAssistant = transAssistant;
	}
	
	public TransAssistantIR getTransAssistant()
	{
		return transAssistant;
	}
}