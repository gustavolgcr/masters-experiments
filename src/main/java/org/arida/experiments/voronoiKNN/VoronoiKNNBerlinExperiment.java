package org.arida.experiments.voronoiKNN;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.graphast.graphgenerator.GraphGenerator;
import org.arida.experiments.MonacoExperiment;
import org.arida.query.voronoiknn.KNNVoronoi;
import org.arida.query.voronoiknn.VoronoiDiagram;
import org.graphast.importer.POIImporter;
import org.graphast.model.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.util.StopWatch;

public class VoronoiKNNBerlinExperiment {

	private VoronoiKNNBerlinExperiment() {

		throw new IllegalAccessError("Utility class");

	}

	public static void main(String[] args) {

		Logger logger = LoggerFactory.getLogger(VoronoiKNNBerlinExperiment.class);

		Graph testGraph = new GraphGenerator().generateBerlinCH();
		POIImporter.generateRandomPoIs(testGraph, 1);
		Long source = testGraph.getNodeId(52.514104415046525,13.393178695287824);

		List<Integer> numberOfNeighbors = new ArrayList<>();

		int j = 1;
		while (j <= testGraph.getPOIs().size()) {
			numberOfNeighbors.add(j);
			j = j * 2;
		}

		if (testGraph.getPOIs().size() % 2 != 0) {
			numberOfNeighbors.add(testGraph.getPOIs().size());
		}

		int numberOfRepetitions = 1;

		double averagePreprocessingTime = 0;

		for (Integer k : numberOfNeighbors) {

			double averageExecutionTime = 0;
			logger.info("Starting to run the Voronoi-based approach for k = {}", k);

			for (int i = 0; i < numberOfRepetitions; i++) {

				StopWatch voronoiPreprocessingSW = new StopWatch();

				testGraph.reverseGraph();

				VoronoiDiagram voronoiDiagram = new VoronoiDiagram(testGraph);
				logger.info("Starting diagram creation");
				voronoiPreprocessingSW.start();
				voronoiDiagram.createDiagram();
				voronoiPreprocessingSW.stop();
				logger.info("Finishing diagram creation: {} seconds.", voronoiPreprocessingSW.getSeconds());

				testGraph.reverseGraph();

				KNNVoronoi knn = new KNNVoronoi(testGraph, voronoiDiagram);
				StopWatch voronoiExecutionSW = new StopWatch();

				voronoiExecutionSW.start();
				knn.executeKNN(source, k);
				voronoiExecutionSW.stop();

				averageExecutionTime += voronoiExecutionSW.getSeconds();
				averagePreprocessingTime += voronoiPreprocessingSW.getSeconds();

			}

			averageExecutionTime = averageExecutionTime / numberOfRepetitions;
			logger.info("averageExecutionTime = {}", averageExecutionTime);

		}

		averagePreprocessingTime = averagePreprocessingTime / (numberOfRepetitions * numberOfNeighbors.size());
		logger.info("averagePreprocessingTime = {}", averagePreprocessingTime);

	}
}
