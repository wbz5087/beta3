package com.example.wubingzhang.week9.notes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.wubingzhang.week9.R;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ACaddNote extends Activity implements OnClickListener{

	private EditText et,etName;
	Button bSave,bDelete,b3;
	TextView tv0,tvtime;
	FileOutputStream fos=null;
	FileInputStream fis=null;
	DataOutputStream dos=null;
	DataInputStream dis=null;
	String pattern,fileName,strTime,strContent;
	int numberofNotes,innerLastId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initiate();

		Bundle bundle=this.getIntent().getExtras();
		numberofNotes=bundle.getInt("intvalue");
		innerLastId=bundle.getInt("intlastid");
		pattern=bundle.getString("Pattern");
		if(pattern.equals("create")){
			tv0.setText("create");
			getTimeNow();
			//tv0.setText(String.valueOf(str.length()));
		}else if(pattern.equals("open")){
			fileName="sav"+bundle.getString("Name");
			tv0.setText(fileName);
			openNote(fileName);
		}else{
			tv0.setText(pattern);
		}


	}
	void initiate(){
		setContentView(R.layout.activity_addnote);
		et=(EditText)findViewById(R.id.editText1);
		etName=(EditText)findViewById(R.id.editText2);
		bSave=(Button)findViewById(R.id.button1);
		bSave.setOnClickListener(this);
		bDelete=(Button)findViewById(R.id.button2);
		bDelete.setOnClickListener(this);
		tv0=(TextView)findViewById(R.id.textView1);
		tvtime=(TextView)findViewById(R.id.textView3);

	}

	void add1toindex(){
		try {

			fos=this.openFileOutput("index", Context.MODE_PRIVATE);
			dos=new DataOutputStream(fos);
			if(pattern.equals("create")){
				dos.writeInt(++numberofNotes);
				dos.writeInt(++innerLastId);
			}

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
	}
	void substract1toindex(){
		try {

			fos=this.openFileOutput("index", Context.MODE_PRIVATE);
			dos=new DataOutputStream(fos);
			dos.writeInt(numberofNotes-1);
			dos.writeInt(innerLastId);
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
	}
	String openNote(String name){
		String sname="";
		try {
			tv0.setText(name);
			if(openFileInput(name)!=null){
				try {
					fis=openFileInput(name);
					dis=new DataInputStream(fis);
					int i=dis.readInt();
					char[] c = new char[i];
					for(int j=0;j<i;j++){
						c[j]=dis.readChar();
					}
					sname=String.valueOf(c);
					tv0.setText(sname);
					etName.setText(sname);
					i=dis.readInt();
					c = new char[i];
					for(int j=0;j<i;j++){
						c[j]=dis.readChar();
					}
					strContent=String.valueOf(c);
					et.setText(strContent);
					i=28;
					c = new char[i];
					for(int j=0;j<i;j++){
						c[j]=dis.readChar();
					}
					strTime=String.valueOf(c);
					tvtime.append(strTime);

				} catch (FileNotFoundException e) {
					//tv0.setText("e1"+name);

					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();//tv0.setText("e2");
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
			e1.printStackTrace();//tv0.setText("e3");
		}
		return sname;
	}
	void getTimeNow(){
		//时间字符串长度28
		SimpleDateFormat    formatter=new  SimpleDateFormat("yyyy年MM月dd日    HH:mm:ss     ");
		Date    curDate    =   new    Date(System.currentTimeMillis());//获取当前时间
		String    str    =    formatter.format(curDate);
		tvtime.setText(str);
		strTime=str;
	}
	String getNameOfNote(){
		String result;
		if(etName.length()!=0){
			result=etName.getText().toString();
		}else{
			result=getNameFromParagraph();
		}

		return result;
	}
	String getNameFromParagraph(){
		String str=et.getText().toString();
		String thename;
		if(str.length()>10){
			thename=str.substring(0, 10);
		}else if(str.length()>0){
			thename=str;
		}
		else
		{
			thename="empty";
		}
		return thename;
	}
	boolean save(String thename){
		boolean result=true;
		String str=et.getText().toString();
		String s="sav"+innerLastId;
		getTimeNow();
		try {
			if(pattern.equals("create"))
				fos=this.openFileOutput(s, Context.MODE_PRIVATE);
			else if(pattern.equals("open"))
				fos=this.openFileOutput(fileName, Context.MODE_PRIVATE);
			dos=new DataOutputStream(fos);
			dos.writeInt(thename.length());
			dos.writeChars(thename);
			dos.writeInt(str.length());
			dos.writeChars(str);
			dos.writeChars(strTime);
			add1toindex();
		} catch (FileNotFoundException e) {
			e.printStackTrace();tv0.setText("e11");result=false;
		} catch (IOException e) {
			e.printStackTrace();tv0.setText("e12");result=false;
		}
		finally{
			try {
				if(fos!=null)
					fos.close();
				if(dos!=null)
					dos.close();
			} catch (IOException e) {
				e.printStackTrace();tv0.setText("e13");result=false;
			}
		}

		return result;
	}
	void delete(){
		if(!pattern.equals("create"))
		{
			if(deleteFile(fileName)) {
				tv0.setText("delete success");
				substract1toindex();
			} else {
				tv0.setText("fail");
			}
		}

	}
	@Override
	public void onClick(View v){
		if(v==bSave){

			if(save(getNameOfNote())){
				Intent intent=getIntent();
				Bundle b=intent.getExtras();
				b.putString("page2Result", "saved");
				intent.putExtras(b);
				setResult(RESULT_OK,intent);
				this.finish();
			}
		}
		else if(v==bDelete){
			delete();
			Intent intent=getIntent();
			Bundle b=intent.getExtras();
			b.putString("page2Result", "deleted");
			intent.putExtras(b);
			setResult(RESULT_OK,intent);
			this.finish();
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_note, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
