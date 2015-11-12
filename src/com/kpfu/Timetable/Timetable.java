package com.kpfu.Timetable;

import java.io.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс, содержащий уроки, привязанные ко дням недели
 * @author Лысенков
 */
class Timetable {

    private final int MAX_PER_DAY = 13; // максимальное количество уроков в день + 1
    private final char SPLIT = (char)30; // разделитель. В нормальном тексте встречаться не должен

    /** Массив уроков. Запись вида Lessons[2, 3] означает "третий урок второго дня недели".
     * Дни недели начинаются с понедельника, а не с воскресенья, как в Date по умолчанию!
     */
    private Lesson[][] lessons;
    private Calendar firstDay; // первый день
    private boolean doubleWeek; // признак двухнедельности
    private Map<Calendar, Lesson[]> specialDays; // Особые дни (праздники, например)

    /**
     * Конструктор
     * <br>Первым числом считает либо первое января, либо первое сентября текущего года
     * @param doubleWeek Является ли расписание двухнедельным?
     */
    Timetable(boolean doubleWeek) {
        this.doubleWeek = doubleWeek;
        int days = doubleWeek ? 15 : 8;
        lessons = new Lesson[days][MAX_PER_DAY];
        Calendar now = Calendar.getInstance();
        int month = now.get(Calendar.MONTH) < 9 ? 1 : 9;
        firstDay = new GregorianCalendar(now.get(Calendar.YEAR), month, 1);
        specialDays = new HashMap<Calendar, Lesson[]>();
    }

    /**
     * Конструктор
     * @param doubleWeek Является ли расписание двухнедельным?
     * @param firstDay Первый день. Особенно важен для для двухнедельного расписания, для отчёта чётных/нечётных недель.
     */
    Timetable(boolean doubleWeek, Calendar firstDay) {
        this.doubleWeek = doubleWeek;
        int days = doubleWeek ? 15 : 8;
        lessons = new Lesson[days][MAX_PER_DAY];
        this.firstDay = firstDay;
        specialDays = new HashMap<Calendar, Lesson[]>();
    }

    /**
     * Возвращает уроки за определённое число
     * @param date Дата
     * @return Массив уроков. Если уроков нет, то пустой массив.
     */
    Lesson[] getLessons(Calendar date) {
        if (specialDays.containsKey(date))
            return specialDays.get(date);
        if (date.compareTo(firstDay) < 0)
            return new Lesson[MAX_PER_DAY];
        date.setFirstDayOfWeek(Calendar.MONDAY);
        Lesson[] result;
        if (!doubleWeek || (date.get(Calendar.WEEK_OF_YEAR) - firstDay.get(Calendar.WEEK_OF_YEAR)) / 2 == 0)
            result = lessons[date.get(Calendar.DAY_OF_WEEK) - 1];
        else
            result = lessons[date.get(Calendar.DAY_OF_WEEK) + 6];
        if (result == null) return new Lesson[MAX_PER_DAY];
        return result;
    }

    /**
     * Возвращает уроки для определённого дня недели
     * @param dayOfWeek Номер дня недели. Например, понедельник = 1, среда = 3, понедельник второй недели = 8.
     * @return Массив уроков. Если уроков нет, то пустой массив.
     */
    Lesson[] getLessons(int dayOfWeek) {
        dayOfWeek = doubleWeek ? (dayOfWeek - 1) % 14 + 1 : (dayOfWeek - 1) % 7 + 1;
        Lesson[] result = lessons[dayOfWeek];
        if (result == null) return new Lesson[MAX_PER_DAY];
        return result;
    }

    /**
     * Возвращает конкретный урок конкретного дня
     * @param date Дата
     * @param count Номер урока (от начала дня, начиная с единицы)
     * @return Экземпляр урока
     */
    Lesson getLesson(Calendar date, int count) {
        if (count >= MAX_PER_DAY) return null;
        return getLessons(date)[count];
    }

    /**
     * Возвращает конкретный урок конкретного дня недели
     * @param dayOfWeek Номер дня недели. Например, понедельник = 1, среда = 3, понедельник второй недели = 8.
     * @param count Номер урока (от начала дня, начиная с единицы)
     * @return Экземпляр урока
     */
    Lesson getLesson(int dayOfWeek, int count) {
        if (count >= MAX_PER_DAY) return null;
        dayOfWeek = doubleWeek ? (dayOfWeek - 1) % 14 + 1 : (dayOfWeek - 1) % 7 + 1;
        return getLessons(dayOfWeek)[count];
    }

