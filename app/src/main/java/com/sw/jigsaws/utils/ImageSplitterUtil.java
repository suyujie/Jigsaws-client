package com.sw.jigsaws.utils;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class ImageSplitterUtil {

    /**
     * @param bitmap
     * @return List<ImagePiece>
     */
    public static List<ImagePiece> splitImage(Bitmap bitmap, int line, int column, int margin) {
        List<ImagePiece> imagePieces = new ArrayList<ImagePiece>();

        int pieceWidth = (bitmap.getWidth() - margin * column) / column;
        int pieceHeight = (bitmap.getHeight() - margin * line) / line;

        int index = 0;
        for (int i = 0; i < line; i++) {
            for (int j = 0; j < column; j++) {

                ImagePiece imagePiece = new ImagePiece();
                imagePiece.setIndex(index++);

                int x = j * pieceWidth;
                int y = i * pieceHeight;

                imagePiece.setBitmap(Bitmap.createBitmap(bitmap, x, y, pieceWidth, pieceHeight));
                imagePieces.add(imagePiece);
            }
        }

        return imagePieces;
    }

}
