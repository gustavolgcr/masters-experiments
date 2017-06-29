package org.arida.experiments.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.arida.query.voronoiknn.KNNVoronoi;
import org.arida.query.voronoiknn.VoronoiDiagram;
import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.importer.POIImporter;
import org.graphast.model.contraction.CHGraph;
import org.graphast.query.knn.BidirectionalKNNSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.util.StopWatch;

public class MainAndorraExperiment {

	private MainAndorraExperiment() {

		throw new IllegalAccessError("Utility class");

	}

	public static void main(String[] args) {

		Logger logger = LoggerFactory.getLogger(MainAndorraExperiment.class);

		int missedVoronoiSeaches = 0;
		int missedBiKNNSeaches = 0;

		for (Integer percentage : Arrays.asList(1, 25, 50, 75, 100)) {
			logger.info("PERCENTAGE OF POINTS OF INTEREST FROM THE GRAPH = {}", percentage);

			CHGraph testGraph = new GraphGenerator().generateAndorra();
			POIImporter.generateRandomPoIs(testGraph, percentage);

			List<Integer> numberOfNeighbors = new ArrayList<>();

			int j = 1;
			while (j <= testGraph.getPOIs().size()) {
				numberOfNeighbors.add(j);
				j = j * 2;
			}

			if (testGraph.getPOIs().size() % (j / 2) != 0) {
				numberOfNeighbors.add(testGraph.getPOIs().size());
			}

			int numberOfRepetitions = 10;

			double averageVoronoiPreprocessingTime = 0;

			for (Integer k : numberOfNeighbors) {

				double averageVoronoiExecutionTime = 0;
				double averageBiKNNExecutionTime = 0;
				logger.info("Starting to run the Voronoi-based approach and the Bi-k-NN-R for k = {}", k);

				for (int i = 0; i < numberOfRepetitions; i++) {

					Random randomGenerator = new Random();
					int source = randomGenerator.nextInt((int) testGraph.getNumberOfNodes() - 1);

					logger.info("Starting to run the Voronoi-based approach.");

					StopWatch voronoiPreprocessingSW = new StopWatch();

					testGraph.reverseGraph();

					VoronoiDiagram voronoiDiagram = new VoronoiDiagram(testGraph);
					voronoiPreprocessingSW.start();
					voronoiDiagram.createDiagram();
					voronoiPreprocessingSW.stop();

					testGraph.reverseGraph();

					KNNVoronoi knnVoronoi = new KNNVoronoi(testGraph, voronoiDiagram);
					StopWatch voronoiExecutionSW = new StopWatch();

					voronoiExecutionSW.start();
					try {
						knnVoronoi.executeKNN(source, k);
					} catch (Exception e) {
						missedVoronoiSeaches++;
						continue;
					}
					voronoiExecutionSW.stop();

					averageVoronoiExecutionTime += voronoiExecutionSW.getSeconds();
					averageVoronoiPreprocessingTime += voronoiPreprocessingSW.getSeconds();

					logger.info("Starting to run the Bi-k-NN-R approach.");

					StopWatch knnSW = new StopWatch();

					BidirectionalKNNSearch knnBidirectional = new BidirectionalKNNSearch(testGraph);
					// KNNCHSearch knn = new KNNCHSearch(testGraph);
					knnSW.start();
					try {
						knnBidirectional.search(testGraph.getNode(source), k);
					} catch (Exception e) {
						missedBiKNNSeaches++;
						continue;
					}
					knnSW.stop();

					averageBiKNNExecutionTime += knnSW.getSeconds();

				}

				averageVoronoiExecutionTime = averageVoronoiExecutionTime / numberOfRepetitions;
				logger.info("averageVoronoiExecutionTime = {}", averageVoronoiExecutionTime);

				averageBiKNNExecutionTime = averageBiKNNExecutionTime / numberOfRepetitions;
				logger.info("averageBiKNNExecutionTime = {} seconds", averageBiKNNExecutionTime);

			}

			averageVoronoiPreprocessingTime = averageVoronoiPreprocessingTime
					/ (numberOfRepetitions * numberOfNeighbors.size());
			logger.info("averagePreprocessingTime = {}", averageVoronoiPreprocessingTime);
			logger.info("Missed Voronoi Searches = {}, Total Number of Searches = {}", missedVoronoiSeaches,
					numberOfNeighbors.size() * numberOfRepetitions);
			logger.info("Missed Bi-k-NN-R Searches = {}, Total Number of Searches = {}", missedBiKNNSeaches,
					numberOfNeighbors.size() * numberOfRepetitions);

		}
	}
}
