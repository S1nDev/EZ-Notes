import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.awt.Font;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

public class FileHandler {
    // Public variables for selected or open files for a user and the active mode
    public static File currentFile;
    public static User currentUser;
    public static String currentMode;

    // Private instance variables for swing components, and I/O processing
    private JTree tree;
    private JTextArea textArea;
    private JProgressBar progressBar;
    private JLabel statusMsg;
    private Scanner inFile;
    private PrintWriter outFile;

    // Private instance variables for notes writer settings
    private Font text;
    private String font;
    private int style;
    private int size;

    private static String prevFileRoot;
    private static ArrayList<File> dirsToSkip = new ArrayList<File>();

    // Create a FileHandler for the save files of a user
    public FileHandler(JTree tree, JTextArea textArea, JProgressBar progressBar, JLabel statusMsg) throws IOException {
        this.tree = tree;
        this.textArea = textArea;
        this.progressBar = progressBar;
        this.statusMsg = statusMsg;
    }

    // Load all files from the current user's account
    public void loadFiles() throws IOException {
        File fileRoot = new File("userfiles/" + currentUser.getUsername(2) + "/");
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(currentUser.getUsername(2));

        loadFromDir(fileRoot, root);

        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        this.tree.setModel(treeModel);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        tree = new JTree(root);

        ((DefaultTreeModel) tree.getModel()).reload();
    }

    // Load all files and files within directories for the current user's account
    public static void loadFromDir(File fileRoot, DefaultMutableTreeNode root) {
        File[] dirFiles = fileRoot.listFiles();

        for (File file : dirFiles) {
            DefaultMutableTreeNode item = new DefaultMutableTreeNode(file.getName());
            root.add(item);

            if (file.isDirectory()) {
                loadFromDir(file, item);
            }
        }
    }
    
    // Load a page into the textarea on the notes writer page
    public void loadPage() throws IOException {
        inFile = new Scanner(currentFile);

        String text = "";

        while (inFile.hasNext()) {
            text += inFile.nextLine() + "\n";
        }

        textArea.setText(text);

        inFile.close();
    }

    // Save a page from the contents of the textarea
    public void savePage() throws IOException {
        outFile = new PrintWriter(currentFile);

        String text = textArea.getText();
        outFile.print(text);

        outFile.close();
    }

    // Add a notebook (directory) to the parent JTree
    public void addNotebook(String notebookName) throws IOException {
        String addToPath = "";

        // Add the new directory to the root if no directory is selected
        if (currentFile == null) {
            addToPath = "userfiles/" + currentUser.getUsername(2) + "/";
        } else {
            String addToDir = currentFile.getName() + "/";
            String fileRoot = "userfiles/" + currentUser.getUsername(2) + "/";
            addToPath = findFile(addToDir, fileRoot);
        }

        System.out.println("Adding to: " + addToPath);

        File newNotebook = new File(addToPath + notebookName + "/");
        newNotebook.mkdir();

        File placeHolder = new File(addToPath + notebookName + "/PlaceHolder.txt");
        placeHolder.createNewFile();

        outFile = new PrintWriter(placeHolder);

        outFile.print("This is a placeholder document for the notebook: \n" + newNotebook.getName()
                + "\nDelete this file if needed.");

        outFile.close();

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(addToPath);
        DefaultMutableTreeNode addNotebook = new DefaultMutableTreeNode(notebookName);
        DefaultMutableTreeNode addPlaceHolder = new DefaultMutableTreeNode("Placeholder.txt");

        root.add(addNotebook);
        root.add(addPlaceHolder);
    }

    // Check if the name of this new file or directory is not taken
    public boolean isOpen(String itemName) throws IOException {
        boolean isOpen = false;

        File isOpenFile = new File(itemName);
        String fileRoot = "userfiles/" + currentUser.getUsername(2) + "/";

        String fileName;

        if (isOpenFile.isDirectory()) {
            // Searching for a similar directory name
            fileName = itemName + "/";
        } else {
            // Searching for a similar file name
            fileName = itemName + ".txt";
        }

        if (findFile(fileName, fileRoot) == null) {
            isOpen = true;
        }

        return isOpen;
    }

    // Construct the precise path of a file or directory based on its name and its
    // parent root directory
    public static String findFile(String fileName, String fileRoot) throws IOException {
        File foundFile = new File(fileRoot + fileName);

        if (foundFile.exists()) {
            fileRoot += fileName;

            // Refresh used instance variables
            prevFileRoot = null;
            dirsToSkip.clear();

            return fileRoot;
        } else if(foundFile.isDirectory()) {
            fileRoot += fileName;

            // Refresh used instance variables
            prevFileRoot = null;
            dirsToSkip.clear();

            return fileRoot;
        } else {
            if (prevFileRoot != "" && !(dirExistsIn(fileRoot))) {
                dirsToSkip.add(new File(fileRoot));
                return findFile(fileName, prevFileRoot);
            } else {
                File foundFiles = new File(fileRoot);
                File[] foundDirFiles = foundFiles.listFiles();

                for (File file : foundDirFiles) {
                    if (file.isDirectory()) {
                        if (dirsToSkip.contains(file)) {
                            // This directory has already been checked
                            continue;
                        } else {
                            // This directory has not yet been checked
                            prevFileRoot = "" + fileRoot;
                            fileRoot += file.getName() + "/";
                            return findFile(fileName, fileRoot);
                        }
                    }
                }
            }
        }

        return null;
    }

