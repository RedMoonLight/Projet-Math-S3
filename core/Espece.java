﻿package core;

import utils.Case;
import utils.Directions;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by r14003530 on 09/10/15.
 */
public abstract class Espece {
    private Point position;
    private int mouvementParTour = 2;
    protected int niveauFaim = 0;
    protected int mouvementRestant;
    private double vision = 15;
    private int nombreReproduction = 1;
    private int frequenceReproduction = 2;
    private int reposReproduction = 0;

    private int dureeDeVie = 3;
    private boolean reprodui = false;
    protected int etatLimiteFaim;

    //GETTERS & SETTERS

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public int getMouvementParTour() {
        return mouvementParTour;
    }

    public void setMouvementParTour(int mouvementParTour) {
        this.mouvementParTour = mouvementParTour;
    }

    public double getVision() {
        return vision;
    }

    public void setVision(double vision) {
        this.vision = vision;
    }

    public int getnombreReproduction() {
        return nombreReproduction;
    }

    public void setnombreReproduction(int nombreReproduction) {
        this.nombreReproduction = nombreReproduction;
    }

    public int getFrequenceReproduction() {
        return periodeReproduction;
    }

    public void setFrequenceReproduction(int periodeReproduction) {
        this.periodeReproduction = periodeReproduction;
    }

    public int getDureeDeVie() {
        return dureeDeVie;
    }

    public void setDureeDeVie(int dureeDeVie) {
        this.dureeDeVie = dureeDeVie;
    }

    public boolean isReprodui() {
        return reprodui;
    }

    public void setReprodui(boolean reprodui) {
        this.reprodui = reprodui;
    }

    public int getReposReproduction() {
        return reposReproduction;
    }

    public void setReposReproduction(int reposReproduction) {
        this.reposReproduction = reposReproduction;
    }


    // Constructeurs

    public Espece(Point position) {
        this.position = position;
        this.generation = generation;

    }

    public Espece(Point position, int mouvementParTour) {
        this.position = position;
        this.mouvementParTour = mouvementParTour;
        this.generation = generation;

    }

    public Espece(Point position, int mouvementParTour, double vision, int nombreReproduction, int periodeReproduction, int dureeDeVie) {
        this.position = position;
        this.mouvementParTour = mouvementParTour;
        this.vision = vision;
        this.nombreReproduction = nombreReproduction;
        this.frequenceReproduction = frequenceReproduction;

        this.dureeDeVie = dureeDeVie;
    }


    /** TROUVER INDIVIDU PROCHE - Comportement:
     * Renvoie l'individu le plus proche dans list
     */

    public Espece trouverIndividuProche(ArrayList<Espece> list) {
        Espece individuProche = null;
        double distancePlusProche = vision;

        double distanceLocale;
        for (int i = 0; i < list.size(); ++i) {
            distanceLocale = position.distance(list.get(i).getPosition()); // Distance entre l'espece courante et l'espece i dans la liste
            if (distanceLocale < distancePlusProche && ! equals(list.get(i))) {
                distancePlusProche = distanceLocale;
                individuProche = list.get(i);
            }
        }

        return individuProche;
    }

    /** TROUVERINDIVUDUCASEADJACENTE - Comportement:
     * Renvoie la première entité de l'espèce donnée en paramètre et qui a une position adjacente à celle qui apelle la fonction
     * sinon renvoie null
     **/
    public Espece trouverIndividuCaseAdjacente(ArrayList<Espece> list) {
        for (Espece e : list) {
            if (estSurCaseAdjacente(e.getPosition())) {
                return e;
            }
        }
        return null;
    }

    /** ESTSURCASEADJACENTE - Comportement:
     *  Renvoie vrai si le point passé en parametre est sur une case adjacente de l'entité qui appelle la fonction
     */

    private boolean estSurCaseAdjacente(Point positionATester) {
        return (positionATester.x == position.x && positionATester.y == position.y + 1) ||
                (positionATester.x == position.x && positionATester.y == position.y - 1) ||
                (positionATester.x == position.x + 1 && positionATester.y == position.y) ||
                (positionATester.x == position.x - 1 && positionATester.y == position.y);
    }

