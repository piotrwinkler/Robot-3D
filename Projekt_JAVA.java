import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Sphere;
import javax.media.j3d.*;
import javax.swing.*;
import java.awt.*;
import com.sun.j3d.utils.universe.SimpleUniverse;
import javax.media.j3d.Transform3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Point3d;
import com.sun.j3d.utils.behaviors.vp.*;
import com.sun.j3d.utils.behaviors.keyboard.*;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.ViewingPlatform;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TimerTask;
import java.util.Timer;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnCollisionEntry;
import javax.media.j3d.WakeupOnCollisionExit;
import javax.media.j3d.WakeupOnCollisionMovement;
import javax.media.j3d.WakeupOr;
import javax.media.j3d.Bounds;
import static java.lang.Math.abs;


//Główna klasa projektu
public class Projekt_JAVA extends JFrame implements ActionListener, KeyListener
{

    //Deklaracje obiektów i zmiennych
    private BranchGroup wezel_scena;
    private SimpleUniverse simpleU;
    private boolean klawisze[];
    private boolean tryb_uczenia;
    private boolean tryb_wykonywania;
    private boolean przedmiot_zlapany = false;
    private boolean kolizja = false;
    private JButton przyciski[];
    private TransformGroup TG_os_pionowa;
    private TransformGroup TG_tlo;
    private TransformGroup TG_ramie1;
    private TransformGroup TG_ramie2;
    private TransformGroup TG_chwytak1;
    private TransformGroup TG_chwytak2;
    private TransformGroup TG_chwytak3;
    private TransformGroup TG_podloga;
    private TransformGroup TG_przedmiot;
    private TransformGroup TG_pomocnicza_przedmiotu;
    private Transform3D trans = new Transform3D();
    private Transform3D trans_rotacja_robota = new Transform3D();
    private Transform3D trans_rotacja_przedmiotu = new Transform3D();
    private BoundingSphere bounds;
    private Timer timer;
    
    
    //Listy i licznik używane do uczenia robota
    private ArrayList lista_obrot_w_lewo;
    private ArrayList lista_obrot_w_prawo;
    private ArrayList lista_podnoszenie_w_pionie;
    private ArrayList lista_opuszczanie_w_pionie;
    private ArrayList lista_wysuwanie_ramienia;
    private ArrayList lista_cofanie_ramienia;
    private ArrayList lista_lapanie_przedmiotu;
    private ArrayList lista_sprawdzanie_kolizji;
    private int licznik = -1;
    private int licznik2 = 0;
    
    //Początkowe wartości współrzędnych kolejnych elementów
    private float wsp_podlogi[] = {0.0f, -1.27f, 0.0f};
    private float wsp_przedmiotu[] = {+1.86f , -1.15f  ,0f };
    private float wsp_os_pionowa[] = {0f, 0f ,0f};
    private float wsp_ramie1[] = {0.45f, 0.0f, 0.0f};
    private float wsp_ramie2[] = {1f, 0.0f, 0.0f};
    private float wsp_chwytak1[] = {1.7f, 0.0f, 0.0f};
    private float wsp_chwytak2[] = {1.9f, 0.0f, 0.18f};
    private float wsp_chwytak3[] = {1.9f, 0.0f, -0.18f};
    private float aktualny_kat_robota = 0;
    private float aktualny_kat_przedmiotu = 0;
    
    //Stałe definiujące wielkośc pojedynczego kroku robota
    private final float przesuniecie_w_pionie = 0.03f;
    private final float przesuniecie_w_poziomie = 0.03f;
    private final float krok_grawitacji = 0.02f;
    private final float kat_obrotu = (float)Math.PI / 64;
    private int przyspieszenie_grawitacyjne = 1;

    
    //Utworzenie okienka oraz przestrzeni 3D
    public Projekt_JAVA()
    {
        super("Projekt_Zad3");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        
        //Definicja przycisków w interfejsie programu
        przyciski = new JButton[8];
        //przyciski[0] = new JButton("Podnieś przedmiot");
        //przyciski[1] = new JButton("Opuść przedmiot");
        przyciski[2] = new JButton("Złap");
        przyciski[3] = new JButton("Puść");
        przyciski[4] = new JButton("Uczenie robota");
        przyciski[5] = new JButton("Wykonywanie programu");
        przyciski[6] = new JButton("Swobodna praca");
        przyciski[7] = new JButton("Reset kamery");
        
        //Deklaracja klawiszy używanych przez program
        klawisze = new boolean[10];
        
        //Definicje list używanych do uczenia
        lista_obrot_w_lewo = new ArrayList();
        lista_obrot_w_prawo = new ArrayList();
        lista_podnoszenie_w_pionie = new ArrayList();
        lista_opuszczanie_w_pionie = new ArrayList();
        lista_wysuwanie_ramienia = new ArrayList();
        lista_cofanie_ramienia = new ArrayList();
        lista_lapanie_przedmiotu = new ArrayList();
        lista_sprawdzanie_kolizji = new ArrayList();
        lista_sprawdzanie_kolizji.add(0);
        
        
        //Dodawanie przycisków do panelu
        JPanel panelPrzyciski = new JPanel(new FlowLayout());
        //panelPrzyciski.add(przyciski[0]);
        //panelPrzyciski.add(przyciski[1]);
        panelPrzyciski.add(przyciski[2]);
        panelPrzyciski.add(przyciski[3]);
        panelPrzyciski.add(przyciski[4]);
        panelPrzyciski.add(przyciski[5]);
        panelPrzyciski.add(przyciski[6]);
        panelPrzyciski.add(przyciski[7]);
        
        //Dodawanie przyciskom ActionListenerów - reakcji na ich wciśnięcie
        //przyciski[0].addActionListener(this);
        //przyciski[0].addKeyListener(this);
        //przyciski[1].addActionListener(this);
        //przyciski[1].addKeyListener(this);
        przyciski[2].addActionListener(this);
        przyciski[2].addKeyListener(this);
        przyciski[3].addActionListener(this);
        przyciski[3].addKeyListener(this);
        przyciski[4].addActionListener(this);
        przyciski[4].addKeyListener(this);
        przyciski[5].addActionListener(this);
        przyciski[5].addKeyListener(this);
        przyciski[6].addActionListener(this);
        przyciski[6].addKeyListener(this);
        przyciski[7].addActionListener(this);
        przyciski[7].addKeyListener(this);
         
    
        //Definiowanie Uniwersum przestrzeni i dołączanie do niego kolejnych elementów
        
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

        //Definiowanie Canvas3D
        Canvas3D canvas3D = new Canvas3D(config);
        canvas3D.setPreferredSize(new Dimension(1500,900));
        canvas3D.addKeyListener(this);
        
        //Inicjowanie czy robot na początku trzyma przedmiot
        przedmiot_zlapany = false;
        
        JPanel panelJava3D = new JPanel(new FlowLayout());
        panelJava3D.add(canvas3D);
        
        Container content = getContentPane();
        content.setLayout(new BorderLayout());
        content.add(panelPrzyciski, BorderLayout.NORTH);
        content.add(panelJava3D, BorderLayout.CENTER);
        
        pack();
        setVisible(true);

        //Tworzenie BranchGroupa sceny
        BranchGroup scena = utworzScene();
	    scena.compile();
            
        //DODANIE TIMERA
        timer = new Timer();
        timer.scheduleAtFixedRate(new Ruchy_robota(),0,45);

        simpleU = new SimpleUniverse(canvas3D);
        simpleU.addBranchGraph(scena);
        
        //OBRÓT MYSZKĄ I ZOOMOWANIE
        
        ViewingPlatform viewingPlatform = simpleU.getViewingPlatform();
        viewingPlatform.setNominalViewingTransform();
     
        OrbitBehavior orbit = new OrbitBehavior(canvas3D, OrbitBehavior.REVERSE_ALL | OrbitBehavior.DISABLE_TRANSLATE);
        
         bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
         
         orbit.setSchedulingBounds(bounds);
         orbit.setMinRadius(1.0d);
         viewingPlatform.setViewPlatformBehavior(orbit);
        
        //początkowe oddalenie kamery od robota
        Transform3D przesuniecie_obserwatora = new Transform3D();
        przesuniecie_obserwatora.set(new Vector3f(0.0f,0.0f,12.0f));

        simpleU.getViewingPlatform().getViewPlatformTransform().setTransform(przesuniecie_obserwatora);
        
    }
    

