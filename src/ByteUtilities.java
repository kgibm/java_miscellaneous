
/**
 * @author kevin.grigorenko@us.ibm.com
 */
public class ByteUtilities {
	public static void printBuffer(byte[] data) {
		printBuffer(data, 0);
	}

	public static void printBuffer(byte[] data, int offset) {
		int bytesPrinted = 0;
		byte[] printed = new byte[16];
		int printedOffset = 0;
		int i = offset;
		while (i < data.length) {
			if (data[i] == 0x7F && data[i + 1] == (byte) 0xFF && data[i + 2] == (byte) 0xFF) {
				if (bytesPrinted > 0) {
					finishPrintingDebug(bytesPrinted, printed, printedOffset);
				}
				return;
			} else {
				if (bytesPrinted == 0) {
					System.out.print(String.format("%08X", bytesPrinted) + "  ");
				}
				System.out.print(String.format("%02X", data[i]));
				printed[printedOffset++] = data[i];
				i++;
				bytesPrinted++;

				if ((bytesPrinted % 16) == 0) {
					printReadableCharacters(printed, printedOffset);

					System.out.print(String.format("%08X", bytesPrinted) + "  ");

					printed = new byte[16];
					printedOffset = 0;
				} else if ((bytesPrinted % 8) == 0) {
					System.out.print(' ');
				}
			}
		}
		finishPrintingDebug(bytesPrinted, printed, printedOffset);
	}

	protected static void finishPrintingDebug(int bytesPrinted, byte[] printed, int printedOffset) {
		while ((bytesPrinted % 16) != 0) {
			System.out.print("  ");
			bytesPrinted++;

			if ((bytesPrinted % 16) != 0 && (bytesPrinted % 8) == 0) {
				System.out.print(' ');
			}
		}
		printReadableCharacters(printed, printedOffset);
		System.out.println();
	}

	protected static void printReadableCharacters(byte[] printed, int printedOffset) {
		System.out.print("  |");
		for (int j = 0; j < printedOffset; j++) {
			if (printed[j] >= 0x20 && printed[j] <= 127) {
				System.out.print((char) printed[j]);
			} else {
				System.out.print('.');
			}
		}
		System.out.println("|");
	}
}
