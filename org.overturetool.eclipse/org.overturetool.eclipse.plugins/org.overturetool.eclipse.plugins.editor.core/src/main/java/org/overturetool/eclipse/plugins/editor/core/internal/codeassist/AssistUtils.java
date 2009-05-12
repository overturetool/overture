package org.overturetool.eclipse.plugins.editor.core.internal.codeassist;

public class AssistUtils {

	public static class PositionCalculator {

		private boolean isMember;
		private final String completion;
		private final String completionPart;
		private final String corePart;

		public PositionCalculator(String conString, int pos, boolean bothSides) {
			int position = pos;
			if (bothSides) {
				int lastDot = -1;
				int k = pos;
				int nestLevel = 0;
				boolean needDot = false;
				if (bothSides) {
					int maxPos = conString.length() - 1;
					if (pos < maxPos) {
						while (pos < maxPos) {
							pos++;
							char charAt = conString.charAt(pos);
							if (charAt == ']') {
								nestLevel++;
								continue;
							}
							if (charAt == '[') {
								nestLevel--;
								continue;
							}
							if (nestLevel > 0)
								continue;
							if (Character.isWhitespace(charAt)) {

								pos += 1;
								break;
							}

							if (!needDot
									&& Character.isJavaIdentifierPart(charAt))
								continue;
							else {
								pos += 1;
								// isMember = false;
								break;
							}
						}
						position = pos - 1;
					} else
						position = pos + 1;

				}
				pos = k;
				l2: while (pos > 0) {
					pos--;
					char charAt = conString.charAt(pos);
					if (charAt == '\n')
						break;
					if (charAt == ']' || charAt == ')') {
						nestLevel++;
						continue;
					}
					if (charAt == '[' || charAt == '(') {
						nestLevel--;
						continue;
					}
					if (nestLevel > 0)
						continue;
					if (Character.isWhitespace(charAt)) {
						needDot = true;
						continue l2;
					}

					if (charAt == '.') {
						isMember = true;
						needDot = false;
						if (lastDot == -1)
							lastDot = pos + 1;

						continue l2;
					}
					if (!needDot && Character.isJavaIdentifierPart(charAt))
						continue l2;
					else {
						pos += 1;
						// isMember = false;
						break l2;
					}
				}
				if (position > conString.length())
					position = conString.length();
				completion = conString.substring(pos, position).trim();
				if (lastDot != -1) {
					isMember = true;
					completionPart = conString.substring(lastDot, position)
							.trim();
					corePart = conString.substring(pos, lastDot - 1).trim();
				} else {
					isMember = false;
					completionPart = completion;
					corePart = completion;
				}
			} else {
				corePart = "else completion";
				completionPart = "else completion";
				completion = "else completion";
				
//				completion = HostCollection.parseCompletionString(conString.substring(0, pos), false);
//				int lastDot = completion.lastIndexOf('.');
//				if (lastDot != -1) {
//					isMember = true;
//					completionPart = completion.substring(lastDot + 1);
//					corePart = completion.substring(0, lastDot);
//				} else {
//					isMember = false;
//					completionPart = completion;
//					corePart = completion;
//				}
				System.out.println("");
			}
		}

		public String getCompletion() {
			return completion;
		}

		public String getCompletionPart() {
			return completionPart;
		}

		public String getCorePart() {
			return corePart;
		}

		public boolean isMember() {
			return isMember;
		}
	}
}
