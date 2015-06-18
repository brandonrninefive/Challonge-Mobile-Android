package com.example.brandon.challongemobile;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import javax.net.ssl.HttpsURLConnection;

public class ShowPlayer extends ActionBarActivity
{
    private String username;
    private String password;
    private String data;
    private ListView listView;
    private ArrayList<String>  playerList;
    private ArrayAdapter<String> arrayadapt;
    private String[] playerArray;
    private BufferedReader b;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_player);
        listView = (ListView) findViewById(R.id.listView3);
        playerList = new ArrayList<String>();
        username = getIntent().getExtras().getString("Username");
        password = getIntent().getExtras().getString("Password");
        data = getIntent().getExtras().getString("Data");

        new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    URL url = new URL("https://api.challonge.com/v1/tournaments/" + data +".json?include_participants=1");

                    HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();

                    connection.setRequestProperty("Accept-Encoding","");

                    connection.setRequestProperty("Authorization","Basic " + new String(Base64.encode(new String(username + ":" +password).getBytes(),Base64.NO_WRAP)));

                    connection.connect();

                    if(connection.getResponseCode() == 200)
                    {
                        b = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String temp = "";

                        while((temp = b.readLine())!=null)
                            data+=temp;

                            JSONObject tournament = new JSONObject(data);
                            tournament = tournament.getJSONObject("tournament");

                            JSONArray participants = tournament.getJSONArray("participants");

                            for(int i = 0;i<participants.length();i++)
                            {
                                JSONObject player = participants.getJSONObject(i).getJSONObject("participant");
                                playerList.add(player.getString("name"));
                            }

                        playerArray= new String[playerList.size()];

                        for(int j=0; j<playerArray.length; j++)
                            playerArray[j]=playerList.get(j);

                        ShowPlayer.this.runOnUiThread(new Runnable()
                        {
                            public void run()
                            {
                                startList();
                            }
                        });
                    }
                    else
                    {
                        System.out.println(connection.getResponseCode() + ":" + connection.getResponseMessage());
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void noPlayers()
    {
       // TextView tv = (TextView) findViewById(R.id.);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
    public void runActivity(String data)
    {
        //Intent intent = new Intent(this,ShowPlayer.class);
        //intent.putExtra("data",data);
       // startActivity(intent);
    }
    public void startList()
    {
        arrayadapt = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, playerArray);

        listView.setAdapter(arrayadapt);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                final String text = ((TextView)view).getText().toString();
                new Thread(new Runnable()
                {
                    public void run()
                    {
                        try
                        {
                            URL url = new URL("https://api.challonge.com/v1/tournaments/"+text+".json?include_participants=1");

                            HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();


                            connection.setRequestProperty("Accept-Encoding","");

                            connection.setRequestProperty("Authorization","Basic " + new String(Base64.encode(new String(username + ":" +password).getBytes(),Base64.NO_WRAP)));

                            connection.connect();

                            if(connection.getResponseCode() == 200)
                            {
                                BufferedReader b = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                                String temp = "";
                                data = "";
                                while((temp = b.readLine())!=null)
                                    data+=temp;

                                ShowPlayer.this.runOnUiThread(new Runnable()
                                {
                                    public void run()
                                    {
                                        runActivity(data);
                                    }
                                });
                            }
                            else
                            {
                                System.out.println(connection.getResponseCode() + ":" + connection.getResponseMessage());
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }
}
