import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


class Node implements Comparable<Node>{ // a general node calss for both actors and films
    String id;  // id of either the actor or the film
    String navn; // name of either the actor or the film
    String type;    // type of node, actor or film
    Double rating;  // if the node is an actor node, this field is null
    ArrayList<Node> naboer; // list of neighbour nodes to the current node
    ArrayList<Edge> connectedEdges; // list of edges connected to the node
    int distance=Integer.MAX_VALUE; // infinity
    Double distance2=Double.MAX_VALUE;

    public Node(String id, String navn, String type, Double rating){
        this.id=id;
        this.navn=navn;
        this.type=type;
        this.rating=rating;
        naboer = new ArrayList<>();
        connectedEdges=new ArrayList<>();

    }

    @Override
    public int compareTo(Node annen){
        if (this.distance>annen.distance) return 1;
        if (this.distance<annen.distance) return -1;
        return 0;
    }

    public String toString(){

        if (this.type.equals("film")) {
            return "( " + navn + " - " + rating + " )";
        }

        else {
            return "( " + navn + "  )"; // give med the name of the node, either actor name or movie name
        }
        // and the type of the node either actor or movie node
    }
    

}

class Edge{

    String filmId; // i chose to load the edge with the film Id
    String filmNavn;        // and filmnavn, since every edge links an actor node to a film node or vide versa
    ArrayList<Node> delimiters; // delimiter nodes, one is an actor node, the other is strictly a film node  (actor Ndoe)* -----filmId---filmnavn-----*(film node)
    int weight=1;
    Double vekt=0.0; // bruker i oppg3

    public Edge(String filmNavn, String filmId){
        this.filmNavn=filmNavn;
        this.filmId=filmId;
        delimiters=new ArrayList<>(); // delimiter nodes of gives edge "2 nodes always"

    }
    public String toString(){
        return delimiters.toString(); // give med info about the edge
    }
}



class Graf{
    String filmFileName;
    String actorFileName;
    ArrayList<Node> noder; // alle noder film og actor
    HashMap<String, Node> filmNoder; 
    ArrayList<Node> actorNoder;
    ArrayList<Edge> edges;// liste of edges i grafen



    public Graf(String filmFileName, String actorFileName){
        this.filmFileName=filmFileName;
        this.actorFileName=actorFileName;
        noder=new ArrayList<>();
        filmNoder=new HashMap<>();
        actorNoder=new ArrayList<>();
        edges =new ArrayList<>();

    }

