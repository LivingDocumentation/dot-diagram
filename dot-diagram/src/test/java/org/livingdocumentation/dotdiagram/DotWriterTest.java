package org.livingdocumentation.dotdiagram;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;

/**
 * Integration Test, requires Graphviz dot installed and read/write access to
 * the disk for temporary files
 */
public class DotWriterTest {

	@Test
	public void render_with_graphviz() throws Exception {
		final Properties p = new Properties();
		p.load(this.getClass().getResourceAsStream("graphviz-dot.properties"));

		DotWriter writer = new GraphvizDotWriter(p);
		final String content = DotGraphTest.readTestResource("simple.dot").trim();
		// System.out.println(content);
		final String image = writer.toImage("toImage", content);
		assertEquals("toImage.png", image);
	}

	@Test
	public void render_with_googlechart() throws Exception {
		DotWriter writer = new GoogleChartDotWriter("target/");
		final String content = DotGraphTest.readTestResource("simple.dot").trim();
		// System.out.println(content);
		final String image = writer.toImage("toGoogleImage", content);
		assertEquals("toGoogleImage.png", image);
	}

}
