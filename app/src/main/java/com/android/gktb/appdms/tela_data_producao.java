package com.android.gktb.appdms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

/**
 * Created by denis on 27/05/15.
 */
public class tela_data_producao extends Activity {

    String dia;
    String mes;
    String ano;

    String dataProd;

    protected void onCreate(Bundle icicle){
        super.onCreate(icicle);
        setContentView(R.layout.tela_data_producao);

        final DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);

        Button btnEnviar = (Button) findViewById(R.id.btnEnviarData);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dia = String.valueOf(datePicker.getDayOfMonth());
                mes = String.valueOf(datePicker.getMonth()+1);
                ano = String.valueOf(datePicker.getYear());

                if (mes.length() <= 1){
                    mes = "0"+mes;
                }

                dataProd = ano+mes+dia;

                Intent it = new Intent(tela_data_producao.this, tela_programacao.class);
                it.putExtra("dataProd", dataProd);
                startActivity(it);
                finish();

            }
        });

    }

}
