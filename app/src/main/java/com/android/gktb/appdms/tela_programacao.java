package com.android.gktb.appdms;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.gktb.library.AdapterListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by denis on 28/05/15.
 */
public class tela_programacao extends Activity {

    private String dataProd;
    ProgressDialog dialog;
    JSONArray programacao = null;
    String TAG_PRIN = "programacao";
    String TAG_LOTE = "codigoLote";
    String TAG_MODELO = "modelo";
    String TAG_LOCAL = "local";
    String TAG_DATA = "dataProd";
    String TAG_QTD = "qtd";
    String TAG_ID = "id";
    ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
    ListView lista;

    protected void onCreate(Bundle icicle){
        super.onCreate(icicle);
        setContentView(R.layout.tela_lista_programacao);

        Intent it = getIntent();
        setDataProd(it.getStringExtra("dataProd"));

        Button btnSair = (Button) findViewById(R.id.btnSairProgramacao);
        btnSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(tela_programacao.this, tela_data_producao.class);
                startActivity(i);
                finish();
            }
        });


        CarregaProgramacaoSeparada();

    }

    public void CarregaProgramacaoSeparada(){

        AsyncTask<String, String, JSONObject> task = new AsyncTask<String, String, JSONObject>() {

            protected void onPreExecute(){
                dialog = new ProgressDialog(tela_programacao.this);
                dialog.setMessage("Carregando ...");
                dialog.setIndeterminate(false);
                dialog.setCancelable(true);
                dialog.show();
            }

            @Override
            protected JSONObject doInBackground(String... params) {
                AdapterListView jParser = new AdapterListView();
                // Getting JSON from URL
                String ord = "http://10.55.1.242/nova_intranet/views/pcp/pcp00016/web_service.php?acao=carrega_programacao&&dataProd="+getDataProd();
                JSONObject json = jParser.getJSONFromUrl(ord);

                return json;
            }

            protected void onPostExecute(JSONObject json){
                dialog.dismiss();

                try {

                    programacao = json.getJSONArray(TAG_PRIN);

                    for (int i = 0; i < programacao.length(); i++) {

                        JSONObject c = programacao.getJSONObject(i);

                        String codLote  = c.getString(TAG_LOTE);
                        String modelo   = c.getString(TAG_MODELO);
                        String local    = c.getString(TAG_LOCAL);
                        String dataProd = c.getString(TAG_DATA);
                        String qtd      = c.getString(TAG_QTD);
                        String id       = c.getString(TAG_ID);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(TAG_LOTE, codLote);
                        map.put(TAG_MODELO, modelo);
                        map.put(TAG_LOCAL, local);
                        map.put(TAG_DATA, dataProd);
                        map.put(TAG_QTD, qtd);
                        map.put(TAG_ID, id);

                        oslist.add(map);

                        lista = (ListView) findViewById(R.id.listaProgramacao);
                        final BaseAdapter adapter = new SimpleAdapter(tela_programacao.this, oslist, R.layout.programacao_custom,
                                new String[] {TAG_LOTE, TAG_MODELO, TAG_LOCAL, TAG_DATA, TAG_QTD, TAG_ID},
                                new int[] {R.id.txtLote, R.id.txtModelo, R.id.txtLocal, R.id.txtDataProd, R.id.txtQtd, R.id.txtId});
                        lista.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                String idProg = oslist.get(+position).get("id");
                                //Toast.makeText(tela_programacao.this, "O item clicado foi: " + idProg, Toast.LENGTH_SHORT).show();

                              /*  Intent it = new Intent(tela_programacao.this, tela_detalhes_programacao.class);
                                it.putExtra("id",idProg);
                                startActivity(it); */

                                Intent it = new Intent(tela_programacao.this, tela_corredor.class);
                                it.putExtra("id",idProg);
                                startActivity(it);
                                finish();

                            }
                        });

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        };
        task.execute();

    }

    public String getDataProd() {
        return dataProd;
    }

    public void setDataProd(String dataProd) {
        this.dataProd = dataProd;
    }
}
