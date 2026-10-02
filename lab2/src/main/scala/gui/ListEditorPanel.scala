package gui

import core.CustomList
import inface.UserTypeInterface
import util.TestDataGenerator
import javax.swing._
import java.awt._
import java.util.function.Consumer

class ListEditorPanel[T](private val `type`: UserTypeInterface[T], private val selection: SelectionPanel) extends JPanel(new BorderLayout()) {
  
  private val customList: CustomList[T] = new CustomList[T]()
  
  // Передаем selection для динамического отслеживания стратегии обхода
  private val iteratorController: ListIteratorController[T] = new ListIteratorController[T](customList, selection)
  
  private val visualizer: ListVisualizer[T] = new ListVisualizer[T](customList, `type`, iteratorController)
  private val output: JTextArea = new JTextArea(4, 40)
  
  private val operations: ListOperationsPanel = new ListOperationsPanel(new Consumer[ListOperationsPanel.Operation]() {
    override def accept(op: ListOperationsPanel.Operation): Unit = {
      execute(new ListEditorPanel.Action() {
        override def run(): Unit = { 
          operate(op)
          iteratorController.validate()
        }
      })
    }
  })
  
  private val algorithms: AlgorithmsPanel = new AlgorithmsPanel(new Consumer[AlgorithmsPanel.Operation]() {
    override def accept(op: AlgorithmsPanel.Operation): Unit = {
      execute(new ListEditorPanel.Action() {
        override def run(): Unit = { 
          algorithm(op)
          iteratorController.validate()
        }
      })
    }
  })
  
  private val blocks: BlockSettingsPanel = new BlockSettingsPanel(customList.getBlockCapacity(), new Runnable() {
    override def run(): Unit = {
      execute(new ListEditorPanel.Action() {
        override def run(): Unit = { regroup() }
      })
    }
  })
  
  private val random: RandomListPanel = new RandomListPanel(new Runnable() {
    override def run(): Unit = {
      execute(new ListEditorPanel.Action() {
        override def run(): Unit = { 
          generateRandomList() 
          iteratorController.reset()
        }
      })
    }
  })

  private val iteratorOperations: IteratorOperationsPanel = new IteratorOperationsPanel(new Consumer[ListIteratorController.Op]() {
    override def accept(op: ListIteratorController.Op): Unit = {
      execute(new ListEditorPanel.Action() {
        override def run(): Unit = { handleIterator(op) }
      })
    }
  })

