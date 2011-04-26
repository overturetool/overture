package org.overture.ide.ui.utility;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.overture.ide.core.utility.FileUtility;
import org.overture.ide.core.utility.SourceLocationConverter;
import org.overturetool.vdmj.lex.LexLocation;

public class EditorUtility
{
	public static void gotoLocation(IFile file, LexLocation location,
			String message) {
		try {

			IWorkbench wb = PlatformUI.getWorkbench();
			IWorkbenchWindow win = wb.getActiveWorkbenchWindow();

			IEditorPart editor = IDE.openEditor(win.getActivePage(), file, true);

			gotoLocaion(file, location, message, editor);

		} catch (CoreException e) {

			e.printStackTrace();
		}
	}

	private static void gotoLocaion(IFile file, LexLocation location,
			String message, IEditorPart editor) throws CoreException
	{
		IMarker marker = file.createMarker(IMarker.MARKER);
		marker.setAttribute(IMarker.MESSAGE, message);
		marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);

		SourceLocationConverter converter = new SourceLocationConverter(FileUtility.getContent(file));
		marker.setAttribute(IMarker.CHAR_START,converter.getStartPos( location));
		marker.setAttribute(IMarker.CHAR_END,converter.getEndPos(location));
		IDE.gotoMarker(editor, marker);

		marker.delete();
	}

	public static void gotoLocation(String editorId, IFile file, LexLocation location,
			String message) {
		try {

			IWorkbench wb = PlatformUI.getWorkbench();
			IWorkbenchWindow win = wb.getActiveWorkbenchWindow();

			IEditorPart editor = IDE.openEditor(win.getActivePage(), file, editorId,true);

			gotoLocaion(file, location, message, editor);

		} catch (CoreException e) {

			e.printStackTrace();
		}
	}
	
//	public static List<Character> getContent(IFile file) {
//		
//		InputStream inStream;
//		InputStreamReader in = null;
//		List<Character> content = new Vector<Character>();
//		try {
//			inStream = file.getContents();
//			in = new InputStreamReader(inStream, file.getCharset());
//
//			int c = -1;
//			while ((c = in.read()) != -1)
//				content.add((char) c);
//
//		} catch (CoreException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			if (in != null)
//				try {
//					in.close();
//				} catch (IOException e) {
//				}
//		}
//
//		return content;
//
//	}
//	public static char[] getCharContent(List<Character> content) {
//		char[] source = new char[content.size()];
//		for (int i = 0; i < content.size(); i++) {
//			source[i] = content.get(i);
//		}
//		return source;
//	}
}
