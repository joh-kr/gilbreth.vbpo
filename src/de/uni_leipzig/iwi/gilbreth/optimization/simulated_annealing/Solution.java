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

import java.util.ArrayList;
import java.util.Collections;

import org.opt4j.core.problem.Phenotype;

/**
 * A solution to the SPL optimization problem. Consists of all values and
 * functional dependencies that determine the profit of a specific solution to
 * the problem. The decision variables x and p vary and depend therefore on the
 * genotype. The values y and r_k are derived from x and therefore calculated
 * from this value.
 * 
 * @author Johannes Müller
 * 
 */
public class Solution implements Phenotype {

	// ---- Data Member ---------------

	/**
	 * Container Data Type saving asset numbers an their contribution to profit
	 * 
	 * @author Johannes Müller
	 * 
	 */
	public static class AssetContainer implements Comparable<AssetContainer> {

		public double delta_profit;
		public int asset;

		public AssetContainer(double delta_profit, int asset) {
			this.delta_profit = delta_profit;
			this.asset = asset;
		}

		@Override
		public int compareTo(AssetContainer o) {
			return Double.compare(this.delta_profit, o.delta_profit);
		}
	}
	// ---- The values that are determined by the genotype
	private boolean[][] x;

	private double[] p;

	// ---- Constructor Section -------

	// ---- The problem description that determines the constant values of a
	// solution
	private final SPLProblemDescription problemDescription;

	/**
	 * Constructor creates a new solution with the given values.
	 * 
	 * @param y
	 *            A boolean array representing the selected products of the
	 *            product line.
	 * @param x
	 *            A boolean 2D array representing the product customer segment
	 *            assignments
	 * @param p
	 *            A double array representing the prices of the products.
	 * @param problemDescription
	 *            A reference on the problem description.
	 */
	public Solution(boolean[][] x, double[] p,
			SPLProblemDescription problemDescription) {
		this.x = x;
		this.p = p;
		this.problemDescription = problemDescription;
	}

	/**
	 * Copy constructor. Creates a new Solution on the basis of a given
	 * solution.
	 * 
	 * @param s
	 *            the Solution to be copied.
	 */
	public Solution(Solution s) {
		this.p = copy(s.p);
		this.x = copy(s.x);
		this.problemDescription = s.problemDescription;
	}

	/**
	 * 
	 * @return the asset step cost incurred by this solution
	 */
	public double assetStepCost() {
		return assetStepCost(x);
	}

	/**
	 * 
	 * @return the asset step cost incurred by this solution
	 */
	protected double assetStepCost(boolean[][] _x) {
		double cost = 0.0d;
		boolean[] rk = determineRk(_x);
		for (int k = 0; k < problemDescription.getFirm().NumberOfAssets(); k++) {
			cost += rk[k] ? problemDescription.getFirm().getCa(k) : 0.0d;
		}
		return cost;
	}

	/**
	 * Calculates a List of contributes of single assets to the overall profit
	 * of a SPL.
	 * 
	 * @return a list of integer values naming the assets ordered by their
	 *         importance to the profit
	 */
	public ArrayList<AssetContainer> calculateAssetImportance() {
		ArrayList<AssetContainer> assets = new ArrayList<AssetContainer>();
		int nrOfAssets = problemDescription.getFirm().NumberOfAssets();

		boolean[][] x_temp = null;
		double profit_temp = 0.0d;

		for (int k = 0; k < nrOfAssets; k++) {
			x_temp = generateTempX(k);
			profit_temp = profit(x_temp);
			// As smaller profit_temp as more important k is for the profit
			// without k is so and so much lower profit generated
			assets.add(new AssetContainer(profit_temp, k));
		}

		Collections.sort(assets);

		return assets;
	}

	/**
	 * calculates the contribution margin of the solution (unit cost minus
	 * revenue)
	 * 
	 * @return the generated revenue from this solution
	 */
	public double contributionMargin() {
		return contributionMargin(x);
	}

