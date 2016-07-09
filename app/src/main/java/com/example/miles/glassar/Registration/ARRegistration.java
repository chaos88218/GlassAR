package com.example.miles.glassar.Registration;

import android.opengl.GLES10;
import android.opengl.Matrix;
import android.util.Log;

import org.artoolkit.ar.base.rendering.RenderUtils;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import static com.example.miles.glassar.LoaderNCalculater.VectorCal.cross;
import static com.example.miles.glassar.LoaderNCalculater.VectorCal.getAngleDeg;
import static com.example.miles.glassar.LoaderNCalculater.VectorCal.getNormByPtArray;
import static com.example.miles.glassar.LoaderNCalculater.VectorCal.getVecByPoint;
import static com.example.miles.glassar.LoaderNCalculater.VectorCal.normalize;
import static com.example.miles.glassar.LoaderNCalculater.VectorCal.rotAngMatrixMultiVec;
import static com.example.miles.glassar.LoaderNCalculater.VectorCal.rotMatrixMultiVec;

/**
 * Created by miles on 2015/10/16.
 */
public class ARRegistration {
    //TODO: rework transformation from angle and axis to matrix
    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;
    private FloatBuffer vertexBuffer1;
    private FloatBuffer colorBuffer1;

    ProMarker proMarker;
    Arpoints arpoints;

    int loaded = 0;
    float[] skullMSPoints;
    float[] skullMSARPoints;

    public float[] aRTransVec = new float[3];
    public float[] aRRotationMatrix = new float[16];
    public float[] aRTransBackVec = new float[3];

    public float ReAngle1;
    public float ReAngle2;
    public float[] ReAxis1;
    public float[] ReAxis2;

    float[] vnCT = new float[3];
    float[] vnAR = new float[3];

    float color[];
    float color1[];

    public ARRegistration() {

        proMarker = new ProMarker("arproject.txt");
        arpoints = new Arpoints("arpoints.txt");

        if ((proMarker.isLoaded() + arpoints.isLoaded()) == 2) {
            skullMSPoints = proMarker.get_skullMSPoints();
            skullMSARPoints = arpoints.getSkullMSARPoints();

            float[] testpoint = skullMSPoints.clone();
            float[] temp = new float[]{testpoint[0], testpoint[1], testpoint[2]};
            Matrix.setIdentityM(aRRotationMatrix, 0);

            color = new float[testpoint.length / 3 * 4];
            for (int i = 0; i < color.length; i = i + 4) {
                color[i + 0] = 1.0f;
                color[i + 1] = 0.0f;
                color[i + 2] = 0.0f;
                color[i + 3] = 1.0f;
            }

            color1 = new float[skullMSARPoints.length / 3 * 4];
            for (int i = 0; i < color1.length; i = i + 4) {
                color1[i + 0] = 0.0f;
                color1[i + 1] = 1.0f;
                color1[i + 2] = 0.0f;
                color1[i + 3] = 1.0f;
            }

            aRTransVec = new float[]{-temp[0], -temp[1], -temp[2]};
            for (int ii = 0; ii < 9; ii += 3) {
                skullMSPoints[0 + ii] = skullMSPoints[0 + ii] - temp[0];
                skullMSPoints[1 + ii] = skullMSPoints[1 + ii] - temp[1];
                skullMSPoints[2 + ii] = skullMSPoints[2 + ii] - temp[2];

                testpoint[0 + ii] = testpoint[ii + 0] - temp[0];
                testpoint[1 + ii] = testpoint[ii + 1] - temp[1];
                testpoint[2 + ii] = testpoint[ii + 2] - temp[2];
            }

            vnCT = getNormByPtArray(skullMSPoints);
            vnAR = getNormByPtArray(skullMSARPoints);

            float[] Vec1;
            float[] Vec2;
            Vec1 = getVecByPoint(new float[]{skullMSPoints[0], skullMSPoints[1], skullMSPoints[2]},
                    new float[]{skullMSPoints[3], skullMSPoints[4], skullMSPoints[5]});
            normalize(Vec1);
            Vec2 = getVecByPoint(new float[]{skullMSARPoints[0], skullMSARPoints[1], skullMSARPoints[2]},
                    new float[]{skullMSARPoints[3], skullMSARPoints[4], skullMSARPoints[5]});
            normalize(Vec2);

            ReAxis1 = cross(Vec1, Vec2);
            ReAngle1 = getAngleDeg(Vec1, Vec2);

            skullMSPoints = rotAngMatrixMultiVec(skullMSPoints, ReAngle1, ReAxis1);
            Matrix.rotateM(aRRotationMatrix, 0, ReAngle1, ReAxis1[0], ReAxis1[1], ReAxis1[2]);//*****************************

            float[] Vec3;
            float[] Vec4;
            vnCT = getNormByPtArray(skullMSPoints);
            Vec1 = getVecByPoint(new float[]{skullMSPoints[0], skullMSPoints[1], skullMSPoints[2]},
                    new float[]{skullMSPoints[3], skullMSPoints[4], skullMSPoints[5]});
            normalize(Vec1);
            Vec2 = getVecByPoint(new float[]{skullMSARPoints[0], skullMSARPoints[1], skullMSARPoints[2]},
                    new float[]{skullMSARPoints[3], skullMSARPoints[4], skullMSARPoints[5]});
            normalize(Vec2);

            Vec3 = cross(vnCT, Vec1);
            Vec4 = cross(vnAR, Vec2);

            ReAxis2 = cross(Vec3, Vec4);
            ReAngle2 = getAngleDeg(Vec3, Vec4);

            Log.e("ARRegiatration VN1", ReAngle2 + "\n" + ReAxis2[0] + " " + ReAxis2[1] + " " + ReAxis2[2]);
            skullMSPoints = rotAngMatrixMultiVec(skullMSPoints, ReAngle2, ReAxis2);
            Matrix.rotateM(aRRotationMatrix, 0, ReAngle2, ReAxis2[0], ReAxis2[1], ReAxis2[2]);//*****************************

            testpoint = rotMatrixMultiVec(testpoint, aRRotationMatrix);

            for (int ii = 0; ii < 9; ii += 3) {
                skullMSPoints[0 + ii] = skullMSPoints[0 + ii] + skullMSARPoints[0];
                skullMSPoints[1 + ii] = skullMSPoints[1 + ii] + skullMSARPoints[1];
                skullMSPoints[2 + ii] = skullMSPoints[2 + ii] + skullMSARPoints[2];

                testpoint[0 + ii] = testpoint[0 + ii] + skullMSARPoints[0];
                testpoint[1 + ii] = testpoint[1 + ii] + skullMSARPoints[1];
                testpoint[2 + ii] = testpoint[2 + ii] + skullMSARPoints[2];
            }
            aRTransBackVec = new float[]{skullMSARPoints[0], skullMSARPoints[1], skullMSARPoints[2]};//**************************

            Log.e("ARRegiatration CT", skullMSPoints[0] + " " + skullMSPoints[1] + " " + skullMSPoints[2] + "\n"
                    + skullMSPoints[3] + " " + skullMSPoints[4] + " " + skullMSPoints[5] + "\n"
                    + skullMSPoints[6] + " " + skullMSPoints[7] + " " + skullMSPoints[8]);

            Log.e("ARRegiatration AR", skullMSARPoints[0] + " " + skullMSARPoints[1] + " " + skullMSARPoints[2] + "\n"
                    + skullMSARPoints[3] + " " + skullMSARPoints[4] + " " + skullMSARPoints[5] + "\n"
                    + skullMSARPoints[6] + " " + skullMSARPoints[7] + " " + skullMSARPoints[8]);

            loaded = 1;

            vertexBuffer = RenderUtils.buildFloatBuffer(skullMSPoints);
            vertexBuffer1 = RenderUtils.buildFloatBuffer(skullMSARPoints);
            colorBuffer = RenderUtils.buildFloatBuffer(color);
            colorBuffer1 = RenderUtils.buildFloatBuffer(color1);
            Log.e("Trans", aRTransVec[0] + " " + aRTransVec[1] + " " + aRTransVec[2] + "\n" + " " + aRTransBackVec[0] + " " + aRTransBackVec[1] + " " + aRTransBackVec[2]);
        }
    }

