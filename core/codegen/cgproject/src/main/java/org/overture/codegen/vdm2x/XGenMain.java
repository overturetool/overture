package org.overture.codegen.vdm2x;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;

public class XGenMain {

	public static void main(String[] args) {

		// Se i JavaCodeGenMain
		System.out.println("Test?");

		File file = new File(args[0]);

		List<File> files = new LinkedList<>();
		files.add(file);

		try {
			List<SClassDefinition> ast = GeneralCodeGenUtils.consClassList(
					files, Dialect.VDM_PP);

			XCodeGen xGen = new XCodeGen();
			GeneratedData data = xGen.generateJavaFromVdm(ast);

			
			for (GeneratedModule module : data.getClasses()) {
				
				if (module.canBeGenerated()) {
					System.out.println(module.getContent());
				}
			}

		} catch (AnalysisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
