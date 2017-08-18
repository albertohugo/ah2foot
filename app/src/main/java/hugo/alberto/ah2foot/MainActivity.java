package hugo.alberto.ah2foot;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar=null;
    private String[] category;
    private ProgressDialog pDialog;
    private JsonParseHandler jsonParseHandler;
    private TextView mTextMessage;
    private Spinner navigationSpinner;

    private static String urlTable = "http://api.football-data.org/v1/competitions/457/leagueTable";
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

    private static String urlCompetitions = "http://api.football-data.org/v1/competitions/?season=2017";
    private static final String TAG_CAMPEONATO= "caption";
    private static final String TAG_IDCAMPEONATO = "id";
    private ArrayList<Campeonato> listCamp = new ArrayList<Campeonato>();

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

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setLogo(R.mipmap.ic_launcher);



        new GetCampeonatos().execute();





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
            
            String jsonStr = sh.serviceCall(urlTable, JsonParseHandler.GET);
            
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
                        contact.put(TAG_CLUBE, nome_equipe);
                        contact.put(TAG_PONTOS, pontos);
                        contact.put(TAG_VITORIAS, num_vitorias);
                        contact.put(TAG_JOGOS, num_jogos);
                        contact.put(TAG_EMPATES, num_empates);
                        contact.put(TAG_DERROTAS, num_derrotas);
                        contact.put(TAG_SALDO, saldo_gols);
                        
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

    private class GetCampeonatos extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            JsonParseHandler sh = new JsonParseHandler();
            String jsonStr = sh.serviceCall(urlCompetitions, JsonParseHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {

                    JSONArray elenco = new JSONArray(jsonStr);

                    // elenco = jsonObj.getJSONArray(TAG_CONTACTS);

                    for (int i = 0; i < elenco.length(); i++) {
                        JSONObject c = elenco.getJSONObject(i);

                        String campeonato = c.getString(TAG_CAMPEONATO);
                        String id_campeonato = c.getString(TAG_IDCAMPEONATO);

                        Campeonato camp = new Campeonato(id_campeonato, campeonato);
                        listCamp.add(camp);
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

            category = listCampeonatoLabel(listCamp);//getResources().getStringArray(R.array.category);
           // category = getResources().getStringArray(R.array.category);
            SpinnerAdapter adapter = new ArrayAdapter<String>(
                    getApplicationContext(), R.layout.spinner_dropdown_item, category);
            SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.category, R.layout.spinner_dropdown_item);
            navigationSpinner = new Spinner(getSupportActionBar().getThemedContext());
            //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            navigationSpinner.setAdapter(adapter);
            toolbar.addView(navigationSpinner, 0);

            navigationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(MainActivity.this,
                            "you selected: " + category[i],
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        }

    }

    public String[] listCampeonatoLabel(ArrayList<Campeonato> listCamp){
        String[] list = new String[100];
        ArrayList<String> listItems = new ArrayList<String>();
        for (int i = 0; i < listCamp.size(); ++i) {
            //listItems.add(listCamp.get(i).getCampeonato());
            String str=listCamp.get(i).getCampeonato();
            list[i]=str;
        }


        return list;
    }
    
}
