/**
 * Represents a bomb disc in the game, which may have special behaviors.
 */
public class BombDisc implements Disc {
    private Player owner;

    /**
     * Constructs a BombDisc with the specified owner.
     *
     * @param Player the owner of the disc.
     */
    public BombDisc(Player Player) {
        this.owner = Player;
    }
    /**
     * Copy constructor for BombDisc.
     *
     * @param disc the disc to copy.
     */
    public BombDisc(BombDisc disc){
        this.owner = disc.getOwner();
    }

    @Override
    public Player getOwner() {
        return owner;
    }

    @Override
    public void setOwner(Player player) {
        owner = player;}

    @Override
    public String getType() {
        return "💣";
    }
}
