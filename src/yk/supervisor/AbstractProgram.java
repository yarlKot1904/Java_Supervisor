package yk.supervisor;

import java.util.Random;

public class AbstractProgram implements Runnable {
    private State state = State.UNKNOWN;
    private volatile boolean shotdown = false;

    public AbstractProgram(){

    }

    public void shotdown(){
        shotdown = true;
    }

    public final State getState(){
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public void run() {
        Runnable daemon = new AbstractProgram.AbstractProgramDaemon(this);
        Thread daemonThread = new Thread(daemon);
        daemonThread.setDaemon(true);
        daemonThread.start();
        while (!shotdown) {
            Thread.yield();
        }
    }

    public enum State {
        FATALERROR,
        RUNNING,
        STOPPING,
        UNKNOWN
    }

    public static final class AbstractProgramDaemon implements Runnable
    {
        private final AbstractProgram program;

        AbstractProgramDaemon(AbstractProgram program){
            this.program = program;
        }

        private static State getRandomState(){
            return State.values()[(int)(Math.random()*(State.values().length - 1))];
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()){
                try {
                    Thread.sleep(3000);
                    synchronized (program){
                        program.setState(getRandomState());
                        program.notify();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

            }
        }
    }
}
