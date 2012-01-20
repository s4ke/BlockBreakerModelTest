package de.hotware.model.generator.test;

import java.util.Scanner;

import de.hotware.blockbreaker.model.Level;
import de.hotware.blockbreaker.model.generator.LevelGenerator;
import de.hotware.blockbreaker.util.misc.Randomizer;

public class GeneratorTest {
	
	private int mCount;
	private int mNumberOfIterations;
	private int mNumberOfFailures;
	private int mNumberOfPasses;
	private IEndListener mEndListener;
	
	public GeneratorTest(int pCount, IEndListener pEndListener) {
		this.mCount = pCount;
		this.mEndListener = pEndListener;
	}
	
	private synchronized void incrementNumberOfIterations() {
		++this.mNumberOfIterations;
		notifyAll();
		if(this.mNumberOfIterations == this.mCount) {
			this.mEndListener.onEnd(this.mNumberOfPasses, this.mNumberOfFailures);
		}
	}
	
	private synchronized void incrementNumberOfFailures() {
		++this.mNumberOfFailures;
		notifyAll();
	}
	
	private synchronized void incrementNumberOfPasses() {
		++this.mNumberOfPasses;
		notifyAll();
	}
	
	private synchronized int getNumberOfIterations() {
		int ret = this.mNumberOfIterations;
		notifyAll();
		return ret;
	}
	
	public void start(int pNumberOfThreads) {
		for(int i = 0; i < pNumberOfThreads; ++i) {
			new GeneratorThread().start();
		}
	}
	
	private class GeneratorThread extends Thread{
		
		public void run() {
			while(GeneratorTest.this.getNumberOfIterations() < GeneratorTest.this.mCount) {
				Level level = LevelGenerator.createRandomLevel(16);
				if(level.getReplacementList().size() == 0 || level.checkWin()) {
					GeneratorTest.this.incrementNumberOfFailures();
				} else {
					GeneratorTest.this.incrementNumberOfPasses();
				}
				GeneratorTest.this.incrementNumberOfIterations();
			}
			System.out.println(this + " finished.");
		}
		
	}
	
	private interface IEndListener {
		public void onEnd(int pPasses, int pFailures);
	}
	
	public static void main(String args[]) {
		
		Scanner sc = new Scanner(System.in);
		
		System.out.println("How many Iterations?");
		int numberOfIterations = sc.nextInt();
		
		System.out.println("How many Threads?");
		int numberOfThreads = sc.nextInt();
		
		Randomizer.setSeed(213481238); //NOT chosen by fair dice roll.
		
		final long preTime = System.nanoTime();
		
		GeneratorTest test = new GeneratorTest(numberOfIterations, new IEndListener() {

				@Override
				public void onEnd(int pPasses, int pFailures) {
					long afterTime = System.nanoTime();
					System.out.println("Done.");
					System.out.println("The Benchmark took: " + (afterTime - preTime)/1000/1000/(double)1000 + "sec.");
				}
					
			}
		);
		
		System.out.println("Starting Benchmark using BlockBreakerModel v0.1a...");
		test.start(numberOfThreads);		
	}

}
