package com.android.gktb.appdms;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.gktb.library.AdapterListView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by denis on 29/05/15.
 */
public class tela_detalhes_programacao extends Activity {

    private String id;
    private String corredor;
    ProgressDialog dialog;
    JSONArray programacao = null;
    String TAG_PRIN = "programacao";
    String TAG_LOTE = "codigoLote";
    String TAG_ID = "id";
    String TAG_MODELO = "modelo";
    String TAG_LOCAL = "local";
    String TAG_MATERIAL = "material";
    String TAG_DATA = "data";
    String TAG_FABRICANTE = "fabricante";
    String TAG_ESTOQUE = "estoque";
    String TAG_QTD = "qtd";
    String TAG_CORREDOR = "corredor";
    ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
    ListView lista;

    protected void onCreate(Bundle icicle){
        super.onCreate(icicle);
        setContentView(R.layout.tela_detalhe_programacao);

        Button btnSair = (Button) findViewById(R.id.btnVoltarDetalhes);
        btnSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(tela_detalhes_programacao.this, tela_data_producao.class);
                startActivity(it);
                finish();
            }
        });

        Intent intent = getIntent();
        setId(intent.getStringExtra("id"));
        setCorredor(intent.getStringExtra("corredor"));

        Button btnVoltar = (Button) findViewById(R.id.btnVoltarDetalhesFinal);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(tela_detalhes_programacao.this, tela_corredor.class);
                i.putExtra("id",getId());
                startActivity(i);
                finish();
            }
        });

        CarregaDetalhesProgramacao();

    }

    public void CarregaDetalhesProgramacao(){

        AsyncTask<String, String, JSONObject> task = new AsyncTask<String, String, JSONObject>() {

            protected void onPreExecute(){
                dialog = new ProgressDialog(tela_detalhes_programacao.this);
                dialog.setMessage("Carregando ...");
                dialog.setIndeterminate(false);
                dialog.setCancelable(true);
                dialog.show();
            }

            @Override
            protected JSONObject doInBackground(String... params) {
                AdapterListView jParser = new AdapterListView();
                // Getting JSON from URL
                String ord = "http://10.55.1.242/nova_intranet/views/pcp/pcp00016/web_service.php?acao=carrega_detalhes&&idProg="+getId()+"&&corredor="+getCorredor();
                JSONObject json = jParser.getJSONFromUrl(ord);

                return json;
            }

            protected void onPostExecute(JSONObject json){
                dialog.dismiss();

                try {

                    programacao = json.getJSONArray(TAG_PRIN);

                    for (int i = 0; i < programacao.length(); i++) {

                        JSONObject c = programacao.getJSONObject(i);

                        String codLote    = c.getString(TAG_LOTE);
                        String id         = c.getString(TAG_ID);
                        String modelo     = c.getString(TAG_MODELO);
                        String local      = c.getString(TAG_LOCAL);
                        String material   = c.getString(TAG_MATERIAL);
                        String data       = c.getString(TAG_DATA);
                        String fabricante = c.getString(TAG_FABRICANTE);
                        String estoque    = c.getString(TAG_ESTOQUE);
                        String qtd        = c.getString(TAG_QTD);
                        String corredor   = c.getString(TAG_CORREDOR);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(TAG_LOTE, codLote);
                        map.put(TAG_ID, id);
                        map.put(TAG_MODELO, modelo);
                        map.put(TAG_LOCAL, local);
                        map.put(TAG_MATERIAL, material);
                        map.put(TAG_DATA, data);
                        map.put(TAG_FABRICANTE, fabricante);
                        map.put(TAG_ESTOQUE, estoque);
                        map.put(TAG_QTD, qtd);
                        map.put(TAG_CORREDOR, corredor);

                        oslist.add(map);

                        lista = (ListView) findViewById(R.id.listaDetalhes);
                        final BaseAdapter adapter = new SimpleAdapter(tela_detalhes_programacao.this, oslist, R.layout.detalhes_custom,
                                new String[] {TAG_LOTE, TAG_ID, TAG_MODELO, TAG_LOCAL, TAG_MATERIAL, TAG_DATA, TAG_FABRICANTE, TAG_ESTOQUE, TAG_QTD, TAG_CORREDOR},
                                new int[] {R.id.txtDetalhesLote, R.id.txtDetalhesId, R.id.txtDetalhesModelo, R.id.txtDetalheLocal, R.id.txtDetalheMaterial,
                                           R.id.txtDetalheData, R.id.txtDetalheFabricante, R.id.txtDetalheEstoque, R.id.txtDetalheQtd, R.id.txtDetalhesCorredor});
                        lista.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                final String idProg = oslist.get(+position).get("id");
                                //Toast.makeText(tela_detalhes_programacao.this, "O item clicado foi: " + idProg, Toast.LENGTH_SHORT).show();

                                AlertDialog.Builder alert = new AlertDialog.Builder(tela_detalhes_programacao.this);
                                alert.setTitle("Aviso");
                                alert.setMessage("Você deseja alimentar esse material?");

                                alert.setNegativeButton("Não",new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });

                                alert.setPositiveButton("Sim",new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        AlimentaComponente(idProg);
                                      /*  Intent it = new Intent(tela_detalhes_programacao.this, tela_detalhes_programacao.class);
                                        it.putExtra("id", getId());
                                        startActivity(it);*/
                                    }
                                });

                                alert.show();

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

    public void AlimentaComponente(final String idComp){

        AsyncTask<String, Object, String> asyncTask = new AsyncTask<String, Object, String>() {

            @Override
            protected void onPreExecute(){
                dialog = new ProgressDialog(tela_detalhes_programacao.this);
                dialog.setMessage("Aguarde...");
                dialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost("http://10.55.1.242/nova_intranet/views/pcp/pcp00016/web_service.php");

                try {

                    List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                    pairs.add(new BasicNameValuePair("acao", "alimentacao"));
                    pairs.add(new BasicNameValuePair("idProg", idComp));
                    post.setEntity(new UrlEncodedFormEntity(pairs));

                    HttpResponse response = client.execute(post);
                    String responseString = EntityUtils.toString(response.getEntity());

                    return responseString.trim();

                } catch (Exception e) {
                    return null;
                }
            }

            protected void onPostExecute(String result) {

                dialog.dismiss();

                String r[] = result.split(";");

                if (r[0].equals("1")) {

                 /*   AlertDialog.Builder alert = new AlertDialog.Builder(tela_detalhes_programacao.this);
                    alert.setTitle("Aviso");
                    alert.setMessage("Material alimentado com sucesso!");

                    alert.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                           Intent it = new Intent(tela_detalhes_programacao.this, tela_detalhes_programacao.class);
                           it.putExtra("id", getId());
                           it.putExtra("corredor", getCorredor());
                           startActivity(it);

                        }
                    });

                    alert.show();*/
                    Intent it = new Intent(tela_detalhes_programacao.this, tela_detalhes_programacao.class);
                    it.putExtra("id", getId());
                    it.putExtra("corredor", getCorredor());
                    startActivity(it);

                } else {
                    Toast.makeText(tela_detalhes_programacao.this, "Problemas para alimentar o material, favor verificar!", Toast.LENGTH_LONG).show();
                }
            }

        };
        asyncTask.execute();

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCorredor() {
        return corredor;
    }

    public void setCorredor(String corredor) {
        this.corredor = corredor;
    }
}
