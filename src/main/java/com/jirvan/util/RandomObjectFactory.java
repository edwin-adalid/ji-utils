/*

Copyright (c) 2012, Jirvan Pty Ltd
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Jirvan Pty Ltd nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package com.jirvan.util;

import java.util.*;

public class RandomObjectFactory<T> {

    private Random random = new Random();
    private WeightedObject[] weightedObjects;

    public RandomObjectFactory(int firstWeighting,
                               T firstObject,
                               Object... remainingWeightsAndObjects) {
        this((Long) null, firstWeighting, firstObject, remainingWeightsAndObjects);
    }

    public RandomObjectFactory(Long seed,
                               int firstWeighting,
                               T firstObject,
                               Object... remainingWeightsAndObjects) {
        this(seed == null ? new Random() : new Random(seed),
             firstWeighting,
             firstObject,
             remainingWeightsAndObjects);
    }

    public RandomObjectFactory(Random random,
                               int firstWeighting,
                               T firstObject,
                               Object... remainingWeightsAndObjects) {

        this.random = random;

        // Create array of weighted objects sorted by descending weight
        List<WeightedObject> weightedObjectsList = new ArrayList<WeightedObject>();
        weightedObjectsList.add(new WeightedObject(firstWeighting, firstObject));
        Integer currentWeighting = null;
        for (Object objectOrWeighting : remainingWeightsAndObjects) {
            if (currentWeighting == null) {
                if (objectOrWeighting == null) {
                    throw new RuntimeException("Weighting cannot be null");
                } else if (objectOrWeighting instanceof Integer) {
                    currentWeighting = (Integer) objectOrWeighting;
                } else {
                    throw new RuntimeException(String.format("Weighting must be an integer (it was a %s)", objectOrWeighting.getClass().getName()));
                }
            } else {
                weightedObjectsList.add(new WeightedObject(currentWeighting, (T) objectOrWeighting));
                currentWeighting = null;
            }
        }
        if (currentWeighting != null) throw new NullPointerException("All weightings must have an associated object");
        weightedObjects = weightedObjectsList.toArray(new WeightedObject[weightedObjectsList.size()]);
        Arrays.sort(weightedObjects, new Comparator<WeightedObject>() {
            public int compare(WeightedObject o1, WeightedObject o2) {
                if (o1.weighting > o2.weighting) {
                    return -1;
                } else if (o1.weighting < o2.weighting) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        int cumulativeWeighting = 0;
        for (WeightedObject weightedObject : weightedObjects) {
            cumulativeWeighting += weightedObject.weighting;
            weightedObject.cumulativeWeighting = cumulativeWeighting;
        }

    }

    public T getRandomObject() {
        int randomNum = random.nextInt(weightedObjects[weightedObjects.length - 1].cumulativeWeighting) + 1;
        for (WeightedObject weightedObject : weightedObjects) {
            if (randomNum <= weightedObject.cumulativeWeighting) {
                return (T) weightedObject.object;
            }
        }
        throw new RuntimeException("Expected at least one object to be selected");
    }

    private static class WeightedObject {

        public int weighting;
        public Object object;
        public int cumulativeWeighting;

        private WeightedObject(int weighting, Object object) {
            this.weighting = weighting;
            this.object = object;
        }

    }

}
