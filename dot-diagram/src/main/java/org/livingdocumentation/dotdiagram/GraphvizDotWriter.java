package org.livingdocumentation.dotdiagram;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.Properties;

/**
 * Java wrapper around the Graphviz Dot grapher; requires Graphviz to be
 * installed on the machine, and read/write access to the disk for temporary
 * .dot files.
 */
public final class GraphvizDotWriter extends AbstractDotWriter {

	private final String path;

	private final String dotPath;

	private final String imageExtension;

	private final String commandTemplate;

	/**
	 * Constructor from the config properties
	 */
	public GraphvizDotWriter(Properties prop) {
		this(prop.getProperty("outpath"), prop.getProperty("dotpath"), prop.getProperty("imageextension"), prop
				.getProperty("commandline"));
	}

	/**
	 * @param path
	 *            The path to dot and png files, must end with a slash
	 * @param dotPath
	 *            The path to the dot executable, must end with a slash
	 * @param cmd
	 *            The command-line template, e.g.
	 *            "$0 dot -Tpng $1.dot -o $1.png -Gdpi=72 -Gsize="6,8.5""
	 */
	public GraphvizDotWriter(String path, String dotPath, String imageExtension, String cmd) {
		this.path = path;
		this.dotPath = dotPath;
		this.imageExtension = imageExtension;
		this.commandTemplate = cmd;
	}

	public String getCommandTemplate() {
		return commandTemplate;
	}

	public String getDotPath() {
		return dotPath;
	}

	@Override
	public String getImageExtension() {
		return imageExtension;
	}

	@Override
	public String getPath() {
		return path;
	}

	/**
	 * @param filename
	 *            The filename without the extension and its path
	 * 
	 *            Run dot: dot -Tpng filename.dot -o filename.png
	 */
	@Override
	public void render(String filename) throws InterruptedException, IOException {
		final Object[] args = { dotPath, path + filename };
		final String command = MessageFormat.format(commandTemplate, args);

		final Process p = Runtime.getRuntime().exec(command);
		final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));

		final String newLine = String.format("%n");
		final StringBuilder errorMessage = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			errorMessage.append(newLine);
			errorMessage.append(line);
		}
		int result = p.waitFor();
		if (result != 0) {
			throw new DotDiagramException("Errors running Graphviz on " + filename + ".dot" + errorMessage);
		}
	}

	public String toString() {
		return "GraphvizDotWriter path=" + path + " dot-path=" + dotPath + " imageExtension=" + imageExtension;
	}
}
