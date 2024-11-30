/**
 * Represents an unflippable disc in the game.
 */
public class UnflippableDisc implements Disc {
    private Player owner;
    /**
     * Constructs an UnflippableDisc with the specified owner.
     *
     * @param player the owner of the disc.
     */
    public UnflippableDisc(Player player) {
        owner = player;
    }
    /**
     * Copy constructor for UnflippableDisc.
     *
     * @param disc the disc to copy.
     */
    public UnflippableDisc(UnflippableDisc disc){
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
        return "⭕";
    }
}
