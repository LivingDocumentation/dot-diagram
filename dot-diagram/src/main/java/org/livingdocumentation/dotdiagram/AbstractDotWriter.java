package org.livingdocumentation.dotdiagram;

import java.io.*;

public abstract class AbstractDotWriter implements DotWriter {

    public abstract String getPath();
    public abstract String getImageExtension();

    /**
     * @param filename
     *            The filename without the extension and its path
     *
     *            Writes as path + filename.dot
     */
    @Override
    public void write(String filename, String content) throws UnsupportedEncodingException, FileNotFoundException {
        final String outputFileName = getPath() + filename + ".dot";
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
    @Override
    public String toImage(String filename, String content) throws InterruptedException, IOException {
        write(filename, content);
        render(filename);
        return filename + getImageExtension();
    }
}
