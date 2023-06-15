package player;


import com.mongodb.MongoClientURI;
import com.mongodb.client.*;
import org.bson.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Score {
    private static Score scoreSystem = null;
    private final HashMap<String, Integer> userRating;
    private final HashMap<String, Stats> userStats;

    private Score() {
        this.userRating = new HashMap<>();
        this.userStats = new HashMap<>();
        readHighscore();
    }

    public static Score getInstance() {
        if (scoreSystem == null) {
            scoreSystem = new Score();
        }
        return scoreSystem;
    }

    /**
     * @return a boolean value if internet is online or offline
     */
    private static boolean netIsAvailable() {
        try {
            final URL url = new URL("http://www.google.com/");
            final URLConnection conn = url.openConnection();
            conn.connect();
            conn.getInputStream().close();
            return true;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Reads highscore from MLab server. Iterates through all saved database entries
     */
    public void readHighscore() {
        boolean netOnline = netIsAvailable();
        //Replace with netOnline when you have added your database
        if(netOnline) {
            //Use your own mlab database here
            try{
                String url = "mongodb+srv://manhtien30:<30102001>@cluster1.gsctkjd.mongodb.net/";
                MongoClientURI uri = new MongoClientURI(url);

                MongoClient mongoClient = MongoClients.create(String.valueOf(uri));
                System.out.println(mongoClient);
                MongoDatabase database = mongoClient.getDatabase("highscore");
                MongoCollection<Document> highscore = database.getCollection("score");
                System.out.println("A");
                try (MongoCursor<Document> cursor = highscore.find().iterator()) {
                    while (cursor.hasNext()) {
                        Document doc = cursor.next();
                        int rating = (int) doc.get("score");
                        Stats stats = readStats(doc.get("stats").toString());
                        userRating.put(doc.get("playername").toString(), rating);
                        userStats.put(doc.get("playername").toString(), stats);
                    }
                }
                mongoClient.close();
            }
            catch(Exception e){
                System.out.println(e);
            }
        }
    }

    private Stats readStats(String stats) {
        String[] temp = stats.split("/");
        int w = Integer.parseInt(temp[0]);
        int d = Integer.parseInt(temp[1]);
        int l = Integer.parseInt(temp[2]);
        return new Stats(w, d, l);
    }

    public ArrayList<String> getScoreboard() {
        ArrayList<String> scoreboard = new ArrayList<>();
        HashMap<String, Integer> temp = new HashMap<>(userRating);
        while (!temp.isEmpty()) {
            int hi = 0;
            String u = "";
            for (String s : temp.keySet()) {
                if (temp.get(s) >= hi) {
                    hi = temp.get(s);
                    u = s;
                }
            }
            scoreboard.add(u);
            temp.remove(u, hi);
        }
        return scoreboard;
    }

    /**
     * takes the old rating of both players, and the result of the game for both players,
     * 1 for win, 0.5 for draw and 0 for loss
     *
     * @param username1   player1
     * @param username2   player2
     * @param gameResult1 gameresult for player 1
     * @param gameResult2 gameresult for player 2
     * @return Array containing updated ratings for both players
     */
    public int[] matchRating(String username1, String username2, double gameResult1, double gameResult2) {
        int[] score = new int[2];
        double player1Rating = userRating.get(username1);
        double player2Rating = userRating.get(username2);

        int K = 32;
        double R1 = Math.pow(10, (player1Rating / 400));
        double R2 = Math.pow(10, (player2Rating / 400));
        double E1 = R1 / (R1 + R2);
        double E2 = R2 / (R1 + R2);
        double newPlayer1Rating = player1Rating + K * (gameResult1 - E1);
        double newPlayer2Rating = player2Rating + K * (gameResult2 - E2);

        score[0] = (int) newPlayer1Rating;
        score[1] = (int) newPlayer2Rating;

        return score;
    }

    private void writeHighscore() {
        boolean netOnline = netIsAvailable();
        //Replace with netOnline when you have added your database
        if(netOnline) {
            System.out.println("BBB");
            List<Document> highscoreDB = new ArrayList<>();
            //Use your own mlab database here
            String url = "mongodb+srv://ngiabao508:Bb12112001@cluster0.hghzt.mongodb.net/test?readPreference=nearest";
            MongoClientURI uri = new MongoClientURI(url);

            MongoClient client = MongoClients.create(String.valueOf(uri));
            MongoDatabase database = client.getDatabase("highscore");
            MongoCollection<Document> highscore = database.getCollection("score");
            highscore.drop();

            for (String s : userRating.keySet()) {
                highscoreDB.add(new Document("playername", s)
                        .append("score", userRating.get(s))
                        .append("stats", userStats.get(s).getStats()));
            }
            highscore.insertMany(highscoreDB);
            client.close();
        }
    }

    /**
     * Adds a username with 1500 rating and empty stats to highscore.txt if the username does not already exist
     *
     * @param username name of the user to add
     */
    public void addUsername(String username) {
        if (!userRating.containsKey(username)) {
            userRating.put(username, 1500);
            userStats.put(username, new Stats(0, 0, 0));
            writeHighscore();
        }
    }

    /**
     * Updates a users highscore
     *
     * @param username name of the user
     * @param newScore what to set the new score to
     */
    public void updateHighscore(String username, int newScore) {
        userRating.put(username, newScore);
        writeHighscore();
    }

    /**
     * Returns a users rating
     *
     * @param username the username of the user
     * @return the score of the given user
     */
    public int getScore(String username) {
        return userRating.get(username);
    }

    /**
     * Returns a users game statistics
     *
     * @param username the username of the user
     * @return a string with wins/draws/losses
     */
    public String getStats(String username) {
        return userStats.get(username).getStats();
    }

    /**
     * Returns a users game statistics
     *
     * @param username the username of the user
     * @return a string with Wins: w, Draws: d, Losses: l
     */
    public String getStatsVerbose(String username) {
        return userStats.get(username).getStatsVerbose();
    }

    public void addWin(String username) {
        userStats.get(username).addWin();
    }

    public void addDraw(String username) {
        userStats.get(username).addDraw();
    }

    public void addLoss(String username) {
        userStats.get(username).addLoss();
    }

    public int size() {
        return userRating.size();
    }

    private class Stats {
        int wins, draws, losses;

        private Stats(int wins, int draws, int losses) {
            this.wins = wins;
            this.draws = draws;
            this.losses = losses;
        }

        private void addWin() {
            wins++;
        }

        private void addDraw() {
            draws++;
        }

        private void addLoss() {
            losses++;
        }

        private String getStats() {
            return wins + "/" + draws + "/" + losses;
        }

        private String getStatsVerbose() {
            return "Wins: " + wins + ", Draws: " + draws + ", Losses: " + losses;
        }
    }
}
