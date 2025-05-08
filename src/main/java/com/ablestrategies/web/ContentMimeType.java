package com.ablestrategies.web;

public enum ContentMimeType {
    MIME_NONE("NONE", "text/plain"),
    // -----------------------------------------
    MIME_TEXT("TEXT", "text/plain; charset=UTF-8"),
    MIME_HTML("HTML", "text/html; charset=UTF-8"),
    MIME_HTM("HTM", "text/html; charset=UTF-8"),
    MIME_CSS("CSS", "text/css"),
    MIME_JS("JS", "text/javascript"),
    // -----------------------------------------
    MIME_JSON("JSON", "application/json"),
    MIME_XML("XML", "application/xml"),
    // -----------------------------------------
    MIME_JPEG("JPEG", "image/jpeg"),
    MIME_JPG("JPG", "image/jpeg"),
    MIME_PNG("PNG", "image/png"),
    MIME_GIF("GIF", "image/gif"),
    MIME_WEBP("PNG", "image/webp"),
    MIME_AVI("AVI", "image/avif"),
    // -----------------------------------------
    MIME_MULTIPART("MULTIPART", "multipart/form-data; boundary=----------SEPARATOR----------");

    private final String suffix;
    private final String mimeType;

    /**
     * Ctor.
     * @param suffix File suffix for this type.
     * @param mimeType Mime type per HTTP header.
     */
    ContentMimeType(String suffix, String mimeType) {
        this.suffix = suffix;
        this.mimeType = mimeType;
    }

    /**
     * [static] Get the Mime Type for a particular file.
     * @param fileName name of file ending in a dot (.) followed by the revealing suffix.
     * @return Mime type for file, possibly MIME_NONE.
     */
    public static ContentMimeType mimeTypeFromSuffix(String fileName) {
        String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
        for (ContentMimeType mimeType : ContentMimeType.values()) {
            if(mimeType.suffix.equals(fileSuffix)) {
                return mimeType;
            }
        }
        return MIME_NONE;
    }

    /**
     * [static] Get the file suffix for a given mime type.
     * @param mimeTypeString String containing the mime type, such as "...text/html...".
     * @return A file suffix with a leading dot (.), or null if not found.
     */
    @SuppressWarnings("ALL")
    public static String fileSuffixFromMimeType(String mimeTypeString) {
        String mimeTypeBase = mimeTypeString.toLowerCase();
        if(mimeTypeBase.contains(";")) {
            mimeTypeBase = mimeTypeBase.substring(0, mimeTypeBase.indexOf(";"));
        }
        for (ContentMimeType mimeType : ContentMimeType.values()) {
            if(mimeType == ContentMimeType.MIME_HTM || mimeType == ContentMimeType.MIME_JPG) {
                continue; // skip short-forms of suffixes
            }
            if(mimeType.mimeType.contains(mimeTypeBase)) {
                return "." + mimeType.suffix.toLowerCase();
            }
        }
        return null;
    }

    public String getMimeType() {
        return mimeType;
    }

}
