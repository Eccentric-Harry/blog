package com.example.blog.dto;

/**
 * Enum representing the type/purpose of an uploaded image.
 * This determines which ImageKit folder the image will be stored in.
 */
public enum ImageType {
    /**
     * Cover/thumbnail image for a blog post.
     * Stored in: /blogs_cover_images
     */
    COVER("blogs_cover_images"),

    /**
     * Inline content image within a blog post.
     * Stored in: /blog_post_images
     */
    CONTENT("blog_post_images");

    private final String folder;

    ImageType(String folder) {
        this.folder = folder;
    }

    /**
     * Returns the ImageKit folder path for this image type.
     */
    public String getFolder() {
        return "/" + folder;
    }
}

