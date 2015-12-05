package com.sw.jigsaws.data;

import android.graphics.Bitmap;

public class GameData {

    public static String sessionId;

    public static Game game;


    public static void setGame(Long imageId, String url, boolean isTryPlay) {

        if (game == null) {
            game = new Game();
        }

        game.imageId = imageId;
        game.url = url;
        game.isTryPlay = isTryPlay;
        game.setHard(2);

    }


    public static void setGame(Bitmap bitmap) {

        if (game == null) {
            game = new Game();
        }

        game.bitmap = bitmap;

    }


}