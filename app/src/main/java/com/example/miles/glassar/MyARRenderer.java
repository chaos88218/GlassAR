package com.example.miles.glassar;

import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLU;
import android.util.Log;

import com.example.miles.glassar.LoaderNCalculater.FileReader;
import com.example.miles.glassar.Models.Model;
import com.example.miles.glassar.Models.OSP;
import com.example.miles.glassar.Registration.ARRegistration;

import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.Cube;
import org.artoolkit.ar.base.rendering.RenderUtils;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * A very simple Renderer that adds a marker and draws a cube on it.
 */
public class MyARRenderer extends org.artoolkit.ar.base.rendering.ARRenderer {

    private int markerID = -1;
    float[] ambientLight = new float[]{0.05f, 0.05f, 0.05f, 1.0f};
    float[] diffuseLight = new float[]{0.45f, 0.45f, 0.45f, 1.0f};
    float[] lightPos = new float[]{100.0f, -130.0f, 140.0f, 0.0f};

    float[] aRMatrix = new float[16];
    float[] drawing_matrix = new float[16];

    private Cube cube = new Cube(10, 0, 0, 5);
    private Cube test = new Cube(20, 0, 0, 10);
    private Model skull;
    private Model maxilla;
    private Model mandible;
    private OSP osp1;
    private OSP osp2;

    private ARRegistration arRegistration;


    private boolean sTLLoadingCheck;
    private float sTLCheckSum = 0;

    public int[] AllSTLLoadingCheck = {0, 0, 0, 0, 0, 0, 0, 0};
    public float rate;
    public float[] ARS;
    public float[] ART;


    /**
     * Markers can be configured here.
     */
    @Override
    public boolean configureARScene() {

        markerID = ARToolKit.getInstance().addMarker("single;Data/N_ART.pat;20");
        if (markerID < 0) return false;
        if (!sTLLoadingCheck) {
            AllSTLLoadingCheck = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
            loadSTL.start();
        }
        return true;
    }



    @Override
    public void onSurfaceChanged(GL10 gl, int w, int h) {
        super.onSurfaceChanged(gl, w, h);
        rate = (float) w / (float) h;
        Log.d("rate", rate + "");
    }

    //TODO: read cali file in and use it
    @Override
    public void draw(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        FloatBuffer ambientBuffer = RenderUtils.buildFloatBuffer(ambientLight);
        FloatBuffer diffuseBuffer = RenderUtils.buildFloatBuffer(diffuseLight);
        FloatBuffer lightPosBuffer = RenderUtils.buildFloatBuffer(lightPos);

        // Apply the ARToolKit projection matrix
        gl.glMatrixMode(GL10.GL_PROJECTION);
        if (MainActivity.STCorN) {
            if (AllSTLLoadingCheck[0] == 1) {
                gl.glLoadIdentity();
                GLU.gluPerspective(gl, ARS[0], rate, 1f, 10000.0f);
            } else {
                gl.glLoadMatrixf(ARToolKit.getInstance().getProjectionMatrix(), 0);
            }
        } else {
            if (AllSTLLoadingCheck[1] == 1) {
                gl.glLoadIdentity();
                GLU.gluPerspective(gl, ART[0], rate, 1f, 10000.0f);
            } else {
                gl.glLoadMatrixf(ARToolKit.getInstance().getProjectionMatrix(), 0);
            }
        }

        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glFrontFace(GL10.GL_CCW);

        gl.glPushMatrix();
        gl.glEnable(GL10.GL_LIGHTING);
        gl.glEnable(gl.GL_LIGHT0);
        gl.glLightModelfv(gl.GL_LIGHT_MODEL_AMBIENT, ambientBuffer);
        gl.glLightfv(GL10.GL_LIGHT0, gl.GL_AMBIENT, diffuseBuffer);
        gl.glLightfv(GL10.GL_LIGHT0, gl.GL_DIFFUSE, diffuseBuffer);
        gl.glLightfv(GL10.GL_LIGHT0, gl.GL_POSITION, lightPosBuffer);
        gl.glEnable(gl.GL_COLOR_MATERIAL);
        gl.glPopMatrix();

        gl.glMatrixMode(GL10.GL_MODELVIEW);


        if (ARToolKit.getInstance().queryMarkerVisible(markerID)) {
            aRMatrix = ARToolKit.getInstance().queryMarkerTransformation(markerID);
            if (sTLLoadingCheck) {
                if (MainActivity.modelViewable) {
                    load_matrix(gl);
                    test.draw(gl);
                    //                arpoints.draw(gl);
                    //                arRegistration.draw(gl);

                    load_matrix(gl);
                    gl.glMultMatrixf(drawing_matrix, 0);
                    skull.draw(gl);
                    //                proMarker.draw(gl);

                    gl.glMultMatrixf(MainActivity.file1Matrix, 0);
                    maxilla.draw(gl);

                    load_matrix(gl);
                    gl.glMultMatrixf(drawing_matrix, 0);
                    gl.glMultMatrixf(MainActivity.file2Matrix, 0);
                    mandible.draw(gl);
                }

                GLES10.glEnable(GLES20.GL_BLEND);
                GLES10.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
                load_matrix(gl);
                gl.glMultMatrixf(drawing_matrix, 0);
                osp1.draw(gl);

                gl.glMultMatrixf(MainActivity.file2Matrix, 0);
                osp2.draw(gl);
                GLES10.glDisable(GLES20.GL_BLEND);
            } else {
                sTLCheckSum = getAllSTLLoadingCheck_sum();
                load_matrix(gl);
                gl.glFrontFace(gl.GL_CW);
                gl.glRotatef(180.0f / 8.0f * sTLCheckSum, 0, 0, 1);
                gl.glTranslatef(0, 0, 16.0f - 16.0f / 8.0f * sTLCheckSum);
                cube.draw(gl);
                gl.glFrontFace(gl.GL_CCW);
            }
        }
    }


