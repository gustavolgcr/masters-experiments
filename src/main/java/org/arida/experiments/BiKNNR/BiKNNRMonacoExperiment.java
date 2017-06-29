package org.arida.experiments.BiKNNR;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.importer.POIImporter;
import org.graphast.model.contraction.CHGraph;
import org.graphast.query.knn.BidirectionalKNNSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.util.StopWatch;

public class BiKNNRMonacoExperiment {

	private BiKNNRMonacoExperiment() {

		throw new IllegalAccessError("Utility class");

	}

	public static void main(String[] args) {

		Logger logger = LoggerFactory.getLogger(BiKNNRMonacoExperiment.class);

		CHGraph testGraph = new GraphGenerator().generateMonacoCHWithPoI();

		StopWatch preprocessingSW = new StopWatch();

		logger.info("Starting to generate PoI'S");
		POIImporter.generateRandomPoIs(testGraph, 25);
		logger.info("Finishing PoI's generation.");

//		Long source = 294l;
//		Long source = testGraph.getNodeId(43.72842465479131, 7.414896579419745);
		int numberOfRepetitions = 100;

		List<Integer> numberOfNeighbors = new ArrayList<>();

		int j = 1;
		while (j <= testGraph.getPOIs().size()) {
			numberOfNeighbors.add(j);
			j = j * 2;
		}

		if (testGraph.getPOIs().size() % (j/2) != 0) {
			numberOfNeighbors.add(testGraph.getPOIs().size());
		}

		for (Integer k : numberOfNeighbors) {
			logger.info("Starting to run the first prunning method for kNN with CH. k = {}", k);
			double averageExecutionTime = 0;

			for (int i = 0; i < numberOfRepetitions; i++) {

				Random randomGenerator = new Random();
				int source = randomGenerator.nextInt((int)testGraph.getNumberOfNodes()-1);
//				logger.info("Source nodeID = {}", source);
				
				StopWatch knnSW = new StopWatch();

				BidirectionalKNNSearch knn = new BidirectionalKNNSearch(testGraph);
//				KNNCHSearch knn = new KNNCHSearch(testGraph);
				knnSW.start();
				knn.search(testGraph.getNode(source), k);
				knnSW.stop();

				averageExecutionTime += knnSW.getSeconds();
			}

			averageExecutionTime = averageExecutionTime / numberOfRepetitions;
			logger.info("averageExecutionTime = {} seconds", averageExecutionTime);

		}

		logger.info("preprocessingTime = {} seconds", preprocessingSW.getSeconds());

	}

}
