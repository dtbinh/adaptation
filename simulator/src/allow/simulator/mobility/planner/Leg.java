/* This program is free software: you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public License
 as published by the Free Software Foundation, either version 3 of
 the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>. */

package allow.simulator.mobility.planner;

import java.util.List;

import allow.simulator.mobility.data.TType;
import allow.simulator.util.Coordinate;
import allow.simulator.world.Street;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)

 /**
 * One leg of a trip -- that is, a temporally continuous piece of the journey that takes place on a
 * particular vehicle (or on foot).
 */

public class Leg {

    /**
     * The date and time this leg begins.
     */
    public long startTime = 0;
    
    /**
     * The date and time this leg ends.
     */
    public long endTime = 0;
    
    /**
     * For transit leg, the offset from the scheduled departure-time of the boarding stop in this leg.
     * "scheduled time of departure at boarding stop" = startTime - departureDelay
     */
    // public int departureDelay = 0;
    
    /**
     * For transit leg, the offset from the scheduled arrival-time of the alighting stop in this leg.
     * "scheduled time of arrival at alighting stop" = endTime - arrivalDelay
     */
    // public int arrivalDelay = 0;
   
    /**
     * The distance traveled while traversing the leg in meters.
     */
    public double distance = 0.0;
    
    /**
     * The mode (e.g., <code>Walk</code>) used when traversing this leg.
     */
    public TType mode = TType.WALK;
    
    /**
     * For transit legs, the ID of the route.
     * For non-transit legs, null.
     */
    public String routeId = null;
    
    /**
     * For transit legs, the ID of the agency.
     * For non-transit legs, null.
     */
    public String agencyId = null;
    
    /**
     * For transit legs, the ID of the trip.
     * For non-transit legs, null.
     */
    public String tripId = null;
    
    /**
     * The position where the leg originates.
     */
    public Coordinate from = null;
    
    /**
     * Stop id where the leg originates.
     */
    public String stopIdFrom = null;
    
    /**
     * The Place where the leg begins.
     */
    public Coordinate to = null;

    /**
     * Stop id where the leg goes to.
     */
    public String stopIdTo = null;
    
    /**
     * List of intermediate stops when transit is used.
     */
    public List<String> stops;
    
    /**
     * The leg's geometry.
     */
    public String legGeometry;
    
    /**
     * List of nodes passed in the routing graph.
     */
    public List<String> osmNodes;
    
    public List<Street> streets;
    
    /**
     * Costs of this leg.
     */
    public double costs;
}
