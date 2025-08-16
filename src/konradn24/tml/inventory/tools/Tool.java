package konradn24.tml.inventory.tools;

import konradn24.tml.entities.creatures.characters.Player;
import konradn24.tml.inventory.items.Item;

public abstract class Tool extends Item {

	public static final int ACTION_CHOP = 100;
	
	private static final int DEFAULT_DURABILITY = 64, DEFAULT_HEAL = 0;
	
	protected int durability, maxDurability;
	protected int damage, heal, range;
	
	public Tool() {
		super();
		
		durability = DEFAULT_DURABILITY;
		maxDurability = DEFAULT_DURABILITY;
		damage = Player.DEFAULT_DAMAGE;
		heal = DEFAULT_HEAL;
		range = Player.DEFAULT_RANGE;
	}
	
	public abstract void onUse();

	public boolean isTool() {
		return true;
	}
	
	//GETTERS AND SETTERS
	public int getDurability() {
		return durability;
	}

	public void setDurability(int durability) {
		this.durability = durability;
	}

	public int getMaxDurability() {
		return maxDurability;
	}

	public void setMaxDurability(int maxDurability) {
		this.maxDurability = maxDurability;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public int getHeal() {
		return heal;
	}

	public void setHeal(int heal) {
		this.heal = heal;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}
}