   //Stworzenie robota - dodawanie kolejnych elementów do BranchGroupa
    public BranchGroup utworzScene()
      {

        wezel_scena = new BranchGroup();

        bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0),100);   
        
        //Ośwtlenie sceny
        AmbientLight lightA = new AmbientLight();       //Definiowanie światła
        lightA.setInfluencingBounds(bounds);            //Ustalanie granic działania światła
        wezel_scena.addChild(lightA);                   // Dodawanie światła do sceny
        
        //Poniżej w kodzie znajduje się tworzenie kolejnych elementów robota - dodawanie TransformGroup i obiektów 3D, przesuwanie i obracanie obiektów, dodawanie tekstur:

        //Oś pionowa robota 
        TG_os_pionowa = new TransformGroup();                                   //TransformGroup którego dzieckiem będzie Shape3D
        TG_os_pionowa.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);      //Pozwolenie TransformGroupowi na ingerowanie w przestrzeni

        Appearance wygladPionu = new Appearance();                                                  //Tworzenie wyglądu obiektu
        Texture TeksturaOs = new TextureLoader("zubr_puszka_texture.jpg", this).getTexture();       //Wczytywanie tekstury
        wygladPionu.setTexture(TeksturaOs);                                                         //Przypisywanie tekstury do danego wyglądu
       // wygladPionu.setColoringAttributes(new ColoringAttributes(0.9f,0.9f,1f,ColoringAttributes.NICEST));

        Cylinder Pion = new Cylinder(0.15f, 2.5f, Cylinder.GENERATE_TEXTURE_COORDS, wygladPionu);       //Tworzenie obiektu - Shape3D

        Transform3D T3D_Pionu = new Transform3D();                                              //Definiowanie Transform3D który posłuży do przesunięca TransformGroupa
        T3D_Pionu.set(new Vector3f(wsp_os_pionowa[0], wsp_os_pionowa[1], wsp_os_pionowa[0]));   //Definiowanie przesunięcia Transform3D

        TG_os_pionowa.setTransform(T3D_Pionu);      //Ustawianie Transform3D dla TransformGroupa
        TG_os_pionowa.addChild(Pion);               //Dodawanie do TransformGroupa dziecka, którym jest Shape3D
        wezel_scena.addChild(TG_os_pionowa);        //Dodawanie TransformGroupa do sceny - BranchGroupa
        
        //Powyższe komentarze dotyczące tworzenia osi pionowej robota obowiązują także dla pozostałych części robota
        
        
