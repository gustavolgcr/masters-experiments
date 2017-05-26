package org.arida.graphgenerator;

import org.graphast.config.Configuration;
import org.graphast.importer.OSMImporterImpl;
import org.graphast.model.contraction.CHGraph;

public class GraphGenerator {
	
	public CHGraph generateAndorraCH() {
		
		String osmFile = this.getClass().getResource("/andorra-latest.osm.pbf").getPath();
		String graphHopperAndorraDir = Configuration.USER_HOME + "/graphhopper/experiments/andorra";
		String graphastAndorraDir = Configuration.USER_HOME + "/graphast/experiments/andorra";

		CHGraph graph = new OSMImporterImpl(osmFile, graphHopperAndorraDir, graphastAndorraDir).executeCH();

		graph.save();

		return graph;
	}

}
