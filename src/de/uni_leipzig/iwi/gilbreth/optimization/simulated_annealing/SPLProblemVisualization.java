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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.opt4j.core.Individual;
import org.opt4j.viewer.IndividualMouseListener;
import org.opt4j.viewer.Viewport;
import org.opt4j.viewer.Widget;
import org.opt4j.viewer.WidgetParameters;

import com.google.inject.Inject;

/**
 * Creates a View that visualizes a solution to the SPL problem in a table. Can
 * be used in the Opt4J runner.
 * 
 * @author Johannes Müller
 * 
 */
public class SPLProblemVisualization implements IndividualMouseListener {

	protected final Viewport viewport;

	// The route is shown by a double click of a individual in the archive
	// monitor panel. Thus we need the ArchiveMonitorPanel and the main
	// GUIFrame.
	@Inject
	public SPLProblemVisualization(Viewport viewport) {
		this.viewport = viewport;
	}

	// If an individual is double clicked, paint the route.
	public void onDoubleClick(Individual individual, Component table, Point p) {
		showSPLSolution(individual);
	}

	// If an individual is clicked with the right mouse button, open a popup
	// menu that contains the option to paint the route.
	public void onPopup(final Individual individual, Component table, Point p,
			JPopupMenu menu) {
		JMenuItem paint = new JMenuItem("show solution");
		menu.add(paint);

		paint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showSPLSolution(individual);
			}
		});

	}

	// Paint the route: Construct a JInternalFrame, add the MyPanel and add the
	// frame to the desktop of the main GUIFrame.
	protected void showSPLSolution(Individual individual) {
		Widget widget = new SPLWidget(individual);
		viewport.addWidget(widget);
	}

	// Use a custom widget
	@WidgetParameters(title = "SPL Solution", resizable = true, maximizable = false)
	protected class SPLWidget implements Widget {

		final Individual individual;
		Solution solution;

		public SPLWidget(Individual individual) {
			this.individual = individual;
			solution = (Solution) individual.getPhenotype();
		}

		private JScrollPane createAssetTable() {
			ArrayList<Solution.AssetContainer> list = solution
					.calculateAssetImportance();

			Object[][] data = new Object[list.size()][2];
			for (int i = 0; i < list.size(); i++) {
				data[i][0] = list.get(i).asset;
				data[i][1] = list.get(i).delta_profit;
			}

			JTable table = new JTable(data, new String[] { "Asset", "Profit" });
			// table.setPreferredScrollableViewportSize(new Dimension(500,
			// 200));
			table.setFillsViewportHeight(true);

			// Create the scroll pane and add the table to it.
			JScrollPane scrollPane = new JScrollPane(table);

			return scrollPane;
		}

		private JScrollPane createResultTable() {
			Object[][] data = new Object[solution.getX().length + 2][solution
					.getX()[0].length + 1];
			String[] columnNames = new String[solution.getP().length + 1];

			columnNames[0] = "Name";

			for (int i = 0; i < solution.getX().length; i++) {
				data[i][0] = "Seg" + String.valueOf(i);
				for (int j = 1; j <= solution.getX()[0].length; j++) {
					data[i][j] = solution.getX()[i][j - 1];
				}
			}
			data[solution.getX().length][0] = "Produced:";
			for (int j = 1; j <= solution.determineY().length; j++) {
				data[solution.getX().length][j] = solution.determineY()[j - 1];
				columnNames[j] = "P" + String.valueOf(j);
			}

			data[solution.getX().length + 1][0] = "Price:";
			for (int j = 1; j <= solution.determineY().length; j++) {
				data[solution.getX().length + 1][j] = solution.getP()[j - 1];
			}

			JTable table = new JTable(data, columnNames);
			// table.setPreferredScrollableViewportSize(new Dimension(500,
			// 200));
			table.setFillsViewportHeight(true);

			// Create the scroll pane and add the table to it.
			JScrollPane scrollPane = new JScrollPane(table);

			return scrollPane;
		}

		@Override
		public JPanel getPanel() {
			JPanel panel = new JPanel();
			panel.add(createResultTable(), BorderLayout.NORTH);
			panel.add(createAssetTable(), BorderLayout.SOUTH);
			panel.setSize(400, 300);
			return panel;
		}

		@Override
		public void init(Viewport arg0) {
			// TODO Auto-generated method stub

		}

	}

}
// EOF