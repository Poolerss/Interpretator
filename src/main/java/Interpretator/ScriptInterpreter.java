package Interpretator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptInterpreter {
    private Map<String, Integer> variables;

    public ScriptInterpreter() {
        variables = new HashMap<>();
    }

    private double eval(final String str){
        return new Object(){
            int pos = -1, ch;

            void nextChar(){
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }
            boolean eat(int charToEat){
                while (ch == ' ') nextChar();{
                    if (ch == charToEat){
                        nextChar();
                        return true;
                    }
                    return false;
                }
            }

            double parseFactor(){
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')){
                    x=parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '_') {
                    while ((ch >= '0' && ch <= '9') || ch == '-')  nextChar();
                        x = Double.parseDouble(str.substring(startPos, this.pos));
                    }else {
                        throw new RuntimeException("Некорректное выражение" + str);
                    }
                    return x;
                }


            double parseTerm(){
                double x = parseFactor();
                    for (; ;){
                        if (eat('*')) {
                            x *= parseFactor();
                        } else if (eat('/')){
                            x/= parseFactor();
                        } else {
                            return x;
                        }
                    }

                }

            double parseExpression(){
                double x = parseTerm();
                for (;;){
                    if (eat('+')){
                        x+=parseTerm();
                    } else if (eat('-')) {
                        x-=parseTerm();
                    }else {
                        return x;
                    }
                }
            }

            double parse(){
                nextChar();
                double x = parseExpression();
                if (pos < str.length()){
                    throw new RuntimeException("Некорректное выражение" + str);
                }
                return x;
            }


        } .parse();

    }

    private void processSetStatement(String statement){
        Pattern pattern = Pattern.compile("set\\s+\\$(\\w+)\\s+(.+)");
        Matcher matcher = pattern.matcher(statement);
        if (matcher.matches()){
            String variableName = matcher.group(1);
            String expression = matcher.group(2);
            int value = evaluateExpression(expression);
            variables.put(variableName, value);
        } else {
            System.out.println("Некорректный оператор:" + statement);
        }
    }

    private int evaluateExpression(String expression){
        expression = expression.replaceAll("\\$(\\w+)", "variables.getOrDefault(\"$1\", 0)");
        try {
            return (int)Math.floor(eval(expression));
        } catch (Exception e){
            System.err.println("Error" + expression);
            e.printStackTrace();
            return 0;
        }
    }

    private void processPrintStatement(String statement){
        Pattern pattern = Pattern.compile("print\\s+(.+)");
        Matcher matcher = pattern.matcher(statement);
        if (matcher.matches()){
            String[] tokens = matcher.group(1).split(",");
            for (String token : tokens){
                token = token.trim();
                if (token.startsWith("\"")&& token.endsWith("\"")){
                    System.out.println(token.substring(1,token.length()-1));
                } else if (token.startsWith("$")) {
                    String variableName = token.substring(1);
                    if (variables.containsKey(variableName)){
                        System.out.println(variables.get(variableName));
                    } else {
                        System.out.println("Неизвестная переменная: " + token);
                        return;
                    }
                } else {
                    System.out.println( "Некорректный токен: " + token);
                    return;
                }
                System.out.println(" ");
            }
            System.out.println();
        } else {
            System.out.println("Некорректный оператор print" + statement);
        }
    }

    public void interpreterScript(String fileName) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;
        while ((line=reader.readLine())!=null){
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")){
             continue;
            }
            if (line.startsWith("set")){
                processSetStatement(line);
            } else if (line.startsWith("print")) {
                processPrintStatement(line);
            }else {
                System.out.println("Некорректный оператор " + line);
            }
            reader.close();
        }


    }
}
