package main.java;

import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private final Callable<Boolean> oxygen = this::releaseOxygen;
    private final Callable<Boolean> hydrogen = this::releaseHydrogen;

    public WaterMolecule(String atoms) {
        List<Callable<Boolean>> tasks = generateTaskList(atoms);
        int moleculeCount = tasks.size();
        if (moleculeCount <= 0) return;
        ExecutorService poolExecutor = Executors.
                newFixedThreadPool(moleculeCount * (ALL_ATOMS_IN_MOLECULE));
        try {
            poolExecutor.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        poolExecutor.shutdown();
    }

    private List<Callable<Boolean>> generateTaskList(String atoms) {
        Function<Character, Callable<Boolean>> toCallable = (ch) -> (ch == 'H') ? hydrogen : oxygen;
        return atoms.chars().
                mapToObj(i -> (char) i).
                filter(ch -> ch == 'H' || ch == 'O').map(toCallable).collect(Collectors.toList());
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
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void printMolecule() {
        System.out.print(buffer.toString() + " ");
        buffer.delete(0, buffer.length());
    }
}
