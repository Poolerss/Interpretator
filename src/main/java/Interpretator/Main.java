package Interpretator;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        ScriptInterpreter scriptInterpreter = new ScriptInterpreter();
        try {
            scriptInterpreter.interpreterScript("src/main/resources/Primer1.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
