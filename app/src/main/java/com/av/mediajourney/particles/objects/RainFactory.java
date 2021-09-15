package com.av.mediajourney.particles.objects;

import android.graphics.Color;
import android.os.Handler;


import com.av.mediajourney.particles.util.Geometry;

import java.util.Random;

import static com.av.mediajourney.particles.objects.RainSystem.MAX_RAIN_NUMS;


public class RainFactory {
    private       float[] mDirectionVector = {0f, 0f, 0.3f, 1f};
    private       float[] mResultVector    = new float[4];
    private final Random  mRandom          = new Random();
    private       float[] mRandoms;
    private       int     mRandomIndex     = 0;
    private       float[] mRotationMatrix  = new float[16];

    private final int     mPreAddParticleCount = 100;
    private final float[] hsv                  = {0f, 1f, 1f};

    private final int SNOW_ENABLE_MASK = 1 << 10;

    public RainFactory() {
        mRandoms = new float[100];
        for (int i = 0; i < mRandoms.length; i ++ ) {
            mRandoms[i] = mRandom.nextFloat() * (mRandom.nextBoolean() ? 1 : 1);
        }
    }

    float nextRandom() {
        if (mRandomIndex >= mRandoms.length)
            mRandomIndex = 0;
        return mRandoms[mRandomIndex++];
    }

    public void addRain(RainSystem rainSystem, Handler handler) {
        final int times = 10;
        for (int t = 0; t < times; t++) {
            handler.postDelayed(() -> {
                float curTime = (System.nanoTime()) / 1000000000f;
                for (int i = 0; i < MAX_RAIN_NUMS / times; i++) {
                    rainSystem.addParticle(new Geometry.Point(mRandom.nextFloat() * (mRandom.nextBoolean() ? 1 : -1) * 2, 4f + nextRandom() * 2, mRandom.nextFloat()),
                            Color.HSVToColor(hsv),
                            null,
                            curTime,
                            ((mRandom.nextInt() % 4) | SNOW_ENABLE_MASK)
                    );
                }
            }, t * 500);

        }

    }


}
