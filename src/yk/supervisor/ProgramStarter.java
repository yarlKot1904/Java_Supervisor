package yk.supervisor;

public class ProgramStarter {


    public static void start(){
        AbstractProgram abstractProgram = new AbstractProgram();
        Runnable supervisor = new Supervisor(abstractProgram);
        Thread supervisorThread = new Thread(supervisor);

        supervisorThread.start();

    }
}
