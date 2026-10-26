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
import org.json.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class MainActivity extends Activity {
    LinearLayout root;
    SharedPreferences prefs;
    int played, wins, draws, losses, points, goalsFor, goalsAgainst, cash;
    String club = "Real Madrid";
    String[] clubs = {"Real Madrid", "FC Barcelona", "Manchester City", "Bayern München", "Legia Warszawa"};
    final ExecutorService apiExecutor = Executors.newSingleThreadExecutor();
    final android.os.Handler ui = new android.os.Handler(android.os.Looper.getMainLooper());
    ArrayList<JSONObject> worldLeagues = new ArrayList<>();
    ArrayList<JSONObject> worldTeams = new ArrayList<>();
    boolean leaguesMore=false, teamsMore=false;
    String currentLeagueId="", currentLeagueName="";
    ArrayList<Player> activeSquad = new ArrayList<>();
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
        int home, away, homeShots, awayShots, homeOnTarget, awayOnTarget;
        int homePossession=50, awayPossession=50, homeCorners, awayCorners, homeFouls, awayFouls;
        int homeYellow, awayYellow, homeRed, awayRed;
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
        club=prefs.getString("club","Real Madrid");
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
        base("CENTRUM KLUBU");

        // Cinematic career header
        LinearLayout hero = panel();
        hero.setPadding(dp(17),dp(17),dp(17),dp(17));
        hero.setBackground(new android.graphics.drawable.GradientDrawable(
            android.graphics.drawable.GradientDrawable.Orientation.TL_BR,
            new int[]{Color.rgb(16,56,51),Color.rgb(18,31,45),Color.rgb(22,28,43)}));
        hero.addView(muted("FOOTBALL EMPIRE  /  SEZON 2026–27",10));
        TextView clubTitle=bold(club,29); clubTitle.setTextColor(Color.WHITE);
        hero.addView(clubTitle);
        TextView managerLine=muted("CENTRUM ZARZĄDZANIA  •  PIERWSZY ZESPÓŁ",10);
        hero.addView(managerLine);
        View spacer=new View(this); hero.addView(spacer,new LinearLayout.LayoutParams(1,dp(12)));
        LinearLayout budgetRow=new LinearLayout(this); budgetRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
        LinearLayout budgetCol=new LinearLayout(this); budgetCol.setOrientation(LinearLayout.VERTICAL);
        budgetCol.addView(muted("BUDŻET TRANSFEROWY",10));
        TextView budget=bold(money(cash),23); budget.setTextColor(LIME); budgetCol.addView(budget);
        budgetRow.addView(budgetCol,new LinearLayout.LayoutParams(0,-2,1));
        TextView badge=bold(initials(club),21); badge.setGravity(android.view.Gravity.CENTER);
        badge.setTextColor(LIME); badge.setBackground(shape(Color.rgb(8,19,29),LIME,18));
        budgetRow.addView(badge,new LinearLayout.LayoutParams(dp(66),dp(66)));
        hero.addView(budgetRow);

        TextView section=bold("SEZON W LICZBACH",12); section.setTextColor(MUTED); root.addView(section);
        LinearLayout stats=new LinearLayout(this); stats.setOrientation(LinearLayout.HORIZONTAL);
        addStat(stats,String.valueOf(points),"PUNKTY");
        addStat(stats,String.valueOf(played),"MECZE");
        addStat(stats,wins+"-"+draws+"-"+losses,"W-R-P");
        LinearLayout.LayoutParams statLp=new LinearLayout.LayoutParams(-1,-2);
        statLp.bottomMargin=dp(15); root.addView(stats,statLp);

        String opponent=clubs[(Math.max(0,Arrays.asList(clubs).indexOf(club))+1)%clubs.length];
        LinearLayout match=panel();
        match.setPadding(dp(15),dp(15),dp(15),dp(15));
        match.setBackground(shape(Color.rgb(19,29,44),Color.rgb(73,99,111),20));
        LinearLayout matchHead=new LinearLayout(this); matchHead.setGravity(android.view.Gravity.CENTER_VERTICAL);
        TextView matchTitle=bold("NASTĘPNY MECZ",13); matchTitle.setTextColor(LIME);
        matchHead.addView(matchTitle,new LinearLayout.LayoutParams(0,-2,1));
        TextView round=muted("KOLEJKA "+(played+1),10); matchHead.addView(round);
        match.addView(matchHead);
        LinearLayout versus=new LinearLayout(this); versus.setGravity(android.view.Gravity.CENTER_VERTICAL);
        versus.setPadding(0,dp(17),0,dp(17));
        LinearLayout left=new LinearLayout(this); left.setOrientation(LinearLayout.VERTICAL); left.setGravity(android.view.Gravity.CENTER);
        TextView homeBadge=bold(initials(club),22); homeBadge.setGravity(android.view.Gravity.CENTER); homeBadge.setTextColor(LIME);
        homeBadge.setBackground(shape(Color.rgb(8,19,29),BORDER,16)); left.addView(homeBadge,new LinearLayout.LayoutParams(dp(62),dp(62)));
        TextView homeName=bold(club,12); homeName.setGravity(android.view.Gravity.CENTER); homeName.setMaxLines(2); left.addView(homeName);
        versus.addView(left,new LinearLayout.LayoutParams(0,-2,1));
        LinearLayout center=new LinearLayout(this); center.setOrientation(LinearLayout.VERTICAL); center.setGravity(android.view.Gravity.CENTER);
        TextView vs=bold("VS",23); vs.setTextColor(Color.WHITE); center.addView(vs);
        TextView time=muted("DZISIAJ  •  18:00",9); center.addView(time);
        versus.addView(center);
        LinearLayout right=new LinearLayout(this); right.setOrientation(LinearLayout.VERTICAL); right.setGravity(android.view.Gravity.CENTER);
        TextView awayBadge=bold(initials(opponent),22); awayBadge.setGravity(android.view.Gravity.CENTER); awayBadge.setTextColor(Color.WHITE);
        awayBadge.setBackground(shape(SURFACE2,BORDER,16)); right.addView(awayBadge,new LinearLayout.LayoutParams(dp(62),dp(62)));
        TextView awayName=bold(opponent,12); awayName.setGravity(android.view.Gravity.CENTER); awayName.setMaxLines(2); right.addView(awayName);
        versus.addView(right,new LinearLayout.LayoutParams(0,-2,1));
        match.addView(versus);
        match.addView(button("ROZEGRAJ SPOTKANIE  →",this::chooseMatch,true));

        TextView toolsTitle=bold("ZARZĄDZAJ KLUBEM",12); toolsTitle.setTextColor(MUTED); root.addView(toolsTitle);
        LinearLayout grid=new LinearLayout(this); grid.setOrientation(LinearLayout.VERTICAL);
        LinearLayout row1=new LinearLayout(this); row1.setOrientation(LinearLayout.HORIZONTAL);
        addHomeTile(row1,"⚽","PIERWSZY ZESPÓŁ","Skład i zawodnicy",this::showSquad);
        addHomeTile(row1,"🏆","ROZGRYWKI","Tabela ligi",this::showTable);
        grid.addView(row1);
        LinearLayout row2=new LinearLayout(this); row2.setOrientation(LinearLayout.HORIZONTAL);
        addHomeTile(row2,"🌍","SKAUTING","Ligi i kluby świata",this::showWorldDatabase);
        addHomeTile(row2,"⇄","TRANSFERy","Zmień klub",this::chooseClub);
        grid.addView(row2);
        root.addView(grid);
        root.addView(button("▤   OSTATNI MECZ I STATYSTYKI",this::showLast));
        View bottomSpace=new View(this); root.addView(bottomSpace,new LinearLayout.LayoutParams(1,dp(8)));
        TextView footer=muted("FOOTBALL EMPIRE  •  TWOJA HISTORIA. TWÓJ KLUB.",9);
        footer.setGravity(android.view.Gravity.CENTER); root.addView(footer);
    }

    void addHomeTile(LinearLayout row,String iconText,String title,String subtitle,Runnable action) {
        LinearLayout tile=new LinearLayout(this); tile.setOrientation(LinearLayout.VERTICAL);
        tile.setPadding(dp(12),dp(13),dp(10),dp(13));
        tile.setBackground(shape(SURFACE,BORDER,16));
        tile.setClickable(true); tile.setFocusable(true); tile.setOnClickListener(v->action.run());
        TextView iconView=bold(iconText,23); iconView.setTextColor(LIME); tile.addView(iconView);
        TextView titleView=bold(title,11); titleView.setPadding(0,dp(7),0,dp(2)); tile.addView(titleView);
        TextView sub=muted(subtitle,10); sub.setMaxLines(2); tile.addView(sub);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-2,1);
        lp.setMargins(0,dp(5),dp(7),dp(5)); row.addView(tile,lp);
    }
    String apiKey() { return prefs.getString("goal_api_key","").trim(); }
    JSONObject apiGet(String path) throws Exception {
        if(apiKey().isEmpty()) throw new Exception("Najpierw zapisz klucz API.");
        URL url=new URL("https://api.goal-api.com/v1"+path);
        HttpURLConnection c=(HttpURLConnection)url.openConnection();
        c.setRequestMethod("GET"); c.setConnectTimeout(12000); c.setReadTimeout(15000);
        c.setRequestProperty("Authorization","Bearer "+apiKey());
        c.setRequestProperty("Accept","application/json");
        int code=c.getResponseCode();
        InputStream in=(code>=200&&code<300)?c.getInputStream():c.getErrorStream();
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        byte[] buf=new byte[4096]; int n;
        while(in!=null&&(n=in.read(buf))>0)out.write(buf,0,n);
        if(in!=null)in.close(); c.disconnect();
        String body=new String(out.toByteArray(),java.nio.charset.StandardCharsets.UTF_8);
        if(code<200||code>=300) throw new Exception("GOAL API HTTP "+code+": "+body);
        JSONObject json=new JSONObject(body);
        if(!json.optBoolean("success",true)) throw new Exception(json.optString("message","API zwróciło błąd"));
        return json;
    }
    JSONArray dataArray(JSONObject json) {
        Object d=json.opt("data");
        if(d instanceof JSONArray)return (JSONArray)d;
        if(d instanceof JSONObject) {
            JSONObject o=(JSONObject)d;
            JSONArray a=o.optJSONArray("items"); if(a!=null)return a;
            a=o.optJSONArray("leagues"); if(a!=null)return a;
            a=o.optJSONArray("teams"); if(a!=null)return a;
            a=o.optJSONArray("players"); if(a!=null)return a;
            a=o.optJSONArray("countries"); if(a!=null)return a;
        }
        return new JSONArray();
    }
    void showWorldDatabase() {
        base("Baza światowa");
        LinearLayout api=panel();
        api.addView(bold("GOAL API  /  GLOBAL FOOTBALL DATA",15));
        api.addView(muted("Katalog lig, klubów i kadr z wielu krajów. Klucz API jest wymagany i pozostaje zapisany lokalnie na tym urządzeniu.",12));
        EditText key=new EditText(this); key.setSingleLine(true); key.setTextColor(Color.WHITE);
        key.setHintTextColor(MUTED); key.setHint("Wklej klucz API GOAL");
        key.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        key.setText(apiKey()); key.setPadding(dp(10),dp(8),dp(10),dp(8)); key.setBackground(shape(SURFACE2,BORDER,10));
        api.addView(key,new LinearLayout.LayoutParams(-1,dp(50)));
        api.addView(button("Zapisz klucz i wczytaj ligi",()->{
            String k=key.getText().toString().trim();
            if(k.isEmpty()){toast("Wklej klucz API GOAL.");return;}
            prefs.edit().putString("goal_api_key",k).apply();
            loadWorldLeagues();
        },true));
        root.addView(button("Wczytaj ligi świata",this::loadWorldLeagues));
        root.addView(button("Wróć do kariery",this::showHome));
        root.addView(muted("Utwórz klucz na goal-api.com. Dostępność lig i pól zależy od planu API.",11));
        if(!worldLeagues.isEmpty()) renderWorldLeagues();
    }
    void toast(String msg){Toast.makeText(this,msg,Toast.LENGTH_LONG).show();}
    void loadWorldLeagues() {
        if(apiKey().isEmpty()){toast("Najpierw wklej i zapisz klucz API GOAL.");showWorldDatabase();return;}
        base("Pobieranie lig");
        root.addView(text("Łączę z GOAL API i pobieram katalog rozgrywek…",15));
        apiExecutor.execute(()->{
            try {
                JSONObject response=apiGet("/leagues?limit=100&offset=0");
                JSONArray a=dataArray(response); ArrayList<JSONObject> result=new ArrayList<>();
                for(int i=0;i<a.length();i++)result.add(a.getJSONObject(i));
                JSONObject pagination=response.optJSONObject("pagination");
                boolean more=pagination!=null&&pagination.optBoolean("hasMore",false);
                ui.post(()->{worldLeagues.clear();worldLeagues.addAll(result);leaguesMore=more;showWorldDatabase();toast("Pobrano "+worldLeagues.size()+" lig/rozgrywek.");});
            } catch(Exception e){ui.post(()->{showWorldDatabase();toast("Nie udało się pobrać lig: "+e.getMessage());});}
        });
    }
    void renderWorldLeagues() {
        TextView heading=bold("ROZGRYWKI ("+worldLeagues.size()+")",13);heading.setTextColor(LIME);root.addView(heading);
        for(JSONObject league:worldLeagues) {
            String name=league.optString("name","Nieznana liga");
            String country=league.optString("countryName",league.optString("country",""));
            String id=league.optString("id",league.optString("apiId",""));
            String label=name+(country.isEmpty()?"":"  •  "+country);
            root.addView(button(label,()->loadWorldTeams(id,name)));
        }
        if(leaguesMore) root.addView(button("Wczytaj kolejne ligi świata  →",()->loadMoreLeagues()));
    }
    void loadMoreLeagues() {
        base("Pobieranie kolejnych lig");
        apiExecutor.execute(()->{
            try {
                int offset=worldLeagues.size();
                JSONObject response=apiGet("/leagues?limit=100&offset="+offset);
                JSONArray a=dataArray(response);ArrayList<JSONObject> result=new ArrayList<>();
                for(int i=0;i<a.length();i++)result.add(a.getJSONObject(i));
                JSONObject pagination=response.optJSONObject("pagination");
                boolean more=pagination!=null&&pagination.optBoolean("hasMore",false);
                ui.post(()->{worldLeagues.addAll(result);leaguesMore=more;showWorldDatabase();});
            }catch(Exception e){ui.post(()->{showWorldDatabase();toast("Błąd pobierania kolejnych lig: "+e.getMessage());});}
        });
    }
    void loadWorldTeams(String leagueId,String leagueName) {
        currentLeagueId=leagueId;currentLeagueName=leagueName;worldTeams.clear();teamsMore=false;
        loadWorldTeamsPage(0,false);
    }
    void loadWorldTeamsPage(int offset,boolean append) {
        base("Kluby / "+currentLeagueName);root.addView(text("Pobieram kluby z rozgrywek…",14));
        apiExecutor.execute(()->{
            try {
                JSONObject response=apiGet("/leagues/"+URLEncoder.encode(currentLeagueId,"UTF-8")+"/teams?limit=100&offset="+offset);
                JSONArray a=dataArray(response);ArrayList<JSONObject> result=new ArrayList<>();
                for(int i=0;i<a.length();i++)result.add(a.getJSONObject(i));
                JSONObject pagination=response.optJSONObject("pagination");
                boolean more=pagination!=null&&pagination.optBoolean("hasMore",false);
                ui.post(()->{
                    if(!append)worldTeams.clear();
                    worldTeams.addAll(result);teamsMore=more;
                    base("Kluby / "+currentLeagueName);
                    root.addView(muted("Wczytano "+worldTeams.size()+" klubów.",12));
                    for(JSONObject team:worldTeams){
                        String id=team.optString("id",team.optString("apiId",""));
                        String name=team.optString("name",team.optString("teamName","Klub"));
                        String country=team.optString("countryName","");
                        root.addView(button(name+(country.isEmpty()?"":" • "+country),()->loadWorldTeamSquad(id,name)));
                    }
                    if(teamsMore)root.addView(button("Wczytaj kolejne kluby  →",()->loadWorldTeamsPage(worldTeams.size(),true)));
                    root.addView(button("← Wróć do lig",this::showWorldDatabase));
                });
            }catch(Exception e){ui.post(()->{base("Błąd bazy");root.addView(text(e.getMessage(),13));root.addView(button("Wróć",this::showWorldDatabase));});}
        });
    }
    void loadWorldTeamSquad(String teamId,String teamName) {
        base("Kadra / "+teamName);root.addView(text("Pobieram zawodników klubu…",14));
        apiExecutor.execute(()->{
            try {
                JSONObject response=apiGet("/teams/"+URLEncoder.encode(teamId,"UTF-8")+"/players");
                JSONArray a=dataArray(response);ArrayList<Player> result=new ArrayList<>();
                for(int i=0;i<a.length();i++){
                    JSONObject p=a.getJSONObject(i);
                    String name=p.optString("name",p.optString("playerName","Zawodnik"));
                    String pos=p.optString("position","Nieznana");
                    int age=p.optInt("age",0);
                    if(age==0&&!p.optString("dateOfBirth","").isEmpty()){
                        try{int year=Integer.parseInt(p.optString("dateOfBirth").substring(0,4));age=Calendar.getInstance().get(Calendar.YEAR)-year;}catch(Exception ignored){}
                    }
                    int rating=p.optInt("rating",p.optInt("overall",60));
                    int potential=p.optInt("potential",Math.min(99,rating+5));
                    int value=p.optInt("marketValue",p.optInt("market_value",0));
                    result.add(new Player(name,pos,rating,potential,age,value));
                }
                ui.post(()->{
                    activeSquad.clear();activeSquad.addAll(result);club=teamName;save();showSquad();
                    toast("Wczytano "+result.size()+" zawodników. Brakujące oceny/potencjał są szacowane.");
                });
            }catch(Exception e){ui.post(()->{base("Błąd kadry");root.addView(text(e.getMessage(),13));root.addView(button("Wróć do bazy",this::showWorldDatabase));});}
        });
    }
    void chooseClub() {
        base("Wybierz klub");
        for(String c:clubs) root.addView(button(c, ()->{club=c; save(); showHome();}));
        root.addView(button("🌍 Wybierz klub z bazy światowej",this::showWorldDatabase,true));
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
    int squadStrength() {
        int total=0; ArrayList<Player> players=currentSquad(); for(Player p:players) total+=p.rating;
        return total / Math.max(1,players.size());
    }
    ArrayList<Player> currentSquad() {
        return activeSquad.isEmpty() ? new ArrayList<>(Arrays.asList(squad)) : activeSquad;
    }
    Player scorerForMinute(int minute) {
        ArrayList<Player> players=currentSquad();
        int total=0;
        for(Player p:players) total+=Math.max(1,p.position.equals("N")||p.position.equalsIgnoreCase("Attacker")?6:
            (p.position.equals("PS")||p.position.equals("LS")||p.position.equalsIgnoreCase("Midfielder")?4:
            (p.position.equals("ŚP")||p.position.equals("ŚPD")?3:1)));
        int pick=random.nextInt(Math.max(1,total));
        for(Player p:players) {
            pick-=Math.max(1,p.position.equals("N")||p.position.equalsIgnoreCase("Attacker")?6:
                (p.position.equals("PS")||p.position.equals("LS")||p.position.equalsIgnoreCase("Midfielder")?4:
                (p.position.equals("ŚP")||p.position.equals("ŚPD")?3:1)));
            if(pick<0) return p;
        }
        return players.get(Math.max(0,players.size()-1));
    }
    String opponentScorer() {
        String[] names={"A. Kowalski","P. Nowicki","M. Adamski","J. Pawłowski","K. Baran","D. Krupa","T. Sikora","R. Wróbel"};
        return names[random.nextInt(names.length)];
    }
    Match simulate(String opponent) {
        // One deterministic-in-this-match event stream feeds BOTH match presentation modes.
        Match m=new Match(0,0,club,opponent);
        int strength=squadStrength();
        int opponentStrength=65+random.nextInt(13);
        int homeEdge=Math.max(-12,Math.min(12,strength-opponentStrength))+3; // home advantage
        m.homePossession=Math.max(35,Math.min(65,50+homeEdge/2+random.nextInt(9)-4));
        m.awayPossession=100-m.homePossession;
        m.events.add("00'  SĘDZIA ROZPOCZYNA SPOTKANIE");
        for(int minute=1;minute<=90;minute++) {
            if(minute==46) m.events.add("45+?  KONIEC PIERWSZEJ POŁOWY");
            // Each side creates attacks throughout the match; quality changes shot and conversion odds.
            double homeAttack=0.175 + homeEdge*0.0025;
            double awayAttack=0.175 - homeEdge*0.0018;
            if(random.nextDouble()<homeAttack) {
                if(random.nextDouble()<0.47) {
                    m.homeShots++;
                    if(random.nextDouble()<Math.min(0.78,0.38+strength*0.0018)) {
                        m.homeOnTarget++;
                        double conversion=Math.max(0.12,Math.min(0.36,0.235+(strength-opponentStrength)*0.004));
                        if(random.nextDouble()<conversion) {
                            m.home++;
                            Player scorer=scorerForMinute(minute);
                            Player assist=scorerForMinute(minute);
                            while(assist.name.equals(scorer.name)) assist=scorerForMinute(minute);
                            m.events.add(minute+"' GOL "+club+" — "+scorer.name+" (asysta: "+assist.name+")");
                        } else if(random.nextBoolean()) m.events.add(minute+"' BRAMKARZ "+opponent+" broni groźny strzał");
                    }
                }
                if(random.nextDouble()<0.105) m.homeCorners++;
            }
            if(random.nextDouble()<awayAttack) {
                if(random.nextDouble()<0.45) {
                    m.awayShots++;
                    if(random.nextDouble()<Math.min(0.78,0.37+opponentStrength*0.0018)) {
                        m.awayOnTarget++;
                        double conversion=Math.max(0.12,Math.min(0.36,0.235+(opponentStrength-strength)*0.003));
                        if(random.nextDouble()<conversion) {
                            m.away++;
                            m.events.add(minute+"' GOL "+opponent+" — "+opponentScorer()+" (asysta: "+opponentScorer()+")");
                        } else if(random.nextBoolean()) m.events.add(minute+"' BRAMKARZ "+club+" ratuje zespół");
                    }
                }
                if(random.nextDouble()<0.095) m.awayCorners++;
            }
            if(random.nextDouble()<0.115) m.homeFouls++;
            if(random.nextDouble()<0.115) m.awayFouls++;
            if(random.nextDouble()<0.018) {
                if(random.nextBoolean()) {m.homeYellow++;m.events.add(minute+"' ŻÓŁTA KARTKA: "+club);}
                else {m.awayYellow++;m.events.add(minute+"' ŻÓŁTA KARTKA: "+opponent);}
            }
            if(minute==45) m.events.add("45'  KONIEC PIERWSZEJ POŁOWY");
        }
        m.events.add("90'  KONIEC MECZU  •  WYNIK "+m.home+":"+m.away);
        Collections.sort(m.events,(a,b)->Integer.compare(eventMinute(a),eventMinute(b)));
        return m;
    }
    int eventMinute(String event) {
        try { return Integer.parseInt(event.substring(0,event.indexOf("'")).replace("+?","45")); }
        catch(Exception e) { return 999; }
    }
    void play(String opponent, boolean animate) {
        Match m=simulate(opponent);
        played++; goalsFor+=m.home; goalsAgainst+=m.away;
        if(m.home>m.away){wins++;points+=3;cash+=250000;} else if(m.home==m.away){draws++;points++;cash+=100000;} else {losses++;cash+=50000;}
        lastMatch=m; save();
        if(animate) show2D(m); else showResult(m);
    }
    void showResult(Match m) {
        base("RAPORT MECZOWY");
        LinearLayout score=panel();
        score.setBackground(shape(Color.rgb(21,42,43),Color.rgb(65,105,74),20));
        score.addView(muted("WYNIK KOŃCOWY  •  90 MIN",10));
        TextView teams=bold(m.home+"     :     "+m.away,34); teams.setGravity(android.view.Gravity.CENTER); score.addView(teams);
        TextView names=bold(m.homeName+"       "+m.awayName,13); names.setGravity(android.view.Gravity.CENTER); score.addView(names);
        TextView statsTitle=bold("STATYSTYKI SPOTKANIA",13); statsTitle.setTextColor(LIME); root.addView(statsTitle);
        LinearLayout stats=panel();
        statLine(stats,"Posiadanie piłki",m.homePossession+"%",m.awayPossession+"%");
        statLine(stats,"Strzały",String.valueOf(m.homeShots),String.valueOf(m.awayShots));
        statLine(stats,"Celne strzały",String.valueOf(m.homeOnTarget),String.valueOf(m.awayOnTarget));
        statLine(stats,"Rzuty rożne",String.valueOf(m.homeCorners),String.valueOf(m.awayCorners));
        statLine(stats,"Faule",String.valueOf(m.homeFouls),String.valueOf(m.awayFouls));
        statLine(stats,"Żółte kartki",String.valueOf(m.homeYellow),String.valueOf(m.awayYellow));
        LinearLayout timeline=panel(); timeline.addView(bold("OŚ CZASU MECZU",14));
        for(String e:m.events) timeline.addView(muted(e,12));
        root.addView(button("Wróć do kariery",this::showHome,true));
    }
    void statLine(LinearLayout parent,String label,String homeValue,String awayValue) {
        LinearLayout row=new LinearLayout(this); row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        TextView left=bold(homeValue,13); left.setGravity(android.view.Gravity.LEFT);
        TextView mid=muted(label,11); mid.setGravity(android.view.Gravity.CENTER);
        TextView right=bold(awayValue,13); right.setGravity(android.view.Gravity.RIGHT);
        row.addView(left,new LinearLayout.LayoutParams(0,-2,1));
        row.addView(mid,new LinearLayout.LayoutParams(0,-2,2));
        row.addView(right,new LinearLayout.LayoutParams(0,-2,1));
        parent.addView(row);
    }
    void show2D(Match m) {
        base("Mecz 2D");
        root.addView(text(m.homeName+"  "+m.home+" : "+m.away+"  "+m.awayName,19));
        PitchView pitch=new PitchView(this,m);
        root.addView(pitch,new LinearLayout.LayoutParams(-1,0,1));
        root.addView(text("Wydarzenia na boisku",18));
        for(String e:m.events) root.addView(text(e,12));
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
        ArrayList<Player> players=currentSquad();
        LinearLayout summary=panel();
        summary.addView(bold("KADRA PIERWSZEGO ZESPOŁU",14));
        summary.addView(muted(players.size()+" zawodników  •  dane z aktywnej kadry lub składu demonstracyjnego",12));
        int total=0; for(Player p:players) total+=p.value;
        TextView value=bold("Wartość kadry: "+money(total),17); value.setTextColor(LIME); summary.addView(value);
        summary.addView(muted("BR — bramkarz • PO/LO — obrońca • ŚO — środkowy obrońca • ŚP/ŚPD — pomocnik • PS/LS — skrzydłowy • N — napastnik",10));
        for(Player p:players) {
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
        hint.addView(muted(activeSquad.isEmpty()?"To kadra demonstracyjna. Otwórz bazę światową i wczytaj skład wybranego klubu z GOAL API.":"Kadra została pobrana z GOAL API. Oceny i potencjał mogą wymagać osobnego źródła danych.",12));
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
