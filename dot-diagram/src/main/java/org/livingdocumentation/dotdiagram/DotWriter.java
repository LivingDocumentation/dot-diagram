package org.livingdocumentation.dotdiagram;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Properties;

/**
 * Java wrapper around the Graphviz Dot grapher; requires Graphviz to be
 * installed on the machine, and read/write access to the disk for temporary
 * .dot files.
 */
public final class DotWriter {

	private final String path;

	private final String dotPath;

	private final String imageExtension;

	private final String commandTemplate;

	/**
	 * Constructor from the config properties
	 */
	public DotWriter(Properties prop) {
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
	public DotWriter(String path, String dotPath, String imageExtension, String cmd) {
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

	public String getPath() {
		return path;
	}

	/**
	 * @param filename
	 *            The filename without the extension and its path
	 * 
	 *            Run dot: dot -Tpng filename.dot -o filename.png
	 */
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

	/**
	 * @param filename
	 *            The filename without the extension and its path
	 * 
	 *            Writes as path + filename.dot
	 */
	public void write(String filename, String content) throws UnsupportedEncodingException, FileNotFoundException {
		final String outputFileName = path + filename + ".dot";
		final String outputEncoding = "ISO-8859-1";
		final FileOutputStream fos = new FileOutputStream(outputFileName);
		final PrintWriter w = new PrintWriter(new BufferedWriter(new OutputStreamWriter(fos, outputEncoding)));

		w.println(content);
		w.flush();
		w.close();
	}

	/**
	 * All-in-on convenience method
	 * 
	 * @return The filename of the dot-generated picture for the given content
	 */
	public String toImage(String filename, String content) throws InterruptedException, IOException {
		write(filename, content);
		render(filename);
		return filename + imageExtension;
	}

	public String toString() {
		return "DotPrinter path=" + path + " dot-path=" + dotPath + " imageExtension=" + imageExtension;
	}
}
