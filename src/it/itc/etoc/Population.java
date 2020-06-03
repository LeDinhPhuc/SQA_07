/**
 * @(#) Population.java	v. 1.0 - July 20, 2004
 *
 * This software was written by Paolo Tonella (tonella@itc.it) at ITC-irst,
 * Centro per la Ricerca Scientifica e Tecnlogica.
 *
 * Distributed under the Gnu GPL (General Public License). See GPL.TXT
 */

package it.itc.etoc;

import java.util.*;

import javax.sql.CommonDataSource;

import it.itc.etoc.Chromosome;
import it.itc.etoc.ChromosomeFormer;
import it.itc.etoc.DataFlowTarget;
import it.itc.etoc.Population;
import it.itc.etoc.Target;
import it.itc.etoc.TestCaseExecutor;
import it.itc.etoc.TestGenerator;

class Population {
	/**
	 * Used to randomly select individuals for new population.
	 */
	static Random randomGenerator = new Random();

	/**
	 * An individual is an object of class Chromosome.
	 *
	 * List<Chromosome>
	 */
	List individuals;

	/**
	 * ChromosomeFormer is responsible for the creation of each single individual
	 * and its evolution/recombination.
	 */
	static ChromosomeFormer chromosomeFormer;

	/**
	 * Measures the total fitness reached by current population.
	 */
	int overallFitness;

	/**
	 * Main parameter of the genetic algorithm: number of individuals (chromosomes)
	 * in population.
	 */
	public static int populationSize = 10; // mandatory: even number

	/**
	 * Convenience constructor. Assumes a list of chromosomes already exists.
	 */
	public Population(List id) {
		individuals = id;
	}

	/**
	 * Static method to generate a new ChromosomeFormer.
	 *
	 * @param signFile File with method signatures. Last method determines the final
	 *                 call in chromosome.
	 */
	public static void setChromosomeFormer(String signFile) {
		chromosomeFormer = new ChromosomeFormer();
		chromosomeFormer.readSignatures(signFile);
	}

	/**
	 * Static method to generate a new population with given target method.
	 * 
	 * @param method Method to be called by last statement in chromosome.
	 * @return object of class Population.
	 */
	public static Population generateRandomPopulation() {
		List individs = new LinkedList();
		for (int j = 0; j < Population.populationSize; j++) {
			chromosomeFormer.buildNewChromosome();
			individs.add(chromosomeFormer.getChromosome());
		}
		return new Population(individs);
	}

	/**
	 * Random mutation/recombination of individuals in Population
	 */
	public void evolve() {
		List individs = new LinkedList();
		for (int i = 0; i < populationSize; i++) {
			int j = (i + 1) % populationSize;
			Chromosome id1 = (Chromosome) ((Chromosome) individuals.get(i)).clone();
			Chromosome id2 = (Chromosome) ((Chromosome) individuals.get(j)).clone();
			chromosomeFormer.setCurrentChromosome(id1);
			chromosomeFormer.mutateChromosome(id2);
			individs.add(chromosomeFormer.getChromosome());
		}
		individuals = individs;
	}

	/**
	 * Selects individuals for new population.
	 * 
	 * Individuals with higher fitness are more likely to be chosen. Individuals are
	 * assumed to be sorted by decreasing fitness.
	 */
	public Population generateNewPopulation() {
		List newIndividuals = new LinkedList();
		for (int i = 0; i < populationSize; i++) {
			Chromosome id = null;
			if (overallFitness == 0) {
				int j = randomGenerator.nextInt(populationSize);
				id = (Chromosome) individuals.get(j);
			} else {
				int r = randomGenerator.nextInt(overallFitness);
				Iterator it = individuals.iterator();
				int prevFitness, cumFitness = 0;
				while (it.hasNext()) {
					id = (Chromosome) it.next();
					prevFitness = cumFitness;
					cumFitness += id.getFitness();
					if (r >= prevFitness && r < cumFitness)
						break;
				}
			}
			id = (Chromosome) id.clone();
			newIndividuals.add(id);
		}
		Population newPopulation = new Population(newIndividuals);
		newPopulation.evolve();
		return newPopulation;
	}

	/**
	 * Determines overall fitness and sorts individuals by decreasing fitness.
	 * 
	 * To use for branch coverage.
	 */
	public void computeBranchFitness(Set path) {
		overallFitness = 0;
		Iterator i = individuals.iterator();
		while (i.hasNext()) {
			Chromosome id = (Chromosome) i.next();
			overallFitness += id.computeBranchFitness(path);
		}
		Collections.sort(individuals);
	}

	/**
	 * Determines overall fitness and sorts individuals by decreasing fitness.
	 * 
	 * To use for data flow coverage.
	 */
	public void computeDataFlowFitness(DataFlowTarget tgt, Set path1, Set path2) {
		overallFitness = 0;
		Iterator i = individuals.iterator();
		while (i.hasNext()) {
			Chromosome id = (Chromosome) i.next();
			overallFitness += id.computeDataFlowFitness(tgt, path1, path2);
		}
		Collections.sort(individuals);
	}

	/**
	 * Execution of test cases associated with individuals.
	 */
	public void executeTestCases() {
		TestCaseExecutor exec = new TestCaseExecutor();

		String classUnderTest = chromosomeFormer.getClassUnderTest();
		// classUnderTest = "BinaryTree";
		Iterator i = individuals.iterator();
		while (i.hasNext()) {
			Chromosome id = (Chromosome) i.next();
			exec.execute(classUnderTest, id.toString());
			Collection trace = exec.getExecutionTrace(classUnderTest);
			if (TestGenerator.dataFlowCoverage)
				id.setCoveredDataFlows((List) trace);
			else
				id.setCoveredBranches((Set) trace);
		}
	}

	/**
	 * Checks if an individual exists covering a given branch.
	 *
	 * @param target branch to be covered
	 * @return individual covering the given branch or null if none exists
	 */
	public Chromosome getCoveringIndividual(Target target) {
		Iterator i = individuals.iterator();
		while (i.hasNext()) {
			Chromosome id = (Chromosome) i.next();
			if (target.coveredBy(id))
				return id;
		}
		return null;
	}

	/**
	 * Checks if an individual exists covering a given branch.
	 *
	 * @param target branch to be covered
	 * @return true if an individual covering the given branch exists; false if none
	 *         exists
	 */
	public boolean covers(Target target) {
		Iterator i = individuals.iterator();
		while (i.hasNext()) {
			Chromosome id = (Chromosome) i.next();
			
			if (target.coveredBy(id))
				return true;
		}
		return false;
	}

	/**
	 * Print facility
	 */
	public String toString() {
		String s = "";
		Iterator i = individuals.iterator();
		while (i.hasNext()) {
			Chromosome id = (Chromosome) i.next();
			s += id.toString() + "\n";
		}
		return s;
	}

}
