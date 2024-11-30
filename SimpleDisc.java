    /**
     * Represents a simple disc in the game, which can be flipped.
     */
public class SimpleDisc implements Disc {
    private Player owner;

    /**
     * Constructs a SimpleDisc with the specified owner.
     *
     * @param Player the owner of the disc.
     */
    public SimpleDisc(Player Player) {
        owner = Player;
    }
    /**
     * Copy constructor for SimpleDisc.
     *
     * @param disc the disc to copy.
     */
    public SimpleDisc(SimpleDisc disc) {
        this.owner = disc.getOwner();
    }

    @Override
    public Player getOwner() {
        return owner;
    }

    @Override
    public void setOwner(Player player) {
        owner = player;
    }

    @Override
    public String getType() {
        return "⬤";
    }
}
