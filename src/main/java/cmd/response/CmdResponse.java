package cmd.response;

public class CmdResponse<T> {
    public final T payload;
    public final int status;
    public final String message;

    public CmdResponse(final T payload, final int status, final String message) {
        this.payload = payload;
        this.status = status;
        this.message = message;
    }
}
