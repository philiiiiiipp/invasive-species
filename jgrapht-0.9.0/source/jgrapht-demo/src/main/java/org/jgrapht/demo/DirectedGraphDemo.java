package org.jgrapht.demo;

import java.util.ArrayList;

import org.jgraph.JGraph;
import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

public class DirectedGraphDemo {
	private static final String friend = "friend";
	private static final String enemy = "enemy";
	private static JGraphModelAdapter<String, RelationshipEdge> jgAdapter;

	public static void main(final String[] args) {
		DirectedGraph<String, RelationshipEdge> graph = new DirectedMultigraph<String, RelationshipEdge>(
				new ClassBasedEdgeFactory<String, RelationshipEdge>(RelationshipEdge.class));

		jgAdapter = new JGraphModelAdapter<String, RelationshipEdge>(graph);

		JGraph jgraph = new JGraph(jgAdapter);

		ArrayList<String> people = new ArrayList<String>();
		people.add("John");
		people.add("James");
		people.add("Sarah");
		people.add("Jessica");

		// John is everyone's friend
		for (String person : people) {
			graph.addVertex(person);

			if (person.equals(people.get(0)))
				continue;

			graph.addEdge(people.get(0), person, new RelationshipEdge<String>(people.get(0), person, friend));
		}

		// Apparently James doesn't really like John
		graph.addEdge("James", "John", new RelationshipEdge<String>("James", "John", enemy));

		// Jessica is Sarah and James's friend
		graph.addEdge("Jessica", "Sarah", new RelationshipEdge<String>("Jessica", "Sarah", friend));
		graph.addEdge("Jessica", "James", new RelationshipEdge<String>("Jessica", "James", friend));

		// But Sarah doesn't really like James
		graph.addEdge("Sarah", "James", new RelationshipEdge<String>("Sarah", "James", enemy));

		for (RelationshipEdge edge : graph.edgeSet()) {
			if (edge.toString().equals("enemy")) {
				System.out.printf(edge.getV1() + "is an enemy of " + edge.getV2() + "\n");
			} else if (edge.toString().equals("friend")) {
				System.out.printf(edge.getV1() + " is a friend of " + edge.getV2() + "\n");
			}
		}
	}

	public static class RelationshipEdge<V> extends DefaultEdge {
		private final V v1;
		private final V v2;
		private final String label;

		public RelationshipEdge(final V v1, final V v2, final String label) {
			this.v1 = v1;
			this.v2 = v2;
			this.label = label;
		}

		public V getV1() {
			return v1;
		}

		public V getV2() {
			return v2;
		}

		@Override
		public String toString() {
			return label;
		}
	}
}