    public Graf byggMeg() throws IOException{
        BufferedReader filmReader=new BufferedReader(new FileReader(filmFileName));
        BufferedReader actorReader=new BufferedReader(new FileReader(actorFileName));

        while (true){
            String line=filmReader.readLine();
            if (line!=null){
                String[] biter=line.split("\t");
                String filmId=biter[0];
                String filmNavn=biter[1];
                Double filmRating=Double.parseDouble(biter[2]);
                String type="film";
                Node node=new Node(filmId, filmNavn, type, filmRating);
                noder.add(node); // add film node to alle noder, 
                filmNoder.put(filmId, node); // add film node to hashmap og mapp med film id

            }else{  
                break;
            }
        }

        while(true){
            String line =actorReader.readLine();
            if(line!=null){
                String [] biter=line.split("\t");
                String actorId=biter[0];
                String actorNavn=biter[1];
                String type="actor";

                Node node=new Node(actorId, actorNavn, type, null); // lag ny node, her actor node, rating ignored, not a film node
                Node linkedNode=null; // we declare a linked node for further use
               
                
                for (int i=2;i<biter.length;i++){ // iterate from pos 2 and further
                    if (filmNoder.containsKey(biter[i])){ // if the film id we are in is present in film hashmap( certain film ids the actor has played in are not present in the file, they should be ignored)
                        linkedNode=filmNoder.get(biter[i]); // get the linked node(film node) by accessing it by its correspondant key
    
                        linkedNode.naboer.add(node); // add this.node(actor node) to linked node´s(film node) list of naboer
                        Edge edge=new Edge(linkedNode.navn, linkedNode.id);// make a new edge, which will bear the linked node(film node) id and navn
                        edge.delimiters.add(linkedNode); // delimit the edge by the linked node(Film node)
                        edge.vekt=linkedNode.rating; //// new COOOOOODE ---------
                        edge.delimiters.add(node);  // and the current node(actor node)
                        node.connectedEdges.add(edge); // ad the already made edge to this node(actor node) list of connected nodes
                        linkedNode.connectedEdges.add(edge); // if you have an arm towars me, then i have the same arm towards you, THE SAME ARRRRM, NOT ANOTHER ARM THAT RESEMBELSE TO YOURS!
                        node.naboer.add(linkedNode); // if you are my neighbour, then i´m your neighbour too
                        edges.add(edge); // finally add the edge to the list of edges(for counting purposes)

                    }
                  
                }
                
               
                actorNoder.add(node); // After all that previous stuff we have an interactive actor node, add it to list of actor nodes
                noder.add(node); // add the actor node to the list of all nodes( again for counting purposes)

                
            }else{
                break;
            }
        }

        return this;
    }

//*************************************DIJKSTRA OPPG 2 FOR KORTESTE VEIER ************************/

    
  public HashMap<Node, Integer> Djikstra(Graf g, Node s, Node dest){

    PriorityQueue<Node> q=new PriorityQueue<>();
    HashMap<Node, Integer> hash=new HashMap<>();
    HashMap<Node, Node> prevNodes = new HashMap<>();
    
    for (Node nod : g.noder) {
        prevNodes.put(nod, null);
    }

    for (Node v:g.noder){
        hash.put(v,v.distance); // here all distances of all n nodes is 3000.
        q.add(v); // with priority of distance, initially 99999999 MAX VALUE.

    }
    hash.replace(s,  0); // replace the value of key s from infinity to 0
    decresePriority(q,s,0);

    while (!q.isEmpty()){
        Node u=q.poll(); // node with minimum distance, initially s with distance 0

        for (Edge e:u.connectedEdges){
            int c=hash.get(u) + e.weight;
            Node denAndre=null;

            for (Node n:e.delimiters){
                if (n!=u){
                    denAndre=n;
                }
            }

            if (c<hash.get(denAndre)){
                hash.replace(denAndre, c);
                decresePriority(q, denAndre, c);
                prevNodes.replace(denAndre, u);
                
                
                
            }
        }
    }
    // prevNodes klar
    
    String shortestPath = "";

    Node currentNode = dest;

    while (currentNode != null) {
        
        if (shortestPath.equals("")) {
            shortestPath =  currentNode + "";
        }
        
        else {
            shortestPath =  currentNode + " -> " + shortestPath;
        }
   
        currentNode = prevNodes.get(currentNode);
    }

    System.out.println(shortestPath);

    this.resetDistance();

    return hash;

}
//***********************************HJELPE METODE FOR DIJKSTRA OPPG 2**************************/

public void decresePriority(PriorityQueue<Node> q, Node s, int dis){
    s.distance=dis; // opdate the distance of s in which s will be sorted according to, to 
    
    for (Node n:q){ // dist, the remove s from the queue, then reinsert it with distance updated
        if (n.equals(s)){
            q.remove(n);// remove the node that is s in the queue
            // reinsert s with new distance value which is 0 now,
            break;
        }

    }
    q.add(s);
    
}



//*********************************METODE FOR Å FINNE CHILLESTE VEI ***********************************


    
public HashMap<Node, Double> chillesteVei(Graf g, Node s, Node dest){

    PriorityQueue<Node> q=new PriorityQueue<>();
    HashMap<Node, Double> hash=new HashMap<>();


    HashMap<Node, Node> prevNodes = new HashMap<>();
    
    for (Node nod : g.noder) {
        prevNodes.put(nod, null);
    }

    for (Node v:g.noder){
        hash.put(v,v.distance2); // here all distances of all n nodes is 3000.
        q.add(v); // with priority of distance, initially 99999999 MAX VALUE.

    }

    hash.replace(s,  0.0); // replace the value of key s from infinity to 0
    decresePriorityChilleste(q,s,0.0);

    while (!q.isEmpty()){
        Node u=q.poll(); // node with minimum distance, initially s with distance 0

        for (Edge e:u.connectedEdges){
            Double c=hash.get(u) + (10 - e.vekt);
            Node denAndre=null;

            for (Node n:e.delimiters){ // istedenfor visited list, no going back to same node 
                if (n!=u){
                    denAndre=n;
                }
            }


            if (c<hash.get(denAndre)){
                hash.replace(denAndre, c);
                decresePriorityChilleste(q, denAndre, c);
                prevNodes.replace(denAndre, u);
                
                
                
            }
        }
    }
    // prevNodes klar
    
    String shortestPath = "";

    Node currentNode = dest;

    while (currentNode != null) {
        
        if (shortestPath.equals("")) {
            shortestPath =  currentNode + "";
        }
        
        else {
            shortestPath =  currentNode + " -> " + shortestPath;
        }
   
        currentNode = prevNodes.get(currentNode);
    }

    System.out.println(shortestPath);
    System.out.println("Total weight: " + hash.get(dest)/2);

    this.resetDistance();

    return hash;

}

//////****************************** HJELPE METODE FOR chillesteVei METODE ***************** */

public void decresePriorityChilleste(PriorityQueue<Node> q, Node s, Double dis){
    s.distance2=dis; // opdate the distance of s in which s will be sorted according to, to 
    
    for (Node n:q){ // dist, the remove s from the queue, then reinsert it with distance updated
        if (n.equals(s)){
            q.remove(n);// remove the node that is s in the queue
            // reinsert s with new distance value which is 0 now,
            break;
        }

    }
    q.add(s);
}

///************************************ BREDDE-FØRST SØK ****************************************



public void bfsVisit(Graf g, Node s, ArrayList<Node> visited){
    Queue<Node> queue=new LinkedList<>();

    queue.add(s);
    visited.add(s);

    while(!queue.isEmpty()){

     Node u=queue.poll();

     for (Node v:u.naboer){
         if (!visited.contains(v) ){
             visited.add(v);

             queue.add(v);
         }
     }

    }


}
///************************************ FIND COMPONENTS METHOD ********************************************************

public HashMap<Integer, Integer> findComponents(Graf g ){
    System.out.println("leter etter komponenter!");
    System.out.println();

    HashMap<Integer, Integer> components=new HashMap<>();

    while (!g.noder.isEmpty()) {
        ArrayList<Node> visited=new ArrayList<>();
        g.bfsVisit(g, g.noder.get(0), visited);
        int k = visited.size();
        if (components.containsKey(k)) {
            components.put(k, components.get(k) + 1);
        }

        else{
            components.put(visited.size(), 1);
        }

        for (Node vis : visited) {
            if (g.noder.contains(vis)) {
                g.noder.remove(vis);
            }
        }
    }

    return components;
}
///****************************RESET DISTANCE CALLED IN THE END OF EVERY DIJKSTRA ALGORITHM*********

public void resetDistance(){
    for (Node nod:this.noder){
        nod.distance=Integer.MAX_VALUE;
        nod.distance2=Double.MAX_VALUE;
    }
}
///************************************************************************************************

}


