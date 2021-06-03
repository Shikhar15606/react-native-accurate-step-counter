package com.synclovis;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class accValues {
    //initializing point arrays
    static Deque<Float> pointsZ = new ArrayDeque<Float>();
    static Deque<Float> avgPointsZ = new ArrayDeque<Float>();

    static int state = 0;
    static int sign = 0;

    static int stepCount = 0;
    static boolean stepCheckEnabled = true;
    static double stepCountNorth = 0;
    static double stepCountEast = 0;

    // Min, max, and reset amplitudes for the algorithm in m/s^2.
    static double minAmplitude = 0.5;
    static double maxAmplitude = 2.2;
    static double resetAmplitude = -0.1;

    // if state is 0, min amplitude is not reached.
    // if state is 1, min amplitude is reached.
    // if state is 2, max amplitude is exceeded.
    final static int avgAmount = 30; //Average the last n samples from the sensor.
    static int amountAdded = 0;

    static void addPoint(float z) {
        pointsZ.addLast(z);
        amountAdded++;

        float totalZ = 0;

        if (amountAdded > avgAmount) {
            pointsZ.removeFirst();
            int loopindex = 0;
            //Log.d("new data smoothing set","-------------------");

            for (Iterator<Float> q = pointsZ.iterator(); q.hasNext(); loopindex++) {
                float val = q.next();
                double avgWeight = hammingWeight(loopindex, avgAmount);
                totalZ += avgWeight * val;
                //Log.d("val, weight, totalZ", String.format("%f, %f, %f", val, avgWeight, totalZ));
            }

            float tempPointZ = totalZ/(float) avgAmount;
            avgPointsZ.addLast(tempPointZ);
            //Log.d("sample values", String.format("%f", tempPoint));
            flipState(tempPointZ);
        }
    }

    // see comment above
    static void flipState(float f) {
        if (f < resetAmplitude) state = 0;
        if (f > maxAmplitude) state = 2;
        if (f > minAmplitude && f < maxAmplitude && state != 2) state = 1;
        if (f > 0) sign = 1;
        if (sign == 1 && f < 0) sign = -2;
        else if ( f < 0 ) sign = -1;
    }

    // refer to the wikipedia article on window functions
    static double hammingWeight(int n, int N) {
        return 0.54 - 0.46 * Math.cos(2.0 * 3.14 * (double) n / (double) (N - 1));
    }

    static double getSlope() {
        float last = 2 << 19, first = 2 << 19;
        if(!avgPointsZ.isEmpty()) {
            last = avgPointsZ.getLast();
            first = avgPointsZ.getFirst();
        }
        //Log.d("last, first, state", String.format("%f, %f, %d", last, first, state));
        if (state == 1) return (last - first) / avgAmount;
        else return 2 << 19;

    }

    static float getAvgPointZ() {
        if (avgPointsZ.size() != 0) return avgPointsZ.peekLast();
        else return 0;
    }
}