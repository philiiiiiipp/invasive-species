package nl.uva.species;

import nl.uva.species.utils.Console;

public class InitialiserMac {
	public static void main(final String[] args) {
		final Console console = new Console("/bin/sh", "mac/a.sh");

		try {
			Thread.sleep(10000);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}
}
