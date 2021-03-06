package nova.core.block;

import nova.core.util.transform.Vector3i;

import java.util.Optional;

/**
 * A generic interface allowing you to interact with a block by its position.
 */
public interface BlockAccess {
	/**
	 * Gets the block which occupies the given position.
	 *
	 * @param position The position to query.
	 * @return The block at the position.
	 */
	Optional<Block> getBlock(Vector3i position);

	/**
	 * Sets the block occupying a given position.
	 *
	 * @param position The position of the block to set.
	 * @param block The block.
	 * @return {@code true} if the replace was successful.
	 */
	boolean setBlock(Vector3i position, Block block);

	/**
	 * Removes the block in the specified position.
	 *
	 * @param position the position of the block to remove.
	 * @return {@code true} if the block was removed.
	 */
	boolean removeBlock(Vector3i position);
}