package com;


import DAO.DatabaseManager;
import exceptions.RepException;
import pojo.Album;
import pojo.LogEntry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;

public class Albums implements Serializable {

    private DatabaseManager db;

    private static Albums albumsInstance = new Albums();
    String [] albumColNames = {"ISRC", "Title", "Description", "Release_Year", "Artist_First_Name", "Artist_Last_Name", "Cover_Image"};
    String [] logColNames = {"Type_Of_Change", "ISRC"};

    private Albums(){

        db = DatabaseManager.getInstance();
    }


    public static Albums getAlbumsInstance(){
        return albumsInstance;
    }


    public String createAlbum(Album album) throws RepException {


            if (db.getAlbum(album.getISRC()) == null) {


                String [] values;
                if(album.getCover_img()==null)
                    values = new String[]{album.getISRC(), album.getTitle(), album.getDescription(), album.getReleaseYear(), album.getArtistFirstName(), album.getArtistLastName(), "NO IMAGE"};

                else
                    values = new String[]{album.getISRC(), album.getTitle(), album.getDescription(), album.getReleaseYear(), album.getArtistFirstName(), album.getArtistLastName(), Base64.getEncoder().encodeToString(album.getCover_img())};

                db.insertOrUpdate(DatabaseManager.OperationType.INSERT, "Albums", albumColNames, values);


                LogEntry newEntry = new LogEntry();
                newEntry.setISRC(album.getISRC());
                newEntry.setT(LogEntry.stringToTypeOfChange("CREATE"));


                values = new String[]{String.valueOf(newEntry.getT()), newEntry.getISRC()};

                db.insertOrUpdate(DatabaseManager.OperationType.INSERT, "LogEntries", logColNames, values);

                System.out.println("ALBUM "+album.getISRC()+" CREATED");
                return "ALBUM "+album.getISRC()+" CREATED";
            } else
                throw new RepException("Album cannot be created - album already exists!");

    }

    public String updateAlbum(Album album){
        try {
            System.out.println("here 1");
            if (db.getAlbum(album.getISRC()) != null) {
                System.out.println("here 2");
                String [] values;
                if(album.getCover_img()==null)
                    values = new String[]{album.getISRC(), album.getTitle(), album.getDescription(), album.getReleaseYear(), album.getArtistFirstName(), album.getArtistLastName(), "NO IMAGE"};

                else
                    values = new String[]{album.getISRC(), album.getTitle(), album.getDescription(), album.getReleaseYear(), album.getArtistFirstName(), album.getArtistLastName(), Base64.getEncoder().encodeToString(album.getCover_img())};

                db.insertOrUpdate(DatabaseManager.OperationType.UPDATE, "Albums", albumColNames, values);

                LogEntry newEntry = new LogEntry();
                newEntry.setISRC(album.getISRC());
                newEntry.setT(LogEntry.stringToTypeOfChange("UPDATE"));


                values = new String[]{String.valueOf(newEntry.getT()), newEntry.getISRC()};

                db.insertOrUpdate(DatabaseManager.OperationType.INSERT, "LogEntries", logColNames, values);

                System.out.println("ALBUM "+album.getISRC()+" UPDATED");
                return "ALBUM "+album.getISRC()+" UPDATED";
            } else
                throw new RepException("Album cannot be updated - album does not exist!");

        }
        catch(RepException e){
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }

    public String deleteAlbum(String ISRC) throws RepException {

        try{
            if(db.getAlbum(ISRC) != null){
                db.delete("Albums","ISRC", ISRC);

                LogEntry newEntry = new LogEntry();
                newEntry.setISRC(ISRC);
                newEntry.setT(LogEntry.stringToTypeOfChange("DELETE"));

                String [] colnames = new String[]{"Type_Of_Change", "ISRC"};
                String [] values = new String[]{String.valueOf(newEntry.getT()), newEntry.getISRC()};

                db.insertOrUpdate(DatabaseManager.OperationType.INSERT, "LogEntries", colnames, values);

                System.out.println("ALBUM "+ISRC+" DELETED");
                return "ALBUM "+ISRC+" DELETED";

            }

            else
                throw new RepException("Album cannot be deleted - album does not exist!");
        }

        catch(RepException e){
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }

    public Album getAlbumInfo(String ISRC) {

        try {

            if (db.getAlbum(ISRC) != null) {
                Album album = db.getAlbum(ISRC);

                System.out.println("ALBUM "+ISRC+" RETURNED.");
                return album;

            }
            else
                throw new RepException("Album does not exist - cannot return info! ");

        }
        catch(RepException e){

            System.out.println(e.getMessage());
        }
        return null;
    }

    public ArrayList<Album> getAlbumsList(){
        return db.getAllAlbums();
    }

    public String updateAlbumCoverImage(String ISRC, byte[] bytes){
        try{

            Album album = db.getAlbum(ISRC);

            if(album != null){
                String [] colnames = {"ISRC", "Title", "Description", "Release_Year", "Artist_First_Name", "Artist_Last_Name", "Cover_Image"};
                String [] values = {album.getISRC(), album.getTitle(), album.getDescription(), album.getReleaseYear(), album.getArtistFirstName(), album.getArtistLastName(), Base64.getEncoder().encodeToString(bytes)};
                update(album, colnames, values);
                System.out.println("ALBUM COVER FOR "+ISRC+" WAS UPDATED");
                return "ALBUM COVER IMAGE FOR "+ISRC+" WAS UPDATED";
            }
            else{
                throw new RepException("Album does not exist - cannot update cover image.");
            }
        }

        catch (RepException e){

            return e.getMessage();
        }

    }



    private void update(Album album, String[] colnames, String[] values) {
        db.insertOrUpdate(DatabaseManager.OperationType.UPDATE, "Albums", colnames, values);

        LogEntry newEntry = new LogEntry();
        newEntry.setISRC(album.getISRC());
        newEntry.setT(LogEntry.stringToTypeOfChange("UPDATE"));

        colnames = new String[]{"Type_Of_Change", "ISRC"};
        values = new String[]{String.valueOf(newEntry.getT()), newEntry.getISRC()};

        db.insertOrUpdate(DatabaseManager.OperationType.INSERT, "LogEntries", colnames, values);
    }

    public String deleteAlbumCoverImage(String ISRC){
        try{

            Album album = db.getAlbum(ISRC);

            if(album != null){
                String [] colnames = {"ISRC", "Title", "Description", "Release_Year", "Artist_First_Name", "Artist_Last_Name", "Cover_Image"};
                String [] values = {album.getISRC(), album.getTitle(), album.getDescription(), album.getReleaseYear(), album.getArtistFirstName(), album.getArtistLastName(), "NO IMAGE"};
                update(album, colnames, values);
                System.out.println("ALBUM COVER FOR "+ISRC+" WAS DELETED");
                return "ALBUM COVER IMAGE FOR "+ISRC+" WAS DELETED";
            }
            else{
                throw new RepException("Album does not exist - cannot delete cover image.");
            }
        }

        catch (RepException e){

            return e.getMessage();
        }

    }
/*    public byte[] getAlbumCoverImage(String ISRC){
        return albums.getCoverImage();
    }*/

    public ArrayList<LogEntry> getLogs(){
        return db.getAllLogEntries();
    }

    public String clearLogs(){

        try{
            throw new RepException("This method is not yet supported");
        }
        catch(RepException e){
            return e.getMessage();
        }
    }


}
