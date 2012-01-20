package de.hotware.model.generator.test;

import java.util.Scanner;

import de.hotware.blockbreaker.model.Level;
import de.hotware.blockbreaker.model.generator.LevelGenerator;

public class GeneratorTest {
	
	public static void main(String args[]) {
		int numberOfIterations;
		System.out.println("How many Iterations?");
		Scanner sc = new Scanner(System.in);
		numberOfIterations = sc.nextInt();
		int numberOfFailures = 0;
		int numberOfPasses = 0;
		long afterTime;
		long preTime = System.nanoTime();
		System.out.println("Starting Benchmark using BlockBreakerModel v0.1a...");
		for(int i = 0; i < numberOfIterations; ++i) {
			Level level = LevelGenerator.createRandomLevel(16);
			if(level.getReplacementList().size() == 0 || level.checkWin()) {
				++numberOfFailures;
			} else {
				++numberOfPasses;
			}
		} 
		afterTime = System.nanoTime();
		System.out.println("Done.");
		System.out.println("Number of Failures: " + numberOfFailures + "\nNumber of Passes: " + numberOfPasses);
		System.out.println("The Benchmark took: " + (afterTime - preTime)/1000/1000/(double)1000 + "sec.");
	}

}
