package hugo.alberto.ah2foot;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static String url = "http://api.football-data.org/v1/competitions/457/leagueTable";
    private ProgressDialog pDialog;
    private JsonParseHandler jsonParseHandler;
    
    
    private TextView mTextMessage;
    private LinearLayout tabelaLayout;
    private static final String TAG_CLASSIFICACAO= "position";
    private static final String TAG_CLUBE = "teamName";
    private static final String TAG_PONTOS = "points";
    private static final String TAG_VITORIAS = "wins";
    private static final String TAG_JOGOS= "playedGames";
    private static final String TAG_EMPATES = "draws";
    private static final String TAG_DERROTAS = "losses";
    private static final String TAG_SALDO = "goals";
    private ListView lv;
    private ArrayList<HashMap<String, String>> tabelaList;
    
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    tabelaList.clear();
                    new GetTabela().execute();
                    tabelaLayout.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    tabelaLayout.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    tabelaLayout.setVisibility(View.GONE);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
    
        tabelaLayout = (LinearLayout) findViewById(R.id.tabelaLayout);
        tabelaList = new ArrayList<HashMap<String, String>>();
        lv = (ListView) findViewById(android.R.id.list);
        new GetTabela().execute();
        
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
    
    
    private class GetTabela extends AsyncTask<Void, Void, Void> {
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Atualizando...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
        
        @Override
        protected Void doInBackground(Void... arg0) {
            
            JsonParseHandler sh = new JsonParseHandler();
            
            String jsonStr = sh.serviceCall(url, JsonParseHandler.GET);
            
            Log.d("Response: ", "> " + jsonStr);
            
            if (jsonStr != null) {
                try {
                    //JSONObject jsonObj = new JSONObject(jsonStr);
    
                    JSONObject elenco = new JSONObject(jsonStr);
    
                    JSONArray elenco2 = elenco.getJSONArray("standing");
                    // elenco = jsonObj.getJSONArray(TAG_CONTACTS);
                    
                    for (int i = 0; i < elenco2.length(); i++) {
                        JSONObject c = elenco2.getJSONObject(i);
                        
                        String posicao = c.getString(TAG_CLASSIFICACAO);
                        String nome_equipe = c.getString(TAG_CLUBE);
                        String pontos = c.getString(TAG_PONTOS);
                        String num_vitorias = c.getString(TAG_VITORIAS);
                        String num_jogos = c.getString(TAG_JOGOS);
                        String num_empates = c.getString(TAG_EMPATES);
                        String num_derrotas = c.getString(TAG_DERROTAS);
                        String saldo_gols = c.getString(TAG_SALDO);
                        
                        HashMap<String, String> contact = new HashMap<String, String>();
                        
                        contact.put(TAG_CLASSIFICACAO, posicao);
                        //contact.put(TAG_CLUBE, nome_equipe);
                        contact.put(TAG_PONTOS, pontos);
                        contact.put(TAG_VITORIAS, num_vitorias);
                        contact.put(TAG_JOGOS, num_jogos);
                        contact.put(TAG_EMPATES, num_empates);
                        contact.put(TAG_DERROTAS, num_derrotas);
                        contact.put(TAG_SALDO, saldo_gols);
                        
                        if(nome_equipe.equals("VitÃ³ria")){
                            contact.put(TAG_CLUBE, "Vitória");
                        }else if(nome_equipe.equals("AmÃ©rica-MG")){
                            contact.put(TAG_CLUBE, "America-MG");
                        }else if(nome_equipe.equals("Sampaio CorrÃªa")){
                            contact.put(TAG_CLUBE, "Sampaio Corrêa");
                        }else if(nome_equipe.equals("ParanÃ¡")){
                            contact.put(TAG_CLUBE, "Paraná");
                        }else if(nome_equipe.equals("CriciÃºma")){
                            contact.put(TAG_CLUBE, "Criciúma");
                        }else if(nome_equipe.equals("AtlÃ©tico-GO")){
                            contact.put(TAG_CLUBE, "Atlético-GO");
                        }else if(nome_equipe.equals("MacaÃ©")){
                            contact.put(TAG_CLUBE, "Macaé");
                        }else if(nome_equipe.equals("CearÃ¡")){
                            contact.put(TAG_CLUBE, "Ceará");
                        }else if(nome_equipe.equals("NÃ¡utico")){
                            contact.put(TAG_CLUBE, "Náutico");
                        }else if(nome_equipe.equals("AvaÃ\u00AD")){
                            contact.put(TAG_CLUBE, "Avaí");
                        }else if(nome_equipe.equals("GoiÃ¡s")){
                            contact.put(TAG_CLUBE, "Goiás");
                        }else {
                            contact.put(TAG_CLUBE, nome_equipe);
                        }
                        
                        tabelaList.add(contact);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            
            return null;
        }
        
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            
            if (MainActivity.this != null){
               
                ListAdapter adapter = new SimpleAdapter(
                        MainActivity.this, tabelaList,
                        R.layout.list_item_tabela, new String[] { TAG_CLASSIFICACAO, TAG_CLUBE, TAG_PONTOS, TAG_VITORIAS, TAG_JOGOS, TAG_EMPATES, TAG_DERROTAS, TAG_SALDO }, new int[] { R.id.classificacao, R.id.clube, R.id.pontos, R.id.vitorias, R.id.jogos, R.id.empates, R.id.derrotas, R.id.saldo,
                });
                
                lv.setAdapter(adapter);
            }
        }
        
    }
    
}
