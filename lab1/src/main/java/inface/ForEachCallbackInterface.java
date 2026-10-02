package inface;

// Аннотация гарантирует корректную работу с лямбда-выражениями в Java и Scala
@FunctionalInterface
public interface ForEachCallbackInterface<T> {
    void toDo(T v);
}
