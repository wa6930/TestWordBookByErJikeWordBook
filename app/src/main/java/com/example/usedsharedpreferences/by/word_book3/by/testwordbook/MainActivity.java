package com.example.usedsharedpreferences.by.word_book3.by.testwordbook;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usedsharedpreferences.by.word_book3.by.testwordbook.Item.DBwordStorage;
import com.example.usedsharedpreferences.by.word_book3.by.testwordbook.Item.MyContentObserver;
import com.example.usedsharedpreferences.by.word_book3.by.testwordbook.Item.Word;
import com.example.usedsharedpreferences.by.word_book3.by.testwordbook.Item.WordSearchAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final static String AUTHORITY = "com.wrodbook.erjikeWordBook";
    public final static Uri WORD_CONTENT = Uri.parse("content://" + AUTHORITY + "/words");
    public ContentResolver contentResolver;
    Button buttonAdd;
    Button buttonDelete;
    Button buttonCheck;
    TextView textWord;
    TextView textTrans;
    RecyclerView recyclerView;
    private ArrayList<Word> wordDetailArrayList=new ArrayList<Word>();
    private  String TAG="ErJike's TestWordBook";
    WordSearchAdapter  wordSearchAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contentResolver = getContentResolver();//activity中可以直接获得contentResolver
        //contentResolver.registerContentObserver(WORD_CONTENT,true,new MyContentObserver(handler));
        buttonAdd = (Button) findViewById(R.id.button_add);
        buttonDelete = (Button) findViewById(R.id.button_delete);
        buttonCheck = (Button) findViewById(R.id.button_check);
        textWord=(TextView)findViewById(R.id.edit_word);
        textTrans=(TextView)findViewById(R.id.edit_trans);
        recyclerView=(RecyclerView)findViewById(R.id.recycler_words);

        /*
         *
         * 不存在时添加，存在即修改
         * */
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
               // Log.i(TAG, "onClick: "+contentResolver.getType(WORD_CONTENT));
                if(!textWord.getText().toString().equals("")){
                    ContentValues contentValues=new ContentValues();
                    contentValues.put("word",textWord.getText().toString());
                    if(!textTrans.getText().toString().equals("")){
                        contentValues.put("trans",textTrans.getText().toString());

                    }
                    else {
                        contentValues.put("trans","Test程序未填写翻译");
                    }
                    contentResolver.insert(WORD_CONTENT,contentValues);

                }
                else{
                    Toast.makeText(MainActivity.this,"至少填写word吧",Toast.LENGTH_LONG).show();
                }



            }
        });
        /*
         *
         *
         * 删除全部数据库
         *
         * */
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contentResolver.delete(WORD_CONTENT,DBwordStorage.TABLE_LIST_1,new String[]{textWord.getText().toString()});

            }
        });
        /***
         * 不输入全查，输入模糊查询
         * **/
        buttonCheck.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                Cursor cursor=contentResolver.query(WORD_CONTENT,null,null,null,null,null);
                Log.i(TAG, "onClick: buttonCheck:成功点击");
                if(cursor!=null){
                    Log.i(TAG, "onClick: buttonCheck:cursor不为空");
                    wordDetailArrayList.clear();
                    while (cursor.moveToNext()) {
                        String wordName = cursor.getString(cursor.getColumnIndex(DBwordStorage.TABLE_LIST_1));
                        String wordtranslation=cursor.getString(cursor.getColumnIndex(DBwordStorage.TABLE_LIST_2));

                        if (!wordDetailArrayList.contains(new Word(wordName,wordtranslation))) {
                            wordDetailArrayList.add(new Word(wordName,wordtranslation));
                            Log.i(TAG, "query word name:" + wordName+",wordtranslation:" + wordtranslation);
                        }
                    }
                    //Toast.makeText(context, "本地存储内容为空，请尝试添加单词！", Toast.LENGTH_LONG).show();

                    cursor.close();
                    final RecyclerView recyclerView=(RecyclerView)findViewById(R.id.recycler_words);//获得对应的recyclerView
                    wordSearchAdapter=new WordSearchAdapter(wordDetailArrayList, MainActivity.this);
                    GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 1);//左右显示一个fragment
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(wordSearchAdapter);
                    wordSearchAdapter.notifyDataSetChanged();
                }

            }
        });


    }
}
