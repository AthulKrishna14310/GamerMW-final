package com.integrals.gamermw.Models;

public class AlbumModel {
    private String CoverPic;
    private String YouTubeLink;
    private String PublishedON;
    private String AlbumName;
    private String Details;
    private String Id;
    private String likes;

    public AlbumModel() {
    }

    public AlbumModel(String coverPic, String youTubeLink, String publishedON,
                      String albumName,
                      String details, String id, String likes) {
        CoverPic = coverPic;
        YouTubeLink = youTubeLink;
        PublishedON = publishedON;
        AlbumName = albumName;
        Details = details;
        Id = id;
        this.likes = likes;
    }

    public String getCoverPic() {
        return CoverPic;
    }

    public void setCoverPic(String coverPic) {
        CoverPic = coverPic;
    }

    public String getYouTubeLink() {
        return YouTubeLink;
    }

    public void setYouTubeLink(String youTubeLink) {
        YouTubeLink = youTubeLink;
    }

    public String getPublishedON() {
        return PublishedON;
    }

    public void setPublishedON(String publishedON) {
        PublishedON = publishedON;
    }

    public String getAlbumName() {
        return AlbumName;
    }

    public void setAlbumName(String albumName) {
        AlbumName = albumName;
    }

    public String getDetails() {
        return Details;
    }

    public void setDetails(String details) {
        Details = details;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }
}