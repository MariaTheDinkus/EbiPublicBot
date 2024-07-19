package to.tinypota.ebipublicbot.api;

@FunctionalInterface
public interface BiConsumer<T, U> {
    void accept(T t, U u) throws Exception;
}