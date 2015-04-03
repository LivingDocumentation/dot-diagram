package org.livingdocumentation.dotdiagram;

import static org.livingdocumentation.dotdiagram.DotRenderer.closeCluster;
import static org.livingdocumentation.dotdiagram.DotRenderer.cluster;
import static org.livingdocumentation.dotdiagram.DotRenderer.openCluster;
import static org.livingdocumentation.dotdiagram.DotRenderer.toLines;
import static org.livingdocumentation.dotdiagram.DotRenderer.withDotNewLine;
import static org.livingdocumentation.dotdiagram.DotRenderer.wrapText;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A simple API to generate Dot (Graphviz) files from a tree of Node and
 * Associations. The output is the content of a dot file.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public final class DotGraph implements Renderable {

	private static final String NODE_ID_PREFIX = "c";
	private static final String CLUSTER_PREFIX = "cluster_";

	private final AbstractNode root;

	private final NodeRegistry registry = new NodeRegistry();

	public DotGraph(final String title) {
		this.root = new Digraph(registry, title);
	}

	public DotGraph(final String title, String direction) {
		this.root = new Digraph(registry, title, direction);
	}

	public Digraph getDigraph() {
		return (Digraph) root;
	}

	public String preloadNode(Object id) {
		return registry.nodeUniqueId(id);
	}

	public String render() {
		return root.render();
	}

	public String toString() {
		return "DotGraph root: " + root;
	}

	/**
	 * Stores unique node id by String node key
	 */
	public static final class NodeRegistry {

		private final Map<Object, String> nodeUids = new HashMap<Object, String>();

		private int count = 0;

		public String existingUniqueId(Object id) {
			return nodeUids.get(id);
		}

		public String nodeUniqueId(Object id) {
			String nodeUid = nodeUids.get(id);
			if (nodeUid != null) {
				return nodeUid;
			}
			nodeUid = NODE_ID_PREFIX + count++;
			nodeUids.put(id, nodeUid);
			return nodeUid;
		}

		public String toString() {
			return "NodeRegistry: " + nodeUids.size() + " nodes registered";
		}
	}

	/**
	 * Represents any abstract node (digraph, cluster, node, record cell)
	 */
	public static abstract class AbstractNode implements Renderable {

		protected final NodeRegistry registry;

		protected final String id;

		protected String comment;

		protected String label;

		protected String options;

		protected final Collection<String> stereotypes = new HashSet<String>();

		protected final Map nodes = new HashMap();

		protected final Collection associations = new HashSet();

		public AbstractNode(NodeRegistry registry, final String id) {
			this.registry = registry;
			this.id = id;
		}

		public String getComment() {
			return comment;
		}

		public AbstractNode setComment(String comment) {
			this.comment = comment;
			return this;
		}

		public Node addPossibleNode(Object id) {
			final String uid = registry.existingUniqueId(id);
			if (uid == null) {
				return null;
			}
			Node node = (Node) nodes.get(uid);
			if (node == null) {
				node = new Node(registry, uid);
				nodes.put(uid, node);
			}
			return node;
		}

		public Node addNode(Object id) {
			final String uid = registry.nodeUniqueId(id);
			Node node = (Node) nodes.get(uid);
			if (node == null) {
				node = new Node(registry, uid);
				nodes.put(uid, node);
			}
			return node;
		}

		public Cluster addCluster(Object id) {
			final String uid = registry.nodeUniqueId(id);
			Cluster node = (Cluster) nodes.get(uid);
			if (node == null) {
				node = new Cluster(registry, uid);
				nodes.put(uid, node);
			}
			return node;
		}

		public AbstractNode addStereotype(String stereotype) {
			stereotypes.add(DotRenderer.stereotype(stereotype));
			return this;
		}

		public Association addExistingAssociation(Object sourceId, Object targetId) {
			final String uid = registry.existingUniqueId(sourceId);
			final String uid2 = registry.existingUniqueId(targetId);
			if (uid != null && uid2 != null) {
				final Association association = new Association(uid, uid2);
				associations.add(association);
				return association;
			}
			return null;
		}

		public Association addExistingAssociation(Object sourceId, Object targetId, String label, String comment,
				String options) {
			final String uid = registry.existingUniqueId(sourceId);
			final String uid2 = registry.existingUniqueId(targetId);
			if (uid != null && uid2 != null) {
				final Association association = new Association(uid, uid2);
				associations.add(association);
				if (label != null) {
					association.setLabel(label);
				}
				if (comment != null) {
					association.setComment(comment);
				}
				if (options != null) {
					association.setOptions(options);
				}
				return association;
			}
			return null;
		}

		public Association addAssociation(Object sourceId, Object targetId) {
			final String uid = registry.nodeUniqueId(sourceId);
			final String uid2 = registry.nodeUniqueId(targetId);
			if (uid != null && uid2 != null) {
				final Association association = new Association(uid, uid2);
				associations.add(association);
				return association;
			}
			return null;
		}

		public String getLabel() {
			return label;
		}

		public AbstractNode setLabel(String label) {
			this.label = label;
			return this;
		}

		public String getOptions() {
			return options;
		}

		public AbstractNode setOptions(String options) {
			this.options = options;
			return this;
		}

		public Collection getAssociations() {
			return associations;
		}

		public String getId() {
			return id;
		}

		public Collection<String> getStereotypes() {
			return stereotypes;
		}

		protected void renderAssociations(final StringBuffer out) {
			Iterator it = associations.iterator();
			while (it.hasNext()) {
				Renderable renderable = (Renderable) it.next();
				out.append(renderable.render());
			}
		}

		protected void renderNodes(final StringBuffer out) {
			final List<Renderable> values = new ArrayList<Renderable>(nodes.values());
			final Comparator comp = new Comparator<Renderable>() {

				public int compare(Renderable r1, Renderable r2) {
					return r1.toString().compareTo(r2.toString());
				}
			};
			Collections.sort(values, comp);
			Iterator it = values.iterator();
			while (it.hasNext()) {
				Renderable renderable = (Renderable) it.next();
				out.append(renderable.render());
			}
		}

		public String toString() {
			return "Node";
		}

		/**
		 * @return true if this Node is equal to the given Node
		 */
		public boolean equals(Object arg0) {
			if (!(arg0 instanceof AbstractNode)) {
				return false;
			}
			final AbstractNode other = (AbstractNode) arg0;
			if (this == other) {
				return true;
			}
			return other.id.equals(id);
		}

		public int hashCode() {
			return id.hashCode();
		}

	}

	/**
	 * Represents an actual node (dot node element)
	 */
	public static final class Node extends AbstractNode {

		public Node(NodeRegistry registry, String id) {
			super(registry, id);
		}

		public String render() {
			if (label == null) {
				return "";
			}
			final StringBuffer out = new StringBuffer();
			if (comment != null) {
				out.append(withDotNewLine(comment));
			}

			final List<String> cells = new ArrayList<String>();
			cells.add(label);
			if (!stereotypes.isEmpty()) {
				cells.addAll(stereotypes);
			}
			final String content = toLines(cells);

			final String wrapText = wrapText(content, 20);
			out.append(DotRenderer.node(id, wrapText, options));

			Iterator it = associations.iterator();
			while (it.hasNext()) {
				AbstractAssociation abstractAssociation = (AbstractAssociation) it.next();

				out.append(abstractAssociation.render());
			}
			return out.toString();
		}

		public String toString() {
			return "Node" + id;
		}
	}

	/**
	 * Represents a dot Digraph element
	 */
	public static final class Digraph extends AbstractNode {

		private final String dir;

		public Digraph(NodeRegistry registry, String title) {
			this(registry, title, null);
		}

		public Digraph(NodeRegistry registry, String title, String dir) {
			super(registry, title);
			this.dir = dir;
			setLabel(title);
		}

		public String render() {
			final StringBuffer out = new StringBuffer();
			out.append(DotRenderer.openGraph(label, dir));

			renderNodes(out);
			renderAssociations(out);

			out.append(DotRenderer.closeGraph());
			return out.toString();
		}

		public String toString() {
			return "Digraph " + id;
		}

		public AbstractNode findNode(String identifier) {
			final String uid = registry.nodeUniqueId(id);
			Iterator it = nodes.values().iterator();
			while (it.hasNext()) {
				AbstractNode node = (AbstractNode) it.next();
				if (uid.equals(node.getId())) {
					return node;
				}
			}

			return null;
		}
	}

	/**
	 * Represents a Node in a dot diagram
	 */
	public static final class Cluster extends AbstractNode {

		public Cluster(NodeRegistry registry, String id) {
			super(registry, id);
		}

		public String render() {
			final StringBuffer out = new StringBuffer();
			final List<String> cells = new ArrayList<String>();
			if (label != null) {
				cells.add(label);
			}
			cells.addAll(stereotypes);
			final String content = toLines(cells);

			out.append(openCluster(CLUSTER_PREFIX + id));
			out.append(cluster(content));

			renderNodes(out);
			renderAssociations(out);

			out.append(closeCluster());
			return out.toString();
		}

		public String toString() {
			return "Cluster " + id;
		}

	}

	/**
	 * Represents any association in a dot diagram
	 * 
	 * @author cyrille martraire
	 */
	public static abstract class AbstractAssociation implements Renderable {
		protected final String sourceId;

		protected final String targetId;

		protected String label;

		protected String comment;

		protected String options;

		public AbstractAssociation(String sourceId, final String targetId) {
			this.sourceId = sourceId;
			this.targetId = targetId;
		}

		public String getLabel() {
			return label;
		}

		public AbstractAssociation setLabel(String label) {
			if (label != null && label.length() > 0) {
				this.label = label;
			}
			return this;
		}

		public String getComment() {
			return comment;
		}

		public AbstractAssociation setComment(String comment) {
			this.comment = comment;
			return this;
		}

		public String getOptions() {
			return options;
		}

		public AbstractAssociation setOptions(String options) {
			this.options = options;
			return this;
		}

		public String getTargetId() {
			return targetId;
		}

		/**
		 * @return true if this Association is equal to the given Association
		 */
		public boolean equals(Object arg0) {
			if (!(arg0 instanceof AbstractAssociation)) {
				return false;
			}
			final AbstractAssociation other = (AbstractAssociation) arg0;
			if (this == other) {
				return true;
			}
			return other.sourceId.equals(sourceId) && other.targetId.equals(targetId);
		}

		public int hashCode() {
			return sourceId.hashCode() ^ targetId.hashCode();
		}

	}

	/**
	 * Represents an association from a node A to a node B
	 */
	public static class Association extends AbstractAssociation {

		public Association(String sourceId, String targetId) {
			super(sourceId, targetId);
		}

		public String render() {
			final StringBuffer out = new StringBuffer();
			final String displayLabel = label == null ? null : "label=\"" + label + "\"";
			out.append(DotRenderer.edge(sourceId, targetId, comment, displayLabel, options));
			return out.toString();
		}

		public String toString() {
			return "Association from " + sourceId + " to " + targetId;
		}
	}

}