	/**
	 * 
	 * @return the generated revenue from this solution
	 */
	protected double contributionMargin(boolean[][] _x) {
		double revenue = 0.0d;
		for (int i = 0; i < problemDescription.getCustomer().numberOfSegments(); i++) {
			for (int j = 0; j < problemDescription.getFirm().NumberOfProducts(); j++) {
				revenue += _x[i][j] ? problemDescription.getCustomer().getQ(i)
						* (p[j] - problemDescription.getFirm().getCv(j)) : 0.0d;
			}
		}
		return revenue;
	}

	/**
	 * returns a deep copy of a given two-dimensional boolean array
	 */
	private boolean[][] copy(boolean[][] a) {
		boolean[][] temp = new boolean[a.length][a[0].length];
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				temp[i][j] = a[i][j];
			}
		}
		return temp;
	}

	/**
	 * returns a deep copy of a given double array.
	 * 
	 * @param a
	 * @return
	 */
	private double[] copy(double[] a) {
		double[] temp = new double[a.length];
		for (int i = 0; i < a.length; i++) {
			temp[i] = a[i];
		}
		return temp;
	}

	/**
	 * 
	 * @return the r_k vector that determines which assets have to be produced
	 */
	public boolean[] determineRk() {
		return determineRk(x);
	}

	/**
	 * 
	 * @return the r_k vector that determines which assets have to be produced
	 */
	protected boolean[] determineRk(boolean[][] _x) {
		boolean[] r = new boolean[problemDescription.getFirm().NumberOfAssets()];

		for (int k = 0; k < r.length; k++) {
			r[k] = false;
			for (int j = 0; j < problemDescription.getFirm().NumberOfProducts(); j++) {
				if (r[k] = problemDescription.getFirm().getA(j, k)
						&& determineY(_x)[j])
					break;
			}
		}
		return r;
	}

	/**
	 * calculates the products that have to be produced on the basis of the x
	 * values.
	 * 
	 * @return a vector of products produced in the product line
	 */
	public boolean[] determineY() {
		return determineY(x);
	}

	// ---- Protected Interface Section -----

	/**
	 * 
	 * @param _x
	 *            the segment assignment
	 * @return the y vector
	 */
	protected boolean[] determineY(boolean[][] _x) {
		boolean[] y = new boolean[problemDescription.getFirm()
				.NumberOfProducts()];

		for (int j = 0; j < problemDescription.getFirm().NumberOfProducts(); j++) {
			for (int i = 0; i < problemDescription.getCustomer()
					.numberOfSegments(); i++) {
				if (_x[i][j]) {
					y[j] = true; // If at least one segments gets a product
									// assigned it has to be produced
					break;
				}
			}

		}
		return y;
	}

	private boolean[][] generateTempX(int k) {
		boolean[][] x_temp = copy(x);

		for (int i = 0; i < problemDescription.getCustomer().numberOfSegments(); i++) {
			for (int j = 0; j < problemDescription.getFirm().NumberOfProducts(); j++) {
				x_temp[i][j] = x_temp[i][j]
						& !problemDescription.getFirm().getA(j, k);
				// if seg i has j assigned and j needs k, than set temporary to
				// false
			}
		}

		return x_temp;
	}

	public double[] getP() {
		return p;
	}

	// ---- Public Interface Section ----
	public boolean[][] getX() {
		return x;
	}

	/**
	 * calculates the profit of the solution
	 * 
	 * @return the profit as double value
	 */
	public double profit() {
		return profit(x);
	}

	/**
	 * calculates the profit for any given segment assignment matrix
	 * 
	 * @return the profit for a given segment assignment matrix
	 */
	protected double profit(boolean[][] _x) {
		return contributionMargin(_x) - systemStepCost(_x) - assetStepCost(_x);
	}

	public void setP(double[] p) {
		this.p = p;
	}

	// ---- Helper functions

	public void setX(boolean[][] x) {
		this.x = x;
	}

	/**
	 * 
	 * @return the incurred system step cost from this solution
	 */
	public double systemStepCost() {
		return systemStepCost(x);
	}

	/**
	 * 
	 * @return the incurred system step cost from this solution
	 */
	protected double systemStepCost(boolean[][] _x) {
		double cost = 0.0d;
		for (int j = 0; j < problemDescription.getFirm().NumberOfProducts(); j++) {
			cost += determineY(_x)[j] ? problemDescription.getFirm().getCf(j)
					: 0.0d;
		}
		return cost;
	}
}

// EOF