package gui

import core.CustomList
import inface.UserTypeInterface
import util.TestDataGenerator
import javax.swing.*
import java.awt.BorderLayout
import java.io.File

class ListEditorPanel<T>(
    private val type: UserTypeInterface<T>, 
    private val selection: SelectionPanel
) : JPanel(BorderLayout()) {
    
    val list = CustomList<T>()
    val visualizer = ListVisualizer(list, type)
    
    private val output = JTextArea(4, 40)
    private val operations: ListOperationsPanel
    private val algorithms: AlgorithmsPanel
    private val blocks: BlockSettingsPanel
    private val random: RandomListPanel

    init {
        visualizer.setTraversal(selection.traversal())
        output.isEditable = false
        output.name = "output"
        
        operations = ListOperationsPanel { operation -> execute { operate(operation) } }
        algorithms = AlgorithmsPanel { operation -> execute { algorithm(operation) } }
        blocks = BlockSettingsPanel(list.blockCapacity) { execute { regroup() } }
        random = RandomListPanel { execute { generateRandomList() } }
        
        blocks.updateStatus(list.size, list.nodeCount)
        
        try {
            operations.setValue(type.serializeValue(type.sampleValues[0]))
        } catch (ex: Exception) {
            output.text = ex.message
        }
        
        val controls = JPanel()
        controls.layout = BoxLayout(controls, BoxLayout.Y_AXIS)
        controls.add(operations)
        controls.add(algorithms)
        controls.add(blocks)
        controls.add(random)
        controls.add(SerializationPanel { save -> execute { fileOperation(save) } })
        
        add(controls, BorderLayout.NORTH)
        add(JScrollPane(visualizer), BorderLayout.CENTER)
        add(JScrollPane(output), BorderLayout.SOUTH)
    }

    private fun parsedClone(): T = type.clone(type.parseValue(operations.value()))

     @Throws(Exception::class)
    private fun operate(operation: ListOperationsPanel.Operation) {
        val traversal = selection.traversal<T>()
        when (operation) {
            ListOperationsPanel.Operation.APPEND -> list.add(parsedClone(), traversal)
            ListOperationsPanel.Operation.PREPEND -> list.insert(0, parsedClone(), traversal)
            ListOperationsPanel.Operation.INSERT -> list.insert(operations.index(), parsedClone(), traversal)
            ListOperationsPanel.Operation.GET -> {
                output.text = type.toString(list.get(operations.index(), traversal))
                return
            }
            ListOperationsPanel.Operation.REMOVE -> list.remove(operations.index(), traversal)
            ListOperationsPanel.Operation.CLEAR -> list.clear()
        }
        output.text = "Элементов: ${list.size}"
    }

    private fun algorithm(operation: Operation) { // Изменено на gui.Operation (top-level из AlgorithmsPanel)
        val traversal = selection.traversal<T>()
        when (operation) {
            Operation.SORT -> {
                list.sort(algorithms.sortStrategy(), type.typeComparator)
                output.text = "Список отсортирован. Элементов: ${list.size}"
            }
            Operation.TRAVERSE -> {
                val text = StringBuilder()
                traversal.traverse(list) { v -> text.append(type.toString(v)).append('\n') }
                output.text = if (text.isEmpty()) "Список пуст" else text.toString()
            }
            Operation.FIND -> {
                val sought = parsedClone()
                val index = list.firstIndexThat({ v -> type.typeComparator.compare(v, sought) == 0 }, traversal)
                output.text = if (index == -1) "Не найдено" else "Логический номер: $index\nЗначение: ${type.toString(list.get(index, traversal))}"
            }
        }
    }

    @Throws(Exception::class)
    private fun generateRandomList() {
        val generated = TestDataGenerator.generateRandomList(type, random.maxSize(), list.blockCapacity, false)
        list.clear()
        generated.forEach(list::add)
        output.text = "Создан случайный список. Элементов: ${list.size}"
    }

    @Throws(Exception::class)
    private fun regroup() {
        list.blockCapacity = blocks.capacity()
        output.text = "Ссылки перегруппированы по ${list.blockCapacity} ячейки в узле. Объекты сохранены."
    }

    fun refreshTraversal() {
        visualizer.setTraversal(selection.traversal())
        output.text = "Порядок обхода изменён; логические номера обновлены."
    }

    @Throws(Exception::class)
    private fun fileOperation(save: Boolean) {
        val strategy = selection.serializer()
        val file = SerializationFileDialog.choose(this, strategy, save) ?: return
        if (save) {
            strategy.save(file.absolutePath, list, type)
        } else {
            strategy.load(file.absolutePath, list, type)
        }
        output.text = (if (save) "Сохранено: " else "Загружено: ") + file.absolutePath + "\\nЭлементов: ${list.size}"
    }

    private fun interface Action {
        @Throws(Exception::class)
        fun run()
    }

    private fun execute(action: Action) {
        try {
            action.run()
            blocks.updateStatus(list.size, list.nodeCount)
            visualizer.revalidate()
            visualizer.repaint()
        } catch (ex: Exception) {
            JOptionPane.showMessageDialog(this, ex.message, "Ошибка", JOptionPane.ERROR_MESSAGE)
        }
    }
}
