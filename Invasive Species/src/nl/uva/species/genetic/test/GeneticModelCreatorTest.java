package nl.uva.species.genetic.test;

import nl.uva.species.genetic.GeneticModelCreator;
import nl.uva.species.model.River;
import nl.uva.species.model.RiverState;

import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

public class GeneticModelCreatorTest {

	public static void main(final String[] args) {
		River river = new River(
				new TaskSpec(
						"VERSION RL-Glue-3.0 PROBLEMTYPE non-episodic DISCOUNTFACTOR 0.9 OBSERVATIONS INTS (28 1 3) ACTIONS INTS (7 1 4) REWARDS (-10000 -16.1) EXTRA [(0, 7), (1, 4), (2, 6), (3, 6), (4, 0), (5, 4), (6, 0)] BUDGET 100 by Majid Taleghan."));
		GeneticModelCreator g = new GeneticModelCreator(river);

		Observation o;
		Action a;

		o = new Observation();
		o.intArray = new int[] { 1, 1, 2, 1, 1, 2, 1, 2, 1, 2, 1, 1, 2, 2, 1, 2, 1, 1, 1, 2, 1, 2, 2, 1, 1, 1, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 2, 1, 1, 2, 1, 2, 1, 2, 1, 1, 2, 2, 1, 2, 1, 1, 1, 2, 1, 2, 2, 1, 1, 1, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 2, 1, 1, 2, 2, 2, 1, 2, 1, 1, 2, 2, 1, 2, 1, 1, 1, 2, 1, 2, 2, 1, 1, 1, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 2, 1, 1, 2, 2, 2, 1, 2, 1, 1, 2, 2, 1, 2, 1, 1, 1, 2, 2, 2, 2, 2, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 2, 1, 1, 2, 2, 2, 1, 2, 1, 1, 2, 2, 1, 2, 1, 1, 1, 2, 2, 2, 2, 2, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 2, 1, 1, 2, 2, 2, 1, 2, 1, 1, 2, 2, 1, 2, 1, 1, 1, 2, 2, 2, 2, 2, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 2, 1, 1, 2, 1, 2, 1, 2, 1, 1, 2, 2, 1, 2, 1, 1, 1, 2, 2, 2, 2, 2, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 2, 1, 2, 2, 1, 2, 2, 2, 1, 2, 2, 2, 1, 2, 1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 2, 1, 2, 2, 1, 2, 1, 2, 1, 2, 2, 2, 2, 2, 1, 1, 1, 2, 2, 2, 2, 2, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 2, 1, 2, 2, 2, 2, 1, 2, 1, 2, 2, 2, 1, 2, 1, 1, 1, 1, 2, 2, 2, 2, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 2, 1, 2, 2, 2, 2, 1, 2, 1, 2, 2, 2, 1, 2, 1, 1, 1, 1, 2, 2, 2, 1, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 2, 1, 2, 2, 2, 1, 2, 1, 1, 2, 2, 2, 1, 2, 1, 1, 1, 1, 2, 2, 2, 1, 1, 1, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 2, 1, 2, 2, 2, 1, 2, 1, 1, 2, 2, 1, 1, 2, 1, 1, 1, 1, 1, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 1, 1, 2, 2, 2, 1, 2, 1, 1, 2, 2, 2, 1, 2, 1, 2, 1, 2, 1, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 1, 1, 2, 2, 2, 1, 2, 1, 1, 2, 2, 2, 1, 2, 1, 2, 1, 2, 1, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 1, 1, 2, 2, 2, 1, 2, 1, 1, 1, 2, 2, 1, 2, 1, 2, 1, 2, 1, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 1, 1, 2, 2, 2, 1, 2, 1, 1, 1, 2, 2, 1, 2, 1, 2, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 1, 2, 2, 2, 2, 1, 2, 1, 1, 1, 2, 2, 1, 2, 1, 2, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 1, 2, 2, 2, 2, 1, 2, 1, 2, 1, 1, 2, 1, 2, 1, 2, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 2, 2, 2, 2, 1, 1, 2, 2, 1, 1, 2, 1, 2, 1, 2, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 2, 2, 2, 2, 1, 1, 2, 2, 1, 1, 2, 1, 2, 1, 2, 1, 1, 1, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 2, 2, 2, 1, 2, 2, 2, 2, 1, 2, 1, 2, 1, 2, 1, 1, 1, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 2, 1, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 2, 1, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 2, 1, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 2, 1, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 2, 1, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 2, 1, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 1, 1, 2, 2, 2, 1, 2, 1, 2, 2, 2, 2, 2, 2, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 1, 1, 2, 2, 2, 1, 1, 1, 2, 1, 2, 2, 2, 2, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 1, 1, 2, 2, 2, 1, 1, 1, 2, 1, 2, 2, 2, 2, 1, 1, 1, 2, 1, 2, 1, 1, 2, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 1, 1, 2, 2, 2, 1, 1, 1, 2, 1, 2, 2, 2, 2, 1, 1, 1, 2, 1, 1, 1, 1, 2, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 1, 1, 1, 2, 2, 1, 1, 1, 2, 1, 1, 2, 2, 2, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 2, 1, 1, 1, 2, 1, 1, 1, 2, 1, 1, 2, 2, 2, 1, 1, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 2, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 2, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 2, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 2, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 2, 1, 1, 2, 1, 1, 2, 1, 2, 1, 1, 1, 2, 2, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 2, 1, 2, 2, 1, 1, 2, 2, 2, 1, 1, 2, 1, 2, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 2, 1, 2, 2, 1, 1, 2, 2, 2, 1, 1, 2, 1, 2, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 2, 1, 2, 2, 1, 1, 2, 2, 2, 2, 1, 2, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 2, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 2, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 2, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 2, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 2, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 2, 1, 2, 1, 1, 1, 2, 1, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 2, 1, 2, 1, 1, 1, 2, 1, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 2, 1, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 2, 1, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 2, 1, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 2, 1, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 2, 1, 1, 1, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1, 2, 1, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 2, 1, 2, 1, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 2, 1, 2, 1, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 2, 1, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 2, 1, 2, 1, 2, 2, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 2, 1, 2, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 2, 1, 2, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 2, 1, 2, 2, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 2, 1, 2, 2, 1, 2, 2, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 2, 1, 2, 2, 1, 2, 2, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 2, 1, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 1, 2, 1, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 2, 1, 2, 2, 2, 2, 2, 1, 2, 2, 1, 2, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 2, 1, 2, 2, 2, 2, 2, 1, 2, 1, 2, 2, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 2, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 2, 1, 2, 2, 2, 2, 2, 1, 2, 1, 2, 2, 2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 1, 2, 2, 2, 2, 2, 2, 1, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 1, 2, 2, 1, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1, 1, 1, 2, 2, 1, 1, 1, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 1, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 2, 2, 1, 1, 1, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 2, 2, 2, 1, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 2, 2, 1, 1, 1, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 2, 1, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 2, 1, 2, 2, 2, 1, 2, 2, 2, 2, 1, 2, 2, 2, 1, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 2, 1, 2, 1, 2, 1, 2, 2, 2, 2, 1, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 2, 1, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 2, 1, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 2, 1, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 2, 1, 1, 1, 2, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 2, 1, 2, 2, 2, 1, 1, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 2, 1, 1, 1, 2, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 2, 1, 2, 1, 2, 1, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 2, 1, 1, 2, 2, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 2, 1, 2, 1, 2, 1, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 2, 2, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 2, 1, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 2, 2, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 2, 1, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 2, 2, 2, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 2, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 2, 1, 2, 1, 2, 1, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 2, 2, 2, 1, 2, 1, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 2, 1, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 2, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 2, 1, 1, 1, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 2, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 1, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 2, 1, 1, 1, 1, 2, 2, 2, 2, 2, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 2, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 2, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 1, 2, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 1, 1, 2, 2, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 2, 2, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 1, 1, 2, 2, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 2, 2, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 1, 2, 2, 1, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 2, 2, 1, 2, 1, 1, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 2, 1, 2, 2, 2, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 2, 2, 1, 2, 1, 1, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 2, 1, 2, 2, 2, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);
		o = new Observation();
		o.intArray = new int[] { 1, 2, 2, 1, 1, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 2, 1, 2, 2, 2, 2, 2 };
		g.addRiverState(new RiverState(river, o));
		g.getBestModel(river);
		a = new Action();
		a.intArray = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		g.addAction(a);

	}

}
