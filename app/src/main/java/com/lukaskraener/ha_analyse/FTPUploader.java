package com.lukaskraener.ha_analyse;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;;
import org.apache.commons.net.ftp.*;


public class FTPUploader {
    FTPClient ftp = null;
    String server = null;
    String user = null;
    String pass = null;
    int port = 21;


    public FTPUploader(String host, String user, String pass, int... Port){
        this.server = host;
        this.user = user;
        this.pass = pass;
        this.port = port;
    }

    public  void connect(){

        this.ftp = new FTPClient();
        try {
            this.ftp.connect(this.server, this.port);
            //showServerReply(ftpClient);
            int replyCode = this.ftp.getReplyCode();

            boolean success = this.ftp.login(this.user, this.pass);
            //showServerReply(ftpClient);
            if (!success) {
                //
                return;
            } else {
                //
            }
        } catch (IOException ex) {
            ex.printStackTrace();
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

    private static void showServerReply(FTPClient ftpClient) {
        System.out.println("1");
        String[] replies = ftpClient.getReplyStrings();
        System.out.println("2");
        if (replies != null && replies.length > 0) {
            System.out.println("2a");
            for (String aReply : replies) {
                System.out.println("SERVER: " + aReply);
                System.out.println("3");
            }
        }
    }

    /*
    public static void main(String[] args) throws Exception {
        System.out.println("Start");
        FTPUploader ftpUploader = new FTPUploader(this., "ftpUser", "ftpPassword");
        //FTP server path is relative. So if FTP account HOME directory is "/home/pankaj/public_html/" and you need to upload
        // files to "/home/pankaj/public_html/wp-content/uploads/image2/", you should pass directory parameter as "/wp-content/uploads/image2/"
        ftpUploader.uploadFile("D:\\Pankaj\\images\\MyImage.png", "image.png", "/wp-content/uploads/image2/");
        ftpUploader.disconnect();
        System.out.println("Done");
    }*/




}