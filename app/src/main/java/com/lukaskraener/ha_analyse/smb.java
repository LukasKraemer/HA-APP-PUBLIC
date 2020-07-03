package com.lukaskraener.ha_analyse;

import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;

import java.io.IOException;

public class smb {
public static String test(String user, String pass, String ip) throws IOException {
    SMBClient client = new SMBClient();
    try (Connection connection = client.connect(ip)) {
        AuthenticationContext ac = new AuthenticationContext(user, pass.toCharArray(), "");
        Session session = connection.authenticate(ac);
        // Connect to Share
        try (DiskShare share = (DiskShare) session.connectShare("shared")) {
            for (FileIdBothDirectoryInformation f : share.list("FOLDER", "*.TXT")) {
                System.out.println("File : " + f.getFileName());
            }return "geht"; }}
catch (Exception e){
        return e.getMessage().toString();
    }}}
