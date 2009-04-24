package org.overturetool.eclipse.plugins.launching.internal.launching;
//package org.overturetool.internal.launching;
//
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.PrintStream;
//
//public class BuiltinsResolver {
//
//	static String init = init();
//	//private static Context enter;
//	//private static ScriptableObject initStandardObjects;
//
//	//TODO
//	public static String init() {
//		return null;
//	}
//
//	//TODO
//	private static void appendObject(StringBuffer buf, String string, Object object) {
//		
//	}
//
//	
//	//TODO
//	/*public static void appendFunction(StringBuffer buf, String name,
//			String[] argNames, Function c) {
//	}*/
//
//	//TODO
//	/*private static void appendObjects(StringBuffer buf,
//			ScriptableObject construct, Object[] elements, boolean funcs) {
//		for (int a = 0; a < elements.length; a++) {
//			String string = elements[a].toString();
//			Object object2 = construct.get(string, construct);
//			String val = funcs ? "function (){};" : "1;";
//			if (object2 instanceof Function) {
//				val = "function (){};";
//			}
//			if (object2 instanceof String) {
//				val = "function (){};";
//			}
//			buf.append("this." + string + "=");
//			buf.append(val);
//			buf.append("\n");
//		}
//	}*/
//
//	//TODO
//	/*public static void appendObject(StringBuffer buf, String name,
//			ScriptableObject scriptableObject) {
//		buf.append("var " + name + "={");
//		ScriptableObject sc = (ScriptableObject) scriptableObject;
//
//		Object[] allIds2 = sc.getAllIds();
//		for (int a = 0; a < allIds2.length; a++) {
//			Object object = allIds2[a];
//			Object object2 = sc.get(allIds2[a].toString(), sc);
//			String value = "1";
//			if (object2 instanceof Function) {
//				value = "function(){}";
//			}
//			buf.append(object.toString() + ":" + value);
//			if (a != allIds2.length - 1)
//				buf.append(",");
//			buf.append('\n');
//		}
//		buf.append("};\n");
//	}*/
//
//	public static void main(String[] args) {
//		String init2 = init();
////		System.out.println(init2);
//		try {
//			FileOutputStream ds = new FileOutputStream("C:/ss");
//			PrintStream st = new PrintStream(ds);
//			st.println(init2);
//			st.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//}
