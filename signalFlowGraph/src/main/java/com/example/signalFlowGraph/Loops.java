package com.example.signalFlowGraph;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

public class Loops {
    private final double[][] adjacencyMatrix;
    protected int vertices;
    public Loops(double[][] adjacencyMatrix) {
        this.adjacencyMatrix = adjacencyMatrix;
        this.vertices = adjacencyMatrix.length;
    }
    public List<List<Integer>> findCycles() {
        List<List<Integer>> cycles = new ArrayList<>();
        Set<List<Integer>> cycleSet = new HashSet<>();
        boolean[] visited = new boolean[vertices];
        for (int i = 0; i < vertices; i++) {
            List<Integer> currentPath = new ArrayList<>();
            dfs(i,i,visited, currentPath, cycles,cycleSet);
        }
        return cycles;
    }
    private void dfs(int startVertex, int vertex, boolean[] visited, List<Integer> currentPath, List<List<Integer>> cycles,Set<List<Integer>> cycleSet) {
        visited[vertex] = true;
        currentPath.add(vertex);

        for (int i = 0; i < vertices; i++) {
            if (adjacencyMatrix[vertex][i] != 0.0) {
                if (i == startVertex) {
                    List<Integer> cycle = new ArrayList<>(currentPath);
                    List<Integer> cycle2 = new ArrayList<>(currentPath);
                    Collections.sort(cycle2);
                    boolean x = cycleSet.add(cycle2);
                    if(x){
                        cycle.add(cycle.get(0));
                        cycles.add(cycle);
                    }
                } else if (!visited[i]) {
                    dfs(startVertex, i, visited, currentPath, cycles,cycleSet);
                }
            }
        }
        currentPath.remove(currentPath.size()-1);
        visited[vertex] = false;
    }
    public Map<List<Integer>,Double> getLoops(List<List<Integer>> cycles){
        Map<List<Integer>,Double> mp = new HashMap<List<Integer>,Double>();
        for(List<Integer> cycle : cycles){
            double gain = 1.0;
            for(int i = 1 ;i<cycle.size();i++){
                gain *= this.adjacencyMatrix[cycle.get(i-1)][cycle.get(i)];
            }
            mp.put(cycle,gain);
        }
        return mp;
    }

    public static void main(String[] args) {
    }
}
