package org.overturetool.eclipse.plugins.editor.core.internal.parser;

import org.overturetool.eclipse.plugins.editor.core.internal.parser.ast.OvertureModuleDeclaration;

public class OvertureParserMarcel {

	
//	public void parseFiles(ArrayList<String> files)
//	{
//		
//		Charset charset = Charset.defaultCharset();
//		for (String filename : files) {
//			try {
//				FileInputStream file = new FileInputStream(filename);
//				InputStreamReader stream = new java.io.InputStreamReader(file,charset);
//				OvertureParser overtureParser = new OvertureParser(stream);
//				overtureParser.parseDocument();
////				OvertureModuleDeclaration moduleDeclaration = new OvertureModuleDeclaration(file., true);
////				OvertureVisitorPPEditor ppVisitor = new OvertureVisitorPPEditor(moduleDeclaration);
//				overtureParser.astDocument.setFilename(filename);
//				if (overtureParser.errors > 0){
//					System.err.println("Parse errors: ");
//				}
//				else
//				{
////					ppVisitor.visitDocument(overtureParser.astDocument);
//				}
//			
//			
//			} catch (FileNotFoundException e) {
//				System.out.println("file not found exception " + e.getMessage());
//				e.printStackTrace();
//			}
//			catch (Exception e) {
//				System.out.println("Exception: " + e.getMessage());
//				e.printStackTrace();
//			}
//			
//		}
//	}
	
	public OvertureModuleDeclaration parseContent(String content, String filename)
	{
//		OvertureModuleDeclaration moduleDeclaration = new OvertureModuleDeclaration(content.length(), true);
//		OvertureParser parser = new OvertureParser(content);
//		try {
//			
//			parser.parseDocument();
//			parser.astDocument.setFilename(filename);
//			if (parser.errors > 0){
//				System.err.println("#Errors: " + parser.errors);
//			}
//			else
//			{
//				OvertureASTTreePopulator populator = new OvertureASTTreePopulator(moduleDeclaration, new DLTKConverter(content.toCharArray()));
//				return populator.populateOverture(parser.astDocument.getSpecifications().getClassList());
//			}
//			return moduleDeclaration;
//		} catch (CGException e) {
//			System.out.println("Couldn't parse the document" + e.getMessage());
//			e.printStackTrace();
//			return null;
//		}
		return null;
	}
}
