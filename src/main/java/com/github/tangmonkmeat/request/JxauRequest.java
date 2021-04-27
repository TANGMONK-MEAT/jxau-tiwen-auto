package com.github.tangmonkmeat.request;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.tangmonkmeat.model.Student;
import com.github.tangmonkmeat.model.StudentGroup;
import com.github.tangmonkmeat.util.HttpUtil;

/**
 * Hello world!
 *
 */
@Component
public class JxauRequest {

	private final static Logger logger = LoggerFactory.getLogger(JxauRequest.class);

	private Integer hourOfDay;

	private Integer minute;

	private Integer second;

	private Integer period;

	private StudentGroup group;
	
	private String domainName;

	/**
	 * 喵码
	 * 
	 */
	private String m;

	private List<String> failureList = new ArrayList<String>();

	/**
	 * 初始化系统配置
	 * 
	 */
	public void init() {
		Properties pro = new Properties();
		try {
			pro.load(this.getClass().getResourceAsStream("/system.properties"));
		} catch (IOException e) {
			logger.error("system.properties 配置文件不存在");
		}

		try {
			Object object0 = pro.get("hourOfDay");
			if (object0 != null) {
				hourOfDay = Integer.valueOf(object0.toString());
			}
			Object object1 = pro.get("minute");
			if (object1 != null) {
				minute = Integer.valueOf(object1.toString());
			}
			Object object2 = pro.get("second");
			if (object2 != null) {
				second = Integer.valueOf(object2.toString());
			}
			Object object3 = pro.get("period");
			if (object3 != null) {
				period = Integer.valueOf(object3.toString()) * 60 * 60 * 1000;
//				period = Integer.valueOf(object3.toString()) * 60 * 1000;
			}
			Object object4 = pro.get("m");
			if (object4 != null) {
				m = object4.toString();
			}
			Object object5 = pro.get("domainName");
			if (object5 != null) {
				domainName = object5.toString();
			}
		} catch (Exception e) {
			throw new RuntimeException("初始化参数错误", e);
		}
	}

