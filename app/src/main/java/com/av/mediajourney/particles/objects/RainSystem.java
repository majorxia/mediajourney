package com.av.mediajourney.particles.objects;

import android.graphics.Color;
import android.opengl.GLES20;
import android.util.Log;

import com.av.mediajourney.particles.data.VertexArray;
import com.av.mediajourney.particles.programs.ParticleShaderProgram;
import com.av.mediajourney.particles.util.Geometry;

import java.util.Random;

public class RainSystem {
    private static final String TAG                      = "SnowFlakeSystem";
    //位置 xyz
    private final        int    POSITION_COMPONENT_COUNT = 3;
    //颜色 rgb
    private final        int    COLOR_COMPONENT_COUNT               = 3;
    //开始时间
    private final        int    PARTICLE_START_TIME_COMPONENT_COUNT = 1;
    // type
    private final        int    PARTICLE_TRACE_TYPE_COMPONENT_COUNT = 1;

    private final       Random mRandom       = new Random();
    public static final int    MAX_RAIN_NUMS = 80;

    private final int TOTAL_COMPONENT_COUNT = POSITION_COMPONENT_COUNT
            + COLOR_COMPONENT_COUNT
            + PARTICLE_START_TIME_COMPONENT_COUNT
            + PARTICLE_TRACE_TYPE_COMPONENT_COUNT;

    //步长
    private final int STRIDE = TOTAL_COMPONENT_COUNT * VertexArray.BYTES_PER_FLOAT;

    private final int SNOW_ENABLE_MASK = 1 << 10;

    private float[] mRandoms;
    private int mRandomIndex = 0;

    //粒子游标
    private       int         nextParticle;
    //粒子计数
    private       int         curParticleCount;
    //粒子数组
    private final float[]     particles;
    //最大粒子数量
    private final int         maxParticleCount;
    //VBO
    private final VertexArray vertexArray;

    public RainSystem(int maxParticleCount) {
        this.particles = new float[maxParticleCount * TOTAL_COMPONENT_COUNT];
        this.maxParticleCount = maxParticleCount;
        this.vertexArray = new VertexArray(particles);

        mRandoms = new float[100];
        for (int i = 0; i < mRandoms.length; i ++ ) {
            mRandoms[i] = mRandom.nextFloat() * (mRandom.nextBoolean() ? 1 : -1);
        }
    }

    float nextRandom() {
        if (mRandomIndex >= mRandoms.length)
            mRandomIndex = 0;
        return mRandoms[mRandomIndex++];
    }

    /**
     * 添加粒子到FloatBuffer
     *
     * @param position        位置
     * @param color           颜色
     * @param direction       运动矢量
     * @param particStartTime 开始时间
     */
    public void addParticle(Geometry.Point position, int color, Geometry.Vector direction, float particStartTime, int type) {
        final int particleOffset = nextParticle * TOTAL_COMPONENT_COUNT;
        int currentOffset = particleOffset;
        nextParticle++;
        if (curParticleCount < maxParticleCount) {
            curParticleCount++;
        }
        //重复使用，避免内存过大
        if (nextParticle == maxParticleCount) {
            nextParticle = 0;
        }
        //填充 位置坐标 xyz
        particles[currentOffset++] = position.x;
        particles[currentOffset++] = position.y;
        particles[currentOffset++] = position.z;

        //填充 颜色 rgb
        particles[currentOffset++] = Color.red(color) / 255f;
        particles[currentOffset++] = Color.green(color) / 255f;
        particles[currentOffset++] = Color.blue(color) / 255f;

        //填充粒子开始时间
        particles[currentOffset++] = particStartTime;

        particles[currentOffset] = (float) type;
        //把新增的粒子添加到顶点数组FloatBuffer中
        vertexArray.updateBuffer(particles, particleOffset, TOTAL_COMPONENT_COUNT);
    }

    public void updateRain(float curTime) {
        for (int i = 0; i < maxParticleCount; i++) {
            final int particleOffset = i * TOTAL_COMPONENT_COUNT;
            int currentOffset = particleOffset;
            int bits = (int) particles[currentOffset + 7];
            boolean enabled = (bits & (SNOW_ENABLE_MASK)) != 0;
            if (!enabled)
                continue;

            int type = (int) particles[currentOffset + 7] & ~SNOW_ENABLE_MASK;

            float elapsedTime = curTime - particles[i * TOTAL_COMPONENT_COUNT + 6];
            float gravityFactor = (float) (elapsedTime * elapsedTime / 9.8);

            float originX = particles[currentOffset];
            if (type == 0)  // 左右摆动
                particles[currentOffset] = (float) (particles[currentOffset] + 0.003);
                //particles[currentOffset] = (float) (particles[currentOffset] + Math.cos(elapsedTime) * 0.33);
            else if (type == 1) {
                // 向下
            } else if (type == 2)  // 向左
                particles[currentOffset] = (float) (particles[currentOffset] + 0.003);
            else if (type == 3)  // 向左
                particles[currentOffset] = (float) (particles[currentOffset] - 0.003);

            currentOffset++;

            float originY = particles[currentOffset];
/*
            float ratio = 0.55f;
            if (type == 0)
                ratio = 0.35f;
            else if (type == 1)
                ratio = 0.75f;
*/
            float ratio = 12.0f;

            particles[currentOffset] = (float) (particles[currentOffset] - gravityFactor * ratio);

//            particles[currentOffset] = mRandom.nextFloat();
//            currentOffset ++;
//            particles[currentOffset] = mRandom.nextFloat();

            vertexArray.updateBuffer(particles, particleOffset, TOTAL_COMPONENT_COUNT);

            // recover the origin value, to avoid cumulative addition.
            if (type != 2 && type != 3)
                particles[currentOffset - 1] = originX;
            particles[currentOffset] = originY;

            if(particles[currentOffset] - gravityFactor * ratio < 0) {
                particles[currentOffset] = 4 + Math.abs(nextRandom()) * 2;
                particles[currentOffset - 1] = mRandom.nextFloat() * 2 * (nextRandom());
                particles[currentOffset + 5] = System.nanoTime() / 1000000000f;
            }
        }
    }

    public void bindData(ParticleShaderProgram program) {
        int dataOffset = 0;
        vertexArray.setVertexAttributePointer(dataOffset,
                program.getaPositionLocation(),
                POSITION_COMPONENT_COUNT, STRIDE);
        dataOffset += POSITION_COMPONENT_COUNT;

        vertexArray.setVertexAttributePointer(dataOffset,
                program.getaColorLocation(),
                COLOR_COMPONENT_COUNT, STRIDE);
        dataOffset += COLOR_COMPONENT_COUNT;

        vertexArray.setVertexAttributePointer(dataOffset,
                program.getaPatricleStartTimeLocation(),
                PARTICLE_START_TIME_COMPONENT_COUNT, STRIDE);
    }

    public void draw() {
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, curParticleCount);
    }


}
