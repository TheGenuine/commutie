package de.reneruck.traincheck;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.reneruck.traincheck.models.Tracker;

public class EditTrackerActivity extends Activity {

    private Tracker mCurrentTracker = new Tracker();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tracker);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        int trackerId = getIntent().getIntExtra("trackerId", -1);
        if(trackerId > 0) {
            mCurrentTracker = Tracker.find(this, trackerId);
        }

        String[] stations = getResources().getStringArray(R.array.stations);
        setupSpinner(R.id.select_primary_station, new String[]{}, stations, new String[]{});
        setupSpinner(R.id.select_direction_primary, new String[]{"Northbound", "Southbound"}, stations, new String[]{});

//        setupSpinner(R.id.select_secondary_station, new String[]{}, stations, new String[]{});
//        setupSpinner(R.id.select_direction_secondary, new String[]{"Northbound", "Southbound"}, stations, new String[]{});

        findViewById(R.id.input_start_time).setOnClickListener(timeChooserListener);
        findViewById(R.id.input_end_time).setOnClickListener(timeChooserListener);

        findViewById(R.id.text_monday).setOnClickListener(dayListener);
        findViewById(R.id.text_tuesday).setOnClickListener(dayListener);
        findViewById(R.id.text_wednesday).setOnClickListener(dayListener);
        findViewById(R.id.text_thursday).setOnClickListener(dayListener);
        findViewById(R.id.text_friday).setOnClickListener(dayListener);
        findViewById(R.id.text_saturday).setOnClickListener(dayListener);
        findViewById(R.id.text_sunday).setOnClickListener(dayListener);

        setValues(stations);

    }

    private void setValues(String[] stations) {
        setDay(R.id.text_monday, mCurrentTracker.getDay(0));
        setDay(R.id.text_tuesday, mCurrentTracker.getDay(1));
        setDay(R.id.text_wednesday, mCurrentTracker.getDay(2));
        setDay(R.id.text_thursday, mCurrentTracker.getDay(3));
        setDay(R.id.text_friday, mCurrentTracker.getDay(4));
        setDay(R.id.text_saturday, mCurrentTracker.getDay(5));
        setDay(R.id.text_sunday, mCurrentTracker.getDay(6));
        ((TextView)findViewById(R.id.input_start_time)).setText(mCurrentTracker.getStartTrackingAtFormatted());
        ((TextView)findViewById(R.id.input_end_time)).setText(mCurrentTracker.getStopTrackingAtFormatted());
        ((Spinner) findViewById(R.id.select_primary_station)).setSelection(ArrayUtils.indexOf(stations, mCurrentTracker.getPrimaryStation()));
        ((Spinner) findViewById(R.id.select_direction_primary)).setSelection(ArrayUtils.indexOf(stations, mCurrentTracker.getDirectionPrimary()) + 2);
//        ((Spinner) findViewById(R.id.select_secondary_station)).setSelection(ArrayUtils.indexOf(stations, mCurrentTracker.getSecondaryStation()));
//        ((Spinner) findViewById(R.id.select_direction_secondary)).setSelection(ArrayUtils.indexOf(stations, mCurrentTracker.getDirectionSecondary()) + 2);
    }

    private void setDay(int fieldId, boolean selected) {
        TextView textView = (TextView)findViewById(fieldId);
        if(selected) {
            textView.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            textView.setTextColor(Color.BLACK);
        } else {
            textView.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            textView.setTextColor(Color.DKGRAY);
        }
    }

    private void setupSpinner(int spinnerId, String[] preElements, String[] elements,String[] postElements) {
        Spinner spinner = (Spinner) findViewById(spinnerId);
        String[] stations = ArrayUtils.addAll(preElements, elements);
        stations = ArrayUtils.addAll(stations, postElements);

        ArrayAdapter<String> adapter  = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stations);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(spinnerItemSelectedListener);
    }

    private View.OnClickListener timeChooserListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {

            String[] split = new String[]{};
            switch(v.getId()){
                case R.id.input_start_time:
                    split = mCurrentTracker.getStartTrackingAt().split(":");
                    break;
                case R.id.input_end_time:
                    split = mCurrentTracker.getStartTrackingAt().split(":");
                    break;
            }

            int hour = Integer.valueOf(split[0]);
            int minute = Integer.valueOf(split[1]);

            new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    switch(v.getId()){
                        case R.id.input_start_time:
                            mCurrentTracker.setStartTrackingAt(hourOfDay + ":" + minute);
                            ((TextView) v).setText(mCurrentTracker.getStartTrackingAtFormatted());
                            break;
                        case R.id.input_end_time:
                            mCurrentTracker.setStopTrackingAt(hourOfDay + ":" + minute);
                            ((TextView) v).setText(mCurrentTracker.getStopTrackingAtFormatted());
                            break;
                    }
                }
            }, hour, minute, true).show();

        }
    };

    private View.OnClickListener dayListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView textView = (TextView)v;
            int pos = Integer.valueOf(v.getTag().toString());
            boolean selected = textView.getTypeface().isBold();

            if(selected) {
                textView.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                textView.setTextColor(Color.DKGRAY);
                mCurrentTracker.setDay(pos, false);
            } else {
                textView.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                textView.setTextColor(Color.BLACK);
                mCurrentTracker.setDay(pos, true);
            }
        }
    };

    private AdapterView.OnItemSelectedListener spinnerItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            TextView textView = (TextView) view;
            String value = textView.getText().toString();
            switch(parent.getId()){
                case R.id.select_primary_station:
                    mCurrentTracker.setPrimaryStation(value);
                    break;
                case R.id.select_direction_primary:
                    mCurrentTracker.setDirectionPrimary(value);
                    break;
//                case R.id.select_secondary_station:
//                    mCurrentTracker.setSecondaryStation(value);
//                    break;
//                case R.id.select_direction_secondary:
//                    mCurrentTracker.setDirectionSecondary(value);
//                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_tracker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        switch (item.getItemId()) {
            case R.id.action_tracker_save:
                if(mCurrentTracker.save(this)){
                    finishActivity(mCurrentTracker.getTrackerId());
                } else {
                    Toast.makeText(this, "Something went wrong while saving the tracker", Toast.LENGTH_LONG).show();
                }
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
