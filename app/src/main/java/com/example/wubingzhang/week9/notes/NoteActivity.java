package com.example.wubingzhang.week9.notes;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.view.View.OnClickListener;

import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import com.example.wubingzhang.week9.R;


public class NoteActivity extends ActionBarActivity implements  Toolbar.OnMenuItemClickListener{
	private Toolbar toolbar;

	TextView tv2,tv1;
	ListView lv1;
	FileOutputStream fos=null;
	FileInputStream fis=null;
	DataOutputStream dos=null;
	DataInputStream dis=null;

	String[] from={"name","id"};              //������ListView��ʾ����ÿһ�е�����
	int[] to={R.id.notename};   //������ListView��ʾÿһ�ж�Ӧ��list_item�пؼ���id

	ArrayList<HashMap<String,String>> list=null;
	HashMap<String,String> map=null;
	int numberofnotes,innerLastId;
	String s2;
	private SimpleAdapter adapter = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note_list);

		toolbar = (Toolbar)findViewById(R.id.toolbar);
		if (toolbar != null)
			initToolbar();

		tv1=(TextView)findViewById(R.id.textView1);
		tv2=(TextView)findViewById(R.id.textView2);
		//tv3=(TextView)findViewById(R.id.textView3);
		lv1=(ListView)findViewById(R.id.listView1);
		lv1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				@SuppressWarnings("unchecked")
				Map<String, String> map = (Map<String, String>) adapter.getItem(position);
				String name = map.get("id");
				//tv2.setText(name);
				openNote(name);
			}
		});
		try {
			if(this.openFileInput("index")==null){
				writezero();
			}
		} catch (FileNotFoundException e1) {

			e1.printStackTrace();
			writezero();
		}
		loadnum();
		loaditems();
	}

	private void initToolbar() {
		toolbar.setTitle("Note");
		toolbar.inflateMenu(R.menu.menu_note);
		toolbar.setOnMenuItemClickListener(this);
	}

	void loadnum(){
		try {
			if(openFileInput("index")!=null){

				try {
					fis=openFileInput("index");
					dis=new DataInputStream(fis);

					int i=dis.readInt();
					tv2.setText("number of note"+i);
					numberofnotes=i;

					i=dis.readInt();
					innerLastId=i;
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finally{

					try {
						if(fis!=null)
							fis.close();
						if(dis!=null)
							dis.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	void writezero(){
		try {
			fos=this.openFileOutput("index", Context.MODE_PRIVATE);
			dos=new DataOutputStream(fos);
			dos.writeInt(0);
			dos.writeInt(0);

			//dos.writeChars(str);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{

			try {
				if(fos!=null)
					fos.close();
				if(dos!=null)
					dos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		loadnum();
		loaditems();

	}
	String getName(int id){
		String sname="";
		try {
			if(this.openFileInput("sav" + id)!=null){
				try {
					fis=this.openFileInput("sav"+id);
					dis=new DataInputStream(fis);
					int i=dis.readInt();
					char[] c = new char[i+1];
					for(int j=0;j<i;j++){
						c[j]=dis.readChar();
					}
					sname=String.valueOf(c);

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				finally{
					try {
						if(fis!=null)
							fis.close();
						if(dis!=null)
							dis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		return sname;
	}
	int tryOpenNext(int j){
		try{
			openFileInput("sav"+j);
			return j;
		}
		catch(FileNotFoundException e) {
			if(j<innerLastId)
				return tryOpenNext(j+1);
			else
				return -1;
		}

	}
	void loaditems() {
		list=new ArrayList<HashMap<String,String>>();
		int j=0;
		for(int i=0; i<numberofnotes; i++){
			j=tryOpenNext(j);

			if(j!=-1){
				map=new HashMap<String,String>();
				map.put("name", getName(j));
				map.put("id", String.valueOf(j));
				list.add(map);


			}
			j++;
		}

		adapter=new SimpleAdapter(this,list,R.layout.list_item,from,to);
		lv1.setAdapter(adapter);
	}


	void createNewNote(){
		Intent intent=new Intent();
		intent.setClass(this, ACaddNote.class);

		Bundle bundle=new Bundle();
		bundle.putInt("intvalue", numberofnotes);
		bundle.putInt("intlastid", innerLastId);
		bundle.putString("Pattern", "create");
		intent.putExtras(bundle);

		//startActivity(intent);
		startActivityForResult(intent,0);
	}

	void openNote(String name){
		Intent intent=new Intent();
		intent.setClass(this, ACaddNote.class);

		Bundle bundle=new Bundle();
		bundle.putInt("intvalue", numberofnotes);
		bundle.putInt("intlastid", innerLastId);
		bundle.putString("Pattern", "open");
		bundle.putString("Name", name);
		intent.putExtras(bundle);

		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode,int resultCode ,Intent data){
		Log.d("CheckStartActivity","onActivityResult and resultCode ="+resultCode);
		super.onActivityResult(requestCode, resultCode,data);
		if(resultCode==RESULT_OK){
			Toast.makeText(this, "Pass", Toast.LENGTH_LONG).show();
			Bundle bundle2=data.getExtras();
			String tt=bundle2.getString("page2Result");

			//tv2.setText(tt);
			loadnum();
			loaditems();
		}
		else{
			Toast.makeText(this, "Fail", Toast.LENGTH_LONG).show();

		}

	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		int id = item.getItemId();
		if(id == R.id.newNote){
			createNewNote();
		}
		if(id == R.id.clear){
			writezero();
		}
		return false;
	}
}
