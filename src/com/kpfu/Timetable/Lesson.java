package com.kpfu.Timetable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;

/**
 * Структура, содержащая данные о конкретном уроке
 */
class Lesson {
    /** Развёрнутое название урока */
    String fullName = null;
    /** Сокращённое название урока */
    String shortName = null;
    /** Здание */
    String building = null;
    /** Аудитория */
    String room = null;
    /** Преподаватель */
    String teacher = null;
    /** Тип урока (лекция/семинар/лабораторная) */
    String type = null;
    /** Любая дополнительная инфа (отметка о контрольной, коротком дне, д/з, напоминалка и т. п.) */
    String comment = null;

    private final char split = (char)31; // разделитель. В нормальном тексте встречаться не должен

    /**
     * Пустой конструктор. Объект занимает минимум памяти, все поля равны null.
     */
    Lesson() { }

    /**
     * Конструктор. Все поля, кроме fullName будут равны null.
     * @param fullName Развёрнутое название урока
     */
    Lesson(String fullName) { this.fullName = fullName; }

    /**
     * Полный конструктор. Если хотите пропустить какое-то поле, вводите null.
     * @param fullName Развёрнутое название урока
     * @param shortName Сокращённое название урока
     * @param building Здание
     * @param room Аудитория
     * @param teacher Преподаватель
     * @param type Тип урока (лекция/семинар/лабораторная)
     * @param comment Любая дополнительная инфа (отметка о контрольной, коротком дне, д/з, напоминалка и т. п.)
     */
    Lesson(String fullName, String shortName, String building, String room, String teacher, String type, String comment) {
        this.fullName = fullName;
        this.shortName = shortName;
        this.building = building;
        this.room = room;
        this.teacher = teacher;
        this.type = type;
        this.comment = comment;
    }

    /**
     * Преобразовывает все записи из некой кодировки в UTF-8
     * @param currentEncoding Текущая кодировка
     * @throws UnsupportedEncodingException Указанной кодировки не существует (или она не поддерживается)
     */
    void fixEncoding(String currentEncoding) throws UnsupportedEncodingException {
        for (Field field : this.getClass().getFields())
            if (field.getType().getSimpleName().equals("String")) try {
                String value = (String)field.get(this);
                value = new String(value.getBytes(currentEncoding), "UTF-8");
                field.set(this, value);
            } catch (IllegalAccessException e) { } // Ну коли так, то ничего не трогать
    }

    /**
     * Записывает поля в поток. Предполагается, что поток уже открыт.
     * @param stream Указатель на поток
     * @throws IOException Ошибка ввода/вывода (либо поток не создан, либо файл недоступен)
     */
    void write(OutputStreamWriter stream) throws IOException {
        // Ввод руками ради сохранения точной последовательности
        if (fullName != null) stream.append(fullName).append(split);
        else stream.append(split);
        if (shortName != null) stream.append(shortName).append(split);
        else stream.append(split);
        if (building != null) stream.append(building).append(split);
        else stream.append(split);
        if (room != null) stream.append(room).append(split);
        else stream.append(split);
        if (teacher != null) stream.append(teacher).append(split);
        else stream.append(split);
        if (type != null) stream.append(type).append(split);
        else stream.append(split);
        if (comment != null) stream.append(comment).append(split);
        else stream.append(split);
    }

    /**
     * Считывает поля из потока. Предполагается, что поток уже открыт.
     * @param stream Указатель на поток
     * @param outerSplit Разделитель записей
     * @throws IOException Ошибка ввода/вывода (либо поток не создан, либо файл недоступен)
     */
    static Lesson read(InputStreamReader stream, char outerSplit) throws IOException {
        Lesson result = new Lesson();
        int field = 0;
        int c = stream.read();
        while (c != -1 && (char)c != outerSplit) {
            if ((char)c == result.split) field++;
            else {
                switch (field) {
                    case 0:
                        if (result.fullName == null) result.fullName = "";
                        result.fullName += (char)c;
                        break;
                    case 1:
                        if (result.shortName == null) result.shortName = "";
                        result.shortName += (char)c;
                        break;
                    case 2:
                        if (result.building == null) result.building = "";
                        result.building += (char)c;
                        break;
                    case 3:
                        if (result.room == null) result.room = "";
                        result.room += (char)c;
                        break;
                    case 4:
                        if (result.teacher == null) result.teacher = "";
                        result.teacher += (char)c;
                        break;
                    case 5:
                        if (result.type == null) result.type = "";
                        result.type += (char)c;
                        break;
                    case 6:
                        if (result.comment == null) result.comment = "";
                        result.comment += (char)c;
                        break;
                }
            }
            c = stream.read();
        }
        return result;
    }
}
