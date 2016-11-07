package de.kompass.kompass_graphhopper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.util.CmdArgs;

public class App 
{
    public static void main( String[] args )
    {
        indexFile(CmdArgs.read(args));
    }

	private static void indexFile(CmdArgs cmdArgs) {
		Logger logger = LoggerFactory.getLogger(App.class);
		logger.info("Starting graphhopper");
		
		KompassGraphHopper graphHopper = new KompassGraphHopper();
		graphHopper.init(cmdArgs);
		
		logger.info("importOrLoad");
        graphHopper.importOrLoad();
	}
}
