package com.kouyemekhane.kouyemekhane;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.provider.DocumentsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends Activity {

    String veri;
    TextView corba,yemek,yardimciyemek,tatli,mesaj;
    ArrayList<String> menu;
    FrameLayout f1,f2,f3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        corba=(TextView)findViewById(R.id.corba);
        yemek=(TextView)findViewById(R.id.anayemek);
        yardimciyemek=(TextView)findViewById(R.id.yardimciYemek);
        tatli=(TextView)findViewById(R.id.tatli);
        mesaj=(TextView)findViewById(R.id.mesaj);
        FrameLayout f1=(FrameLayout)findViewById(R.id.bilgiler);
        FrameLayout f2=(FrameLayout)findViewById(R.id.bilgiler2);
        FrameLayout f3=(FrameLayout)findViewById(R.id.bilgiler3);

        if(checkInternetConnection()) {
            new Baslik().execute();
        }
        else
        {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("İnternet Bağlantısı Yok");
            alertDialog.setMessage("Lütfen internet erişimine izin veriniz. !");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.this.finish();
                            System.exit(0);
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }




    }

    public boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
// test for connection
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        }
        else {
            Log.v("Internet", "Internet Connection Not Present");
            return false;
        }
    }


    private class Baslik extends AsyncTask<Void,Void,Void>
    {
        String URL="http://sksdb.kocaeli.edu.tr/SKSDB/yemeklistesi";//site urlsi
        String baslik;
        ProgressDialog dialog;//dialog nesnesini tanımladık
        @Override
        protected  void onPreExecute()//Verilerin çekilme esnasında proggres dialog çalıştırır.
        {
            super.onPreExecute();
            dialog=new ProgressDialog(MainActivity.this);
            dialog.setTitle("Kou Yemekhane.");
            dialog.setMessage("Menu Listeleniyor...");
            dialog.setIndeterminate(false);
            dialog.show();
        }
        @Override
        protected Void doInBackground(Void... params) {//Arka plan işlemleri gerçekleştirilir.
            try {
                menu = new ArrayList<>();
                Document doc= Jsoup.connect(URL).get();//Siteye bağlantı sağlanıyor.
                Element table = doc.select("table").get(0); //select the first table.
                Elements rows = table.select("tr");
                    Element row = rows.get(1);
                    Elements cols = row.select("td");

                    for(int i=0;i<cols.size();i++)
                    {
                        menu.add(cols.get(i).text().toString());
                    }




            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void avoid)//Arka plan işlemleri bittikten sonra başlık yazdırılır.
        {
            if(menu.size()>=3) {
                corba.setText(menu.get(0).toString());
                yemek.setText(menu.get(1).toString());
                yardimciyemek.setText(menu.get(2).toString());
                tatli.setText(menu.get(3).toString());
            }
            else {
                f1.setVisibility(View.INVISIBLE);
                f2.setVisibility(View.INVISIBLE);
                f3.setVisibility(View.INVISIBLE);
                mesaj.setText("Bu gün yemekhane kapalıdır.");
            }
            dialog.dismiss();
        }
    }

}
