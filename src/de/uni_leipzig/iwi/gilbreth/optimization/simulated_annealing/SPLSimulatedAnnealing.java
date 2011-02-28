/*   
 * Copyright 2011 Johannes Müller, University of Leipzig
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.uni_leipzig.iwi.gilbreth.optimization.simulated_annealing;

import org.opt4j.common.random.Rand;
import org.opt4j.core.Archive;
import org.opt4j.core.Individual;
import org.opt4j.core.IndividualBuilder;
import org.opt4j.core.Population;
import org.opt4j.core.optimizer.Completer;
import org.opt4j.core.optimizer.Control;
import org.opt4j.core.optimizer.Iterations;
import org.opt4j.core.optimizer.StopException;
import org.opt4j.core.optimizer.TerminationException;
import org.opt4j.core.problem.Genotype;
import org.opt4j.operator.copy.Copy;
import org.opt4j.operator.neighbor.Neighbor;
import org.opt4j.optimizer.sa.CoolingSchedule;
import org.opt4j.optimizer.sa.SimulatedAnnealing;
import org.opt4j.start.Constant;

import com.google.inject.Inject;

/**
 * A special implementation of the opt4j simulated annealing algorithm. Contains
 * a stoping criteria that stops the optimization if the solution does not
 * change dramatically within a given period of iterations.
 * 
 * @author Johannes Müller
 * 
 */
public class SPLSimulatedAnnealing extends SimulatedAnnealing {

	protected double delta = 0.0d;
	protected int changeIterations = 0;
	private int changecounter = changeIterations;

	/**
	 * Constructs a new {@code SimulatedAnnealing}.
	 * 
	 * @param population
	 *            the population
	 * @param archive
	 *            the archive
	 * @param individualBuilder
	 *            the individual builder
	 * @param completer
	 *            the completer
	 * @param control
	 *            the control
	 * @param random
	 *            the random number generator
	 * @param neighbor
	 *            the neighbor operator
	 * @param copy
	 *            the copy operator
	 * @param iterations
	 *            the number of maximal iterations
	 * @param coolingSchedule
	 *            the cooling schedule
	 */
	@Inject
	public SPLSimulatedAnnealing(Population population, Archive archive,
			IndividualBuilder individualBuilder, Completer completer,
			Control control, Rand random, Neighbor<Genotype> neighbor,
			Copy<Genotype> copy, @Iterations int iterations,
			CoolingSchedule coolingSchedule,
			@Constant(value = "delta") double delta,
			@Constant(value = "change") int changeIterations) {
		super(population, archive, individualBuilder, completer, control,
				random, neighbor, copy, iterations, coolingSchedule);
		this.delta = delta;
		this.changecounter = this.changeIterations = changeIterations;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opt4j.core.optimizer.Optimizer#optimize()
	 */
	public void optimize() throws TerminationException, StopException {

		Individual x = individualBuilder.build();
		Individual y = null;

		population.add(x);
		completer.complete(population);
		archive.update(x);
		nextIteration();

		double fx = f(x);
		double fy;

		long starttime = System.currentTimeMillis();

		for (int i = 1; i < iterations && changecounter > 0; i++) {

			Genotype g = copy.copy(x.getGenotype());
			neighbor.neighbor(g);

			y = individualBuilder.build(g);

			completer.complete(y);
			archive.update(y);

			fy = f(y);

			// boolean value that indicates a switch of the individuals
			boolean sw = false;

			if (fy <= fx) {
				sw = true;
				calculateBreakCriteria(fx, fy);
			} else {
				double a = (fx - fy)
						/ coolingSchedule.getTemperature(i, iterations);
				double e = Math.exp(a);
				if (random.nextDouble() < e) {
					sw = true;
				}
			}

			if (sw) {
				population.remove(x);
				population.add(y);
				fx = fy;
				x = y;
			}

			nextIteration();
		}
		System.out.println((System.currentTimeMillis() - starttime) / 1000
				+ "s");

	}

	private void calculateBreakCriteria(double fx, double fy) {
		double epsilon = fx != 0.0d ? (fx - fy) / Math.abs(fx) : 1.0d;
		// System.out.println("Fx: " + fx + "Fy: " + fy + " fx - fy: " + (fx -
		// fy) + "absx" +(Math.abs(fx))+ " /fx: " +((fx - fy)/Math.abs(fx))+
		// "epsilon: " + epsilon);
		if (epsilon < delta) {
			changecounter--;
		} else {
			changecounter = changeIterations;
		}
	}

}
// EOF