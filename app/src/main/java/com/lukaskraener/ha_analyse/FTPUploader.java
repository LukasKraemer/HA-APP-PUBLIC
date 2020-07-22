package com.lukaskraener.ha_analyse;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;;
import org.apache.commons.net.ftp.*;


public class FTPUploader {
    private FTPClient ftp;
    private String server;
    private String user;
    private String pass;
    private int port;


    public FTPUploader(String host, String user, String pass, int Port){
        this.server = host;
        this.user = user;
        this.pass = pass;
        this.port = Port;
    }

    public boolean connect(){

        this.ftp = new FTPClient();
        try {
            this.ftp.connect(this.server, this.port);
            //showServerReply(ftpClient);
            int replyCode = this.ftp.getReplyCode();

            boolean success = this.ftp.login(this.user, this.pass);
            //showServerReply(ftpClient);
            if (!success) {
                //
                return success;
            } else {
                //
            }
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public void uploadFile(String localFileFullName, String fileName, String hostDir)
            throws Exception {
        try(InputStream input = new FileInputStream(new File(localFileFullName))){
            this.ftp.storeFile(hostDir + fileName, input);
        }
    }

    public void disconnect() throws IOException {
        if (this.ftp.isConnected()) {
            this.ftp.logout();
            this.ftp.disconnect();

        }
    }


}