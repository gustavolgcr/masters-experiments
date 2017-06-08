package org.arida.experiments.CHKNN;

import java.util.ArrayList;
import java.util.List;

import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.importer.POIImporter;
import org.graphast.model.contraction.CHGraph;
import org.graphast.query.knnch.lowerbounds.KNNCHSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.util.StopWatch;

public class CHKNNMonacoExperiment {

	private CHKNNMonacoExperiment() {

		throw new IllegalAccessError("Utility class");

	}

	public static void main(String[] args) {

		Logger logger = LoggerFactory.getLogger(CHKNNMonacoExperiment.class);

		CHGraph testGraph = new GraphGenerator().generateMonacoCHWithPoI();

		StopWatch preprocessingSW = new StopWatch();

//		preprocessingSW.start();
//		testGraph.prepareNodes();
//		testGraph.contractNodes();
//		preprocessingSW.stop();

		logger.info("Starting to generate PoI'S");
		POIImporter.generateRandomPoIs(testGraph, 75);
		logger.info("Finishing PoI's generation.");

		Long source = testGraph.getNodeId(43.72842465479131, 7.414896579419745);
		int numberOfRepetitions = 1;

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
