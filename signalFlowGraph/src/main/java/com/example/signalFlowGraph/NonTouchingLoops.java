package com.example.signalFlowGraph;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NonTouchingLoops {
    private List<List<Integer>> powerset = new ArrayList<>();
    private List<List<Integer>> nonTouched = new ArrayList<>();

    private void powerSet(int n) {
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                List<Integer> l = new ArrayList<>();
                l.add(i);
                l.add(j);
                powerset.add(l);
            }
        }
        int m;
        if (n > 15)
            m = 6;
        else
            m = n / 2;
        int start = 0, end = powerset.size();
        while (m-- > 0) {
            List<List<Integer>> temp = new ArrayList<>();
            for (int k = start; k < powerset.size(); k++) {
                List<Integer> l = powerset.get(k);
                for (int i = l.get(l.size()-1) + 1; i < n; i++) {
                    List<Integer> l2 = new ArrayList<>(l);
                    l2.add(i);
                    temp.add(l2);
                }
            }
            powerset.addAll(temp);
            start = end;
            end = powerset.size();
        }
    }
    public List<List<String>> getNonTouched(List<List<Integer>> nloops,List<List<Integer>> cycles){
        List<List<String>> res = new ArrayList<>();
        for(List<Integer> l : nloops){
            List<String> myList = new ArrayList<>();
            String str = "";
            for(int x : l){
                str = cycles.get(x).toString();
                myList.add(str);
            }
            res.add(myList);
        }
        return res;
    }

    public List<List<Integer>> getNonTouching(Map<List<Integer>, Double> mp,List<List<Integer>> cycles, int n) {

        powerSet(cycles.size()); // calculate the different n-combination of loops

        // represent the loops in the form of bitwise sets
        boolean[][] arr = new boolean[n][n];
        int j = 0;
        for (List<Integer> cycle : cycles) {
            for (Integer x : cycle) {
                arr[j][x] = true;
            }
            j++;
        }
        // check for all different n-combinations in the powerset list if it is non-touching or not
        for (List<Integer> l : powerset) {
            int[] check = new int[n];
            for (int w=0; w<n; w++) {
                check[w] = 0;
            }
            boolean flag = false;
            for (int i = 0; i < l.size(); i++) {
                for (int w = 0; w < n; w++) {
                    if (arr[l.get(i)][w]) {
                        check[w]++;
                    }
                }
            }
            for (int w = 0; w < n; w++) {
                if (check[w] > 1) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                nonTouched.add(l);
            }
        }
        return this.nonTouched;
    }

    public static void main(String[] args) {
        NonTouchingLoops l = new NonTouchingLoops();
        long startTime = System.nanoTime();
        l.powerSet(15);
        long endTime = System.nanoTime();
        System.out.println("size of power set = " + l.powerset.size());

        double t = (endTime-startTime)/1000000.0;
        System.out.println("Time taken = " + t +" Milli Seconds");
    }
}
