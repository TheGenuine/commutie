package de.reneruck.traincheck;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.provider.BasicImageButtonsCardProvider;
import com.dexafree.materialList.view.MaterialListView;

import java.util.ArrayList;
import java.util.List;

import de.reneruck.traincheck.models.Tracker;
import de.reneruck.traincheck.models.TrackerCardProvider;
import de.reneruck.traincheck.receivers.UpdateTrackingReceiver;

public class MainActivity extends Activity {

    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPreferences = getPreferences(Context.MODE_PRIVATE);
        List<Tracker> trackers = loadTrackers();
        initializeList(trackers);
    }

    private void initializeList(List<Tracker> trackers) {
        MaterialListView mListView = (MaterialListView) findViewById(R.id.material_listview);

        for(Tracker tracker : trackers){
            Card card = new Card.Builder(this)
                    .setTag(tracker.getTrackerId())
                    .withProvider(TrackerCardProvider.class)
                    .setStartTime(tracker.getStartTrackingAt())
                    .setEndTime(tracker.getStopTrackingAt())
                    .setPrimaryStation(tracker.getPrimaryStation())
                    .setPrimaryDirection(tracker.getDirectionPrimary())
                    .setEnabled(tracker.isEnabled())
                    .setOnCheckChangedListener(trackerEnableListener)
                    .setCardClickListener(cardClickListener)
                    .endConfig()
                    .build();

            mListView.add(card);
        }
    }

    private List<Tracker> loadTrackers() {
        return Tracker.find(this, null);
    }

    private CompoundButton.OnCheckedChangeListener trackerEnableListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Toast.makeText(buttonView.getContext(), "Tracker enabled " + isChecked, Toast.LENGTH_LONG).show();
        }
    };

    private View.OnClickListener cardClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = Integer.valueOf(((View)v.getParent()).getTag().toString());
            Intent intent = new Intent(getApplicationContext(), EditTrackerActivity.class);
            intent.getExtras().putInt("trackerId", id);
            startActivityForResult(intent, id);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_settings:
                return true;
            case R.id.action_add_tracker:
                startActivity(new Intent(getApplicationContext(), EditTrackerActivity.class));
                return true;
            case R.id.action_send_test_broadcast:
                Intent serviceIntent = new Intent(getApplicationContext(), UpdateTrackingReceiver.class);
                sendBroadcast(serviceIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ((MaterialListView)findViewById(R.id.material_listview)).invalidate();
    }
}
