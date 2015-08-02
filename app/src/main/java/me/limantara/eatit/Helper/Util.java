package me.limantara.eatit.Helper;

import java.util.Random;

/**
 * Created by edwinlimantara on 8/1/15.
 */
public class Util {

    private Util(){}

    /**
     * Get a random number between min and max.
     * Min is inclusive, but max is exclusive.
     *
     * @param min
     * @param max
     * @return
     */
    public static synchronized int getRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

}