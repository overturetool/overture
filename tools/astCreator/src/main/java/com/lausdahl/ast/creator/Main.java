package com.lausdahl.ast.creator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import com.lausdahl.ast.creator.definitions.CommonTreeClassDefinition;
import com.lausdahl.ast.creator.definitions.CustomClassDefinition;
import com.lausdahl.ast.creator.definitions.Field;
import com.lausdahl.ast.creator.definitions.GenericArgumentedIInterfceDefinition;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.IInterfaceDefinition;
import com.lausdahl.ast.creator.definitions.PredefinedClassDefinition;
import com.lausdahl.ast.creator.methods.CheckCacheMethod;
import com.lausdahl.ast.creator.methods.Method;
import com.lausdahl.ast.creator.methods.analysis.CopyNode2ExtendedNode;
import com.lausdahl.ast.creator.methods.analysis.CopyNode2ExtendedNodeListHelper;
import com.lausdahl.ast.creator.methods.analysis.CopyNode2ExtendedNodeListListHelper;

public class Main
{
	public enum RunType
	{
		OvertureII, OvertureII_Interpreter, Test
	}

	/**
	 * Set this to false to generate the overture II AST
	 */
	public static boolean test = false;

	private static final String INPUT_FILENAME_OVERTURE_II = "..\\..\\core\\ast\\src\\main\\resources\\overtureII.astv2".replace('\\', File.separatorChar);
	private static final String INPUT_FILENAME_OVERTURE_II_INTERPRETER = "..\\..\\core\\interpreter\\src\\main\\resources\\overtureII.astv2".replace('\\', File.separatorChar);
	private static final String INPUT_FILENAME_TEST = "src\\main\\resources\\testdata\\nested1.astV2";
//	private static final String INPUT_FILENAME = "src\\main\\resources\\testdata\\extend\\t1.astV2";
//
//	private static final String INPUT_FILENAME2 = "src\\main\\resources\\testdata\\extend\\t2.astV2";
	// private static final String ANALYSIS_PACKAGE_NAME = "org.overture.ast.analysis";
	private static File generated = null;

