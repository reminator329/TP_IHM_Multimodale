/*
 * Classe Cercle
 */
 
public class Data {
  
  Action action;
  Forme forme;
  Couleur couleur;
  ArrayList<Point> positions;
  boolean where = false;
  boolean localisation = false;
  
  public Data() {
    positions = new ArrayList();
    action = null;
    couleur = null;
  }
  
  public void setAction(Action action) {
    this.action = action;
  }
  
  public void setAction(String action) {
    this.setAction(Action.getByName(action));
  }
  
  public void setWhere(boolean where) {
    this.where = where;
  }
  
  public void setLocalisation(boolean localisation) {
    this.localisation = localisation;
  }
  
  public void setForme(Forme forme) {
    this.forme = forme;
  }
  
  public void setForme(Formes formes) {
    Forme forme;
    switch(formes) {
      case RECTANGLE:
        forme = new Rectangle();
        break;
      case LOSANGE:
        forme = new Losange();
        break;
      case TRIANGLE:
        forme = new Triangle();
        break;
      case CERCLE:
        forme = new Cercle();
        break;
      default:
        return;
    }
    this.setForme(forme);
  }
            
  
  public void setCouleur(Couleur couleur) {
    this.couleur = couleur;
  }
  public void setCouleur(String couleurIvy) {
    Couleurs couleurs = Couleurs.getByName(couleurIvy);
    if (couleurs == null) return;
    this.setCouleur(new Couleur(color(couleurs.r, couleurs.g, couleurs.b)));
  }
  
  public void addPosition(Point position) {
    this.positions.add(position);
  }
  
  public Action getAction() {
    return this.action;
  }
  
  public Forme getForme() {
    return this.forme;
  }
  
  public Couleur getCouleur() {
    return this.couleur;
  }
  
  public Point getLastPosition(int n) {
    return this.positions.get(this.positions.size() - n - 1);
  }
  
  public int getNbPositions() {
    return this.positions.size();
  }
}
