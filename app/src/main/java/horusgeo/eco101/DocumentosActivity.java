package horusgeo.eco101;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;
import java.util.List;

public class DocumentosActivity extends AppCompatActivity {

    DBHandler db;

    ArrayList<Docs> docs;

    Spinner spinner;

    ImageHelper imgPhoto;

    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documentos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        final String idProp = intent.getStringExtra("idProp");

        db = new DBHandler(this, null, null, 1);
        docs = db.getDBDocs(idProp);

        db.removeDocs(idProp);

        spinner = (Spinner) findViewById(R.id.docSpinner);

        layout = (LinearLayout) findViewById(R.id.docsLayout);

        imgPhoto = new ImageHelper();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.addDoc(docs);
                Intent intent = new Intent(DocumentosActivity.this, CadastroActivity.class);
                intent.putExtra("tipo", "edit");
                intent.putExtra("string", idProp);
                intent.putExtra("user", "");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed(){

    }

    @Override
    protected void onStart(){
        super.onStart();

        final List<String> list = new ArrayList<String>();
        for(Docs temp : docs){
            Integer id = FotoTypes.CODES.indexOf(Integer.parseInt(temp.getType()));
            if(!list.contains(FotoTypes.NAMES.get(id)))
                list.add(FotoTypes.NAMES.get(id));
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,list);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Integer type = FotoTypes.CODES.get(FotoTypes.NAMES.indexOf(list.get(position)));
                while(layout.getChildCount() > 1)
                    layout.removeViewAt(1);
                Log.d("HorusGeo", "type = " + type);
                callThumbs(type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public void callThumbs(Integer type){
        for(Docs temp : docs){
            if(temp.getType().equals(String.valueOf(type))){
                addThumb(temp.getPath());
            }
        }
    }

    public void addThumb(final String file){
        LayoutInflater inflater = this.getLayoutInflater();
        View rootView = inflater.inflate(R.layout.layout_docs, null);

        ImageView img = (ImageView) rootView.findViewById(R.id.docShow);
        ImageButton button = (ImageButton) rootView.findViewById(R.id.delDoc);
        Log.d("HorusGeo", file);
        img.setImageBitmap(imgPhoto.decodeSampledBitmapFromFile(file, 300, 300));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(Docs temp : docs){
                    if(temp.getPath().equals(file)) {
                        docs.remove(temp);
                        break;
                    }
                }

                Integer index = FotoTypes.NAMES.indexOf(spinner.getSelectedItem().toString());

                while(layout.getChildCount() > 1)
                    layout.removeViewAt(1);
                callThumbs(FotoTypes.CODES.get(index));
            }
        });

        layout.addView(rootView);
    }
}