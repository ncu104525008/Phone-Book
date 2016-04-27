package com.example.sango.ghw4;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class PhoneActivity extends AppCompatActivity {
    private Button saveBtn;
    private EditText nameText;
    private EditText phoneText;
    private SQLiteDatabase db;
    private int phoneId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = this.getIntent().getExtras();
        phoneId = bundle.getInt("phoneId");
        Log.w("onCreate: phoneId=", ""+phoneId);

        db = MyDBHelper.getDatabase(PhoneActivity.this);

        saveBtn = (Button) findViewById(R.id.btnSave);
        nameText = (EditText) findViewById(R.id.textName);
        phoneText = (EditText) findViewById(R.id.textPhone);
        saveBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues cv = new ContentValues();
                cv.put(MyDBHelper.NAME_COLUMN, nameText.getText().toString());
                cv.put(MyDBHelper.PHONE_COLUMN, phoneText.getText().toString());
                cv.put(MyDBHelper.STATUS_COLUMN, 0);
                String where = MyDBHelper.KEY_ID + "=" + phoneId;

                if(phoneId == -1) {
                    db.insert(MyDBHelper.TABLE_NAME, null, cv);
                } else {
                    db.update(MyDBHelper.TABLE_NAME, cv, where, null);
                }

                Intent intent = new Intent(PhoneActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        if(phoneId != -1) {
            setTextView();
        }
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

    private PhoneCard getPhoneCard(int id) {
        String where = MyDBHelper.KEY_ID + "=" + id;
        Cursor cursor = db.query(MyDBHelper.TABLE_NAME, null, where, null, null, null, null, null);
        PhoneCard card = null;

        while (cursor.moveToNext()) {
            int pid = cursor.getInt(0);
            String name = cursor.getString(1);
            String phone = cursor.getString(2);
            int status = cursor.getInt(3);

            card = new PhoneCard(id, name, phone, status);
        }
        return card;
    }

    private void setTextView() {
        PhoneCard card = getPhoneCard(phoneId);
        nameText.setText(card.getName());
        phoneText.setText(card.getPhone());
    }
}
