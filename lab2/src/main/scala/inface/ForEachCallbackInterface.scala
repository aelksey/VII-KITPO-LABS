package inface

// Call-back интерфейс для действий с объектами коллекции
trait ForEachCallbackInterface[T]{def toDo(v: T): Unit}
