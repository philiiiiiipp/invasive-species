package nl.uva.species;

import java.io.IOException;

import nl.uva.species.utils.Console;

public class Initialiser {

    public static void main(String[] args) {
        final Console console = new Console("python src/InvasiveEnvironment.py");

        try {
            Thread.sleep(10000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

}
