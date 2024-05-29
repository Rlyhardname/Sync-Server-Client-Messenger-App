package client.utils;

import client.models.Config;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.io.*;

public class FileTransfer {
    public static String pickDirectory() {
        JFileChooser fileChooserj = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        fileChooserj.setFileSelectionMode(JFileChooser.FILES_ONLY);
        File file = fileChooserj.getSelectedFile();
        fileChooserj.addChoosableFileFilter(new FileNameExtensionFilter("jpg", "jpg"));
        fileChooserj.addChoosableFileFilter(new FileNameExtensionFilter("gif", "gif"));
        fileChooserj.addChoosableFileFilter(new FileNameExtensionFilter("txt", "txt"));
        fileChooserj.setAcceptAllFileFilterUsed(true);
        String path = "";
        String name = "";
        int r = fileChooserj.showSaveDialog(null);
        if (r == JFileChooser.APPROVE_OPTION) {
            path = fileChooserj.getSelectedFile().getAbsolutePath();
            name = fileChooserj.getName();
        }

        return path+","+name;
    }

    public static void receiveFile(String fileName, Config connection) {
        int bytes = 0;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(fileName);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            //	long size = inputFile.readLong(); // read file size
            byte[] buffer = new byte[4 * 1024];
            while ((bytes = connection.getFileInput().read(buffer)) != -1) {
                // write the file using write method
                System.out.println(" Buffer " + buffer + " bytes " + bytes);
                fileOutputStream.write(buffer, 0, bytes);
                if (bytes < 4096) {
                    System.out.println(" Buffer " + buffer + " bytes " + bytes);
                    fileOutputStream.write(buffer, 0, bytes);
                    break;
                }
            }
            // TODO log file received
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileOutputStream.flush();
                connection.getFileOutput().flush();
                fileOutputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

    }

    public static void sendFile(String path, Config connection) {
        int bytes = 0;
        // Open the File where he located in your pc
        File file = new File(path);
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // send the File
        try {
            connection.getFileOutput().writeLong(file.length());
            // break file into chunks
            byte[] buffer = new byte[4 * 1024];
            while ((bytes = fileInputStream.read(buffer)) != -1) {
                // Send the file to Server Socket
                connection.getFileOutput().write(buffer, 0, bytes);
                connection.getFileOutput().flush();
                if(bytes==0){
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
