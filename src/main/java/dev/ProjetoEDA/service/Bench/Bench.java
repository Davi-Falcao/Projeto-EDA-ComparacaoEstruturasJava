package dev.ProjetoEDA.service.Bench;

public abstract class Bench {

    // ---------------- 10^3 RANDOM ----------------

    public abstract void n1e3Random50Insert25Remove25Search();
    public abstract void n1e3Random75Insert25Remove();
    public abstract void n1e3Random75Insert25Search();
    public abstract void n1e3Random100Insert();

    // ---------------- 10^3 CRESCENTE ----------------

    public abstract void n1e3Crescente50Insert25Remove25Search();
    public abstract void n1e3Crescente75Insert25Remove();
    public abstract void n1e3Crescente75Insert25Search();
    public abstract void n1e3Crescente100Insert();

    // ---------------- 10^3 DECRESCENTE ----------------

    public abstract void n1e3Decrescente50Insert25Remove25Search();
    public abstract void n1e3Decrescente75Insert25Remove();
    public abstract void n1e3Decrescente75Insert25Search();
    public abstract void n1e3Decrescente100Insert();

    // ---------------- 10^6 RANDOM ----------------

    public abstract void n1e6Random50Insert25Remove25Search();
    public abstract void n1e6Random75Insert25Remove();
    public abstract void n1e6Random75Insert25Search();
    public abstract void n1e6Random100Insert();

    // ---------------- 10^6 CRESCENTE ----------------

    public abstract void n1e6Crescente50Insert25Remove25Search();
    public abstract void n1e6Crescente75Insert25Remove();
    public abstract void n1e6Crescente75Insert25Search();
    public abstract void n1e6Crescente100Insert();

    // ---------------- 10^6 DECRESCENTE ----------------

    public abstract void n1e6Decrescente50Insert25Remove25Search();
    public abstract void n1e6Decrescente75Insert25Remove();
    public abstract void n1e6Decrescente75Insert25Search();
    public abstract void n1e6Decrescente100Insert();

}