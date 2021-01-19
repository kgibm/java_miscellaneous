import java.math.BigDecimal;
import java.util.Date;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BurnSomeCPU {
	public static void main(String[] args) {
		int iterations = Integer.getInteger("ITERATIONS", 50);
		boolean morework = Boolean.parseBoolean(System.getProperty("MOREWORK", "true"));
		int moreWorkIterations = Integer.getInteger("MOREWORK_ITERATIONS", 50);

		for (int i = 0; i < iterations; i++) {
			Pattern p = Pattern.compile(
					"(@)?(href=')?(HREF=')?(HREF=\")?(href=\")?(http://)?[a-zA-Z_0-9\\-]+(\\.\\w[a-zA-Z_0-9\\-]+)+(/[#&\\n\\-=?\\+\\%/\\.\\w]+)?",
					Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
			Matcher m = p.matcher("<a href='test.html'>");
			while (m.find()) {
				String href = m.group();
			}

			BigDecimal pi = computePi(1000);
			pi.add(new BigDecimal("1000"));

			String data = "a";
			int j = i * 100;
			while (j-- > 0) {
				data += "a";
			}
			System.out.println("Completed iteration " + (i + 1));
		}

		if (morework) {
			Hashtable<Date, String> dates = new Hashtable<Date, String>();
			for (int i = 1; i < moreWorkIterations; i++) {
				try {
					Thread.sleep((int) (Math.random() * 300));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (0 == i % 5) {
					// Generate a random Date and index by its text representation
					for (int j = 0; j < 9; j++) {
						Date date = new Date((long) (Math.random() * 100000000));
						String dateString = date.toString();
						dates.put(date, dateString);
					}
				} else {
					String lPif = new String("Pif!");
					Integer lInt = new Integer(28);
				}
				System.out.println("Completed additional iteration " + (i + 1));
			}
		}
	}

	/** constants used in pi computation */
	private static final BigDecimal FOUR = BigDecimal.valueOf(4);

	/** rounding mode to use during pi computation */
	private static final int roundingMode = BigDecimal.ROUND_HALF_EVEN;

	/**
	 * Compute the value of pi to the specified number of digits after the decimal
	 * point. The value is computed using Machin's formula:
	 * 
	 * pi/4 = 4*arctan(1/5) - arctan(1/239)
	 * 
	 * and a power series expansion of arctan(x) to sufficient precision.
	 */
	public static BigDecimal computePi(int digits) {
		int scale = digits + 5;
		BigDecimal arctan1_5 = arctan(5, scale);
		BigDecimal arctan1_239 = arctan(239, scale);
		BigDecimal pi = arctan1_5.multiply(FOUR).subtract(arctan1_239).multiply(FOUR);
		return pi.setScale(digits, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * Compute the value, in radians, of the arctangent of the inverse of the
	 * supplied integer to the specified number of digits after the decimal point.
	 * The value is computed using the power series expansion for the arc tangent:
	 * 
	 * arctan(x) = x - (x^3)/3 + (x^5)/5 - (x^7)/7 + (x^9)/9 ...
	 */
	public static BigDecimal arctan(int inverseX, int scale) {
		BigDecimal result, numer, term;
		BigDecimal invX = BigDecimal.valueOf(inverseX);
		BigDecimal invX2 = BigDecimal.valueOf(inverseX * inverseX);

		numer = BigDecimal.ONE.divide(invX, scale, roundingMode);

		result = numer;
		int i = 1;
		do {
			numer = numer.divide(invX2, scale, roundingMode);
			int denom = 2 * i + 1;
			term = numer.divide(BigDecimal.valueOf(denom), scale, roundingMode);
			if ((i % 2) != 0) {
				result = result.subtract(term);
			} else {
				result = result.add(term);
			}
			i++;
		} while (term.compareTo(BigDecimal.ZERO) != 0);
		return result;
	}
}
