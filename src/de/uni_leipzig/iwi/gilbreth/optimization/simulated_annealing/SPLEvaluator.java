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

import java.util.Arrays;
import java.util.Collection;

import org.opt4j.core.Objective;
import org.opt4j.core.Objective.Sign;
import org.opt4j.core.Objectives;
import org.opt4j.core.problem.Evaluator;

import com.google.inject.Inject;

/**
 * Responsible for evaluation a specific Solution. It calculates the generated
 * profit and saves it in a objective object.
 * 
 * @author Johannes Müller
 * 
 */
public class SPLEvaluator implements Evaluator<Solution> {

	private Objective profit = new Objective("profit", Sign.MAX);
	private SPLProblem problem;

	@Inject
	public SPLEvaluator(SPLProblem problem) {
		this.problem = problem;
	}


	private int countSegmentsBuyingFromCompetitor(Solution solution) {
		boolean[][] x = solution.getX();
		double[] p = solution.getP();
		SPLProblemDescription.Customer c = problem.getSPLProblemDescription()
				.getCustomer();
		SPLProblemDescription.Firm f = problem.getSPLProblemDescription()
				.getFirm();
		SPLProblemDescription.Competition co = problem
				.getSPLProblemDescription().getCompetition();
		double value = 0.0d;
		int count = 0;
		for (int i = 0; i < c.numberOfSegments(); i++) {
			value = 0.0d;
			for (int j = 0; j < f.NumberOfProducts(); j++) {
				if (x[i][j]) {
					value += c.getWTP(i, j) - p[j];
				}
			}
			if (value < co.getW(i))
				count++;
		}

		return count;
	}

	private int countSegmentsWithSuboptimalProductsAssigned(Solution solution) {
		boolean[][] x = solution.getX();
		boolean[] y = solution.determineY();
		double[] p = solution.getP();
		SPLProblemDescription.Customer c = problem.getSPLProblemDescription()
				.getCustomer();
		SPLProblemDescription.Firm f = problem.getSPLProblemDescription()
				.getFirm();
		double welfare = 0.0d;
		int count = 0;

		for (int i = 0; i < c.numberOfSegments(); i++) {
			welfare = 0.0d;
			for (int s = 0; s < f.NumberOfProducts(); s++) {
				if (x[i][s])
					welfare = c.getWTP(i, s) - p[s];
			}
			for (int j = 0; j < f.NumberOfProducts(); j++) {
				if (y[j] && welfare < (c.getWTP(i, j) - p[j]))
					count++;
			}
		}
		return count;
	}

	/**
	 * Evaluates the performance of a given solution with respect to the
	 * generated profit.
	 * 
	 * @return A collection of objectives. Contains only one objective, the
	 *         profit objective as a maximization problem and the corresponding
	 *         profit generated with the given solution.
	 */
	@Override
	public Objectives evaluate(Solution solution) {

		double _profit = 0.0d;

		int suboptimalSegments = countSegmentsWithSuboptimalProductsAssigned(solution);
		int segmentsBuyingFromComp = countSegmentsBuyingFromCompetitor(solution);

		if (segmentsBuyingFromComp == 0 && suboptimalSegments == 0) {
			_profit = solution.profit();
		} else {
			double pr = solution.profit();
			_profit = pr >= 0.0d ? Math.sqrt(pr)
					/ (suboptimalSegments + segmentsBuyingFromComp) : 0.0d;
		}

		// Collection of objectives, since we have a single objective problem,
		// the collection
		// contains exactly one objective
		Objectives objectives = new Objectives();
		objectives.add(profit, _profit);
		return objectives;
	}

	/**
	 * @return a collection of objective objects. It contains only one
	 *         objective, namely the profit objective as a maximization problem.
	 */
	@Override
	public Collection<Objective> getObjectives() {
		return Arrays.asList(profit);
	}
	/*
	 * currently not used
	 * 
	private boolean allRequiredAssetsBuilt(Solution solution) {
		boolean[] r = solution.determineRk();
		boolean[] y = solution.determineY();
		SPLProblemDescription.Firm f = problem.getSPLProblemDescription()
				.getFirm();

		for (int k = 0; k < f.NumberOfAssets(); k++) {
			for (int j = 0; j < f.NumberOfProducts(); j++) {
				if ((f.getA(j, k) && y[j]) && !r[k]) {
					return false;
				}
			}
		}

		return true;
	}
    */

/*
 * currently not used
 *
	private boolean isEveryAssignedProductProduced(Solution solution) {
		boolean[][] x = solution.getX();
		boolean[] y = solution.determineY();
		for (int i = 0; i < x.length; i++) {
			for (int j = 0; j < x[0].length; j++) {
				if (x[i][j] ^ y[j]) {
					return false;
				}
			}
		}
		return true;
	}
*/
	
/*
 * currently not used
 * 
	private boolean isEverySegmentTheBestProductAssigned(Solution solution) {
		boolean[][] x = solution.getX();
		boolean[] y = solution.determineY();
		double[] p = solution.getP();
		SPLProblemDescription.Customer c = problem.getSPLProblemDescription()
				.getCustomer();
		SPLProblemDescription.Firm f = problem.getSPLProblemDescription()
				.getFirm();
		double welfare = 0.0d;

		for (int i = 0; i < c.numberOfSegments(); i++) {
			welfare = 0.0d;
			for (int s = 0; s < f.NumberOfProducts(); s++) {
				if (x[i][s])
					welfare = c.getWTP(i, s) - p[s];
			}
			for (int j = 0; j < f.NumberOfProducts(); j++) {
				if (y[j] && welfare < (c.getWTP(i, j) - p[j]))
					return false;
			}
		}
		return true;
	}
*/
	
/*
 * currently not used
	private boolean isOneProductPerSegment(Solution solution) {
		boolean[][] x = solution.getX();
		int count = 0;
		for (int i = 0; i < x.length; i++) {
			count = 0;
			for (int j = 0; j < x[0].length; j++) {
				if (x[i][j]) {
					count++;
				}
				if (count > 1) {
					return false;
				}
			}
		}
		return true;
	}
*/
/*
 * currently not used
 *
	private boolean isZeroProductPriceZero(Solution solution) {

		return Double.compare(solution.getP()[0], 0.0d) == 0;
	}

	private boolean notBeatenByCompetitor(Solution solution) {
		boolean[][] x = solution.getX();
		double[] p = solution.getP();
		SPLProblemDescription.Customer c = problem.getSPLProblemDescription()
				.getCustomer();
		SPLProblemDescription.Firm f = problem.getSPLProblemDescription()
				.getFirm();
		SPLProblemDescription.Competition co = problem
				.getSPLProblemDescription().getCompetition();
		double value = 0.0d;

		for (int i = 0; i < c.numberOfSegments(); i++) {
			value = 0.0d;
			for (int j = 0; j < f.NumberOfProducts(); j++) {
				if (x[i][j]) {
					value += c.getWTP(i, j) - p[j];
				}
			}
			if (value < co.getW(i))
				return false;
		}

		return true;
	}
*/
}
// EOF