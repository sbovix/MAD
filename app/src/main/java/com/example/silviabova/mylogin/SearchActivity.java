package com.example.silviabova.mylogin;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

public class SearchActivity extends AppCompatActivity {

    public ListView lst;
    public ArrayAdapter adapter;
    public DatabaseReference database;
    public Spinner spin;

    List<String> LIST = new ArrayList<String>();
    List<String> isbnList = new ArrayList<String>();
    private int spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        lst = (ListView)findViewById(R.id.ListView);
        spin = (Spinner)findViewById(R.id.Spinner);

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,LIST);
        lst.setAdapter(adapter);
        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getBaseContext(), "List item " + (i + 1) + " Selected", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SearchActivity.this, BookUserDetails.class);
                intent.putExtra("ISBN",isbnList.get(i));
                startActivity(intent);
            }
        });


        database = FirebaseDatabase.getInstance().getReference("/Books/");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //elimina la barra sopra
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //permette di mostrare il logo
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo);

    }



    //back button in the navigation bar
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivityForResult(myIntent, 0);

        return true;

    }


    @Override

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search,menu);

        MenuItem item = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView)item.getActionView();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String s) {
                LIST.clear();
                isbnList.clear();
                database.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String selected;
                        String selectedItem = spin.getSelectedItem().toString().trim();//riferisce alla stringa che utente seleziona

                        if(selectedItem.compareTo("Titolo")==0 || selectedItem.compareTo("Title")==0 || selectedItem.compareTo("Titre")==0
                                || selectedItem.compareTo("Título")==0) {
                            selected= "title";
                          //  Toast.makeText(SearchActivity.this, selected, Toast.LENGTH_SHORT).show();

                        }
                        else if (selectedItem.compareTo("Autore")==0 || selectedItem.compareTo("Author")==0 || selectedItem.compareTo("Auteur")==0
                                || selectedItem.compareTo("Autor")==0) {
                            selected  = "author";
                         //   Toast.makeText(SearchActivity.this, selected, Toast.LENGTH_SHORT).show();

                        }
                        else if (selectedItem.compareTo("Editore")==0 || selectedItem.compareTo("Publisher")==0 || selectedItem.compareTo("Éditeur")==0
                                || selectedItem.compareTo("Año de edición")==0) {
                            selected  = "publisher";
                          //  Toast.makeText(SearchActivity.this, selected, Toast.LENGTH_SHORT).show();

                        }

                        else if (selectedItem.compareTo("Anno")==0 || selectedItem.compareTo("Year")==0 || selectedItem.compareTo("Année")==0
                                || selectedItem.compareTo("Editor")==0) {
                            selected  = "edition_year";
                           // Toast.makeText(SearchActivity.this, selected, Toast.LENGTH_SHORT).show();

                        }

                        else if (selectedItem.compareTo("Condizioni")==0 || selectedItem.compareTo("Condition")==0 || selectedItem.compareTo("Condiciones")==0
                                || selectedItem.compareTo("Conditions")==0) {
                            selected  = "book_condition";
                           // Toast.makeText(SearchActivity.this, selected, Toast.LENGTH_SHORT).show();

                        }

                        else{
                            selected = "title";
                        }


                        boolean found = false;
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            if(ds.child(selected).getValue(String.class).toLowerCase().contains(s.toLowerCase().trim())){
                                LIST.add(ds.child(selected).getValue(String.class));
                                isbnList.add(ds.getKey().toString());
                                // Log.d("ISBN", "ho trovato "+B.getKey());
                                //Toast.makeText(SearchActivity.this, "Found", Toast.LENGTH_SHORT).show();
                                found = true;
                            }
                        }

{

}
                        if(found == false) {
//                                  LIST.add("Nessun elemento trovato");
                            Toast.makeText(SearchActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);

    }
}

