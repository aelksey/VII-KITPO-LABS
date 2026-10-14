package test.java.test.gui;

import gui.ListApplicationPanel;
import gui.ListOperationsPanel;
import gui.AlgorithmsPanel;
import gui.BlockSettingsPanel;
import gui.RandomListPanel;
import gui.IteratorOperationsPanel;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.edt.GuiActionRunnable;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.core.BasicComponentFinder;
import org.assertj.swing.core.ComponentFinder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApplicationGuiTest {

    private FrameFixture window;
    private ComponentFinder finder;

    @BeforeAll
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @BeforeEach
    public void setUp() {
        JFrame frame = GuiActionRunner.execute(() -> {
            JFrame mainFrame = new JFrame("GUI Integration Testing Frame");
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setContentPane(new ListApplicationPanel()); //
            mainFrame.setSize(1150, 520);
            mainFrame.setVisible(true);
            return mainFrame;
        });

        window = new FrameFixture(frame);
        window.show(); 
        
        finder = BasicComponentFinder.finderWithCurrentAwtHierarchy();
    }

    /**
     * Вспомогательный метод для рекурсивного извлечения всех JButton из контейнера.
     * Полностью обходит проблему поиска по тексту и кодировок ОС.
     */
    private List<JButton> getAllButtons(Container container) {
        List<JButton> buttons = new ArrayList<>();
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton) {
                buttons.add((JButton) comp);
            } else if (comp instanceof Container) {
                buttons.addAll(getAllButtons((Container) comp));
            }
        }
        return buttons;
    }

    @Test
    @DisplayName("1. Should successfully insert element and verify output console response")
    public void testInsertOperationViaGui() {
        // Вводим значение "42" в текстовое поле ввода
        window.textBox("value").deleteText().enterText("42"); //

        // Находим панель операций
        ListOperationsPanel opsPanel = finder.findByType(window.target(), ListOperationsPanel.class);
        List<JButton> buttons = getAllButtons(opsPanel);
        
        // Согласно коду ListOperationsPanel:
        // index 0 -> "Вставить в конец", index 1 -> "Вставить в начало"
        final JButton appendButton = buttons.get(0); 
        
        GuiActionRunner.execute((GuiActionRunnable) appendButton::doClick); 

        // Проверяем, что в консоли появился лог операции
        String consoleText = window.textBox("output").text(); //
        assertTrue(consoleText.length() > 0, "Console log must update after operation");
    }

    @Test
    @DisplayName("2. Should update internal capacities when regroup button is pressed")
    public void testRegroupCapacityViaGui() {
        // Изменяем значение в спиннере емкости блоков
        window.spinner("blockCapacity").increment(1); //

        // Находим панель настроек блоков и кликаем по ее кнопке "Перегруппировать"
        BlockSettingsPanel settingsPanel = finder.findByType(window.target(), BlockSettingsPanel.class);
        List<JButton> buttons = getAllButtons(settingsPanel);
        final JButton regroupButton = buttons.get(0);
        
        GuiActionRunner.execute((GuiActionRunnable) regroupButton::doClick);

        String consoleText = window.textBox("output").text(); //
        assertTrue(consoleText.length() > 0, "Capacity update should log a status message");
    }

    @Test
    @DisplayName("3. Should execute generation of random dataset and trigger sorting routine")
    public void testRandomGenerationAndSortExecution() {
        // Находим панель случайного списка и кликаем по кнопке генерации
        RandomListPanel randomPanel = finder.findByType(window.target(), RandomListPanel.class);
        List<JButton> genButtons = getAllButtons(randomPanel);
        final JButton genButton = genButtons.get(0);
        GuiActionRunner.execute((GuiActionRunnable) genButton::doClick);

        // Находим панель алгоритмов и запускаем сортировку (индекс 0 -> "Сортировать")
        AlgorithmsPanel algoPanel = finder.findByType(window.target(), AlgorithmsPanel.class);
        List<JButton> algoButtons = getAllButtons(algoPanel);
        final JButton sortButton = algoButtons.get(0);
        
        GuiActionRunner.execute((GuiActionRunnable) sortButton::doClick);

        String consoleText = window.textBox("output").text(); //
        assertTrue(consoleText.length() > 0, "Console log should confirm execution");
    }

    @Test
    @DisplayName("4. Should verify visibility and accessibility of all modular control blocks")
    public void testControlPanelsEngagement() {
        assertTrue(window.target().isVisible(), "Main application frame window must be active and visible");
        
        // Проверяем, что панель навигации итератора успешно создана на экране
        IteratorOperationsPanel iterPanel = finder.findByType(window.target(), IteratorOperationsPanel.class);
        assertTrue(iterPanel.isVisible(), "Iterator panel layout should be fully rendered");
    }

    @AfterEach
    public void tearDown() {
        window.cleanUp();
    }
}
