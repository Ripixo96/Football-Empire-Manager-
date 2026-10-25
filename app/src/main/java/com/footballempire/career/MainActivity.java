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

    static class Player {
        String name, position; int rating, potential, age, value;
        Player(String n,String p,int r,int pot,int a,int v){name=n;position=p;rating=r;potential=pot;age=a;value=v;}
    }
    final Player[] squad = {
        new Player("M. Nowak","BR",72,79,25,1800000),
        new Player("A. Zieliński","PO",68,76,22,950000),
        new Player("K. Wiśniewski","ŚO",71,78,24,1400000),
        new Player("P. Wójcik","ŚO",70,74,28,1100000),
        new Player("J. Kamiński","LO",69,80,21,1200000),
        new Player("T. Lewandowski","ŚPD",73,77,27,2100000),
        new Player("B. Dąbrowski","ŚP",75,82,23,3200000),
        new Player("S. Kozłowski","ŚP",70,81,20,1700000),
        new Player("D. Szymański","PS",74,79,24,2600000),
        new Player("M. Kaczmarek","N",76,78,26,3500000),
        new Player("O. Mazur","LS",72,84,19,2400000),
        new Player("R. Pawlak","BR",65,70,31,350000),
        new Player("Ł. Król","ŚO",66,73,22,600000),
        new Player("F. Jankowski","ŚP",67,77,20,850000),
        new Player("C. Urban","N",68,75,23,900000)
    };

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
    static final int BG = Color.rgb(8,13,25), SURFACE = Color.rgb(18,27,45), SURFACE2 = Color.rgb(25,37,58);
    static final int MUTED = Color.rgb(157,172,194), LIME = Color.rgb(194,255,82), BORDER = Color.rgb(43,59,82);
    int dp(float v) { return (int)(v*getResources().getDisplayMetrics().density+0.5f); }
    android.graphics.drawable.GradientDrawable shape(int color, int stroke, int radius) {
        android.graphics.drawable.GradientDrawable d=new android.graphics.drawable.GradientDrawable();
        d.setColor(color); d.setCornerRadius(dp(radius)); if(stroke!=0)d.setStroke(dp(1),stroke); return d;
    }
    TextView text(String s, int size) {
        TextView t=new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(Color.WHITE);
        t.setPadding(dp(3),dp(5),dp(3),dp(5)); return t;
    }
    TextView muted(String s,int size) { TextView t=text(s,size); t.setTextColor(MUTED); return t; }
    TextView bold(String s,int size) { TextView t=text(s,size); t.setTypeface(Typeface.DEFAULT,Typeface.BOLD); return t; }
    LinearLayout panel() {
        LinearLayout p=new LinearLayout(this); p.setOrientation(LinearLayout.VERTICAL); p.setPadding(dp(15),dp(13),dp(15),dp(13));
        p.setBackground(shape(SURFACE,BORDER,17)); LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);
        lp.bottomMargin=dp(12); root.addView(p,lp); return p;
    }
    Button button(String s, Runnable r) { return button(s,r,false); }
    Button button(String s, Runnable r, boolean primary) {
        Button b=new Button(this); b.setText(s); b.setAllCaps(false); b.setTextSize(14); b.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        b.setTextColor(primary?Color.rgb(12,22,14):Color.WHITE); b.setMinHeight(dp(50)); b.setPadding(dp(10),dp(7),dp(10),dp(7));
        b.setBackground(shape(primary?LIME:SURFACE2,primary?LIME:BORDER,13)); b.setOnClickListener(v->r.run());
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2); lp.topMargin=dp(4); lp.bottomMargin=dp(4); b.setLayoutParams(lp); return b;
    }
    void base(String title) {
        getWindow().setStatusBarColor(BG); getWindow().setNavigationBarColor(BG);
        root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(17),dp(16),dp(17),dp(24)); root.setBackgroundColor(BG);
        ScrollView sc=new ScrollView(this); sc.setFillViewport(true); sc.addView(root); setContentView(sc);
        TextView brand=bold("FE  /  FOOTBALL EMPIRE",12); brand.setTextColor(LIME); root.addView(brand);
        TextView h=bold(title,27); h.setPadding(0,dp(5),0,0); root.addView(h);
        TextView sub=muted("ULTIMATE CAREER  •  MANAGER MODE",10); root.addView(sub);
        View gap=new View(this); root.addView(gap,new LinearLayout.LayoutParams(1,dp(15)));
    }
    void addStat(LinearLayout row,String value,String label) {
        LinearLayout box=new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL); box.setPadding(dp(10),dp(9),dp(6),dp(9)); box.setBackground(shape(SURFACE2,0,12));
        TextView v=bold(value,19); v.setTextColor(LIME); box.addView(v); box.addView(muted(label,10));
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-2,1); lp.setMargins(0,0,dp(6),0); row.addView(box,lp);
    }
    String initials(String name) {
        String out=""; for(String part:name.split(" ")) if(!part.isEmpty()&&out.length()<3)out+=part.substring(0,1).toUpperCase(Locale.ROOT); return out;
    }
    String money(int n) { return String.format(Locale.US,"%,d",n).replace(',',' ')+" zł"; }
    void showHome() {
        base("Centrum kariery");
        LinearLayout hero=panel();
        hero.addView(muted("TWÓJ KLUB  /  SEZON 2026–27",10));
        hero.addView(bold(club,27));
        hero.addView(muted("Każdy mecz przybliża Cię do mistrzostwa.",12));
        View spacer=new View(this); hero.addView(spacer,new LinearLayout.LayoutParams(1,dp(12)));
        TextView budget=bold("BUDŻET TRANSFEROWY     "+money(cash),15); budget.setTextColor(LIME); hero.addView(budget);
        TextView title=bold("SEZON W LICZBACH",13); root.addView(title);
        LinearLayout stats=new LinearLayout(this); stats.setOrientation(LinearLayout.HORIZONTAL);
        addStat(stats,String.valueOf(points),"PUNKTY"); addStat(stats,String.valueOf(played),"MECZE"); addStat(stats,wins+"-"+draws+"-"+losses,"W-R-P");
        LinearLayout.LayoutParams stlp=new LinearLayout.LayoutParams(-1,-2); stlp.bottomMargin=dp(13); root.addView(stats,stlp);
        LinearLayout next=panel(); next.addView(muted("NASTĘPNE SPOTKANIE",10));
        String opponent=clubs[random.nextInt(clubs.length)]; while(opponent.equals(club))opponent=clubs[random.nextInt(clubs.length)];
        LinearLayout matchup=new LinearLayout(this); matchup.setGravity(android.view.Gravity.CENTER); matchup.setPadding(0,dp(8),0,dp(8));
        TextView home=bold(initials(club)+"\n"+club,15); home.setGravity(android.view.Gravity.CENTER); matchup.addView(home,new LinearLayout.LayoutParams(0,-2,1));
        TextView vs=bold("VS",18); vs.setTextColor(MUTED); matchup.addView(vs);
        TextView away=bold(opponent+"\n"+initials(opponent),15); away.setGravity(android.view.Gravity.CENTER); matchup.addView(away,new LinearLayout.LayoutParams(0,-2,1));
        next.addView(matchup); next.addView(button("ROZEGRAJ MECZ   →",this::chooseMatch,true));
        LinearLayout menu=panel(); menu.addView(bold("ZARZĄDZANIE KLUBEM",13));
        menu.addView(button("👥   Skład i zawodnicy",this::showSquad));\n        menu.addView(button("📊   Tabela ligi",this::showTable));
        menu.addView(button("🛡   Zmień klub",this::chooseClub));
        menu.addView(button("📋   Historia ostatniego meczu",this::showLast));
        menu.addView(button("↻   Nowa kariera (reset)",this::resetCareer));
        TextView note=muted("PROTOTYP OFFLINE  •  WERSJA 0.2",10); note.setGravity(android.view.Gravity.CENTER); root.addView(note);
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
    void showSquad() {
        base("Skład drużyny");
        LinearLayout summary=panel();
        summary.addView(bold("KADRA PIERWSZEGO ZESPOŁU",14));
        summary.addView(muted("15 zawodników  •  ocena, potencjał i wartość rynkowa",12));
        int total=0; for(Player p:squad) total+=p.value;
        TextView value=bold("Wartość kadry: "+money(total),17); value.setTextColor(LIME); summary.addView(value);
        summary.addView(muted("BR — bramkarz • PO/LO — obrońca • ŚO — środkowy obrońca • ŚP/ŚPD — pomocnik • PS/LS — skrzydłowy • N — napastnik",10));
        for(Player p:squad) {
            LinearLayout row=new LinearLayout(this); row.setOrientation(LinearLayout.HORIZONTAL); row.setGravity(android.view.Gravity.CENTER_VERTICAL);
            row.setPadding(dp(8),dp(8),dp(8),dp(8)); row.setBackground(shape(SURFACE2,0,12));
            LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,-2); rp.bottomMargin=dp(7); root.addView(row,rp);
            TextView rating=bold(String.valueOf(p.rating),20); rating.setTextColor(p.rating>=75?LIME:Color.WHITE); rating.setGravity(android.view.Gravity.CENTER);
            row.addView(rating,new LinearLayout.LayoutParams(dp(42),-2));
            LinearLayout info=new LinearLayout(this); info.setOrientation(LinearLayout.VERTICAL);
            info.addView(bold(p.name,14)); info.addView(muted(p.position+"  •  "+p.age+" lat  •  potencjał "+p.potential,11));
            row.addView(info,new LinearLayout.LayoutParams(0,-2,1));
            TextView market=muted(money(p.value),11); market.setGravity(android.view.Gravity.RIGHT|android.view.Gravity.CENTER_VERTICAL);
            row.addView(market,new LinearLayout.LayoutParams(dp(105),-2));
        }
        LinearLayout hint=panel(); hint.addView(bold("RYNEK TRANSFEROWY",14));
        hint.addView(muted("Kolejny etap: negocjacje transferowe, pensje, kontrakty i rozwój zawodników. Na razie to przykładowa kadra prototypu.",12));
        root.addView(button("←  Wróć do kariery",this::showHome,true));
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
