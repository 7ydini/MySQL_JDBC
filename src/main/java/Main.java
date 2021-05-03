import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class Main {
    static String URL = "jdbc:mysql://localhost:3306/StudentDataBase";
    static String USERNAME = "root";
    static String PASSWORD = "root";
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.jdbc.Driver").getDeclaredConstructor().newInstance();
            try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
                System.out.println("Connection to Student DB successfully!");
                conn.close();
            }
        } catch (Exception ex) {
            System.out.println("Connection failed...\n" + ex);
        }
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                DataBaseGUI window = new DataBaseGUI();
            }
        });

    }

    public static void addQuery() {
        String sqlAddCommand = "INSERT INTO Студент (idСтудента, Имя, Фамилия, Отчество, `Дата рождения`, Группа) " +
                "VALUES (?, ?, ?, ?, ?, ?);";
        int year = Calendar.getInstance().get(Calendar.YEAR);

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            preparedStatement = conn.prepareStatement(sqlAddCommand);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        for (int i = 1; i < DataBaseGUI.getTextFields().size() + 1; i++) {//Заполняем знаки вопроса из SQL запроса
                switch (i) {
                    case (1) -> {
                        try {
                        preparedStatement.setInt(i, Integer.parseInt(DataBaseGUI.getTextFields().get(i - 1).getText().strip()));//Добавляем интовую переменную
                            if(Integer.parseInt(DataBaseGUI.getTextFields().get(i-1).getText()) < 1){
                                DataBaseGUI.setWarningLabel("Введите корректный ID студента(ID > 0)");
                            }
                    } catch (NumberFormatException ex){
                            DataBaseGUI.setWarningLabel("Введите корректый ID студента(Целое число).");
                            return;
                        }catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                    case (5) -> {
                        try {
                            LocalDate date = LocalDate.parse(DataBaseGUI.getTextFields().get(i - 1).getText().strip(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));//Вводить дату в GUI нужно в таком формате.
                            if(date.getYear() < 0 || date.getYear() > year){
                                DataBaseGUI.setWarningLabel("Указана не корректная дата!");
                                return;
                            }
                            preparedStatement.setDate(i, Date.valueOf(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));//Добавляем дату
                        }catch (DateTimeParseException ex) {
                            DataBaseGUI.setWarningLabel("Введите корректную дату(dd.MM.yyyy).");
                            return;
                        }catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                    default -> {
                        try {
                            preparedStatement.setString(i, DataBaseGUI.getTextFields().get(i - 1).getText().strip());//Передаем строки

                        } catch (SQLException ex) {
                            ex.printStackTrace();;
                        }
                    }
                }
            }

        try {
            preparedStatement.executeUpdate();//Совершаем запрос на добавление данных.
        } catch (SQLException throwables) {
            DataBaseGUI.setWarningLabel("Введите корректные данные(Проверьте ID)");
            return;
        }
        DataBaseGUI.setWarningLabel("Студент успешно добавлен!");
            DataBaseGUI.getTextFields().forEach(text -> text.setText(""));
            refreshQuery();
        try {
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void deleteQuery() {
        String sqlAddCommand = "DELETE FROM Студент WHERE idСтудента = ?;";
        try {
            int delID = DataBaseGUI.getDeleteID();
            boolean isReal = false;
            if(delID < 1){
                return;
            }
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = conn.prepareStatement(sqlAddCommand);
            ResultSet rs = preparedStatement.executeQuery("SELECT * FROM Студент");//Проверка: есть ли в таблице студент с таким ID
            while (rs.next()){
                if(delID == rs.getInt(1)){
                    isReal = true;
                }
            }
            if(isReal) {
                preparedStatement.setInt(1, DataBaseGUI.getDeleteID());//Передаем параметр ID студента, которого мы хотим удалить.
                preparedStatement.executeUpdate();//Совершаем запрос на удаление.
                DataBaseGUI.setWarningDeleteLabel("Студент с ID: " + DataBaseGUI.getDeleteID() + " успешно удален!");
                DataBaseGUI.setDeleteText("");
                refreshQuery();
                conn.close();
            }else{
                DataBaseGUI.setWarningDeleteLabel("Введите существующие значение ID");
            }
        } catch (SQLException throwables) {
            DataBaseGUI.setWarningDeleteLabel("Не удалось удалить студента.");
            throwables.printStackTrace();
        }
    }

    public static void refreshQuery() {
        try{
            String[][] select = new String[getRowCount()][6];
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM Студент");//Запрос на получение всех данных из таблицы
            int i = 0;
            while (rs.next()) {//Пока в запросе есть строки считываем их
                for (int j = 1; j < DataBaseGUI.getColunCount() + 1; j++) {//Считываем каждый столбец
                    switch (j) {
                        case (1) -> {
                            select[i][j - 1] = String.valueOf(rs.getInt(j)).strip();
                        }
                        case (5) -> {
                            LocalDate date = LocalDate.parse(String.valueOf(rs.getDate(j)).strip(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));//Вводить дату в GUI нужно в таком формате.
                            select[i][j - 1] = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));//Добавляем дату
                            }
                            default -> {
                                select[i][j - 1] = rs.getString(j).strip();
                            }
                        }
                    }
                i++;
                }
                ArrayList<String[]> data = new ArrayList<>(Arrays.asList(select));
                rs.close();
                conn.close();
                DataBaseGUI.createTable(data);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static int getRowCount() throws SQLException {//Считаем кол-во строк в таблице
        Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM Студент");
        int i = 0;
        while (rs.next()) {
            i++;
        }
        conn.close();
        return i;
    }
}