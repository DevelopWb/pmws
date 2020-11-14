package com.softconfig.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Process;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.softconfig.R;
import com.softconfig.Utils.DecryptionZipUtil;
import com.softconfig.Utils.PublicUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class PmwsConfigActivity extends Activity  {
    private Button saveButton, readButton, getImeiButton;
    private TextView showTextView;
    private EditText regEditText, imeiEditText, dayEditText, yearEditText;
    private static final String CONFIG_PATH_FOLDER = "/sdcard/.SPconfig";
    private static final String CONFIG_PATH = "/sdcard/.SPconfig/.config.xml";
    private String password = "fls94#@AB";
    /**
     * 请输入手机MAC
     */
    private EditText mMacEt;
    /**
     * 获取MAC
     */
    private Button mGetMacBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pmws_activity);
        initView();
        // 创建一个文件夹要是不存在的话
        File linceseFile = new File(CONFIG_PATH_FOLDER);
        if (!linceseFile.exists()) {
            linceseFile.mkdir();
        }


    }


    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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


    /**
     * 检查是否有效
     */
    private void checkInfo() {

        if (PublicUtils.fileIsExists(CONFIG_PATH_FOLDER + "/config.zip")) {
            DecryptionZipUtil.unzip(PmwsConfigActivity.this, CONFIG_PATH_FOLDER
                    + "/config.zip", CONFIG_PATH_FOLDER, password);// 要解压缩的文件，解压后的文件名，密码

            // 睡一会，等待解压好
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // 解压好开始执行检测
            boolean timeBoolean = true;
            File file = new File(CONFIG_PATH);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = null;
            try {
                db = dbf.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
            Document doc = null;
            try {
                doc = db.parse(file);
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Element root = doc.getDocumentElement();
            NodeList reginfos = root.getElementsByTagName("reginfo");

            Element reginfo = (Element) reginfos.item(0);

            Element reg = (Element) reginfo.getElementsByTagName("reg").item(0);
            Element imei = (Element) reginfo.getElementsByTagName("imei").item(
                    0);
            Element time = (Element) reginfo.getElementsByTagName("time").item(
                    0);

            String regString = reg.getFirstChild().getNodeValue();
            String imeiString0 = imei.getFirstChild().getNodeValue();
            String timeString = time.getFirstChild().getNodeValue();

            String imeiString = ((TelephonyManager) getApplicationContext()
                    .getSystemService(TELEPHONY_SERVICE)).getDeviceId();

            // 比较时间
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date savedTime = dateFormat.parse(timeString);


                String days = PublicUtils.getCurrentTime();
                Date nowTime = dateFormat.parse(days);

                long timeLong = savedTime.getTime() - nowTime.getTime();

                if (timeLong > 0) {
                    timeBoolean = true;
                } else {
                    timeBoolean = false;
                }


            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (!regString.equals("0000")) {
                new AlertDialog.Builder(PmwsConfigActivity.this)

                        .setTitle("验证码不正确")
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        Process
                                                .killProcess(Process
                                                        .myPid()); // 退出软件的代码

                                    }
                                }).show();
            } else if (!imeiString0.equals(imeiString)) {
                new AlertDialog.Builder(PmwsConfigActivity.this)

                        .setTitle("手机imei不正确")
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        Process
                                                .killProcess(Process
                                                        .myPid()); // 退出软件的代码

                                    }
                                }).show();
            } else if (!timeBoolean) {
                new AlertDialog.Builder(PmwsConfigActivity.this)

                        .setTitle("使用期限已到")
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        Process
                                                .killProcess(Process
                                                        .myPid()); // 退出软件的代码

                                    }
                                }).show();
            }

            // 用完就删呗
            Timer mTimer1 = new Timer();
            TimerTask mTimerTask1 = new TimerTask() {// 创建一个线程来执行run方法中的代码
                @Override
                public void run() {
                    // 要执行的代码

                    File f = new File(CONFIG_PATH_FOLDER + "/config.xml");
                    if (f.exists()) {
                        f.delete();
                    }
                }
            };
            mTimer1.schedule(mTimerTask1, 5000);

        } else {
            new AlertDialog.Builder(PmwsConfigActivity.this)

                    .setTitle("退出软件")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    Process
                                            .killProcess(Process
                                                    .myPid()); // 退出软件的代码

                                }
                            }).show();
        }
    }

    /**
     * 获取mac地址
     *
     * @return
     */
    private String getMacAddress() {
        String address = null;
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = (null == wifiManager ? null : wifiManager.getConnectionInfo());
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(), "请打开WiFi后重试", Toast.LENGTH_LONG).show();
            return address;
        }

        try {
            // 把当前机器上的访问网络接口的存入 Enumeration集合中
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface netWork = interfaces.nextElement();
                // 如果存在硬件地址并可以使用给定的当前权限访问，则返回该硬件地址（通常是 MAC）。
                byte[] by = netWork.getHardwareAddress();
                if (by == null || by.length == 0) {
                    continue;
                }
                StringBuilder builder = new StringBuilder();
                for (byte b : by) {
                    builder.append(String.format("%02X:", b));
                }
                if (builder.length() > 0) {
                    builder.deleteCharAt(builder.length() - 1);
                }
                String mac = builder.toString();
                // 从路由器上在线设备的MAC地址列表，可以印证设备Wifi的 name 是 wlan0
                if (netWork.getName().equals("wlan0")) {
                    address = mac;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return address;
    }

    // dom解析xml文件
    private void domParseXML() {
        DecryptionZipUtil.unzip(PmwsConfigActivity.this, CONFIG_PATH_FOLDER
                + "/.config.zip", CONFIG_PATH_FOLDER, password);// 要解压缩的文件，解压后的文件名，密码

        // 睡一会，等待解压好
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // 解压好开始执行检测
        File file = new File(CONFIG_PATH);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document doc = null;
        try {
            doc = db.parse(file);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (doc != null) {
            Element root = doc.getDocumentElement();
            NodeList reginfos = root.getElementsByTagName("reginfo");

            Element reginfo = (Element) reginfos.item(0);

            Element reg = (Element) reginfo.getElementsByTagName("reg").item(0);
            Element imei = (Element) reginfo.getElementsByTagName("imei").item(0);
            Element mac = (Element) reginfo.getElementsByTagName("mac").item(0);
            Element time = (Element) reginfo.getElementsByTagName("time").item(0);
            StringBuilder sb = new StringBuilder();
            if (reg != null) {
                String regString = reg.getFirstChild().getNodeValue();
                sb.append("注册码：")
                        .append(regString)
                        .append("\n");
            }
            if (imei != null) {
                String imeiString = imei.getFirstChild().getNodeValue();
                if (!TextUtils.isEmpty(imeiString)) {
                    sb.append("手机IMEI号：")
                            .append(imeiString)
                            .append("\n");
                }
            }
            if (mac != null) {
                String macString = mac.getFirstChild().getNodeValue();
                if (!TextUtils.isEmpty(macString)) {
                    sb.append("手机Mac号：")
                            .append(macString)
                            .append("\n");
                }
            }
            if (time != null) {
                String timeString = time.getFirstChild().getNodeValue();
                sb.append("使用期限到:").append(timeString);
            }
//            String imeiString = ((TelephonyManager) getApplicationContext()
//                    .getSystemService(TELEPHONY_SERVICE)).getDeviceId();
            showTextView.setText(sb.toString().trim());
        }

    }

    private void openTo() {

        if (PublicUtils.fileIsExists(CONFIG_PATH_FOLDER + "/.config.zip")) {
            domParseXML();
        } else {
            showTextView.setText("");
            Toast.makeText(this, "配置文件不存在", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 保存文件到指定的目录
     */
    private void savePreToSDcard() {

        // 7.18add 要是在软件然后配置文件目录被删除的话再创建一个 修复误删文件目录的BUG
        File linceseFile = new File(CONFIG_PATH_FOLDER);

        if (!linceseFile.exists()) {
            linceseFile.mkdir();
        }
        String reg = regEditText.getText().toString();
        String imei = imeiEditText.getText().toString();
        String mac = mMacEt.getText().toString().trim();
        // 判断输入不能为空
        if (reg.isEmpty()) {
            Toast.makeText(this, "注册码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (imei.isEmpty() && mac.isEmpty()) {
            Toast.makeText(this, "IMEI和Mac至少填写一个", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!dayEditText.getText().toString().isEmpty()) {
            if (yearEditText.getText().toString().isEmpty()) {
                // 只有天那有输入


                int day = Integer.parseInt(dayEditText.getText()
                        .toString());

                String days = PublicUtils.getTimeString(PublicUtils.getCurrentTime(), day);

                createXmlFile(reg, imei, mac, days);

                Toast.makeText(this, "生成中，请稍后", Toast.LENGTH_SHORT).show();

                regEditText.setText("");
                imeiEditText.setText("");
                mMacEt.setText("");
                dayEditText.setText("");
                yearEditText.setText("");


                // 加密压缩配置文件
                Timer mTimer1 = new Timer();
                TimerTask mTimerTask1 = new TimerTask() {//

                    @Override
                    public void run() {
                        // 要执行的代码
                        DecryptionZipUtil.zip(PmwsConfigActivity.this,
                                CONFIG_PATH, CONFIG_PATH_FOLDER
                                        + "/.config.zip", password);// 要压缩的文件，压缩后的文件名，密码

                    }
                };
                mTimer1.schedule(mTimerTask1, 1500);// 延迟1.5秒执行


                // 生成压缩文件成功后删除原文件
                Timer mTimer = new Timer();
                TimerTask mTimerTask = new TimerTask() {//

                    @Override
                    public void run() {
                        // 压缩文件的代码
                        try {

                            File f = new File(CONFIG_PATH);
                            if (f.exists()) {
                                f.delete();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                };
                mTimer.schedule(mTimerTask, 4000);
                Toast.makeText(this, "生成成功", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "天和年必须输入一个", Toast.LENGTH_SHORT)
                        .show();
                return;
            }

        } else {
            if (!yearEditText.getText().toString().isEmpty()) {
                // 只有年那输入


                int day = Integer.parseInt(yearEditText.getText()
                        .toString());

                String days = PublicUtils.getTimeString(PublicUtils.getCurrentTime(), day * 365);

                createXmlFile(reg, imei, mac, days);
                Toast.makeText(this, "生成中，请稍后", Toast.LENGTH_SHORT).show();


                regEditText.setText("");
                imeiEditText.setText("");
                dayEditText.setText("");
                yearEditText.setText("");

                // 加密压缩配置文件
                Timer mTimer1 = new Timer();
                TimerTask mTimerTask1 = new TimerTask() {//

                    @Override
                    public void run() {
                        // 要执行的代码
                        DecryptionZipUtil.zip(PmwsConfigActivity.this,
                                CONFIG_PATH, CONFIG_PATH_FOLDER
                                        + "/.config.zip", password);// 要压缩的文件，压缩后的文件名，密码
                    }
                };
                mTimer1.schedule(mTimerTask1, 1500);// 延迟3秒执行


                // 生成压缩文件成功后删除原文件
                Timer mTimer = new Timer();
                TimerTask mTimerTask = new TimerTask() {//

                    @Override
                    public void run() {
                        // 压缩文件的代码
                        try {

                            File f = new File(CONFIG_PATH);
                            if (f.exists()) {
                                f.delete();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                };
                mTimer.schedule(mTimerTask, 4000);

                Toast.makeText(this, "生成成功", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "天和年必须输入一个", Toast.LENGTH_SHORT)
                        .show();
                return;
            }

        }


    }

    private void createXmlFile(String reg, String imei, String mac, String time) {

        File linceseFile = new File(CONFIG_PATH);


        try {
            linceseFile.createNewFile();
        } catch (IOException e) {
            Log.e("IOException", "exception in createNewFile() method");
        }
        FileOutputStream fileos = null;
        try {
            fileos = new FileOutputStream(linceseFile);
        } catch (FileNotFoundException e) {
            Log.e("FileNotFoundException", "can't create FileOutputStream");
        }
        XmlSerializer serializer = Xml.newSerializer();
        try {
            serializer.setOutput(fileos, "UTF-8");
            serializer.startDocument(null, true);
            serializer.startTag(null, "reginfos");

            serializer.startTag(null, "reginfo");
            serializer.startTag(null, "reg");
            serializer.text(reg);
            serializer.endTag(null, "reg");
            if (!TextUtils.isEmpty(imei)) {
                serializer.startTag(null, "imei");
                serializer.text(imei);
                serializer.endTag(null, "imei");
            }
            if (!TextUtils.isEmpty(mac)) {
                serializer.startTag(null, "mac");
                serializer.text(mac);
                serializer.endTag(null, "mac");
            }
            serializer.startTag(null, "time");
            serializer.text(time);
            serializer.endTag(null, "time");
            serializer.endTag(null, "reginfo");

            serializer.endTag(null, "reginfos");
            serializer.endDocument();
            serializer.flush();
            fileos.close();
        } catch (Exception e) {
            Log.e("Exception", "error occurred while creating xml file");
        }
        // Toast.makeText(getApplicationContext(), "创建xml文件成功!",
        // Toast.LENGTH_SHORT).show();
    }

    private void initView() {
        mMacEt = (EditText) findViewById(R.id.mac_et);
        mGetMacBt = (Button) findViewById(R.id.get_mac_bt);
        mGetMacBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String mac = getMacAddress();
                if (!TextUtils.isEmpty(mac)) {
                    mMacEt.setText(initMac(mac));
                }
            }
        });
        saveButton = (Button) findViewById(R.id.button1);
        saveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                savePreToSDcard();
            }
        });
        readButton = (Button) findViewById(R.id.button2);
        readButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                openTo();
            }
        });
        getImeiButton = (Button) findViewById(R.id.getimei);
        getImeiButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String imeiString = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE))
                        .getDeviceId();
                imeiEditText.setText(imeiString);
            }
        });
        showTextView = (TextView) findViewById(R.id.textView1);
        regEditText = (EditText) findViewById(R.id.reg_et);
        imeiEditText = (EditText) findViewById(R.id.imei_et);
        dayEditText = (EditText) findViewById(R.id.day_et);
        yearEditText = (EditText) findViewById(R.id.year_et);
    }


    /**
     * 格式化Mac地址
     *
     * @param mac
     * @return
     */
    private String initMac(String mac) {
        if (mac.contains(":")) {
            mac = mac.replaceAll(":", "");
        }
        return mac;
    }

}
