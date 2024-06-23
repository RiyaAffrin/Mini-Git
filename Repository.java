/*Riya Affrin
4/28/24
CSE 123 BG
TA: Eric Bea 
P1 Mini Git

This is mini git. A git is a source control system. Here we are making a mini version of that
to use. In our mini git, a set of documents and their histories are referred to as a
repository, which is the class we are creating. The class contains methods such as
getHistory, getRepoHead, getRepoSize, toString, contains, commit, drop, and synchronize.
All these methods work together along with the Commit class to creat our git.*/

import java.util.*;
import java.text.SimpleDateFormat;

//Here is the class header along with our private fields
//String name, a commit List named commits and a commit head
public class Repository {
    private String name;
    private List<Commit> commits;
    private Commit head;


    //Here is our constructor for the class. Nothing is returned and string name is passed in
    //we throw an IllegalArgumentException incase the name String is empty or null
    public Repository(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException();
        }
        //here we are initializing our fields so that they are updated throughout the code
        this.name = name;
        this.commits = new ArrayList<>();
    }

    //This is getRepoHead. Depending on whether head is null or not we return either a string
    //head.id or null. This method does not pass in any parameters
    public String getRepoHead() {
        //Id comes from the commit class
        if (head == null) {
            return null;
        } else {
            return head.id;
        }
    }

    //getRepoSize method returns the number of elements in commits list which represents the size
    //of our repository. this method does not need to pass in parameters
    public int getRepoSize() {
        return commits.size();
    }

    //The toString method returns a string which shows the repository's name and the message no
    //commits if the commits is empty or the current head's details if commits is not empty
    public String toString() {
        if (commits.isEmpty()) {
            return name + " - No commits";
        } else {
            return name + " - Current head: " + head.toString();
        }
    }

    //The contains method returns a boolean and passes in a string targetId
    //The purpose of this method is to check if there is a commit in the commits list with an id
    //that matches the passed in string. If it is found we return true otherwise we return false
    public boolean contains(String targetId) {
        for (Commit commit : commits) {
            if (commit.id.equals(targetId)) {
                return true;
            }
        }
        return false;
    }

    //This method getHistory passes in an integer and returns a string
    //first thing we do is check if the number passed in is less than or equal to 0
    //if it is we throw an IllegalArgumentException
    //if n is >= 0 then we get the last nth commits 
    //we return it as a string. We return each one in order of most recent to oldest.
    public String getHistory(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }
        int historySize = Math.min(n, commits.size());
        String history = "";
        //String[] history = new String[historySize];
        int indexHistory = historySize -1;
        int indexCommit = commits.size()-1;

        while(indexHistory >=0 && indexCommit >= 0){
            Commit com = commits.get(indexCommit);

            history += com.toString() + "\n";
            indexCommit--;
            indexHistory--;
            
        }
        if(history.endsWith("\n")){
            history = history.substring(0, history.length() - 1);
        }
        return history;
    }

    //The commit method returns a String and passes in a String called message.
    //This method creates a new commit with a given message and updates the head with the new
    //commit. Lastly it return sthe ID of the new commit
    public String commit(String message) {
        Commit newCommit = new Commit(message, head);
        commits.add(newCommit);
        head = newCommit;
        return newCommit.id;
    }

    //The drop method returns a boolean and passes in a string targetId
    //this method removes or "drops" a commit based on if it has the passed in string targetId
    // if the commit that is being dropped is the last one then the head gets updated
    //to the previous commit. If the removed commit is not last then the head points to the next
    //commit. If it is successfully deleted then the method returns true otherwise
    // it will return false

    public boolean drop(String targetId) {
        for (int i = 0; i < commits.size(); i++) {
            Commit com = commits.get(i);
            if (com.id.equals(targetId)) {
                commits.remove(i);
                if (i == commits.size()) {
                    if (i > 0) {
                        head = commits.get(i - 1);
                    } else {
                        head = null;
                    }
                } else {
                    head = commits.get(i);
                }
                return true;
            }
        }
        return false;
    }

    //This doesn't return anything but passes in a repository named other. This method is used to
    //combine and updates the history of 2 repositories. If other is empty then no change is made. 
    //If the current repository is empty then it copies over the information from other into it. 
    //If both are not empty then it mixes them together and based on their timestamp. 
    //This method is to make sure that the current repository is up to date

    public void synchronize(Repository other) {
        boolean isOtherEmpty = other.commits.isEmpty();
        boolean isCurrEmpty = commits.isEmpty();
    
        if (isOtherEmpty) {
            // Do nothing, since the other repository is empty
        } else if (isCurrEmpty) {
            commits = new ArrayList<>(other.commits);
            head = other.head;
            other.commits.clear();
            other.head = null;
        } else {
            List<Commit> mixed = new ArrayList<>();
            Commit current = head;
            List<Commit> otherCommits = new ArrayList<>(other.commits);
    
            while (current  != null || !otherCommits.isEmpty()) {
                if (current  != null && (otherCommits.isEmpty() || current.timeStamp > otherCommits.get(0).timeStamp)) {
                    mixed.add(current );
                    current  = current .past;
                } else {
                    mixed.add(otherCommits.get(0));
                    otherCommits.remove(0);
                }
            }
    
            commits = mixed;
            head = commits.get(commits.size() - 1);
            other.commits.clear();
            other.head = null;
        }
    }





    /**
     * DO NOT MODIFY
     * A class that represents a single commit in the repository.
     * Commits are characterized by an identifier, a commit message,
     * and the time that the commit was made. A commit also stores
     * a reference to the immediately previous commit if it exists.
     *
     * Staff Note: You may notice that the comments in this 
     * class openly mention the fields of the class. This is fine 
     * because the fields of the Commit class are public. In general, 
     * be careful about revealing implementation details!
     */
    public class Commit {

        private static int currentCommitID;

        /**
         * The time, in milliseconds, at which this commit was created.
         */
        public final long timeStamp;

        /**
         * A unique identifier for this commit.
         */
        public final String id;

        /**
         * A message describing the changes made in this commit.
         */
        public final String message;

        /**
         * A reference to the previous commit, if it exists. Otherwise, null.
         */
        public Commit past;

        /**
         * Constructs a commit object. The unique identifier and timestamp
         * are automatically generated.
         * @param message A message describing the changes made in this commit.
         * @param past A reference to the commit made immediately before this
         *             commit.
         */
        public Commit(String message, Commit past) {
            this.id = "" + currentCommitID++;
            this.message = message;
            this.timeStamp = System.currentTimeMillis();
            this.past = past;
        }

        /**
         * Constructs a commit object with no previous commit. The unique
         * identifier and timestamp are automatically generated.
         * @param message A message describing the changes made in this commit.
         */
        public Commit(String message) {
            this(message, null);
        }

        /**
         * Returns a string representation of this commit. The string
         * representation consists of this commit's unique identifier,
         * timestamp, and message, in the following form:
         *      "[identifier] at [timestamp]: [message]"
         * @return The string representation of this collection.
         */
        @Override
        public String toString() {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            Date date = new Date(timeStamp);

            return id + " at " + formatter.format(date) + ": " + message;
        }

        /**
        * Resets the IDs of the commit nodes such that they reset to 0.
        * Primarily for testing purposes.
        */
        public static void resetIds() {
            Commit.currentCommitID = 0;
        }
    }
}