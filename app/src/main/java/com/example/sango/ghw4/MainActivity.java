package com.example.sango.ghw4;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private SQLiteDatabase db;
    private List<PhoneCard> phones;
    private String[] list;
    private ArrayAdapter<String> listAdapter;

    private final static String CALL = "android.intent.action.CALL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        phones = new ArrayList<>();
        db = MyDBHelper.getDatabase(MainActivity.this);

        listView = (ListView)findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                callPhone(position);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                myListAlertDialog(position);
                return true;
            }
        });

        showPhones();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PhoneActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("phoneId", -1);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showPhones() {
        phones.clear();
        String where = MyDBHelper.STATUS_COLUMN + "=" + 0;
        Cursor cursor = db.query(MyDBHelper.TABLE_NAME, null, where, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String phone = cursor.getString(2);
            int status = cursor.getInt(3);

            PhoneCard card = new PhoneCard(id, name, phone, status);
            phones.add(card);
        }

        list = getCardList();
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(listAdapter);
    }

    private String[] getCardList() {
        int size = phones.size();
        String[] cardList;
        cardList = new String[size];
        for(int i=0;i<size;i++) {
            cardList[i] = phones.get(i).getName();
        }

        return cardList;
    }

    private void callPhone(int id) {
        PhoneCard phone = phones.get(id);
        String phoneNumber = phone.getPhone();
        Log.w("callPhone: ", phoneNumber);
        Intent call = new Intent(CALL, Uri.parse("tel:" + phoneNumber));
        startActivity(call);
    }

    private void myListAlertDialog(final int id) {
        final String[] ListStr = { "撥打電話", "修改電話", "刪除電話"};
        AlertDialog.Builder MyListAlertDialog = new AlertDialog.Builder(this);
        MyListAlertDialog.setTitle("選擇動作");
        // 建立List的事件
        DialogInterface.OnClickListener ListClick = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        callPhone(which);
                        break;
                    case 1:
                        PhoneCard card = getPhoneCard(phones.get(id).getId());
                        Intent intent = new Intent(MainActivity.this, PhoneActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("phoneId", card.getId());
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                    case 2:
                        delPhoneCard(phones.get(id).getId());
                        break;
                }
            }
        };
        // 建立按下取消什麼事情都不做的事件
        DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        };
        MyListAlertDialog.setItems(ListStr, ListClick);
        MyListAlertDialog.setNeutralButton("取消", OkClick);
        MyListAlertDialog.show();
    }

    private PhoneCard getPhoneCard(int id) {
        Log.w("getPhoneCard: id = ",  ""+id);
        String where = MyDBHelper.KEY_ID + "=" + id;
        Cursor cursor = db.query(MyDBHelper.TABLE_NAME, null, where, null, null, null, null, null);
        PhoneCard card = null;

        while (cursor.moveToNext()) {
            int pid = cursor.getInt(0);
            String name = cursor.getString(1);
            String phone = cursor.getString(2);
            int status = cursor.getInt(3);

            card = new PhoneCard(pid, name, phone, status);
        }
        return card;
    }

    private void delPhoneCard(int id) {
        ContentValues cv = new ContentValues();

        cv.put(MyDBHelper.STATUS_COLUMN, -1);
        String where = MyDBHelper.KEY_ID + "=" + id;
        db.update(MyDBHelper.TABLE_NAME, cv, where, null);
        showPhones();
    }
}
