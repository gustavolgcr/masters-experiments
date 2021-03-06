package org.arida.experiments.voronoiKNN;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.graphast.graphgenerator.GraphGenerator;
import org.arida.experiments.MonacoExperiment;
import org.arida.query.voronoiknn.KNNVoronoi;
import org.arida.query.voronoiknn.VoronoiDiagram;
import org.graphast.importer.POIImporter;
import org.graphast.model.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.util.StopWatch;

public class VoronoiKNNMonacoExperiment {

	private VoronoiKNNMonacoExperiment() {

		throw new IllegalAccessError("Utility class");

	}

	public static void main(String[] args) {

		Logger logger = LoggerFactory.getLogger(MonacoExperiment.class);

		int missedSeaches = 0;
		
		Graph testGraph = new GraphGenerator().generateMonacoCHWithPoI();
		POIImporter.generateRandomPoIs(testGraph, 75);
//		Long source = testGraph.getNodeId(43.72842465479131, 7.414896579419745);

		List<Integer> numberOfNeighbors = new ArrayList<>();

		int j = 1;
		while (j <= testGraph.getPOIs().size()) {
			numberOfNeighbors.add(j);
			j = j * 2;
		}

		if (testGraph.getPOIs().size() % (j/2) != 0) {
			numberOfNeighbors.add(testGraph.getPOIs().size());
		}

		int numberOfRepetitions = 100;

		double averagePreprocessingTime = 0;

		for (Integer k : numberOfNeighbors) {

			double averageExecutionTime = 0;
			logger.info("Starting to run the Voronoi-based approach for k = {}", k);

			for (int i = 0; i < numberOfRepetitions; i++) {

				Random randomGenerator = new Random();
				int source = randomGenerator.nextInt((int)testGraph.getNumberOfNodes()-1);
				
				StopWatch voronoiPreprocessingSW = new StopWatch();

				testGraph.reverseGraph();

				VoronoiDiagram voronoiDiagram = new VoronoiDiagram(testGraph);
//				logger.info("Starting diagram creation");
				voronoiPreprocessingSW.start();
				voronoiDiagram.createDiagram();
				voronoiPreprocessingSW.stop();
//				logger.info("Finishing diagram creation: {} seconds.", voronoiPreprocessingSW.getSeconds());

				testGraph.reverseGraph();

				KNNVoronoi knn = new KNNVoronoi(testGraph, voronoiDiagram);
				StopWatch voronoiExecutionSW = new StopWatch();

				voronoiExecutionSW.start();
				
				try {
					knn.executeKNN(source, k);
				} catch (Exception e) {
					missedSeaches++;
					continue;
				}
				
				
				
				voronoiExecutionSW.stop();

				averageExecutionTime += voronoiExecutionSW.getSeconds();
				averagePreprocessingTime += voronoiPreprocessingSW.getSeconds();

			}

			averageExecutionTime = averageExecutionTime / numberOfRepetitions;
			logger.info("averageExecutionTime = {}", averageExecutionTime);

		}

		averagePreprocessingTime = averagePreprocessingTime / (numberOfRepetitions * numberOfNeighbors.size());
		logger.info("averagePreprocessingTime = {}", averagePreprocessingTime);
		logger.info("Missed Searches = {}, Total Number of Searches = {}", missedSeaches, numberOfNeighbors.size()*numberOfRepetitions);

	}
}
