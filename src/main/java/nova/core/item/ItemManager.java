package nova.core.item;

import nova.core.block.Block;
import nova.core.block.BlockManager;
import nova.core.event.EventBus;
import nova.core.event.EventListener;
import nova.core.event.EventListenerHandle;
import nova.core.item.event.ItemIDNotFoundEvent;
import nova.core.util.ReflectionUtil;
import nova.core.util.Registry;

import java.util.Optional;
import java.util.function.Supplier;

public class ItemManager {

	public final Registry<ItemFactory> registry;
	private final Supplier<BlockManager> blockManager;

	private final EventBus<ItemIDNotFoundEvent> idNotFoundListeners = new EventBus<>();
	private final EventBus<ItemRegistrationEvent> itemRegistryListeners = new EventBus<>();

	private ItemManager(Registry<ItemFactory> itemRegistry, Supplier<BlockManager> blockManager) {
		this.registry = itemRegistry;
		this.blockManager = blockManager;
	}

	//TODO: Return an item factory
	public Item register(Class<? extends Item> item) {
		return register(() -> ReflectionUtil.newInstance(item));
	}

	/**
	 * Register a new item with custom constructor arguments.
	 * @param constructor The lambda expression to create a new constructor.
	 * @return Dummy item
	 */
	public Item register(Supplier<Item> constructor) {
		return register(new ItemFactory(constructor));
	}

	public Item register(ItemFactory factory) {
		registry.register(factory);

		itemRegistryListeners.publish(new ItemRegistrationEvent(factory));

		return factory.getDummy();
	}

	public ItemFactory getItemFactoryFromBlock(Block block) {
		return registry.get(block.getID()).get();
	}

	public Item getItemFromBlock(Block block) {
		return getItemFactoryFromBlock(block).getDummy();
	}

	public Optional<Block> getBlockFromItem(Item item) {
		return blockManager.get().getBlock(item.getID());
	}

	/**
	 * Using this method will only get the dummy item. Use ItemFactory instead!
	 */
	@Deprecated
	public Optional<Item> getItem(String name) {
		Optional<ItemFactory> factory = getItemFactory(name);
		if (factory.isPresent()) {
			return Optional.of(factory.get().getDummy());
		} else {
			return Optional.empty();
		}
	}

	public Optional<ItemFactory> getItemFactory(String name) {
		if (!registry.contains(name)) {
			ItemIDNotFoundEvent event = new ItemIDNotFoundEvent(name);
			idNotFoundListeners.publish(event);

			if (event.getRemappedFactory() != null) {
				registry.register(event.getRemappedFactory());
			}
		}

		return registry.get(name);
	}

	public EventListenerHandle<ItemIDNotFoundEvent> whenIDNotFound(EventListener<ItemIDNotFoundEvent> listener) {
		return idNotFoundListeners.add(listener);
	}

	public EventListenerHandle<ItemRegistrationEvent> whenItemRegistered(EventListener<ItemRegistrationEvent> listener) {
		return itemRegistryListeners.add(listener);
	}

	public class ItemRegistrationEvent {
		public final ItemFactory itemFactory;

		public ItemRegistrationEvent(ItemFactory itemFactory) {
			this.itemFactory = itemFactory;
		}
	}
}
