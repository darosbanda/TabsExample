package a.id2216.proxmindr.Tabs.MyReminders;



import android.content.Context;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import a.id2216.proxmindr.ReminderPackage.Configuration.ReminderConfiguration;
import a.id2216.proxmindr.R;
import a.id2216.proxmindr.ReminderPackage.Reminder;
import a.id2216.proxmindr.Listeners.ReminderListener;
import a.id2216.proxmindr.ReminderPackage.ReminderStorage;


public class MyRemindersFragment extends Fragment {



    private Context mContext;
    private ReminderStorage reminderStorage = ReminderStorage.getInstance();
    private View rootView;
    private ExpandableListView lv;
    private ArrayList<String> groups = new ArrayList<>();
    private ArrayList<List<String>> children = new ArrayList<>();
    private ReminderListener listener = new ReminderListener() {
        @Override
        public void onReminderAdded(String key, Reminder r) {
            storeReminder(key, r);
            render();
        }
        @Override
        public void onReminderRemoved(String key) {
            removeReminder(key);
            render();
        }
    };


    private void render() {
        lv.setAdapter(new ExpandableListAdapter(groups, children, LayoutInflater.from(mContext)));
    }

    private void removeReminder(String key) {
        for (int i = 0; i < groups.size(); i++) {
            List<String> currentChild = children.get(i);
            if (currentChild.get(0).equals(key)){
                children.remove(i);
                groups.remove(i);
            }
        }
    }


    private void storeReminder(String key, Reminder r) {
        Geocoder geo = new Geocoder(mContext);
        ReminderConfiguration config = r.getConfig();
        groups.add(r.getName());
        List<String> temp = new ArrayList<>();
        temp.add(key);
        temp.add(r.getMessage());
        String address = "Could not find address";
        try {
            address = geo.getFromLocation(r.getLatLng().latitude, r.getLatLng().longitude, 1).get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        temp.add(address);
        temp.add("Remind me on: " + (r.isDeparting() ? "Departure" : "Arrival"));

        if (config.isRecurring()) {
            temp.add(config.getWeek().toString());
            if (config.getInterval() != null) {
                temp.add(config.getInterval().toString());
            }
            if (config.getPeriod() != null) {
                temp.add(config.getPeriod().toString());
            }
        }
        children.add(temp);
    }

    private void getReminders() {
        Map<String, Reminder> reminderMap = reminderStorage.getReminders();

        for (Map.Entry<String, Reminder> stringReminderEntry : reminderMap.entrySet()) {
            storeReminder(stringReminderEntry.getKey(), stringReminderEntry.getValue());
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reminderStorage.subscribe(listener);
        getReminders();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.my_reminders_tab, container, false);
        return rootView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lv = view.findViewById(R.id.expListView);
        render();
        lv.setGroupIndicator(null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
