package com.synclovis;


public class StepDetector {

    private static final int ACCEL_RING_SIZE = 50;
    private static final int VEL_RING_SIZE = 10;

    // change this threshold according to your sensitivity preferences
    private final float STEP_THRESHOLD;
    private final int STEP_DELAY_NS;

    private int accelRingCounter = 0;
    private float[] accelRingX = new float[ACCEL_RING_SIZE];
    private float[] accelRingY = new float[ACCEL_RING_SIZE];
    private float[] accelRingZ = new float[ACCEL_RING_SIZE];
    private int velRingCounter = 0;
    private float[] velRing = new float[VEL_RING_SIZE];
    private long lastStepTimeNs = 0;
    private float oldVelocityEstimate = 0;

    private StepListener listener;

    public void registerListener(StepListener listener) {
        this.listener = listener;
    }


    public void updateAccel(long timeNs, float x, float y, float z) {
        float[] currentAccel = new float[3];
        currentAccel[0] = x;
        currentAccel[1] = y;
        currentAccel[2] = z;

        // First step is to update our guess of where the global z vector is.
        accelRingCounter++;
        accelRingX[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[0];
        accelRingY[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[1];
        accelRingZ[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[2];

        float[] worldZ = new float[3];
        worldZ[0] = SensorFilter.sum(accelRingX) / Math.min(accelRingCounter, ACCEL_RING_SIZE);
        worldZ[1] = SensorFilter.sum(accelRingY) / Math.min(accelRingCounter, ACCEL_RING_SIZE);
        worldZ[2] = SensorFilter.sum(accelRingZ) / Math.min(accelRingCounter, ACCEL_RING_SIZE);

        float normalization_factor = SensorFilter.norm(worldZ);

        worldZ[0] = worldZ[0] / normalization_factor;
        worldZ[1] = worldZ[1] / normalization_factor;
        worldZ[2] = worldZ[2] / normalization_factor;

        float currentZ = SensorFilter.dot(worldZ, currentAccel) - normalization_factor;
        velRingCounter++;
        velRing[velRingCounter % VEL_RING_SIZE] = currentZ;

        float velocityEstimate = SensorFilter.sum(velRing);

        if (velocityEstimate > STEP_THRESHOLD && oldVelocityEstimate <= STEP_THRESHOLD
                && (timeNs - lastStepTimeNs > STEP_DELAY_NS)) {
            listener.step(timeNs);
            lastStepTimeNs = timeNs;
        }
        oldVelocityEstimate = velocityEstimate;
    }

    StepDetector(float THRESHOLD, int DELAY_NS){
        this.STEP_THRESHOLD = THRESHOLD;
        this.STEP_DELAY_NS = DELAY_NS;
    }

    StepDetector(){
        this.STEP_THRESHOLD = 10f;
        this.STEP_DELAY_NS = 80000000;
    }
}
