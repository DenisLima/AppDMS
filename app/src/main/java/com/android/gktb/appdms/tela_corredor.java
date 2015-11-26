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
 * Created by denis on 04/06/15.
 */
public class tela_corredor extends Activity {

    private String id;
    ProgressDialog dialog;
    JSONArray corredores = null;
    String TAG_PRIN = "corredores";
    String TAG_CORREDOR = "corredor";
    String TAG_ID = "id";
    ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
    ListView lista;

    protected void onCreate(Bundle icicle){
        super.onCreate(icicle);
        setContentView(R.layout.tela_lista_corredor);

        Intent it = getIntent();
        setId(it.getStringExtra("id"));


        Button btnSair = (Button) findViewById(R.id.btnSairCorredor);
        btnSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(tela_corredor.this, tela_data_producao.class);
                startActivity(it);
                finish();
            }
        });

        CarregaCorredor();

    }

    public void CarregaCorredor(){

        AsyncTask<String, String, JSONObject> task = new AsyncTask<String, String, JSONObject>() {

            protected void onPreExecute(){
                dialog = new ProgressDialog(tela_corredor.this);
                dialog.setMessage("Carregando ...");
                dialog.setIndeterminate(false);
                dialog.setCancelable(true);
                dialog.show();
            }

            @Override
            protected JSONObject doInBackground(String... params) {
                AdapterListView jParser = new AdapterListView();
                // Getting JSON from URL
                String ord = "http://10.55.1.242/nova_intranet/views/pcp/pcp00016/web_service.php?acao=carrega_corredor";
                JSONObject json = jParser.getJSONFromUrl(ord);

                return json;
            }

            protected void onPostExecute(JSONObject json){
                dialog.dismiss();

                try {

                    corredores = json.getJSONArray(TAG_PRIN);

                    for (int i = 0; i < corredores.length(); i++) {

                        JSONObject c = corredores.getJSONObject(i);

                        String corredor   = c.getString(TAG_CORREDOR);
                        String id         = c.getString(TAG_ID);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(TAG_CORREDOR, corredor);
                        map.put(TAG_ID, id);

                        oslist.add(map);

                        lista = (ListView) findViewById(R.id.listaCorredor);
                        final BaseAdapter adapter = new SimpleAdapter(tela_corredor.this, oslist, R.layout.corredor_custom,
                                new String[] {TAG_CORREDOR, TAG_ID},
                                new int[] {R.id.txtCorredor, R.id.txtIdCorredor});
                        lista.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                final String corredor = oslist.get(+position).get("id");
                                //Toast.makeText(tela_corredor.this, "O item clicado foi: " + corredor, Toast.LENGTH_SHORT).show();

                                Intent it = new Intent(tela_corredor.this, tela_detalhes_programacao.class);
                                it.putExtra("id", getId());
                                it.putExtra("corredor", corredor);
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
