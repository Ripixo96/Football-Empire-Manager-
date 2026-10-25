package com.footballempire.career;

import android.app.Activity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {
    LinearLayout root;
    SharedPreferences prefs;
    int played, wins, draws, losses, points, goalsFor, goalsAgainst, cash;
    String club = "Radom FC";
    final String[] clubs = {"Radom FC", "Warszawa United", "Kraków Athletic", "Gdańsk City", "Poznań Rovers"};
    final Random random = new Random();
    Match lastMatch;

    static class Match {
        int home, away;
        String homeName, awayName;
        ArrayList<String> events = new ArrayList<>();
        Match(int h, int a, String hn, String an) { home=h; away=a; homeName=hn; awayName=an; }
    }

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = getSharedPreferences("career", MODE_PRIVATE);
        load();
        showHome();
    }

    void load() {
        played=prefs.getInt("played",0); wins=prefs.getInt("wins",0); draws=prefs.getInt("draws",0);
        losses=prefs.getInt("losses",0); points=prefs.getInt("points",0);
        goalsFor=prefs.getInt("gf",0); goalsAgainst=prefs.getInt("ga",0); cash=prefs.getInt("cash",5000000);
        club=prefs.getString("club","Radom FC");
    }
    void save() {
        prefs.edit().putInt("played",played).putInt("wins",wins).putInt("draws",draws).putInt("losses",losses)
            .putInt("points",points).putInt("gf",goalsFor).putInt("ga",goalsAgainst).putInt("cash",cash).putString("club",club).apply();
    }
    TextView text(String s, int size) {
        TextView t=new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(Color.WHITE);
        t.setPadding(8,8,8,8); return t;
    }
    Button button(String s, Runnable r) {
        Button b=new Button(this); b.setText(s); b.setAllCaps(false);
        b.setOnClickListener(v -> r.run()); return b;
    }
    void base(String title) {
        root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(20,18,20,18);
        root.setBackgroundColor(Color.rgb(18,22,32));
        ScrollView sc=new ScrollView(this); sc.addView(root); setContentView(sc);
        TextView h=text(title,26); h.setTypeface(null,Typeface.BOLD); root.addView(h);
    }
    void showHome() {
        base("FOOTBALL EMPIRE");
        root.addView(text("ULTIMATE CAREER  •  WERSJA 0.1",13));
        root.addView(text("Twój klub: "+club,21));
        root.addView(text("Bilans ligowy: "+played+" meczów   W: "+wins+"  R: "+draws+"  P: "+losses,16));
        root.addView(text("Punkty: "+points+"   Bramki: "+goalsFor+":"+goalsAgainst,16));
        root.addView(text("Budżet: "+String.format(Locale.US,"%,d",cash)+" zł",16));
        root.addView(button("Rozegraj następny mecz", this::chooseMatch));
        root.addView(button("Zmień klub", this::chooseClub));
        root.addView(button("Tabela ligi", this::showTable));
        root.addView(button("Historia ostatniego meczu", this::showLast));
        root.addView(button("Nowa kariera (reset)", this::resetCareer));
        root.addView(text("Prototyp offline: zapis kariery, wspólny silnik wyników i wybór trybu meczu. Baza klubów jest demonstracyjna; rzeczywiste dane wymagają legalnego źródła.",13));
    }
    void chooseClub() {
        base("Wybierz klub");
        for(String c:clubs) root.addView(button(c, ()->{club=c; save(); showHome();}));
        root.addView(button("Wróć",this::showHome));
    }
    void chooseMatch() {
        base("Przed meczem");
        String selectedOpponent=clubs[random.nextInt(clubs.length)];
        while(selectedOpponent.equals(club)) selectedOpponent=clubs[random.nextInt(clubs.length)];
        final String opponent=selectedOpponent;
        root.addView(text(club+"  vs  "+opponent,21));
        root.addView(text("Wybierz sposób rozegrania. Oba tryby używają tego samego wyniku silnika.",15));
        root.addView(button("Szybka symulacja",()->play(opponent,false)));
        root.addView(button("Mecz 2D",()->play(opponent,true)));
        root.addView(button("Wróć",this::showHome));
    }
    Match simulate(String opponent) {
        int home=1+random.nextInt(4), away=random.nextInt(4);
        Match m=new Match(home,away,club,opponent);
        for(int i=0;i<home;i++) m.events.add((8+random.nextInt(82))+"' GOL: "+club);
        for(int i=0;i<away;i++) m.events.add((8+random.nextInt(82))+"' GOL: "+opponent);
        Collections.sort(m.events, (a,b)->Integer.compare(Integer.parseInt(a.substring(0,a.indexOf("'"))),Integer.parseInt(b.substring(0,b.indexOf("'")))));
        return m;
    }
    void play(String opponent, boolean animate) {
        Match m=simulate(opponent);
        played++; goalsFor+=m.home; goalsAgainst+=m.away;
        if(m.home>m.away){wins++;points+=3;cash+=250000;} else if(m.home==m.away){draws++;points++;cash+=100000;} else {losses++;cash+=50000;}
        lastMatch=m; save();
        if(animate) show2D(m); else showResult(m);
    }
    void showResult(Match m) {
        base("Koniec meczu");
        root.addView(text(m.homeName+"  "+m.home+" : "+m.away+"  "+m.awayName,23));
        root.addView(text("Statystyki prototypu",18));
        root.addView(text("Strzały (symulowane): "+(m.home*3+2)+" : "+(m.away*3+1),15));
        root.addView(text("Posiadanie piłki (symulowane): "+(40+random.nextInt(21))+"% : reszta",15));
        for(String e:m.events) root.addView(text(e,15));
        root.addView(button("Wróć do kariery",this::showHome));
    }
    void show2D(Match m) {
        base("Mecz 2D");
        root.addView(text(m.homeName+"  "+m.home+" : "+m.away+"  "+m.awayName,19));
        PitchView pitch=new PitchView(this,m);
        root.addView(pitch,new LinearLayout.LayoutParams(-1,0,1));
        root.addView(text("Przebieg spotkania",18));
        for(String e:m.events) root.addView(text(e,14));
        root.addView(button("Zakończ i wróć do kariery",this::showHome));
    }
    class PitchView extends View {
        Paint p=new Paint(3); Match m;
        PitchView(Activity a,Match match){super(a);m=match;setMinimumHeight(600);}
        @Override protected void onDraw(Canvas c) {
            super.onDraw(c); float w=getWidth(),h=getHeight();
            p.setColor(Color.rgb(36,120,66)); c.drawRect(0,0,w,h,p);
            p.setColor(Color.rgb(42,132,72));
            for(int i=0;i<8;i++) if(i%2==0)c.drawRect(w*i/8,0,w*(i+1)/8,h,p);
            p.setColor(Color.WHITE);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(3);
            c.drawRect(12,12,w-12,h-12,p);c.drawLine(w/2,12,w/2,h-12,p);c.drawCircle(w/2,h/2,Math.min(w,h)*.12f,p);
            c.drawRect(12,h*.28f,w*.16f,h*.72f,p);c.drawRect(w*.84f,h*.28f,w-12,h*.72f,p);p.setStyle(Paint.Style.FILL);
            p.setTextSize(32);p.setTypeface(Typeface.DEFAULT_BOLD);p.setColor(Color.WHITE);
            c.drawText(m.homeName+"  "+m.home,w*.05f,48,p);c.drawText(m.away+"  "+m.awayName,w*.56f,48,p);
            for(int i=0;i<11;i++){
                float x=w*.12f+(i%4)*w*.08f; float y=h*.2f+(i/4)*h*.22f;
                p.setColor(Color.rgb(242,242,242));c.drawCircle(x,y,10,p);
                p.setColor(Color.rgb(65,120,255));c.drawCircle(w-x,y,10,p);
            }
            p.setColor(Color.WHITE);c.drawCircle(w*.52f,h*.52f,7,p);
        }
    }
    void showTable() {
        base("Tabela ligi");
        root.addView(text("Pozycja  Klub                 M   Pkt   Bramki",15));
        ArrayList<String> names=new ArrayList<>(Arrays.asList(clubs));
        names.sort((a,b)-> a.equals(club)?-1:(b.equals(club)?1:a.compareTo(b)));
        int pos=1;
        for(String n:names) root.addView(text(pos+++".  "+n+(n.equals(club)?"   "+played+"   "+points+"   "+goalsFor+":"+goalsAgainst:"   —    —      —"),16));
        root.addView(text("Uwaga: pełna symulacja pozostałych klubów i terminarz ligowy są kolejnym etapem.",13));
        root.addView(button("Wróć",this::showHome));
    }
    void showLast() {
        base("Ostatni mecz");
        if(lastMatch==null){root.addView(text("Brak meczu w tej sesji. Wynik kariery jest zapisany.",16));}
        else {root.addView(text(lastMatch.homeName+" "+lastMatch.home+" : "+lastMatch.away+" "+lastMatch.awayName,20)); for(String e:lastMatch.events)root.addView(text(e,15));}
        root.addView(button("Wróć",this::showHome));
    }
    void resetCareer() {
        new android.app.AlertDialog.Builder(this).setTitle("Nowa kariera").setMessage("Usunąć zapis i rozpocząć od początku?")
            .setNegativeButton("Anuluj",(d,w)->{}).setPositiveButton("Resetuj",(d,w)->{
                prefs.edit().clear().apply();load();lastMatch=null;showHome();
            }).show();
    }
}
