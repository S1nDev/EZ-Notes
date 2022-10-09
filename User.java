import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class User 
{
    // Private instance variables for User data and I/O processing
    private static File user, files;
    private PrintWriter outFile;
    private Scanner inFile;
    private String username, password, nickname, birthday;
    private int credits;
    private static boolean isInit = false; 

    // Create a full-defined User and temporarlily save their information to instance variables to check for errors
    public User(String username, String password, String nickname, String birthday)
    {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.birthday = birthday;
        credits = 0;
    }

    // Create a partially-defined User to save a username and password to be paired with a nickname and birthday
    public User(String username, String password)
    {
        this.username = username;
        this.password = password;
    }

    /**
     * The user save file is as follows line by line 
     * for each registered account:
     * 
     * 1. Username
     * 2. Password
     * 3. Nickname
     * 4. Birthday
     * 5. Credits
     * 
     */

    // Save User information to its own text file
    public void initUser() throws IOException
    {
        // Create file in "userdata" for account information
        user = new File("userdata/" + username + ".txt");

        outFile = new PrintWriter(user);

        // Save login information
        outFile.println(username);
        outFile.println(password);
        outFile.println(nickname);
        outFile.println(birthday);
        outFile.println(credits);
        
        // Save the default purchase information for the user's garden
        if(!(isInit))
        {
            outFile.print("\n");

            // Initialize purchase information
            outFile.println("0");
            outFile.println("false");
            outFile.println("0");
            outFile.println("false");
            outFile.println("false");
            outFile.println("0");
            outFile.println("false");
            outFile.println("0");
            outFile.println("false");
            outFile.println("false");
        }
        else
        {
            outFile.print("\n");

            // Initialize purchase information
            outFile.println(PurchaseTracker.stats.get(0));
            outFile.println(PurchaseTracker.stats.get(1));
            outFile.println(PurchaseTracker.stats.get(2));
            outFile.println(PurchaseTracker.stats.get(3));
            outFile.println(PurchaseTracker.stats.get(4));
            outFile.println(PurchaseTracker.stats.get(5));
            outFile.println(PurchaseTracker.stats.get(6));
            outFile.println(PurchaseTracker.stats.get(7));
            outFile.println(PurchaseTracker.stats.get(8));
            outFile.println(PurchaseTracker.stats.get(9));
        }
        
        outFile.close();

        // Create a directory in "userfiles" for a user's notes files and copy README.txt as an instructions manual for the user
        if(!(isInit))
        {
            files = new File("userfiles/" + username + "/");
            files.mkdirs();

            File readme = new File("README.txt");
            inFile = new Scanner(readme);

            File welcome = new File("userfiles/" + username + "/Welcome.txt");
            outFile = new PrintWriter(welcome);

            while(inFile.hasNext())
            {
                outFile.println(inFile.nextLine());
            }

            inFile.close();
            outFile.close();
        }

        isInit = true;
    }

    // Get User's username from variable before initUser() is called (2), or from text file after initUser() is called (1)
    public String getUsername(int option) throws IOException
    {
        if(option == 1)
        {
            inFile = new Scanner(user);

            // Read line 1
            String findUsername = inFile.nextLine();

            inFile.close();

            return findUsername;
        }
        else if(option == 2)
        {
            return username;
        }
        else
        {
            System.out.println("(!) Invalid option inputted");
            return null;
        }
    }

    // Get User's password from variable before initUser() is called (2), or from text file after initUser() is called (1)
    public String getPassword(int option) throws IOException
    {
        if(option == 1)
        {
            inFile = new Scanner(user);

            String findPassword = "";

            // Read line 2
            for(int i = 0; i < 2; i++)
            {
                findPassword = inFile.nextLine();
            }

            inFile.close();

            return findPassword;
        }
        else if(option == 2)
        {
            return password;
        }
        else
        {
            System.out.println("(!) Invalid option inputted");
            return null;
        }
    }

    // Get User's nickname from variable before initUser() is called (2), or from text file after initUser() is called (1)
    public String getNickname(int option) throws IOException
    {
        if(option == 1)
        {
            inFile = new Scanner(user);

            String findNickname = "";

            // Read line 3
            for(int i = 0; i < 3; i++)
            {
                findNickname = inFile.nextLine();
            }

            inFile.close();

            return findNickname;
        }
        else if(option == 2)
        {
            return nickname;
        }
        else 
        {
            System.out.println("(!) Invalid option inputted");
            return null;
        }
    }

    // Get User's birthday from variable before initUser() is called (2), or from text file after initUser() is called (1)
    public String getBirthday(int option) throws IOException
    {
        if(option == 1)
        {
            inFile = new Scanner(user);

            String findBirthday = "";

            // Read line 4
            for(int i = 0; i < 4; i++)
            {
                findBirthday = inFile.nextLine();
            }
            
            inFile.close();

            return findBirthday;
        }
        if(option == 2)
        {
            return birthday;
        }
        else
        {
            System.out.println("(!) Invalid option inputted");
            return null;
        }
    }

    // Get User's credits from variable before initUser() is called (2), or from text file after initUser() is called (1)
    public int getCredits(int option) throws IOException
    {
        if(option == 1)
        {
            inFile = new Scanner(user);

            String findCredits = "";

            // Read line 5
            for(int i = 0; i < 5; i++)
            {
                findCredits = inFile.nextLine(); 
            }

            inFile.close();

            return Integer.parseInt(findCredits);
        }
        else if(option == 2)
        {
            return credits;
        }
        else
        {
            System.out.println("(!) Invalid option inputted");
            return 0;
        }
    }

    // Set the number of credits in a User's text file
    public void setCredits(int newCredits) throws IOException
    {
        credits = newCredits + 0;
        this.initUser();
    }

    // Set the User reference file to help in getting elements from this particular file
    public void setUserFile(File newFile)
    {
        user = newFile;
    }

    // Check if username and/or password is available
    public boolean areOpen() throws IOException
    {
        File dir = new File("userdata/");
        File[] dirFiles = dir.listFiles();

        boolean areOpen = true;

        for(File file : dirFiles)
        {
            inFile = new Scanner(file);
            
            String fileUsername = inFile.nextLine();
            String filePassword = inFile.nextLine();

            if(fileUsername.equals(username) || filePassword.equals(password))
            {
                areOpen = false;
                break;
            }
        }

        return areOpen;
    }

    // Check if username and password corresponds to a user
    public boolean exists() throws IOException
    {
        File dir = new File("userdata/");
        File[] dirFiles = dir.listFiles();

        boolean exists = false;

        for(File file : dirFiles)
        {
            inFile = new Scanner(file);
            
            String fileUsername = inFile.nextLine();
            String filePassword = inFile.nextLine();

            if(fileUsername.equals(username) && filePassword.equals(password))
            {
                exists = true;
            }
        }

        return exists;
    }

    // Check if birthday input was formatted correctly
    public boolean birthdayFormat()
    {
        boolean birthdayFormat = true;

        if(birthday.charAt(2) != '/' || birthday.charAt(5) != '/')
        {
            birthdayFormat = false;
        }
        else
        {
            int month = Integer.parseInt(birthday.substring(0, 2));

            if(month > 12 || month < 1)
            {
                birthdayFormat = false;
            }
            else
            {
                int day = Integer.parseInt(birthday.substring(3, 5));

                switch(month)
                {
                    case 1:
                        // January
                        if(day > 31 || day < 1)
                        {
                            birthdayFormat = false;
                        }
                    case 2:
                        // February
                        if(day > 28 || day < 1)
                        {
                            birthdayFormat = false;
                        }
                    case 3:
                        // March
                        if(day > 31 || day < 1)
                        {
                            birthdayFormat = false;
                        }
                    case 4:
                        // April
                        if(day > 30 || day < 1)
                        {
                            birthdayFormat = false;
                        }
                    case 5:
                        // May
                        if(day > 31 || day < 1)
                        {
                            birthdayFormat = false;
                        }
                    case 6:
                        // June
                        if(day > 30 || day < 1)
                        {
                            birthdayFormat = false;
                        }
                    case 7:
                        // July
                        if(day > 31 || day < 1)
                        {
                            birthdayFormat = false;
                        }
                    case 8:
                        // August
                        if(day > 31 || day < 1)
                        {
                            birthdayFormat = false;
                        }
                    case 9:
                        // September
                        if(day > 30 || day < 1)
                        {
                            birthdayFormat = false;
                        }
                    case 10:
                        // October
                        if(day > 31 || day < 1)
                        {
                            birthdayFormat = false;
                        }
                    case 11:
                        // November
                        if(day > 30 || day < 1)
                        {
                            birthdayFormat = false;
                        }
                    case 12:
                        // December
                        if(day > 31 || day < 1)
                        {
                            birthdayFormat = false;
                        }
                }
            }
        }
        
        return birthdayFormat;
    }

    // Parse the char[] input of a jPasswordField to a string
    public static String parsePassword(char[] myPassword)
    {
        String loginPassword = "";

        for(char character : myPassword)
        {
            loginPassword += character;
        }

        return loginPassword;
    }

    public String toString()
    {
        return "New user registered at " + user;
    }
}
