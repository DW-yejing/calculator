import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 数字工具类 YJ_20200724
 */
public class NumberUtils {
    public static final char[] SYMBOLS = {'+', '-', '*', '/', '(', ')'};
    public static final String[] SYMBOLS_STRING = {"+", "-", "*", "/", "(", ")"};
    public static final String[] ARITH_SYMBOLS_STRING = {"+", "-", "*", "/",};
    public static final Map<String, Integer> ARITH_PRIORITY_MAP = new HashMap<>();
    private static final int DEFAULT_SCALE = 2;

    static {
        ARITH_PRIORITY_MAP.put("+", 1);
        ARITH_PRIORITY_MAP.put("-", 1);
        ARITH_PRIORITY_MAP.put("*", 2);
        ARITH_PRIORITY_MAP.put("/", 2);
    }
    /**
     * 判断字符串是否是数字  YJ_20200724
     */
    public static boolean isNumber(CharSequence str) {
        if (StringUtils.isBlank(str)) {
            return false;
        } else {
            char[] chars = str.toString().toCharArray();
            int sz = chars.length;
            boolean hasExp = false;
            boolean hasDecPoint = false;
            boolean allowSigns = false;
            boolean foundDigit = false;
            int start = chars[0] != '-' && chars[0] != '+' ? 0 : 1;
            int i;
            if (sz > start + 1 && chars[start] == '0' && (chars[start + 1] == 'x' || chars[start + 1] == 'X')) {
                i = start + 2;
                if (i == sz) {
                    return false;
                } else {
                    while(i < chars.length) {
                        if ((chars[i] < '0' || chars[i] > '9') && (chars[i] < 'a' || chars[i] > 'f') && (chars[i] < 'A' || chars[i] > 'F')) {
                            return false;
                        }

                        ++i;
                    }

                    return true;
                }
            } else {
                --sz;

                for(i = start; i < sz || i < sz + 1 && allowSigns && !foundDigit; ++i) {
                    if (chars[i] >= '0' && chars[i] <= '9') {
                        foundDigit = true;
                        allowSigns = false;
                    } else if (chars[i] == '.') {
                        if (hasDecPoint || hasExp) {
                            return false;
                        }

                        hasDecPoint = true;
                    } else if (chars[i] != 'e' && chars[i] != 'E') {
                        if (chars[i] != '+' && chars[i] != '-') {
                            return false;
                        }

                        if (!allowSigns) {
                            return false;
                        }

                        allowSigns = false;
                        foundDigit = false;
                    } else {
                        if (hasExp) {
                            return false;
                        }

                        if (!foundDigit) {
                            return false;
                        }

                        hasExp = true;
                        allowSigns = true;
                    }
                }

                if (i < chars.length) {
                    if (chars[i] >= '0' && chars[i] <= '9') {
                        return true;
                    } else if (chars[i] != 'e' && chars[i] != 'E') {
                        if (chars[i] == '.') {
                            return (!hasDecPoint && !hasExp) && foundDigit;
                        } else if (allowSigns || chars[i] != 'd' && chars[i] != 'D' && chars[i] != 'f' && chars[i] != 'F') {
                            if (chars[i] != 'l' && chars[i] != 'L') {
                                return false;
                            } else {
                                return foundDigit && !hasExp;
                            }
                        } else {
                            return foundDigit;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return !allowSigns && foundDigit;
                }
            }
        }
    }

    /**
     * 中缀表达式转后缀表达式 YJ_20200724
     * RPN: reverse polish notation(后缀表达式/逆波兰表达式)
     */
    public static Stack<String> convert2RPN(String expression){
        // 获取操作数及运算符队列
        List<String> elementList = new ArrayList<>();
        int cursor = 0;
        for(int i=0; i<expression.length(); i++){
            if(i+1 == expression.length()){
                String element = expression.substring(cursor, i+1);
                elementList.add(element);
            }
            char ch = expression.charAt(i);
            if(ArrayUtils.contains(SYMBOLS, ch)){
                if(i==0){
                    elementList.add(String.valueOf(ch));
                    cursor = i+1;
                    continue;
                }
                String element = expression.substring(cursor, i);
                if(StringUtils.isNotBlank(element)){
                    elementList.add(element);
                }
                elementList.add(String.valueOf(ch));
                cursor = i+1;
            }
        }

        /**
         * 策略
         * 1. 如果是字符'('，添加到临时栈
         * 2. 如果是数字，添加到后缀栈
         * 3. 如果是运算符，先将临时栈顶优先级不低于该运算符的运算符出栈，添加到后缀栈，再将运算符加入临时栈
         * 4. 如果是字符')'，将临时栈出栈，添加到后缀栈，直到临时栈出栈的是'('
         * 5. 如果中缀表达式遍历结束，但临时栈中还有元素，将所有元素出栈，添加到后缀栈
         */
        // 生成后缀表达式
        Stack<String> tempStack = new Stack<>(); // 临时栈
        Stack<String> elementStack = new Stack<>(); // 后缀栈
        for(String element : elementList){
            // 1. 如果是字符'('，添加到临时栈
            if(Objects.equals(element, SYMBOLS_STRING[4])){
                tempStack.push(element);
                continue;
            }
            // 2. 如果是数字，添加到后缀栈
            if(NumberUtils.isNumber(element)){
                elementStack.push(element);
                continue;
            }
            // 3. 如果是运算符，先将临时栈顶优先级不低于该运算符的运算符出栈，添加到后缀栈，再将运算符加入临时栈
            if(ArrayUtils.contains(ARITH_SYMBOLS_STRING, element)){
                while(true){
                    if(tempStack.isEmpty()){
                        break;
                    }
                    String top = tempStack.peek();
                    if(ArrayUtils.contains(ARITH_SYMBOLS_STRING, top)){
                        if(ARITH_PRIORITY_MAP.get(top)>=ARITH_PRIORITY_MAP.get(element)){
                            elementStack.push(top);
                            tempStack.pop();
                        }else{
                            break;
                        }
                    }else{
                        break;
                    }
                }
                tempStack.push(element);
            }
            // 4. 如果是字符')'，将临时栈出栈，添加到后缀栈，直到临时栈出栈的是'('
            if(Objects.equals(element, SYMBOLS_STRING[5])){
                while(true){
                    if(tempStack.isEmpty()){
                        break;
                    }
                    String top = tempStack.peek();
                    if(Objects.equals(top,SYMBOLS_STRING[4])){
                        tempStack.pop();
                        break;
                    }
                    elementStack.push(top);
                    tempStack.pop();
                }
            }
        }
        // 5. 如果中缀表达式遍历结束，但临时栈中还有元素，将所有元素出栈，添加到后缀栈
        if(!tempStack.isEmpty()){
            while(true){
                if(tempStack.isEmpty()){
                    break;
                }
                String top = tempStack.peek();
                elementStack.push(top);
                tempStack.pop();
            }
        }
        return elementStack;
    }

    /**
     * 无精度损失表达式自动计算  YJ_20200724
     */
    public static BigDecimal eval(String expression){
        Stack<String> rpnStack = NumberUtils.convert2RPN(expression);
        Stack<String> tempStack = new Stack<>();
        for(String element : rpnStack){
            if(NumberUtils.isNumber(element)){
                tempStack.push(element);
                continue;
            }
            if(ArrayUtils.contains(ARITH_SYMBOLS_STRING, element)){
                String num1 = tempStack.pop();
                String num2 = tempStack.pop();
                BigDecimal resultTemp = NumberUtils.noPrecisionLossCalculate(num2, num1, element, DEFAULT_SCALE);
                tempStack.push(resultTemp.toString());
            }
        }
        return new BigDecimal(tempStack.pop());
    }

    /**
     * 基于BigDecimal的运算符运算  YJ_20200724
     */
    public static BigDecimal noPrecisionLossCalculate(String num1Str, String num2Str, String operation, int scale){
        BigDecimal num1 = new BigDecimal(num1Str);
        BigDecimal num2 = new BigDecimal(num2Str);
        BigDecimal result;
        switch (operation){
            case "+":
                result = num1.add(num2);
                break;
            case "-":
                result = num1.subtract(num2);
                break;
            case "*":
                result = num1.multiply(num2);
                break;
            case "/":
                result= num1.divide(num2, scale, RoundingMode.HALF_UP);
                break;
            default:
                result = null;
        }
        return result;
    }
}
