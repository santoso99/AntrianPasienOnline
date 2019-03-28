package com.simpus.antrianpasienonline;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.simpus.antrianpasienonline.adapters.ListPoliAdapter;
import com.simpus.antrianpasienonline.objects.poli;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KonfirmasiActivity extends AppCompatActivity implements View.OnClickListener  {
    private Button btn_batal,btn_antri;
    TextView PasienView,PoliView,DokterView, RujukanView, TglView;
    private String norm,norujukan,idjadwal,poli,namaDokter,namaPasien,namaPoli, nomorrujukan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konfirmasi);

        //inisiasi variable
        btn_batal =(Button)findViewById(R.id.btn_batal );
        btn_antri =(Button)findViewById(R.id.btn_antri );
        PasienView =(TextView)findViewById(R.id.PasienView );
        PoliView =(TextView)findViewById(R.id.PoliView );
        DokterView =(TextView)findViewById(R.id.DokterView );
        RujukanView =(TextView)findViewById(R.id.RujukanView );
        TglView =(TextView)findViewById(R.id.TglView );

        //mendefinisikan onclick listener
        btn_batal.setOnClickListener(this);
        btn_antri.setOnClickListener(this);

        //mengambil intent dari intent sebelumnya
        Bundle extras = getIntent().getExtras();
        idjadwal = extras.getString("id_jadwal");
        poli = extras.getString("poli");
        namaDokter = extras.getString("namaDokter");
        //mendefinisikan variable dari shared preference
        namaPasien = getSharedPreferences("data", Context.MODE_PRIVATE).getString("nama","");


    }
    // mendefiniskan ketika button di click
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_batal:
                Toast.makeText(this, "antrian dibatalkan", Toast.LENGTH_SHORT).show();
                Intent IntenOut = new Intent(KonfirmasiActivity.this, KartuAntrianActivity.class);
                startActivity(IntenOut);

                finish();
                break;

            case R.id.btn_antri:
//                Intent IntenOut = new Intent(MenuActivity.this, FinishedActivity.class);
//                startActivity(IntenOut);
                AlertDialog.Builder builder1 = new AlertDialog.Builder(KonfirmasiActivity.this);
                builder1.setMessage("Write your message here.");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                sendData();
                                dialog.cancel();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
                Toast.makeText(this, "memulai antri", Toast.LENGTH_SHORT).show();
                break;

        }
    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void sendData(){
        //algoritma untuk POST login
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, //request post
                "http://apitest.kinaryatama.id/sms/add_antrian.php",
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        Log.d("CREATE", response);
                        try{
                            //mengubah string menjadi jsonObject
                            JSONObject object = new JSONObject(response);
                            //mendapatkan string dari status
                            String status = object.getString("status");
                            //jika berhasil ditambahkan
                            if(status.equalsIgnoreCase("success")){
                                //Menyimpan data Login
                                JSONObject loginData = object.getJSONObject("data");
                                Intent intent = new Intent(KonfirmasiActivity.this,DetailKartuAntrianActivity.class);
                                intent.putExtra("Nama", PasienView.getText().toString());
                                intent.putExtra("Poli", PoliView.getText().toString());
                                intent.putExtra("Dokter", DokterView.getText().toString());
                                intent.putExtra("Rujukan", RujukanView.getText().toString());
                                intent.putExtra("Tanggal", TglView.getText().toString());
                                intent.putExtra("estimasi", loginData.getString("estimasi"));
                                intent.putExtra("nomor", loginData.getString("nomor"));
                                intent.putExtra("norm", loginData.getString("norm"));

                                //menampilkan pesan berhasil
                                Toast.makeText(KonfirmasiActivity.this,object.getString("message"),Toast.LENGTH_LONG).show();
                                startActivity(intent);
                                progressDialog.dismiss();
                                finish();
                            }else {
                                //menampilkan pesan gagal
                                Log.d("CREATE", status);
                                progressDialog.dismiss();
                                Toast.makeText(KonfirmasiActivity.this, object.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        }catch (Exception e){
                            Log.d("CREATE", e.toString());
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Log.d("CREATE", error.toString());
                        Toast.makeText(KonfirmasiActivity.this,"Error, Login Gagal <!>",Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }){

            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                //menambahkan parameter post, nama put sama dengan nama variable pada webservice PHP
                params.put("idjadwal", idjadwal);
                params.put("norm", norm);
                params.put("tanggal", TglView.getText().toString());
                params.put("norujukan", norujukan);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }


}