import java.util.ArrayList;
import java.util.List;

public class BurnSomeMemory {
	public static void main(String[] args) {
		int iterations = Integer.getInteger("ITERATIONS", 50);
		int blockSize = Integer.getInteger("BLOCK_SIZE", 1048576);
		boolean keepBlocks = Boolean.getBoolean("KEEP_BLOCKS");

		List<byte[]> list = new ArrayList<>();
		for (int i = 0; i < iterations; i++) {
			byte[] block = new byte[blockSize];
			if (keepBlocks) {
				list.add(block);
			}
			System.out.println("Completed iteration " + (i + 1) + " " + block + " " + list.size());
		}
		System.out.println("Finished.");
	}
}
