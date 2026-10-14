package nstu.rgr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import nstu.rgr.core.CustomList;
import nstu.rgr.gui.ListIteratorController;
import nstu.rgr.gui.ListVisualizerView;
import nstu.rgr.inface.UserTypeInterface;
import nstu.rgr.inface.TraverseStrategyInterface;
import nstu.rgr.factory.UserFactory;
import nstu.rgr.factory.TraverseFactory;
import nstu.rgr.factory.SerializeFactory; // Подключаем фабрику сериализации
import nstu.rgr.util.TestDataGenerator;

public class MainActivity extends AppCompatActivity {

    private androidx.activity.result.ActivityResultLauncher<Intent> fileChooserLauncher;
    private androidx.activity.result.ActivityResultLauncher<Intent> fileSaverLauncher;


    private final CustomList<Object> list = new CustomList<>();
    private UserTypeInterface<Object> currentType;
    private ListIteratorController<Object> iteratorController;

    private android.widget.ImageView ivListRender;

    private final android.graphics.Matrix matrix = new android.graphics.Matrix();
    private final android.graphics.Matrix savedMatrix = new android.graphics.Matrix();
    private final android.graphics.PointF startPoint = new android.graphics.PointF();
    private final android.graphics.PointF midPoint = new android.graphics.PointF();
    private float oldDist = 1f;
    private int touchMode = 0;
    private EditText etValue, etIndex, etCapacity, etRandomMax;
    private TextView tvOutput, tvStatus;
    private Spinner spinnerType, spinnerTraverse, spinnerFormat; // Добавлен spinnerFormat

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Поиск UI компонентов по ID
        ivListRender = findViewById(R.id.iv_list_render);
        setupImageViewTouchGesture(ivListRender);
        etValue = findViewById(R.id.et_value);
        etIndex = findViewById(R.id.et_index);
        etCapacity = findViewById(R.id.et_capacity);
        etRandomMax = findViewById(R.id.et_random_max); // Связываем максимум для рандома
        tvOutput = findViewById(R.id.tv_output);
        tvStatus = findViewById(R.id.tv_status);
        spinnerType = findViewById(R.id.spinner_type);
        spinnerTraverse = findViewById(R.id.spinner_traverse);
        spinnerFormat = findViewById(R.id.spinner_format); // Инициализация спиннера сериализации

        initSelectors();

