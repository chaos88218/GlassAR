package com.example.miles.glassar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sime.speech.SiMESpeechRecognitionListener;
import com.sime.speech.SiMESpeechRecognizer;

import org.artoolkit.ar.base.ARActivity;
import org.artoolkit.ar.base.camera.CameraPreferencesActivity;
import org.artoolkit.ar.base.rendering.ARRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;

public class MainActivity extends ARActivity {

    Handler mHandler = new Handler();
    private FrameLayout ARINLayout;

    private SiMESpeechRecognizer mRecognizer;
    private SiMESpeechRecognitionListener mListener;

    private TextView DAout;
    private TextView DDout;
    private TextView FDAout;
    private TextView HDAout;
    private TextView PDDout;

    public Button SockConn;

    public EditText IPadd;
    public TextView LogMes;

    int width;
    int height;
    //****************************************************************Strings****************************************//
    String LogMsg = "**********LogMsg**********";
    private char[] patientID = new char[50];
    private char[] fileName1 = new char[50];
    private char[] fileName2 = new char[50];
    //****************************************************************Matrix****************************************//
    protected static final float[] file1Matrix = new float[16];
    protected static final float[] file2Matrix = new float[16];
    protected static final float[] fiveNumbers = new float[5];
    //***************************************************************Constant**************************************//
    public static boolean modelViewable = true;
    private boolean VOICE_SWITCH = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        DAout = (TextView) findViewById(R.id.DAText);
        DDout = (TextView) findViewById(R.id.DDText);
        FDAout = (TextView) findViewById(R.id.FDAText);
        HDAout = (TextView) findViewById(R.id.HDAText);
        PDDout = (TextView) findViewById(R.id.PDDText);

        SockConn = (Button) findViewById(R.id.SockBut);

        RelativeLayout ARLayout = (RelativeLayout) findViewById(R.id.ARLayout);
        ARINLayout = (FrameLayout) findViewById(R.id.ARINLayout);

        IPadd = (EditText) findViewById(R.id.IPText);
        LogMes = (TextView) findViewById(R.id.LogMesg);
        LogMes.setMovementMethod(new ScrollingMovementMethod());

        //TODO: Add Screen adjuster for calibration loading;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = (int) (width * 3.0f / 4.0f);

        RelativeLayout.LayoutParams ll = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
        ll.setMargins(0, (int) -((height - size.y) / 2.0f), 0, (int) -((height - size.y) / 2.0f));
        ARLayout.setLayoutParams(ll);

        //TODO: Add switching glView function to close camera view
        SockConn.setOnClickListener(Connectlistener);

        mRecognizer = new SiMESpeechRecognizer(this);
        mRecognizer.setLanguageModelFiles("5467.dic", "5467.lm");
        mRecognizer.setKeyPhrase("GLASS");
        mRecognizer.setKeywordThreshold("1e-20");
        mRecognizer.setVadThreshold("3.0f");
        mRecognizer.setBeam("1e-45f");
        mRecognizer.setListeningTimeout(6000);
        mRecognizer.setOSDBitmap(new String[]{
                "SiME Speech1.png",
                "SiME Speech2.png",
                "SiME Speech3.png",
                "SiME Speech4.png",
                "SiME Speech5.png",
                "SiME Speech6.png",
                "SiME Speech7.png",
                "SiME Speech8.png",
                "SiME Speech9.png"}, 50);
        mRecognizer.setEnabledOSDView(true);

