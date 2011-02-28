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

import org.opt4j.start.Constant;

import com.google.inject.Inject;

/**
 * A description of the SPL optimization problem. contains also some methods to initialize 
 * problems.
 * 
 * @author Johannes Müller
 *
 */
public class SPLProblem {

	private SPLProblemDescription problemDescription;

	@Inject
	public SPLProblem(
			@Constant(value = "nrOfSegments") int nrOfSegments,
			@Constant(value = "nrOfProducts") int nrOfProducts,
			@Constant(value = "nrOfAssets") int nrOfAssets,
			@Constant(value = "priceLevel") double priceLevel,
			@Constant(value = "priceStep") int priceStep,
			@Constant(value = "problemSelection") SPLModule.ProblemSelection problemSelection) {

		switch (problemSelection) {
		case RANDOM:
			initProblem(nrOfSegments, nrOfProducts, nrOfAssets, priceLevel,
					priceStep);
			break;
		case SMALL:
			initProblem();
			break;
		case PAPER:
			initPaperExampleProblem();
			break;
		default:
			initProblem();
		}

	}

	public SPLProblemDescription getSPLProblemDescription() {
		return problemDescription;
	}

	private void initPaperExampleProblem() {

		int[] q = { 23, 60 };

		double[][] wtp = { { 0.0, 18.0, 7.0, 25.0, 30.0 },
				{ 0.0, 7.0, 4.0, 30.0, 32.0 } };

		SPLProblemDescription.Customer customer = new SPLProblemDescription.Customer(
				q, wtp);

		// The best competing product per segment
		// Depends on the WTP of the customers and the prices of the competition
		double[] w = { 5.0, 0.0 };

		SPLProblemDescription.Competition competition = new SPLProblemDescription.Competition(
				w);

		double[] cv = { 0.0, 1.0, 1.0, 1.0, 1.0 };
		double[] cf = { 0.0, 35.0, 30.0, 45.0, 70.0 };

		// Products/Assets
		boolean[][] a = { { false, false, false, false },
				{ true, false, false, true }, { false, false, true, true },
				{ true, true, false, true }, { true, true, true, true } };

		// Cost per asset
		double[] ca = { 300.0, 400.0, 100.0, 1000.0 };

		SPLProblemDescription.Firm firm = new SPLProblemDescription.Firm(cv,
				cf, ca, a);

		int price_step = 100;

		problemDescription = new SPLProblemDescription(customer, firm,
				competition, price_step);

	}

	private void initProblem() {

		int[] q = { 10, 30, 50 };

		double[][] wtp = { { 0.0, 1.0, 3.0, 4.0, 5.0 },
				{ 0.0, 4.0, 1.0, 2.0, 1.0 }, { 0.0, 2.0, 2.3, 4.5, 2.3 } };

		SPLProblemDescription.Customer customer = new SPLProblemDescription.Customer(
				q, wtp);

		double[] w = { 0.0, 0.0, 0.0 };

		SPLProblemDescription.Competition competition = new SPLProblemDescription.Competition(
				w);

		double[] cv = { 0.0, 0.1, 0.4, 0.5, 0.01 };
		double[] cf = { 0.0, 5.0, 6.0, 1.0, 2.0 };

		// Products/Assets
		boolean[][] a = { { true, false, true, true, true, false },
				{ false, true, false, false, true, false },
				{ true, true, false, false, false, false },
				{ false, false, false, false, false, true },
				{ false, false, false, false, false, false } };

		// Cost per asset
		double[] ca = { 4.0, 3.0, 2.0, 4.0, 5.0, 7.0 };

		SPLProblemDescription.Firm firm = new SPLProblemDescription.Firm(cv,
				cf, ca, a);

		int price_step = 100;

		problemDescription = new SPLProblemDescription(customer, firm,
				competition, price_step);

	}

	private void initProblem(int nrOfSegments, int nrOfProducts,
			int nrOfAssets, double priceLevel, int priceStep) {

		double fixCostLevel = 100 * priceLevel;
		double varCostLevel = priceLevel / 10;
		double assetCostLevel = fixCostLevel / 10;

		Random random = new Random();

		int[] q = new int[nrOfSegments];
		for (int i = 0; i < nrOfSegments; i++) {
			q[i] = random.nextInt(1000) + 1; // at least one product is sold in
												// a segment
		}

		double[][] wtp = new double[nrOfSegments][nrOfProducts];
		for (int i = 0; i < nrOfSegments; i++) {
			for (int j = 0; j < nrOfProducts; j++) {
				wtp[i][j] = j == 0 ? 0.0d : priceLevel * random.nextDouble();
			}
		}
		SPLProblemDescription.Customer customer = new SPLProblemDescription.Customer(
				q, wtp);

		double[] w = new double[nrOfSegments];
		for (int i = 0; i < nrOfSegments; i++) {
			w[i] = i == 0 ? 0.0d : priceLevel * random.nextDouble();
		}
		SPLProblemDescription.Competition competition = new SPLProblemDescription.Competition(
				w);

		double[] cv = new double[nrOfProducts];
		double[] cf = new double[nrOfProducts];
		for (int j = 0; j < nrOfProducts; j++) {
			cv[j] = j == 0 ? 0.0d : random.nextDouble() * varCostLevel;
			cf[j] = j == 0 ? 0.0d : random.nextDouble() * fixCostLevel;
		}

		// Products/Assets
		boolean[][] a = new boolean[nrOfProducts][nrOfAssets];

		// Cost per asset
		double[] ca = new double[nrOfAssets];

		for (int j = 0; j < nrOfProducts; j++) {
			for (int k = 0; k < nrOfAssets; k++) {
				a[j][k] = random.nextBoolean();
			}
		}
		for (int k = 0; k < nrOfAssets; k++) {
			ca[k] = assetCostLevel * random.nextDouble();
		}

		SPLProblemDescription.Firm firm = new SPLProblemDescription.Firm(cv,
				cf, ca, a);

		problemDescription = new SPLProblemDescription(customer, firm,
				competition, priceStep);
	}
}
// EOF