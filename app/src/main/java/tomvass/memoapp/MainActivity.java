package tomvass.memoapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class MainActivity extends ActionBarActivity {

    private ListView memoList;
    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        memoList = (ListView) findViewById(R.id.memoList);
        items = new ArrayList<String>();
        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        memoList.setAdapter(itemsAdapter);
        setupListViewListener();

        Spinner spinner = (Spinner) findViewById(R.id.prioritySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.priorities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        loadMemos();
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

    private boolean loadMemos (){
        File memoFile = new File( getFilesDir(), "memos.txt");
        try{
            Scanner s = new Scanner(memoFile);
            String memo;
            while (s.hasNextLine()){
                memo = s.nextLine();
                items.add(memo);

            }
            s.close();
            itemsAdapter.notifyDataSetChanged();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    private boolean saveMemos(ArrayList<String> memos) {
        File memoFile = new File(getFilesDir(), "memos.txt");
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput("memos.txt", Context.MODE_PRIVATE);
            for (String memo : memos) {
                try {
                    memo += "\n";
                    outputStream.write(memo.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    private void setupListViewListener() {
        memoList.setOnItemLongClickListener(
            new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View item, int pos, long id) {
                showWarningMessage(pos);
                return true;
            }
        });
    }
    private void showWarningMessage(int pos){
        final int location = pos;
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Modify entry")
                .setMessage("Select Option.")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        items.remove(location);
                        itemsAdapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), "Message Deleted",
                                Toast.LENGTH_SHORT).show();
                        saveMemos(items);
                    }

                })
                .setNegativeButton("Modify", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       modifyMemo(location);
                       saveMemos(items);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    private void modifyMemo(int location){
        EditText newText = (EditText) findViewById(R.id.memoText);
        String memo = items.get(location);
        int breakPoint = memo.indexOf(" ")+1;
        memo = memo.substring(breakPoint);
        newText.setText(memo);
        items.remove(location);
        itemsAdapter.notifyDataSetChanged();
        saveMemos(items);
    }

    public void addMemo(View view) {
        EditText newText = (EditText) findViewById(R.id.memoText);
        String memo = newText.getText().toString();
        Spinner prioritySpinner = (Spinner) findViewById(R.id.prioritySpinner);
        String priority = prioritySpinner.getSelectedItem().toString();
        itemsAdapter.add(priority +": " + memo);
        newText.setText("");
        saveMemos(items);
    }
}