  {
    visualizer.setTraversal(selection.traversal())
    output.setEditable(false)
    output.setName("output")
    blocks.updateStatus(customList.getSize(), customList.getNodeCount())
    
    try {
      operations.setValue(`type`.serializeValue(`type`.sampleValues().get(0)))
    } catch {
      case ex: Exception => {
        output.setText(ex.getMessage())
      }
    }
    
    val controls = new JPanel()
    controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS))
    controls.add(operations)
    controls.add(algorithms)
    controls.add(blocks)
    controls.add(random)
    controls.add(iteratorOperations)
    
    controls.add(new SerializationPanel(new Consumer[java.lang.Boolean]() {
      override def accept(save: java.lang.Boolean): Unit = {
        execute(new ListEditorPanel.Action() {
          override def run(): Unit = { 
            fileOperation(save.booleanValue()) 
            if (!save.booleanValue()) iteratorController.reset()
          }
        })
      }
    }))
    
    add(controls, BorderLayout.NORTH)
    add(new JScrollPane(visualizer), BorderLayout.CENTER)
    add(new JScrollPane(output), BorderLayout.SOUTH)
  }

  private def handleIterator(op: ListIteratorController.Op): Unit = {
    op match {
      case ListIteratorController.TO_BEGIN => iteratorController.toBegin()
      case ListIteratorController.TO_END   => iteratorController.toEnd()
      case ListIteratorController.NEXT     => iteratorController.next()
      case ListIteratorController.PREV     => iteratorController.prev()
    }
    
    val logIdx = iteratorController.getCurrentLogicalIndex
    if (logIdx == -1) {
      output.setText("Итератор не указывает на элемент (список пуст или сброшен)")
    } else {
      output.setText(s"Итератор на логической позиции: \\$logIdx (Физический индекс: \\${iteratorController.getCurrentPhysicalIndex})")
    }
  }

  private def parsedClone(): T = {
    `type`.`clone`(`type`.parseValue(operations.value()))
  }

  @throws[Exception]
  private def operate(operation: ListOperationsPanel.Operation): Unit = {
    val traversal = selection.traversal[T]()
    if (operation == ListOperationsPanel.APPEND) {
      customList.add(parsedClone(), traversal)
    } else {
      if (operation == ListOperationsPanel.PREPEND) {
        customList.insert(0, parsedClone(), traversal)
      } else {
        if (operation == ListOperationsPanel.INSERT) {
          customList.insert(operations.index(), parsedClone(), traversal)
        } else {
          if (operation == ListOperationsPanel.GET) {
            output.setText(`type`.toString(customList.get(operations.index(), traversal)))
          } else {
            if (operation == ListOperationsPanel.REMOVE) {
              customList.remove(operations.index(), traversal)
            } else {
              if (operation == ListOperationsPanel.CLEAR) {
                customList.clear()
                iteratorController.reset()
              }
            }
          }
        }
      }
    }
    output.setText("Элементов: " + customList.getSize())
  }

  private def algorithm(operation: AlgorithmsPanel.Operation): Unit = {
    if (operation == AlgorithmsPanel.SORT) {
      customList.sort(algorithms.sortStrategy(), `type`.getTypeComparator())
      output.setText("Список отсортирован. Элементов: " + customList.getSize())
    } else {
      if (operation == AlgorithmsPanel.TRAVERSE) {
        val text = new java.lang.StringBuilder()
        selection.traversal[T]().traverse(customList, new inface.ForEachCallbackInterface[T]() {
          override def toDo(v: T): Unit = {
            text.append(`type`.toString(v)).append('\n')
          }
        })
        if (text.length() == 0) {
          output.setText("Список пуст")
        } else {
          output.setText(text.toString())
        }
      } else {
        if (operation == AlgorithmsPanel.FIND) {
          val sought = parsedClone()
          val traversal = selection.traversal[T]()
          val index = customList.firstIndexThat(new java.util.function.Predicate[T]() {
            override def test(v: T): Boolean = {
              `type`.getTypeComparator().compare(v, sought) == 0
            }
          }, traversal)
          
          if (index == -1) {
            output.setText("Не найдено")
          } else {
            output.setText("Логический номер: " + index + "\nЗначение: " + `type`.toString(customList.get(index, traversal)))
          }
        }
      }
    }
  }

  @throws[Exception]
  private def generateRandomList(): Unit = {
    val generated = TestDataGenerator.generateRandomList(`type`, random.maxSize(), customList.getBlockCapacity(), false)
    customList.clear()
    generated.forEach(new inface.ForEachCallbackInterface[T]() {
      override def toDo(v: T): Unit = {
        customList.add(v)
      }
    })
    output.setText("Создан случайный список. Элементов: " + customList.getSize())
  }

  @throws[Exception]
  private def regroup(): Unit = {
    customList.setBlockCapacity(blocks.capacity())
    output.setText("Ссылки перегруппированы по " + customList.getBlockCapacity() + " ячейки в узле. Объекты сохранены.")
  }

  def refreshTraversal(): Unit = {
    visualizer.setTraversal(selection.traversal())
    // При смене порядка обхода логический номер сохраняется, но физическое положение элемента 
    // на экране изменится, поэтому нужно принудительно перерисовать визуализатор.
    visualizer.repaint()
    output.setText("Порядок обхода изменён; логические номера обновлены.")
  }

  @throws[Exception]
  private def fileOperation(save: Boolean): Unit = {
    val strategy = selection.serializer()
    val file = SerializationFileDialog.choose(this, strategy, save)
    if (file == null) {
      // Имитация пустого return
    } else {
      if (save) {
        strategy.save(file.getAbsolutePath(), customList, `type`)
      } else {
        strategy.load(file.getAbsolutePath(), customList, `type`)
      }
      output.setText((if (save) { "Сохранено: " } else { "Загружено: " }) + file.getAbsolutePath() + "\nЭлементов: " + customList.getSize())
    }
  }

  private def execute(action: ListEditorPanel.Action): Unit = {
    try {
      action.run()
      blocks.updateStatus(customList.getSize(), customList.getNodeCount())
      visualizer.revalidate()
      visualizer.repaint()
    } catch {
      case ex: Exception => {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE)
      }
    }
  }
}

object ListEditorPanel {
  trait Action {
    @throws[Exception]
    def run(): Unit
  }
}
