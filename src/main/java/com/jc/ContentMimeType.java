package com.jc;

public enum ContentMimeType {
    MIME_NONE(0, "NONE", "text/plain"),
    // -----------------------------------------
    MIME_TEXT(1, "TEXT", "text/plain; charset=UTF-8"),
    MIME_HTML(2, "HTML", "text/html; charset=UTF-8"),
    MIME_HTM(3, "HTM", "text/html; charset=UTF-8"),
    MIME_CSS(4, "CSS", "text/css"),
    // -----------------------------------------
    MIME_JS(10, "JS", "text/javascript"),
    MIME_JSON(11, "JSON", "application/json"),
    MIME_XML(12, "XML", "application/xml"),
    // -----------------------------------------
    MIME_JPEG(20, "JPEG", "image/jpeg"),
    MIME_JPG(21, "JPG", "image/jpeg"),
    MIME_PNG(22, "PNG", "image/png"),
    MIME_GIF(23, "GIF", "image/gif"),
    MIME_WEBP(24, "PNG", "image/webp"),
    MIME_AVI(25, "AVI", "image/avif"),
    // -----------------------------------------
    MIME_MULTIPART(50, "MULTIPART", "multipart/form-data; boundary=----------SEPARATOR----------");

    private final int ordinal;
    private final String suffix;
    private final String mimeType;

    /**
     * Ctor.
     * @param ordinal Index into values.
     * @param suffix File suffix for this type.
     * @param mimeType Mime type per HTTP header.
     */
    ContentMimeType(int ordinal, String suffix, String mimeType) {
        this.ordinal = ordinal;
        this.suffix = suffix;
        this.mimeType = mimeType;
    }

    /**
     * [static] Get the Mime Type for a particular file.
     * @param fileName name of file ending in revealing suffix.
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

    @SuppressWarnings("ALL")
    public int getOrdinal() {
        return ordinal;
    }

    @SuppressWarnings("ALL")
    public String getSuffix() {
        return suffix;
    }

    public String getMimeType() {
        return mimeType;
    }

}
