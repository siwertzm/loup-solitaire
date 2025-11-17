package com.loupsolitaire.backend;
import java.util.Random;

public class CombatSimulator {

    // ============================
    // Tables issues de ton CombatService
    // ============================

    private static final int[][] TABLE_LS = {
        { -6, 0,  0,  0,  0, -1, -2, -3, -4, -5 }, // col 0 : -11 ou +
        { -7, 0,  0,  0, -1, -2, -3, -4, -5, -6 }, // col 1 : -10/-9
        { -8, 0,  0, -1, -2, -3, -4, -5, -6, -7 }, // col 2 : -8/-7
        { -9, 0, -1, -2, -3, -4, -5, -6, -7, -8 }, // col 3 : -6/-5
        { -10, -1, -2, -3, -4, -5, -6, -7, -8, -9 }, // col 4 : -4/-3
        { -11, -2, -3, -4, -5, -6, -7, -8, -9, -10 }, // col 5 : -2/-1
        { -12, -3, -4, -5, -6, -7, -8, -9, -10, -11 }, // col 6 : 0
        { -14, -4, -5, -6, -7, -8, -9, -10, -11, -12 }, // col 7 : 1/2
        { -16, -5, -6, -7, -8, -9, -10, -11, -12, -14 }, // col 8 : 3/4
        { -18, -6, -7, -8, -9, -10, -11, -12, -14, -16 }, // col 9 : 5/6
        { -999, -7, -8, -9, -10, -11, -12, -14, -16, -18 }, // col 10 : 7/8
        { -999, -8, -9, -10, -11, -12, -14, -16, -18, -999 }, // col 11 : 9/10
        { -999, -9, -10, -11, -12, -14, -16, -18, -999, -999 }  // col 12 : 11 ou +
    };

    private static final int[][] TABLE_E = {
    { -999, 0,  -3,  -4,  -5,  -6,  -7,  -8,  -9, -999 }, // col 0 : -11 ou +
    { -999, 0,  -3,  -4,  -5,  -6,  -6,  -7,  -7,  -10 }, // col 1 : -10/-9
    { -10, 0,  -2,  -3,  -4,  -5,  -5,  -6,  -6,  -8 }, // col 2 : -8/-7
    { -8, 0,  -1,  -2,  -3,  -4,  -4,  -5,  -5,  -6 }, // col 3 : -6/-5
    { -7, 0,  -1,  -1,  -2,  -3,  -4,  -4,  -5,  -5 }, // col 4 : -4/-3
    { -6, 0,  -1,  -1,  -2,  -2,  -3,  -4,  -4,  -5 }, // col 5 : -2/-1
    { -5, 0,   0,  -1,  -1,  -2,  -2,  -3,  -4,  -4 }, // col 6 : 0
    { -5, 0,   0,  -1,  -1,  -2,  -2,  -3,  -3,  -4 }, // col 7 : 1/2
    { -4, 0,   0,  -1,  -1,  -2,  -2,  -2,  -3,  -3 }, // col 8 : 3/4
    { -4, 0,   0,   0,  -1,  -1,  -2,  -2,  -3,  -3 }, // col 9 : 5/6
    { -4, 0,   0,   0,   0,  -1,  -2,  -2,  -2,  -3 }, // col 10 : 7/8
    { -3, 0,   0,   0,   0,  -1,  -2,  -2,  -2,  -3 }, // col 11 : 9/10
    { -3, 0,   0,   0,   0,  -1,  -1,  -2,  -2,  -2 }, // col 12 : 11 ou +
};;

    private static final int[] REDUCTION_DEFENSE = {100,10,20,30,40,50,60,70,75,80};
    private static final int[] BONUS_HABILITE    = {0,0,0,1,1,2,2,2,3,3};

    private final Random rng = new Random();

    // ============================
    // Types pour la simulation
    // ============================

    public enum PlayerStrategy {
        ALWAYS_ATTACK,
        DEFEND_FIRST_THEN_ATTACK,
        DEFEND_WHEN_LOW_HP
    }

