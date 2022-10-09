import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.swing.JLabel;

public class PurchaseTracker {

    public static ArrayList<String> stats = new ArrayList<String>();

    private PrintWriter outFile;
    private Scanner inFile;
    private File user;

    /**
     * The purchase list is as follows according to the ID
     * of each purchasable item (this is 
     * integrated into the preexisting user data file):
     * 
     * 1. Tree upgrades:
     *      [1] - Tree level 2 (500 c)
     *      [2] - Tree level 3 (1000 c)
     *      [3] - Tree level 4 (2500 c)
     *      [4] - Tree level 5 (5000 c)
     * 2. Buy Bushes (100 c)
     * 3. Bush upgrades:
     *      [1] - Bushes level 2 (500 c)
     *      [2] - Bushes level 3 (850 c)
     *      [3] - Bushes level 4 (1200 c)
     *      [4] - Bushes level 5 (1550 c)
     * 4. Buy Roses (1000 c)
     * 5. Buy Vegetable Plot (2000 c)
     * 6. Vegetable upgrades:
     *      [1] - Vegtable plant #1 (1000 c)
     *      [2] - Vegtable plant #2 (1500 c)
     *      [3] - Vegtable plant #3 (2500 c)
     * 7. Buy Fruit Plot (3000 c)
     * 8. Fruit upgrades:
     *      [1] - Fruit plant #1 (1500 c)
     *      [2] - Fruit plant #2 (2500 c)
     *      [3] - Fruit plant #3 (4000 c)
     *      [4] - Fruit plant #4 (6000 c)
     * 9. Build Treehouse (5000 c)
     * 10. Build Greenhouse (10000 c)
     * 
     */

    public PurchaseTracker(File user) throws IOException
    {
        this.user = user;
        
        inFile = new Scanner(user);

        // Load user purchase list into and ArrayList
        stats.add(this.checkStat(1));
        stats.add(this.checkStat(2));
        stats.add(this.checkStat(3));
        stats.add(this.checkStat(4));
        stats.add(this.checkStat(5));
        stats.add(this.checkStat(6));
        stats.add(this.checkStat(7));
        stats.add(this.checkStat(8));
        stats.add(this.checkStat(9));
        stats.add(this.checkStat(10));

        inFile.close();
    }

    public void updateFile() throws IOException
    {
        outFile = new PrintWriter(user);

        // Reiterate login information
        outFile.println(FileHandler.currentUser.getUsername(2));
        outFile.println(FileHandler.currentUser.getPassword(2));
        outFile.println(FileHandler.currentUser.getNickname(2));
        outFile.println(FileHandler.currentUser.getBirthday(2));
        outFile.println(FileHandler.currentUser.getCredits(2));

        outFile.print("\n");
        // Add updated purchase information
        for(String stat : stats)
        {
            outFile.println(stat);
        }

        outFile.close();
    }

    public void purchase(int itemID, int price) throws IOException
    {
        int credits = FileHandler.currentUser.getCredits(1);
        FileHandler.currentUser.setCredits(credits - price);

        inFile = new Scanner(user);

        String findItem = "";
        int itemLine = itemID + 6;

        // Read the line specified by the item ID
        for(int i = 0; i < itemLine; i++)
        {
            findItem = inFile.nextLine();
        }

        // Check the data type of the findItem String
        if(findItem.equals("false"))
        {
            // Found stat is a boolean
            stats.remove(itemID-1);
            stats.add(itemID-1, "true");
        }
        else
        {
            // Found stat is an int
            int item = Integer.parseInt(findItem);
            item++;
            findItem = Integer.toString(item);

            stats.remove(itemID-1);
            stats.add(itemID-1, findItem);
        }

        inFile.close();

        // Load configured stats into the user file
        this.updateFile();
    }

    public String checkStat(int itemID) throws IOException
    {
        inFile = new Scanner(user);

        String findItem = "";
        int itemLine = itemID + 6;

        // Read the line specified by the item ID
        for(int i = 0; i < itemLine; i++)
        {
            findItem = inFile.nextLine();
        }

        inFile.close();

        return findItem;
    }

    public void updateCounters(JLabel counter1, JLabel counter2)
    {
        // Continuously update credits counters
        Runnable creditsCounter = new Runnable() 
        {
            public void run()
            {
                while(FileHandler.currentUser != null)
                {
                    try{
                        counter1.setText("" + FileHandler.currentUser.getCredits(1));
                        counter2.setText("" + FileHandler.currentUser.getCredits(1));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (NoSuchElementException e2) {
                        // Wait for 1 second to not exhaust the getCredits() method
                        long startTime = System.currentTimeMillis();
                        long elapsedTime = 0;
                        int elapsedSeconds = 0;

                        while(elapsedSeconds < 1)
                        {
                            elapsedTime = System.currentTimeMillis() - startTime;
                            elapsedSeconds = (int)elapsedTime / 1000;
                        }
                    }
                }

                Thread.currentThread().interrupt();
            }
        };

        new Thread(creditsCounter).start();
    }
}
