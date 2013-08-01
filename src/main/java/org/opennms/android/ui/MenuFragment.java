package org.opennms.android.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.opennms.android.R;
import org.opennms.android.ui.alarms.AlarmsActivity;
import org.opennms.android.ui.events.EventsActivity;
import org.opennms.android.ui.nodes.NodesActivity;
import org.opennms.android.ui.outages.OutagesActivity;

public class MenuFragment extends ListFragment {

    private static final int MENU_ITEM_NODES = 0;
    private static final int MENU_ITEM_ALARMS = 1;
    private static final int MENU_ITEM_OUTAGES = 3;
    private static final int MENU_ITEM_EVENTS = 4;
    private MenuAdapter adapter;
    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.nav_drawer_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activity = getActivity();

        adapter = new MenuAdapter(activity);
        adapter.add(new MenuItem(getString(R.string.nodes), MENU_ITEM_NODES));
        adapter.add(new MenuItem(getString(R.string.alarms), MENU_ITEM_ALARMS));
        adapter.add(new MenuItem(getString(R.string.outages), MENU_ITEM_OUTAGES));
        adapter.add(new MenuItem(getString(R.string.events), MENU_ITEM_EVENTS));
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (activity instanceof BaseActivity) {
            final BaseActivity baseActivity = (BaseActivity) activity;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    baseActivity.closeDrawer();
                }
            }, 200);
        }

        Boolean finish = true;

        Intent intent;
        switch (adapter.getItem(position).id) {
            case MENU_ITEM_NODES:
                intent = new Intent(activity, NodesActivity.class);
                if (activity instanceof NodesActivity) {
                    finish = false;
                }
                break;
            case MENU_ITEM_OUTAGES:
                intent = new Intent(activity, OutagesActivity.class);
                if (activity instanceof OutagesActivity) {
                    finish = false;
                }
                break;
            case MENU_ITEM_ALARMS:
                intent = new Intent(activity, AlarmsActivity.class);
                if (activity instanceof AlarmsActivity) {
                    finish = false;
                }
                break;
            case MENU_ITEM_EVENTS:
                intent = new Intent(activity, EventsActivity.class);
                if (activity instanceof EventsActivity) {
                    finish = false;
                }
                break;
            default:
                return;
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP |
                        Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);

        if (finish) {
            activity.finish();
            activity.overridePendingTransition(0, 0);
        }
    }

    private class MenuItem {

        String title;
        int id;

        public MenuItem(String title, int id) {
            this.title = title;
            this.id = id;
        }

    }

    public class MenuAdapter extends ArrayAdapter<MenuItem> {

        public MenuAdapter(Context context) {
            super(context, 0);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            MenuItem item = getItem(position);
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(
                        getContext()).inflate(R.layout.nav_drawer_list_item, parent, false);
                holder = new ViewHolder();
                holder.attach(convertView);
                convertView.setTag(holder);
                holder.title.setText(item.title);
            }
            return convertView;
        }

    }

    class ViewHolder {

        public TextView title;

        public void attach(View v) {
            title = (TextView) v.findViewById(R.id.nav_item_title);
        }

    }

}