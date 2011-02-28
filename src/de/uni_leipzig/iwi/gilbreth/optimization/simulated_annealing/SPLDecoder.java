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

import org.opt4j.core.problem.Decoder;

import com.google.inject.Inject;

/**
 * Decoder to decode a SPLGenotype into the corresponding Solution phenotype.
 * The Genotype is the abstract representation of the phenotype. Usually it
 * contains only those things that determine the structure of the solution
 * whereas the phenotype is the complete representation of the solution
 * containing also all constant values and the relations between them.
 * 
 * @author Johannes Müller
 * 
 */
public class SPLDecoder implements Decoder<SPLGenotype, Solution> {

	SPLProblem problem;

	@Inject
	public SPLDecoder(SPLProblem problem) {

		this.problem = problem;
	}

	/**
	 * 
	 * @return the phenotype Solution to the corresponding genotype SPLGenotype
	 */
	@Override
	public Solution decode(SPLGenotype genotype) {
		boolean[][] x = genotype.getXAsMatrix();
		double[] p = genotype.getPAsVector();

		Solution solution = new Solution(x, p,
				problem.getSPLProblemDescription());

		return solution;
	}
}
// EOF