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

import org.opt4j.core.problem.ProblemModule;
import org.opt4j.operator.neighbor.BasicNeighborModule;
import org.opt4j.start.Constant;
import org.opt4j.viewer.VisualizationModule;

/**
 * Compiles all configurations for the SPL optimization problem.
 * 
 * @author Johannes Müller
 *
 */
public class SPLModule extends ProblemModule {

	public enum ProblemSelection {
		PAPER, SMALL, RANDOM;
	}

	@Constant(value = "nrOfSegments")
	int nrOfSegments = 5;

	@Constant(value = "nrOfProducts")
	int nrOfProducts = 20;

	@Constant(value = "nrOfAssets")
	int nrOfAssets = 30;

	@Constant(value = "priceLevel")
	double priceLevel = 100.0d;

	@Constant(value = "priceStep")
	int priceStep = 100;

	@Constant(value = "problemSelection")
	ProblemSelection problemSelection = ProblemSelection.PAPER;

	@Override
	protected void config() {
		bindProblem(SPLCreator.class, SPLDecoder.class, SPLEvaluator.class);

		BasicNeighborModule.addNeighbor(this.binder(),
				NeighborSPLGenotype.class);
		// bindProblem(SPLSATDecoder.class,
		// SPLSATDecoder.class,SPLEvaluator.class);

		VisualizationModule.addIndividualMouseListener(binder(),
				SPLProblemVisualization.class);

	}

	public int getNrOfAssets() {
		return nrOfAssets;
	}

	public int getNrOfProducts() {
		return nrOfProducts;
	}

	public int getNrOfSegments() {
		return nrOfSegments;
	}

	public double getPriceLevel() {
		return priceLevel;
	}

	public int getPriceStep() {
		return priceStep;
	}

	public ProblemSelection getProblemSelection() {
		return problemSelection;
	}

	public void setNrOfAssets(int nrOfAssets) {
		this.nrOfAssets = nrOfAssets;
	}

	public void setNrOfProducts(int nrOfProducts) {
		this.nrOfProducts = nrOfProducts;
	}

	public void setNrOfSegments(int nrOfSegments) {
		this.nrOfSegments = nrOfSegments;
	}

	public void setPriceLevel(double priceLevel) {
		this.priceLevel = priceLevel;
	}

	public void setPriceStep(int priceStep) {
		this.priceStep = priceStep;
	}

	public void setProblemSelection(ProblemSelection problemSelection) {
		this.problemSelection = problemSelection;
	}

}
