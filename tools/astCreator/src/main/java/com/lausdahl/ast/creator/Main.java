package com.lausdahl.ast.creator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import com.lausdahl.ast.creator.definitions.CommonTreeClassDefinition;
import com.lausdahl.ast.creator.definitions.CustomClassDefinition;
import com.lausdahl.ast.creator.definitions.GenericArgumentedIInterfceDefinition;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.IClassDefinition.ClassType;
import com.lausdahl.ast.creator.definitions.IInterfaceDefinition;
import com.lausdahl.ast.creator.methods.Method;
import com.lausdahl.ast.creator.methods.analysis.CopyNode2ExtendedNode;
import com.lausdahl.ast.creator.methods.analysis.CopyNode2ExtendedNodeListHelper;

public class Main
{
	/**
	 * Set this to false to generate the overture II AST
	 */
	public static final boolean test = false;

	public static final boolean extend = false;

	private static final String INPUT_FILENAME_OVERTURE_II = "..\\..\\core\\ast\\src\\main\\resources\\overtureII.astv2";
	private static final String INPUT_FILENAME = "src\\main\\resources\\testdata\\test.astV2";
	private static final String INPUT_FILENAME2 = "src\\main\\resources\\testdata\\testExtended.astV2";
	// private static final String ANALYSIS_PACKAGE_NAME = "org.overture.ast.analysis";
	private static File generated = new File("..\\..\\ast\\src\\");

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		if (!test)
		{
			System.out.println("Running with overture II");
			generated = new File("..\\..\\core\\ast\\src\\main\\java\\");
			System.out.println("Generator starting with input: "
					+ INPUT_FILENAME_OVERTURE_II);
			String defaultPackage = "org.overture.ast.node";
			String analysisPackage = "org.overture.ast.analysis";
			Environment env1 = create(INPUT_FILENAME_OVERTURE_II, defaultPackage, analysisPackage, "", generated, true);
			System.out.println("\n\nGenerator completed with "
					+ env1.getAllDefinitions().size() + " generated files.\n\n");
		} else
		{
			System.out.println("TESTING...");
			generated = new File("..\\..\\astTest\\src\\");
			System.out.println("Generator starting with input: "
					+ INPUT_FILENAME);
			String defaultPackage = "org.overture.ast.node";
			String analysisPackage = "org.overture.ast.analysis";
			Environment env1 = create(INPUT_FILENAME, defaultPackage, analysisPackage, "", generated, true);

			if (extend)
			{
				defaultPackage = "org.overture.interpreter.ast.node";
				analysisPackage = "org.overture.interpreter.ast.analysis";
				String extendName = "Interpreter";
				Environment env2 = create(INPUT_FILENAME2, defaultPackage, analysisPackage, extendName, generated, true);

				createCopyAdaptor(env1, env2, defaultPackage, extendName, generated);
			}
			System.out.println("TESTING...DONE.");
		}

	}

	public static Environment create(String inputFile, String defaultPackage,
			String analysisPackage, String extendName, File outputBase,
			boolean write) throws IOException, InstantiationException,
			IllegalAccessException
	{
		Environment env = new Generator().generate(inputFile, defaultPackage, analysisPackage);

		String namePostfix = extendName == null ? "" : extendName;
		for (IInterfaceDefinition def : env.getAllDefinitions())
		{
			def.setNamePostfix(namePostfix);
		}
		if (write)
		{
			SourceFileWriter.write(outputBase, env, defaultPackage, analysisPackage);
		}
		return env;
	}

	public static void createCopyAdaptor(Environment source,
			Environment destination, String defaultPackage, String namePostfix,
			File outputFolder) throws Exception
	{
		List<Method> methods = new Vector<Method>();
		for (CommonTreeClassDefinition c : Generator.getClasses(source.getClasses()))
		{
			if (c.getType() == IClassDefinition.ClassType.Production
					|| c.getType() == ClassType.SubProduction)
			{
				continue;
			}
			IClassDefinition destDef = null;
			for (IClassDefinition def : destination.getClasses())
			{
				if (def.getName().replace(def.getNamePostfix(), "").equals(c.getName()))
				{
					destDef = def;
				}
			}
			if (destDef == null)
			{
				System.err.println("Source class: " + c.getName()
						+ " has no match in target environment.");
				System.err.println("Target Environment:");
				System.err.println(destination);
				throw new Exception("Tree match error on copy");
			}
			Method m = new CopyNode2ExtendedNode(c, destDef, source, destination);
			m.setClassDefinition(c);
			m.setEnvironment(source);
			methods.add(m);

		}

		CustomClassDefinition copyAdaptor = new CustomClassDefinition("CopyAdaptor", destination);
		copyAdaptor.setAnnotation("@SuppressWarnings(\"unused\")");
		copyAdaptor.setPackageName(defaultPackage);
		// copyAdaptor.interfaces.add(source.getTaggedDef(destination.TAG_IAnswer).getSignatureName()+"<"+destination.node.getSignatureName()+">");
		copyAdaptor.interfaces.add(new GenericArgumentedIInterfceDefinition(source.getTaggedDef(destination.TAG_IAnswer), destination.node));
		copyAdaptor.methods.addAll(methods);
		copyAdaptor.methods.add(new CopyNode2ExtendedNodeListHelper(source, destination));
		copyAdaptor.imports.addAll(source.getAllDefinitions());
		copyAdaptor.imports.addAll(destination.getAllDefinitions());
		copyAdaptor.setNamePostfix(namePostfix);
		destination.addClass(copyAdaptor);
		SourceFileWriter.write(outputFolder, copyAdaptor);
	}

}
