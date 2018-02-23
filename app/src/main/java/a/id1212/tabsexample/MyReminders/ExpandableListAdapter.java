package a.id1212.tabsexample.MyReminders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import a.id1212.tabsexample.R;
import a.id1212.tabsexample.ReminderPackage.ReminderStorage;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private ReminderStorage reminderStorage = ReminderStorage.getInstance();
    private final LayoutInflater inf;
    private List<String> groups;
    private HashMap<String, List<String>> children;

    public ExpandableListAdapter(List<String> groups, HashMap<String, List<String>> children, LayoutInflater inf) {
        this.groups = groups;
        this.children = children;
        this.inf = inf;
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return children.get(groups.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return children.get(groups.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            if (isLastChild) {
                Log.d("FIRST TAG", "getChildView: isLastChild is true, groupPosition:" + groupPosition + " childPosition:" + childPosition);
                convertView = inf.inflate(R.layout.delete_child, parent, false);
                Button delete = convertView.findViewById(R.id.delete_reminder_button);
                delete.setText(children.get(groups.get(groupPosition)).get(childPosition));
                delete.setOnClickListener(v -> {
                    children.remove(groups.get(groupPosition));
                    groups.remove(groupPosition);
                    reminderStorage.removeReminder(groupPosition);
                    ExpandableListAdapter.this.notifyDataSetChanged();
                });
            } else {
                Log.d("SECOND TAG", "getChildView: isLastChild is false, groupPosition:" + groupPosition + " childPosition:" + childPosition);
                convertView = inf.inflate(R.layout.list_item, parent, false);
                TextView textView = convertView.findViewById(R.id.lblListItem);
                textView.setText(getChild(groupPosition, childPosition).toString());
            }
        } else {
            Log.d("THIRD TAG", "getChildView: convertView was NOT NULL, groupPosition:" + groupPosition + " childPosition:" + childPosition);
        }
        return convertView;

         /*   return convertView;
        } else {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inf.inflate(R.layout.list_item, parent, false);
                holder = new ViewHolder();
                holder.text = convertView.findViewById(R.id.lblListItem);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.text.setText(getChild(groupPosition, childPosition).toString());
        return convertView;
        }*/
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inf.inflate(R.layout.list_group, parent, false);

            holder = new ViewHolder();
            holder.text = convertView.findViewById(R.id.lblListHeader);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.text.setText(getGroup(groupPosition).toString());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private class ViewHolder {
        TextView text;
    }
}