package com.example2.acer.guesscelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
      ArrayList <String>  celepbURLs=new ArrayList<String>();
      ArrayList <String>  celebNames=new ArrayList<String>();
      int choseCeleb=0,locationOfCorrectAnser=0;
      String [] answer=new String[4];
      ImageView imageView;
      Button boButton0,boButton1,boButton2,boButton3;
      public void celebChosen(View view){
        if (view.getTag().toString().equals(Integer.toString(locationOfCorrectAnser))){
            Toast.makeText(getApplicationContext(),"Correct!",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(),"Wrong! It was "+celebNames.get(choseCeleb),Toast.LENGTH_LONG).show();
        }
        creatNewQuestion();

  }
    public class ImageDownloader extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {
                URL url=new URL(urls[0]);
                HttpURLConnection connection=(HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream=connection.getInputStream();
                Bitmap myBitmap= BitmapFactory.decodeStream(inputStream);
                return  myBitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
    public class DownloadTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url;
            HttpURLConnection urlConnection=null;
            try {
                url=new URL(urls[0]);
                urlConnection=(HttpURLConnection)url.openConnection();
                InputStream in=urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();
                while (data != -1){
                    char current=(char) data;
                    result +=current;
                    data =reader.read();
                }
                return result;

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DownloadTask task=new DownloadTask();
        imageView=(ImageView)findViewById(R.id.imageView) ;
        boButton0=(Button) findViewById(R.id.button);
        boButton1=(Button) findViewById(R.id.button1);
        boButton2=(Button) findViewById(R.id.button2);
        boButton3=(Button) findViewById(R.id.button3);
        String result=null;
        try {
            result=task.execute("http://www.posh24.se/kandisar").get();
            String[] splitResult=result.split("<div class=\"sidebarContainer\">");
            Pattern p=Pattern.compile("<img src=\"(.*?)\"");
            Matcher m=p.matcher(splitResult[0]);
            while (m.find()){
                celepbURLs.add(m.group(1));
                //System.out.println(m.group(1));
            }

             p=Pattern.compile("alt=\"(.*?)\"");
             m=p.matcher(splitResult[0]);
            while (m.find()){
                celebNames.add(m.group(1));
               // System.out.println(m.group(1));
            }




          //  Log.i("Content of URL", result);
            // <div class="sidebarContainer">

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        creatNewQuestion();
    }
    public void creatNewQuestion(){
        Random random=new Random();
        choseCeleb=random.nextInt(celepbURLs.size());
        ImageDownloader imageTaske=new ImageDownloader();
        Bitmap celebImage;
        try {
            celebImage =imageTaske.execute(celepbURLs.get(choseCeleb)).get();
            imageView.setImageBitmap(celebImage);
            locationOfCorrectAnser=random.nextInt(4);
            int incorrectAnserLocation;
            for (int i=0; i<4 ;i++){
                if (i== locationOfCorrectAnser){
                    answer[i]=celebNames.get(choseCeleb);
                }else{
                    incorrectAnserLocation=random.nextInt(celepbURLs.size());
                    while (incorrectAnserLocation == choseCeleb){
                        incorrectAnserLocation=random.nextInt(celepbURLs.size());
                    }
                    answer[i]=celebNames.get(incorrectAnserLocation);
                }

            }

            boButton0.setText(answer[0]);
            boButton1.setText(answer[1]);
            boButton2.setText(answer[2]);
            boButton3.setText(answer[3]);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
