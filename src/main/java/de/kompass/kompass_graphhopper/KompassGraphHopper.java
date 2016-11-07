package de.kompass.kompass_graphhopper;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.reader.DataReader;
import com.graphhopper.reader.OSMReader;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.util.HintsMap;
import com.graphhopper.routing.util.Weighting;
import com.graphhopper.storage.DataAccess;
import com.graphhopper.storage.Directory;
import com.graphhopper.storage.GraphHopperStorage;
import com.graphhopper.util.BitUtil;

import de.kompass.kompass_graphhopper.routing.weighting.KompassWeighting;

public class KompassGraphHopper extends GraphHopper {
	protected DataAccess edgeMapping;
	protected DataAccess edgeCountMapping;
	protected BitUtil bitUtil;
	
	@Override
	public boolean load(String graphHopperFolder){
        boolean loaded = super.load(graphHopperFolder);

        Directory dir = getGraphHopperStorage().getDirectory();
        bitUtil = BitUtil.get(dir.getByteOrder());
        edgeMapping = dir.find("edge_mapping");
        edgeCountMapping = dir.find("edge_count_mapping");

        if (loaded) {
        	edgeMapping.loadExisting();
        	if(!edgeCountMapping.loadExisting()){
        		edgeCountMapping.create(1000);
        	}
        }

        return loaded;
	}

    @Override
    protected DataReader createReader(GraphHopperStorage ghStorage) {
        OSMReader reader = new OSMReader(ghStorage) {

            {
                edgeMapping.create(1000);
            }

            @Override
            protected void storeOsmWayID(int edgeId, long osmWayId) {
                super.storeOsmWayID(edgeId, osmWayId);

                long pointer = 8L * edgeId;
                edgeMapping.ensureCapacity(pointer + 8L);

                edgeMapping.setInt(pointer, bitUtil.getIntLow(osmWayId));
                edgeMapping.setInt(pointer + 4, bitUtil.getIntHigh(osmWayId));
            }

            @Override
            protected void finishedReading() {
                super.finishedReading();

                edgeMapping.flush();
            }
        };

        return initOSMReader(reader);
    }

    public long getOSMWay(int internalEdgeId) {
        long pointer = 8L * internalEdgeId;
        return bitUtil.combineIntsToLong(edgeMapping.getInt(pointer), edgeMapping.getInt(pointer + 4L));
    }
    
    public void storeEdgeCount(int edgeId, int count){
    	long pointer = 4L * edgeId;
    	edgeCountMapping.ensureCapacity(pointer + 4L);
    	
    	edgeCountMapping.setInt(pointer, count);
    }
    
    public void flushEdgeCounts(){
    	edgeCountMapping.flush();
    }
    
    public int getEdgeCount(int edgeId){
    	long pointer = 4L * edgeId;
    	if(pointer > edgeCountMapping.getCapacity()){
    		return 0;
    	}
    	return edgeCountMapping.getInt(pointer);
    }

    @Override
    public GHResponse route( GHRequest request )
    {
        GHResponse response = super.route(request);
        response.getHints().put("Test", "MM");
        
        return response;
    }
    
    @Override
    public Weighting createWeighting(HintsMap weightingMap, FlagEncoder encoder) {
    	String weighting = weightingMap.getWeighting().toLowerCase();
    	
    	if("kompass".equals(weighting)){
    		return new KompassWeighting(encoder, weightingMap, this);
    	}
    	
    	return super.createWeighting(weightingMap, encoder);
    }
}
