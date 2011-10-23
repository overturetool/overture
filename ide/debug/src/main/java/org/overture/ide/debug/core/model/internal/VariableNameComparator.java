package org.overture.ide.debug.core.model.internal;

import java.util.Comparator;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;

public class VariableNameComparator implements Comparator<Object> {

	public int compare(Object o1, Object o2) {
		int result = 0;
		IVariable v1 = (IVariable) o1;
		IVariable v2 = (IVariable) o2;
		try {
			String v1Str = (v1 != null) ? v1.getName() : ""; //$NON-NLS-1$
			v1Str = v1Str.replaceAll("\\[", "").replaceAll("\\]", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			int v1Int = 0;
			boolean v1IsInt;
			String v2Str = (v2 != null) ? v2.getName() : ""; //$NON-NLS-1$
			v2Str = v2Str.replaceAll("\\[", "").replaceAll("\\]", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			int v2Int = 0;
			boolean v2IsInt;

			try {
				v1Int = Integer.parseInt(v1Str);
				v1IsInt = true;
			} catch (NumberFormatException nxcn) {
				v1IsInt = false;
			}

			try {
				v2Int = Integer.parseInt(v2Str);
				v2IsInt = true;
			} catch (NumberFormatException nxcn) {
				v2IsInt = false;
			}

			if ((v1IsInt == true) && (v2IsInt == true)) {
				if (v1Int > v2Int) {
					result = 1;
				} else if (v1Int < v2Int) {
					result = -1;
				} else {
					result = 0;
				}
			} else {
				result = v1Str.compareTo(v2Str);

				if (result > 0) {
					result = 1;
				} else if (result < 0) {
					result = -1;
				} else {
					result = 0;
				}
			}
		} catch (DebugException e) {
		}

		return result;
	}

}
