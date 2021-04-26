package com.github.tangmonkmeat.config;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.github.tangmonkmeat.model.Student;
import com.github.tangmonkmeat.model.StudentGroup;
import com.github.tangmonkmeat.request.JxauRequest;

public class UserInfoConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    private static final Logger logger = LoggerFactory.getLogger(UserInfoConfig.class);
	
	public static String CONFIG_FILE;
	
	// 初始化配置文件路径
    static {
        try {
//            CONFIG_FILE = UserInfoConfig.class.getClassLoader().getResource("students.json").toURI().getPath();
        	CONFIG_FILE = JxauRequest.getPath();
            File file = new File(CONFIG_FILE);
            if (!file.exists()){
                file.createNewFile();
            }
        } catch (Exception e) {
           logger.error("",e);
        }
    }
	
	private StudentGroup users;
	
	private static volatile UserInfoConfig config;
	
	private UserInfoConfig() {
		update(null);
	}
	
	public static UserInfoConfig instance() {
		if(config == null) {
			synchronized (UserInfoConfig.class) {
				if(config == null) {
					config = new UserInfoConfig();
				}
			}
		}
		return config;
	}
	
	/**
	 * 更新配置
	 * 
	 * @param configJson
	 */
	public synchronized void update(String configJson) {
        File file = new File(CONFIG_FILE);
        InputStream in = null;
        try {
            // 如果json 为 null，且配置文件存在
            if (configJson == null && file.exists()) {
                in = new FileInputStream(file);
                byte[] buf = new byte[1024];
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int readIndex;
                while ((readIndex = in.read(buf)) != -1) {
                    out.write(buf, 0, readIndex);
                }

                in.close();
                configJson = new String(out.toByteArray(), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if(in != null){
                    in.close();
                }
            } catch (IOException e) {
                logger.error("close inputStream fail",e);
            }
        }
        
        users = JSON.parseObject(configJson, StudentGroup.class);
        if(users == null) {
        	users = new StudentGroup();
        }
        
        // 更新配置文件
        if (configJson != null) {
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                out.write(configJson.getBytes(StandardCharsets.UTF_8));
                out.flush();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    assert out != null;
                    out.close();
                } catch (IOException e) {
                    logger.error("close FileOutputStream fail",e);
                }
            }
        }
	}
	
	public boolean isExist(String account) {
		for(Student info : users.getStudents()) {
			if(info.getAccount().equals(account)) {
				return true;
			}
		}
		return false;
	}
	
	public void addUser(Student info) {
		users.getStudents().add(info);
	}
	
	public synchronized String addUserThenGetJson(Student info) {
		addUser(info);
		return getUsersJson();
	}

	public List<Student> getUsers() {
		return users.getStudents();
	}
	
	public String getUsersJson() {
		return JSON.toJSONString(users);
	}

	public void setUsers(List<Student> users) {
		this.users.setStudents(users);;
	}
}