        //TODO: rework Speech comments: new comments, screenshot and new needs
        mListener = new SiMESpeechRecognitionListener() {
            @Override
            public void onResult(final String resultword) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("resultword: ", resultword);
                        Toast.makeText(MainActivity.this, resultword, Toast.LENGTH_SHORT).show();
                        if (resultword == "VOICE") {
                            VOICE_SWITCH = !VOICE_SWITCH;
                        }
                        if (VOICE_SWITCH) {
                            switch (resultword) {
                                case "CONNECT": {
                                    if (SockConn.isClickable()) {
                                        SockConn.setEnabled(false);
                                        ppthread.start();
                                    }
                                }
                                break;

                                case "MODEL": {
                                    modelViewable = !modelViewable;
                                }
                                break;

                                case "OFF": {

                                }
                                break;

                                case "CALIBRATE": {

                                }
                                break;

                                case "SHOT": {
                                    Date now = new Date();
                                    android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

                                    try {
                                        // image naming and path  to include sd card  appending name you choose for file
                                        String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".png";

                                        // create bitmap screen capture
                                        View v1 = getWindow().getDecorView().getRootView();
                                        v1.setDrawingCacheEnabled(true);
                                        Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
                                        v1.setDrawingCacheEnabled(false);

                                        File imageFile = new File(mPath);

                                        FileOutputStream outputStream = new FileOutputStream(imageFile);
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                                        outputStream.flush();
                                        outputStream.close();
                                    } catch (Throwable e) {
                                        // Several error may come out with file handling or OOM
                                        e.printStackTrace();
                                    }
                                }
                                break;
                            }
                        }

                    }
                });
            }
        };

        mRecognizer.connectService();
        mRecognizer.registerListener(mListener);
    }

    @Override
    protected void onPause() {
        mRecognizer.unregisterListener(mListener);
        mRecognizer.disconnectService();
        super.onPause();
    }

    @Override
    public void onResume() {
        mRecognizer.connectService();
        mRecognizer.registerListener(mListener);
        super.onResume();
    }

    @Override
    public void onStop() {
        mRecognizer.unregisterListener(mListener);
        mRecognizer.disconnectService();
        super.onStop();
    }

    @Override
    protected void onStart() {
        mRecognizer.connectService();
        mRecognizer.registerListener(mListener);
        super.onStart();
    }

    @Override
    protected ARRenderer supplyRenderer() {
        return new MyARRenderer();
    }

    @Override
    protected FrameLayout supplyFrameLayout() {
        return ARINLayout;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            startActivity(new Intent(this, CameraPreferencesActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void SendtoUI(final String string) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                LogMsg = string + "\n" + LogMsg;
                LogMes.setText(LogMsg);
            }
        });
    }

    public void SockConHandle(final boolean cc) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                SockConn.setEnabled(cc);
