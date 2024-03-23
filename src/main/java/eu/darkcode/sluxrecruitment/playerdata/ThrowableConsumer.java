package eu.darkcode.sluxrecruitment.playerdata;

public interface ThrowableConsumer<T> {
    void accept(T t) throws Throwable;
}