	/**
	 * 加载 学生的json 信息
	 * 
	 * 
	 * @param filePath json 文件的路径
	 * @return
	 */
	public boolean loadStudentGroup(String filePath) {
		File file = new File(filePath);

		if (!file.exists()) {
			throw new RuntimeException("students.json 配置文件不存在");
		}

		BufferedReader reader = null;
		InputStreamReader in = null;
		try {
			in = new InputStreamReader(new FileInputStream(file), "utf-8");
			reader = new BufferedReader(in);

			StringBuffer jsonStr = new StringBuffer();
			String line;
			while ((line = reader.readLine()) != null) {
				jsonStr.append(line);
			}
			group = JSON.parseObject(jsonStr.toString(), StudentGroup.class);
			logger.info(group.toString());
			return true;
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				logger.error("关闭 输入流失败", e);
				return false;
			}
		}
		return false;
	}
	
	/**
	 * 获取用户的Guid，此Guid 用于获取token
	 * 
	 * @param xh 学号
	 * @param password 密码，默认身份证后六位
	 * @param args 其他必要参数
	 * @return Guid
	 */
	public static String getGuid(String account,String password,String appKey) {
		final String url = "https://jxnydxxgcprod.zxhnzq.com/SSO/Check/Login";
		String params = "{\n"
				+ "    \"account\": \"" + account + "\",\n"
				+ "    \"password\": \"" + password + "\",\n"
				+ "    \"appKey\": \"jxauapi\"\n"
				+ "}";
		String res = HttpUtil.post(url, params,null);
		JSONObject parseObject = JSONObject.parseObject(res);
		JSONObject result = JSONObject.parseObject(parseObject.getString("Result"));
		if(result == null) {
			return null;
		}
		return result.getString("Guid");
	}

	/**
	 * 获取 Authorization
	 * 
	 * @return Authorization
	 */
	public String requestAuthorization(String guid) {
		final String url = "https://wlz.dev.yunwucm.com/api/Check/WebToken?guid=" + guid;
		String result = HttpUtil.get(url, null);
		if (result == null) {
			throw new RuntimeException("获取 Authorization 失败");
		}
		JSONObject parseObject = JSONObject.parseObject(result);
		String token = parseObject.getString("Message");
		if (token == null) {
			return null;
		}
		return token;
	}

	/**
	 * 随机产生36.5 到 37.3的摄氏温度
	 * 
	 * 
	 * @return [36.5, 37.3]之间的随机值
	 */
	public static float randomTemp() {
		Random random = new Random();
		float temp = (float) (365 + random.nextInt(8));
		return temp / 10;
	}

	/**
	 * 返回经纬度
	 * 
	 * @return lat 和 lon 的 Map
	 */
	public Map<String, Object> requestLatAndLng(String ip) {
		JSONObject ipObject = null;

		String url = "https://apis.map.qq.com/ws/location/v1/ip?key=RPEBZ-J2ZWW-2CIRC-OO3Q2-HH7X2-7GBEJ&sig=7817334a06d1ddb3bac9137847e65b6b&ip="
				+ ip;
		String ipJson = HttpUtil.get(url, null);
//		logger.info(ipJson);
		if (ipJson == null) {
			throw new RuntimeException("获取 lat 和 lng 失败");
		}
		ipObject = JSONObject.parseObject(ipJson);
		JSONObject result = JSONObject.parseObject(ipObject.getString("result"));
		return JSONObject.parseObject(result.getString("location"));
	}

	/**
	 * 根据 经纬度获取具体的地址
	 * 
	 * @param lat 经度
	 * @param lng 纬度
	 * @return 具体的地址 的 Map
	 *         <p>
	 *         - province 省份
	 *         </p>
	 *         <p>
	 *         - city 城市
	 *         </p>
	 *         <p>
	 *         - district 地区
	 *         </p>
	 *         <p>
	 *         - street 街道
	 *         </p>
	 */
	public Map<String, Object> requestLatAndLng(String lat, String lng) {
		String url = "https://apis.map.qq.com/ws/geocoder/v1/?coord_type=5&get_poi=0&output=json&key=D3CBZ-SN73D-T2E4Z-PRGPC-KHMMH-KPB3P&location="
				+ lat + "%2C" + lng + "";
		String addressJson = HttpUtil.get(url, null);
		if (addressJson == null) {
			throw new RuntimeException("获取 具体地址 失败");
		}
		JSONObject totalMap = JSONObject.parseObject(addressJson);
		JSONObject result = JSONObject.parseObject(totalMap.getString("result"));
		String addressResult = result.getString("address_component");
		return JSONObject.parseObject(addressResult);
	}

	/**
	 * 获取当前学年， 例如： 20201
	 * 
	 * @return 学年
	 */
	public String requestCurrentXq(String token) {
		String url = "https://wlz.dev.yunwucm.com/jxauapi/api/Roll/GetCurrentXq";
		String xpJson = HttpUtil.get2(url, "bearer " + token);
		if (xpJson == null) {
			throw new RuntimeException("获取 学年 失败");
		}
		JSONObject xpMap = JSONObject.parseObject(xpJson);
		JSONArray addressArr = JSONObject.parseArray(xpMap.getString("Result"));
		JSONObject addressMap = JSONObject.parseObject(addressArr.getString(0));
		return addressMap.getString("DqXq");
	}

	/**
	 * 获取 具体地址集合
	 * 
	 * @param ip
	 * @return address-map
	 */
	public Map<String, Object> requestAddressMap(String ip) {
		Map<String, Object> latAndLng = requestLatAndLng(ip);
		String lat = latAndLng.get("lat").toString();
		String lng = latAndLng.get("lng").toString();

		Map<String, Object> addressMap = requestLatAndLng(lat, lng);
		addressMap.put("lat", lat);
		addressMap.put("lng", lng);
		return addressMap;
	}

	/**
	 * 获取 位置ID
	 * 
	 * @param token
	 * @param stu
	 * @return positionid
	 */
	public String requestPositionId1(String token, Student stu, Map<String, Object> addressMap) {
		String registerdate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		String xq = requestCurrentXq(token);

		String lat = addressMap.get("lat").toString();
		String lon = addressMap.get("lng").toString();
		Object province = addressMap.get("province");
		Object city = addressMap.get("city");
		Object district = addressMap.get("district");
		Object street = addressMap.get("street");

		String address = province + "" + city + "" + district + "" + street;

		String url = domainName + "JxauXg/XgPosition/AddPosition";
		String body = "{\r\n" + "	\"usercode\": \"" + stu.getAccount() + "\",\r\n" + "	\"xq\": \"" + xq + "\",\r\n"
				+ "	\"typeid\": 1,\r\n" + "	\"address\": \"" + address + "\",\r\n" + "	\"province\": \"" + province
				+ "\",\r\n" + "	\"city\": \"" + city + "\",\r\n" + "	\"district\": \"" + district + "\",\r\n"
				+ "	\"street\": \"" + street + "\",\r\n" + "	\"lat\": " + lat + ",\r\n" + "	\"lng\": " + lon
				+ ",\r\n" + "	\"biztime\": \"" + registerdate + " " + hourOfDay + ":" + minute + ":" + second
				+ "\"\r\n" + "}";
		String result = HttpUtil.post2(url, body, "bearer " + token);
		if (result == null) {
			throw new RuntimeException("获取位置ID（positionid）失败");
		}
		JSONObject resultMap = JSONObject.parseObject(result);
		return resultMap.getString("Result");
	}

	public String getAddress(Map<String, Object> addressMap) {
		Object province = addressMap.get("province");
		Object city = addressMap.get("city");
		Object district = addressMap.get("district");
		Object street = addressMap.get("street");

		return province + "" + city + "" + district + "" + street;
	}

	/**
	 * 获取健康数据ID
	 * 
	 * @param token
	 * @param xh
	 * @return 没有登记体温返回null
	 */
	public Integer requestHealth(String token, String xh) {
		String registerdate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		String url = domainName + "JxauXg/XgHealthxs/GetHealth?registerdate=" + registerdate + "&xh="
				+ xh;
		String data = HttpUtil.get2(url, "bearer " + token);
		if (data == null) {
			throw new RuntimeException("获取健康数据失败");
		}
		JSONObject health = JSONObject.parseObject(data);
		String result = health.getString("Result");
		if (result == null) {
			return null;
		}
		return Integer.valueOf(JSONObject.parseObject(result).get("id").toString());
	}

	/**
	 * 体温登记
	 * 
	 * @param positionId
	 * @param address
	 * @param id
	 * @param xh
	 * @param token
	 * @return 返回true代表登记成功；否则失败
	 */
	public void addHealth(String positionId, String address, Integer id, String xh, String token) {
		String registerdate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		final String url = domainName + "JxauXg/XgHealthxs/AddHealth";
		String body;
		if (id != null) {
			body = "{\r\n" + "	\"xh\": \"" + xh + "\",\r\n" + "	\"status\": 1,\r\n" + "	\"registerdate\": \""
					+ registerdate + "\",\r\n" + "	\"bodytemperature\": " + randomTemp() + ",\r\n"
					+ "	\"bodystatus\": \"正常\",\r\n" + "	\"quarantinestate\": \"正常居家\",\r\n"
					+ "	\"xsremark\": \"\",\r\n" + "	\"positionid\": " + positionId + ",\r\n" + "	\"address\": \""
					+ address + "\",\r\n" + "	\"iscontractxinguan\": \"否\",\r\n"
					+ "	\"bodyabnormalinfo\": \"无\",\r\n" + "	\"quarantineplace\": \"无\",\r\n" + "	\"id\": " + id
					+ "\r\n" + "}";
		} else {
			body = "{\r\n" + "	\"xh\": \"" + xh + "\",\r\n" + "	\"status\": 1,\r\n" + "	\"registerdate\": \""
					+ registerdate + "\",\r\n" + "	\"bodytemperature\": " + randomTemp() + ",\r\n"
					+ "	\"bodystatus\": \"正常\",\r\n" + "	\"quarantinestate\": \"正常居家\",\r\n"
					+ "	\"xsremark\": \"\",\r\n" + "	\"positionid\": " + positionId + ",\r\n" + "	\"address\": \""
					+ address + "\",\r\n" + "	\"iscontractxinguan\": \"否\",\r\n"
					+ "	\"bodyabnormalinfo\": \"无\",\r\n" + "	\"quarantineplace\": \"无\"\r\n" + "}";
		}
		logger.info(body);
		String result = HttpUtil.post2(url, body, "bearer " + token);
		if (result == null) {
			throw new RuntimeException("体温登记失败");
		}
	}

	/**
	 * 每天的 9:00 点执行一次
	 * 
	 */
	public void timerClockIn() {
		String path;
		try {
//			path = App.class.getClassLoader().getResource("students.json").toURI().getPath();
			path = getPath();
		} catch (Exception e) {
			logger.error("", e);
			return;
		}
		// 预先执行一遍
//		exec(path);
//		miaotixing();
		// 指定的任务
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				exec(path);
				miaotixing();
				group = null;
			}
		};
		LocalDateTime now = LocalDateTime.now();
		Calendar calendar = Calendar.getInstance();
		// 每天的 9:00 点执行一次
		calendar.set(now.getYear(), now.getMonthValue() - 1, now.getDayOfMonth(), hourOfDay, minute, second);
		// 第一次执行定时任务的时间
		Date firstTime = calendar.getTime();

		// 如果第一次执行定时任务的时间 小于 当前的时间
		// 此时要在 第一次执行定时任务的时间 加一天，以便此任务在下个时间点执行,
		// 如果不加一天任务会立即执行
		if (firstTime.before(new Date())) {
			firstTime = this.addDate(firstTime, 1);
		}

		Timer timer = new Timer();
		// 安排指定的任务在指定的时间开始进行重复的固定延迟执行
		timer.schedule(timerTask, firstTime, period);
	}

	/**
	 * 瞄提醒
	 * 
	 */
	public void miaotixing() {

//		if (!failureList.isEmpty()) {
		String text = failureList.toString();
		String url = "http://miaotixing.com/trigger?id=" + m + "&text=";
		try {
			url = url + URLEncoder.encode(text, "utf-8");
			HttpUtil.get2(url, null);
			failureList.clear();
		} catch (Exception e) {
			logger.error("url: {}", url, e);
		}
//		}
	}

	public void exec(String path) {
		if (group == null || group.getStudents() == null) {
			loadStudentGroup(path);
		}
		List<Student> list = group.getStudents();
		int len = list.size();
		Student stu = null;
		for (int i = 0; i < len; i++) {
			stu = list.get(i);
			try {
				task(stu);
			} catch (Exception e) {
				logger.error("学号：{}, 体温登记失败", stu.getAccount(), e);
			}
		}
	}

	/**
	 * 任务
	 * 
	 */
	public void task(Student student) {
//		String url = "http://miaotixing.com/trigger?id=";
		String xh = student.getAccount();
//		String m1 = student.getMiaoTixingSuccess();
//		String m2 = student.getMiaoTixingFiald();
		try {
			String token = requestAuthorization(student.getGuid());

			Map<String, Object> addressMap = requestAddressMap(student.getIp());

			String positionId = requestPositionId1(token, student, addressMap);

			String address = getAddress(addressMap);

			Integer id = requestHealth(token, xh);
			if (id != null) {
				logger.info("今天已经登记体温，此次登记将覆盖上次登记，id：{}", id);
			}
			addHealth(positionId, address, id, xh, token);
			logger.info("学号：{}，体温登记成功", xh);

//			if (m1 != null && !"".equals(m1)) {
//				// 喵提醒
//				HttpUtil.get2(url + m1, null);
//			}
		} catch (Exception e) {
//			if (m2 != null && !"".equals(m2)) {
			failureList.add(xh);
//				// 喵提醒
//				HttpUtil.get2(url + m2, null);
//			}
			logger.error("学号：{}, 体温登记失败", xh, e);
		}
	}

	/**
	 * 增加或减少天数
	 * 
	 * @param date
	 * @param num
	 * @return
	 */
	public Date addDate(Date date, int num) {
		Calendar startDT = Calendar.getInstance();
		startDT.setTime(date);
		startDT.add(Calendar.DAY_OF_MONTH, num);
		return startDT.getTime();
	}
	
	/**
	 * 获取 students.json 路径
	 * 
	 * @return
	 */
	public static String getPath() {
		java.net.URL url = JxauRequest.class.getProtectionDomain().getCodeSource().getLocation();
		String path = null;
		try {
			path = java.net.URLDecoder.decode(url.getPath(), "utf-8");
		} catch (Exception e) {
			logger.error("", e);
		}
		int lastIndex = path.lastIndexOf("/") + 1;
		path = path.substring(0, lastIndex);
		return path + "students.json";
	}

//	public static void main(String[] args) {
//		JxauRequest app = new JxauRequest();
////		app.requestLatAndLng("117.40.134.106");
//		app.init();
//		app.timerClockIn();
////		try {
////			String path = App.class.getClassLoader().getResource("students.json").toURI().getPath();
////			System.out.println(app.loadStudentGroup(path));
////		} catch (URISyntaxException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
//	}
}
