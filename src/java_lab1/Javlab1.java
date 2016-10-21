/* 支持减号和空格；支持多个字母之间，字母括号之间省略乘号；支持浮点数；支持命令语法检查；支持同时赋值多个变量
 * 支持浮点数的幂，支持无穷多括号嵌套后的幂，支持高次（十位数以上）整数幂；支持表达式基本语法检查
 * */

package javalab;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author WeiSiDa
 */
public class Javlab1 {
    /** eps. */
    private final double eps = 1e-6;
    /** maxNumber. */
    private final int maxNumber = 1000;
    /** bigNumber. */
    private final int bigNumber = 30;
    /** checkNumber. */
    private final int checkNumber = 30;
    /** exponentMatrix. */
    private int[][] exponentMatrix; // 存储次方数据
    /** coefficientArray. */
    private double[] coefficientArray; // 存储系数

    /**
     * main.
     *
     * 
     * @param args
     *            string
     */
    public static void main(final String[] args) {
        String a = "a+b+c"; // 之前导致bug的原因是100这个maxNumber设置的太小了
        Javlab1 test = new Javlab1();
        // a=myinput.read_string();
        // a="(1+2)2";
        // !simplify a=1.001 b=1.5 c=1.333 d=0 f=1 g=2 h=10
        // !simplify a=1.001 b=1.5 c=1.333 g=2 h=10 //这个会出现误差，不要拿这个颜演示
        while (!test.syntaxCheck(a)) {
            System.out.println("Syntax error! Input again");
            a = myinput.read_string();
        }
        a = test.initialize(a);
        System.out.println(a);
        test.stringToArray(a);

        System.out.println("Simplified expression:");
        System.out.println(test.matrixToString(test.exponentMatrix,
                test.coefficientArray));
        System.out.println("Input command:");
        String command = "";

        while (true) {
            command = myinput.read_string();
            if (command
                    .matches("^!simplify[\\s]+([a-zA-Z]=[-0-9.]+[\\s]*)+$")) {
                System.out.println("Simplify:");
                System.out.println(test.simplify(command));
            } else if (command.matches("^!d/d[a-zA-Z]$")) {
                System.out.println("Deriative:");
                System.out.println(test.deriative(command));
            } else if (command.matches("^![\\s]*d\\/d[\\s]*$")) {
                System.out.println("No variable .");
            } else if (command.contains("quit") || command.contains("exit")) {
                System.out.println("Over.");
                break;
            } else if (command.matches("^!simplify[\\s]*$")) {
                System.out.println(test.matrixToString(test.exponentMatrix,
                        test.coefficientArray));
            } else {
                System.out.println("Input error !");
            }
        }
    }

    /**
     * zero.
     * 
     * @return 0
     * 
     * @param a
     *            0
     */
    final boolean zero(final double a) {
        return (a < eps) && (a > -eps);
    }

