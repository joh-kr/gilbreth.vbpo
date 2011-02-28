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

import org.opt4j.core.problem.Genotype;
import org.opt4j.genotype.BooleanGenotype;
import org.opt4j.genotype.CompositeGenotype;
import org.opt4j.genotype.DoubleGenotype;

/**
 * A Genotype representing the genome of a SPL optimization problem It consists
 * of three sub genomes for the selected products (Y), the product-segment
 * assignment (X) and the prices (P). Y and X are represented by boolean genomes
 * (just lists of booleans), P is represented by DoubleGenome, a Genome for
 * double values varied within given bounds.
 * 
 * @author Johannes Müller
 * 
 */
public class SPLGenotype extends CompositeGenotype<Integer, Genotype> {

	public DoubleGenotype getP() {
		return this.get(2);
	}

	/**
	 * 
	 * @return the P sub genotype as array of doubles
	 */
	public double[] getPAsVector() {
		double[] p = new double[getP().size()];
		for (int j = 0; j < p.length; j++) {
			p[j] = getP().get(j);
		}
		return p;
	}

	public BooleanGenotype getX() {
		return this.get(1);
	}

	/**
	 * 
	 * @return the sub genotype X as boolean matrix
	 */
	public boolean[][] getXAsMatrix() {
		boolean[][] x = new boolean[getX().size() / getP().size()][getP()
				.size()];

		int offset = x[0].length;
		for (int i = 0; i < x.length; i++) {
			for (int j = 0; j < x[0].length; j++) {
				x[i][j] = getX().get(i * offset + j);
			}
		}
		return x;
	}

	public void setP(DoubleGenotype genotype) {
		this.put(2, genotype);
	}

	public void setX(BooleanGenotype genotype) {
		this.put(1, genotype);
	}
}
// EOF
