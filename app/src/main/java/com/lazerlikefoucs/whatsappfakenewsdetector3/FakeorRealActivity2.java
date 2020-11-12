package com.lazerlikefoucs.whatsappfakenewsdetector3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.loader.ResourcesLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import org.deeplearning4j.nn.modelimport.keras.exceptions.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.preprocessing.text.KerasTokenizer;
import org.nd4j.common.io.Resource;
import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.HashMap;

public class FakeorRealActivity2 extends AppCompatActivity {

    private TextView detecttext;
    private EditText newstext;
    private ImageButton detect;
    private String stringToPython, path;
    public String[] separated;
    public Interpreter tflite;
    //public Path path;

    public static KerasTokenizer kerasTokenizer;

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

        try {
            tflite = new Interpreter(loadModelFile());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!Python.isStarted()){
            Python.start(new AndroidPlatform(this));
        }


        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!newstext.getText().toString().isEmpty()){
                    stringToPython = newstext.getText().toString();

                    pythonInitialize();
                    //clean_string(stringToPython);
                    try {
                        tokenizer();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InvalidKerasConfigurationException e) {
                        e.printStackTrace();
                    }

                    //detecttext.setText(stringToPython);

                    //float prediction = doInference(stringToPython);
                    //detecttext.setText(Float.toString(prediction));
                }
            }
        });
    }

    //tokenize + pad Seq + encode docs
    private void tokenizer() throws IOException, InvalidKerasConfigurationException {

        //AssetManager am = this.getApplicationContext().getAssets();
        //InputStream is = am.open("tokenizer.json");
        //kerasTokenizer.fromJson(String.valueOf(is));

        //kerasTokenizer.fromJson("file:///android_asset/src\\main\\assets\\tokenizer.json");

        //kerasTokenizer.fromJson("app/src/main/assets/tokenizer.json");

        //kerasTokenizer.fromJson("file:///android_asset/tokenizer.json");

        //kerasTokenizer.fromJson("src/main/python/tokenizer.json");

        //String path = this.getApplicationContext().getFilesDir().getAbsolutePath();
        //File file = new File(path + "\\src\\main\\assets\\tokenizer.json");

        //kerasTokenizer.fromJson(file.getPath());
        System.out.println(path);

        kerasTokenizer = KerasTokenizer.fromJson(path);

        String line = stringToPython;
        //String regex = "[^\\d]+";
        String regex = "\\W";

        String[] string = line.split(regex);

        Integer[][] docs = kerasTokenizer.textsToSequences(string);

        Integer[][] arr2 = Arrays.copyOf(docs, maxlen);

        for (int i=0; i < maxlen; i++){
            System.out.println(arr2[0][i]);
            //data[i] = arr2[0][i];
        }
    }


    //python
    private void pythonInitialize() {

        Python py = Python.getInstance();
        final PyObject pyObj  = py.getModule("clean_string");
        PyObject obj = pyObj.callAttr("token");
        path = obj.toString();

/*        //data = pyObj.callAttr("find", stringToPython).toJava(Array[].class);

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
        }*/
    }


    //tflite
    private float doInference(String inputString) {

        //change input and output dims
        //input is 42d array
        //output is 1d array

        //input shape from spyder is (None, 42)
        float[][] inputVal = new float[1][maxlen];
        //int[][] inputVal = new int[1][maxlen];

        for (int i=0; i < maxlen; i++){
            inputVal[0][i] = data[i]; // here we need to covert the stringtopython string to an array.
            System.out.println("itemTF = " + inputVal[0][i]);
        }

        //output shape from spyder is (None, 1)
        float[][] outputVal = new float[1][1]; //here we need to output a single digit.

        //predict on model
        tflite.run(inputVal, outputVal);

        //inferred value is at [0]
        float inferredVal = outputVal[0][0];

        return inferredVal;
    }

    private MappedByteBuffer loadModelFile() throws IOException {

        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("model_whatsapp_fakenews.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();

        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
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

    /*
    //tokenizer
    private void tokenizer() {

        String sentence = stringToPython;

        //Instantiating SimpleTokenizer class
        SimpleTokenizer simpleTokenizer = SimpleTokenizer.INSTANCE;

        //Tokenizing the given sentence
        String tokens[] = simpleTokenizer.tokenize(sentence);

        //Printing the tokens
        for(String token : tokens) {
            System.out.println(token);
        }
    }
    */

}