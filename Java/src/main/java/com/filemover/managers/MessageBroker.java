package com.filemover.managers;

/**
 * Created by al on 2015-12-23.
 */
public class MessageBroker
{
    public enum MessageLevel
    {
        SILENT,
        NORMAL,
        DEBUG,
        HYSTERICAL
    }

    private MessageLevel messageOutputLevel;

    public MessageBroker()
    {
        // Create a normal message broaker unless we set another value later.
        messageOutputLevel = MessageLevel.NORMAL;
    }

    public void setMessageLevel(MessageLevel msglvl)
    {
        messageOutputLevel = msglvl;
    }

    public void PrintMessage(String msg, MessageLevel level)
    {
        if(level.compareTo(messageOutputLevel) < 1) { System.out.println(msg); }
    }
}