    private void load_matrix(GL10 gl) {
        if (MainActivity.STCorN) {
            if (AllSTLLoadingCheck[0] == 1) {
                gl.glLoadMatrixf(ARS, 1);
            } else {
                gl.glLoadIdentity();
            }
        } else {
            if (AllSTLLoadingCheck[1] == 1) {
                gl.glLoadMatrixf(ART, 2);
            } else {
                gl.glLoadIdentity();
            }
        }
        gl.glMultMatrixf(aRMatrix, 0);
    }

    public int getAllSTLLoadingCheck_index(int n) {
        return AllSTLLoadingCheck[n];
    }

    public int getAllSTLLoadingCheck_sum() {
        int a = 0;
        for (int i = 0; i < AllSTLLoadingCheck.length; i++) {
            a += AllSTLLoadingCheck[i];
        }
        return a;
    }

    Thread loadSTL = new Thread(new Runnable() {
        @Override
        public void run() {
            ARS = FileReader.ARSReadTxt("HECmatrix.txt");
            if (ARS[0] != Float.NaN) {
                AllSTLLoadingCheck[0] = 1;
            }

            ART = FileReader.ARTReadBinary("optical_param_left.dat");
            if (ART[0] != Float.NaN) {
                AllSTLLoadingCheck[1] = 1;
            }

            skull = new Model("skull.stl");
            AllSTLLoadingCheck[2] = skull.isLoaded();

            maxilla = new Model("maxilla.stl");
            AllSTLLoadingCheck[3] = maxilla.isLoaded();

            mandible = new Model("mandible.stl");
            AllSTLLoadingCheck[4] = mandible.isLoaded();

            osp1 = new OSP("max_OSP.stl", new float[]{0, 1, 0});
            AllSTLLoadingCheck[5] = osp1.isLoaded();

            osp2 = new OSP("man_OSP.stl", new float[]{0, 0, 1});
            AllSTLLoadingCheck[6] = osp2.isLoaded();

            arRegistration = new ARRegistration();
            AllSTLLoadingCheck[7] = arRegistration.isLoaded();

            if (getAllSTLLoadingCheck_sum() == 8) {
                sTLLoadingCheck = true;
                drawing_matrix = arRegistration.drawing_matrix();
            }
        }
    });
}