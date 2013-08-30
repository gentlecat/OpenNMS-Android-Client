package org.opennms.android.net;

/**
 * Stores server response.
 */
public class Response {

    private int code;
    private String message;

    /**
     * @param code    HTTP status code.
     * @param message Message returned by server.
     */
    public Response(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
