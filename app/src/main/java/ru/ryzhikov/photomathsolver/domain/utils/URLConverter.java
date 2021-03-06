package ru.ryzhikov.photomathsolver.domain.utils;

/**
 * Используется для преобразования строки в параметр url
 */
public class URLConverter {

    public static String getUrlForLatexFormula(String latexFormula) {
        return latexFormula
                .replace(" ", "")
                .replace("\\", " \\")
                .replace("+", "%2B").trim();
    }

    public static String getUrlForWolframFormula(String wolframFormula) {
        return wolframFormula
                .replace("+", "%2B");
    }
}
