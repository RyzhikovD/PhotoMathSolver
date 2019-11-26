package ru.ryzhikov.photomathsolver;

public class URLConverter {

    public static String getUrlForFormula(String editedFormula) {
        return editedFormula
                .replace(" ", "")
                .replace("\\", " \\")
                .replace("+", "%2B").trim();
    }

    public static String getLatexFromWolfram(String wolframFormula) {
        return wolframFormula
                .replace("(", "{")
                .replace(")", "}")
                .replace("to", "\\rightarrow");
    }
}
