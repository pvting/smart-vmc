package vmc.machine.core;

/**
 * <b>Create Date:</b> 9/6/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public interface VMCCallBack<S, E> {

    void onSuccess(S success);

    void onError(E error);
}
