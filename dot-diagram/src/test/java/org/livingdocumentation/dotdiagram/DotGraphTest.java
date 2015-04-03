package org.livingdocumentation.dotdiagram;

import static org.junit.Assert.assertEquals;
import static org.livingdocumentation.dotdiagram.DotStyles.ASSOCIATION_EDGE_STYLE;
import static org.livingdocumentation.dotdiagram.DotStyles.INSTANTIATION_EDGE_STYLE;
import static org.livingdocumentation.dotdiagram.DotStyles.NOTE_EDGE_STYLE;
import static org.livingdocumentation.dotdiagram.DotStyles.STUB_NODE_OPTIONS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;
import org.livingdocumentation.dotdiagram.DotGraph.Cluster;
import org.livingdocumentation.dotdiagram.DotGraph.Digraph;

public class DotGraphTest {

	@Test
	public void test_simple() {
		final String testName = "simple";
		final DotGraph graph = new DotGraph(testName + " test");

		final Digraph digraph = graph.getDigraph();
		digraph.addNode("Car").setLabel("My Car").setComment("This is BMW").setOptions(STUB_NODE_OPTIONS);
		digraph.addNode("Wheel").setLabel("Its wheels").setComment("The wheels of my car");
		digraph.addAssociation("Car", "Wheel").setLabel("4*").setComment("There are 4 wheels")
				.setOptions(ASSOCIATION_EDGE_STYLE);

		final String actual = graph.render().trim();

		// System.out.println(actual);
		final String expected = readTestResource(testName + ".dot").trim();
		assertEquals(expected.trim(), actual);
	}

	@Test
	public void test_cluster() {
		final String testName = "clustering";
		final DotGraph graph = new DotGraph(testName + " test");

		final Digraph digraph = graph.getDigraph();
		final Cluster cluster = digraph.addCluster("Brand");
		cluster.setLabel("BMW brand").setComment("my cluster");
		cluster.addNode("Car").setLabel("My Car").setComment("This is BMW").setOptions(STUB_NODE_OPTIONS);
		cluster.addNode("Wheel").setLabel("Its wheels").setComment("The wheels of my car");
		cluster.addAssociation("Car", "Wheel").setLabel("4*").setComment("There are 4 wheels")
				.setOptions(ASSOCIATION_EDGE_STYLE);

		digraph.addNode("Customer").setLabel("My Customer").setComment("He loves BMW").setOptions(NOTE_EDGE_STYLE);
		digraph.addAssociation("Customer", "Car").setLabel("buys").setComment("The buyer of the car")
				.setOptions(INSTANTIATION_EDGE_STYLE);

		final String actual = graph.render().trim();

		// System.out.println(actual);
		final String expected = readTestResource(testName + ".dot").trim();
		assertEquals(expected.trim(), actual);
	}

	@Test
	public void test_simple_preloadNodes() {
		final String testName = "preload";
		final DotGraph graph = new DotGraph(testName + " test");

		final Digraph digraph = graph.getDigraph();
		digraph.addNode("Car").setLabel("My Car").setComment("This is BMW").setOptions(STUB_NODE_OPTIONS);
		digraph.addNode("Wheel").setLabel("Its wheels").setComment("The wheels of my car");
		digraph.addExistingAssociation("Car", "Wheel").setLabel("4*").setComment("There are 4 wheels")
				.setOptions(ASSOCIATION_EDGE_STYLE);

		// this association must be ignored
		digraph.addExistingAssociation("Car", "Boat");
		// this association must be ignored too
		digraph.addExistingAssociation("Plane", "Car");
		// this association must be ignored too
		digraph.addExistingAssociation("Plane", "Boat");

		// this association should be ignored but will be there
		digraph.addAssociation("Car", "Plane").setLabel("-").setComment("This association should be ignored")
				.setOptions(ASSOCIATION_EDGE_STYLE);

		final String actual = graph.render().trim();

		// System.out.println(actual);
		final String expected = readTestResource(testName + ".dot").trim();
		assertEquals(expected.trim(), actual);
	}

	/**
	 * @return A String that represents the content of the file
	 */
	public static String readTestResource(final String filename) {
		String lineSep = String.format("%n");
		final StringBuffer buffer = new StringBuffer();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					DotGraphTest.class.getResourceAsStream(filename)));
			String str = null;
			while ((str = in.readLine()) != null) {
				buffer.append(lineSep);
				buffer.append(str);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer.toString();
	}

}
