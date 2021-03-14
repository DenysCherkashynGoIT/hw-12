package main.java.repeat;

@Repeat(3)
public class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("Hello!");
    }
}
