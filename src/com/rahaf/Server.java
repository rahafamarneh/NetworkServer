package com.rahaf;

public class Server {
    public double add(double x, double y){
        return x+y;
    }
    public long fact(int a1) {
        long fac = 1;
        for (int i = 1; i <= a1; i++) {
            fac = fac * i;
        }
        return fac;
    }

}
