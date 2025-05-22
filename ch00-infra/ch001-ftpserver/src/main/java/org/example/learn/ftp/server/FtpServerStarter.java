package org.example.learn.ftp.server;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.ClearTextPasswordEncryptor;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.example.learn.ftp.server.constant.UserPropertiesConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;

public class FtpServerStarter {

    private static final Logger logger = LoggerFactory.getLogger(FtpServerStarter.class);

    // 用户配置文件
    public static final String USERS_PROPERTIES_IN_CLASSPATH = "users.properties";

    public static void main(String[] args) throws Exception {
        FtpServerFactory ftpServerFactory = new FtpServerFactory();

        ListenerFactory listenerFactory = new ListenerFactory();
        listenerFactory.setPort(2121); // 默认21端口可能会被占用或被权限限制
        ftpServerFactory.addListener("default", listenerFactory.createListener());

        // 配置用户（使用基于 properties 文件的用户管理）
        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
        userManagerFactory.setFile(new File(FtpServerStarter.class.getClassLoader().getResource(USERS_PROPERTIES_IN_CLASSPATH).toURI()));
        userManagerFactory.setPasswordEncryptor(new ClearTextPasswordEncryptor()); // 明文密码
        ftpServerFactory.setUserManager(userManagerFactory.createUserManager());

        ensureHomeDirectoryExists();

        // 启动 FTP 服务器
        FtpServer server = ftpServerFactory.createServer();
        server.start();
    }

    public static void ensureHomeDirectoryExists() throws Exception {
        Properties props = new Properties();
        props.load(FtpServerStarter.class.getClassLoader().getResourceAsStream(USERS_PROPERTIES_IN_CLASSPATH));

        // 遍历找出所有 homedirectory，创建之
        for (String key : props.stringPropertyNames()) {
            if (key.endsWith("." + UserPropertiesConstant.KEY_HOME_DIRECTORY)) {
                String path = props.getProperty(key);
                ensureHomeDirectoryExists(path);
            }
        }
    }

    public static void ensureHomeDirectoryExists(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                logger.info("created home directory:{}", dir.getAbsolutePath());
            } else {
                logger.info("failed to create home directory:{}", dir.getAbsolutePath());
            }
        } else {
            logger.info("exists home directory:{}", dir.getAbsolutePath());
        }
    }
}