        // Добавить внутрь метода onCreate() перед initOperations()
        fileChooserLauncher = registerForActivityResult(
                new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                        android.net.Uri uri = result.getData().getData();
                        if (uri != null) {
                            execute(() -> loadFileFromUri(uri));
                        }
                    }
                }
        );

        // Добавить внутрь метода onCreate() рядом с fileChooserLauncher
        fileSaverLauncher = registerForActivityResult(
                new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                        android.net.Uri uri = result.getData().getData();
                        if (uri != null) {
                            execute(() -> saveFileToUri(uri));
                        }
                    }
                }
        );



        initOperations();
        initIterator();

        updateTypeAndTraversal();
    }

    // Вызывается при нажатии на кнопку "Открыть файл"
    private void openFileDialog() throws Exception {
        if (spinnerFormat.getSelectedItem() == null) return;
        String selectedFormat = spinnerFormat.getSelectedItem().toString();
        nstu.rgr.inface.SerializeStrategyInterface strategy = nstu.rgr.factory.SerializeFactory.getStrategyByName(selectedFormat);
        if (strategy == null) throw new Exception("Стратегия сериализации не найдена");

        // Создаем Intent для открытия системного файлового менеджера
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // ДИНАМИЧЕСКИЙ ФИЛЬТР расширений, поддерживаемых выбранной стратегией
        String extension = strategy.fileExtension().toLowerCase(java.util.Locale.ROOT);
        switch (extension) {
            case "json":
                intent.setType("application/json");
                break;
            case "txt":
                intent.setType("text/plain");
                break;
            case "bin":
            case "dat":
                intent.setType("application/octet-stream"); // Для бинарных файлов
                break;
            default:
                intent.setType("*/*"); // Общий фильтр для любых файлов
                break;
        }

        // Запускаем диалог
        fileChooserLauncher.launch(Intent.createChooser(intent, "Выберите файл " + strategy.formatName()));
    }

    // Вызывается автоматически, когда пользователь ткнул на файл в диалоге
    private void loadFileFromUri(android.net.Uri uri) throws Exception {
        String selectedFormat = spinnerFormat.getSelectedItem().toString();
        nstu.rgr.inface.SerializeStrategyInterface strategy = nstu.rgr.factory.SerializeFactory.getStrategyByName(selectedFormat);

        // Чтобы старые java.nio.file методы внутри вашей JsonSerializeStrategy смогли прочитать файл,
        // мы создаем временную физическую копию файла во внутреннем кэше приложения
        java.io.File tempFile = new java.io.File(getCacheDir(), "temp_import." + strategy.fileExtension());

        // Потоковое копирование данных из Uri во временный файл
        try (java.io.InputStream inputStream = getContentResolver().openInputStream(uri);
             java.io.FileOutputStream outputStream = new java.io.FileOutputStream(tempFile)) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        // Передаем путь к временному файлу в вашу готовую бизнес-логику load
        String absolutePath = tempFile.getAbsolutePath();
        strategy.load(absolutePath, list, currentType);

        if (iteratorController != null) iteratorController.reset();

        // Удаляем временный файл за собой
        tempFile.delete();

        tvOutput.setText("Успешно загружено внешнее хранилище\nЭлементов: " + list.getSize());
    }


    @SuppressWarnings("unchecked")
    private void initSelectors() {
        // 1. Селектор типов данных
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, UserFactory.getTypeNameList());
        spinnerType.setAdapter(typeAdapter);

        // 2. Селектор обходов
        ArrayAdapter<String> traverseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, TraverseFactory.getStrategyNameList());
        spinnerTraverse.setAdapter(traverseAdapter);

        // 3. ВОССТАНОВЛЕНО: Селектор форматов сериализации (теперь данные берутся из вашей фабрики)
        ArrayAdapter<String> formatAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, SerializeFactory.getFormatNameList());
        spinnerFormat.setAdapter(formatAdapter);

        // Слушатели изменений
        spinnerType.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> p, android.view.View v, int pos, long id) {
                updateTypeAndTraversal();
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> p) {}
        });

        spinnerTraverse.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> p, android.view.View v, int pos, long id) {
                if (iteratorController != null) iteratorController.reset();
                updateVisualizer();
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> p) {}
        });
    }

    @SuppressWarnings("unchecked")
    private void updateTypeAndTraversal() {
        if (spinnerType.getSelectedItem() == null) return;
        list.clear();

        String selectedTypeName = spinnerType.getSelectedItem().toString();
        currentType = (UserTypeInterface<Object>) UserFactory.getBuilderByName(selectedTypeName);
        // Сбрасываем контроллер итератора
        if (iteratorController != null) {
            iteratorController.reset();
        } else {
            iteratorController = new ListIteratorController<>(list, null);
        }

        try {
            // Устанавливаем дефолтное тестовое значение для нового типа в поле ввода
            etValue.setText(currentType.serializeValue(currentType.sampleValues().get(0)));
        } catch (Exception e) {
            tvOutput.setText(e.getMessage());
        }

        // Выводим информацию в консоль приложения
        tvOutput.setText("Тип данных изменен на: " + selectedTypeName + ". Структура очищена.");

        updateVisualizer();
    }



    private void initOperations() {
        findViewById(R.id.btn_append).setOnClickListener(v -> execute(() -> {
            list.add(getParsedValue(), getSelectedTraversal());
            tvOutput.setText("Элементов: " + list.getSize());
        }));

        findViewById(R.id.btn_prepend).setOnClickListener(v -> execute(() -> {
            list.insert(0, getParsedValue(), getSelectedTraversal());
            tvOutput.setText("Элементов: " + list.getSize());
        }));

        findViewById(R.id.btn_insert).setOnClickListener(v -> execute(() -> {
            int idx = Integer.parseInt(etIndex.getText().toString());
            list.insert(idx, getParsedValue(), getSelectedTraversal());
            tvOutput.setText("Элементов: " + list.getSize());
        }));

        findViewById(R.id.btn_get).setOnClickListener(v -> execute(() -> {
            int idx = Integer.parseInt(etIndex.getText().toString());
            Object result = list.get(idx, getSelectedTraversal());
            tvOutput.setText(currentType.toString(result));
        }));

        findViewById(R.id.btn_remove).setOnClickListener(v -> execute(() -> {
            int idx = Integer.parseInt(etIndex.getText().toString());
            list.remove(idx, getSelectedTraversal());
            tvOutput.setText("Элементов: " + list.getSize());
        }));

        findViewById(R.id.btn_clear).setOnClickListener(v -> execute(() -> {
            list.clear();
            if (iteratorController != null) iteratorController.reset();
            tvOutput.setText("Список очищен");
        }));

        findViewById(R.id.btn_regroup).setOnClickListener(v -> execute(() -> {
            int cap = Integer.parseInt(etCapacity.getText().toString());
            list.setBlockCapacity(cap);
            tvOutput.setText("Ссылки перегруппированы по " + list.getBlockCapacity() + " ячеек в узле.");
        }));

        // ВОССТАНОВЛЕНО: Логика кнопки "Случайный список" (Замена элементов через TestDataGenerator)
        findViewById(R.id.btn_random_generate).setOnClickListener(v -> execute(() -> {
            int max = Integer.parseInt(etRandomMax.getText().toString());
            var generated = TestDataGenerator.generateRandomList(currentType, max, list.getBlockCapacity(), false);
            list.clear();
            if (iteratorController != null) iteratorController.reset();
            // Кастуем и добавляем сгенерированные элементы в список
            for (Object item : generated.toArrayList()) {
                list.add(item);
            }
            tvOutput.setText("Создан случайный список. Элементов: " + list.getSize());
        }));

        // Найдите в initOperations() старую кнопку btn_save и замените на:
        findViewById(R.id.btn_save).setOnClickListener(v -> execute(this::saveFileDialog));
        // Найдите в initOperations() старую кнопку btn_load и замените на:
        findViewById(R.id.btn_load).setOnClickListener(v -> execute(this::openFileDialog));

    }

    // Вызывается при нажатии на кнопку "Сохранить файл"
    private void saveFileDialog() throws Exception {
        if (spinnerFormat.getSelectedItem() == null) return;
        String selectedFormat = spinnerFormat.getSelectedItem().toString();
        nstu.rgr.inface.SerializeStrategyInterface strategy = nstu.rgr.factory.SerializeFactory.getStrategyByName(selectedFormat);
        if (strategy == null) throw new Exception("Стратегия сериализации не найдена");

        // Создаем Intent для создания нового документа
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Подставляем имя по умолчанию и MIME-тип под выбранную стратегию
        String extension = strategy.fileExtension().toLowerCase(java.util.Locale.ROOT);
        intent.putExtra(Intent.EXTRA_TITLE, "list." + extension);

        switch (extension) {
            case "json":
                intent.setType("application/json");
                break;
            case "txt":
                intent.setType("text/plain");
                break;
            case "bin":
            case "dat":
                intent.setType("application/octet-stream");
                break;
            default:
                intent.setType("*/*");
                break;
        }

        // Запускаем системный диалог выбора места сохранения
        fileSaverLauncher.launch(Intent.createChooser(intent, "Сохранить как..."));
    }

    // Вызывается автоматически, когда пользователь выбрал папку и нажал "Сохранить"
    private void saveFileToUri(android.net.Uri uri) throws Exception {
        String selectedFormat = spinnerFormat.getSelectedItem().toString();
        nstu.rgr.inface.SerializeStrategyInterface strategy = nstu.rgr.factory.SerializeFactory.getStrategyByName(selectedFormat);

        // Создаем временный файл во внутреннем кэше приложения, чтобы скормить его путь
        // вашей стратегии, использующей java.nio.file.Path
        java.io.File tempFile = new java.io.File(getCacheDir(), "temp_export." + strategy.fileExtension());
        String absolutePath = tempFile.getAbsolutePath();

        // Вызываем вашу отлаженную бизнес-логику сохранения во временный файл
        strategy.save(absolutePath, list, currentType);

        // ИСПРАВЛЕНО: передаем режим "wt" (Write + Truncate), чтобы Android сам очищал файл при перезаписи.
        // Это полностью избавляет нас от необходимости вызывать несуществующие методы очистки дескриптора.
        try (java.io.FileInputStream inputStream = new java.io.FileInputStream(tempFile);
             android.os.ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "wt");
             java.io.FileOutputStream outputStream = new java.io.FileOutputStream(pfd.getFileDescriptor())) {

            // Буфер для потокового переноса байт
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        // Удаляем временный файл за собой
        if (tempFile.exists()) {
            tempFile.delete();
        }

        tvOutput.setText("Файл успешно сохранен на устройство через системный диалог!");
    }




    private void fileOperation(boolean save) throws Exception {
        if (spinnerFormat.getSelectedItem() == null) return;
        String selectedFormat = spinnerFormat.getSelectedItem().toString();
        nstu.rgr.inface.SerializeStrategyInterface strategy = SerializeFactory.getStrategyByName(selectedFormat);

        if (strategy == null) throw new Exception("Стратегия сериализации не найдена");

        java.io.File directory = getFilesDir(); // Внутреннее хранилище Android папки приложения
        java.io.File file = new java.io.File(directory, "list." + strategy.fileExtension());
        String absolutePath = file.getAbsolutePath();

        if (save) {
            strategy.save(absolutePath, list, currentType);
            tvOutput.setText("Сохранено в файл: " + absolutePath + "\nЭлементов: " + list.getSize());
        } else {
            strategy.load(absolutePath, list, currentType);
            if (iteratorController != null) iteratorController.reset();
            tvOutput.setText("Загружено из файла: " + absolutePath + "\nЭлементов: " + list.getSize());
        }
    }

    private void initIterator() {
        findViewById(R.id.btn_iter_begin).setOnClickListener(v -> { if(iteratorController != null) { iteratorController.toBegin(); updateVisualizer(); } });
        findViewById(R.id.btn_iter_prev).setOnClickListener(v -> { if(iteratorController != null) { iteratorController.prev(); updateVisualizer(); } });
        findViewById(R.id.btn_iter_next).setOnClickListener(v -> { if(iteratorController != null) { iteratorController.next(); updateVisualizer(); } });
        findViewById(R.id.btn_iter_end).setOnClickListener(v -> { if(iteratorController != null) { iteratorController.toEnd(); updateVisualizer(); } });
    }

    private Object getParsedValue() {
        String valStr = etValue.getText().toString();
        return currentType.clone(currentType.parseValue(valStr));
    }

    @SuppressWarnings("unchecked")
    private TraverseStrategyInterface<Object> getSelectedTraversal() {
        String selectedTraverseName = spinnerTraverse.getSelectedItem().toString();
        return TraverseFactory.getStrategyByName(selectedTraverseName);
    }

    private void updateVisualizer() {
        if (ivListRender != null && spinnerTraverse.getSelectedItem() != null) {
            // Просим класс сгенерировать текущий кадр структуры данных в Bitmap
            Bitmap frame = ListVisualizerView.generateRenderFrame(list, currentType, getSelectedTraversal(), iteratorController);
            ivListRender.setImageBitmap(frame);

            tvStatus.setText("Объектов: " + list.getSize() + "   Узлов-массивов: " + list.getNodeCount());
        }
    }


    private void execute(RunnableAction action) {
        try {
            action.run();
            updateVisualizer();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка диска: " + e.getMessage(), Toast.LENGTH_LONG).show();
            tvOutput.setText("Ошибка диска: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
            tvOutput.setText(e.getMessage());
        }
    }

    interface RunnableAction { void run() throws Exception; }

    @android.annotation.SuppressLint("ClickableViewAccessibility")
    private void setupImageViewTouchGesture(android.widget.ImageView view) {
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    savedMatrix.set(matrix);
                    startPoint.set(event.getX(), event.getY());
                    touchMode = 1; // Режим перемещения (Drag)
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = calculateDistance(event);
                    if (oldDist > 10f) {
                        savedMatrix.set(matrix);
                        calculateMidPoint(midPoint, event);
                        touchMode = 2; // Режим масштабирования (Zoom)
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    touchMode = 0;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (touchMode == 1) { // Скролл во все стороны пальцем
                        matrix.set(savedMatrix);
                        matrix.postTranslate(event.getX() - startPoint.x, event.getY() - startPoint.y);
                    } else if (touchMode == 2) { // Зум щипком
                        float newDist = calculateDistance(event);
                        if (newDist > 10f) {
                            matrix.set(savedMatrix);
                            float scale = newDist / oldDist;
                            matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                        }
                    }
                    break;
            }
            view.setImageMatrix(matrix); // Применяем изменения к кадру списка
            return true;
        });
    }

    private float calculateDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void calculateMidPoint(android.graphics.PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

}