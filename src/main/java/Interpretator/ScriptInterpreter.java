package Interpretator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ScriptInterpreter {

    private Map<String, Integer> variables;

    public ScriptInterpreter() {
        variables = new HashMap<>();
    }

    /**
     * обрабатывает файл
     * имя файла принимает в качестве параметра
     */
    public void interpreterScript(String filename){
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(filename));
            Stream<String> lines = bufferedReader.lines();
            lines.forEach(this::processLine);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * обработка скрипта построчно
     */
    private void processLine(String line){
        line = line.trim();
        if(line.isEmpty() || line.startsWith("#")){
            return;
        }
        if (line.startsWith("set")){
            processSetStatement(line);
        } else if (line.startsWith("print")) {
            processPrintStatement(line);
        } else {
            System.out.println("invalid statement:" + line);
        }
    }

    /**
     * обработка оператора set
     */
    private void processSetStatement(String line){
        String[] token = line.split("="); //разделяем на 2 части по =
        String variableName = token[0].trim().substring(5);
        String expression = token[1].trim();
        int value = evaluateExpression(expression); // передача выражения
        if (value != Integer.MIN_VALUE){
            variables.put(variableName, value);
        } else {
            System.out.println("Invalid SET statement" + line);
        }
    }


    /**
     * обработка оператора print
     */
    private void processPrintStatement(String line){
        String content =  line.substring(6).trim();
        String[] tokens = content.split(",");
        StringBuilder sb = new StringBuilder();

        for (String token : tokens){
            token = token.trim();
            if (token.startsWith("\"") && token.endsWith("\"")){
                if(token.contains("$")){
                    sb.append(token,2,token.length()-2);
                }else {
                sb.append(token, 1, token.length()-1);}
            } else if (token.startsWith("$")) {
                String variableName = token.substring(1);
                Integer value = variables.get(variableName);
                if (value!=null){
                    sb.append(value);
                }else {
                    System.out.println("Unknown variable in print statement " + line);
                    return;
                }
            } else {
                sb.append(token);
            }
            sb.append(" ");
        }
        System.out.println(sb.toString().trim());
    }


    /**
     * вычисляет математическое выражение ???
     */
    private int evaluateExpression(String expression) {
        String[] tokens = expression.split("\\s+");

        int result = Integer.MIN_VALUE;
        int sign = 1;

        for (String token : tokens) {
            token = token.trim();

            if (token.equals("-")) {
                sign = -1;
            } else if (token.equals("+")) {
                sign = 1;
            } else {
                int value;
                if (token.startsWith("$")) {
                    String variableName = token.substring(1);
                    if (variables.containsKey(variableName)) {
                        value = variables.get(variableName);
                    } else {
                        value = Integer.MIN_VALUE;
                    }
                } else {
                    value = Integer.parseInt(token);
                }

                if (result == Integer.MIN_VALUE) {
                    result = value;
                } else {
                    result = result + (sign * value);
                }
            }
        }

        return result;
    }


}





