import java.math.BigDecimal;

public class CalculatorTest {

    public static void main(String[] args) {
        BigDecimal result = NumberUtils.eval("0.1+0.2");
        System.out.println(result.toString());
    }
}
