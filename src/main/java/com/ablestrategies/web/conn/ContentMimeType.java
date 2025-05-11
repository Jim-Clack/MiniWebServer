package com.ablestrategies.web.conn;

public enum ContentMimeType {
    MIME_NONE("NONE", "text/plain", false),
    // -----------------------------------------
    MIME_TEXT("TEXT", "text/plain; charset=UTF-8", false),
    MIME_HTML("HTML", "text/html; charset=UTF-8", false),
    MIME_HTM("HTM", "text/html; charset=UTF-8", true),
    MIME_CSS("CSS", "text/css", false),
    MIME_JS("JS", "text/javascript", false),
    // -----------------------------------------
    MIME_JSON("JSON", "application/json", false),
    MIME_TXTJSON("JSON", "text/json", true),
    MIME_XML("XML", "application/xml", false),
    MIME_TXTXML("XML", "text/xml", true),
    // -----------------------------------------
    MIME_JPEG("JPEG", "image/jpeg", false),
    MIME_JPG("JPG", "image/jpeg", true),
    MIME_PNG("PNG", "image/png", false),
    MIME_GIF("GIF", "image/gif", false),
    MIME_WEBP("PNG", "image/webp", false),
    MIME_AVI("AVI", "image/avif", false),
    // -----------------------------------------
    MIME_MULTIPART("MULTIPART", "multipart/form-data; boundary=----------SEPARATOR----------", false);

    /** File suffix for this mime type. */
    private final String suffix;

    /** The Internet/HTTP standard for this mime type. */
    private final String mimeString;

    /** True=secondary usage, false=preferred/primary. */
    private final boolean alternateForm;

    /**
     * Ctor.
     * @param suffix File suffix for this type.
     * @param mimeString Mime type per HTTP header.
     * @param alternateForm True if this is not the preferred mime form.
     */
    ContentMimeType(String suffix, String mimeString, boolean alternateForm) {
        this.suffix = suffix;
        this.mimeString = mimeString;
        this.alternateForm = alternateForm;
    }

    /**
     * Get the HTTP standard string for this mime type.
     * @return I.e. "application/json".
     */
    public String getMimeString() {
        return mimeString;
    }

    /**
     * Get the base mime type in lowercase.
     * @param mimeString Mime type, possible including extraneous info after a semicolon.
     * @return lowercase mime type.
     */
    public static String getMimeStringBase(String mimeString) {
        if(mimeString.contains(";")) {
            return mimeString.substring(0, mimeString.indexOf(";")).trim().toLowerCase();
        }
        return mimeString.trim().toLowerCase();
    }

    /**
     * Get a mime type from header values.
     * @param headerValues Mime type strings, typically from the list of ";"-delimited values.
     * @return The ContentMimeType, MIME_HTML if not found. Generally NOT an alternate form.
     */
    public static ContentMimeType getMimeType(String[] headerValues) {
        boolean foundPreferred = false;
        ContentMimeType resultMimeType = ContentMimeType.MIME_HTML;
        if(headerValues != null) {
            // generally the outer loop only executes one time...
            for(String headerValue : headerValues) {
                String headerValueBase = ContentMimeType.getMimeStringBase(headerValue);
                for (ContentMimeType checkMimeType : ContentMimeType.values()) {
                    String checkBase = ContentMimeType.getMimeStringBase(checkMimeType.getMimeString());
                    if(headerValueBase.equals(checkBase)) {
                        if(!checkMimeType.alternateForm) {
                            foundPreferred = true; // otherwise keep looping
                        }
                        resultMimeType = checkMimeType;
                        break;
                    }
                }
                if(foundPreferred) {
                    break;
                }
            }
        }
        return resultMimeType;
    }

    /**
     * [static] Get the Mime Type for a particular file.
     * @param fileName name of file ending in a dot (.) followed by the revealing suffix.
     * @return Mime type for file, possibly MIME_NONE.
     */
    public static ContentMimeType mimeTypeFromFileSuffix(String fileName) {
        String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
        for (ContentMimeType mimeType : ContentMimeType.values()) {
            if(mimeType.suffix.equals(fileSuffix)) {
                return mimeType;
            }
        }
        return MIME_NONE;
    }

    /**
     * [static] Get the Mime Type based on the mime type string in the HTTP header.
     * @param mimeTypeString From header Content-Type or Accepts, may have more info after semicolon.
     * @return Mime type, possibly MIME_NONE. May return alternateForms.
     */
    @SuppressWarnings("ALL")
    public static ContentMimeType mimeTypeFromHeaderValue(String mimeTypeString) {
        String mimeStringBase = getMimeStringBase(mimeTypeString);
        for (ContentMimeType mimeType : ContentMimeType.values()) {
            if(mimeType.mimeString.contains(mimeStringBase)) {
                return mimeType;
            }
        }
        return null;
    }

    /**
     * [static] Get the file suffix for a given mime type.
     * @param mimeTypeString String containing the mime type, such as "...text/html...".
     * @return A file suffix with a leading dot (.), or null if not found. Skips alternateForms.
     */
    @SuppressWarnings("ALL")
    public static String fileSuffixFromMimeType(String mimeTypeString) {
        String mimeStringBase = getMimeStringBase(mimeTypeString);
        for (ContentMimeType mimeType : ContentMimeType.values()) {
            if(mimeType.alternateForm) {
                continue; // skip short-forms of suffixes
            }
            if(mimeStringBase.endsWith(mimeType.suffix)) {
                return "." + mimeType.suffix;
            }
        }
        return null;
    }

}
