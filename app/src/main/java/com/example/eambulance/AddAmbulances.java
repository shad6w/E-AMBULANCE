package com.example.eambulance;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddAmbulances extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    ListView lvambulance;
    Ambulances ambulances= new Ambulances();
    FirebaseDatabase database;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> arrayList=new ArrayList<>();
    DatabaseReference ref;
    private String hospitalID;
    private EditText searchbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ambulances);

        lvambulance = findViewById(R.id.lvambulance);
        database=FirebaseDatabase.getInstance();
        searchbar=findViewById(R.id.searchbar);

        arrayAdapter= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayList);
        lvambulance.setAdapter(arrayAdapter);

        firebaseAuth=FirebaseAuth.getInstance();
        hospitalID=firebaseAuth.getCurrentUser().getUid();
        ref= FirebaseDatabase.getInstance().getReference().child("ADMIN").child("HOSPITALS").child(hospitalID).child("AMBULANCES");

        ref.addValueEventListener(new ValueEventListener() {

           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               arrayList.clear();
                for(DataSnapshot item: dataSnapshot.getChildren()){
                    ambulances=item.getValue(Ambulances.class);
                    arrayList.add(ambulances.getInputdname().toString() +"\n"+ambulances.getInputdphone().toString() +"\n"+ambulances.getInputaid().toString() );
                    Collections.sort(arrayList);
                }

                arrayAdapter.notifyDataSetChanged();
                lvambulance.setAdapter(arrayAdapter);

           }

           @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                (AddAmbulances.this).arrayAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    //    class MyAdapter extends ArrayAdapter<String> {
//
//        public MyAdapter(@NonNull Context context, int resource) {
//            super(context, resource);
//        }
//
//        public MyAdapter(@NonNull Context context, int resource, int textViewResourceId) {
//            super(context, resource, textViewResourceId);
//        }
//
//        public MyAdapter(@NonNull Context context, int resource, @NonNull String[] objects) {
//            super(context, resource, objects);
//        }
//
//        public MyAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull String[] objects) {
//            super(context, resource, textViewResourceId, objects);
//        }
//
//        public MyAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
//            super(context, resource, objects);
//        }
//
//        public MyAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<String> objects) {
//            super(context, resource, textViewResourceId, objects);
//        }
//
//        @NonNull
//        @Override
//        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            View row = layoutInflater.inflate(R.layout.row, parent, false);
//            TextView txtname, txtphn, txtaid;
//            txtname = findViewById(R.id.txtname);
//            txtphn = findViewById(R.id.txtphn);
//            txtaid = findViewById(R.id.txtaid);
//
//            return super.getView(position, convertView, parent);
//
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflatter=getMenuInflater();
        inflatter.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.adddriver:
                Intent intent=new Intent(AddAmbulances.this,RegisterAmbulance.class);
                startActivity(intent);
                break;

            case R.id.profile:
                startActivity(new Intent(AddAmbulances.this,HospitalProfileActivity.class));
                break;

        }
        return true;
    }
}
