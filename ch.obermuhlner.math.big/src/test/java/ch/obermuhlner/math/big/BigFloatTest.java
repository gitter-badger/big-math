package ch.obermuhlner.math.big;

import ch.obermuhlner.math.big.BigFloat.*;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.List;

import static ch.obermuhlner.math.big.BigFloat.*;
import static org.junit.Assert.*;

public class BigFloatTest {

	@Test
	public void testContext() {
		MathContext mathContext = new MathContext(20);
		Context context = context(mathContext);

		assertEquals(mathContext, context.getMathContext());
		assertEquals(mathContext.getPrecision(), context.getPrecision());
		assertEquals(mathContext.getRoundingMode(), context.getRoundingMode());

		assertEquals(1000, context(1000).getPrecision());

		assertEquals(context, context(mathContext));
		assertEquals(context.hashCode(), context(mathContext).hashCode());
		assertEquals(context.toString(), context(mathContext).toString());

		assertNotEquals(context, null);
		assertNotEquals(context, "string");
	}

	@Test
	public void testValueOf() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(0, BigDecimal.ONE.compareTo(context.valueOf(BigDecimal.ONE).toBigDecimal()));
		assertEquals(0, BigDecimal.ONE.compareTo(context.valueOf(1).toBigDecimal()));
		assertEquals(0, BigDecimal.ONE.compareTo(context.valueOf(1L).toBigDecimal()));
		assertEquals(0, BigDecimal.ONE.compareTo(context.valueOf(1.0).toBigDecimal()));

		Context anotherContext = context(MathContext.DECIMAL64);
		assertEquals(0, BigDecimal.ONE.compareTo(context.valueOf(anotherContext.valueOf(1.0)).toBigDecimal()));
		assertEquals(0, BigDecimal.ONE.compareTo(anotherContext.valueOf(context.valueOf(1.0)).toBigDecimal()));

