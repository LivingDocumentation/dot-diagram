package org.livingdocumentation.dotdiagram;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public interface DotWriter {

	void render(String filename) throws InterruptedException, IOException;

	void write(String filename, String content) throws UnsupportedEncodingException, FileNotFoundException;

	String toImage(String filename, String content) throws InterruptedException, IOException;
}
