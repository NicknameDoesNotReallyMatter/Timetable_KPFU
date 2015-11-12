package com.kpfu.Timetable;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Основная деятельность - здесь отображается расписание
 */
public class MainActivity extends Activity {

    private String group = null; // Номер группы, расписание которой должно быть отражено
    private Timetable timetable = null; // Собственно расписание

    final int LOWER_SHOW_LIMIT = -7; // Сколько записей до сегодняшнего дня отображать при загрузке
    final int UPPER_SHOW_LIMIT = 14; // Сколько записей после сегодняшнего дня отображать при загрузке
    final int SHOW_PORTION = 7; // По сколько записей загружать, когда пользователь доскроллил до границы

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        SharedPreferences settings = getSharedPreferences(getString(R.string.settings_filename), MODE_PRIVATE);
        group = settings.getString("group", getString(R.string.default_group));
        if (!group.equals(getString(R.string.default_group))) {
            timetable = Timetable.loadFrom(group); // загрузка из файла
            if (timetable == null) { // если из файла загрузить не удалось...
                // TODO: Здесь будет загрузка расписания из интернета
            }
        }
        timetable = DebugExample(); // берём расписание из ниоткуда
        if (timetable != null) {
            findViewById(R.id.textViewNoData).setVisibility(View.GONE); // Убираем заглушку
            findViewById(R.id.scrollViewMain).setVisibility(View.VISIBLE); // Показываем расписание
            TableLayout table = (TableLayout)findViewById(R.id.tableLayoutMain);
            Calendar date = Calendar.getInstance();
            date.add(Calendar.DAY_OF_YEAR, LOWER_SHOW_LIMIT);
            for (int i = LOWER_SHOW_LIMIT; i <= UPPER_SHOW_LIMIT; i++) { // В цикле добавляем дни
                addDayRow(table, date);
                date.add(Calendar.DAY_OF_YEAR, 1);
            }
        }
        else {
            findViewById(R.id.scrollViewMain).setVisibility(View.GONE); // Скрываем расписание
            findViewById(R.id.textViewNoData).setVisibility(View.VISIBLE); // Ставим заглушку
        }
        // TODO: загрузить из настроек время последней загрузки этого расписания и, если пришёл срок, в фоновом потоке обновить его
    }

    /**
     * Добавление элемента "строка таблицы про один день из расписания"
     * @param parent Элемент, в который нужно вложить эту строку
     * @param date Дата
     */
    private void addDayRow(ViewGroup parent, Calendar date) {
        TableRow day = (TableRow)getLayoutInflater().inflate(R.layout.day, parent, false); // берём заготовку "день",..
        String dateText = Integer.toString(date.get(Calendar.DAY_OF_MONTH)) + ".";
        dateText += Integer.toString(date.get(Calendar.MONTH)) + ".";
        dateText += Integer.toString(date.get(Calendar.YEAR));
        TextView dateView = (TextView)day.findViewById(R.id.textViewDate);
        dateView.setText(dateText); // меняем текст заголовка строки,..
        int count = 1;
        TableLayout table = (TableLayout) day.findViewById(R.id.tableLayoutLessons);
        for (Lesson lesson : timetable.getLessons(date)) { // вставляем (или не вставляем) заготовку "урок" для каждого урока,..
            if (lesson != null && lesson.fullName != null) {
                TableRow lessonRow = (TableRow) getLayoutInflater().inflate(R.layout.lesson, table, false);
                TextView lessonTime = (TextView) lessonRow.findViewById(R.id.textViewTime);
                TextView lessonName = (TextView) lessonRow.findViewById(R.id.textViewFullName);
                lessonTime.setText(Integer.toString(count)); // внутри меняем заголовок про время,...
                lessonName.setText(lesson.fullName); // и название урока,..
                table.addView(lessonRow); // собственно вставляем урок в день,..
            }
            count++;
        }
        if (table.getChildCount() > 0)
            parent.addView(day); // а день вставляем в расписание (если в нём есть хоть один урок).
    }

    /**
     * Функция для отладки. Берёт расписание из ниоткуда.
     */
    private Timetable DebugExample() {
        Timetable ex = new Timetable(true);
        Lesson tatar = new Lesson("Татарский язык", "Тат.яз.", "УЛК-2", "415", "Магадиева Г. Ф.", "семинар", null);
        Lesson web = new Lesson("Web-программирование", "Web", "УЛК-2", "413", "Галиуллин Л. А.", "лабораторная", null);
        Lesson psycho = new Lesson("Психология", "Псих.", "УЛК-1", "414", "Бурганова Н. Т.", "лекция", null);
        Lesson psycho2 = new Lesson("Психология", "Псих.", "УЛК-1", "456", "Бурганова Н. Т.", "семинар", null);
        Lesson cult = new Lesson("Физическая культура", "Физ-ра", "УЛК-5", null, "Садыкова Г. С.", null, null);
        Lesson terver = new Lesson("Теория вероятностей и математическая статистика", "Тер.вер.", "УЛК-2", "410", "Соловьёва С. А.", "лекция", null);
        Lesson terver2 = new Lesson("Теория вероятностей и математическая статистика", "Тер.вер.", "УЛК-2", "410", "Соловьёва С. А.", "семинар", null);
        Lesson parr = new Lesson("Параллельные вычисления", "Пар.выч.", "УЛК-2", "405", "Каримов Т. Н.", "лабораторная", null);
        Lesson ECM = new Lesson("ЭВМ и периферийные устройства", "ЭВМ", "УЛК-2", "410", "Тазмеев А. Х.", "лекция", null);
        Lesson ECM2 = new Lesson("ЭВМ и периферийные устройства", "ЭВМ", "УЛК-2", "418", "Тазмеев А. Х.", "лабораторная", null);
        Lesson asm = new Lesson("Программирование на языке низкого уровня", "Ассемблер", "УЛК-2", "405", "Хузятов Ш. Ш.", "лекция", null);
        Lesson asm2 = new Lesson("Программирование на языке низкого уровня", "Ассемблер", "УЛК-2", "410", "Хузятов Ш. Ш.", "лабораторная", null);
        Lesson BCT = new Lesson("Основы теории управления", "ОТУ", "УЛК-2", "410", "Зубков Е. В.", "лекция", null);
        Lesson BCT2 = new Lesson("Основы теории управления", "ОТУ", "УЛК-2", "415", "Зубков Е. В.", "семинар", null);
        Lesson BD = new Lesson("Базы данных", "БД", "УЛК-2", "405", "Хузятов Ш. Ш.", "лекция", null);
        Lesson BD2 = new Lesson("Базы данных", "БД", "УЛК-2", "410", "Хузятов Ш. Ш.", "лабораторная", null);

        ex.setLessons(new Lesson[]{tatar, web, web, psycho}, 1);
        ex.setLessons(new Lesson[]{null, null, cult, terver, terver2}, 2);
        ex.setLessons(new Lesson[]{null, parr, ECM, ECM2}, 3);
        ex.setLessons(new Lesson[]{asm, asm2, BCT2}, 4);
        ex.setLessons(new Lesson[]{BD, BD2, cult}, 5);

        ex.setLessons(new Lesson[]{tatar, web, web, psycho2}, 8);
        ex.setLessons(new Lesson[]{null, null, cult, terver}, 9);
        ex.setLessons(new Lesson[]{null, parr, ECM, ECM2}, 10);
        ex.setLessons(new Lesson[]{asm, BCT, BCT}, 11);
        ex.setLessons(new Lesson[]{BD, BD2, cult}, 12);

        return ex;
    }
}
