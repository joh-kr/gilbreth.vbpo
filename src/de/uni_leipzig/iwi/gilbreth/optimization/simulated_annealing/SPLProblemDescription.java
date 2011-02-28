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

/**
 * Contains the values that describe the SPL Optimization problem.
 * 
 * @author Johannes Müller
 * 
 */
public class SPLProblemDescription {

	// ---- Data Member Section ----

	public static class Competition {
		private double[] w;

		public Competition(double[] w) {
			this.w = w;
		}

		public double[] getW() {
			return w;
		}

		public double getW(int i) {
			return w[i];
		}

	}

	public static class Customer {

		private int[] q;

		// willingness to pay of segment i for product j
		private double[][] wtp;

		public Customer(int[] q, double[][] wtp) {
			this.q = q;
			this.wtp = wtp;
		}

		public int getQ(int i) {
			return q[i];
		}

		public double getWTP(int i, int j) {
			return wtp[i][j];
		}

		public int numberOfSegments() {
			return wtp.length;
		}
	}

	public static class Firm {
		private double[] cv;

		private double[] cf;

		private double[] ca;

		private boolean[][] a;

		public Firm(double[] cv, double[] cf, double[] ca, boolean[][] a) {
			if (cv.length != cf.length)
				throw new IllegalArgumentException(
						"Variable and fixed cost not equal. Must be equal because each product owns its cv and cf.");
			if (a[0].length != ca.length)
				throw new IllegalArgumentException(
						"The cost array of asset cost is unequal to the number of assets in the problem description.");
			if (a.length != cf.length)
				throw new IllegalArgumentException(
						"The number of products in array a is unequal to the number in cf.");

			this.cv = cv;
			this.ca = ca;
			this.cf = cf;
			this.a = a;
		}

		public boolean getA(int j, int k) {
			return a[j][k];
		}

		public double getCa(int i) {
			return ca[i];
		}

		public double getCf(int i) {
			return cf[i];
		}

		public double getCv(int i) {
			return cv[i];
		}

		public int NumberOfAssets() {
			return a[0].length;
		}

		public int NumberOfProducts() {
			return cv.length;
		}
	}

	private Competition competition;

	// ---- Constructor Section ----

	private Customer customer;

	// ---- Getter/Setter Section

	private Firm firm;

	private int price_steps;

	public SPLProblemDescription(Customer customer, Firm firm,
			Competition competition, int price_steps) {
		this.customer = customer;
		this.firm = firm;
		this.competition = competition;
		this.price_steps = price_steps;
	}

	// ---- Method Section ----

	public Competition getCompetition() {
		return competition;
	}

	public Customer getCustomer() {
		return customer;
	}

	public Firm getFirm() {
		return firm;
	}

	public double lowerPriceBound(int j) {
		return firm.getCv(j);

	}

	// --- internal class section ----

	public double priceRange(int i) {
		return upperPriceBound(i) - lowerPriceBound(i);
	}

	public double priceStep(int i) {
		return priceRange(i) / price_steps;
	}

	public double upperPriceBound(int j) {
		if (customer.numberOfSegments() <= 0)
			throw new IllegalArgumentException(
					"To calculate a upper bound for the price at least one customer segment has to be specified! ");

		double max = customer.getWTP(0, j);

		for (int i = 1; i < customer.numberOfSegments(); i++) {
			max = max < customer.getWTP(i, j) ? customer.getWTP(i, j) : max;
		}

		return max;
	}

}
