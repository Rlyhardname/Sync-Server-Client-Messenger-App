package client.utils;

import client.models.Config;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.util.Objects;

public class FileTransfer {
    public static String pickDirectory() {
        JFileChooser fileChooserJ = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        fileChooserJ.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooserJ.addChoosableFileFilter(new FileNameExtensionFilter("jpg", "jpg"));
        fileChooserJ.addChoosableFileFilter(new FileNameExtensionFilter("gif", "gif"));
        fileChooserJ.addChoosableFileFilter(new FileNameExtensionFilter("txt", "txt"));
        fileChooserJ.setAcceptAllFileFilterUsed(true);
        String path = "";
        String name = "";
        int r = fileChooserJ.showSaveDialog(null);
        if (r == JFileChooser.APPROVE_OPTION) {
            path = fileChooserJ.getSelectedFile().getAbsolutePath();
            name = fileChooserJ.getName();
        }

        return path + "," + name;
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
                // TODO Refactor class, something isn't right here.
                fileOutputStream.flush();
                connection.getFileOutput().flush();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
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
                if (bytes == 0) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (Objects.nonNull(fileInputStream)) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
