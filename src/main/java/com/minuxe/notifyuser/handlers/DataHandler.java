package com.minuxe.notifyuser.handlers;

import java.io.*;

public class DataHandler<T> {
    String filePath;
    T data;

    public DataHandler(T data, String filePath) {
        this.data = data;
        this.filePath = filePath;
        loadFromFile();
    }

    public DataHandler(T data, String fileName, File dataFolder) {
        this(data, dataFolder + File.separator + fileName);
    }

    public DataHandler(T data, String fileName, String dirName) {
        this(data, dirName + fileName);
    }

    private boolean loadFromFile() {
        File file = new File(filePath);
        if (file.exists()) return load();
        else return save();
    }

    public boolean load() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath));
            T result = (T) ois.readObject();
            ois.close();
            data = result;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            data = null;
            return false;
        }
    }

    public boolean save() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath));
            oos.writeObject(data);
            oos.flush();
            oos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public T get() {
        return data;
    }

    public String getFilePath() {
        return this.filePath;
    }
}
