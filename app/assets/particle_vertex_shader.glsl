uniform float u_Time;
uniform mat4 u_Matrix; //定义矩阵数据类型变量

attribute vec3 a_Position;
attribute vec3 a_Color;
attribute vec3 a_Direction;
attribute float a_ParticleStartTime;

varying vec3 v_Color;
varying float v_ElapsedTime;

void main(){
    v_Color = a_Color;
    //粒子已经持续时间  当前时间-开始时间
    v_ElapsedTime = u_Time - a_ParticleStartTime;
    //重力或者阻力因子，随着时间的推移越来越大
    float gravityFactor = v_ElapsedTime * v_ElapsedTime / 9.8;
    //当前的运动到的位置 粒子起始位置+（运动矢量*持续时间）
    // vec3 curPosition = a_Position + (a_Direction * v_ElapsedTime);
    // vec3 curPosition = a_Position +  v_ElapsedTime;
    vec3 curPosition = vec3(a_Position.x + cos(v_ElapsedTime), a_Position.y + v_ElapsedTime, a_Position.z);
    //减去重力或阻力的影响
    curPosition.y -= gravityFactor;

    //把当前位置通过内置变量传给片元着色器
//    gl_Position =  vec4(curPosition,1.0);

    //把当前位置和MVP矩阵相乘后，通过内置变量传给片元着色器
    gl_Position = u_Matrix * vec4(curPosition, 1.0);

    gl_PointSize = 25.0;
}

