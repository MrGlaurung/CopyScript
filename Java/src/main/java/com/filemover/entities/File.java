package com.filemover.entities;

public class File extends Content
{
    private String setExtention(String fileName)
    {
        String[] fileParts = fileName.split(".");
        if(fileParts.length == 0) return "";
        else return fileParts[fileParts.length-1];
    }
}
