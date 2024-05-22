// import java.util.HashMap;
// import java.util.Map;
// import java.util.List;
// import com.example.signalFlowGraph.*;

// public class Main {
//     public static void main(String[] args) {
//        /* double[][] adjacencyMatrix = {
//                 {0, 2, 0, 0, 0},
//                 {3, 0, 4, 0, 0},
//                 {0, 0, 0, 5, 0},
//                 {0, 0, 7, 0, 1},
//                 {0, 0, 6, 0, 0}
//         };
//        /* double[][] adjacencyMatrix = {
//                 {0, 1, 0, 0, 0, 0},
//                 {2, 0, 1, 0, 0, 0},
//                 {0, 0, 0, 1, 0, 0},
//                 {0, 0, 4, 0, 1, 0},
//                 {0, 0, 0, 0, 0, 1},
//                 {0, 0, 0, 0, 6, 0}
//         };*/
//       double[][] adjacencyMatrix = {
//                 {0, 1, 0, 0, 0, 1, 0, 0},
//                 {0, 0, 2, 0, 0, 0, 0, 0},
//                 {0, 2, 0, 3, 0, 0, 0, 0},
//                 {0, 0, 0, 0, 4, 0, 0, 0},
//                 {0, 0, 0, 2, 0, 5, 0, 0},//
//                 {0, 0, 0, 0, 0, 0, 6, 0},
//                 {0, 0, 0, 0, 0, 0, 0, 7},
//                 {0, 8, 0, 0, 0, 0, 1, 0}
//         };
//         Loops graph = new Loops(adjacencyMatrix);
//         long startTime = System.nanoTime();
//         List<List<Integer>> cycles = graph.findCycles();
//         Map<List<Integer>,Double> loops = graph.getLoops(cycles);
//         long endTime = System.nanoTime();
//         for (Map.Entry<List<Integer>,Double> e : loops.entrySet()){
//             System.out.println("Loop: " + e.getKey() + ", gain = " + e.getValue());
//         }
//         double t = (endTime-startTime)/1000.0;
//         System.out.println("Time taken = " + t +" Micro seconds");

//         NonTouchingLoops nLoops = new NonTouchingLoops();
//         List<List<Integer>> nonTouched = nLoops.getNonTouching(new HashMap<>(loops),cycles,adjacencyMatrix.length);
//         System.out.println("\nthere is " + nonTouched.size() + " non-touching-loops");
//         for (List<Integer> l : nonTouched) {
//             for(int i=0;i<l.size();i++){
//                 if(i != l.size()-1)
//                     System.out.print(cycles.get(l.get(i)) + " and");
//                 else
//                     System.out.println(" "+cycles.get(l.get(i)));
//             }
//         }

//         forwardPaths fPaths = new forwardPaths(adjacencyMatrix, 0, 5);
//         for (Map.Entry<List<Integer>,Double> e : fPaths.getFpaths().entrySet()){
//             System.out.println("Path: " + e.getKey() + ", gain = " + e.getValue());
//         }

//         delta delta = new delta(adjacencyMatrix ,nonTouched, loops, fPaths.getFpaths(), cycles);
//         System.out.println("overall delta = "+delta.calcDelta());
//         for (Map.Entry<List<Integer>,Double> e : fPaths.getFpaths().entrySet()){
//             System.out.println("delta path = "+delta.deltaFpath(e.getKey()));
//         }
//         double tf = delta.calculate(0,5);
//         System.out.println("Transfer function = "+tf);
//     }
// }