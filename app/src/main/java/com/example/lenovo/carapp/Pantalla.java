package com.example.lenovo.carapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class Pantalla extends AppCompatActivity {
    Handler bluetoothIn;
    final int handlerState = 0;        				 //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();
    private ConnectedThread mConnectedThread;
    public String dataInPrint;
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static String address = null;
    public String[] colors = {" ", "Rojo", "Azul", "Amarillo", "Verde", "Otro"};
    private String[] actions = {" ", "Mover Izquierda", "Mover Derecha", "Esquivar Izquierda", "Esquivar Derecha"};
    public String[] selected = {"","","","",""};
    public int[] options = {-1,-1,-1,-1,-1};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i = 1; i < 5; i++)
                    if(options[i] == -1) {
                        Snackbar.make(view, "Debe seleccionar una acción para cada color.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        return;
                    }
                for(int i = 0; i < 5; i++) {
                    mConnectedThread.write(String.valueOf(options[i]));
                }
            }
        });

        Spinner sColor, sAction;
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, colors);
        sColor = (Spinner) findViewById(R.id.sColor1);
        sColor.setAdapter(adapter);
        setAction(sColor);
        sColor = (Spinner) findViewById(R.id.sColor2);
        sColor.setAdapter(adapter);
        setAction(sColor);
        sColor = (Spinner) findViewById(R.id.sColor3);
        sColor.setAdapter(adapter);
        setAction(sColor);
        sColor = (Spinner) findViewById(R.id.sColor4);
        sColor.setAdapter(adapter);
        setAction(sColor);
        sColor = (Spinner) findViewById(R.id.sColor5);
        sColor.setAdapter(adapter);
        setAction(sColor);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, actions);
        sAction = (Spinner) findViewById(R.id.sAction1);
        sAction.setAdapter(adapter);
        setActionOfAction(sAction);
        sAction = (Spinner) findViewById(R.id.sAction2);
        sAction.setAdapter(adapter);
        setActionOfAction(sAction);
        sAction = (Spinner) findViewById(R.id.sAction2);
        sAction.setAdapter(adapter);
        setActionOfAction(sAction);
        sAction = (Spinner) findViewById(R.id.sAction3);
        sAction.setAdapter(adapter);
        setActionOfAction(sAction);
        sAction = (Spinner) findViewById(R.id.sAction4);
        sAction.setAdapter(adapter);
        setActionOfAction(sAction);
        sAction = (Spinner) findViewById(R.id.sAction5);
        sAction.setAdapter(adapter);
        setActionOfAction(sAction);
        dataInPrint = "";
        bluetoothIn = new Handler(){
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {                                        //if message is what we want
                    String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);                                    //keep appending to string until ~
                    int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
                        dataInPrint = recDataString.substring(0, endOfLineIndex);
                        System.out.println("*********************************************************"+dataInPrint);// extract string
                        //punteo.setText(dataInPrint);
                        recDataString.delete(0, recDataString.length());                    //clear all string data
                        dataInPrint = "";
                    }
                }
            }
        };
        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();
    }

    private void setAction(Spinner spinner){
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String> (parent.getContext(), android.R.layout.simple_spinner_item, colors), aux = new ArrayAdapter<String> (parent.getContext(), android.R.layout.simple_spinner_item, colors);
                String seleccionado = parent.getSelectedItem().toString();
                String arreglo[] = null;
                if(((Spinner) parent).equals((Spinner) findViewById(R.id.sColor1))) {
                    position = 0;
                    arreglo = new String[5];
                    switch (seleccionado){
                        case "Rojo":
                            parent.setBackgroundColor(Color.RED);
                            selected[position] = "Rojo";
                            adapter = aux;
                            int i, j, k = 0;
                            boolean es = true;
                            for(i = 0; i < 6; i++){
                                seleccionado = adapter.getItem(i);
                                for(j = 0; j < 1 + position; j++)
                                    if(seleccionado.equals(selected[j])){
                                        es = !es;
                                        break;
                                    }
                                if(es){
                                    arreglo[k] = seleccionado;
                                    k++;
                                }
                                es = true;
                            }
                            adapter = new ArrayAdapter<String> (parent.getContext(), android.R.layout.simple_spinner_item, arreglo);
                            ((Spinner)findViewById(R.id.sColor1)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor2)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor3)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor4)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor5)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor2)).setBackgroundColor(Color.TRANSPARENT);
                            ((Spinner)findViewById(R.id.sColor3)).setBackgroundColor(Color.TRANSPARENT);
                            ((Spinner)findViewById(R.id.sColor4)).setBackgroundColor(Color.TRANSPARENT);
                            ((Spinner)findViewById(R.id.sColor5)).setBackgroundColor(Color.TRANSPARENT);
                            options[1] = -1;
                            options[2] = -1;
                            options[3] = -1;
                            options[4] = -1;
                            break;
                        case "Azul":
                            parent.setBackgroundColor(Color.BLUE);
                            selected[position] = "Azul";
                            adapter = aux;
                            k = 0;
                            es = true;
                            for(i = 0; i < 6; i++){
                                seleccionado = adapter.getItem(i);
                                for(j = 0; j < 1 + position; j++)
                                    if(seleccionado.equals(selected[j])){
                                        es = !es;
                                        break;
                                    }
                                if(es){
                                    arreglo[k] = seleccionado;
                                    k++;
                                }
                                es = true;
                            }
                            adapter = new ArrayAdapter<String> (parent.getContext(), android.R.layout.simple_spinner_item, arreglo);
                            ((Spinner)findViewById(R.id.sColor1)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor2)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor3)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor4)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor5)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor2)).setBackgroundColor(Color.TRANSPARENT);
                            ((Spinner)findViewById(R.id.sColor3)).setBackgroundColor(Color.TRANSPARENT);
                            ((Spinner)findViewById(R.id.sColor4)).setBackgroundColor(Color.TRANSPARENT);
                            ((Spinner)findViewById(R.id.sColor5)).setBackgroundColor(Color.TRANSPARENT);
                            options[1] = -1;
                            options[2] = -1;
                            options[3] = -1;
                            options[4] = -1;
                            break;
                        case "Amarillo":
                            parent.setBackgroundColor(Color.YELLOW);
                            selected[position] = "Amarillo";
                            adapter = aux;
                            k = 0;
                            es = true;
                            for(i = 0; i < 6; i++){
                                seleccionado = adapter.getItem(i);
                                for(j = 0; j < 1 + position; j++)
                                    if(seleccionado.equals(selected[j])){
                                        es = !es;
                                        break;
                                    }
                                if(es){
                                    arreglo[k] = seleccionado;
                                    k++;
                                }
                                es = true;
                            }
                            adapter = new ArrayAdapter<String> (parent.getContext(), android.R.layout.simple_spinner_item, arreglo);
                            ((Spinner)findViewById(R.id.sColor1)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor2)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor3)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor4)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor5)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor2)).setBackgroundColor(Color.TRANSPARENT);
                            ((Spinner)findViewById(R.id.sColor3)).setBackgroundColor(Color.TRANSPARENT);
                            ((Spinner)findViewById(R.id.sColor4)).setBackgroundColor(Color.TRANSPARENT);
                            ((Spinner)findViewById(R.id.sColor5)).setBackgroundColor(Color.TRANSPARENT);
                            options[1] = -1;
                            options[2] = -1;
                            options[3] = -1;
                            options[4] = -1;
                            break;
                        case "Verde":
                            parent.setBackgroundColor(Color.GREEN);
                            selected[position] = "Verde";
                            adapter = aux;
                            k = 0;
                            es = true;
                            for(i = 0; i < 6; i++){
                                seleccionado = adapter.getItem(i);
                                for(j = 0; j < 1 + position; j++)
                                    if(seleccionado.equals(selected[j])){
                                        es = !es;
                                        break;
                                    }
                                if(es){
                                    arreglo[k] = seleccionado;
                                    k++;
                                }
                                es = true;
                            }
                            adapter = new ArrayAdapter<String> (parent.getContext(), android.R.layout.simple_spinner_item, arreglo);
                            ((Spinner)findViewById(R.id.sColor1)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor2)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor3)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor4)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor5)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor2)).setBackgroundColor(Color.TRANSPARENT);
                            ((Spinner)findViewById(R.id.sColor3)).setBackgroundColor(Color.TRANSPARENT);
                            ((Spinner)findViewById(R.id.sColor4)).setBackgroundColor(Color.TRANSPARENT);
                            ((Spinner)findViewById(R.id.sColor5)).setBackgroundColor(Color.TRANSPARENT);
                            options[1] = -1;
                            options[2] = -1;
                            options[3] = -1;
                            options[4] = -1;
                            break;
                        case "Otro":
                            parent.setBackgroundColor(Color.LTGRAY);
                            selected[position] = "Otro";
                            adapter = aux;
                            k = 0;
                            es = true;
                            for(i = 0; i < 6; i++){
                                seleccionado = adapter.getItem(i);
                                for(j = 0; j < 1 + position; j++)
                                    if(seleccionado.equals(selected[j])){
                                        es = !es;
                                        break;
                                    }
                                if(es){
                                    arreglo[k] = seleccionado;
                                    k++;
                                }
                                es = true;
                            }
                            adapter = new ArrayAdapter<String> (parent.getContext(), android.R.layout.simple_spinner_item, arreglo);
                            ((Spinner)findViewById(R.id.sColor1)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor2)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor3)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor4)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor5)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor2)).setBackgroundColor(Color.TRANSPARENT);
                            ((Spinner)findViewById(R.id.sColor3)).setBackgroundColor(Color.TRANSPARENT);
                            ((Spinner)findViewById(R.id.sColor4)).setBackgroundColor(Color.TRANSPARENT);
                            ((Spinner)findViewById(R.id.sColor5)).setBackgroundColor(Color.TRANSPARENT);
                            options[1] = -1;
                            options[2] = -1;
                            options[3] = -1;
                            options[4] = -1;
                            break;
                        default:
                            break;
                    }
                }else if(((Spinner) parent).equals((Spinner) findViewById(R.id.sColor2))) {
                    position = 1;
                    arreglo = new String[4];
                    switch (seleccionado){
                        case "Rojo":
                            parent.setBackgroundColor(Color.RED);
                            selected[position] = "Rojo";
                            adapter = aux;
                            int i, j, k = 0;
                            boolean es = true;
                            for(i = 0; i < 6; i++){
                                seleccionado = adapter.getItem(i);
                                for(j = 0; j < 1 + position; j++)
                                    if(seleccionado.equals(selected[j])){
                                        es = !es;
                                        break;
                                    }
                                if(es){
                                    arreglo[k] = seleccionado;
                                    k++;
                                }
                                es = true;
                            }
                            adapter = new ArrayAdapter<String> (parent.getContext(), android.R.layout.simple_spinner_item, arreglo);
                            ((Spinner)findViewById(R.id.sColor2)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor3)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor4)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor5)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor3)).setBackgroundColor(Color.TRANSPARENT);
                            ((Spinner)findViewById(R.id.sColor4)).setBackgroundColor(Color.TRANSPARENT);
                            ((Spinner)findViewById(R.id.sColor5)).setBackgroundColor(Color.TRANSPARENT);
                            options[2] = -1;
                            options[3] = -1;
                            options[4] = -1;
                            break;
                        case "Azul":
                            parent.setBackgroundColor(Color.BLUE);
                            selected[position] = "Azul";
                            adapter = aux;
                            k = 0;
                            es = true;
                            for(i = 0; i < 6; i++){
                                seleccionado = adapter.getItem(i);
                                for(j = 0; j < 1 + position; j++)
                                    if(seleccionado.equals(selected[j])){
                                        es = !es;
                                        break;
                                    }
                                if(es){
                                    arreglo[k] = seleccionado;
                                    k++;
                                }
                                es = true;
                            }
                            adapter = new ArrayAdapter<String> (parent.getContext(), android.R.layout.simple_spinner_item, arreglo);
                            ((Spinner)findViewById(R.id.sColor2)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor3)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor4)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor5)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor3)).setBackgroundColor(Color.TRANSPARENT);
                            ((Spinner)findViewById(R.id.sColor4)).setBackgroundColor(Color.TRANSPARENT);
                            ((Spinner)findViewById(R.id.sColor5)).setBackgroundColor(Color.TRANSPARENT);
                            options[2] = -1;
                            options[3] = -1;
                            options[4] = -1;
                            break;
                        case "Amarillo":
                            parent.setBackgroundColor(Color.YELLOW);
                            selected[position] = "Amarillo";
                            adapter = aux;
                            k = 0;
                            es = true;
                            for(i = 0; i < 6; i++){
                                seleccionado = adapter.getItem(i);
                                for(j = 0; j < 1 + position; j++)
                                    if(seleccionado.equals(selected[j])){
                                        es = !es;
                                        break;
                                    }
                                if(es){
                                    arreglo[k] = seleccionado;
                                    k++;
                                }
                                es = true;
                            }
                            adapter = new ArrayAdapter<String> (parent.getContext(), android.R.layout.simple_spinner_item, arreglo);
                            ((Spinner)findViewById(R.id.sColor2)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor3)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor4)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor5)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor3)).setBackgroundColor(Color.TRANSPARENT);
                            ((Spinner)findViewById(R.id.sColor4)).setBackgroundColor(Color.TRANSPARENT);
                            ((Spinner)findViewById(R.id.sColor5)).setBackgroundColor(Color.TRANSPARENT);
                            options[2] = -1;
                            options[3] = -1;
                            options[4] = -1;
                            break;
                        case "Verde":
                            parent.setBackgroundColor(Color.GREEN);
                            selected[position] = "Verde";
                            adapter = aux;
                            k = 0;
                            es = true;
                            for(i = 0; i < 6; i++){
                                seleccionado = adapter.getItem(i);
                                for(j = 0; j < 1 + position; j++)
                                    if(seleccionado.equals(selected[j])){
                                        es = !es;
                                        break;
                                    }
                                if(es){
                                    arreglo[k] = seleccionado;
                                    k++;
                                }
                                es = true;
                            }
                            adapter = new ArrayAdapter<String> (parent.getContext(), android.R.layout.simple_spinner_item, arreglo);
                            ((Spinner)findViewById(R.id.sColor2)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor3)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor4)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor5)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor3)).setBackgroundColor(Color.TRANSPARENT);
                            ((Spinner)findViewById(R.id.sColor4)).setBackgroundColor(Color.TRANSPARENT);
                            ((Spinner)findViewById(R.id.sColor5)).setBackgroundColor(Color.TRANSPARENT);
                            options[2] = -1;
                            options[3] = -1;
                            options[4] = -1;
                            break;
                        case "Otro":
                            parent.setBackgroundColor(Color.LTGRAY);
                            selected[position] = "Otro";
                            adapter = aux;
                            k = 0;
                            es = true;
                            for(i = 0; i < 6; i++){
                                seleccionado = adapter.getItem(i);
                                for(j = 0; j < 1 + position; j++)
                                    if(seleccionado.equals(selected[j])){
                                        es = !es;
                                        break;
                                    }
                                if(es){
                                    arreglo[k] = seleccionado;
                                    k++;
                                }
                                es = true;
                            }
                            adapter = new ArrayAdapter<String> (parent.getContext(), android.R.layout.simple_spinner_item, arreglo);
                            ((Spinner)findViewById(R.id.sColor2)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor3)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor4)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor5)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor3)).setBackgroundColor(Color.TRANSPARENT);
                            ((Spinner)findViewById(R.id.sColor4)).setBackgroundColor(Color.TRANSPARENT);
                            ((Spinner)findViewById(R.id.sColor5)).setBackgroundColor(Color.TRANSPARENT);
                            options[2] = -1;
                            options[3] = -1;
                            options[4] = -1;
                            break;
                        default:
                            break;
                    }
                }else if(((Spinner) parent).equals((Spinner) findViewById(R.id.sColor3))) {
                    position = 2;
                    arreglo = new String[3];
                    switch (seleccionado){
                        case "Rojo":
                            parent.setBackgroundColor(Color.RED);
                            selected[position] = "Rojo";
                            adapter = aux;
                            int i, j, k = 0;
                            boolean es = true;
                            for(i = 0; i < 6; i++){
                                seleccionado = adapter.getItem(i);
                                for(j = 0; j < 1 + position; j++)
                                    if(seleccionado.equals(selected[j])){
                                        es = !es;
                                        break;
                                    }
                                if(es){
                                    arreglo[k] = seleccionado;
                                    k++;
                                }
                                es = true;
                            }
                            adapter = new ArrayAdapter<String> (parent.getContext(), android.R.layout.simple_spinner_item, arreglo);
                            ((Spinner)findViewById(R.id.sColor3)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor4)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor5)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor4)).setBackgroundColor(Color.TRANSPARENT);
                            ((Spinner)findViewById(R.id.sColor5)).setBackgroundColor(Color.TRANSPARENT);
                            options[3] = -1;
                            options[4] = -1;
                            break;
                        case "Azul":
                            parent.setBackgroundColor(Color.BLUE);
                            selected[position] = "Azul";
                            adapter = aux;
                            k = 0;
                            es = true;
                            for(i = 0; i < 6; i++){
                                seleccionado = adapter.getItem(i);
                                for(j = 0; j < 1 + position; j++)
                                    if(seleccionado.equals(selected[j])){
                                        es = !es;
                                        break;
                                    }
                                if(es){
                                    arreglo[k] = seleccionado;
                                    k++;
                                }
                                es = true;
                            }
                            adapter = new ArrayAdapter<String> (parent.getContext(), android.R.layout.simple_spinner_item, arreglo);
                            ((Spinner)findViewById(R.id.sColor3)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor4)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor5)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor4)).setBackgroundColor(Color.TRANSPARENT);
                            ((Spinner)findViewById(R.id.sColor5)).setBackgroundColor(Color.TRANSPARENT);
                            options[3] = -1;
                            options[4] = -1;
                            break;
                        case "Amarillo":
                            parent.setBackgroundColor(Color.YELLOW);
                            selected[position] = "Amarillo";
                            adapter = aux;
                            k = 0;
                            es = true;
                            for(i = 0; i < 6; i++){
                                seleccionado = adapter.getItem(i);
                                for(j = 0; j < 1 + position; j++)
                                    if(seleccionado.equals(selected[j])){
                                        es = !es;
                                        break;
                                    }
                                if(es){
                                    arreglo[k] = seleccionado;
                                    k++;
                                }
                                es = true;
                            }
                            adapter = new ArrayAdapter<String> (parent.getContext(), android.R.layout.simple_spinner_item, arreglo);
                            ((Spinner)findViewById(R.id.sColor3)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor4)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor5)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor4)).setBackgroundColor(Color.TRANSPARENT);
                            ((Spinner)findViewById(R.id.sColor5)).setBackgroundColor(Color.TRANSPARENT);
                            options[3] = -1;
                            options[4] = -1;
                            break;
                        case "Verde":
                            parent.setBackgroundColor(Color.GREEN);
                            selected[position] = "Verde";
                            adapter = aux;
                            k = 0;
                            es = true;
                            for(i = 0; i < 6; i++){
                                seleccionado = adapter.getItem(i);
                                for(j = 0; j < 1 + position; j++)
                                    if(seleccionado.equals(selected[j])){
                                        es = !es;
                                        break;
                                    }
                                if(es){
                                    arreglo[k] = seleccionado;
                                    k++;
                                }
                                es = true;
                            }
                            adapter = new ArrayAdapter<String> (parent.getContext(), android.R.layout.simple_spinner_item, arreglo);
                            ((Spinner)findViewById(R.id.sColor3)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor4)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor5)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor4)).setBackgroundColor(Color.TRANSPARENT);
                            ((Spinner)findViewById(R.id.sColor5)).setBackgroundColor(Color.TRANSPARENT);
                            options[3] = -1;
                            options[4] = -1;
                            break;
                        case "Otro":
                            parent.setBackgroundColor(Color.LTGRAY);
                            selected[position] = "Otro";
                            adapter = aux;
                            k = 0;
                            es = true;
                            for(i = 0; i < 6; i++){
                                seleccionado = adapter.getItem(i);
                                for(j = 0; j < 1 + position; j++)
                                    if(seleccionado.equals(selected[j])){
                                        es = !es;
                                        break;
                                    }
                                if(es){
                                    arreglo[k] = seleccionado;
                                    k++;
                                }
                                es = true;
                            }
                            adapter = new ArrayAdapter<String> (parent.getContext(), android.R.layout.simple_spinner_item, arreglo);
                            ((Spinner)findViewById(R.id.sColor3)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor4)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor5)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor4)).setBackgroundColor(Color.TRANSPARENT);
                            ((Spinner)findViewById(R.id.sColor5)).setBackgroundColor(Color.TRANSPARENT);
                            options[3] = -1;
                            options[4] = -1;
                            break;
                        default:
                            break;
                    }
                }else if(((Spinner) parent).equals((Spinner) findViewById(R.id.sColor4))) {
                    position = 3;
                    arreglo = new String[2];
                    switch (seleccionado){
                        case "Rojo":
                            parent.setBackgroundColor(Color.RED);
                            selected[position] = "Rojo";
                            adapter = aux;
                            int i, j, k = 0;
                            boolean es = true;
                            for(i = 0; i < 6; i++){
                                seleccionado = adapter.getItem(i);
                                for(j = 0; j < 1 + position; j++)
                                    if(seleccionado.equals(selected[j])){
                                        es = !es;
                                        break;
                                    }
                                if(es){
                                    arreglo[k] = seleccionado;
                                    k++;
                                }
                                es = true;
                            }
                            adapter = new ArrayAdapter<String> (parent.getContext(), android.R.layout.simple_spinner_item, arreglo);
                            ((Spinner)findViewById(R.id.sColor4)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor5)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor5)).setBackgroundColor(Color.TRANSPARENT);
                            options[4] = -1;
                            break;
                        case "Azul":
                            parent.setBackgroundColor(Color.BLUE);
                            selected[position] = "Azul";
                            adapter = aux;
                            k = 0;
                            es = true;
                            for(i = 0; i < 6; i++){
                                seleccionado = adapter.getItem(i);
                                for(j = 0; j < 1 + position; j++)
                                    if(seleccionado.equals(selected[j])){
                                        es = !es;
                                        break;
                                    }
                                if(es){
                                    arreglo[k] = seleccionado;
                                    k++;
                                }
                                es = true;
                            }
                            adapter = new ArrayAdapter<String> (parent.getContext(), android.R.layout.simple_spinner_item, arreglo);
                            ((Spinner)findViewById(R.id.sColor4)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor5)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor5)).setBackgroundColor(Color.TRANSPARENT);
                            options[4] = -1;
                            break;
                        case "Amarillo":
                            parent.setBackgroundColor(Color.YELLOW);
                            selected[position] = "Amarillo";
                            adapter = aux;
                            k = 0;
                            es = true;
                            for(i = 0; i < 6; i++){
                                seleccionado = adapter.getItem(i);
                                for(j = 0; j < 1 + position; j++)
                                    if(seleccionado.equals(selected[j])){
                                        es = !es;
                                        break;
                                    }
                                if(es){
                                    arreglo[k] = seleccionado;
                                    k++;
                                }
                                es = true;
                            }
                            adapter = new ArrayAdapter<String> (parent.getContext(), android.R.layout.simple_spinner_item, arreglo);
                            ((Spinner)findViewById(R.id.sColor4)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor5)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor5)).setBackgroundColor(Color.TRANSPARENT);
                            options[4] = -1;
                            break;
                        case "Verde":
                            parent.setBackgroundColor(Color.GREEN);
                            selected[position] = "Verde";
                            adapter = aux;
                            k = 0;
                            es = true;
                            for(i = 0; i < 6; i++){
                                seleccionado = adapter.getItem(i);
                                for(j = 0; j < 1 + position; j++)
                                    if(seleccionado.equals(selected[j])){
                                        es = !es;
                                        break;
                                    }
                                if(es){
                                    arreglo[k] = seleccionado;
                                    k++;
                                }
                                es = true;
                            }
                            adapter = new ArrayAdapter<String> (parent.getContext(), android.R.layout.simple_spinner_item, arreglo);
                            ((Spinner)findViewById(R.id.sColor4)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor5)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor5)).setBackgroundColor(Color.TRANSPARENT);
                            options[4] = -1;
                            break;
                        case "Otro":
                            parent.setBackgroundColor(Color.LTGRAY);
                            selected[position] = "Otro";
                            adapter = aux;
                            k = 0;
                            es = true;
                            for(i = 0; i < 6; i++){
                                seleccionado = adapter.getItem(i);
                                for(j = 0; j < 1 + position; j++)
                                    if(seleccionado.equals(selected[j])){
                                        es = !es;
                                        break;
                                    }
                                if(es){
                                    arreglo[k] = seleccionado;
                                    k++;
                                }
                                es = true;
                            }
                            adapter = new ArrayAdapter<String> (parent.getContext(), android.R.layout.simple_spinner_item, arreglo);
                            ((Spinner)findViewById(R.id.sColor4)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor5)).setAdapter(adapter);
                            ((Spinner)findViewById(R.id.sColor5)).setBackgroundColor(Color.TRANSPARENT);
                            options[4] = -1;
                            break;
                        default:
                            break;
                    }
                }else if(((Spinner) parent).equals((Spinner) findViewById(R.id.sColor5))){
                    position = 4;
                    arreglo = new String[1];
                    arreglo[0] = " ";
                    switch (seleccionado){
                        case "Rojo":
                            parent.setBackgroundColor(Color.RED);
                            selected[position] = "Rojo";
                            adapter = new ArrayAdapter<String> (parent.getContext(), android.R.layout.simple_spinner_item, arreglo);
                            ((Spinner)findViewById(R.id.sColor5)).setAdapter(adapter);
                            break;
                        case "Azul":
                            parent.setBackgroundColor(Color.BLUE);
                            selected[position] = "Azul";
                            adapter = new ArrayAdapter<String> (parent.getContext(), android.R.layout.simple_spinner_item, arreglo);
                            ((Spinner)findViewById(R.id.sColor5)).setAdapter(adapter);
                            break;
                        case "Amarillo":
                            parent.setBackgroundColor(Color.YELLOW);
                            selected[position] = "Amarillo";
                            adapter = new ArrayAdapter<String> (parent.getContext(), android.R.layout.simple_spinner_item, arreglo);
                            ((Spinner)findViewById(R.id.sColor5)).setAdapter(adapter);
                            break;
                        case "Verde":
                            parent.setBackgroundColor(Color.GREEN);
                            selected[position] = "Verde";
                            adapter = new ArrayAdapter<String> (parent.getContext(), android.R.layout.simple_spinner_item, arreglo);
                            ((Spinner)findViewById(R.id.sColor5)).setAdapter(adapter);
                            break;
                        case "Otro":
                            parent.setBackgroundColor(Color.LTGRAY);
                            selected[position] = "Otro";
                            adapter = new ArrayAdapter<String> (parent.getContext(), android.R.layout.simple_spinner_item, arreglo);
                            ((Spinner)findViewById(R.id.sColor5)).setAdapter(adapter);
                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setActionOfAction(Spinner spinner){
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int posColor = 0;
                if((Spinner)parent == (Spinner)findViewById(R.id.sAction1)){
                    switch(selected[0]){
                        case "Rojo":
                            posColor = 0;
                            options[posColor] = position - 1;
                            break;
                        case "Azul":
                            posColor = 1;
                            options[posColor] = position - 1;
                            break;
                        case "Amarillo":
                            posColor = 2;
                            options[posColor] = position - 1;
                            break;
                        case "Verde":
                            posColor = 3;
                            options[posColor] = position - 1;
                            break;
                        case "Otro":
                            posColor = 4;
                            options[posColor] = position - 1;
                            break;
                    }
                }else if((Spinner)parent == (Spinner)findViewById(R.id.sAction2)){
                    switch(selected[1]){
                        case "Rojo":
                            posColor = 0;
                            options[posColor] = position - 1;
                            break;
                        case "Azul":
                            posColor = 1;
                            options[posColor] = position - 1;
                            break;
                        case "Amarillo":
                            posColor = 2;
                            options[posColor] = position - 1;
                            break;
                        case "Verde":
                            posColor = 3;
                            options[posColor] = position - 1;
                            break;
                        case "Otro":
                            posColor = 4;
                            options[posColor] = position - 1;
                            break;
                    }
                }else if((Spinner)parent == (Spinner)findViewById(R.id.sAction3)){
                    switch(selected[2]){
                        case "Rojo":
                            posColor = 0;
                            options[posColor] = position - 1;
                            break;
                        case "Azul":
                            posColor = 1;
                            options[posColor] = position - 1;
                            break;
                        case "Amarillo":
                            posColor = 2;
                            options[posColor] = position - 1;
                            break;
                        case "Verde":
                            posColor = 3;
                            options[posColor] = position - 1;
                            break;
                        case "Otro":
                            posColor = 4;
                            options[posColor] = position - 1;
                            break;
                    }
                }else if((Spinner)parent == (Spinner)findViewById(R.id.sAction4)){
                    switch(selected[3]){
                        case "Rojo":
                            posColor = 0;
                            options[posColor] = position - 1;
                            break;
                        case "Azul":
                            posColor = 1;
                            options[posColor] = position - 1;
                            break;
                        case "Amarillo":
                            posColor = 2;
                            options[posColor] = position - 1;
                            break;
                        case "Verde":
                            posColor = 3;
                            options[posColor] = position - 1;
                            break;
                        case "Otro":
                            posColor = 4;
                            options[posColor] = position - 1;
                            break;
                    }
                }else if((Spinner)parent == (Spinner)findViewById(R.id.sAction5)){
                    switch(selected[4]){
                        case "Rojo":
                            posColor = 0;
                            options[posColor] = position - 1;
                            break;
                        case "Azul":
                            posColor = 1;
                            options[posColor] = position - 1;
                            break;
                        case "Amarillo":
                            posColor = 2;
                            options[posColor] = position - 1;
                            break;
                        case "Verde":
                            posColor = 3;
                            options[posColor] = position - 1;
                            break;
                        case "Otro":
                            posColor = 4;
                            options[posColor] = position - 1;
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pantalla, menu);
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

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getIntent();
        BluetoothDevice device;
        try {
            device = btAdapter.getRemoteDevice(address);
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try
            {
                btSocket.close();
            } catch (IOException e2)
            {
                //insert code to deal with this
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
        // mConnectedThread.write("x");
    }

    @Override
    public void onPause() {
        super.onPause();
        try
        {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this
        }
    }

    private void checkBTState() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);        	//read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }
}
