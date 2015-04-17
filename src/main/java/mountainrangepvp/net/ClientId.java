package mountainrangepvp.net;

/**
 * Unique identifiers for connected clients.
 */
public class ClientId {
    public final long id;

    public ClientId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "@(" + id + ')';
    }
}
