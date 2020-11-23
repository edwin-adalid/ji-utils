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

public class Chance {

    private static Random random = new Random();

    public static void resetSeed(long newSeed) {
        Chance.random = new Random(newSeed);
    }

    public static void resetRandom(Random random) {
        Chance.random = random;
    }

    public static void resetRandom() {
        Chance.random = new Random();
    }

    public static boolean percent(int percentageChance) {
        return random.nextInt(100) + 1 <= percentageChance;
    }

    public static <T> T oneOf(int firstWeighting,
                              Object firstObject,
                              Object... remainingWeightsAndObjects) {
        if (firstObject instanceof Task) {
            RandomObjectFactory<Task> randomActionFactory = new RandomObjectFactory<>(random,
                                                                                      firstWeighting, (Task) firstObject,
                                                                                      remainingWeightsAndObjects);
            randomActionFactory.getRandomObject().perform();
            return null;
        } else if (firstObject instanceof SupplierTask) {
            RandomObjectFactory<SupplierTask> randomActionFactory = new RandomObjectFactory<>(random,
                                                                                              firstWeighting, (SupplierTask) firstObject,
                                                                                              remainingWeightsAndObjects);
            return (T) randomActionFactory.getRandomObject().perform();
        } else {
            RandomObjectFactory<T> randomActionFactory = new RandomObjectFactory<>(random,
                                                                                   firstWeighting, (T) firstObject,
                                                                                   remainingWeightsAndObjects);
            return randomActionFactory.getRandomObject();
        }
    }

}
