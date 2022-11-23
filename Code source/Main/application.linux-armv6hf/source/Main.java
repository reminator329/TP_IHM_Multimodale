import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.awt.Point; 
import fr.dgac.ivy.*; 

import fr.dgac.ivy.*; 
import fr.dgac.ivy.tools.*; 
import gnu.getopt.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Main extends PApplet {





private ArrayList<Forme> formes; // liste de formes stockées
private FSM mae; // Finite Sate Machine
private Data data;

Ivy bus;

public void setup() {
  data = new Data();
  
  surface.setResizable(true);
  surface.setTitle("TP multimodale");
  surface.setLocation(20,20);
  
  formes= new ArrayList(); // nous créons une liste vide
  noStroke();
  mae = FSM.INITIAL;
  
  try
  {
    bus = new Ivy("demo", " demo_processing is ready", null);
    bus.start("127.255.255.255:2010");
    
    bus.bindMsg("sra5 Parsed=action=(.*) where=(.*) form=(.*) color=(.*) localisation=(.*) Confidence=(.*) NP=(.*) Num_A=(.*)", new IvyMessageListener()
    {
      public void receive(IvyClient client,String[] args)
      {        
        int i = 0;
        String actionIvy = args[i];
        i += 1;
        String whereIvy = args[i];
        i += 1;
        String formIvy = args[i];
        i += 1;
        String colorIvy = args[i];
        i += 1;
        String localisationIvy = args[i];
        i += 1;
        String confidenceIvy = args[i];
        i += 1;
        
          String confianceString = confidenceIvy.replace(",", ".");
          
          float f = Float.parseFloat(confianceString);
          
          if (f >= 0.5f) {
            Formes formes = Formes.getByName(formIvy);
            
            println("Avant setAction");
            println(actionIvy);
            data.setAction(actionIvy);
            
            if (!whereIvy.equals("undefined")) {
              data.setWhere(true);
            }

            if (formes != null) {
              data.setForme(formes);
            }
            
            if (!colorIvy.equals("undefined")) {
              data.setCouleur(colorIvy);
            }
            
            if (!localisationIvy.equals("undefined")) {
              data.setLocalisation(true);
            }
          }
          
      }        
    });
    
  }
  catch (IvyException ie)
  {
    ie.printStackTrace();
  }
}

public void draw() {
  background(0);
  switch (mae) {
    case INITIAL:  // Etat INITIAL
      background(255);
      fill(0);
      text("Veuillez énoncer votre demande. Voir exemple ci-dessous.", 50,50);
      text("Voix : \"Créer <Forme> <Couleur> ici\" ; Click souris", 50,80);
      text("Voix : \"Supprimer cette forme\" ; Click souris", 50,110);
      text("Voix : \"Déplacer cette forme\" ; Click souris ; Voix : \"ici\" ; Clicks souris", 50,140);
      break;
      
    case PRE_FUSION:
    if (data.getAction() != null) {
      mae = FSM.POSSIBLE_FUSION;
    }
    affiche();
    
    break;
      
    case POSSIBLE_FUSION: 
      if (data.getAction() != null) {
        switch(data.getAction()) {
          case CREER:
            if (data.getForme() != null && data.getNbPositions() >= 1 && data.localisation) {
              mae = FSM.FUSION;
            }
            break;
          case SUPPRIMER:
            if ((data.getForme() != null || data.where) && data.getNbPositions() >= 1) {
              mae = FSM.FUSION;
            }
            break;
          case DEPLACER:
            if ((data.getForme() != null || data.where) && data.getNbPositions() >= 2 && data.localisation) {
              mae = FSM.FUSION;
            }
            break;
        }
          
      }
    affiche();
    break;
      
    case FUSION:
        
        System.out.println(data.getAction());
        Point p;
        switch(data.getAction()) {
          case CREER:
            p = data.getLastPosition(0);
            Forme forme = data.getForme();
            creer(forme, p);
          break;
          
          case SUPPRIMER:
            p = data.getLastPosition(0);
            supprimer(p);
          break;
          
          case DEPLACER:
            Point p1 = data.getLastPosition(1);
            Point p2 = data.getLastPosition(0);
            deplacer(p1, p2);
          break;
          
          
        }
        data = new Data();
        mae = FSM.PRE_FUSION;
    break;
       
    default:
      break;
  }
}

private void creer(Forme forme, Point p) {
            forme.setLocation(p);
            if (data.getCouleur() != null) {
              forme.setColor(data.getCouleur().getColor());
            }
            formes.add(forme);
}

private Forme supprimer(Point p) {
            
            int indexFormeClicked = 0;
            boolean found = false;
            for (int i = formes.size() - 1; i >= 0; i--) {
              if (formes.get(i).isClicked(p)) {
                found = true;
                indexFormeClicked = i;
                break;
              }
            }
            
            if (!found) return null;
            
            return formes.remove(indexFormeClicked);
}

private void deplacer(Point p1, Point p2) {
  Forme forme = supprimer(p1);
  if (forme == null) return;
  creer(forme, p2);
}

private void affiche() {
  background(255);
  for (int i=0;i<formes.size();i++)
    (formes.get(i)).update();
}

public void mousePressed() {
  Point p = new Point(mouseX,mouseY);
  
  data.addPosition(p);
  
  switch (mae) {
    case INITIAL:
      mae = FSM.PRE_FUSION;
    break;
  
    default:
      break;
  }
}
public enum Action {


    CREER("CREATE"), /* Etat Initial */
    DEPLACER("MOVE"),
    SUPPRIMER("DELETE"),
    ;

    String name;

    private Action(String name) {
        this.name = name;
    }

    public static Action getByName(String name) {
        for (Action action : values()) {
            if (action.name.equalsIgnoreCase(name)) {
                return action;
            }
        }
        return null;
    }
}
/*
 * Classe Cercle
 */ 
 
public class Cercle extends Forme {
  
  int rayon;
  
  public Cercle(Point p) {
    super(p);
    this.rayon=80;
  }
  
  public Cercle() {
    super();
    this.rayon=80;
  }
   
  public void update() {
    fill(this.c);
    circle((int) this.origin.getX(),(int) this.origin.getY(),this.rayon);
  }  
   
  public boolean isClicked(Point p) {
    // vérifier que le cercle est cliqué
   PVector OM= new PVector( (int) (p.getX() - this.origin.getX()),(int) (p.getY() - this.origin.getY())); 
   if (OM.mag() <= this.rayon/2)
     return(true);
   else 
     return(false);
  }
  
  protected double perimetre() {
    return(2*PI*this.rayon);
  }
  
  protected double aire(){
    return(PI*this.rayon*this.rayon);
  }
}
public class Couleur {
  int couleur;
  
  public Couleur(int c) {
     this.couleur = c;
  }
  
  public int getColor() {
    return this.couleur;
  }
}
public enum Couleurs {


    ROUGE("RED", 255, 0, 0),
    BLEU("BLUE", 0, 0, 255),
    VERT("GREEN", 0, 255, 0),
    ;

    String name;
    int r, g, b;

    private Couleurs(String name, int r, int g, int b) {
        this.name = name;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public static Couleurs getByName(String name) {
        for (Couleurs couleurs : values()) {
            if (couleurs.name.equalsIgnoreCase(name)) {
                return couleurs;
            }
        }
        return null;
    }

    public int getG() {
        return g;
    }

    public int getR() {
        return r;
    }

    public int getB() {
        return b;
    }
}
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
/*
 * Enumération de a Machine à Etats (Finite State Machine)
 *
 *
 */
 
public enum FSM {
  INITIAL, /* Etat Initial */ 
  PRE_FUSION, 
  POSSIBLE_FUSION,
  FUSION
}

abstract class Forme {
 Point origin;
 int c;
 
 Forme() {
   this.c = color(127);
 }
 
 Forme(Point p) {
   this();
   this.origin=p;
 }
 
 public void setColor(int c) {
   this.c=c;
 }
 
 public int getColor(){
   return(this.c);
 }
 
 public abstract void update();
 
 public Point getLocation() {
   return(this.origin);
 }
 
 public void setLocation(Point p) {
   this.origin = p;
 }
 
 public abstract boolean isClicked(Point p);
 
 // Calcul de la distance entre 2 points
 protected double distance(Point A, Point B) {
    PVector AB = new PVector( (int) (B.getX() - A.getX()),(int) (B.getY() - A.getY())); 
    return(AB.mag());
 }
 
 protected abstract double perimetre();
 protected abstract double aire();
}
public enum Formes {


    RECTANGLE("RECTANGLE"),
    LOSANGE("DIAMOND"),
    CERCLE("CIRCLE"),
    TRIANGLE("TRIANGLE"),
    ;

    String name;

    private Formes(String name) {
        this.name = name;
    }

    public static Formes getByName(String name) {
        for (Formes forme : values()) {
            if (forme.name.equalsIgnoreCase(name)) {
                return forme;
            }
        }
        return null;
    }
}
/*
 * Classe Losange
 */ 
 
public class Losange extends Forme {
  Point A, B,C,D;
  
  public Losange(Point p) {
    super(p);
    // placement des points
    A = new Point();    
    A.setLocation(p);
    B = new Point();    
    B.setLocation(A);
    C = new Point();  
    C.setLocation(A);
    D = new Point();
    D.setLocation(A);
    B.translate(40,60);
    D.translate(-40,60);
    C.translate(0,120);
  }
  
  public Losange() {
    super();
    A = new Point();  
    B = new Point();   
    C = new Point();     
    D = new Point();
  }
  
  public void setLocation(Point p) {
      super.setLocation(p);
      // redéfinition de l'emplacement des points
      A.setLocation(p);   
      B.setLocation(A);  
      C.setLocation(A);
      D.setLocation(A);
      B.translate(40,60);
      D.translate(-40,60);
      C.translate(0,120);   
  }
  
  public void update() {
    fill(this.c);
    quad((float) A.getX(), (float) A.getY(), (float) B.getX(), (float) B.getY(), (float) C.getX(), (float) C.getY(),  (float) D.getX(),  (float) D.getY());
  }  
  
  public boolean isClicked(Point M) {
    // vérifier que le losange est cliqué
    // aire du rectangle AMD + AMB + BMC + CMD = aire losange  
    if (round( (float) (aire_triangle(A,M,D) + aire_triangle(A,M,B) + aire_triangle(B,M,C) + aire_triangle(C,M,D))) == round((float) aire()))
      return(true);
    else 
      return(false);  
  }
  
  protected double perimetre() {
    //
    PVector AB= new PVector( (int) (B.getX() - A.getX()),(int) (B.getY() - A.getY())); 
    PVector BC= new PVector( (int) (C.getX() - B.getX()),(int) (C.getY() - B.getY())); 
    PVector CD= new PVector( (int) (D.getX() - C.getX()),(int) (D.getY() - C.getY())); 
    PVector DA= new PVector( (int) (A.getX() - D.getX()),(int) (A.getY() - D.getY())); 
    return( AB.mag()+BC.mag()+CD.mag()+DA.mag()); 
  }
  
  protected double aire(){
    PVector AC= new PVector( (int) (C.getX() - A.getX()),(int) (C.getY() - A.getY())); 
    PVector BD= new PVector( (int) (D.getX() - B.getX()),(int) (D.getY() - B.getY())); 
    return((AC.mag()*BD.mag())/2);
  } 
  
  private double perimetre_triangle(Point I, Point J, Point K) {
    //
    PVector IJ= new PVector( (int) (J.getX() - I.getX()),(int) (J.getY() - I.getY())); 
    PVector JK= new PVector( (int) (K.getX() - J.getX()),(int) (K.getY() - J.getY())); 
    PVector KI= new PVector( (int) (I.getX() - K.getX()),(int) (I.getY() - K.getY())); 
    
    return( IJ.mag()+JK.mag()+KI.mag()); 
  }
   
  private double aire_triangle(Point I, Point J, Point K){
    double s = perimetre_triangle(I,J,K)/2;
    double aire = s*(s-distance(I,J))*(s-distance(J,K))*(s-distance(K,I));
    return(sqrt((float) aire));
  }
}
/*
 * Classe Rectangle
 */ 
 
public class Rectangle extends Forme {
  
  int longueur;
  
  public Rectangle() {
    super();
    this.longueur=60;
  }
  
  public Rectangle(Point p) {
    super(p);
    this.longueur=60;
  }
   
  
   
  public void update() {
    fill(this.c);
    square((int) this.origin.getX(),(int) this.origin.getY(),this.longueur);
  }  
  
  public boolean isClicked(Point p) {
    int x= (int) p.getX();
    int y= (int) p.getY();
    int x0 = (int) this.origin.getX();
    int y0 = (int) this.origin.getY();
    
    // vérifier que le rectangle est cliqué
    if ((x>x0) && (x<x0+this.longueur) && (y>y0) && (y<y0+this.longueur))
      return(true);
    else  
      return(false);
  }
  
  // Calcul du périmètre du carré
  protected double perimetre() {
    return(this.longueur*4);
  }
  
  protected double aire(){
    return(this.longueur*this.longueur);
  }
}
/*
 * Classe Triangle
 */ 
 
public class Triangle extends Forme {
  Point A, B,C;
  public Triangle(Point p) {
    super(p);
    // placement des points
    A = new Point();    
    A.setLocation(p);
    B = new Point();    
    B.setLocation(A);
    C = new Point();    
    C.setLocation(A);
    B.translate(40,60);
    C.translate(-40,60);
  }
  
  public Triangle() {
    super();
    A = new Point();  
    B = new Point();   
    C = new Point();       
  }
  
    public void setLocation(Point p) {
      super.setLocation(p);
      // redéfinition de l'emplacement des points
      A.setLocation(p);   
      B.setLocation(A);  
      C.setLocation(A);
      B.translate(40,60);
      C.translate(-40,60);   
  }
  
  public void update() {
    fill(this.c);
    triangle((float) A.getX(), (float) A.getY(), (float) B.getX(), (float) B.getY(), (float) C.getX(), (float) C.getY());
  }  
  
  public boolean isClicked(Point M) {
    // vérifier que le triangle est cliqué
    
    PVector AB= new PVector( (int) (B.getX() - A.getX()),(int) (B.getY() - A.getY())); 
    PVector AC= new PVector( (int) (C.getX() - A.getX()),(int) (C.getY() - A.getY())); 
    PVector AM= new PVector( (int) (M.getX() - A.getX()),(int) (M.getY() - A.getY())); 
    
    PVector BA= new PVector( (int) (A.getX() - B.getX()),(int) (A.getY() - B.getY())); 
    PVector BC= new PVector( (int) (C.getX() - B.getX()),(int) (C.getY() - B.getY())); 
    PVector BM= new PVector( (int) (M.getX() - B.getX()),(int) (M.getY() - B.getY())); 
    
    PVector CA= new PVector( (int) (A.getX() - C.getX()),(int) (A.getY() - C.getY())); 
    PVector CB= new PVector( (int) (B.getX() - C.getX()),(int) (B.getY() - C.getY())); 
    PVector CM= new PVector( (int) (M.getX() - C.getX()),(int) (M.getY() - C.getY())); 
    
    if ( ((AB.cross(AM)).dot(AM.cross(AC)) >=0) && ((BA.cross(BM)).dot(BM.cross(BC)) >=0) && ((CA.cross(CM)).dot(CM.cross(CB)) >=0) ) { 
      return(true);
    }
    else
      return(false);
  }
  
  protected double perimetre() {
    //
    PVector AB= new PVector( (int) (B.getX() - A.getX()),(int) (B.getY() - A.getY())); 
    PVector AC= new PVector( (int) (C.getX() - A.getX()),(int) (C.getY() - A.getY())); 
    PVector BC= new PVector( (int) (C.getX() - B.getX()),(int) (C.getY() - B.getY())); 
    
    return( AB.mag()+AC.mag()+BC.mag()); 
  }
   
  // Calcul de l'aire du triangle par la méthode de Héron 
  protected double aire(){
    double s = perimetre()/2;
    double aire = s*(s-distance(B,C))*(s-distance(A,C))*(s-distance(A,B));
    return(sqrt((float) aire));
  }
}
  public void settings() {  size(800,600); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Main" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
