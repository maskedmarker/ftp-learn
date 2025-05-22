# apache-ftp-server

## 用户

默认用户
```text
admin
anonymous
```

自定义用户
```text
格式如下,其中name为用户名.ftpserver.user.为固定前缀
ftpserver.user.name.userpassword=123456
ftpserver.user.name.homedirectory=./path/to/dir
ftpserver.user.name.enableflag=true
ftpserver.user.name.writepermission=true
ftpserver.user.name.maxloginnumber=10
ftpserver.user.name.maxloginperip=5
ftpserver.user.name.idletime=300
```