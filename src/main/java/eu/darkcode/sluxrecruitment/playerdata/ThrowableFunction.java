package eu.darkcode.sluxrecruitment.playerdata;

public interface ThrowableFunction<T, R> {
    R apply(T t) throws Throwable;
}
