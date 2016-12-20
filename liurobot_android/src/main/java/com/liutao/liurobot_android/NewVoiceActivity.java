package com.liutao.liurobot_android;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.liutao.liurobot_android.util.NowTime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewVoiceActivity extends Activity {
	Button back;
	Button save;
	EditText edt_name;
	EditText edt_question;
	EditText edt_response;
	private static String filename = "robotconfig.xml";
	private static File xmlPath = new File(Environment.getExternalStorageDirectory(), filename);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_voice);
		back = (Button) findViewById(R.id.button1);
		save = (Button) findViewById(R.id.button2);
		edt_question = (EditText) findViewById(R.id.editText3);
		edt_response = (EditText) findViewById(R.id.editText4);
		edt_name = (EditText) findViewById(R.id.editText2);

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(NewVoiceActivity.this, VoiceControlActivity.class));
			}
		});
		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (edt_name.getText().toString().length() != 0) {
					if (edt_question.getText().toString().length() != 0) {
						if (edt_response.getText().toString().length() != 0) {
							// ***保存语音xml
							DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
							dbf.setIgnoringElementContentWhitespace(false);
							try {
								DocumentBuilder db = dbf.newDocumentBuilder();
								Document parse = db.parse(xmlPath);
								NodeList list = parse.getElementsByTagName("voicelibrary");
								Element root = (Element) list.item(0);
								Element nodevoice = parse.createElement("voice");
								nodevoice.setAttribute("name", edt_name.getText().toString());
								nodevoice.setAttribute("id",new NowTime().getCurrentTime());
								//nodevoice.setAttribute("isemploy", "no");
								Element nodeQ = parse.createElement("question");
								nodeQ.setTextContent(edt_question.getText().toString());
								Element nodeR = parse.createElement("response");
								nodeR.setTextContent(edt_response.getText().toString());
								nodevoice.appendChild(nodeQ);
								nodevoice.appendChild(nodeR);
								root.appendChild(nodevoice);

								TransformerFactory factory = TransformerFactory.newInstance();
								Transformer former = factory.newTransformer();
								former.transform(new DOMSource(parse), new StreamResult(new File(xmlPath.toString())));

							} catch (Exception e) {
								e.printStackTrace();
							}

							Toast.makeText(NewVoiceActivity.this, "语音保存成功！", Toast.LENGTH_SHORT).show();
							startActivity(new Intent(getApplicationContext(), VoiceControlActivity.class));
						} else {
							Toast.makeText(NewVoiceActivity.this, "回答不能为空！", Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(NewVoiceActivity.this, "问题不能为空！", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(NewVoiceActivity.this, "名称不能为空！", Toast.LENGTH_SHORT).show();
				}

			}
		});
	}

}
