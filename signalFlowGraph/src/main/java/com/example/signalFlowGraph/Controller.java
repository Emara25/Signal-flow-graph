package com.example.signalFlowGraph;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
public class Controller {
    public Loops graph;
    public NonTouchingLoops nLoops;
    public forwardPaths fPaths;
    public delta delta;
    public double bigDelta;
    public double tf;
    public List<List<Integer>> forwardPaths;
    public List<List<Integer>> cycles;
    public List<Double> pathsDelta;
    public List<List<String>> notTouching;

    @PostMapping(value = "/sendGraph")
    public void adjacencyMatrix(@RequestBody Request data) {

        int des=data.getDes();
        int source=data.getSource();
        double[][]adjacencyMatrix =data.getAdjacencyMatrix(); 
        System.out.println(adjacencyMatrix.length);                            
        this.graph = new Loops(adjacencyMatrix);
        this.cycles = graph.findCycles();
        Map<List<Integer>,Double> loops = graph.getLoops(cycles);


        nLoops = new NonTouchingLoops();
        List<List<Integer>> nonTouched = nLoops.getNonTouching(new HashMap<>(loops),this.cycles,adjacencyMatrix.length);
        this.notTouching = nLoops.getNonTouched(nonTouched,this.cycles);


        fPaths = new forwardPaths(adjacencyMatrix, source, des);
        fPaths.getFpaths();
        this.forwardPaths = fPaths.getForwardpaths();


        delta = new delta(adjacencyMatrix ,nonTouched, loops, fPaths.getFpaths(), cycles);
        this.bigDelta = delta.calcDelta();
        this.pathsDelta = new ArrayList<>();
        for (Map.Entry<List<Integer>,Double> e : fPaths.getFpaths().entrySet()){
            this.pathsDelta.add(delta.deltaFpath(e.getKey()));
        }

        this.tf = delta.calculate(source,des);



    }
    @GetMapping(value = "/forwardPaths")
    public List<List<Integer>>getForwardPaths(){
        return this.forwardPaths;
    }
    @GetMapping(value = "/loops")
    public List<List<Integer>>getLoops(){
        return this.cycles;
    }
    @GetMapping(value = "/nonTouchingLoops")
    public List<List<String>>getNonTouchingLoops(){
        return this.notTouching;
    }
    @GetMapping(value = "/bigDelta")
    public double getBigDelta(){
        return this.bigDelta;
    }
    @GetMapping(value = "/pathsDelta")
    public List<Double> getPathsDelta(){
        return this.pathsDelta;
    }
    @GetMapping(value = "/transferFunction")
    public double getTf(){
        return this.tf;
    }

}