    /**
     * Устанавливает уроки для определённого дня недели
     * @param lessons Массив объектов типа Timetable.Lesson
     * @param dayOfWeek Номер дня недели. Например, понедельник = 1, среда = 3, понедельник второй недели = 8.
     */
    void setLessons(Lesson[] lessons, int dayOfWeek) {
        dayOfWeek = doubleWeek ? (dayOfWeek - 1) % 14 + 1 : (dayOfWeek - 1) % 7 + 1;
        if (lessons.length == MAX_PER_DAY)
            this.lessons[dayOfWeek] = lessons;
        else {
            Lesson[] align = new Lesson[MAX_PER_DAY];
            System.arraycopy(lessons, 0, align, 0, Math.min(lessons.length, MAX_PER_DAY));
            this.lessons[dayOfWeek] = align;
        }
    }

    /**
     * Устанавливает уроки для определённого дня. Эти уроки будут вместо основных по расписанию.
     * @param lessons Массив объектов типа Timetable.Lesson
     * @param date Дата
     */
    void setLessons(Lesson[] lessons, Calendar date) {
        if (specialDays.containsKey(date))
            specialDays.remove(date);
        if (lessons.length == MAX_PER_DAY)
            specialDays.put(date, lessons);
        else {
            Lesson[] align = new Lesson[MAX_PER_DAY];
            System.arraycopy(lessons, 0, align, 0, Math.min(lessons.length, MAX_PER_DAY));
            specialDays.put(date, align);
        }
    }

    /**
     * Устанавливает некоторый по счёту урок в определённого дня недели.
     * @param lesson Экземпляр объекта Timetable.Lesson
     * @param dayOfWeek Номер дня недели. Например, понедельник = 1, среда = 3, понедельник второй недели = 8.
     * @param count Номер урока (от начала дня, начиная с единицы)
     */
    void setLesson(Lesson lesson, int dayOfWeek, int count) {
        if (count >= MAX_PER_DAY)
            count = MAX_PER_DAY - 1;
        dayOfWeek = doubleWeek ? (dayOfWeek - 1) % 14 + 1 : (dayOfWeek - 1) % 7 + 1;
        this.lessons[dayOfWeek][count] = lesson;
    }

    /**
     * Заменяет один урок для конкретной даты. Остальные уроки в этот день остаются как в расписании.
     * @param lesson Экземпляр объекта Timetable.Lesson
     * @param date Дата
     * @param count Номер урока (от начала дня, начиная с единицы)
     */
    void setLesson(Lesson lesson, Calendar date, int count) {
        if (count >= MAX_PER_DAY)
            count = MAX_PER_DAY - 1;
        Lesson[] from = getLessons(date);
        from[count] = lesson;
        if (specialDays.containsKey(date))
            specialDays.remove(date);
        specialDays.put(date, from);
    }

    /**
     * @return Является ли расписание двухнедельным?
     */
    boolean isDoubled() {
        return doubleWeek;
    }

    /**
     * Назначение дня праздничным. Все уроки в этот день отменяются.
     * @param holiday Дата выходного дня
     */
    void addHoliday(Calendar holiday) {
        if (specialDays.containsKey(holiday))
            specialDays.remove(holiday);
        specialDays.put(holiday, new Lesson[MAX_PER_DAY]);
    }

    /**
     * Назначение дня праздничным. Все уроки переносятся на другой день.
     * @param holiday Дата выходного дня
     * @param shift Дата, на которую переносятся занятия
     */
    void addHoliday(Calendar holiday, Calendar shift) {
        if (specialDays.containsKey(shift))
            specialDays.remove(shift);
        specialDays.put(shift, getLessons(holiday));
        if (specialDays.containsKey(holiday))
            specialDays.remove(holiday);
        specialDays.put(holiday, new Lesson[MAX_PER_DAY]);
    }

    /**
     * Отменяет все ранее назначенные праздники
     * <br>Подумайте о бедных студентах, прежде чем вызывать этот метод
     */
    void flushHolidays() { specialDays.clear(); }

