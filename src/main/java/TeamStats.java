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

    public String getName() {
        return name;
    }

    public double getPowersPerGame() {
        return powers/gamesPlayed;
    }

    public double getsPerGame() {
        return gets/gamesPlayed;
    }

    public double getPpg() {
        return ppg;
    }

    public double getPpb() {
        return ppb;
    }
}
