package com.lazerlikefoucs.whatsappfakenewsdetector3;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.util.Scanner;

public class FakeorRealActivity2 extends AppCompatActivity {

    private TextView detecttext;
    private EditText newstext;
    private ImageButton detect;
    private String stringToPython;
    public String[] separated;


    public int maxlen = 42;
    //public int maxlen = 500;

    public float[] data = new float[maxlen];
    //public int[] data = new int[maxlen];

    public Scanner sc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fakeor_real2);

        newstext = findViewById(R.id.editTextTextMultiLine);
        detect = findViewById(R.id.imageButton_detect);
        detecttext = findViewById(R.id.textView_detect);

        if (!Python.isStarted()){
            Python.start(new AndroidPlatform(this));
        }

        //pythonInitialize();

        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!newstext.getText().toString().isEmpty()){
                    stringToPython = newstext.getText().toString();

                    pythonInitialize();
                    //clean_string(stringToPython);

                    //detecttext.setText(stringToPython);

                    //float prediction = doInference(stringToPython);
                    //detecttext.setText(Float.toString(prediction));
                }
            }
        });
    }


    //python
    private void pythonInitialize() {

        Python py = Python.getInstance();
        final PyObject pyObj  = py.getModule("clean_string");
        PyObject obj = pyObj.callAttr("find",stringToPython);
        stringToPython = obj.toString();

        //data = pyObj.callAttr("find", stringToPython).toJava(Array[].class);


        //String str = stringToPython.replaceAll("[^0-9]", " ");

        String str1 = stringToPython.replaceAll("\\D+"," ");

        String str = str1.replaceAll("[^0-9]", " ");

        //String str = stringToPython;
        System.out.println("item = " + str);

        String line = stringToPython;
        String regex = "[^\\d]+";

        String[] string = line.split(regex);

        System.out.println(string[1]);

        //Scanner sc = new Scanner(str).useDelimiter("[^\\d]+");

        for (int i=0; i < maxlen; i++){

            //System.out.println("item = " + sc.next());
            //data[i] = Float.parseFloat(sc.next());

            System.out.println("item = " + string[i+1]);

            //data[i] = Float.parseFloat(string[i+1]);
            data[i] = Integer.parseInt(string[i+1]);
        }
    }



    //data cleaning
    private void clean_string(String string) {

        //String currentString = string;
        //currentString.toLowerCase();
        //separated = currentString.split(" ");
        separated = string.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");

        for (String item : separated)
        {
            System.out.println("item = " + item);
        }
    }



}