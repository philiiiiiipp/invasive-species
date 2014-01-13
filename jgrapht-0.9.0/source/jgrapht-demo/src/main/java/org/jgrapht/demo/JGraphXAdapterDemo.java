/* This program and the accompanying materials are dual-licensed under
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
package org.jgrapht.demo;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JApplet;
import javax.swing.JFrame;

import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.swing.mxGraphComponent;

/**
 * A demo applet that shows how to use JGraphX to visualize JGraphT graphs. Applet based on JGraphAdapterDemo.
 * 
 * @since July 9, 2013
 */
public class JGraphXAdapterDemo extends JApplet {

	private static final long serialVersionUID = 2202072534703043194L;
	private static final Dimension DEFAULT_SIZE = new Dimension(530, 320);

	private JGraphXAdapter<String, DefaultEdge> jgxAdapter;

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
		ListenableGraph<String, DefaultEdge> g = new ListenableDirectedGraph<String, DefaultEdge>(DefaultEdge.class);

		// create a visualization using JGraph, via an adapter
		jgxAdapter = new JGraphXAdapter<String, DefaultEdge>(g);

		getContentPane().add(new mxGraphComponent(jgxAdapter));
		resize(DEFAULT_SIZE);

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
			String v = "( " + i + " )";

			vertex.add(v);
			g.addVertex(v);
		}

		for (Tuple<Integer, Integer> tuple : parse) {
			g.addEdge(vertex.get(tuple.left), vertex.get(tuple.right));
		}

		// positioning via jgraphx layouts
		mxCircleLayout layout = new mxCircleLayout(jgxAdapter);
		layout.execute(jgxAdapter.getDefaultParent());

		// that's all there is to it!...
	}

	public class Tuple<L, R> {
		public final L left;
		public final R right;

		public Tuple(final L left, final R right) {
			this.left = left;
			this.right = right;
		}
	}
}

// End JGraphXAdapterDemo.java