    /** SEREPRODUIRE - Comportement:
     * SI un autre individu de al meme espece est sur la même case ET qu'ils ne se sont pas reproduis
     * ALORS crée une nouvelle espèce sur une case adjacente
     */
    public void seReproduire(ArrayList<Espece> espece, Case[][] positionsEspeces, ArrayList<Espece> buffer) {
        Espece Congenere = trouverIndividuCaseAdjacente(espece);

        if (CongenereDeGenerationLaPlusProche != null && ! CongenereDeGenerationLaPlusProche.isReprodui() && ! isReprodui()) {
            reposReproduction = 0;
            setReprodui(true);
            CongenereDeGenerationLaPlusProche.setReprodui(true);
            CongenereDeGenerationLaPlusProche.setReposReproduction(0);

            for (int i=0; i < nombreReproduction; i++) {
                Point positionNouveauNe = choisirCaseNaissance(this, Congenere, positionsEspeces);
                if (positionNouveauNe != null) {
                    Espece nouveauNe;
                    if (this instanceof Proie) {
                        nouveauNe = new Proie(positionNouveauNe, mouvementParTour, vision, nombreReproduction, frequenceReproduction, dureeDeVie, Generation);
                    }
                    else {
                        nouveauNe = new Predateur(positionNouveauNe, mouvementParTour, vision, nombreReproduction, frequenceReproduction, dureeDeVie, Generation, etatLimiteFaim);

                    }
                    buffer.add(nouveauNe);
                }
            }
        }
    }


    /** CHOISIRCASENAISSANCE - Comportement:
     *  Choisit une case aléatoire entre les cases qui entourent les deux parents
     */

    private Point choisirCaseNaissance(Espece e1, Espece e2, Case[][] positionsEspeces) {
        ArrayList<Point> casesDisponibles = new ArrayList<>();
        for (int deltaX = -1; deltaX <= 1; deltaX += 2) {
            ajouterCaseDispo(e1, positionsEspeces, deltaX, 0, casesDisponibles);
            ajouterCaseDispo(e2, positionsEspeces, deltaX, 0, casesDisponibles);
        }
        for (int deltaY = -1; deltaY <= 1; deltaY += 2) {
            ajouterCaseDispo(e1, positionsEspeces, 0, deltaY, casesDisponibles);
            ajouterCaseDispo(e2, positionsEspeces, 0, deltaY, casesDisponibles);
        }

        //System.out.println(e1.getClass().toString() + casesDisponibles);
        if (! casesDisponibles.isEmpty()) {
            Random rand = new Random();
            return casesDisponibles.get(rand.nextInt(casesDisponibles.size()));
        } else {
            return null;
        }
    }


    /** AJOUTERCASEDISPO - Comportement:
     *  Ajoute une case qui entourne les deux parents et qui ne contient pas déjà une entité pour traitement ultérieur
     */
    private void ajouterCaseDispo(Espece e, Case[][] positionsEspeces, int deltaX, int deltaY, ArrayList<Point> casesDisponibles) {
        Point caseAdjacente = new Point(e.getPosition());
        caseAdjacente.translate(deltaX, deltaY);
        if (caseAdjacente.x >= 0 && caseAdjacente.x < positionsEspeces.length &&
                caseAdjacente.y >= 0 && caseAdjacente.y < positionsEspeces.length &&
                positionsEspeces[caseAdjacente.x][caseAdjacente.y] == Case.Vide) {
            casesDisponibles.add(caseAdjacente);
        }
    }


    /** ALLERVERSPOSITION - Comportement:
     * Se déplace vers positionCible autant que mouvement le permette et si aucun obstacle ne l'en empêche
     */

