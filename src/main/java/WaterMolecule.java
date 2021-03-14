package main.java;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WaterMolecule extends Thread {
    //constants for design molecule of water
    private static final int OXYGEN_IN_MOLECULE = 1;
    private static final int HYDROGEN_IN_MOLECULE = 2;
    private static final int ALL_ATOMS_IN_MOLECULE = OXYGEN_IN_MOLECULE + HYDROGEN_IN_MOLECULE;
    //barriers:
    private final Semaphore atomOxygen = new Semaphore(OXYGEN_IN_MOLECULE);
    private final Semaphore atomHydrogen = new Semaphore(HYDROGEN_IN_MOLECULE);
    private final CyclicBarrier moleculeBarrier = new CyclicBarrier((ALL_ATOMS_IN_MOLECULE), this::printMolecule);
    //String buffer and threads:
    private volatile StringBuffer buffer = new StringBuffer();
    private final Callable<Boolean> oxygen = () -> releaseOxygen();
    private final Callable<Boolean> hydrogen = () -> releaseHydrogen();

    public WaterMolecule(int moleculeCount) {
        if (moleculeCount <= 0) return;
        ExecutorService poolExecutor = Executors.
                newFixedThreadPool(moleculeCount * (ALL_ATOMS_IN_MOLECULE));
        try {
            poolExecutor.invokeAll(generateTaskList(moleculeCount));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        poolExecutor.shutdown();
    }

    private List<Callable<Boolean>> generateTaskList(int moleculeCount) {
        //generating list with the (moleculeCount * HYDROGEN_IN_MOLECULE) hydrogen type tasks
        List<Callable<Boolean>> tasks = IntStream.range(0, moleculeCount * HYDROGEN_IN_MOLECULE)
                .mapToObj(i -> hydrogen)
                .collect(Collectors.toList());
        //adding to previous list the (moleculeCount * OXYGEN_IN_MOLECULE) oxygen type tasks
        IntStream.range(0, moleculeCount * OXYGEN_IN_MOLECULE).forEach(i -> tasks.add(oxygen));
        //shuffle all tasks
        Collections.shuffle(tasks);
        return tasks;
    }

    private boolean releaseOxygen() {
        return releaseAtom(atomOxygen, "O");
    }

    private boolean releaseHydrogen() {
        return releaseAtom(atomHydrogen, "H");
    }

    private boolean releaseAtom(Semaphore atomBarrier, String atom) {
        try {
            atomBarrier.acquire();
                buffer.append(atom);
                moleculeBarrier.await();
            atomBarrier.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
        return true;
    }

    ;

    private void printMolecule() {
        System.out.print(buffer.toString() + " ");
        buffer.delete(0, buffer.length());
    }
}
