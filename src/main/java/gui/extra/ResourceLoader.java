package gui.extra;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import javafx.scene.image.Image;
import pieces.Alliance;
import pieces.Piece;

import java.io.IOException;
import java.net.URL;

/**
 * Load all the resources into memory
 */
public class ResourceLoader {

    private static ResourceLoader resourceLoader = null;

    //Pieces
    public final Image BB, BK, BN, BP, BQ, BR, WB, WK, WN, WP, WQ, WR;

    //GUI images
    public final Image AppIcon, hintButton, undoButton, gameStats;

    //Rule text
    public final String tutor;

    private ResourceLoader() {
        BB = new Image("images/" + "BB" + ".png");
        BK = new Image("images/" + "BK" + ".png");
        BN = new Image("images/" + "BN" + ".png");
        BP = new Image("images/" + "BP" + ".png");
        BQ = new Image("images/" + "BQ" + ".png");
        BR = new Image("images/" + "BR" + ".png");

        WB = new Image("images/" + "WB" + ".png");
        WK = new Image("images/" + "WK" + ".png");
        WN = new Image("images/" + "WN" + ".png");
        WP = new Image("images/" + "WP" + ".png");
        WQ = new Image("images/" + "WQ" + ".png");
        WR = new Image("images/" + "WR" + ".png");

        hintButton = new Image("images/GUI/HintButton.png");
        undoButton = new Image("images/GUI/UndoButton.png");
        gameStats = new Image("images/GUI/GameStats.png");
        AppIcon = new Image("images/GUI/" + "AppIcon" + ".png");

        tutor = readFile("rules/tutor.txt");
    }

    public static ResourceLoader getInstance() {
        if (resourceLoader == null) {
            resourceLoader = new ResourceLoader();
        }
        return resourceLoader;
    }

    /**
     * Find an image to represent a piece
     *
     * @param p piece to find image for
     * @return image representation of piece
     */
    public Image getPieceImage(Piece p) {
        Alliance pieceAlliance = p.getPieceAlliance();
        boolean isWhite = pieceAlliance == Alliance.WHITE;
        switch (p.getPieceType()) {
            case BISHOP:
                return isWhite ? WB : BB;
            case KING:
                return isWhite ? WK : BK;
            case KNIGHT:
                return isWhite ? WN : BN;
            case PAWN:
                return isWhite ? WP : BP;
            case QUEEN:
                return isWhite ? WQ : BQ;
            case ROOK:
                return isWhite ? WR : BR;
            default:
                return null;
        }
    }

    /**
     * Reads a text file from a given location
     *
     * @param location of txt file
     * @return text contents of the file
     */
    private String readFile(String location) {
        URL url = Resources.getResource(location);
        String text = "";
        try {
            text = Resources.toString(url, Charsets.UTF_8);
        } catch (IOException e) {
            System.out.println("AN ERROR OCCURRED WHEN READING RULE FILES");
            e.printStackTrace();
        }
        return text;
    }
}