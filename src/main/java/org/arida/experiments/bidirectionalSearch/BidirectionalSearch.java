package org.arida.experiments.bidirectionalSearch;

import static org.junit.Assert.assertEquals;

import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.model.contraction.CHGraph;
import org.graphast.query.route.shortestpath.bidirectionalastar.BidirectionalAStar;
import org.graphast.query.route.shortestpath.dijkstra.Dijkstra;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraConstantWeight;
import org.graphast.query.route.shortestpath.model.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.util.StopWatch;

public class BidirectionalSearch {

	public static void main(String[] args) {

		Logger logger = LoggerFactory.getLogger(BidirectionalSearch.class);

		CHGraph testGraph = new GraphGenerator().generateGraphHopperExample4();

		int numberOfRepetitions = 1000;

		double averageBidirectionalDijkstraPreprocessingTime = 0;
		double averageBidirectionalAStarPreprocessingTime = 0;

		for (int i = 0; i <= numberOfRepetitions; i++) {

			System.out.println("Repetition " + i);
			double x = 0;
			double y = 0;

			for (int source = 0; source < testGraph.getNumberOfNodes(); source++) {
				for (int destination = 0; destination < testGraph.getNumberOfNodes(); destination++) {

					logger.info("SOURCE: {}, DESTINATION: {}.", source, destination);

					Dijkstra dijkstra = new DijkstraConstantWeight(testGraph);

					StopWatch bidirectionalDijkstraExecutionSW = new StopWatch();
					bidirectionalDijkstraExecutionSW.start();
					dijkstra.shortestPath(testGraph.getNode(source), testGraph.getNode(destination));
					bidirectionalDijkstraExecutionSW.stop();

					BidirectionalAStar bidirectionalDijkstra = new BidirectionalAStar(testGraph);
					StopWatch bidirectionalAStarExecutionSW = new StopWatch();
					bidirectionalAStarExecutionSW.start();
					bidirectionalDijkstra.execute(testGraph.getNode(source), testGraph.getNode(destination));
					bidirectionalAStarExecutionSW.stop();

					x += bidirectionalDijkstraExecutionSW.getNanos();
					y += bidirectionalAStarExecutionSW.getNanos();

				}

			}
			averageBidirectionalDijkstraPreprocessingTime += x;
			averageBidirectionalAStarPreprocessingTime += y;

		}

		System.out.println("averageBidirectionalDijkstraPreprocessingTime: "
				+ averageBidirectionalDijkstraPreprocessingTime / numberOfRepetitions + " nanoseconds.");
		System.out.println("averageBidirectionalAStarPreprocessingTime: "
				+ averageBidirectionalAStarPreprocessingTime / numberOfRepetitions + " nanoseconds.");

		if (averageBidirectionalDijkstraPreprocessingTime > averageBidirectionalAStarPreprocessingTime) {
			System.out.println("The Bidirectional Dijkstra Search is "
					+ averageBidirectionalDijkstraPreprocessingTime / averageBidirectionalAStarPreprocessingTime
					+ " faster.");
		} else {
			System.out.println("The Bidirectional A* Search is "
					+ averageBidirectionalAStarPreprocessingTime / averageBidirectionalDijkstraPreprocessingTime
					+ " faster.");
		}

	}

}
