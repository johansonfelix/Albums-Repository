package com;

import DAO.DatabaseManager;
import exceptions.RepException;
import pojo.Album;
import pojo.LogEntry;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

public class Albums implements Serializable {

    DatabaseManager db = DatabaseManager.getInstance();

    public Albums(){}

    public void createAlbum(Album album) throws RepException {

        try {


            if (db.getAlbum(album.getISRC()) == null) {
                db.insertAlbum(album);

                LogEntry newEntry = new LogEntry();

                newEntry.setISRC(album.getISRC());
                newEntry.setT(LogEntry.stringToTypeOfChange("CREATE"));
                newEntry.setTimestamp(new Timestamp(System.currentTimeMillis()));
                db.insertLogEntry(newEntry);
            } else
                throw new RepException("Album cannot be created - album already exists!");

        }
        catch(RepException e){
            System.out.println(e.getMessage());
        }
    }

    public void updateAlbum(Album album) throws RepException {
        try {
            if (db.getAlbum(album.getISRC()) != null) {
                db.updateAlbum(album);

                LogEntry newEntry = new LogEntry();

                newEntry.setISRC(album.getISRC());
                newEntry.setT(LogEntry.stringToTypeOfChange("UPDATE"));
                newEntry.setTimestamp(new Timestamp(System.currentTimeMillis()));
                db.insertLogEntry(newEntry);
            } else
                throw new RepException("Album cannot be updated - album does not exist!");

        }
        catch(RepException e){
            System.out.println(e.getMessage());
        }
    }

    public void deleteAlbum(String ISRC) throws RepException {

        try{
            if(db.getAlbum(ISRC) != null){
                db.deleteAlbum(ISRC);

                LogEntry newEntry = new LogEntry();

                newEntry.setISRC(ISRC);
                newEntry.setT(LogEntry.stringToTypeOfChange("DELETE"));
                newEntry.setTimestamp(new Timestamp(System.currentTimeMillis()));
                db.insertLogEntry(newEntry);
            }

            else
                throw new RepException("Album cannot be deleted - album does not exist!");
        }

        catch(RepException e){
            System.out.println(e.getMessage());
        }
    }

    public Album getAlbumInfo(String ISRC) throws RepException {

        try {

            if (db.getAlbum(ISRC) != null) {
                Album album = db.getAlbum(ISRC);

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

    public void updateAlbumCoverImage(){}
    public void deleteAlbumCoverImage(){}
    public void getAlbumCoverImage(){}

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
