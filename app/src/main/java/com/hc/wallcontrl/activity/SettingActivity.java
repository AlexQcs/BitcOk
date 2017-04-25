package com.hc.wallcontrl.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.hc.wallcontrl.R;
import com.hc.wallcontrl.bean.ClsSerializableSetPram;
import com.hc.wallcontrl.view.MyTable;


public class SettingActivity extends Activity {

    SharedPreferences sp = null;

    EditText eT_IP,eT_Port;
    Spinner spRs,spCs;
    CheckBox cb_Connected;
    Button btnConfirm,btnSetTable;
    String strIP, strPort;
    int Rs,Cs;
    boolean bConnected;

    MyTable table;
    LinearLayout llPutLable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.hc.wallcontrl.R.layout.activity_setting);

        GetPrams();
        InitForm();
    }

    private void GetPrams()
    {
        Intent myIntent = this.getIntent();
        ClsSerializableSetPram setPram = (ClsSerializableSetPram)myIntent.getSerializableExtra("Set");
        strIP = setPram.getIP();
        strPort = setPram.getPort();
        Rs = setPram.getRs();
        Cs = setPram.getCs();
        bConnected = setPram.getConn();
    }

    private void InitForm()
    {

        Context ctx = SettingActivity.this;
        sp = ctx.getSharedPreferences("SP", Context.MODE_PRIVATE);

        eT_IP = (EditText)findViewById(com.hc.wallcontrl.R.id.eT_IP);
        eT_Port  = (EditText)findViewById(com.hc.wallcontrl.R.id.eT_Port);
        spRs = (Spinner)findViewById(com.hc.wallcontrl.R.id.spinnerRs);
        spCs = (Spinner)findViewById(com.hc.wallcontrl.R.id.spinnerCs);
        cb_Connected  = (CheckBox)findViewById(com.hc.wallcontrl.R.id.cB_Connect);
        btnConfirm = (Button)findViewById(com.hc.wallcontrl.R.id.btnSetConfirm);
        btnSetTable= (Button) findViewById(R.id.btnSetTable);
        llPutLable= (LinearLayout) findViewById(R.id.llPutLables);
        btnConfirm.setOnClickListener(new MyClickListener());
        btnSetTable.setOnClickListener(new MyClickListener());

        eT_IP.setText(strIP);
        eT_Port.setText(strPort);
        cb_Connected.setChecked(bConnected);

        ArrayAdapter adapterRsum = ArrayAdapter.createFromResource(this, com.hc.wallcontrl.R.array.ListArray,android.R.layout.simple_spinner_item);
        spRs.setAdapter(adapterRsum);
        spRs.setOnItemSelectedListener(new MySpinnerSeltListener());
        spRs.setVisibility(View.VISIBLE);
        spRs.setSelection(Rs-1);

        ArrayAdapter adapterCsum = ArrayAdapter.createFromResource(this, com.hc.wallcontrl.R.array.ListArray,android.R.layout.simple_spinner_item);
        spCs.setAdapter(adapterCsum);
        spCs.setOnItemSelectedListener(new MySpinnerSeltListener());
        spCs.setVisibility(View.VISIBLE);
        spCs.setSelection(Cs - 1);
    }

    void setTable(){


        GetRowColumns();
        llPutLable.removeAllViews();
        table=new MyTable(SettingActivity.this,llPutLable.getWidth(),llPutLable.getHeight());
        Log.e("SetTableHeight:", String.valueOf(llPutLable.getHeight()));

        table.mySetRows(Rs);
        table.mySetCols(Cs);

        table.LoadTable();
        llPutLable.addView(table);
    }

    private void GetRowColumns() {
        Rs = spRs.getSelectedItemPosition()+1;
        Cs = spCs.getSelectedItemPosition()+1;
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("Rs",Rs);
        editor.putInt("Cs",Cs);
        editor.commit();
    }

    private class MySpinnerSeltListener implements AdapterView.OnItemSelectedListener{
        public void onItemSelected(AdapterView<?>arg0,View v,int arg2,long arg3){
            TextView tv = (TextView)v;
            tv.setTextColor(getResources().getColor(com.hc.wallcontrl.R.color.white));
            tv.setGravity(Gravity.CENTER);
        }
        public void onNothingSelected(AdapterView<?> arg0){

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == com.hc.wallcontrl.R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //设置返回表单
    void setConfirm(){
        strIP = eT_IP.getText().toString().trim();
        strPort = eT_Port.getText().toString().trim();
        Rs = spRs.getSelectedItemPosition()+1;
        Cs = spCs.getSelectedItemPosition()+1;
        bConnected = cb_Connected.isChecked();

        SharedPreferences.Editor editor = sp.edit();
        editor.putString("IP", strIP);
        editor.putString("Port", strPort);
        editor.putInt("Rs", Rs);
        editor.putInt("Cs", Cs);
        editor.putBoolean("bConn", bConnected);
        editor.commit();

        ClsSerializableSetPram setPram = new ClsSerializableSetPram();
        setPram.setConn(bConnected);
        setPram.setIP(strIP);
        setPram.setPort(strPort);
        setPram.setRs(Rs);
        setPram.setCs(Cs);

        Intent intent = new Intent();
        intent.setClass(SettingActivity.this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("Set",setPram);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private class MyClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            switch (v.getId()){

                case R.id.btnSetTable:
                    setTable();
                    break;

                case R.id.btnSetConfirm:
                    setConfirm();
                    break;
            }


        }
    }


}
