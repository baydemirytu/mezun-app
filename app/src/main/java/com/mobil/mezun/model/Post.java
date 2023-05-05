package com.mobil.mezun.model;

public class Post {

    private String postId;

    private String userId;

    private String message;

    private String imageId;

    public Post() {
    }

    public Post(String postId, String userId, String message, String imageId) {
        this.postId = postId;
        this.userId = userId;
        this.message = message;
        this.imageId = imageId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    @Override
    public String toString() {
        return "Post{" +
                "postId='" + postId + '\'' +
                ", userId='" + userId + '\'' +
                ", message='" + message + '\'' +
                ", imageId='" + imageId + '\'' +
                '}';
    }
}
