package de.reneruck.traincheck.models;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.provider.CardProvider;

import de.reneruck.traincheck.R;

/**
 * Created by reneruck on 16/11/2015.
 */
public class TrackerCardProvider extends CardProvider<TrackerCardProvider> {


    private String startTrackingAt;
    private String stopTrackingAt;
    private String primaryStation;
    private String secondaryStation;
    private String directionPrimary;
    private String directionSecondary;
    private boolean enabled = false;
    private CompoundButton.OnCheckedChangeListener checkChangedListener;
    private View.OnClickListener cardClickListener;

    @Override
    public void render(@NonNull View view, @NonNull Card card) {
        super.render(view, card);
        ((TextView)view.findViewById(R.id.tracking_time_start)).setText(startTrackingAt);
        ((TextView)view.findViewById(R.id.tracking_time_end)).setText(stopTrackingAt);
        ((TextView)view.findViewById(R.id.primary_station)).setText(primaryStation);
//        ((TextView)view.findViewById(R.id.secondary_station)).setText(secondaryStation);
        ((TextView)view.findViewById(R.id.primary_direction)).setText(directionPrimary);
//        ((TextView)view.findViewById(R.id.secondary_direction)).setText(directionSecondary);
        ((Switch)view.findViewById(R.id.enabled)).setChecked(enabled);
        ((Switch)view.findViewById(R.id.enabled)).setOnCheckedChangeListener(checkChangedListener);
        view.findViewById(R.id.text_side).setOnClickListener(cardClickListener);
    }

    public TrackerCardProvider setStartTime(String startTime){
        this.startTrackingAt = startTime;
        notifyDataSetChanged();
        return this;
    }

    public TrackerCardProvider setEndTime(String endTime){
        this.stopTrackingAt = endTime;
        notifyDataSetChanged();
        return this;
    }

    public TrackerCardProvider setPrimaryStation(String primaryStation){
        this.primaryStation = primaryStation;
        notifyDataSetChanged();
        return this;
    }

    public TrackerCardProvider setSecondaryStation(String secondaryStation){
        this.secondaryStation = secondaryStation;
        notifyDataSetChanged();
        return this;
    }

    public TrackerCardProvider setPrimaryDirection(String directionPrimary){
        this.directionPrimary = directionPrimary;
        notifyDataSetChanged();
        return this;
    }

    public TrackerCardProvider setSecondaryDirection(String directionSecondary){
        this.directionSecondary = directionSecondary;
        notifyDataSetChanged();
        return this;
    }

    public TrackerCardProvider setEnabled(boolean enabled){
        this.enabled = enabled;
        notifyDataSetChanged();
        return this;
    }

    public TrackerCardProvider setOnCheckChangedListener(CompoundButton.OnCheckedChangeListener listener){
        this.checkChangedListener = listener;
        notifyDataSetChanged();
        return this;
    }

    public TrackerCardProvider setCardClickListener(View.OnClickListener cardClickListener) {
        this.cardClickListener = cardClickListener;
        notifyDataSetChanged();
        return this;
    }

    @Override
    public int getLayout() {
        return R.layout.tracker_card;
    }


}
