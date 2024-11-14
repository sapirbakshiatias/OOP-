public class BombDisc implements Disc {
    private Player owner;
    public BombDisc(Player Player) {
        this.owner = Player;
    }
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
