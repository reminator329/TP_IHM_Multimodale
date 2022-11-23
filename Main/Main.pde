import java.awt.Point;
import fr.dgac.ivy.*;


private ArrayList<Forme> formes; // liste de formes stockées
private FSM mae; // Finite Sate Machine
private Data data;

Ivy bus;

void setup() {
  data = new Data();
  size(800,600);
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
          
          if (f >= 0.5) {
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

void draw() {
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

void mousePressed() {
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
