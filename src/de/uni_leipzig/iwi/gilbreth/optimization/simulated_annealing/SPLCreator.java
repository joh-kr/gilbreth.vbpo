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

import java.util.Random;

import org.opt4j.core.problem.Creator;
import org.opt4j.genotype.BooleanGenotype;
import org.opt4j.genotype.DoubleBounds;
import org.opt4j.genotype.DoubleGenotype;

import com.google.inject.Inject;

/**
 * Responsible for creation of SPLGenotypes for a given SPLProblem.
 * 
 * @author Johannes Müller
 * 
 */
public class SPLCreator implements Creator<SPLGenotype> {

	protected final SPLProblem problem;
	private final SPLProblemDescription problemDescription;
	private Random random = new Random();

	@Inject
	public SPLCreator(SPLProblem problem) {
		this.problem = problem;
		this.problemDescription = problem.getSPLProblemDescription();
	}

	/**
	 * 
	 * @return a SPLGenotype representing the SPLProblem to solve in an abstract
	 *         fashion
	 */
	@Override
	public SPLGenotype create() {

		// The SPLGenotype consists of three sub genotypes for the selected
		// products in the SPL (y)
		// the product-segment assignment (x) and the prices (p)
		BooleanGenotype x;
		DoubleGenotype p;

		// initialize the product-segment assignments
		x = new BooleanGenotype();

		int setTrue = 0;
		for (int i = 0; i < problemDescription.getCustomer().numberOfSegments(); i++) {
			setTrue = random.nextInt(problemDescription.getFirm()
					.NumberOfProducts());
			for (int j = 0; j < problemDescription.getFirm().NumberOfProducts(); j++) {

				x.add(j == setTrue);
			}
		}

		// initialize the price genotype
		// the price for each product can vary within predefined bounds
		double[] upperbounds = new double[problemDescription.getFirm()
				.NumberOfProducts()];
		double[] lowerbounds = new double[problemDescription.getFirm()
				.NumberOfProducts()];

		// The lower and upper bounds for each product are calculated in the
		// problem description
		// since they are fixed
		for (int i = 0; i < problemDescription.getFirm().NumberOfProducts(); i++) {
			upperbounds[i] = problemDescription.upperPriceBound(i);
			lowerbounds[i] = problemDescription.lowerPriceBound(i);
		}

		DoubleBounds db = new DoubleBounds(lowerbounds, upperbounds);

		p = new DoubleGenotype(db);

		// init with random prices within the given bounds
		p.init(random, problemDescription.getFirm().NumberOfProducts());
		// set the 0 product explicitly to 0
		p.set(0, 0.0d);
		// Assemble SPLGenotype
		SPLGenotype splgenotype = new SPLGenotype();
		splgenotype.setP(p);
		splgenotype.setX(x);

		return splgenotype;
	}
}

// EOF
