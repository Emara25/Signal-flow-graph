package com.example.signalFlowGraph;
import java.util.*;
public class delta {
    private final double[][] adjacencyMatrix;
    private final List<List<Integer>> nonTouched;
    private final Map<List<Integer>,Double> loops;
    private final Map<List<Integer>,Double> fPaths;
    private final List<List<Integer>> cycles;
    private final List<Double> deltas = new ArrayList<>();
    private final List<Double> pathGains = new ArrayList<>();

    public delta(double[][] adjacencyMatrix, List<List<Integer>> nonTouched, Map<List<Integer>,Double> loops,
                     Map<List<Integer>,Double> fPaths, List<List<Integer>> cycles) {
        this.adjacencyMatrix = adjacencyMatrix;
        this.nonTouched = nonTouched;
        this.loops = loops;
        this.fPaths = fPaths;
        this.cycles = cycles;
    }

    public double calcDelta(){
        double delta = 1;
        int sum = 0;
        for (Map.Entry<List<Integer>,Double> e : loops.entrySet()){
            sum += e.getValue();
        }
        delta -= sum;

        int k = 2;
        for (List<Integer> l : nonTouched) {
            int gain = 1;
            for(int i=0;i<l.size();i++){
                if(i != l.size()-1)
                    gain *= loops.get(cycles.get(l.get(i)));
                else
                    gain *= loops.get(cycles.get(l.get(i)));
            }
            if(l.size() != k){
                k++;
            }
            delta += Math.pow(-1, k)*gain;
        }

        return delta;
    }

    public double deltaFpath(List<Integer> Fpath){
        int n = adjacencyMatrix.length;
        double [][] newAdj = new double[n][n];
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++){
                newAdj[i][j] = adjacencyMatrix[i][j];
            }
        }
                ;
        for(int i = 0; i < Fpath.size()-1; i++){
            newAdj[Fpath.get(i)][Fpath.get(i+1)] = 0;
        }
        Loops graph = new Loops(newAdj);
        List<List<Integer>> cycles = graph.findCycles();
        Map<List<Integer>,Double> loops = graph.getLoops(cycles);
        NonTouchingLoops nLoops = new NonTouchingLoops();
        List<List<Integer>> nonTouched = nLoops.getNonTouching(new HashMap<>(loops),cycles,newAdj.length);

        double delta = 1;
        int sum = 0;
        for (Map.Entry<List<Integer>,Double> e : loops.entrySet()){
            sum += e.getValue();
        }
        delta -= sum;

        int k = 2;
        for (List<Integer> l : nonTouched) {
            int gain = 1;
            for(int i=0;i<l.size();i++){
                if(i != l.size()-1)
                    gain *= loops.get(cycles.get(l.get(i)));
                else
                    gain *= loops.get(cycles.get(l.get(i)));
            }
            if(l.size() != k){
                k++;
            }
            delta += Math.pow(-1, k)*gain;
        }

        return delta;
    }
    public double calculate(int source,int des){
        double bigDelta = this.calcDelta();
        for (Map.Entry<List<Integer>,Double> e : fPaths.entrySet()){
            this.deltas.add(deltaFpath(e.getKey()));
        }
        forwardPaths fPaths = new forwardPaths(adjacencyMatrix, source, des);
        for (Map.Entry<List<Integer>,Double> e : fPaths.getFpaths().entrySet()){
            this.pathGains.add(e.getValue());
        }
        double sum = 0;
        for(int i=0;i<pathGains.size();i++){
            sum += pathGains.get(i)*deltas.get(i);
        }
        return sum/bigDelta ;
    }
}