    public static class FightResult {
        public boolean joueurGagne;
        public int tours;
        public int endJoueurFinale;
        public int endEnnemiFinale;
    }

    public static class SimulationStats {
        public int combats;
        public int victoires;
        public double tauxVictoire;
        public double toursMoyens;
        public double endJoueurMoy;
        public double endEnnemiMoy;

        @Override
        public String toString() {
            return "Combats=" + combats +
                   ", Victoires=" + victoires +
                   ", TauxVictoire=" + String.format("%.2f%%", tauxVictoire * 100) +
                   ", ToursMoyens=" + String.format("%.2f", toursMoyens) +
                   ", EndJoueurMoy=" + String.format("%.2f", endJoueurMoy) +
                   ", EndEnnemiMoy=" + String.format("%.2f", endEnnemiMoy);
        }
    }

    // ============================
    // API publique : simulation
    // ============================

    public SimulationStats simulerPlusieursCombats(
            int nbCombats,
            int habJoueur,
            int endJoueur,
            int habEnnemi,
            int endEnnemi,
            PlayerStrategy strategy
    ) {
        SimulationStats stats = new SimulationStats();
        stats.combats = nbCombats;

        long totalTours = 0;
        long totalEndJ = 0;
        long totalEndE = 0;
        int victoires = 0;

        for (int i = 0; i < nbCombats; i++) {
            FightResult result = simulerUnCombat(
                    habJoueur, endJoueur,
                    habEnnemi, endEnnemi,
                    strategy
            );

            if (result.joueurGagne) victoires++;
            totalTours += result.tours;
            totalEndJ += Math.max(result.endJoueurFinale, 0);
            totalEndE += Math.max(result.endEnnemiFinale, 0);
        }

        stats.victoires = victoires;
        stats.tauxVictoire = (double) victoires / nbCombats;
        stats.toursMoyens = (double) totalTours / nbCombats;
        stats.endJoueurMoy = (double) totalEndJ / nbCombats;
        stats.endEnnemiMoy = (double) totalEndE / nbCombats;

        return stats;
    }

    public FightResult simulerUnCombat(
            int habJoueurBase,
            int endJoueurBase,
            int habEnnemiBase,
            int endEnnemiBase,
            PlayerStrategy strategy
    ) {
        int habJoueurTemp = 0;
        int endJoueur = endJoueurBase;
        int habEnnemi = habEnnemiBase;
        int endEnnemi = endEnnemiBase;

        int reductionDegat = 0;
        int bonusHabilite = 0;
        boolean combatTermine = false;

        int tour = 1;
        boolean joueurAGagne = false;

        while (!combatTermine && tour < 1000) { // garde-fou
            Action action = choisirAction(strategy, tour, endJoueur, endJoueurBase);

            if (action == Action.ATTACK) {
                // Attaque du joueur avec bonusHabilite
                int habJ = habJoueurBase + habJoueurTemp + bonusHabilite;
                int diff = habJ - habEnnemi;
                int col = diffToColumn(diff);
                int hasard = tirageHasard();
                int degats = TABLE_LS[col][hasard];

                endEnnemi += degats; // degats négatifs => perte
                bonusHabilite = 0;   // bonus consommé à l’attaque

                if (endEnnemi <= 0) {
                    combatTermine = true;
                    joueurAGagne = true;
                }

                if (!combatTermine) {
                    // Attaque de l’ennemi SANS réduction ni bonus
                    int habJ2 = habJoueurBase + habJoueurTemp;
                    int diff2 = habJ2 - habEnnemi;
                    int col2 = diffToColumn(diff2);
                    int hasard2 = tirageHasard();
                    int degatsBase = TABLE_E[col2][hasard2];

                    // pas de réduction ici (car pas en défense)
                    endJoueur = endJoueur + degatsBase;

                    if (endJoueur <= 0) {
                        combatTermine = true;
                        joueurAGagne = false;
                    }
                }

            } else if (action == Action.DEFEND) {
                // Défense : tirage pour réduction + bonus futur
                int hasard = tirageHasard();
                reductionDegat = REDUCTION_DEFENSE[hasard];
                bonusHabilite = BONUS_HABILITE[hasard];

                // L’ennemi attaque tout de suite, avec réductionDegat
                int habJ = habJoueurBase + habJoueurTemp; // pas de bonus sur son attaque
                int diff = habJ - habEnnemi;
                int col = diffToColumn(diff);
                int hasard2 = tirageHasard();
                int degatsBase = TABLE_E[col][hasard2];

                if (reductionDegat > 0) {
                    double d = degatsBase * (100.0 - reductionDegat) / 100.0;
                    // Arrondi "dur" : vers -∞ pour les négatifs
                    degatsBase = (int) Math.floor(d);
                }

                endJoueur = endJoueur + degatsBase;

                // après l’attaque ennemie, on enlève juste la réduction,
                // mais on GARDE le bonusHabilite pour la prochaine attaque du joueur
                reductionDegat = 0;

                if (endJoueur <= 0) {
                    combatTermine = true;
                    joueurAGagne = false;
                }
            }

            tour++;
        }

        FightResult fr = new FightResult();
        fr.joueurGagne = joueurAGagne;
        fr.tours = tour - 1;
        fr.endJoueurFinale = endJoueur;
        fr.endEnnemiFinale = endEnnemi;
        return fr;
    }

