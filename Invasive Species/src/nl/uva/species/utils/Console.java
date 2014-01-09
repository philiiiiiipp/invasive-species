package nl.uva.species.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * A console to execute commands in.
 */
public class Console {

    /** The number counting the amount of consoles opened */
    private static int sConsoleCount = 0;

    /** The thread running the process */
    final Thread mProcessThread;

    /** The thread reading the process's results */
    final Thread mResultThread;

    /** The builder that can start the process */
    ProcessBuilder mBuilder;

    /** The process in which the command is executed */
    Process mProcess;

    /** The output in which to write new commands */
    BufferedWriter mOutput;

    /** The input from which to get the results */
    BufferedReader mInput;

    /** How many-th console this is */
    final private int mConsoleCount;

    /** The prefix to use for console */
    final private String mPrefix;

    /** Whether or not the console is currently starting the process */
    private boolean mStarting = true;

    /** Whether or not the console is currently running */
    private boolean mRunning = false;

    /**
     * Starts a new console while executing the specified command within a shell.
     * 
     * @param command
     *            The command to execute
     */
    public Console(final String command) {
        final Thread mainThread = Thread.currentThread();

        // Keep track of which console we're using
        mConsoleCount = ++sConsoleCount;
        mPrefix = "[" + mConsoleCount + "] ";

        // Prepare the process to be started
        mBuilder = new ProcessBuilder("cmd", "/C", command);
        mBuilder.redirectErrorStream(true);

        // Prepare the threads that run and keep track of the process
        mProcessThread = new Thread(new ProcessRunner(mainThread));
        mResultThread = new Thread(new ResultReader());

        // Execute the command
        mProcessThread.start();

        // Wait for the process to be started (doesn't require command to be finish)
        synchronized (mainThread) {
            if (mStarting) {
                try {
                    mainThread.wait();
                } catch (final InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Execute a command in the opened process.
     * 
     * @param command
     */
    public void exec(final String command) {
        if (!mRunning) {
            System.out.println(mPrefix + "Cannot execute a command in a stopped console");
        }

        try {
            mOutput.write(command);
            mOutput.flush();
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Stops the running process.
     */
    public void stop() {
        mProcess.destroy();
    }

    /**
     * The runnable containing the process to run in parallel.
     */
    private class ProcessRunner implements Runnable {

        /** The parent thread for this process */
        final Thread mParentThread;

        /**
         * Prepares a new thread that a process can run in.
         * 
         * @param parent
         *            The thread starting this one
         */
        public ProcessRunner(final Thread parent) {
            mParentThread = parent;
        }

        @Override
        public void run() {
            try {
                // Start process and retrieve streams
                mProcess = mBuilder.start();
                mOutput = new BufferedWriter(new OutputStreamWriter(mProcess.getOutputStream()));
                mInput = new BufferedReader(new InputStreamReader(mProcess.getInputStream()));

                // Keep track of the output
                mRunning = true;
                mResultThread.start();

                // Notify that the process has started
                synchronized (mParentThread) {
                    mStarting = false;
                    mParentThread.notify();
                }

                // Wait for the process to finish
                try {
                    mProcess.waitFor();
                } catch (final InterruptedException ex) {
                    ex.printStackTrace();
                }

                // Shut down the console
                mRunning = false;
                mResultThread.stop();

            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Reads the results from the process and prints them.
     */
    private class ResultReader implements Runnable {
        @Override
        public void run() {
            while (mRunning) {
                try {
                    System.out.println(mPrefix + mInput.readLine());
                } catch (final IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