///************************************************************************************************
///************************************************************************************************
///*************************************** HOVED PROGRAM ******************************************
class Hoved{
    public static void main(String[] args) throws IOException {
        Graf g=new Graf(args[0], args[1]);
        g.byggMeg();

        HashMap<String, String> examples = new HashMap<>();
        examples.put("nm2255973", "nm0000460");
        examples.put("nm0424060", "nm0000243");
        examples.put("nm4689420", "nm0000365");
        examples.put("nm0000288", "nm0001401");
        examples.put("nm0031483", "nm0931324");
        Node n1=null;
        Node n2=null;
        System.out.println("oppgave 1");
        System.out.println("antall noder er :"+ g.noder.size());
        System.out.println("antall kanter er :"+g.edges.size());

        System.out.println("Oppgave 2:");

        for (String k:examples.keySet()){
            for (Node nod:g.noder){
                if (k.equals(nod.id)){
                    n1=nod;
                }
                if (examples.get(k).equals(nod.id)){
                    n2=nod;
                }
            }

            g.Djikstra(g, n1, n2);
        }

        System.out.println("Oppgave3:");

        for (String k:examples.keySet()){
            for (Node nod:g.noder){
                if (k.equals(nod.id)){
                    n1=nod;
                }
                if (examples.get(k).equals(nod.id)){
                    n2=nod;
                }
            }

            g.chillesteVei(g, n1, n2);
        }

        System.out.println("Oppgave4:");

        HashMap<Integer,Integer> comps=g.findComponents(g);
        for (int key : comps.keySet()) {
            System.out.println("There are " + comps.get(key) + " components of size " + key);
        }

        
  
    
}
}

