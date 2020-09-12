package com.imubit.loginTracker.login;

import com.imubit.loginTracker.exceptions.FatalInitException;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Use wtmp file to get last login users. The file format is machine dependent, so parsing it, would not get persistent results
 *  we will use "who /var/log/wtmp" in order to get last login users.
 * A sample output of this command:
 * navap2   pts/3        2020-09-07 22:22
 * navap3   pts/3        2020-09-07 22:23
 * navap3   pts/3        2020-09-07 22:23
 * navap2   pts/3        2020-09-07 22:23
 * navap2   pts/3        2020-09-07 22:28
 * navap2   pts/3        2020-09-07 22:35
 * navap2   pts/3        2020-09-07 22:41
 * navap2   pts/3        2020-09-07 22:42
 * navap2   pts/3        2020-09-07 22:42
 * navap2   pts/3        2020-09-07 22:42
 * navap    :0           2020-09-08 08:17 (:0)
 *
 */
@Slf4j
public class LinuxLoginListenerService{

    /**
     * place holder for the line to look for
     */
    private static final  String LAST_LINE_MARKER = "$$$";
    /**
     * Command to get all login users
     */
    private static final String WHO_COMMAND = "who /var/log/wtmp";

    /**
     * This will bringth the login users from newwest to oldest
     */
    public static final String[] WHO_REVERSE_ORDER = {"/bin/sh","-c",WHO_COMMAND +"|tac"} ;

    /**
     * Place holder for the number of times a multiple identical login row exists
     */
    private static final String NUMBER_OF_ROWS_MARKER = "n$n$n$";

    /**
     * This will bring login users since specific entry in the wtmp file
     * Explanation regarding the grep doing on the who output:
     * First we match any character (.) zero or multiple times (*) until an occurrence of the last line.
     * Now, the part inside the brackets (.*\n) matches any character except a newline (.) zero or multiple times (*)
     * followed by a newline (\n). And all that (that's inside the buckets) can occur zero or multiple times;
     * that's the meaning of the last *. It should now match all other lines, after the first occurrence of last line.
     * At the end doing tail in order to get the last line used previous time
     */
    private static final  String LAST_LOGIN_COMMAND =WHO_COMMAND + "|grep -Pzo \".*" +LAST_LINE_MARKER + "(.*\\n)*\" |tail -n  +" +
            NUMBER_OF_ROWS_MARKER;

    /**
     * The path to wtmp file
     */
    public static final String LOGIN_FILE = "/var/log/wtmp";
    /**
     * The folder of wtmp file
     */
    public static final String LOGIN_FILE_FOLDER = "/var/log";


    private static final String LOG_MESSAGE_REG = "(^\\S+)\\s.*";

    private static final Pattern logMessagePattern = Pattern.compile(LOG_MESSAGE_REG);


    /**
     * The last line read by who command
     */
    private String lastLine;

    /**
     * The time resolution in output is HH:mm
     * There might be multiple identical records ,
     * This value count them
     */
    private int numIdenticalUserRecord = 1;

    /**
     * Get the last login  in wtmp. This will be the basic for starting monitoring login users
     * Each time the wtmp change, we will get users since last recorded login user
     * the time resolution in wtmp files is "HH:mm", so there might be some identical rows
     *  since there might be that user logged in multiple time at the same time resolution
     */
    public void createWtmpFirstStatus(){
        String s;
        Process p;
        try {
            //reading current login users from the end
          //counting number of identical row

            p = Runtime.getRuntime().exec(WHO_REVERSE_ORDER);
           try( BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream()))) {
               while ((s = br.readLine()) != null) {
                   if (lastLine != null) {
                       if (!s.equals(lastLine)) {
                           break;
                       }
                   }
                   lastLine = s;
                   numIdenticalUserRecord++;
               }
           }
            p.waitFor();
            log.debug("Read all users");
            p.destroy();
        } catch (Exception e) {
            log.error("Failed to parse initial users logged into system from wtmp file",e);
            throw new FatalInitException("Failed to init the system" ,e);
        }
    }

    private String[] constructLastUsersCommand() {
        String command = WHO_COMMAND;
        if (lastLine != null) {
            command = LAST_LOGIN_COMMAND.replace(LAST_LINE_MARKER, lastLine).replace(NUMBER_OF_ROWS_MARKER, String.valueOf(numIdenticalUserRecord));
        }
        return new String[] {"/bin/sh","-c",command};
    }

    public ArrayList<String>  checkForLastLoggedUsers(){
        String[] command = constructLastUsersCommand();
        ArrayList<String> loggedUserList = new ArrayList<>();
        String s;
        Process p;
        try {
            //reading current login users from the end
            //counting number of identical row at the end
            p = Runtime.getRuntime().exec(command);
            try( BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream()))) {
                while ((s = br.readLine()) != null) {
                    if (lastLine != null) {
                        if(s.trim().isEmpty())
                            continue;
                        if (s.equals(lastLine)) {
                            numIdenticalUserRecord++;
                        } else
                            numIdenticalUserRecord = 2;
                    }
                    lastLine = s;

                    try {
                        Matcher matcher = logMessagePattern.matcher(lastLine);
                        boolean hasMatch = matcher.matches();
                        if(!hasMatch)
                            log.error("Unable to parse the following log messafge:\n" + lastLine + "\nThis line is ignored");
                        else {
                            loggedUserList.add( matcher.group(1));
                        }
                    }catch(IllegalStateException e){
                        log.error("Unable to parse the following log messafge:\n" + lastLine + "\nThis line is ignored");
                    }
                }
            }
            p.waitFor();
            log.debug("Read all users");
            p.destroy();
        } catch (Exception e) {
            log.error("Failed to parse initial users logged into system from wtmp file",e);
            throw new FatalInitException("Failed to init the system" ,e);
        }
        return loggedUserList;

    }
}
