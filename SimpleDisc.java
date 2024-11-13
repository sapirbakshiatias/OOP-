public class SimpleDisc implements Disc {
    private Player owner;

    public SimpleDisc(Player Player) {
        owner = Player;
    }

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
