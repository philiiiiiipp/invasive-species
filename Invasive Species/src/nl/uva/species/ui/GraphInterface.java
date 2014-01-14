package nl.uva.species.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JApplet;
import javax.swing.JFrame;

import nl.uva.species.model.Reach;
import nl.uva.species.model.River;
import nl.uva.species.model.RiverState;
import nl.uva.species.utils.Utilities;

import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.ListenableDirectedGraph;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.types.Observation;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;

@SuppressWarnings("rawtypes")
public class GraphInterface extends JApplet {

	private static final long serialVersionUID = 2202072534703043194L;

	private JGraphXAdapter<String, RelationshipEdge> mJgAdapter;

	private final HashMap<Integer, Set<RelationshipEdge<String>>> mEdges = new HashMap<>();

	private final ListenableGraph<String, RelationshipEdge> mGraph = new ListenableDirectedGraph<String, RelationshipEdge>(
			RelationshipEdge.class);

	private RiverState mRiverState;

	private int[] mActions = null;

	/**
	 * Generate a Graph, filled with test data
	 */
	public GraphInterface() {
		mRiverState = generateTestData1();
	}

	/**
	 * Generate the Graph with a given river state
	 * 
	 * @param riverState
	 */
	public GraphInterface(final RiverState riverState) {
		mRiverState = riverState;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() {

		// create a visualization using JGraph, via an adapter
		mJgAdapter = new JGraphXAdapter<String, RelationshipEdge>(mGraph);

		mxGraphComponent graphComponent = new mxGraphComponent(mJgAdapter);
		getContentPane().add(graphComponent);

		mGraph.addVertex(generateName(mRiverState.getRiver().getRootNode()));

		Set<Reach> temp = new HashSet<>();
		temp.add(mRiverState.getRootReach());

		addOrUpdateChildren(temp, mRiverState.getRiver(), mRiverState.getRiver().getRootNode(), false);

		mxHierarchicalLayout layout = new mxHierarchicalLayout(mJgAdapter);
		layout.execute(mJgAdapter.getDefaultParent());
	}

	public void update(final RiverState riverState) {
		mRiverState = riverState;

		Set<Reach> temp = new HashSet<>();
		temp.add(mRiverState.getRootReach());

		addOrUpdateChildren(temp, mRiverState.getRiver(), mRiverState.getRiver().getRootNode(), true);

		mxHierarchicalLayout layout = new mxHierarchicalLayout(mJgAdapter);
		layout.execute(mJgAdapter.getDefaultParent());
	}

	/**
	 * Show all given actions in the graph.
	 * 
	 * @see GraphInterface#removeActions
	 * 
	 * @param actions
	 */
	public void showActions(final int[] actions) {
		if (mActions != null)
			removeActions();

		mActions = actions;

		for (int i = 0; i < actions.length; ++i) {
			String vertex = generateActionName(i, actions[i]);

			mGraph.addVertex(vertex);

			RelationshipEdge<String> relEdge = new RelationshipEdge<String>(generateName(i), vertex, "Action");

			mGraph.addEdge(generateName(i), vertex, relEdge);
		}

		mxHierarchicalLayout layout = new mxHierarchicalLayout(mJgAdapter);
		layout.execute(mJgAdapter.getDefaultParent());
		this.repaint();
	}

	/**
	 * Remove all showed actions
	 */
	public void removeActions() {
		for (int i = 0; i < mActions.length; ++i) {
			mGraph.removeEdge(generateName(i), generateActionName(i, mActions[i]));
			mGraph.removeVertex(generateActionName(i, mActions[i]));
		}

		mActions = null;
		this.repaint();
	}

	/**
	 * Add all children's to the given reach
	 * 
	 * @param graph
	 * @param reach
	 */
	private void addOrUpdateChildren(final Set<Reach> reaches, final River river, final int currentRiverID,
			final boolean update) {

		Set<Integer> riverChildrens = river.getStructure().get(currentRiverID);
		if (riverChildrens == null)
			return;

		for (Integer riverChild : riverChildrens) {

			if (!update)
				mGraph.addVertex(generateName(riverChild));

			for (Reach reach : reaches) {
				if (reach.getIndex() == riverChild) {
					// Reach belongs to that riverNode
					Set<RelationshipEdge<String>> edges = mEdges.get(riverChild);

					if (update) {
						for (RelationshipEdge<String> edge : edges) {
							if (edge.getParent().equals(generateName(riverChild))
									&& edge.getChild().equals(generateName(currentRiverID))) {
								// correct edge to update
								edge.setLabel(generateHabitat(reach));
								break;
							}
						}
					} else {
						if (edges == null) {
							edges = new HashSet<RelationshipEdge<String>>();
							mEdges.put(riverChild, edges);
						}

						// Create edge
						RelationshipEdge<String> relEdge = new RelationshipEdge<String>(generateName(riverChild),
								generateName(currentRiverID), generateHabitat(reach));

						edges.add(relEdge);

						// Add edge to the graph
						mGraph.addEdge(generateName(riverChild), generateName(currentRiverID), relEdge);
					}

					addOrUpdateChildren(reach.getChildren(), river, riverChild, update);
				}
			}
		}
	}

	/**
	 * Generates a String representation of the habitat
	 * 
	 * @param tamarisk
	 * @param natural
	 * @return A String representation of the habitat
	 */
	private String generateHabitat(final Reach reach) {
		return "T: " + reach.getHabitatsInvaded() + " N: " + reach.getHabitatsNatural();
	}

	/**
	 * Generate the name for the given node
	 * 
	 * @param number
	 * @return The String representation the the node
	 */
	private String generateName(final int node) {
		return "(       " + node + "       )";
	}

	/**
	 * Generate a string representation of the action
	 * 
	 * @param reachID
	 * @param actionNum
	 * @return The String representation of the action
	 */
	private String generateActionName(final int reachID, final int actionNum) {
		switch (actionNum) {
		case Utilities.Not:
			return reachID + ": " + "Nothing";

		case Utilities.Erad:
			return reachID + ": " + "Eradicate";

		case Utilities.Res:
			return reachID + ": " + "Restore";

		case Utilities.EradRes:
			return reachID + ": " + "Erad+Rest";
		default:
			return "Don't send invalid Actions!";
		}
	}

	/**
	 * Generate layout test data.
	 * 
	 * @return A dummy RiverState object
	 */
	private static RiverState generateTestData1() {

		River river = new River(
				new TaskSpec(
						"VERSION RL-Glue-3.0 PROBLEMTYPE non-episodic DISCOUNTFACTOR 0.9 OBSERVATIONS INTS (28 1 3) ACTIONS INTS (7 1 4) REWARDS (-10000 -16.1) EXTRA [(0, 7), (1, 4), (2, 6), (3, 6), (4, 0), (5, 4), (6, 0)] BUDGET 100 by Majid Taleghan."));

		Observation observation = new Observation();
		int[] temp = { 1, 1, 1, 3, 3, 1, 2, 3, 2, 1, 1, 1, 1, 2, 1, 2, 3, 1, 1, 3, 3, 2, 1, 1, 2, 3, 2, 2 };
		observation.intArray = temp;

		return new RiverState(river, observation);
	}

	/**
	 * Generate layout test data. Different from generateTestData1 in reach 0.
	 * 
	 * @return A dummy RiverState object
	 */
	@SuppressWarnings("unused")
	private static RiverState generateTestData2() {

		River river = new River(
				new TaskSpec(
						"VERSION RL-Glue-3.0 PROBLEMTYPE non-episodic DISCOUNTFACTOR 0.9 OBSERVATIONS INTS (28 1 3) ACTIONS INTS (7 1 4) REWARDS (-10000 -16.1) EXTRA [(0, 7), (1, 4), (2, 6), (3, 6), (4, 0), (5, 4), (6, 0)] BUDGET 100 by Majid Taleghan."));

		Observation observation = new Observation();
		int[] temp = { 1, 1, 1, 1, 3, 1, 2, 3, 2, 1, 1, 1, 1, 2, 1, 2, 3, 1, 1, 3, 3, 2, 1, 1, 2, 3, 2, 2 };
		observation.intArray = temp;

		return new RiverState(river, observation);
	}

	/**
	 * An alternative starting point for this demo, to also allow running this applet as an application.
	 * 
	 * @param args
	 *            ignored.
	 */
	public static void main(final String[] args) {
		GraphInterface applet = new GraphInterface();
		applet.init();

		JFrame frame = new JFrame();
		frame.getContentPane().add(applet);
		frame.setTitle("The invasive species domain");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}