    // ============================
    // Stratégies & utils
    // ============================

    private enum Action {
        ATTACK, DEFEND
    }

    private Action choisirAction(PlayerStrategy strategy, int tour, int endJ, int endJMax) {
        switch (strategy) {
            case ALWAYS_ATTACK:
                return Action.ATTACK;
            case DEFEND_FIRST_THEN_ATTACK:
                return (tour == 1) ? Action.DEFEND : Action.ATTACK;
            case DEFEND_WHEN_LOW_HP:
                double ratio = (double) endJ / endJMax;
                if (ratio < 0.3) {
                    return Action.DEFEND;
                } else {
                    return Action.ATTACK;
                }
            default:
                return Action.ATTACK;
        }
    }

    private int diffToColumn(int diff) {
        if (diff <= -11) return 0;
        if (diff <= -9)  return 1;
        if (diff <= -7)  return 2;
        if (diff <= -5)  return 3;
        if (diff <= -3)  return 4;
        if (diff <= -1)  return 5;
        if (diff == 0)   return 6;
        if (diff <= 2)   return 7;
        if (diff <= 4)   return 8;
        if (diff <= 6)   return 9;
        if (diff <= 8)   return 10;
        if (diff <= 10)  return 11;
        return 12;
    }

    private int tirageHasard() {
        return rng.nextInt(10); // 0..9
    }

    // ============================
    // Petit main de test
    // ============================

    public static void main(String[] args) {
        CombatSimulator sim = new CombatSimulator();

        int habJ = 17;
        int endJ = 24;
        int habE = 20;
        int endE = 30;

        System.out.println("=== Strategie: ALWAYS_ATTACK ===");
        SimulationStats s1 = sim.simulerPlusieursCombats(
                10_000, habJ, endJ, habE, endE, PlayerStrategy.ALWAYS_ATTACK
        );
        System.out.println(s1);

        System.out.println("=== Strategie: DEFEND_FIRST_THEN_ATTACK ===");
        SimulationStats s2 = sim.simulerPlusieursCombats(
                10_000, habJ, endJ, habE, endE, PlayerStrategy.DEFEND_FIRST_THEN_ATTACK
        );
        System.out.println(s2);

        System.out.println("=== Strategie: DEFEND_WHEN_LOW_HP ===");
        SimulationStats s3 = sim.simulerPlusieursCombats(
                10_000, habJ, endJ, habE, endE, PlayerStrategy.DEFEND_WHEN_LOW_HP
        );
        System.out.println(s3);
    }
}