		assertEquals(context, context.valueOf(anotherContext.valueOf(1.0)).getContext());
		assertEquals(anotherContext, anotherContext.valueOf(context.valueOf(1.0)).getContext());
	}

	@Test
	public void testValueOfRounding() {
		Context context = context(new MathContext(3));

		assertEquals(0, BigDecimal.valueOf(123000).compareTo(context.valueOf(123456).toBigDecimal()));
		assertEquals(0, BigDecimal.valueOf(123000).compareTo(context.valueOf(123456L).toBigDecimal()));
		assertEquals(0, BigDecimal.valueOf(123000).compareTo(context.valueOf(123456.0).toBigDecimal()));
		assertEquals(0,
				BigDecimal.valueOf(123000).compareTo(context.valueOf(BigDecimal.valueOf(123456)).toBigDecimal()));
		assertEquals(0,
				BigDecimal.valueOf(123000).compareTo(context.valueOf(context(20).valueOf(123456)).toBigDecimal()));
	}

	@Test
	public void testToDouble() {
		Context context = context(MathContext.DECIMAL64);
		assertEquals(3.14, context.valueOf(3.14).toDouble(), 0.0);
	}

	@Test
	public void testToLong() {
		Context context = context(MathContext.DECIMAL64);
		assertEquals(1234L, context.valueOf(1234).toLong());
	}

	@Test
	public void testToInt() {
		Context context = context(MathContext.DECIMAL64);
		assertEquals(1234, context.valueOf(1234).toInt());
	}

	@Test
	public void testIsIntValue() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(true, context.valueOf(0).isIntValue());
		assertEquals(true, context.valueOf(123).isIntValue());

		assertEquals(false, context.valueOf(123E99).isIntValue());
		assertEquals(false, context.valueOf(123.456).isIntValue());
		assertEquals(false, context.valueOf(0.456).isIntValue());
	}

	@Test
	public void testIsDoubleValue() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(true, context.valueOf(0).isDoubleValue());
		assertEquals(true, context.valueOf(123).isDoubleValue());

		assertEquals(true, context.valueOf(123E99).isDoubleValue());
		assertEquals(true, context.valueOf(123.456).isDoubleValue());
		assertEquals(true, context.valueOf(0.456).isDoubleValue());

		assertEquals(false, context.valueOf("1E309").isDoubleValue());
		assertEquals(false, context.valueOf("-1E309").isDoubleValue());
	}

	@Test
	public void testGetMantissa() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(context.valueOf(1.23), context.valueOf(1.23E99).getMantissa());
	}

	@Test
	public void testGetExponent() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(context.valueOf(99), context.valueOf(1.23E99).getExponent());
	}

	@Test
	public void testGetIntegralPart() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(context.valueOf(123), context.valueOf(123.456).getIntegralPart());
	}

	@Test
	public void testGetFractionalPart() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(context.valueOf(0.456), context.valueOf(123.456).getFractionalPart());
	}

	@Test
	public void testEquals() {
		Context context = context(MathContext.DECIMAL32);

		assertNotEquals(context.valueOf(1234), null);
		assertNotEquals(context.valueOf(1234), "string");

		BigFloat value1 = context.valueOf(1);
		assertEquals(value1, value1);

		assertEquals(context.valueOf(1234), context.valueOf(1234));
		assertNotEquals(context.valueOf(1234), context.valueOf(9999));
		assertNotEquals(context.valueOf(9999), context.valueOf(1234));

		Context equalContext = context(MathContext.DECIMAL32);
		assertEquals(context, equalContext);
		assertEquals(context.valueOf(1234), equalContext.valueOf(1234));
		assertEquals(context.valueOf(1234), equalContext.valueOf(1234.0));
		assertEquals(context.valueOf(1234), equalContext.valueOf("1234.0000"));
		assertEquals(equalContext.valueOf(1234), context.valueOf(1234));

		Context anotherContext = context(MathContext.DECIMAL64);
		assertNotEquals(context, anotherContext);
		assertEquals(context.valueOf(1234), anotherContext.valueOf(1234));
		assertEquals(anotherContext.valueOf(1234), context.valueOf(1234));

	}

	@Test
	public void testHashCode() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(context.valueOf(1).hashCode(), context.valueOf(1).hashCode());
		assertNotEquals(context.valueOf(1).hashCode(), context.valueOf(999).hashCode());

		Context equalContext = context(MathContext.DECIMAL32);
		assertEquals(context, equalContext);
		assertEquals(context.valueOf(1).hashCode(), equalContext.valueOf(1).hashCode());
		assertEquals(context.valueOf(1).hashCode(), equalContext.valueOf(1.0).hashCode());
		assertEquals(context.valueOf(1).hashCode(), equalContext.valueOf("1.0000").hashCode());
		assertNotEquals(context.valueOf(1).hashCode(), equalContext.valueOf(999).hashCode());

		Context anotherContext = context(MathContext.DECIMAL64);
		assertEquals(context.valueOf(1).hashCode(), anotherContext.valueOf(1).hashCode());
	}

	@Test
	public void testToString() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals("0", context.valueOf(0).toString());
		assertEquals("0.0", context.valueOf(0.0).toString()); // I hate BigDecimal
		assertEquals("1.234", context.valueOf(1.234).toString());
		assertEquals("-1.234", context.valueOf(-1.234).toString());
	}

	@Test
	public void testPi() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(BigDecimalMath.pi(context.getMathContext()), context.pi().toBigDecimal());
	}

	@Test
	public void testE() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(BigDecimalMath.e(context.getMathContext()), context.e().toBigDecimal());
	}

	@Test
	public void testSignum() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(-1, context.valueOf("-1E999").signum());
		assertEquals(-1, context.valueOf("-1E-999").signum());
		assertEquals(-1, context.valueOf(-5).signum());
		assertEquals(0, context.valueOf(0).signum());
		assertEquals(0, context.valueOf("0.000000").signum());
		assertEquals(1, context.valueOf(5).signum());
		assertEquals(1, context.valueOf("1E999").signum());
		assertEquals(1, context.valueOf("1E-999").signum());
	}

	@Test
	public void testIsNegative() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(true, context.valueOf("-1E999").isNegative());
		assertEquals(true, context.valueOf("-1E-999").isNegative());
		assertEquals(true, context.valueOf(-5).isNegative());
		assertEquals(false, context.valueOf(0).isNegative());
		assertEquals(false, context.valueOf("0.000000").isNegative());
		assertEquals(false, context.valueOf(5).isNegative());
		assertEquals(false, context.valueOf("1E999").isNegative());
		assertEquals(false, context.valueOf("1E-999").isNegative());
	}

	@Test
	public void testIsZero() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(false, context.valueOf("-1E999").isZero());
		assertEquals(false, context.valueOf("-1E-999").isZero());
		assertEquals(false, context.valueOf(-5).isZero());
		assertEquals(true, context.valueOf(0).isZero());
		assertEquals(true, context.valueOf("0.000000").isZero());
		assertEquals(false, context.valueOf(5).isZero());
		assertEquals(false, context.valueOf("1E999").isZero());
		assertEquals(false, context.valueOf("1E-999").isZero());
	}

	@Test
	public void testIsPositive() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(false, context.valueOf("-1E999").isPositive());
		assertEquals(false, context.valueOf("-1E-999").isPositive());
		assertEquals(false, context.valueOf(-5).isPositive());
		assertEquals(false, context.valueOf(0).isPositive());
		assertEquals(false, context.valueOf("0.000000").isPositive());
		assertEquals(true, context.valueOf(5).isPositive());
		assertEquals(true, context.valueOf("1E999").isPositive());
		assertEquals(true, context.valueOf("1E-999").isPositive());
	}

	@Test
	public void testIsEqual() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(true, context.valueOf(5).isEqual(context.valueOf(5)));
		assertEquals(true, context.valueOf(5).isEqual(context.valueOf(5.0)));
		assertEquals(false, context.valueOf(1).isEqual(context.valueOf(5)));
	}

	@Test
	public void testIsLessThan() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(true, context.valueOf(1).isLessThan(context.valueOf(5)));
		assertEquals(false, context.valueOf(5).isLessThan(context.valueOf(1)));
		assertEquals(false, context.valueOf(5).isLessThan(context.valueOf(5)));
	}

	@Test
	public void testIsLessThanOrEqual() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(true, context.valueOf(1).isLessThanOrEqual(context.valueOf(5)));
		assertEquals(false, context.valueOf(5).isLessThanOrEqual(context.valueOf(1)));
		assertEquals(true, context.valueOf(5).isLessThanOrEqual(context.valueOf(5)));
	}

	@Test
	public void testIsGreaterThan() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(true, context.valueOf(5).isGreaterThan(context.valueOf(1)));
		assertEquals(false, context.valueOf(1).isGreaterThan(context.valueOf(5)));
		assertEquals(false, context.valueOf(5).isGreaterThan(context.valueOf(5)));
	}

	@Test
	public void testIsGreaterThanOrEqual() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(true, context.valueOf(5).isGreaterThanOrEqual(context.valueOf(1)));
		assertEquals(false, context.valueOf(1).isGreaterThanOrEqual(context.valueOf(5)));
		assertEquals(true, context.valueOf(5).isGreaterThanOrEqual(context.valueOf(5)));
	}

	@Test
	public void testCompareTo() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(1, context.valueOf(5).compareTo(context.valueOf(1)));
		assertEquals(-1, context.valueOf(1).compareTo(context.valueOf(5)));
		assertEquals(0, context.valueOf(5).compareTo(context.valueOf(5)));
	}

	@Test
	public void testAdd() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(context.valueOf(5), context.valueOf(2).add(3));
		assertEquals(context.valueOf(5), context.valueOf(2).add(3L));
		assertEquals(context.valueOf(5), context.valueOf(2).add(3.0));
		assertEquals(context.valueOf(5), context.valueOf(2).add(BigDecimal.valueOf(3)));
		assertEquals(context.valueOf(5), context.valueOf(2).add(context.valueOf(3)));
	}

	@Test
	public void testAddBigFloat() {
		Context smallContext = context(MathContext.DECIMAL32);
		Context largeContext = context(MathContext.DECIMAL64);

		assertEquals(smallContext, smallContext.valueOf(2).add(smallContext.valueOf(3)).getContext());
		assertEquals(largeContext, smallContext.valueOf(2).add(largeContext.valueOf(3)).getContext());
		assertEquals(largeContext, largeContext.valueOf(2).add(smallContext.valueOf(3)).getContext());
		assertEquals(largeContext, largeContext.valueOf(2).add(largeContext.valueOf(3)).getContext());
	}

	@Test
	public void testSubtract() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(context.valueOf(2), context.valueOf(5).subtract(3));
		assertEquals(context.valueOf(2), context.valueOf(5).subtract(3L));
		assertEquals(context.valueOf(2), context.valueOf(5).subtract(3.0));
		assertEquals(context.valueOf(2), context.valueOf(5).subtract(BigDecimal.valueOf(3)));
		assertEquals(context.valueOf(2), context.valueOf(5).subtract(context.valueOf(3)));
	}

	@Test
	public void testMultiply() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(context.valueOf(6), context.valueOf(2).multiply(3));
		assertEquals(context.valueOf(6), context.valueOf(2).multiply(3L));
		assertEquals(context.valueOf(6), context.valueOf(2).multiply(3.0));
		assertEquals(context.valueOf(6), context.valueOf(2).multiply(BigDecimal.valueOf(3)));
		assertEquals(context.valueOf(6), context.valueOf(2).multiply(context.valueOf(3)));
	}

	@Test
	public void testDivide() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(context.valueOf(2), context.valueOf(6).divide(3));
		assertEquals(context.valueOf(2), context.valueOf(6).divide(3L));
		assertEquals(context.valueOf(2), context.valueOf(6).divide(3.0));
		assertEquals(context.valueOf(2), context.valueOf(6).divide(BigDecimal.valueOf(3)));
		assertEquals(context.valueOf(2), context.valueOf(6).divide(context.valueOf(3)));
	}

	@Test
	public void testRemainder() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(context.valueOf(1), context.valueOf(10).remainder(3));
		assertEquals(context.valueOf(1), context.valueOf(10).remainder(3L));
		assertEquals(context.valueOf(1), context.valueOf(10).remainder(3.0));
		assertEquals(context.valueOf(1), context.valueOf(10).remainder(BigDecimal.valueOf(3)));
		assertEquals(context.valueOf(1), context.valueOf(10).remainder(context.valueOf(3)));
	}

	@Test
	public void testPow() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(context.valueOf(8), context.valueOf(2).pow(3));
		assertEquals(context.valueOf(8), context.valueOf(2).pow(3L));
		assertEquals(context.valueOf(8), context.valueOf(2).pow(3.0));
		assertEquals(context.valueOf(8), context.valueOf(2).pow(BigDecimal.valueOf(3)));
		assertEquals(context.valueOf(8), context.valueOf(2).pow(context.valueOf(3)));
	}

	@Test
	public void testRoot() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(context.valueOf(2), context.valueOf(8).root(3));
		assertEquals(context.valueOf(2), context.valueOf(8).root(3L));
		assertEquals(context.valueOf(2), context.valueOf(8).root(3.0));
		assertEquals(context.valueOf(2), context.valueOf(8).root(BigDecimal.valueOf(3)));
		assertEquals(context.valueOf(2), context.valueOf(8).root(context.valueOf(3)));
	}

	@Test
	public void testFactorial() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(context.valueOf(720), context.factorial(6));
	}

	@Test
	public void testLog() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(log(context.valueOf(3)).toBigDecimal(),
				BigDecimalMath.log(BigDecimal.valueOf(3), MathContext.DECIMAL32));
	}

	@Test
	public void testLog2() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(log2(context.valueOf(3)).toBigDecimal(),
				BigDecimalMath.log2(BigDecimal.valueOf(3), MathContext.DECIMAL32));
	}

	@Test
	public void testLog10() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(log10(context.valueOf(3)).toBigDecimal(),
				BigDecimalMath.log10(BigDecimal.valueOf(3), MathContext.DECIMAL32));
	}

	@Test
	public void testExp() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(exp(context.valueOf(3)).toBigDecimal(),
				BigDecimalMath.exp(BigDecimal.valueOf(3), MathContext.DECIMAL32));
	}

	@Test
	public void testSqrt() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(BigDecimalMath.sqrt(BigDecimal.valueOf(3), MathContext.DECIMAL32),
				sqrt(context.valueOf(3)).toBigDecimal());
	}

	@Test
	public void testPow2() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(BigDecimalMath.pow(BigDecimal.valueOf(2), BigDecimal.valueOf(3), MathContext.DECIMAL32),
				context.valueOf(2).pow(3).toBigDecimal());
	}

	@Test
	public void testRoot2() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(BigDecimalMath.root(BigDecimal.valueOf(8), BigDecimal.valueOf(3), MathContext.DECIMAL32),
				context.valueOf(8).root(3).toBigDecimal());
	}

	@Test
	public void testPowStatic() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(BigDecimalMath.pow(BigDecimal.valueOf(2), BigDecimal.valueOf(3), MathContext.DECIMAL32),
				pow(context.valueOf(2), context.valueOf(3)).toBigDecimal());
	}

	@Test
	public void testRootStatic() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(BigDecimalMath.root(BigDecimal.valueOf(8), BigDecimal.valueOf(3), MathContext.DECIMAL32),
				root(context.valueOf(8), context.valueOf(3)).toBigDecimal());
	}

	@Test
	public void testSin() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(BigDecimalMath.sin(BigDecimal.valueOf(0), MathContext.DECIMAL32),
				sin(context.valueOf(0)).toBigDecimal());
	}

	@Test
	public void testCos() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(BigDecimalMath.cos(BigDecimal.valueOf(0), MathContext.DECIMAL32),
				cos(context.valueOf(0)).toBigDecimal());
	}

	@Test
	public void testTan() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(BigDecimalMath.tan(BigDecimal.valueOf(0), MathContext.DECIMAL32),
				tan(context.valueOf(0)).toBigDecimal());
	}

	@Test
	public void testCot() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(BigDecimalMath.cot(BigDecimal.valueOf(1), MathContext.DECIMAL32),
				cot(context.valueOf(1)).toBigDecimal());
	}

	@Test
	public void testAsin() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(BigDecimalMath.asin(BigDecimal.valueOf(0), MathContext.DECIMAL32),
				asin(context.valueOf(0)).toBigDecimal());
	}

	@Test
	public void testAcos() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(BigDecimalMath.acos(BigDecimal.valueOf(0), MathContext.DECIMAL32),
				acos(context.valueOf(0)).toBigDecimal());
	}

	@Test
	public void testAtan() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(BigDecimalMath.atan(BigDecimal.valueOf(0), MathContext.DECIMAL32),
				atan(context.valueOf(0)).toBigDecimal());
	}

	@Test
	public void testAcot() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(BigDecimalMath.acot(BigDecimal.valueOf(0), MathContext.DECIMAL32),
				acot(context.valueOf(0)).toBigDecimal());
	}

	@Test
	public void testSinh() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(BigDecimalMath.sinh(BigDecimal.valueOf(0), MathContext.DECIMAL32),
				sinh(context.valueOf(0)).toBigDecimal());
	}

	@Test
	public void testCosh() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(BigDecimalMath.cosh(BigDecimal.valueOf(0), MathContext.DECIMAL32),
				cosh(context.valueOf(0)).toBigDecimal());
	}

	@Test
	public void testCoth() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(BigDecimalMath.coth(BigDecimal.valueOf(1.1), MathContext.DECIMAL32),
				coth(context.valueOf(1.1)).toBigDecimal());
	}

	@Test
	public void testTanh() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(BigDecimalMath.tanh(BigDecimal.valueOf(0), MathContext.DECIMAL32),
				tanh(context.valueOf(0)).toBigDecimal());
	}

	@Test
	public void testAsinh() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(BigDecimalMath.asinh(BigDecimal.valueOf(0), MathContext.DECIMAL32),
				asinh(context.valueOf(0)).toBigDecimal());
	}

	@Test
	public void testAcosh() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(BigDecimalMath.acosh(BigDecimal.valueOf(1.1), MathContext.DECIMAL32),
				acosh(context.valueOf(1.1)).toBigDecimal());
	}

	@Test
	public void testAtanh() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(BigDecimalMath.atanh(BigDecimal.valueOf(0), MathContext.DECIMAL32),
				atanh(context.valueOf(0)).toBigDecimal());
	}

	@Test
	public void testAcoth() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(BigDecimalMath.acoth(BigDecimal.valueOf(1.1), MathContext.DECIMAL32),
				acoth(context.valueOf(1.1)).toBigDecimal());
	}

	@Test
	public void testAbs() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(context.valueOf(3), abs(context.valueOf(3)));
		assertEquals(context.valueOf(3), abs(context.valueOf(-3)));
		assertEquals(context.valueOf(0), abs(context.valueOf(0)));
	}

	@Test
	public void testNegate() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(context.valueOf(-3), negate(context.valueOf(3)));
		assertEquals(context.valueOf(3), negate(context.valueOf(-3)));
		assertEquals(context.valueOf(0), negate(context.valueOf(0)));
	}

	@Test
	public void testMax() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(context.valueOf(9), max(context.valueOf(9), context.valueOf(2)));
		assertEquals(context.valueOf(9), max(context.valueOf(2), context.valueOf(9)));

		assertEquals(context.valueOf(9), max(context.valueOf(2), context.valueOf(9), context.valueOf(3)));
		assertEquals(context.valueOf(9), max(context.valueOf(9), context.valueOf(2), context.valueOf(3)));
	}

	@Test
	public void testMin() {
		Context context = context(MathContext.DECIMAL32);
		assertEquals(context.valueOf(1), min(context.valueOf(1), context.valueOf(2)));
		assertEquals(context.valueOf(1), min(context.valueOf(2), context.valueOf(1)));

		assertEquals(context.valueOf(1), min(context.valueOf(2), context.valueOf(1), context.valueOf(3)));
		assertEquals(context.valueOf(1), min(context.valueOf(1), context.valueOf(2), context.valueOf(3)));
	}

	@SuppressWarnings("SimplifiableJUnitAssertion")
	@Test
	public void testSpecial() {
		Context context = context(MathContext.DECIMAL32);
		assertSame(NaN, context.valueOf(Double.NaN));
		assertSame(POSITIVE_INFINITY, context.valueOf(Double.POSITIVE_INFINITY));
		assertSame(NEGATIVE_INFINITY, context.valueOf(Double.NEGATIVE_INFINITY));

		//Double.NaN equals nothing even itself
		assertTrue(Double.isNaN(NaN.toDouble()));
		assertTrue(Double.isInfinite(POSITIVE_INFINITY.toDouble()));
		assertTrue(Double.isInfinite(NEGATIVE_INFINITY.toDouble()));

		//it's final so it can be with ==
		assertTrue(NEGATIVE_ONE.divide(ZERO) == NEGATIVE_INFINITY);
		assertTrue(ZERO.divide(ZERO) == NaN);
		assertTrue(ONE.divide(ZERO) == POSITIVE_INFINITY);
	}

	enum Op {
		PLUS("+"),
		MINUS("-"),
		MULTIPLY("*"),
		DIVIDE("/");

		public final String symbol;

		Op(String symbol) {
			this.symbol = symbol;
		}
	}

	@Test
	public void testOperationsWithSpecial() {
		List<Double> values = Arrays.asList(0.0, 2.0, -2.0, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);

		Context context = BigFloat.context(MathContext.DECIMAL64);

		for (Op op : Op.values()) {
			for (double left : values) {
				BigFloat leftFloat = context.valueOf(left);
				for (double right : values) {
					BigFloat rightFloat = context.valueOf(right);

					double result;
					BigFloat resultFloat;
					switch (op) {
						case PLUS:
							result = left + right;
							resultFloat = leftFloat.add(rightFloat);
							break;
						case MINUS:
							result = left - right;
							resultFloat = leftFloat.subtract(rightFloat);
							break;
						case MULTIPLY:
							result = left * right;
							resultFloat = leftFloat.multiply(rightFloat);
							break;
						case DIVIDE:
							result = left / right;
							resultFloat = leftFloat.divide(rightFloat);
							break;
						default:
							throw new RuntimeException("Unknown operation: " + op);
					}

					String description = left + " " + op.symbol + " " + right + " = " + result;
					assertEquals(description, result, resultFloat.toDouble(), 0.00000001);
					assertEquals(description, context.valueOf(result), resultFloat);
				}
			}
		}
	}
}