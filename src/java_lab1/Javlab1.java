/* ֧�ּ��źͿո�֧�ֶ����ĸ֮�䣬��ĸ����֮��ʡ�Գ˺ţ�֧�ָ�������֧�������﷨��飻֧��ͬʱ��ֵ�������
 * ֧�ָ��������ݣ�֧�����������Ƕ�׺���ݣ�֧�ָߴΣ�ʮλ�����ϣ������ݣ�֧�ֱ��ʽ�����﷨���
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
    private int[][] exponentMatrix; // �洢�η�����
    /** coefficientArray. */
    private double[] coefficientArray; // �洢ϵ��

    /**
     * main.
     *
     * 
     * @param args
     *            string
     */
    public static void main(final String[] args) {
        String a = "a+b+c"; // ֮ǰ����bug��ԭ����100���maxNumber���õ�̫С��
        Javlab1 test = new Javlab1();
        // a=myinput.read_string();
        // a="(1+2)2";
        // !simplify a=1.001 b=1.5 c=1.333 d=0 f=1 g=2 h=10
        // !simplify a=1.001 b=1.5 c=1.333 g=2 h=10 //������������Ҫ���������ʾ
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
    final boolean syntaxCheck(final String a) { // ���ʽ�����﷨���
        if (!a.matches("^[0-9a-zA-Z().+*\\-\\^]+$")) { // �����ַ����
            return false;
        }

        Pattern[] check = new Pattern[checkNumber];
        int checkIndex = 0;
        check[checkIndex++] = Pattern.compile("^.*\\.[^0-9].*$");
        // С���������Ĳ������ֵ����
        check[checkIndex++] = Pattern.compile("^.*\\^[0-9]*\\.[0-9]*$");
        // С����ǰ�治�����ֵ����

        check[checkIndex++] = Pattern.compile("^.*\\^[0-9]*[.\\^].*$"); // С�������
        check[checkIndex++] = Pattern.compile("^.*\\^[^0-9].*$"); // ^�������ֵ����

        for (int i = 0; i < checkIndex; i++) {
            Matcher test = check[i].matcher(a);
            if (test.find()) {
                return false;
            }
        }

        // ���ŵļ��
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
        } // �����ŵ��б�

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
    final String initialize(final String b) { // ���ַ�������Ԥ����

        String a = b;
        a = a.replaceAll("[\\s]", ""); // �ȴ���ո�

        a = a.replaceAll("([^0-9])\\.", "$10."); // С����ǰ���0�Ĳ���

        a = a.replaceAll("([a-zA-Z0-9])([A-Za-z])", "$1*$2"); // ��ĸ֮��ĳ˺�
        a = a.replaceAll("([a-zA-Z0-9])([A-Za-z])", "$1*$2"); // ��������ʽ�����ԣ��˴�Ҫ����

        a = a.replaceAll("([a-zA-Z])([0-9])", "$1*$2"); // a4������Ϊa*4

        a = a.replaceAll("([0-9a-zA-Z])(\\()", "$1*$2"); // ����/��ĸ������

        a = a.replaceAll("(\\))([A-Za-z0-9])", "$1*$2"); // ���ų���ĸ/����
        // ע������һ��Ҫѡ���̰��ģʽ
        a = a.replaceAll("\\)\\(", ")*("); // ���ų�����
        System.out.println(a);
        a = a.replace("-", "+%*"); // ��%����-1,������Ϊһ���µ�����

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
            a = buffer1.toString(); // ����Ϊ�ݵĴ���

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
            // ȥ�˺�,������

            a = a.replaceAll("\\(([.0-9a-zA-Z%*]*)\\)", "$1");
            a = a.replaceAll("\\(\\(([^()]+)\\)\\+", "($1+");
            a = a.replaceAll("\\+\\(([^()]+)\\)\\)", "+$1)");
            a = a.replaceAll("\\+\\(([^()]+)\\)\\+", "+$1+");
            a = a.replaceAll("^\\(([^()]+)\\)\\+", "$1+");
            a = a.replaceAll("\\+\\(([^()]+)\\)$", "+$1");
            // ȥ����
            a = a.replaceAll("^\\(([^()]+)\\)$", "$1");
            // ȥ�������� ��12+3432*67��

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
    final void stringToArray(final String a) { // �Ѵ�������ַ����洢�ھ�����

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
            for (int j = 0; j < stringMatrix[i].length(); j++) { // ��%���ֵĴ���
                if (stringMatrix[i].charAt(j) == '%') {
                    coefficientCheckNumber++;
                }
            }
            if (coefficientCheckNumber % 2 !=0) {
                coefficientArray[i] *= -1;
            } // �������
            stringMatrix[i] = stringMatrix[i].replaceAll("%", ""); // �޳�%
            stringMatrix[i] = stringMatrix[i].replaceAll("\\*", ""); // �޳�*
            // ����Ϊ��2.34��%֮�����ݵĴ���������֮��ÿ���ַ�����������you֮���

            // System.out.println(stringMatrix[i]);
            // System.out.println(stringMatrix[i].length());
            // System.out.println("=================================");
            for (int j = 0; j < stringMatrix[i].length(); j++) {
                exponentMatrix[i][(int) (stringMatrix[i].charAt(j) - 'a')]++;
            }
        }
        // ���ݴ洢��������

        // ���½��кϲ�ͬ����ͻ���
        for (int i = 0; i < maxNumber; i++) {
            if (zero(coefficientArray[i])) {
                continue;
            }
            for (int j = i + 1; j < maxNumber; j++) {
                if (zero(coefficientArray[j])) {
                    continue;
                } // ϵ��Ϊ��ֱ������

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
        // �Ѿ���洢ת��Ϊ�ַ���

        for (int i = 0; i < maxNumber; i++) {
            if (zero(b[i])) {
                continue;
            }
            for (int j = i + 1; j < maxNumber; j++) {
                if (zero(b[j])) {
                    continue;
                } // ϵ��Ϊ��ֱ������

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
        // �ϲ�ͬ����

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

        toReturn = toReturn.replaceAll("^\\+(.*)$", "$1"); // ȥ�����ʽ��ͷ�ļӺ�

        toReturn = toReturn.replaceAll("(\\.[0-9]*[1-9]+)0{5,}[1-9]{1,}", "$1");
        // ���java�����澫�ȵ�����
        // ĳ��С�������ֺܶ��0��ĩβһ�����㣬ֱ����ȥ��β�ķ��㡣
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
    final String simplify(final String command1) { // �����н��л�������

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
    final String deriative(final String command) { // �����н���������
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
