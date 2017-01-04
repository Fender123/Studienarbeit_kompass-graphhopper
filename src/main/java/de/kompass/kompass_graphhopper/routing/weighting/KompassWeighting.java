package de.kompass.kompass_graphhopper.routing.weighting;

import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.util.PriorityWeighting;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.PMap;

import de.kompass.kompass_graphhopper.KompassGraphHopper;

public class KompassWeighting extends PriorityWeighting {

    protected final static double MAX_COUNT = 32767.0;	//max positive short value    
    
    private final double minFactor;
    private final KompassGraphHopper graphHopper;
       

    public KompassWeighting( FlagEncoder encoder, PMap pMap, KompassGraphHopper graphHopper )
    {
        super(encoder, pMap);
        double maxPriority = 1; // BEST / BEST
        minFactor = 1 / (0.5 + maxPriority);
        this.graphHopper = graphHopper;
    }

    @Override
    public double getMinWeight( double distance )
    {
        return minFactor * super.getMinWeight(distance);
    }

    @Override
    public double calcWeight( EdgeIteratorState edgeState, boolean reverse, int prevOrNextEdgeId )
    {
        double weight = super.calcWeight(edgeState, reverse, prevOrNextEdgeId);
        int edgeId = edgeState.getEdge();
        if (Double.isInfinite(weight))
            return Double.POSITIVE_INFINITY;
        double prio = Math.min(1.0, graphHopper.getEdgeCount(edgeId) / MAX_COUNT);
        return weight / (0.5 + prio);
    }
	@Override
	public String getName() {
		return "kompass";
	}

}
