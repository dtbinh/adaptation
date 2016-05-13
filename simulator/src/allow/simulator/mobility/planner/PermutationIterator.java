package allow.simulator.mobility.planner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class PermutationIterator<E> {

	/**
     * Permutation is done on theses keys to handle equal objects.
     */
    private int[] keys;

    /**
     * Mapping between keys and objects.
     */
    private Map<Integer, E> objectMap;

    /**
     * Direction table used in the algorithm:
     * <ul>
     *   <li>false is left</li>
     *   <li>true is right</li>
     * </ul>
     */
    private boolean[] direction;

    /**
     * Next permutation to return. When a permutation is requested
     * this instance is provided and the next one is computed.
     */
    private List<E> nextPermutation;

    /**
     * Standard constructor for this class.
     * @param coll  the collection to generate permutations for
     * @throws NullPointerException if coll is null
     */
    public PermutationIterator(final Collection<? extends E> coll) {
        if (coll == null) {
            throw new NullPointerException("The collection must not be null");
        }

        keys = new int[coll.size()];
        direction = new boolean[coll.size()];
        Arrays.fill(direction, false);
        int value = 1;
        objectMap = new HashMap<Integer, E>();
        for (E e : coll) {
            objectMap.put(Integer.valueOf(value), e);
            keys[value - 1] = value;
            value++;
        }
        nextPermutation = new ArrayList<E>(coll);
    }

    /**
     * Indicates if there are more permutation available.
     * @return true if there are more permutations, otherwise false
     */
    public boolean hasNext() {
        return nextPermutation != null;
    }

    /**
     * Returns the next permutation of the input collection.
     * @return a list of the permutator's elements representing a permutation
     * @throws NoSuchElementException if there are no more permutations
     */
    public List<E> next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        // find the largest mobile integer k
        int indexOfLargestMobileInteger = -1;
        int largestKey = -1;
        for (int i = 0; i < keys.length; i++) {
            if ((direction[i] && i < keys.length - 1 && keys[i] > keys[i + 1]) ||
                (!direction[i] && i > 0 && keys[i] > keys[i - 1])) {
                if (keys[i] > largestKey) {
                    largestKey = keys[i];
                    indexOfLargestMobileInteger = i;
                }
            }
        }
        if (largestKey == -1) {
            List<E> toReturn = nextPermutation;
            nextPermutation = null;
            return toReturn;
        }

        // swap k and the adjacent integer it is looking at
        final int offset = direction[indexOfLargestMobileInteger] ? 1 : -1;
        final int tmpKey = keys[indexOfLargestMobileInteger];
        keys[indexOfLargestMobileInteger] = keys[indexOfLargestMobileInteger + offset];
        keys[indexOfLargestMobileInteger + offset] = tmpKey;
        boolean tmpDirection = direction[indexOfLargestMobileInteger];
        direction[indexOfLargestMobileInteger] = direction[indexOfLargestMobileInteger + offset];
        direction[indexOfLargestMobileInteger + offset] = tmpDirection;

        // reverse the direction of all integers larger than k and build the result
        final List<E> nextP = new ArrayList<E>();
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] > largestKey) {
                direction[i] = !direction[i];
            }
            nextP.add(objectMap.get(Integer.valueOf(keys[i])));
        }
        final List<E> result = nextPermutation;
        nextPermutation = nextP;
        return result;
    }

    public void remove() {
        throw new UnsupportedOperationException("remove() is not supported");
    }
	
}
