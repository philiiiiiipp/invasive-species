/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
/* ----------------------
 * JGraphAdapterDemo.java
 * ----------------------
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
 *
 * Original Author:  Barak Naveh
 * Contributor(s):   -
 *
 * $Id$
 *
 * Changes
 * -------
 * 03-Aug-2003 : Initial revision (BN);
 * 07-Nov-2003 : Adaptation to JGraph 3.0 (BN);
 *
 */
package org.jgrapht.demo;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JApplet;
import javax.swing.JFrame;

import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;

// resolve ambiguity

/**
 * A demo applet that shows how to use JGraph to visualize JGraphT graphs.
 * 
 * @author Barak Naveh
 * @since Aug 3, 2003
 */
public class JGraphAdapterDemo extends JApplet {

	private static final long serialVersionUID = 3256444702936019250L;

	private JGraphXAdapter<String, RelationshipEdge> jgAdapter;

	/**
	 * An alternative starting point for this demo, to also allow running this applet as an application.
	 * 
	 * @param args
	 *            ignored.
	 */
	public static void main(final String[] args) {
		JGraphAdapterDemo applet = new JGraphAdapterDemo();
		applet.init();

		JFrame frame = new JFrame();
		frame.getContentPane().add(applet);
		frame.setTitle("JGraphT Adapter to JGraph Demo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() {
		// create a JGraphT graph
		ListenableGraph<String, RelationshipEdge> g = new ListenableDirectedGraph<String, RelationshipEdge>(
				RelationshipEdge.class);

		// create a visualization using JGraph, via an adapter
		jgAdapter = new JGraphXAdapter<String, RelationshipEdge>(g);

		mxGraphComponent graph = new mxGraphComponent(jgAdapter);
		getContentPane().add(new mxGraphComponent(jgAdapter));

		List<Tuple<Integer, Integer>> parse = new ArrayList<>();
		parse.add(new Tuple<Integer, Integer>(0, 7));
		parse.add(new Tuple<Integer, Integer>(1, 4));
		parse.add(new Tuple<Integer, Integer>(2, 6));
		parse.add(new Tuple<Integer, Integer>(3, 6));
		parse.add(new Tuple<Integer, Integer>(4, 0));
		parse.add(new Tuple<Integer, Integer>(5, 4));
		parse.add(new Tuple<Integer, Integer>(6, 0));

		List<String> vertex = new ArrayList<>();
		for (int i = 0; i < 8; ++i) {
			String v = "(           " + i + "           )";

			vertex.add(v);
			g.addVertex(v);
		}

		List<RelationshipEdge<String>> relations = new ArrayList<>();
		for (Tuple<Integer, Integer> tuple : parse) {
			RelationshipEdge<String> rel = new RelationshipEdge<String>(vertex.get(tuple.left),
					vertex.get(tuple.right), "T: 2 N: 2");
			relations.add(rel);

			g.addEdge(vertex.get(tuple.left), vertex.get(tuple.right), rel);
		}

		mxHierarchicalLayout layout = new mxHierarchicalLayout(jgAdapter);
		layout.execute(jgAdapter.getDefaultParent());

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

	public class Tuple<L, R> {
		public final L left;
		public final R right;

		public Tuple(final L left, final R right) {
			this.left = left;
			this.right = right;
		}
	}

	// String v1 = "v1";
	// String v2 = "v2";
	// String v3 = "v3";
	// String v4 = "v4";
	//
	// // add some sample data (graph manipulated via JGraphT)
	// g.addVertex(v1);
	// g.addVertex(v2);
	// g.addVertex(v3);
	// g.addVertex(v4);
	//
	// g.addEdge(v1, v2);
	// g.addEdge(v2, v3);
	// g.addEdge(v3, v1);
	// g.addEdge(v4, v3);
	//
	// // position vertices nicely within JGraph component
	// positionVertexAt(v1, 130, 40);
	// positionVertexAt(v2, 60, 200);
	// positionVertexAt(v3, 310, 230);
	// positionVertexAt(v4, 380, 70);
	//
	// // that's all there is to it!...
	// }
	//
}

// End JGraphAdapterDemo.java
