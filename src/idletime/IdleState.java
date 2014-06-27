package idletime;

/**
 * Created by Kyly on 6/26/2014.
 */
public class IdleState {

    private State userState;

    public IdleState() {
        userState = State.UNKNOWN;
    }

    public IdleState(State st) {
        userState = st;
    }

    public State getUserSate() {
        return userState;
    }

    public void setUserSate(State userState) {
        this.userState = userState;
    }

    @Override
    public String toString() {
        return userState.toString();
    }
}
