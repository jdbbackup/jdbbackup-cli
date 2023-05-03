![Maven Central](https://img.shields.io/maven-central/v/com.fathzer/jdbbackup-cli)
![License](https://img.shields.io/badge/license-Apache%202.0-brightgreen.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=jdbbackup_jdbbackup-cli&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=jdbbackup_jdbbackup-cli)
[![javadoc](https://javadoc.io/badge2/com.fathzer/jdbbackup-cli/javadoc.svg)](https://javadoc.io/doc/com.fathzer/jdbbackup-cli)

# jdbbackup-cli
A ready to command line to backup data sources based on [jdbbackup-core](https://github.com/jdbbackup/jdbbackup-core).

## How to use it
This application requires Java11+.

The [artifact deployed in Maven central](https://repo1.maven.org/maven2/com/fathzer/jdbbackup-fakesource/1.0.0/jdbbackup-fakesource-1.0.0.jar) is a runnable jar.  
Launch it with ```java -jar jdbbackup-fakesource-1.0.0.jar -h``` to see available options.

This jar manages only mySQL data source and file backup destination. You can add more destinations (S3, SFTP, etc...) using the -e option.  
Please have a look at [jdbbackup-core](https://github.com/jdbbackup/jdbbackup-core) to have the exact list or known how to develop your own extensions.

Please note that MySQL data source requires mysqldump command to be installed on the machine running this application.

Here is an example to save a MySQL database to an sftp server and a local file in *backup* folder:  
```java -jar jdbbackup-fakesource-1.0.0.jar -e=extensions/jdbbackup-sftp-1.0.0.jar mysql://root:pwd@host:port/database sftp://user:pwd@u300/jdbbackup/backupfile file:\\backup\db```.  
The *jdbbackup-sftp-1.0.0.jar* jar file should be in the *extension* folder. Please have a look at [jdbbackup-core](https://github.com/jdbbackup/jdbbackup-core) to learn how source and destinations uri are built.

If you want to include this application in a Java program, the main class is *com.fathzer.jdbbackup.cmd.JDbBackupCmd*.
