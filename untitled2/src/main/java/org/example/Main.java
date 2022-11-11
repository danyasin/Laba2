package org.example;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Синицин Данила
 */

public class Main {
    public static void main(String[] args) {
        String expressionText = "(6-3)*8+3";
        List<Elem> elemes = calculation(expressionText);
        WorkWithExpress lexemeBuffer = new WorkWithExpress(elemes);
        System.out.println(expr(lexemeBuffer));
    }

    /**
     * Здесь описаны типы элементов строки
     */
    public enum ElemType {
        l_brack, r_brack,
        plus, minus, mul, div,
        num,
        end
    }
    /**
     * Elem - представление отдельного элемента
     */
    public static class Elem {
        ElemType type;
        String value;
        public Elem(ElemType type, String value) {
            this.type = type;
            this.value = value;
        }

        public Elem(ElemType type, Character value) {
            this.type = type;
            this.value = value.toString();
        }

    }

    /**
     * WorkWithExpress - работа с выражением
     */
    public static class WorkWithExpress {
        private int pos;
        public List<Elem> elemes;
        public WorkWithExpress(List<Elem> elemes) {
            this.elemes = elemes;
        }
        public Elem next() {
            return elemes.get(pos++);
        }
        public void back() {
            pos--;
        }
        public int getPos() {
            return pos;
        }
    }

    /**
     *calculation - выражение с учетом арифметических операций
     */
    public static List<Elem> calculation(String expText) {
        ArrayList<Elem> elemes = new ArrayList<>();
        int pos = 0;
        while (pos< expText.length()) {
            char c = expText.charAt(pos);
            switch (c) {
                case '(':
                    elemes.add(new Elem(ElemType.l_brack, c));
                    pos++;
                    continue;
                case ')':
                    elemes.add(new Elem(ElemType.r_brack, c));
                    pos++;
                    continue;
                case '+':
                    elemes.add(new Elem(ElemType.plus, c));
                    pos++;
                    continue;
                case '-':
                    elemes.add(new Elem(ElemType.minus, c));
                    pos++;
                    continue;
                case '*':
                    elemes.add(new Elem(ElemType.mul, c));
                    pos++;
                    continue;
                case '/':
                    elemes.add(new Elem(ElemType.div, c));
                    pos++;
                    continue;
                default:
                    if (c <= '9' && c >= '0') {
                        StringBuilder sb = new StringBuilder();
                        do {
                            sb.append(c);
                            pos++;
                            if (pos >= expText.length()) {
                                break;
                            }
                            c = expText.charAt(pos);
                        } while (c <= '9' && c >= '0');
                        elemes.add(new Elem(ElemType.num, sb.toString()));
                    } else {
                        if (c != ' ') {
                            throw new RuntimeException("Unexpected character: " + c);
                        }
                        pos++;
                    }
            }
        }
        elemes.add(new Elem(ElemType.end, ""));
        return elemes;
    }

    /**
     *Функции для вычисления выражения
     */
    public static int expr(WorkWithExpress elemes) {
        Elem elem = elemes.next();
        if (elem.type == ElemType.end) {
            return 0;
        } else {
            elemes.back();
            return plusminus(elemes);
        }
    }

    public static int plusminus(WorkWithExpress elemes) {
        int value = multdiv(elemes);
        while (true) {
            Elem elem = elemes.next();
            switch (elem.type) {
                case plus:
                    value += multdiv(elemes);
                    break;
                case minus:
                    value -= multdiv(elemes);
                    break;
                case end:
                case r_brack:
                    elemes.back();
                    return value;
                default:
                    throw new RuntimeException("Ошибка: " + elem.value
                            + " в позиции: " + elemes.getPos());
            }
        }
    }

    public static int multdiv(WorkWithExpress elemes) {
        int value = factor(elemes);
        while (true) {
            Elem lexeme = elemes.next();
            switch (lexeme.type) {
                case mul:
                    value *= factor(elemes);
                    break;
                case div:
                    value /= factor(elemes);
                    break;
                case end:
                case r_brack:
                case plus:
                case minus:
                    elemes.back();
                    return value;
                default:
                    throw new RuntimeException("Ошибка: " + lexeme.value
                            + " в позиции: " + elemes.getPos());
            }
        }
    }

    public static int factor(WorkWithExpress elemes) {
        Elem elem = elemes.next();
        switch (elem.type) {
            case num:
                return Integer.parseInt(elem.value);
            case l_brack:
                int value = plusminus(elemes);
                elem = elemes.next();
                if (elem.type != ElemType.r_brack) {
                    throw new RuntimeException("Ошибка: " + elem.value
                            + " в позиции: " + elemes.getPos());
                }
                return value;
            default:
                throw new RuntimeException("Ошибка: " + elem.value
                        + " в позиции: " + elemes.getPos());
        }
    }
}