    /**
     * Преобразовывает все записи из некой кодировки в UTF-8
     * @param currentEncoding Текущая кодировка
     * @throws UnsupportedEncodingException Указанной кодировки не существует (или она не поддерживается)
     */
    void fixEncoding(String currentEncoding) throws UnsupportedEncodingException {
        for (Lesson[] day : lessons)
            for (Lesson lesson : day)
                if (lesson != null)
                    lesson.fixEncoding(currentEncoding);
        for (Lesson[] day : specialDays.values())
            for (Lesson lesson : day)
                if (lesson != null)
                    lesson.fixEncoding(currentEncoding);
    }

    /**
     * Сохраняет расписание в файл
     * @param filename Имя файла (расширение не обязательно). Номер группы подойдёт.
     * @return Успешно ли сохранение?
     */
    boolean saveAs(String filename) {
        try {
            FileOutputStream file = new FileOutputStream(filename, false);
            OutputStreamWriter osw = new OutputStreamWriter(file);
            osw.append(doubleWeek ? '1' : '0');
            osw.append(SPLIT);
            osw.append(Integer.toString(firstDay.get(Calendar.DAY_OF_MONTH))).append(SPLIT);
            osw.append(Integer.toString(firstDay.get(Calendar.MONTH))).append(SPLIT);
            osw.append(Integer.toString(firstDay.get(Calendar.YEAR))).append(SPLIT);
            for (Lesson[] day : lessons)
                for (Lesson lesson : day) {
                    lesson.write(osw);
                    osw.append(SPLIT);
                }
            for (Calendar day : specialDays.keySet()) {
                osw.append(Integer.toString(day.get(Calendar.DAY_OF_MONTH))).append(SPLIT);
                osw.append(Integer.toString(firstDay.get(Calendar.MONTH))).append(SPLIT);
                osw.append(Integer.toString(day.get(Calendar.YEAR))).append(SPLIT);
                for (Lesson lesson : specialDays.get(day)) {
                    lesson.write(osw);
                    osw.append(SPLIT);
                }
            }
            osw.flush();
            osw.close();
        } catch (IOException e) {return false;}
        return true;
    }

    /**
     * Загружает расписание из файла
     * @param filename Имя файла (расширение не обязательно)
     * @return Экземпляр расписания
     */
    static Timetable loadFrom(String filename) {
        try {
            boolean doubleWeek;
            FileInputStream file = new FileInputStream(filename);
            InputStreamReader isr = new InputStreamReader(file);
            int c = isr.read();
            if (c == -1) return null;
            switch ((char)c) {
                case '0':
                    doubleWeek = false;
                    break;
                case '1':
                    doubleWeek = true;
                    break;
                default:
                    return null; // Неверный формат файла
            }
            Timetable result = new Timetable(doubleWeek);
            c = isr.read();
            if (c == -1 || (char)c != result.SPLIT) return null; // Неверный формат файла
            Calendar firstDay = dateFromStream(isr, result.SPLIT);
            if (firstDay != null)
                result.firstDay = firstDay;
            for (int i = 0; i < result.lessons.length; i++)
                for (int j = 0; j < result.lessons[0].length; j++)
                    result.lessons[i][j] = Lesson.read(isr, result.SPLIT);
            c = isr.read();
            while (c != -1) {
                Calendar date = dateFromStream(isr, result.SPLIT);
                Lesson[] fullDay = new Lesson[result.MAX_PER_DAY];
                for (int i = 0; i < fullDay.length; i++)
                    fullDay[i] = Lesson.read(isr, result.SPLIT);
                result.specialDays.put(date, fullDay);
                c = isr.read();
            }
            isr.close();
            return result;
        } catch (IOException e) {return null;}
    }

    private static Calendar dateFromStream(InputStreamReader stream, char split) throws IOException {
        String day = "";
        String month = "";
        String year = "";
        int c = (stream.read());
        while (c != -1 && (char)c != split) day += (char)c;
        c = (stream.read());
        while (c != -1 && (char)c != split) month += (char)c;
        c = (stream.read());
        while (c != -1 && (char)c != split) year += (char)c;
        Calendar result;
        try { result = new GregorianCalendar(Integer.parseInt(year),
                Integer.parseInt(month),
                Integer.parseInt(day));
        } catch (NumberFormatException e) {return null;}
        return result;
    }
}