	public final static RunType run = RunType.OvertureII;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		String input1 = null;
		String input2 = null;
		String output = null;
		switch (run)
		{
			case OvertureII:
				output = "..\\..\\core\\ast\\src\\main\\java\\";
				input1 = INPUT_FILENAME_OVERTURE_II;
				input2 = null;
				break;
			case OvertureII_Interpreter:
				output = "..\\..\\core\\interpreter\\src\\main\\java\\";
				input1 = INPUT_FILENAME_OVERTURE_II;
				input2 = INPUT_FILENAME_OVERTURE_II_INTERPRETER;
				break;
			case Test:
				output = "..\\..\\astTest\\src\\";
				input1 = INPUT_FILENAME_TEST;
				input2 = null;
				test = true;
				break;

		}
		try
		{
			input1 = input1.replace('/', File.separatorChar).replace('\\', File.separatorChar);
			if (input2 != null)
			{
				input2 = input2.replace('/', File.separatorChar).replace('\\', File.separatorChar);
			}
			generated = new File(output.replace('/', File.separatorChar).replace('\\', File.separatorChar));

			
			System.out.println("Output location set to: "+ generated.getAbsolutePath());
			switch (run)
			{
				case OvertureII:
				{
						System.out.println("Generator starting with input: "
							+ input1);

					Environment env1 = create(input1, generated, true);
					System.out.println("\n\nGenerator completed with "
							+ env1.getAllDefinitions().size()
							+ " generated files.\n\n");
				}
					break;
				case OvertureII_Interpreter:
				{
					System.out.println("Generator starting with input: "
							+ input1);
					Main.create(new File(input1), new File(input2), generated, "Interpreter");
					System.out.println("Done.");
				}
					break;
				case Test:
				{
					System.out.println("TESTING...");
					Environment env1 = create(input1, generated, true);
					// Main.create(new File(INPUT_FILENAME), new File(INPUT_FILENAME2), generated, "Interpreter");
					// System.out.println("Generator starting with input: "
					// + INPUT_FILENAME);
					// // String defaultPackage = "org.overture.ast.node";
					// // String analysisPackage = "org.overture.ast.analysis";
					// Environment env1 = create(INPUT_FILENAME, generated, true);
					//
					// if (extend)
					// {
					// // defaultPackage = "org.overture.interpreter.ast.node";
					// // analysisPackage = "org.overture.interpreter.ast.analysis";
					// String extendName = "Interpreter";
					// // Environment env2 = create(INPUT_FILENAME2, defaultPackage, analysisPackage, extendName,
					// // generated, true);
					// Generator generator = new Generator();
					// Environment env2 = generator.generate(INPUT_FILENAME);
					// Environment env2Extension = generator.generate(INPUT_FILENAME2);
					// env2 = env2.extendWith(env2Extension);
					// generator.runPostGeneration(env2);
					// setExtendName(env2, extendName);
					//
					// SourceFileWriter.write(generated, env2);
					//
					// createCopyAdaptor(env1, env2, extendName, generated);
					// }
					System.out.println("TESTING...DONE.");
				}
					break;
			}

		} catch (AstCreatorException e)
		{
			System.err.println();
			System.err.println(e.getMessage());
		}

	}

	public static Environment create(String inputFile, File outputBase,
			boolean write) throws IOException, InstantiationException,
			IllegalAccessException, AstCreatorException
	{
		Generator generator = new Generator();
		Environment env = generator.generate(inputFile);
		generator.runPostGeneration(env);

		if (write)
		{
			SourceFileWriter.write(outputBase, env);
		}
		return env;
	}

	public static void create(File ast1, File ast2, File generated,
			String extendName) throws Exception
	{
		System.out.println("TESTING...");

		System.out.println("Generator starting with input: " + ast1);
		Environment env1 = create(ast1.getAbsolutePath(), generated, false);

		System.out.println("Generator starting with input: " + ast2);
		Generator generator = new Generator();
		Environment env2 = generator.generate(ast1.getAbsolutePath());
		Environment env2Extension = generator.generate(ast2.getAbsolutePath());
		env2 = env2.extendWith(env2Extension);

		setExtendName(env2, extendName);
		generator.runPostGeneration(env2);
		setExtendName(env2, extendName);

		SourceFileWriter.write(generated, env2);

		createCopyAdaptor(env1, env2, extendName, generated);

	}

	public static void setExtendName(Environment env, String extendName)
	{
		String namePostfix = extendName == null ? "" : extendName;
		for (IInterfaceDefinition def : env.getAllDefinitions())
		{
			def.setNamePostfix(namePostfix);
		}
	}

	public static void createCopyAdaptor(Environment source,
			Environment destination, String namePostfix, File outputFolder)
			throws Exception
	{
		CustomClassDefinition convertFactory = new CustomClassDefinition("ConvertFactory", destination);
		convertFactory.setNamePostfix(namePostfix);
		convertFactory.setPackageName(destination.getDefaultPackage());
		convertFactory.isAbstract = true;

		List<Method> methods = new Vector<Method>();
		for (CommonTreeClassDefinition c : Generator.getClasses(source.getClasses()))
		{
			if (c.getType() == IClassDefinition.ClassType.Production
			/* || c.getType() == ClassType.SubProduction */)
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
			Method m = new CopyNode2ExtendedNode(c, destDef, source, destination, convertFactory);
			m.setClassDefinition(c);
			m.setEnvironment(source);
			methods.add(m);

		}

		CustomClassDefinition copyAdaptor = new CustomClassDefinition("CopyAdaptor", destination);
		Field converFactoryField = new Field(destination);
		converFactoryField.name = "factory";
		converFactoryField.type = convertFactory;
		copyAdaptor.addField(converFactoryField);

		Field cacheField = new Field(destination);
		cacheField.name = "cache";
		cacheField.setType("Hashtable");
		destination.addClass(new PredefinedClassDefinition("java.util", "Hashtable", true));
		copyAdaptor.addField(cacheField);

		copyAdaptor.setAnnotation("@SuppressWarnings({\"unused\",\"unchecked\",\"rawtypes\"})");
		copyAdaptor.setPackageName(destination.getDefaultPackage());
		// copyAdaptor.interfaces.add(source.getTaggedDef(destination.TAG_IAnswer).getSignatureName()+"<"+destination.node.getSignatureName()+">");
		copyAdaptor.interfaces.add(new GenericArgumentedIInterfceDefinition(source.getTaggedDef(destination.TAG_IAnswer), destination.node));
		copyAdaptor.methods.addAll(methods);
		copyAdaptor.methods.add(new CopyNode2ExtendedNodeListHelper(source, destination));
		copyAdaptor.methods.add(new CopyNode2ExtendedNodeListListHelper(source, destination));
		copyAdaptor.methods.add(new CheckCacheMethod(copyAdaptor, destination));
		// copyAdaptor.methods.add(new ConstructorMethod(copyAdaptor,destination));
		// copyAdaptor.imports.addAll(source.getAllDefinitions());
		// copyAdaptor.imports.addAll(destination.getAllDefinitions());
		copyAdaptor.setNamePostfix(namePostfix);
		destination.addClass(copyAdaptor);
		SourceFileWriter.write(outputFolder, copyAdaptor);
		SourceFileWriter.write(outputFolder, convertFactory);

	}

}
