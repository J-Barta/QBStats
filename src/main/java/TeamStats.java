public class TeamStats {
    private String name;
    private double ppg;
    private double powers;
    private double gets;
    private double ppb;
    private double gamesPlayed;

    public TeamStats(String name, double ppg, double powers, double regs, double ppb, double gamesPlayed) {
        this.name = name;
        this.ppg = ppg;
        this.powers = powers;
        this.gets = powers + regs;
        this.ppb = ppb;
        this.gamesPlayed = gamesPlayed;
    }

    @Override
    public String toString() {
        return "name: " + name +
                " ppg: " + ppg +
                " powers: " + powers +
                " gets: " + gets +
                " ppb: " + ppb +
                " gamesPlayed: " + gamesPlayed
                ;
    }

    /**
     * Combine the stats of a team with their stats from another part of the tournament (prelims, etc)
     * @param other
     */
    public void combineWith(TeamStats other) {
        this.ppg = (this.ppg * this.gamesPlayed + other.ppg * other.gamesPlayed)/ (this.gamesPlayed + other.gamesPlayed);
        this.ppb = (this.ppb * this.gamesPlayed + other.ppb * other.gamesPlayed)/ (this.gamesPlayed + other.gamesPlayed);

        this.powers += other.powers;
        this.gets += other.gets;

        this.gamesPlayed += other.gamesPlayed;
    }

    public String getName() {
        return name;
    }

    public double getPowersPerGame() {
        return powers/gamesPlayed;
//                Math.round((powers/gamesPlayed) * 10)/10;
    }

    public double getsPerGame() {
        return gets/gamesPlayed;
                //Math.round((gets/gamesPlayed) * 10)/10;
    }

    public double getPpg() {
        return ppg;
                //Math.round(ppg * 10)/10;
    }

    public double getPpb() {
        return ppb;
                //Math.round(ppb * 10)/10;
    }
}
