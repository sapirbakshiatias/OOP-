public class UnflippableDisc implements Disc {
    Player owner;
    public UnflippableDisc(Player player) {
        owner = player;
    }
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
