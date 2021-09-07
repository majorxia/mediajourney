package com.av.mediajourney.particles.objects;

import android.graphics.Color;
import android.opengl.Matrix;

import com.av.mediajourney.particles.util.Geometry;

import java.util.Random;

public class SnowFlakeFactory {
    private       float[] mDirectionVector = {0f, 0f, 0.3f, 1f};
    private       float[] mResultVector    = new float[4];
    private final Random  mRandom          = new Random();
    private       float[] mRotationMatrix  = new float[16];

    private final int     mPreAddParticleCount = 100;
    private final float[] hsv                  = {0f, 1f, 1f};

    public void addSnow(SnowFlakeSystem SnowFlakeSystem, float curTime) {

        for (int i = 0; i < 100; i ++ ) {
            SnowFlakeSystem.addParticle(new Geometry.Point(mRandom.nextFloat(), 0.5f, 0f),
                    Color.HSVToColor(hsv),
                    null,
                    curTime
                    );
        }
    }

    public void addSnowFlakes(SnowFlakeSystem SnowFlakeSystem, Geometry.Point position, float curTime) {


        //不是OnDrawFrame就添加烟花爆炸粒子，而是采用1/100的采样率 ，让粒子飞一会，从而产生烟花爆炸效果
        if (mRandom.nextFloat() < 1.0f / mPreAddParticleCount) {

            hsv[0] = mRandom.nextInt(360);
            int color = Color.HSVToColor(hsv);

            //同一时刻添加100*5个方向360随机的粒子
            for (int i = 0; i < mPreAddParticleCount * 3; i++) {
                Matrix.setRotateEulerM(mRotationMatrix, 0, mRandom.nextFloat() * 360, mRandom.nextFloat() * 360, mRandom.nextFloat() * 360);
                Matrix.multiplyMV(mResultVector, 0, mRotationMatrix, 0, mDirectionVector, 0);
                SnowFlakeSystem.addParticle(
                        position,
                        color,
                        new Geometry.Vector(mResultVector[0],
                                mResultVector[1] + 0.3f,
                                mResultVector[2]),
                        curTime);
            }
        }
    }

}
