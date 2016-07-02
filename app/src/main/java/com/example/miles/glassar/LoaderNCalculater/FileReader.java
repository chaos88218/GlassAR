package com.example.miles.glassar.LoaderNCalculater;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by miles on 2015/9/23.
 */
public class FileReader {

    //TODO : Add AST Cali-file Reader;
    public static float[] ARTReadBinary(String fileName) {
        double[] ospVert = new double[18];
        File file = new File("/sdcard/" + fileName);
        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(file);

            byte[] buffer = new byte[144];
            inputStream.read(buffer);

            for (int Line = 0; Line < 18; Line++) {
                ByteBuffer temp = ByteBuffer.wrap(buffer, Line * 8, 8);
                ospVert[Line] = temp.getDouble();
                Log.v("read", ospVert[Line] + "");
            }
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new float[]{Float.NaN};
        } catch (IOException e) {
            e.printStackTrace();
            return new float[]{Float.NaN};
        } finally {
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return new float[]{Float.NaN};
                }
        }

        return new float[]{
                (float) ospVert[0], (float) ospVert[1],
                (float) ospVert[2], (float) ospVert[3], (float) ospVert[4], (float) ospVert[5],
                (float) ospVert[6], (float) ospVert[7], (float) ospVert[8], (float) ospVert[9],
                (float) ospVert[10], (float) ospVert[11], (float) ospVert[12], (float) ospVert[13],
                (float) ospVert[14], (float) ospVert[15], (float) ospVert[16], (float) ospVert[17],
        };
    }

    public static float[] ReadStlBinary(String fileName){
        float[] ospVert = new float[0];
        File file = new File("/sdcard/"+fileName);
        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(file);

            int count;
            byte[] buffer = new byte[84];
            inputStream.read(buffer);
            count = ByteBuffer.wrap(buffer, 80, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();

            ospVert = new float[count*9];
            buffer = new byte[50*count];
            inputStream.read(buffer);
            int num1 = 0;
            int num2 = 0;

            for (int Line = 0; Line < count; Line++) {
                ByteBuffer temp = ByteBuffer.wrap(buffer, num2+12, 36).order(ByteOrder.LITTLE_ENDIAN);
                for (int jjj = 0; jjj<9; jjj++)
                {
                    ospVert[num1+jjj] = temp.getFloat();
                }
                num1 += 9;
                num2 += 50;
            }
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new float[]{-1.0f};
        } catch (IOException e) {
            e.printStackTrace();
            return new float[]{-1.0f};
        }finally{
            if(inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return new float[]{-1.0f};
                }
        }

        return ospVert;
    }

    public static float[] ReadProjectSkullMarker(String projectName){
        int floatArraycount = 0;
        float[] s_Markers = new float[27];
        char[] tempCharArray = new char[15];

        File file = new File("/sdcard/"+projectName);
        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(file);

            byte[] buffer = new byte[1];
            int count = 0;
            int n = 0;
            while(true)
            {
                inputStream.read(buffer);
                if(buffer[0] == 13/* "/n" */)
                {count++;}

                if (count >24)
                {break;}

                if(count > 14 && buffer[0] != 32 )
                {
                    tempCharArray[n] = (char)buffer[0];
                    n++;
                }else if (count > 14 && floatArraycount < 27)
                {
                    s_Markers[floatArraycount] = Float.valueOf(String.valueOf(tempCharArray, 0, n));
                    floatArraycount++;
                    n = 0;
                }else
                {
                    n = 0;
                }
            }
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new float[]{-1.0f};
        } catch (IOException e) {
            e.printStackTrace();
            return new float[]{-1.0f};
        }finally{
            if(inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return new float[]{-1.0f};
                }
        }
        return s_Markers;
    }

    public static float[] ReadArPoints(String projectName){
        int floatArraycount = 0;
        float[] s_Markers = new float[21];
        char[] tempCharArray = new char[15];

        File file = new File("/sdcard/"+projectName);
        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(file);

            byte[] buffer = new byte[1];
            int n = 0;
            while(true)
            {
                inputStream.read(buffer);
                if (floatArraycount >= 21)
                {break;}

                if(buffer[0] != '\t' && buffer[0] != '\n')
                {
                    tempCharArray[n] = (char)buffer[0];
                    n++;
                }else if (floatArraycount < 21)
                {
                    s_Markers[floatArraycount] = Float.valueOf(String.valueOf(tempCharArray, 0, n));
                    floatArraycount++;
                    n = 0;
                }else
                {
                    n = 0;
                }
            }
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new float[]{-1.0f};
        } catch (IOException e) {
            e.printStackTrace();
            return new float[]{-1.0f};
        }finally{
            if(inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return new float[]{-1.0f};
                }
        }
        return s_Markers;
    }
}
