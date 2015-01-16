package org.livingdocumentation.dotdiagram;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * The resource bundle for UML styles for Graphviz Dot
 */
public class DotStyles {
	private static final String BUNDLE_NAME = "org.livingdocumentation.dotdiagram.dotstyles";

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private DotStyles() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	public static final String CLASS_NODE_OPTIONS = getString("CLASS_NODE_OPTIONS");

	public static final String NOTE_NODE_OPTIONS = getString("NOTE_NODE_OPTIONS");

	public static final String STUB_NODE_OPTIONS = getString("STUB_NODE_OPTIONS");

	public static final String COLLABORATION_NODE_OPTIONS = getString("COLLABORATION_NODE_OPTIONS");

	public static final String ELLIPSIS_NODE_OPTIONS = getString("ELLIPSIS_NODE_OPTIONS");

	// ---

	public static final String ASSOCIATION_EDGE_STYLE = getString("ASSOCIATION_EDGE_STYLE");

	public static final String INSTANTIATION_EDGE_STYLE = getString("INSTANTIATION_EDGE_STYLE");

	public static final String IMPLEMENTS_EDGE_STYLE = getString("IMPLEMENTS_EDGE_STYLE");

	public static final String EXTENDS_EDGE_STYLE = getString("EXTENDS_EDGE_STYLE");

	public static final String NOTE_EDGE_STYLE = getString("NOTE_EDGE_STYLE");

	public static final String COLLABORATION_EDGE_STYLE = getString("COLLABORATION_EDGE_STYLE");

	public static final String CLIENT_EDGE_STYLE = getString("CLIENT_EDGE_STYLE");
}
