package shared;

import shared.interfaces.IFonds;

public class Fonds implements IFonds {

    private final String naam;
    private double koers;

    public Fonds(String naam, double koers) {
        this.naam = naam;
        this.koers = koers;
    }

    @Override
    public String getNaam() {
        return naam;
    }

    @Override
    public double getKoers() {
        return koers;
    }

    public void setKoers(double koers) {
        this.koers = (this.koers + koers < 0.0) ? 0.0 : koers;
    }
}
