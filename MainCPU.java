package com.vin.server.monitor;

public class MainCPU {
	public static PerformanceMonitor monitor = null;


    public static void main(String[] args) {
        monitor = new PerformanceMonitor();
        for(int i=0 ; i<10000 ; i++){

            start();
            double usage = monitor.getCpuUsage();
            if(usage!=0)System.out.println("Current CPU usage in pourcentage : "+(usage*100));
       	 try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

    private static void start() {
        int count=0;
        for(int i=0 ; i<100000 ; i++){
            count=(int) Math.random()*100;
        }
    }
}
