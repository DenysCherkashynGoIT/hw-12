package main.java;

import main.java.repeat.CustomThreadPoolExecutor;
import main.java.repeat.MyRunnable;

public class Application {


    public static void main(String[] args) throws InterruptedException {
        System.out.println("/************   Задание № 1  ************/");
        new WaterMolecule("HOH");
        System.out.println();
        new WaterMolecule("OOHHHH");
        System.out.println("\n\n/************   Задание № 2 ************/");

        CustomThreadPoolExecutor customThreadPoolExecutor = new CustomThreadPoolExecutor(10);
        customThreadPoolExecutor.execute(new MyRunnable());
        customThreadPoolExecutor.shutdown();
    }


}
