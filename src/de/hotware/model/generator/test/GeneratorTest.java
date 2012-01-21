package de.hotware.model.generator.test;

import java.util.Scanner;

import de.hotware.blockbreaker.model.Level;
import de.hotware.blockbreaker.model.generator.LevelGenerator;
import de.hotware.blockbreaker.util.misc.Randomizer;

public class GeneratorTest {
	
	private int mCount;
	private int mRequestSize;
	private IEndListener mEndListener;
	private GeneratorThread[] mThreads;
	
	public GeneratorTest(int pCount) {
		this.mCount = pCount;
	}
	
	public void setEndListener(IEndListener pEndListener) {
		this.mEndListener = pEndListener;
	}
	
	public synchronized int request() {
		int ret;
		if(this.mCount > this.mRequestSize) {
			this.mCount -= this.mRequestSize;
			ret = this.mRequestSize;
		} else {
			ret = this.mCount;
			this.mCount = 0;
		}
		notify();
		return ret;
	}
	
	public void init(int pNumberOfThreads) {
		if(this.mCount > 20) {
			this.mRequestSize = this.mCount / pNumberOfThreads / 10;
		} else {
			this.mRequestSize = 5;
		}
		this.mThreads = new GeneratorThread[pNumberOfThreads];
		for(int i = 0; i < pNumberOfThreads; ++i) {
			this.mThreads[i] = new GeneratorThread();
		}		
	}
	
	public void start() {
		int numberOfThreads = this.mThreads.length;
		for(int i = 0; i < numberOfThreads; ++i) {
			this.mThreads[i].start();
		}
		for(int i = 0; i < numberOfThreads; ++i) {
			try {
				this.mThreads[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.mEndListener.onEnd(this.mThreads);
	}
	
	private class GeneratorThread extends Thread {
		
		private int mCount;
		private int mNumberOfFailures;
		
		public GeneratorThread() {
			this.mNumberOfFailures = 0;
		}
				
		public void run() {
			Level level;
			while((this.mCount = request()) > 0) {
				while(this.mCount-- > 0) {
					level = LevelGenerator.createRandomLevel(16);
					if(level.getReplacementList().size() == 0 || level.checkWin()) {
						++this.mNumberOfFailures;
					}
				}
			}
			System.out.println(this + " finished.");
		}
		
		public int getNumberOfFailures() {
			return this.mNumberOfFailures;
		}
		
	}
	
	private interface IEndListener {
		public void onEnd(GeneratorThread[] pTr);
	}
	
	public static void main(String args[]) {
		
		Scanner sc = new Scanner(System.in);
		
		System.out.println("How many Iterations?");
		int numberOfIterations = sc.nextInt();
		
		System.out.println("How many Threads?");
		int numberOfThreads = sc.nextInt(); 
		
		Randomizer.setSeed(213481238); //NOT chosen by fair dice roll.
		
		GeneratorTest test = new GeneratorTest(numberOfIterations);
		test.init(numberOfThreads);
		
		final long preTime = System.nanoTime();
		test.setEndListener(new IEndListener() {

				@Override
				public void onEnd(GeneratorThread pTr[]) {
					long afterTime = System.nanoTime();
					System.out.println("Done.");
					int failures = 0;
					for(int i = 0; i < pTr.length; ++i) {
						failures += pTr[i].getNumberOfFailures();
					}
					System.out.println("Number of Failures: " + failures);
					System.out.println("The Benchmark took: " + (afterTime - preTime)/1000/1000/(double)1000 + "sec.");
				}
					
		});		
		System.out.println("Starting Benchmark using BlockBreakerModel v0.1a...");
		test.start();
	}

}