    // An exists() method for a directory in a specified path
    public static boolean dirExistsIn(String path) {
        boolean dirExists = false;

        File files = new File(path);
        File[] dirFiles = files.listFiles();

        for (File file : dirFiles) 
        {
            if (file.isDirectory()) 
            {
                dirExists = true;
            }
        }

        return dirExists;
    }

    public void updateMode(String newMode) throws IOException
    {
        currentMode = newMode;

        switch(newMode)
        {
            case "Standard Mode":
                // Set notes writer to standard mode
                progressBar.setValue(0);
                progressBar.setEnabled(false);
                statusMsg.setIcon(null);

                statusMsg.setText("Progress is not being tracked");

                break;
            case "Focus Mode":
                // Set notes writer to focus mode
                progressBar.setValue(0);
                progressBar.setEnabled(true);

                statusMsg.setText("Progress to 100 credits:");
                statusMsg.setIcon(null);

                // Check time in the background
                Runnable timeThread = new Runnable() 
                {
                    public void run()
                    {
                        while(currentMode.equals("Focus Mode"))
                        {
                            long startTime = System.currentTimeMillis();
                            long elapsedTime = 0;
                            int elapsedSeconds = 0;

                            // Increment the value of the progress bar every second
                            while(elapsedSeconds < 100)
                            {
                                elapsedTime = System.currentTimeMillis() - startTime;
                                elapsedSeconds = (int)elapsedTime / 1000;
                                System.out.println(elapsedSeconds);
                                progressBar.setValue(elapsedSeconds);

                                // Reward this user with 100 credits
                                if(elapsedSeconds == 100)
                                {
                                    startTime = System.currentTimeMillis();

                                    try {
                                        currentUser.setCredits(currentUser.getCredits(2) + 100);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                // If mode has changed, stop tracking time
                                if(!(currentMode.equals("Focus Mode")))
                                {
                                    break;
                                }
                            }
                            
                            Thread.currentThread().interrupt();
                        }
                    }
                };

                new Thread(timeThread).start();
                
                break;
            case "Zen Mode":
                // Set notes writer to zen mode
                progressBar.setValue(0);
                progressBar.setEnabled(false);
                
                statusMsg.setText("");
                statusMsg.setIcon(new javax.swing.ImageIcon(getClass().getResource("images/audio.png")));

                File audioDir = new File("audio/");
                File[] audio = audioDir.listFiles();

                // Play audio in the background
                Runnable audioThread = new Runnable() 
                {
                    public void run()
                    {
                        // Pick a random starting position in the audio directory
                        Random random = new Random();
                        int startIndex = random.nextInt(audio.length);

                        while(currentMode.equals("Zen Mode"))
                        {
                            // Play audio starting from randomlay selected position
                            if(startIndex != audio.length)
                            {
                                while(startIndex < audio.length)
                                {
                                    MediaPlayer.play(audio[startIndex]);
    
                                    // If mode has changed, stop playing audio
                                    if(!(currentMode.equals("Zen Mode")))
                                    {
                                        Thread.currentThread().interrupt();
                                    }

                                    startIndex++;
                                }
                            }
                            else
                            {
                                // Cycle back through audio
                                for(File media : audio)
                                {
                                    MediaPlayer.play(media);
    
                                    // If mode has changed, stop playing audio
                                    if(!(currentMode.equals("Zen Mode")))
                                    {
                                        Thread.currentThread().interrupt();
                                    }
                                }
                            }
                        }
                    }
                };
                
                new Thread(audioThread).start();

                break;
        }
    }

    public void updateFont(String newFont)
    {
        font = newFont;
        text = new Font(newFont, style, size);
        textArea.setFont(text);
    }

    public void updateStyle(int newStyle)
    {
        style = newStyle;
        text = new Font(font, newStyle, size);
        textArea.setFont(text);
    }

    public void updateSize(int newSize)
    {
        size = newSize;
        text = new Font(font, style, newSize);
        textArea.setFont(text);
    }

    // Continuously match a label to the input of a textfield on an "Adding Page" or "Adding Notebook" dialog
    public void updateName(JDialog dialog, JTextField textField, JLabel name, int pgOrNb)
    {
        Runnable updateName = new Runnable() 
        {
            public void run()
            {
                // Wait for 50 milliseconds to catch up to dialog.setVisible(true) method
                long startTime = System.currentTimeMillis();
                long elapsedTime = 0;

                while(elapsedTime < 50)
                {
                    elapsedTime = System.currentTimeMillis() - startTime;
                }

                while(dialog.isVisible())
                {
                    if(pgOrNb == 1)
                    {
                        // A page is being added
                        name.setText(textField.getText() + ".txt");
                    }
                    else if(pgOrNb == 2)
                    {
                        // A notebook is being added
                        name.setText(textField.getText() + "/");
                    }
                    else
                    {
                        System.out.println("(!) Invalid option inputted");
                        break;
                    }
                }

                Thread.currentThread().interrupt();
            }
        };

        new Thread(updateName).start();
    }
}
