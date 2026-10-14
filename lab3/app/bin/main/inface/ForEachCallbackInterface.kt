package inface

// functional интерфейс гарантирует корректную работу с лямбда-выражениями
fun interface ForEachCallbackInterface<T> {
    fun toDo(v: T)
}
