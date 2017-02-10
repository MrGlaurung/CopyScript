package com.filemover;

import com.filemover.entities.Directory;
import com.filemover.entities.File;
import com.filemover.managers.FileMoveThread;
import com.filemover.managers.MessageBroker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;

@SpringBootApplication
public class FileMover
{
    private MessageBroker mess;

    public static void main(String[] args)
    {
        // Main entry point into the applicaton. Here I create an object of the main class
        // and start up the show. Let's do it!
        // What is SpringApplication.run? Is this something I could theoretically use instead
        // of the go function? I doubt it since there is no instantiated object and this is called
        // anyway.
        SpringApplication.run(FileMover.class, args);
        FileMover fma = new FileMover();
        // Call the GO function! Go go go!
        fma.go(args);
    }

    // Since this is the main class I will not have an elaborate constructor.
    // I don't know if this is stupid, but everything I need is placed in the
    // go function instead of the constructor. Not sure if this is the best idea.
    public FileMover() {}

    // go function. This is the actual start of the applications real job.
    private void go(String[] args)
    {
        // Setup the message broker so we can send messages to the user in whatever way we wish.
        // According to some teachers this can be handled by Java classes.
        // But what does he know... he's Belgian.
        mess = new MessageBroker();
        String[] cleanArgs;

        // First of all - find the Message level from command line.
        if(args.length > 1 && args[1].substring(0,1) == "-") mess = setDebugLevel(args[1], mess);

        mess.PrintMessage("We have now started the show.", MessageBroker.MessageLevel.HYSTERICAL);
        // First of all find all arguments and sort them.
        // Skip the first argument though.
        mess.PrintMessage("Starting to parse arguments.", MessageBroker.MessageLevel.HYSTERICAL);
        cleanArgs = ReadCmdLineArguments(Arrays.copyOfRange(args,1,args.length), mess);

        RealPathFactory realPathFactory = new RealPathFactory();

        Directory source = new Directory(realPathFactory, cleanArgs[1]);
        Directory destination = new Directory(realPathFactory, cleanArgs[2]);

        // Make sure there are no errors.
        if(source.getValidated() == false) MyError(source.getErrorMsg());
        if(destination.getValidated() == false) MyError(source.getErrorMsg());
        if(source.getPath().equals(destination.getPath())) MyError("The source and destination directory is the same.");

        mess.PrintMessage("source:  " + source.getPath(), MessageBroker.MessageLevel.DEBUG);
        mess.PrintMessage("dest:    " + destination.getPath(), MessageBroker.MessageLevel.DEBUG);

        // Fourth start spawning threads to move files and perform the moves.
        if(!performWork(source, mess)) this.MyError("Cannot find any directories in Source");

        // Sixth exit application.
        mess.PrintMessage("All done. Have a nice day.", MessageBroker.MessageLevel.HYSTERICAL);
    }

    private MessageBroker setDebugLevel(String flagArguments, MessageBroker mess)
    {
        if(flagArguments.contains("s")) mess.setMessageLevel(MessageBroker.MessageLevel.SILENT);
        if(flagArguments.contains("d")) mess.setMessageLevel(MessageBroker.MessageLevel.DEBUG);
        if(flagArguments.contains("h")) mess.setMessageLevel(MessageBroker.MessageLevel.HYSTERICAL);

        return mess;
    }

    private String[] ReadCmdLineArguments(String[] args, MessageBroker mess)
    {
        String flags = "", source = "./", dest = "../";
        mess.PrintMessage("Parsing cmd line arguments.", MessageBroker.MessageLevel.HYSTERICAL);

        for (String arg : args)
        {
            if (arg.startsWith("-"))
            {
                // Find flags (if any)
                mess.PrintMessage("Found flag: " + arg.toString(), MessageBroker.MessageLevel.DEBUG);
                flags += GetFlag(arg);
            }
            else
            {
                mess.PrintMessage("Found path: " + arg + ". source=" + source + ", dest=" + dest + "."
                        , MessageBroker.MessageLevel.DEBUG);
                if (source.equals("./")) source = arg;
                else if (dest.equals("../")) dest = arg;
                else MyError("Too many arguments.");
            }
        }

        mess.PrintMessage("CMDLine arguments: flags:" + flags + ", source: " + source + ", dest: " + dest
                , MessageBroker.MessageLevel.HYSTERICAL);
        return new String[] { flags, source, dest };
    }

    private String GetFlag(String input)
    {
        // Return value.
        String validReturnFlags = "";
        // Make sure to remove the - sign.
        input = input.substring(1);

        final Set<String> validFlags = new HashSet<String>(Arrays.asList(new String[] {"sdh"}));

        // Make sure the flag is valid, currently there are no valid flags.
        for(int i = 0; i < input.length(); i++)
        {
            if(validFlags.contains(input.substring(i,i+1))) validReturnFlags += input.substring(i,i+1);
            else MyError("Incorrect flags.");
        }

        return validReturnFlags;
    }

    private Set<File> fetchSourceContent(RealPathFactory rpf, File source, MessageBroker mess)
    {
        Set<File> returnValue = new HashSet<File>();
        java.io.File folder = new java.io.File(source.getPath().toString());
        java.io.File[] listContent = folder.listFiles();

        mess.PrintMessage("Fetching source content.", MessageBroker.MessageLevel.DEBUG);
        for( java.io.File oneContent : listContent )
        {
            mess.PrintMessage("Content found: " + oneContent, MessageBroker.MessageLevel.HYSTERICAL);
            File content = new File(rpf);
            if( content.validate(oneContent.toString()) )
            {
                returnValue.add(content);
            }
        }

        return returnValue;
    }

    private Boolean performWork(Set<File> completeContent, MessageBroker mess)
    {
        // I want to create threads to work with the files in the directory
        // it seems like the best cause of action would be to create a runnable
        // class and not change this main class to runnable.

        // Make sure we don't have more than three threads running. I was
        // actually planning on having five threads but I changed my mind
        // to make sure that I actually hit the maximum number of threads.
        int counter = 1;
        int joinCounter = 1;
        // Have to find out why I cannot do this.
        // Map<java.lang.Integer, FileMoveThread> pfm = new HashMap<Integer, FileMoveThread>();
        FileMoveThread[] pfm = new FileMoveThread[completeContent.size()+1];
        for( File oneContent : completeContent)
        {
            if(counter%3 == 1)
            {
                try
                {
                    pfm[joinCounter].t.join();
                }
                catch(Exception ex) { }

                joinCounter++;
            }

            FileMoveThread oneFileMover = new FileMoveThread(oneContent);
            oneFileMover.start();
            pfm[counter] = oneFileMover;

            counter++;
        }

        return true;
    }

    private void MyError(String errorMsg)
    {
        // Error messages.
        System.out.println("Error: " + errorMsg);
        System.out.println("TV-series copy program.\n");
        System.out.println("Specify source and destination folders. The source folder should contain the downloaded");
        System.out.println("TV-series episodes and the destination should consist of the final directories of");
        System.out.println("storage consisting of directories in the following format:\n");
        System.out.println("Series Name/Season 1/\n");
        System.out.println("Series name has to be the same as the TV-Series exactly.\n");
        System.out.println("Command arguments:\n");
        System.out.println("-s\t\t\tSilent. Turns off all output.");
        System.out.println("-d\t\t\tDebug. Debug mode.");
        System.out.println("-h\t\t\tHysterical output. Will tell you everything.");
        System.out.println("Oh, and by the way - OMG, you suck.");
        System.exit(0);
    }
}
