package org.opennms.android.net;

/**
 * Helper-class that is used as temporary storage for server response.
 */
public class Response {

    private int code;
    private String message;

    /**
     * @param code    HTTP status code. See status-codes in {@link java.net.HttpURLConnection}.
     * @param message Message returned by server.
     */
    public Response(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * @return HTTP status code. See status-codes in {@link java.net.HttpURLConnection}.
     */
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
