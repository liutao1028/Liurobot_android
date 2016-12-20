package com.liutao.liurobot_android;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;

public class TestActivity extends Activity {
	private static String filename = "robotconfig.xml";
	private static File xmlPath = new File(
			Environment.getExternalStorageDirectory(), filename);
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		String robot_qusetion = null; //机器人传入的语音
		String get_mapname = null;//当前应用的地图名
		
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringElementContentWhitespace(false);//创建DOM解析实例
			DocumentBuilder db;
			try {
				db = dbf.newDocumentBuilder();
				Document parse = db.parse(xmlPath);//读取xml文件 得到文件对象
				//访问voicelibrary 语音库
				NodeList voicelibrarylist = parse.getElementsByTagName("voicelibrary");
				Element voicelibraryroot = (Element) voicelibrarylist.item(0);
				//遍历语音库 匹配语音
				NodeList voice_nodelist = voicelibraryroot.getElementsByTagName("voice");
				for (int i = 0; i < voice_nodelist.getLength(); i++) {
					Element voicenode = (Element) voice_nodelist.item(i);
					if ((voicenode.getElementsByTagName("question").item(0).getFirstChild().getNodeValue()).equals(robot_qusetion)) {
						//if匹配成功 得到xml中对应回答
						String robot_respond = voicenode.getElementsByTagName("response").item(0).getFirstChild().getNodeValue();
						
						//以下为 循环读取事件中所有需要传递的数据 
						String voicename = voicenode.getAttribute("name");//通过匹配语音 拿到对应事件
						//访问maplibrary 地图库
						NodeList map_library_list = parse.getElementsByTagName("maplibrary");
						Element maplibraryroot = (Element) map_library_list.item(0);
						NodeList map_node_list = maplibraryroot.getElementsByTagName("map");
						for (int j = 0; j < map_node_list.getLength(); j++) {
							Element map_node = (Element) map_node_list.item(j);
							if (map_node.getAttribute("name").equals(get_mapname)) {//通过当前地图名 语音出发该地图下的对应事件
								//访问该地图下事件列表
								NodeList event_node_list = map_node.getElementsByTagName("event");
								for (int k = 0; k < event_node_list.getLength(); k++) {
									Element event_node = (Element) event_node_list.item(k);
									if (event_node.getAttribute("voice").equals(voicename)) {//通过语音 得到该语音所在的事件
										NodeList content_node_list = event_node.getElementsByTagName("contentname");
										for (int l = 0; l < content_node_list.getLength(); l++) {
											//拿到事件中每个节点的内容，内容格式为   【类型:内容:延迟秒数】
											String contentValue = content_node_list.item(l).getFirstChild().getNodeValue();
											String[] split = contentValue.split(":");
											//拆分内容  通过内容开头的类型判断 进行操作
											switch (split[0]) {
											case "action"://事件中的动作类型
												String action_name = split[1];//动作项的名字
												String action_seconds = split[2];//延迟发送的秒数
												NodeList action_library_list = parse.getElementsByTagName("actionlibrary");
												Element actionlibraryroot = (Element) map_library_list.item(0);
												NodeList actions_node_list = actionlibraryroot.getElementsByTagName("actions");
												for (int m = 0; m < actions_node_list.getLength(); m++) {
													Element actions_node = (Element) actions_node_list.item(m);
													if (actions_node.getAttribute("name").equals(action_name)) {
														NodeList action_node_list = actions_node.getElementsByTagName("action");
														//循环读取一个动作项目中的每个动作
														for (int n = 0; n < action_node_list.getLength(); n++) {
															Element action_node = (Element) action_node_list.item(n);
															String action_content = action_node.getFirstChild().getNodeValue();
															String[] action_split = action_content.split(":");
															String action_content_name = action_split[0];//动作名
															String action_content_time = action_split[1];//参数-时间
															String action_content_speed = action_split[2];//参数-速度
															//*****************
															//此处应循环向下层发送指令
														}
													}
												}
												
												break;
											case "media":
												String media_name = split[1];//文件绝对路径
												String media_seconds = split[2];//延迟秒数
												//*****************
												//此处应通过路径打开文件
												
												break;
											case "coord":
												String coord_name = split[1];
												String coord_seconds = split[2];//同上
												NodeList coords_node_list = map_node.getElementsByTagName("coords");
												Element coords_node = (Element) coords_node_list.item(0);
												NodeList coord_node_list = coords_node.getElementsByTagName("coord");
												for (int n = 0; n < coord_node_list.getLength(); n++) {
													Element coord_node = (Element) coord_node_list.item(n);
													//循环得到坐标中数据
													if (coord_node.getAttribute("name").equals(coord_name)) {
														String coord_x = coord_node.getElementsByTagName("x").item(0).getFirstChild().getNodeValue();
														String coord_y = coord_node.getElementsByTagName("y").item(0).getFirstChild().getNodeValue();
														String coord_z = coord_node.getElementsByTagName("z").item(0).getFirstChild().getNodeValue();
														String coord_w = coord_node.getElementsByTagName("w").item(0).getFirstChild().getNodeValue();
														String coord_dir = coord_node.getElementsByTagName("dir").item(0).getFirstChild().getNodeValue();
														//以上为每个坐标中的值 发送至底层即可
														
													}
												}
												break;

											default:
												break;
											}
										}
										
									}
								}
							}
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
}