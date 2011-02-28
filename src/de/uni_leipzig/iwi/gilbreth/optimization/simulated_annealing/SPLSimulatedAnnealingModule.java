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

import org.opt4j.optimizer.sa.SimulatedAnnealingModule;
import org.opt4j.start.Constant;

/**
 * Configures the special implementation of the simulated annealing algorithm
 * for the SPL problem.
 * 
 * @author Johannes Müller
 * 
 */
public class SPLSimulatedAnnealingModule extends SimulatedAnnealingModule {

	@Constant(value = "delta")
	protected double delta = 0.01;

	@Constant(value = "change")
	protected int changeIterations = 1000;

	@Override
	public void config() {

		bindOptimizer(SPLSimulatedAnnealing.class);
	}

	public int getChangeIterations() {
		return changeIterations;
	}

	public double getDelta() {
		return delta;
	}

	public void setChangeIterations(int changeIterations) {
		this.changeIterations = changeIterations;
	}

	public void setDelta(double delta) {
		this.delta = delta;
	}

}
// EOF