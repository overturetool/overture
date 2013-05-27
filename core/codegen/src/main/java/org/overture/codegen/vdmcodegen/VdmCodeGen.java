package org.overture.codegen.vdmcodegen;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.velocity.app.Velocity;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.node.INode;
import org.overture.codegen.cgast.AClassTypeDeclCG;
import org.overture.codegen.logging.DefaultLogger;
import org.overture.codegen.logging.ILogger;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.visitor.CodeGenVisitor;

public class VdmCodeGen
{
	private ILogger log;
	
	public VdmCodeGen()
	{
		init(null);
	}
	
	public VdmCodeGen(ILogger log)
	{
		init(log);
	}
	
	private void init(ILogger log)
	{
		initVelocity();
		
		if(log == null)
			this.log = new DefaultLogger();
		else
			this.log = log;	
	}


	private void initVelocity()
	{
		String propertyPath = VdmCodeGenUtil.getVelocityPropertiesPath("velocity.properties");
		Velocity.init(propertyPath);
	}

	public void generateCode(
			List<SClassDefinition> mergedParseLists) throws AnalysisException
	{
		CodeGenVisitor codeGenVisitor = new CodeGenVisitor(log);

		for (INode node : mergedParseLists)
		{
			node.apply(codeGenVisitor);
		}

		ArrayList<AClassTypeDeclCG> classes = codeGenVisitor.getClasses();
		MergeVisitor mergeVisitor = new MergeVisitor();
		
		for (AClassTypeDeclCG classCg : classes)
		{
			try
			{
				StringWriter writer = new StringWriter();
				classCg.apply(mergeVisitor, writer);
				String code = writer.toString();

				// take default Eclipse formatting options
				@SuppressWarnings("unchecked")
				Map<String, String> options = DefaultCodeFormatterConstants.getEclipseDefaultSettings();
				
				// initialize the compiler settings to be able to format 1.5 code
				options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_5);
				options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_5);
				options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_5);
				
				// change the option to wrap each enum constant on a new line
				options.put(
					DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_ENUM_CONSTANTS,
					DefaultCodeFormatterConstants.createAlignmentValue(
						true,
						DefaultCodeFormatterConstants.WRAP_ONE_PER_LINE,
						DefaultCodeFormatterConstants.INDENT_ON_COLUMN));
				
				CodeFormatter codeFormatter = ToolFactory.createCodeFormatter(options);
				
				TextEdit textEdit = codeFormatter.format(CodeFormatter.K_COMPILATION_UNIT, code, 0, code.length(), 0, null);
				IDocument doc = new Document(code);
				try {
					textEdit.apply(doc);
					System.out.println(doc.get());
				} catch (MalformedTreeException e) {
					e.printStackTrace();
				} catch (BadLocationException e)
				{
					e.printStackTrace();
				}
				
			} catch (org.overture.codegen.cgast.analysis.AnalysisException e)
			{
				e.printStackTrace();
			} 
		}
	}
	
//	public void save(CodeGenContextMap contextMap)
//	{
//
		// java.net.URI absolutePath = vdmProject.getModelBuildPath().getOutput().getLocationURI();//
		// iFile.getLocationURI();
		// URL url;
		// try
		// {
		// url = FileLocator.toFileURL(absolutePath.toURL());
		// File file = new File(url.toURI());
		// } catch (IOException e)
		// {
		// e.printStackTrace();
		// } catch (URISyntaxException e)
		// {
		// e.printStackTrace();
		// }

		// ********** COMMENT BELOW BACK IN:

//		Set<String> set = contextMap.getContextKeys();
//
//		Template template = Vdm2CppUtil.getTemplate("class.vm");
//
//		if (template == null)
//		{
//			return;
//		}
//		PrintWriter out = new PrintWriter(System.out);
//		for (String classDef : set)
//		{
//			template.merge(contextMap.getContext(classDef).getVelocityContext(), out);
//			out.flush();
//			out.println();
//		}
//	}
	
}