    public void allerVersPosition(Point positionCible, Case[][] positionsIndividus) {
        boolean bloque = false;

        while (mouvementRestant > 0 && ! position.equals(positionCible) && ! estSurCaseAdjacente(positionCible)) {
            int distanceX = positionCible.x - position.x;
            int distanceY = positionCible.y - position.y;

            if (Math.abs(distanceX) > Math.abs(distanceY)) {
                if (distanceX > 0) {
                    allerSurCaseAdjacente(Directions.Droite, positionsIndividus);
                }
                else {
                    allerSurCaseAdjacente(Directions.Gauche, positionsIndividus);
                }
            }
            else {
                if (distanceY > 0) {
                    allerSurCaseAdjacente(Directions.Bas, positionsIndividus);
                }
                else {
                    allerSurCaseAdjacente(Directions.Haut, positionsIndividus);
                }
            }

            mouvementRestant--;
        }
    }


    /**ALLERSURCASEADJACENTE - Comportement:
     * Se déplace sur une case adjacente en fonction de la direction choisie
     */

    private void allerSurCaseAdjacente(Directions direction, Case[][] positionsIndividus) {
        switch (direction) {
            case Haut:
                seDeplacer(0, -1, positionsIndividus);
                break;
            case Bas:
                seDeplacer(0, 1, positionsIndividus);
                break;
            case Gauche:
                seDeplacer(-1, 0, positionsIndividus);
                break;
            case Droite:
                seDeplacer(1, 0, positionsIndividus);
                break;
        }
    }

    /**
     * ESTSURCASEVALIDE - Comportement:
     * Renvoie vrai si la position est à l'intérieur de la map
     */

    private boolean estCaseValide(int x, int y, int tailleMap) {
        return x >= 0 && x < tailleMap
                && y >= 0 && y < tailleMap;
    }


    /**SEDEPLACER - Comportement:
     * Se déplace vers une case vide et dans la map
     */

    private void seDeplacer(int deltaX, int deltaY, Case[][] positionsIndividus) {
        if (estCaseValide(position.x + deltaX, position.y + deltaY, positionsIndividus.length) && positionsIndividus[position.x + deltaX][position.y + deltaY] == Case.Vide) {
            positionsIndividus[position.x][position.y] = Case.Vide;
            position.move(position.x + deltaX, position.y + deltaY);
            positionsIndividus[position.x][position.y] = (this instanceof Proie ? Case.Proie : Case.Predateur);
        }
    }


    /**ALLERVERSCONGENERE - Comportement:
     * Se déplace vers le congénère le plus proche
     */

    public void allerVersCongenere (ArrayList <Espece> especes, Case[][] positionEspeces) {
        Espece individuProche = trouverIndividuProche(especes);
        if (individuProche != null) {
            allerVersPosition(individuProche.getPosition(), positionEspeces);
        }
    }


    /** MOURIR - Comportement:
     * Vide la case de l'entité devant mourrir (mais ne supprime pas l'entité de son tableau d'espèce)
     */

    public void mourir(Case[][] positionsEspeces) {
        positionsEspeces[position.x][position.y] = Case.Vide;
    }


    /** REINITTOUR - Comportement:
     *  Réinitialise l'appêtit sexuel de la créature au début de chaque tour pour qu'elle puisse coïtter en paix.
     */

    public void reinitTour() {
        ++reposReproduction;
        if (reposReproduction >= frequenceReproduction) {
            reprodui = false;
        }
    }


    /** JOUERTOUR - Comportement
     * Jouer un tour de l'individu
     */

    protected abstract void jouerTour(ArrayList <Espece> Proie, ArrayList <Espece> Predateur, Case[][] positionsEsp, int mapSize, ArrayList<Espece> buffer);

    @Override
    public String toString() {
        return "Espece{" +
                "position=" + position +
                ", mouvementParTour=" + mouvementParTour +
                ", vision=" + vision +
                ", nombreReproduction=" + nombreReproduction +
                ", periodeReproduction=" + periodeReproduction +
                ", dureeDeVie=" + dureeDeVie +
                '}';
    }
}