    public int isLoaded() {
        return loaded;
    }

    public float[] drawing_matrix() {
        float[] matrix = new float[16];

        Matrix.setIdentityM(matrix, 0);
        Matrix.translateM(matrix, 0, aRTransBackVec[0], aRTransBackVec[1], aRTransBackVec[2]);
        Matrix.rotateM(matrix, 0, ReAngle2, ReAxis2[0], ReAxis2[1], ReAxis2[2]);
        Matrix.rotateM(matrix, 0, ReAngle1, ReAxis1[0], ReAxis1[1], ReAxis1[2]);
        Matrix.translateM(matrix, 0, aRTransVec[0], aRTransVec[1], aRTransVec[2]);

        return matrix;
    }

    public void draw(GL10 unused) {

        GLES10.glColorPointer(4, GLES10.GL_FLOAT, 0, colorBuffer);
        GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 0, vertexBuffer);

        GLES10.glEnableClientState(GLES10.GL_COLOR_ARRAY);
        GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);

        GLES10.glDisable(GLES10.GL_CULL_FACE);
        GLES10.glPointSize(10);
        GLES10.glDrawArrays(GLES10.GL_POINTS, 0, skullMSPoints.length / 3);
        GLES10.glEnable(GLES10.GL_CULL_FACE);

        GLES10.glDisableClientState(GLES10.GL_COLOR_ARRAY);
        GLES10.glDisableClientState(GLES10.GL_VERTEX_ARRAY);


        GLES10.glColorPointer(4, GLES10.GL_FLOAT, 0, colorBuffer1);
        GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 0, vertexBuffer1);

        GLES10.glEnableClientState(GLES10.GL_COLOR_ARRAY);
        GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);

        GLES10.glDisable(GLES10.GL_CULL_FACE);
        GLES10.glPointSize(10);
        GLES10.glDrawArrays(GLES10.GL_POINTS, 0, skullMSARPoints.length / 3);
        GLES10.glEnable(GLES10.GL_CULL_FACE);

        GLES10.glDisableClientState(GLES10.GL_COLOR_ARRAY);
        GLES10.glDisableClientState(GLES10.GL_VERTEX_ARRAY);
    }
}
