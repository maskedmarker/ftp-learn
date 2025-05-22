package org.example.learn.ftp.server;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.ClearTextPasswordEncryptor;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class FtpServerStarter {

    public static void main(String[] args) throws Exception {

        FtpServerFactory serverFactory = new FtpServerFactory();

        ListenerFactory factory = new ListenerFactory();
        factory.setPort(2121); // 默认21端口可能会被占用或被权限限制

        // 替换默认监听器
        serverFactory.addListener("default", factory.createListener());

        // 配置用户（使用基于 properties 文件的用户管理）
        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
        userManagerFactory.setFile(new File("users.properties")); // 用户配置文件
        userManagerFactory.setPasswordEncryptor(new ClearTextPasswordEncryptor()); // 明文密码
        serverFactory.setUserManager(userManagerFactory.createUserManager());

        ensureHomeDirectoryExists();

        // 启动 FTP 服务器
        FtpServer server = serverFactory.createServer();
        server.start();

        ensureHomeDirectoryExists("/tmp/ftpuser");
    }

    public static void ensureHomeDirectoryExists() throws Exception {
        // 加载 properties 文件
        Properties props = new Properties();
        props.load(new FileInputStream("resources/users.properties"));

        // 遍历找出所有 homedirectory，创建之
        for (String key : props.stringPropertyNames()) {
            if (key.endsWith(".homedirectory")) {
                String path = props.getProperty(key);
                ensureHomeDirectoryExists(path);
            }
        }
    }

    public static void ensureHomeDirectoryExists(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                System.out.println("Created home directory: " + path);
            } else {
                System.err.println("Failed to create home directory: " + path);
            }
        }
    }
}
