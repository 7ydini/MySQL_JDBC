import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;


public class DataBaseGUI extends JFrame {
    private static final String[] label = {"ID Студента", "Имя", "Фамилия", "Отчество", "Дата рождения", "Группа"};//Названия наших столбцов
    private static final int column = 6;
    private static final JFrame frame = new JFrame("Студент");
    private static JScrollPane pane;//
    private static final JPanel panel = new JPanel();

    private static final JLabel warningLabel = new JLabel();
    private static final JLabel warningDeleteLabel = new JLabel();

    private static final JTextField deleteTextField = new JTextField(16);

    private final List<JLabel> textLabels = new ArrayList<>();
    private static final List<JTextField> textFields = new ArrayList<>();

    private final JButton addQueryButton = new JButton("Добавить");
    private final JButton deleteQueryButton = new JButton("Удалить");
    private final JButton updateQueryButton = new JButton("Обновить");

    //SETTINGS
    int frameWidth = 1080;//Разрешение фрейма
    int frameHeight = 420;

    DataBaseGUI(){//Конструктор в котором и создаём наш фрейм
        frame.setSize(frameWidth, frameHeight);
        frame.setResizable(false);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameFilling();

        frame.repaint();

        addQueryButton.addActionListener(new AddQuery());
        deleteQueryButton.addActionListener(new DeleteQuery());
        updateQueryButton.addActionListener(new UpdateQuery());
    }

    public static int getColunCount() {
        return column;
    }


    private void frameFilling() {
        int xLabel = 10, xText = 150, y = 10, widthLabel = 140, widthText = 160, height = 20, gap = 10;
        panel.setLayout(null);
        panel.setBounds(0, 0, 310, 360 );

        JLabel titleAdd = new JLabel("Добавить строку в таблицу `Студент`");
        titleAdd.setBounds(xLabel, y, widthLabel + widthText, height);
        panel.add(titleAdd);//Заголовок

        for(int i = 0; i < 6; i++){
            textLabels.add(new JLabel(label[i]));//Добавляем наши вывески
            textFields.add(new JTextField(16));//Добавляем наши поля для ввода
        }
        for (int i = 0; i < textFields.size(); i++) {//Добавляем все поля
            textLabels.get(i).setBounds(xLabel, y += height + gap, widthLabel, height);
            textFields.get(i).setBounds(xText, y, widthText, height);
            panel.add(textLabels.get(i));
            panel.add(textFields.get(i));
        }
        //Добавляем кнопки, поля для удаления студентов и Labels с информацией об ошибке.
        addQueryButton.setBounds(xLabel, y += height + gap, (widthLabel + widthText)/2, height);
        updateQueryButton.setBounds(xLabel + (widthLabel + widthText)/2, y, (widthLabel + widthText)/2, height);
        panel.add(addQueryButton);
        panel.add(updateQueryButton);

        warningLabel.setBounds(xLabel, y += height + gap, widthLabel + widthText, height);
        panel.add(warningLabel);//Оповестительный Label об добавлении/ошибке.

        JLabel deleteLabel = new JLabel("Введите ID Студента, которго хотите удалить:");
        deleteLabel.setBounds(xLabel, (y += height + gap) + gap, widthLabel + widthText, height);
        panel.add(deleteLabel);//Заголовок

        deleteTextField.setBounds(xLabel, (y += height + gap) + gap, (widthLabel + widthText)/2, height);
        panel.add(deleteTextField);//ID студента для удаления

        deleteQueryButton.setBounds(xLabel + (widthLabel + widthText)/2, y + gap, (widthLabel + widthText)/2, height);
        panel.add(deleteQueryButton);//Кнопка для удаления

        warningDeleteLabel.setBounds(xLabel, y += height + gap, widthLabel + widthText, height);
        panel.add(warningDeleteLabel);//Оповещение об удалении/ошибке.

        panel.setVisible(true);
        frame.add(panel);
        Main.refreshQuery();

    }

    private static void createPaneTable(JTable table) {//Создаём нашу панель с таблицей, задаём расположение и добавляем во фрейм
        pane = new JScrollPane(table);
        pane.setBounds(320, 10, 720, 355);
        pane.setVisible(true);
        table.setAutoscrolls(true);
        frame.add(pane);
        frame.repaint();
    }

    private class AddQuery implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            boolean corrected = true;
            for (JTextField text: textFields) {
                if(text.getText().strip().isEmpty()){//Проверка на пустые поля
                    corrected = false;
                }
            }
            if (corrected){
                Main.addQuery();
            }else warningLabel.setText("Заполните все поля!");
        }
    }

    private class DeleteQuery implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (deleteTextField.getText().strip().isEmpty()){
                warningDeleteLabel.setText("Введите корректный ID Студента!");
            }else {
                Main.deleteQuery();
            }
        }
    }

    private class UpdateQuery implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Main.refreshQuery();
            warningLabel.setText("Таблица обновлена!");
        }
    }
    public static void createTable(ArrayList<String[]> select){//Для обновления таблицы мы ее пересоздаем.
        deleteScrollPane();
        DefaultTableModel tableModel = new DefaultTableModel(getLabel(), 0);//
        for (String[] row: select) {
            tableModel.addRow(row);
        }
        JTable table = new JTable(tableModel);
        createPaneTable(table);
    }
    private static void deleteScrollPane() {

        if(pane != null){
            frame.getContentPane().remove(pane);
            pane = null;
        }
    }
    public static String[] getLabel(){
        return label;
    }

    public static List<JTextField> getTextFields(){
        return textFields;
    }

    public static int getDeleteID(){//Будем получать данные нужного формата.
        try {
            return Integer.parseInt(deleteTextField.getText().strip());
        }catch (NumberFormatException ex){
            setWarningDeleteLabel("Введите корректный ID студента!");
            return 0;
        }
    }
    public static void setWarningLabel(String label){
        warningLabel.setText(label);
    }
    public static void setWarningDeleteLabel(String label){
        warningDeleteLabel.setText(label);
    }
    public static void setDeleteText(String s) {
        deleteTextField.setText(s);
    }

}
