package yk.supervisor;

public class Supervisor implements Runnable {

    private final AbstractProgram abstractProgram;

    Supervisor(AbstractProgram abstractProgram){
        this.abstractProgram = abstractProgram;
    }



    private void startAbstractProgram(){
        Thread abstractProgramThread = new Thread(abstractProgram);
        abstractProgramThread.start();
        abstractProgram.setState(AbstractProgram.State.RUNNING);
        System.out.println("Статус программы: RUNNING");
    }

    private void stopAbstractProgram(){
        abstractProgram.setState(AbstractProgram.State.STOPPING);
        System.out.println("Статус программы: STOPPING");
        abstractProgram.shotdown();
    }

    private void restartAbstractProgram(){
        stopAbstractProgram();
        startAbstractProgram();
    }



    @Override
    public void run() {
            startAbstractProgram();
            synchronized (abstractProgram){
                try {
                    boolean isFatalError=false;
                    while (!isFatalError) {
                        abstractProgram.wait();
                        AbstractProgram.State state = abstractProgram.getState();
                        switch (state){
                            case STOPPING -> {
                                System.out.println("Текущий статус: STOPPING. Перезапуск...");
                                restartAbstractProgram();
                            }
                            case FATALERROR -> {
                                System.out.println("Текущий статус: FATALERROR. Завершение программы");
                                isFatalError = true;
                            }
                            case UNKNOWN -> {
                                System.out.println("Текущий статус: UNKNOWN. Запуск...");
                                startAbstractProgram();
                            }
                            default -> System.out.println("Текущий статус: " + state);
                        }
                    }
                    stopAbstractProgram();
                } catch (InterruptedException e) {
                    System.out.println("Поток уже прерван(при корректной работе программы вы это сообщение не увидите)");
                }
        }
    }
}
