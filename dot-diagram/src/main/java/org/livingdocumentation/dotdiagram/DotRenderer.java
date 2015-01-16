package org.livingdocumentation.dotdiagram;

import java.util.List;
import java.util.StringTokenizer;

/**
 * Renders a graph with basic UML diagrams elements into the dot syntax
 */
public class DotRenderer {

	private static final String NEWLINE = String.format("%n");

	protected static final String TAB = "\t";

	private static final String OPEN_STEREOTYPE = "\\<" + "\\<";

	private static final String CLOSE_STEREOTYPE = "\\>" + "\\>";

	protected DotRenderer() {
	}

	public static String fontname(String fontname) {
		return "fontname=\"" + fontname + "\"";
	}

	public static String fontsize(int fontsize) {
		return "fontsize=" + fontsize;
	}

	public static String options(String fontname, int fontsize) {
		return fontname(fontname) + "," + fontsize(fontsize);
	}

	public static String options(boolean isAbstract) {
		return fontname(isAbstract ? "Verdana-Italic" : "Verdana") + ", " + fontsize(9);
	}

	public static String stereotype(String str) {
		return OPEN_STEREOTYPE + str + CLOSE_STEREOTYPE;
	}

	public static String openGraph(String title) {
		final StringBuffer sb = new StringBuffer();
		sb.append("# Class diagram ");
		sb.append(title);
		sb.append(NEWLINE);
		sb.append("digraph G {");

		if (title != null) {
			sb.append(graphTitle(title));
		}
		sb.append(optionsEdge());
		sb.append(optionsNode());

		return sb.toString();
	}

	public static String closeGraph() {
		return NEWLINE + "}" + NEWLINE;
	}

	public static String openCluster(String id) {
		return NEWLINE + "subgraph " + id + " {";
	}

	public static String cluster(String content) {
		return NEWLINE + "label = \"" + content + "\";";
	}

	public static String closeCluster() {
		return NEWLINE + "}";
	}

	public static String withDotNewLine(String s) {
		return NEWLINE + "//" + s;
	}

	public static String graphTitle(String title) {
		final StringBuffer sb = new StringBuffer();
		sb.append(NEWLINE);
		sb.append(TAB);
		sb.append("graph");
		sb.append(" ");
		sb.append("[");
		sb.append("labelloc=top,label=\"");
		sb.append(title);
		sb.append("\"");
		sb.append(",");
		sb.append(options("Verdana", 12));
		sb.append("]");
		sb.append(";");
		return sb.toString();
	}

	public static String optionsNode() {
		final StringBuffer sb = new StringBuffer();
		sb.append(NEWLINE);
		sb.append(TAB);
		sb.append("node");
		sb.append(" ");
		sb.append("[");
		sb.append(options("Verdana", 9));

		sb.append(",");
		sb.append("shape=record");

		sb.append("]");
		sb.append(";");
		return sb.toString();
	}

	public static String optionsEdge() {
		final StringBuffer sb = new StringBuffer();
		sb.append(NEWLINE);
		sb.append(TAB);
		sb.append("edge");
		sb.append(" ");
		sb.append("[");
		sb.append(options("Verdana", 9));

		sb.append(",");
		sb.append("labelfontname=\"");
		sb.append("Verdana");
		sb.append("\",labelfontsize=");
		sb.append(9);

		sb.append("]");
		sb.append(";");
		return sb.toString();
	}

	public static String edge(String uniqueNameFrom, String uniqueNameTo, String comment, String labels,
			String edgeStyle) {
		if (uniqueNameFrom == null || uniqueNameTo == null) {
			return "";
		}
		final StringBuffer sb = new StringBuffer();
		sb.append(NEWLINE);
		sb.append(TAB);
		sb.append("// ");
		sb.append(comment);

		sb.append(NEWLINE);
		sb.append(TAB);
		sb.append(uniqueNameFrom);
		sb.append(" -> ");
		sb.append(uniqueNameTo);
		sb.append(" [");
		if (labels != null) {
			sb.append(labels);
			sb.append("  ");
			sb.append(", ");
		}
		if (edgeStyle != null) {
			sb.append(edgeStyle);
		}
		sb.append("];");

		return sb.toString();
	}

	public static String toLines(final List<String> cells) {
		return toLines((String[]) cells.toArray(new String[cells.size()]), "\\n ", "");
	}

	private static String toLines(String[] cells, String prefix, String postfix) {
		if (cells == null || cells.length == 0) {
			return "";
		}
		final StringBuffer sb = new StringBuffer();
		for (int i = 0; i < cells.length; i++) {
			if (i > 0) {
				sb.append(prefix);
			}
			sb.append(cells[i]);
			sb.append(postfix);
		}
		return sb.toString();
	}

	public static String wrapText(String text, int length) {
		final StringBuffer sb = new StringBuffer();
		StringTokenizer st = new StringTokenizer(text, " \t\n", true);
		int lineLength = 0;
		while (st.hasMoreElements()) {
			String token = st.nextToken();
			if (lineLength > length) {
				lineLength = 0;
				sb.append("\\l");
			}
			sb.append(token);
			lineLength += token.length();
		}
		return sb.toString();
	}

	public static String node(String uniqueName, final String label, String options) {
		final StringBuffer sb = new StringBuffer();
		sb.append(NEWLINE);
		sb.append(TAB);
		sb.append(uniqueName);
		sb.append(" ");
		sb.append("[");
		sb.append("label=\"");
		sb.append(label);
		sb.append("\"");
		if (options != null) {
			sb.append(", ");
			sb.append(options);
		}
		sb.append("]");
		return sb.toString();
	}

}