//                ARINLayout.setClickable(cc);
            }
        });
    }

    public void FiveNumbers(final int label, final float string) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                switch (label) {
                    case 1: {
                        DAout.setText("DA: " + String.format("%.1f", string) + "°");
                        break;
                    }
                    case 2: {
                        DDout.setText("DD: " + String.format("%.1f", string) + "mm");
                        break;
                    }
                    case 3: {
                        FDAout.setText("FDA: " + String.format("%.1f", string) + "°");
                        break;
                    }
                    case 4: {
                        HDAout.setText("HDA: " + String.format("%.1f", string) + "°");
                        break;
                    }
                    case 5: {
                        PDDout.setText("PDD: " + String.format("%.1f", string) + "mm");
                        break;
                    }
                }
            }
        });
    }

    protected View.OnClickListener Connectlistener = new View.OnClickListener() {
        public void onClick(View v) {
            SockConn.setEnabled(false);
            ppthread.start();
        }
    };

    final Thread ppthread = new Thread(
            new Runnable() {
                @Override
                public void run() {
                    if (MyARRenderer.AllSTLLoadingCheck[0] + MyARRenderer.AllSTLLoadingCheck[1] + MyARRenderer.AllSTLLoadingCheck[2] + MyARRenderer.AllSTLLoadingCheck[3]
                            + MyARRenderer.AllSTLLoadingCheck[4] + MyARRenderer.AllSTLLoadingCheck[5] + MyARRenderer.AllSTLLoadingCheck[6] + MyARRenderer.AllSTLLoadingCheck[7] == 8) {
                        SendtoUI("File: All Loaded!!!");
                        try {
                            int servPort = 59979;
                            SendtoUI("Sever: Connecting.....");
                            Socket socket = new Socket(IPadd.getText().toString(), servPort);
                            SendtoUI("Sever: Connected! " + IPadd.getText().toString() + ":" + servPort);
                            SockConHandle(false);

                            InputStream inS = socket.getInputStream();
                            OutputStream outS = socket.getOutputStream();

                            byte[] rebyte = new byte[256];

                            int rebytelength = inS.read(rebyte);

                            if (rebytelength != -1) {
                                if (rebyte[0] == 'S' && rebyte[1] == '1') {
                                    SendtoUI("Sever: Server Confirm");
                                    int i;
                                    int id = 0;
                                    int f1 = 0;
                                    int f2 = 0;
                                    for (i = 2; rebyte[i] != 124; i++) {
                                        patientID[i - 2] = (char) rebyte[i];
                                        id++;
                                    }

                                    for (i = i + 1; rebyte[i] != 124; i++) {
                                        fileName1[i - id - 3] = (char) rebyte[i];
                                        f1++;
                                    }

                                    for (i = i + 1; i < rebytelength && rebyte[i] != 0; i++) {
                                        fileName2[i - f1 - id - 4] = (char) rebyte[i];
                                        f2++;
                                    }

                                    if (String.valueOf(fileName1, 0, f1 - 2) == null || String.valueOf(fileName2, 0, f2 - 3) == null) {
                                        SendtoUI("Server :Socket close ----> FileName Disable");
                                        socket.close();
                                        return;
                                    }
                                    SendtoUI("[ Data: Received: \n" + "ID: " + String.valueOf(patientID, 0, id) + "\nFile1: " + String.valueOf(fileName1, 0, f1) + "\nFile2: " + String.valueOf(fileName2, 0, f2) + " ]");

                                    outS.write(("C1" + '\0').getBytes(), 0, 3);
//*********************************************loop************************************
                                    inS.read(rebyte);
                                    if (rebyte[0] == 'S' && rebyte[1] == '3') {
                                        SendtoUI("Sever: Communicating......");
                                        while (rebyte[0] != 'S' || rebyte[1] != '2') {
                                            Thread.sleep(50);

                                            int floatArraycount = 0;
                                            char[] tempCharArray = new char[30];
                                            //******************************************fileName1-Maxilla*****************************************************************************
                                            outS.write(("C5" + String.valueOf(fileName1, 0, f1) + '\0').getBytes(), 0, 3 + f1);
                                            int MatrixLength = inS.read(rebyte);

                                            if (rebyte[0] == 'S' && rebyte[1] == '5') {
                                                int n;
                                                for (i = 2, n = 0; i < MatrixLength; i++) {
                                                    if (rebyte[i] != 32 && rebyte[i] != 0) {
                                                        tempCharArray[n] = (char) rebyte[i];
                                                        n++;
                                                    } else if (floatArraycount < 16) {
                                                        file1Matrix[floatArraycount] = Float.valueOf(String.valueOf(tempCharArray, 0, n));
                                                        floatArraycount++;
                                                        n = 0;
                                                    } else {
                                                        n = 0;
                                                    }
                                                }
                                            } else {
                                                SendtoUI("Wrong Communication: S5-1");
                                            }

                                            //*****************************************fileName2-Mandible******************************************************************************

                                            floatArraycount = 0;
                                            outS.write(("C5" + String.valueOf(fileName2, 0, f2) + '\0').getBytes(), 0, 3 + f2);
                                            MatrixLength = inS.read(rebyte);

                                            if (rebyte[0] == 'S' && rebyte[1] == '5') {
                                                int n;
                                                for (i = 2, n = 0; i < MatrixLength; i++) {
                                                    if (rebyte[i] != 32 && rebyte[i] != 0) {
                                                        tempCharArray[n] = (char) rebyte[i];
                                                        n++;
                                                    } else if (floatArraycount < 16) {
                                                        file2Matrix[floatArraycount] = Float.valueOf(String.valueOf(tempCharArray, 0, n));
                                                        floatArraycount++;
                                                        n = 0;
                                                    } else {
                                                        n = 0;
                                                    }
                                                }
//                                                    Log.w("File2", "C5" + String.valueOf(fileName2, 0, f2));
                                            } else {
                                                SendtoUI("Sever: Wrong Communication S5-2");
                                            }

//                                                String test = null;
//
//                                                for(int lll = 0; lll<rebyte.length; lll++)
//                                                {
//                                                    test = test + (char)rebyte[lll]+"";
//                                                }
//
//                                                Log.w("rebyte", test);
                                            //****************************fiveNumbers*******************************************************************************************
                                            floatArraycount = 0;
                                            outS.write(("C6" + '\0').getBytes(), 0, 3);
                                            MatrixLength = inS.read(rebyte);
                                            if (rebyte[0] == 'S' && rebyte[1] == '6') {
                                                if (rebyte[2] == 'N' && rebyte[3] == 'G') {
                                                    for (int j = 5; j < 5; j++) {
                                                        fiveNumbers[j] = Float.NaN;
                                                    }
                                                } else {
                                                    int n;
                                                    for (i = 5, n = 0; i < MatrixLength; i++) {
                                                        if (rebyte[i] != 32 && rebyte[i] != 0) {
                                                            tempCharArray[n] = (char) rebyte[i];
                                                            n++;
                                                        } else if (floatArraycount < 5) {
                                                            fiveNumbers[floatArraycount] = Float.valueOf(String.valueOf(tempCharArray, 0, n));
                                                            floatArraycount++;
                                                            n = 0;
                                                        }
                                                    }
                                                }
                                            } else {
                                                SendtoUI("Sever: Wrong Communication S5-2");
                                            }
                                            FiveNumbers(1, fiveNumbers[0]);
                                            FiveNumbers(2, fiveNumbers[1]);
                                            FiveNumbers(3, fiveNumbers[2]);
                                            FiveNumbers(4, fiveNumbers[3]);
                                            FiveNumbers(5, fiveNumbers[4]);
                                            //***********************************************************************************************************************
                                        }
                                        inS.close();
                                        outS.close();
                                    } else {
                                        SendtoUI("Sever: Wrong Communication S3");
                                    }
//*********************************************loop************************************
                                } else {
                                    SendtoUI("Sever: Wrong Communication S1");
                                }
                            } else {
                                SendtoUI("Sever: Can't Receive ID/StlName1/StlName2 ------> null");
                            }

                            socket.close();
                            SendtoUI("Servr: Socket closed S2");
                            SockConHandle(true);
                            inS.close();
                            outS.close();
                        } catch (RuntimeException e) {
                            SendtoUI("Server: Runtime *Error");
                            SockConHandle(true);
                        } catch (IOException e) {
                            SendtoUI("Server: IO *Error");
                            SockConHandle(true);
                        } catch (InterruptedException e) {
                            SendtoUI("Server: Thread Interrupted *Error");
                            SockConHandle(true);
                        }
                        SockConHandle(true);
                    } else {
                        //TODO: Add calibration file checking
                        for (int iiii = 0; iiii < 8; iiii++) {
                            String temp = "";
                            if (MyARRenderer.AllSTLLoadingCheck[iiii] == 0) {
                                switch (iiii) {
                                    case 0: {
                                        temp = "Project";
                                    }
                                    break;
                                    case 1: {
                                        temp = "Skull";
                                    }
                                    break;
                                    case 2: {
                                        temp = "Maxilla";
                                    }
                                    break;
                                    case 3: {
                                        temp = "Mandible";
                                    }
                                    break;

                                    case 4: {
                                        temp = "Maxilla OSP";
                                    }
                                    break;
                                    case 5: {
                                        temp = "Mandible OSP";
                                    }
                                    break;
                                    case 6: {
                                        temp = "AR Points";
                                    }
                                    break;
                                    case 7: {
                                        temp = "AR Registration";
                                    }
                                    break;
                                }
                                SendtoUI("*File " + temp + " UnLoaded.");
                            }
                        }
                    }
                    SockConHandle(true);
                }
            });
}