    /**
     * syntaxCheck.
     * 
     * @return false
     * 
     * @param a
     *            string
     */
    final boolean syntaxCheck(final String a) { // 表达式基本语法检查
        if (!a.matches("^[0-9a-zA-Z().+*\\-\\^]+$")) { // 基本字符检查
            return false;
        }

        Pattern[] check = new Pattern[checkNumber];
        int checkIndex = 0;
        check[checkIndex++] = Pattern.compile("^.*\\.[^0-9].*$");
        // 小数点后面跟的不是数字的情况
        check[checkIndex++] = Pattern.compile("^.*\\^[0-9]*\\.[0-9]*$");
        // 小数点前面不是数字的情况

        check[checkIndex++] = Pattern.compile("^.*\\^[0-9]*[.\\^].*$"); // 小数的情况
        check[checkIndex++] = Pattern.compile("^.*\\^[^0-9].*$"); // ^后不是数字的情况

        for (int i = 0; i < checkIndex; i++) {
            Matcher test = check[i].matcher(a);
            if (test.find()) {
                return false;
            }
        }

        // 括号的检查
        int leftParentheses = 0;
        int rightParentheses = 0;
        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) == '(') {
                leftParentheses++;
            }
            if (a.charAt(i) == ')') {
                rightParentheses++;
            }
            if (leftParentheses < rightParentheses) {
                return false;
            }
        }
        if (leftParentheses != rightParentheses) {
            return false;
        } // 对括号的判别

        return true;
    }

    /**
     * initialize.
     * 
     * @return string(finished)
     * 
     * @param b
     *            string
     */
    final String initialize(final String b) { // 对字符串进行预处理

        String a = b;
        a = a.replaceAll("[\\s]", ""); // 先处理空格

        a = a.replaceAll("([^0-9])\\.", "$10."); // 小数点前面的0的补充

        a = a.replaceAll("([a-zA-Z0-9])([A-Za-z])", "$1*$2"); // 字母之间的乘号
        a = a.replaceAll("([a-zA-Z0-9])([A-Za-z])", "$1*$2"); // 因正则表达式的特性，此处要两次

        a = a.replaceAll("([a-zA-Z])([0-9])", "$1*$2"); // a4将补充为a*4

        a = a.replaceAll("([0-9a-zA-Z])(\\()", "$1*$2"); // 数字/字母乘括号

        a = a.replaceAll("(\\))([A-Za-z0-9])", "$1*$2"); // 括号乘字母/数字
        // 注意上面一定要选择非贪心模式
        a = a.replaceAll("\\)\\(", ")*("); // 括号乘括号
        System.out.println(a);
        a = a.replace("-", "+%*"); // 用%代表-1,将其视为一个新的数字

        while (a.contains("(") || a.contains("^")) {
            String oldString = "";
            oldString = oldString.concat(a);
            StringBuffer buffer1 = new StringBuffer();
            Pattern regexTest1 = Pattern.compile(
                    "(\\([^()]+\\)" + "|[0-9.]+|[a-zA-Z]+)\\^([.0-9]+)");
            Matcher regexTest1Matcher = regexTest1.matcher(a);
            while (regexTest1Matcher.find()) {
                String toInsert = regexTest1Matcher.group(1);
                for (int i = 1; i < Integer
                        .parseInt(regexTest1Matcher.group(2)); i++) {
                    toInsert += "*" + regexTest1Matcher.group(1);
                }
                regexTest1Matcher.appendReplacement(buffer1, toInsert);
            }
            regexTest1Matcher.appendTail(buffer1);
            a = buffer1.toString(); // 以上为幂的处理

            // System.out.println(a);

            a = a.replaceAll(
                    "([%0-9.a-zA-Z]+)\\*\\(([%0-9.a-zA-Z*]+)\\+([^()]*)\\)",
                    "($1*$2+$1*($3))");
            a = a.replaceAll(
                    "\\(([%0-9.a-zA-Z*]+)\\+([^()]*)\\)\\*([%0-9.a-zA-Z]+)",
                    "($1*$3+($2)*$3)");

            a = a.replaceAll(
                    "\\(([%0-9.a-zA-Z*]+?)\\+([^()]*)\\)\\*\\"
                            + "(([%0-9.a-zA-Z*]+?)\\+([^()]*)\\)",
                    "($1*$3+$3*($2)+$1*($4)+($2)*($4))");
            // 去乘号,加括号

            a = a.replaceAll("\\(([.0-9a-zA-Z%*]*)\\)", "$1");
            a = a.replaceAll("\\(\\(([^()]+)\\)\\+", "($1+");
            a = a.replaceAll("\\+\\(([^()]+)\\)\\)", "+$1)");
            a = a.replaceAll("\\+\\(([^()]+)\\)\\+", "+$1+");
            a = a.replaceAll("^\\(([^()]+)\\)\\+", "$1+");
            a = a.replaceAll("\\+\\(([^()]+)\\)$", "+$1");
            // 去括号
            a = a.replaceAll("^\\(([^()]+)\\)$", "$1");
            // 去括号形如 （12+3432*67）

            if (a.equals(oldString)) {
                System.out.println("Can not simpify !");
                System.out.println("Check syntax !");
                return "";
            }
        }

        return a;
    }

    /**
     * stringToArray.
     * 
     * @param a
     *            string
     */
    final void stringToArray(final String a) { // 把处理过的字符串存储在矩阵中

        // System.out.println(a);

        exponentMatrix = new int[maxNumber][bigNumber];
        coefficientArray = new double[maxNumber];
        String[] stringMatrix = a.split("\\+");

        for (int i = 0; i < maxNumber; i++) {
            coefficientArray[i] = 0;
        }

        for (int i = 0; i < stringMatrix.length; i++) {
            coefficientArray[i] = 1;

            StringBuffer buffer1 = new StringBuffer();
            Pattern regexTest1 = Pattern.compile("([.0-9]+)");
            Matcher regexTest1Matcher = regexTest1.matcher(stringMatrix[i]);
            while (regexTest1Matcher.find()) {
                coefficientArray[i] *= Double
                        .parseDouble(regexTest1Matcher.group(0));
                String toInsert = "";
                regexTest1Matcher.appendReplacement(buffer1, toInsert);
            }
            regexTest1Matcher.appendTail(buffer1);
            stringMatrix[i] = buffer1.toString();

            int coefficientCheckNumber = 0;
            for (int j = 0; j < stringMatrix[i].length(); j++) { // 找%出现的次数
                if (stringMatrix[i].charAt(j) == '%') {
                    coefficientCheckNumber++;
                }
            }
            if (coefficientCheckNumber % 2 !=0) {
                coefficientArray[i] *= -1;
            } // 处理符号
            stringMatrix[i] = stringMatrix[i].replaceAll("%", ""); // 剔除%
            stringMatrix[i] = stringMatrix[i].replaceAll("\\*", ""); // 剔除*
            // 以上为对2.34、%之类数据的处理，处理完之后每个字符串都是类似you之类的

            // System.out.println(stringMatrix[i]);
            // System.out.println(stringMatrix[i].length());
            // System.out.println("=================================");
            for (int j = 0; j < stringMatrix[i].length(); j++) {
                exponentMatrix[i][(int) (stringMatrix[i].charAt(j) - 'a')]++;
            }
        }
        // 次幂存储在数组中

        // 以下进行合并同类项和化简
        for (int i = 0; i < maxNumber; i++) {
            if (zero(coefficientArray[i])) {
                continue;
            }
            for (int j = i + 1; j < maxNumber; j++) {
                if (zero(coefficientArray[j])) {
                    continue;
                } // 系数为零直接跳过

                int check = 1;
                for (int k = 0; k < bigNumber; k++) {
                    if (exponentMatrix[i][k] != exponentMatrix[j][k]) {
                        check = 0;
                        break;
                    }
                }
                if (check == 1) {
                    coefficientArray[i] += coefficientArray[j];
                    coefficientArray[j] = 0;
                }
            }
        }
    }

    /**
     * matrixToString.
     * 
     * @return string
     * 
     * @param a
     *            number
     *
     * @param b
     *            matrix
     */
    final String matrixToString(final int[][] a, final double[] b) {
        // 把矩阵存储转化为字符串

        for (int i = 0; i < maxNumber; i++) {
            if (zero(b[i])) {
                continue;
            }
            for (int j = i + 1; j < maxNumber; j++) {
                if (zero(b[j])) {
                    continue;
                } // 系数为零直接跳过

                int check = 1;
                for (int k = 0; k < bigNumber; k++) {
                    if (a[i][k] != a[j][k]) {
                        check = 0;
                        break;
                    }
                }
                if (check == 1) {
                    b[i] += b[j];
                    b[j] = 0;
                }
            }
        }
        // 合并同类项

        String toReturn = "";
        for (int i = 0; i < maxNumber; i++) {
            if (!zero(b[i])) {
                if (b[i] > 0) {
                    toReturn = toReturn.concat("+" + Double.toString(b[i]));
                    for (int j = 0; j < bigNumber; j++) {
                        for (int k = 0; k < a[i][j]; k++) {
                            char temp = (char) (j + 'a');
                            toReturn = toReturn.concat("*" + temp);
                        }
                    }
                } else {
                    toReturn = toReturn.concat(Double.toString(b[i]));
                    for (int j = 0; j < bigNumber; j++) {
                        for (int k = 0; k < a[i][j]; k++) {
                            char temp = (char) (j + 'a');
                            toReturn = toReturn.concat("*" + temp);
                        }
                    }
                }
            }
        }

        toReturn = toReturn.replaceAll("^\\+(.*)$", "$1"); // 去掉表达式开头的加号

        toReturn = toReturn.replaceAll("(\\.[0-9]*[1-9]+)0{5,}[1-9]{1,}", "$1");
        // 解决java本身储存精度的问题
        // 某个小数点后出现很多次0，末尾一个非零，直接舍去结尾的非零。
        if (toReturn.isEmpty()) {
            toReturn = "0";
        }
        return toReturn;
    }

    /**
     * simplify.
     * 
     * @return simplified string
     * 
     * @param command1
     *            string
     */
    final String simplify(final String command1) { // 矩阵中进行化简运算

        String command = command1;
        int[][] matrixCalculate = new int[maxNumber][bigNumber];
        double[] coefficientCalculate = new double[maxNumber];
        for (int i = 0; i < maxNumber; i++) {
            coefficientCalculate[i] = coefficientArray[i];
        }
        for (int i = 0; i < maxNumber; i++) {
            for (int j = 0; j < bigNumber; j++) {
                matrixCalculate[i][j] = exponentMatrix[i][j];
            }
        }

        command = command.replaceAll("^!simplify[\\s]*", "");
        while (command.contains("  ")) {
            command = command.replaceAll("  ", " ");
        }
        String[] commandList = command.split(" ");
        for (int i = 0; i < commandList.length; i++) {
            int index = commandList[i].charAt(0) - 'a';
            double value = Double.parseDouble(commandList[i].substring(2));
            for (int j = 0; j < maxNumber; j++) {
                if (!zero(coefficientCalculate[j])
                        && matrixCalculate[j][index] != 0) {
                    coefficientCalculate[j] *= Math.pow(value,
                            matrixCalculate[j][index]);
                }
                matrixCalculate[j][index] = 0;
            }
        }
        return matrixToString(matrixCalculate, coefficientCalculate);
    }

    /**
     * deriative.
     * 
     * @return Deriative
     * 
     * @param command
     *            string
     */
    final String deriative(final String command) { // 矩阵中进行求导运算
        int index = command.charAt('4') - 'a';

        int[][] matrixCalculate = new int[maxNumber][bigNumber];
        double[] coefficientCalculate = new double[maxNumber];
        for (int i = 0; i < maxNumber; i++) {
            coefficientCalculate[i] = coefficientArray[i];
        }
        for (int i = 0; i < maxNumber; i++) {
            for (int j = 0; j < bigNumber; j++) {
                matrixCalculate[i][j] = exponentMatrix[i][j];
            }
        }

        for (int i = 0; i < maxNumber; i++) {
            coefficientCalculate[i] *= matrixCalculate[i][index];
            if (matrixCalculate[i][index] != 0) {
                matrixCalculate[i][index]--;
            }
        }

        return matrixToString(matrixCalculate, coefficientCalculate);
    }

}
