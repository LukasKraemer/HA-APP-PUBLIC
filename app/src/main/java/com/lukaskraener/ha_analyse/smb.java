package com.lukaskraener.ha_analyse;

import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;

import java.io.IOException;

public class smb {
    static Session session;

    public static void init(String username,String passwort,String ip){
        SMBClient client = new SMBClient();


        try (Connection connection = client.connect(ip)) {
            AuthenticationContext ac = new AuthenticationContext(username, passwort.toCharArray(), ip);
            session = connection.authenticate(ac);
        }
        catch (Exception e){
            e.printStackTrace();
        }


    }




    public static int reader() throws IOException {

        // Connect to Share
        try (DiskShare share = (DiskShare) session.connectShare("SHARENAME")) {
            for (FileIdBothDirectoryInformation f : share.list("FOLDER", "*.TXT")) {
                System.out.println("File : " + f.getFileName());
            }
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }
    }



    public static int delete(){

        SMBClient client = new SMBClient();

        try (Connection connection = client.connect("SERVERNAME")) {
            AuthenticationContext ac = new AuthenticationContext("USERNAME", "PASSWORD".toCharArray(), "DOMAIN");
            Session session = connection.authenticate(ac);
            DiskShare share = (DiskShare) session.connectShare("SHARENAME"); {
            share.rm("FILE");}

            } catch (IOException e) {
                e.printStackTrace();
            }

        return 0;
    }
}