//        //Tworzenie tła 3D
//        TG_tlo = new TransformGroup();
//        TG_tlo.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
//        
//        Texture Tekstura_tlo = new TextureLoader("blue_texture.jpg", this).getTexture();
//        Appearance Wyglad_tlo = new Appearance();
//        Wyglad_tlo.setTexture(Tekstura_tlo);
//        Wyglad_tlo.setTextureAttributes(new TextureAttributes());
//        
//        Sphere Sfera_tlo = new Sphere(11.5f, Sphere.GENERATE_NORMALS_INWARD + Sphere.GENERATE_TEXTURE_COORDS, Wyglad_tlo);
//        
//        Transform3D T3D_tlo = new Transform3D();
//        T3D_tlo.set(new Vector3f(wsp_os_pionowa[0], wsp_os_pionowa[1], wsp_os_pionowa[0]));
//
//        TG_tlo.setTransform(T3D_tlo);
//        TG_tlo.addChild(Sfera_tlo);
//        wezel_scena.addChild(TG_tlo);


        //Tworzenie statycznego tła 2D
        TextureLoader Tekstura_tlo = new  TextureLoader("blue_texture.jpg",this);
        ImageComponent2D myImage = Tekstura_tlo.getImage( );
        Background tlo =new Background();
        tlo.setImage(myImage);
        tlo.setApplicationBounds(bounds);
        wezel_scena.addChild(tlo);


        //Ramię robota część 1 
        TG_ramie1 = new TransformGroup();
        TG_ramie1.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        Appearance wygladRamienia1 = new Appearance();
        Texture TeksturaRamie1 = new TextureLoader("metal_texture.jpg", this).getTexture();
        wygladRamienia1.setTexture(TeksturaRamie1);
        //wygladRamienia1.setColoringAttributes(new ColoringAttributes(0.4f,0f,0f,ColoringAttributes.NICEST));

        Box Ramie1 = new Box(0.7f, 0.15f, 0.23f, Box.GENERATE_TEXTURE_COORDS, wygladRamienia1);

        Transform3D T3D_Ramienia1 = new Transform3D();
        T3D_Ramienia1.set(new Vector3f(wsp_ramie1[0], wsp_ramie1[1], wsp_ramie1[2]));

        TG_ramie1.setTransform(T3D_Ramienia1);
        TG_ramie1.addChild(Ramie1);
        TG_os_pionowa.addChild(TG_ramie1);


        //Ramię robota część 2
        TG_ramie2 = new TransformGroup();
        TG_ramie2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        Appearance wygladRamienia2 = new Appearance();
        wygladRamienia2.setColoringAttributes(new ColoringAttributes(0f,0.4f,0f,ColoringAttributes.NICEST));

        Cylinder Ramie2 = new Cylinder(0.1f, 1.3f, wygladRamienia2);

        //Pierwszy Transform3D odpowiada za przsunięcie, zapewnia odpowiednie położenie w przestrzeni
        Transform3D T3D_Ramienia2 = new Transform3D();
        T3D_Ramienia2.set(new Vector3f(wsp_ramie2[0], wsp_ramie2[1], wsp_ramie2[2]));

        //Drugi Transform3D odpowiada za przechylenie obiektu (obrót wokół osi Z o 90 stopnio), tak żeby ten element robota był poziomo, a nie pionowo
        Transform3D obrot_Ramienia2 = new Transform3D();
        obrot_Ramienia2.rotZ(-Math.PI/2);

        //Połączenie dwóch Transform3D
        T3D_Ramienia2.mul(obrot_Ramienia2);

        TG_ramie2.setTransform(T3D_Ramienia2);
        TG_ramie2.addChild(Ramie2);
        TG_os_pionowa.addChild(TG_ramie2);


        //Chwytak1
        TG_chwytak1 = new TransformGroup();
        TG_chwytak1.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        Appearance wygladChwytaka = new Appearance();
        wygladChwytaka.setColoringAttributes(new ColoringAttributes(0f,0f,0.4f,ColoringAttributes.NICEST));

        Box Chwytak1 = new Box(0.05f, 0.15f, 0.2f, wygladChwytaka);

        Transform3D T3D_Chwytaka1 = new Transform3D();
        T3D_Chwytaka1.set(new Vector3f(wsp_chwytak1[0], wsp_chwytak1[1], wsp_chwytak1[2]));
       
        TG_chwytak1.setTransform(T3D_Chwytaka1);
        TG_chwytak1.addChild(Chwytak1);
        TG_os_pionowa.addChild(TG_chwytak1);


        //Chwytak2
        TG_chwytak2 = new TransformGroup();
        TG_chwytak2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        Box Chwytak2 = new Box(0.15f, 0.15f, 0.02f, wygladChwytaka);

        Transform3D T3D_Chwytaka2 = new Transform3D();
        T3D_Chwytaka2.set(new Vector3f(wsp_chwytak2[0], wsp_chwytak2[1], wsp_chwytak2[2]));

        TG_chwytak2.setTransform(T3D_Chwytaka2);
        TG_chwytak2.addChild(Chwytak2);
        TG_os_pionowa.addChild(TG_chwytak2);


        //Chwytak3
        TG_chwytak3 = new TransformGroup();
        TG_chwytak3.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        Box Chwytak3 = new Box(0.15f, 0.15f, 0.02f, wygladChwytaka);

        Transform3D T3D_Chwytaka3 = new Transform3D();
        T3D_Chwytaka3.set(new Vector3f(wsp_chwytak3[0], wsp_chwytak3[1], wsp_chwytak3[2]));

        TG_chwytak3.setTransform(T3D_Chwytaka3);
        TG_chwytak3.addChild(Chwytak3);
        TG_os_pionowa.addChild(TG_chwytak3);


        //Podłoga
        TG_podloga = new TransformGroup();
        TG_podloga.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        Appearance wygladPodlogi = new Appearance();
        Texture TeksturaMur = new TextureLoader("pink_plusz_floor.jpg", this).getTexture();
        wygladPodlogi.setTexture(TeksturaMur);
        //wygladPodlogi.setColoringAttributes(new ColoringAttributes(0.5f,0.5f,0.5f,ColoringAttributes.NICEST));

        Box Podloga = new Box(4f, 0.01f, 3f,Box.GENERATE_TEXTURE_COORDS, wygladPodlogi);

        Transform3D T3D_Podlogi = new Transform3D();
        T3D_Podlogi.set(new Vector3f(wsp_podlogi[0], wsp_podlogi[1], wsp_podlogi[2]));
        
        TG_podloga.setTransform(T3D_Podlogi);
        TG_podloga.addChild(Podloga);
        wezel_scena.addChild(TG_podloga);


        //Przedmiot 
        //Transform Group TG_przedmiot jest rodzicem TG_pomocnicza_przedmiotu, sam przedmiot jest dzieckiem TG_pomocnicza_przedmiotu, więc jest najniżej w "drzewie", TG_przedmiot jest dzieckiem BranchGroupa czyli jest najwyżej w drzewie
        //Tworzy to nam taką drabinkę: BranchGroup(wezel_sceny) -> TG_przedmiot -> TG_pomocnicza_przedmiotu -> przedmiot
        //TG_pomocnicza_przedmiotu używany jest do przemieszczania przedmiotu w przestrzeni za pomocą Translacji jego Transform3D
        /* TG_przedmiot używany jest do obrotu przedmiotu wokół innej osi niż jego własna, w przypadku rotacji TG_przedmiot, osią tego obrotu jest oś własna TG_przedmiot, 
        TG_pomocnicza_przedmiotu jako że jest dzieckiem TG_przedmiot także podlega tej rotacji, ale wokół osi TG_przedmiot, a nie swojej, bo dziedziczy tą oś po rodzicu, 
        rotacja TG_pomocnicza_przedmiotu powoduje rotację podpiętego do niego przedmiotu wokół wybranej przez nas osi*/

        TG_przedmiot = new TransformGroup();
        TG_przedmiot.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        wezel_scena.addChild(TG_przedmiot);

        Appearance wygladPrzedmiotu = new Appearance();
        Texture TeksturaPrzedmiot = new TextureLoader("can_texture.jpg", this).getTexture();
        wygladPrzedmiotu.setTexture(TeksturaPrzedmiot);
        //wygladPrzedmiotu.setColoringAttributes(new ColoringAttributes(1f,0f,0f,ColoringAttributes.NICEST));

        //Box Przedmiot = new Box(0.06f, 0.06f, 0.06f, Box.GENERATE_TEXTURE_COORDS, wygladPrzedmiotu);
        Cylinder Przedmiot = new Cylinder(0.1f, 0.2f, Cylinder.GENERATE_TEXTURE_COORDS, wygladPrzedmiotu);
        //Sphere Przedmiot = new Sphere(0.06f, Sphere.GENERATE_TEXTURE_COORDS, wygladPrzedmiotu);

        Transform3D T3D_Przedmiotu = new Transform3D();
        T3D_Przedmiotu.set(new Vector3f(wsp_przedmiotu[0], wsp_przedmiotu[1], wsp_przedmiotu[2]));
       
        TG_pomocnicza_przedmiotu = new TransformGroup(T3D_Przedmiotu);
        TG_pomocnicza_przedmiotu.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        TG_pomocnicza_przedmiotu.addChild(Przedmiot);
        TG_przedmiot.addChild(TG_pomocnicza_przedmiotu);
       
        //Inicjalizacja wykrywania kolizji
        CollisionDetector myColDet = new CollisionDetector(Przedmiot.getShape(WIDTH), Przedmiot.getBounds());
        TG_os_pionowa.addChild(myColDet);

        return wezel_scena;
    }

     
   //MAIN
   public static void main(String args[])
   {
        Projekt_JAVA Projekt_JAVA; /*projekt_JAVA*/
        Projekt_JAVA = new Projekt_JAVA();
   }
   
   
    //KLASA OBSŁUGUJĄCA RUCHY ROBOTA OPARTA NA TIMERZE
   class Ruchy_robota extends TimerTask
    {

        public void run()
        {   
            //czyszczenie licznika obsługującego pamięć robota jeśli zostały już wykonane wszystkie polecenia z listy
            if(tryb_wykonywania == true)
            {
                licznik++;
                if(licznik + 1 == lista_obrot_w_lewo.size())
                    licznik = 0;
            }
            
            //rotacja robota w lewo, ręcznie lub przez nauczoną sekwencję
            if(klawisze[0] == true || (tryb_wykonywania == true && !lista_obrot_w_lewo.isEmpty() && (int)lista_obrot_w_lewo.get(licznik) == 1))     //Sprawdzanie czy ruch został zainicjonwany przez wciśnięcie odpowiedniego klawisza lub przez wymuszenie przez stworzoną pamięć robota
            {     
                //Sprawdzanie czy ruch robota w danym kierunku nie jest zablokowany przez kontakt z prymitywem
                if(kolizja == false || (kolizja == true && (int)lista_sprawdzanie_kolizji.get(lista_sprawdzanie_kolizji.size() - 1) != 1 && (int)lista_sprawdzanie_kolizji.get(lista_sprawdzanie_kolizji.size() - 2) != 1 ) || przedmiot_zlapany == true)
                    Obrot_w_lewo_robota();
                
                if(tryb_uczenia == true)            //Sprawdzanie czy robot jest w trybie uczenia
                    lista_obrot_w_lewo.add(1);      //Dodawanie danego ruchu do pamięci
                
                lista_sprawdzanie_kolizji.add(1);   //Zapamiętanie ostatniego wykonanego ruchu i dodatnie go do odpowieniej listy, która służy do blokowanie ruchu w kierunku kolizji
                licznik2++;                         //Licznik obsługujący listę ostatnich ruchów robota
            } 
            else 
            {
                if(tryb_uczenia == true)            
                    lista_obrot_w_lewo.add(0);      //Zapamiętanie w pamięci, że robot się nie rusza
            }
            
            //rotacja robota w prawo, ręcznie lub przez nauczoną sekwencję
            if(klawisze[1] == true || (tryb_wykonywania == true && !lista_obrot_w_prawo.isEmpty() && (int)lista_obrot_w_prawo.get(licznik) == 2))
            {
               if(kolizja == false || (kolizja == true && (int)lista_sprawdzanie_kolizji.get(lista_sprawdzanie_kolizji.size() - 1) != 2 && (int)lista_sprawdzanie_kolizji.get(lista_sprawdzanie_kolizji.size() - 2) != 2 ) || przedmiot_zlapany == true)
                   Obrot_w_prawo_robota();
               
               if(tryb_uczenia == true)
                   lista_obrot_w_prawo.add(2);
               
               lista_sprawdzanie_kolizji.add(2);
               licznik2++;
            } 
            else 
            {
                if(tryb_uczenia == true)
                    lista_obrot_w_prawo.add(0);
            }
            
            //przesunięcie w pionie do góry, ręcznie lub przez nauczoną sekwencję
            if(klawisze[2] == true || (tryb_wykonywania == true && !lista_podnoszenie_w_pionie.isEmpty() && (int)lista_podnoszenie_w_pionie.get(licznik) == 3)) 
            {
               if(kolizja == false || (kolizja == true && (int)lista_sprawdzanie_kolizji.get(lista_sprawdzanie_kolizji.size() - 1) != 3 && (int)lista_sprawdzanie_kolizji.get(lista_sprawdzanie_kolizji.size() - 2) != 3 ) || przedmiot_zlapany == true)
                    Podnoszenie_sie_w_pionie_robota();
               
               if(tryb_uczenia == true)
                   lista_podnoszenie_w_pionie.add(3);
               
               lista_sprawdzanie_kolizji.add(3);
               licznik2++;
            }
            else 
            {
                if(tryb_uczenia == true)
                    lista_podnoszenie_w_pionie.add(0);
            }
            
            //przesunięcie w pionie w dół, ręcznie lub przez nauczoną sekwencję
            if(klawisze[3] == true || (tryb_wykonywania == true && !lista_opuszczanie_w_pionie.isEmpty() && (int)lista_opuszczanie_w_pionie.get(licznik) == 4))
            {
               if(kolizja == false || (kolizja == true && (int)lista_sprawdzanie_kolizji.get(lista_sprawdzanie_kolizji.size() - 1) != 4 && (int)lista_sprawdzanie_kolizji.get(lista_sprawdzanie_kolizji.size() - 2) != 4 ) || przedmiot_zlapany == true)
                    Opuszczanie_sie_w_pionie_robota();
               
               if(tryb_uczenia == true)
                   lista_opuszczanie_w_pionie.add(4);
               
               lista_sprawdzanie_kolizji.add(4);
               licznik2++;
            }
            else 
            {
                if(tryb_uczenia == true)
                    lista_opuszczanie_w_pionie.add(0);
            }
            
            //przesunięcie w poziomie - wysuwanie, ręcznie lub przez nauczoną sekwencję
            if(klawisze[4] == true || (tryb_wykonywania == true && !lista_wysuwanie_ramienia.isEmpty() && (int)lista_wysuwanie_ramienia.get(licznik) == 5))
            {
               if(kolizja == false || (kolizja == true && (int)lista_sprawdzanie_kolizji.get(lista_sprawdzanie_kolizji.size() - 1) != 5 && (int)lista_sprawdzanie_kolizji.get(lista_sprawdzanie_kolizji.size() - 2) != 5 ) || przedmiot_zlapany == true) 
                    Wysuwanie_ramienia_robota();
               
               if(tryb_uczenia == true)
                   lista_wysuwanie_ramienia.add(5);
               
               lista_sprawdzanie_kolizji.add(5);
               licznik2++;
            } 
            else 
            {
                if(tryb_uczenia == true)
                    lista_wysuwanie_ramienia.add(0);
            }
            
            //przesunięcie w poziomie - wsuwanie, ręcznie lub przez nauczoną sekwencję
            if(klawisze[5] == true || (tryb_wykonywania == true && !lista_cofanie_ramienia.isEmpty() && (int)lista_cofanie_ramienia.get(licznik) == 6))
            {
                if(kolizja == false || (kolizja == true && (int)lista_sprawdzanie_kolizji.get(lista_sprawdzanie_kolizji.size() - 1) != 6 && (int)lista_sprawdzanie_kolizji.get(lista_sprawdzanie_kolizji.size() - 2) != 6 ) || przedmiot_zlapany == true)
                        Cofanie_ramienia_robota();
                
                if(tryb_uczenia == true)
                   lista_cofanie_ramienia.add(6);
                
                lista_sprawdzanie_kolizji.add(6);
                licznik2++;
            } 
            else 
            {
                if(tryb_uczenia == true)
                    lista_cofanie_ramienia.add(0);
            }
            
            //Sprawdzenie czy został wciśniety klawisz powodujący ruch przedmiotu w lewo
            if(klawisze[6] == true)
            {
                Ruch_przedmiotu_lewo();
            }
            
            //Sprawdzenie czy został wciśniety klawisz powodujący ruch przedmiotu w prawo
            if(klawisze[7] == true)
            {
                Ruch_przedmiotu_prawo();
            }
            
            //Sprawdzenie czy został wciśniety klawisz powodujący ruch przedmiotu do przodu
            if(klawisze[8] == true)
            {
                Ruch_przedmiotu_przod();
            }
            
            //Sprawdzenie czy został wciśniety klawisz powodujący ruch przedmiotu do tyłu
            if(klawisze[9] == true)
            {
                Ruch_przedmiotu_tyl();
            }
            
            //Nauka łapania przedmiotu
            if(tryb_uczenia == true)
            {
                if(przedmiot_zlapany == true)
                    lista_lapanie_przedmiotu.add(1);        //Zapamiętanie w pamięci, że robot złapał przedmiot
                
                if(przedmiot_zlapany == false)
                    lista_lapanie_przedmiotu.add(0);        //Zapamiętanie w pamięci, że robot puścił przedmiot
            }
            
            //Łapanie przedmiotu przez robota wykonującego zapamiętany program
            if(tryb_wykonywania == true && !lista_lapanie_przedmiotu.isEmpty() && (int)lista_lapanie_przedmiotu.get(licznik) == 1)      
                przedmiot_zlapany = true;
            
            //Puszczanie przedmiotu przez robota wykonującego zapamiętany program
            if(tryb_wykonywania == true && !lista_lapanie_przedmiotu.isEmpty() && (int)lista_lapanie_przedmiotu.get(licznik) == 0)
                przedmiot_zlapany = false;
            
            
            // Grawitacja
            //Działanie abs(wsp_przedmiotu[1] - wsp_podlogi[1] )-0.12f >0.20f to badanie czy jest taka możliwość, że przedmiot wbije się w podłogę przez to, 
            //że odległość od podłogi jest tak mała, że kolejny krok przekroczy prawidłowe położenie przedmiotu i wbije się on w podłogę, 0.2f f to maksymalny krok grawitacji
            if(przedmiot_zlapany == false && wsp_przedmiotu[1] > wsp_podlogi[1] +0.12f && abs(wsp_przedmiotu[1] - wsp_podlogi[1] )-0.12f >0.20f)    //Sprawdzanie czy przedmiot spada
            {
                Grawitacja();
            }
            //Poprawka w grawitacji, żeby przedmiot lądował na podłodze a nie wbijał się w niego, przez to, że ostatni wykonywany krok jest za duży
            else if(przedmiot_zlapany == false && wsp_przedmiotu[1] > wsp_podlogi[1] +0.12f && abs(wsp_przedmiotu[1] - wsp_podlogi[1])-0.12f <= 0.20f)
            {
                wsp_przedmiotu[1] = wsp_podlogi[1] +0.12f;
                trans.setTranslation(new Vector3f(wsp_przedmiotu[0],wsp_przedmiotu[1],wsp_przedmiotu[2]));
                TG_pomocnicza_przedmiotu.setTransform(trans);
            }
            //Przedmiot już nie spada, więc można zresetować przyspieszenie grawitacyjne, żeby było gotowe do kolejnego spadania
            else
            {
                przyspieszenie_grawitacyjne = 1;
            }
         
            //Czyszczenie listy sprawdzającej kolizję
            if(licznik2 == 20)
            {
                while(lista_sprawdzanie_kolizji.size() != 5)
                {
                    lista_sprawdzanie_kolizji.remove(0);
                }
                licznik2 = 0;
            }
            
            
            //Kontrola kąta położenia przedmiotu aby znajdował się w przedziale <-2*pi, 2*pi>
            if(aktualny_kat_przedmiotu > (float)(2*Math.PI))
                aktualny_kat_przedmiotu = aktualny_kat_przedmiotu - (float)(2*Math.PI);
            
            if(aktualny_kat_przedmiotu < -(float)(2*Math.PI))
                aktualny_kat_przedmiotu = aktualny_kat_przedmiotu + (float)(2*Math.PI);
            
            
            //Kontrola kąta położenia robota aby znajdował się w przedziale <-2*pi, 2*pi>
            if(aktualny_kat_robota > (float)(2*Math.PI))
                aktualny_kat_robota = aktualny_kat_robota - (float)(2*Math.PI);
            
            if(aktualny_kat_robota < -(float)(2*Math.PI))
                aktualny_kat_robota = aktualny_kat_robota + (float)(2*Math.PI);
        }
  }
   
   
   //Klasa zapewniająca wykrywanie kolizji 
    class CollisionDetector extends Behavior 
    {
    /** The separate criteria used to wake up this beahvior. */
    protected WakeupCriterion[] theCriteria;

    /** The OR of the separate criteria. */
    protected WakeupOr oredCriteria;

    /** The shape that is watched for collision. */
    protected Shape3D collidingShape;

    /**
     * @param theShape
     *            Shape3D that is to be watched for collisions.
     * @param theBounds
     *            Bounds that define the active region for this behaviour
     */
    public CollisionDetector(Shape3D theShape, Bounds theBounds) {
      collidingShape = theShape;
      setSchedulingBounds(theBounds);
    }

    /**
     * This creates an entry, exit and movement collision criteria. These are
     * then OR'ed together, and the wake up condition set to the result.
     */
    //Sposób wykrywania kolizji oparty jest na USE_GEOMETRY co zapewnia duża dokładność, ale wymaga dużej mocy obliczeniowej
    public void initialize() {
      theCriteria = new WakeupCriterion[3];
      theCriteria[0] = new WakeupOnCollisionEntry(collidingShape, WakeupOnCollisionEntry.USE_GEOMETRY);
      theCriteria[1] = new WakeupOnCollisionExit(collidingShape, WakeupOnCollisionExit.USE_GEOMETRY);
      theCriteria[2] = new WakeupOnCollisionMovement(collidingShape, WakeupOnCollisionMovement.USE_GEOMETRY);
      oredCriteria = new WakeupOr(theCriteria);
      wakeupOn(oredCriteria);
    }

    /**
     * Where the work is done in this class. A message is printed out using the
     * userData of the object collided with. The wake up condition is then set
     * to the OR'ed criterion again.
     */
    public void processStimulus(Enumeration criteria) 
    {
      WakeupCriterion theCriterion = (WakeupCriterion) criteria.nextElement();
      if (theCriterion instanceof WakeupOnCollisionEntry)   //Wykrywanie udzerzenia robota w prymityw
      {
        if(przedmiot_zlapany == false)
        kolizja = true;
        System.out.println(kolizja);
      } 
      else if (theCriterion instanceof WakeupOnCollisionExit)   //Wykrywanie braku kontaktu robota z prymitywem
      {
        kolizja = false;
        System.out.println(kolizja);
      } 
      else 
      {
      }
      wakeupOn(oredCriteria);  }
}
    
    //Metoda obsługująca przyciski w interfejsie
    @Override
    public void actionPerformed(ActionEvent e) 
    {
        //podnoszenie przedmiotu
       /* if(e.getSource()== przyciski[0])
        {          
           wsp_przedmiotu[1] += przesuniecie_w_pionie;
           trans.setTranslation(new Vector3f(wsp_przedmiotu[0],wsp_przedmiotu[1],wsp_przedmiotu[2]));
            TG_pomocnicza_przedmiotu.setTransform(trans);
        } 
        
        //opuszczanie przedmiotu
        else if(e.getSource()== przyciski[1])
        {
           wsp_przedmiotu[1] -= przesuniecie_w_pionie;
           trans.setTranslation(new Vector3f(wsp_przedmiotu[0],wsp_przedmiotu[1],wsp_przedmiotu[2]));
            TG_pomocnicza_przedmiotu.setTransform(trans);
        }*/
        
        //łapanie przedmiotu
       if(e.getSource()== przyciski[2])
       {
           if(przedmiot_zlapany==false)
           
               //Sprawdzanie czy puszka znaduje się pomiędzy chwytakami
               if(wsp_przedmiotu[0]-wsp_chwytak1[0] < 0.25f &&wsp_przedmiotu[0]-wsp_chwytak1[0] > 0.15f && wsp_ramie1[1] < -1.02f && (abs(aktualny_kat_robota-aktualny_kat_przedmiotu)< Math.PI/32 || abs(aktualny_kat_robota + 2*Math.PI -aktualny_kat_przedmiotu)< Math.PI/32 || abs(aktualny_kat_robota - 2*Math.PI -aktualny_kat_przedmiotu)< Math.PI/32))
                   przedmiot_zlapany = true;                               
       }
       
       //puszczanie przedmiotu
       else if(e.getSource()== przyciski[3])
       {
           if(przedmiot_zlapany==true)
                przedmiot_zlapany = false;
       }
       
       //Uruchomienie uczenia sekwencji ruchów robota
        else if(e.getSource()== przyciski[4])
         {
             //opróżnianie pamięci ruchów robota
            while(!lista_obrot_w_lewo.isEmpty())
            {
                lista_obrot_w_lewo.remove(0);
                lista_obrot_w_prawo.remove(0);
                lista_podnoszenie_w_pionie.remove(0);
                lista_opuszczanie_w_pionie.remove(0);
                lista_wysuwanie_ramienia.remove(0);
                lista_cofanie_ramienia.remove(0);
                lista_lapanie_przedmiotu.remove(0);
            }
            
            tryb_uczenia = true;
            tryb_wykonywania = false;
         }
        
        //Uruchomienie wykonywania nauczonej sekswencji ruchów przez robota
        else if(e.getSource()== przyciski[5])
        {
            licznik = -1;
            tryb_uczenia = false;
            tryb_wykonywania = true;
        }
        
        //Uruchomienie swobodnego sterowania robotem, bez uczenia i bez wykonywania programu
        else if(e.getSource()== przyciski[6])
        {
            tryb_uczenia = false;
            tryb_wykonywania = false;
        }
        
        //Resetowanie ustawienia kamery do ustawienia domyślnego
        else if(e.getSource()== przyciski[7])
        {
            Transform3D reset_kamery = new Transform3D();
            reset_kamery.set(new Vector3f(0.0f,0.0f,12.0f));

            simpleU.getViewingPlatform().getViewPlatformTransform().setTransform(reset_kamery);
        }
        

    }
    
    @Override
    public void keyTyped(KeyEvent e) 
    {
           
    }
    
    
    //Metoda obsługująca wciśnięcie odpowiedniego klawisza klawiatury przez użytkownika
    @Override
    public void keyPressed(KeyEvent e) 
    {
            switch(e.getKeyCode()){
                case KeyEvent.VK_A:       klawisze[0] = true; break;
                case KeyEvent.VK_D:       klawisze[1] = true; break;
                case KeyEvent.VK_W:       klawisze[2] = true; break;
                case KeyEvent.VK_S:       klawisze[3] = true; break;
                case KeyEvent.VK_E:       klawisze[4] = true; break;
                case KeyEvent.VK_Q:       klawisze[5] = true; break;
                case KeyEvent.VK_LEFT:    klawisze[6] = true; break;
                case KeyEvent.VK_RIGHT:   klawisze[7] = true; break;
                case KeyEvent.VK_UP:      klawisze[8] = true; break;
                case KeyEvent.VK_DOWN:    klawisze[9] = true; break;
            }  
    }
    
    //Metoda obsługująca puszczenie odpowiedniego klawisza klawiatury przez użytkownika
    @Override
    public void keyReleased(KeyEvent e) 
    {
            switch(e.getKeyCode()){
                case KeyEvent.VK_A:       klawisze[0] = false; break;
                case KeyEvent.VK_D:       klawisze[1] = false; break;
                case KeyEvent.VK_W:       klawisze[2] = false; break;
                case KeyEvent.VK_S:       klawisze[3] = false; break;
                case KeyEvent.VK_E:       klawisze[4] = false; break;
                case KeyEvent.VK_Q:       klawisze[5] = false; break;
                case KeyEvent.VK_LEFT:    klawisze[6] = false; break;
                case KeyEvent.VK_RIGHT:   klawisze[7] = false; break;
                case KeyEvent.VK_UP:      klawisze[8] = false; break;
                case KeyEvent.VK_DOWN:    klawisze[9] = false; break;
            }
    }
    
    
    //Metoda obsłuhująca podnoszenie się robota w pionie
    public void Podnoszenie_sie_w_pionie_robota() 
    {
        //przesuwanie kolejnych elementów robota zgodnie z wymaganych ruchem
        if(wsp_ramie1[1]<=1.075f)       //Sprawdzenie czy robot nie podniesie się poza swoje ograniczenia
            {
                wsp_ramie1[1] += przesuniecie_w_pionie;
                trans.setTranslation(new Vector3f(wsp_ramie1[0],wsp_ramie1[1],wsp_ramie1[2]));
                TG_ramie1.setTransform(trans);

                wsp_ramie2[1] += przesuniecie_w_pionie;
                trans.setTranslation(new Vector3f(wsp_ramie2[0],wsp_ramie2[1],wsp_ramie2[2]));
                Transform3D obrot_ramienia = new Transform3D();
                obrot_ramienia.rotZ(-Math.PI/2);
                trans.mul(obrot_ramienia);
                TG_ramie2.setTransform(trans);
                obrot_ramienia.rotZ(+Math.PI/2);
                trans.mul(obrot_ramienia);

                wsp_chwytak1[1] += przesuniecie_w_pionie;
                trans.setTranslation(new Vector3f(wsp_chwytak1[0],wsp_chwytak1[1],wsp_chwytak1[2]));
                TG_chwytak1.setTransform(trans);

                wsp_chwytak2[1] += przesuniecie_w_pionie;
                trans.setTranslation(new Vector3f(wsp_chwytak2[0],wsp_chwytak2[1],wsp_chwytak2[2]));
                TG_chwytak2.setTransform(trans);

                wsp_chwytak3[1] += przesuniecie_w_pionie;
                trans.setTranslation(new Vector3f(wsp_chwytak3[0],wsp_chwytak3[1],wsp_chwytak3[2]));
                TG_chwytak3.setTransform(trans);
                
                //Sprawdzenie czy przedmiot porusza się razem z robotem czy nie
                if(przedmiot_zlapany==true)
                {
                    wsp_przedmiotu[1] += przesuniecie_w_pionie;
                    trans.setTranslation(new Vector3f(wsp_przedmiotu[0],wsp_przedmiotu[1],wsp_przedmiotu[2]));
                    TG_pomocnicza_przedmiotu.setTransform(trans);
                }
            
            } 
    }
    
    
     //Metoda obsłuhująca opuszczanie się robota w pionie
    public void Opuszczanie_sie_w_pionie_robota() 
    {
        //przesuwanie kolejnych elementów robota zgodnie z wymaganych ruchem
        if(wsp_ramie1[1]>=-1.075f)      //Sprawdzenie czy robot nie obniży się poza swoje ograniczenia
            {
                wsp_ramie1[1] -= przesuniecie_w_pionie;
                trans.setTranslation(new Vector3f(wsp_ramie1[0],wsp_ramie1[1],wsp_ramie1[2]));
                TG_ramie1.setTransform(trans);

                wsp_ramie2[1] -= przesuniecie_w_pionie;
                trans.setTranslation(new Vector3f(wsp_ramie2[0],wsp_ramie2[1],wsp_ramie2[2]));
                Transform3D obrot_ramienia = new Transform3D();
                obrot_ramienia.rotZ(-Math.PI/2);
                trans.mul(obrot_ramienia);
                TG_ramie2.setTransform(trans);
                obrot_ramienia.rotZ(+Math.PI/2);
                trans.mul(obrot_ramienia);

                wsp_chwytak1[1] -= przesuniecie_w_pionie;
                trans.setTranslation(new Vector3f(wsp_chwytak1[0],wsp_chwytak1[1],wsp_chwytak1[2]));
                TG_chwytak1.setTransform(trans);

                wsp_chwytak2[1] -= przesuniecie_w_pionie;
                trans.setTranslation(new Vector3f(wsp_chwytak2[0],wsp_chwytak2[1],wsp_chwytak2[2]));
                TG_chwytak2.setTransform(trans);

                wsp_chwytak3[1] -= przesuniecie_w_pionie;
                trans.setTranslation(new Vector3f(wsp_chwytak3[0],wsp_chwytak3[1],wsp_chwytak3[2]));
                TG_chwytak3.setTransform(trans);
               
                //Sprawdzenie czy przedmiot porusza się razem z robotem czy nie
                if(przedmiot_zlapany==true)
                {
                    wsp_przedmiotu[1] -= przesuniecie_w_pionie;
                    trans.setTranslation(new Vector3f(wsp_przedmiotu[0],wsp_przedmiotu[1],wsp_przedmiotu[2]));
                    TG_pomocnicza_przedmiotu.setTransform(trans);
                }
            }
    }
    
    
     //Metoda obsłuhująca wysuwanie się ramienia robota
    public void Wysuwanie_ramienia_robota() 
    {
        //przesuwanie kolejnych elementów robota zgodnie z wymaganych ruchem
        if(wsp_ramie2[0]<=1.6f)     //Sprawdzenie czy robot nie wysunie ramienia poza swoje ograniczenia
            {   
                wsp_ramie2[0] += przesuniecie_w_poziomie;
                trans.setTranslation(new Vector3f(wsp_ramie2[0],wsp_ramie2[1],wsp_ramie2[2]));
                Transform3D obrot_ramienia = new Transform3D();
                obrot_ramienia.rotZ(-Math.PI/2);
                trans.mul(obrot_ramienia);
                TG_ramie2.setTransform(trans);
                obrot_ramienia.rotZ(+Math.PI/2);
                trans.mul(obrot_ramienia);
                
                wsp_chwytak1[0] += przesuniecie_w_poziomie;
                trans.setTranslation(new Vector3f(wsp_chwytak1[0],wsp_chwytak1[1],wsp_chwytak1[2]));
                TG_chwytak1.setTransform(trans);

                wsp_chwytak2[0] += przesuniecie_w_poziomie;
                trans.setTranslation(new Vector3f(wsp_chwytak2[0],wsp_chwytak2[1],wsp_chwytak2[2]));
                TG_chwytak2.setTransform(trans);

                wsp_chwytak3[0] += przesuniecie_w_poziomie;
                trans.setTranslation(new Vector3f(wsp_chwytak3[0],wsp_chwytak3[1],wsp_chwytak3[2]));
                TG_chwytak3.setTransform(trans);
                
                //Sprawdzenie czy przedmiot porusza się razem z robotem czy nie
                if(przedmiot_zlapany==true)
                {
                    wsp_przedmiotu[0] += przesuniecie_w_poziomie;
                    trans.setTranslation(new Vector3f(wsp_przedmiotu[0],wsp_przedmiotu[1],wsp_przedmiotu[2]));
                    TG_pomocnicza_przedmiotu.setTransform(trans);
                }
                
            }
    }
    
    
     //Metoda obsłuhująca cofanie się ramienia robota
    public void Cofanie_ramienia_robota() 
    {
        //przesuwanie kolejnych elementów robota zgodnie z wymaganych ruchem
        if(wsp_ramie2[0]>=0.6f)         //Sprawdzenie czy robot nie cofnie ramienia poza swoje ograniczenia
            {
                wsp_ramie2[0] -= przesuniecie_w_poziomie;
                trans.setTranslation(new Vector3f(wsp_ramie2[0],wsp_ramie2[1],wsp_ramie2[2]));
                Transform3D obrot_ramienia = new Transform3D();
                obrot_ramienia.rotZ(-Math.PI/2);
                trans.mul(obrot_ramienia);
                TG_ramie2.setTransform(trans);
                obrot_ramienia.rotZ(+Math.PI/2);
                trans.mul(obrot_ramienia);

                wsp_chwytak1[0] -= przesuniecie_w_poziomie;
                trans.setTranslation(new Vector3f(wsp_chwytak1[0],wsp_chwytak1[1],wsp_chwytak1[2]));
                TG_chwytak1.setTransform(trans);

                wsp_chwytak2[0] -= przesuniecie_w_poziomie;
                trans.setTranslation(new Vector3f(wsp_chwytak2[0],wsp_chwytak2[1],wsp_chwytak2[2]));
                TG_chwytak2.setTransform(trans);

                wsp_chwytak3[0] -= przesuniecie_w_poziomie;
                trans.setTranslation(new Vector3f(wsp_chwytak3[0],wsp_chwytak3[1],wsp_chwytak3[2]));
                TG_chwytak3.setTransform(trans);
                
                //Sprawdzenie czy przedmiot porusza się razem z robotem czy nie
                if(przedmiot_zlapany==true)
                {
                    wsp_przedmiotu[0] -= przesuniecie_w_poziomie;
                    trans.setTranslation(new Vector3f(wsp_przedmiotu[0],wsp_przedmiotu[1],wsp_przedmiotu[2]));
                    TG_pomocnicza_przedmiotu.setTransform(trans);
                }
            }
    }
    
    
    //Metoda obsługąca obrót robota w lewo
    public void Obrot_w_lewo_robota() 
    {
        trans_rotacja_robota.setTranslation(new Vector3f(wsp_os_pionowa[0],wsp_os_pionowa[1],wsp_os_pionowa[2]));
        Transform3D obrot_robota = new Transform3D();
        obrot_robota.rotY(kat_obrotu);
        trans_rotacja_robota.mul(obrot_robota);
        TG_os_pionowa.setTransform(trans_rotacja_robota);
         
         aktualny_kat_robota -= kat_obrotu;     //Uaktualnienie obecnego położenia robota
         
         //Sprawdzenie czy przedmiot porusza się razem z robotem czy nie
          if(przedmiot_zlapany==true)
          {
                trans_rotacja_przedmiotu.setTranslation(new Vector3f(wsp_os_pionowa[0],wsp_os_pionowa[1],wsp_os_pionowa[2]));
                Transform3D obrot_przedmiotu = new Transform3D();
                obrot_przedmiotu.rotY(kat_obrotu);
                trans_rotacja_przedmiotu.mul(obrot_przedmiotu);
                TG_przedmiot.setTransform(trans_rotacja_przedmiotu);
                
                aktualny_kat_przedmiotu -= kat_obrotu;      //Uaktualnienie obecnego położenia prymitywu
          }
                
    }
    
    
    //Metoda obsługąca obrót robota w prawo
     public void Obrot_w_prawo_robota() 
    {
        trans_rotacja_robota.setTranslation(new Vector3f(wsp_os_pionowa[0],wsp_os_pionowa[1],wsp_os_pionowa[2]));
        Transform3D obrot_robota = new Transform3D();
        obrot_robota.rotY(-kat_obrotu);
        trans_rotacja_robota.mul(obrot_robota);
        TG_os_pionowa.setTransform(trans_rotacja_robota);
        
        aktualny_kat_robota += kat_obrotu;      //Uaktualnienie obecnego położenia robota
        
        //Sprawdzenie czy przedmiot porusza się razem z robotem czy nie
        if(przedmiot_zlapany==true)
        {
            trans_rotacja_przedmiotu.setTranslation(new Vector3f(wsp_os_pionowa[0],wsp_os_pionowa[1],wsp_os_pionowa[2]));
            Transform3D obrot_przedmiotu = new Transform3D();
            obrot_przedmiotu.rotY(-kat_obrotu);
            trans_rotacja_przedmiotu.mul(obrot_przedmiotu);
            TG_przedmiot.setTransform(trans_rotacja_przedmiotu);
            
            aktualny_kat_przedmiotu += kat_obrotu;      //Uaktualnienie obecnego położenia prymitywu
        }

    }
     
     
     //Metoda obsługująca przemieszenie się prymitywu po kole w lewo względem osi pionowej robota
     public void Ruch_przedmiotu_lewo() 
    {
        aktualny_kat_przedmiotu -= kat_obrotu;      //Uaktualnienie obecnego położenia prymitywu

         trans_rotacja_przedmiotu.setTranslation(new Vector3f(wsp_os_pionowa[0],wsp_os_pionowa[1],wsp_os_pionowa[2]));
         Transform3D obrot_przedmiotu = new Transform3D();
         obrot_przedmiotu.rotY(kat_obrotu);
         trans_rotacja_przedmiotu.mul(obrot_przedmiotu);
         TG_przedmiot.setTransform(trans_rotacja_przedmiotu);
    }
     
     
     //Metoda obsługująca przemieszenie się prymitywu po kole w prawo względem osi pionowej robota
     public void Ruch_przedmiotu_prawo() 
    {
        aktualny_kat_przedmiotu += kat_obrotu;          //Uaktualnienie obecnego położenia prymitywu
        
         trans_rotacja_przedmiotu.setTranslation(new Vector3f(wsp_os_pionowa[0],wsp_os_pionowa[1],wsp_os_pionowa[2]));
         Transform3D obrot_przedmiotu = new Transform3D();
         obrot_przedmiotu.rotY(-kat_obrotu);
         trans_rotacja_przedmiotu.mul(obrot_przedmiotu);
         TG_przedmiot.setTransform(trans_rotacja_przedmiotu);
    }
     
     
     //Metoda obsługująca oddalanie się prymitu od robota w linii prostej
     public void Ruch_przedmiotu_przod() 
    {
        wsp_przedmiotu[0] += przesuniecie_w_poziomie;
        trans.setTranslation(new Vector3f(wsp_przedmiotu[0],wsp_przedmiotu[1],wsp_przedmiotu[2]));
        TG_pomocnicza_przedmiotu.setTransform(trans);
    }
     
     
     //Metoda obsługująca przybliżenie się prymitu do robota w linii prostej
     public void Ruch_przedmiotu_tyl() 
    {
        wsp_przedmiotu[0] -= przesuniecie_w_poziomie;
        trans.setTranslation(new Vector3f(wsp_przedmiotu[0],wsp_przedmiotu[1],wsp_przedmiotu[2]));
        TG_pomocnicza_przedmiotu.setTransform(trans);
    }
     
     
     //Metoda obsługująca przemieszczanie się robota w doł pod wpływem grawitacji
    public void Grawitacja()
     {
        wsp_przedmiotu[1] -= krok_grawitacji*przyspieszenie_grawitacyjne;
        trans.setTranslation(new Vector3f(wsp_przedmiotu[0],wsp_przedmiotu[1],wsp_przedmiotu[2]));
        TG_pomocnicza_przedmiotu.setTransform(trans);
        
        if(przyspieszenie_grawitacyjne <10)
            przyspieszenie_grawitacyjne++;
     }
     
     

}

