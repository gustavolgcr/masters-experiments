package org.arida.experiments.CHKNN;

import java.util.ArrayList;
import java.util.List;

import org.arida.graphgenerator.GraphGenerator;
import org.graphast.importer.POIImporter;
import org.graphast.model.contraction.CHGraph;
import org.graphast.query.knnch.lowerbounds.KNNCHSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.util.StopWatch;

public class CHKNNAndorraExperiment {

	private CHKNNAndorraExperiment() {

		throw new IllegalAccessError("Utility class");

	}
	
	public static void main(String[] args) {

		Logger logger = LoggerFactory.getLogger(CHKNNAndorraExperiment.class);

		CHGraph testGraph = new GraphGenerator().generateAndorraCH();

		StopWatch preprocessingSW = new StopWatch();

		preprocessingSW.start();
		logger.info("Starting to prepare nodes.");
		testGraph.prepareNodes();
		logger.info("Finishing nodes preparation.");
		logger.info("Starting to contract nodes.");
		testGraph.contractNodes();
		preprocessingSW.stop();
		logger.info("Finishing nodes contraction.");

		logger.info("Starting to generate PoI'S.");
		POIImporter.generateRandomPoIs(testGraph, 100);
		logger.info("Finishing PoI's generation.");

		Long source = 100l;
		int numberOfRepetitions = 100;

		List<Integer> numberOfNeighbors = new ArrayList<>();

		int j = 1;
		while (j <= testGraph.getPOIs().size()) {
			numberOfNeighbors.add(j);
			j = j * 2;
		}

		if (testGraph.getPOIs().size() % 2 != 0) {
			numberOfNeighbors.add(testGraph.getPOIs().size());
		}

		for (Integer k : numberOfNeighbors) {
			logger.info("Starting to run the first prunning method for kNN with CH. k = {}", k);
			double averageExecutionTime = 0;

			for (int i = 0; i < numberOfRepetitions; i++) {
				StopWatch knnSW = new StopWatch();

				KNNCHSearch knn = new KNNCHSearch(testGraph);
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
