package pl.edu.pwr.musicalone;


import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.content.Intent;

public class Settings extends AppCompatActivity  {

    private Spinner spinner;
    private Switch dark;

    private TextView looping;
    private int colorPrim=Color.BLACK;
    private int colorSec=Color.WHITE;
    private Intent returnIntent=new Intent();
    private ConstraintLayout background;


    private void changeColors(){
        dark.setTextColor(colorPrim);
        background.setBackgroundColor(colorSec);
        //spinner.setBackgroundColor(Color.WHITE);
        //spinner.setDrawingCacheBackgroundColor(colorSec);
        looping.setTextColor(colorPrim);
        findViewById(R.id.constraintLayout).setBackgroundColor(Color.WHITE);
    }



    @Override
    public void onBackPressed() {
        returnIntent.putExtra("dark",dark.isChecked());

        setResult(RESULT_OK, returnIntent);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        background= findViewById(R.id.background);
        dark=findViewById(R.id.darkSwitch);
        looping=findViewById(R.id.loopingText);



        //final Intent returnIntent = new Intent();

        spinner = findViewById(R.id.spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.settings, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);

                // Notify the selected item text
                returnIntent.putExtra("loop", selectedItemText);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        dark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(dark.isChecked()) {
                    colorSec = Color.BLACK;
                    colorPrim = Color.WHITE;
                }
                else {
                    colorSec = Color.WHITE;
                    colorPrim = Color.BLACK;
                }
                changeColors();
                returnIntent.putExtra("dark",dark.isChecked());
            }
        });

        dark.setChecked(getIntent().getBooleanExtra("isDark",false));
        String mode = getIntent().getStringExtra("mode");
        ArrayAdapter myAdap = (ArrayAdapter) spinner.getAdapter(); //cast to an ArrayAdapter

        int spinnerPosition = myAdap.getPosition(mode);

//set the default according to value
        spinner.setSelection(spinnerPosition);

        changeColors();




    }
}
