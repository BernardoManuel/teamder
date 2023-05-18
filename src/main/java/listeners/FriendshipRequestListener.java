package listeners;

import model.Friendship;
import model.User;

public interface FriendshipRequestListener {

    // Implementación del escuchador
    void onRequestReceived(User usuario, Friendship friendRequest);

    // Implementación del escuchador
    void onFriendRequestReceived(User usuario, Friendship friendRequest);
}
