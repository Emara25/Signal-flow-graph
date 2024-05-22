package com.example.signalFlowGraph;
import java.util.*;
public class forwardPaths {
    private final double[][] adjacencyMatrix;
    protected int vertices;
    private final int source;
    private final int destination;
    public List<List<Integer>> forwardpaths;

    public forwardPaths(double[][] adjacencyMatrix, int source, int destination) {
        this.adjacencyMatrix = adjacencyMatrix;
        this.vertices = adjacencyMatrix.length;
        this.source = source;
        this.destination = destination;
    }
    public List<List<Integer>> getForwardpaths(){
        return this.forwardpaths;
    }


    public Map<List<Integer>,Double> getFpaths(){
        List<List<Integer>> paths = new ArrayList<>();
        boolean[] visited = new boolean[vertices];
        List<Integer> path = new ArrayList<>();
        dfsForwardPath(source, destination, adjacencyMatrix, visited, path, paths);
        this.forwardpaths = new ArrayList<>(paths);

        Map<List<Integer>,Double> mp = new HashMap<List<Integer>,Double>();
        for(List<Integer> eachPath : paths){
            double gain = 1.0;
            for(int i = 1 ;i<eachPath.size();i++){
                gain *= this.adjacencyMatrix[eachPath.get(i-1)][eachPath.get(i)];
            }
            mp.put(eachPath,gain);
        }
        return mp;
    }

    public void dfsForwardPath(int node, int destination, double[][] adjacencyMatrix, boolean[] visited,
                               List<Integer> path, List<List<Integer>> paths){
        visited[node] = true;
        path.add(node);
        if (node == destination) {
            paths.add(new ArrayList<>(path));
        }
        else {
            for (int i = 0; i < vertices; i++) {
                if (adjacencyMatrix[node][i] != 0 && !visited[i]) {
                    dfsForwardPath(i, destination, adjacencyMatrix, visited, path, paths);
                }
            }
        }
        path.remove(path.size()-1);
        visited[node] = false;
    }


}
