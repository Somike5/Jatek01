package com.example.proba;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button newGameButton, tippButton;
    EditText etGuess;
    TextView tvMessage, tvTrials, tvHof;
    private int genNum = 0;
    private int counter = -1;
    String fHofName = "hallOfFame";
    File fHoF;
    String hofSeparator = "##";
    Map<Integer, String> unsortedMap = new HashMap<Integer, String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvMessage = findViewById(R.id.tvMessage);
        etGuess = findViewById(R.id.etGuess);
        tippButton = findViewById(R.id.hintButton);
        newGameButton = findViewById(R.id.newButton);
        tvTrials = findViewById(R.id.tvTrialRes);
        tvHof = findViewById(R.id.tvHof);
        tippButton.setEnabled(false);



        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Megnyomtak -e", Toast.LENGTH_SHORT).show();

                Random r = new Random(System.currentTimeMillis());
                genNum = r.nextInt(100)+1;
                Toast.makeText(MainActivity.this, "Szám" + genNum, Toast.LENGTH_SHORT).show();
                Log.d("newGame", "newGame ON");
                //newGameButton.setEnabled(false);
                tippButton.setEnabled(true);
                etGuess.setHint(R.string.pleaseTipp);
                counter = 0;
                tvTrials.setText(""+counter);
                tvMessage.setText(R.string.welcome);
            }
        });
        tippButton.setOnClickListener(this);
        setHofFile();

        Context ctx = getApplicationContext();
        FileInputStream fileInputStream = null;
        try {
            //fileInputStream = new FileInputStream(f);
            fileInputStream = ctx.openFileInput(fHofName);

            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String lineData = bufferedReader.readLine();
            while(lineData != null){
                String[] t = lineData.split(hofSeparator);
                unsortedMap.put(Integer.parseInt(t[0]),t[1]);
                lineData = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            tvTrials.append("fnf: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            tvTrials.append("ioe: "+e.getMessage());
        }

        setHofList();

    }

    private void setHofList(){
        tvHof.setText("");
        for(Map.Entry map  :  unsortedMap.entrySet() ) {
            tvHof.append(map.getKey() + " -- " + map.getValue() + "\n");
        }
    }

    private void setHofFile(){
        File fileDir = getFilesDir();
        String sep = File.separator;
        String path = fileDir.getPath()+sep+fHofName;
        fHoF = new File(path);
        try {
            fHoF.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private  void writeHofFile(){
        File file = new File(getFilesDir(), fHofName);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
            for(Map.Entry map  :  unsortedMap.entrySet() ) {
                bufferedWriter.write(map.getKey() + hofSeparator + map.getValue());
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onClick(View view) {
        //Toast.makeText(MainActivity.this, "ButtonID: "+ view.getId() + " !!!", Toast.LENGTH_SHORT).show();
        //etGuess.getText().toString().replaceAll("[.]", "");
        counter++;
        try {
            int guess = Integer.parseInt(etGuess.getText().toString());
            String message = "AAA";

            if(guess == genNum){
                message = getString(R.string.txtEqual);
                genNum = 0;
                //newGameButton.setEnabled(true);
                tippButton.setEnabled(false);
                etGuess.setHint(R.string.pleaseNew);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateandTime = sdf.format(new Date());
                unsortedMap.put(counter, currentDateandTime );
                setHofList();
                writeHofFile();
            }else if (guess < genNum){
                message = getString(R.string.txtSmaller);
            }else {
                message = getString(R.string.txtBigger);
            }
            etGuess.setText("");
            tvMessage.setText(message);
            tvTrials.setText(""+counter);

        }catch(NumberFormatException nfe){
            Toast.makeText(MainActivity.this, "Írj be egy számot", Toast.LENGTH_SHORT).show();
        }


    }
}